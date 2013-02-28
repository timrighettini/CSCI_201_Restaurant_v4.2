package restaurant.test.Mock;

import restaurant.Bill;
import restaurant.Menu;
import restaurant.CustomerAgent.AgentState;
import restaurant.interfaces.Cashier;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Waiter;

public class MockCustomer extends MockAgent implements Customer {

	public Bill bill = null; // Will be given from the waiter and passed to the cashier
	public volatile double wallet = 125.00; // Will hold how much money a customer has (will be thread safe) -- Starting value is $15.00, but can be changed in the GUI
	public volatile double amountOwed = 0.00; // How much money the customer owes the restaurant (will be thread safe)
	
	public Cashier cashier; // Will be set in the testing code

	public MockCustomer(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	public MockCustomer(String name, Cashier c) {
		super(name);
		// TODO Auto-generated constructor stub
		cashier = c;
	}
	
	/* These three methods are required for Unit testing the cashier -- the rest of the messages may be filled in later, but not for the cashier testing */
	
	@Override
	public void msgHereIsYourFood(Bill bill) {
    	this.bill = bill;
		log.add(new LoggedEvent(getName() + " got the bill"));
	}
	
	@Override
	public void msgThankYouComeAgain() {
	    // Since I am using doubles and NOT dollars/cents for this, I am currently not 
	    // implementing giving change to customers for the sake of simplicity
		log.add(new LoggedEvent(getName() + " paid in full"));
	}

	@Override
	public void msgNextTimePayTheDifference(double difference) {
    	amountOwed+=difference;		
    	log.add(new LoggedEvent(getName() + " did not pay in full"));
	}
	
	/* Not Required Messages */

	@Override
	public void setHungry() {
		// TODO Auto-generated method stub
		// Not Needed
	}

	@Override
	public void msgFollowMeToTable(Waiter waiter, Menu menu) {
		// TODO Auto-generated method stub
		// Not Needed		
	}

	@Override
	public void msgDecided() {
		// TODO Auto-generated method stub
		// Not Needed
	}

	@Override
	public void msgWhatWouldYouLike() {
		// TODO Auto-generated method stub
		// Not Needed
	}

	@Override
	public void msgDoneEating() {
		// TODO Auto-generated method stub
		// Not Needed
	}

	@Override
	public void msgPleaseReorder(Menu menu) {
		// TODO Auto-generated method stub
		// Not Needed
	}

	@Override
	public void msgSorryTablesAreOccupied(int wSize) {
		// TODO Auto-generated method stub
		// Not Needed
	}
	
	// Mock Actions from the real customer agent
	public void payBill() { // Have the Customer send the bill to the cashier
		cashier.msgHereIsCustomerPayment(subtractFromWallet(bill.totalCost), bill);
		log.add(new LoggedEvent(getName() + " paid the bill"));
	}

	private double subtractFromWallet (double num) { // Will return how much was subtracted from wallet
		wallet -= num; 
		double val = 0;
		if (wallet < 0) { val  = wallet + num; wallet = 0; return val; }
		else { return num; }
	}

}
