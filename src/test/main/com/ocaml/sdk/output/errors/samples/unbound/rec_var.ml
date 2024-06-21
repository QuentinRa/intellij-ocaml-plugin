(*
Error: Unbound value foobar2 Hint: Did you mean foobar1? Hint: If this is a recursive definition, you should add the 'rec' keyword on line 2
*)
let foobar1 () = () in
let foobar2 () = foobar2 () (* typo? or missing "rec"? *) in
()