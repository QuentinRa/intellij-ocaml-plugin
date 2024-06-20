package com.ocaml.ide.wizard.view;

import com.intellij.CommonBundle;
import com.intellij.icons.AllIcons;
import com.intellij.ide.HelpTooltip;
import com.intellij.ide.JavaUiBundle;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.ProjectJdkForModuleStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ui.configuration.projectRoot.ProjectSdksModel;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.ActionLink;
import com.ocaml.OCamlBundle;
import com.ocaml.icons.OCamlIcons;
import com.ocaml.ide.wizard.OCamlModuleBuilder;
import com.ocaml.sdk.OCamlSdkType;
import com.ocaml.sdk.providers.OCamlSdkProvidersManager;
import com.ocaml.sdk.providers.simple.DetectionResult;
import com.ocaml.sdk.providers.simple.OCamlNativeDetector;
import com.ocaml.sdk.providers.simple.SimpleSdkData;
import com.ocaml.utils.OCamlBrowseUtil;
import com.ocaml.utils.adaptor.RequireJavaPlugin;
import com.ocaml.utils.adaptor.ui.JdkComboBoxAdaptor;
import com.ocaml.utils.listener.DeferredDocumentListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * This class is providing a step to create a project or a module.
 * The only thing we need to do by ourselves is to create an SDK.
 * <p>
 * Either the user will use an already created, or use "add SDK"
 * and select an SDK installed using opam. Actually, as long as the structure
 * of the folder is the same as the one used by opam, it will work.
 * <p>
 * There is an option to create an "opam-like" SDK. The only thing we
 * need for now, is to know where are the sources, and where is the ocaml
 * binary.<br>
 *
 * <p>
 * Features of the class
 * <ul>
 *     <li><b>OK</b>: can add/use an SDK using the JdkComboBox</li>
 *     <li><b>OK</b>: the JDKComboBox must be loaded with the existing JDKs</li>
 *     <li><b>OK</b>: can create an SDK using the other fields</li>
 *     <li><b>OK</b>: warning if the "create simple" is selected but the ocaml binary location was installed using opam.</li>
 *     <li><b>OK</b>: prefill the ocaml binary location ({@link OCamlNativeDetector#detectNativeSdk()})</li>
 *     <li><b>OK</b>: prefill the ocamlc binary location ({@link OCamlNativeDetector#detectNativeSdk()})</li>
 *     <li><b>OK</b>: prefill the ocaml version ({@link OCamlNativeDetector#detectNativeSdk()})</li>
 *     <li><b>OK</b>: prefill the sources location ({@link OCamlNativeDetector#detectNativeSdk()})</li>
 *     <li><b>OK</b>: fill the ocamlc binary location when ocaml binary location is defined ({@link OCamlNativeDetector#detectNativeSdk(String)} )</li>
 *     <li><b>OK</b>: fill the ocaml version when the ocaml binary location is defined ({@link OCamlNativeDetector#detectNativeSdk(String)})</li>
 *     <li><b>OK</b>: add a loading icon and a check (valid/invalid) icon, as we have in CLion</li>
 *     <li><b>OK</b>: check that everything is valid</li>
 *     <li><b>OK</b>: add a warning if the user is trying to open the project without setting an SDK.</li>
 *     <li><b>OK</b>: handle possible bug if the user is pressing next while the async codes was not finished</li>
 *     <li><b>OK</b>: add a label "an opam-like SDK will be created in ~/.jdks"</li>
 *     <li><b>OK</b>: if we detect an "cygwin" SDK, then update libs with /lib/ocaml ({@link OCamlNativeDetector#detectNativeSdk(String)})</li>
 *     <li><b>OK</b>: allow the user of ocaml.exe</li>
 *     <li><b>OK</b>: messages with a path (ex: expected "/bin/ocaml"), should be changed if the path was \\bin\\ocaml)</li>
 *     <li><b>OK</b>: add "?" with a message</li>
 * </ul>
 *
 * @see ProjectJdkForModuleStep
 * @see SimpleSdkData for the creation of an opam-like SDK
 */
@RequireJavaPlugin(what = "ProjectJdkForModuleStep, JavaUiBundle, ProjectStructureConfigurable")
public class OCamlSdkWizardStep extends ModuleWizardStep {
    @NotNull private final WizardContext myWizardContext;
    @NotNull private final OCamlModuleBuilder myModuleBuilder;
    private Project myProject;
    private ProjectSdksModel mySdksModel; // project SDK, add/create SDKs, ...

    @NotNull private JPanel myPanel; // the view
    @NotNull private JLabel myWizardTitle; // title of the wizard
    @SuppressWarnings("unused") private ActionLink myPluginDocumentation; // link to the documentation
    @SuppressWarnings("unused") private ActionLink myIssueLink; // link to open an issue

    @NotNull private ButtonGroup myUseSdkChoice; // the group of two buttons
    private boolean isUseSelected; // true if the first menu is selected, false else

    private JLabel myLabelSdk; // 1# to prompt "Project" or "Module"
    @NotNull private JdkComboBoxAdaptor myJdkChooser; // 1# select JDK
    @NotNull private JPanel myCreateComponents; // 1# to disable every component in the second menu

    @NotNull private JPanel myUseComponents; // 2# to disable every component in the second menu
    @NotNull private JLabel myOcamlVersion; // 2# show ocaml version, fetched from myOCamlLocation
    @NotNull private JLabel mySdkSources; // 2# submit sources
    @NotNull private TextFieldWithBrowseButton myOCamlLocation; // 2# submit ocaml binary location
    @NotNull private JLabel myOCamlCompilerLocation; // 2# show compiler location deduced using myOCamlLocation
    @NotNull private JLabel myCreateLocationLabel; // 2# show were the created sdk will be stored
    private JLabel myOCamlBinaryLabel;
    @Nullable private Sdk createSDK; // 2# the sdk that we created
    boolean shouldValidateAgain = true; // selected SDK changed
    private SimpleSdkData myCustomSdkData; // 2# data of the SDK we are about to create

    public OCamlSdkWizardStep(@NotNull WizardContext wizardContext,
                              @NotNull OCamlModuleBuilder moduleBuilder) {
        myWizardContext = wizardContext;
        myModuleBuilder = moduleBuilder;

        Disposable disposable = myWizardContext.getDisposable();
        if (disposable != null) {
            Disposable stepDisposable = () -> mySdksModel.disposeUIResources();
            Disposer.register(disposable, stepDisposable);
        }

        // are we inside a project?
        int choice = wizardContext.isCreatingNewProject() ? 0 : 1;
        myLabelSdk.setText(OCamlBundle.message("project.wizard.module.prompt.sdk", choice));
        myWizardTitle.setText(OCamlBundle.message("project.wizard.title", choice));
        myLabelSdk.setLabelFor(myJdkChooser);

        // Disable create
        setEnabledPanel(myCreateComponents, false);
        isUseSelected = true;

        // Add the listener on one choice, triggered
        // when the button is enabled/disabled
        // Enable/Disable the right panel
        myUseSdkChoice.getSelection().addItemListener((c) -> {
            isUseSelected = !isUseSelected;
            setEnabledPanel(myCreateComponents, !isUseSelected);
            setEnabledPanel(myUseComponents, isUseSelected);
        });

        // Detect and prefill fields
        DetectionResult output = OCamlNativeDetector.detectNativeSdk();
        myOCamlLocation.setText(output.ocaml);
        myOCamlCompilerLocation.setText(output.ocamlCompiler);
        mySdkSources.setText(output.sources);
        myOcamlVersion.setText(output.version);
        showIconForCreateFields(output.isError);

        // On Field Updated (manually)
        DeferredDocumentListener.addDeferredDocumentListener(
                myOCamlLocation.getTextField(),
                e -> onOCamlLocationChange(),
                () -> {
                    myOCamlCompilerLocation.setText("");
                    // we are waiting for a version
                    myOcamlVersion.setText("");
                    mySdkSources.setText("");
                    showIconForCreateFields(null);
                },
                1000
        );
        // On File selected
        myOCamlLocation.addBrowseFolderListener(
                new TextBrowseFolderListener(FileChooserDescriptorFactory.createSingleFileDescriptor(), myProject) {
                    @SuppressWarnings("UnstableApiUsage")
                    @Override
                    protected void onFileChosen(@NotNull VirtualFile chosenFile) {
                        super.onFileChosen(chosenFile);
                        onOCamlLocationChange();
                    }
                }
        );

        // help
        myOCamlBinaryLabel.setIcon(AllIcons.General.ContextHelp);
        myOCamlBinaryLabel.setHorizontalTextPosition(SwingConstants.LEFT);
        new HelpTooltip()
                .setDescription(OCamlBundle.message("project.wizard.sdk.ocaml.binary.help"))
                .installOn(myOCamlBinaryLabel);

        // set the text + color
        myCreateLocationLabel.setText(OCamlBundle.message("project.wizard.create.location", SimpleSdkData.SDK_FOLDER));
        myCreateLocationLabel.setForeground(JBColor.GRAY);
    }

    /**
     * Show the icons next to the labels
     *
     * @param error null if loading, true if error, false if no errors
     */
    private void showIconForCreateFields(@Nullable Boolean error) {
        Icon icon;
        if (error == null) icon = OCamlIcons.UI.LOADING;
        else icon = error ? OCamlIcons.UI.FIELD_INVALID : OCamlIcons.UI.FIELD_VALID;

        myOCamlCompilerLocation.setIcon(icon);
        myOcamlVersion.setIcon(icon);
        mySdkSources.setIcon(icon);
    }

    // update the two other labels once the ocaml location was set
    private void onOCamlLocationChange() {
        // must validate again as the path changed
        shouldValidateAgain = true;
        // get the ocaml binary path
        String path = myOCamlLocation.getText();
        // Ask for the values
        var detection = OCamlNativeDetector.detectNativeSdk(path);
        showIconForCreateFields(detection.isError);
        myOCamlCompilerLocation.setText(detection.ocamlCompiler);
        myOcamlVersion.setText(detection.version);
        mySdkSources.setText(detection.sources);
    }

    // re-enable every component in a panel
    private void setEnabledPanel(JPanel panel, boolean enabled) {
        for (Component component : panel.getComponents()) {
            component.setEnabled(enabled);
        }
    }

    @Override public JComponent getComponent() {
        return myPanel;
    }

    @Override public void updateDataModel() {
        Sdk sdk = getSdk();

        boolean isProject = myWizardContext.isCreatingNewProject();

        if (isProject) {
            myWizardContext.setProjectJdk(sdk);
        } else {
            // use project SDK
            myModuleBuilder.setModuleJdk(sdk);
        }
    }

    private Sdk getSdk() {
        return isUseSelected ? myJdkChooser.getSelectedJdk() : createSDK;
    }

    @Override public @Nullable Icon getIcon() {
        return myWizardContext.getStepIcon();
    }

    @Override public boolean validate() throws ConfigurationException {
        boolean sdkSelected = true;
        if (isUseSelected) {
            if (myJdkChooser.isProjectJdkSelected()) return applyModel();
            sdkSelected = myJdkChooser.getSelectedJdk() != null;
        } else {
            if (!shouldValidateAgain) return true;

            boolean showWarning = Boolean.TRUE.equals(OCamlSdkProvidersManager.INSTANCE.isOpamBinary(myOCamlLocation.getText()));
            if (showWarning && Messages.showDialog(
                    JavaUiBundle.message("dialog.message.0.do.you.want.to.proceed", OCamlBundle.message("project.wizard.create.opam.warning.desc")),
                    OCamlBundle.message("project.wizard.create.opam.warning.title"),
                    new String[]{CommonBundle.getYesButtonText(), CommonBundle.getNoButtonText()},
                    1, Messages.getWarningIcon()) != Messages.YES) {
                return false;
            }

            myCustomSdkData = new SimpleSdkData(
                    myOCamlLocation.getText(),
                    myOCamlCompilerLocation.getText(),
                    myOcamlVersion.getText(),
                    mySdkSources.getText()
            );
            shouldValidateAgain = false;
        }

        if (!sdkSelected) {
            int result = Messages.showOkCancelDialog(JavaUiBundle.message("prompt.confirm.project.no.jdk"),
                    JavaUiBundle.message("title.no.jdk.specified"),
                    Messages.getOkButton(),
                    Messages.getCancelButton(),
                    Messages.getWarningIcon());
            return result == Messages.OK;
        }
        if (!isUseSelected) {
            mySdksModel.addSdk(
                    OCamlSdkType.getInstance(),
                    myCustomSdkData.homePath,
                    sdk -> createSDK = sdk // save
            );
            // reload when a new Sdk is added
            myJdkChooser.reloadModel();
        }

        return applyModel();
    }

    // we need to apply the model everytime we are updating the model , see issue #26
    private boolean applyModel() {
        try {
            mySdksModel.apply(null, true);
        } catch (ConfigurationException e) {
            // We should allow Next step if user has wrong SDK
            if (Messages.showDialog(JavaUiBundle.message("dialog.message.0.do.you.want.to.proceed", e.getMessage()),
                    e.getTitle(),
                    new String[]{CommonBundle.getYesButtonText(), CommonBundle.getNoButtonText()},
                    1, Messages.getWarningIcon()) != Messages.YES) {
                return false;
            }
        }
        return true;
    }

    // create components, needed by the ".form" since some must be created manually.
    // called before the rest of the constructor, after initializing the first two variables
    // that's why this code can't be moved in the constructor.
    // Moreover, variables that are initialized by the ".form" are also null here.
    public void createUIComponents() {
        Condition<? super SdkTypeId> sdkTypeFilter = sdk -> sdk instanceof OCamlSdkType;

        // adding the instructions
        myPluginDocumentation = OCamlBrowseUtil.toDocumentation();
        myIssueLink = OCamlBrowseUtil.toIssues();
    }
}
