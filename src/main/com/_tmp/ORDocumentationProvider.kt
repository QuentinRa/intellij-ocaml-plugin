package com.ocaml.ide.commenter._tmp

import com.intellij.openapi.editor.Editor
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.ocaml.OCamlLanguage
import java.util.*

class ORDocumentationProvider : DocumentationProvider {
    override fun generateDoc(element: PsiElement, originalElement: PsiElement?): String? {
        var element = element
        val languageProperties: ORLanguageProperties = ORLanguageProperties.cast(originalElement?.language)
        if (element is PsiFakeModule) {
            val child = element.containingFile.firstChild
            var text = ""

            var nextSibling = child
            while (nextSibling is PsiComment) {
                if (isDocumentation(nextSibling)) {
                    text = nextSibling.getText()
                    nextSibling = null
                } else {
                    // Not a special comment, try with next child until no more comments found
                    nextSibling = PsiTreeUtil.nextVisibleLeaf(nextSibling)
                }
            }

            if (!text.isEmpty()) {
                return DocFormatter.format(element.containingFile, element, languageProperties, text)
            }
        } else if (element is PsiUpperIdentifier || element is PsiLowerIdentifier) {
            element = element.parent

            // If it's an alias, resolve to the alias
            if (element is PsiLet) {
                val alias: String = (element as PsiLet).getAlias()
                if (alias != null) {
                    val resolvedAlias: PsiElement = (element as PsiLet).resolveAlias()
                    if (resolvedAlias is PsiLowerIdentifier) {
                        element = resolvedAlias.parent
                    }
                }
            }

            // Try to find a comment just below (OCaml only)
            if (element.language === OCamlLanguage.INSTANCE) {
                val belowComment = findBelowComment(element)
                if (belowComment != null) {
                    return if (isDocumentation(belowComment)
                    ) DocFormatter.format(element.containingFile, element, languageProperties, belowComment.text)
                    else belowComment.text
                }
            }

            // Else try to find a comment just above
            val aboveComment = findAboveComment(element)
            if (aboveComment != null) {
                if (aboveComment is PsiAnnotation) {
                    val value: PsiElement = aboveComment.getValue()
                    val text = if (value == null) null else value.text
                    return text?.substring(1, text.length - 1)
                }

                return if (isDocumentation(aboveComment)
                ) DocFormatter.format(element.containingFile, element, languageProperties, aboveComment.text)
                else aboveComment.text
            }
        }

        return null
    }

    override fun getQuickNavigateInfo(resolvedIdentifier: PsiElement, originalElement: PsiElement): String? {
        var quickDoc: String? = null
        val languageProperties: ORLanguageProperties = ORLanguageProperties.cast(originalElement.language)

        if (resolvedIdentifier is ORFakeResolvedElement) {
            // A fake element, used to query inferred types
            quickDoc = "Show usages of fake element '" + resolvedIdentifier.text + "'"
        } else if (resolvedIdentifier is FileBase) {
            LOG.debug("Quickdoc of topModule", resolvedIdentifier)

            val resolvedFile: FileBase = resolvedIdentifier as FileBase
            val relative_path: String = Platform.getRelativePathToModule(resolvedFile)
            quickDoc =
                ("<div style='white-space:nowrap;font-style:italic'>"
                        + relative_path
                        + "</div>"
                        + "Module " //+ DocFormatter.NAME_START
                        + resolvedFile.getModuleName())
            //+ DocFormatter.NAME_END;
        } else {
            val resolvedElement = if ((resolvedIdentifier is PsiLowerIdentifier
                        || resolvedIdentifier is PsiUpperIdentifier)
            ) resolvedIdentifier.parent
            else resolvedIdentifier
            LOG.trace("Resolved element", resolvedElement)

            if (resolvedElement is PsiType) {
                val type = resolvedElement as PsiType
                val path: Array<String> = ORUtil.getQualifiedPath(type)
                val typeBinding = if (type.isAbstract()
                ) "This is an abstract type"
                else DocFormatter.escapeCodeForHtml(type.getBinding())
                return createQuickDocTemplate(path, "type", resolvedIdentifier.text, typeBinding)
            }

            if (resolvedElement is PsiSignatureElement) {
                val signature: PsiSignature = (resolvedElement as PsiSignatureElement).getSignature()
                if (signature != null) {
                    val sig: String = DocFormatter.escapeCodeForHtml(signature.asText(languageProperties))
                    if (resolvedElement is PsiQualifiedPathElement) {
                        val qualifiedElement: PsiQualifiedPathElement = resolvedElement as PsiQualifiedPathElement
                        val elementType: String = PsiTypeElementProvider.getType(resolvedIdentifier)
                        return createQuickDocTemplate(
                            qualifiedElement.getPath(),
                            elementType,
                            qualifiedElement.getName(),
                            sig
                        )
                    }
                    return sig
                }
            }

            // No signature found, but resolved
            if (resolvedElement is PsiQualifiedNamedElement) {
                LOG.debug("Quickdoc resolved to ", resolvedElement)

                val elementType: String = PsiTypeElementProvider.getType(resolvedIdentifier)
                val desc = resolvedElement.name
                val path: Array<String> = ORUtil.getQualifiedPath(resolvedElement)

                val psiFile = originalElement.containingFile
                var inferredType = getInferredSignature(originalElement, psiFile, languageProperties)

                if (inferredType == null) {
                    // Can't find type in the usage, try to get type from the definition
                    inferredType = getInferredSignature(
                        resolvedIdentifier,
                        resolvedElement.getContainingFile(),
                        languageProperties
                    )
                }

                var sig = if (inferredType == null) null else DocFormatter.escapeCodeForHtml(inferredType)
                if (resolvedElement is PsiVariantDeclaration) {
                    sig = "type " + (resolvedElement.getParent().parent as PsiType).getName()
                }

                return createQuickDocTemplate(path, elementType, desc, if (resolvedElement is PsiModule) null else sig)
            }
        }

        return quickDoc
    }

