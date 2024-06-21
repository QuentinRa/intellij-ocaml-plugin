open Hello_world
(*open Alone*)

include Hello_world
let v = hello_world ()
let v = Hello_world.hello_world ()
let v = Hello_world.hello_world ()

module Test(X: Set.OrderedType) = struct
end