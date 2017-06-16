import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;


public class Requirement implements ScheduleElement, Comparable<Requirement>{
	HashSet<Requirement> choices;
	int numToChoose; //the number of classes that must be taken.
	// If this is a "2 of these choices" requirement, then numToChoose
	// should be set to 2.
	// if this requirement usesCreditHours, then numToChoose is the number
	// of credit hours, and storedCoursesLeft stores the number of credit hours left.
	boolean usesCreditHours;
	String name;
	boolean storedIsComplete;
	int storedCoursesLeft;
	double storedPercentComplete;

	//Used to check if %complete is close to 0.
	public static final double tolerance = 1000 * Double.MIN_VALUE;


	public Requirement(){
		this.choices = new HashSet<Requirement>();
		this.numToChoose = 1;
		usesCreditHours = false;
	}


	/**
	 * An old constructor for backwards compatability.
	 * Can be removed.
	 * @param choices
	 * @param numToChoose
	 */
	public Requirement(Prefix[] choices, int numToChoose){
		this(new HashSet<Prefix>(Arrays.asList(choices)), numToChoose);
	}
	public Requirement(HashSet<Prefix> choices, int numToChoose){
		this();
		for(Prefix p : choices){
			TerminalRequirement r = new TerminalRequirement(p);
			this.choices.add(r);
		}
		this.numToChoose = numToChoose;
	}

	public void addRequirement(Requirement r){
		this.choices.add(r);
	}
	
	public void setHoursNeeded(int hours){
		this.numToChoose = hours;
		this.usesCreditHours = true;
	}


