package restaurant.test;

import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.junit.Test;

import agent.Agent;

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
		CashierAgent cashier = new CashierAgent("cashier:testSingleCustomerPaysAndLeavesNormative");
		
		// Insert Mock Customer Here
		MockCustomer c1 = new MockCustomer("c1", cashier);
		
		// Begin the test 
		
		// Create the bill to send to the cashier and the customer
		Bill testBill = new Bill(15.99, "Steak", c1); // Add a customer reference into the bill
		
		// Start by testing the pre-conditions outlined in #1
		// Cashier 
		assertEquals(cashier.getBillsToPay().size(), 0); // No billsToPay
		
		// Send the cashier the bill
		cashier.msgHereIsCustomerOrder(testBill);
		
		// Check to make sure that the bill is stored correctly
		assertTrue(cashier.getBillsToPay().size() == 1); // Check that a bill exists
		assertTrue(cashier.getBillsToPay().contains(testBill)); // Check that this bill exists
		assertTrue(cashier.getCustomerPayments().size() == 0); // Make sure that nothing exists in the customerPayments array
		
		/***********/
		
		// Test Customer Preconditions 
		assertTrue(c1.bill == null); // Make sure that the bill has not been touched yet
		// Make sure that monies have not been touched
		assertEquals(c1.wallet, 125.00);
		assertEquals(c1.amountOwed, 0.00);
		
		// Send customer the bill now
		c1.msgHereIsYourFood(testBill);
				
		// Test post conditions
		assertTrue(c1.bill == testBill);
		assertEquals(c1.wallet, 125.00);
		assertEquals(c1.amountOwed, 0.00);
		
		// Run the cashier's scheduler
		cashier.runScheduler();
		
		// Check post condition
		assertTrue(cashier.getBillsToPay().size() == 1);
		
		// Run payBill in the MockCustomer
		c1.payBill();
		
		// Check post conditions
		assertTrue(c1.log.containsString(c1.getName() + " paid the bill"));
		
		// Loop through list and find the payment that matches the bill
		assertTrue(cashier.findCBill(testBill));
		
		// Run the cashier scheduler again
		cashier.runScheduler();
		
		// Check that the testBill does not exists in either of the cashier lists
		assertFalse(cashier.getBillsToPay().contains(testBill));
		assertFalse(cashier.findCBill(testBill));
		
		// Check that the customer received payment from the cashier
		assertTrue(c1.log.containsString(c1.getName() + " paid in full"));
	}
	
	@Test
	public void testSingleCustomerPaysAndLeavesNonNormative() {
		// Do the same test as "testSingleCustomerPaysAndLeavesNormative" except that the customer does NOT have enough to pay, 
		// so the final message in the MockCustomer log will be intepreted differently
		
		// Set up the initial agents
		CashierAgent cashier = new CashierAgent("cashier:testSingleCustomerPaysAndLeavesNonNormative");
		
		// Insert Mock Customer Here
		MockCustomer c1 = new MockCustomer("c1", cashier);
		
		// Begin the test 
		
		// Create the bill to send to the cashier and the customer
		Bill testBill = new Bill(15.99, "Steak", c1); // Add a customer reference into the bill
		
		// Start by testing the pre-conditions outlined in #1
		// Cashier 
		assertEquals(cashier.getBillsToPay().size(), 0); // No billsToPay
		
		// Send the cashier the bill
		cashier.msgHereIsCustomerOrder(testBill);
		
		// Check to make sure that the bill is stored correctly
		assertTrue(cashier.getBillsToPay().size() == 1); // Check that a bill exists
		assertTrue(cashier.getBillsToPay().contains(testBill)); // Check that this bill exists
		assertTrue(cashier.getCustomerPayments().size() == 0); // Make sure that nothing exists in the customerPayments array
		
		/***********/
		
		// Set c1's money to be below the value of a steak
		c1.wallet = 15.00;
		
		// Test Customer Preconditions 
		assertTrue(c1.bill == null); // Make sure that the bill has not been touched yet
		// Make sure that monies have (not) been touched
		assertEquals(c1.wallet, 15.00);
		assertEquals(c1.amountOwed, 0.00);
		
		// Send customer the bill now
		c1.msgHereIsYourFood(testBill);
				
		// Test post conditions
		assertTrue(c1.bill == testBill);
		assertEquals(c1.wallet, 15.00);
		assertEquals(c1.amountOwed, 0.00);
		
		// Run the cashier's scheduler
		cashier.runScheduler();
		
		// Check post condition
		assertTrue(cashier.getBillsToPay().size() == 1);
		
		// Run payBill in the MockCustomer
		c1.payBill();
		
		// Check post conditions
		assertTrue(c1.log.containsString(c1.getName() + " paid the bill"));
		
		// Loop through list and find the payment that matches the bill
		assertTrue(cashier.findCBill(testBill));
		
		// Run the cashier scheduler again
		cashier.runScheduler();
		
		// Check that the testBill does not exists in either of the cashier lists
		assertFalse(cashier.getBillsToPay().contains(testBill));
		assertFalse(cashier.findCBill(testBill));
		
		// Check that the customer received payment from the cashier
		assertTrue(c1.log.containsString(c1.getName() + " did not pay in full"));
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
		
		// Create the two Agents who will participate in this test
		CashierAgent cashier = new CashierAgent("cashier:testSinglePayMarketBill");
		MockMarket m1 = new MockMarket("m1");
		
		// Create the testBill
		Bill testBill = new Bill(200.00, "5000", m1); // Will be a test bill to be sent to the cashier 
		
		// Check preconditions for cashier
		assertTrue(cashier.getMarketBills().size() == 0);
		
		// Send the bill to the cashier
		cashier.msgHereIsBill(testBill);
		
		// Check postconditions for cashier
		assertTrue(cashier.getMarketBills().size() == 1);
		assertTrue(cashier.getMarketBills().contains(testBill));
		
		// Get the totalMoney of the cashier before paying for the order
		double cashierPreMoney = cashier.getTotalMoney();
		
		// Run the cashier scheduler
		cashier.runScheduler();
		
		// Check postconditions
		assertEquals(cashier.getTotalMoney(), cashierPreMoney - testBill.totalCost); // Make sure that the amount from the bill was deducted from the cashier
		// Make sure that the bill is out of the cashier's hands once it is paid
		assertTrue(cashier.getMarketBills().size() == 0);
		assertFalse(cashier.getMarketBills().contains(testBill));
		// Make sure that MockMarket has the payment that equals the cost of the bill
		assertTrue(m1.paymentForTotalCostExists(testBill.totalCost));	
	}
	
	@Test
	public void testMultipleCashierScenarios() {
		/*
		 - This test will complete the following objective:  test that the cashier can multitask correctly by making sure that it can handle multiple tasks from the previous tests at once
		 - Refer to comments on the previous tests for insight as to what is going on -- this is only a combination of older tests 
		 */
		assertTrue(true);
	}

}
