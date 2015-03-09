package gasemissions;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import ridership.*;

public class Emissions {
	//object variables, some will be used to determine emissions
	//all of these will show up on final file
	private int stop_id;
	private int bus_id; //this is bus line
	private String arrival_time;
	private double distance;
	private int stop_sequence_number;
	private int boarding;
	private int alighting;
	private int onbus;
	private int speed;
	private int bus_type; //value given in integer capacity of bus
	private int model_year;
	private int grade;
	private String season;
	private String road_type;
	private double emissions; //this could be emission factor taken from table
	private double totalemissions;
	//private double person_kms;
	
	//Constructor
	//will automatically take data from a BusInfo object array
	public Emissions(BusInfo bus) {
		stop_id = bus.getStop_id();
		bus_id = bus.getBus_id();
		arrival_time = bus.getArrival_time();
		distance = bus.getDistance();
		stop_sequence_number = bus.getStop_sequence_number();
		boarding = bus.getPeoplein();
		alighting = bus.getPeopleout();
		onbus = bus.getPeopleonbus();
		speed = bus.getSpeed();
		setBus_type(); //unorthodox way of setting bus type
		model_year = 0; //use generateYear() because same years need to be for each line
		grade = 0;
		season = "Summer"; //automatic for now
		road_type = "Unrestricted"; //automatic for now will generate once we can relate bus_id to road_type
		//person_kms = bus.getPerson_kms(); //will include after if necessary or could just compute by hand on excel
	}
	
	//This constructor will be used if there is already a file with all the data in it
	//not the emission data of course
	public Emissions(String emission) {
		String[] array = emission.split(",");
		stop_id = Integer.parseInt(array[0]);
		bus_id = Integer.parseInt(array[1]);
		arrival_time = array[2];
		distance = Double.parseDouble(array[3]);
		stop_sequence_number = Integer.parseInt(array[4]);
		boarding = Integer.parseInt(array[5]);
		alighting = Integer.parseInt(array[6]);
		onbus = Integer.parseInt(array[7]);
		speed = Integer.parseInt(array[8]);
		bus_type = Integer.parseInt(array[9]);
		model_year = Integer.parseInt(array[10]);
		grade = Integer.parseInt(array[11]);
		season = array[12];
		road_type = array[13];
	}

	public int getStop_id() {
		return stop_id;
	}

	public int getBus_id() {
		return bus_id;
	}
	
	public String getArrival_time() {
		return arrival_time;
	}

	public double getDistance() {
		return distance;
	}

	public int getStop_sequence_number() {
		return stop_sequence_number;
	}

	public int getBoarding() {
		return boarding;
	}

	public int getAlighting() {
		return alighting;
	}

	public int getOnbus() {
		return onbus;
	}

	public int getSpeed() {
		return speed;
	}

	public int getBus_type() {
		return bus_type;
	}
	
	//somewhat unorthodox method to set bus_type but it works in this case
	public void setBus_type() {
		if ( bus_id == 67 || bus_id == 69 || bus_id == 80 || bus_id == 121 || bus_id == 139 || bus_id == 165 || bus_id == 467 || bus_id == 535 ) {
			bus_type = 115;
		}
		else {
			bus_type = 75;
		}
	}

	public int getModel_year() {
		return model_year;
	}

	public int getGrade() {
		return grade;
	}

	public String getSeason() {
		return season;
	}
	
	public String getRoad_type() {
		return road_type;
	}

	public double getEmissions() {
		return emissions;
	}
	
