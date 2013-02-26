package restaurant;

import restaurant.CookAgent.Status;
import restaurant.interfaces.Waiter;
import restaurant.layoutGUI.Food;

public class CookOrder {
	public WaiterAgent waiter;
	public int tableNum;
	public String choice;
	public Status status;
	public Food food; //a gui variable
	public boolean waitingForShipment = false; // This will be used keep an order on standby until a shipment arrives if it has already been ordered
	
	public CookOrder(Waiter waiter, int tableNum, String choice){
	    this.waiter = (WaiterAgent) waiter;
	    this.choice = choice;
	    this.tableNum = tableNum;
	    this.status = Status.pending;
	}
	public String toString(){
		return choice + " for " + waiter ;
	}
}
