(* Warning 32: unused value x. *)
let x = 5
let x = 7

(* unused values in functor argument *)
module F (_ : sig val t : int end) = struct end

module G (X : sig val x : int end) = X

module H (X : sig val x : int end) = X