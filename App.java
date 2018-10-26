

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class App {

		
	//TODO change westeros to search problem
 	public static  Node general_search(Search_Problem sp, String strategy) {
 		
 		ArrayList<Node> q = new ArrayList<Node>();
 		State init = sp.initState;
 		Node root = new Node(init, null, null, 0, 0, 0);
 		if(strategy.length() == 3)
 			root.heuCost = sp.heuristic_func(root, Integer.parseInt(strategy.substring(2)));
		q.add(root);

		int l =0,limit=0; //This variable is to count the limit for the ids
		
		while(!q.isEmpty())
		{
			Node cur = q.remove(0);
						
			if(sp.goal_test(cur))
				return cur;
			
				switch(strategy.substring(0,2)) {
				
				case "BF":
				{
					ArrayList<Node> children= sp.expand(cur);
					for (Node child:children)
						q.add(child);
					break;
				}
					
				case "DF":
				{
					ArrayList<Node> children= sp.expand(cur);
					for (Node child:children)
						q.add(0,child);
					break;
				}
					
				case "ID":
					{
						
						while(l <= limit) 
						{
							ArrayList<Node> children= sp.expand(cur);
							for (Node child:children)
								q.add(0,child);
							
							l++;
						}
						l=0;
						limit++;
						break;
					}
					
				case "UC":
					{
						ArrayList<Node> children= sp.expand(cur);						
						for (Node child:children)
							q.add(child);
						Collections.sort(q);
						break;
					}
					
				case "GR":
				{
					if(strategy.length()!=3)
					{
						System.out.println("Invalid Strategy GR should be of length 3.");
						return null;	
					}
					ArrayList<Node> children= sp.expand(cur);						
					for (Node child:children)
						q.add(child);
					Collections.sort(q, new Comparator<Node>() {
						@Override
						public int compare(Node o1, Node o2) {
							// TODO Auto-generated method stub
							return (int) (o1.heuCost-o2.heuCost);
						}
						
					});
					
					break;
				}
				
				case "AS":
				{
					if(strategy.length()!=3)
					{
						System.out.println("Invalid Strategy AS should be of length 3.");
						return null;
						
					}
					ArrayList<Node> children= sp.expand(cur);						
					for (Node child:children)
						q.add(child);
					Collections.sort(q, new Comparator<Node>() {
						@Override
						public int compare(Node o1, Node o2) {
							// TODO Auto-generated method stub
							return (int) ((o1.heuCost+o1.pathCost)-(o2.heuCost+o2.pathCost));
						}
						
					});
					break;
					
				}
				
				default:
					System.out.println("invalid strategy!");
					return null;
				}
			
		}
 		return null;
	}

		

	public static void main(String[] args) {
		
		Search_Problem sw = new SaveWesteros();
		char[][] grid= ((SaveWesteros)sw).genGrid();
		System.out.println("grid height: "+grid.length);
		System.out.println("grid width: "+grid[0].length);


		Object[] ans = sw.search(grid, "AS1", true);
		if(ans != null) {
			
			Node cur = (Node)((ArrayList)ans[0]).get(0);
			for (int i = 0; i < ((ArrayList<Node>)ans[0]).size(); i++) {
				System.out.println("x position: "+((Westeros_State)((((ArrayList<Node>)ans[0]).get(i)).state)).agentPosition.x);
				System.out.println("y position: "+((Westeros_State)((((ArrayList<Node>)ans[0]).get(i)).state)).agentPosition.y);
				System.out.println("__________________________");
			}
			
			System.out.println("cost of solution: "+ans[1]);
			System.out.println("Number of expanded nodes: "+ans[2]);

		}else {
			System.out.println("No solution");
		}

		
	}
}
