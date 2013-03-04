package restaurant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RestaurantProducerConsumerMonitor {	  
	private int MAX = 20; // This is a maximum number of orders that the revolving stand can hold at any one time
	private List<CookOrder> orders = Collections.synchronizedList(new ArrayList<CookOrder>()); // A list that holds all of the orders that are pending for the cook

	synchronized public void add(CookOrder o) {
//		while (orders.size() == MAX) {
//			System.out.println("Revolving Stand: Full, waiting to add...");
//			try {
//				wait(500); // Wait for 500 milliseconds and then try again
//			}
//			catch (InterruptedException e) {}
//		}
		
		// Removed the code that Blocks a waiter if there is too much in the list -- the waiter may have something else to do
		
		addOrder(o);
		
		if (orders.size() == 1) {
			System.out.println("Revolving Stand: Not Empty");
//            notify(); 
			// Also do not need the notify() if no threads are forced to wait
		}
	}
	
	synchronized public CookOrder remove() {
		if (orders.size() == 0) { // Nothing to return, return null
			return null;
		}
		
		CookOrder c;
//		while (orders.size() == 0) {
//			System.out.println("Revolving Stand: Empty, waiting to remove...");
//			try {
//				wait(500); // Wait for 500 milliseconds and then try again
//			}
//			catch (InterruptedException e) {}
//		}
		
		// Removed the code that Blocks a cook if there is nothing in the list -- the cook may have something else to do
		
		c = removeOrder(); // Remove the top most order from the list
		
		if (orders.size() == MAX - 1) {
//			System.out.println("Revolving Stand: Not Full");
//            notify(); 
			// Also do not need the notify() if no threads are forced to wait
		}
		return c;
	}
	
	private void addOrder(CookOrder o) {
		orders.add(o);
	}
	
	private CookOrder removeOrder() {
		CookOrder o = orders.remove(0); // Remove first element
		return o;
	}
	
	public int getSize() {
		return orders.size();
	}
}
