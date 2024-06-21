let f x y = 5

let _ = f 1 0.0
let _ = f 1 "0.0"
let _ = f 1 (* this is stopping type inference *) 3

let x s = f 1 s

(* not inferred "y", because the wrong x is resolved *)
let _ = x 0

(* ensure no "(" and "s" around s *)
let var(s) = "toto"
let x = var "x"

(* bug inferred check 2 *)
let nes t =
    let nx f (k: float list) = function
    | [] -> []
    | _ -> []
    in nx (nx [] []) []

let nes t =
    let nx f (k: int list) = function
    | [] -> []
    | _ -> []
    in nx (nx [] []) []

(* bug parenthesis marked as k *)
let nes t =
    let nx f k = function
    | [] -> []
    | _ -> []
    in nx (nx []) []