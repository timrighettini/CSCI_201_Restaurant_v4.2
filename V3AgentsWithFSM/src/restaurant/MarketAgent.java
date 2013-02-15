package restaurant;

import agent.Agent;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

import restaurant.layoutGUI.*;
import java.awt.Color;

public class MarketAgent extends Agent {
	//Name: MarketAgent
	//Description:  This agent will deliver food to the cook, accepting payments from the cashier

	//Data: 
	/*Part 2 Normative*/
	enum orderState {unprocessed, processing, pending, packaging, shipped, delivered}; //status of the order, used below
	private class Order { // Will holds the cook’s food orders
		public String id; // order id
		public Map<String, Integer> items; // Items ordered
		public orderState state = orderState.unprocessed; // Set the state to unprocessed upon creation (because that is the state is needs to be in)
		
		public Order (String id, Map<String, Integer> items) {
			this.id = id;
			this.items = items;
		}		
	}

	private class Payment { // cashier payments
		public double money;
		public String id; // Matched to bills
		
		public Payment(double money, String id) {
			this.money = money;
			this.id = id;
		}
	}

	// Agent References
	private CookAgent cook; 
	private CashierAgent cashier;

	private List<Order> orders = new ArrayList<Order>(); // List of all the cook’s orders
	private List<Payment> cashierPayments = new ArrayList<Payment>(); // Payments from the cashier.  bill.choice will the id of an order instead of the item choice itself

	private volatile double totalMoney = 0.00; // Money that the market has, this value may not be implemented unless it is needed in v4.2
	private int timeForDelivery = 4000; // Shipping time used to estimate arrival times in milliseconds

	private Timer timer = new Timer(); // Used to simulate shipping times for orders

	//-----//
	private Map<String, Integer> inventory = new HashMap<String, Integer>(); // Total amount of each food item
	private Map<String, Double> foodPrices = new HashMap<String, Double>(); // Will help calculate bills
	
	Random rand = new Random(); // Used for generating random order IDs
	
	// Other important variables
	private String name; // Name of the market
	int STARTING_NUM = 10; // This will hold how much of each item a market will start with.
	DecimalFormat df = new DecimalFormat("######.##");
	
	// Constructor
	public MarketAgent(String name) {
		super();
		this.name = name; // Set the name of the market
		
		// Set up the inventory for the market
		inventory.put("Steak", STARTING_NUM);
		inventory.put("Chicken", STARTING_NUM);
		inventory.put("Pizza", STARTING_NUM);
		inventory.put("Salad", STARTING_NUM);
		
		// Initialize the foodPrices Map -- Sets up the prices for food -- 1/4 of what the restaurant charges customers
		foodPrices.put("Steak", 3.99);
		foodPrices.put("Chicken", 2.75);
		foodPrices.put("Pizza", 2.49);
		foodPrices.put("Salad", 1.49);
		df.setRoundingMode(RoundingMode.UP);
	}

	// Messages:
	/*Part 2 Normative*/
	public void msgNeedFoodDelivered(Map<String, Integer> choices) { // Add cook order to list
		// Make the order ID, and MAKE SURE that there are no duplicates!
		
		String orderID; // ID for the order
		
		while (true) {	
			orderID = Integer.toString(rand.nextInt(1000000));
			
			for (Order o: orders) { // Check orders to make sure that a duplicate order number does not exist
				if (orderID.equals(o.id)) {
					continue;
				}
			}
			break; // All order IDs == unique
		}
		
		orders.add(new Order(orderID, choices));
		stateChanged();
	}

	public void msgHereIsCashierPayment(double money, String oID) { // Get payment from cashier
		cashierPayments.add(new Payment(money, oID));
		stateChanged();
	}

	//Scheduler:
	/*Part 2 Normative*/
	/* PseudoCode: */
//	if ($ o in orders) then
//		if ($ o s.t. o.state == orderState.unprocessed) then
//			processFoodOrder(o);
//	if ($ o in orders s.t. o.state == orderState.pending && $ cP in cashierPayments s.t. cP.id == o.id) then
//		shipFoodOrder(o, cP);	
//	if ($ o s.t. o.state == orderState.delivered) then
//		deliverFoodOrder(o);
//
//	return false;
	
	protected boolean pickAndExecuteAnAction() {
		for (Order o: orders) { // Loop through all of the orders
			if (o.state == orderState.unprocessed) { // The food order needs to be processed, and a bill sent out
				processFoodOrder(o);
				return true;
			}
			
			if (o.state == orderState.pending) { // Order will be checked to incoming customer payments if it is pending
				for (Payment cP: cashierPayments) { // Check to a cashierPayment
					if (cP.id.equals(o.id) ) {
						shipFoodOrder(o, cP);
						return true;
					}
				}
			}
			
			if (o.state == orderState.delivered) { // Order needs to be removed from the list once it has been delivered
				deliverFoodOrder(o);
				return true;
			}
		}
		
		return false;
	}

