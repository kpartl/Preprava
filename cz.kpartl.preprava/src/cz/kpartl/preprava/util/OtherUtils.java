package cz.kpartl.preprava.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OtherUtils {

	public static boolean isValidDate(String inDate) {
		return parseDate(inDate) != null;
	}

	public static Date parseDate(String inDate) {

		if (inDate == null)
			return null;

		// set the format to use as a constructor argument
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

		if (inDate.trim().length() != dateFormat.toPattern().length())
			return null;

		dateFormat.setLenient(false);

		try {
			// parse the inDate parameter
			return dateFormat.parse(inDate.trim());
		} catch (ParseException pe) {
			return null;
		}

	}

}
