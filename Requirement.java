import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Function;


/**
 * 
 * Blurb written 7/18/2017
 * Last updated 7/18/2017
 * 
 * General use
 * 
 * The requirement class represents a requirement for some number of courses. Requirements
 * are used in Majors and as prerequsites, so a major might be a list of requirements, and
 * a prereq might be requirement for some particular course. Requirements are in 
 * the DATA group of classes.
 * 
 * 
 * There are 2 essential components of each requirement - the number to choose, and the choices.
 * To make a requirement for "MTH-110 and MTH-220", you would make a requirement with numToChoose = 2
 * and choices = [MTH-110, MTH-220]. If the requirement is for a certain number of credit hours, 
 * numToChoose represents this number - the requirement "4 ch of (MTH > 100)" will have numToChoose = 4
 * and choices = [MTH > 100].
 * 
 * Terminal Requirements VS Requirements
 * 
 * Every element of choices must itself be a requirement - there is a special subclass 
 * TerminalRequirement that represents a requirement with no subchoices. When saving and reading,
 * every set of parenthesis implies a requirement, and terminal requirements 
 * never have a set of parenthesis. Thus, "MTH-110" is a TerminalRequirement 
 * but 1 of (MTH-110) is a requirement (with the terminal MTH-110 as its only choice).
 * This parenthesis rule does not apply for strings displayed to a user of the program.
 * 
 * Loop hazards
 * 
 * There are a number of recursive methods in requirement, and these work by calling the same method
 * on each of the Requirements in choices. For example, the method isSatisfiedBy(ScheduleElement e)
 * checks if e satisfies any of this requirement's choices, and if so returns true. These methods
 * can easily cause errors or infinite loops if they are not carefully overwritten 
 * in the class Terminal Requirement. Because of this hazard, for any method that recurses 
 * in this way, please add the string //INFINITELOOPHAZARD just before the method.
 * 
 * To reduce the number of such hazardous methods, much of the recursive work in Requirement is done
 * by the method minMoreNeeded(ListOfElements). This method appropriately subtracts from 
 * numToChoose 
 * 
 * 
 *  
 *
 */
public class Requirement implements ScheduleElement, Comparable<Requirement>, HasCreditHours,  java.io.Serializable{
	private static final long serialVersionUID = 1L;
	private HashSet<Requirement> choices;
	int numToChoose; //the number of classes that must be taken.
	// If this is a "2 of these choices" requirement, then numToChoose
	// should be set to 2.
	// if this requirement usesCreditHours, then numToChoose is the number
	// of credit hours, and storedCoursesLeft stores the number of credit hours left.
	private boolean usesCreditHours;
	String name;

	// These stored fields are used to reduce computation time for expensive recursive methods.
	private int originalNumberNeeded; //stores minMoreNeeded(emptyList)
	private int storedNumberLeft; // this field is essentially a holder for passing data between 
	//the DATA side and the GUI side. Data classes, like schedule, will call
	// an expensive recursive method, like minMoreNeeded, and tell this requirement to
	// store the result to be used for display. When GUI classes display a requirement, 
	// they use this field to determine how complete it is (using the getStoredXXXX methods).


	public static int defaultCreditHours =4;


	//Used to check if percentComplete is close to 0.
	public static final double tolerance = 1000 * Double.MIN_VALUE;


	public Requirement(){
		this.choices = new HashSet<Requirement>();
		this.numToChoose = 1;
		this.originalNumberNeeded = 1;
		usesCreditHours = false;
	}


	/**
	 * An old constructor for backwards compatability.
	 * Can be removed (will just require a bunch of other edits)
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


	/////////////////////////
	/////////////////////////
	////Getters and setters
	/////////////////////////
	/////////////////////////
	@SuppressWarnings("unused")
	private boolean ___GettersAndSetters_________;

	public void addChoice(Requirement r){
		this.choices.add(r);
		this.recalcOriginalNumberNeeded();
		this.storedNumberLeft = this.originalNumberNeeded;
	}

	/**
	 * This method should be called after all choices are added, so that 
	 * we can check if your numToChoose is less than choices.size().
	 * @param n
	 */
	public void setNumToChoose(int n){
		if(n > choices.size()){
			throw new RuntimeException("Not enough choices to set numToChoose of " + n);
		}
		this.numToChoose = n;
		recalcOriginalNumberNeeded();
		this.storedNumberLeft = this.originalNumberNeeded;
	}



	@SuppressWarnings("unused")
	private boolean ______storedGetters_________;


	/**
	 * Return a stored estimate of the number of courses this requirement still
	 * needs in order to be complete. Automatically takes creditHours requirements
	 * into account.
	 * @return
	 */
	public int getStoredCoursesLeft(){
		if(this.usesCreditHours()){
			return storedNumberLeft / 4;
		}
		return storedNumberLeft;
	}

	/**
	 * Return the number needed - may be a number of courses or a number of
	 * credit hours.
	 * @return
	 */
	public int getStoredNumberLeft(){
		return this.storedNumberLeft;
	}

