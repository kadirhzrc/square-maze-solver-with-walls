import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;

/* Kadir Hizarci
 * AI Project I
 * 150116004
 */

public class Main {
	static String[] walls = new String[100];
	static String[] traps = new String[100];
	static String start, line;
	static int size, startCoorx, startCoory, numOfGoals, numOfWalls, numOfTraps, levelCounter,
	maxLevel, nodeCounter;
	static int[] goalsCoorx = new int[100];
	static int[] goalsCoory = new int[100];
	static int[] wallsStartx = new int[1000];
	static int[] wallsStarty = new int[1000];
	static int[] trapsCoorx = new int[100];
	static int[] trapsCoory = new int[100];
	static int[] wallsEndx = new int[1000];
	static int[] wallsEndy = new int[1000];
	static boolean pathFound;
	static Stack<Node> depthStack = new Stack<>();
	static Node solution = new Node();
	static Node[] visited = new Node[100000];
	static Queue<Node> queue = new LinkedList<>();
	// Comparators sort each Node object in the priority queue depending on their comparing function
	static Comparator<Node> uniformCompare = Comparator.comparing(Node::getCost);	
	static Comparator<Node> greedyCompare = Comparator.comparing(Node::getHeuristics);
	static Comparator<Node> aStarCompare = Comparator.comparing(Node::getHeuristicsPlusCost);
	static PriorityQueue<Node> priority;
	
	
	public static void main(String[] args) throws Exception {
		readMaze();	// Reads maze elements from maze.txt from the same directory
		int option;	// Gets an input from user choosing a specific search
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter the search you want to initiate: \n 1 for Depth First Search \n 2 for "
				+ "Breadth First Search \n 3 for Iterative Deepening Search \n 4 for Uniform Cost Search"
				+ "\n 5 for Greedy Best First Search \n 6 for A* Search");
		option = scan.nextInt();
		
