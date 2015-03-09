package ridership;
import java.io.FileNotFoundException;
import java.util.Random;

public class Ridership {
	
	private static double board;
	private static double alight;
	public static String[][] stops = new String[8738][137];
	static int i, j = 0, stopHour, stopMinute, stopTime, lineID, stopID, currentLine, total;
	static double b_value, a_value, b_prob_k1, b_prob_k2, b_prob_k3, b_prob_k4, a_prob_k1, a_prob_k2, a_prob_k3, a_prob_k4, stopLineFactor, hourFactor;
	static String stopCategory, stopTimePeriod = "";		
	
	public static double[] ridership(int stopID, String time, String[][] stops, int lineID, int[][] stopLinesAM, int[][] stopLinesOPD, int[][] stopLinesPM, int[][] stopLinesOPN) throws FileNotFoundException {
		
		String[] timeComponents = time.split(":", 0);
		stopHour = Integer.parseInt(timeComponents[0]);
		stopMinute = Integer.parseInt(timeComponents[1]);
		
		stopTime = stopHour * 100 + stopMinute;
		
		// The stop time is then associated with one of the four time periods 
		if ( stopTime >= 630 && stopTime <= 930 ) {
			stopTimePeriod = "AM";
		} else if ( stopTime >= 1530 && stopTime <= 1830 ) {
			stopTimePeriod = "PM";
		} else if ( stopTime > 930 && stopTime < 1530 ) {
			stopTimePeriod = "OPD";
		} else if ( stopTime < 630 || stopTime > 1830 ) {
			stopTimePeriod = "OPN";
		}
				
		for ( j = 0 ; j <= 8736 ; j++ ) {
			
			long stopIDcomparison = Long.parseLong(stops[j][0]);
			// Land-use, transport infrastructure, and transit supply variables used by the ridership models
			double headwayAM = Double.parseDouble(stops[j][133]);
			double headwayOPD = Double.parseDouble(stops[j][135]);
			double headwayPM = Double.parseDouble(stops[j][134]);
			double headwayOPN = Double.parseDouble(stops[j][136]);
			double linesThroughStop = Double.parseDouble(stops[j][25]);
			double nightBus = Double.parseDouble(stops[j][87]);
			double busStops_200m = Double.parseDouble(stops[j][28]);
			double busStops_400m = Double.parseDouble(stops[j][33]);
			double busStops_600m = Double.parseDouble(stops[j][38]);
			double busStops_800m = Double.parseDouble(stops[j][43]);
			double busStops_1km = Double.parseDouble(stops[j][48]);
			double metroStops_200m = Double.parseDouble(stops[j][26]);
			double metroStops_400m = Double.parseDouble(stops[j][36]);
			double metroStops_600m = Double.parseDouble(stops[j][36]);
			double trainStops_200m = Double.parseDouble(stops[j][27]);
			double trainStops_400m = Double.parseDouble(stops[j][32]);
			double busLineLength_TAZ = Double.parseDouble(stops[j][93]);
			double metroLength_TAZ = Double.parseDouble(stops[j][95]);
			double trainLineLength_TAZ = Double.parseDouble(stops[j][91]);
			double trainStops_TAZ = Double.parseDouble(stops[j][92]);
			double majorRoadsLength_400m = Double.parseDouble(stops[j][68]);
			double majorRoadsLength_600m = Double.parseDouble(stops[j][69]);
			double majorRoadsLength_800m = Double.parseDouble(stops[j][70]);
			double highwayLength_800m = Double.parseDouble(stops[j][60]);
			double bikePathLength_400m = Double.parseDouble(stops[j][53]);
			double bikePathLength_600m = Double.parseDouble(stops[j][54]);
			double bikePathLength_1km = Double.parseDouble(stops[j][56]);
			double distanceCBD = Double.parseDouble(stops[j][51]);
			double parkArea_400m = Double.parseDouble(stops[j][78]);
			double parkArea_600m = Double.parseDouble(stops[j][79]);
			double parks_200m = Double.parseDouble(stops[j][30]);
			double parks_400m = Double.parseDouble(stops[j][50]);
			double parks_600m = Double.parseDouble(stops[j][40]);
			double parks_1km = Double.parseDouble(stops[j][50]);
			double commerce_200m = Double.parseDouble(stops[j][29]);
			double commerce_400m = Double.parseDouble(stops[j][34]);
			double commerce_600m = Double.parseDouble(stops[j][39]);
			double commerce_800m = Double.parseDouble(stops[j][44]);
			double commerce_1km = Double.parseDouble(stops[j][49]);
			double govInstArea_TAZ = Double.parseDouble(stops[j][102]);
			double resArea_TAZ = Double.parseDouble(stops[j][99]);
			double commArea_TAZ = Double.parseDouble(stops[j][103]);
			double resoIndArea_TAZ = Double.parseDouble(stops[j][98]);
			double parksRecArea_TAZ = Double.parseDouble(stops[j][100]);
			double jobDensity_TAZ = Double.parseDouble(stops[j][123]);
			double walkScore = Double.parseDouble(stops[j][120]);
			
			if ( stopID == stopIDcomparison ) {
				stopCategory = stops[j][1];
				stopLineFactor(stopTimePeriod, stopID, lineID, stopLinesAM, stopLinesOPD, stopLinesPM, stopLinesOPN);
				// this is the first big differentiation point between stop categories High, Medium and Low
				if ( stopCategory.equals("MEDIUM") ) {
					// the second big differentiation point is then between time categories OPN, AM, OPD, and PM
					if ( stopTimePeriod.equals("OPN")) {

						b_value = -0.005*headwayOPN + 0.238*nightBus + 0.009*busStops_600m - 0.003*jobDensity_TAZ;
						a_value = -0.006*headwayOPN + 0.004*busStops_800m - 0.058*metroStops_600m + 0.003*parks_1km - 1.021*parkArea_400m - 0.141*resArea_TAZ;
						
						b_prob_k1 = CNDF(-0.344 - b_value);
						b_prob_k2 = b_prob_k1 + CNDF(0.252 - b_value) - CNDF(-0.344 - b_value);
						b_prob_k3 = b_prob_k2 + CNDF(0.66 - b_value) - CNDF(0.252 - b_value);
						b_prob_k4 = 1;
						
						a_prob_k1 = CNDF(-0.576 - a_value);
						a_prob_k2 = a_prob_k1 + CNDF(-0.067 - a_value) - CNDF(-0.576 - a_value);
						a_prob_k3 = a_prob_k2 + CNDF(0.305 - a_value) - CNDF(-0.067 - a_value);
						a_prob_k4 = 1;
						
						Random generator = new Random();
						double rand = generator.nextDouble();
						
						if ( rand <= b_prob_k1 ) {
							board = ((rand - 0)/(b_prob_k1 - 0)) * (0.5 - 0);
						} else if ( rand <= b_prob_k2 && rand > b_prob_k1 ) {
							board = ((rand - b_prob_k1)/(b_prob_k2 - b_prob_k1)) * (1 - 0.5) + 0.5;
						} else if ( rand <= b_prob_k3 && rand > b_prob_k2 ) {
							board = ((rand - b_prob_k2)/(b_prob_k3 - b_prob_k2)) * (1.5 - 1) + 1;
						} else if ( rand <= b_prob_k4 && rand > b_prob_k3 ) {
							board = ((rand - b_prob_k3)/(b_prob_k4 - b_prob_k3)) * (5.4 - 1.5) + 1.5;
						}
						
						rand = generator.nextDouble();
						
						if ( rand <= a_prob_k1 ) {
							alight = ((rand - 0)/(a_prob_k1 - 0)) * (0.5 - 0);
						} else if ( rand <= a_prob_k2 && rand > a_prob_k1 ) {
							alight = ((rand - a_prob_k1)/(a_prob_k2 - a_prob_k1)) * (1 - 0.5) + 0.5;
						} else if ( rand <= a_prob_k3 && rand > a_prob_k2 ) {
							alight = ((rand - a_prob_k2)/(a_prob_k3 - a_prob_k2)) * (1.5 - 1) + 1;
						} else if ( rand <= a_prob_k4 && rand > a_prob_k3 ) {
							alight = ((rand - a_prob_k3)/(a_prob_k4 - a_prob_k3)) * (5.4 - 1.5) + 1.5;
						}
											
					} else if ( stopTimePeriod.equals("AM") ) {

						b_value = -0.047*headwayAM - 0.041*linesThroughStop + 0.012*busStops_600m - 0.246*metroStops_400m - 0.025*busLineLength_TAZ - 0.008*trainLineLength_TAZ - 0.062*majorRoadsLength_400m + 0.105*bikePathLength_400m + 0.016*parks_400m - 0.001*commerce_400m + 0.311*resArea_TAZ;
						a_value = -0.015*headwayAM + 0.308*trainStops_400m + 0.013*busLineLength_TAZ + 0.051*majorRoadsLength_600m - 0.057*bikePathLength_600m - 0.011*distanceCBD + 0.004*walkScore + 0.466*govInstArea_TAZ - 0.336*resArea_TAZ;
						
						b_prob_k1 = CNDF(-1.015 - b_value);
						b_prob_k2 = b_prob_k1 + CNDF(-0.14 - b_value) - CNDF(-1.015 - b_value);
						b_prob_k3 = b_prob_k2 + CNDF(0.366 - b_value) - CNDF(-0.14 - b_value);
						b_prob_k4 = 1;
						
						a_prob_k1 = CNDF(-0.228 - a_value);
						a_prob_k2 = a_prob_k1 + CNDF(0.674 - a_value) - CNDF(-0.228 - a_value);
						a_prob_k3 = a_prob_k2 + CNDF(1.138 - a_value) - CNDF(0.674 - a_value);
						a_prob_k4 = 1;
						
						Random generator = new Random();
						double rand = generator.nextDouble();
						
						if ( rand <= b_prob_k1 ) {
							board = ((rand - 0)/(b_prob_k1 - 0)) * (2 - 0);
						} else if ( rand <= b_prob_k2 && rand > b_prob_k1 ) {
							board = ((rand - b_prob_k1)/(b_prob_k2 - b_prob_k1)) * (6 - 2) + 2;
						} else if ( rand <= b_prob_k3 && rand > b_prob_k2 ) {
							board = ((rand - b_prob_k2)/(b_prob_k3 - b_prob_k2)) * (10 - 6) + 6;
						} else if ( rand <= b_prob_k4 && rand > b_prob_k3 ) {
							board = ((rand - b_prob_k3)/(b_prob_k4 - b_prob_k3)) * (30.8 - 10) + 10;
						}
						
						rand = generator.nextDouble();
						
						if ( rand <= a_prob_k1 ) {
							alight = ((rand - 0)/(a_prob_k1 - 0)) * (2 - 0);
						} else if ( rand <= a_prob_k2 && rand > a_prob_k1 ) {
							alight = ((rand - a_prob_k1)/(a_prob_k2 - a_prob_k1)) * (6 - 2) + 2;
						} else if ( rand <= a_prob_k3 && rand > a_prob_k2 ) {
							alight = ((rand - a_prob_k2)/(a_prob_k3 - a_prob_k2)) * (10 - 6) + 6;
						} else if ( rand <= a_prob_k4 && rand > a_prob_k3 ) {
							alight = ((rand - a_prob_k3)/(a_prob_k4 - a_prob_k3)) * (30.8 - 10) + 10;
						}
													
					} else if ( stopTimePeriod.equals("OPD") ) {

						b_value = -0.011*headwayOPD + 0.009*busStops_600m - 0.026*highwayLength_800m + 0.585*parkArea_600m - 0.003*jobDensity_TAZ;
						a_value = -0.026*headwayOPD - 0.049*linesThroughStop + 0.017*busLineLength_TAZ - 0.006*trainLineLength_TAZ - 0.049*highwayLength_800m;
						
						b_prob_k1 = CNDF(-0.427 - b_value);
						b_prob_k2 = b_prob_k1 + CNDF(0.288 - b_value) - CNDF(-0.427 - b_value);
						b_prob_k3 = b_prob_k2 + CNDF(0.789 - b_value) - CNDF(0.288 - b_value);
						b_prob_k4 = 1;
						
						a_prob_k1 = CNDF(-1.001 - a_value);
						a_prob_k2 = a_prob_k1 + CNDF(-0.246 - a_value) - CNDF(-1.001 - a_value);
						a_prob_k3 = a_prob_k2 + CNDF(0.311 - a_value) - CNDF(-0.246 - a_value);
						a_prob_k4 = 1;
						
						Random generator = new Random();
						double rand = generator.nextDouble();
						
						if ( rand <= b_prob_k1 ) {
							board = ((rand - 0)/(b_prob_k1 - 0)) * (1.5 - 0);
						} else if ( rand <= b_prob_k2 && rand > b_prob_k1 ) {
							board = ((rand - b_prob_k1)/(b_prob_k2 - b_prob_k1)) * (3 - 1.5) + 1.5;
						} else if ( rand <= b_prob_k3 && rand > b_prob_k2 ) {
							board = ((rand - b_prob_k2)/(b_prob_k3 - b_prob_k2)) * (4.5 - 3) + 3;
						} else if ( rand <= b_prob_k4 && rand > b_prob_k3 ) {
							board = ((rand - b_prob_k3)/(b_prob_k4 - b_prob_k3)) * (11.6 - 4.5) + 4.5;
						}
						
						rand = generator.nextDouble();
						
						if ( rand <= a_prob_k1 ) {
							alight = ((rand - 0)/(a_prob_k1 - 0)) * (1.5 - 0);
						} else if ( rand <= a_prob_k2 && rand > a_prob_k1 ) {
							alight = ((rand - a_prob_k1)/(a_prob_k2 - a_prob_k1)) * (3 - 1.5) + 1.5;
						} else if ( rand <= a_prob_k3 && rand > a_prob_k2 ) {
							alight = ((rand - a_prob_k2)/(a_prob_k3 - a_prob_k2)) * (4.5 - 3) + 3;
						} else if ( rand <= a_prob_k4 && rand > a_prob_k3 ) {
							alight = ((rand - a_prob_k3)/(a_prob_k4 - a_prob_k3)) * (11.6 - 4.5) + 4.5;
						}
						
					} else if ( stopTimePeriod.equals("PM") ) {

						b_value = -0.015*headwayPM + 0.334*trainStops_400m + 0.014*busLineLength_TAZ + 0.052*majorRoadsLength_600m - 0.045*highwayLength_800m - 0.01*distanceCBD + 0.001*commerce_400m + 1.034*commArea_TAZ - 0.267*resArea_TAZ;
						a_value = -0.047*headwayPM - 0.067*linesThroughStop + 0.009*busStops_600m - 0.022*busLineLength_TAZ - 0.009*trainLineLength_TAZ - 0.029*highwayLength_800m + 0.046*bikePathLength_600m + 0.013*parks_400m - 0.001*commerce_400m + 0.221*resArea_TAZ;
						
						b_prob_k1 = CNDF(-0.515 - b_value);
						b_prob_k2 = b_prob_k1 + CNDF(0.585 - b_value) - CNDF(-0.515 - b_value);
						b_prob_k3 = b_prob_k2 + CNDF(1.163 - b_value) - CNDF(0.585 - b_value);
						b_prob_k4 = 1;
						
						a_prob_k1 = CNDF(-1.309 - a_value);
						a_prob_k2 = a_prob_k1 + CNDF(-0.292 - a_value) - CNDF(-1.309 - a_value);
						a_prob_k3 = a_prob_k2 + CNDF(0.299 - a_value) - CNDF(-0.292 - a_value);
						a_prob_k4 = 1;
						
						Random generator = new Random();
						double rand = generator.nextDouble();
						
						if ( rand <= b_prob_k1 ) {
							board = ((rand - 0)/(b_prob_k1 - 0)) * (2 - 0);
						} else if ( rand <= b_prob_k2 && rand > b_prob_k1 ) {
							board = ((rand - b_prob_k1)/(b_prob_k2 - b_prob_k1)) * (6 - 2) + 2;
						} else if ( rand <= b_prob_k3 && rand > b_prob_k2 ) {
							board = ((rand - b_prob_k2)/(b_prob_k3 - b_prob_k2)) * (10 - 6) + 6;
						} else if ( rand <= b_prob_k4 && rand > b_prob_k3 ) {
							board = ((rand - b_prob_k3)/(b_prob_k4 - b_prob_k3)) * (30.8 - 10) + 10;
						}
						
						rand = generator.nextDouble();
						
						if ( rand <= a_prob_k1 ) {
							alight = ((rand - 0)/(a_prob_k1 - 0)) * (2 - 0);
						} else if ( rand <= a_prob_k2 && rand > a_prob_k1 ) {
							alight = ((rand - a_prob_k1)/(a_prob_k2 - a_prob_k1)) * (6 - 2) + 2;
						} else if ( rand <= a_prob_k3 && rand > a_prob_k2 ) {
							alight = ((rand - a_prob_k2)/(a_prob_k3 - a_prob_k2)) * (10 - 6) + 6;
						} else if ( rand <= a_prob_k4 && rand > a_prob_k3 ) {
							alight = ((rand - a_prob_k3)/(a_prob_k4 - a_prob_k3)) * (30.8 - 10) + 10;
						}
					}
//////////////////////////////////////////////////////////
				} else if ( stopCategory.equals("HIGH") ) {
					if ( stopTimePeriod.equals("OPN")) {

						b_value = -0.01*headwayOPN + 0.066*busStops_200m + 0.777*metroStops_200m + 0.055*majorRoadsLength_400m - 0.026*highwayLength_800m + 1.205*commArea_TAZ - 0.006*jobDensity_TAZ;
						a_value = -0.04*headwayOPN + 0.043*busStops_200m + 0.495*metroStops_200m - 0.01*distanceCBD - 0.001*commerce_800m + 0.003*jobDensity_TAZ;
						
						b_prob_k1 = CNDF(-0.113 - b_value);
						b_prob_k2 = b_prob_k1 + CNDF(0.611 - b_value) - CNDF(-0.113 - b_value);
						b_prob_k3 = b_prob_k2 + CNDF(1.436 - b_value) - CNDF(0.611 - b_value);
						b_prob_k4 = 1;
						
						a_prob_k1 = CNDF(-0.925 - a_value);
						a_prob_k2 = a_prob_k1 + CNDF(-0.339 - a_value) - CNDF(-0.925 - a_value);
						a_prob_k3 = a_prob_k2 + CNDF(0.398 - a_value) - CNDF(-0.339 - a_value);
						a_prob_k4 = 1;
						
						Random generator = new Random();
						double rand = generator.nextDouble();
						
						if ( rand <= b_prob_k1 ) {
							board = ((rand - 0)/(b_prob_k1 - 0)) * (2.5 - 0);
						} else if ( rand <= b_prob_k2 && rand > b_prob_k1 ) {
							board = ((rand - b_prob_k1)/(b_prob_k2 - b_prob_k1)) * (5 - 2.5) + 2.5;
						} else if ( rand <= b_prob_k3 && rand > b_prob_k2 ) {
							board = ((rand - b_prob_k2)/(b_prob_k3 - b_prob_k2)) * (10 - 5) + 5;
						} else if ( rand <= b_prob_k4 && rand > b_prob_k3 ) {
							board = ((rand - b_prob_k3)/(b_prob_k4 - b_prob_k3)) * (86.8 - 10) + 10;
						}
						
						rand = generator.nextDouble();
						
						if ( rand <= a_prob_k1 ) {
							alight = ((rand - 0)/(a_prob_k1 - 0)) * (2.5 - 0);
						} else if ( rand <= a_prob_k2 && rand > a_prob_k1 ) {
							alight = ((rand - a_prob_k1)/(a_prob_k2 - a_prob_k1)) * (5 - 2.5) + 2.5;
						} else if ( rand <= a_prob_k3 && rand > a_prob_k2 ) {
							alight = ((rand - a_prob_k2)/(a_prob_k3 - a_prob_k2)) * (10 - 5) + 5;
						} else if ( rand <= a_prob_k4 && rand > a_prob_k3 ) {
							alight = ((rand - a_prob_k3)/(a_prob_k4 - a_prob_k3)) * (86.8 - 10) + 10;
						}
											
					} else if ( stopTimePeriod.equals("AM") ) {

						b_value = -0.075*headwayAM + 0.059*busStops_200m + 0.583*metroStops_200m + 0.507*trainStops_200m + 0.014*parkArea_600m - 0.632*parks_600m - 0.001*commerce_600m - 0.003*jobDensity_TAZ - 0.47*resoIndArea_TAZ;
						a_value = -0.043*headwayAM + 0.026*busStops_200m + 0.546*metroStops_200m + 0.09*majorRoadsLength_400m + 0.031*parks_200m + 2.373*govInstArea_TAZ - 0.431*resArea_TAZ;
						
						b_prob_k1 = CNDF(-0.77 - b_value);
						b_prob_k2 = b_prob_k1 + CNDF(0.029 - b_value) - CNDF(-0.77 - b_value);
						b_prob_k3 = b_prob_k2 + CNDF(0.837 - b_value) - CNDF(0.029 - b_value);
						b_prob_k4 = 1;
						
						a_prob_k1 = CNDF(-0.189 - a_value);
						a_prob_k2 = a_prob_k1 + CNDF(0.542 - a_value) - CNDF(-0.189 - a_value);
						a_prob_k3 = a_prob_k2 + CNDF(1.142 - a_value) - CNDF(0.542 - a_value);
						a_prob_k4 = 1;
						
						Random generator = new Random();
						double rand = generator.nextDouble();
						
						if ( rand <= b_prob_k1 ) {
							board = ((rand - 0)/(b_prob_k1 - 0)) * (10 - 0);
						} else if ( rand <= b_prob_k2 && rand > b_prob_k1 ) {
							board = ((rand - b_prob_k1)/(b_prob_k2 - b_prob_k1)) * (25 - 10) + 10;
						} else if ( rand <= b_prob_k3 && rand > b_prob_k2 ) {
							board = ((rand - b_prob_k2)/(b_prob_k3 - b_prob_k2)) * (50 - 25) + 25;
						} else if ( rand <= b_prob_k4 && rand > b_prob_k3 ) {
							board = ((rand - b_prob_k3)/(b_prob_k4 - b_prob_k3)) * (330.8 - 50) + 50;
						}
						
						rand = generator.nextDouble();
						
						if ( rand <= a_prob_k1 ) {
							alight = ((rand - 0)/(a_prob_k1 - 0)) * (10 - 0);
						} else if ( rand <= a_prob_k2 && rand > a_prob_k1 ) {
							alight = ((rand - a_prob_k1)/(a_prob_k2 - a_prob_k1)) * (25 - 10) + 10;
						} else if ( rand <= a_prob_k3 && rand > a_prob_k2 ) {
							alight = ((rand - a_prob_k2)/(a_prob_k3 - a_prob_k2)) * (50 - 25) + 25;
						} else if ( rand <= a_prob_k4 && rand > a_prob_k3 ) {
							alight = ((rand - a_prob_k3)/(a_prob_k4 - a_prob_k3)) * (330.8 - 50) + 50;
						}
													
					} else if ( stopTimePeriod.equals("OPD") ) { 

						b_value = -0.057*headwayOPD + 0.064*busStops_200m + 0.424*metroStops_200m + 0.334*metroLength_TAZ + 0.679*commArea_TAZ - 0.009*jobDensity_TAZ - 0.699*resoIndArea_TAZ;
						a_value = -0.064*headwayOPD + 0.045*busStops_200m + 0.7*metroStops_200m - 0.02*highwayLength_800m + 0.006*parkArea_600m + 1.894*commArea_TAZ + 0.7*govInstArea_TAZ;
						
						b_prob_k1 = CNDF(-0.426 - b_value);
						b_prob_k2 = b_prob_k1 + CNDF(0.422 - b_value) - CNDF(-0.426 - b_value);
						b_prob_k3 = b_prob_k2 + CNDF(0.894 - b_value) - CNDF(0.422 - b_value);
						b_prob_k4 = 1;
						
						a_prob_k1 = CNDF(-0.363 - a_value);
						a_prob_k2 = a_prob_k1 + CNDF(0.473 - a_value) - CNDF(-0.363 - a_value);
						a_prob_k3 = a_prob_k2 + CNDF(0.98 - a_value) - CNDF(0.473 - a_value);
						a_prob_k4 = 1;
						
						Random generator = new Random();
						double rand = generator.nextDouble();
						
						if ( rand <= b_prob_k1 ) {
							board = ((rand - 0)/(b_prob_k1 - 0)) * (10 - 0);
						} else if ( rand <= b_prob_k2 && rand > b_prob_k1 ) {
							board = ((rand - b_prob_k1)/(b_prob_k2 - b_prob_k1)) * (20 - 10) + 10;
						} else if ( rand <= b_prob_k3 && rand > b_prob_k2 ) {
							board = ((rand - b_prob_k2)/(b_prob_k3 - b_prob_k2)) * (30 - 20) + 20;
						} else if ( rand <= b_prob_k4 && rand > b_prob_k3 ) {
							board = ((rand - b_prob_k3)/(b_prob_k4 - b_prob_k3)) * (188.6 - 30) + 30;
						}
						
						rand = generator.nextDouble();
						
						if ( rand <= a_prob_k1 ) {
							alight = ((rand - 0)/(a_prob_k1 - 0)) * (10 - 0);
						} else if ( rand <= a_prob_k2 && rand > a_prob_k1 ) {
							alight = ((rand - a_prob_k1)/(a_prob_k2 - a_prob_k1)) * (20 - 10) + 10;
						} else if ( rand <= a_prob_k3 && rand > a_prob_k2 ) {
							alight = ((rand - a_prob_k2)/(a_prob_k3 - a_prob_k2)) * (30 - 20) + 20;
						} else if ( rand <= a_prob_k4 && rand > a_prob_k3 ) {
							alight = ((rand - a_prob_k3)/(a_prob_k4 - a_prob_k3)) * (188.6 - 30) + 30;
						}
						
					} else if ( stopTimePeriod.equals("PM") ) {

						b_value = -0.041*headwayPM + 0.069*busStops_200m + 0.569*metroStops_200m + 0.002*commerce_200m + 0.916*commArea_TAZ - 0.003*jobDensity_TAZ - 0.395*resArea_TAZ;
						a_value = -0.08*headwayPM + 0.037*busStops_200m + 0.381*metroStops_200m + 0.171*trainStops_TAZ + 0.013*parkArea_600m - 0.66*parks_600m - 0.001*commerce_600m + 0.54*commArea_TAZ + 0.003*jobDensity_TAZ - 0.475*resoIndArea_TAZ;
						
						b_prob_k1 = CNDF(-0.456 - b_value);
						b_prob_k2 = b_prob_k1 + CNDF(0.529 - b_value) - CNDF(-0.456 - b_value);
						b_prob_k3 = b_prob_k2 + CNDF(1.19 - b_value) - CNDF(0.529 - b_value);
						b_prob_k4 = 1;
						
						a_prob_k1 = CNDF(-1.091 - a_value);
						a_prob_k2 = a_prob_k1 + CNDF(-0.18 - a_value) - CNDF(-1.091 - a_value);
						a_prob_k3 = a_prob_k2 + CNDF(0.674 - a_value) - CNDF(-0.18 - a_value);
						a_prob_k4 = 1;
						
						Random generator = new Random();
						double rand = generator.nextDouble();
						
						if ( rand <= b_prob_k1 ) {
							board = ((rand - 0)/(b_prob_k1 - 0)) * (10 - 0);
						} else if ( rand <= b_prob_k2 && rand > b_prob_k1 ) {
							board = ((rand - b_prob_k1)/(b_prob_k2 - b_prob_k1)) * (25 - 10) + 10;
						} else if ( rand <= b_prob_k3 && rand > b_prob_k2 ) {
							board = ((rand - b_prob_k2)/(b_prob_k3 - b_prob_k2)) * (50 - 25) + 25;
						} else if ( rand <= b_prob_k4 && rand > b_prob_k3 ) {
							board = ((rand - b_prob_k3)/(b_prob_k4 - b_prob_k3)) * (330.8 - 50) + 50;
						}
						
						rand = generator.nextDouble();
						
						if ( rand <= a_prob_k1 ) {
							alight = ((rand - 0)/(a_prob_k1 - 0)) * (10 - 0);
						} else if ( rand <= a_prob_k2 && rand > a_prob_k1 ) {
							alight = ((rand - a_prob_k1)/(a_prob_k2 - a_prob_k1)) * (25 - 10) + 10;
						} else if ( rand <= a_prob_k3 && rand > a_prob_k2 ) {
							alight = ((rand - a_prob_k2)/(a_prob_k3 - a_prob_k2)) * (50 - 25) + 25;
						} else if ( rand <= a_prob_k4 && rand > a_prob_k3 ) {
							alight = ((rand - a_prob_k3)/(a_prob_k4 - a_prob_k3)) * (330.8 - 50) + 50;
						}
					}
////////////////////////////////////////////////
				} else if ( stopCategory.equals("LOW") ) {
					if ( stopTimePeriod.equals("OPN")) {

						b_value = -0.0001*headwayOPN + 0.024*busStops_400m + 0.544*bikePathLength_1km + 0.009*parks_600m - 0.001*commerce_600m - 0.403*parksRecArea_TAZ;
						a_value = -0.0001*headwayOPN + 0.011*busStops_600m + 0.275*bikePathLength_1km - 0.001*commerce_600m - 0.352*parksRecArea_TAZ;
						
						b_prob_k1 = CNDF(0.957 - b_value);
						b_prob_k2 = 1;
						
						a_prob_k1 = CNDF(0.528 - a_value);
						a_prob_k2 = 1;
						
						Random generator = new Random();
						double rand = generator.nextDouble();
						
						if ( rand <= b_prob_k1 ) {
							board = ((rand - 0)/(b_prob_k1 - 0)) * (0.25 - 0);
						} else if ( rand <= b_prob_k2 && rand > b_prob_k1 ) {
							board = ((rand - b_prob_k1)/(b_prob_k2 - b_prob_k1)) * (1.3 - 0.25) + 0.25;
						}
						
						rand = generator.nextDouble();
						
						if ( rand <= a_prob_k1 ) {
							alight = ((rand - 0)/(a_prob_k1 - 0)) * (0.25 - 0);
						} else if ( rand <= a_prob_k2 && rand > a_prob_k1 ) {
							alight = ((rand - a_prob_k1)/(a_prob_k2 - a_prob_k1)) * (1.3 - 0.25) + 0.25;
						}
											
					} else if ( stopTimePeriod.equals("AM") ) { 

						b_value = -0.0002*headwayAM + 0.017*busStops_600m - 0.983*majorRoadsLength_400m + 0.416*bikePathLength_1km + 0.018*parks_600m - 0.001*commerce_1km - 0.478*govInstArea_TAZ + 0.165*resArea_TAZ - 0.408*parksRecArea_TAZ - 0.555*resoIndArea_TAZ;
						a_value = -0.0002*headwayAM + 0.139*majorRoadsLength_800m - 0.846*bikePathLength_600m - 0.167*distanceCBD + 1.057*commArea_TAZ + 0.003*walkScore - 0.22*resArea_TAZ;
						
						b_prob_k1 = CNDF(-0.009 - b_value);
						b_prob_k2 = b_prob_k1 + CNDF(0.428 - b_value) - CNDF(-0.009 - b_value);
						b_prob_k3 = 1;
						
						a_prob_k1 = CNDF(-0.43 - a_value);
						a_prob_k2 = a_prob_k1 + CNDF(0.028 - a_value) - CNDF(-0.43 - a_value);
						a_prob_k3 = 1;
						
						Random generator = new Random();
						double rand = generator.nextDouble();
						
						if ( rand <= b_prob_k1 ) {
							board = ((rand - 0)/(b_prob_k1 - 0)) * (0.5 - 0);
						} else if ( rand <= b_prob_k2 && rand > b_prob_k1 ) {
							board = ((rand - b_prob_k1)/(b_prob_k2 - b_prob_k1)) * (1 - 0.5) + 0.5;
						} else if ( rand <= b_prob_k3 && rand > b_prob_k2 ) {
							board = ((rand - b_prob_k2)/(b_prob_k3 - b_prob_k2)) * (6 - 1) + 1;
						} 
						
						rand = generator.nextDouble();
						
						if ( rand <= a_prob_k1 ) {
							alight = ((rand - 0)/(a_prob_k1 - 0)) * (0.5 - 0);
						} else if ( rand <= a_prob_k2 && rand > a_prob_k1 ) {
							alight = ((rand - a_prob_k1)/(a_prob_k2 - a_prob_k1)) * (1 - 0.5) + 0.5;
						} else if ( rand <= a_prob_k3 && rand > a_prob_k2 ) {
							alight = ((rand - a_prob_k2)/(a_prob_k3 - a_prob_k2)) * (6 - 1) + 1;
						} 
													
					} else if ( stopTimePeriod.equals("OPD") ) {

						b_value = -0.0001*headwayOPD + 0.007*busStops_1km + 0.313*bikePathLength_1km + 0.019*parks_600m - 0.001*commerce_800m - 0.231*parksRecArea_TAZ;
						a_value = -0.0001*headwayOPD + 0.009*busStops_1km + 0.286*bikePathLength_1km + 0.019*parks_600m - 0.001*commerce_800m + 0.165*resArea_TAZ - 0.264*parksRecArea_TAZ;
						
						b_prob_k1 = CNDF(0.221 - b_value);
						b_prob_k2 = b_prob_k1 + CNDF(0.743 - b_value) - CNDF(0.221 - b_value);
						b_prob_k3 = 1;
						
						a_prob_k1 = CNDF(0.345 - a_value);
						a_prob_k2 = a_prob_k1 + CNDF(0.816 - a_value) - CNDF(0.345 - a_value);
						a_prob_k3 = 1;
						
						Random generator = new Random();
						double rand = generator.nextDouble();
						
						if ( rand <= b_prob_k1 ) {
							board = ((rand - 0)/(b_prob_k1 - 0)) * (0.25 - 0);
						} else if ( rand <= b_prob_k2 && rand > b_prob_k1 ) {
							board = ((rand - b_prob_k1)/(b_prob_k2 - b_prob_k1)) * (0.5 - 0.25) + 0.25;
						} else if ( rand <= b_prob_k3 && rand > b_prob_k2 ) {
							board = ((rand - b_prob_k2)/(b_prob_k3 - b_prob_k2)) * (2.3 - 0.5) + 0.5;
						} 
						
						rand = generator.nextDouble();
						
						if ( rand <= a_prob_k1 ) {
							alight = ((rand - 0)/(a_prob_k1 - 0)) * (0.25 - 0);
						} else if ( rand <= a_prob_k2 && rand > a_prob_k1 ) {
							alight = ((rand - a_prob_k1)/(a_prob_k2 - a_prob_k1)) * (0.5 - 0.25) + 0.25;
						} else if ( rand <= a_prob_k3 && rand > a_prob_k2 ) {
							alight = ((rand - a_prob_k2)/(a_prob_k3 - a_prob_k2)) * (2.3 - 0.5) + 0.5;
						} 
						
					} else if ( stopTimePeriod.equals("PM") ) {

						b_value = -0.0002*headwayPM - 0.167*distanceCBD - 0.19*resArea_TAZ - 0.229*parksRecArea_TAZ;
						a_value = -0.0002*headwayPM + 0.017*busStops_600m - 0.636*majorRoadsLength_600m + 0.282*highwayLength_800m + 0.385*bikePathLength_1km + 0.015*parks_600m - 0.001*commerce_1km - 0.006*jobDensity_TAZ - 0.555*govInstArea_TAZ + 0.196*resArea_TAZ - 0.228*parksRecArea_TAZ - 0.544*resoIndArea_TAZ;
						
						b_prob_k1 = CNDF(-0.51 - b_value);
						b_prob_k2 = b_prob_k1 + CNDF(0.007 - b_value) - CNDF(-0.51 - b_value);
						b_prob_k3 = 1;
						
						a_prob_k1 = CNDF(-0.064 - a_value);
						a_prob_k2 = a_prob_k1 + CNDF(0.433 - a_value) - CNDF(-0.064 - a_value);
						a_prob_k3 = 1;
						
						Random generator = new Random();
						double rand = generator.nextDouble();
						
						if ( rand <= b_prob_k1 ) {
							board = ((rand - 0)/(b_prob_k1 - 0)) * (0.5 - 0);
						} else if ( rand <= b_prob_k2 && rand > b_prob_k1 ) {
							board = ((rand - b_prob_k1)/(b_prob_k2 - b_prob_k1)) * (1 - 0.5) + 0.5;
						} else if ( rand <= b_prob_k3 && rand > b_prob_k2 ) {
							board = ((rand - b_prob_k2)/(b_prob_k3 - b_prob_k2)) * (6 - 1) + 1;
						} 
						
						rand = generator.nextDouble();
						
						if ( rand <= a_prob_k1 ) {
							alight = ((rand - 0)/(a_prob_k1 - 0)) * (0.5 - 0);
						} else if ( rand <= a_prob_k2 && rand > a_prob_k1 ) {
							alight = ((rand - a_prob_k1)/(a_prob_k2 - a_prob_k1)) * (1 - 0.5) + 0.5;
						} else if ( rand <= a_prob_k3 && rand > a_prob_k2 ) {
							alight = ((rand - a_prob_k2)/(a_prob_k3 - a_prob_k2)) * (6 - 1) + 1;
						} 
					}
				}
								
				board = (double)Math.round(board * stopLineFactor * 1) / 1;
				alight = (double)Math.round(alight * stopLineFactor * 1) / 1;
								
				break;
			}	
		}
		
		double[] ridership = {board, alight, stopLineFactor};
		return ridership;
	}

