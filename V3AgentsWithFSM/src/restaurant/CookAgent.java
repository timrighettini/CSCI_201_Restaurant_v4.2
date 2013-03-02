package restaurant;

import restaurant.interfaces.*;
import agent.Agent;
import java.util.*;

import restaurant.layoutGUI.*;
import java.awt.Color;


/** Cook agent for restaurant.
 *  Keeps a list of orders for waiters
 *  and simulates cooking them.
 *  Interacts with waiters only.
 */
public class CookAgent extends Agent implements Cook {

    //List of all the orders
    private List<CookOrder> orders = Collections.synchronizedList(new ArrayList<CookOrder>());
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
    
    // New Map to determine what has been ordered
    Map<String, Boolean> itemOrdered = new HashMap<String, Boolean>();
    
    // Make a global cashier reference, since there is only ONE CASHIER for any given restaurant
    /*
   	To quote the professor:
    	Your market should not have a global for a SINGLE cook and a
		SINGLE cashier. A market can have many client Restaurants. What market
		only deals with one restaurant? Our restaurant only has one cook and
		one cashier, but there might be many restaurants in a system.
		
	He sent this to me in an e-mail, meaning that HERE I can have a global cashier reference, but not for the marketAgent
	*/
    List<Cashier> cashiers = Collections.synchronizedList(new ArrayList<Cashier>()); // Will be given to the cook by the restaurant simulation

    /** Constructor for CookAgent class
     * @param name name of the cook
     */
    public CookAgent(String name, Restaurant restaurant) {
	super();

	this.name = name;
	this.restaurant = restaurant;
	//Create the restaurant's inventory.
	inventory.put("Steak",new FoodData("Steak", 5, FOOD_AMOUNT));
	inventory.put("Chicken",new FoodData("Chicken", 4, FOOD_AMOUNT));
	inventory.put("Pizza",new FoodData("Pizza", 3, FOOD_AMOUNT));
	inventory.put("Salad",new FoodData("Salad", 2, FOOD_AMOUNT));
	
	// Initialize order tracking map
    itemOrdered.put("Steak", false);
    itemOrdered.put("Chicken", false);
    itemOrdered.put("Pizza", false);
    itemOrdered.put("Salad", false);
	
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
	
		public FoodData(String type, double cookTime, int num){
		    this.type = type;
		    this.cookTime = cookTime;
		    this.amount = num;
		}
    }
    /** Private class to store order information.
     *  Contains the waiter, table number, food item,
     *  cooktime and status.
     */
    
    /*This class was made public within the restaurant package to allow for easy usage of shared data for the restaurant producer-consumer monitor*/
//    private class CookOrder {
//	public WaiterAgent waiter;
//	public int tableNum;
//	public String choice;
//	public Status status;
//	public Food food; //a gui variable
//	public boolean waitingForShipment = false; // This will be used keep an order on standby until a shipment arrives if it has already been ordered
//
//	/** Constructor for CookOrder class 
//	 * @param waiter waiter that this order belongs to
//	 * @param tableNum identification number for the table
//	 * @param choice type of food to be cooked 
//	 */
//	public CookOrder(Waiter waiter, int tableNum, String choice){
//	    this.waiter = (WaiterAgent) waiter;
//	    this.choice = choice;
//	    this.tableNum = tableNum;
//	    this.status = Status.pending;
//	}
//
//	/** Represents the object as a string */
//	public String toString(){
//	    return choice + " for " + waiter ;
//	}
//    }

    /*Part 2 Normative*/
    private class ETA {
    	long orderTime;
    	int deliveryTime;
    	Map<String, Integer> items;
    	
    	public ETA(long o, int d, Map i) {
    		orderTime = o;
    		deliveryTime = d;
    		items = i;
    	}
    }

    List<Map<String, Integer>> deliveries = Collections.synchronizedList(new ArrayList<Map<String, Integer>>()); // Deliveries given from the market
    List <MarketAgent> markets = Collections.synchronizedList(new ArrayList<MarketAgent>()); // List of cook�s markets
    int nextMarket = 0; // Used to selected which market to order from
    List<ETA> arrivalTimes = Collections.synchronizedList(new ArrayList<ETA>()); // Receipts for deliveries with tracking information

    /*Part 2 Non-Normative*/
    int REASONABLE_WAIT = 8000; // The cook will be willing to wait 5000 milliseconds for an order to arrive, else he/she will tell the customer to change an order
    
    /* New to v4.2*/
    private volatile int totalCallTasks = 0; // This value will keep track of how many callTasks have been called in the scheduler.  If this value is not 0, then the callTask will not fire
    
    // *** MESSAGES ***

    /** Message from a waiter giving the cook a new order.
     * @param waiter waiter that the order belongs to
     * @param tableNum identification number for the table
     * @param choice type of food to be cooked
     */
    public void msgHereIsAnOrder(Waiter waiter, int tableNum, String choice){
	orders.add(new CookOrder(waiter, tableNum, choice));
	stateChanged();
    }
    
