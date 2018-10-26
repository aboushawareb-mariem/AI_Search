import java.util.ArrayList;

abstract public class Search_Problem  {
	State initState;
	Object[] operators; //set of possible operators
	abstract public State state_space(State s,Object action ); //this is the transition function. Given a state and an action, it produces the next state
	abstract public int path_cost_func(Node n); //Assigns a cost to a given state from the root
	abstract public double heuristic_func(Node n, int heu);
	abstract public boolean goal_test(Node n); //Given a state, tells if this state is a goal or not
	abstract public ArrayList<Node> expand(Node n); //Applies the set of operators on a given state
	abstract public Object[] search(char [][] grid, String strategy, Boolean visualize);
	
}
