(* Warning 53: the "deprecated" attribute cannot appear in this context *)
exception B [@@deprecated]

let _ = B

let i x = x [@inlined]

module J = Set.Make [@@inlined]
module J' = Set.Make [@@ocaml.inlined]

let n x = x [@ocaml.tailcall]