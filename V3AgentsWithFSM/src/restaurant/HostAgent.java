package restaurant;

import restaurant.interfaces.*;

import agent.Agent;
import java.util.*;


/** Host agent for restaurant.
 *  Keeps a list of all the waiters and tables.
 *  Assigns new customers to waiters for seating and 
 *  keeps a list of waiting customers.
 *  Interacts with customers and waiters.
 */
public class HostAgent extends Agent implements Host {

    /** Private class storing all the information for each table,
     * including table number and state. */
    private class Table {
		public int tableNum;
		public boolean occupied;
	
		/** Constructor for table class.
		 * @param num identification number
		 */
		public Table(int num){
		    tableNum = num;
		    occupied = false;
		}	
    }

    /** Private class to hold waiter information and state */
    private class MyWaiter {
	public WaiterAgent wtr;

	/** Constructor for MyWaiter class
	 * @param waiter
	 */
	public MyWaiter(Waiter waiter){
	    wtr = (WaiterAgent) waiter;
	}
    }

    //List of all the customers that need a table
    private List<CustomerAgent> waitList =
		Collections.synchronizedList(new ArrayList<CustomerAgent>());

    //List of all waiter that exist.
    private List<MyWaiter> waiters =
		Collections.synchronizedList(new ArrayList<MyWaiter>());
    private int nextWaiter = 0; //The next waiter that needs a customer
    
    //List of all the tables
    int nTables;
    private Table tables[];

    //Name of the host
    private String name;
    
    /*Part 3 (Non-)Normative*/
    List<WaiterAgent> waitersWhoWantToBreak = Collections.synchronizedList(new ArrayList<WaiterAgent>()); // The dataType may be changed to something else for simplicity or to reduce redundancy in the future

    /*Part 4 Non-Normative*/
    enum customerState {pending, yes, no};
    private class PotentialCustomer {
    	CustomerAgent cmr;
    	customerState willWait = customerState.pending; // Initialized to this state
    	boolean messaged; // This will determine if this customer has been messaged by the host or not
    	
    	public PotentialCustomer(Customer c) {
    		this.cmr = (CustomerAgent) c;
    		this.messaged = false;
    	}
    }
    List<PotentialCustomer> tablesFullCustomers = Collections.synchronizedList(new ArrayList<PotentialCustomer>());


    /** Constructor for HostAgent class 
     * @param name name of the host */
    public HostAgent(String name, int ntables) {
	super();
	this.nTables = ntables;
	tables = new Table[nTables];

	for(int i=0; i < nTables; i++){
	    tables[i] = new Table(i);
	}
	this.name = name;
    }

    // *** MESSAGES ***

    /** Customer sends this message to be added to the wait list 
     * @param customer customer that wants to be added */
    public void msgIWantToEat(Customer customer){
    	boolean tableFree = false; // If there are NO free tables, then this variable will NOT be changed in the following loop
    	int tablesOccupied = 0; // Will keep track of how many tables are occupied, and how many seats are left
    	synchronized(tables) {
	    	for (int i = 0; i < tables.length; i++) {
	    		if (tables[i].occupied == false) {
	    			tableFree = true;
	    			tablesOccupied++;
	    			break;
	    		}
	    	}
    	}
    	if (tableFree == true && waitList.size() <= 4 - tablesOccupied) { // Add customer to waitlist, as seen regularly
    		waitList.add((CustomerAgent) customer);
    	}
    	else { // Add this customer to a potential customer list, and ask if she/he wants to wait
    		((CustomerAgent) customer).setRestaurantFull(true); // Will be used to begin restaurant full non-normative scenario
    		waitList.add((CustomerAgent) customer);
    		
    	}
		stateChanged();
    }

    /** Waiter sends this message after the customer has left the table 
     * @param tableNum table identification number */
    public void msgTableIsFree(int tableNum){
	tables[tableNum].occupied = false;
	stateChanged();
    }
    
    /*Part 3 (Non-)Normative*/
    public void msgMayITakeABreak(Waiter waiter) {
    	print("I got your message to break: " + waiter.getName());
    	waitersWhoWantToBreak.add((WaiterAgent) waiter);
    	stateChanged();
    }

    /*Part 4 Non-Normative*/
    public void  msgThankYouIllWait(Customer c) {
    	synchronized(tablesFullCustomers) {
	    	for (PotentialCustomer t: tablesFullCustomers) {
	    		if (t.cmr.getName().equals(c.getName())) {
	    			t.willWait = customerState.yes;
	        		stateChanged();
	    		}
	    	}
    	}
    }

    public void msgSorryIHaveToLeave(Customer c) {
    	synchronized(tablesFullCustomers) {
	    	for (PotentialCustomer t: tablesFullCustomers) {
	    		if (t.cmr.getName().equals(c.getName())) {
	    			t.willWait = customerState.no;
	        		stateChanged();
	    		}
	    	}
    	}
    }


