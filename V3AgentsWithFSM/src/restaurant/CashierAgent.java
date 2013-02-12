package restaurant;

import agent.Agent;
import java.util.*;

public class CashierAgent extends Agent {
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

	private List <Bill> billsToPay; // List of bills received from waiters
	private List<PayCustomer> customerPayments; // List of customer submitted payments
	private Map <String, Double> foodPrices; // This will be used to help create bills in billsToPay, since the waiter will not pass an actual bill as an argument to the cashier in “msgHereIsCustomerOrder()”
	private volatile double totalMoney = 0.00; // The total cash that the restaurant currently has in stock.  It will be added to when bills are processed and subtracted from when paying to buy food orders.
	
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
	}

	/*Part 2 Normative*/
	List<Bill> marketBills; // Bills that come from markets – market agent is given as a reference in the bill itself

	//Messages:
	/*Part 1 Normative*/
	public void msgHereIsCustomerOrder(CustomerAgent customer, String choice) { // The waiter will send what his/her customer orders to the cashier, and then the cashier will generate a bill from this information
		billsToPay.add(new Bill(0.00 /*Add in appropriate value here ASAP*/, choice, customer));
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

//	Scheduler:
	/*Pseudocode*/
//	if ($ cp in customerPayments) then /*Part 1 Normative*/
//	if ($ bill in billsToPay s.t. bill.Agent == cp.cBill.Agent) then
//		checkCustomerPayment(cp, bill);
//		return true;
//	if ($ b in marketBills) then /*Part 2 Normative*/
//		payMarketBill(b);
//		return true;
//	return false
	
	protected boolean pickAndExecuteAnAction() {
		for (PayCustomer cp: customerPayments) { /*Part 1 Normative*/
			for (Bill b: billsToPay) { // If a bill has been given to the cashier from the waiter, check to see if there is a matching customer payment
				if (b.agent instanceof CustomerAgent) { // Then the agent contained within the bill is a customer -- Check to see if customer name matches a customer in a payment
					// Cast the agents to the correct type for comparison
					CustomerAgent bTemp = (CustomerAgent) b.agent;
					CustomerAgent cTemp = (CustomerAgent) cp.cBill.agent;
					if (bTemp.getName().equals(cTemp.getName())) { // Then both customers are the same! 
						// May switch to comparing a different variable between customers later, or make it so that customer names are unique
						checkCustomerPayment(cp, b);
						return true;
					}
				}
			}
		}
		
		for (Bill b: marketBills) { /*Part 2 Normative*/ 
			// If there is a bill in marketBills, pay it off
			payMarketBill(b);
			return true;
		}
		
		return false;
	}

	// Actions:
	/*Part 1 Normative*/
	public void checkCustomerPayment(PayCustomer cp, Bill bill) { 
	// Check to see if cp’s bill matches a bill in the billsToPay dataBase, and then parse information 
		if (cp.payment == cp.cBill.totalCost) {
			totalMoney += cp.payment; // Increment the money earned for the restaurant
			// send a message to customer saying that correct payment amount was fulfilled
//			cp.cBill.agent.msgThankYouComeAgain(); 
			customerPayments.remove(cp);
			billsToPay.remove(bill);
			stateChanged();
		}	
		if (cp.payment < cp.cBill.totalCost) {
			totalMoney += cp.payment; // Increment the money earned for the restaurant
			// send a message to customer saying that payment was received, but that it was 
			// not enough: The amount left over will have to be repaid ASAP.	
//			cp.cBill.agent.msgNextTimePayTheDifference(cp.cBill.totalCost - cp.payment /* is amountLeftToPay*/); 
			customerPayments.remove(cp);
			billsToPay.remove(bill);
			stateChanged();
		}
	}

	/*Part 2 Normative*/
	public void payMarketBill(Bill bill) { // Pay the bill sent from a market by using the Agent reference in bill
		totalMoney -= bill.totalCost;
//		bill.agent.msgHereIsCashierPayment(bill.totalCost, bill.choice);
		marketBills.remove(bill);
		stateChanged();
	}

	//Other Methods:
	public String getName() {
		return name;
	}
	
	public String toString() {
		return "Market " + getName();
	}

}
