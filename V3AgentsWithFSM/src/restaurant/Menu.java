package restaurant;


public class Menu {

    public String choices[] = new String[]
	{ "Steak"  ,
	  "Chicken", 
	  "Salad"  , 
	  "Pizza"  };
    
    public double prices[] = new double [] {
    	15.99, // Steak Price
    	10.99, // Chicken Price
    	5.99, // Salad Price
    	8.99, // Pizza Price
    };
    
    public boolean[] unavailableItems = new boolean[] {
    	false,
    	false,
    	false,
    	false,
    };
    
    public Menu() {
    	// DO nothing
    }
    
    public Menu(String choice) { // This constructor will eliminate a choice from the menu
    	for (int i = 0; i < choices.length; i++) {
    		if (choices[i].equals(choice)) { // Remove the choice from this menu
    			unavailableItems[i] = true; // This item cannot be ordered by the customer
    		}
    	}
    }
}
    
