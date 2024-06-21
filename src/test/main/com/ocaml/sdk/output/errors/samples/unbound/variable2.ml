(*
Error: Unbound value fox Hint: Did you mean foo?
*)
let foo = 12
module M = struct
  let foo = 13
end
open M

let _ = fox;;