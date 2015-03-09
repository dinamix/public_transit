package gasemissions;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import ridership.*;

/*
 * THIS CLASS IS USED TO CHECK FOR ERRORS IN THE LARGE DISTANCE FILE
 */
//747 IS NOT ACTUALLY AN ERROR
//ALTERED TWO OF THE 197 FILES BY HAND BECAUSE THEY MAKE NO SENSE
public class CheckDistances {
	private int rownumber;
	private int bus_id;
	private int stop_id;
	
	public CheckDistances(int rownumber, int bus_id, int stop_id) {
		this.rownumber = rownumber;
		this.bus_id = bus_id;
		this.stop_id = stop_id;
	}
	
	public int getRownumber() {
		return rownumber;
	}

	public int getBus_id() {
		return bus_id;
	}

	public int getStop_id() {
		return stop_id;
	}
	
	public String toString() {
		return rownumber + "," + bus_id + "," + stop_id;
	}
	
	//this function will find if there are more than two 0 distances in a row for a bus line
	//if so it will output the bus line and stop id and row number to the console
	//this will be used to find any errors in the distances so that they can be checked
	//and potentially recomputed on ArcGIS
	public static CheckDistances[] findWrongLines(BusInfo[] businfo) {
		int currentbusid = 0;
		int errorcount = 0;
		
		CheckDistances[] errors = new CheckDistances[10000];
		for(int i = 0; i < businfo.length - 1; i++) {
			if(businfo[i].getDistance() == 0 && businfo[i+1].getDistance() == 0
					&& businfo[i].getBus_id() == businfo[i+1].getBus_id()) {
				if(currentbusid != businfo[i].getBus_id()) {
					errors[errorcount] = new CheckDistances(i+2,businfo[i].getBus_id(), businfo[i].getStop_id());
					errorcount++;
				}
				currentbusid = businfo[i].getBus_id();
			}
		}
		errors = trimArray(errors);
		return errors;
		//now we want to trim the array down to the proper length
	}
	
	private static CheckDistances[] trimArray(CheckDistances[] errors) {
		int arraylength = 0;
		for(int i = 0; i < errors.length; i++) {
			if(errors[i] == null) {
				arraylength = i;
				break;
			}
			else if (i >= errors.length - 1) {
				arraylength = errors.length;
			}
		}
		CheckDistances[] newerrors = new CheckDistances[arraylength];
		for(int i = 0; i < newerrors.length; i++) {
			newerrors[i] = errors[i];
		}
		
		return newerrors;
	}
	
	public static CheckDistances[] findUnique(CheckDistances[] errors) {
		CheckDistances[] uniqueerrors = new CheckDistances[errors.length];
		int uniquecount = 0;
		boolean foundcopy = false;
		for(int i = 0; i < errors.length; i++) {
			foundcopy = false;
			if(i > 0) {
			for(int j = i - 1; j >= 0; j--) {
				//System.out.println(j + " " + errors[i].getBus_id() + " " + errors[i].getStop_id()
				//		+ " " + errors[j].getBus_id() + " " + errors[j].getStop_id());
				if(errors[i].getBus_id() == errors[j].getBus_id() 
					&& errors[i].getStop_id() == errors[j].getStop_id()) {
					foundcopy = true;
					break;
				}
			}
			}
			//System.out.println(foundcopy);
			if(foundcopy == false) {
				uniqueerrors[uniquecount] = errors[i];
				uniquecount++;
			}
		}
		uniqueerrors = trimArray(uniqueerrors);
		return uniqueerrors;
	}
	
	//this checks to make sure that the line file names match up with the distance files so they are all there
 	public static void checkLine() {
		//Intake busline files
		File buslines = new File("outputs//GISPoints//Bus_Line_NOREPEAT");
		File[] buslinesarray = buslines.listFiles();
		
		//Intake bus distance files
		File busdistances = new File("inputs//Bus_Line_Distances");
		File[] busdistancesarray = busdistances.listFiles();
		
		/*Now we compare the names to find any discrepancies
		for(int i = 0; i < busdistancesarray.length; i++) {
			String name = busdistancesarray[i].getName();
			name = "Line" + name.replaceAll(".txt",".csv");
			//System.out.println("Line" + busdistancesarray[0].getName() + " " + name);
			//compare names in this loop
			for(int j = 0; j < buslinesarray.length; j++) {
				if(name.equals(buslinesarray[j].getName())) {
					break;
				}
				
				//if reach end and no match then print name to console
				if(j == buslinesarray.length - 1) {
					System.out.println(name);
				}
			}
		}*/
		
		for(int i = 0; i < buslinesarray.length; i++) {
			String name = busdistancesarray[i].getName();
			name = "Line" + name.replaceAll(".txt",".csv");
			
			if(name.equals(buslinesarray[i].getName())) {
				System.out.println("good " + i);
				continue;
			}
			
			else {
				System.out.println(name + " " + buslinesarray[i].getName());
			}
		}
	}
	
	//SET UP TO FIND WRONG DISTANCE MATCHUPS IN RIDERSHIP BUS LINES
	public static void main(String[] args) throws IOException {
		File file = new File("inputs//EF_test//distance_all_FINAL.csv");
		Object[] array = BusInfo.createArray(file);
		BusInfo[] busarray = (BusInfo[])array;
		
		CheckDistances[] errors = findWrongLines(busarray);
		
		CheckDistances[] uniqueerrors = findUnique(errors);
		
		for(int i = 0; i < uniqueerrors.length; i++) {
			System.out.println((i+1) + "," + uniqueerrors[i].toString());
		}
	}
}
