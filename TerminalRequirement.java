import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;


/**
 * Each terminal requirements represents a requirement that can be
 * satisfied by 1 course in furman's system.
 * 
 * For example, a terminal requirement might represent MTH - 150.
 * 
 * It might also represent strange things like BusinessBlock, or
 * 		any MTH greater than 200.
 * 
 *
 */
public class TerminalRequirement extends Requirement {
	public Prefix p;
	public int number; 
	
	
	
	
	public TerminalRequirement(Prefix p){
		this.p = p;
	}
	public TerminalRequirement(Prefix p, int number){
		this.p = p;
		this.number = number;
	}
	
	
	
	/**
	 * Takes a string of the form MTH-140 or MTH140 
	 * 
	 * If no '-' is in the string, it looks for the first switch between an alpha
	 * character and a numeric character. This means that, for example, ACC-BLK has to 
	 * contain its '-'.
	 * 
	 * @param s
	 * @return
	 */
	public static TerminalRequirement readFrom(String s){
		//TODO needs to be replaced to handle cases like MTH > 200
		//System.out.println(s);
		if(!s.contains("-")){
			s = s.replaceAll("(?<=[a-zA-Z])(?=\\d)", "-");
		}
		if(!s.contains("-")){
			throw new RuntimeException("A terminal requirement has to include a '-', or else both letters and numbers");
		}
		String[] split = s.split("-");
		if(split.length > 2){
			throw new RuntimeException("You need a comma between terminal requirements");
		}
		Prefix p = new Prefix(split[0], split[1]); //in case of BLK
		TerminalRequirement result = new TerminalRequirement(p);
		if(split[1].matches("\\d+")){
			result.number = Integer.parseInt(split[1]);
		}
		return result;
	}
	
	public String saveString(){
		return p.toString();
	}
	
	
	
	
	
	
	@Override
	public HashSet<Prefix> fastestCompletionSet(HashSet<Prefix> taken) {
		HashSet<Prefix> result = new HashSet<Prefix>();
		if(taken.contains(p)){
			return result;
		}
		else{
			result.add(p);
			return result;
		}
	}
	@Override
	public double percentComplete(HashSet<Prefix> taken) {
		//either 1 or 0 by the definition of terminal requirement.
		if(taken.contains(p)){
			return 1;
		}
		return 0;
	}
	@Override
	public int compareTo(Requirement o) {
		if(!(o instanceof TerminalRequirement)){
			//Terminal requirements are less than other types of requirements.
			return -1;
		}
		TerminalRequirement other = (TerminalRequirement)o;
		return this.saveString().compareTo(other.saveString());
	}
	
	@Override
	public Prefix getPrefix() {
		return p;
	}
	
	@Override
	public String getDisplayString() {
		//TODO make this handle > or <
		return this.p.toString();
	}
	@Override
	public ArrayList<Requirement> getRequirementsFulfilled() {
		//TODO think through the implications of this
		return null;
	}
	@Override
	public boolean isSatisfiedBy(Prefix p) {
		if(p.compareTo(this.p) == 0){
			return true;
		}
		//TODO make this handle > or <
		return false;
	}
	
}
