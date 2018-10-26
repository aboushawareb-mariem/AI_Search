import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class SaveWesteros extends Search_Problem {
	
	static int gridX;
	static int gridY;
	 int maxGlass;
	 int heu;
	static HashSet<Westeros_State> repeatedStates ;
	ArrayList<String>[] operators = new ArrayList[4]; //populated in the empty constructor of this class
	//0 : dx, 1: dy, 2: take dragon stone, 3: kill
	static int[] dx={0,0,-1,1},dy={-1,1,0,0};
	
	public SaveWesteros() {
		//populating the operators arraylist
		
		
		for (int i = 0; i < operators.length; i++) {
			operators[i] = new ArrayList<String>();
		}
		
		for (int i = 0; i < dx.length; i++) {
			this.operators[0].add(Integer.toString(dx[i]));
			this.operators[1].add(Integer.toString(dy[i])) ;
		}
		
		operators[2].add("Grab"); //grab dragon glass
		operators[3].add("Kill"); //kill whiteWalkers
	}
	
	//tells u if a the arraylist contains a position object with the same x and y as the input position (returns the index if it finds it and -1 if it doesn't)
	public static int doesContain(Position p , ArrayList<Position> a) {
		int x = p.x;
		int y = p.y;
		for (int i = 0; i < a.size(); i++) {
			if(a.get(i).x == x && a.get(i).y == y)
				return i;
		}
		return -1;
	}
	

	public static boolean valid(Position p, Westeros_State s) {
		if(!(doesContain(p, s.walkersPositions)>=0 || doesContain(p, s.obstaclesPositions)>=0 || p.x <0 || p.y < 0 || p.x >= gridX || p.y >= gridY))
			return true;
		return false;
	}
	
	public static boolean canKill(Position p, Westeros_State s)
	{
		if(p.x<0||p.x>=gridX||p.y<0||p.y>=gridY||s.curGlass<=0)
			return false;
		return true;
	}
	
	/* This function is to check if the given state has
	 * been visited before to help in handling the repeated states.
	 */
	public boolean visitedStates(Westeros_State s)
	{
		int x = s.agentPosition.x;
		int y =s.agentPosition.y;
		int curGlass= s.curGlass;
		ArrayList<Position> walkersPosition =s.walkersPositions;
		for(Westeros_State cur: repeatedStates)
		{
			if(cur.agentPosition.x==x&& cur.agentPosition.y==y&&cur.curGlass==curGlass)
			{
				if(cur.walkersPositions.size()==walkersPosition.size())
				{
					for (int i = 0; i < cur.walkersPositions.size(); i++) {
						boolean walker=false;
						int walkerX=cur.walkersPositions.get(i).x;
						int walkerY=cur.walkersPositions.get(i).y;
						for (int j = 0; j <walkersPosition.size(); j++) {
							if(walkersPosition.get(j).x==walkerX && walkersPosition.get(j).y==walkerY)
							{ 
								walker=true;
								break;
							}						
						}
						if(!walker) return false;
					}
					return true;
				}  	
			}
		}
		return false;
	
	}
	public State state_space(State s, Object action) {
		
		//create an instance of the next state
		Westeros_State	nextState=((Westeros_State)s).getCopy();
		
		//agent's position
		if(action instanceof Position) {
			
			int x = nextState.agentPosition.x;
			int y = nextState.agentPosition.y;
			//transition in the grid
			int deltaX = ((Position)action).x;
			int deltaY = ((Position)action).y;
			//new agent position
			nextState.agentPosition = new Position(0,0);
			nextState.agentPosition.x = x+deltaX;
			nextState.agentPosition.y = y+deltaY;

			if(valid(nextState.agentPosition, nextState)&&!visitedStates(nextState))
			{
				repeatedStates.add(nextState);
				return nextState;
			}
				
			
		}else if(action instanceof String) {
			//grab dragon stone 
			if(action.equals("Grab")&&nextState.curGlass==0) 
			{
				
				//check to only grab if we're in a dragon stone cell
				if(nextState.agentPosition.x == nextState.dragonStonePosition.x)
					if(nextState.agentPosition.y == nextState.dragonStonePosition.y)
						{
							nextState.curGlass = maxGlass;
							if(!visitedStates(nextState))
							{
								repeatedStates.add(nextState);
								return nextState;
							}
							
						}
					
			}
			//kill whiteWalkers around you
		else if(action.equals("Kill")) 
		{
			
				boolean noWhiteWalkers = true;
				int x = nextState.agentPosition.x;
				int y = nextState.agentPosition.y;
				for (int i = 0; i < operators[0].size(); i++) 
				{
					
					int x_neighbour = x+ Integer.parseInt(operators[0].get(i));
					int y_neighbour = y+ Integer.parseInt(operators[1].get(i));
					
					Position neighbour_cell = new Position(x_neighbour, y_neighbour);
					
					//check if there is a white walker in this neighbour cell and if there is, get its index
					int neighbourPos = doesContain(neighbour_cell, nextState.walkersPositions);
					
					//check so we don't use the kill operator unless there are indeed whitewalkers
					if(canKill(neighbour_cell,nextState)&&neighbourPos>=0) 
					{	noWhiteWalkers=false;
						Position tmp =nextState.walkersPositions.get(neighbourPos); 
						nextState.walkersPositions.remove(neighbourPos);

					}
					
				}

				if(noWhiteWalkers)
					return null;
				
				nextState.curGlass--;
				nextState.kills++;
				
				
				if(!visitedStates(nextState))
				{
					repeatedStates.add(nextState);
					return nextState;
				}
				
			}
		}

		return null;
	}

	
	public int path_cost_func(Node n) {
		int factor = 0;
		char operatorChar= (""+n.operator).charAt(0);
		
		if(n.operator instanceof Position)			
			factor =5;
		else if(n.operator instanceof String) {

			if(n.operator.equals("Kill"))
				factor= gridX*gridY;
			else if(n.operator.equals("Grab"))
				factor = 0;
			
		}
		return factor;
		
	}

	
	public boolean goal_test(Node n) {
		if(((Westeros_State)n.state).walkersPositions.isEmpty())
			return true;
		return false;
	}

	@Override
	public ArrayList<Node> expand(Node n) {
		ArrayList<Node> expansionNodes = new ArrayList<Node>();
		
			
		//the motion operators (dx, and dy)
		for (int i = 0; i < 4; i++) {
			Position deltaPosition = new Position(Integer.parseInt(operators[0].get(i)), Integer.parseInt(operators[1].get(i)));
			State s = this.state_space(n.state, deltaPosition );
			if(s!=null) {
				int depth = n.depth+1;
				Node resultNode = new Node(s, n, deltaPosition, depth, 0,0);
				resultNode.pathCost= n.pathCost + this.path_cost_func(resultNode);
				resultNode.heuCost= this.heuristic_func(resultNode, this.heu);
				expansionNodes.add(resultNode);
			}
		}
		
		//the grabbing or killing operators
		for (int i = 2; i < operators.length; i++) {

			State s = this.state_space(n.state,((String)operators[i].get(0)) );
			
			if(s!=null)
			{
				int depth = n.depth+1;
				Node resultNode = new Node(s, n, operators[i].get(0), depth, 0,0);
				resultNode.pathCost= n.pathCost + this.path_cost_func(resultNode);
				resultNode.heuCost= this.heuristic_func(resultNode, this.heu);
				expansionNodes.add(resultNode);
			}
		}
		
		

		
		return expansionNodes;
	}

	public double heuristic_func(Node n, int inHeu) {
		if(inHeu == 1) {
			return Math.ceil((((Westeros_State)n.state).walkersPositions.size())/3.0);
			
		}else if(inHeu == 2) {
			
			Position agent = ((Westeros_State)n.state).agentPosition;
			int min = (int)1e6;
			for (int i = 0; i < ((Westeros_State)n.state).walkersPositions.size() ; i++) {
				int temp = euclideanDistance(agent, ((Westeros_State)n.state).walkersPositions.get(i) );
				min = Math.min(temp, min);
			}
			return  (Math.ceil((((Westeros_State)n.state).walkersPositions.size())/3.0)+min);
		}
		return -1;//indicates something wrong going on
	}
	
	
	public static int euclideanDistance(Position p1, Position p2) {
		return (Math.abs(p1.x - p2.x)+Math.abs(p1.y-p2.y));
	}
	
	@Override
	public Object[] search(char [][] grid, String strategy, Boolean visualize) {
		
		//creating an empty state
		repeatedStates= new HashSet<>();
		this.initState = new Westeros_State();
		if(strategy.length()==3) {
			this.heu = Integer.parseInt(strategy.substring(2));
		}
		((Westeros_State) (this.initState)).curGlass = 0;
	 	// maxGlass=5;
		//in case the grid is not a square I get the length of the x and length of the y since in the description it says N x M grid 
		((Westeros_State) (this.initState)).agentPosition = new Position(grid.length -1, grid[0].length-1);
		
		//getting necessary information from the grid about the environment at the beginning of the search 
		//to create the initial state
		gridX = grid.length;
		gridY = grid[0].length;
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				if(grid[i][j] == 'O') {
					((Westeros_State) (this.initState)).obstaclesPositions.add(new Position(i,j));
				}else if(grid[i][j] == 'W') {
					((Westeros_State) (this.initState)).walkersPositions.add(new Position(i, j));
				}else if(grid[i][j] == 'S') {
					((Westeros_State) (this.initState)).dragonStonePosition = new Position(i, j);
				}
			}
			
		}
		
		
		
		repeatedStates.add((Westeros_State)this.initState);
		

		Object[] ans = new Object[3];
		//goal node reached
		Node resNode = App.general_search(this, strategy);
		
		if(resNode != null) {
			
			//the sequence of nodes followed till goal
			ans[0] = new ArrayList<Node>();
			//adding the goal node
			((ArrayList<Node>)ans[0]).add(resNode);
			//temp node to backtrack
			Node node = resNode;
			//propagating the arraylist with the sequence till goal
			while(node!=null&&node.parent != null) {
				((ArrayList<Node>)ans[0]).add(node.parent);
				node = node.parent;
			}
			//cost and number of expanded nodes
			ans[1] = ((Node)((ArrayList)ans[0]).get(0)).pathCost;
			ans[2] = ((ArrayList)ans[0]).size();
			
			//visualization
			if(visualize) {
			
			char[][] visualizeArray = new char[gridX][gridY];
			for(int i = 0; i < ((ArrayList<Node>)ans[0]).size() ; i++) 
			{
							
				for (int j = 0; j < visualizeArray.length; j++) {
					Arrays.fill(visualizeArray[j], '.');
				}
				visualizeArray[((Westeros_State)this.initState).dragonStonePosition.x][((Westeros_State)this.initState).dragonStonePosition.y] = 'S';
				
				
				
				int jx = ((Westeros_State)((ArrayList<Node>)ans[0]).get(i).state).agentPosition.x;
				int jy = ((Westeros_State)((ArrayList<Node>)ans[0]).get(i).state).agentPosition.y;
				visualizeArray[jx][jy] = 'J';
				
				for (int j = 0; j < ((Westeros_State)((ArrayList<Node>)ans[0]).get(i).state).walkersPositions.size(); j++) {
					int x = ((Westeros_State)((ArrayList<Node>)ans[0]).get(i).state).walkersPositions.get(j).x;
					int y = ((Westeros_State)((ArrayList<Node>)ans[0]).get(i).state).walkersPositions.get(j).y;
					visualizeArray[x][y] = 'W';
					
				}
				for (int j = 0; j <((Westeros_State)((ArrayList<Node>)ans[0]).get(i).state).obstaclesPositions.size(); j++) {
					int x = ((Westeros_State)((ArrayList<Node>)ans[0]).get(i).state).obstaclesPositions.get(j).x;
					int y = ((Westeros_State)((ArrayList<Node>)ans[0]).get(i).state).obstaclesPositions.get(j).y;
					visualizeArray[x][y] = 'O';
				}
				
				for (int j = 0; j < visualizeArray.length; j++) {
					for (int j2 = 0; j2 < visualizeArray[j].length; j2++) {
						System.out.print(visualizeArray[j][j2]+ " ");
					}
					System.out.println();
				}
				System.out.println("x: "+ ((Westeros_State)((ArrayList<Node>)ans[0]).get(i).state).agentPosition.x);
				System.out.println("y: "+ ((Westeros_State)((ArrayList<Node>)ans[0]).get(i).state).agentPosition.y);
				System.out.println("walkers: "+((Westeros_State)((ArrayList<Node>)ans[0]).get(i).state).walkersPositions.size());
				System.out.println("PathCost: "+(((ArrayList<Node>)ans[0]).get(i).pathCost));
				System.out.println("currGlass: "+ ((Westeros_State)((ArrayList<Node>)ans[0]).get(i).state).curGlass);
				System.out.println("___________________");
			}
			
			}
			
			
			return ans;
		}else {
			System.out.println("No goal node found");
			return null;
		}
	}
	
	public char[][] genGrid() {
 		// dimensions: between 4 and 7
 		int m = (int) (Math.random() * 4 + 4); 
 		int n = (int) (Math.random() * 4 + 4); 
 		this.gridX = m;
 		this.gridY = n;
 		
 		char[][] grid = new char[m][n];
 		for(int i = 0; i < m; i++)
 			for(int j = 0; j < n; j++)
 				grid[i][j] = '.';
 		
 		// set initial position of John Snow
 		grid[m-1][n-1] = 'J';
 		
 		// dragonstone is in a random cell
 		while(true) {
 			int dragonstoneX = (int) (Math.random() * m); 
 	 	 	int dragonstoneY = (int) (Math.random() * n); 
 	 	 	
 	 	 	if(grid[dragonstoneX][dragonstoneY] == '.') {
 	 	 		grid[dragonstoneX][dragonstoneY] = 'S';
 	 	 		break;
 	 	 	}
 		}
 		
 		// fill one quarter of the grid with white walkers
 	 	int whiteWalkers = (m * n) / 4;
 		for(int i = 0; i < whiteWalkers; i++)
 			while(true) {
 				int whiteWalkerX = (int) (Math.random() * m); 
 	 	 	 	int whiteWalkerY = (int) (Math.random() * n); 
 	 			if(grid[whiteWalkerX][whiteWalkerY] == '.') {
 	 				grid[whiteWalkerX][whiteWalkerY] = 'W';
 	 				break;
 	 			}
 			}
 		
 		// fill one quarter of the grid with obstacles
 	 	int obstacles = (m * n) / 4;
 		for(int i = 0; i < obstacles; i++)
 			while(true) {
 				int obstacleX = (int) (Math.random() * m); 
 	 	 	 	int obstacleY = (int) (Math.random() * n); 
 	 			if(grid[obstacleX][obstacleY] == '.') {
 	 				grid[obstacleX][obstacleY] = 'O';
 	 				break;
 	 			}
 			}
 		
 		// max dragonglass to carry: between 1 and number of white walkers
 		int maxDragonglass = (int) (Math.random() * whiteWalkers + 1);
 		this.maxGlass = maxDragonglass;
 		
 		return grid;	
 	}
	
}
