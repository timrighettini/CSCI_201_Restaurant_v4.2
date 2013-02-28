package restaurant;

import restaurant.interfaces.*;
import agent.Agent;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

public class CashierAgent extends Agent implements Cashier{
	//Name: CashierAgent
	//Description:  Customer pays this agent for his/her food before notifying the waiter & leaving

	//Data: 
	/*Part 1 Normative*/
	private class PayCustomer { // A customer that has submitted a payment, this class/object will be checked to the billsToPay list when processing an order
		double payment; // The submitted payment of a customerAgent
		Bill cBill; // The accompanying bill of the customerAgent
		
		public PayCustomer(double money, Bill bill) {
			this.payment = money;
			this.cBill = bill;
		}
	}

	private List <Bill> billsToPay = Collections.synchronizedList(new ArrayList<Bill>()); // List of bills received from waiters
	private List<PayCustomer> customerPayments = Collections.synchronizedList(new ArrayList<PayCustomer>()); // List of customer submitted payments
	private Map <String, Double> foodPrices = new HashMap<String, Double>(); // This will be used to help create bills in billsToPay, since the waiter will not pass an actual bill as an argument to the cashier in “msgHereIsCustomerOrder()”
	private volatile double totalMoney = 0.00; // The total cash that the restaurant currently has in stock.  It will be added to when bills are processed and subtracted from when paying to buy food orders.
	DecimalFormat df = new DecimalFormat("######.##");
	
	private String name; // Holds the name of the agent
	
	// Constructor	
	public CashierAgent(String name) {
		super();
		
		this.name = name;
		
		// Initialize the foodPrices Map -- Sets up the prices for food
		foodPrices.put("Steak", 15.99);
		foodPrices.put("Chicken", 10.99);
		foodPrices.put("Pizza", 5.99);
		foodPrices.put("Salad", 8.99);
		
		df.setRoundingMode(RoundingMode.UP);
	}

	/*Part 2 Normative*/
	List<Bill> marketBills = Collections.synchronizedList(new ArrayList<Bill>()); // Bills that come from markets – market agent is given as a reference in the bill itself

	//Messages:
	/*Part 1 Normative*/
	public void msgHereIsCustomerOrder(Bill bill) { // The waiter will send what his/her customer orders to the cashier, and then the cashier will generate a bill from this information
		billsToPay.add(bill);
		stateChanged();
	}

	public void msgHereIsCustomerPayment(double money, Bill bill) { // The customer will send his/her order though this message
		customerPayments.add(new PayCustomer(money, bill));
		stateChanged();
	}

	/*Part 2 Normative*/
	public void msgHereIsBill(Bill bill) { // Get a bill from a market
		marketBills.add(bill);
		stateChanged();
	}

	protected boolean pickAndExecuteAnAction() {
		// Use two temporary variables to determine if the loop navigation was successful or not
		Bill tempBill = null;
		PayCustomer tempPC = null;
		// If both of these values are not null, then execute the action AFTER the loop instead of during the loop
		
		synchronized (customerPayments) {
			for (PayCustomer cp: customerPayments) { /*Part 1 Normative*/
				if (tempBill != null && tempPC != null) {break;} // Variables have been found, break				
				synchronized(billsToPay) {
					for (Bill b: billsToPay) { // If a bill has been given to the cashier from the waiter, check to see if there is a matching customer payment
						if (b.agent instanceof Customer) { // Then the agent contained within the bill is a customer -- Check to see if customer name matches a customer in a payment
							// Cast the agents to the correct type for comparison
							Customer bTemp = (Customer) b.agent;
							Customer cTemp = (Customer) cp.cBill.agent;
							if (bTemp.getName().equals(cTemp.getName())) { // Then both customers are the same! 
								tempBill = b;
								tempPC = cp;
								break;
							}
						}
					}
				}
			}
		}		
		if (tempBill != null && tempPC != null) {
			// May switch to comparing a different variable between customers later, or make it so that customer names are unique
			checkCustomerPayment(tempPC, tempBill);
			return true;
		}
		
		// Use a temporary variable as seen in the above loop, but now with a new bill variable
		Bill tempMarketBill = null;
		
		synchronized(marketBills) {
			for (Bill b: marketBills) { /*Part 2 Normative*/ 
				tempMarketBill = b;
				break;
			}
		}		
		if (tempMarketBill != null) {
			// If there is a bill in marketBills, pay it off
			payMarketBill(tempMarketBill);
			return true;
		}
		
		return false;
	}

	// Actions:
	/*Part 1 Normative*/
	private void checkCustomerPayment(PayCustomer cp, Bill bill) { 
	// Check to see if cp’s bill matches a bill in the billsToPay dataBase, and then parse information 
		if (cp.payment == cp.cBill.totalCost) {
			print(((Customer) cp.cBill.agent).getName() + "'s payment is correct: " + df.format(cp.payment));
			totalMoney += cp.payment; // Increment the money earned for the restaurant
			// send a message to customer saying that correct payment amount was fulfilled
			((Customer) cp.cBill.agent).msgThankYouComeAgain(); 
			customerPayments.remove(cp);
			billsToPay.remove(bill);
			stateChanged();
			print("Total Money = " + df.format(totalMoney));
		}	
		if (cp.payment < cp.cBill.totalCost) {
			totalMoney += cp.payment; // Increment the money earned for the restaurant
			// send a message to customer saying that payment was received, but that it was 
			// not enough: The amount left over will have to be repaid ASAP.	
			print(((Customer) cp.cBill.agent).getName() + "'s payment is short: " + df.format(cp.payment) +  ".  Please repay ASAP!");
			((Customer) cp.cBill.agent).msgNextTimePayTheDifference(cp.cBill.totalCost - cp.payment /* is amountLeftToPay*/); 
			customerPayments.remove(cp);
			billsToPay.remove(bill);
			stateChanged();
			print("Total Money = " + df.format(totalMoney));
		}
	}

	/*Part 2 Normative*/
	private void payMarketBill(Bill bill) { // Pay the bill sent from a market by using the Agent reference in bill
		if (bill.agent instanceof Market) { // Just to be sure...but ONLY marketAgent bills should be sent to this list
			totalMoney -= bill.totalCost;
			print("Bill paid to market: " + ((Market) bill.agent).getName() + ".  Cost: " + df.format(bill.totalCost) + ".  Totalmoney = " + df.format(totalMoney));
			((Market) bill.agent).msgHereIsCashierPayment(bill.totalCost, bill.choice);
			marketBills.remove(bill);
			stateChanged();	
		}
	}

	//Other Methods:
	public String getName() {
		return name;
	}
	
	public String toString() {
		return "Market " + getName();
	}
	
	// Methods for JUnit testing
	
	public List<Bill> getBillsToPay() {
		return billsToPay;
	}
	
	public List<PayCustomer> getCustomerPayments() {
		return customerPayments;
	}
	
	public double getTotalMoney() {
		return totalMoney;
	}
	
	public List<Bill> getMarketBills() {
		return marketBills;
	}
	
	public void runScheduler() {
		pickAndExecuteAnAction();
	}
	
	public boolean findCBill(Bill b) {
		for (PayCustomer p: customerPayments) {
			if (p.cBill == b) {
				return true;
			}
		}
		return false;
	}

}
