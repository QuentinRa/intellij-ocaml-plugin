(*
Error: This expression has type unit -> int
but an expression was expected of type int
Hint: Did you forget to provide `()' as argument?
*)
let x = read_int in   (* missing unit argument *)
print_int x;;