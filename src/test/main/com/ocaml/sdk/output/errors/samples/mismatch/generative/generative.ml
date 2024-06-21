(*
Error: The functor Generative is generative, it cannot be applied in type expressions
*)
module M = struct type t let equal = (=) end
module Generative() = struct type t end
type t = Generative(M).t