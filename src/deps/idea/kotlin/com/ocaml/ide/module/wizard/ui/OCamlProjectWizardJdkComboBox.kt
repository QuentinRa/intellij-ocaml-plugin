// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
// Adapted from the code for Java to use OCamlSDk instead
package com.ocaml.ide.module.wizard.ui

import com.intellij.ide.JavaUiBundle
import com.intellij.ide.projectWizard.ProjectWizardJdkIntent
import com.intellij.ide.projectWizard.ProjectWizardJdkIntent.*
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.*
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.observable.util.whenDisposed
import com.intellij.openapi.progress.blockingContext
import com.intellij.openapi.projectRoots.*
import com.intellij.openapi.projectRoots.impl.DependentSdkType
import com.intellij.openapi.projectRoots.impl.SdkConfigurationUtil
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.popup.ListSeparator
import com.intellij.platform.util.coroutines.namedChildScope
import com.intellij.ui.*
import com.intellij.ui.AnimatedIcon.ANIMATION_IN_RENDERER_ALLOWED
import com.intellij.ui.components.JBLabel
import com.intellij.util.application
import com.intellij.util.ui.EmptyIcon
import com.ocaml.sdk.OCamlSdkType
import com.ocaml.sdk.utils.OCamlSdkHomeUtils
import com.ocaml.sdk.utils.OCamlSdkVersionUtils
import kotlinx.coroutines.*
import java.awt.BorderLayout
import java.awt.Component
import java.util.*
import javax.accessibility.AccessibleContext
import javax.swing.DefaultComboBoxModel
import javax.swing.Icon
import javax.swing.JList

@Service(Service.Level.APP)
internal class OCamlProjectWizardJdkComboBoxService(
    private val coroutineScope: CoroutineScope
) {
    fun childScope(name: String): CoroutineScope = coroutineScope.namedChildScope(name)
}

