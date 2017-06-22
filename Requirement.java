import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;


public class Requirement implements ScheduleElement, Comparable<Requirement>{
	private HashSet<Requirement> choices;
	int numToChoose; //the number of classes that must be taken.
	// If this is a "2 of these choices" requirement, then numToChoose
	// should be set to 2.
	// if this requirement usesCreditHours, then numToChoose is the number
	// of credit hours, and storedCoursesLeft stores the number of credit hours left.
	boolean usesCreditHours;
	String name;
	private int originalCoursesNeeded;
	private int storedCoursesLeft;
	
	boolean allCompletionSetsCalculated=false;
	HashSet<HashSet<TerminalRequirement>> allCompletionSets;

	//Used to check if %complete is close to 0.
	public static final double tolerance = 1000 * Double.MIN_VALUE;


	public Requirement(){
		this.choices = new HashSet<Requirement>();
		this.numToChoose = 1;
		this.originalCoursesNeeded = 1;
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
		this.recalcOriginalCoursesNeeded();
	}
	public void setNumToChoose(int n){
		if(n > choices.size()){
			throw new RuntimeException("Not enough choices to set numToChoose of " + n);
		}
		this.numToChoose = n;
		recalcOriginalCoursesNeeded();
	}
	
	public int storedCoursesLeft(){
		return storedCoursesLeft;
	}
	
