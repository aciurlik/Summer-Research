
/**
 * The essential elements of a course, without any time associated.
 * 
 * @author dannyrivers
 *
 */
public class Prefix implements Comparable<Prefix>, JSONable<Prefix>{
	private String subject;
	private int courseNumber;


	public Prefix(String subject, int courseNumber) {
		this.subject = subject;
		this.courseNumber = courseNumber;
	}

	public String getSubject() {
		return subject;
	}
	public int getNumber() {
		return courseNumber;
	}

	public String toString(){
		return this.subject + "-" + this.courseNumber;
	}

	public static Prefix readFrom(String prefixString){
		String[] pair = prefixString.split("-");
		return new Prefix (pair[0], Integer.parseInt(pair[1]));
	}

	@Override
	public int compareTo(Prefix other) {
		int strDiff = this.subject .compareTo(other.subject);
		if(strDiff!=0){
			return strDiff;
		}
		return this.courseNumber - other.courseNumber;
	}

	
	public static Prefix readFromJSON(String s) {
		return readFrom(SaverLoader.peel(s));
	}

	@Override
	public String saveAsJSON() {
		return this.toString();
	}

}
