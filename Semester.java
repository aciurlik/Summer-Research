
public class Semester {
	public static final int SPRING = 2;
	public static final int FALL = 1;
	public static final int SUMMER = 3;
	public static final int MAYX = 4;
	public static final int OTHER = 5;
	
	int year;
	int sNumber; //one of FALL, SPRING, MAYX, SUMMER, OTHER
	
	public Semester(int year, int semesterNumber){
		this.year = year;
		this.sNumber = semesterNumber;
	}
	
	public int getYear(){
		return year;
	}
	
	public int getStartMonth(){
		switch(sNumber){
		case FALL:
			return 8;
		case SPRING:
			return 1;
		case SUMMER:
			return 6;
		case MAYX:
			return 5;
		default:
			return 0;
		}
	}
}
