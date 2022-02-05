# Changelog
All notable changes to this project will be documented in this file.
The plugin is experimental. You should implement any feature directly in the [ReasonML](https://github.com/giraud/reasonml-idea-plugin) plugin.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.0.6-EAP]
### Internal
- Keeping a Changelog

## [0.0.5] - 2022-02-01
### Added
- 🚀 can create .ml, .mli, or .ml+.mli files #34
- 🚀 highlight #34
- ✨ suggest setting up SDK if opening a file while not having set the SDK
- 🚀 REPL with variable view #32
- 🚀 Dune Language Support (from ReasonML) #33
- 🚀 Show warnings, alerts, and errors in the editor
- 🚀 Tested on Linux (finally) #14

### Changed
- 🪲 Fixing NPE/errors when saving module settings

## [0.0.4] - 2022-01-22
### Internal
- 🚀 The code is not dependent on SDK Providers (ex: WSL, Cygwin, OCaml64, etc.) any more. We can add/remove them however we want.

### Added
- 🪲 Fixing #9

### Changed
- ✨ icon for library roots, filter non-ocaml files #27
- 🚀 trying to follow IntelliJ Platform Guidelines

## [0.0.3] - 2022-01-19
### Added
- 🚀 sources are detected. You can browse them in the editor. You can add/remove them, in the project structure. #18 #19
- 🚀 can use a template (**None**, **Makefile**, or **Dune**) #7
- 🪲 Fixing #16
- 🪲 Fixing #25
- 🪲 Fixing #26
- 🚀 Improving modules' editor (adding content entry)

### Changed
- ✨ the field for the detected sources' folder in the wizard is now a label (97ab383)

## [0.0.2] - 2022-01-17
### Internal
- Targeting both 211, 212, 213, with the code on **one** branch
- Writing README, create tasks on GitHub, planning of the development
- Testing the code
- Bundle

### Added
- 🚀 Adding a project Wizard. One can create an opam-like SDK (for those that do not have opam), or create an opam SDK. 
- ✨ For non-opam SDK, fields aside from the `ocaml binary` are detected by the plugin.
- 🚀 You can use "Project SDK" as the module SDK, or you can select a custom SDK.
- 🚀 You can set the compilation output directory

### Removed
- 📘 Everything available in 0.0.1, will be added back later

## [0.0.1] - 2021-03-10
### Added
- 📘 Create an SDK -> Create a library
- 📘 Building application (CTRL-F9), see errors in the "build" window
- 📘 REPL
- 📘 Run Configuration (can execute a program)