package restaurant.interfaces;

import restaurant.layoutGUI.*;

public interface Waiter {
	public abstract  void msgSitCustomerAtTable(Customer customer, int tableNum);

		    /** Customer sends this when they are ready.
		     * @param customer customer who is ready to order.
		     */
	public abstract  void msgImReadyToOrder(Customer customer);

    /** Customer sends this when they have decided what they want to eat 
     * @param customer customer who has decided their choice
     * @param choice the food item that the customer chose */
    public abstract  void msgHereIsMyChoice(Customer customer, String choice);

    /** Cook sends this when the order is ready.
     * @param tableNum identification number of table whose food is ready
     * @param f is the guiFood object */
    public abstract  void msgOrderIsReady(int tableNum, Food f);

    /** Customer sends this when they are done eating.
     * @param customer customer who is leaving the restaurant. */
    public abstract  void msgDoneEatingAndLeaving(Customer customer);

    /** Sent from GUI to control breaks 
     * @param state true when the waiter should go on break and 
     *              false when the waiter should go off break
     *              Is the name onBreak right? What should it be?*/
    public abstract void setBreakStatus(boolean state);

    /*Part 2 Non-Normative*/
    public abstract void msgOutOfThisItem(String choice, int table);

    /*Part 3 (Non-)Normative*/
    public abstract void msgGuiButtonPressed();

    public abstract void msgYesAfterYourCustomersFinish();

    public abstract void msgNoItIsTooBusy();
    
	/** Returns the name of the waiter */
	public abstract String getName();
}
