import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class Requirement implements Comparable<Requirement>, ScheduleElement{
	Prefix[] choices;
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
	
	public boolean isSatisfiedBy(Course c){
		for (Prefix p : choices){
			if(c.coursePrefix.compareTo(p) == 0){
				return true;
			}
		}
		return false;
	}

	public Requirement(Prefix[] choices, int numToChoose){
		this.choices = choices;
		Arrays.sort(this.choices);
		this.numToChoose = numToChoose;
		this.doubleDipNumber = Requirement.defaultDDN;
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

	public boolean isComplete(){
		return numToChoose <= numFinished;
	}


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
		
		//Compare based on numToChoose
		int numChooseDifference = this.numToChoose - other.numToChoose;
		if(numChooseDifference != 0){
			return numChooseDifference;
		}
		//Finally, compare based on prefixes.
		for (int i = 0; i < this.choices.length ; i ++){
			if(other.choices.length >= i){
				//this one has more, so other should come first. 
				// that means other < this
				return 1;
			}
			int diff = this.choices[i].compareTo(other.choices[i]);
			if(diff != 0){
				return diff;
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
		if(numToChoose == 1){
			return choices[0].toString();
		}
		else{
			return String.format("%d of %s", numToChoose, choices.toString());
		}
	}
	
	public static final String[] SAVE_DELIMETERS = {" of ","; \t "," Completed; DDN:"};

	public String saveString(){
		Object[] data = {
				numToChoose,
				Arrays.toString(choices),
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
}