	public void recalcOriginalCoursesNeeded(){
		this.originalCoursesNeeded = minMoreNeeded(new ArrayList<ScheduleElement>(), false);
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
	
	
	
	
	
	/**
	 * Given this ordered list of schedule elements, calculate the number
	 * of elements that are not courses but that satisfy this requirement.
	 * @param taken
	 * @return
	 */
	private int numPlannedLater(ArrayList<ScheduleElement> taken){
		int numPlanned = 0;
		for(ScheduleElement s : taken){
			if(s instanceof Requirement){
				if(s == this || s.equals(this)){
					numPlanned ++;
				}
			}
		}
		return numPlanned;
	}
	
	
	/**
	 * Check whether this set of schedule elements completes this requirements.
	 * If storeValue is true, this requirement will store the value it calculates
	 * from minMoreNeeded.
	 * @param taken
	 * @param storeValue
	 * @return
	 */
	public boolean isComplete(ArrayList<ScheduleElement> taken, boolean storeValue){
		return minMoreNeeded(taken, storeValue) <= 0;
	}
	public boolean storedIsComplete(){
		return this.storedCoursesLeft <= 0;
	}


	/**
	 *  Find the maximum % complete this
	 *  requirement could be given this collection of schedule elements.
	 * @param taken
	 * @param storeValue
	 * @return
	 */
	public double percentComplete(ArrayList<ScheduleElement> taken, boolean storeValue){
		double minNeeded = minMoreNeeded(taken, storeValue);
		double originalNeeded = this.originalCoursesNeeded;
		double result = (1.0 - (minNeeded * 1.0/originalNeeded));
		return result;
	}
	public double storedPercentComplete(){
		double result = (1.0 - (storedCoursesLeft * 1.0/  this.originalCoursesNeeded)); 
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
		for(int i = 0 ; i < taken.size(); i ++){
			if(taken.get(i) instanceof Requirement){
				Requirement r = (Requirement) taken.get(i);
				if(r.isTerminal()){
					taken.set(i, r.getTerminal());
				}
			}
		}
		return minMoreNeeded(taken, numPlanned, storeValue);
	}
	/**
	 * Find the minimum number of courses or credits you would need to completely
	 * satisfy this requirement, given this set of things you've already taken.
	 */
	private int minMoreNeeded(ArrayList<ScheduleElement> taken, int numPlannedLater, boolean storeValue){
		int result = minMoreNeeded(taken);
		result = result - numPlannedLater;
		result = Math.max(result, 0);
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
			int result = numToChoose;
			ArrayList<Requirement> completedSubreqs = new ArrayList<Requirement>();
			for(Requirement r : choices){
				if(r.isComplete(taken, false)){
					//you get to add the credits from it
					completedSubreqs.add(r);
				}
			}
			for(ScheduleElement e : taken){
				//if any of your completed subrequirements uses it, you can add its credits.
				if(e instanceof HasCreditHours){
					int credits = ((HasCreditHours)e).getCreditHours();
					boolean found = false;
					for(Requirement r : completedSubreqs){
						if(r.isSatisfiedBy(e)){
							found = true;
							break;
						}
					}
					if(found){
						result -= credits;
						if(result <= 0){
							return 0;
						}
					}
				}
			}
			return result;
		}
		else{ //this doesn't use credit hours.
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
	



	
	public boolean isTerminal(){
		if(this.choices.size() != 1){
			return false;
		}
		if(this.numToChoose != 1){
			return false;
		}
		for(Requirement r : choices){
			return r.isTerminal();
		}
		return false;
	}
	public TerminalRequirement getTerminal(){
		for(Requirement r : choices){
			return r.getTerminal();
		}
		return null;
	}
	
	public boolean isSatisfiedBy(ScheduleElement e) {
		if(e instanceof Requirement){
			if (((Requirement)e).alsoCompletes(this)){
				return true;
			}
		}
		return isSatisfiedBy(e.getPrefix());
	}
	//INFINITELOOPHAZARD
	public boolean isSatisfiedBy(Prefix p){
		for(Requirement r : this.choices){
			if(r.isSatisfiedBy(p)){
				return true;
			}
		}
		return false;
	}
	
	//INFINITELOOPHAZARD
	/**
	 * Return true if any strategy for completing this requirement
	 * will also complete r.
	 * Currently does not handle credit hours requirements.
	 * @param r
	 * @return
	 */
	public boolean alsoCompletes(Requirement r){
		if(this.equals(r)){
			return true;
		}
		if(!RequirementGraph.doesPlayNice(this, r)){
			return false;
		}
		long start = System.currentTimeMillis();
		CompletionSetsIter csi = new CompletionSetsIter(this);
		while(csi.hasNext()){
			HashSet<TerminalRequirement> nextCompletionSet = csi.next();
			//System.out.println(nextCompletionSet);
			if(!r.isCompletedBy(nextCompletionSet)){
				return false;
			}
			if(System.currentTimeMillis() - start > 50){
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean equals(Object o){
		if(!(o instanceof Requirement)){
			return false;
		}
		return equals((Requirement)o);
	}
	
	//INFINITELOOPHAZARD
	public boolean equals(Requirement r){
		if(r instanceof TerminalRequirement){
			return false;
		}
		if(r.numToChoose != this.numToChoose){
			return false;
		}
		for(Requirement choice : choices){
			if(!r.choices.contains(choice)){
				return false;
			}
		}
		return true;
	}
	
	//INFINITELOOPHAZARD
	@Override
	public int hashCode(){
		int total = 0;
		for(Requirement r : choices){
			total += r.hashCode();
		}
		return total;
	}
	
	//INFINITELOOPHAZARD
	public boolean isCompletedBy(HashSet<TerminalRequirement> s){
		int numSubComplete = 0;
		for(Requirement r : choices){
			if(r.isCompletedBy(s)){
				numSubComplete ++;
				if(numSubComplete >= numToChoose){
					return true;
				}
			}
		}
		return false;
	}
	
	
	public void updateAllStoredValues(ArrayList<ScheduleElement> taken){
		this.minMoreNeeded(taken, true);
	}
	
	
	
	
	
	
	
	
	
	

	
	
	/**
	 * This class iterates over a requirement's possible completion sets.
	 * Each completion set is a set of terminal requirements.
	 * @author dannyrivers
	 *
	 */
	private class CompletionSetsIter implements Iterator<HashSet<TerminalRequirement>>{
		SizedPowerSetsIter<Requirement> iter;
		ArrayList<Requirement> workingSetOfRequirementsToChoose;
		ArrayList<CompletionSetsIter> subIters;
		boolean isTerminal;
		boolean start;
		public CompletionSetsIter(Requirement r){
			
			if(r instanceof TerminalRequirement){
				start = true;
				isTerminal = true;
				workingSetOfRequirementsToChoose = new ArrayList<Requirement>();
				workingSetOfRequirementsToChoose.add(r);
			}
			else{
				isTerminal = false;
				this.iter = new SizedPowerSetsIter<Requirement> (r.choices, r.numToChoose);
				subIters = new ArrayList<CompletionSetsIter>(r.numToChoose);
				for(int i = 0; i < r.numToChoose ; i ++){
					subIters.add(null);
				}
				refreshSubiters();
				start = true;
			}
		}
		@Override
		public boolean hasNext() {
			if(start){
				return true;
			}
			if(isTerminal){
				return false;
			}
			if(!iter.hasNext()){
				return nextSubiterToIncrement() == -1;
			}
			else{
				return true;
			}
		}
		@Override
		public HashSet<TerminalRequirement> next() {
			if(start){
				start = false;
				return fullSet();
			}
			if(isTerminal){
				HashSet<TerminalRequirement> result = new HashSet<TerminalRequirement>();
				result.add((TerminalRequirement)workingSetOfRequirementsToChoose.get(0));
				return result;
			}
			int nextToIncrement = nextSubiterToIncrement();
			if(nextToIncrement == -1){
				//We've finished this working set of requirements, time 
				// to make a new one.
				refreshSubiters();
				return fullSet();
				
			}
			else{
				//increment the next subiter, while restarting any
				// subiter above it in the list
				subIters.get(nextToIncrement).next();
				for(int i = nextToIncrement - 1 ; i >= 0 ; i --){
					subIters.set(i, new CompletionSetsIter(workingSetOfRequirementsToChoose.get(i)));
				}
				return fullSet();
			}
		}
		
		private void refreshSubiters(){
			workingSetOfRequirementsToChoose = new ArrayList<Requirement>(iter.next());
			//System.out.println(workingSetOfRequirementsToChoose);
			for(int i = 0; i < workingSetOfRequirementsToChoose.size() ; i ++){
				Requirement chosenReq = workingSetOfRequirementsToChoose.get(i);
				//System.out.println(chosenReq instanceof TerminalRequirement);
				CompletionSetsIter newIter = new CompletionSetsIter(chosenReq);
				subIters.set(i,newIter);
			}
		}
		//If we think of each subiter as on its own row,
		// with the first subiter on the top row,
		// find the first subiter that does have a next.
		private int nextSubiterToIncrement(){
			for(int i = 0 ; i <subIters.size(); i ++){
				if(subIters.get(i) == null){
					return -1;
				}
				if(subIters.get(i).hasNext()){
					return i;
				}
			}
			return -1;
		}
		/**
		 * Returns one full set of terminal requirements without changing
		 * any state of the iter.
		 * @return
		 */
		public HashSet<TerminalRequirement> fullSet(){
			HashSet<TerminalRequirement> result = new HashSet<TerminalRequirement>();
			if(isTerminal){
				TerminalRequirement thisReq = (TerminalRequirement)workingSetOfRequirementsToChoose.get(0);
				result.add(thisReq);
				return result;
			}
			for(CompletionSetsIter csi : subIters){
				result.addAll(csi.fullSet());
			}
			return result;
		}
		@Override
		public void remove() {
		}
	}
	
	
	
	
	
	
	
	
	
	
	





	/////////////////////////////////
	/////////////////////////////////
	///// CompareTo 
	/////////////////////////////////
	/////////////////////////////////




	@Override
	/**
	 * This comparison method is used to sort a 
	 * requirementList displayed to the user.
	 */
	public int compareTo(Requirement o) {
		if(! (o instanceof Requirement)){
			//Requirements are greater than terminalRequirements.
			return 1;
		}
		Requirement other = (Requirement)o;
		//first compare based on whether they're complete, completed coming later
		if(this.storedCoursesLeft <= 0 && !(other.storedCoursesLeft <= 0)){
			return 1;
		}
		if(!(this.storedCoursesLeft <= 0) && other.storedCoursesLeft <= 0){
			return -1;
		}

		//Then compare based on % complete
		double percentDifference = this.storedPercentComplete() - other.storedPercentComplete();
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
		if(this.numToChoose == 1 && this.choices.size() == 1){
			result = result.substring(6, result.length() - 1);
		}
		return result;
	}
	
	@Override
	public String shortString() {
		String result = this.getDisplayString();
		if(result.length () > 20 && this.name == null){
			result = result.replaceAll(" ", "");
			if(result.length() > 40){
				result = result.substring(0, 40);
			}
		}
		return result;
	}


	@Override
	public ArrayList<Requirement> getRequirementsFulfilled(HashSet<Requirement> r) {
		ArrayList<Requirement> result = new ArrayList<Requirement>();
		for(Requirement otherReq : r){
			if(otherReq.equals(this)){
				result.add(otherReq);
			}
		}
		return result;
	}
	
	@Override
	public String toString(){
		return this.getDisplayString();
	}


	public int getCreditHours() {
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
	 * It should be unambiguous for any reader to see what this requirement is.
	 * @return
	 */
	public String saveString(){

		//Add the prefix
		StringBuilder result = new StringBuilder();
		result.append(numToChoose);
		if(this.usesCreditHours){
			result.append(" ch");
		}
		result.append(" of (");
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
			result.recalcOriginalCoursesNeeded();
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
		// first digit of the number (a parenthesis or comma should precede that digit).
		s = s.replaceAll("(?<=[0-9])of", "of ");
		s = s.replaceAll("(?<=[0-9])chof", "chof ");
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
		else if(next.matches("[0-9]+chof")){
			int numToChoose = Integer.parseInt(
					next.substring(0, next.length() - 4));
			Requirement temp = parse(tokens);
			if(! (temp instanceof Requirement)){
				throw new RuntimeException("Make sure to use parenthesis after saying \" n of \" ");
			}
			Requirement result = (Requirement)temp;
			result.numToChoose = numToChoose;
			result.usesCreditHours = true;
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

	
	public static void testAlsoCompletes(){
		String[] tests = new String[]{
				"(MTH-110)", // 1, 2
				"2 of (MTH-110, 2 of (MTH 120, MTH 130))",  //0, 2
				"3 of (MTH-110, MTH 120, MTH 130)", //0, 1
				"4 of (MTH-100, MTH-200, MTH-300, MTH-400, MTH-500)", //4
				"3 of (MTH-100, MTH-200, MTH-300, MTH-400)", //3
				"3 of (1 of (MTH-1, MTH-2, MTH-3, MTH-4), MTH-200, MTH-300, MTH-400)",//6, 7
				"2 of (1 of (MTH-1, MTH-2, MTH-3), MTH-400)",//5, 7
				"2 of (1 of (MTH-1, MTH-2, MTH-3), MTH-400, 1 of (MTH-1, MTH-4))"//5, 6
				
		};
		int[][] matchups = new int[][]
			{
				{0,1, 2},
				{0,1, 2},
				{0,1, 2},
				{4},
				{3},
				{6, 7},
				{5, 7},
				{5, 6}
			};
		
		
		for(int i = 0; i < matchups.length ; i ++){
			Requirement r = Requirement.readFrom(tests[i]);
			for(int j : matchups[i]){
				Requirement t = Requirement.readFrom(tests[j]);
				boolean forward = r.alsoCompletes(t);
				//boolean backward = t.alsoCompletes(r);
				System.out.println("fwd:" + forward  + " \"" + tests[i] + "\" alsoCompletes? \"" + tests[j] + "\"" ); 
			}
		}
		
	}

	public static void testReading(){
		String[] tests = new String[]{
				"(MTH 110)",
				//"(MTH-110)",
				"MTH110",
				//"MTH-110",
				//"MTH 110",
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
				"1 of (MTH 150, 2 of (MTH 145, MTH 120))",

				"2 ch of (MTH-110, MTH-120, MTH-130)",
				"2 chof (MTH-110, MTH-120, MTH-130)",
				"8 chof (2 of (MTH-110, ACC-110), MTH 120, MTH 330)",
				"8 chof (2 of (MTH-110, ACC-110), 1 of (MTH 120, MTH 800), MTH 330)"

		};
		
		ArrayList<ScheduleElement> takens = new ArrayList<ScheduleElement>();

		//takens.add(new PrefixHours(new Prefix("MTH", "110"), 4));
		//takens.add(TerminalRequirement.readFrom("MTH-110"));
		takens.add(Requirement.readFrom("(MTH-110)"));
		//takens.add(new PrefixHours(new Prefix("MTH", "120"), 4));
		//takens.add(TerminalRequirement.readFrom("MTH-120"));
		takens.add(Requirement.readFrom("(MTH-120)"));

		System.out.print("Taken prefixes: ");
		for(ScheduleElement p : takens){
			System.out.print(p + " ");
		}
		System.out.println();
		System.out.println();
		
		Requirement previous = new Requirement();

		for(String toRead : tests){
			boolean needsToBeShown = false;
			Requirement r = Requirement.readFrom(toRead);
			boolean equalToLast = r.equals(previous);
			previous = r;
			boolean complete = r.isComplete(takens, true);
			double percentComplete = r.percentComplete(takens, true);
			int minLeft = r.minMoreNeeded(takens, true);
			
			double tol = Double.MIN_VALUE * 10000;
			if(r.storedIsComplete() != complete){
				needsToBeShown = true;
			}
			if(r.storedPercentComplete() != percentComplete){
				needsToBeShown = true;
			}

			needsToBeShown = true;


			if(needsToBeShown){
				//System.out.println("ReadingFrom \"" +toRead + "\"");
				System.out.println("    got \"" + r.saveString() + "\"");
				//System.out.println("Equal to last?" + equalToLast);
				//System.out.println("Uses CH? " + r.usesCreditHours);
				System.out.println("Complete?" + complete );
				//System.out.println("Percent Complete:" + percentComplete + "/" + r.storedPercentComplete());
				//System.out.println("minLeft:" + minLeft + "/" + r.storedCoursesLeft);
				System.out.println();
			}
			
		}
		System.out.println("Finished testing");
	}

	
	public static void main(String[] args){
		testReading();
	}


	


}