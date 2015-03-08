package edu.dartmouth.cs.SleepDrunk;

import java.text.DecimalFormat;

import android.location.Location;
import com.google.android.gms.maps.model.LatLng;



public class Utils {
	  public static final double KiloToMile = 0.621371;
	  public static final double MeterToFoot = 3.28084;
	  public static final int BLOCK_QUEUE_CAPACITY =  64;
	  
	  public static final DecimalFormat df = new  DecimalFormat("#.##");
	  public static LatLng fromLocationToLatLng(Location location){
			return new LatLng(location.getLatitude(), location.getLongitude());			
		}
}