    /*Part 2 Normative*/
    public void msgHereIsFoodDelivery(Map<String, Integer> items) {
    	deliveries.add(items);
    	stateChanged();
    }

    public void msgSorryWeCannotFulfillOrder(String item) {
    	nextMarket++;
    	if (nextMarket == markets.size()) {// Reset to 0 if == markets.size()
    		nextMarket = 0;
    		print("No markets can fulfill my order for: " + item + ".  I will try again next time I am awake");
    	}
    	else {
    		print("Market " + markets.get(nextMarket - 1).getName() + " could not fulfill my order for " + item  + ", attempting to order from market " + markets.get(nextMarket).getName());
    		stateChanged(); // Will not indefinitely run the scheduler
    	}
    	itemOrdered.put(item, false);    	
    }

    public void msgHereIsYourTrackingInformation(long orderTime, int deliveryTime, Map<String, Integer> items) {
    	arrivalTimes.add(new ETA(orderTime, deliveryTime, items));
    	stateChanged();
    }

    /** Scheduler.  Determine what action is called for, and do it. */
    protected boolean pickAndExecuteAnAction() {
	
	    /*Part 2 Normative*/
	    
	    // Use temp variable for the synchronized loop
	    Map<String, Integer> tempDelivery = null;
	    
	    synchronized(deliveries) {
		    for (Map<String, Integer> d: deliveries) { // If there is a delivery, get the items from it
		    	tempDelivery = d;
		    	break;
		    }	    
	    }
	    if (tempDelivery != null) {
	    	addFoodToInventory(tempDelivery);
	    	return true;
	    }
	    
	    // Use temp variable for the synchronized loop
	    CookOrder tempOrder = null;
	    	
		//If there exists an order o whose status is done, place o.
	    synchronized(orders) {
			for (CookOrder o:orders){
			    if(o.status == Status.done && o.waitingForShipment == false){
			    	tempOrder = o;
			    	break;
			    }
			}
	    }
		if (tempOrder != null) {
			placeOrder(tempOrder);
			return true;
		}
		
		
		//If there exists an order o whose status is pending, cook o.
		synchronized(orders) {
			for (CookOrder o:orders){
			    if(o.status == Status.pending){
			    	tempOrder = o;
			    	break;
			    }
			}		
		}
		if (tempOrder != null) {
			cookOrder(tempOrder);
			return true;
		}
		
		// If there exists an order in the revolving stand, take it out and place it within the cooks order list
		if (restaurant.revolvingStand.getSize() > 0) { /* New to v4.2 */
			getOrderFromRevolvingStand();
			return true;
		}
		
	//	/*Part 2 Normative*/
		// Get the keys of the inventory and turn it into an array	
		Set<String> keys = inventory.keySet(); // Iterate through the map
		synchronized(keys) {
			for (String k: keys) {
				if (inventory.get(k).amount <= inventory.get(k).threshold) { // If the amount of something in the inventory is < threshold, order that item type
					if (itemOrdered.get(k) == false && markets.size() > 0) { // The order cannot already be ordered, and markets must exist for this operation to occur
						orderFromMarket(k);
						return true;
					}			
				}
			}
		}
		
		// Make sure to wake up the cook so that he/she can constantly check the shared data structure
		if (super.getPermitNumber() == 0 && totalCallTasks == 0) {
			timer.schedule(new CallTask(this), 10000);
			totalCallTasks++;
		}
	
		//we have tried all our rules (in this case only one) and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
		return false;
    }
    

    // *** ACTIONS ***
    
    /** Starts a timer for the order that needs to be cooked. 
     * @param order
     */
    private void cookOrder(CookOrder order){
    	if (inventory.get(order.choice).amount <= 0 && order.waitingForShipment == false) {
    		boolean removeOrder = true; // flag to remove the order
    		long timeForDelivery = 0;
    		synchronized(arrivalTimes) { // Since I'm not synchronizing the entire action, the system should not deadlock
	    		for (ETA eta: arrivalTimes) {
	    			if (eta.items.get(order.choice) != null) { // If an order for this item actually exists
	    				timeForDelivery = eta.deliveryTime + (eta.orderTime - System.currentTimeMillis());
	    				//print(eta.deliveryTime + " " + eta.orderTime + " " + System.currentTimeMillis()); // Debug for the algorithm
	    				if (timeForDelivery > REASONABLE_WAIT) { 
	    					// If an item of this choice is is NOT coming soon (if time ordered - currentTime > deliveryTime - REASONABLE_WAIT)
	    					removeOrder = true;
	    				}
	    				else { // The order is coming soon, break out of loop and continue with regular ordering stuff
	    					removeOrder = false;
	    					break;
	    				}
	    			}
	    		}
    		}
    		if (removeOrder == true) { // If no delivery is coming soon for the proper ingredients
    			print("Out of item: " + order.choice + ".  Telling " + order.waiter + " to get a different order.");
    			order.waiter.msgOutOfThisItem(order.choice, order.tableNum);
    			orders.remove(order);  			
    		}
    	    else { // Wait to cook the order until it arrives
    	    	print("Currently out of stock of: " + order.choice + " for " + order.tableNum + ", but shipment will arrive in " +  (timeForDelivery) + " milliseconds.");
    	    	print("Will cook it when shipment arrives.");
    	    	order.waitingForShipment = true;
    	    }
    	}
    	
	    else if (inventory.get(order.choice).amount > 0) { // Do not cook an order if there is NOT any of that item currently in the inventory 
	    	DoCooking(order);
	    	order.status = Status.cooking;
	    }
    }

