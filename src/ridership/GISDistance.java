package ridership;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * THIS CLASS IS USED TO GET DISTANCES FROM GIS AND THEN ADD THEM
 * TO A SPECIFIED RIDERSHIP FILE
 * IT WILL BE USED WITH THE BUSINFO CLASS
 */

public class GISDistance {
	private int stop_id;
	private double cummudistance; //cummulative distances given by GIS
	private double distancenext; //distance to next stop to conform to code
	
	public GISDistance(String line) {
		String[] linearray = line.split(",");
		stop_id = Integer.parseInt(linearray[1].replace("\"",""));
		cummudistance = Double.parseDouble(linearray[15]);
	}

	public int getStop_id() {
		return stop_id;
	}

	public double getCummudistance() {
		return cummudistance;
	}
	
	public double getDistancenext() {
		return distancenext;
	}

	//trying to make this function but problems arise when trying to change
	//to different object type names without explicitly writing them in the function
	public static ArrayList<Object> createArraylist(File filename) throws FileNotFoundException {
		Scanner in = createScannerComma(filename);
		ArrayList<Object> arraylist = new ArrayList<Object>();
		in.nextLine();
			
		while(in.hasNextLine()) {
			arraylist.add(new GISDistance(in.nextLine()));
		}
		
		arraylist.trimToSize();
		in.close();
		return arraylist;
	}
	
	//create a bus object array given specified file GENERIC
	public static Object[] createArray(File file) throws IOException {
		ArrayList<Object> arraylist = createArraylist(file);
		Object[] busarray = convertArraylist(arraylist);
		return busarray;
	}
	
	//convert arraylist in order to make array GENERIC
	public static Object[] convertArraylist(ArrayList<Object> arraylist) {
		Object[] busarray = new GISDistance[arraylist.size()];
		
		arraylist.toArray(busarray);
		
		return busarray;
	}
	
	//creates a scanner object that is comma delimited for various STM file to 
	//avoid any kind of whitespace delimiting issues
	public static Scanner createScannerComma(File filename) throws FileNotFoundException {
		Scanner in = new Scanner(filename);
		in.useDelimiter(",");
		return in;
	}
	
	//This functions will calculate the non-cummulative distances
	//and will also place these distances in the previous stop_id to conform to coding standard
	public static void fixDistance(GISDistance[] array) {
		for(int i = 0; i < array.length; i++) {
			if(i < array.length - 1) {
				array[i].distancenext = (array[i+1].cummudistance - array[i].cummudistance)*0.000621371; //meter-miles conversion
			}
			else {
				array[i].distancenext = 0;
			}
		}
	}

	public static void main(String[] args) throws IOException {
		File test = new File("inputs//Bus_Line_Distances//10-108481.txt");
		Object[] testarray = createArray(test);
		GISDistance[] array = (GISDistance[])testarray;
		fixDistance(array);
		System.out.println(array[32].stop_id + " " + array[32].distancenext + " " + array.length);
	}

}
