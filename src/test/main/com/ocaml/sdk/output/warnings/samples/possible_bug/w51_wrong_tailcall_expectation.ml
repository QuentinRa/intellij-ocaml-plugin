(* Warning 51: expected tailcall *)
let rec foldl op acc = function
    [] -> acc
    | x :: xs ->
        try (foldl [@tailcall]) op (op x acc) xs
        with Not_found -> assert false

let rec fact = function
  | 1 -> 1
  | n -> n * (fact [@tailcall true]) (n-1)