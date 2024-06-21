(* unused values in functor argument *)
[@@@warning "-32-60"]
module F (X : sig val x : int end) = struct end

module G (X : sig val x : int end) = X

module H (X : sig val x : int end) = X