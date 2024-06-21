let f1 = fun x -> 5
let _ = f1 0

let f2 = fun x -> fun y -> x + y
let _ = f2 0 5

(* bug y -> (y) *)
let f2' = fun x (y) -> x + y
let _ = f2' 0 5

let f3 = function a -> function b -> a + b
let _ = f3 0 7

let f4 a = function b -> a + b
let _ = f4 0 7