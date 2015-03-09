package ridership;
import java.io.*;
import java.util.*;

//Object for the stops.txt file in order to take in long and lat
public class Stops {
	private int stop_id;
	private int stop_code;
	private String stop_name;
	private double stop_lat;
	private double stop_lon;
	private String stop_url;
	private int wheelchair_boarding;
	
	public Stops(String businfo){
		String[] busarray = businfo.split(",");
		stop_id = Integer.parseInt(busarray[0]);
		stop_code = Integer.parseInt(busarray[1]);
		stop_name = busarray[2];
		stop_lat = Double.parseDouble(busarray[3]);
		stop_lon = Double.parseDouble(busarray[4]);
		stop_url = busarray[5];
		wheelchair_boarding = Integer.parseInt(busarray[6]);
	}
	
	public int getStop_id() {
		return stop_id;
	}

	public int getStop_code() {
		return stop_code;
	}

	public String getStop_name() {
		return stop_name;
	}

	public double getStop_lat() {
		return stop_lat;
	}
	
	public double getStop_lon() {
		return stop_lon;
	}

	public String getStop_url() {
		return stop_url;
	}

	public int getWheelchair_boarding() {
		return wheelchair_boarding;
	}
	
	//using a stops object, you can print out to a specified filename
	//and the true in the FileWriter allows to continue appending to the file
	//First function just writes name and lon and lat to file
	//while second function will skip a line after writing the same thing
	public void writeLonLatTo(File filename) {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename,true)));
			out.print(stop_name + ",");
			out.print(stop_lon + ",");
			out.print(stop_lat + ",");
			out.close();
		}
		catch (IOException e) {
			System.out.println("there's problems with the file, might be open or something"
					+ "quitting now");
			System.exit(0);
		}
	}
	
	public void writeLonLatToln(File filename) {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename,true)));
			out.print(stop_lon + ",");
			out.println(stop_lat);
			out.close();
		}
		catch (IOException e) {
			System.out.println("there's problems with the file, might be open or something"
					+ "quitting now");
			System.exit(0);
		}
	}
	
	//trying to make this function but problems arise when trying to change
	//to different object type names without explicitly writing them in the function
	public static ArrayList createArraylist(File filename) throws FileNotFoundException {
		Scanner in = createScannerComma(filename);
		ArrayList arraylist = new ArrayList();
		in.nextLine();
		if(filename.equals(new File("inputs//stm_data//stops.txt"))) {
			while(in.hasNextLine()) {
				arraylist.add(new Stops(in.nextLine()));
			}
		}
		else if(filename.equals(new File("inputs//NewGIS.csv"))) {
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
	private static Object[] convertArraylist(ArrayList arraylist) {
		Object[] busarray = new Stops[arraylist.size()];
		
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
		File file = BusTest.createFile("inputs//stm_data//stops.txt");
		Scanner in = createScannerComma(file);
		in.nextLine(); //skip the first line of stop.txt
		in.close();
		
/*		String test = in.nextLine();
		
		Object newobject = new Stops(test);
		System.out.println(((Stops)newobject).getStop_id());*/
		
		//if using generic programming, then explicit casting is required to use Stops variables
		Object[] arraytest = createArray(file);
		
		//Can use object array for any file format
		//need to explicitly cast to object type that you want afterwards
		Stops[] stoparray = (Stops[]) arraytest;
		System.out.println(stoparray.length);
		
		File fileout = new File("outputs//GISPoints.csv");
		
		for(int i = 0; i < stoparray.length; i++) {
			stoparray[i].writeLonLatTo(fileout);
			if(i >= stoparray.length - 1) {
				stoparray[0].writeLonLatTo(fileout);
			}
			else {
				stoparray[i+1].writeLonLatToln(fileout);
			}
		}
	}
}
