import java.util.ArrayList;
import java.util.HashSet;


/**
 * Blurb written: 7/26/2017
 * Last updated: 7/26/2017
 * 
 * This class represents the subject and number of a course, as in 
 * "MTH-120" or "BUS-3320".
 * 
 * It is part of the DATA group of classes.
 * 
 * 
 *
 */
public class Prefix implements ScheduleElement, Comparable<Prefix>, java.io.Serializable{
	private static final long serialVersionUID = 1L;
	private String subject;
	private String courseNumber; //We made this field into
	// a string when we found a few strange courses, like
	// BUS-BLK (business block) and MUS-120A (unknown what this is).


	
	public Prefix(String subject, String courseNumber) {
		this.subject = subject;
		this.courseNumber = courseNumber;
	}
	
	public Prefix(String subject, int courseNumber){
		this.subject = subject;
		this.courseNumber = "" + courseNumber;
	}

	public String getSubject() {
		return subject;
	}
	public String getNumber() {
		return courseNumber;
	}

	/**
	 * User friendly and easy to parse:
	 * "subject-number"
	 * @return
	 */
	public String toString(){
		return this.subject + "-" + this.courseNumber;
	}

	
	public static Prefix readFrom(String prefixString){
		String[] pair = prefixString.split("-");
		return new Prefix (pair[0], pair[1]);
	}

	@Override
	public boolean equals(Object o){
		if(!(o instanceof Prefix)){
			return false;
		}
		Prefix other = (Prefix)o;
		return this.subject.equals(other.subject) && this.courseNumber.equals(other.courseNumber);
	}

	@Override
	public int hashCode(){
		return subject.hashCode() + courseNumber.hashCode();
	}


	@Override
	public int compareTo(Prefix other) {
		int subjDiff = this.subject.compareTo(other.subject);
		if(subjDiff!=0){
			return subjDiff;
		}
		return this.courseNumber.compareTo(other.courseNumber);
	}

	////////////////////////
	////////////////////////
	/////// Methods from ScheduleElement
	////////////////////////
	////////////////////////
	
	@Override
	public Prefix getPrefix() {
		return this;
	}

	@Override
	public int getCreditHours() {
		return CourseList.getCoursesCreditHours(this);
	}

	@Override
	public boolean isDuplicate(ScheduleElement other) {
		return other.getPrefix().equals(this);
	}

	@Override
	public String getDisplayString() {
		return this.toString();
	}

	@Override
	public String shortString(int preferredLength) {
		return this.toString();
	}

	@Override
	public ArrayList<Requirement> filterEnemyRequirements(
			ArrayList<Requirement> loaded) {
		//remove all enemies
		HashSet<Requirement> enemies = RequirementGraph.enemiesIn(new HashSet<Requirement>(loaded));
		ArrayList<Requirement> result = new ArrayList<Requirement>();
		for(Requirement r : loaded){
			if(!enemies.contains(r)){
				result.add(r);
			}
		}
		return result;
	}
}
