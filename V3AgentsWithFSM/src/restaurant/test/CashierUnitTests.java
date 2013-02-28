package restaurant.test;

import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.junit.Test;

import restaurant.*;
import restaurant.interfaces.*;
import restaurant.test.Mock.*;

public class CashierUnitTests extends TestCase {

	@Test
	public void testSingleCustomerPaysAndLeavesNormative() {
		/*
		 - This test will complete the following objective: test that the cashier appropriately handles a customer paying and leaving with enough $ to pay
		 - How this test will work
		 1.  The Test will act as a waiter and give the customer/cashier bills/bill contents of the same amount
		 	a.  Cashier will be given a bill first
		 		(1) Check the following preconditions: No bills in billsToPay List for customers
		 		(*) Send cashier the bill
		 		(2) Check the following postconditions: bill just sent == bill in billsToPay, nothing exists in customerPayments
		 	b.  Customer will be given bill second
		 		(1) Check the following preconditions: bill == null, get totalMoney and amountOwed
		 		(*) Send customer the bill
		 		(2) Check the following postconditions: bill == bill just sent, check that totalMoney and AmountOwed have not changed
		 2.  Run the cashier scheduler BEFORE the customer pays -- Size of billsToPay should == 1
		 3.  Run payBill() in the mockCustomer -- simulate the scheduler and the action there
		 	(1) Check the log for a message about the customer sending the bill the to cashier
		 	(2) Check to see if the cashier has a customerPayment to the bill just sent from the customer
		 4.  Run the cashier scheduler again
		 	(1) Check that both lists in the cashier are empty and/or the bill sent does not exist in the cashier
		 	(2) Check the customer log for a message about the cashier receiving payment from the customer, depending to how much the customer paid
		 		(a) getName() + " paid in full" or getName() + " did not pay in full"
		*/
		
		// Set up the initial agents
		Cashier cashier = new CashierAgent("cashier");
		
		// Insert Mock Customer Here
		Customer c1 = new MockCustomer("c1", cashier);
		
		// Begin the test -- 
		
		fail("Not yet implemented");
	}
	
	@Test
	public void testSingleCustomerPaysAndLeavesNonNormative() {
		// Do the same test as "testSingleCustomerPaysAndLeavesNormative" except that the customer does NOT have enough to pay, 
		// so the final message in the MockCustomer log will be intepreted differently
		
		
		fail("Not yet implemented");
	}
	
	@Test
	public void testSinglePayMarketBill() {
		/*
		 - This test will complete the following objective:  test that the cashier correctly pays for a bill sent from a market
		 - How this test will work:
		 1. The test will send the cashier a bill from a "market" -- the ID will be the name of the MockMarket who processes the payment -- the cashier will also get a reference to the MockMarket to pay to
		 	a.  Check PreConditions:  MarketBills is empty
		 	b.  Send the Bill to the cashier from the test code
		 	c.  Check to see if the size of the list == 1, and that the bill exists within that list
		 2. Run the cashier scheduler -- cashier should pay the market referenced in the bill
		 	a. Check the following Post-Conditions:
		 		(1) Cashier's totalMoney was changed relative to the price of the bill
		 		(2) Cashiers marketBills list == 0 and the Bill previously sent to the cashier is not in the list
		 		(3) MockMarket has a payment that matches the totalCost of the bill
		 */
		
		fail("Not yet implemented");
	}
	
	@Test
	public void testMultipleCashierScenarios() {
		/*
		 - This test will complete the following objective:  test that the cashier can multitask correctly by making sure that it can handle multiple tasks from the previous tests at once
		 - Refer to comments on the previous tests for insight as to what is going on -- this is only a combination of older tests 
		 */
		fail("Not yet implemented");
	}

}
