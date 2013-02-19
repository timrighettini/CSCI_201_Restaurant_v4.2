package restaurant.test;

import junit.framework.TestCase;

public class testTestTwo extends TestCase {
	public void testOne(){
		assertTrue(true);
	}
	
	public void testTwo() {
		assertFalse("This is false", false);
	}
}
