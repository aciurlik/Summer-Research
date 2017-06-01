
/**
 * The essential elements of a course, without any time associated.
 * 
 * @author dannyrivers
 *
 */
public class Prefix implements Comparable<Prefix>, JSONable<Prefix>{
	private String subject;
	private String courseNumber; //Became a string when we found a few strange courses.


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

	public String toString(){
		return this.subject + "-" + this.courseNumber;
	}

	public static Prefix readFrom(String prefixString){
		String[] pair = prefixString.split("-");
		return new Prefix (pair[0], pair[1]);
	}

	@Override
	public int compareTo(Prefix other) {
		int strDiff = this.subject .compareTo(other.subject);
		if(strDiff!=0){
			return strDiff;
		}
		return this.courseNumber.compareTo(other.courseNumber);
	}

	
	public static Prefix readFromJSON(String s) {
		return readFrom(SaverLoader.peel(s));
	}

	@Override
	public String saveAsJSON() {
		return this.toString();
	}

}
