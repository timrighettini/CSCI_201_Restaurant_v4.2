/**
 * 
 */
package restaurant.test.Mock;

import agent.Agent;

/**
 * This is the base class for a mock agent. It only defines that an agent should
 * contain a name.
 * 
 * @author Sean Turner
 * 
 */
public class MockAgent extends Agent {
	private String name;
	
	public EventLog log = new EventLog(); // Will use this in my MockAgents 

	public MockAgent(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return this.getClass().getName() + ": " + name;
	}

	@Override
	protected boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		return false;
	}

}
