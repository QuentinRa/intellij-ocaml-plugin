(* Warning 8 [partial-match]: this pattern-matching is not exhaustive.
   Here is an example of a case that is not matched:
   Some true *)

let 1 = 1;;

let dont_warn_with_partial_match None x = x

let test_match_exhaustiveness () =
    match None with
    | exception _ -> ()
    | Some false -> ()
    | None -> ()

let test_match_exhaustiveness_nest1 () =
    match None with
    | Some false -> ()
    | None | exception _ -> ()

let test_match_exhaustiveness_nest2 () =
    match None with
    | Some false | exception _ -> ()
    | None -> ()

let test_match_exhaustiveness_full () =
    match None with
    | exception _ -> ()
    | Some false -> ()
    | None -> ()