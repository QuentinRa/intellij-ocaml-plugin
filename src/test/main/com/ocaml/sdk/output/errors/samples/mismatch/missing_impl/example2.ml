(*
Error: Modules do not match:
sig type t = M.t val equal : 'a -> 'a -> bool end
is not included in Set.OrderedType
The value `compare' is required but not provided
File "set.mli", line 55, characters 4-31: Expected declaration
*)
module M = struct type t let equal = (=) end
type t = Set.Make(M).t