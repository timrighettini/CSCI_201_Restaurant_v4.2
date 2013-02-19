/**
 * 
 */
package restaurant.test.Mock;

import java.util.Map;

import restaurant.interfaces.Cook;
import restaurant.interfaces.Waiter;

/**
 * @author Sean Turner
 * 
 */
public class MockCook extends MockAgent implements Cook {

	public MockCook(String name) {
		super(name);
	}

	public EventLog log = new EventLog();

	public void msgHereIsAnOrder(Waiter waiter, int tableNum, String choice) {
		log.add(new LoggedEvent(
				"Received message msgHereIsAnOrder from waiter "
						+ waiter.toString() + " for table number " + tableNum
						+ " to cook item " + choice + "."));

	}

	@Override
	public void msgHereIsFoodDelivery(Map<String, Integer> items) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgSorryWeCannotFulfillOrder(String item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHereIsYourTrackingInformation(long orderTime,
			int deliveryTime, Map<String, Integer> items) {
		// TODO Auto-generated method stub
		
	}

}
