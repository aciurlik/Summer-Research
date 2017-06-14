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
	public int min;
	public int max;
	
	boolean usesMin;
	boolean usesMax;
	
	
	
	
	public TerminalRequirement(Prefix p){
		this.p = p;
		this.usesMin = false;
		this.usesMax = false;
	}
	public TerminalRequirement(Prefix p, int min){
		this(p);
		this.allNumbersLES(min);
	}
	public TerminalRequirement(Prefix p, int min, int max){
		this(p);
	}
	
	
	
	
	/////////////////////
	/////////////////////
	///// Adding comparison pieces
	/////////////////////
	/////////////////////
	
	private void allNumbersLEQ(int max){
		this.max = max+1;
		this.usesMax = true;
	}
	private void allNumbersLES(int max){
		this.max = max;
		this.usesMax = true;
	}
	private void allNumbersGEQ(int min){
		this.min = min-1;
		this.usesMin = true;
	}
	private void allNumbersGRE(int min){
		this.min = min;
		this.usesMin = true;
	}
	
	public void addComparison(String comp, int num){
		switch(comp){
		case "<":
			allNumbersLES(num);
			break;
		case">":
			allNumbersGRE(num);
			break;
		case"<=":
			allNumbersLEQ(num);
			break;
		case">=":
			allNumbersGEQ(num);
			break;
		default:
			throw new RuntimeException("The string \"" + comp + "\" Isn't a valid comparison.");
		}
	}
	
	
	
	
	/////////////////////
	/////////////////////
	///// Reading / saving
	/////////////////////
	/////////////////////
	
	
	
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
		TerminalRequirement result;
		//First look for either a '-', a '>', or a '<'.
		if(s.contains(">") || s.contains("<")){
			result = readFromInequality(s);
		}
		else{
			if(!s.contains("-")){
				s = s.replaceAll("(?<=[a-zA-Z])(?=\\d)", "-");
			}
			if(!s.contains("-")){
				System.out.println(s);
				throw new RuntimeException("A terminal requirement has to include a '-', or else both letters and numbers");
			}
			String[] split = s.split("-");
			if(split.length > 2){
				System.out.println(s);
				throw new RuntimeException("You need a comma between terminal requirements");
			}
			Prefix p = new Prefix(split[0], split[1]); //in case of BLK
			result = new TerminalRequirement(p);
		}
		return result;
	}
	
	private static TerminalRequirement readFromInequality(String s){
		// MTH > 200
		// MTH < 400 > 100
		// MTH > 300 < 112
		// MTH >= 200 <= 234
		Stack<String> tokens = tokenize(s);
		String subject = tokens.pop();
		String firstComp = parseComparison(tokens);
		int firstNum = Integer.parseInt(tokens.pop());
		TerminalRequirement result = new TerminalRequirement(new Prefix(subject, firstNum));
		result.addComparison(firstComp, firstNum);
		if(tokens.isEmpty()){
			return result;
		}
		else{
			String secondComp = parseComparison(tokens);
			int secondNum = Integer.parseInt(tokens.pop());
			result.addComparison(secondComp, secondNum);
		}
		return result;
		
		
	}
	
	private static String parseComparison(Stack<String> tokens){
		String comp = tokens.pop();
		if(!comp.equals(">") && ! comp.equals("<")){
			throw new RuntimeException("Comparisons must be either '>', '<', '>=', or '<='.");
		}
		String next = tokens.peek();
		if(next.equals("=")){
			comp = comp + "=";
			tokens.pop();
		}
		return comp;
	}
	
	
	public static Stack<String> tokenize(String s){
		// MTH > 200
		// MTH < 400 > 100
		// MTH > 300 < 112
		// MTH >= 200 <= 234
		s = s.replaceAll("=", " = ");
		s = s.replaceAll("<", " < ");
		s = s.replaceAll(">", " > ");
		
		String[] split = s.split("\\s+");
		Stack<String> result = new Stack<String>();
		for(int i = split.length - 1; i >= 0 ; i --){
			result.push(split[i]);
		}
		return result;
		
	}
	
	public String saveString(){
		String result;
		if((!usesMin) && (!usesMax)){
			result =  p.toString();
		}
		else{
			result = p.getSubject();
			if(usesMin){
				result  +=  ">" + min;
			}
			if(usesMax){
				result += "<" + max;
			}
		}
		return result;
	}
	
	
	
	
	
	/////////////////////
	/////////////////////
	///// Methods overwritten to prevent infinte loops in requirement
	/////////////////////
	/////////////////////
	
	
	
	@Override
	public HashSet<Prefix> fastestCompletionSet(HashSet<Prefix> taken) {
		HashSet<Prefix> result = new HashSet<Prefix>();
		if(isSatisfiedBy(taken)){
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
		if(isSatisfiedBy(taken)){
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
		return this.saveString();
	}
	@Override
	public ArrayList<Requirement> getRequirementsFulfilled() {
		//TODO think through the implications of this
		return null;
	}
	
	public boolean isSatisfiedBy(HashSet<Prefix> taken){
		if(!usesMax && !usesMin){
			if(taken.contains(p)){
				return true;
			}
			return false;
		}
		for(Prefix p : taken){
			if(isSatisfiedBy(p)){
				return true;
			}
		}
		return false;
	}
	@Override
	public boolean isSatisfiedBy(Prefix p) {
		if(!usesMax && !usesMin){
			if(p.compareTo(this.p) == 0){
				return true;
			}
		}
		if(p.getSubject().equals(this.p.getSubject())){
			int takenNum;
			try{
				takenNum = Integer.parseInt(p.getNumber());
			}catch (Exception e){
				return false;
			}
			boolean maxGood = takenNum < this.max ;
			boolean minGood = takenNum > this.min;
			if((!usesMax || maxGood) && (!usesMin || minGood)){
				return true;
			}
		}
		return false;
	}
	
	
	public static void testTerminalRequirements(){
		String[] test = { 
				"MTH - 150",
				"MTH > 150",
				"MTH < 150",
				"MTH >= 150",
				"MTH <= 150",
				"MTH > 150 < 200",
				"MTH < 200 > 150",
				"MTH <= 200 >= 150",
				"MTH >= 150 <= 200"
		};
		
		HashSet<Prefix> taken = new HashSet<Prefix>();
		taken.add(new Prefix("MTH", 150));
		
		for(String s : test){
			TerminalRequirement t = readFrom(s);
			System.out.println(t.saveString()+ ", " + t.isSatisfiedBy(taken));
		}
	}
	
	public static void main(String[] args){
		testTerminalRequirements();
	}
	
}
