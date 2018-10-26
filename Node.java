import java.util.Comparator;

public class Node implements Comparable<Node>{
	
	State state; // the state of this node
	Node parent; 
	Object operator; //the operator that was used to generate this node
	int depth;
	double pathCost; //the path cost from the root to this node
	double heuCost; //heuristic function value from this node
	
	public Node(State state, Node parent, Object operator, int depth, double pathCost, double heuCost) {
		this.state = state;
		this.parent = parent;
		this.operator = operator;
		this.depth = depth;
		this.pathCost = pathCost;
		this.heuCost = heuCost;
	}
	

	
	public int compareTo(Node otherNode) {

		return (int) (this.pathCost-otherNode.pathCost);

	}
	
	

	 
}
