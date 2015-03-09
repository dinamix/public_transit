package ridership;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class GISpoints {
	private int stop_id;
	private int bus_line;
	private double stop_lat;
	private double stop_lon;
	 
	public GISpoints (String info) throws FileNotFoundException {
		String[] array = info.split(",");
		stop_id = Integer.parseInt(array[0]);
		bus_line = Integer.parseInt(array[1]);
	}
	
	public int getStop_id() {
		return stop_id;
	}
	public int getBus_line() {
		return bus_line;
	}
	
	public double getStop_lat() {
		return stop_lat;
	}

	public void setStop_lat(double stop_lat) {
		this.stop_lat = stop_lat;
	}

	public double getStop_lon() {
		return stop_lon;
	}

	public void setStop_lon(double stop_lon) {
		this.stop_lon = stop_lon;
	}

	//trying to make this function but problems arise when trying to change
	//to different object type names without explicitly writing them in the function
	public static ArrayList createArraylist(File filename) throws FileNotFoundException {
		Scanner in = createScannerComma(filename);
		ArrayList arraylist = new ArrayList();
		//in.nextLine();
		if(filename.equals(new File("inputs//NewGIS.csv"))) {
			while(in.hasNextLine()) {
				arraylist.add(new GISpoints(in.nextLine()));
			}
		}
		
		arraylist.trimToSize();
		in.close();
		return arraylist;
	}
	
	//create a bus object array given specified file GENERIC
	public static Object[] createArray(File file) throws IOException {
		ArrayList arraylist = createArraylist(file);
		Object[] busarray = convertArraylist(arraylist);
		return busarray;
	}
	
	//convert arraylist in order to make array GENERIC
	public static Object[] convertArraylist(ArrayList arraylist) {
		Object[] busarray = new GISpoints[arraylist.size()];
		
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
	
	public static void main(String[] args) throws IOException {
		File file = new File("inputs//NewGIS.csv");
		Object[] GISarray = createArray(file);
		
		GISpoints[] pointsarray = (GISpoints[])GISarray;
		
		System.out.println(pointsarray[0].getStop_id());
	}
}