class OCamlProjectWizardJdkComboBox(
    val projectJdk: Sdk? = null,
    disposable: Disposable
): ComboBox<ProjectWizardJdkIntent>() {
    val registered: MutableList<ExistingJdk> = mutableListOf()
    val detectedJDKs: MutableList<DetectedJdk> = mutableListOf()
    var isLoadingExistingJdks: Boolean = true
    val progressIcon: JBLabel = JBLabel(AnimatedIcon.Default.INSTANCE)
    val coroutineScope = application.service<OCamlProjectWizardJdkComboBoxService>().childScope("ProjectWizardJdkComboBox")

    init {
        model = DefaultComboBoxModel(Vector(initialItems()))

        disposable.whenDisposed { coroutineScope.cancel() }
        coroutineScope.launch {
            addExistingJdks()
        }

        isSwingPopup = false
        ClientProperty.put(this, ANIMATION_IN_RENDERER_ALLOWED, true)
        renderer = object : GroupedComboBoxRenderer<ProjectWizardJdkIntent>(this) {
            override fun separatorFor(value: ProjectWizardJdkIntent): ListSeparator? {
                return when (value) {
                    registered.firstOrNull() -> ListSeparator(JavaUiBundle.message("jdk.registered.items"))
                    is AddJdkFromJdkListDownloader -> ListSeparator("")
                    detectedJDKs.firstOrNull() -> ListSeparator(JavaUiBundle.message("jdk.detected.items"))
                    else -> null
                }
            }

            override fun customize(item: SimpleColoredComponent, value: ProjectWizardJdkIntent, index: Int, isSelected: Boolean, cellHasFocus: Boolean) {
                item.icon = when {
                    value is NoJdk && index == -1 -> null
                    else -> getIcon(value)
                }

                when (value) {
                    is NoJdk -> item.append(JavaUiBundle.message("jdk.missing.item"), SimpleTextAttributes.ERROR_ATTRIBUTES)
                    is ExistingJdk -> {
                        if (value.jdk == projectJdk) {
                            item.append(JavaUiBundle.message("jdk.project.item"))
                            item.append(" ${projectJdk.name}", SimpleTextAttributes.GRAYED_ATTRIBUTES)
                        } else {
                            item.append(value.jdk.name)
                            val version = value.jdk.versionString ?: (value.jdk.sdkType as SdkType).presentableName
                            item.append(" $version", SimpleTextAttributes.GRAYED_ATTRIBUTES)
                        }
                    }

                    is DetectedJdk -> {
                        item.append(value.version)
                        item.append(" ${value.home}", SimpleTextAttributes.GRAYED_ATTRIBUTES)
                    }

                    else -> {}
                }
            }

            override fun getIcon(item: ProjectWizardJdkIntent): Icon? {
                return when (item) {
                    is ExistingJdk, is DetectedJdk -> OCamlSdkType.instance!!.icon
                    else -> EmptyIcon.ICON_16
                }
            }

            override fun getListCellRendererComponent(list: JList<out ProjectWizardJdkIntent>?,
                                                      value: ProjectWizardJdkIntent,
                                                      index: Int,
                                                      isSelected: Boolean,
                                                      cellHasFocus: Boolean): Component {
                val component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
                if (index == -1 && isLoadingExistingJdks && selectedItem !is DownloadJdk) {
                    val panel = object : CellRendererPanel(BorderLayout()) {
                        override fun getAccessibleContext(): AccessibleContext = component.accessibleContext
                    }
                    component.background = null
                    panel.add(component, BorderLayout.CENTER)
                    panel.add(progressIcon, BorderLayout.EAST)
                    return panel
                }
                else {
                    return component
                }
            }
        }
    }

    private fun initialItems(): MutableList<ProjectWizardJdkIntent> {
        val items = mutableListOf<ProjectWizardJdkIntent>()

        // Add JDKs from the ProjectJdkTable
        registered.addAll(
            ProjectJdkTable.getInstance().allJdks
                .filter { jdk ->
                    jdk.sdkType is OCamlSdkType && jdk.sdkType !is DependentSdkType
                }
                .map { ExistingJdk(it) }
        )

        if (registered.isNotEmpty()) {
            items.addAll(registered)
        } else {
            items.add(NoJdk)
        }

        return items
    }

    private suspend fun addExistingJdks() {
        val detected = blockingContext {
            OCamlSdkHomeUtils.suggestHomePaths().map { homePath: String ->
                val version = OCamlSdkVersionUtils.parse(homePath)
                DetectedJdk(version, homePath)
            }
        }

        withContext(Dispatchers.EDT + ModalityState.any().asContextElement()) {
            detected.forEach {
                detectedJDKs.add(it)
                addItem(it)
            }
            if ((selectedItem is NoJdk || selectedItem is DownloadJdk) && detected.any()) {
                val regex = "(\\d+)".toRegex()
                detected
                    .maxBy { regex.find(it.version)?.value?.toInt() ?: 0 }
                    .let { selectedItem = it }
            }
            isLoadingExistingJdks = false
        }
    }

    override fun setSelectedItem(anObject: Any?) {
        val toSelect = when (anObject) {
            is String -> {
                registered.firstOrNull { it.jdk.name == anObject } ?: selectedItem
            }
            is DetectedJdk -> {
                registerJdk(anObject.home, this)
                selectedItem
            }
            else -> anObject
        }
        super.setSelectedItem(toSelect)
    }

    val lastRegisteredJdkIndex
        get() = (0 until itemCount).firstOrNull { getItemAt(it) is AddJdkFromJdkListDownloader } ?: 0

    val comment: String?
        get() = when (selectedItem) {
            is DownloadJdk -> JavaUiBundle.message("jdk.download.comment")
            is NoJdk -> when {
                (0 until itemCount).any { getItemAt(it) is DownloadJdk } -> JavaUiBundle.message("jdk.missing.item.comment")
                else -> JavaUiBundle.message("jdk.missing.item.no.internet.comment")
            }
            else -> null
        }
}

private fun registerJdk(path: String, combo: OCamlProjectWizardJdkComboBox) {
    runWriteAction {
        SdkConfigurationUtil.createAndAddSDK(path, OCamlSdkType.instance!!)?.let {
            //JdkComboBoxCollector.jdkRegistered(it)
            val comboItem = ExistingJdk(it)
            val index = combo.lastRegisteredJdkIndex
            combo.registered.add(comboItem)
            combo.insertItemAt(ExistingJdk(it), index)
            combo.selectedIndex = index
        }
    }
}