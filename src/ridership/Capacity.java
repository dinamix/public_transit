package ridership;

import java.io.*;

public class Capacity {
	private int bus_id;
	private int capacity;
	
	//Constructor
	public Capacity(int bus_id, int capacity){
		this.bus_id = bus_id;
		this.capacity = capacity;
	}
	
	//accessors
	public int getBus_id(){
		return bus_id;
	}
	
	public int getCapacity(){
		return capacity;
	}
	
	//create a capacity object from a bustest object directly
	public static Capacity createFrom(BusTest bus){
		//the buscapacity function is taken from the BusTest file
		//which relates bus id to bus capacity
		int newcapacity = BusTest.busCapacity(bus.getBus_id()); 
		Capacity forthisid = new Capacity(bus.getBus_id(),newcapacity);
		return forthisid; 
	}
	
	//create an array of Capacity objects from a bustest array
	public static Capacity[] createarrayFrom(BusTest[] busarray){
		Capacity[] capacityarray = new Capacity[busarray.length];
		capacityarray[0] = createFrom(busarray[0]); //initiate array to ease the newid requirements later
		
		//lets use a separate variable to keep track of the capacity array
		int j = 1;
		
		//now we'll use the createFrom method to create capacity objects from the bus array
		//and then put them into the capacityarray for easy access
		for (int i = 1; i < busarray.length; i++){
			if (newid(busarray[i].getBus_id(),busarray[i-1].getBus_id())) {
				capacityarray[j] = createFrom(busarray[i]);
				j++;
			}
		}
		
		//let's trim the array to save some space
		Capacity[] trimmedarray = new Capacity[j];
				
		System.arraycopy(capacityarray, 0, trimmedarray, 0, j);
		
		return trimmedarray;
	}
	
	//function built for the previous createarrayFrom function to check
	//if id is new compared to the old one in the array
	private static boolean newid(int newid, int oldid){
		if (newid == oldid) 
			return false;
		else 
			return true;
	}
	
	//override the toString method if needed to print out bus id with specified capacity
	public String toString(){
		String printing = "Bus id is " + bus_id + 
							"\nCapacity is " + capacity;
		return printing;
	}
	
	public static void main(String[] args) throws IOException{
		File file = BusTest.createFile("inputs//sorted_sample_long.csv");
		
		BusTest[] testarray = BusTest.createArray(file);
		
		Capacity[] test = createarrayFrom(testarray);
		
		System.out.println(test[455]);
		System.out.println(test.length);
	}
}
