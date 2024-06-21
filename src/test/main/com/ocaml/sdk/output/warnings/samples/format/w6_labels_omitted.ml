(* Warning 6: label bar was omitted in the application of this function. *)
let foo ~bar = ignore bar (* one label *)
let bar ~foo ~baz = ignore (foo, baz) (* two labels *)

let () = foo 2
let () = bar 4 2