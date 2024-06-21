module type Something = sig
    type t
	val empty: int
	val add: int -> int
end

module Make (X: Set.OrderedType) : Something with type t = X.t = struct
    let empty = 5
    let add _ = 5
end

(*
File "file.ml", line 7, characters 65-114:
Error: Signature mismatch:
       Modules do not match:
         sig val empty : int val add : 'a -> int end
       is not included in
         sig type t = X.t val empty : int val add : int -> int end
       The type `t' is required but not provided
       File "file.ml", line 7, characters 50-62: Expected declaration
*)