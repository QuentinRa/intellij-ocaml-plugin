(*
Error: This expression has type int but an expression was expected of type unit -> 'a
Hint: Did you forget to wrap the expression using `fun () ->'?
*)
let g f = f ()
let _ = g 3;;       (* missing `fun () ->' *)