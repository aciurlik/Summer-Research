import java.util.ArrayList;
import java.util.HashSet;


public class PrefixHours implements ScheduleElement, HasCreditHours, java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int creditHours;
	Prefix p;
	public PrefixHours(Prefix p, int creditHours){
		this.p = p;
		this.creditHours = creditHours;
	}


	@Override
	public int getCreditHours(){
		return creditHours;
	}


	public String toString(){
		return p.toString() + ", " + creditHours + " hours";
	}


	@Override
	public Prefix getPrefix() {
		return p;
	}
	@Override
	public boolean isDuplicate(ScheduleElement other) {
		return this.p.equals(other.getPrefix());
	}
	@Override
	public String getDisplayString() {
		return toString();
	}
	@Override
	public ArrayList<Requirement> filterEnemyRequirements(
			ArrayList<Requirement> loaded) {
		ArrayList<Requirement> result = new ArrayList<Requirement>(); 
		for(Requirement r : loaded){ 
			if(r.isSatisfiedBy(this.p)){ 
				result.add(r); 
			} 
		} 
		return result; 
	}


	@Override
	public String shortString(int preferredLength) {
		return getDisplayString();
	}

}
