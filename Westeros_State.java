import java.util.ArrayList;
import java.util.LinkedList;

public class Westeros_State implements State, Cloneable {

	ArrayList<Position> walkersPositions = new ArrayList<Position>();
	ArrayList<Position> obstaclesPositions = new ArrayList<Position>();
	Position agentPosition;
	Position dragonStonePosition;
	int curGlass,kills;
	

	public Westeros_State(ArrayList<Position> walkersPositions, ArrayList<Position> obstaclesPositions, Position agentPosition, Position dragonStonePosition, int curGlass, int kills) {
		this.walkersPositions = walkersPositions;
		this.obstaclesPositions = obstaclesPositions;
		this.agentPosition = agentPosition;
		this.dragonStonePosition = dragonStonePosition;
		this.curGlass = curGlass;
		this.kills=kills;
	}
	
	public Westeros_State() {
		
	}
	
	public  Westeros_State getCopy()
	{
	  Westeros_State ans = new Westeros_State();
	  ans.agentPosition=new Position(this.agentPosition.x,this.agentPosition.y);
	  ans.dragonStonePosition=new Position(this.dragonStonePosition.x,this.dragonStonePosition.y);
	  ans.walkersPositions=new ArrayList<Position>();
	  ans.obstaclesPositions=new ArrayList<Position>();
	  
	  for(Position p: this.walkersPositions)
		  ans.walkersPositions.add(new Position(p.x,p.y));
	  
	  for(Position p: this.obstaclesPositions)
		  ans.obstaclesPositions.add(new Position(p.x,p.y));
	  
	  ans.curGlass=this.curGlass;
	  ans.kills=this.kills;
	  return ans;
	}
	
	
	

}