		if(option == 1)
			depthFirst();
		else if(option == 2)
			breadthFirst();
		else if(option == 3)
			iterativeDeep();
		else if(option == 4)
			uniformCost();
		else if(option == 5)
			greedyBest();
		else if(option == 6)
			AStar();
		else
			System.out.println("Invalid choice.");
	}	// end of main function
	
	// read maze from text
		public static void readMaze() throws Exception {
			File file = new File("maze.txt").getAbsoluteFile();	// get maze.txt
			BufferedReader br = new BufferedReader(new FileReader(file)); // read from buffer
			
			String temp, temp2;  
			int elementCounter = 0;
			
			// get size
			line = br.readLine();
			line = line.substring(line.lastIndexOf(" ") + 1);
			size = Integer.parseInt(line);
			
			// get walls
			line = br.readLine();
			elementCounter = countSpace(line);
			numOfWalls = elementCounter;	// keeps number of walls
			
			for(int k=0; k<elementCounter; k++) {	// for each wall, set starting and ending coordinates
				temp = line.substring((6 + 8 * k),(13 + 8 * k));
				
				temp2 = temp.substring(0,1);
				wallsStartx[k] = Integer.parseInt(temp2);	
				
				temp2 = temp.substring(2,3);
				wallsStarty[k] = Integer.parseInt(temp2);

				temp2 = temp.substring(4,5);
				wallsEndx[k] = Integer.parseInt(temp2);

				temp2 = temp.substring(6,7);
				wallsEndy[k] = Integer.parseInt(temp2);
			}
			
			// get traps
			line = br.readLine();
			elementCounter = countSpace(line);
			numOfTraps = elementCounter;
			
			
			for(int k=0; k<elementCounter; k++) {	// for each trap, set coordinates
				temp = line.substring((6 + 4 * k),(9 + 4 * k));
				
				temp2 = temp.substring(0,1);
				trapsCoorx[k] = Integer.parseInt(temp2);
				
				temp2 = temp.substring(2,3);
				trapsCoory[k] = Integer.parseInt(temp2);
			}
			
			// get start
			line = br.readLine();
			start = line.substring(6,9);
			
			// set start coordinates to static variables for future use
			temp = start.substring(0,1);
			startCoorx = Integer.parseInt(temp);
			
			temp = start.substring(2,3);
			startCoory = Integer.parseInt(temp);
			
			// get goals
			line = br.readLine();
			elementCounter = countSpace(line);
			numOfGoals = elementCounter;
			
			for(int k=0; k<elementCounter; k++) {	// set each goal to variables
				temp = line.substring((6 + 4 * k),(9 + 4 * k));
				
				temp2 = temp.substring(0,1);
				goalsCoorx[k] = Integer.parseInt(temp2);
				
				temp2 = temp.substring(2,3);
				goalsCoory[k] = Integer.parseInt(temp2);
			}
		}	// end of readMaze function
	
	public static void depthFirst() {	// start of depth first search
		Node temp = new Node();	// create temporary node
		nodeCounter = 0;	// keeps number of visited nodes
		pathFound = false;	// when a path's found, sets to true
		
		Node rootNode = new Node(startCoorx, startCoory, 0);	// create root node
		push(rootNode);	// push root node to stack
		
		while(!pathFound) {	// pop the next node in stack until a path is found
			pop();
		}
		
		if(pathFound) {	// print the traversed nodes, solution path and cost
			temp = solution;
			printSolution(temp);
		}
	}	// end of depth first search
	
	public static void breadthFirst() {	// start of breadth first search
		Node temp = new Node();
		nodeCounter = 0;
		pathFound = false;
		
		Node rootNode = new Node(startCoorx, startCoory, 0);
		queue.add(rootNode);	// same as depth first search but instead there is a queue instead of a stack
		
		while(!pathFound) {
			remove();	// remove from queue until path is found
		}
		if(pathFound) {	// print the traversed nodes, solution path and cost
			temp = solution;
			printSolution(temp);
		}
	}	// end of breadth first search
	
	
	public static void iterativeDeep() {	// start of iterative deepening search
		Node temp = new Node();
		nodeCounter = 0;
		pathFound = false;
		
		Node rootNode = new Node(startCoorx, startCoory, 0);
		push(rootNode);	// push to queue
		
		// maximum possible depth is 100
		// set level to 0 and increment by 1 until the solution is found
		for(maxLevel=0; maxLevel <100 && !pathFound; maxLevel++) {	
			// upon each failed iteration, clear the stack, visited array and counter
			nodeCounter = 0;
			Arrays.fill(visited, null);
			depthStack.clear();
			
			rootNode.coorX = startCoorx;
			rootNode.coorY = startCoory;
			push(rootNode);
			while(!pathFound) {
				if(depthStack.size() == 0)	// if stack is empty, move on to next iteration,
					break;	// break and increase max level by 1
				popIterative();	// pop until solution is found
			}
		}
		
		
		if(pathFound) {	// print the traversed nodes, solution path and cost
			temp = solution;
			printSolution(temp);
		}
	}	// end of iterative deepening search
	
	
	public static void uniformCost() {	// start of uniform cost search
		priority = new PriorityQueue<>(uniformCompare);	// set comparing method of priority queue
		Node temp = new Node();
		nodeCounter = 0;
		pathFound = false;
		Node rootNode = new Node(startCoorx, startCoory, 0);
		
		priority.add(rootNode);	// add to priority queue
		while(!pathFound) {
			removeUniform();
		}
		
		if(pathFound) {	// print the traversed nodes, solution path and cost
			temp = solution;
			printSolution(temp);
		}
	}	// end of uniform cost search
	
	
	public static void greedyBest() {	// start of greedy best first search
		priority = new PriorityQueue<>(greedyCompare);
		Node temp = new Node();
		nodeCounter = 0;
		pathFound = false;
		Node rootNode = new Node(startCoorx, startCoory, 0);
		priority.add(rootNode);
		while(!pathFound) {
			removeUniform();
		}
		if(pathFound) {	// print the traversed nodes, solution path and cost
			temp = solution;
			printSolution(temp);
		}
	}	// end of greedy best first search
	
	
	public static void AStar() {
		priority = new PriorityQueue<>(aStarCompare);
		Node temp = new Node();
		nodeCounter = 0;
		pathFound = false;
		Node rootNode = new Node(startCoorx, startCoory, 0);
		priority.add(rootNode);
		
		while(!pathFound) {
			removeUniform();
		}
		if(pathFound) {	// print the traversed nodes, solution path and cost
			temp = solution;
			printSolution(temp);
		}
	}
	
	public static void printSolution(Node temp) {
		int counter = 0;
		int[] pathX = new int[temp.cost];
		int[] pathY = new int[temp.cost];
		
		System.out.println("Main node is {" + temp.coorX + "," + temp.coorY + "}\n");
		System.out.println("Solution found! Cost of solution is: " + temp.cost);
		
		while(temp.parent !=null) {	// set coordinate values in array for reverse printing
			pathX[counter] = temp.coorX;
			pathY[counter] = temp.coorY;
			temp = temp.parent;
			counter++;
		}
		System.out.println("Starting from node:");
		System.out.println("{" + startCoorx + "," + startCoory + "}");
		
		for(int k=counter-1; k>-1; k--) {	// print array
			System.out.println("{" + pathX[k] + "," + pathY[k] + "}");
		}
	}
	
	public static void pop() {
		Node node = depthStack.pop();	// pop node from the stack
		
		visited[nodeCounter] = node;	// add node to visited array
		nodeCounter++;	// increase amount of visited nodes
		Node n,e,s,w;	// declare nodes
		n = e = s = w = null;	// set to null
		
		if(!isGoalState(node)) {	// if the currently popped node is not the goal state
			System.out.println("Main node is " + "{" + node.coorX + "," + node.coorY + "}");
			if(isValid(node, "north")) {	// check if it's possible to go north
				n = new Node(node.coorX, node.coorY-1,node.cost + findCost(node.coorX,node.coorY-1));
				
				if(!isVisited(n)) {	// check if already visited, if not push north to stack
					System.out.println("Push north to the stack " + "{" + n.coorX + "," + n.coorY + "}");
					n.parent = node;
					push(n);
				}
			}
			if(isValid(node, "west")) {	// check if it's possible to go west
				w = new Node(node.coorX-1, node.coorY,node.cost + findCost(node.coorX-1,node.coorY));
				
				if(!isVisited(w)) {
					System.out.println("Push west to the stack " + "{" + w.coorX + "," + w.coorY + "}");
					w.parent = node;
					push(w);
				}
			}
			if(isValid(node, "south")) {	// check if it's possible to go south
				s = new Node(node.coorX, node.coorY+1,node.cost + findCost(node.coorX,node.coorY+1));
				
				if(!isVisited(s)) {
					System.out.println("Push south to the stack " + "{" + s.coorX + "," + s.coorY + "}");
					s.parent = node;
					push(s);
				}
			}
			if(isValid(node, "east")) {	// check if it's possible to go east
				e = new Node(node.coorX+1, node.coorY,node.cost + findCost(node.coorX+1,node.coorY));
				
				if(!isVisited(e)) {
					System.out.println("Push east to the stack " + "{" + e.coorX + "," + e.coorY + "}");
					e.parent = node;
					push(e);
				}
			}
			System.out.println();
		}
		else {	// goal state is reached, flip pathFound to true to stop the while loop
			pathFound = true;
			solution = node;
		}
	}
	
	public static void remove() {
		Node node = queue.remove();	// take the node at the front of the queue
		visited[nodeCounter] = node;
		nodeCounter++;
		Node n,e,s,w;
		n = e = s = w = null;
		
		if(!isGoalState(node)) {
			System.out.println("Main node is " + "{" + node.coorX + "," + node.coorY + "}");
			if(isValid(node, "east")) {	// check if it's possible to go east
				e = new Node(node.coorX+1,node.coorY,node.cost + findCost(node.coorX+1,node.coorY));
				
				if(!isVisited(e)) {
					System.out.println("Push east to the queue " + "{" + e.coorX + "," + e.coorY + "}");
					e.parent = node;
					queue.add(e);
				}
			}
			if(isValid(node, "south")) {	// check if it's possible to go south
				s = new Node(node.coorX,node.coorY+1,node.cost + findCost(node.coorX,node.coorY+1));
				
				if(!isVisited(s)) {
					System.out.println("Push south to the queue " + "{" + s.coorX + "," + s.coorY + "}");
					s.parent = node;
					queue.add(s);
				}
			}
			if(isValid(node, "west")) {	// check if it's possible to go west
				w = new Node(node.coorX-1, node.coorY,node.cost + findCost(node.coorX-1,node.coorY));
				
				if(!isVisited(w)) {
					System.out.println("Push west to the queue " + "{" + w.coorX + "," + w.coorY + "}");
					w.parent = node;
					queue.add(w);
				}
			}
			if(isValid(node, "north")) {	// check if it's possible to go north
				n = new Node(node.coorX, node.coorY-1,node.cost + findCost(node.coorX,node.coorY-1));
				
				if(!isVisited(n)) {
					System.out.println("Push north to the queue " + "{" + n.coorX + "," + n.coorY + "}");
					n.parent = node;
					queue.add(n);
				}
			}
			
			System.out.println();
		}
		else {
			// reached goal state
			pathFound = true;
			solution = node;
		}
	}
	
	public static void popIterative() {
		Node node = depthStack.pop();
		visited[nodeCounter] = node;
		nodeCounter++;
		Node n,e,s,w;
		n = e = s = w = null;
		
		if (depthCalculator(node) < maxLevel) {	// calculate the depth of the current node
			if(!isGoalState(node)) {	// don't push its children to stack if it is at depth limit
				System.out.println("Main node is " + "{" + node.coorX + "," + node.coorY + "}");
				if(isValid(node, "north")) {	// check if it's possible to go north
					n = new Node(node.coorX, node.coorY-1,node.cost + findCost(node.coorX,node.coorY-1));
				
					if(!isVisited(n)) {
						System.out.println("Push north to the stack " + "{" + n.coorX + "," + n.coorY + "}");
						n.parent = node;
						push(n);
					}
				}
				if(isValid(node, "west")) {	// check if it's possible to go west
					w = new Node(node.coorX-1, node.coorY,node.cost + findCost(node.coorX-1,node.coorY));
				
					if(!isVisited(w)) {
						System.out.println("Push west to the stack " + "{" + w.coorX + "," + w.coorY + "}");
						w.parent = node;
						push(w);
					}
				}
				if(isValid(node, "south")) {	// check if it's possible to go south
					s = new Node(node.coorX, node.coorY+1,node.cost + findCost(node.coorX,node.coorY+1));
				
					if(!isVisited(s)) {
						System.out.println("Push south to the stack " + "{" + s.coorX + "," + s.coorY + "}");
						s.parent = node;
						push(s);
					}
				}
				if(isValid(node, "east")) {	// check if it's possible to go east
					e = new Node(node.coorX+1, node.coorY,node.cost + findCost(node.coorX+1,node.coorY));
				
					if(!isVisited(e)) {
						System.out.println("Push east to the stack " + "{" + e.coorX + "," + e.coorY + "}");
						e.parent = node;
						push(e);
					}
				}
				System.out.println();
			}
			else {
				// reached goal state
				pathFound = true;
				solution = node;
			}
		}

	}
	
	public static void removeUniform() {
		Node node = priority.remove();
		visited[nodeCounter] = node;
		nodeCounter++;
		Node n,e,s,w;
		n = e = s = w = null;
		
		if(!isGoalState(node)) {
			System.out.println("Main node is " + "{" + node.coorX + "," + node.coorY + "}");
			if(isValid(node, "east")) {	// check if it's possible to go east

				e = new Node(node.coorX+1, node.coorY,node.cost + findCost(node.coorX+1,node.coorY));
				
				if(!isVisited(e)) {
					System.out.println("Push east to the queue " + "{" + e.coorX + "," + e.coorY + "}");
					e.parent = node;
					priority.add(e);
				}
			}
			if(isValid(node, "south")) {	// check if it's possible to go south

				s = new Node(node.coorX, node.coorY+1,node.cost + findCost(node.coorX,node.coorY+1));
				
				if(!isVisited(s)) {
					System.out.println("Push south to the queue " + "{" + s.coorX + "," + s.coorY + "}");
					s.parent = node;
					priority.add(s);
				}
			}
			if(isValid(node, "west")) {	// check if it's possible to go west

				w = new Node(node.coorX-1, node.coorY,node.cost + findCost(node.coorX-1,node.coorY));
				
				if(!isVisited(w)) {
					System.out.println("Push west to the queue " + "{" + w.coorX + "," + w.coorY + "}");
					w.parent = node;
					priority.add(w);
				}
			}
			if(isValid(node, "north")) {	// check if it's possible to go north

				n = new Node(node.coorX, node.coorY-1,node.cost + findCost(node.coorX,node.coorY-1));
				
				if(!isVisited(n)) {
					System.out.println("Push north to the queue " + "{" + n.coorX + "," + n.coorY + "}");
					n.parent = node;
					priority.add(n);
				}
			}
			
			System.out.println();
		}
		else {
			// reached goal state
			pathFound = true;
			solution = node;
		}
	}
	
	
	public static int countSpace(String line) {	// counts the number of space in a string and returns it
		int elementCounter = 0;
		for(int i=0; i< line.length(); i++) {
			if(line.charAt(i) == ' ')
				elementCounter++;
		}
		return elementCounter;
	}
	
	public static boolean isVisited(Node node) {	// check if the node is already visited, return boolean
		for(int k=0; k<nodeCounter; k++) {
			if(node.coorX == visited[k].coorX && node.coorY == visited[k].coorY)
				return true;
		}
		return false;
	}
	
	public static int findCost(int coorX, int coorY) {	// finds the cost of given coordinates
		for(int k=0; k<numOfTraps; k++) {
			if(trapsCoorx[k] == coorX && trapsCoory[k] == coorY)
				return 7;
		}
		return 1;
	}
	// check if the node in possible directions are valid
	public static boolean isValid(Node node, String direction) { 
		
		 //check if hits the wall or goes out of bounds
		if (direction.equals("north")) 
			return !wallCheck(node.coorX, node.coorY, node.coorX, node.coorY-1);	// send north coordinate
		else if(direction.equals("west"))
			return !wallCheck(node.coorX, node.coorY, node.coorX-1, node.coorY);	// send west coordinate
		else if(direction.equals("south"))
			return !wallCheck(node.coorX, node.coorY, node.coorX, node.coorY+1);	// send south coordinate
		else
			return !wallCheck(node.coorX, node.coorY, node.coorX+1, node.coorY);	// send east coordinate
	}
	
	public static boolean wallCheck(int initialX, int initialY, int destX, int destY) {
		if(destX < 1 || destX > size || destY < 1 || destY > size)	// check if out of bounds
			return true;
		
		for(int k=0; k<numOfWalls; k++) {
			if (wallsStartx[k] == wallsEndx[k]) {	// vertical wall, blocks certain moves
				if(initialX == wallsEndx[k] && initialY == wallsEndy[k] && destX == initialX+1 && destY == initialY)
					return true;
				if(initialX-1 == wallsEndx[k] && initialY == wallsEndy[k] && destX == initialX-1 && destY == initialY)
					return true;
			}
			else {	// horizontal wall, blocks certain moves
				if(initialX == wallsEndx[k] && initialY == wallsEndy[k] && destX == initialX && destY == initialY+1)
					return true;
				if(initialX == wallsEndx[k] && initialY-1 == wallsEndy[k] && destX == initialX && destY == initialY-1)
					return true;
			}
		}
		return false;
	}
	
	
	public static boolean isGoalState(Node node) {	// check if given node is the goal state
		for(int k=0; k<numOfGoals; k++) {
			if(node.coorX == goalsCoorx[k] && node.coorY == goalsCoory[k])
				return true;
		}
		return false;
	}
	
	public static void push(Node node) {	// push given node to the stack
		depthStack.push(node);
	}
	
	public static int depthCalculator(Node node) {	// calculates depth of the current node
		int depth = 0;
		while(node.parent != null) {
			depth++;
			node = node.parent;
		}
		return depth;
		
	}
}
