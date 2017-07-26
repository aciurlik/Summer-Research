import java.util.ArrayList;
import java.util.HashSet;


/*
 * 
 * 
 * This class is on its way out.
 * 
 * The methods in it should be transfered to FileHandler and then 
 * this class should be deleted.
 */
public class SaverLoader{
	
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
