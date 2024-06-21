let m1 k v = k v
(* cannot be inferred *)
let m2 k v = v k
(* infer k and v *)
let rec m3 k v = m3 k v