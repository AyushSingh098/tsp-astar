package tsp_astar;

import java.util.List;

public class Node {
	//actual cost
		private double g_cost;
		//heuristic cost
		private double h_cost;
		//final cost
		private double f_cost;
		private Node parent;
		private int cityId;
		//cost matrix
		private double[][] map;
		private List<Integer> state;

		//Constructor
		public Node(double g_cost, Node parent, int cityId, double[][] map,List<Integer> state) 
		{
			this.g_cost = g_cost;
			this.parent = parent;
			this.cityId = cityId;
			this.map = map;
			this.state = state;
		}

		//Standard getter setter methods
		public List<Integer> getState() 
		{
			return state;
		}
		public void setState(List<Integer> state) 
		{
			this.state = state;
		}
		public double getG_cost() 
		{
			return g_cost;
		}
		public void setG_cost(double g_cost) 
		{
			this.g_cost = g_cost;
		}
		public double getH_cost() 
		{
			return h_cost;
		}
		public void setH_cost(double h_cost) 
		{
			this.h_cost = h_cost;
			setF_cost(this.g_cost+this.h_cost);
		}
		public double getF_cost() 
		{
			return f_cost;
		}
		public void setF_cost(double f_cost) 
		{
			this.f_cost = f_cost;
		}
		public Node getParent() 
		{
			return parent;
		}
		public void setParent(Node parent) 
		{
			this.parent = parent;
		}
		public int getCityId() 
		{
			return cityId;
		}
		public void setCityId(int cityId) 
		{
			this.cityId = cityId;
		}
		public double[][] getMap() 
		{
	      return map;
		}
		public void setMap(double[][] map) 
		{
			this.map = map;
		}

}
