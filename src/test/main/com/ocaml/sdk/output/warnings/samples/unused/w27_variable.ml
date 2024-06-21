(* Warning 27: unused variable v. *)

let test_unused_variable1 () = match None with
| Some v -> ()
| None -> ()

let test_unused_variable2 () = match None with
| Some( v )-> ()
| None -> ()

let test_unused_variable3 () = match None with
| Some(v)-> ()
| None -> ()

type tuple_int = A of int * int

let test_unused_variable4 (v: tuple_int) = match v with
| A (v, _) -> 5