	public double getStoredPercentComplete(){
		double result = (1.0 - (storedNumberLeft * 1.0/  this.originalNumberNeeded)); 
		return result;
	}
	public boolean getStoredIsComplete(){
		return this.storedNumberLeft <= 0;
	}


	/**
	 * Return an estimate of the number of courses originally needed,
	 * assuming each course is 4 credit hours.
	 * @return
	 */
	public int getOriginalCoursesNeeded(){
		if(this.usesCreditHours){
			return this.originalNumberNeeded / 4;
		}
		else{
			return this.originalNumberNeeded;
		}
	}
	/**
	 * Return the number needed, may represent courses or credit hours.
	 * @return
	 */
	public int getOriginalNumberNeeded(){
		return this.originalNumberNeeded;
	}



	public boolean usesCreditHours(){
		return usesCreditHours;
	}

	public void setHoursNeeded(int hours){
		this.numToChoose = hours;
		this.usesCreditHours = true;
	}


	/**
	 * How many credit hours will this requirement grant, if this requirement
	 * is a schedule element dragged into the semester?
	 * 
	 */
	public int getCreditHours() {
		if(this.isTerminal()){
			return this.getTerminal().getCreditHours();
		}
		else{
			return defaultCreditHours;
		}
	}


	public void setName(String name){
		this.name = name;
	}
	public String getName() {
		return this.name;
	}





















	@SuppressWarnings("unused")
	private boolean ___RecursiveMethods_________;
	/////////////////////
	/////////////////////
	///// Recursive methods
	/////////////////////
	/////////////////////
	// This section should include all methods that recurse on each member of choices, 
	// and thus need to be overwritten in TerminalRequirement to prevent infinite loops.
	//
	// This section may also include other methods related to the recursive methods,
	// like the public version of minMoreNeeded.
	//







	//The following methods, until you reach minMoreNeeded(taken) , use MinMoreNeeded
	// to do the recursive work.
	/**
	 * recalculate how many courses it would take to complete this requirement
	 * if you started over from scratch.
	 */
	public void recalcOriginalNumberNeeded(){
		int result = minMoreNeeded(new ArrayList<ScheduleElement>(), false);
		this.originalNumberNeeded = result;
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


	/**
	 *  Find the maximum % complete this
	 *  requirement could be given this collection of schedule elements.
	 * @param taken
	 * @param storeValue
	 * @return
	 */
	public double percentComplete(ArrayList<ScheduleElement> taken, boolean storeValue){
		double minNeeded = minMoreNeeded(taken, storeValue);
		double originalNeeded = this.originalNumberNeeded;
		double result = (1.0 - (minNeeded * 1.0/originalNeeded));
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
		int result =  minMoreNeeded(taken);
		result = Math.max(result, 0);
		if(storeValue){
			this.storedNumberLeft = result;
		}
		return result;
	}


	//INFINITELOOPHAZARD
	/**
	 * WARNING WARNING WARNING
	 * This method should not be called by outside classes.
	 * Use minMoreNeeded(taken, boolean storeValue) instead.
	 * 
	 * Only Requirement and TerminalRequirement should use it.
	 * (it has to be protected so TerminalRequirement can overwrite it.)
	 * 
	 * Figure out how complete this requirement is if we've 
	 * taken all the schedule elements in taken.
	 * 
	 * Includes scheduled instances of this requirement, but may ignore subset
	 * requirements (i.e., the element "1 of (A thru K)" would not count towards satisfying
	 * the req "1 of (A thru Z)".)
	 * However, it will catch terminal requirement subsets. 
	 * (so "1 of A" will count towards "1 of (A thru Z)")
	 * 
	 * @param taken
	 * @return
	 */

	protected int minMoreNeeded(ArrayList<ScheduleElement> taken){
		if(this.isTerminal()){
			return this.getTerminal().minMoreNeeded(taken);
		}
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
				//Check if you planned any instances later out
				if(this.equals(e)){
					result -= Requirement.defaultCreditHours;
				}
				else{
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
			}
			return result;
		}
		else{//this doesn't use credit hours

			//Special case for requirements of the form
			// "5 of these, at least 3 like this"
			// Requirements like this should calculate minMoreNeeded differently from normal.

			ArrayList<Requirement> isAtLeastRequirementPairs = atLeastRequirementPairs();
			if(isAtLeastRequirementPairs.size() != 0){
				Requirement superset = isAtLeastRequirementPairs.get(0);
				Requirement subset = isAtLeastRequirementPairs.get(1);
				//In we're looking at a requirement of the form
				// "5 of these, at least 3 like this,"
				// we'll use the following algorithm
				//Split your requirement into 2 pieces, the one with 5 needed (superset)
				//		and the one with 3 needed (subset).
				// Imagine taking 6 courses from superset, 2 of which counted for subset. 
				// We can infer that you took 4 superset-only and 2 subset-only courses.
				//   Because 5-3 is 2, we can use at most 2 of those 4 superset-only courses
				// So we get to count 2 courses from subset and min(4, 2) courses from superset.
				// We need 5 total courses (comes from superset's originalNeeded).
				// So min more needed should be 5-4 = 1.

				//I'll put the numbers from the example next to 
				// each of the variables below so that you can
				// follow along with the algorithm.
				int moreNeededSuperset = superset.minMoreNeeded(taken); //0
				int originalNeededSuperset = superset.originalNumberNeeded; //5
				int moreNeededSubset = subset.minMoreNeeded(taken); //1
				int originalNeededSubset = subset.originalNumberNeeded; //3
				int supersetTaken = originalNeededSuperset - moreNeededSuperset; //6 in the example 
				//(here supersetTaken would be 5 instead, but there is no difference in the algorithm.)
				int subsetTaken = originalNeededSubset - moreNeededSubset;//2
				int supersetOnly = supersetTaken - subsetTaken; //4 in example
				int maxFromSupersetOnly = originalNeededSuperset - originalNeededSubset;// 5-3 = 2 in example
				int actualFromSupersetOnly = Math.min(supersetOnly, maxFromSupersetOnly);// max(4, 2) in example
				int totalTaken = subsetTaken + actualFromSupersetOnly; //4 total taken
				int result = originalNeededSuperset - totalTaken; //5 - 4 = 1 more needed
				//Check if you planned any instances later out
				for(ScheduleElement e : taken){
					if(this.equals(e)){
						result --;
					}
				}
				return result;
			}

			//Look at each of your sub requirements, figure out how
			// many each of them needs.
			// Find the best numToChoose that you could pick, and
			// use the minMoreNeeded from those numToChoose.
			ArrayList<Integer> otherNums = new ArrayList<Integer>();

			for(Requirement r : choices){
				otherNums.add(r.minMoreNeeded(taken));
			}
			Collections.sort(otherNums);
			int result = 0;
			for(int i = 0; i < numToChoose ; i ++){
				result += otherNums.get(i);
			}

			//Check if you planned any instances later out
			for(ScheduleElement e : taken){
				if(this.equals(e)){
					result --;
				}
			}

			return result;
		}
	}


