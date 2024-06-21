let x = 5
let _ = 5
let _ = true
let _ = false
let _ = 5.0
let _ = "Hello, World!"
let _ = ()

type s = { n : int }
let _ = { n = 5 }

let f1 x y = ()
let f2 = fun x -> fun y -> ()

let derivative dx f = fun x -> (f (x +. dx) -. f x) /. dx

let li = 1::2::3::[]
let _ = [1; 2; 3]

type expr = Num of int | Var of string | Let of string * expr * expr | Binop of string * expr * expr

let rec eval env = function
| Num i -> i
| Var x -> List.assoc x env
| Let (x, e1, in_e2) ->
   let val_x = eval env e1 in
   eval ((x, val_x) :: env) in_e2
| Binop (op, e1, e2) ->
   let v1 = eval env e1 in
   let v2 = eval env e2 in
   eval_op op v1 v2
and eval_op op v1 v2 = match op with
    | "+" -> v1 + v2
    | "-" -> v1 - v2
    | "*" -> v1 * v2
    | "/" -> v1 / v2
    | _ -> failwith ("Unknown operator: " ^ op);;

(* Français *)
let _ = "Some word"