import java.util.Arrays;
import java.util.Collections;


public class Requirement implements Comparable<Requirement>{
	Prefix[] choices;
	int numToChoose; //the number of classes that must be taken.
	// If this is a "2 of these choices" requirement, then numToChoose
	// should be set to 2.
	int numFinished;
	int doubleDipNumber;


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
		this.doubleDipNumber = -1;
	}

	public void setDoubleDipNumber(int newVal){
		this.doubleDipNumber = newVal;
	}
	public int getDoubleDipNumber(){
		return this.doubleDipNumber;
	}



	public String displayName(){
		if(numToChoose == 1){
			return choices[0].toString();
		}
		else{
			return String.format("%d of %s", numToChoose, choices.toString());
		}
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
}
