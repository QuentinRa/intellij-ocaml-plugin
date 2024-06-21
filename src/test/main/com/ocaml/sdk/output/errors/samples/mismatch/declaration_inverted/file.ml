module type Something = sig
    type t
    val add: int -> int
	val empty: int
end

(*
File "file.ml", line 1:
Error: The implementation file.ml does not match the interface file.cmi:
       Module type declarations do not match:
         module type Something =
           sig type t val add : int -> int val empty : int end
       does not match
         module type Something =
           sig type t val empty : int val add : int -> int end
       At position module type Something = <here>
       Illegal permutation of structure fields

Sometimes, we got (4.12.0+)
         For example,
         the value "add" and the value "empty" are not in the same order
         in the expected and actual module types.
*)