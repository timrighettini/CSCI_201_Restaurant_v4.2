package restaurant;

import agent.Agent;
import java.util.Timer;
import java.util.TimerTask;
import java.util.*;
import restaurant.layoutGUI.*;
import java.awt.Color;


/** Cook agent for restaurant.
 *  Keeps a list of orders for waiters
 *  and simulates cooking them.
 *  Interacts with waiters only.
 */
public class CookAgent extends Agent {

    //List of all the orders
    private List<Order> orders = new ArrayList<Order>();
    private Map<String,FoodData> inventory = new HashMap<String,FoodData>();
    public enum Status {pending, cooking, done}; // order status

    //Name of the cook
    private String name;

    //Timer for simulation
    Timer timer = new Timer();
    Restaurant restaurant; //Gui layout
    
    // Constants
    int FOOD_AMOUNT = 5;
    int THRESHOLD_INIT = 3;
    int MAX_VAL = 10;

    /** Constructor for CookAgent class
     * @param name name of the cook
     */
    public CookAgent(String name, Restaurant restaurant) {
	super();

	this.name = name;
	this.restaurant = restaurant;
	//Create the restaurant's inventory.
	inventory.put("Steak",new FoodData("Steak", 5));
	inventory.put("Chicken",new FoodData("Chicken", 4));
	inventory.put("Pizza",new FoodData("Pizza", 3));
	inventory.put("Salad",new FoodData("Salad", 2));
    }
    /** Private class to store information about food.
     *  Contains the food type, its cooking time, and ...
     */
    private class FoodData {
		String type; //kind of food
		double cookTime;
		/*New to v4.1*/ /*Part 2 Normative*/
		int amount = FOOD_AMOUNT;
		int threshold = THRESHOLD_INIT;
		int MAX = MAX_VAL;
    
	
	public FoodData(String type, double cookTime){
	    this.type = type;
	    this.cookTime = cookTime;
	}
    }
    /** Private class to store order information.
     *  Contains the waiter, table number, food item,
     *  cooktime and status.
     */
    private class Order {
	public WaiterAgent waiter;
	public int tableNum;
	public String choice;
	public Status status;
	public Food food; //a gui variable

	/** Constructor for Order class 
	 * @param waiter waiter that this order belongs to
	 * @param tableNum identification number for the table
	 * @param choice type of food to be cooked 
	 */
	public Order(WaiterAgent waiter, int tableNum, String choice){
	    this.waiter = waiter;
	    this.choice = choice;
	    this.tableNum = tableNum;
	    this.status = Status.pending;
	}

	/** Represents the object as a string */
	public String toString(){
	    return choice + " for " + waiter ;
	}
    }

    /*Part 2 Normative*/
    private class ETA {
    	int orderTime;
    	int deliveryTime;
    	Map<String, Integer> items;
    	
    	public ETA(int o, int d, Map i) {
    		orderTime = o;
    		deliveryTime = d;
    		items = i;
    	}
    }

    List<Map<String, Integer>> deliveries = new ArrayList<Map<String, Integer>>(); // Deliveries given from the market
    List <MarketAgent> markets = new ArrayList<MarketAgent>(); // List of cook’s markets
    int nextMarket = 0; // Used to selected which market to order from
    List<ETA> arrivalTimes = new ArrayList<ETA>(); // Receipts for deliveries with tracking information

    /*Part 2 Non-Normative*/
    int REASONABLE_WAIT = 3000; // The cook will be willing to wait 3000 for an order to arrive, else he/she will tell the customer to change an order

    
    
    // *** MESSAGES ***

    /** Message from a waiter giving the cook a new order.
     * @param waiter waiter that the order belongs to
     * @param tableNum identification number for the table
     * @param choice type of food to be cooked
     */
    public void msgHereIsAnOrder(WaiterAgent waiter, int tableNum, String choice){
	orders.add(new Order(waiter, tableNum, choice));
	stateChanged();
    }
    
    /*Part 2 Normative*/
    public void msgHereIsFoodDelivery(Map<String, Integer> items) {
    	deliveries.add(items);
    	stateChanged();
    }

    public void msgSorryWeCannotFulfillOrder() {
    	nextMarket++; // Reset to 0 if past markets.size()
    	stateChanged();
    }

    public void msgHereIsYourTrackingInformation(int orderTime, int deliveryTime, Map<String, Integer> items) {
    	arrivalTimes.add(new ETA(orderTime, deliveryTime, items));
    	stateChanged();
    }

