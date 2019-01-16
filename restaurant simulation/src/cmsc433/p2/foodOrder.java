package cmsc433.p2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class foodOrder {
	private LinkedList<Food> foods;
	private int orderNumber;
	private boolean ready;
	public foodOrder(LinkedList<Food> orderList,int OrderNumber) {
		foods=orderList;
		this.orderNumber=OrderNumber;
		ready=false;
	}
	
	public LinkedList<Food> getOrder(){
		return foods;
	}
	
	public int getOrderNumber() {
		return orderNumber;
	}
	
	public boolean isFinished() {
		return ready;
	}
	
	public void changeFinish() {
		ready=true;
	}
	
}
