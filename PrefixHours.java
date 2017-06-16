import java.util.ArrayList;
import java.util.HashSet;


public class PrefixHours implements ScheduleElement, HasCreditHours{

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
	public ArrayList<Requirement> getRequirementsFulfilled(
			HashSet<Requirement> loaded) {
		return null;
	}


	@Override
	public String shortString() {
		return getDisplayString();
	}

}
