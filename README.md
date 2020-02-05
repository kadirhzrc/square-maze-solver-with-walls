# square-maze-solver-with-walls
A Java program which takes a predefined .txt input format to solve a square nxn size maze. First term project for CSE4082 AI course.

Search methods included in the program are:
1- Depth First Search
2- Breadth First Search
3- Iterative Deepening Search
4- Uniform Cost Search
5- Greedy Best First Search
6- A* Heuristic Search

maze.txt example input:
size 8 -> 8x8 maze
walls 2,0,2,1 3,1,4,1 (x1,y1,x2,y2) -> wall coordinates, x1,y1 being the smaller one. Separate with a space to add multiple
traps 4,2 -> Going through a trap costs 6 by default. Cost variable search methods will be affected.
start 2,3 -> Starting coordinate
goals 7,3 -> Goal coordinate (can be multiple)

Leave a space instead of a coordinate if there is an unwanted feature.
e.g
traps 
start 2,3
...

