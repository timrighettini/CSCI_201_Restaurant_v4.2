package restaurant;

import java.awt.Color;

import restaurant.WaiterAgent.CustomerState;
import restaurant.WaiterAgent.MyCustomer;
import restaurant.layoutGUI.Restaurant;
import restaurant.layoutGUI.Table;
import astar.AStarTraversal;

public class SharedDataWaiterAgent extends WaiterAgent {
	
	

	public SharedDataWaiterAgent(String name, AStarTraversal aStar,
			Restaurant restaurant, Table[] tables) {
		super(name, aStar, restaurant, tables);
		// TODO Auto-generated constructor stub
	}
	
    protected void giveOrderToCook(MyCustomer customer) {
	//In our animation the waiter does not move to the cook in
	//order to give him an order. We assume some sort of electronic
	//method implemented as our message to the cook. So there is no
	//animation analog, and hence no DoXXX routine is needed.
	print("Giving " + customer.cmr + "'s choice of " + customer.choice + " to revolving stand.");

	customer.state = CustomerState.NO_ACTION;
	
	restaurant.revolvingStand.add(new CookOrder(this, customer.tableNum, customer.choice)); // Add item to the revolving stand
	
	stateChanged();
	
	//Here's a little animation hack. We put the first two
	//character of the food name affixed with a ? on the table.
	//Simply let's us see what was ordered.
	tables[customer.tableNum].takeOrder(customer.choice.substring(0,2)+"?");
	restaurant.placeFood(tables[customer.tableNum].foodX(),
			     tables[customer.tableNum].foodY(),
			     new Color(255, 255, 255), customer.choice.substring(0,2)+"?");
    }

}
