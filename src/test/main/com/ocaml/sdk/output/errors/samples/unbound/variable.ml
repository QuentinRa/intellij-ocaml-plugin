(*
Error: Unbound value value2 Hint: Did you mean value1?
*)
let value1 = 3 in
let value2 = value2 (* typo: should be value1 *) + 1 in
()