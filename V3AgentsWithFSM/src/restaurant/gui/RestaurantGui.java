package restaurant.gui;

import restaurant.CustomerAgent;
import restaurant.MarketAgent;
import restaurant.WaiterAgent;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.File;


/** Main GUI class.
 * Contains the main frame and subsequent panels */
public class RestaurantGui extends JFrame implements ActionListener{
   
    private final int WINDOWX = 650;
    private final int WINDOWY = 450;

    private RestaurantPanel restPanel = new RestaurantPanel(this);
    private JPanel infoPanel = new JPanel();
    private JLabel infoLabel = new JLabel(
    "<html><pre><i>(Click on a customer/waiter/market)</i></pre></html>");
    private JCheckBox stateCB = new JCheckBox();
	private JButton addTable = new JButton("Add Table");

    private Object currentPerson;
    
    // New GUI variables for v4.1
    private JPanel customerPanel = new JPanel();
    private JLabel moneyLabel = new JLabel(" Total $:   ");
    private JTextField customerMoneyTF = new JTextField(5);
    private JLabel customerMoneyOwedLabelA = new JLabel("Money Owed: ");
    private JLabel customerMoneyOwedLabelB = new JLabel("0.00");
    private JButton payOffButton = new JButton("Pay Debt");
    private JCheckBox onlyPayFullyCB = new JCheckBox("Pay Fully");
    private JCheckBox willWaitCB = new JCheckBox("Will Wait");
    
    private JPanel marketPanel = new JPanel();
    private ArrayList<JLabel> marketLabels = new ArrayList<JLabel>();
    private ArrayList<JTextField> marketTFs = new ArrayList<JTextField>();
    
    updateGUI cUpdater = new updateGUI(); // Has a timer method to help show the correct values for a customer    
    private javax.swing.Timer updateTimer = new javax.swing.Timer(100, cUpdater);

