type spec =
  | Unit of (unit -> unit)     (** Call the function with unit argument *)
  | Bool of (bool -> unit)     (** Call the function with a bool argument *)
  | Set of bool ref            (** Set the reference to true *)
  | Clear of bool ref          (** Set the reference to false *)
  | String of (string -> unit) (** Call the function with a string argument *)
  | Set_string of string ref   (** Set the reference to the string argument *)
  | Int of (int -> unit)       (** Call the function with an int argument *)
  | Set_int of int ref         (** Set the reference to the int argument *)
  | Float of (float -> unit)   (** Call the function with a float argument *)
  | Set_float of float ref     (** Set the reference to the float argument *)
  | Tuple of spec list         (** Take several arguments according to the
                                   spec list *)
  | Symbol of string list * (string -> unit)
                               (** Take one of the symbols as argument and
                                   call the function with the symbol *)
  | Rest of (string -> unit)   (** Stop interpreting keywords and call the
                                   function with each remaining argument *)
  | Rest_all of (string list -> unit)
                               (** Stop interpreting keywords and call the
                                   function with all remaining arguments *)
  | Expand of (string -> string array) (** If the remaining arguments to process
                                           are of the form
                                           [["-foo"; "arg"] @ rest] where "foo"
                                           is registered as [Expand f], then the
                                           arguments [f "arg" @ rest] are
                                           processed. Only allowed in
                                           [parse_and_expand_argv_dynamic]. *)
(** The concrete type describing the behavior associated
   with a keyword. *)