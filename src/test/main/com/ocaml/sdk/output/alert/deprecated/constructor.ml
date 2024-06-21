type t =
  | Foo of unit [@deprecated]
  | Bar;;

 let x = Foo ();;
(* Alert deprecated: Foo *)

 function
 Foo _ -> () | Bar -> ();;
 (* Alert deprecated: Foo *)