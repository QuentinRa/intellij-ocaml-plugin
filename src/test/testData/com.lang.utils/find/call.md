# lists
* [ ] `let hd l _ = List.hd l`
* [ ] `let _ = hd [0;3;5] 0`
* [ ] `let _ = hd [0;3;5] 7`
* [ ] `let _ = List.hd [0;3;5]`

# Labeled

* [ ] `(*let f ~x ~y = x - y*)`
* [ ] `(*let _ = f ~x: 3 ~y: 5*)`

# optional

* [ ] (*`let bump ?(step = 1) x = x + step*)`
* [ ] (*`let _ = bump 2*)`
* [ ] (*`let _ = bump ~step:3 2*)`
* [ ] (*`let test ?(x = 0) ?(y = 0) () ?(z = 0) () = (x, y, z)*)`
* [ ] (*`let _ = test ~x:2 () ~z:3 ()*)`