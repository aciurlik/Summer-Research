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
	String name;
	boolean storedIsComplete;
	int storedCoursesLeft;
	double storedPercentComplete;
	
	//Used to check if %complete is close to 0.
	public static final double tolerance = 1000 * Double.MIN_VALUE;
	
	
	public Requirement(){
		this.choices = new HashSet<Requirement>();
		this.numToChoose = 1;
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
	
	
	public void setName(String name){
		this.name = name;
	}
	public String getName() {
		return this.name;
	}
	
	
	
	private Object[] separate(Iterable<ScheduleElement> taken){
		HashSet<Prefix> takenPrefixes = new HashSet<Prefix>();
		int numPlanned = 0;
		for(ScheduleElement s : taken){
			Prefix p = s.getPrefix();
			if(p!= null){
				takenPrefixes.add(p);
			}
			else{
				if(s == this){
					//TODO figure out about subset requirements.
					numPlanned ++;
				}
			}
		}
		return new Object[]{takenPrefixes, numPlanned};
	}
	
	//INFINITELOOPHAZARD
	/**
	 * Check whether this set of schedule elements completes this requirements.
	 * If storeValue is true, this requirement will store the value it calculates
	 *   in the field storedCompletionValue.
	 * @param taken
	 * @param storeValue
	 * @return
	 */
	public boolean isComplete(Iterable<ScheduleElement> taken, boolean storeValue){
		
		//Find all the prefixes,
		// and count how many things will magically satisfy this requirement
		Object[] separated = separate(taken);
		HashSet<Prefix> takenPrefixes = (HashSet<Prefix>)separated[0];
		int numPlanned = (int) separated[1];
		return isComplete(takenPrefixes, numPlanned, storeValue);
	}
	
	//INFINITELOOPHAZARD
	/** 
	 * Check if these prefixes satisfy this requirement.
	 * Requirements are optimists - if this set of taken things
	 *  has any shot of satisfying this requirement, then the requirement.
	 *  will say that it does. 
	 * @param taken
	 * @return
	 */
	private boolean isComplete(HashSet<Prefix> taken, int numPlannedLater, boolean storeValue){
		boolean result = minCoursesNeeded(taken, numPlannedLater, storeValue) <= 0;
		if(storeValue){
			this.storedIsComplete = result;
		}
		return result;
	}
	
	//INFINITELOOPHAZARD
	/**
	 * Find the minimum number of courses you would need to completely
	 * satisfy this requirement, given this set of things you've already taken.
	 * @param taken
	 * @param storeValue
	 * @return
	 */
	public int minCoursesNeeded(Iterable<ScheduleElement> taken, boolean storeValue){
		Object[] separated = separate(taken);
		HashSet<Prefix> takenPrefixes = (HashSet<Prefix>)separated[0];
		int numPlanned = (int)separated[1];
		return minCoursesNeeded(takenPrefixes, numPlanned, storeValue);
	}
	
	//INFINITELOOPHAZARD
	/**
	 * Find the minimum number of courses you would need to completely
	 * satisfy this requirement, given this set of things you've already taken.
	 */
	private int minCoursesNeeded(HashSet<Prefix> taken, int numPlannedLater, boolean storeValue){
		int result = fastestCompletionSet(taken).size() - numPlannedLater;
		if(storeValue){
			this.storedCoursesLeft = result;
		}
		return result;
	}
	
	//INFINITELOOPHAZARD
	/** 
	 * Find the set of prefixes that would most quickly complete this requirement
	 * @return
	 */
	public HashSet<Prefix> fastestCompletionSet(HashSet<Prefix> taken){
		//If you have n to choose of k choices, then you need the 
		// n fastest completion sets from all your choices.
		ArrayList<HashSet<Prefix>> subSets = new ArrayList<HashSet<Prefix>>();
		for(Requirement r : choices){
			subSets.add(r.fastestCompletionSet(taken));
		}
		Collections.sort(subSets,new Comparator<HashSet>(){
			@Override
			public int compare(HashSet o1, HashSet o2) {
				return o1.size() - o2.size();
			}
		});
		HashSet<Prefix> result = new HashSet<Prefix>();
		for(int i = 0; i < this.numToChoose ; i ++){
			result.addAll(subSets.get(i));
		}
		return result;
	}
	
	
	/**
	 *  Find, given the best case scenario, the maximum % complete this
	 *  requirement could be given this collection of schedule elements.
	 * @param taken
	 * @param storeValue
	 * @return
	 */
	public double percentComplete(Iterable<ScheduleElement> taken, boolean storeValue){
		Object[] separated = separate(taken);
		HashSet<Prefix> takenPrefixes = (HashSet<Prefix>) separated[0];
		int numPlanned = (int)separated[1];
		return percentComplete(takenPrefixes, numPlanned, storeValue);
	}
	
	
	//INFINITELOOPHAZARD
	/** 
	 * Find what % this requirement is complete if you get the best-case scenario for
	 * each of the numPlannedLater courses.
	 * @param taken
	 * @param numPlannedLater
	 * @param storeValue
	 * @return
	 */
	public double percentComplete(HashSet<Prefix> taken, int numPlannedLater, boolean storeValue){
		//While this requirement isn't 100% complete, add numPlannedLater
		// imaginary courses to the working set.
		HashSet<Prefix> workingSet = new HashSet<Prefix>(taken);
		for(int i = 0; i < numPlannedLater ; i ++){
			if(Math.abs(percentComplete(workingSet) - 1.0) < tolerance ){
				return 1.0;
			}
			Prefix next = findBestToTake(workingSet);
			workingSet.add(next);
		}
		double result = percentComplete(workingSet);
		if(storeValue){
			storedPercentComplete = result;
		}
		return result;
	}
	
	//INFINITELOOPHAZARD
	/**
	 * Figure out what % complete this requirement is.
	 */
	public double percentComplete(HashSet<Prefix> taken){
		//We need the top numToChoose percents.
		// Once we find these n values, say a, b, and c, we want
		// 1/3 a + 1/3 b + 1/3 c, or in general,
		// sum( largest n subPercents) / numToChoose.
		ArrayList<Double> subPercents = new ArrayList<Double>();
		for(Requirement r : this.choices){
			subPercents.add(r.percentComplete(taken));
		}
		Collections.sort(subPercents);
		double miniPercent = 0;
		for(int i = 0 ; i < subPercents.size() && i < numToChoose ; i ++){
			int index = subPercents.size() - (i + 1);
			miniPercent += subPercents.get(index);
		}
		return ((double)miniPercent)  / numToChoose;
	}
	
	//INFINITELOOPHAZARD
	/** TODO needs to be tested
	 * Find the prefix that would bring this requirement closest to completion.
	 * Return the prefix and the % it would add in an Object[]
	 * @param taken
	 * @return
	 */
	public Prefix findBestToTake(HashSet<Prefix> taken){
		for(Prefix p : fastestCompletionSet(taken)){
			return p;
		}
		return null;
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
		String result = this.saveString();
		if(this.numToChoose == 1){
			result = result.substring(1, result.length() - 1);
		}
		return result;
	}
	

	@Override
	public ArrayList<Requirement> getRequirementsFulfilled() {
		ArrayList<Requirement> result = new ArrayList<Requirement>(1);
		result.add(this);
		return result;
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
		Stack<String> tokens = tokenize(saveString);
		Requirement result = parse(tokens);
		if(!tokens.isEmpty()){
			throw new RuntimeException("End of string while parsing requirement");
		}
		return result;
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
	 *  ECN-111 and MTH-141 or MTH-150 and ECN-225, MTH-241 or MTH-340
	 *  ACC-111, ECN-111 or 225, MTH-141 or 150
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
				"3 of (BIO 110, BIO 112, BIO 120, BIO 130)"
		};
		Prefix[] prefixes = new Prefix[]{
				new Prefix("MTH", "110"),
				new Prefix("MTH", "120"),
				new Prefix("MTH", "130"),
				new Prefix("MTH", "140"),
				new Prefix("MTH", "150"),
				new Prefix("MTH", "160")
		};
		HashSet<Prefix> takens = new HashSet<Prefix>();
		takens.add(prefixes[0]);//MTH 110
		takens.add(prefixes[1]);//MTH 120
		
		System.out.print("Taken prefixes: ");
		for(Prefix p : takens){
			System.out.print(p + " ");
		}
		System.out.println();
		System.out.println();
		
		for(String toRead : tests){
			boolean needsToBeShown = false;
			System.out.println("ReadingFrom \"" +toRead + "\"");
			Requirement r = Requirement.readFrom(toRead);
			boolean complete = r.isComplete(takens, 0, true);
			double percentComplete = r.percentComplete(takens, 0, true);
			HashSet<Prefix> completionSet = r.fastestCompletionSet(takens);
			
			boolean completeAfter = r.isComplete(takens, completionSet.size(), false);
			double percentAfter = r.percentComplete(takens, completionSet.size(), false);
			
			
			double tol = Double.MIN_VALUE * 10000;
			if(complete && ( percentComplete < 1.0 - tol || completionSet.size() != 0)){
				needsToBeShown = true;
			}
			if(!complete && percentComplete > 1.0 + tol && completionSet.size() <= 0 ){
				needsToBeShown = true;
			}
			if(percentComplete < 1.0 - tol && completionSet.size() == 0 ){
				needsToBeShown = true;
			}
			if(percentComplete > 1.0 - tol && completionSet.size() != 0){
				needsToBeShown = true;
			}
			if(completeAfter != true){
				needsToBeShown = true;
			}
			if(percentAfter < 1.0 - tol){
				needsToBeShown = true;
			}
			
			if(r.storedIsComplete != complete){
				needsToBeShown = true;
			}
			if(r.storedCoursesLeft != completionSet.size()){
				needsToBeShown = true;
			}
			if(r.storedPercentComplete != percentComplete){
				needsToBeShown = true;
			}
			
			needsToBeShown = true;
			

			if(needsToBeShown){
				System.out.println("Complete?" + complete);
				System.out.println("Percent Complete:" + percentComplete);
				System.out.println("Fastest completion set:");
				for(Prefix p : completionSet){
					System.out.println("  " + p);
				}
				System.out.println("Complete with scheduled requirements?" + completeAfter);
				System.out.println("Percent complete with scheduled requirements:" + percentAfter);
				System.out.println("Stored courses left:" + r.storedCoursesLeft);
				
			}
			else{
				System.out.println("No obvious errors found");
			}
			System.out.println();
		}
	}

	public static void main(String[] args){
		testReading();
	}
	
	
}