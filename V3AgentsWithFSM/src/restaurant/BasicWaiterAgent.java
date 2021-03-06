package restaurant;

import restaurant.layoutGUI.Restaurant;
import restaurant.layoutGUI.Table;
import astar.AStarTraversal;

public class BasicWaiterAgent extends WaiterAgent {

	public BasicWaiterAgent(String name, AStarTraversal aStar,
			Restaurant restaurant, Table[] tables) {
		super(name, aStar, restaurant, tables);
		// Same as a regular waiter agent, nothing more required here
	}

}