	public boolean isAtLeastRequirement(){
		return !atLeastRequirementPairs().isEmpty();
	}
	/**
	 * Check if this requirement looks like
	 * "5 of A-Z, at least 3 of A-K". 
	 * If it is, put the superset in result.get(0)
	 * and put the subset in result.get(1)
	 * Otherwise return an empty array list.
	 * @return
	 */
	public ArrayList<Requirement> atLeastRequirementPairs(){
		if(this.numToChoose == 2 && this.choices.size() == 2){

			ArrayList<Requirement> choices = new ArrayList<Requirement>();
			for(Requirement r : this.choices){
				choices.add(r);
			}
			boolean swapNeeded = false;
			if(choices.get(0).isSubset(choices.get(1))){
				if(choices.get(1).isSubset(choices.get(0))){
					//they're equal, so 
					// the one with more to choose has to be the superset
					if(choices.get(0).numToChoose<choices.get(1).numToChoose){
						swapNeeded = true;
					}
					else{
						swapNeeded = false;
					}
				}
				else{
					swapNeeded = true;
				}
				if(swapNeeded){
					Requirement holder = choices.get(0);
					choices.set(0, choices.get(1));
					choices.set(1, holder);
				}
				return choices;
			}
			else if(choices.get(1).isSubset(choices.get(0))){

				//1 is a subset of 0
				// so we put 1 second
				return choices;
			}
		}
		return new ArrayList<Requirement>();
	}



	//INFINITELOOPHAZARD
	/**
	 * Check if this requirement if just a nested list of 1 terminal, like
	 * 1 of (1 of (1 of (MTH-110)))
	 * If so, you can use getTerminal() to see the TerminalRequirement for it.
	 * @return
	 */
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

	//INFINITELOOPHAZARD
	/**
	 * Check if e completes any subrequirement of this requirement.
	 * 
	 * Equivalently, returns true if minMoreNeeded(e) < minMoreNeeded(emptyList).
	 * 
	 * TODO make a test method for this.
	 * 
	 * @param e
	 * @return
	 */
	public boolean isSatisfiedBy(ScheduleElement e) {
		if(e instanceof Requirement){
			Requirement r = (Requirement)e;
			if (r.isTerminal()){
				for(Requirement c : choices){
					if(c.isSatisfiedBy(r.getTerminal())){
						// the recursive call will either use this method
						// if c is a requirement, or the 
						// isSatisfiedBy(TerminalRequirement other) method
						// if c is a terminal.
						return true;
					}
				}
			}
			else if(r.equals(this)){
				return true;
			}
			return false;
		}
		else{
			return isSatisfiedBy(e.getPrefix());
		}
	}

	//INFINITELOOPHAZARD
	/**
	 * See if this prefix could ever possibly help this 
	 * requirement become more satisfied.
	 * 
	 * Formally, this is equivalent to checking if
	 *   minMoreNeeded(emptyset) > minMoreNeeded({p})
	 * @param p
	 * @return
	 */
	public boolean isSatisfiedBy(Prefix p){
		for(Requirement r : this.choices){
			if(r.isSatisfiedBy(p)){
				return true;
			}
		}
		return false;
	}


