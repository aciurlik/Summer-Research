
/**
 * The essential elements of a course, without any time associated.
 * 
 * @author dannyrivers
 *
 */
public class Prefix {
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
	
}
