package restaurant;

import restaurant.gui.RestaurantGui;
import restaurant.layoutGUI.*;
import agent.Agent;
import java.util.*;
import java.awt.Color;

/** Restaurant customer agent. 
 * Comes to the restaurant when he/she becomes hungry.
 * Randomly chooses a menu item and simulates eating 
 * when the food arrives. 
 * Interacts with a waiter only */
public class CustomerAgent extends Agent {
    private String name;
    private int hungerLevel = 5;  // Determines length of meal
    private RestaurantGui gui;
    
    // ** Agent connections **
    private HostAgent host;
    private WaiterAgent waiter;
    Restaurant restaurant;
    private Menu menu; // Will allow the customer to order and pay for food
    Timer timer = new Timer();
    GuiCustomer guiCustomer; //for gui
   // ** Agent state **
    private boolean isHungry = false; //hack for gui
    public enum AgentState
	    {DoingNothing, WaitingInRestaurant, WaitingTablesAreFull /*Part 4.1 NN*/,  SeatedWithMenu, WaiterCalled, WaitingForFood, Eating, Paying /*New to v4.1*/};
	//{NO_ACTION,NEED_SEATED,NEED_DECIDE,NEED_ORDER,NEED_EAT,NEED_LEAVE};
    private AgentState state = AgentState.DoingNothing;//The start state
    public enum AgentEvent 
	    {gotHungry, tablesAreFull /*Part 4.1 NN*/, beingSeated, decidedChoice, waiterToTakeOrder, foodDelivered, doneEating, donePaying /*New to v4.1*/};
    List<AgentEvent> events = new ArrayList<AgentEvent>();
    
    /*New to v4.1*/
    private CashierAgent cashier; // The customer pays this agent before leaving with “wallet” – see below 

    private Bill bill; // Will be given from the waiter and passed to the cashier
    private volatile double wallet = 15.00; // Will hold how much money a customer has (will be thread safe) -- Starting value is $15.00
    private volatile double amountOwed = 0.00; // How much money the customer owes the restaurant (will be thread safe)

    /*Part 4.1 Non-Normative*/
    // These values all deal with the tables full non normative scenario
    private boolean willingToWait = false; // Will be set-able with GUI
    private int waitListSize = 0; // Size of the waitlist
    private int LOW_WAITLIST = 3; // Number used to determine if the customer will wait in line or not 

    /*Part 4.2 Non-Normative*/
    private boolean willOnlyPayFully = true;  // Will be set-able in the GUI or in some other fashion

    
    /** Constructor for CustomerAgent class 
     * @param name name of the customer
     * @param gui reference to the gui so the customer can send it messages
     */
    public CustomerAgent(String name, RestaurantGui gui, Restaurant restaurant) {
	super();
	this.gui = gui;
	this.name = name;
	this.restaurant = restaurant;
	guiCustomer = new GuiCustomer(name.substring(0,2), new Color(0,255,0), restaurant);
    }
    public CustomerAgent(String name, Restaurant restaurant) {
	super();
	this.gui = null;
	this.name = name;
	this.restaurant = restaurant;
	guiCustomer = new GuiCustomer(name.substring(0,1), new Color(0,255,0), restaurant);
    }
    // *** MESSAGES ***
    /** Sent from GUI to set the customer as hungry */
    public void setHungry() {
	events.add(AgentEvent.gotHungry);
	isHungry = true;
	print("I'm hungry");
	stateChanged();
    }
    /** Waiter sends this message so the customer knows to sit down 
     * @param waiter the waiter that sent the message
     * @param menu a reference to a menu */
    public void msgFollowMeToTable(WaiterAgent waiter, Menu menu) {
	this.menu = menu;
	this.waiter = waiter;
	print("Received msgFollowMeToTable from" + waiter);
	events.add(AgentEvent.beingSeated);
	stateChanged();
    }
    /** Waiter sends this message to take the customer's order */
    public void msgDecided(){
	events.add(AgentEvent.decidedChoice);
	stateChanged(); 
    }
    /** Waiter sends this message to take the customer's order */
    public void msgWhatWouldYouLike(){
	events.add(AgentEvent.waiterToTakeOrder);
	stateChanged(); 
    }

    /** Waiter sends this when the food is ready 
     * @param choice the food that is done cooking for the customer to eat */
    public void msgHereIsYourFood(String choice) {
	events.add(AgentEvent.foodDelivered);
	stateChanged();
    }
    /** Timer sends this when the customer has finished eating */
    public void msgDoneEating() {
	events.add(AgentEvent.doneEating);
	stateChanged(); 
    }