    private fun findAboveComment(element: PsiElement?): PsiElement? {
        if (element == null) {
            return null
        }

        var commentElement: PsiElement? = null

        // search for a comment above
        var search = true
        var prevSibling = element.prevSibling
        while (search) {
            if (prevSibling is PsiComment) {
                search = false
                commentElement = prevSibling
            } else if (prevSibling is PsiWhiteSpace) {
                prevSibling = prevSibling.getPrevSibling()
            } else if (prevSibling is PsiAnnotation) {
                val annotation = prevSibling
                if ("@ocaml.doc" == annotation.getName()) {
                    search = false
                    commentElement = annotation
                } else {
                    prevSibling = prevSibling.getPrevSibling()
                }
            } else {
                search = false
            }
        }

        return commentElement
    }

    private fun findBelowComment(element: PsiElement?): PsiElement? {
        if (element != null) {
            val nextSibling = element.nextSibling
            val nextNextSibling = nextSibling?.nextSibling
            if ((nextNextSibling is PsiComment
                        && nextSibling is PsiWhiteSpace) && nextSibling.getText()
                    .replace("[ \t]".toRegex(), "").length == 1
            ) {
                return nextNextSibling
            }
        }

        return null
    }

    override fun getCustomDocumentationElement(
        editor: Editor,
        file: PsiFile,
        contextElement: PsiElement?,
        targetOffset: Int
    ): PsiElement? {
        if (contextElement != null && contextElement.parent is PsiLowerSymbol) {
            val reference: PsiReference = contextElement.parent.getReference()
            if (reference is PsiPolyVariantReference) {
                val lowerReference: PsiLowerSymbolReference = reference as PsiLowerSymbolReference
                val resolveResults: Array<ResolveResult> = lowerReference.multiResolve(false)
                if (0 < resolveResults.size) {
                    Arrays.sort<ResolveResult>(
                        resolveResults
                    ) { rr1: ResolveResult, rr2: ResolveResult ->
                        if ((rr1 as PsiLowerSymbolReference.LowerResolveResult).isInterface()
                        ) -1
                        else (if ((rr2 as PsiLowerSymbolReference.LowerResolveResult).isInterface()) 1 else 0)
                    }
                    return resolveResults[0].element
                }
            }
        }

        return null
    }

    private fun getInferredSignature(element: PsiElement, psiFile: PsiFile, language: ORLanguageProperties): String? {
        val signaturesContext: SignatureProvider.InferredTypesWithLines =
            psiFile.getUserData(SignatureProvider.SIGNATURES_CONTEXT)
        if (signaturesContext != null) {
            val elementSignature: PsiSignature = signaturesContext.getSignatureByOffset(element.textOffset)
            if (elementSignature != null) {
                return elementSignature.asText(language)
            }
        }
        return null
    }

    private fun createQuickDocTemplate(path: Array<String>, type: String?, name: String?, signature: String?): String {
        return (Joiner.join(".", path)
                + "<br/>"
                + (type ?: "")
                + (" <b>$name</b>")
                + (if (signature == null) "" else "<hr/>$signature"))
    }

    companion object {
        private val LOG: Log = Log.create("doc")

        fun isDocumentation(element: PsiElement?): Boolean {
            if (element == null) {
                return false
            }

            val nextText = element.text
            return (nextText.startsWith("(**") || nextText.startsWith("/**")) && nextText[3] != '*'
        }
    }
}
