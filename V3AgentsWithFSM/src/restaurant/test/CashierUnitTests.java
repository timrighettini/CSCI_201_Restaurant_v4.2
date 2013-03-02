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
	public void testTwoCustomersPayAndLeaveNormative() { 
		// This test is the same as the single one, but now there are two mockcustomers to test with instead of one.
		
		// Set up the initial agents
		CashierAgent cashier = new CashierAgent("cashier:testTwoCustomersPayAndLeaveNormative");
		
		// Insert Mock Customer Here
		MockCustomer c1 = new MockCustomer("c1", cashier);
		MockCustomer c2 = new MockCustomer("c2", cashier);
		
		// Begin the test 
		
		// Create the bill to send to the cashier and the customer
		Bill testBill_1 = new Bill(15.99, "Steak", c1); // Add a customer reference into the bill
		Bill testBill_2 = new Bill(5.99, "Salad", c2); // Add a customer reference into the bill
		
		// Start by testing the pre-conditions outlined in #1
		// Cashier 
		assertEquals(cashier.getBillsToPay().size(), 0); // No billsToPay
		
		// Send the cashier the bill
		cashier.msgHereIsCustomerOrder(testBill_1);
		
		// Check to make sure that the bill is stored correctly
		assertTrue(cashier.getBillsToPay().size() == 1); // Check that a bill exists
		assertTrue(cashier.getBillsToPay().contains(testBill_1)); // Check that this bill exists
		assertTrue(cashier.getCustomerPayments().size() == 0); // Make sure that nothing exists in the customerPayments array
		
		// Send the cashier the bill
		cashier.msgHereIsCustomerOrder(testBill_2);
		
		// Check to make sure that the bill is stored correctly
		assertTrue(cashier.getBillsToPay().size() == 2); // Check that a bill exists
		assertTrue(cashier.getBillsToPay().contains(testBill_2)); // Check that this bill exists
		assertTrue(cashier.getCustomerPayments().size() == 0); // Make sure that nothing exists in the customerPayments array
		
		/***********/
		
		// Test Customer Preconditions 
		assertTrue(c1.bill == null); // Make sure that the bill has not been touched yet
		// Make sure that monies have not been touched
		assertEquals(c1.wallet, 125.00);
		assertEquals(c1.amountOwed, 0.00);
		
		// Send customer the bill now
		c1.msgHereIsYourFood(testBill_1);
				
		// Test post conditions
		assertTrue(c1.bill == testBill_1);
		assertEquals(c1.wallet, 125.00);
		assertEquals(c1.amountOwed, 0.00);
		
		// Check post condition
		assertTrue(cashier.getBillsToPay().size() == 2);
		
		// Test Customer Preconditions 
		assertTrue(c2.bill == null); // Make sure that the bill has not been touched yet
		// Make sure that monies have not been touched
		assertEquals(c2.wallet, 125.00);
		assertEquals(c2.amountOwed, 0.00);
		
		// Send customer the bill now
		c2.msgHereIsYourFood(testBill_2);
				
		// Test post conditions
		assertTrue(c2.bill == testBill_2);
		assertEquals(c2.wallet, 125.00);
		assertEquals(c2.amountOwed, 0.00);
		
		// Run the cashier's scheduler
		cashier.runScheduler();
		
		// Check post condition
		assertTrue(cashier.getBillsToPay().size() == 2);
		
		// Run payBill in the MockCustomer
		c1.payBill();
		
		// Check post conditions
		assertTrue(c1.log.containsString(c1.getName() + " paid the bill"));
		
		// Loop through list and find the payment that matches the bill
		assertTrue(cashier.findCBill(testBill_1));
		
		// Run the cashier scheduler again
		cashier.runScheduler();
		
		// Check that the testBill does not exists in either of the cashier lists
		assertFalse(cashier.getBillsToPay().contains(testBill_1));
		assertFalse(cashier.findCBill(testBill_1));
		assertTrue(cashier.getBillsToPay().size() == 1); // There is one bill left in the array still
		
		// Run payBill in the MockCustomer
		c2.payBill();
		
		// Check post conditions
		assertTrue(c2.log.containsString(c2.getName() + " paid the bill"));
		
		// Loop through list and find the payment that matches the bill
		assertTrue(cashier.findCBill(testBill_2));
		
		// Run the cashier scheduler again
		cashier.runScheduler();
		
		// Check that the testBill does not exists in either of the cashier lists
		assertFalse(cashier.getBillsToPay().contains(testBill_2));
		assertFalse(cashier.findCBill(testBill_2));
		assertTrue(cashier.getBillsToPay().size() == 0); // There should be no bills left
		
		// Check that the customer received payment from the cashier
		assertTrue(c1.log.containsString(c1.getName() + " paid in full"));
		// Check that the customer received payment from the cashier
		assertTrue(c2.log.containsString(c2.getName() + " paid in full"));	
	}
	
	@Test
	public void testFourCustomersPayAndLeaveNormative() { 
		// This test is the same as the single one, but now there are four mockcustomers to test with instead of one.

		// Set up the initial agents
		CashierAgent cashier = new CashierAgent("cashier:testFourCustomersPayAndLeaveNormative");
		
		// Insert Mock Customer Here
		MockCustomer c1 = new MockCustomer("c1", cashier);
		MockCustomer c2 = new MockCustomer("c2", cashier);
		MockCustomer c3 = new MockCustomer("c3", cashier);
		MockCustomer c4 = new MockCustomer("c4", cashier);
		
		// Begin the test 
		
		// Create the bill to send to the cashier and the customer
		Bill testBill_1 = new Bill(15.99, "Steak", c1); // Add a customer reference into the bill
		Bill testBill_2 = new Bill(5.99, "Salad", c2); // Add a customer reference into the bill
		Bill testBill_3 = new Bill(8.99, "Pizza", c3); // Add a customer reference into the bill
		Bill testBill_4 = new Bill(10.99, "Chicken", c4); // Add a customer reference into the bill
		
		// Start by testing the pre-conditions outlined in #1
		// Cashier 
		assertEquals(cashier.getBillsToPay().size(), 0); // No billsToPay
		
		// Send the cashier the bill
		cashier.msgHereIsCustomerOrder(testBill_1);
		
		// Check to make sure that the bill is stored correctly
		assertTrue(cashier.getBillsToPay().size() == 1); // Check that a bill exists
		assertTrue(cashier.getBillsToPay().contains(testBill_1)); // Check that this bill exists
		assertTrue(cashier.getCustomerPayments().size() == 0); // Make sure that nothing exists in the customerPayments array
		
		// Send the cashier the bill
		cashier.msgHereIsCustomerOrder(testBill_2);
		
		// Check to make sure that the bill is stored correctly
		assertTrue(cashier.getBillsToPay().size() == 2); // Check that a bill exists
		assertTrue(cashier.getBillsToPay().contains(testBill_2)); // Check that this bill exists
		assertTrue(cashier.getCustomerPayments().size() == 0); // Make sure that nothing exists in the customerPayments array
		
		// Send the cashier the bill
		cashier.msgHereIsCustomerOrder(testBill_3);
		
		// Check to make sure that the bill is stored correctly
		assertTrue(cashier.getBillsToPay().size() == 3); // Check that a bill exists
		assertTrue(cashier.getBillsToPay().contains(testBill_3)); // Check that this bill exists
		assertTrue(cashier.getCustomerPayments().size() == 0); // Make sure that nothing exists in the customerPayments array
		
		// Send the cashier the bill
		cashier.msgHereIsCustomerOrder(testBill_4);
		
		// Check to make sure that the bill is stored correctly
		assertTrue(cashier.getBillsToPay().size() == 4); // Check that a bill exists
		assertTrue(cashier.getBillsToPay().contains(testBill_4)); // Check that this bill exists
		assertTrue(cashier.getCustomerPayments().size() == 0); // Make sure that nothing exists in the customerPayments array
		
		/***********/
		
		// Test Customer Preconditions 
		assertTrue(c1.bill == null); // Make sure that the bill has not been touched yet
		// Make sure that monies have not been touched
		assertEquals(c1.wallet, 125.00);
		assertEquals(c1.amountOwed, 0.00);
		
		// Send customer the bill now
		c1.msgHereIsYourFood(testBill_1);
				
		// Test post conditions
		assertTrue(c1.bill == testBill_1);
		assertEquals(c1.wallet, 125.00);
		assertEquals(c1.amountOwed, 0.00);
		
		// Check post condition
		assertTrue(cashier.getBillsToPay().size() == 4);
		
		// Test Customer Preconditions 
		assertTrue(c2.bill == null); // Make sure that the bill has not been touched yet
		// Make sure that monies have not been touched
		assertEquals(c2.wallet, 125.00);
		assertEquals(c2.amountOwed, 0.00);
		
		// Send customer the bill now
		c2.msgHereIsYourFood(testBill_2);
				
		// Test post conditions
		assertTrue(c2.bill == testBill_2);
		assertEquals(c2.wallet, 125.00);
		assertEquals(c2.amountOwed, 0.00);
		
		// Test Customer Preconditions 
		assertTrue(c3.bill == null); // Make sure that the bill has not been touched yet
		// Make sure that monies have not been touched
		assertEquals(c3.wallet, 125.00);
		assertEquals(c3.amountOwed, 0.00);
		
		// Send customer the bill now
		c3.msgHereIsYourFood(testBill_3);
				
		// Test post conditions
		assertTrue(c3.bill == testBill_3);
		assertEquals(c3.wallet, 125.00);
		assertEquals(c3.amountOwed, 0.00);
		
		// Check post condition
		assertTrue(cashier.getBillsToPay().size() == 4);
		
		// Test Customer Preconditions 
		assertTrue(c4.bill == null); // Make sure that the bill has not been touched yet
		// Make sure that monies have not been touched
		assertEquals(c4.wallet, 125.00);
		assertEquals(c4.amountOwed, 0.00);
		
		// Send customer the bill now
		c4.msgHereIsYourFood(testBill_4);
				
		// Test post conditions
		assertTrue(c4.bill == testBill_4);
		assertEquals(c4.wallet, 125.00);
		assertEquals(c4.amountOwed, 0.00);
		
		// Run the cashier's scheduler
		cashier.runScheduler();
		
		// Check post condition
		assertTrue(cashier.getBillsToPay().size() == 4);
		
		// Run payBill in the MockCustomer
		c1.payBill();
		
		// Check post conditions
		assertTrue(c1.log.containsString(c1.getName() + " paid the bill"));
		
		// Loop through list and find the payment that matches the bill
		assertTrue(cashier.findCBill(testBill_1));
		
		// Run the cashier scheduler again
		cashier.runScheduler();
		
		// Check that the testBill does not exists in either of the cashier lists
		assertFalse(cashier.getBillsToPay().contains(testBill_1));
		assertFalse(cashier.findCBill(testBill_1));
		assertTrue(cashier.getBillsToPay().size() == 3); // There is one bill left in the array still
		
		// Run payBill in the MockCustomer
		c2.payBill();
		
		// Check post conditions
		assertTrue(c2.log.containsString(c2.getName() + " paid the bill"));
		
		// Loop through list and find the payment that matches the bill
		assertTrue(cashier.findCBill(testBill_2));
		
		// Run the cashier scheduler again
		cashier.runScheduler();
		
		// Check that the testBill does not exists in either of the cashier lists
		assertFalse(cashier.getBillsToPay().contains(testBill_2));
		assertFalse(cashier.findCBill(testBill_2));
		assertTrue(cashier.getBillsToPay().size() == 2); // There is one bill left in the array still
		
		// Run payBill in the MockCustomer
		c3.payBill();
		
		// Check post conditions
		assertTrue(c3.log.containsString(c3.getName() + " paid the bill"));
		
		// Loop through list and find the payment that matches the bill
		assertTrue(cashier.findCBill(testBill_3));
		
		// Run the cashier scheduler again
		cashier.runScheduler();
		
		// Check that the testBill does not exists in either of the cashier lists
		assertFalse(cashier.getBillsToPay().contains(testBill_3));
		assertFalse(cashier.findCBill(testBill_3));
		assertTrue(cashier.getBillsToPay().size() == 1); // There is one bill left in the array still
		
		// Run payBill in the MockCustomer
		c4.payBill();
		
		// Check post conditions
		assertTrue(c4.log.containsString(c4.getName() + " paid the bill"));
		
		// Loop through list and find the payment that matches the bill
		assertTrue(cashier.findCBill(testBill_4));
		
		// Run the cashier scheduler again
		cashier.runScheduler();
		
		// Check that the testBill does not exists in either of the cashier lists
		assertFalse(cashier.getBillsToPay().contains(testBill_4));
		assertFalse(cashier.findCBill(testBill_4));
		assertTrue(cashier.getBillsToPay().size() == 0); // There should be no bills left

		
		// Check that the customer received payment from the cashier
		assertTrue(c1.log.containsString(c1.getName() + " paid in full"));
		// Check that the customer received payment from the cashier
		assertTrue(c2.log.containsString(c2.getName() + " paid in full"));		
		// Check that the customer received payment from the cashier
		assertTrue(c3.log.containsString(c3.getName() + " paid in full"));
		// Check that the customer received payment from the cashier
		assertTrue(c4.log.containsString(c4.getName() + " paid in full"));			
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
	public void testTwoCustomersPayAndLeaveNonNormative() { 
	// This test is the same as the single one, but now there are two mockcustomers to test with instead of one.
		
		// Set up the initial agents
		CashierAgent cashier = new CashierAgent("cashier:testTwoCustomersPayAndLeaveNonNormative");
		
		// Insert Mock Customer Here
		MockCustomer c1 = new MockCustomer("c1", cashier);
		MockCustomer c2 = new MockCustomer("c2", cashier);
		
		// Begin the test 
		
		// Create the bill to send to the cashier and the customer
		Bill testBill_1 = new Bill(15.99, "Steak", c1); // Add a customer reference into the bill
		Bill testBill_2 = new Bill(5.99, "Salad", c2); // Add a customer reference into the bill
		
		// Start by testing the pre-conditions outlined in #1
		// Cashier 
		assertEquals(cashier.getBillsToPay().size(), 0); // No billsToPay
		
		// Send the cashier the bill
		cashier.msgHereIsCustomerOrder(testBill_1);
		
		// Check to make sure that the bill is stored correctly
		assertTrue(cashier.getBillsToPay().size() == 1); // Check that a bill exists
		assertTrue(cashier.getBillsToPay().contains(testBill_1)); // Check that this bill exists
		assertTrue(cashier.getCustomerPayments().size() == 0); // Make sure that nothing exists in the customerPayments array
		
		// Send the cashier the bill
		cashier.msgHereIsCustomerOrder(testBill_2);
		
		// Check to make sure that the bill is stored correctly
		assertTrue(cashier.getBillsToPay().size() == 2); // Check that a bill exists
		assertTrue(cashier.getBillsToPay().contains(testBill_2)); // Check that this bill exists
		assertTrue(cashier.getCustomerPayments().size() == 0); // Make sure that nothing exists in the customerPayments array
		
		/***********/
		
		// Set c1's money to be below the value of a steak
		c1.wallet = 15.00;
		c2.wallet = 5.00;
		
		// Test Customer Preconditions 
		assertTrue(c1.bill == null); // Make sure that the bill has not been touched yet
		// Make sure that monies have not been touched
		assertEquals(c1.wallet, 15.00);
		assertEquals(c1.amountOwed, 0.00);
		
		// Send customer the bill now
		c1.msgHereIsYourFood(testBill_1);
				
		// Test post conditions
		assertTrue(c1.bill == testBill_1);
		assertEquals(c1.wallet, 15.00);
		assertEquals(c1.amountOwed, 0.00);
		
		// Check post condition
		assertTrue(cashier.getBillsToPay().size() == 2);
		
		// Test Customer Preconditions 
		assertTrue(c2.bill == null); // Make sure that the bill has not been touched yet
		// Make sure that monies have not been touched
		assertEquals(c2.wallet, 5.00);
		assertEquals(c2.amountOwed, 0.00);
		
		// Send customer the bill now
		c2.msgHereIsYourFood(testBill_2);
				
		// Test post conditions
		assertTrue(c2.bill == testBill_2);
		assertEquals(c2.wallet, 5.00);
		assertEquals(c2.amountOwed, 0.00);
		
		// Run the cashier's scheduler
		cashier.runScheduler();
		
		// Check post condition
		assertTrue(cashier.getBillsToPay().size() == 2);
		
		// Run payBill in the MockCustomer
		c1.payBill();
		
		// Check post conditions
		assertTrue(c1.log.containsString(c1.getName() + " paid the bill"));
		
		// Loop through list and find the payment that matches the bill
		assertTrue(cashier.findCBill(testBill_1));
		
		// Run the cashier scheduler again
		cashier.runScheduler();
		
		// Check that the testBill does not exists in either of the cashier lists
		assertFalse(cashier.getBillsToPay().contains(testBill_1));
		assertFalse(cashier.findCBill(testBill_1));
		assertTrue(cashier.getBillsToPay().size() == 1); // There is one bill left in the array still
		
		// Run payBill in the MockCustomer
		c2.payBill();
		
		// Check post conditions
		assertTrue(c2.log.containsString(c2.getName() + " paid the bill"));
		
		// Loop through list and find the payment that matches the bill
		assertTrue(cashier.findCBill(testBill_2));
		
		// Run the cashier scheduler again
		cashier.runScheduler();
		
		// Check that the testBill does not exists in either of the cashier lists
		assertFalse(cashier.getBillsToPay().contains(testBill_2));
		assertFalse(cashier.findCBill(testBill_2));
		assertTrue(cashier.getBillsToPay().size() == 0); // There should be no bills left
		
		// Check that the customer received payment from the cashier
		assertTrue(c1.log.containsString(c1.getName() + " did not pay in full"));
		// Check that the customer received payment from the cashier
		assertTrue(c2.log.containsString(c2.getName() + " did not pay in full"));	
	}
	
	@Test
	public void testFourCustomersPayAndLeaveNonNormative() { 
		// This test is the same as the single one, but now there are four mockcustomers to test with instead of one.

		// Set up the initial agents
		CashierAgent cashier = new CashierAgent("cashier:testFourCustomersPayAndLeaveNormative");
		
		// Insert Mock Customer Here
		MockCustomer c1 = new MockCustomer("c1", cashier);
		MockCustomer c2 = new MockCustomer("c2", cashier);
		MockCustomer c3 = new MockCustomer("c3", cashier);
		MockCustomer c4 = new MockCustomer("c4", cashier);
		
		// Begin the test 
		
		// Create the bill to send to the cashier and the customer
		Bill testBill_1 = new Bill(15.99, "Steak", c1); // Add a customer reference into the bill
		Bill testBill_2 = new Bill(5.99, "Salad", c2); // Add a customer reference into the bill
		Bill testBill_3 = new Bill(8.99, "Pizza", c3); // Add a customer reference into the bill
		Bill testBill_4 = new Bill(10.99, "Chicken", c4); // Add a customer reference into the bill
		
		// Start by testing the pre-conditions outlined in #1
		// Cashier 
		assertEquals(cashier.getBillsToPay().size(), 0); // No billsToPay
		
		// Send the cashier the bill
		cashier.msgHereIsCustomerOrder(testBill_1);
		
		// Check to make sure that the bill is stored correctly
		assertTrue(cashier.getBillsToPay().size() == 1); // Check that a bill exists
		assertTrue(cashier.getBillsToPay().contains(testBill_1)); // Check that this bill exists
		assertTrue(cashier.getCustomerPayments().size() == 0); // Make sure that nothing exists in the customerPayments array
		
		// Send the cashier the bill
		cashier.msgHereIsCustomerOrder(testBill_2);
		
		// Check to make sure that the bill is stored correctly
		assertTrue(cashier.getBillsToPay().size() == 2); // Check that a bill exists
		assertTrue(cashier.getBillsToPay().contains(testBill_2)); // Check that this bill exists
		assertTrue(cashier.getCustomerPayments().size() == 0); // Make sure that nothing exists in the customerPayments array
		
		// Send the cashier the bill
		cashier.msgHereIsCustomerOrder(testBill_3);
		
		// Check to make sure that the bill is stored correctly
		assertTrue(cashier.getBillsToPay().size() == 3); // Check that a bill exists
		assertTrue(cashier.getBillsToPay().contains(testBill_3)); // Check that this bill exists
		assertTrue(cashier.getCustomerPayments().size() == 0); // Make sure that nothing exists in the customerPayments array
		
		// Send the cashier the bill
		cashier.msgHereIsCustomerOrder(testBill_4);
		
		// Check to make sure that the bill is stored correctly
		assertTrue(cashier.getBillsToPay().size() == 4); // Check that a bill exists
		assertTrue(cashier.getBillsToPay().contains(testBill_4)); // Check that this bill exists
		assertTrue(cashier.getCustomerPayments().size() == 0); // Make sure that nothing exists in the customerPayments array
		
		/***********/
		// Set some monies to be below the value of a steak
		c1.wallet = 7.00;
		c3.wallet = 5.00;
		
		// Test Customer Preconditions 
		assertTrue(c1.bill == null); // Make sure that the bill has not been touched yet
		// Make sure that monies have not been touched
		assertEquals(c1.wallet, 7.00);
		assertEquals(c1.amountOwed, 0.00);
		
		// Send customer the bill now
		c1.msgHereIsYourFood(testBill_1);
				
		// Test post conditions
		assertTrue(c1.bill == testBill_1);
		assertEquals(c1.wallet, 7.00);
		assertEquals(c1.amountOwed, 0.00);
		
		// Check post condition
		assertTrue(cashier.getBillsToPay().size() == 4);
		
		// Test Customer Preconditions 
		assertTrue(c2.bill == null); // Make sure that the bill has not been touched yet
		// Make sure that monies have not been touched
		assertEquals(c2.wallet, 125.00);
		assertEquals(c2.amountOwed, 0.00);
		
		// Send customer the bill now
		c2.msgHereIsYourFood(testBill_2);
				
		// Test post conditions
		assertTrue(c2.bill == testBill_2);
		assertEquals(c2.wallet, 125.00);
		assertEquals(c2.amountOwed, 0.00);
		
		// Test Customer Preconditions 
		assertTrue(c3.bill == null); // Make sure that the bill has not been touched yet
		// Make sure that monies have not been touched
		assertEquals(c3.wallet, 5.00);
		assertEquals(c3.amountOwed, 0.00);
		
		// Send customer the bill now
		c3.msgHereIsYourFood(testBill_3);
				
		// Test post conditions
		assertTrue(c3.bill == testBill_3);
		assertEquals(c3.wallet, 5.00);
		assertEquals(c3.amountOwed, 0.00);
		
		// Check post condition
		assertTrue(cashier.getBillsToPay().size() == 4);
		
		// Test Customer Preconditions 
		assertTrue(c4.bill == null); // Make sure that the bill has not been touched yet
		// Make sure that monies have not been touched
		assertEquals(c4.wallet, 125.00);
		assertEquals(c4.amountOwed, 0.00);
		
		// Send customer the bill now
		c4.msgHereIsYourFood(testBill_4);
				
		// Test post conditions
		assertTrue(c4.bill == testBill_4);
		assertEquals(c4.wallet, 125.00);
		assertEquals(c4.amountOwed, 0.00);
		
		// Run the cashier's scheduler
		cashier.runScheduler();
		
		// Check post condition
		assertTrue(cashier.getBillsToPay().size() == 4);
		
		// Run payBill in the MockCustomer
		c1.payBill();
		
		// Check post conditions
		assertTrue(c1.log.containsString(c1.getName() + " paid the bill"));
		
		// Loop through list and find the payment that matches the bill
		assertTrue(cashier.findCBill(testBill_1));
		
		// Run the cashier scheduler again
		cashier.runScheduler();
		
		// Check that the testBill does not exists in either of the cashier lists
		assertFalse(cashier.getBillsToPay().contains(testBill_1));
		assertFalse(cashier.findCBill(testBill_1));
		assertTrue(cashier.getBillsToPay().size() == 3); // There is one bill left in the array still
		
		// Run payBill in the MockCustomer
		c2.payBill();
		
		// Check post conditions
		assertTrue(c2.log.containsString(c2.getName() + " paid the bill"));
		
		// Loop through list and find the payment that matches the bill
		assertTrue(cashier.findCBill(testBill_2));
		
		// Run the cashier scheduler again
		cashier.runScheduler();
		
		// Check that the testBill does not exists in either of the cashier lists
		assertFalse(cashier.getBillsToPay().contains(testBill_2));
		assertFalse(cashier.findCBill(testBill_2));
		assertTrue(cashier.getBillsToPay().size() == 2); // There is one bill left in the array still
		
		// Run payBill in the MockCustomer
		c3.payBill();
		
		// Check post conditions
		assertTrue(c3.log.containsString(c3.getName() + " paid the bill"));
		
		// Loop through list and find the payment that matches the bill
		assertTrue(cashier.findCBill(testBill_3));
		
		// Run the cashier scheduler again
		cashier.runScheduler();
		
		// Check that the testBill does not exists in either of the cashier lists
		assertFalse(cashier.getBillsToPay().contains(testBill_3));
		assertFalse(cashier.findCBill(testBill_3));
		assertTrue(cashier.getBillsToPay().size() == 1); // There is one bill left in the array still
		
		// Run payBill in the MockCustomer
		c4.payBill();
		
		// Check post conditions
		assertTrue(c4.log.containsString(c4.getName() + " paid the bill"));
		
		// Loop through list and find the payment that matches the bill
		assertTrue(cashier.findCBill(testBill_4));
		
		// Run the cashier scheduler again
		cashier.runScheduler();
		
		// Check that the testBill does not exists in either of the cashier lists
		assertFalse(cashier.getBillsToPay().contains(testBill_4));
		assertFalse(cashier.findCBill(testBill_4));
		assertTrue(cashier.getBillsToPay().size() == 0); // There should be no bills left

		
		// Check that the customer received payment from the cashier
		assertTrue(c1.log.containsString(c1.getName() + " did not pay in full"));
		// Check that the customer received payment from the cashier
		assertTrue(c2.log.containsString(c2.getName() + " paid in full"));	
		// Check that the customer received payment from the cashier
		assertTrue(c3.log.containsString(c3.getName() + " did not pay in full"));
		// Check that the customer received payment from the cashier
		assertTrue(c4.log.containsString(c4.getName() + " paid in full"));	
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
	public void testDoublePayMarketBill() { 
		// This test is the same as the single one, but now there are two mockmarkets to test with instead of one.

		// Create the Agents who will participate in this test
		CashierAgent cashier = new CashierAgent("cashier:testDoublePayMarketBill");
		MockMarket m1 = new MockMarket("m1");
		MockMarket m2 = new MockMarket("m2");
		
		// Create the testBill
		Bill testBill_1 = new Bill(100.00, "1000", m1); // Will be a test bill to be sent to the cashier
		Bill testBill_2 = new Bill(200.00, "2000", m2); // Will be a test bill to be sent to the cashier
		
		// Check preconditions for cashier
		assertTrue(cashier.getMarketBills().size() == 0);
		
		// Send the bill to the cashier
		cashier.msgHereIsBill(testBill_1);
		
		// Check postconditions for cashier
		assertTrue(cashier.getMarketBills().size() == 1);
		assertTrue(cashier.getMarketBills().contains(testBill_1));
		
		// Send the bill to the cashier
		cashier.msgHereIsBill(testBill_2);
		
		// Check postconditions for cashier
		assertTrue(cashier.getMarketBills().size() == 2);
		assertTrue(cashier.getMarketBills().contains(testBill_2));
		
		// Get the totalMoney of the cashier before paying for the order
		double cashierPreMoney_1 = cashier.getTotalMoney();
		
		// Run the cashier scheduler
		cashier.runScheduler();
		
		// Check postconditions
		assertEquals(cashier.getTotalMoney(), cashierPreMoney_1 - testBill_1.totalCost); // Make sure that the amount from the bill was deducted from the cashier
		// Make sure that the bill is out of the cashier's hands once it is paid
		assertTrue(cashier.getMarketBills().size() == 1);
		assertFalse(cashier.getMarketBills().contains(testBill_1));
		// Make sure that MockMarket has the payment that equals the cost of the bill
		assertTrue(m1.paymentForTotalCostExists(testBill_1.totalCost));	
		
		// Get the totalMoney of the cashier before paying for the order
		double cashierPreMoney_2 = cashier.getTotalMoney();
		
		// Run the cashier scheduler
		cashier.runScheduler();
		
		// Check postconditions
		assertEquals(cashier.getTotalMoney(), cashierPreMoney_2 - testBill_2.totalCost); // Make sure that the amount from the bill was deducted from the cashier
		// Make sure that the bill is out of the cashier's hands once it is paid
		assertTrue(cashier.getMarketBills().size() == 0);
		assertFalse(cashier.getMarketBills().contains(testBill_2));
		// Make sure that MockMarket has the payment that equals the cost of the bill
		assertTrue(m2.paymentForTotalCostExists(testBill_2.totalCost));	

	}
	
	@Test
	public void testQuadPayMarketBill() { 
		// This test is the same as the single one, but now there are four mockmarkets to test with instead of one.

		// This test is the same as the single one, but now there are two mockmarkets to test with instead of one.

		// Create the Agents who will participate in this test
		CashierAgent cashier = new CashierAgent("cashier:testQuadPayMarketBill");
		MockMarket m1 = new MockMarket("m1");
		MockMarket m2 = new MockMarket("m2");
		MockMarket m3 = new MockMarket("m3");
		MockMarket m4 = new MockMarket("m4");
		
		// Create the testBill
		Bill testBill_1 = new Bill(100.00, "1000", m1); // Will be a test bill to be sent to the cashier
		Bill testBill_2 = new Bill(200.00, "2000", m2); // Will be a test bill to be sent to the cashier
		Bill testBill_3 = new Bill(300.00, "3000", m3); // Will be a test bill to be sent to the cashier
		Bill testBill_4 = new Bill(400.00, "4000", m4); // Will be a test bill to be sent to the cashier
		
		// Check preconditions for cashier
		assertTrue(cashier.getMarketBills().size() == 0);
		
		// Send the bill to the cashier
		cashier.msgHereIsBill(testBill_1);
		
		// Check postconditions for cashier
		assertTrue(cashier.getMarketBills().size() == 1);
		assertTrue(cashier.getMarketBills().contains(testBill_1));
		
		// Send the bill to the cashier
		cashier.msgHereIsBill(testBill_2);
		
		// Check postconditions for cashier
		assertTrue(cashier.getMarketBills().size() == 2);
		assertTrue(cashier.getMarketBills().contains(testBill_2));
		
		// Send the bill to the cashier
		cashier.msgHereIsBill(testBill_3);
		
		// Check postconditions for cashier
		assertTrue(cashier.getMarketBills().size() == 3);
		assertTrue(cashier.getMarketBills().contains(testBill_3));
		
		// Send the bill to the cashier
		cashier.msgHereIsBill(testBill_4);
		
		// Check postconditions for cashier
		assertTrue(cashier.getMarketBills().size() == 4);
		assertTrue(cashier.getMarketBills().contains(testBill_4));
		
		// Get the totalMoney of the cashier before paying for the order
		double cashierPreMoney_1 = cashier.getTotalMoney();
		
		// Run the cashier scheduler
		cashier.runScheduler();
		
		// Check postconditions
		assertEquals(cashier.getTotalMoney(), cashierPreMoney_1 - testBill_1.totalCost); // Make sure that the amount from the bill was deducted from the cashier
		// Make sure that the bill is out of the cashier's hands once it is paid
		assertTrue(cashier.getMarketBills().size() == 3);
		assertFalse(cashier.getMarketBills().contains(testBill_1));
		// Make sure that MockMarket has the payment that equals the cost of the bill
		assertTrue(m1.paymentForTotalCostExists(testBill_1.totalCost));	
		
		// Get the totalMoney of the cashier before paying for the order
		double cashierPreMoney_2 = cashier.getTotalMoney();
		
		// Run the cashier scheduler
		cashier.runScheduler();
		
		// Check postconditions
		assertEquals(cashier.getTotalMoney(), cashierPreMoney_2 - testBill_2.totalCost); // Make sure that the amount from the bill was deducted from the cashier
		// Make sure that the bill is out of the cashier's hands once it is paid
		assertTrue(cashier.getMarketBills().size() == 2);
		assertFalse(cashier.getMarketBills().contains(testBill_2));
		// Make sure that MockMarket has the payment that equals the cost of the bill
		assertTrue(m2.paymentForTotalCostExists(testBill_2.totalCost));
		
		// Get the totalMoney of the cashier before paying for the order
		double cashierPreMoney_3 = cashier.getTotalMoney();
		
		// Run the cashier scheduler
		cashier.runScheduler();
		
		// Check postconditions
		assertEquals(cashier.getTotalMoney(), cashierPreMoney_3 - testBill_3.totalCost); // Make sure that the amount from the bill was deducted from the cashier
		// Make sure that the bill is out of the cashier's hands once it is paid
		assertTrue(cashier.getMarketBills().size() == 1);
		assertFalse(cashier.getMarketBills().contains(testBill_3));
		// Make sure that MockMarket has the payment that equals the cost of the bill
		assertTrue(m3.paymentForTotalCostExists(testBill_3.totalCost));	
		
		// Get the totalMoney of the cashier before paying for the order
		double cashierPreMoney_4 = cashier.getTotalMoney();
		
		// Run the cashier scheduler
		cashier.runScheduler();
		
		// Check postconditions
		assertEquals(cashier.getTotalMoney(), cashierPreMoney_4 - testBill_4.totalCost); // Make sure that the amount from the bill was deducted from the cashier
		// Make sure that the bill is out of the cashier's hands once it is paid
		assertTrue(cashier.getMarketBills().size() == 0);
		assertFalse(cashier.getMarketBills().contains(testBill_4));
		// Make sure that MockMarket has the payment that equals the cost of the bill
		assertTrue(m4.paymentForTotalCostExists(testBill_4.totalCost));	
	}
	
	@Test
	public void testMultipleCashierScenarios_v1() {
		/*
		 - This test will complete the following objective:  test that the cashier can multitask correctly by making sure that it can handle multiple tasks from the previous tests at once
		 - Refer to comments on the previous tests for insight as to what is going on -- this is only a combination of older tests
		 
		 - The combination for this test will be the following:
		 (1) Four Customers will come in and pay for their food -- two will be able to pay and two will not
		 (2) One MockMarket will be instantiated, but the cashier will have to pay for two bills that have been sent from this market -- this was not tested until now
		 
		 - The ordering at which these tasks will be completed will be spliced -- as it would occur in the actual simulation
		 - This test will have an emphasis as the customer paying bills one (quad), but (double) market interactions will be spliced within it, as marked by /************/		 
		//*/
		
		// This test is the same as the single one, but now there are two mockmarkets to test with instead of one.
		
		// Begin the test 
		
		// Create the Agents who will participate in this test
		CashierAgent cashier = new CashierAgent("cashier:testMultipleCashierScenarios_v1");
		MockMarket m1 = new MockMarket("m1");
		
		// Insert Mock Customer Here
		MockCustomer c1 = new MockCustomer("c1", cashier);
		MockCustomer c2 = new MockCustomer("c2", cashier);
		MockCustomer c3 = new MockCustomer("c3", cashier);
		MockCustomer c4 = new MockCustomer("c4", cashier);
		
		// Create the testBillMarket
		Bill testBillMarket_1 = new Bill(100.00, "1000", m1); // Will be a test bill to be sent to the cashier
		Bill testBillMarket_2 = new Bill(200.00, "2000", m1); // Will be a test bill to be sent to the cashier
		
		// Create the bill to send to the cashier and the customer
		Bill testBill_1 = new Bill(15.99, "Steak", c1); // Add a customer reference into the bill
		Bill testBill_2 = new Bill(5.99, "Salad", c2); // Add a customer reference into the bill
		Bill testBill_3 = new Bill(8.99, "Pizza", c3); // Add a customer reference into the bill
		Bill testBill_4 = new Bill(10.99, "Chicken", c4); // Add a customer reference into the bill
		
		// Start by testing the pre-conditions outlined in #1
		// Cashier 
		assertEquals(cashier.getBillsToPay().size(), 0); // No billsToPay
		
		// Send the cashier the bill
		cashier.msgHereIsCustomerOrder(testBill_1);
		
		// Check to make sure that the bill is stored correctly
		assertTrue(cashier.getBillsToPay().size() == 1); // Check that a bill exists
		assertTrue(cashier.getBillsToPay().contains(testBill_1)); // Check that this bill exists
		assertTrue(cashier.getCustomerPayments().size() == 0); // Make sure that nothing exists in the customerPayments array
		
		// Send the cashier the bill
		cashier.msgHereIsCustomerOrder(testBill_2);
		
		// Check to make sure that the bill is stored correctly
		assertTrue(cashier.getBillsToPay().size() == 2); // Check that a bill exists
		assertTrue(cashier.getBillsToPay().contains(testBill_2)); // Check that this bill exists
		assertTrue(cashier.getCustomerPayments().size() == 0); // Make sure that nothing exists in the customerPayments array
		
		/************/
		// Check preconditions for cashier
		assertTrue(cashier.getMarketBills().size() == 0);
		
		// Send the Market bill to the cashier
		cashier.msgHereIsBill(testBillMarket_1);
		
		// Check postconditions for cashier
		assertTrue(cashier.getMarketBills().size() == 1);
		assertTrue(cashier.getMarketBills().contains(testBillMarket_1));
		/************/
		
		// Send the cashier the bill
		cashier.msgHereIsCustomerOrder(testBill_3);
		
		// Check to make sure that the bill is stored correctly
		assertTrue(cashier.getBillsToPay().size() == 3); // Check that a bill exists
		assertTrue(cashier.getBillsToPay().contains(testBill_3)); // Check that this bill exists
		assertTrue(cashier.getCustomerPayments().size() == 0); // Make sure that nothing exists in the customerPayments array
		
		/************/
		// Check preconditions for cashier
		assertTrue(cashier.getMarketBills().size() == 1);
		
		// Send the 2nd Market bill to the cashier
		cashier.msgHereIsBill(testBillMarket_2);
		
		// Check postconditions for cashier
		assertTrue(cashier.getMarketBills().size() == 2);
		assertTrue(cashier.getMarketBills().contains(testBillMarket_2));
		/************/
		
		// Send the cashier the bill
		cashier.msgHereIsCustomerOrder(testBill_4);
		
		// Check to make sure that the bill is stored correctly
		assertTrue(cashier.getBillsToPay().size() == 4); // Check that a bill exists
		assertTrue(cashier.getBillsToPay().contains(testBill_4)); // Check that this bill exists
		assertTrue(cashier.getCustomerPayments().size() == 0); // Make sure that nothing exists in the customerPayments array
		
		/***********/
		// Set some monies to be below the value of a steak
		c1.wallet = 7.00;
		c3.wallet = 5.00;
		
		// Test Customer Preconditions 
		assertTrue(c1.bill == null); // Make sure that the bill has not been touched yet
		// Make sure that monies have not been touched
		assertEquals(c1.wallet, 7.00);
		assertEquals(c1.amountOwed, 0.00);
		
		// Send customer the bill now
		c1.msgHereIsYourFood(testBill_1);
				
		// Test post conditions
		assertTrue(c1.bill == testBill_1);
		assertEquals(c1.wallet, 7.00);
		assertEquals(c1.amountOwed, 0.00);
		
		// Check post condition
		assertTrue(cashier.getBillsToPay().size() == 4);
		
		/************/		
		// Get the totalMoney of the cashier before paying for the order
		double cashierPreMoney_1 = cashier.getTotalMoney();
		
		// Run the cashier scheduler
		cashier.runScheduler();
		
		//cashier.testprint(Double.toString(cashier.getTotalMoney()) + " : " +  Double.toString(cashierPreMoney_1 - testBillMarket_1.totalCost));
		
		// Check postconditions
		assertEquals(cashier.getTotalMoney(), cashierPreMoney_1 - testBillMarket_1.totalCost); // Make sure that the amount from the bill was deducted from the cashier
		// Make sure that the bill is out of the cashier's hands once it is paid
		assertTrue(cashier.getMarketBills().size() == 1);
		assertFalse(cashier.getMarketBills().contains(testBillMarket_1));
		// Make sure that MockMarket has the payment that equals the cost of the bill
		assertTrue(m1.paymentForTotalCostExists(testBillMarket_1.totalCost));		
		/************/
		
		// Test Customer Preconditions 
		assertTrue(c2.bill == null); // Make sure that the bill has not been touched yet
		// Make sure that monies have not been touched
		assertEquals(c2.wallet, 125.00);
		assertEquals(c2.amountOwed, 0.00);
		
		// Send customer the bill now
		c2.msgHereIsYourFood(testBill_2);
				
		// Test post conditions
		assertTrue(c2.bill == testBill_2);
		assertEquals(c2.wallet, 125.00);
		assertEquals(c2.amountOwed, 0.00);
		
		// Test Customer Preconditions 
		assertTrue(c3.bill == null); // Make sure that the bill has not been touched yet
		// Make sure that monies have not been touched
		assertEquals(c3.wallet, 5.00);
		assertEquals(c3.amountOwed, 0.00);
		
		// Send customer the bill now
		c3.msgHereIsYourFood(testBill_3);
				
		// Test post conditions
		assertTrue(c3.bill == testBill_3);
		assertEquals(c3.wallet, 5.00);
		assertEquals(c3.amountOwed, 0.00);
		
		// Check post condition
		assertTrue(cashier.getBillsToPay().size() == 4);
		
		// Test Customer Preconditions 
		assertTrue(c4.bill == null); // Make sure that the bill has not been touched yet
		// Make sure that monies have not been touched
		assertEquals(c4.wallet, 125.00);
		assertEquals(c4.amountOwed, 0.00);
		
		// Send customer the bill now
		c4.msgHereIsYourFood(testBill_4);
				
		// Test post conditions
		assertTrue(c4.bill == testBill_4);
		assertEquals(c4.wallet, 125.00);
		assertEquals(c4.amountOwed, 0.00);
		
		/************/
		// Get the totalMoney of the cashier before paying for the order
		double cashierPreMoney_2 = cashier.getTotalMoney();
		
		// Run the cashier scheduler -- Note that I merged this for checking the market bills and the customer bills
		// Even though scheduler would NOT do anything with processing the customer payments, it still would pay the MarketBill anyway
		cashier.runScheduler();
		
		//cashier.testprint(Double.toString(cashier.getTotalMoney()) + " : " +  Double.toString(cashierPreMoney_2 - testBillMarket_2.totalCost));
		
		// Check postconditions
		assertEquals(cashier.getTotalMoney(), cashierPreMoney_2 - testBillMarket_2.totalCost); // Make sure that the amount from the bill was deducted from the cashier
		// Make sure that the bill is out of the cashier's hands once it is paid
		assertTrue(cashier.getMarketBills().size() == 0);
		assertFalse(cashier.getMarketBills().contains(testBillMarket_2));
		// Make sure that MockMarket has the payment that equals the cost of the bill
		assertTrue(m1.paymentForTotalCostExists(testBillMarket_2.totalCost));	
		/************/
		
		// Check post condition of the test before this too
		assertTrue(cashier.getBillsToPay().size() == 4);
		
		// Run payBill in the MockCustomer
		c1.payBill();
		
		// Check post conditions
		assertTrue(c1.log.containsString(c1.getName() + " paid the bill"));
		
		// Loop through list and find the payment that matches the bill
		assertTrue(cashier.findCBill(testBill_1));
		
		// Run the cashier scheduler again
		cashier.runScheduler();
		
		// Check that the testBill does not exists in either of the cashier lists
		assertFalse(cashier.getBillsToPay().contains(testBill_1));
		assertFalse(cashier.findCBill(testBill_1));
		assertTrue(cashier.getBillsToPay().size() == 3); // There is one bill left in the array still
		
		// Run payBill in the MockCustomer
		c2.payBill();
		
		// Check post conditions
		assertTrue(c2.log.containsString(c2.getName() + " paid the bill"));
		
		// Loop through list and find the payment that matches the bill
		assertTrue(cashier.findCBill(testBill_2));
		
		// Run the cashier scheduler again
		cashier.runScheduler();
		
		// Check that the testBill does not exists in either of the cashier lists
		assertFalse(cashier.getBillsToPay().contains(testBill_2));
		assertFalse(cashier.findCBill(testBill_2));
		assertTrue(cashier.getBillsToPay().size() == 2); // There is one bill left in the array still
		
		// Run payBill in the MockCustomer
		c3.payBill();
		
		// Check post conditions
		assertTrue(c3.log.containsString(c3.getName() + " paid the bill"));
		
		// Loop through list and find the payment that matches the bill
		assertTrue(cashier.findCBill(testBill_3));
		
		// Run the cashier scheduler again
		cashier.runScheduler();
		
		// Check that the testBill does not exists in either of the cashier lists
		assertFalse(cashier.getBillsToPay().contains(testBill_3));
		assertFalse(cashier.findCBill(testBill_3));
		assertTrue(cashier.getBillsToPay().size() == 1); // There is one bill left in the array still
		
		// Run payBill in the MockCustomer
		c4.payBill();
		
		// Check post conditions
		assertTrue(c4.log.containsString(c4.getName() + " paid the bill"));
		
		// Loop through list and find the payment that matches the bill
		assertTrue(cashier.findCBill(testBill_4));
		
		// Run the cashier scheduler again
		cashier.runScheduler();
		
		// Check that the testBill does not exists in either of the cashier lists
		assertFalse(cashier.getBillsToPay().contains(testBill_4));
		assertFalse(cashier.findCBill(testBill_4));
		assertTrue(cashier.getBillsToPay().size() == 0); // There should be no bills left
		
		// Check that the customer received payment from the cashier
		assertTrue(c1.log.containsString(c1.getName() + " did not pay in full"));
		// Check that the customer received payment from the cashier
		assertTrue(c2.log.containsString(c2.getName() + " paid in full"));	
		// Check that the customer received payment from the cashier
		assertTrue(c3.log.containsString(c3.getName() + " did not pay in full"));
		// Check that the customer received payment from the cashier
		assertTrue(c4.log.containsString(c4.getName() + " paid in full"));
	}
	
	public void testMultipleCashierScenarios_v2() {
		/*
		 - This test will complete the following objective:  test that the cashier can multitask correctly by making sure that it can handle multiple tasks from the previous tests at once
		 - Refer to comments on the previous tests for insight as to what is going on -- this is only a combination of older tests
		 
		 - The ordering at which these tasks will be completed will be spliced -- as it would occur in the actual simulation
		 - This test will have an emphasis as the market bills one (quad), but (quad) customer interactions will be spliced within it, as marked by /************/
		 //*/
		
		// Begin the test 
		
		// Set up the initial agents
		CashierAgent cashier = new CashierAgent("cashier:testMultipleCashierScenarios_v2");

		// Create the Agents who will participate in this test
		MockMarket m1 = new MockMarket("m1");
		MockMarket m2 = new MockMarket("m2");
		
		// Create the testMarketBill
		Bill testMarketBill_1 = new Bill(100.00, "1000", m1); // Will be a test bill to be sent to the cashier
		Bill testMarketBill_2 = new Bill(200.00, "2000", m2); // Will be a test bill to be sent to the cashier
		Bill testMarketBill_3 = new Bill(300.00, "3000", m1); // Will be a test bill to be sent to the cashier
		Bill testMarketBill_4 = new Bill(400.00, "4000", m2); // Will be a test bill to be sent to the cashier
		
		// Insert Mock Customer Here
		MockCustomer c1 = new MockCustomer("c1", cashier);
		MockCustomer c2 = new MockCustomer("c2", cashier);
		MockCustomer c3 = new MockCustomer("c3", cashier);
		MockCustomer c4 = new MockCustomer("c4", cashier);
		
		// Create the bill to send to the cashier and the customer
		Bill testBill_1 = new Bill(15.99, "Steak", c1); // Add a customer reference into the bill
		Bill testBill_2 = new Bill(5.99, "Salad", c2); // Add a customer reference into the bill
		Bill testBill_3 = new Bill(8.99, "Pizza", c3); // Add a customer reference into the bill
		Bill testBill_4 = new Bill(10.99, "Chicken", c4); // Add a customer reference into the bill
		
		// Check preconditions for cashier
		assertTrue(cashier.getMarketBills().size() == 0);
		
		// Send the bill to the cashier
		cashier.msgHereIsBill(testMarketBill_1);
		
		// Check postconditions for cashier
		assertTrue(cashier.getMarketBills().size() == 1);
		assertTrue(cashier.getMarketBills().contains(testMarketBill_1));
		
		/************/
		// Cashier 
		assertEquals(cashier.getBillsToPay().size(), 0); // No billsToPay
		
		// Send the cashier the bill
		cashier.msgHereIsCustomerOrder(testBill_1);
		
		// Check to make sure that the bill is stored correctly
		assertTrue(cashier.getBillsToPay().size() == 1); // Check that a bill exists
		assertTrue(cashier.getBillsToPay().contains(testBill_1)); // Check that this bill exists
		assertTrue(cashier.getCustomerPayments().size() == 0); // Make sure that nothing exists in the customerPayments array
		/************/
		
		// Send the bill to the cashier
		cashier.msgHereIsBill(testMarketBill_2);
		
		// Check postconditions for cashier
		assertTrue(cashier.getMarketBills().size() == 2);
		assertTrue(cashier.getMarketBills().contains(testMarketBill_2));
		
		// Send the bill to the cashier
		cashier.msgHereIsBill(testMarketBill_3);
		
		// Check postconditions for cashier
		assertTrue(cashier.getMarketBills().size() == 3);
		assertTrue(cashier.getMarketBills().contains(testMarketBill_3));
		
		/************/		
		// Cashier 
		assertEquals(cashier.getBillsToPay().size(), 1); // billsToPay == 1
		
		// Send the cashier the bill
		cashier.msgHereIsCustomerOrder(testBill_2);
		
		// Check to make sure that the bill is stored correctly
		assertTrue(cashier.getBillsToPay().size() == 2); // Check that a bill exists
		assertTrue(cashier.getBillsToPay().contains(testBill_2)); // Check that this bill exists
		assertTrue(cashier.getCustomerPayments().size() == 0); // Make sure that nothing exists in the customerPayments array	
		/************/
		
		// Send the bill to the cashier
		cashier.msgHereIsBill(testMarketBill_4);
		
		// Check postconditions for cashier
		assertTrue(cashier.getMarketBills().size() == 4);
		assertTrue(cashier.getMarketBills().contains(testMarketBill_4));
		
		/************/
		assertEquals(cashier.getBillsToPay().size(), 2); // billsToPay == 2
		
		// Send the cashier the bill
		cashier.msgHereIsCustomerOrder(testBill_3);
		
		// Check to make sure that the bill is stored correctly
		assertTrue(cashier.getBillsToPay().size() == 3); // Check that a bill exists
		assertTrue(cashier.getBillsToPay().contains(testBill_3)); // Check that this bill exists
		assertTrue(cashier.getCustomerPayments().size() == 0); // Make sure that nothing exists in the customerPayments array
		
		// Send the cashier the bill
		cashier.msgHereIsCustomerOrder(testBill_4);
		
		// Check to make sure that the bill is stored correctly
		assertTrue(cashier.getBillsToPay().size() == 4); // Check that a bill exists
		assertTrue(cashier.getBillsToPay().contains(testBill_4)); // Check that this bill exists
		assertTrue(cashier.getCustomerPayments().size() == 0); // Make sure that nothing exists in the customerPayments array
		/************/
		
		/************/
		// Set some monies to be below the value of a steak
		c1.wallet = 7.00;
		c3.wallet = 5.00;
		
		// Test Customer Preconditions 
		assertTrue(c1.bill == null); // Make sure that the bill has not been touched yet
		// Make sure that monies have not been touched
		assertEquals(c1.wallet, 7.00);
		assertEquals(c1.amountOwed, 0.00);
		
		// Send customer the bill now
		c1.msgHereIsYourFood(testBill_1);
				
		// Test post conditions
		assertTrue(c1.bill == testBill_1);
		assertEquals(c1.wallet, 7.00);
		assertEquals(c1.amountOwed, 0.00);
		
		// Check post condition
		assertTrue(cashier.getBillsToPay().size() == 4);	
		/************/
		
		// Get the totalMoney of the cashier before paying for the order
		double cashierPreMoney_1 = cashier.getTotalMoney();
		
		// Run the cashier scheduler
		cashier.runScheduler();
		
		// Check postconditions
		assertEquals(cashier.getTotalMoney(), cashierPreMoney_1 - testMarketBill_1.totalCost); // Make sure that the amount from the bill was deducted from the cashier
		// Make sure that the bill is out of the cashier's hands once it is paid
		assertTrue(cashier.getMarketBills().size() == 3);
		assertFalse(cashier.getMarketBills().contains(testMarketBill_1));
		// Make sure that MockMarket has the payment that equals the cost of the bill
		assertTrue(m1.paymentForTotalCostExists(testMarketBill_1.totalCost));	
		
		// Get the totalMoney of the cashier before paying for the order
		double cashierPreMoney_2 = cashier.getTotalMoney();
		
		// Run the cashier scheduler
		cashier.runScheduler();
		
		// Check postconditions
		assertEquals(cashier.getTotalMoney(), cashierPreMoney_2 - testMarketBill_2.totalCost); // Make sure that the amount from the bill was deducted from the cashier
		// Make sure that the bill is out of the cashier's hands once it is paid
		assertTrue(cashier.getMarketBills().size() == 2);
		assertFalse(cashier.getMarketBills().contains(testMarketBill_2));
		// Make sure that MockMarket has the payment that equals the cost of the bill
		assertTrue(m2.paymentForTotalCostExists(testMarketBill_2.totalCost));
		
		/************/
		// Test Customer Preconditions 
		assertTrue(c2.bill == null); // Make sure that the bill has not been touched yet
		// Make sure that monies have not been touched
		assertEquals(c2.wallet, 125.00);
		assertEquals(c2.amountOwed, 0.00);
		
		// Send customer the bill now
		c2.msgHereIsYourFood(testBill_2);
				
		// Test post conditions
		assertTrue(c2.bill == testBill_2);
		assertEquals(c2.wallet, 125.00);
		assertEquals(c2.amountOwed, 0.00);
		
		// Check post condition
		assertTrue(cashier.getBillsToPay().size() == 4);
		
		// Test Customer Preconditions 
		assertTrue(c3.bill == null); // Make sure that the bill has not been touched yet
		// Make sure that monies have not been touched
		assertEquals(c3.wallet, 5.00);
		assertEquals(c3.amountOwed, 0.00);
		
		// Send customer the bill now
		c3.msgHereIsYourFood(testBill_3);
				
		// Test post conditions
		assertTrue(c3.bill == testBill_3);
		assertEquals(c3.wallet, 5.00);
		assertEquals(c3.amountOwed, 0.00);
		
		// Check post condition
		assertTrue(cashier.getBillsToPay().size() == 4);
		
		// Test Customer Preconditions 
		assertTrue(c4.bill == null); // Make sure that the bill has not been touched yet
		// Make sure that monies have not been touched
		assertEquals(c4.wallet, 125.00);
		assertEquals(c4.amountOwed, 0.00);
		
		// Send customer the bill now
		c4.msgHereIsYourFood(testBill_4);
				
		// Test post conditions
		assertTrue(c4.bill == testBill_4);
		assertEquals(c4.wallet, 125.00);
		assertEquals(c4.amountOwed, 0.00);
		/************/
		
		// Get the totalMoney of the cashier before paying for the order
		double cashierPreMoney_3 = cashier.getTotalMoney();
		
		// Run the cashier scheduler
		cashier.runScheduler();
		
		// Check postconditions
		assertEquals(cashier.getTotalMoney(), cashierPreMoney_3 - testMarketBill_3.totalCost); // Make sure that the amount from the bill was deducted from the cashier
		// Make sure that the bill is out of the cashier's hands once it is paid
		assertTrue(cashier.getMarketBills().size() == 1);
		assertFalse(cashier.getMarketBills().contains(testMarketBill_3));
		// Make sure that MockMarket has the payment that equals the cost of the bill
		assertTrue(m1.paymentForTotalCostExists(testMarketBill_3.totalCost));
		
		/************/
		// Check post condition
		assertTrue(cashier.getBillsToPay().size() == 4);	
		/************/
		
		// Get the totalMoney of the cashier before paying for the order
		double cashierPreMoney_4 = cashier.getTotalMoney();
		
		// Run the cashier scheduler
		cashier.runScheduler();
		
		// Check postconditions
		assertEquals(cashier.getTotalMoney(), cashierPreMoney_4 - testMarketBill_4.totalCost); // Make sure that the amount from the bill was deducted from the cashier
		// Make sure that the bill is out of the cashier's hands once it is paid
		assertTrue(cashier.getMarketBills().size() == 0);
		assertFalse(cashier.getMarketBills().contains(testMarketBill_4));
		// Make sure that MockMarket has the payment that equals the cost of the bill
		assertTrue(m2.paymentForTotalCostExists(testMarketBill_4.totalCost));
		
		/************/
		// Check post condition
		assertTrue(cashier.getBillsToPay().size() == 4);	
		/************/
		
		// Run the cashier's scheduler
		cashier.runScheduler();
		
		// Check post condition
		assertTrue(cashier.getBillsToPay().size() == 4);
		
		// Run payBill in the MockCustomer
		c1.payBill();
		
		// Check post conditions
		assertTrue(c1.log.containsString(c1.getName() + " paid the bill"));
		
		// Loop through list and find the payment that matches the bill
		assertTrue(cashier.findCBill(testBill_1));
		
		// Run the cashier scheduler again
		cashier.runScheduler();
		
		// Check that the testBill does not exists in either of the cashier lists
		assertFalse(cashier.getBillsToPay().contains(testBill_1));
		assertFalse(cashier.findCBill(testBill_1));
		assertTrue(cashier.getBillsToPay().size() == 3); // There is one bill left in the array still
		
		// Run payBill in the MockCustomer
		c2.payBill();
		
		// Check post conditions
		assertTrue(c2.log.containsString(c2.getName() + " paid the bill"));
		
		// Loop through list and find the payment that matches the bill
		assertTrue(cashier.findCBill(testBill_2));
		
		// Run the cashier scheduler again
		cashier.runScheduler();
		
		// Check that the testBill does not exists in either of the cashier lists
		assertFalse(cashier.getBillsToPay().contains(testBill_2));
		assertFalse(cashier.findCBill(testBill_2));
		assertTrue(cashier.getBillsToPay().size() == 2); // There is one bill left in the array still
		
		// Run payBill in the MockCustomer
		c3.payBill();
		
		// Check post conditions
		assertTrue(c3.log.containsString(c3.getName() + " paid the bill"));
		
		// Loop through list and find the payment that matches the bill
		assertTrue(cashier.findCBill(testBill_3));
		
		// Run the cashier scheduler again
		cashier.runScheduler();
		
		// Check that the testBill does not exists in either of the cashier lists
		assertFalse(cashier.getBillsToPay().contains(testBill_3));
		assertFalse(cashier.findCBill(testBill_3));
		assertTrue(cashier.getBillsToPay().size() == 1); // There is one bill left in the array still
		
		// Run payBill in the MockCustomer
		c4.payBill();
		
		// Check post conditions
		assertTrue(c4.log.containsString(c4.getName() + " paid the bill"));
		
		// Loop through list and find the payment that matches the bill
		assertTrue(cashier.findCBill(testBill_4));
		
		// Run the cashier scheduler again
		cashier.runScheduler();
		
		// Check that the testBill does not exists in either of the cashier lists
		assertFalse(cashier.getBillsToPay().contains(testBill_4));
		assertFalse(cashier.findCBill(testBill_4));
		assertTrue(cashier.getBillsToPay().size() == 0); // There should be no bills left
		
		// Check that the customer received payment from the cashier
		assertTrue(c1.log.containsString(c1.getName() + " did not pay in full"));
		// Check that the customer received payment from the cashier
		assertTrue(c2.log.containsString(c2.getName() + " paid in full"));	
		// Check that the customer received payment from the cashier
		assertTrue(c3.log.containsString(c3.getName() + " did not pay in full"));
		// Check that the customer received payment from the cashier
		assertTrue(c4.log.containsString(c4.getName() + " paid in full"));
	}
}
