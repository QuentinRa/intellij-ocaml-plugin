(* Warning 60: unused module X. *)
module F (X : sig end) = struct
end

(* private and unused *)
module M = struct
  let f x = x
  let g x = x
  let h x = x
  and i x = x
  let j x = x
  and k x = x
end