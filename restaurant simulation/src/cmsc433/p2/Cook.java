package cmsc433.p2;

import java.awt.event.ItemEvent;
import java.util.LinkedHashMap;

/**
 * Cooks are simulation actors that have at least one field, a name.
 * When running, a cook attempts to retrieve outstanding orders placed
 * by Eaters and process them.
 */
public class Cook implements Runnable {
	private final String name;
	private boolean atWork;
	private LinkedHashMap<Integer, foodOrder> currentOrders;
	private LinkedHashMap<Food, Machine> cookUnits;
	/**
	 * You can feel free to modify this constructor.  It must
	 * take at least the name, but may take other parameters
	 * if you would find adding them useful. 
	 *
	 * @param: the name of the cook
	 */
	public Cook(String name,LinkedHashMap<Food, Machine> cookUnits,LinkedHashMap<Integer, foodOrder> currentOrder) {
		this.name = name;
		Simulation.logEvent(SimulationEvent.cookStarting(this));
		atWork=true;
		this.cookUnits=cookUnits;
		this.currentOrders=currentOrder;
		
	}

	public String toString() {
		return name;
	}

	/**
	 * This method executes as follows.  The cook tries to retrieve
	 * orders placed by Customers.  For each order, a List<Food>, the
	 * cook submits each Food item in the List to an appropriate
	 * Machine, by calling makeFood().  Once all machines have
	 * produced the desired Food, the order is complete, and the Customer
	 * is notified.  The cook can then go to process the next order.
	 * If during its execution the cook is interrupted (i.e., some
	 * other thread calls the interrupt() method on it, which could
	 * raise InterruptedException if the cook is blocking), then it
	 * terminates.
	 */
	public void run() {

		Simulation.logEvent(SimulationEvent.cookStarting(this));
		try {
			while(atWork) {
				//YOUR CODE GOES HERE...
				
				LinkedHashMap<Thread, Food> currentCooking=new LinkedHashMap<Thread,Food>();
				foodOrder temp=null;
				int ordernum=-1;
				synchronized (currentOrders) {
					while(currentOrders.size()==0) {
						currentOrders.wait();
					}
					ordernum=currentOrders.keySet().iterator().next();
					if(currentOrders.containsKey(ordernum)) {
						temp=currentOrders.get(ordernum);
						Simulation.logEvent(SimulationEvent.cookReceivedOrder(this, temp.getOrder(), temp.getOrderNumber()));
						currentOrders.remove(ordernum);
					}
				}
				if(temp!=null) {
					synchronized (temp) {
						for(Food i:temp.getOrder()) {
							
							currentCooking.put(cookUnits.get(i).makeFood(), i);
						
						}
						

						for(Thread i:currentCooking.keySet()) {
							i.join();
							Simulation.logEvent(SimulationEvent.cookStartedFood(this, currentCooking.get(i), temp.getOrderNumber()));
							
						}
						
						
						temp.changeFinish();
						temp.notifyAll();
						Simulation.logEvent(SimulationEvent.cookCompletedOrder(this, temp.getOrderNumber()));
						
					}
				}
				
				
				
			}
		}
		catch(InterruptedException e) {
			// This code assumes the provided code in the Simulation class
			// that interrupts each cook thread when all customers are done.
			// You might need to change this if you change how things are
			// done in the Simulation class.
			Simulation.logEvent(SimulationEvent.cookEnding(this));
			atWork=false;
		}
	}
}