    /** Constructor for RestaurantGui class.
     * Sets up all the gui components. */
    public RestaurantGui(){

	super("Restaurant Application");

	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setBounds(50,50, WINDOWX, WINDOWY);

	getContentPane().setLayout(new BoxLayout((Container)getContentPane(),BoxLayout.Y_AXIS));

	Dimension rest = new Dimension(WINDOWX, (int)(WINDOWY*.5));
	Dimension info = new Dimension(WINDOWX, (int)(WINDOWY*.25));
	restPanel.setPreferredSize(rest);
	restPanel.setMinimumSize(rest);
	restPanel.setMaximumSize(rest);
	infoPanel.setPreferredSize(info);
	infoPanel.setMinimumSize(info);
	infoPanel.setMaximumSize(info);
	infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));

	stateCB.setVisible(false);
	stateCB.addActionListener(this);

	infoPanel.setLayout(new GridLayout(1,5, 30,0));
	infoPanel.add(infoLabel);
	infoPanel.add(stateCB);
	
	// New Functionality
	// Set up the customerPanel
	customerPanel.setLayout(new BoxLayout(customerPanel,BoxLayout.X_AXIS));
	customerPanel.add(moneyLabel);
	customerPanel.add(customerMoneyTF);
	customerPanel.add(Box.createRigidArea(new Dimension(10, 0)));
	customerPanel.add(customerMoneyOwedLabelA);
	customerPanel.add(customerMoneyOwedLabelB);
	customerPanel.add(Box.createRigidArea(new Dimension(10, 0)));
	customerPanel.add(payOffButton);
	customerPanel.add(onlyPayFullyCB);
	customerPanel.add(willWaitCB);
	
	// Set Sizes for Components
	customerMoneyTF.setPreferredSize(new Dimension(100, 20));
	customerMoneyTF.setMinimumSize(new Dimension(100, 20));
	customerMoneyTF.setMaximumSize(new Dimension(100, 20));
	customerMoneyTF.setHorizontalAlignment(SwingConstants.CENTER);
	
	customerMoneyOwedLabelB.setPreferredSize(new Dimension(75, 20));
	customerMoneyOwedLabelB.setMinimumSize(new Dimension(75, 20));
	customerMoneyOwedLabelB.setMaximumSize(new Dimension(75, 20));
	
	// Set Alignment
	customerMoneyOwedLabelB.setHorizontalAlignment(SwingConstants.CENTER);
	
	// Set up the marketPanel
	marketPanel.setLayout(new BoxLayout(marketPanel,BoxLayout.X_AXIS));
	marketLabels.add(new JLabel("Steak: "));
	marketLabels.add(new JLabel("Chikn: "));
	marketLabels.add(new JLabel("Pizza: "));
	marketLabels.add(new JLabel("Salad: "));
	marketTFs.add(new JTextField(10));
	marketTFs.get(marketTFs.size() - 1).setPreferredSize(new Dimension(75, 20));
	marketTFs.get(marketTFs.size() - 1).setMaximumSize(new Dimension(75, 20));
	marketTFs.get(marketTFs.size() - 1).setMinimumSize(new Dimension(75, 20));
	marketTFs.get(marketTFs.size() - 1).setHorizontalAlignment(SwingConstants.CENTER);
	marketTFs.add(new JTextField(10));
	marketTFs.get(marketTFs.size() - 1).setPreferredSize(new Dimension(75, 20));
	marketTFs.get(marketTFs.size() - 1).setMaximumSize(new Dimension(75, 20));
	marketTFs.get(marketTFs.size() - 1).setMinimumSize(new Dimension(75, 20));
	marketTFs.get(marketTFs.size() - 1).setHorizontalAlignment(SwingConstants.CENTER);
	marketTFs.add(new JTextField(10));
	marketTFs.get(marketTFs.size() - 1).setPreferredSize(new Dimension(75, 20));
	marketTFs.get(marketTFs.size() - 1).setMaximumSize(new Dimension(75, 20));
	marketTFs.get(marketTFs.size() - 1).setMinimumSize(new Dimension(75, 20));
	marketTFs.get(marketTFs.size() - 1).setHorizontalAlignment(SwingConstants.CENTER);
	marketTFs.add(new JTextField(10));
	marketTFs.get(marketTFs.size() - 1).setPreferredSize(new Dimension(75, 20));
	marketTFs.get(marketTFs.size() - 1).setMaximumSize(new Dimension(75, 20));
	marketTFs.get(marketTFs.size() - 1).setMinimumSize(new Dimension(75, 20));
	marketTFs.get(marketTFs.size() - 1).setHorizontalAlignment(SwingConstants.CENTER);
	
	// Add everything into the panel
	for (int i = 0; i < marketTFs.size(); i++) {
		marketPanel.add(marketLabels.get(i));
		marketPanel.add(marketTFs.get(i));
	}
	
	/*
	 *    private JPanel marketPanel = new JPanel();
    private ArrayList<JLabel> marketLabels = new ArrayList<JLabel>();
    private ArrayList<JTextField> marketTFs = new ArrayList<JTextField>();
	 */
	
	getContentPane().add(restPanel);
	getContentPane().add(addTable);
	getContentPane().add(infoPanel);
	// Add new panels
	getContentPane().add(customerPanel);
	getContentPane().add(marketPanel);
	
	customerPanel.setVisible(false); // Set my panel to be NOT visible until a customer is clicked on
	marketPanel.setVisible(false); // Only set this panel to visible when a market is clicked
	updateTimer.start(); // Set the updating timer
	
	addTable.addActionListener(this);
    }


    /** This function takes the given customer or waiter object and 
     * changes the information panel to hold that person's info.
     * @param person customer or waiter object */
    public void updateInfoPanel(Object person){
	stateCB.setVisible(true);
	currentPerson = person;
	
	if(person instanceof CustomerAgent){
	    CustomerAgent customer = (CustomerAgent) person;
	    stateCB.setText("Hungry?");
	    stateCB.setSelected(customer.isHungry());
	    stateCB.setEnabled(!customer.isHungry());
	    infoLabel.setText(
	    "<html><pre>     Name: " + customer.getName() + " </pre></html>");
	    customerPanel.setVisible(true); // Set my panel to be visible to the customer
	    marketPanel.setVisible(false);

	}else if(person instanceof WaiterAgent){
	    WaiterAgent waiter = (WaiterAgent) person;
	    stateCB.setText("On Break?");
	    stateCB.setSelected(waiter.isOnBreak());
	    stateCB.setEnabled(true);
	    infoLabel.setText(
	    "<html><pre>     Name: " + waiter.getName() + " </html>");
	    customerPanel.setVisible(false); // Set my panel to be NOT visible to the waiter
	    marketPanel.setVisible(false);
	}
	
	else if(person instanceof MarketAgent){
		MarketAgent market = (MarketAgent) person;
	    stateCB.setText("Check Market Inventory Below");
	    stateCB.setSelected(false);
	    stateCB.setEnabled(false);
	    infoLabel.setText(
	    "<html><pre>     Name: " + market.getName() + " </html>");
	    customerPanel.setVisible(false); // Set my panel to be NOT visible to the market
	    marketPanel.setVisible(true);
	}	

	infoPanel.validate();
    }

    /** Action listener method that reacts to the checkbox being clicked */
    public void actionPerformed(ActionEvent e){

	if(e.getSource() == stateCB){
	    if(currentPerson instanceof CustomerAgent){
		CustomerAgent c = (CustomerAgent) currentPerson;
		c.setHungry();
		stateCB.setEnabled(false);

	    }else if(currentPerson instanceof WaiterAgent){
		WaiterAgent w = (WaiterAgent) currentPerson;
		w.setBreakStatus(stateCB.isSelected());
	    }
		System.out.println("Check Box Clicked!");
	}
	else if (e.getSource() == addTable)
	{
		try {
			System.out.println("[Gautam] Add Table!");
			//String XPos = JOptionPane.showInputDialog("Please enter X Position: ");
			//String YPos = JOptionPane.showInputDialog("Please enter Y Position: ");
			//String size = JOptionPane.showInputDialog("Please enter Size: ");
			//restPanel.addTable(10, 5, 1);
			//restPanel.addTable(Integer.valueOf(YPos).intValue(), Integer.valueOf(XPos).intValue(), Integer.valueOf(size).intValue());
			restPanel.addTable();
		}
		catch(Exception ex) {
			System.out.println("Unexpected exception caught in during setup:"+ ex);
		}
	}
	    
    }

    /** Message sent from a customer agent to enable that customer's 
     * "I'm hungery" checkbox.
     * @param c reference to the customer */
    public void setCustomerEnabled(CustomerAgent c){
	if(currentPerson instanceof CustomerAgent){
	    CustomerAgent cust = (CustomerAgent) currentPerson;
	    if(c.equals(cust)){
		stateCB.setEnabled(true);
		stateCB.setSelected(false);
	    }
	}
    }
	
	
    /** Main routine to get gui started */
    public static void main(String[] args){
	RestaurantGui gui = new RestaurantGui();
	gui.setVisible(true);
	gui.setResizable(false);
    }
    
    private class updateGUI implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			// Do any other actions here
			
			// Update all customer boxes/fields to the most updated values
			
		}
    	
    }
}
