package cmsc433.p2;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.omg.CORBA.PRIVATE_MEMBER;

import cmsc433.p2.Machine.MachineType;

/**
 * Simulation is the main class used to run the simulation.  You may
 * add any fields (static or instance) or any methods you wish.
 */
public class Simulation {
	// List to track simulation events during simulation
	public static List<SimulationEvent> events;  

	/**
	 * Used by other classes in the simulation to log events
	 * @param event
	 */
	public static void logEvent(SimulationEvent event) {
		events.add(event);
		System.out.println(event);
	}

	/**
	 * 	Function responsible for performing the simulation. Returns a List of 
	 *  SimulationEvent objects, constructed any way you see fit. This List will
	 *  be validated by a call to Validate.validateSimulation. This method is
	 *  called from Simulation.main(). We should be able to test your code by 
	 *  only calling runSimulation.
	 *  
	 *  Parameters:
	 *	@param numCustomers the number of customers wanting to enter Ratsie's
	 *	@param numCooks the number of cooks in the simulation
	 *	@param numTables the number of tables in Ratsie's (i.e. Ratsie's capacity)
	 *	@param machineCapacity the capacity of all machines in Ratsie's
	 *  @param randomOrders a flag say whether or not to give each customer a random order
	 *
	 */
	public static List<SimulationEvent> runSimulation(
			int numCustomers,
			int numCooks,
			int numTables, 
			int machineCapacity,
			boolean randomOrders
			) {

		// This method's signature MUST NOT CHANGE.  


		// We are providing this events list object for you.  
		// It is the ONLY PLACE where a concurrent collection object is 
		// allowed to be used.
		events = Collections.synchronizedList(new ArrayList<SimulationEvent>());




		// Start the simulation
		logEvent(SimulationEvent.startSimulation(numCustomers,
				numCooks,
				numTables,
				machineCapacity));



		// Set things up you might need
		

		// Start up machines



		

		// Build the customers.
		LinkedHashMap<Integer, foodOrder> foodOrders= new LinkedHashMap<Integer, foodOrder> ();

		
		ArrayList<String> tables=new ArrayList<String>();
		Thread[] customers = new Thread[numCustomers];
		LinkedList<Food> order;
		if (!randomOrders) {
			order = new LinkedList<Food>();
			order.add(FoodType.wings);
			order.add(FoodType.pizza);
			order.add(FoodType.sub);
			order.add(FoodType.soda);
			for(int i = 0; i < customers.length; i++) {
				customers[i] = new Thread(
						new Customer("Customer " + (i), order,numTables,foodOrders,tables)
						);
			}
		}
		else {
			for(int i = 0; i < customers.length; i++) {
				Random rnd = new Random();
				int wingsCount = rnd.nextInt(4);
				int pizzaCount = rnd.nextInt(4);
				int subCount = rnd.nextInt(4);
				int sodaCount = rnd.nextInt(4);
				order = new LinkedList<Food>();
				for (int b = 0; b < wingsCount; b++) {
					order.add(FoodType.wings);
				}
				for (int f = 0; f < pizzaCount; f++) {
					order.add(FoodType.pizza);
				}
				for (int f = 0; f < subCount; f++) {
					order.add(FoodType.sub);
				}
				for (int c = 0; c < sodaCount; c++) {
					order.add(FoodType.soda);
				}
				customers[i] = new Thread(
						new Customer("Customer " + (i+1),order,numTables,foodOrders,tables)
				);
			}
		}
		
		// Let cooks in
	
		Thread[] cooks = new Thread[numCooks];
		LinkedHashMap<Food, Machine> orderUnits=new LinkedHashMap<Food, Machine>();
		orderUnits.put(FoodType.pizza, new Machine(MachineType.oven, FoodType.pizza, machineCapacity));
		orderUnits.put(FoodType.wings, new Machine(MachineType.fryer, FoodType.wings, machineCapacity));
		orderUnits.put(FoodType.sub, new Machine(MachineType.grillPress, FoodType.sub, machineCapacity));
		orderUnits.put(FoodType.soda, new Machine(MachineType.fountain, FoodType.soda, machineCapacity));
		for(int i=0;i<numCooks;i++) {
			cooks[i]=new Thread(new Cook("cook"+i, orderUnits, foodOrders));
		}
		
		
		// Now "let the customers know the shop is open" by
		//    starting them running in their own thread.
		for(int i = 0; i < customers.length; i++) {
			customers[i].start();
			//NOTE: Starting the customer does NOT mean they get to go
			//      right into the shop.  There has to be a table for
			//      them.  The Customer class' run method has many jobs
			//      to do - one of these is waiting for an available
			//      table...
		}
		
		for(Thread i:cooks) {
			i.start();
		}
		try {
			// Wait for customers to finish
			//   -- you need to add some code here...
		
			
			for(Thread i:customers) {
				i.join();
			}
			
		
			
			
			
			// Then send cooks home...
			// The easiest way to do this might be the following, where
			// we interrupt their threads.  There are other approaches
			// though, so you can change this if you want to.
			for(int i = 0; i < cooks.length; i++)
				cooks[i].interrupt();
			for(int i = 0; i < cooks.length; i++)
				cooks[i].join();

		}
		catch(InterruptedException e) {
			System.out.println("Simulation thread interrupted.");
		}

		// Shut down machines
		for(Machine i:orderUnits.values()) {
			logEvent(SimulationEvent.machineEnding(i));
		}




		// Done with simulation		
		logEvent(SimulationEvent.endSimulation());

		return events;
	}

	/**
	 * Entry point for the simulation.
	 *
	 * @param args the command-line arguments for the simulation.  There
	 * should be exactly four arguments: the first is the number of customers,
	 * the second is the number of cooks, the third is the number of tables
	 * in Ratsie's, and the fourth is the number of items each machine
	 * can make at the same time.  
	 */
	public static void main(String args[]) throws InterruptedException {
		// Parameters to the simulation
		/*
		if (args.length != 4) {
			System.err.println("usage: java Simulation <#customers> <#cooks> <#tables> <capacity> <randomorders");
			System.exit(1);
		}
		int numCustomers = new Integer(args[0]).intValue();
		int numCooks = new Integer(args[1]).intValue();
		int numTables = new Integer(args[2]).intValue();
		int machineCapacity = new Integer(args[3]).intValue();
		boolean randomOrders = new Boolean(args[4]);
		 */
		int numCustomers = 10;
		int numCooks =1;
		int numTables = 5;
		int machineCapacity = 4;
		boolean randomOrders = false;


		// Run the simulation and then 
		//   feed the result into the method to validate simulation.
		System.out.println("Did it work? " + 
				Validate.validateSimulation(
						runSimulation(
								numCustomers, numCooks, 
								numTables, machineCapacity,
								randomOrders
								)
						)
				);
	}

}
