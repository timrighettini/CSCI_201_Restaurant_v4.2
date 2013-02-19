package restaurant.interfaces;

import agent.Agent;
import java.util.*;

import restaurant.Bill;

public interface Cook {

	/**
	 * Message from a waiter giving the cook a new order.
	 * 
	 * @param waiter
	 *            waiter that the order belongs to
	 * @param tableNum
	 *            identification number for the table
	 * @param choice
	 *            type of food to be cooked
	 */
	public abstract void msgHereIsAnOrder(Waiter waiter, int tableNum,
			String choice);

   /*Part 2 Normative*/
    public abstract void msgHereIsFoodDelivery(Map<String, Integer> items);

    public abstract void msgSorryWeCannotFulfillOrder(String item);

    public abstract void msgHereIsYourTrackingInformation(long orderTime, int deliveryTime, Map<String, Integer> items);
    
	/** Returns the name of the cook */
	public abstract String getName();
}