    private void placeOrder(CookOrder order){
	DoPlacement(order);
	order.waiter.msgOrderIsReady(order.tableNum, order.food);
	orders.remove(order);
    }
    
    /*Part 2 Normative*/
    private void orderFromMarket(String item) { // Send order to market
      	print("Ordering " + item + " from market " + markets.get(nextMarket));
    	// Make an order from the string
    	Map<String, Integer> items = new HashMap<String, Integer>();
    	int numOfItemsToOrder = inventory.get(item).MAX - inventory.get(item).amount;
    	items.put(item, numOfItemsToOrder); // Max out the order - amount
    	markets.get(nextMarket).msgNeedFoodDelivered(items, this, cashiers.get(0));
    	
    	itemOrdered.put(item, true);
    	//System.out.println("Item: " + item + " : " + itemOrdered.get(item) );
    }

    private void addFoodToInventory(Map<String, Integer> items) { // Fetch a delivery & add contents to inventory
    	// Iterate through inventory and add items to cook
    	synchronized(arrivalTimes) {
	    	for (ETA aT: arrivalTimes) {
	    		if (aT.items == items) {
	    			arrivalTimes.remove(aT);
	    			break;
	    		}
	    	}
    	}
    	
		Set<String> setKeys = items.keySet(); // The size of setKeys will always be one, but if there ever is more than one item to be ordered, this loop is usable in the future
		synchronized(setKeys) {
			for (String s: setKeys) {
				inventory.get(s).amount += items.get(s); // Add the items from the order into the inventory
				itemOrdered.put(s, false); // Set this ordered value to false so that the item can be ordered again if it runs out
				// Check through orders arrayList and make all waitForShipment values to false so that the items can actually be cooked
				synchronized(orders) {
					for (CookOrder o: orders) {
						o.waitingForShipment = false;	
					}
				}
			}   
		}
    	
    	deliveries.remove(items);
    }

    /* New in v4.2 */
    private void getOrderFromRevolvingStand() {
    	CookOrder o = restaurant.revolvingStand.remove();
    	orders.add(o);
    	print("Order removed from the revolving stand: " + o.toString());
    }

    // *** EXTRA -- all the simulation routines***

    /** Returns the name of the cook */
    public String getName(){
        return name;
    }

    private void DoCooking(final CookOrder order){
    // Decrement the inventory foodData amount for this specific item by 1
    inventory.get(order.choice).amount -= 1;
    	
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
    
    public void DoPlacement(CookOrder order){
	print("Order finished: " + order + " for table:" + (order.tableNum+1));
	order.food.placeOnCounter();
    }
    
    // Temporary Addition to the code just to see if my implementation will work - Lab 2 - might be permanent...
	private class CookTask extends TimerTask {
	// This would get all of the TimerTask functionality
		final CookOrder cookOrder; // Need a reference to the order, or else this will not work
		public CookTask(final CookOrder order) {
			cookOrder = order;
		}
	
		public void run() { // This method will be called by the timer thread
			cookOrder.status = Status.done;
			stateChanged();
		} // Since this class will be declared INSIDE CookAgent, the data from CookAgent is accessible from this class
	}
	
	private class CallTask extends TimerTask { // Used to keep the number of checks for the shared data array down
		CookAgent c;
		public CallTask(CookAgent c) {
			this.c = c;	
		}
		public void run() {
			if (c.getPermitNumber() == 0 && c.getTotalCallTasks() == 1) { // Only this thread remains
				c.print("Checked the revolving stand for items.");
				stateChanged();
			}
			c.decTotalCallTasks();
		}
	}
	
	public int getTotalCallTasks() {
		return totalCallTasks;
	}
	
	public void decTotalCallTasks() {
		if (totalCallTasks > 0) {
			totalCallTasks--;
		}
	}
	
	public void addMarket(Market m) { // Will add a market to the cook's set of markets
		markets.add((MarketAgent) m);
	}
	
	public void removeMarket(Market m) { // Will remove a market from the markets array
		markets.remove(m);
	}

	public int getInventoryItemNumber(String t) { // Get the amount that an item has 
		return inventory.get(t).amount;		
	}
	
	public void setInventoryItemNumber(String choice, int num) { // Set the amount that an item will have
		inventory.get(choice).amount = num;
		stateChanged(); // This is necessary so that the cook can check the state of the inventory and order items if necessary after that occurs
	}
	
	public void setCashier(Cashier c) {
		cashiers.add(c); // There will only be one cashier for this restaurant, but it can be improved to add more cashiers later on...
	}
	
}


    