    /** Scheduler.  Determine what action is called for, and do it. */
    protected boolean pickAndExecuteAnAction() {
	
	if(!waitList.isEmpty() && !waiters.isEmpty()){
	    synchronized(waiters){
			//Finds the next waiter that is working
			while(waiters.get(nextWaiter).wtr.getOnBreak()){
			    nextWaiter = (nextWaiter+1)%waiters.size();
			}
	    }
	    print("picking waiter number:"+nextWaiter);
	    //Then runs through the tables and finds the first unoccupied 
	    //table and tells the waiter to sit the first customer at that table
	    synchronized(tables) {
		    for(int i=0; i < nTables; i++){
	
				if(!tables[i].occupied && waitList.get(0).getRestaurantFull() == false) { // Will prevent a customer from being selected that is in the middle of deciding to stay or not
				    synchronized(waitList){
					tellWaiterToSitCustomerAtTable(waiters.get(nextWaiter),
					    waitList.get(0), i);
				    }
				    return true;
				}
		    }      
	    }
	}
	
    /*Part 4 Non-Normative*/
    // Check to see if any non-waitlist customers are in need of service
	
	// Use temp variable to make sure that the action is not called in the synchronized loop
	Customer tempCustomer = null;
	synchronized(waitList) {
	    for (Customer t: waitList) {    	
	    	tempCustomer = t;
	    	break;
	    }
	}
	if (tempCustomer != null) {
    	if (((CustomerAgent) tempCustomer).getRestaurantFull() == true) {
	    	if (((CustomerAgent) tempCustomer).getMessaged() == false) {
	    		doSendCustomerWaitingMessage(tempCustomer); // Send cmr a message to see if he/she wants to wait
	    		((CustomerAgent) tempCustomer).setMessaged(true);
	    		return true;
	    	}
	    	else {
	    		doAddCustomerToWaitList(tempCustomer); // do (not) add the customer to the waitlist after the response has been received
	    		return true;
	    	}
		}  		
	}
    
    // Check to see if any waiters would like to go on break
	
	// Make temp variable here, as with the last loop
	Waiter tempWaiter = null;
	synchronized(waitersWhoWantToBreak) {
	    for (Waiter w: waitersWhoWantToBreak) {
	    	tempWaiter = w;
	    	break;
	    }
	}
    if (tempWaiter != null) {
		doMessageWaiterBreak(tempWaiter, !checkRestaurantBusy());	
    	return true;
    }

	//we have tried all our rules (in this case only one) and found
	//nothing to do. So return false to main loop of abstract agent
	//and wait.
	return false;
    }
    
    // *** ACTIONS ***
    
    /** Assigns a customer to a specified waiter and 
     * tells that waiter which table to sit them at.
     * @param waiter
     * @param customer
     * @param tableNum */
    private void tellWaiterToSitCustomerAtTable(MyWaiter waiter, Customer customer, int tableNum){
	print("Telling " + waiter.wtr + " to sit " + customer +" at table "+(tableNum+1));
	waiter.wtr.msgSitCustomerAtTable(customer, tableNum);
	tables[tableNum].occupied = true;
	waitList.remove(customer);
	nextWaiter = (nextWaiter+1)%waiters.size();
	stateChanged();
    }
	
    /*Part 3 (Non-)Normative*/
    private void doMessageWaiterBreak(Waiter w, boolean b) {
	    if (b == true) { 
	    	print(w.getName() + ", you may take a break when all of your customers leave.");
	    	w.msgYesAfterYourCustomersFinish();
	    }
	    else  {
	    	print(w.getName() + ", you may not take a break.");
	    	w.msgNoItIsTooBusy();
	    }
	    	waitersWhoWantToBreak.remove(w);
	    	stateChanged();
    }

    /*Part 4 Non-Normative*/
    private void doSendCustomerWaitingMessage(Customer c) {
    	print(c.getName() + ", sorry, but all of the tables are full.  Would you like to wait?");
    	c.msgSorryTablesAreOccupied(waitList.size());
    	stateChanged();
    }

    private void doAddCustomerToWaitList(Customer c) {
    	// Set the appropriate values to false
    	((CustomerAgent) c).setRestaurantFull(false);
    	((CustomerAgent) c).setMessaged(false);    	
    	if (((CustomerAgent) c).getWillingToWait() == false) { // Remove the customer from the wait list if he/she wants to leave
    		waitList.remove(c);
    	}    	
    	stateChanged();
    }
    
    private boolean checkRestaurantBusy() { // Will check to see if the restaurant is busy
    	// Check to see if all waiters are on break
    	int numWaitersOnBreak = 0;
    	
    	synchronized(waiters) {
	    	for (int i = 0; i < waiters.size(); i++) {
	    		if (waiters.get(i).wtr.isOnBreak() == true) {
	    			numWaitersOnBreak++;
	    		}
	    	}
    	}
    	
    	print("Waiters on Break: " + numWaitersOnBreak);
    	
    	if ((waitList.size() == 0) && (numWaitersOnBreak < waiters.size() - 1)) { 
    		// If there are no customers to service, or if all of the waiters - 1 are NOT on break, return false 
    		return false;
    	}
    	else {
    		return true;
    	}
    }


    // *** EXTRA ***

    /** Returns the name of the host 
     * @return name of host */
    public String getName(){
        return name;
    }    

    /** Hack to enable the host to know of all possible waiters 
     * @param waiter new waiter to be added to list
     */
    public void setWaiter(Waiter waiter){
	waiters.add(new MyWaiter(waiter));
	stateChanged();
    }
    
    //Gautam Nayak - Gui calls this when table is created in animation
    public void addTable() {
	nTables++;
	Table[] tempTables = new Table[nTables];
	synchronized(tables) {
		for(int i=0; i < nTables - 1; i++){
		    tempTables[i] = tables[i];
		}  		  			
	}
	tempTables[nTables - 1] = new Table(nTables - 1);
	tables = tempTables;
    }
}
