package cmsc433.p2;

import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Customers are simulation actors that have two fields: a name, and a list
 * of Food items that constitute the Customer's order.  When running, an
 * customer attempts to enter the Ratsie's (only successful if the
 * Ratsie's has a free table), place its order, and then leave the
 * Ratsie's when the order is complete.
 */
public class Customer implements Runnable {
	//JUST ONE SET OF IDEAS ON HOW TO SET THINGS UP...
	private final String name;
	private final LinkedList<Food> order;
	private final int orderNum;    
	
	private static int runningCounter = 0;
	
	private int maxTables;
	
	private LinkedHashMap<Integer, foodOrder> orderList;
	private ArrayList<String> currentTable;

	/**
	 * You can feel free modify this constructor.  It must take at
	 * least the name and order but may take other parameters if you
	 * would find adding them useful.
	 */
	public Customer(String name, LinkedList<Food> order,
			int maxTables,LinkedHashMap<Integer, foodOrder> orderList,ArrayList<String> currentTable) {
		this.name = name;
		this.order =  order;
		this.orderNum = ++runningCounter;
		
		Simulation.logEvent(SimulationEvent.customerStarting(this));
		this.maxTables=maxTables;
		this.orderList=orderList;
		this.currentTable=currentTable;
		
	}

	public String toString() {
		return name;
	}

	/** 
	 * This method defines what an Customer does: The customer attempts to
	 * enter the Ratsie's (only successful when the Ratsie's has a
	 * free table), place its order, and then leave the Ratsie's
	 * when the order is complete.
	 */
	public void run() {
		foodOrder temp=null;
		synchronized (currentTable) {
			while (maxTables<=currentTable.size()) {
				try {
					currentTable.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			Simulation.logEvent(SimulationEvent.customerEnteredRatsies(this));
			currentTable.add(name);
			temp=new foodOrder(order,orderNum);
			
			
			
		}
		synchronized (orderList) {
			Simulation.logEvent(SimulationEvent.customerPlacedOrder(this, order, orderNum));
			orderList.put(orderNum, temp);
			orderList.notifyAll();	
		}
		
		synchronized (temp) {
			while(temp.isFinished()==false) {
				try {
					temp.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Simulation.logEvent(SimulationEvent.customerReceivedOrder(this, order, orderNum));
		}
		
		synchronized (currentTable) {
			Simulation.logEvent(SimulationEvent.customerLeavingRatsies(this));
			currentTable.remove(name);
			currentTable.notifyAll();
		}
		
		
	}
}
