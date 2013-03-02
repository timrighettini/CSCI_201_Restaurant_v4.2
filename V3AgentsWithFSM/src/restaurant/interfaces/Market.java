package restaurant.interfaces;

import java.util.Map;

public interface Market {
	public abstract void msgNeedFoodDelivered(Map<String, Integer> choices, Cook c, Cashier ca);		

	public abstract void msgHereIsCashierPayment(double money, String oID);
	
	/** Returns the name of the market */
	public abstract String getName();
}
