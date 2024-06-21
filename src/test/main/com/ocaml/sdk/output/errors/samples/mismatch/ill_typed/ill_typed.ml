(*
Error: The functor application is ill-typed.
These arguments:
X Z
do not match these parameters:
functor (X : x) (Y : y) (Z : z) -> ...
1. Module X matches the expected module type x
2. An argument appears to be missing with module type y
3. Module Z matches the expected module type z
*)
module type a
module type b
module type c

module type x = sig type x end
module type y = sig type y end
module type z = sig type z end


module type empty = sig end

module Empty = struct end
module X: x = struct type x end
module Y: y = struct type y end
module Z: z = struct type z end
module F(X:x)(Y:y)(Z:z) = struct end

module M = F(X)(Z)