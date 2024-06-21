let fx x y =
    let z = x + y
    in z

let m k v = k v

let rec f first second =
    let z = first
    (* bug, only because first is a variable *)
    in f z second

let rec f a b =
    let z = a
    (* bug, only because first is a variable *)
    in fx a b