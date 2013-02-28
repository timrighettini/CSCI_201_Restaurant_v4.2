package restaurant.test.Mock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import restaurant.interfaces.Market;

public class MockMarket extends MockAgent implements Market {

	private class Payment { // cashier payments
		public double money;
		public String id; // Matched to bills
		
		public Payment(double money, String id) {
			this.money = money;
			this.id = id;
		}
	}
	
	private List<Payment> cashierPayments = new ArrayList<Payment>(); 
	// Payments from the cashier.  bill.choice will the id of an order instead of the item choice itself
	// In this case, it will be used to make sure that a bill with x dollars is sent from the cashier to the market correctly

	
	public MockMarket(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	/* Required for cashier unit testing */

	@Override
	public void msgHereIsCashierPayment(double money, String oID) {
		// TODO Auto-generated method stub
		cashierPayments.add(new Payment(money, oID));
	}
	
	/* Extra method to be used to check that a payment for a certain bill.totalCost exists */
	public boolean paymentForTotalCostExists(double totalCost) {
		for (Payment p: cashierPayments) {
			if (p.money == totalCost);
			return true;
		}
		
		return false;
	}
	
	/*Not required*/
	
	@Override
	public void msgNeedFoodDelivered(Map<String, Integer> choices) {
		// TODO Auto-generated method stub
		
	}

}
