(* FIXED, the *\) was ignored, as this matched the (\** (start of the documentation) *)

type t = bool = false | true (**)
(** The type of booleans (truth values).

    The constructors [false] and [true] are included here so that they have
    paths, but they are not intended to be used in user-defined data types.
 *)
