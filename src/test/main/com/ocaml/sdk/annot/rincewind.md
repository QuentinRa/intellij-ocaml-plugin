# Rincewind Adapter

First line before every output is "__|/home/user/Bureau/untitled6/test.ml|/home/usr/Bureau/untitled6"

<table>
<tr><th></th><th>in</th><th>out</th><th>annot</th></tr>

<tr><td>1</td><td>
let x = 5
</td><td>
Va|1.4,1.5|x|int
</td><td>
<pre>
"test.ml" 1 0 4 "test.ml" 1 0 5
type(
  int
)
ident(
  def x "test.ml" 1 0 9 "test.ml" 0 0 -1
)
"test.ml" 1 0 8 "test.ml" 1 0 9
type(
  int
)
</pre>
</td></tr>


<tr><td>2</td><td>
let x = 5<br>
let y = 5
</td><td>
Va|1.4,1.5|x|int<br>
Va|2.4,2.5|y|int
</td><td>

<pre>
"test.ml" 1 0 4 "test.ml" 1 0 5
type(
  int
)
ident(
  def x "test.ml" 2 10 10 "test.ml" 0 0 -1
)
"test.ml" 1 0 8 "test.ml" 1 0 9
type(
  int
)
"test.ml" 2 10 14 "test.ml" 2 10 15
type(
  int
)
ident(
  def y "test.ml" 2 10 19 "test.ml" 0 0 -1
)
"test.ml" 2 10 18 "test.ml" 2 10 19
type(
  int
)
</pre>
</td></tr>

<tr><td>3</td><td>
let x = 5 and y = 5
</td><td>
Va|1.4,1.5|x|int<br>
Va|1.14,1.15|y|int
</td><td>
<pre>
"test.ml" 1 0 4 "test.ml" 1 0 5
type(
  int
)
ident(
  def x "test.ml" 1 0 19 "test.ml" 0 0 -1
)
"test.ml" 1 0 8 "test.ml" 1 0 9
type(
  int
)
"test.ml" 1 0 14 "test.ml" 1 0 15
type(
  int
)
ident(
  def y "test.ml" 1 0 19 "test.ml" 0 0 -1
)
"test.ml" 1 0 18 "test.ml" 1 0 19
type(
  int
)
</pre>
</td></tr>

<tr><td>4</td><td>

<pre>
let x =
    let y = 5
    in y
</pre>
</td><td>

Va|1.4,1.5|x|int<br>
Va|2.8,2.9|y|int<br>
Id|3.7,3.8|y|y|int
</td><td>
<pre>
"test.ml" 1 0 4 "test.ml" 1 0 5
type(
  int
)
ident(
  def x "test.ml" 3 22 30 "test.ml" 0 0 -1
)
"test.ml" 2 8 16 "test.ml" 2 8 17
type(
  int
)
ident(
  def y "test.ml" 3 22 29 "test.ml" 3 22 30
)
"test.ml" 2 8 20 "test.ml" 2 8 21
type(
  int
)
"test.ml" 3 22 29 "test.ml" 3 22 30
type(
  int
)
ident(
  int_ref y "test.ml" 2 8 16 "test.ml" 2 8 17
)
"test.ml" 2 8 12 "test.ml" 3 22 30
type(
  int
)
</pre>
</td></tr>

<tr><td>5</td><td>