	static double stopLineFactor(String stopPeriod, int stopID, int lineID, int[][] stopLinesAM, int[][] stopLinesOPD, int[][] stopLinesPM, int[][] stopLinesOPN) {
		
		if ( stopPeriod.equals("OPN") ) {
			for ( int x = 0 ; x < 8738 ; x++ ) {
				if ( stopID == stopLinesOPN[x][0] ) {
					total = stopLinesOPN[x][1];
					hourFactor = (double) total / 12;
					if (stopLinesOPN[x][2] == 0)
						hourFactor = (double) total / 7;
					for ( int y = 3 ; y < 197 ; y++ ) {
						if ( lineID == stopLinesOPN[0][y] ) {
							currentLine = stopLinesOPN[x][y];
							break;
						}
					}
					break;
				}
			}	
		} else if ( stopPeriod.equals("AM") ) {
			for ( int x = 0 ; x < 8738 ; x++ ) {
				if ( stopID == stopLinesAM[x][0] ) {
					total = stopLinesAM[x][1];
					hourFactor = (double) total / 3;
					for ( int y = 2 ; y < 176 ; y++ ) {
						if ( lineID == stopLinesAM[0][y] ) {
							currentLine = stopLinesAM[x][y];
							break;
						}
					}
					break;
				}
			}	
		} else if ( stopPeriod.equals("OPD") ) {
			for ( int x = 0 ; x < 8738 ; x++ ) {
				if ( stopID == stopLinesOPD[x][0] ) {
					total = stopLinesOPD[x][1];
					hourFactor = (double) total / 6;
					for ( int y = 2 ; y < 170 ; y++ ) {
						if ( lineID == stopLinesOPD[0][y] ) {
							currentLine = stopLinesOPD[x][y];
							break;
						}
					}
					break;
				}
			}	
		} else if ( stopPeriod.equals("PM") ) {
			for ( int x = 0 ; x < 8738 ; x++ ) {
				if ( stopID == stopLinesPM[x][0] ) {
					total = stopLinesPM[x][1];
					hourFactor = (double) total / 3;
					for ( int y = 2 ; y < 174 ; y++ ) {
						if ( lineID == stopLinesPM[0][y] ) {
							currentLine = stopLinesPM[x][y];
							break;
						}
					}
					break;
				}
			}	
		}
		
		stopLineFactor = ( (double)currentLine / (double)total ) / hourFactor;
		return stopLineFactor;
	}
	
	static double CNDF(double x)
	{
	    int neg = (x < 0d) ? 1 : 0;
	    if ( neg == 1) 
	        x *= -1d;

	    double k = (1d / ( 1d + 0.2316419 * x));
	    double y = (((( 1.330274429 * k - 1.821255978) * k + 1.781477937) *
	                   k - 0.356563782) * k + 0.319381530) * k;
	    y = 1.0 - 0.398942280401 * Math.exp(-0.5 * x * x) * y;

	    return (1d - neg) * y + neg * (1d - y);
	}

}