	public void setName(String name){
		this.name = name;
	}
	public String getName() {
		return this.name;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/////////////////////
	/////////////////////
	///// Recursive methods
	/////////////////////
	/////////////////////
	//
	//  The call chain here looks something like:
	//
	//
	//
	//									percentComplete(taken, bool)
	//											|
	//											V
	//  isComplete(taken, bool)				MMN(taken, bool)
	//			|								|
	//			V								V
	//  isComplete(taken, int, bool) ---> MMN(taken, int, bool)
	//			^								|
	// 			|								|
	//			|								V
	// 			-----recurse on choices----- MMN(taken)
	//											|
	//											|
	//											|
	// isSatisfied(p) <----recurse on choices---
	// |		  ^		
	// | 		  |	
	// |			--------
	// |					|
	// -recurse on choices--
	//
	// Terminal requirment only needs to overwrite methods that have
	// an outedge that recurses, so MMN(taken, int, bool) and
	// isSatisfied.
	//
	
	
	private int numPlannedLater(ArrayList<ScheduleElement> taken){
		int numPlanned = 0;
		for(ScheduleElement s : taken){
			if(s == this){
				numPlanned ++;
			}
		}
		return numPlanned;
	}
	/**
	 * Find the subset of schedule elements that have prefixes
	 * @param taken
	 * @return
	 */
	private HashSet<ScheduleElement> takenPrefixes(ArrayList<ScheduleElement> taken){
		HashSet<ScheduleElement> takenElements = new HashSet<ScheduleElement>();
		for(ScheduleElement s : taken){
			if(s.getPrefix()!= null){
				takenElements.add(s);
			}
		}
		return takenElements;
	}
	
	/**
	 * Check whether this set of schedule elements completes this requirements.
	 * If storeValue is true, this requirement will store the value it calculates
	 *   in the field storedCompletionValue.
	 * @param taken
	 * @param storeValue
	 * @return
	 */
	public boolean isComplete(ArrayList<ScheduleElement> taken, boolean storeValue){
		int numPlanned = numPlannedLater(taken);
		return isComplete(taken, numPlanned, storeValue);
	}

	/** 
	 * Check if these prefixes satisfy this requirement.
	 * Requirements are optimists - if this set of taken things
	 *  has any shot of satisfying this requirement, then the requirement.
	 *  will say that it does. 
	 *  For credit hours, all future requirements are assumed to satisfy 4 credits.
	 * @param taken
	 * @return
	 */
	private boolean isComplete(ArrayList<ScheduleElement> taken, int numPlannedLater, boolean storeValue){
		boolean result = minMoreNeeded(taken, numPlannedLater, storeValue) <= 0;
		if(storeValue){
			this.storedIsComplete = result;
		}
		return result;
	}

	/**
	 * Find the minimum number of courses or credits you would need to completely
	 * satisfy this requirement, given this set of things you've already taken.
	 * @param taken
	 * @param storeValue
	 * @return
	 */
	public int minMoreNeeded(ArrayList<ScheduleElement> taken, boolean storeValue){
		int numPlanned = numPlannedLater(taken);
		return minMoreNeeded(taken, numPlanned, storeValue);
	}
	/**
	 * Find the minimum number of courses or credits you would need to completely
	 * satisfy this requirement, given this set of things you've already taken.
	 */
	private int minMoreNeeded(ArrayList<ScheduleElement> taken, int numPlannedLater, boolean storeValue){
		int result = minMoreNeeded(taken);
		result = result - numPlannedLater;
		if(storeValue){
			this.storedCoursesLeft = result;
		}
		return result;
	}
	
	
	//INFINITELOOPHAZARD
	/**
	 * 
	 * @param taken
	 * @return
	 */
	protected int minMoreNeeded(ArrayList<ScheduleElement> taken){
		if(this.usesCreditHours){
			int result = 0;
			ArrayList<Requirement> completed = new ArrayList<Requirement>();
			for(Requirement r : choices){
				if(r.isComplete(taken, false)){
					//you get to add the credits from it
					completed.add(r);
				}
			}
			for(ScheduleElement e : taken){
				//if any of your completed subrequirements uses it, you can add its credits.
				if(e instanceof HasCreditHours){
					int credits = ((HasCreditHours)e).getCreditHours();
					boolean found = false;
					for(Requirement r : completed){
						if(r.isSatisfiedBy(e.getPrefix())){
							found = true;
							break;
						}
					}
					if(found){
						result += credits;
					}
				}
			}
			return result;
		}
		else{
			ArrayList<Integer> otherNums = new ArrayList<Integer>();
			for(Requirement r : choices){
				otherNums.add(r.minMoreNeeded(taken));
			}
			Collections.sort(otherNums);
			int result = 0;
			for(int i = 0; i < numToChoose ; i ++){
				result += otherNums.get(i);
			}
			return result;
		}
	}
	




	/**
	 *  Find, given the best case scenario, the maximum % complete this
	 *  requirement could be given this collection of schedule elements.
	 * @param taken
	 * @param storeValue
	 * @return
	 */
	public double percentComplete(ArrayList<ScheduleElement> taken, boolean storeValue){
		double minNeeded = minMoreNeeded(taken, storeValue);
		double originalNeeded = minMoreNeeded(new ArrayList<ScheduleElement>(), false);
		double result = (1.0 - minNeeded/originalNeeded);
		if(storeValue){
			this.storedPercentComplete = result;
		}
		return result;
	}
	
	
	

	//INFINITELOOPHAZARD
	public boolean isSatisfiedBy(Prefix p) {
		for(Requirement r : this.choices){
			if(r.isSatisfiedBy(p)){
				return true;
			}
		}
		return false;
	}
	
	
	
	
	
	
	
	
	
	
	
	





	/////////////////////////////////
	/////////////////////////////////
	///// CompareTo 
	/////////////////////////////////
	/////////////////////////////////




	//TODO Let the user decide which comparisons should come first.
	//This comparison method is used to sort the 
	// requirementList displayed to the user.
	@Override
	public int compareTo(Requirement o) {
		if(! (o instanceof Requirement)){
			//Requirements are greater than terminalRequirements.
			return 1;
		}
		Requirement other = (Requirement)o;
		//first compare based on whether they're complete, completed coming later
		if(this.storedIsComplete && !other.storedIsComplete){
			return 1;
		}
		if(!this.storedIsComplete && other.storedIsComplete){
			return -1;
		}

		//Then compare based on % complete
		double percentDifference = storedPercentComplete - other.storedPercentComplete;
		if(percentDifference < tolerance){
			return -1;
		}
		if(percentDifference > tolerance){
			return 1;
		}

		//Then compare based on number to choose
		int numChooseDifference = this.numToChoose - other.numToChoose;
		if(numChooseDifference != 0){
			return numChooseDifference;
		}


		//then compare based on prefixes.
		// first number of prefixes, then containment.
		if(this.choices.size() != other.choices.size()){
			return this.choices.size() - other.choices.size();
		}

		// check if this is contained in that.
		// if not, return that it's greater. 
		// Note that this ruins the total ordering property, 
		// two requirements can be greater than each other.
		//TODO add containment check


		//TODO add check for exact equality
		return 0;

	}

	/////////////////////////////////
	/////////////////////////////////
	/////Methods from ScheduleElement
	/////////////////////////////////
	/////////////////////////////////

	@Override
	public Prefix getPrefix() {
		return null;
	}


	@Override
	public boolean isDuplicate(ScheduleElement other) {
		return false;
	}

	@Override
	public String getDisplayString() {
		if(this.name != null){
			return this.name;
		}
		int counter = 0;
		String finalResult = new String();
		String result = this.saveString();
		if(this.numToChoose == 1){
			result = result.substring(1, result.length() - 1);
		}
		return result;
	}
	
	@Override
	public String shortString() {
		return this.getDisplayString();
	}


	@Override
	public ArrayList<Requirement> getRequirementsFulfilled(HashSet<Requirement> r) {
		ArrayList<Requirement> result = new ArrayList<Requirement>(1);
		result.add(this);
		return result;
	}
	
	@Override
	public String toString(){
		return this.getDisplayString();
	}


	public int getCreditHours() {
		// TODO Auto-generated method stub
		return 4;
	}




	/////////////////////////////////
	/////////////////////////////////
	///// Saving and Reading methods
	/////////////////////////////////
	/////////////////////////////////



	/** REQUIREMENT SAVING AND READING TUTORIAL
	 * 
	 * The language of requirements.
	 * 
	 * All requirements are made of 2 parts:
	 * 		a number to choose
	 * 		a list of choices (some of which may themselves be requirements)
	 * 
	 * For example, the requirement 
	 * 		2 of (MTH 145, MTH 220)
	 *  	is a valid requirement. It has 2 to choose, and 
	 *  	has 2 choices, either MTH 145 or MTH 220.
	 *  	This requirement might be interpreted as
	 * 			"Take MTH 145 and MTH 220."
	 * 
	 * In general, to differentiate between 'or' and 'and', just change 
	 * 		the number to choose.
	 * 		2 of (MTH 145, MTH 220) --> means MTH 145 and MTH 220
	 * 		1 of (MTH 145, MTH 220) --> means MTH 145 or MTH 220
	 * 
	 * Requirements may be nested, for example
	 * 		2 of (MTH 140, 1 of (MTH 110, MTH 220), 1 of (MTH 100))
	 * 		Commas and parenthesis differentiate between different levels of requirements
	 * 
	 * Notice that impossible requirements may be written down, for example
	 * 		2 of (MTH 110)
	 * 		cannot logically be satisfied.
	 * 
	 * We may omit the number to choose if desired. In this case, it defaults to one. 
	 * 		(MTH 140, MTH 110)
	 * 			is the same as
	 * 		1 of (MTH 140, MTH 110)
	 * 			Which might be interpreted as
	 * 		MTH 140 or MTH 110
	 * 
	 * All whitespace is ignored.
	 * 
	 * A list of choices must be enclosed in parenthesis, and
	 * must be separated by commas. The following are INVALID.
	 * 		NOT GOOD     MTH 140, MTH 110 
	 * 		Correction   (MTH 140, MTH 110)
	 * 
	 * 		NOT GOOD     1 of MTH 220
	 * 		Correction   1 of ( MTH 220      )
	 * 
	 * 		NOT GOOD     2 of (MTH 140, 1 of MTH 220, MTH 110, MTH 213)
	 * 		Correction   2 of (MTH 140, 1 of (MTH 220), MTH 110, MTH 213)
	 * 			Which is equivalent to
	 * 				2 of (MTH 140, MTH 220, MTH 110, MTH 213)
	 * 		Alternate    2 of (MTH 140, 1 of (MTH 220, MTH 110), MTH 213)
	 * 			Which is not equivalent to the simpler requirement.
	 * 
	 * 		NOT GOOD     1 of (MTH 140 MTH 220)
	 * 		Correction	 1 of (MTH 140, MTH 220)
	 * 			
	 * 
	 * Additionally, though it is not recommended, you may include the text
	 *   'or' before the last choice in a list of choices. This must immediately follow the comma.
	 * 		2 of (MTH 110, MTH 220,  or   MTH 330)
	 * 		This is a valid requirement.
	 * 		Saying 'or' may cause confusion for cases like this:
	 * 		3 of (MTH 110, MTH 220,  or   MTH 330)
	 * 
	 */




	//INFINITELOOPHAZARD
	/**
	 * see REQUIREMENT SAVING AND READING TUTORIAL in Requirement class.
	 * Make a save string for this requirement.
	 * @return
	 */
	public String saveString(){

		//Add the prefix
		StringBuilder result = new StringBuilder();
		if(this.numToChoose == 1){
			result.append("(");
		}
		else{
			result.append(numToChoose + " of (");
		}
		//Add the guts of this requirement - each sub-requirement


		//handle the first n-2 requirements
		ArrayList<Requirement> choiceList = new ArrayList<Requirement>(choices);
		Collections.sort(choiceList);
		for(int i = 0; i < choiceList.size() - 2 ; i ++){
			result.append(choiceList.get(i).saveString());
			result.append(", ");
		}
		//handle the last 2 requirements (may have 0, 1, or 2 last ones
		//  depending on the number of choices.)
		if(choiceList.size() > 2){
			result.append(choiceList.get(choiceList.size() - 2).saveString());
			result.append(", or ");
			result.append(choiceList.get(choiceList.size() - 1).saveString());
		}
		else if (choiceList.size() == 2){
			result.append(choiceList.get(choiceList.size() - 2).saveString());
			result.append(", ");
			result.append(choiceList.get(choiceList.size() - 1).saveString());
		}
		else if(choiceList.size() == 1){
			result.append(choiceList.get(0).saveString());
		}
		else{ //choiceList.size() == 0
			return "()";
		}
		result.append(")");
		return result.toString();
	}


	//INFINITELOOPHAZARD
	/**
	 * see the REQUIREMENT SAVING AND READING TUTORIAL in the Requirement Class.
	 * Read this requirement from such a string.
	 * @param saveString
	 * @return
	 */
	public static Requirement readFrom(String saveString){
		try{
			return Requirement.readFromJSON(saveString);
		}
		catch(Exception e){
			Stack<String> tokens = tokenize(saveString);
			Requirement result = parse(tokens);
			if(!tokens.isEmpty()){
				throw new RuntimeException("End of string while parsing requirement");

			}
			return result;
		}

	}

	public static Stack<String> tokenize(String s){
		//Tokens are: 
		// '(' 
		// ')' 
		// '[0-9]+of' 
		// ',[or]*'
		// a terminal requirement's save string

		//remove all whitespace.
		s = s.replaceAll("\\s", "");
		//Add whitespace around tokens (we'll split on whitespace in a sec)
		s = s.replaceAll("\\(", " ( ");
		s = s.replaceAll("\\)", " ) ");
		s = s.replaceAll(",[or]*", " , ");
		// If you see the string "6of", replace it with "6of ".
		// Hopefully all other replaces will handle the space before the
		// first digit of the number (a parenthesis should preceed that digit).
		s = s.replaceAll("(?<=[0-9])of", "of ");

		s = s.trim();

		Stack<String> reversed = new Stack<String>();
		for(String token : s.split("\\s+")){
			reversed.push(token);
		}

		//Reverse the stack so that the first character comes out first.
		Stack<String> result = new Stack<String>();
		while(!reversed.isEmpty()){
			String t = reversed.pop();
			result.push(t);
		}

		return result;

	}

	public static Requirement parse(Stack<String> tokens){
		String next = tokens.pop();
		if(next.equals("(")){
			Requirement result = new Requirement();
			result.numToChoose = 1;
			do{
				result.addRequirement(parse(tokens));
				if(tokens.isEmpty()){
					throw new RuntimeException("Missing ) while parsing requirement");
				}
				next = tokens.pop();
			}while(next.equals(","));
			if(!next.equals(")")){
				throw new RuntimeException("Missing ) while parsing requirement");
			}
			return result;
		}
		else if(next.matches("[0-9]+of")){
			int numToChoose = Integer.parseInt(
					next.substring(0, next.length() - 2));
			Requirement temp = parse(tokens);
			if(! (temp instanceof Requirement)){
				throw new RuntimeException("Make sure to use parenthesis after saying \" n of \" ");
			}
			Requirement result = (Requirement)temp;
			result.numToChoose = numToChoose;
			return result;
		}
		else{
			return TerminalRequirement.readFrom(next);
		}
	}

	/**
	 * Example inputs:
	 * 
	 *  "ECN-111 and MTH-141 or MTH-150 and ECN-225, MTH-241 or MTH-340"
	 *  "ACC-111, ECN-111 or 225, MTH-141 or 150"
	 *  "CSC-105, BIO-111, CHM-110, EES-110, EES-112, EES-113, MTH-141, MTH-150, or PHY-111"
	 *  
	 *  This method has to be very careful because syntax might be
	 *  department dependent. The above examples show a clear syntax,
	 *  but it's not clear that all strings following that syntax are
	 *  definitely what they seem. 
	 *  
	 *  
	 *  
	 * @param furmanString
	 * @return
	 */
	public static Requirement readFromFurmanPrereqs(String furmanString){
		if(furmanString.matches("\\w\\w\\w\\-\\w\\w\\w")){
			return Requirement.readFrom("(" + furmanString + ")");
		}
		else{
			throw new RuntimeException("readFromFurmanPrereqs in Requirement is not fully implemented yet");
		}
	}





	public static final String[] SAVE_DELIMETERS = {" of ","; \t "," Completed; DDN:"};
	/**
	 * Read from a JSON save string (kept for backwards compatability)
	 * @param s
	 * @return
	 */
	public static Requirement readFromJSON(String s) {
		//Get the list of objects in this string, after chopping off the first and last characters 
		// (hopefully "{" and "}" ) and ignoring anything outside brackets ("{" or "}").
		Iterator<String> i = SaverLoader.fromJSON(SaverLoader.peel(s)).iterator();

		//Peal off each piece of data from this iterator of strings.
		int numToChoose = Integer.parseInt(SaverLoader.peel(i.next()));
		Iterable<String> prefixes = SaverLoader.fromJSON(SaverLoader.peel(i.next()));
		ArrayList<Prefix> choices = new ArrayList<Prefix>();
		for(String p : prefixes){
			choices.add(Prefix.readFromJSON(p));
		}
		int doubleDipNumber = Integer.parseInt(SaverLoader.peel(i.next()));
		String name = SaverLoader.peel(i.next());
		if(name.equals("null")){
			name = null;
		}
		Requirement result = new Requirement(choices.toArray(new Prefix[choices.size()]), numToChoose);

		result.setName(name);
		return result;
	}


	public static void testReading(){
		String[] tests = new String[]{
				"(MTH 110)",
				"(MTH-110)",
				"MTH110",
				"2 of (MTH 110, MTH 120, MTH 130)",
				"2 of (MTH-110, MTH 120, MTH 130)",
				"2 of (ACC-110, MTH 120, MTH 130)",
				"2 of (MTH-110, MTH 120, or MTH 130)",
				"2 of (MTH 110, (MTH 120), (MTH 130))",
				"2 of (MTH 110, (MTH 120, MTH 130))",
				"2 of (MTH 110, (2 of (MTH 120, MTH 130)))",
				"3 of (MTH 110, MTH 120, MTH 130)",
				"1 of ( 2 of (MTH - 110, MTH120 ) , MTH 140, MTH 150, or MTH 160)",
				"2 of (BIO 110, BIO 112, BIO 120)",
				"3 of (BIO 110, BIO 112, BIO 120, BIO 130)",
				"1 of (MTH 150, 2 of (MTH 145, MTH 120))"
		};
		Prefix[] prefixes = new Prefix[]{
				new Prefix("MTH", "110"),
				new Prefix("MTH", "120"),
				new Prefix("MTH", "130"),
				new Prefix("MTH", "140"),
				new Prefix("MTH", "150"),
				new Prefix("MTH", "160")
		};
		ArrayList<ScheduleElement> takens = new ArrayList<ScheduleElement>();
		takens.add(new PrefixHours(prefixes[0], 4));//MTH 110
		takens.add(new PrefixHours(prefixes[1], 4));//MTH 120

		System.out.print("Taken prefixes: ");
		for(ScheduleElement p : takens){
			System.out.print(p + " ");
		}
		System.out.println();
		System.out.println();

		for(String toRead : tests){
			boolean needsToBeShown = false;
			Requirement r = Requirement.readFrom(toRead);
			boolean complete = r.isComplete(takens, 0, true);
			double percentComplete = r.percentComplete(takens, true);
			int minLeft = r.minMoreNeeded(takens, true);
			
			double tol = Double.MIN_VALUE * 10000;
			if(r.storedIsComplete != complete){
				needsToBeShown = true;
			}
			if(r.storedPercentComplete != percentComplete){
				needsToBeShown = true;
			}

			needsToBeShown = true;


			if(needsToBeShown){
				System.out.println("ReadingFrom \"" +toRead + "\"");
				System.out.println("Complete?" + complete + "/" + r.storedIsComplete);
				System.out.println("Percent Complete:" + percentComplete + "/" + r.storedPercentComplete);
				System.out.println("minLeft:" + minLeft + "/" + r.storedCoursesLeft);
				System.out.println();
			}
			
		}
		System.out.println("Finished testing");
	}

	
	public static void main(String[] args){
		testReading();
	}


	


}