	//This function will find the emissions of a specific Emissions object and add it to the emissions variable
	public static void generateEmissions(Emissions[] busarray) throws IOException {
		File directory2 = new File("EF_New");
		EFTable[] datatables = EFTable.allFilesInDirectory(directory2);
		double idleEF = 0; //used to keep track of idle emissions for later
		
		//this loop is for each bus object
		for(int i = 0; i < busarray.length; i++) {
			
			//this loop is to check through all the EF data table files
			for(int j = 0; j < datatables.length; j++) {
				
				/*
				 * THIS WAS LEFT HERE IN CASE FURTHER TESTING WAS NEEDED
				 * BECAUSE THERE WERE MANY BUGS WITH THIS PART OF THE CODE
				 */
				//System.out.println(i + " " + busarray[i].toString());
				//System.out.println(datatables[j].getRoad_type() + " " + datatables[j].getBus_type());
				//System.out.println(busarray[i].getSpeed() + " " + busarray[i].getOnbus());
				/*
				 * TEST
				 */
				
				//This is the scenario where the bus object matches the file name exactly
				if(busarray[i].getBus_type() == datatables[j].getBus_type() && busarray[i].getRoad_type().equals(datatables[j].getRoad_type())
						&& busarray[i].getSeason().equals(datatables[j].getSeason()) && busarray[i].getGrade() == datatables[j].getGrade()) {
					if(busarray[i].getModel_year() == datatables[j].getModel_year()) {
						busarray[i].emissions = datatables[j].getSpeed_passengersAt(busarray[i].getSpeed(),busarray[i].getOnbus());
						idleEF = datatables[j].getSpeed_passengersAt(0,0);
					}
					
					//this elseif statement would be used for interpolating between tables according to years
					//if the bus object does not match the file year exactly
					//We use the fact that there are 4 of each year files to our advantage here
					//Make sure we are less then 4 from the max and then compare to see if year is between the two
					else if(j < (datatables.length - 4) && busarray[i].getModel_year() > datatables[j].getModel_year()
							&& busarray[i].getModel_year() < datatables[j + 4].getModel_year()) {
						busarray[i].emissions = interpolate(datatables[j].getModel_year(),datatables[j+4].getModel_year(),busarray[i].getModel_year(),
												datatables[j].getSpeed_passengersAt(busarray[i].getSpeed(),busarray[i].getOnbus()),
												datatables[j+4].getSpeed_passengersAt(busarray[i].getSpeed(),busarray[i].getOnbus()));
						idleEF = datatables[j].getSpeed_passengersAt(0,0);
					}
				}
			}
			//Here now we multiply EF by the distance to get total moving emissions (moving emissions in grams/mile)
			busarray[i].totalemissions = (busarray[i].getEmissions() * busarray[i].getDistance());
			
			//Here we will add the idle emissions to the data (idle emissions in grams/hour)
			busarray[i].totalemissions += (idleEF * peopleTime(busarray[i].getBoarding(),busarray[i].getAlighting()));
		}
	}
	
	private static double interpolate(int startYear, int endYear, int interYear, double startEF, double endEF) {
		return startEF + (endEF - startEF) * (interYear - startYear) / (endYear - startYear);
	}
	
	//Here we calculate time it takes for people to board and alight
	//By taking either boarding or alighting being bigger we then take the larger one
	//and compute the time taken per person using a standard normal distribution
	private static double peopleTime(int boarding, int alighting) {
		//set up standard random normal variable
		Random normal = new Random();
		double standardnormal = 0;
		double totaltime = 0;
		
		//case where boarding is greater than alighting
		//Boarding mean is taken as 3s/person with same standard deviation
		if(boarding > alighting) {
			for(int i = 0; i < boarding; i++) {
				//use this loop to ensure that time per person is not less than 1 second
				//1 second is subject to change
				while(standardnormal <= 1) {
					standardnormal = (3 + 3*normal.nextGaussian());
				}
				//sum up the times of each person
				totaltime += standardnormal;
			}
		}
		
		//case where alighting greater than boarding
		//alighting mean is taken as 2s/person with same standard deviation
		else {
			for(int i = 0; i < alighting; i++) {
				//use this loop to ensure that time per person is not less than 0 second
				//0 seconds is subject to change
				while(standardnormal <= 0) {
					standardnormal = (2 + 2*normal.nextGaussian());
				}
				//sum up the times of each person
				totaltime += standardnormal;
			}
		}
		totaltime = totaltime/60.0; //this is to ensure that we have our units in hours
		return totaltime;
	}
	
	public String toString() {
		return stop_id + "," + bus_id + "," + arrival_time + "," + distance + "," + stop_sequence_number + "," + boarding 
				+ "," + alighting + "," + onbus + "," + speed + "," + bus_type + "," + model_year + "," 
				+ grade + "," + season + "," + road_type + "," + emissions + "," + totalemissions;
	}
	
