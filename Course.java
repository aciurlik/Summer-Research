package scheduler;


public class Course {
	
	
	private class Prefix{
	 private int ClassNumber;
	 private int SectionNumber;
	 private String MajorPrefix;
	 

/**
 * This creates a prefix object, used by the requirements. For example MTH-420-01
 * @param c Class number is 420
 * @param s Section Number 01
 * @param m Major Prefix MTH
 */

	public Prefix( int c, int s, String m){
		ClassNumber=c;
		SectionNumber=s;
		MajorPrefix=m;
	}
	 
	public int getClassNumber() {
		return ClassNumber;
	}




	public int getSectionNumber() {
		return SectionNumber;
	}





	public String getMajorPrefix() {
		return MajorPrefix;
	}


	}
	
	
	private String [] GER;
	private int CreditHours;
	private Prefix CoursePrefix;
	
/**	
 * Creates a course object

 * @param c
 * @param p
 */

public Course( int c, Prefix p, String[]g){

	CreditHours=c;
	CoursePrefix=p;
	GER=g;
}
	
	public Prefix getPrefix(){
		return CoursePrefix;
	}
	
    public int getCreditHours(){
    	return CreditHours;
    
    }
    
    public String[] getGER(){
    	return GER;
    }
/**
 * This is to keep the Course List Error free still have to add to Course object. 
 * @return
 */
	public int getSemester() {
		// TODO Auto-generated method stub
		return 0;
	}
}
