module type Something = sig
    type t
	val empty: int
	val add: int -> int
end

module Make (X: Set.OrderedType) : Something with type t = X.t