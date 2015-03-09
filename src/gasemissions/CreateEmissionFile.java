package gasemissions;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import ridership.*;

/*
 * This class can be used to run all of the code in general
 * but I originally just ran each separate piece through its
 * own main function for legibility
 */

public class CreateEmissionFile {
	
	public static void generateEmissionFile() throws IOException {
		//section used to generate new files for emissions later
		File file = new File("inputs//EF_test//ridership_test2.csv");
		
		Object[] array = BusInfo.createArray(file);
		
		BusInfo[] busarray = (BusInfo[])array;
		
		BusInfo.generateTime(busarray);
		
		for(int i = 0; i < busarray.length; i++) {
			busarray[i].calculateSpeed();
		}
		
		//this will be the array that includes all data except for the actual emissions
		Emissions[] Earray = new Emissions[busarray.length];
		
		//this will get random model years according to STM distribution
		Emissions.generateYear(Earray);
		
		for(int i = 0; i < Earray.length; i++) {
			Earray[i] = new Emissions(busarray[i]);
		}
		
		Emissions.generateEmissions(Earray);
		
		//from here we start making the emissions file
		File emissiontest = new File("inputs//EF_test//emissionssampleyear.csv");
		PrintWriter out = new PrintWriter(emissiontest);
		
		//this will print out the header for the file
		out.println("stop_id,bus_id,arrival_time,distance,stop_sequence_number,boarding,alighting,onbus,speed,bus_type"
				+ ",model_year,grade,season,road_type,emissions");
		for(int i = 0; i < Earray.length; i++) {
		//this will print out each line of data using the overrided toString function
			out.println(Earray[i].toString());
		}
		out.close();
	}
	
	public static void main(String[] args) throws IOException {
		generateEmissionFile();
	}
}
