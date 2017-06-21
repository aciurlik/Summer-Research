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
		//First look for either '-', '>', '<', or 'of'.
		s = s.replaceAll("\\s+", "");
		if(s.contains("of")){
			int i = s.indexOf("of");
			result = readFrom(s.substring(i + 2));
			String numString = s.substring(0, i);
			try{
				result.numToChoose = Integer.parseInt(numString);
			}catch(Exception e){
				parseException(s, "the 'of' in a Terminal Requirement must be preceeded by an integer.");
			}
			return result;
		}
		if(s.contains(">") || s.contains("<")){
			result = readFromInequality(s);
		}
		else{
			if(!s.contains("-")){
				s = s.replaceAll("(?<=[a-zA-Z])(?=\\d)", "-");
			}
			if(!s.contains("-")){
				parseException(s, "A terminal requirement has to include a '-', or else both letters and numbers");
			}
			String[] split = s.split("-");
			if(split.length > 2){
				parseException(s,"You need a comma between terminal requirements");
			}
			Prefix p = new Prefix(split[0], split[1]); //in case of BLK
			result = new TerminalRequirement(p);
		}
		return result;
	}
	
	private static void parseException(String s, String message){
		throw new RuntimeException("Issue parsing \"" + s + "\" :\n" + message);
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
		String result = "";
		if((!usesMin) && (!usesMax)){
			result =  p.toString();
		}
		else{
			if(numToChoose != 1){
				result += numToChoose + " of ";
			}
			result += p.getSubject();
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
	///// Methods overwritten to prevent infinite loops in requirement
	/////////////////////
	/////////////////////
	//See Requirement class for explanation of which methods need to
	// be overwritten

	@Override 
	protected int minMoreNeeded(ArrayList<ScheduleElement> taken){
		//TODO what if this usesCreditHours? Is that even possible? (I don't think so).
		int result = numToChoose;
		for(ScheduleElement e : taken){
			if(isSatisfiedBy(e.getPrefix())){
				result --;
				if(result <= 0){
					return result;
				}
			}
		}
		return result;
	}
	
	
	
	/**
	 * Efficiently calculate whether this prefix counts towards
	 * this terminal requirement at all.
	 */
	@Override
	public boolean isSatisfiedBy(Prefix p) {
		if(p == null){
			return false;
		}
		if(isExact()){
			if(p.compareTo(this.p) == 0){
				return true;
			}
			return false;
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
	
	@Override
	public boolean alsoCompletes(Requirement r){
		if(this.isExact()){
			ArrayList<ScheduleElement> taken = new ArrayList<ScheduleElement>();
			//TODO instead of 4, make this accurate.
			taken.add(new PrefixHours(this.p, 4));
			return r.isComplete(taken, false);
		}
		else{
			if(r instanceof TerminalRequirement){
				TerminalRequirement t = (TerminalRequirement)r;
				if(t.usesMax){
					if(!this.usesMax || this.max > t.max){
						return false;
					}
				}
				if(t.usesMin){
					if(!this.usesMin || this.min < t.min){
						return false;
					}
				}
				return true;
			}
			//what if it's just a requirement?
			return false;
		}
		
	}
	

	/**
	 * test whether this requirement uses min or max, or instead
	 * is an exact requirement (a requirement for exactly one prefix)
	 * @return
	 */
	public boolean isExact(){
		return ((!usesMin) && (!usesMax));
	}
	
	public boolean isSatisfiedBy(ArrayList<ScheduleElement> taken){
		return minMoreNeeded(taken) <= 0;
	}

	
	
	
	
	
	
	/////////////////////
	/////////////////////
	///// Methods from ScheduleElement
	/////////////////////
	/////////////////////
	
	@Override
	public Prefix getPrefix() {
		return p;
	}
	
	@Override
	public String getDisplayString() {
		return this.saveString();
	}
	@Override
	public ArrayList<Requirement> getRequirementsFulfilled(HashSet<Requirement> r) {
		//TODO think through the implications of this
		ArrayList<Requirement> result = new ArrayList<Requirement>();
		result.add(this);
		return result;
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
	

	
	
	
	
	
	
	
	public static void testTerminalRequirements(){
		String[] test = { 
				"MTH-150",
				"MTH-001",
				"MTH-500",
				"MTH > 150",
				"MTH < 150",
				"MTH >= 150",
				"MTH <= 150",
				"MTH > 150 < 200",
				"MTH < 200 > 150",
				"MTH <= 200 >= 150",

				"MTH >= 150 <= 200",
				"2 of MTH>100"
		};
		
		ArrayList<ScheduleElement> taken = new ArrayList<ScheduleElement>();
		taken.add(new PrefixHours(new Prefix("MTH", 150), 4));
		//taken.add(new Prefix("MTH", 120));


		double tol = Double.MIN_VALUE * 10000;
		
		for(String s : test){
			TerminalRequirement t = readFrom(s);
			boolean sat =  t.isSatisfiedBy(taken);
			int minMore =  t.minMoreNeeded(taken);
			double percent = t.percentComplete(taken, false);
			boolean show = false;
			if(sat && (percent < 1.0 - tol || minMore > 0)){
				show = true;
			}
			if((!sat) && (percent > 1.0-tol || minMore <= 0)){
				show = true;
			}
			if(percent > 1.0-tol && minMore >0){
				show = true;
			}
			if(percent < 1.0-tol && minMore <= 0){
				show = true;
			}
			
			//show = true;
			if(show){
			System.out.println(t.saveString()+ " choose:" + t.numToChoose + ",\t" + sat
					+ ",\t" +minMore + ",\t" + percent);
			}
		}
		System.out.println("Finished testing");
	}
	
	//INFINITELOOPHAZARD
	public boolean isCompletedBy(HashSet<TerminalRequirement> s){
		if(s.contains(this)){
			return true;
		}
		for(TerminalRequirement t : s){
			if(this.completedBy(t)){
				return true;
			}
		}
		return false;
	}
	
	public boolean completedBy(TerminalRequirement t){
		if(this.isExact()){
			return t.p.equals(this.p);
		}
		if(!t.p.getSubject().equals(this.p.getSubject())){
			return false;
		}
		int left = Integer.MIN_VALUE;
		int right = Integer.MAX_VALUE;
		int otherLeft = Integer.MIN_VALUE;
		int otherRight = Integer.MAX_VALUE;
		if(this.usesMin){
			left = this.min;
		}
		if(this.usesMax){
			right = this.max;
		}
		if(t.usesMin){
			otherLeft=  t.min;
		}
		if(t.usesMax){
			otherRight = t.max;
		}
		Interval<Integer> ourInterval = new Interval<Integer>(left, right);
		Interval<Integer> theirInterval = new Interval<Integer>(otherLeft, otherRight);
		return theirInterval.contains(ourInterval);
	}
	
	
	//INFINITELOOPHAZARD
	public boolean equals(Requirement r){
		//TODO
		return r == this;
	}
	
	
	
	
	
	
	
	public static void main(String[] args){
		testTerminalRequirements();
	}
	
}
