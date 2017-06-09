import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;



public class Requirement implements Comparable<Requirement>, ScheduleElement, JSONable, RequirementInterface{
	HashSet<RequirementInterface> choices;
	int numToChoose; //the number of classes that must be taken.
	// If this is a "2 of these choices" requirement, then numToChoose
	// should be set to 2.
	String name;
	boolean storedCompletionValue;
	int storedCoursesLeft;
	double storedPercentComplete;
	
	public static final double tolerance = 1000 * Double.MIN_VALUE;
	/**
	 *
	 */
	public static Requirement testRequirement(){
		return new Requirement(new Prefix[]{new Prefix("MTH", 220)}, 1);
	}

	
	
	
	
	public Requirement(){
		this.choices = new HashSet<RequirementInterface>();
	}

	
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

	
	public RequirementInterface cloneRequirement() {
		Requirement newR = new Requirement();
		for(RequirementInterface lower : this.choices){
			newR.addRequirement(lower.cloneRequirement());
		}
		newR.name = this.name + "";
		return newR;
	}
	
	
	public void addRequirement(RequirementInterface r){
		this.choices.add(r);
	}
	
	
	
	public void setName(String name){
		this.name = name;
	}
	
	
	
	
	/**  TODO needs to be tested
	 * Check if these prefixes satisfy this requirement.
	 * Requirements are optimists - if this set of taken things
	 *  has any shot of satisfying this requirement, then the requirement.
	 *  will say that it does. 
	 * @param taken
	 * @return
	 */
	public boolean isComplete(HashSet<Prefix> taken, int numPlannedLater, boolean storeValue){
		boolean result = minCoursesNeeded(taken, numPlannedLater, storeValue) <= 0;
		if(storeValue){
			this.storedCompletionValue = result;
		}
		return result;
	}
	
	
	/** TODO needs to be tested
	 * Find the minimum number of courses you would need to completely
	 * satisfy this requirement, given this set of things you've already taken.
	 */
	public int minCoursesNeeded(HashSet<Prefix> taken, int numPlannedLater, boolean storeValue){
		int result = fastestCompletionSet(taken).size() - numPlannedLater;
		if(storeValue){
			this.storedCoursesLeft = result;
		}
		return result;
	}
	/**  TODO needs to be tested
	 * Find the set of prefixes that would most quickly complete this requirement
	 * @return
	 */
	public HashSet<Prefix> fastestCompletionSet(HashSet<Prefix> taken){
		//If you have n to choose of k choices, then you need the 
		// n fastest completion sets from all your choices.
		ArrayList<HashSet<Prefix>> subSets = new ArrayList<HashSet<Prefix>>();
		for(RequirementInterface r : choices){
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
	
	/** TODO needs to be tested
	 * Find what % this requirement is complete if you get the best-case scenario for
	 * each of the numPlannedLater courses.
	 * @param taken
	 * @param numPlannedLater
	 * @param storeValue
	 * @return
	 */
	public double percentComplete(HashSet<Prefix> taken, int numPlannedLater, boolean storeValue){
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
	
	/** TODO needs to be tested
	 * Figure out what % complete this requirement is.
	 */
	public double percentComplete(HashSet<Prefix> taken){
		ArrayList<Double> subPercents = new ArrayList<Double>();
		for(RequirementInterface r : this.choices){
			subPercents.add(r.percentComplete(taken));
		}
		Collections.sort(subPercents);
		int miniPercent = 0;
		for(int i = 0 ; i < subPercents.size() && i < numToChoose ; i ++){
			int index = subPercents.size() - (i + 1);
			miniPercent += subPercents.get(index);
		}
		return ((double)miniPercent)  / numToChoose;
	}
	
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


	
	
	
	//TODO Let the user decide which comparisons should come first.
	//This comparison method is used to sort the 
	// requirementList displayed to the user.
	@Override
	public int compareTo(Requirement other) {
		//first compare based on whether they're complete, completed coming later
		if(this.storedCompletionValue && !other.storedCompletionValue){
			return 1;
		}
		if(!this.storedCompletionValue && other.storedCompletionValue){
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
		ArrayList<Prefix> choiceList = new ArrayList<Prefix>(choices);
		Collections.sort(choiceList);
		if(choiceList.size() == 1){
			return choiceList.get(0).toString();
		}
		else{ //choices has length at least 2.
			String choicesString = "";
			for(Prefix p : choiceList){
				choicesString += p.toString() + ", ";
			}
			choicesString = choicesString.substring(0,choicesString.length() - 2);
			return String.format("%d of %s", numToChoose, choicesString);
		}
	}

	public static final String[] SAVE_DELIMETERS = {" of ","; \t "," Completed; DDN:"};

	
	/**
	 * Strings of the form
	 * (MTH 220, MTH 330, MTH 400) --> implied 'or' = 1 of
	 * 2 of (MTH 220, MTH 330)
	 * 2 of ( (MTH 150), 2 of (MTH 145, MTH 120) )
	 * ( MTH 150, 2 of (MTH 145, MTH 120) ) --> implied 'or' = 1 of
	 * (MTH 150)
	 * @return
	 */
	public String saveString(){
		
		//Add the prefix
		StringBuilder result = new StringBuilder();
		if(this.numToChoose == 1){
			result.append("(");
		}
		else{
			result.append(numToChoose + "of (");
		}
		//Add the guts of this requirement - each sub-requirement
		int index = 0;
		int size = choices.size();
		for(RequirementInterface r : this.choices){
			result.append(r.saveString() + ", ");
			if(index == size - 1 && size > 1){
				//This would be where you could add 'or' to the string
			}
			index += 1;
		}
		//add the postfix
		result.append(")");
		return result.toString();
	}

	public RequirementInterface readFrom(String saveString){
		Stack<String> tokens = tokenize(saveString);
		return parse(tokens);
	

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
		
		Stack<String> result = new Stack<String>();
		for(String token : s.split("\\s+")){
			result.push(token);
		}
		return result;
		
	}
	
	public static RequirementInterface parse(Stack<String> tokens){
		String next = tokens.peek();
		if(next.equals("(")){
			Requirement result = new Requirement();
			result.numToChoose = 1;
			result.addRequirement(parse(tokens));
			next = tokens.pop();
			while(next.equals(",")){
				result.addRequirement(parse(tokens));
				next = tokens.pop();
			}
			if(!next.equals(")")){
				throw new RuntimeException("missing ) while parsing requirement");
			}
			return result;
		}
		else if(next.matches("[0-9]+of")){
			int numToChoose = Integer.parseInt(
					next.substring(0, next.length() - 2));
			tokens.pop();
			RequirementInterface temp = parse(tokens);
			if(! (temp instanceof Requirement)){
				throw new RuntimeException("Make sure to use parenthesis after saying \" n of \" ");
			}
			Requirement result = (Requirement)temp;
			result.numToChoose = numToChoose;
			return result;
		}
		else{
			return new TerminalRequirement(tokens.pop());
		}
	}

	@Override
	public ArrayList<Requirement> getRequirementsFulfilled() {
		ArrayList<Requirement> result = new ArrayList<Requirement>();
		result.add(this);
		return result;
	}

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

	public int getCreditHours() {
		// TODO Auto-generated method stub
		return 4;
	}

	@Override
	public String saveAsJSON() {
		return String.format(
				"{Number to Choose: {%s} Choices: {%s} DDN: {%s} Requirement Name:{%s}}",
				numToChoose,
				SaverLoader.toJSON(new ArrayList<JSONable>(choices)),
				doubleDipNumber,
				name);
	}

	public static void main(String[] args){
		String test = "1 of (MTH 140, MTH 240, or MTH 330)";
		for(String s : Requirement.tokenize(test)){
			System.out.println(s);
		}
	}


}