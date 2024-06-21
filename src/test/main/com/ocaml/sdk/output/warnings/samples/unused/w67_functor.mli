(* value in functor argument *)
module F (X : sig val x : int end) : sig end

module G (X : sig val x : int end) : sig end

module H (X : sig val x : int end) : sig val x : int end