	/**
	 * Check for equality.
	 * 
	 * Note that the requirement "1 of (1 of (MTH 150))" is equal to the 
	 * requirement "MTH 150", as both are terminals.
	 */
	@Override
	public boolean equals(Object o){
		if(!(o instanceof Requirement)){
			return false;
		}
		return equals((Requirement)o);
	}

	//INFINITELOOPHAZARD
	public boolean equals(Requirement r){
		if(r == null){
			return false;
		}
		//use terminal equality if 
		if(r instanceof TerminalRequirement){
			if(this.isTerminal()){
				return this.getTerminal().equals(r);
			}
			return false;
		}
		//Check numTochoose are equal
		if(r.numToChoose != this.numToChoose){
			return false;
		}
		//check that this contains that and that contains this.
		if(!this.choices.containsAll(r.choices)){
			return false;
		}
		if(!r.choices.containsAll(r.choices)){
			return false;
		}
		//check for names being equal
		if(this.name != null && !this.name.equals(r.name)){
			return false;
		}
		if(r.name != null && !r.name.equals(this.name)){
			return false;
		}
		return true;
	}

	//INFINITELOOPHAZARD
	@Override
	public int hashCode(){
		int total = numToChoose;
		for(Requirement r : choices){
			total += r.hashCode();
		}
		return total;
	}






	/**
	 * Ensure that all stored values (storedIsComplete(), storedPercentComplete(), storedMinMoreNeeded())
	 * reflect the values that would be returned based on this taken set
	 * i.e. unless overwritten storedIsComplete() will return the same value as isComplete(taken).
	 * @param taken
	 */
	public void updateAllStoredValues(ArrayList<ScheduleElement> taken){
		this.minMoreNeeded(taken, true);
	}



	@SuppressWarnings("unused")
	private boolean ___Comparisons_________;

	/////////////////////////////////
	/////////////////////////////////
	///// Comparisons
	/////////////////////////////////
	/////////////////////////////////




	@Override
	/**
	 * Compare by, in order:
	 * 		Terminal or not (term < req)
	 * 		stored is complete
	 * 		stored percent complete
	 * 		numToChoose
	 * 		choices.size()
	 * 
	 * note - a return of 0 may NOT indicate that these 
	 * 		requirements are equal. This compareTo
	 * 		is NOT CONSISTENT WITH EQUALS.
	 * 
	 * TODO should this be made consistent with equals?
	 */
	public int compareTo(Requirement o) {
		if(o.isTerminal()){
			//Requirements are greater than terminalRequirements.
			return 1;
		}
		Requirement other = (Requirement)o;
		//first compare based on whether they're complete, completed coming later
		if(this.getStoredIsComplete() && ( !other.getStoredIsComplete() )){
			return 1;
		}
		if(( !this.getStoredIsComplete()) && other.getStoredIsComplete()){
			return -1;
		}

		//Then compare based on % complete
		double percentDifference = this.getStoredPercentComplete() - other.getStoredPercentComplete();
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


		//number of choices
		if(this.choices.size() != other.choices.size()){
			return this.choices.size() - other.choices.size();
		}
		return 0;

	}

	/**
	 * Check (while avoiding large computation times)
	 * whether this requirement is obviously a subset of 
	 * the other requirement. Only handles the case where
	 * this is a list of terminals and other is also a list of
	 * terminals.
	 * 
	 * We tried earlier to do a method 'alsoCompletes', but we couldn't
	 * figure out an algorithm for it that didn't take enormous amounts of time
	 * (it was causing delays up to 3 seconds to drag a requirement).
	 * @param other
	 * @return
	 */
	public boolean isSubset(Requirement other){

		//To build a requirement's set of terminals, 
		//first check if the requirement itself is a terminal, then
		// (if it isn't) add each of its choices to the set of
		// terminals. If any choice isn't a terminal, just stop computation.
		//build the set of the other's terminal requirements
		HashSet<TerminalRequirement> othersSubreqs = new HashSet<TerminalRequirement>();
		other.addAllTerminalRequirements(othersSubreqs);
		
		//build the set of this's terminal requirements
		HashSet<TerminalRequirement> thisSubreqs = new HashSet<TerminalRequirement>();
		this.addAllTerminalRequirements(thisSubreqs);


		return othersSubreqs.containsAll(thisSubreqs);
	}

	
	public void addAllTerminalRequirements(HashSet<TerminalRequirement> tReqs){
		if(this.isTerminal()){
			tReqs.add(this.getTerminal());
			return;
		}
		for(Requirement r: this.choices){
			r.addAllTerminalRequirements(tReqs);
		}

	}



	/////////////////////////////////
	/////////////////////////////////
	/////Methods from ScheduleElement
	/////////////////////////////////
	/////////////////////////////////
	@SuppressWarnings("unused")
	private boolean ___MethodsFromScheduleElement_________;


	@Override
	/**
	 * returns null unless this is an exact terminal requirement.
	 */
	public Prefix getPrefix() {
		if(this.isTerminal()){
			return this.getTerminal().getPrefix();
		}
		return null;
	}

