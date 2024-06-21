type t
type nucleotide = A | C | G | T
type brin = nucleotide list
type acide = Ala | Arg | Asn | Asp | Cys | Glu | Gln | Gly | His | Ile | Leu | Lys | Phe | Pro | Ser | Thr | Trp | Tyr | Val | START | STOP

let hw = "Hello, World!@."
let x = 5 and y = 3
let u = [3; 4; 5]
let t = Some A
let big_list = [3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3;4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4;5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5;3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3;4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4;5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5;3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3; 4; 5; 3;4; 5; 3; 4; 5]
let list_c1 = [3, 4, 5, 3, 5]
let list_c2 = [(3, 4, 5, 3, 5);(3, 4, 5, 3, 5)]
let (x, y) = (5, 3)

let f1 y = 5
let f2 number1 number2 number3 number4 number5 = number1 + number2 + number3 + number4 + number5
let f3 number1 g number2 number3 number4 number5 = number1 +. g number2 +. number3 +. number4 +. number5
let f4 y = 5 and f5 y = 3
let f6 y = 0 and v = 7
let f ~x ~y = x + y

exception E1
exception E2 of int * int

type 'a option = None | Some of 'a
type ('a, 'b) result = Ok of 'a | Error of 'b

let (//) x y =
  if y = 0 then Error "Division by zero"
  else Ok (x / y)

let distance x y : int =
  let diff f s c = match f with
    | [] -> c
    | _ -> 4
    in diff x y 0

module X1 = struct end
module X2 = struct
type t = int
let compare = compare
end
module My_Set = Set.Make(X2)

module type A
module type B = sig end
module type C = sig
    type t
    val r : int
    val k : int * int
    val z : float
end