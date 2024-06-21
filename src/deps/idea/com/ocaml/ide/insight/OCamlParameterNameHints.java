package com.ocaml.ide.insight;

import com.intellij.codeInsight.hints.InlayInfo;
import com.intellij.codeInsight.hints.InlayParameterHintsProvider;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.ocaml.lang.core.PsiFakeLet;
import com.ocaml.utils.adaptor.RequireJavaPlugin;
import com.or.ide.search.index.LetFqnIndex;
import com.or.lang.core.psi.PsiParameter;
import com.or.lang.core.psi.*;
import com.or.lang.core.psi.impl.PsiLowerIdentifier;
import com.or.lang.core.psi.impl.PsiParameterImpl;
import com.or.lang.core.psi.impl.PsiUpperIdentifier;
import com.or.lang.core.psi.impl.PsiValImpl;
import com.or.lang.core.psi.reference.PsiLowerSymbolReference;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@RequireJavaPlugin(what = "CompilerManager")
@Deprecated(forRemoval = true) @SuppressWarnings("UnstableApiUsage")
public class OCamlParameterNameHints implements InlayParameterHintsProvider {

    private static final List<InlayInfo> EMPTY_COLLECTION = Collections.emptyList();

    @SuppressWarnings("UnstableApiUsage")
    @Override public @NotNull Set<String> getDefaultBlackList() {
        return new HashSet<>();
    }

    @Override public @NotNull List<InlayInfo> getParameterHints(@NotNull PsiElement element, @NotNull PsiFile file) {
        // skip unsupported element (whitespaces, ...)
        // exceptions are added one by one
        if (OCamlInsightFilter.shouldSkip(element)) return EMPTY_COLLECTION;

        // is file excluded ? ignore
        VirtualFile virtualFile = file.getVirtualFile();
        Project project = file.getProject();
        if (CompilerManager.getInstance(project).isExcludedFromCompilation(virtualFile))
            return EMPTY_COLLECTION;
        // not excluded, do the job
        return getParameterHints(element);
    }

    @Override public @NotNull List<InlayInfo> getParameterHints(@NotNull PsiElement element) {
        // get a reference to the function
        PsiLowerSymbol functionName = PsiTreeUtil.getPrevSiblingOfType(element, PsiLowerSymbol.class);
        PsiReference reference = functionName == null ? null : functionName.getReference();
        if (!(reference instanceof PsiLowerSymbolReference)) return EMPTY_COLLECTION;
        // resolve the real element?
        ((PsiLowerSymbolReference) reference).setResolveFor(element);
        PsiElement resolvedRef = reference.resolve();
        PsiElement resolvedElement = (resolvedRef instanceof PsiLowerIdentifier || resolvedRef instanceof PsiUpperIdentifier) ? resolvedRef.getParent() : resolvedRef;
        if (resolvedElement instanceof PsiParameterImpl) {
            PsiElement resolvedElementParent = resolvedElement.getParent();
            if (!(resolvedElementParent instanceof PsiParameters)) {
                // log
                System.out.println("warn: the cast of PsiParameters (l!=1) in function wasn't handled.");
                return EMPTY_COLLECTION;
            }
            ArrayList<PsiParameter> params = new ArrayList<>();
            boolean found = false;
            for (PsiParameter parameter : ((PsiParameters) resolvedElementParent).getParametersList()) {
                if (parameter.equals(resolvedElement)) { found = true; continue; }
                if (!found) continue;
                params.add(parameter);
            }
            resolvedElement = new PsiFakeLet( params );
        }

        // log?
        //System.out.println("for:"+element.getText());
        // System.out.println("  resolved:"+(resolvedElement != null ? resolvedElement.getText() : null));
        //  System.out.println("  class:"+(resolvedElement != null ? resolvedElement.getClass() : null));
        if (!(resolvedElement instanceof PsiLet)) {
            // look for the implementation of val
            if (resolvedElement instanceof PsiValImpl) {
                Project project = element.getProject();
                GlobalSearchScope scope = GlobalSearchScope.allScope(project);
                String valQName = ((PsiValImpl) resolvedElement).getQualifiedName();
                Collection<PsiLet> elements = LetFqnIndex.getElements(valQName.hashCode(), project, scope);
                Optional<PsiLet> first = elements.stream().findFirst();
                if (first.isPresent()) {
                    resolvedElement = first.get();
                }
            }
            // found or not?
            if(!(resolvedElement instanceof PsiLet)) return EMPTY_COLLECTION;
        }
        // get the parameters
        PsiLet psiLet = (PsiLet) resolvedElement;
        List<PsiParameter> parameters = null;
        if (psiLet.isFunction()) {
            PsiFunction psiFunction = psiLet.getFunction();
            if (psiFunction != null) {
                parameters = psiFunction.getParameters();
            }
        }
        // log?
        // System.out.println("  has params?"+parameters);
        if (parameters == null) return EMPTY_COLLECTION;
        // get the name for this value
        int indexOfParameter = findIndexOfParameter(element, functionName.getText());
        // log?
        // System.out.println("    index is:"+indexOfParameter);
        if (indexOfParameter < parameters.size()) {
            PsiParameter psiParameter = parameters.get(indexOfParameter);
            return List.of(new InlayInfo(psiParameter.getRealName(), element.getTextOffset()));
        }

        return EMPTY_COLLECTION;
    }

    public static int findIndexOfParameter(@NotNull PsiElement element, String functionName) {
        PsiElement prevSibling = element.getPrevSibling();
        int count = 0;
        while (prevSibling != null) {
            // skip white spaces
            // and comments (fix, added)
            if (prevSibling instanceof PsiWhiteSpace || prevSibling instanceof PsiComment) {
                prevSibling = prevSibling.getPrevSibling();
                continue;
            }
            // done, we found the name of the function
            if (prevSibling.getText().equals(functionName)) return count;
            // pass, this is an argument (I hope so ><)
            count++;
            prevSibling = prevSibling.getPrevSibling();
        }

        return -1; // not found
    }
}
