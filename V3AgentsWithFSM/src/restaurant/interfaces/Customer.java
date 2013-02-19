package restaurant.interfaces;

import restaurant.Bill;
import restaurant.Menu;

public interface Customer {
	public abstract void setHungry();
    /** Waiter sends this message so the customer knows to sit down 
     * @param waiter the waiter that sent the message
     * @param menu a reference to a menu */
    public abstract void msgFollowMeToTable(Waiter waiter, Menu menu);
    
    /** Waiter sends this message to take the customer's order */
    public abstract void msgDecided();
    
    /** Waiter sends this message to take the customer's order */
    public abstract void msgWhatWouldYouLike();
    
    /** Waiter sends this when the food is ready 
     * @param choice the food that is done cooking for the customer to eat */
    public abstract void msgHereIsYourFood(Bill bill);
    
    /** Timer sends this when the customer has finished eating */
    public abstract void msgDoneEating();

    /*New to v4.1*/
    public abstract void msgThankYouComeAgain();

    public abstract void msgNextTimePayTheDifference(double difference);

    /*Part 2 Non-Normative*/
    public abstract void msgPleaseReorder(Menu menu);

    /*Part 4.1 Non-Normative*/
    public abstract void msgSorryTablesAreOccupied(int wSize);
    
	/** Returns the name of the customer */
	public abstract String getName();
}