	//Actions:
	/*Part 2 (Non-)Normative*/
	private void processFoodOrder(final Order o) { // Check to see if an order is fillable
		print("Processing Order: " + o.id + " " +  o.items);
		o.state = orderState.processing; // Set the order state to processing, so that nothing weird happens with the scheduler
		timer.schedule(new TimerTask() {
			public void run() {				
				doProcessFoodOrder(o); // Simulate a delay for processing the order
			}			
		}, 2000);
	}
	
	private void doProcessFoodOrder (Order o) {
		double d = doGetTotalCost(o.items);
		if (d <= 0) {
			Set<String> setKeys = o.items.keySet(); // The size of setKeys will always be one, but if there ever is more than one item to be ordered, this loop is usable in the future
			for (String s: setKeys) {
				print("Order Cannot be Fulfilled -- Order Cancelled: " + o.id + " " +  o.items);
				cook.msgSorryWeCannotFulfillOrder(s);
			}
			orders.remove(o);
		}
		else {
			cashier.msgHereIsBill(new Bill(d, o.id, this));
			o.state = orderState.pending;
			print("Order Successfully Fulfilled: " + o.id + " " +  o.items + ".  Bill sent to " + cashier.getName());
		}
	}

	private void shipFoodOrder(final Order o, final Payment p) { // Send the order for shipping
		totalMoney += p.money;
		print("Packaging food order: " + o.id + " " +  o.items + ".  Total Money = " + df.format(totalMoney));
		o.state = orderState.packaging; // Set the order state to packaging, so that nothing weird happens with the scheduler
		timer.schedule(new TimerTask() {
			public void run() {
				doSendOrder(o); // Simulate time for packaging the order
				cashierPayments.remove(p);
				stateChanged();
			}
			
		}, 1500);
	}

	private void doSendOrder(final Order o) { // Set up shipping
	timer.schedule(new TimerTask() {
		public void run() {
			print("Delivering food order: " + o.id + " " +  o.items);
			o.state = orderState.delivered;
			stateChanged();
		}
	}, timeForDelivery * (rand.nextInt(9) + 1)); // Will make the time anywhere 
		o.state = orderState.shipped;
		print("Shipped food order: " + o.id + " " +  o.items + ".  Estimated time for delivery (in milliseconds): " + (timeForDelivery * (rand.nextInt(9) + 1)));
		cook.msgHereIsYourTrackingInformation(System.currentTimeMillis(), timeForDelivery, o.items);
	}

	private void deliverFoodOrder(Order o) {  // Deliver the order to the cook
		print("Order Delivered: " + o.id + " " +  o.items);
		cook.msgHereIsFoodDelivery(o.items);
		orders.remove(o);
	}

	//Other Methods:
	private double doGetTotalCost(Map<String, Integer> items) {
		double d = 0.00;
		
		// Create the methodology for determining d
		Set<String> keys = items.keySet(); // Iterate through the map
		for (String k: keys) {
			if (items.get(k) > inventory.get(k)) { // If this market has less items in its inventory than the items Map asked for, then return 0.00 for cost
				d = 0.00;
				return d;
			}
			else  {// Else, actually make the bill's cost 
				d += foodPrices.get(k) * items.get(k); // Get the price * number of items to be bought
			}
		}
		
		// If Cost is created successfully, decrement the inventory of the market appropriately
		for (String k: keys) {
			inventory.put(k, (inventory.get(k) - items.get(k))); // Subtract out the appropriate values from the inventory 
		}
		
		return d;
	} // Will determine the cost of a bill based on what the cooks wants and current market inventory.   returns 0 if inventory cannot handle order.

	public String getName() {
		return name;
	}
	
	public String toString() {
		return "Market " + getName();
	}
	
	// Create setCook and setCashier methods
	public void setCook(CookAgent cook) {
		this.cook = cook;
	}
	
	public void setCashier(CashierAgent cashier) {
		this.cashier = cashier;
	}	
	
	public void setInventory(String c, int num) {
		inventory.put(c, num); // This will override the previous entry and add in the new one
	}
	
	public Map<String, Integer> getInventory() {
		return inventory;		
	}
	
	public void setInventory(Map<String, Integer> items) { // Second manner in which to change the inventory
		inventory = items;		
	}
}