    /** Scheduler.  Determine what action is called for, and do it. */
    protected boolean pickAndExecuteAnAction() {
	
//   /*Part 2 Normative*/
//   if ($ d in deliveries) then
//   	addFoodToInventory(d); return true;

    	
	//If there exists an order o whose status is done, place o.
	for(Order o:orders){
	    if(o.status == Status.done){
		placeOrder(o);
		return true;
	    }
	}
	//If there exists an order o whose status is pending, cook o.
	for(Order o:orders){
	    if(o.status == Status.pending){
		cookOrder(o);
		return true;
	    }
	}
	
//	/*Part 2 Normative*/
//	if (V choice s.t. inventory.get(choice).amounts < threashold) then
//		orderFromMarket(new Map<V choice, int num>);
//		// Implementation detail = getting V choice and num
//		return true;


	//we have tried all our rules (in this case only one) and found
	//nothing to do. So return false to main loop of abstract agent
	//and wait.
	return false;
    }
    

    // *** ACTIONS ***
    
    /** Starts a timer for the order that needs to be cooked. 
     * @param order
     */
    private void cookOrder(Order order){
    	if (inventory.get(order.choice).amount == 0) {
    		boolean removeOrder = false; // flag to remove the order
    		for (ETA eta: arrivalTimes) {
    			if (eta.items.get(order.choice) > 0) {
    				if (eta.deliveryTime > REASONABLE_WAIT) { // If an item of this choice is is NOT coming soon
    					removeOrder = true;
    				}
    				else { // The order is coming soon, break out of loop and continue with regular ordering stuff
    					removeOrder = false;
    					break;
    				}
    			}
    		}
    		if (removeOrder == true) { // If no delivery is coming soon for the proper ingredients
//    			order.waiter.msgOutOfThisItem(order.choice, order.tableNum);
    			orders.remove(order);  			
    		}
    	    else { // Continue as normal
    	    	DoCooking(order);
    	    	order.status = Status.cooking;
    	    }
    	}
    	
	    else {
	    	DoCooking(order);
	    	order.status = Status.cooking;
	    }
    }

    private void placeOrder(Order order){
	DoPlacement(order);
	order.waiter.msgOrderIsReady(order.tableNum, order.food);
	orders.remove(order);
    }
    
    /*Part 2 Normative*/
    private void orderFromMarket(Map<String, Integer> items) { // Send order to market
    	markets.get(nextMarket).msgNeedFoodDelivered(items);
    	stateChanged();
    }

    private void addFoodToInventory(Map<String, Integer> items) { // Fetch a delivery & add contents to inventory
    	// Iterate through inventory and add items to cook
    	for (ETA aT: arrivalTimes) {
    		if (aT.items == items) {
    			arrivalTimes.remove(aT);
    		}
    	}
    deliveries.remove(items);
    }



    // *** EXTRA -- all the simulation routines***

    /** Returns the name of the cook */
    public String getName(){
        return name;
    }

    private void DoCooking(final Order order){
	print("Cooking:" + order + " for table:" + (order.tableNum+1));
	//put it on the grill. gui stuff
	order.food = new Food(order.choice.substring(0,2),new Color(0,255,255), restaurant);
	order.food.cookFood();
	timer.schedule(new CookTask(order), (int)(inventory.get(order.choice).cookTime*1000));
	/*
	timer.schedule(new TimerTask(){
	    public void run(){//this routine is like a message reception    
		order.status = Status.done;
		stateChanged();
	    }
	}, (int)(inventory.get(order.choice).cookTime*1000));
	*/
    }
    
    public void DoPlacement(Order order){
	print("Order finished: " + order + " for table:" + (order.tableNum+1));
	order.food.placeOnCounter();
    }
    
    // Temporary Addition to the code just to see if my implementation will work - Lab 2 - might be permanent...
	private class CookTask extends TimerTask {
	// This would get all of the TimerTask functionality
		final Order cookOrder; // Need a reference to the order, or else this will not work
		public CookTask(final Order order) {
			cookOrder = order;
		}
	
		public void run() { // This method will be called by the timer thread
			cookOrder.status = Status.done;
			stateChanged();
		} // Since this class will be declared INSIDE CookAgent, the data from CookAgent is accessible from this class
	}
	
	public void addMarket(MarketAgent m) { // Will add a market to the cook's set of markets
		markets.add(m);
	}
	
	public void removeMarket(MarketAgent m) { // Will remove a market from the markets array
		markets.remove(m);
	}

}


    
