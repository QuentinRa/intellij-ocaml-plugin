let parse t = ()
(** [Arg.parse speclist anon_fun usage_msg] parses the command line.
    [speclist] is a list of triples [(key, spec, doc)].
    [key] is the option keyword, it must start with a ['-'] character.
    [spec] gives the option type and the function to call when this option
    is found on the command line.
    [doc] is a one-line description of this option.
    [anon_fun] is called on anonymous arguments.
    The functions in [spec] and [anon_fun] are called in the same order
    as their arguments appear on the command line.

    If an error occurs, [Arg.parse] exits the program, after printing
    to standard error an error message as follows:
-   The reason for the error: unknown option, invalid or missing argument, etc.
-   [usage_msg]
-   The list of options, each followed by the corresponding [doc] string.
    Beware: options that have an empty [doc] string will not be included in the
    list.

    For the user to be able to specify anonymous arguments starting with a
    [-], include for example [("-", String anon_fun, doc)] in [speclist].

    By default, [parse] recognizes two unit options, [-help] and [--help],
    which will print to standard output [usage_msg] and the list of
    options, and exit the program.  You can override this behaviour
    by specifying your own [-help] and [--help] options in [speclist].
*)