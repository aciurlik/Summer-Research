import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;


/*
 * 
 * 
 * This class is on its way out.
 * 
 * As soon as we transfer all stored majors over into the new format, 
 * this class should be deleted.
 */
public class SaverLoader implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


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
	
	
	public static Iterable<String> fromJSON(String s){
		ArrayList<String> result = new ArrayList<String>();
		boolean odd = false;
		for(String str : JSONIterable(s)){
			if(odd){
				result.add(str);
			}
			odd = !odd;
		}
		return result;
	}
	
	public static String peel(String s){
		return s.substring(1, s.length() - 1);
	}

	/**
	 * Given a list of the form
	 * {  obj1 obj2 obj3 }
	 * return an iterable of each object.
	 * @param s
	 * @return
	 */
	public static Iterable<String> JSONIterable(String s){
		int depth = 0;
		ArrayList<String> result = new ArrayList<String>();
		StringBuilder chunk = new StringBuilder();
		for(String element : tokenize(s)){
			if(element.equals("{")){
				depth += 1;
			}
			if(element.equals("}")){
				depth = depth - 1;
			}
			chunk.append(element);
			
			if(depth == 0){
				result.add(chunk.toString());
				chunk = new StringBuilder();
			}
		}
		result.add(chunk.toString());
		return result;
	}
	
	public static ArrayList<String> tokenize(String s){
		ArrayList<String> result = new ArrayList<String>();
		StringBuilder chunk = new StringBuilder();
		boolean inQuotes = false;
		boolean addedBracket = false;
		for(char c : s.toCharArray()){
			if(addedBracket){
				result.add(chunk.toString());
				chunk = new StringBuilder();
				addedBracket = false;
			}
			if(!inQuotes && (c == '{' || c == '}')){
				result.add(chunk.toString());
				chunk = new StringBuilder();
				addedBracket = true;
			}
			if(c == '"'){
				inQuotes = !inQuotes;
			}
			chunk.append(c);
		}
		result.add(chunk.toString());
		return result;
	}
	
	
	/**
	 * Split this string based on its commas, ignoring commas inside
	 * quotes.
	 * 
	 * Most of the code was found online, we added the hash set of ignore characters.
	 * @param CSVText
	 * @return
	 */
	public static ArrayList<String> parseCSVLine(String csvLine, HashSet<Character> ignoreCharacters){
		ArrayList<String> result = new ArrayList<>();
		if (csvLine == null || csvLine.isEmpty()) {
			return result;
		}
		StringBuilder chunk = new StringBuilder();
		boolean inQuotes = false;
		char prevChar = 0; //could be used to check if you have a '/' inside quotes.

		for (char ch : csvLine.toCharArray()) {
			if (inQuotes) {
				if (ch == '"') {
					inQuotes = false;
				} 
				else {
					chunk.append(ch);
					prevChar = ch;
				}
			} else { //if you're not in quotes
				if (ch == '"') {
                    inQuotes = true;
                } 
				else if (ch == ',') {
                    result.add(chunk.toString());
                    chunk = new StringBuilder();
                } 
				else if (ignoreCharacters.contains(ch)) {
                    //ignore LF characters
                    continue;
                }
				else if (ch == '\n') {
                    //the end, break!
                    break;
                } 
				else {
                    chunk.append(ch);
                }
            }
        } 
        result.add(chunk.toString());
        return result;
    }
	
	public static ArrayList<String> parseCSVLine(String csvLine){
		HashSet<Character> ignoreCharacters = new HashSet<Character>();
		ignoreCharacters.add('\r');
		return parseCSVLine(csvLine, ignoreCharacters);
	}
	
	public static ArrayList<String> parseAdvisorImportCSVLine(String csvLine){
		HashSet<Character> ignoreCharacters = new HashSet<Character>();
		ignoreCharacters.add('\r');
		ignoreCharacters.add('='); //some lines are of the form :
		// ="99999", ="2017D2","asdfas","asdfasd"
		return parseCSVLine(csvLine, ignoreCharacters);
	}

}
