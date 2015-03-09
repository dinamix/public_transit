package ridership;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public abstract class VariableArray {
	//trying to make this function but problems arise when trying to change
	//to different object type names without explicitly writing them in the function
	public static ArrayList<Object> createArraylist(File filename) throws FileNotFoundException {
		Scanner in = createScannerComma(filename);
		ArrayList<Object> arraylist = new ArrayList<Object>();
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
		ArrayList<Object> arraylist = createArraylist(file);
		Object[] busarray = convertArraylist(arraylist);
		return busarray;
	}
	
	//convert arraylist in order to make array GENERIC
	public static Object[] convertArraylist(ArrayList<Object> arraylist) {
		Object[] busarray = new Object[arraylist.size()];
		
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
}
