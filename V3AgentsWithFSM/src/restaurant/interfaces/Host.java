package restaurant.interfaces;

public interface Host {
	   /** Customer sends this message to be added to the wait list 
     * @param customer customer that wants to be added */
    public abstract void msgIWantToEat(Customer customer);

    /** Waiter sends this message after the customer has left the table 
     * @param tableNum table identification number */
    public abstract void msgTableIsFree(int tableNum);
    
    /*Part 3 (Non-)Normative*/
    public abstract void msgMayITakeABreak(Waiter waiter);

    /*Part 4 Non-Normative*/
    public abstract void  msgThankYouIllWait(Customer c);

    public abstract void msgSorryIHaveToLeave(Customer c);
    
	/** Returns the name of the host */
	public abstract String getName();

}