<pre>
let mx a b = max a b
</pre>
</td><td>
Va|1.4,1.6|mx|'a -> 'a -> 'a<br>
Id|1.13,1.16|max|max|'a -> 'a -> 'a<br>
Id|1.17,1.18|a|a|'a<br>
Id|1.19,1.20|b|b|'a
</td><td>
<pre>
"test.ml" 1 0 4 "test.ml" 1 0 6
type(
  'a -> 'a -> 'a
)
ident(
  def mx "test.ml" 1 0 20 "test.ml" 0 0 -1
)
"test.ml" 1 0 7 "test.ml" 1 0 8
type(
  'a
)
ident(
  def a "test.ml" 1 0 9 "test.ml" 1 0 20
)
"test.ml" 1 0 9 "test.ml" 1 0 10
type(
  'a
)
ident(
  def b "test.ml" 1 0 13 "test.ml" 1 0 20
)
"test.ml" 1 0 13 "test.ml" 1 0 16
type(
  'a -> 'a -> 'a
)
ident(
  int_ref Stdlib.max "stdlib.mli" 186 7963 7963 "stdlib.mli" 186 7963 7987
)
"test.ml" 1 0 17 "test.ml" 1 0 18
type(
  'a
)
ident(
  int_ref a "test.ml" 1 0 7 "test.ml" 1 0 8
)
"test.ml" 1 0 19 "test.ml" 1 0 20
type(
  'a
)
ident(
  int_ref b "test.ml" 1 0 9 "test.ml" 1 0 10
)
"test.ml" 1 0 13 "test.ml" 1 0 20
type(
  'a
)
</pre>
</td></tr>

<tr><td>6</td><td>

<pre>
module type A

module Make(F: A) = struct end
</pre>
</td><td>
Md|3.0,3.30|Make
</td><td>
<pre>
"test.ml" 3 15 22 "test.ml" 3 15 26
ident(
  def Make "test.ml" 3 15 45 "test.ml" 0 0 -1
)
</pre>
</td></tr>

<tr><td>7</td><td>

<pre>
module Make = struct
    let x = 5
end
</pre>
</td><td>
Md|1.0,3.3|Make<br>
Va|2.5,2.6|x|int
</td><td>
<pre>
"test.ml" 1 0 7 "test.ml" 1 0 11
ident(
  def Make "test.ml" 3 35 38 "test.ml" 0 0 -1
)
"test.ml" 2 21 29 "test.ml" 2 21 30
type(
  int
)
ident(
  def x "test.ml" 2 21 34 "test.ml" 3 35 38
)
"test.ml" 2 21 33 "test.ml" 2 21 34
type(
  int
)
</pre>
</td></tr>

<tr><td>8</td><td><pre>
type t = int
exception E of int * int
</pre>
</td><td>
empty
</td><td>
empty
</td></tr>

<tr><td>9</td><td><pre>
let _ = "Hello, world!"
let rec f x = x
</pre>
</td><td>
Va|2.8,2.9|f|'a -> 'a<br>
Id|2.14,2.15|x|x|'a
</td><td>
<pre>
"test.ml" 1 0 4 "test.ml" 1 0 5
type(
  string
)
"test.ml" 1 0 8 "test.ml" 1 0 23
type(
  string
)
"test.ml" 2 24 32 "test.ml" 2 24 33
type(
  'a -> 'a
)
ident(
  def f "test.ml" 2 24 24 "test.ml" 0 0 -1
)
"test.ml" 2 24 34 "test.ml" 2 24 35
type(
  'a
)
ident(
  def x "test.ml" 2 24 38 "test.ml" 2 24 39
)
"test.ml" 2 24 38 "test.ml" 2 24 39
type(
  'a
)
ident(
  int_ref x "test.ml" 2 24 34 "test.ml" 2 24 35
)
</pre>
</td></tr>

<tr><td>10</td><td><pre>
let x = function ()
| _ -> ()
</pre>
</td><td>
Va|1.4,1.5|x|unit -> unit
</td><td>
<pre>
"test.ml" 1 0 4 "test.ml" 1 0 5
type(
  unit -> unit
)
ident(
  def x "test.ml" 2 20 29 "test.ml" 0 0 -1
)
"test.ml" 1 0 17 "test.ml" 1 0 19
type(
  unit
)
"test.ml" 2 20 22 "test.ml" 2 20 23
type(
  unit
)
"test.ml" 1 0 17 "test.ml" 2 20 23
type(
  unit
)
"test.ml" 2 20 27 "test.ml" 2 20 29
type(
  unit
)
"test.ml" 1 0 8 "test.ml" 2 20 29
type(
  unit -> unit
)
</pre>
</td></tr>

