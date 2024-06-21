(*
Error: The module X.F is abstract, it cannot be applied
*)
module F(X : sig module type S module F : S end) = struct
  type t = X.F(Parsing).t
end