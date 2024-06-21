open Toto
open Tata

let hw = Hello_world.hello_world

module M (X: Set.OrderedType) = struct end

module S = Set.Make(struct type t = int let compare = Stdlib.compare end)