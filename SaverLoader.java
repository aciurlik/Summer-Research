import java.util.ArrayList;


public class SaverLoader {

	/**
	 * Will create a string of the form
object[0].toString() delimiter[0] object[1].toString() .... delimiter[n-1] object[n].toString() 
	 *   
	 * @param delimiters
	 * @param toSave 
	 */
	public static String saveString( String[] delimiters, Object[] toSave){
		if(delimiters.length + 1 != toSave.length){
			throw new RuntimeException("wrong lengths for saving: \n"
					+ " delimeters should have one fewer element than toSave \n"
					+ " delimeters had " + delimiters.length + " elements.");
		}
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < delimiters.length ; i ++){
			result.append(toSave[i].toString() + delimiters[i] );
		}
		result.append(toSave[toSave.length-1].toString());
		return result.toString();
	}
	/**
	 * Will read from a string created by SaveLoader.saveString
	 * @param delimiters
	 * @param toParse
	 * @return
	 */
	public static String[] parseString(String[] delimiters, String toParse){
		ArrayList<String> result = new ArrayList<String>();
		String rest = toParse;
		for(int i = 0; i < delimiters.length ; i ++){
			//rest has the form  object[i], delimiters[i], object[i+1] ...
			// split rest into two strings, object[i] and object[i+1]....
			// add object[i] to the list of objects
			int startIndex = rest.indexOf(delimiters[i]);
			int endIndex = rest.indexOf(delimiters[i]) + delimiters[i].length();
			result.add(rest.substring(0,startIndex));
			rest = rest.substring(endIndex);
		}
		return result.toArray(new String[ result.size()]);
	}


}