	@Override
	public boolean isDuplicate(ScheduleElement other) {
		//We never want to throw duplicate errors for requirements,
		// because you can schedule the same requirement many times.
		return false;
	}



	@Override
	public ArrayList<Requirement> filterEnemyRequirements(ArrayList<Requirement> reqList) {
		if(this.isTerminal()){
			//remove all enemies first, because the requirement graph might have
			// edges using this req rather than the terminal req (this 
			// is unnecessary while .equals() uses the terminal requirement,
			// because RequirementGraph uses hashSets which will make all
			// such requirements into enemies.).
			ArrayList<Requirement> enemyless = new ArrayList<Requirement>();
			for(Requirement r : reqList){
				if(RequirementGraph.doesPlayNice(this, r)){
					enemyless.add(r);
				}
			}
			return this.getTerminal().filterEnemyRequirements(enemyless);
		}
		//if it's not terminal, remove enemies as you go.
		ArrayList<Requirement> result = new ArrayList<Requirement>();
		for(Requirement otherReq : reqList){
			if(RequirementGraph.doesPlayNice(otherReq, this)){
				//Figure out if this satisfies otherReq
				// If this doesn't satisfy otherReq, we can definitely skip
				// otherReq. 
				// TODO Should we delete this check? It may be preventing
				// any subreqs from satisfying other reqs, and that might be bad.
				// Is there a better way to do this?
				// TODO
				if(otherReq.equals(this)){
					result.add(otherReq);
				}
				/* Warning - the subreq might only help this
				 * req in ways that this req no longer needs,
				 * for example if this = "2 of (1 of (A thru K), 1 of (L thru Z))"
				 * and we've already taken L, the subreq
				 *  "1 of (L thru P)" should not make this any more complete.
				if(this.isSubset(otherReq)){
					result.add(otherReq);
				}
				 */
			}
		}
		return result;
	}





	/////////////////////////
	/////////////////////////
	////String methods 
	/////////////////////////
	/////////////////////////
	@SuppressWarnings("unused")
	private boolean ___StringMethods_________;



	//////
	////// String methods from Schedule Element
	//////


	@Override
	public String getDisplayString() {
		StringBuilder result = new StringBuilder();

		//Special case for lab - "2 of () with at least 1 lab from ()"
		// This if clause will be taken on the second recursion.
		if("NWL".equals(this.name)){
			recursePrintOn(s -> result.append(s), r ->r.getDisplayString(),
					"1 lab from (",
					", ",
					")");
			return result.toString();
		}

		//if you've got an atLeast requirement, the order matters,
		// so we can't just use recursePrintOn.
		if(this.isAtLeastRequirement()){
			ArrayList<Requirement> possiblePairs = this.atLeastRequirementPairs();
			Requirement superset = possiblePairs.get(0);
			Requirement subset = possiblePairs.get(1);
			result.append(superset.getDisplayString());
			result.append(" with at least ");
			result.append(subset.getDisplayString());
			return result.toString();
		}

		//Otherwise, we can just use recursePrintOn.
		String[] startMidEnd = syntaxSugars();
		recursePrintOn(s -> result.append(s), r -> r.getDisplayString(), 
				startMidEnd[0], 
				startMidEnd[1], 
				startMidEnd[2]);
		return result.toString();
	}

	/**
	 * Helper method for recursive string methods.
	 * Return a list of the start, divider, and end strings for
	 * special cases of requirements.
	 * These strings will be placed around recursive calls.
	 * For example, if this is a requirement for 1 of 2 choices,
	 *    this method returns {"either (", " or ", ")"}
	 *    and another method would fill in the missing pieces
	 *    to make something like
	 *    "either (" + "MTH 110" + " or " + "MTH 220" + ")" ;
	 * @return
	 */
	private String[] syntaxSugars(){
		if(this.numToChoose == 1 && this.choices.size() == 1){
			return new String[]{"", ", ", ""};
		}
		if(this.isTerminal()){
			return new String[]{"", ", ",""};
		}
		if(this.usesCreditHours){
			return new String[]{numToChoose + " credit hours of (" , ", " , ")"};
		}


		//The next few cases use englishResult
		// because they all have similar behavior with inner parenthesis.
		// If you need to make a string like 
		// "either of (X) or (Y)", then you use the dividers
		// "either of (",  ") or (", ")"
		// rather than the dividers
		// "either of (", " or ", ")".
		String[] englishResult = {};
		if(this.numToChoose == 1 && this.choices.size() == 2){
			englishResult = new String[]{"either of " , " or " , ""};
		}
		if(this.numToChoose != 1 && 
				this.numToChoose == this.choices.size()){
			if(this.numToChoose == 2){
				englishResult =  new String[]{"both of ", " and ", ""};
			}
			else{
				englishResult = new String[]{"all of ", ", ", ""};
			}
		}
		if(englishResult.length != 0){
			if(syntaxSugarNeedsInnerParenthesis()){
				englishResult[0] = englishResult[0] + "(";
				englishResult[1] = ")" + englishResult[1] + "(";
				englishResult[2] = englishResult[2] + ")";
			}
			else{
				//Not sure if these should be included in the final version
				//englishResult[0] = englishResult[0] + "(";
				//englishResult[2] = englishResult[2] + ")";
			}
			return englishResult;
		}


		//TODO remove this to speed things up, its an expensive debug check.
		if(this.isAtLeastRequirement()){
			throw new RuntimeException("Should not use syntax sugar method for atLeast "
					+ "requirements - they need to know subchoice order");
		}

		//default case
		return new String[]{numToChoose + " of (" ,", ",")"};
	}

