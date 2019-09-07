package tsp_astar;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class TSP 
{
	//In case the cities are not connected, for making the distance between them infinite
	public static final int INFINITY = Integer.MAX_VALUE;
    private int num_nodes_expanded;
    public static void main(String[] args) 
    {
    	//Epsilon check
        double eps = 0.1;
        TSP problem = new TSP();
        Scanner ob =new Scanner(System.in);
        int numOfCitites;
		System.out.println("How many cities ?");
		numOfCitites = ob.nextInt();
		
		problem.problemInstance(numOfCitites ,eps);
		ob.close();
    }
    
    //Creates the cost/distance matrix and runs A*
    public void problemInstance(int cityCount,double eps)
    {
        Node root = generateProblem(cityCount,eps);
        this.num_nodes_expanded=0;
        
        //Implement OPEN List as a Heap(priority queue)
        PriorityQueue<Node> frontier = new PriorityQueue<Node>(new Comparator<Node>() 
        {
            @Override
            public int compare(Node o1, Node o2) 
            {
                if (o1.getF_cost() > o2.getF_cost())
                {
                    return 1;
                }
                else if(o1.getF_cost() < o2.getF_cost())
                {
                    return -1;
                }
                else return 0;
            }
        });
        
        //Expanded List as a HashMap for fast search
        Map<List<Integer>,Node> explored = new HashMap<List<Integer>,Node>();
        Map<List<Integer>,Node> frontierMap = new HashMap<List<Integer>,Node>();

        //Calculate the heuristic value for root node --> f_cost = g_cost + h_cost
        double root_h = calculateHeuristic(root,cityCount);
        root.setH_cost(root_h);
        
        //insert the completely analyzed city node into the Priority Queue
        frontier.offer(root);
        
        //Put it in a HashMap for easy retrieval in future
        frontierMap.put(root.getState(),root);
        
        Node solution = findSolution(explored,frontier,frontierMap,cityCount);
        
        System.out.println("");
        System.out.print("Travelling Salesman Path = ");
        List<Integer> path = new ArrayList<>();
        if(solution!=null)
        {
            for(Integer i:solution.getState())
            {
            	path.add(i);
                System.out.print(i+1+" ");
            }
            System.out.println("");
        }
        
        System.out.println("Number of Nodes expanded = "+this.num_nodes_expanded);
        System.out.println("");
    }
    
  //Read the input matrix from a file, return starting node
    public Node generateProblem(int cityCount,Double eps)
    {
    	//if there is no city
        if(cityCount<1) 
        	return null;
        Scanner scanner;
        double [][] distances = new double[cityCount][cityCount];
        try 
        {
        	//Input the file path
        	scanner = new Scanner(new File("lau15.txt"));
			//Cost Matrix
			for(int i = 0; i < cityCount; i++)
			{
				for(int j = 0; j < cityCount && scanner.hasNextInt(); j++)
				{
					int nxtInt = scanner.nextInt();
					//city[i][j] --> -1 : Not connected
					if(nxtInt < 0)							
						distances[i][j] = TSP.INFINITY;
					else
						distances[i][j] = nxtInt;
				}
			}
        }
        catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
        
        //Print the Cost Matrix
        System.out.println("___________________The Distance Matrix_______________________");
        System.out.println();
        for(int a = 0;a<cityCount;a++)
        {
          for(int b = 0;b<cityCount;b++)
          {
          	System.out.print(distances[a][b]+ " ");
          }
          System.out.println();
        }
        System.out.println();
        
        //Create root node
        List<Integer> rootState = new ArrayList<>();
        rootState.add(0);
        
        //Node(g_cost, parent, city_id, map, state)
        return new Node(0.0,null,0,distances,rootState);
    }

    //Calculates the Heuristic
    public Double calculateHeuristic(Node node,int cityCount)
    {
    	//Heuristic value from the MST of the unvisited cities
        double mst = calculateMST(node,cityCount);
        //Cost from the current expanded node to the root
        double min_to_root = calculateMinToRoot(node,cityCount);
        //Cost from the current expanded node to the favorable child node
        double min_to_child = calculateMinToChild(node,cityCount);

        return mst+min_to_root+min_to_child;
    }

    //Cost from the current expanded node to the favorable child node
    public Double calculateMinToChild(Node node,int cityCount)
    {
    	//if all the cities have been visited
        if(node.getState().size()>=cityCount)
        {
            return 0.0;
        }
        double min_to_child = Double.MAX_VALUE;
        for(int i=0;i<cityCount;i++)
        {
        	//finding the minimum distance from that node to a child node
        	//leaving the node itself (first condition)
            if(!node.getState().contains(i) && node.getMap()[node.getCityId()][i]<min_to_child )
            {
                min_to_child = node.getMap()[node.getCityId()][i];
            }
        }
        return min_to_child;
    }

    public Double calculateMinToRoot(Node node,int cityCount)
    {
    	//if all cities have been visited
    	//get current city id and find the check its distance in matrix
        if(node.getState().size()>=cityCount)
        {
            return node.getMap()[node.getCityId()][0];
        }
        double min_to_root = Double.MAX_VALUE;
        
        //finding the minimum distance from that node to the root node
    	//leaving the node itself
        for(int i=0;i<cityCount;i++)
        {
            if(!node.getState().contains(i) && node.getMap()[i][0]<min_to_root )
            {
                min_to_root = node.getMap()[i][0];
            }
        }
        return min_to_root;
    }


    //Returns sum of edges in a MST from a distance matrix, not including cities in removed set
    public double calculateMST(Node node,int cityCount)
    {
    	//if only single node remains
        if(cityCount-node.getState().size()<2)
        {
            return 0.0;
        }
        double cost = 0.0;
        
        //list of visited cities
        List<Integer> visited = new ArrayList<>();

        //Stores the cities with minimum edge distance between them
        int minCity1=-1;
        int minCity2=-1;
        double minDistance=Double.MAX_VALUE;

        //Find the first minimum edge of the MST
        for(int i =0;i<cityCount;i++)
        {
            for (int j=0;j<cityCount;j++)
            {
                if(!node.getState().contains(i) && !node.getState().contains(j) && i!=j)
                {
                    if(node.getMap()[i][j]<minDistance)
                    {
                        minDistance = node.getMap()[i][j];
                        minCity1 = i;
                        minCity2 = j;
                    }
                }
            }
        }
        visited.add(minCity1);
        visited.add(minCity2);
        cost = cost + node.getMap()[minCity1][minCity2];
        System.out.println("Added EDGE "+node.getMap()[minCity1][minCity2]);
        //System.out.println();

        //Find remaining edges from the visited nodes
        while (visited.size()< (cityCount-node.getState().size()))
        {
            int minC = -1;
            double minD = Double.MAX_VALUE;
            for(Integer visitedCity:visited)
            {
                for (int i=0;i<cityCount;i++)
                {
                	//if the node city is in Node state's list or is a visited city --> skip
                    if(!node.getState().contains(i) && !visited.contains(i))
                    {
                        if(node.getMap()[visitedCity][i]<minD)
                        {
                            minD = node.getMap()[visitedCity][i];
                            minC = i;
                        }
                    }
                }
            }
            visited.add(minC);
            cost = cost+minD;
        }
        return cost;
    }

    //Runs A*
    //findSolution(Explored List, OPEN List, HashMap, cityCount)
    public Node findSolution(Map<List<Integer>,Node> explored,PriorityQueue<Node> frontier,Map<List<Integer>,Node> frontierMap,int cityCount)
    {
    	//Continue till OPEN List is not empty
        while (!frontier.isEmpty())
        {
        	//Get the first element with best heuristic value from the heap
            Node current = frontier.poll();
            
            //Remove it from the HashMap
            frontierMap.remove(current.getState());

            //Information about the explored city node
            System.out.println("Explored City : "+current.getCityId()+"\nF_COST : "+current.getF_cost()+" G_COST : "+current.getG_cost()+" H_COST : "+current.getH_cost()+" L:"+current.getState().toString());
            
            //If we have reached back to the initial city i.e. 0
            //path size = cityCount+1 and first element is 0 and last element is 0
            if(current.getState().size()==cityCount+1 && current.getState().get(0)==0 && current.getState().get(cityCount)==0)
            {
                return current;
            }
            else 
            {
            	//Put the node on explored/expanded list --> HashMap
                explored.put(current.getState(),current);
                
                //Increment Number of Nodes expanded
                this.num_nodes_expanded++;
                
                //Get the list of all neighbors
                List<Node> children = getChildren(current,cityCount);
                
                //for every child node in children vector
                for(Node child:children)
                {
                	//if the current child node is not explored or not inside the current OPEN List
                    if(!explored.containsKey(child.getState()) && !frontierMap.containsKey(child.getState()))
                    {
                    	//then add it to the OPEN List according to the heuristic value
                        frontier.offer(child);
                        
                        //Add it to the HashMap
                        frontierMap.put(child.getState(),child);
                        System.out.println("Adding child : " +child.getState());
                    }
                    else if(frontierMap.containsKey(child.getState()) && frontierMap.get(child.getState()).getF_cost()>child.getF_cost())
                    {
                        frontier.remove(frontierMap.get(child.getState()));
                        frontier.add(child);
                        frontierMap.put(child.getState(),child);
                    }
                }
            }
        }
        return null;
    }

    //Returns children of a node
    public List<Node> getChildren(Node parent,Integer cityCount)
    {
    	//list of children
        List<Node> children = new ArrayList<Node>();
        for(int i=0;i<cityCount;i++)
        {
            if(!parent.getState().contains(i))
            {
                List<Integer> childState=new ArrayList<>();
                //adds in the same order
                childState.addAll(parent.getState());
                System.out.println(childState);
                childState.add(i);
                System.out.println(childState);
                
                //Make the new child node
                //g_cost = parent's cost + parent to this child cost
                //parent = parent
                //city_id = i
                Node child=new Node(parent.getG_cost()+parent.getMap()[parent.getCityId()][i],parent,i,parent.getMap(),childState);
                child.setH_cost(calculateHeuristic(child,cityCount));
                
                //Return the array of children nodes generated from the parent node
                children.add(child);
                System.out.println();
            }
        }
        
        //If no children were added
        if (children.size()==0)
        {
            List<Integer> goalState=new ArrayList<>();
            goalState.addAll(parent.getState());
            goalState.add(0);
            Node child=new Node(parent.getG_cost()+parent.getMap()[parent.getCityId()][0],parent,0,parent.getMap(),goalState);
            child.setH_cost(calculateHeuristic(child,cityCount));
            children.add(child);
        }
        return children;
    }
}