package ridership;
import java.util.*;
import java.io.*;

/*
 * This is the bust test class connected to other classes in the same package
 * It will produce a ridership model for all the stops given the sorted.csv file
 * It can be run several times to generate different ridership data 
 * The distances can then be copy/pasted to the column after stop line factor
 * and then Emissions.java can be run to generate emissions for each stop
 */

public class BusTest {
	
	//Data Fields
	private String list_number;
	private String unknown;
	private String specific_route1;
	private int specific_route2;
	private int bus_id;
	private String arrival_time;
	private String departure_time;
	private int stop_id;
	private int stop_sequence_number;
	private int peoplein;
	private int peopleout;
	private int peopleonbus;
	private int peoplewait;
	private double stopLineFactor;
	private static int busCapacity;
	public static double[] ridership = new double[3];
	public static String[][] stops = new String[8737][137];
	public static int[][] stopLinesAM = new int[8738][176];
	public static int[][] stopLinesOPD = new int[8738][170];
	public static int[][] stopLinesPM = new int[8738][174];
	public static int[][] stopLinesOPN = new int[8738][197];
	public static String[][] passWaiting = new String[8738][196];
	
	//Constructor
	public BusTest(String businfo) {
		String[] array = businfo.split(",");
		list_number = array[0];
		unknown = array[1];
		specific_route1 = array[2];
		specific_route2 = Integer.parseInt(array[3]);
		bus_id = Integer.parseInt(array[4]);
		arrival_time = array[5];
		departure_time = array[6];
		stop_id = Integer.parseInt(array[7]);
		stop_sequence_number = Integer.parseInt(array[8]);
		peoplein = 0;
		peopleout = 0;
		peopleonbus = 0;
		peoplewait = 0;
		stopLineFactor = 0;
	}
	
	//Accessors
	public String getList_number() {
		return list_number;	
	}
	
	public String getUnknown() {
		return unknown;	
	}
	
	public String getSpecific_route1() {
		return specific_route1;	
	}
	
	public int getSpecific_route2() {
		return specific_route2;	
	}
	
	public int getBus_id() {
		return bus_id;	
	}
	
	public String getArrival_time() {
		return arrival_time;	
	}
	
	public String getDeparture_time() {
		return departure_time;	
	}
	
	public int getStop_id() {
		return stop_id;	
	}
	
	public int getStop_sequence_number() {
		return stop_sequence_number;	
	}
	
	//creates a file given file name useful for multifile use because no name change
	public static File createFile(String filename) {
		File file = new File(filename);	
		
		return file;
	}
	
	//create an arraylist given specified file
	public static ArrayList<BusTest> createArraylist(File file) throws IOException{
		Scanner in = new Scanner(file);
		ArrayList<BusTest> arraylist = new ArrayList<BusTest>();
		
		while(in.hasNextLine()) {
			arraylist.add(new BusTest(in.nextLine()));
		}
		
		arraylist.trimToSize();
		in.close();
		return arraylist;
	}
	
	//create a bus object array given specified file
	public static BusTest[] createArray(File file) throws IOException {
		ArrayList<BusTest> arraylist = createArraylist(file);
		
		BusTest[] busarray = convertArraylist(arraylist);
		
		return busarray;
	}
	
	//convert arraylist in order to make array
	private static BusTest[] convertArraylist(ArrayList<BusTest> arraylist) {
		BusTest[] busarray = new BusTest[arraylist.size()];
		
		arraylist.toArray(busarray);
		
		return busarray;
	}
	
	// the stop input file containing all land-use, transport supply and ridership numbers is read into the stop array
	private static void stopsFile() throws FileNotFoundException {
		File inputFile = new File("inputs//stops.txt");
		Scanner input = new Scanner (inputFile);
		String row = input.nextLine();
		
		for ( int i = 2 ; i <= 8738 ; i++ ) {
			row = input.nextLine();
			String[] rowComponents = row.split("\t");
			for ( int j = 1 ; j <= 137 ; j++ ) {
				stops[i-2][j-1] = rowComponents[j-1];
			}
		}
		input.close();
	}
	
