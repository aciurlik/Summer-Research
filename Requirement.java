import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;



public class Requirement implements Comparable<Requirement>, ScheduleElement, JSONable{
	HashSet<Prefix> choices;
	int numToChoose; //the number of classes that must be taken.
	// If this is a "2 of these choices" requirement, then numToChoose
	// should be set to 2.
	int numFinished;
	int doubleDipNumber;
	String name;

	public static final int defaultDDN = 0;



	/**
	 *
	 */
	public static Requirement testRequirement(){
		return new Requirement(new Prefix[]{new Prefix("MTH", 220)}, 1);
	}

	
	
	public Requirement(Prefix[] choices, int numToChoose){
		this(new HashSet<Prefix>(Arrays.asList(choices)), numToChoose);

	}

	
	
	public Requirement(HashSet<Prefix> choices, int numToChoose){
		this.choices = choices;
		this.numToChoose = numToChoose;
		this.doubleDipNumber = Requirement.defaultDDN;
	}
	
	public Requirement(Prefix p){
		this(new Prefix[]{p}, 1);
	}

	
	public Requirement cloneRequirement(Requirement r) {
		
		Requirement newR = new Requirement (r.choices, r.numToChoose);
		newR.doubleDipNumber=r.doubleDipNumber;
		newR.name = r.name;
		newR.numFinished = r.numFinished;
		return newR;
	}
	
	
	
	
	
	public boolean isSatisfiedBy(ScheduleElement e){
		if(e instanceof Course){
			Course c = (Course)e;

			for (Prefix p : choices){
				if(c.coursePrefix.compareTo(p) == 0){
					return true;
				}
			}
			return false;
		}
		else if (e instanceof Requirement){
			if(this.choices.equals(((Requirement)e).choices)){
				return true;
			}
			return false;
		}
		else if(e.getRequirementsFulfilled().contains(this)){
			return true;
		}
		return false;

	}
	public void setName(String name){
		this.name = name;
	}

	public void setDoubleDipNumber(int newVal){
		this.doubleDipNumber = newVal;
	}
	public int getDoubleDipNumber(){
		return this.doubleDipNumber;
	}
	
	public void addChoice(Prefix p){
		choices.add(p);
	}

	public boolean isComplete(){
		return numToChoose <= numFinished;
	}


	//TODO Let the user decide which comparisons should come first.
	//This comparison method is used to sort the 
	// requirementList displayed to the user.
	@Override
	public int compareTo(Requirement other) {
		//first compare based on whether they're complete, completed coming later
		if(this.isComplete() && !other.isComplete()){
			return 1;
		}
		if(!this.isComplete() && other.isComplete()){
			return -1;
		}
		//then compare based on doubleDipNumber, as this will separate 
		// GER from major and each major from the others.
		int dipDifference = this.doubleDipNumber - other.doubleDipNumber;
		if(dipDifference != 0){
			return dipDifference;
		}
		//Then compare based on numToChoose
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
		for(Prefix p : this.choices){
			if(!other.choices.contains(p)){
				return 1;
			}
		}
		
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

	public String saveString(){
		Object[] data = {
				numToChoose,
				choices.toString(),
				numFinished,
				doubleDipNumber};
		return SaverLoader.saveString(SAVE_DELIMETERS, data);
	}

	public static Requirement readFrom(String saveString){
		String[] parsed = SaverLoader.parseString(SAVE_DELIMETERS, saveString);
		int newNumToChoose = Integer.parseInt(parsed[0]);
		ArrayList<Prefix> newChoices = new ArrayList<Prefix>();
		String prefixArrayString = parsed[1];
		for(String prefixString : prefixArrayString.substring(1,prefixArrayString.length() - 1).split(",") ){
			newChoices.add(Prefix.readFrom(prefixString));
		}
		Requirement result = new Requirement(newChoices.toArray(new Prefix[newChoices.size()]), newNumToChoose);
		try{
			result.numFinished = Integer.parseInt(parsed[2]);
			result.setDoubleDipNumber(Integer.parseInt(parsed[3]));
		}
		catch (Exception e){

		}
		return result;

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
		result.setDoubleDipNumber(doubleDipNumber);
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
		Requirement r = Requirement.testRequirement();
		System.out.println(r.saveAsJSON());
		Requirement z = Requirement.readFromJSON(r.saveAsJSON());
		System.out.println(z.saveAsJSON());
		System.out.println(z.saveAsJSON().equals(r.saveAsJSON()));
	}


}