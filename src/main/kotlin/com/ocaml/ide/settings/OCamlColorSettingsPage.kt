package com.ocaml.ide.settings

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import com.ocaml.icons.OCamlIcons
import com.ocaml.ide.colors.OCamlColor
import com.ocaml.ide.highlight.OCamlSyntaxHighlighter
import javax.swing.Icon

class OCamlColorSettingsPage : ColorSettingsPage {
    override fun getDisplayName(): String = "OCaml"
    override fun getIcon(): Icon = OCamlIcons.FileTypes.OCAML_SOURCE
    override fun getAttributeDescriptors() = Constants.ATTRS
    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY
    override fun getHighlighter(): SyntaxHighlighter = OCamlSyntaxHighlighter()
    override fun getAdditionalHighlightingTagToDescriptorMap() = Constants.ANNOTATOR_TAGS
    override fun getDemoText() = Constants.DEMO_TEXT

    internal object Constants {
        val ATTRS: Array<AttributesDescriptor> = OCamlColor.entries.map{ it.attributesDescriptor }.toTypedArray()
        val ANNOTATOR_TAGS: Map<String, TextAttributesKey> = OCamlColor.entries.associateBy({ it.name }, { it.textAttributesKey })
        val DEMO_TEXT: String by lazy {
            """(* Variables and Types *)
let <GLOBAL_VARIABLE>x</GLOBAL_VARIABLE> = 10 and <GLOBAL_VARIABLE>z</GLOBAL_VARIABLE> = 'c'
let <GLOBAL_VARIABLE>y</GLOBAL_VARIABLE> = 3.14
let <GLOBAL_VARIABLE>name</GLOBAL_VARIABLE> = "OCaml" (** Hello, World! *)
let <GLOBAL_VARIABLE>t</GLOBAL_VARIABLE> = 
    let <LOCAL_VARIABLE>v</LOCAL_VARIABLE> = 5 
    in v * v

(* Pattern Matching *)
let rec <FUNCTION_DECLARATION>factorial</FUNCTION_DECLARATION> n =
  match n with
  | 0 -> 1
  | _ -> n * factorial (n - 1)

(* Functions *)
let <FUNCTION_DECLARATION>add</FUNCTION_DECLARATION> a b = a + b
let <FUNCTION_DECLARATION>square</FUNCTION_DECLARATION> x = x * x

let <FUNCTION_DECLARATION>is_even</FUNCTION_DECLARATION> n =
  if n mod 2 = 0 then
    true
  else
    false

let <FUNCTION_DECLARATION>divide</FUNCTION_DECLARATION> a b =
  try
    a / b
  with
  | Division_by_zero -> 0

(* Structures *)
let <GLOBAL_VARIABLE>list</GLOBAL_VARIABLE> = [1; 2; 3; 4; 5]
let <GLOBAL_VARIABLE>array</GLOBAL_VARIABLE> = [|1; 2; 3; 4; 5|]
let <GLOBAL_VARIABLE>pair</GLOBAL_VARIABLE> = (10, "hello")
type <TYPE>person</TYPE> = { name: string; age: int }
let <GLOBAL_VARIABLE>john</GLOBAL_VARIABLE> = { name = "John"; age = 30 }

(* Modules *)
module Math = struct
  let <GLOBAL_VARIABLE>pi</GLOBAL_VARIABLE> = 3.14
  let <FUNCTION_DECLARATION>square</FUNCTION_DECLARATION> x = x * x
end

module type Printable = sig
  val print : unit -> unit
end

module Printer (M: Printable) = struct
  let <FUNCTION_DECLARATION>print_twice</FUNCTION_DECLARATION> () =
    M.print ();
    M.print ()
end

module HelloPrinter = Printer(PrintHello)
"""
        }
    }

}