    /*New to v4.1*/
    public void msgThankYouComeAgain() { /*Part 1 Normative*/
    // Since I am using doubles and NOT dollars/cents for this, I am currently not 
    // implementing giving change to customers for the sake of simplicity
    	events.add(AgentEvent.donePaying);
    	stateChanged();
    }

    public void msgNextTimePayTheDifference(double difference) { /*Part 1 Non-Normative*/
    	events.add(AgentEvent.donePaying);
    	amountOwed+=difference;
    	stateChanged();
    }

    /*Part 2 Non-Normative*/
    public void msgPleaseReorder(Menu menu) {
    	this.menu = menu;
    	state = AgentState.WaiterCalled;
    	events.add(AgentEvent.waiterToTakeOrder);
    	stateChanged();
    }

    /*Part 4.1 Non-Normative*/
    public void msgSorryTablesAreOccupied(int wSize) {
    	state = AgentState.WaitingTablesAreFull;
    	events.add(AgentEvent.tablesAreFull);
    	waitListSize = wSize;
    	stateChanged();
    }

    
    
    /** Scheduler.  Determine what action is called for, and do it. */
    protected boolean pickAndExecuteAnAction() {
	if (events.isEmpty()) return false;
	AgentEvent event = events.remove(0); //pop first element
	
	//Simple finite state machine
	if (state == AgentState.DoingNothing){
	    if (event == AgentEvent.gotHungry)	{
		goingToRestaurant();
		state = AgentState.WaitingInRestaurant;
		return true;
	    }
	    // elseif (event == xxx) {}
	}
	
//	/*Part 4 Non-Normative*/
//	if ($ state s.t. state == AgentState.WaitingTablesAreFull) then
//		if (event == AgentEvent.tablesAreFull) then
//			doWaitResponse();
//			return true;
//
	
	if (state == AgentState.WaitingTablesAreFull) {
	    if (event == AgentEvent.tablesAreFull)	{
			doWaitResponse();
			return true;
	    }
	}
	
	if (state == AgentState.WaitingInRestaurant) {
	    if (event == AgentEvent.beingSeated)	{
		makeMenuChoice();
		state = AgentState.SeatedWithMenu;
		return true;
	    }
	}
	if (state == AgentState.SeatedWithMenu) {
	    if (event == AgentEvent.decidedChoice)	{
		callWaiter();
		state = AgentState.WaiterCalled;
		return true;
	    }
	}
	if (state == AgentState.WaiterCalled) {
	    if (event == AgentEvent.waiterToTakeOrder)	{
		orderFood();
		state = AgentState.WaitingForFood;
		return true;
	    }
	}
	if (state == AgentState.WaitingForFood) {
	    if (event == AgentEvent.foodDelivered)	{
		eatFood();
		state = AgentState.Eating;
		return true;
	    }
	}
	///*
	if (state == AgentState.Eating) {
	    if (event == AgentEvent.doneEating)	{
		leaveRestaurant();
		state = AgentState.DoingNothing;
		return true;
	    }
	}
	//*/
	
//	/*New to v4.1*/
//	/*Part 1 Normative*/
//	if ($ state s.t. state == AgentState.Eating) then
//		if (event == AgentEvent.doneEating) then
//			payBill(); // Action
//			state = AgentState.Paying; // Change to appropriate state
//			return true;
//
//	if ($ state s.t. state == AgentState.Paying) then
//		if (event == AgentEvent.donePaying) then
//			leaveRestaurant(); // Action
//			state = AgentState.DoingNothing; // Change to appropriate state
//			return true;

	/*
	if (state == AgentState.Eating) {
	    if (event == AgentEvent.doneEating)	{
			payBill(); // Action
			state = AgentState.Paying; // Change to appropriate state
			return true;
	    }
	}
	
	if (state == AgentState.Paying) {
	    if (event == AgentEvent.donePaying)	{
			leaveRestaurant(); // Action
			state = AgentState.DoingNothing; // Change to appropriate state
			return true;
	    }
	}
	*/
	
	print("No scheduler rule fired, should not happen in FSM, event="+event+" state="+state);
	return false;
    }
    
    // *** ACTIONS ***
    
    /** Goes to the restaurant when the customer becomes hungry */
    private void goingToRestaurant() {
	print("Going to restaurant");
	guiCustomer.appearInWaitingQueue();
	host.msgIWantToEat(this);//send him our instance, so he can respond to us
	stateChanged();
    }
    
