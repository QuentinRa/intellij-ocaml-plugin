let f x =
  match x with
  | _ -> ()
  | exception _ -> .

(*
Error: This match case could not be refuted.
       Here is an example of a value that would reach it: _
*)