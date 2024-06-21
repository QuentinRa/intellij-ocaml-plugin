(*
Warning 52: Code should not depend on the actual values of this constructor's arguments. They are only for information and may change in future versions. (See manual section 11.5)
*)
let () = try () with Invalid_argument "Any" -> ();;