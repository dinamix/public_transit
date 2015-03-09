package gasemissions;

import java.io.*;
import java.util.Scanner;

/*TEST CLASS
 *THIS IS A TEST CLASS USED TO MAKE THE EFTable OBJECT
 *IT IS LEFT HERE IN CASE FURTHER CHANGES NEED TO BE MADE
 *TO THE EF TABLES AND SO TESTING CAN CONTINUE THROUGH THIS CLASS 
 */

public class EFTableTest {
	private int grade;
	private String season;
	private int model_year;
	private String bus_type;
	private String road_type;
	private double[][] speed_passengers;
	
	public EFTableTest(File file) throws IOException {
		String[] filename = file.getName().split("_");
		grade = Integer.parseInt(filename[0]);
		season = filename[1];
		model_year = Integer.parseInt(filename[2]);
		bus_type = filename[3];
		road_type = filename[4];
		setSpeed_passengers(file);
	}
	
	//intake of speed and passengers
	//will need to create an array that intakes all of the speeds in first dimension
	//and then intakes passengers in second dimension
	//will interpolate automatically so that speed and number of passengers can be precise
	// so that when you get something it will only need to search for the object in O(n) time
	// and then the speed and passengers will already be specified as speed_passenger[speed][passenger]
	private void setSpeed_passengers(File file) throws IOException {
		Scanner in = new Scanner(file);
		in.useDelimiter(","); //delimiter here is comma and end of line |\\n
		
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
		System.out.println(length + " " + width);
		
		/***
		 * TEST
		 */
		//in.nextLine();
		//String line1 = in.nextLine();
		//System.out.println(line1);
		//String[] array2 = line1.split(",");
		//double[] testarray = convertArray(array2);
		/*
		 * TEST
		 ***/

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
		countwidth = splitline.length-1; //remove one for subject at beginning of row
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

	public String getBus_type() {
		return bus_type;
	}

	public String getRoad_type() {
		return road_type;
	}

	public double[][] getSpeed_passengers() {
		return speed_passengers;
	}

	public static void main(String[] args) throws IOException {
		File file = new File("EF//0_Summer_1983_Standard_Restricted.csv");
		EFTableTest test = new EFTableTest(file);
		//System.out.println(test.speed_passengers[72][15]);	
	}

}
