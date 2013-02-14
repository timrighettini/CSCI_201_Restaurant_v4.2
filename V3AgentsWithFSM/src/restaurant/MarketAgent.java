package restaurant;

import agent.Agent;

import java.util.*;

import restaurant.layoutGUI.*;
import java.awt.Color;

public class MarketAgent extends Agent {
	//Name: MarketAgent
	//Description:  This agent will deliver food to the cook, accepting payments from the cashier

	//Data: 
	/*Part 2 Normative*/
	enum orderState {unprocessed, pending, shipped, delivered}; //status of the order, used below
	private class Order { // Will holds the cook’s food orders
		String id; // order id
		Map<String, Integer> items; // Items ordered
		orderState state = orderState.unprocessed; // Set the state to unprocessed upon creation (because that is the state is needs to be in)
		
		public Order (String id, Map<String, Integer> items) {
			this.id = id;
			this.items = items;
		}		
	}

	private class Payment { // cashier payments
		double money;
		String id; // Matched to bills
		
		public Payment(double money, String id) {
			this.money = money;
			this.id = id;
		}
	}

	private List<Bill> billsToPay = new ArrayList<Bill>(); // Bills that the customers need to pay

	// Agent References
	private CookAgent cook; 
	private CashierAgent cashier;

	private List<Order> orders = new ArrayList<Order>(); // List of all the cook’s orders
	private List<Payment> cashierPayments = new ArrayList<Payment>(); // Payments from the cashier.  bill.choice will the id of an order instead of the item choice itself

	private volatile double totalMoney = 0.00; // Money that the market has, this value may not be implemented unless it is needed in v4.2
	private int timeForDelivery; // Shipping time used to estimate arrival times

	private Timer timer = new Timer(); // Used to simulate shipping times for orders

	//-----//
	private Map<String, Integer> inventory = new HashMap<String, Integer>(); // Total amount of each food item
	private Map<String, Double> foodPrices = new HashMap<String, Double>(); // Will help calculate bills
	
	// Other important variables
	private String name; // Name of the market
	int STARTING_NUM = 10; // This will hold how much of each item a market will start with.
	
	// Constructor
	public MarketAgent(String name) {
		super();
		this.name = name; // Set the name of the market
		
		// Set up the inventory for the market
		inventory.put("Steak", STARTING_NUM);
		inventory.put("Chicken", STARTING_NUM);
		inventory.put("Pizza", STARTING_NUM);
		inventory.put("Salad", STARTING_NUM);
		
		// Initialize the foodPrices Map -- Sets up the prices for food
		foodPrices.put("Steak", 15.99);
		foodPrices.put("Chicken", 10.99);
		foodPrices.put("Pizza", 5.99);
		foodPrices.put("Salad", 8.99);
	}

	// Messages:
	/*Part 2 Normative*/
	public void msgNeedFoodDelivered(Map<String, Integer> choices) { // Add cook order to list
		String orderID = "";
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
	public void processFoodOrder(Order o) { // Check to see if an order is fillable
		double d = doGetTotalCost(o.items);
		if (d <= 0) {
//			cook.msgSorryWeCannotFulfillOrder();
			orders.remove(o);
	}
		else {
//			cashier.msgHereIsYourBill(new Bill(d, o.id, this));
			o.state = orderState.pending;
		}
	}

	public void shipFoodOrder(Order o, Payment p) { // Send the order for shipping
		totalMoney += p.money;
		doSendOrder(o);
		cashierPayments.remove(p);
		stateChanged();
	}

	public void doSendOrder(final Order o) { // Set up shipping
	timer.schedule(new TimerTask() {
		public void run() {
			o.state = orderState.delivered;
			stateChanged();
		}
	}, timeForDelivery);
		o.state = orderState.shipped;
//		cook.msgHereIsYourTrackingInfo(currentTime, timeForDelivery, o.items);
	}

	public void deliverFoodOrder(Order o) {  // Deliver the order to the cook
//		cook.msgHereIsFoodDelivery(o.items);
		orders.remove(o);
	}

	//Other Methods:
	public double doGetTotalCost(Map<String, Integer> items) {
		double d = 0.00;
		
		// Create the methodology for determining d
				
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
