

public class SemesterDate {
	public static final int SPRING = 1;
	public static final int FALL = 2;
	public static final int SUMMER = 3;
	public static final int MAYX = 4;
	public static final int OTHER = 5;
	
	int year;
	int sNumber; //one of FALL, SPRING, MAYX, SUMMER, OTHER
	
	public SemesterDate(int year, int semesterNumber){
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
	
	public SemesterDate next(){
		if(this.sNumber == SemesterDate.FALL){
			return new SemesterDate(this.year + 1, SemesterDate.SPRING);
		}
		else{
			return new SemesterDate(this.year,(this.sNumber + 1)%4 );
		}
	}

	public String getSeason(int p){
		String[] season = {null, "Spring", "Fall", "Summer", "MayX", "Other"};
				
				
		return season[p];
		
	}
	
	//@Override
	public int compareTo(SemesterDate o) {
		if(this.year < o.year){
			return - 1;
		}
		if(this.year > o.year){
			return  1;
		}
		
		if(this.sNumber < o.sNumber){
			return -1;
		}
		if(this.sNumber == o.sNumber && this.year == o.year){
			return 0;
		}
		else{
			return 1;
		}
		
	}
}