<tr><td>11</td><td><pre>
class type name = object
	method name : int * int
end
</pre>
</td><td>
aucun
</td><td>
aucun
</td></tr>

<tr><td>12</td><td><pre>
class stack_of_ints =
    object (self)
      val mutable the_list = ([] : int list)
    end
</pre>
</td><td>
aucun
</td><td>
<pre>
"test.ml" 2 22 33 "test.ml" 2 22 39
type(
  < .. >
)
"test.ml" 3 40 70 "test.ml" 3 40 72
type(
  int list
)
</pre>
</td></tr>

<tr><td>12</td><td><pre>
class stack_of_ints =
    object (self)
      val mutable the_list = ([] : int list)
    end

let s = new stack_of_ints;;
</pre>
</td><td>
Va|5.4,5.5|s|stack_of_ints
</td><td>
<pre>
"test.ml" 2 22 33 "test.ml" 2 22 39
type(
  < .. >
)
"test.ml" 3 40 70 "test.ml" 3 40 72
type(
  int list
)
"test.ml" 5 93 97 "test.ml" 5 93 98
type(
  stack_of_ints
)
ident(
  def s "test.ml" 5 93 118 "test.ml" 0 0 -1
)
"test.ml" 5 93 101 "test.ml" 5 93 118
type(
  stack_of_ints
)
</pre>
</td></tr>

<tr><td>13</td><td><pre>
module E = Set.Make(
struct type t = int let compare = compare end
)
</pre>
</td><td>
???
</td><td>
<pre>
"test.ml" 1 0 44 "test.ml" 1 0 51
type(
  'a -> 'a -> int
)
ident(
  def compare "test.ml" 1 0 61 "test.ml" 1 0 65
)
"test.ml" 1 0 54 "test.ml" 1 0 61
type(
  'a -> 'a -> int
)
ident(
  int_ref Stdlib.compare "stdlib.mli" 93 3855 3855 "stdlib.mli" 93 3855 3902
)
"test.ml" 1 0 11 "test.ml" 1 0 66
call(
  stack
)
</pre>
</td></tr>

<tr><td>14 (bug)</td><td><pre>
let id s = s
let _ = id id id id 0
</pre>
</td><td>
???
</td><td>
<pre>
"test.ml" 58 763 771 "test.ml" 58 763 773
type(
  ((int -> int -> int) -> int -> int -> int) ->
  (int -> int -> int) -> int -> int -> int
)
ident(
  int_ref id "test.ml" 56 732 736 "test.ml" 56 732 738
)
</pre>
</td></tr>

<tr><td>15 (bug)</td><td><pre>
let hd (l: 'a list)
= List.hd l
</pre>
</td><td>
???
</td><td>
<pre>
"err.ml" 1 0 8 "err.ml" 1 0 9
type(
  'a list
)
type(
  'a list
)
ident(
  def l "err.ml" 2 20 22 "err.ml" 2 20 31
)
</pre>
</td></tr>

<tr><td>16 (bug)</td><td><pre>
let bump ?(step = 1) x = x + step
</pre>
</td><td>
???
</td><td>
<pre>
"err.ml" 1 0 18 "err.ml" 1 0 19
type(
  int
)
type(
  int option
)
type(
  int option
)
type(
  int
)
ident(
  int_ref *sth* "err.ml" 1 0 18 "err.ml" 1 0 19
)
type(
  int
)
type(
  int option
)
type(
  int option
)
ident(
  def *sth* "err.ml" 1 0 18 "err.ml" 1 0 19
)
</pre>
</td></tr>

<tr><td>17 (bug)</td><td><pre>
let _ = hello_world ()
</pre>
</td><td>
???
</td><td>
<pre>
"test_hello_world.ml" 3 18 26 "test_hello_world.ml" 3 18 40
call(
  stack
)
type(
  unit
)
</pre>
</td></tr>
</table>