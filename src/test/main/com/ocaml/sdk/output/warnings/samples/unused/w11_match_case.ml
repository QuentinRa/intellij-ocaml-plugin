(* Warning 11 [redundant-case]: this match case is unused. *)

let test_case () =
    match None with
    | Some _ -> ()
    | Some _ -> ()
    | None -> ()