	private boolean syntaxSugarNeedsInnerParenthesis(){
		for(Requirement r : choices){
			if(!r.isTerminal()){
				return true;
			}
			if(!r.getTerminal().isExact()){
				return true;
			}
		}
		return false;
	}


	@Override
	/**
	 * returns:
	 * 	name, if one exists
	 *  else display string, if its small enough
	 *  else recursePrintOn(shortString) if that result is small enough
	 *  else (numToChoose + " choices").
	 *  
	 * the recursive third option means that you can have a 
	 * short string of the form
	 * "1 of (2 choices, 3 choices)"
	 * and other complex nested behavior, but all complex behavior
	 */
	public String shortString(int preferredLength) {
		//Return name, if you have one
		if(this.name != null){
			return this.name;
		}
		// If display string is small enough, return it.
		String result = this.getDisplayString();
		if(result.length() <= preferredLength){
			return result;
		}
		//If you're an at least, just show them superset
		// They'll have to know about subset on their own
		// or from popups.
		ArrayList<Requirement> reqPairs = atLeastRequirementPairs();
		if(!reqPairs.isEmpty()){
			return reqPairs.get(0).shortString(preferredLength);
		}

		//Now comes the actual attempt to reduce characters.
		StringBuilder builtResult = new StringBuilder();
		String[] sugars = syntaxSugars();


		//Reduce the length of the given sugars
		// then check if you're short enough.
		String replacementFirst = sugars[0];
		if(replacementFirst.contains("credit hours")){
			replacementFirst = replacementFirst.replace("credit hours", "credits");
		}
		String replacementMid = ",";
		String replacementEnd = sugars[2];

		sugars[0] = replacementFirst;
		sugars[1] = replacementMid;
		sugars[2] = replacementEnd;

		final int recursivePreferredLength = 
				( 
						preferredLength 
						- sugars[0].length() 
						- sugars[2].length() 
						- (this.choices.size() - 1) * sugars[1].length()
						) / choices.size();
		recursePrintOn(s -> builtResult.append(s), r -> r.shortString(recursivePreferredLength),
				sugars[0],
				sugars[1],
				sugars[2]);
		//Here's where we check if the replacements made us short enough.
		// If we're still too long, then we can use the last resort,
		// which doesn't write out subreqs and instead writes out
		// 1 of (2 choices).
		if(builtResult.length() > preferredLength){
			String choicesPluralOrNot = " choice";
			if(choices.size() != 1){
				choicesPluralOrNot += "s";
			}
			String lastResort = sugars[0] + choices.size() + choicesPluralOrNot + sugars[2];
			return lastResort;
		}
		return builtResult.toString();
	}

	///
	/// Other string methods
	///


	@Override
	public String toString(){
		return this.getDisplayString();
	}

	public String examineRequirementString(){
		StringBuilder result = new StringBuilder();
		//http://www.furman.edu/academics/academics/majorsandminors/Pages/default.aspx
		result.append(
				"This text shows, in as much detail as possible, how this requirement "
						+ "\n  works within this program. "
						+ "\nIf the text doesn't make sense, ask an advisor or check out the Furman "
						+ "\n  website for another explanation of the requirement."
						+ "\n\n");
		result.append(this.getDisplayString());
		return indent(result.toString(), "   ");
	}




	/**
	 * Turn the save string of this requirement into something you might
	 * see from eclipse, with nice spacing and a limit on line length.
	 * @return
	 */
	public String coderString(){
		String tab = "  ";
		return indent(this.saveString(), tab);
	}


