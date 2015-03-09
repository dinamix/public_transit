package gasemissions;

import java.io.*;
import java.util.Scanner;

/*
 * THIS CLASS CREATES AN OBJECT THAT CORRESPONDS TO A GIVEN EF TABLE
 */
public class EFTable {
	private int grade;
	private String season;
	private int model_year;
	private int bus_type;
	private String road_type;
	private double[][] speed_passengers; //this will give the emission given specified speed and passenger in that order
	
	public EFTable(File file) throws IOException {
		String[] filename = file.getName().split("_|.csv");
		grade = Integer.parseInt(filename[0]);
		season = filename[1];
		model_year = Integer.parseInt(filename[2]);
		bus_type = setBus_type(filename[4]);
		road_type = filename[3];
		setSpeed_passengers(file);
	}
	
	/*
	 * THIS FUNCTION INTAKES THE SPEED AND CORRESPINDG PASSENGER EMISSION
	 * WHEN A SPECIFIC EFTABLE OBJECT IS MADE
	 * AN EFTABLE OBJECT IS MADE GIVEN A FILE AND THEREFORE EACH OBJECT WILL CORRESPOND
	 * TO ITS OWN SEPARATE FILE
	 * THIS OBJECT WILL THEN BE USED TO COMPARE AN EMISSIONS OBJECT TO IT AND TO FETCH THE 
	 * CORRESPONDING EMISSIONS GIVEN THE SPECIFIED EMISSIONS OBJECT CRITERIA
	 * i.e. bus type, road type...
	 
	//intake of speed and passengers
	//create an array that intakes all of the speeds in first dimension
	//and then intakes passengers in second dimension
	//will interpolate automatically so that speed and number of passengers can be accessed to the 1
	//When you get something it will only need to search for the object in O(n) time
	//and then the speed and passengers will already be specified as speed_passenger[speed][passenger]
	 * 
	 */
	private void setSpeed_passengers(File file) throws IOException {
		Scanner in = new Scanner(file);
		in.useDelimiter(",|\\n"); //delimiter here is comma and end of line |\\n
		
		//Take in the number of passengers to use as a basis
		//NEED TO START FROM 1 IN ORDER TO NOT INTAKE THE SPEED SUBJECT COLUMN!!!
		String passengers = in.nextLine();		
		String[] array1 = passengers.split(",");
		double[] passengerarray = convertArrayToDouble(array1);

		//initiate array with proper width and length from file
		//multiply width by 5 in order to consider the interpolations till max
		int width = (findWidth(file)-1)*5+1;//passengers : removed one for subject in findWidth()

		int length = findLength(file);//speed : subject already removed in function findLength()
		
		//initiate array with proper size
		speed_passengers = new double[length][width];

		//input values from table into array
		//DOUBLEARRAY IS ONE BIGGER THAN SPEED_PASSENGERS SO ALWAYS KEEP IT ONE HIGHER
		for(int i = 0; i < length; i++) {
			//this will keep count of the passenger array and double array separately from speed_passengers
			int passengercount = 1;//this keeps it one higher by starting at 1
			
			//Intake one line at a time in order to facilitate interpolation between 2 values
			String line = in.nextLine();
			String[] linearray = line.split(",");
			double[] doublearray = convertArrayToDouble(linearray);;
			
			//loop will intake values in table and then linearly interpolate the next 4 values
			for(int j = 0; j < width; j += 5) {
				
				//this intakes the first value given in the data
				speed_passengers[i][j] = doublearray[passengercount];
				
				//this accounts for interpolating only till the second to last value
				if(j < width - 5) {
					//each of these interpolates using the linear interpolation function
					speed_passengers[i][j+1] = interpolate(passengerarray[passengercount],passengerarray[passengercount+1],j+1,doublearray[passengercount],doublearray[passengercount+1]);
					speed_passengers[i][j+2] = interpolate(passengerarray[passengercount],passengerarray[passengercount+1],j+2,doublearray[passengercount],doublearray[passengercount+1]);
					speed_passengers[i][j+3] = interpolate(passengerarray[passengercount],passengerarray[passengercount+1],j+3,doublearray[passengercount],doublearray[passengercount+1]);
					speed_passengers[i][j+4] = interpolate(passengerarray[passengercount],passengerarray[passengercount+1],j+4,doublearray[passengercount],doublearray[passengercount+1]);
				}
				passengercount++;
			}
			//System.out.println(speed_passengers[72][74]);
		}
		in.close();
	}
	
	private static int findLength(File file)  {
		int countlength = 0;
		try {
			Scanner in = new Scanner(file);
			in.nextLine();
			while(in.hasNextLine()) {
					countlength++;
			in.nextLine();
			}
			in.close();
		} catch(FileNotFoundException ex) {
			ex.printStackTrace();
		}
		return countlength;
	}
	
	private static int findWidth(File file) throws FileNotFoundException{
		int countwidth;
		Scanner in = new Scanner(file);
		String firstline = in.nextLine();
		String[] splitline = firstline.split(",");
		in.close();
		countwidth = splitline.length - 1; //remove one for subject at beginning of row
		return countwidth;
	}
	
	private static double[] convertArrayToDouble(String[] stringarray) {
		double[] doublearray = new double[stringarray.length];
		for(int i = 0; i < stringarray.length; i++) {
			doublearray[i] = Double.parseDouble(stringarray[i]);
		}
		return doublearray;
	}
	
	private static double interpolate(double startpass, double endpass, double interpass, double startE, double endE) {
		return startE + (endE - startE) * (interpass - startpass) / (endpass - startpass);
	}

	public int getGrade() {
		return grade;
	}

	public String getSeason() {
		return season;
	}
	
	public int getModel_year() {
		return model_year;
	}

	public int getBus_type() {
		return bus_type;
	}
	
	//somewhat unorthodox method to set bus_type but it works in this case
	public int setBus_type(String filename) {
		//Assumes standard bus
		int capacity = 75;
		//Else put it as articulated
		if (filename.equals("Articulated")) {
			capacity = 115;
		}
		return capacity;
	}

	public String getRoad_type() {
		return road_type;
	}

	public double[][] getSpeed_passengers() {
		return speed_passengers;
	}
	
	public double getSpeed_passengersAt(int speed, int passengers) {
		return speed_passengers[speed][passengers];
	}
	
	public static EFTable[] allFilesInDirectory(File directory) throws IOException {
		File[] filenames = directory.listFiles();
		EFTable[] datatables = new EFTable[filenames.length];
		for(int i = 0; i < filenames.length; i++) {
			datatables[i] = new EFTable(filenames[i]);
		}
		return datatables;
	}
	
	//MAIN FUCNTION USED FOR TESTING
	public static void main(String[] args) throws IOException {
		/*File directory = new File("EF");
		File[] files = directory.listFiles();
		File file = new File("EF//0_Summer_1983_Standard_Restricted.csv");
		EFTable test = new EFTable(files[0]);
		System.out.println(test.speed_passengers[15][0]);*/
		
	 	File directory2 = new File("EF_New");
		EFTable[] datatables = allFilesInDirectory(directory2);
		
		System.out.println(datatables[23].speed_passengers[3][75]);
		System.out.println(datatables.length);
	}

}
