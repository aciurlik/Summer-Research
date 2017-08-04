import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;


/**
 * Blurb written: 7/24/2016
 * Last updated: 7/27/2017
 * 
 * In our program, requirements have a number of things to choose from,
 * and those choices may themselves be requirements. A terminal requirement
 * is a requirement that doesn't have any sub-requirements, and so 
 * ends the recursion.
 * 
 * To start, you can imagine that each terminal requirements represents a 
 *     requirement that can be satisfied by just 1 course in furman's system.
 *     For example, a terminal requirement might represent "you need MTH-150".
 * 
 * in reality, a terminal requirement can represent any prefix (see the Prefix class)
 * and may represent complex statements like "any MTH > 200." 
 * 
 * See the terminal requirement tutorial in the ReadingSaving section for a full explanation of 
 * what can be a terminal requirement.
 * 
 * 
 *
 */
public class TerminalRequirement extends Requirement implements  java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Prefix p;
	public int min;
	public int max;
	boolean usesMin;
	boolean usesMax;
	public static int defaultCreditHours =4;
	public ArrayList<SemesterDate> scheduledSemester = new ArrayList<SemesterDate>();
	public int storedCreditHours = -1;




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
	///// Making a terminal requirement into a >, < or between requirement.
	/////////////////////
	/////////////////////
	@SuppressWarnings("unused")
	private boolean ___makingInexact_________;


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
	///// Reading and saving
	/////////////////////
	/////////////////////
	@SuppressWarnings("unused")
	private boolean ___ReadingSaving_________;


	/**
	 * TERMINAL REQUIREMENT SAVING AND READING
	 * 
	 * When reading a string, terminal requirements ignore spaces.
	 * You can always differentiate between terminal requirements and requirements, 
	 * because a terminal requirement has no parenthesis. (If a user examines a
	 * requirement, some syntactical operations remove the parenthesis, but the
	 * parenthesis rule applies to all strings that a requirement can be read from.)
	 * 
	 * Terminal requirements come in three forms:
	 * 		standard (exact)  	: "MTH-110"
	 * 		inequality 			: "MTH>110", "MTH>110 < 200"
	 * 		numToChoose (must include an inequality)
	 * 							: "4 of MTH>110", "2 of MTH < 500"
	 * 
	 * exact:
	 * 		An exact terminal requirement is a requirement for just one prefix.
	 * 		The prefix may be specified in a number of syntaxes: if the string 
	 * 			contains a '-', then it will use the Prefix readFrom method.
	 * 			Otherwise it will look for the first switch from letters
	 * 			to numbers, and assume that this is the break in the subject
	 * 			and number.
	 * inequality:
	 * 		A terminal requirement may use any of '>', '<', '>=', or '<=' in place
	 * 			of the '-'. In addition, one extra inequality symbol and number
	 * 			may come after the first number. This allows for strings of the form
	 * 			"MTH>110",  "MTH > 110 <= 235", and "MTH <= 100 > 300". Notice that this
	 * 			last is a terminal requirement, but nothing can satisfy it.
	 * 
	 * numToChoose:
	 * 		Normal Requirements can't have more to choose than they have choices. Due to 
	 * 			this, it can be difficult to write requirements of the form "4 math classes
	 * 			numbered at least 200." In order to handle this case, TerminalRequirements 
	 * 			still respect their numberToChoose. You may write a TerminalRequirements of 
	 * 			the form "4 of MTH > 200", where the 'of' is necessary. 
	 *
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
			result.recalcOriginalNumberNeeded();
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
	@SuppressWarnings("unused")
	private boolean ___MethodsFromRequirement_________;

	@Override 
	protected int minMoreNeeded(ArrayList<ScheduleElement> taken){
		int result = numToChoose;
		for(ScheduleElement e : taken){
			if(e instanceof Requirement){
				if(((Requirement) e).isTerminal()){
					if(((Requirement) e).getTerminal().alsoCompletes(this)){
						result --;
						if(result <= 0){
							return result;
						}
					}
				}
			}
			else{
				if(isSatisfiedBy(e.getPrefix())){
					result --;
					if(result <= 0){
						return result;
					}
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
	public boolean isTerminal(){
		return true;
	}
	@Override
	public TerminalRequirement getTerminal(){
		return this;
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


	//INFINITELOOPHAZARD
	@Override
	public boolean equals(Requirement r){
		//If r isn't a terminal, we can quickly recurse with a
		// terminal or else say no.
		if(r == null){
			return false;
		}
		if(!(r instanceof TerminalRequirement)){
			if(r.isTerminal()){
				return this.equals(r.getTerminal());
			}
			else{
				return false;
			}
		}
		//after recursion, r is a terminal.
		TerminalRequirement other = (TerminalRequirement)r;

		//check if they complete each other
		if(! (this.completedBy(other) && other.completedBy(this) )){
			return false;
		}
		//check that numToChoose is the same for both
		if(! (this.numToChoose == other.numToChoose)){
			return false;
		}
		//check name equality
		if(this.name!=null && !this.name.equals(other.name)){
			return false;
		}
		if(other.name != null && !other.name.equals(this.name)){
			return false;
		}
		//We're good!
		return true;
	}

	//INFINITELOOPHAZARD
	@Override 
	public int hashCode(){
		int result = this.p.hashCode();
		if(usesMin){
			result += 10;
			result += min;
		}
		if(usesMax){
			result += 10;
			result += max;
		}
		return result;
	}
	
	
	/////////////////////
	/////////////////////
	///// Methods from ScheduleElement
	/////////////////////
	/////////////////////
	@SuppressWarnings("unused")
	private boolean ___MethodsFromScheduleElement_________;

	@Override
	public int getCreditHours(){
		if(this.storedCreditHours == -1){
			this.storedCreditHours = calculateCreditHours();
		}
		return this.storedCreditHours;
	}
	private int calculateCreditHours(){
		if(this.isExact()){
			return CourseList.getCoursesCreditHours(p);
		}
		else{
			return defaultCreditHours;
		}
	}

	@Override
	public Prefix getPrefix() {
		if(this.isExact()){
			return p;
		}
		return null;
	}

	@Override public String getDisplayString(){
		if(this.isExact()){
			return this.saveString();
		}
		String result = "";
		if(this.numToChoose!= 1){
			result = numToChoose + " ";
		}
		if(this.usesMax && this.usesMin){
			return result + this.p.getSubject() + " between " + min + " and " + max + " exclusive";
		}
		if(this.usesMax){
			return result + this.p.getSubject() + " numbered less than " + max;
		}
		//this uses min.
		if(this.min == 0){
			String end = "any " + p.getSubject() + " course"; 
			if(this.numToChoose!= 1){
				return numToChoose + " of " + end;
			}
			else{
				return end; 
			}
		}
		return result + this.p.getSubject() + " numbered greater than " + min;
	}

	@Override public String shortString(int preferredLength){
		if(name != null){
			return name;
		}
		String result = getDisplayString();
		if(result.length() > preferredLength){
			result = saveString();
		}
		return result;
	}


	@Override
	public ArrayList<Requirement> filterEnemyRequirements(ArrayList<Requirement> reqList) {
		ArrayList<Requirement> result = new ArrayList<Requirement>();
		for(Requirement r : reqList){
			if(this.equals(r)){
				result.add(r);
			}
			else{
				if(RequirementGraph.doesPlayNice(r, this)){
					if(r.isSatisfiedBy(this)){
						result.add(r);
					}
				}
			}
		}
		return result;
	}


	
	
	
	/////////////////////
	/////////////////////
	///// Utilities
	/////////////////////
	/////////////////////
	@SuppressWarnings("unused")
	private boolean ___Utilities_________;
	

	public boolean isSatisfiedBy(TerminalRequirement t){
		return t.isSubset(this);
	}


	/**
	 * test whether this requirement uses min or max, or instead
	 * is an exact requirement (a requirement for exactly one prefix)
	 * @return
	 */
	public boolean isExact(){
		return ((!usesMin) && (!usesMax));
	}
	
	/**
	 * Check if every way of completing this terminal also completes r
	 * @param r
	 * @return
	 */
	public boolean alsoCompletes(Requirement r){
		if(this.isExact()){
			//see if r is complete by the list of elements {this}
			ArrayList<ScheduleElement> taken = new ArrayList<ScheduleElement>();
			taken.add(new ScheduleCourse(new Course(this.p,this.getCreditHours()), null));
			return r.isComplete(taken, false);
		}
		else{
			if(r.isTerminal()){
				TerminalRequirement t = r.getTerminal();
				return t.completedBy(this);
			}
			//TODO what if this isn't exact, and r is a full blown requirement?
			return false;
		}
	}
	
	/**
	 * Check if satisfying t definitely satisfies this.
	 * @param t
	 * @return
	 */
	public boolean completedBy(TerminalRequirement t){
		if(this.numToChoose > t.numToChoose){
			return false;
		}
		return isSuperset(t);
	}

	/**
	 * true iff one course scheduled for t 
	 * is also a course scheduled for this.
	 * 
	 * TODO make a test method for this method.
	 * 
	 * @param other
	 * @return
	 */
	public boolean isSuperset(TerminalRequirement t){
		if(this.isExact()){
			if(!t.isExact()){
				return false;
			}
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
		return ourInterval.contains(theirInterval, true);
	}

	
	

	/////////////////////
	/////////////////////
	///// Testing
	/////////////////////
	/////////////////////
	@SuppressWarnings("unused")
	private boolean ___Testing_________;


	public static void testTerminalRequirements(){
		CourseList.loadAllCourses();
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
		taken.add(new TerminalRequirement(new Prefix("MTH", 150)));
		//taken.add(new Prefix("MTH", 120));


		double tol = Double.MIN_VALUE * 10000;

		for(String s : test){
			TerminalRequirement t = readFrom(s);
			//boolean sat =  t.isCompletedBy(taken);
			int minMore =  t.minMoreNeeded(taken);
			double percent = t.percentComplete(taken, false);
			boolean show = false;
			if(percent > 1.0-tol && minMore >0){
				show = true;
			}
			if(percent < 1.0-tol && minMore <= 0){
				show = true;
			}

			//show = true;
			if(show){
				System.out.println(t.saveString()+ " choose:" + t.numToChoose
						+ ",\t" +minMore + ",\t" + percent);
			}
		}
		System.out.println("Finished testing");
	}


	public static void testTerminalsEquality(){
		TerminalRequirement t = TerminalRequirement.readFrom("ART>199<300");
		TerminalRequirement x = TerminalRequirement.readFrom("ART>199<300");
		Requirement y = Requirement.readFrom("(ART>199<300)");
		System.out.println(t.equals(x));
		System.out.println(x.equals(t));
		System.out.println(y.equals(x));
		System.out.println(x.equals(y));


		/*System.out.println(t.hashCode());
		System.out.println(x.hashCode());

		HashSet<TerminalRequirement> set = new HashSet<TerminalRequirement>();
		set.add(t);
		set.add(x);
		System.out.println(set.size());
		System.out.println(set.contains(x));
		 */
	}




	public static void main(String[] args){
		//testTerminalRequirements();
		//testTerminalsEquality();
	}

}