	/**
	 * Indents using default ( and ) as open and closing parens.
	 * @param s
	 * @param maxLineLength
	 * @param tab
	 * @return
	 */
	public String indent(String s, String tab){
		HashSet<Character> openParens = new HashSet<Character>();
		openParens.add('(');
		HashSet<Character> closeParens = new HashSet<Character>();
		closeParens.add(')');
		return indent(s, tab, openParens, closeParens);
	}
	/**
	 * Indent this string as if it were written by a coder or eclipse,
	 * based on parenthesis.
	 * @param s
	 */
	public String indent(String s, String tab, HashSet<Character> openParens, HashSet<Character> closeParens){
		StringBuilder result = new StringBuilder();
		int depth = 0;
		int lineLength = 0;
		LinkedList<Character> stack = new LinkedList<Character>();
		for(char c : s.toCharArray()){
			stack.addLast(c);
		}

		while(!stack.isEmpty()){
			char c = stack.pop();
			if(openParens.contains(c)){
				depth ++;
				result.append(c);
				//newline
				result.append("\n");
				lineLength = 0;
				for(int i = 0; i < depth ; i ++){
					result.append(tab);
					lineLength += tab.length();
				}
			}
			else if(closeParens.contains(c)){
				depth --;

				//newline
				result.append("\n");
				lineLength = 0;
				for(int i = 0; i < depth ; i ++){
					result.append(tab);
					lineLength += tab.length();
				}

				result.append(c);

				//newline
				result.append("\n");
				lineLength = 0;
				for(int i = 0; i < depth ; i ++){
					result.append(tab);
					lineLength += tab.length();
				}
			}
			else{
				if(c == '\n'){
					//newline
					result.append("\n");
					lineLength = 0;
					for(int i = 0; i < depth ; i ++){
						result.append(tab);
						lineLength += tab.length();
					}
				}
				else{
					lineLength ++;
					if(lineLength > 70 && c == ' '){
						//go to the last space in this chunk.
						// if the character after it isn't a newline, then
						// make this whole chunk of spaces into a newline.
						while(stack.peek() == ' '){
							c = stack.pop();
						}
						if(stack.peek() != '\n'){

							//newline
							result.append("\n");
							lineLength = 0;
							for(int i = 0; i < depth ; i ++){
								result.append(tab);
								lineLength += tab.length();
							}
						}
					}
					result.append(c);
				}
			}
		}
		return result.toString();
	}


	///
	/// String methods for saving / reading
	///