    /** Starts a timer to simulate the customer thinking about the menu */
    private void makeMenuChoice(){
	print("Deciding menu choice...(3000 milliseconds)");
	timer.schedule(new TimerTask() {
	    public void run() {  
		msgDecided();	    
	    }},
	    3000);//how long to wait before running task
	stateChanged();
    }
    private void callWaiter(){
	print("I decided!");
	waiter.msgImReadyToOrder(this);
	stateChanged();
    }

    /** Picks a random choice from the menu and sends it to the waiter */
    private void orderFood(){
    	String choice; // Will be instantiated once the customer does not choose "unavailable" 
    	while (true) {
    		choice = menu.choices[(int)(Math.random()*4)];
    		if (choice.equals("unavailable")) {
    			continue; // The customer cannot order this item, he/she will have to try again
    		}
    		else {
    			break; // Item is orderable, break
    		}
    	}
	print("Ordering the " + choice);
	waiter.msgHereIsMyChoice(this, choice);
	stateChanged();
    }

    /** Starts a timer to simulate eating */
    private void eatFood() {
	print("Eating for " + hungerLevel*1000 + " milliseconds.");
	timer.schedule(new TimerTask() {
	    public void run() {
		msgDoneEating();    
	    }},
	    getHungerLevel() * 1000);//how long to wait before running task
	stateChanged();
    }
    

    /** When the customer is done eating, he leaves the restaurant */
    private void leaveRestaurant() {
	print("Leaving the restaurant");
	guiCustomer.leave(); //for the animation
	waiter.msgDoneEatingAndLeaving(this);
	isHungry = false;
	stateChanged();
	gui.setCustomerEnabled(this); //Message to gui to enable hunger button
    
	//hack to keep customer getting hungry. Only for non-gui customers
	if (gui==null) becomeHungryInAWhile();//set a timer to make us hungry.
    }
    
    
	
	/*New to v4.1*/
	private void payBill() { // Have the Customer send the bill to the cashier
//		cashier.msgHereIsCustomerPayment(subtractFromWallet(bill.totalCost), bill); 
		stateChanged();
	}

	//-----//
	private void doWaitResponse() {
		if (willingToWait == true || waitListSize < LOW_WAITLIST) {
//			host.msgThankYouIllWait(this);
			state = AgentState.WaitingInRestaurant;
		}
		else {
//			host.msgSorryIHaveToLeave(this);
			state = AgentState.DoingNothing;
		}
		stateChanged();
	}

	private double subtractFromWallet (double num) { // Will return how much was subtracted from wallet
		wallet -= num; 
		double val = 0;
		if (wallet < 0) { val  = wallet + num; wallet = 0; return val; }
		else { return num; }
	}
    
    
    /** This starts a timer so the customer will become hungry again.
     * This is a hack that is used when the GUI is not being used */
    private void becomeHungryInAWhile() {
	timer.schedule(new TimerTask() {
	    public void run() {  
		setHungry();		    
	    }},
	    15000);//how long to wait before running task
    }

    // *** EXTRA ***

    /** establish connection to host agent. 
     * @param host reference to the host */
    public void setHost(HostAgent host) {
		this.host = host;
    }
    
    /** Returns the customer's name
     *@return name of customer */
    public String getName() {
	return name;
    }

    /** @return true if the customer is hungry, false otherwise.
     ** Customer is hungry from time he is created (or button is
     ** pushed, until he eats and leaves.*/
    public boolean isHungry() {
	return isHungry;
    }

    /** @return the hungerlevel of the customer */
    public int getHungerLevel() {
	return hungerLevel;
    }
    
    /** Sets the customer's hungerlevel to a new value
     * @param hungerLevel the new hungerlevel for the customer */
    public void setHungerLevel(int hungerLevel) {
	this.hungerLevel = hungerLevel; 
    }
    public GuiCustomer getGuiCustomer(){
	return guiCustomer;
    }
    
    /** @return the string representation of the class */
    public String toString() {
	return "customer " + getName();
    }
    
    
    // Other v4.1 values
    public double getWallet() {
    	return wallet;
    }

    public void setWallet(double money) { // Set it so that the customer has money
    	wallet = money;
    }
    
    public double getAmountOwed() {
    	return amountOwed;
    }
    
    public void setAmountOwed(double money) {
    	amountOwed = money;
    }
    
    public boolean getWillingToWait() {
    	return willingToWait;
    }
    
    public void setWillingToWait(boolean b) {
    	willingToWait = b;
    }
    
    public boolean getWillOnlyPayFully() {
    	return willOnlyPayFully;
    }
    
    public void setWillOnlyPayFully(boolean b) {
    	willOnlyPayFully = b;
    }
    
    public void setCashier(CashierAgent c) {
    	this.cashier = c;
    }
    
}