	// the four files linking bus lines to stop IDs are then read into four distinct arrays
	private static void stopLineFiles() throws FileNotFoundException {
		File inputFileAM = new File("inputs//stopArrayAM.csv");
		Scanner inputAM = new Scanner(inputFileAM);
		for ( int i = 0 ; i < 8738 ; i++ ) {
			String rowAM = inputAM.nextLine();
			String[] rowAMcomponents = rowAM.split(",");
			for ( int j = 0 ; j < 176 ; j++ ) {
				stopLinesAM[i][j] = Integer.parseInt(rowAMcomponents[j]);
			}
		}
		inputAM.close();
		
		File inputFileOPD = new File("inputs//stopArrayOPD.csv");
		Scanner inputOPD = new Scanner(inputFileOPD);
		for ( int i = 0 ; i < 8738 ; i++ ) {
			String rowOPD = inputOPD.nextLine();
			String[] rowOPDcomponents = rowOPD.split(",");
			for ( int j = 0 ; j < 170 ; j++ ) {
				stopLinesOPD[i][j] = Integer.parseInt(rowOPDcomponents[j]);
			}
		}
		inputOPD.close();
		
		File inputFilePM = new File("inputs//stopArrayPM.csv");
		Scanner inputPM = new Scanner(inputFilePM);
		for ( int i = 0 ; i < 8738 ; i++ ) {
			String rowPM = inputPM.nextLine();
			String[] rowPMcomponents = rowPM.split(",");
			for ( int j = 0 ; j < 174 ; j++ ) {
				stopLinesPM[i][j] = Integer.parseInt(rowPMcomponents[j]);
			}
		}
		inputPM.close();
		
		File inputFileOPN = new File("inputs//stopArrayOPN.csv");
		Scanner inputOPN = new Scanner(inputFileOPN);
		for ( int i = 0 ; i < 8738 ; i++ ) {
			String rowOPN = inputOPN.nextLine();
			String[] rowOPNcomponents = rowOPN.split(",");
			for ( int j = 0 ; j < 196 ; j++ ) {
				stopLinesOPN[i][j] = Integer.parseInt(rowOPNcomponents[j]);
				if ( i == 0 || j == 0 ) {
					passWaiting[i][j] = rowOPNcomponents[j];
				}
			}
		}
		inputOPN.close();
	}
	
	// method adds to the passengers waiting array in the case where the bus is at capacity
	private static void addToPassWaiting(int bus_id, String arrival_time, int stop_id, int peoplewait) {
		for ( int i = 0 ; i < 8738 ; i++ ) {
			if ( stop_id == Integer.parseInt(passWaiting[i][0]) ) {
				for ( int j = 0 ; j < 196 ; j++ ) {
					if ( bus_id == Integer.parseInt(passWaiting[0][j]) ) {
						if ( passWaiting[i][j] != null ) {
							String[] waitingComponents = passWaiting[i][j].split(":");
							peoplewait = peoplewait + Integer.parseInt(waitingComponents[0]);
							passWaiting[i][j] = peoplewait + ":" + arrival_time;
						} else {
							passWaiting[i][j] = peoplewait + ":" + arrival_time;
						}
						break;
					}
				}
				break;
			}
		}
	}
	
	// method includes the passengers waiting in the number of boardings
	private static int minusFromPassWaiting(int bus_id, String arrival_time, int stop_id) {
		int passengersWaiting = 0;
		
		for ( int i = 0 ; i < 8738 ; i++ ) {
			if ( stop_id == Integer.parseInt(passWaiting[i][0]) ) {
				for ( int j = 0 ; j < 196 ; j++ ) {
					if ( bus_id == Integer.parseInt(passWaiting[0][j]) ) {
						if ( passWaiting[i][j] != null ) {
							String[] waitingComponents = passWaiting[i][j].split(":");
							int waitingHour = Integer.parseInt(waitingComponents[1]);
							int waitingMinute = Integer.parseInt(waitingComponents[2]);
							
							int waitingTime = waitingHour * 100 + waitingMinute;
							
							String[] timeComponents = arrival_time.split(":");
							int stopHour = Integer.parseInt(timeComponents[0]);
							int stopMinute = Integer.parseInt(timeComponents[1]);
							
							int stopTime = stopHour * 100 + stopMinute;
							
							if ( stopTime >= waitingTime ) {
								passengersWaiting = Integer.parseInt(waitingComponents[0]);
								passWaiting[i][j] = null;
							} else {
								System.out.println(waitingComponents[0] + " passengers waiting for bus " + bus_id + " at stop number " + stop_id + " at " + arrival_time + " could not board.");
							}
						}
						break;
					}
				}
				break;
			}
		}
		return passengersWaiting;
	}
	
	//relates line to bus capacity because bus_id is bus line
	public static int busCapacity(int bus_id) {
		int busCapacity = 75;
			if ( bus_id == 67 || bus_id == 69 || bus_id == 80 || bus_id == 121 || bus_id == 139 || bus_id == 165 || bus_id == 467 || bus_id == 535 )
				busCapacity = 115;
		return busCapacity;
	}
	
