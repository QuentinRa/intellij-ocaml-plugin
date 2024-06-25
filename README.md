# intellij-ocaml

[![Checks](https://badgen.net/github/checks/QuentinRa/intellij-ocaml/)](https://github.com/QuentinRa/intellij-ocaml/actions)
[![Version](https://img.shields.io/jetbrains/plugin/v/18531-ocaml.svg)](https://plugins.jetbrains.com/plugin/18531-ocaml)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/18531-ocaml.svg)](https://plugins.jetbrains.com/plugin/18531-ocaml)
[![License: MIT](https://badgen.net/github/license/quentinRa/intellij-ocaml?color=yellow)](LICENSE)
![Dependabot: Active](https://badgen.net/github/dependabot/QuentinRa/intellij-ocaml/)
[![0.2.0](https://badgen.net/github/milestones/quentinra/intellij-ocaml/1)](https://github.com/QuentinRa/intellij-ocaml/milestone/1)
[![0.3.0](https://badgen.net/github/milestones/quentinra/intellij-ocaml/2)](https://github.com/QuentinRa/intellij-ocaml/milestone/2)
[![0.4.0](https://badgen.net/github/milestones/quentinra/intellij-ocaml/3)](https://github.com/QuentinRa/intellij-ocaml/milestone/3)
[![0.5.0](https://badgen.net/github/milestones/quentinra/intellij-ocaml/4)](https://github.com/QuentinRa/intellij-ocaml/milestone/4)
[![0.6.0](https://badgen.net/github/milestones/quentinra/intellij-ocaml/5)](https://github.com/QuentinRa/intellij-ocaml/milestone/8)
[![0.7.0](https://badgen.net/github/milestones/quentinra/intellij-ocaml/6)](https://github.com/QuentinRa/intellij-ocaml/milestone/6)


At least for now, the repository is **unmaintained**. [![No Maintenance Intended](http://unmaintained.tech/badge.svg)](http://unmaintained.tech/)

This plugin is providing support for OCaml for JetBrains IDEs. It's based on both ReasonML and intellij-rust. If you are looking for a complete and maintained solution, please check out the [ReasonML](https://github.com/giraud/reasonml-idea-plugin) plugin (that is based on [ocaml-ide](https://github.com/sidharthkuruvila/ocaml-ide) btw). I'm a student, and I'm lacking the knowledge to do something great.

[Getting Started 🗺️](https://plugins.jetbrains.com/plugin/18531-ocaml/documentation/getting-started) | [Features 🚀](https://plugins.jetbrains.com/plugin/18531-ocaml/documentation/features)

## Plugin Features Overview

The goal is to have a plugin similar to VSCode with OCaml LSP server, but without the LSP server as it is not fully available for plugin developers yet, and as it exclusive to paid users.

**Base features** (✅ = YES, ❌ = No/Not yet, 🆗 = Must compile first)

|                           | OCaml         | ReasonML | VSCode        | VSCode LSP |
|---------------------------|---------------|----------|---------------|------------|
| Highlighter               | ✅             | ✅        | ✅             | ✅          |
| Find Usages               | ❌             | ✅        | ❌             | ✅          |
| Type inference (codelens) | ❌             | ❌        | ❌             | ✅          |
| Type checking             | ❌             | ❌        | ❌             | ✅          |
| Autocompletion            | ❌             | ✅        | ✅<sup>1</sup> | ✅          |
| Live Templates/Snippets   | ✅             | ✅        | ✅             | ✅          |
| Debugging                 | ❌             | ?        | ?             | ?          |
| Navigation <sup>2</sup>   | ✅<sup>3</sup> | ✅        | ❌             | ✅          |
| Warnings                  | ❌             | ❌        | ❌             | ✅          |
| Error                     | ❌             | ❌        | ❌             | ✅          |

<sup>1</sup> Autocompletion is not context sensitive

<sup>2</sup> From a function/type/etc. to its declaration/implementation

<sup>3</sup> Minimal support (only for functions, and not all function calls)

**Build Systems** (✅ = Triggered from the editor, ❌ = Manually, 🆗 = Using An External Plugin, 😓 = Syntax Highlight Only)

|                      | OCaml | ReasonML | VSCode | VSCode (LSP)   |
|----------------------|-------|----------|--------|----------------|
| Native Build         | ❌     | ❌        | ❌      | ❌              |
| Dune integration     | ✅    | 😓       | ❌      | ✅ <sup>3</sup> |
| Makefile integration | ?     | ?        | ?      | ?              |
| REPL integration     | ❌     | ❌        | ✅      | ✅              |

<sup>3</sup> Run "build task" and select which dune file to build.

**IntelliJ Features**

|                              | OCaml | ReasonML | VSCode | VSCode LSP |
|------------------------------|-------|----------|--------|------------|
| Structure/Outline View       | ✅     | ✅        | ❌      | ✅          |
| Spell-checker                | ✅     | ✅        | ❌      | ❌          |
| Braces Matching              | ✅     | ✅        | ✅      | ✅          |
| Comment Handler (line/block) | ✅     | ✅        | ✅      | ✅          |
| Navigate to declaration      | ✅     | ✅        | ❌      | ✅          |
| Navigate to implementation   | ✅     | ✅        | ❌      | ✅          |
| Refactoring                  | ❌     | ?        | ❌      | ✅          |
| UML Generation               | ❌     | ❌        | ❌      | ?          |
| Parser Recovery <sup>4</sup> | ✅     | ❌        | ❌      | ✅          |

<sup>4</sup> The ability of the plugin to function with a file that doesn't compile

**External Dependencies Integration**

|             | OCaml | ReasonML | VSCode |
|-------------|-------|----------|--------|
| Opam        | ❌     | ✅        | ✅      |
| OCamlFormat | ❌     | ✅        | ✅      |

## 🎯 What's my goal?

* [ ] Can run/compile ocaml files (without having to install some external build system)
* [ ] Support for opam (can install / update packages, ...)
* [ ] Support for Makefile
* [ ] Support for Dune
* [x] Any installation must be simple, and effortless
* [ ] **Real-time checks for errors/warnings**, including quick fixes. Can suppress warnings.
* [x] **Real-time type inference**
* [ ] **Smart auto-completion**
* [x] **REPL support**: we must be able to send commands from our file, to a REPL console
* [x] **Using a bundle** (possible support for Localization later)
* [x] Working in minor IDEs (CLion, PHPStorm, etc.)

## 🛣️ Project Notes

To update the gradle project to the latest version:

* The plugin is based on [the official template](https://github.com/JetBrains/intellij-platform-plugin-template)
* Manually check important files for changes (`build.gradle.kts`, `gradle/libs.versions.toml`, `gradle/wrapper/gradle-wrapper.properties`, etc.)

To build the plugin (🪦 please make this easier...):

* Download the project and run a gradle build
* Locate `OCaml.bnf` and generate OCamlParser
* Locate `OCaml.bnf` and use the commented out `parserClass` and the commented out `unit_interface` instead of the current `parserClass` and `unit_implementation` (respectively). This will generate the OCamlInterfaceParser
* Locate `Dune.bnf` and generate DuneParser
* Locate `_OCamlLexer.flex` to generate _OCamlLexer
* Locate `_DuneLexer.flex` to generate _DuneLexer
* You can now use `Run Plugin`

## 📄 License

This project is [MIT licensed](LICENSE).