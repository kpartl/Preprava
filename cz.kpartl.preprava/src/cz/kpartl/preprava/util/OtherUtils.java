package cz.kpartl.preprava.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class OtherUtils {
	
	 public static boolean isValidDate(String inDate) {

		    if (inDate == null)
		      return false;

		    //set the format to use as a constructor argument
		    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		    
		    if (inDate.trim().length() != dateFormat.toPattern().length())
		      return false;

		    dateFormat.setLenient(false);
		    
		    try {
		      //parse the inDate parameter
		      dateFormat.parse(inDate.trim());
		    }
		    catch (ParseException pe) {
		      return false;
		    }
		    return true;
		  }

}
