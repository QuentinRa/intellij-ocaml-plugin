(* Warning 5: this function application is partial, maybe some arguments are missing. *)
let _ = (Format.printf "side-effect!@."; List.iter (fun () -> ()))

(* Warning 5: this function application is partial, maybe some arguments are missing. *)

let f x y = x;;
f 1; f 1;;