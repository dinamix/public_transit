package ridership;

import java.io.*;
import java.util.ArrayList;

public class BusLineFiles {
	
	//this function matches longitude and latitude with specified GISpoints object compared to stops objects
	//mainly used with GPS points from the STM stops.txt file and cross-references with the
	//NewGIS points objects
	public static GISpoints[] matchLonLat(GISpoints[] GIS, Stops[] stops) {
		for(int i = 0; i < GIS.length; i++) { 
			for(int j = 0; j < stops.length; j++) {
				if(GIS[i].getStop_id() == stops[j].getStop_id()) {
					GIS[i].setStop_lon(stops[j].getStop_lon());
					GIS[i].setStop_lat(stops[j].getStop_lat());
				}
			}
		}
		System.out.println(GIS.length);
		return GIS;
	}
	
	//this creates an array that gets separated at each bus line given a starting point
	//private because it is made specifically for the creatBusLineFiles() function
	private static GISpoints[] createBusLineArray(GISpoints[] pointsarray,int start) {
		ArrayList<GISpoints> arraylist = new ArrayList<GISpoints>();
		int count = 0;
		for(int i = start; i < pointsarray.length; i++) {
			if(i + 1 > pointsarray.length - 1) {
				break;
			}
			if(pointsarray[i].getBus_line() == pointsarray[i+1].getBus_line()) {
				arraylist.add(pointsarray[i]);
				continue;
			}
			arraylist.add(pointsarray[i]); //add the extra one when they are not equal at the bottom of each list
			break;
		}
		arraylist.trimToSize();
		Object[] array = GISpoints.convertArraylist(arraylist); //taken from the GISpoints class
		GISpoints[] gisarray = (GISpoints[])array;
		return gisarray;
	}
	
	//this function may go into infinite loop at the end but files are still good
	//it uses the array created in createBusLineArray() to generate files with that array
	//and also includes headers and object_id
	public static void createBusLineFiles(GISpoints[] pointsarray) throws IOException {
		int i = 0;
		
		//while loop to ensure that only goes till end of all points
		while(i < pointsarray.length) {
			
			//create one array for each bus line
			GISpoints[] array = createBusLineArray(pointsarray,i);
			System.out.println(array.length); //so you can tell its running properly and for infinite loop
			
			//this creates a file for each bus line in the specified folder with different names
			//names are (line(#))-(stop_id of first stop in line)-(number of array to be used as
			//object_id but subtract one because array starts at zero)
			
			//NOTE: with noted option, you get all files including repeats
			File file = new File("outputs//GISPoints//Bus_Line_REPEAT//Line" + pointsarray[i].getBus_line() + 
									"-" + pointsarray[i].getStop_id() + "-(" + i + ").csv"); //depending on file you want generated
			
			//new print writer created each time for each separate file
			PrintWriter out = new PrintWriter(file);
			
			//header for the file so that ArcGIS works properly
			out.println("Object_ID,Stop_id,Bus_Line,Ox,Oy");
			
			//copies the created buslinearray into its own file
			for(int j = 0; j < array.length; j++) {
				System.out.println("Bus line is " + array[0].getBus_line());
				out.print((j+1) + ",");
				out.print(array[j].getStop_id() + ",");
				out.print(array[j].getBus_line() + ",");
				out.print(array[j].getStop_lon() + ",");
				out.println(array[j].getStop_lat());
			}
			System.out.println("out of loop"); //for infinite loop check
			out.close(); //need this or else printwriter will not copy next array to new file
			
			//adds length so that next array length can be calculated
			if(i < pointsarray.length - 1) {
				i += array.length;
			}
			else {
				i++; //this is for the last iteration so it can jump out of while loop
			}
		}
	}
	
	private static int getArraySize(GISpoints[] pointsarray, int start) {
		int newstart = start;
		int size = 0;
		while(pointsarray[start].getBus_line() == pointsarray[start+1].getBus_line()) {
			size++;
		}
		return size;
	}
	
	public static void main(String[] args) throws IOException {
		//initialize GIS array
		File fileGIS = new File("inputs//NewGIS.csv");
		Object[] GISarray = GISpoints.createArray(fileGIS);
		GISpoints[] pointsarray = (GISpoints[])GISarray;

		//initialize stops array
		File filestops = new File("inputs//stm_data//stops.txt");
		Object[] arraytest = Stops.createArray(filestops);
		Stops[] stoparray = (Stops[])arraytest;
		//match up both arrays
		pointsarray = matchLonLat(pointsarray, stoparray);
		
		createBusLineFiles(pointsarray);
		
	}
}
