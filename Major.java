import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;



public class Major implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String name;
	int groupNumber;
	ArrayList<Requirement> reqList;
	ArrayList<Integer> reqFriendGroups;
	ArrayList<Integer> degreeTypes = new ArrayList<Integer>();
	public int chosenDegree;
	
	public String notes;



	public static final String NORMAL_MAJOR = "Major";
	public static final String MINOR = "Minor";
	public static final String TRACK = "Track";

	String majorType;


	int degreeType;

	public static final int MajorDDNRange = 100;

	public Major(String name){
		this.name = name;
		this.majorType = NORMAL_MAJOR;
		this.reqList = new ArrayList<Requirement>();
		this.reqFriendGroups = new ArrayList<Integer>();
	}

	public static Major testMajor(){
		Major result = new Major("Math");
		result.degreeTypes.add(CourseList.BA);
		result.degreeTypes.add(CourseList.BS);
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
	
	public void addDegreeType(String degreeType){
		if(degreeType.equals("BM")){
			this.degreeTypes.add(CourseList.BM);
		}
		if(degreeType.equals("BS")){
			this.degreeTypes.add(CourseList.BS);
		}
		if(degreeType.equals("BA")){
			this.degreeTypes.add(CourseList.BA);
		}
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
	
	public void addToNotes(String line){
		notes += line + "\n";
	}
	public String getNodes(){
		return this.notes;
	}






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
	 * SYNTAX AND FORMAT OF MAJOR FILES.
	 * 
	 * Example file:
	 * 
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
	 * R1:R2
	 * R3:4
	 * R4:4
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * Example file with explanations:
	 * 
	 * Math-BS   ---> This is the name of the major.
	 * 
	 * Type: Major ---> This optional line can specify Major, Minor, or Track. 
	 * 			------>  The string 'Type: ' must be exact.
	 * 
	 * Possible Degree(s): BA, BS ---> This optional line can specify which GER 
	 * 						--------->   types may be associated with this major. 
	 * 						--------->  Values should be separated by commas, but it ignores spacing.
	 * 						---------> The string "Possible Degree(s):" should be exact.
	 * 
	 * R0:(MTH-250)  --> This is one of the requirements for this major. See the Requirement class saving and reading tutorial
	 *  		   ---->  for an explanation of valid requirements.
	 *  
	 * R1:(MTH-260)
	 * R2:(MTH-350, MTH-450)
	 * R3:(MTH-360, MTH-460)
	 * R4 MTH Electives:7 of (MTH-151, MTH-160, MTH-250, MTH-320, MTH-335, MTH-337, MTH-340, MTH-341, MTH-350, MTH-360, MTH-420, MTH-435, MTH-450, MTH-451, MTH-460, MTH-461, or MTH-504)
	 *     				------>This requirement has a name associated with it.
	 *     				------> The name is defined to be all characters before the colon, but after the first space.
	 *     				------>  So, "R5  MYNAME: VALIDREQUIREMENTSTRING"
	 *     				------>  would make a requirement with a name ' MYNAME', not 'MYNAME'. 
	 *     
	 * R5 MR: 1 of (MTH-150, 2 of (MTH 145, MTH 120) )
	 * 					------> This requirement has a name of MR and has an unnamed sub-requirement.
	 * 
	 * Collection of Requirement Enemies: --> this string must be exact, and it marks the beginning of the 
	 * 								-----> portion of the Requirement Graph stored by this major.
	 * 								-----> The requirement graph specifies which requirements are enemies,
	 * 								-----> meaning that a course satisfying one cannot also satisfy the other
	 * 								-----> at the same time. For example, if r1 is "1 of (MTH-360, MTH 460)" and
	 * 								-----> r2 is the MTH elective requirement, the course MTH 360 might satisfy both
	 * 								-----> r1 and r2, but it is not allowed to do so. This means that r1 and r2 are 
	 * 								-----> enemies.
	 * 
	 * R1:R2
	 * R3:4 -----> Enemies may also be specified by friend groups. A requirement with a friend group
	 * 		-----> is automatically enemies with any other requirement that has a friend group, unless 
	 * 		-----> they are in the same friend group. In this example, the friend groups mean that
	 * 		-----> R3 and R5 are enemies, R4 and R5 are enemies, but R3 and R4 are friends.
	 * R4:4
	 * R5:3
	 * R1:ALL----> If the string "ALL" is found, this requirement will be made a loner.
	 * 		-----> Loners are enemies with all other requirements (including from other majors)
	 * 		-----> except for those specified here.
	 * R1:R2 ----> In this example, R1 is enemies with every requirement other than R2.
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * @param saveString
	 * @return
	 */
	public static Major readFrom(String saveString){
		saveString = saveString.replaceAll("\\r", "\n");
		saveString = saveString.trim();
		String[] lines = saveString.split("[\\n]+");
		Major result = new Major(lines[0]);
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
		int i = startIndex;
		while(i < lines.length && lines[i].indexOf("R") == 0){
			Requirement newRequirement = readRequirementLine(lines[i]);
			result.addRequirement(newRequirement);
			i++;
		}
		//Check if there are any reqGraph edges listed after.
		if(i == lines.length){
			return result;
		}
		if(!lines[i].equals(reqGraphString)){
			throw new RuntimeException("The " + result.name + " file has an incorrect key string.\n"
					+ "Should be " + reqGraphString );
		}
		i++;
		while(i < lines.length && lines[i].indexOf("R") == 0){
			result.readEnemiesLine(lines[i]);
			i++;
		}
		result.addEnemyEdgesFromFriendGroups();
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

	private static Requirement readRequirementLine(String s){
		int colonIndex = s.indexOf(":");
		int firstSpace = s.indexOf(" ");
		String reqString = s.substring(colonIndex + 1);
		Requirement newRequirement = Requirement.readFrom(reqString);
		if(firstSpace != -1 && firstSpace < colonIndex){
			String name = s.substring(firstSpace, colonIndex).trim();
			newRequirement.setName(name);
		}
		return newRequirement;
	}

	/**
	 * Given a line of the form "R3: R1, R2, R7 "
	 *  or  "R3: 5"
	 *  or "R3: ALL"
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
	private void addEnemyEdgesFromFriendGroups(){
		for(int i = 0; i < reqList.size() ; i ++){
			int groupNumber = reqFriendGroups.get(i);
			if(groupNumber == 0){
				continue;
			}
			for(int j = i+1 ; j < reqList.size() ; j ++){
				if(reqFriendGroups.get(i)!=reqFriendGroups.get(j)){
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




}
