(*
Warning 4: this pattern-matching is fragile. It will remain exhaustive when constructors are added to type t2.
*)

type t2 = A | B | C

let y (y:t2) = match y with
| A -> 0
| _ -> 0