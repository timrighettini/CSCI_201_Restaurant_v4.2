package restaurant.interfaces;

import restaurant.Bill;

public interface Cashier {
	/*Part 1 Normative*/
	public abstract void msgHereIsCustomerOrder(Bill bill);

	public abstract void msgHereIsCustomerPayment(double money, Bill bill);

	/*Part 2 Normative*/
	public abstract void msgHereIsBill(Bill bill);
	
	/** Returns the name of the cashier */
	public abstract String getName();
}
