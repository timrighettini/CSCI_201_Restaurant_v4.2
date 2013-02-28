package restaurant;

import agent.Agent;

public class Bill {
	//Name: Bill
	//Description:  The class w/values allowing customers to pay for items � this is more like a struct.

	//Data:
	public double totalCost; // Amount for the bill;
	public String choice; // What was ordered
	public Agent agent; // Determines what type of agent made the order
	
	
	public Bill(double totalCost, String choice, Agent agent) {
		this.totalCost = totalCost;
		this.choice = choice;
		this.agent = agent;
	}
}
