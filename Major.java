import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;



public class Major {
	String name;
	int groupNumber;
	ArrayList<Requirement> reqList;
	ArrayList<Integer> reqFriendGroups;
	ArrayList<Integer> degreeTypes = new ArrayList<Integer>();
	
	public int chosenDegree;
	


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

	public static Major testMajor(){
		Major result = new Major("Math-BS");
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

	
	

	static final String typeString = "Type: ";
	static final String degreeTypeString = "Possible Degree(s):";
	static final String reqGraphString = "Collection of Requirement Enemies:";
	public String saveString(){
		StringBuilder result = new StringBuilder();
		result.append(name + "\n");
		result.append(typeString + this.majorType + "\n");
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
					if(!RequirementGraph.doesPlaysNice(reqList.get(i), reqList.get(j))){
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


	/**
	 * 
	 * @param saveString
	 * @return
	 */
	public static Major readFrom(String saveString){
		String[] lines = saveString.split("[\n]+");
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
		while(lines[i].indexOf("R") == 0){
			Requirement newRequirement = readRequirementLine(lines[i]);
			result.addRequirement(newRequirement);
			i++;
		}
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
	 *  the former means to make r1, r2, and r7 enemies of r3.
	 *   the latter means to put r3 in friend group 5
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
			reqFriendGroups.set(firstReqNum,Integer.parseInt(split[1]));
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
	


	public static Major readFrom(File f){

		Scanner scan;
		try {
			scan = new Scanner(f);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		scan.useDelimiter("\\Z");
		String contents = scan.next();
		scan.close();
		return readFrom(contents);
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
		
		//ListOfMajors m = ListOfMajors.testList();
		//System.out.println(m.getCompleteMajorsList().get(1).saveString());

	}




}
