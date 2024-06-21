(* cancel autocompletion in annot too *)

[@@@ocaml.deprecated {|
     As you could guess, Deprecated_module is deprecated.
     Please use something else!
   |} ]

[@@@ocaml.deprecated {|
     As you could guess, Deprecated_module is deprecated.
     Please use something else! This token is ignored : ].
   |} ]

let [@ocaml.deprecated] x = 5 [@unbox]

let[@ocaml.deprecated {|
     As you could guess, Deprecated_module is deprecated.
     Please use something else!
   |} ] x = 5 [@unbox]

type t =
  | Foo of unit [@deprecated]
  | Bar;;