	//method used to compute total number of people on bus and put directly into object array
	public static void computeTotalPeople(BusTest[] array) throws FileNotFoundException {
		
		for ( int i = 0; i < array.length; i++ ) {
			ridership = Ridership.ridership(array[i].getStop_id(), array[i].getArrival_time(), stops, array[i].getBus_id(), stopLinesAM, stopLinesOPD, stopLinesPM, stopLinesOPN);
			busCapacity = busCapacity(array[i].getBus_id());
			array[i].stopLineFactor = ridership[2];
			if(i == 0 || array[i].getStop_sequence_number() != 
						array[i-1].getStop_sequence_number() + 1) {
				array[i].peoplein = (int) ridership[0] + minusFromPassWaiting(array[i].getBus_id(), array[i].getDeparture_time(), array[i].getStop_id());	
				array[i].peopleout = 0;
				if ( array[i].peoplein <= busCapacity ) {
					array[i].peopleonbus = array[i].peoplein;			
				} else {
					array[i].peoplewait = array[i].peoplein - busCapacity;
					addToPassWaiting(array[i].getBus_id(), array[i].getDeparture_time(), array[i].getStop_id(), array[i].peoplewait);
					array[i].peoplein = busCapacity;
					array[i].peopleonbus = busCapacity;
				}
			} else if ( i == array.length - 1 || array[i].getStop_sequence_number() != array[i+1].getStop_sequence_number() - 1) {
				array[i].peoplein = 0;
				array[i].peopleout = array[i-1].peopleonbus;
				array[i].peopleonbus = 0;
			} else {
				array[i].peoplein = (int) ridership[0] + minusFromPassWaiting(array[i].getBus_id(), array[i].getDeparture_time(), array[i].getStop_id());
				array[i].peopleout = (int) ridership[1];
				if (array[i].peopleout > array[i-1].peopleonbus) {
					array[i].peopleout = array[i-1].peopleonbus;
				}
				
				array[i].peopleonbus = array[i-1].peopleonbus - array[i].peopleout + array[i].peoplein;	
				
				if ( array[i].peopleonbus > busCapacity ) {
					array[i].peoplewait = array[i].peopleonbus - busCapacity;
					addToPassWaiting(array[i].getBus_id(), array[i].getDeparture_time(), array[i].getStop_id(), array[i].peoplewait);
					array[i].peoplein = array[i].peoplein - array[i].peoplewait;
					array[i].peopleonbus = busCapacity;
				}
			}	
		}		
	}
	
	//print people on bus to a csv file
	//@SuppressWarnings("static-access")
	public static void printPeople(BusTest[] array) throws IOException{
		File file = createFile("outputs//ridership_all.csv");
		PrintWriter out = new PrintWriter(file);
		
		out.println("List Number,Unknown,Specific Route 1,Specific Route 2, Bus ID,Arrival Time,Departure Time,Stop ID,Stop Sequence Number,Boarding,Alighting,Total On Bus,Waiting,Stop Line Factor");
		
		for(int i = 0; i < array.length; i++) {
			out.println(array[i].getList_number() + "," + array[i].getUnknown() + ","
							+ array[i].getSpecific_route1() + "," +
							array[i].getSpecific_route2() + "," + 
							+ array[i].getBus_id() + "," + array[i].getArrival_time() + ","
							+ array[i].getDeparture_time() + "," + array[i].getStop_id() + ","
							+ array[i].getStop_sequence_number() + ","
							+ array[i].peoplein + "," + array[i].peopleout + 
							"," + array[i].peopleonbus + "," + array[i].peoplewait + "," + array[i].stopLineFactor);
		}	
		out.close();	
	}
	
	private static void printPassWaiting() throws FileNotFoundException {
		File file = new File("outputs//passengersWaiting.csv");
		PrintWriter out = new PrintWriter(file);
		
		for( int i = 0 ; i < 8738 ; i++) {
			for ( int j = 0 ; j < 196 ; j++ ) {
				out.print(passWaiting[i][j] + ",");
			}			
			out.print("\n");
		}	
		out.close();
	}
	
	private static boolean newid(int newid, int oldid){
		if (newid == oldid) 
			return false;
		else 
			return true;
	}
	
	//This is a test function that performs that same thing as the Object file
	//It currently just takes bus ids from a bustest array without duplicating any ids
	//To generate capacities along side bus ids refer to the capacity file/object
	public static int[] bus_idTocapacity(BusTest[] busarray){
		int[] capacityarray = new int[busarray.length];
		
		//let's initiate the array to have a starting point
		capacityarray[0] = busarray[0].bus_id;
		
		int j = 1; //let's use j as a counter for capacity array
		
		//this loop will take in bus_ids only if they are new with function newid (previous function)
		for(int i = 1; i < capacityarray.length; i++){
			if (newid(busarray[i].bus_id,busarray[i-1].bus_id)){
				capacityarray[j] = busarray[i].bus_id;
				j++;
			}
		}
		
		//let's trim the array to save some space
		int[] trimmedarray = new int[j];
		
		System.arraycopy(capacityarray, 0, trimmedarray, 0, j);
		
		System.out.println("trimmedarray is size " + trimmedarray.length + " compared to regular " + capacityarray.length);
		
		return trimmedarray;
	}
	
	//test main function
	public static void main(String[] args) throws IOException{
		long startTime, endTime, runTime;
		
		startTime = System.currentTimeMillis();
		
		File file = createFile("inputs//sorted.csv");
		
		BusTest[] testarray = createArray(file);
		
		stopsFile();
		
		stopLineFiles();
		
		computeTotalPeople(testarray);
		
		printPeople(testarray);
		
		printPassWaiting();
		
		endTime = System.currentTimeMillis();
		runTime = endTime - startTime;
		System.out.println("The program run time was: " + (runTime / 1000) + " seconds.");
	}
}