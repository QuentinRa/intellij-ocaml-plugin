let _ = max 5 7

let _ = max (5 + max 5 7) (7 + 3)

let id v = v

let _ = id max 0 5
let _ = max 0.5 5.5

let f ~x:x1 ~y:y1 = x1 - int_of_float y1
let _ = f ~x:3 ~y:2.0
let f ~x:x1 ~y:y1 z1 = x1 - int_of_float y1 + z1
let _ = f ~x:3 ~y:2.0 3
let _ = f 3 ~x:3 ~y:2.0
let f ~x ~y = x - int_of_float y
let _ = f ~y:2.0 ~x:3

let bump ?(step = 1) x = x + step
let _ = bump 3
let _ = bump ~step:2 3
let _ = bump 3 ~step: 2

let bump ?(s1 = 1) ?(s2 = 1) x = x + s1 + s2
let _ = bump 3 ~s1: 2 ~s2: 2
let _ = bump ~s1: 2 ~s2: 2 5
let _ = bump ~s1: 2 55 ~s2: 2
let _ = bump
let _ = bump 42

(* spaces *)
let _ = f ~x:3 ~y: 2.0(*caret*)
let _ = f ~x: 3 ~y:2.0(*caret*)
let _ = f ~x: 3 ~y: 2.0(*caret*)

(* multiline *)
let _ = bump 3 ~s1: 2


~s2: 2

(* multiline with comment *)
let _ = bump 3 ~s1: 2
(* ELRaphik loves Mari *)

~s2: 2