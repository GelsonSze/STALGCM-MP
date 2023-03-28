# STALGCM-MP

This machine project aims to determine the equivalence of N number of NFAs (Nondeterministic Finite Automata).

Input syntax is as follows:
```
<Number of Machines>
  
<NFA Name> (For each N machine)
|Q|
<|Q| lines follow, each with a state name>
|S|
<|S| lines follow, each with a stimulus symbol>
|δ|
<|δ|, each with a transition of the format <src> <stimulus> <dest>>
q_I - initial state (guaranteed to only have one)
|F|
<|F| lines follow, each with a final state, which is a valid member of Q from above>
  
e.g.

3

M1
2
A
B
2
0
1
4
A 0 A
A 1 B
B 0 A
B 1 B
A
1
B

M2
3
A
B
C
2
0
1
8
A 0 A
A 0 B
A 1 C
C 1 A
C 1 C
B 0 B
B 1 B
B 1 C
A
2
A
B

M3
3
A
B
C
2
0
1
6
A 0 A
A 1 B
B 0 C
B 1 B
C 0 A
C 1 B
A
1
B
```
