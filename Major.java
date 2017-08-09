import java.util.ArrayList;

/**
 * Blurb written 7/26/2017
 * Last updated 7/26/2017
 * 
 * This class represents one major, defined by a major name
 * and a list of Requirements for that major.
 * 
 * Majors can be read and saved in a user-friendly text format - see 
 * the method readFrom(String) for an explanation.
 * 
 * Major is in the DATA group of classes.
 * 
 *
 */
public class Major implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	

	public static final String NORMAL_MAJOR = "Major";
	public static final String MINOR = "Minor";
	public static final String TRACK = "Track";
	
	//GER will pick the highest number given. 
	public static final int BA = 1;
	public static final int BS = 2;
	public static final int BM = 0;
	public static final int None = -1;
	
	public static final int MajorDDNRange = 100;
	
	
	String name; //the name of this major, like "Applied Math"
	
	ArrayList<Requirement> reqList; //The list of requirements that must be
	// satisfied to complete this major.
	
	ArrayList<Integer> reqFriendGroups; //used when loading a major to 
	// store which requirements are in the same friend group - see
	// the method readEnemiesLine for the algorithm behind requirement friend groups.
	ArrayList<Integer> degreeTypes = new ArrayList<Integer>();
	//The possible degree types (BS, BA, BM) that the user can pick for this major.
	
	public int chosenDegree = None;
	//The degree type (BS, BA, BM) that the user has chosen for this major when it is in
	//the schedule.
	
	
	public String notes;
	//Notes displayed to the user each time that they add this major to their schedule,
	//or when they check all errors.

	String majorType;

	public Major(String name){
		this.name = name;
		this.majorType = NORMAL_MAJOR;
		this.reqList = new ArrayList<Requirement>();
		this.reqFriendGroups = new ArrayList<Integer>();
	}

	public static Major testMajor(){
		Major result = new Major("Math");
		result.degreeTypes.add(BA);
		result.degreeTypes.add(BS);
		result.addRequirement(new Requirement(new Prefix[]{new Prefix("MTH", 250)}, 1));
		result.addRequirement(new Requirement(new Prefix[]{new Prefix("MTH", 260)}, 1));
		result.addRequirement(new Requirement(new Prefix[]{
				new Prefix("MTH", 350), new Prefix("MTH",450)}, 1));
		result.addRequirement(new Requirement(new Prefix[]{
				new Prefix("MTH", 360), new Prefix("MTH",460)}, 1));
		Requirement electives = new Requirement(new Prefix[]{
				new Prefix("MTH",151), 
				new Prefix("MTH",160),
				new Prefix("MTH",250),
				new Prefix("MTH",320),
				new Prefix("MTH",335),
				new Prefix("MTH",337),
				new Prefix("MTH",340),
				new Prefix("MTH",341),
				new Prefix("MTH",350),
				new Prefix("MTH",360),
				new Prefix("MTH",420),
				new Prefix("MTH",435),
				new Prefix("MTH",450),
				new Prefix("MTH",451),
				new Prefix("MTH",450),
				new Prefix("MTH",460),
				new Prefix("MTH",461),
				new Prefix("MTH",504)
		}, 		
				7);
		electives.setName("MTH Electives");
		result.addRequirement(electives);

		//result.reqFriendGroups.set(4, 2);
		//result.reqFriendGroups.set(3,1);
		//result.reqFriendGroups.set(2,1);
		//result.reqFriendGroups.set(1,1);
		RequirementGraph.putEdge(result.reqList.get(1), result.reqList.get(2));
		return result;
	}
	
	
	
	
	////////////////////////
	////////////////////////
	/////Getters and Setters
	////////////////////////
	////////////////////////
	@SuppressWarnings("unused")
	private boolean ___GettersAndSetters_________;
	

	
	/**
	 * Add this degree type to the list of possible degrees for this major.
	 * Returns false if the add was not successful.
	 * @param degreeType
	 * @return
	 */
	public boolean addDegreeType(String degreeType){
		if(degreeType.equals("BM")){
			this.degreeTypes.add(BM);
			return true;
		}
		if(degreeType.equals("BS")){
			this.degreeTypes.add(BS);
			return true;
		}
		if(degreeType.equals("BA")){
			this.degreeTypes.add(BA);
			return true;
		}
		return false;
	}


	
	
	
	

	public int getChosenDegree() {
		return chosenDegree;
	}

	public void setChosenDegree(int chosenDegree) {
		this.chosenDegree = chosenDegree;
	}
	
	/**
	 * If you know this major contains a particular requirement, 
	 * you can ask for it.
	 * Return null if not found.
	 * @param reqName
	 */
	public Requirement getRequirement(String reqName){
		for(Requirement r : reqList){
			if(reqName.equals(r.name)){
				return r;
			}
		}
		return null;
	}

	/**
	 * Sets the type of this major.
	 * Should be one of NORMAL_MAJOR, MINOR, or TRACK. 
	 * @param majorType
	 */
	public void setType(String majorType){
		this.majorType = majorType;
	}

	public boolean isType(String majorType){
		return this.majorType.equals(majorType);
	}

	public void addRequirement(Requirement r){
		this.reqList.add(r);
		reqFriendGroups.add(0);
	}
	
	public boolean removeRequirement(Requirement r){
		int index = 0;
		while(index < reqList.size() && !reqList.get(index).equals(r)){
			index ++;
		}
		if(index >= reqList.size()){
			return false;
		}
		this.reqList.remove(index);
		reqFriendGroups.remove((int)index);
		return true;
	}
	
	
	/**
	 * Add this line (which should be one paragraph 
	 * @param line
	 */
	public void addToNotes(String line){
		notes += line + "\n";
	}
	
	
	////////////////////////
	////////////////////////
	/////Saving and reading
	////////////////////////
	////////////////////////
	@SuppressWarnings("unused")
	private boolean ___SavingReading_________;
	

	
	
	
	public String saveString(){
		StringBuilder result = new StringBuilder();
		result.append(name + "\n");
		result.append(typeString + this.majorType + "\n");
		result.append(degreeTypeString);
		String degree="";
		if(degreeTypes.size()>0){
			for(int i: degreeTypes){

				degree = degree + (CourseList.getDegreeTypeString(i) + ", ");

			}


			String toAppend = degree.substring(0, degree.length()-2);
			result.append(toAppend);
			
		}
		result.append("\n");
		for (int i = 0; i  <this.reqList.size() ; i ++){
			Requirement r = this.reqList.get(i);
			result.append("R" + i);
			//Either R3 Name:1 of (MTH)
			// or    R3:1 of (MTH)
			String s = r.getName();
			if(s != null){
				result.append(" " + s + ":" + r.saveString() + "\n");
			}
			else{
				result.append(":" + r.saveString() + "\n");
			}
		}
		//Add the requirement graph data
		result.append(reqGraphString );
		for(int i = 0; i < reqList.size(); i ++){
			//if this requirement has a friend group
			if(reqFriendGroups.get(i) != 0){
				result.append("\nR" + i + ":" + reqFriendGroups.get(i));
			}
			//check to see if this requirement has any enemies within this major.
			else{
				ArrayList<Integer> enemies = new ArrayList<Integer>();
				for(int j = i+1 ; j < reqList.size(); j ++){
					if(!RequirementGraph.doesPlayNice(reqList.get(i), reqList.get(j))){
						enemies.add(j);
					}
				}
				if(enemies.size() > 0){
					String enemyString = "";
					for(int enemy : enemies){
						enemyString+= "R" + enemy + ", ";
					}
					enemyString = enemyString.substring(0, enemyString.length()-2);
					result.append("\nR" + i + ":" + enemyString);
				}
			}
		}
		return result.toString();
	}
	static final String typeString = "Type: ";
	static final String degreeTypeString = "Possible Degree(s):";
	static final String reqGraphString = "Collection of Requirement Enemies:";

	/**
	 * 
	 * MAJOR SAVING AND READING TUTORIAL
	 * 
	 * Example Major file:
	 * 
	 ***************************************
	 * Math-BS
	 * Type: Major
	 * Possible Degree(s): BA, BS
	 * R0:(MTH-250)
	 * R1:(MTH-260)
	 * R2:(MTH-350, MTH-450)
	 * R3:(MTH-360, MTH-460)
	 * R4 MTH Electives:7 of (MTH-151, MTH-160, MTH-250, MTH-320, MTH-335, MTH-341, MTH-350, MTH-360, MTH-420, MTH-435, or MTH-504)
	 * R5 MR: 1 of (MTH-150, 2 of (MTH 145, MTH 120) )
	 * Collection of Requirement Enemies:
	 * R0:R2,R5
	 * R3:4
	 * R4:4
	 * R5:3
	 * R1:ALL
	 * R1:R2
	 * Notes:
	 * The GPA of all Math courses must be at least 2.00 for successful completion of the major.
	 * See your advisor for details.
	 ****************************************
	 *
	 *
	 *EXPLANATION OF EXAMPLE FILE
	 *
	 *Spacing/newlines
	 * 		In a major file, there are a lot of details about spaces and punctuation.
	 * 		Sometimes spacing is ignored, sometimes it is not.
	 * 		Read this tutorial to find out when.
	 * 		Each line may be separated by as many newline characters as you want, but 
	 * 		if any characters exist in the line it is considered to be a line.
	 * 		    so, if '\n' means newline and you have a string of the form 
	 * 		    "MYLINE  \n\n  \nMYNEXTLINE "
	 * 		    which might look like
	 * 		    "
	 * 		    MYLINE__
	 * 		    
	 * 		    _
	 * 		    MYNEXTLINE_"
	 * 		    the invisible space above MYNEXTLINE would be counted as a line.
	 * 
	 * The file is organized into a number of sections. Each section is optional, excluding
	 * 		NAME_SECTION and REQUIREMENTS_SECTION.
	 * 
	 * NAME_SECTION
	 * TYPE_SECTION - defaults to Major
	 * POSSIBLE_DEGREES_SECTION - defaults to no choices
	 * REQUIREMENTS_SECTION
	 * REQUIREMENT_ENEMIES_SECTION
	 * NOTES_SECTION
	 * 
	 * 
	 * 
	 * 
	 * other explanations:
	 * 
	 ************************
	 *-->NAME_SECTION
	 * Math-BS   ---> This is the name of the major.
	 * 
	 * -->TYPE_SECTION
	 * Type: Major ---> This optional line can specify Major, Minor, or Track. 
	 * 			------> The string 'Type: ' must be exact, including the space
	 * 			------>    after the colon.
	 * -->POSSIBLE_DEGREES_SECTION
	 * Possible Degree(s): BA, BS ---> This optional line can specify which GER 
	 * 						--------->     types may be associated with this major. 
	 * 						---------> Values should be separated by commas, but it ignores spacing.
	 * 						---------> The string "Possible Degree(s):" should be exact.
	 * -->REQUIREMENTS_SECTION
	 * R0:(MTH-250)  --> This is one of the requirements for this major. 
	 *  			---> Each requirement must have an 'R' followed by a colon ':' followed by a requirement string.
	 *  			--->    See the Requirement class saving and reading tutorial for an explanation of valid requirements strings.
	 *  			---> Spacing after the colon is ignored, but spacing before the colon is important (see R4 below).
	 *  			---> The number after the R is ignored, but you may want to include one for your convenience when dealing
	 *  			--->     with requirement enemies (see "Collection of Requirement Enemies" below)
	 *  
	 * R1:(MTH-260)
	 * R2:(MTH-350, MTH-450)
	 * R3:(MTH-360, MTH-460)
	 * R4 MTH Electives:7 of (MTH-151, MTH-160, MTH-250, MTH-320, MTH-335, MTH-337, MTH-340, MTH-341, MTH-350, MTH-360, MTH-420, MTH-435, MTH-450, MTH-451, MTH-460, MTH-461, or MTH-504)
	 *     				------>The above requirement has a name associated with it.
	 *     				------>The name is defined to be all characters before the colon, but after the first space.
	 * 					------>So requirement R4 has name "MTH Electives".
	 *     				------>  Notice that "R5   this Is My Requirements Name : VALID-REQUIREMENT-STRING"
	 *     				------>  would make a requirement with a name "  this Is My Requirements Name ",
	 *     				------>  which includes extra spaces in the beginning and end. 
	 *     				------>  In addition, "R5 : VALID-REQUIREMENT-STRING" would make a requirement with name " ".
	 *     
	 * R5 MR: 1 of (MTH-150, 2 of (MTH 145, MTH 120) )
	 * 					------> This requirement has a name of MR and has an unnamed sub-requirement, 2 of (...)
	 * 					------> Sub-requirements cannot be named, and sub-requirements cannot be made into enemies of other requirements.
	 * -->REQUIREMENT_ENEMIES_SECTION
	 * Collection of Requirement Enemies: --> this string must be exact (no spaces), and it marks the beginning of the 
	 * 								-----> portion of the Requirement Graph stored by this major.
	 * 								-----> Edges of the requirement graph specify which requirements are enemies with each other,
	 * 								-----> meaning that if a course satisfies one requirement then that course cannot also satisfy the other
	 * 								-----> at the same time. For example, if r1 is "1 of (MTH-360, MTH 460)" and
	 * 								-----> r2 is the MTH elective requirement, the course MTH 360 could theoretically satisfy both
	 * 								-----> r1 and r2, but it is not allowed to do so. This means that r1 and r2 are  enemies.
	 * 
	 * R0:R2,R5 --------> This line means that requirement 0 in this major (the first one listed) is enemies with 
	 *       -------->     requirement 2 in this major (the third one listed) and requirement 5 in this major 
	 *       -------->     (the sixth one listed). The numbers here (0, 2, and 5) refer to the
	 *       -------->     requirement's place in the list, NOT to the numbers used in the first section. 
	 *       --------> If you mis-number a requirement in the first section, for example writing:
	 *       -------->     R4: VALID-REQUIREMENT-STRING
	 *       -------->     R4: OTHER-VALID-REQUIREMENT-STRING 
	 *       -------->     then the second requirement will still be treated as R5 in this section. Only
	 *       -------->     the order matters, not the number used in the first section.
	 *       -------->     If you delete a requirement (say, removing the entire line specifying R2) then all
	 *       -------->     requirement reference numbers will decrement by one in this section, and you will need
	 *       -------->     to rewrite the Collection of Requirement Enemies section.
	 *       
	 * R3:4 -----> Enemies may also be specified by their friend groups. If you omit all "R"s after the colon,
	 *      ----->     then the program will assume you are specifying a friend group. Direct enemy specification requires
	 *      ----->     the presence of the "R" before each number. 
	 *      -----> Friend group 0 is reserved to mean 'no friend group', and should not be used in a file. 
	 *      -----> Requirements with no friend group, or with group 0, can still be friends 
	 *      ----->     with any requirement in a friend group.
	 *      -----> A requirement with a friend group is automatically made enemies with any other requirement 
	 *      ----->     that has a friend group, unless they are in the same friend group. In this example, 
	 *      ----->     the friend groups mean that R3 and R5 are enemies, R4 and R5 are enemies, 
	 *      ----->     but R3 and R4 are friends. In addition, R3 is still friends with R1 and R0.
	 *      ----->     If not for the line "R0: R2, R5", then R5 would still be friends with R0,
	 *      ----->     but because of that line R5 is not friends with R0.
	 * R4:4
	 * R5:3
	 * R1:ALL----> If the string "ALL" is the only string on an enemy specification line (no spaces allowed), 
	 *      ----->     then this requirement will be made a "loner."
	 * 		-----> "Loners" are enemies with all other requirements (including requirements from other majors)
	 * 		----->     The requirement graph takes on the opposite meaning for loners - if there is an edge between
	 * 		----->     two requirements and one of them is a loner, then the requirements are friends, and if there is no
	 * 		----->     edge between two requirements and one is a loner, then they requirements are enemies.
	 * R1:R2 ----> In this example, R1 is enemies with every requirement other than R2.
	 * 
	 * -->NOTES_SECTION
	 * Notes: ---> This line marks the start of the notes section.
	 * 		-----> It must contain the string "notes:" (any capitalization) and will ignore the rest of the line.
	 * 		-----> Each future line will be considered one note - each note is displayed as a bullet point to the user 
	 * 		----->     when they see the notes for the major. 
	 * 		-----> The "Notes:" line can be omitted, but everything line that is not in any of the previously mentioned sections
	 * 		----->     is considered to be part of the notes section (so if you omit the "Notes:" line, notes may still exist.)
	 * 		----->     That means that if you misspell this line, it will be considered to be a note and will be 
	 * 		----->     displayed to the user.
	 * 		-----> We use notes to convey information that the program cannot handle
	 * 		----->     or that the program does not explain clearly.
	 * 
	 * The GPA of all Math courses must be at least 2.00 for successful completion of the major.
	 * See your advisor for details.
	 * 		-----> This major has 2 notes, each of which will be given a bullet point.
	 ************************ 
	 * 
	 * 
	 * 
	 * @param saveString
	 * @return
	 */
	public static Major readFrom(String saveString){
		saveString = saveString.replaceAll("\\r", "\n"); //windows adds \r to some newlines.
		saveString = saveString.trim();
		String[] lines = saveString.split("[\\n]+");
		//Name
		Major result = new Major(lines[0].trim());
		
		//startIndex is used until we have finished the header section and reach
		// the section that specifies requirements.
		//startIndex should then be index of the first requirement line.
		int startIndex = 1;
		if(lines[startIndex].contains(typeString)){
			result.setType(lines[startIndex].substring(typeString.length()));
			startIndex ++;	
		}
		if(lines[startIndex].contains(degreeTypeString)){
			String cat = lines[startIndex].substring(degreeTypeString.length());
			String noSpace = cat.replaceAll("\\s+","");
			String[] degreeType = noSpace.split(",");
			for(String s: degreeType){
				result.addDegreeType(s);
			}
			startIndex++;
		}
		//requirements section
		int i = startIndex;
		while(i < lines.length && lines[i].indexOf("R") == 0){
			Requirement newRequirement = readRequirementLine(lines[i]);
			result.addRequirement(newRequirement);
			i++;
		}
		//enemies section
		if(i == lines.length){
			return result;
		}
		if(lines[i].equals(reqGraphString)){
			i++;
			while(i < lines.length && lines[i].indexOf("R") == 0){
				result.readEnemiesLine(lines[i]);
				i++;
			}
		}
		result.addEnemyEdgesFromFriendGroups();
		//Notes section
		if(i < lines.length){
			result.notes = "";
			//Skip the line saying "Notes:"
			if(lines[i].toUpperCase().contains("NOTES:")){
				i++;
			}
		}
		for(; i <lines.length ; i ++){
			result.addToNotes(lines[i]);
		}
		return result;
	}

	/**
	 * Get the requirement specified by this line (assumed to be in the form
	 * of a line in the REQUIREMENT section of a saved major.)
	 * @param s
	 * @return
	 */
	private static Requirement readRequirementLine(String s){
		int colonIndex = s.indexOf(":");
		int firstSpace = s.indexOf(" ");
		String reqString = s.substring(colonIndex + 1);
		Requirement newRequirement = Requirement.readFrom(reqString);
		if(firstSpace != -1 && firstSpace < colonIndex){
			String name = s.substring(firstSpace, colonIndex).trim();
			if(!name.trim().equals("")){ //This keeps mistakes in spacing from messing up the naming. 
				newRequirement.setName(name);
			}
		}
		return newRequirement;
	}

	/**
	 * Update the Requirement Graph based on this string,
	 * 		assumed to be of the form of a line in the 
	 * 		REQUIREMENT_ENEMIES section of a major file.
	 * 
	 * so given "R3: R1, R2, R7 "
	 *  	 or "R3: 5"
	 *  	 or "R3: ALL"
	 *  the first means to make r1, r2, and r7 enemies of r3.
	 *   the middle means to put r3 in friend group 5
	 *   the third means to make r3 a loner, so it is enemies with all requirements
	 *   
	 * @param s
	 */
	private void readEnemiesLine(String s){
		String[] split = s.split(":");
		int firstReqNum = parseReqNumber(split[0]);
		if(split[1].contains("R")){
			String[] nestedSplit = split[1].split(",");
			for(String enemyS : nestedSplit){
				int nextReqNum = parseReqNumber(enemyS);
				RequirementGraph.putEdge(this.reqList.get(firstReqNum), this.reqList.get(nextReqNum));
			}
		}
		else{
			if(split[1].toUpperCase().equals("ALL")){
				RequirementGraph.makeLoner(this.reqList.get(firstReqNum));
			}
			else{
				reqFriendGroups.set(firstReqNum,Integer.parseInt(split[1]));
			}
		}
	}
	private int parseReqNumber(String s){
		return Integer.parseInt(s.split("R")[1]);
	}
	
	/**
	 * Look through the friend groups for this major's requirements.
	 * If any two requirements have differing friend groups and neither one
	 * has group 0, then put an enemy edge between them.
	 */
	private void addEnemyEdgesFromFriendGroups(){
		for(int i = 0; i < reqList.size() ; i ++){
			int groupNumber = reqFriendGroups.get(i);
			if(groupNumber == 0){
				continue;
			}
			for(int j = i+1 ; j < reqList.size() ; j ++){
				int otherGroup = reqFriendGroups.get(j);
				if(otherGroup != 0 && groupNumber != otherGroup){
					RequirementGraph.putEdge(reqList.get(i), reqList.get(j));
				}
			}
		}
	}


	@Override
	public String toString(){
		return this.name;
	}

	@Override 
	public boolean equals(Object o){
		if(!(o instanceof Major)){
			return false;
		}
		Major other = (Major) o;
		if(this.name.equals(other.name)){
			return true;
		}
		else{
			return false;
		}
	}

	public static void main(String[] args){
		Major t = Major.testMajor();
		System.out.println(t.saveString());

		Major x = Major.readFrom(t.saveString());
		System.out.println(x.saveString());

		System.out.println(RequirementGraph.doesPlayNice(x.reqList.get(1), x.reqList.get(2)));

		//ListOfMajors m = ListOfMajors.testList();
		//System.out.println(m.getCompleteMajorsList().get(1).saveString());

	}

	public boolean isNormalMajor() {
		return(this.majorType.equals(Major.NORMAL_MAJOR));
	}




}
