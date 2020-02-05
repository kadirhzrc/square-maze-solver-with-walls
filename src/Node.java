
public class Node {
	int coorX;	// x coordinate of the node object
	int coorY;	// y coordinate of the node object
	Node parent;	// parent of the node object
	int cost;	// cumulative cost so far to the object
	int heuristicValue, heuristicCost;	// heuristicValue is h(x) function itself while cost is h(x)+cost
	
	Node() {	// default constructor
		parent = null;
		cost = 0;
		
	}
	Node(int coorx, int coory, int cost) {	// constructor with arguments
		this.coorX = coorx;
		this.coorY = coory;
		this.cost = cost;
		this.heuristicValue = heuristics(Main.goalsCoorx, Main.goalsCoory, Main.numOfGoals);	// find h(x)
		this.heuristicCost = heuristicsPlusCost(Main.goalsCoorx, Main.goalsCoory, Main.numOfGoals, cost);	// find h(x)+cost
	}
	
	public int getCost() {	
		return this.cost;
	}
	
	public int getHeuristics() {
		return heuristicValue;
	}
	
	public int getHeuristicsPlusCost() {
		return heuristicCost;
	}
	
	public int heuristics(int[] goalX, int[] goalY, int numOfGoals) {	// calculates h(x) value for the node
		int minDistance = 100;
		for(int k=0; k<numOfGoals; k++) {
			if(Math.abs(goalX[k] - this.coorX) + Math.abs(goalY[k] - this.coorY) < minDistance)	// block distance of the closest goal state to the node
				minDistance = Math.abs(goalX[k] - this.coorX) + Math.abs(goalY[k] - this.coorY);
		}
		return minDistance;
	}
	
	public int heuristicsPlusCost(int[] goalX, int[] goalY, int numOfGoals, int cost) {	// calculates h(x) value+cost
		int minDistance = 100;	// set default minDistance to max possible value
		for(int k=0; k<numOfGoals; k++) {
			if(Math.abs(goalX[k] - this.coorX) + Math.abs(goalY[k] - this.coorY) < minDistance)
				minDistance = Math.abs(goalX[k] - this.coorX) + Math.abs(goalY[k] - this.coorY);
		}
		return minDistance + cost;
	}
	
}