	/////////////////////////////////
	/////////////////////////////////
	///// Saving and Reading methods
	/////////////////////////////////
	/////////////////////////////////
	@SuppressWarnings("unused")
	private boolean ___SavingAndReadingMethods_________;



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
	 * It should be unambiguous and easy for a computer to read
	 *  (so it follows a standard format with no special cases).
	 * special cases).
	 * @return
	 */
	public String saveString(){

		//Add the prefix
		StringBuilder result = new StringBuilder();
		result.append(numToChoose);
		if(this.usesCreditHours()){
			result.append(" ch");
		}
		result.append(" of ");
		//Add the guts of this requirement - each sub-requirement

		recursePrintOn(s -> result.append(s), r -> r.saveString(), "(", ", ", ")");
		return result.toString();
	}


	//INFINITELOOPHAZARD
	/**
	 * Recursively applies func to each of choices, and appends to out as 
	 * it goes, producing a string of the form
	 * "("+func(c1)+", "+func(c2)+", "+func(c3)+", "... ", " + func(cn)+")"
	 * where start, end, and divider specify the strings in quotes.
	 * @param out
	 * @param func
	 */
	public void recursePrintOn(Consumer<String> printFunction, Function<Requirement, String> func, String start, String divider, String end){
		ArrayList<Requirement> choiceList = new ArrayList<Requirement>(choices);
		Collections.sort(choiceList);
		printFunction.accept(start);
		//Add all but the last, each addition followed by a comma.
		for(int i = 0; i < choiceList.size() - 1 ; i ++){
			printFunction.accept(func.apply(choiceList.get(i)));
			printFunction.accept(divider);
		}
		//Add the last if it exists, else there are no choices so the result is ().
		if(choiceList.size() >= 1){
			printFunction.accept(func.apply(choiceList.get(choiceList.size() - 1)));
		}
		printFunction.accept(end);
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
		result.recalcOriginalNumberNeeded();
		if(result.isAtLeastRequirement()){
			for(Requirement r : result.choices){
				r.recalcOriginalNumberNeeded();
			}
		}
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
				result.addChoice(parse(tokens));
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







	@SuppressWarnings("unused")
	private boolean ___Testing_________;



	public static void testStringDisplays(){
		String[] tests = new String[]{
				"(MTH 110)",
				"MTH110",
				"((MTH 110))",
				"(((MTH 110)))",
				"2 of (MTH-110, MTH 120, MTH 130)",
				"2 of (MTH 110, (MTH 120), (MTH 130))",
				"1 of (2 of (MTH-110, MTH-120), MTH-130)",
				"2 of (MTH 110, (MTH 120, MTH 130))",
				"2 of (MTH 110, (2 of (MTH 120, MTH 130)))",
				"1 of (MTH 110, (1 of (MTH 120, MTH 130)))",
				"1 of (MTH-110)",
				"2 of (MTH 110, MTH 120)",
				"3 of (MTH 110, MTH 120, MTH 130)",
				"1 of (MTH 110, MTH 120)",

				"1 of ( 2 of (MTH - 110, MTH120 ) , MTH 140, MTH 150, MTH 160)",
				"3 of (BIO 110, BIO 112, BIO 120, BIO 130)",
				"1 of (MTH 150, 2 of (MTH 145, MTH 120))",

				"2 ch of (MTH-110, MTH-120, MTH-130)",
				"8 chof (2 of (MTH-110, ACC-110), MTH 120, MTH 330)",
				"8 chof (2 of (MTH-110, ACC-110), 1 of (MTH 120, MTH 800), MTH 330)",
				"1 of (CHN>200<302, FRN>200<302, GRK>200<302, JPN>200<302, LTN>200<302, or SPN>200<302)",
				"1 of (CHN>200<302, FRN>200<302, GRK>200<302, JPN>200<302, LTN>200<302, or SPN>200<302)",
				"(1 of MTH>100)",
				"1 of MTH>100",
				"4 of MTH>100",
				"1 of MTH > 300", 
				"2 of (1 of (2 of (MTH-151, MTH-334, MTH-121)), 4 of (MTH-151, MTH-334, MTH-121, MTH-335, MTH-721, MTH-4821))"
		};

		int testNum = 0;
		for(String s : tests){
			System.out.println("Test number " + testNum);
			testNum ++;
			System.out.println(s);
			Requirement r = Requirement.readFrom(s);
			testStrings(r, true);
			System.out.println("\n\n");
		}

		String[][] namedTests = {
				{"ART < 150 > 300", "Western art"},
				{"ART > 150 < 300", "Western art"},
				{"4 of ART > 150 < 300", "Western art"},
				{"(4 of ART > 150 < 300)", "Western art"},
				{"ART > 100", "Western art"},
				{"ART < 100", "Western art"},
				{"ART- 100", "Western art"},
				{"(ART > 100)", "Western art"},
				{"(ART < 100)", "Western art"},
				{"(ART - 100)", "Western art"},
				{"(ART-100)", "short"},
				{"(ART-100)", "A very long name that aught to be shorter in practice"},
				{"ART-100", "short"},
				{"ART-100", "A very long name that aught to be shorter in practice"}
		};
		for(String[] s : namedTests){
			System.out.println("Test number " + testNum);
			testNum ++;
			System.out.println(s[0] + ", named: \"" + s[1] + "\"");
			Requirement r = Requirement.readFrom(s[0]);
			r.setName(s[1]);
			testStrings(r, true);
			System.out.println("\n\n");
		}


	}
	private static void testStrings(Requirement r, boolean autoPrint){
		if(autoPrint){
			System.out.println("Save string:\t\t" + r.saveString());
			System.out.println("Display string:\t\t" + r.getDisplayString());
			System.out.println("Short string 50:\t" + r.shortString(50));
			System.out.println("Short string 10:\t" + r.shortString(10));
		}
	}

	public static void generalTest(){
		String[] tests = new String[]{
				"MTH-110",
				"(MTH 110)",
				//"(MTH-110)",
				"((MTH110))",
				"(((MTH-110)))",
				//"MTH-110",
				//"MTH 110",
				"2 of (MTH 110, MTH 120, MTH 130)",
				"2 of (MTH-110, MTH 120, MTH 130)",
				"2 of (ACC-110, MTH 120, MTH 130)",
				"2 of (MTH-110, MTH 120, or MTH 130)",
				"1 of (2 of (MTH-110, MTH-120), MTH-130)",
				"2 of (MTH 110, (MTH 120, MTH 130))",
				"1 of (MTH 110, (1 of (MTH 120, MTH 130)))",
				"2 of (MTH 110, (2 of (MTH 120, MTH 130)))",
				"3 of (MTH 110, MTH 120, MTH 130)",
				"1 of ( 2 of (MTH - 110, MTH120 ) , MTH 140, MTH 150, or MTH 160)",
				"2 of (BIO 110, BIO 112, BIO 120)",
				"3 of (BIO 110, BIO 112, BIO 120, BIO 130)",
				"1 of (MTH 150, 2 of (MTH 145, MTH 120))",

				"2 ch of (MTH-110, MTH-120, MTH-130)",
				"2 chof (MTH-110, MTH-120, MTH-130)",
				"8 chof (2 of (MTH-110, ACC-110), MTH 120, MTH 330)",
				"8 chof (2 of (MTH-110, ACC-110), 1 of (MTH 120, MTH 800), MTH 330)",
				"1 of (CHN>200<302, FRN>200<302, GRK>200<302, JPN>200<302, LTN>200<302, or SPN>200<302)",
				"1 of (CHN>200<302, FRN>200<302, GRK>200<302, JPN>200<302, LTN>200<302, or SPN>200<302)",
				"(1 of MTH>100)",
				"1 of MTH>100",
				"4 of MTH>100",
				"1 of MTH > 300"
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
			if(r.getStoredIsComplete() != complete){
				needsToBeShown = true;
			}
			if(r.getStoredPercentComplete() != percentComplete){
				needsToBeShown = true;
			}

			needsToBeShown = true;


			if(needsToBeShown){
				//System.out.println("ReadingFrom \"" +toRead + "\"");
				System.out.println("    got \"" + r.saveString() + "\"");
				System.out.println("Equal to last?" + equalToLast);
				//System.out.println("Uses CH? " + r.usesCreditHours);
				System.out.println("Complete?" + complete );
				//System.out.println("Percent Complete:" + percentComplete + "/" + r.storedPercentComplete());
				//System.out.println("minLeft:" + minLeft + "/" + r.storedCoursesLeft);
				System.out.println(r.numToChoose);
				System.out.println();
			}

		}
		System.out.println("Finished testing");
	}





	public static void main(String[] args){
		testStringDisplays();
	}





}