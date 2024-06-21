(* if "t" is declared twice at a variable, others "t" in the
 variable view should not be deleted (unless this was something having the same
  type => redefinition) *)
type t
type nucleotide = A | C | G | T
let t = Some A
let t y = Some A