	//trying to make this function but problems arise when trying to change
	//to different object type names without explicitly writing them in the function
	public static ArrayList<Object> createArraylist(File filename) throws FileNotFoundException {
		Scanner in = createScannerComma(filename);
		ArrayList<Object> arraylist = new ArrayList<Object>();
		in.nextLine();
			
		while(in.hasNextLine()) {
			arraylist.add(new Emissions(in.nextLine()));
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
		Object[] busarray = new Emissions[arraylist.size()];
		
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
	
	//This function will be used to generate bus model years based on the bus distribution
	//given by the STM
	//It needs to be a separate function because each bus line needs to have the same year and so
	//it must be generated after the array is made
	public static void generateYear(Emissions[] busarray) {
		for(int i = 0; i < busarray.length - 1; i++) {
			//Distribution for standard buses
			if(busarray[i].getBus_type() == 75) {
				busarray[i].randomStandardYear();
			}
			//Distribution for articulated buses
			else {
				busarray[i].randomArticulatedYear();
			}
			//this ensures that all buses on the same line will have same model year
			while(busarray[i].getBus_id() == busarray[i+1].getBus_id()) {
				busarray[i+1].model_year = busarray[i].getModel_year();
				//for end of file
				if(i >= busarray.length - 2) {
					break;
				}
				i++;
			}
		}
	}
	
	//These are probability functions made from scratch
	private void randomStandardYear() {
		while(model_year ==0) {
		Random random = new Random();
		int randomnum = (random.nextInt(100) + 1);

		if(randomnum <= 5) {
			model_year = 2001;
		}
		else if(randomnum > 5 && randomnum <= 20) {
			model_year = 2002;
		}
		else if(randomnum > 20 && randomnum <= 24) {
			model_year = 2003;
		}
		else if(randomnum > 25 && randomnum <= 30) {
			model_year = 2004;
		}
		else if(randomnum < 30 && randomnum <= 35) {
			model_year = 2005;
		}
		else if(randomnum > 35 && randomnum <= 40) {
			model_year = 2006;
		}
		else if(randomnum > 40 && randomnum <= 45) {
			model_year = 2007;
		}
		else if(randomnum > 45 && randomnum <= 55) {
			model_year = 2008;
		}
		else if(randomnum > 55 && randomnum <= 65) {
			model_year = 2009;
		}
		else if(randomnum > 65 && randomnum <= 83) {
			model_year = 2010;
		}
		else if(randomnum > 83 && randomnum <= 100) {
			model_year = 2011;
		}
		}
	}
	
	private void randomArticulatedYear() {
		while(model_year == 0) {
		Random random = new Random();
		int randomnum = (random.nextInt(100) + 1);

		if(randomnum <= 23) {
			model_year = 2009;
		}
		else if(randomnum > 23 && randomnum <= 55) {
			model_year = 2010;
		}
		else if(randomnum > 55 && randomnum <= 83) {
			model_year = 2011;
		}
		else if(randomnum > 83 && randomnum <= 100) {
			model_year = 2013;
		}
		}
	}
	
	//SET UP TO GENERATE EMISSIONS FILE
	public static void main(String[] args) throws IOException {
		double time = System.currentTimeMillis();
		File file = new File("inputs//distance_all_FINAL.csv");
		Object[] array = BusInfo.createArray(file);
		BusInfo[] busarray = (BusInfo[])array;
		
		BusInfo.generateTime(busarray);
		
		for(int i = 0; i < busarray.length; i++) {
			busarray[i].calculateSpeed();
		}
		
		//this will be the array that includes all data except for the actual emissions
		Emissions[] Earray = new Emissions[busarray.length];
				
		for(int i = 0; i < Earray.length; i++) {
			Earray[i] = new Emissions(busarray[i]);
		}
		
		//this will get random model years according to STM distribution
		Emissions.generateYear(Earray);
		
		Emissions.generateEmissions(Earray);
		
		//from here we start making the emissions file
		File emissiontest = new File("outputs//EF_all.csv");
		PrintWriter out = new PrintWriter(emissiontest);
				
		//this will print out the header for the file
		out.println("stop_id,bus_id,arrival_time,distance,stop_sequence_number,boarding,alighting,onbus,speed,bus_type"
				+ ",model_year,grade,season,road_type,EF,total_emissions");
		for(int i = 0; i < Earray.length; i++) {
		//this will print out each line of data using the overrided toString function
			out.println(Earray[i].toString());
		}
		out.close();
		
		System.out.println("Time taken: " + (System.currentTimeMillis() - time)/1000 + " seconds");
	}
}
