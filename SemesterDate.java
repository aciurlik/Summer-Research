

public class SemesterDate {
	public static final int SPRING = 1;
	public static final int FALL = 4;
	public static final int SUMMER = 3;
	public static final int MAYX = 2;
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

	public String saveString(){
		return this.year + "-" + this.sNumber;
	}
	public static SemesterDate readFrom(String saveString){
		String[] parsed = saveString.split("-");
		return new SemesterDate(Integer.parseInt(parsed[0]), Integer.parseInt(parsed[1]));
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

	/**
	 * Return the next school semester, either in fall or spring.
	 *  
	 * @return
	 */
	public SemesterDate next(){
		if(this.sNumber == SemesterDate.FALL){
			return new SemesterDate(this.year + 1, SemesterDate.SPRING);
		}
		if(this.sNumber == SemesterDate.SPRING){
			return new SemesterDate(this.year, SemesterDate.FALL);
		}
		else{
			return new SemesterDate(this.year,(this.sNumber + 1)%4 );
		}
	}

	
	public String getSeason(int p){
		String[] season = {null, "Spring", "MayX", "Summer", "Fall", "Other"};


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
	
 @Override 
 public boolean equals(Object other){
	 if(!(other instanceof SemesterDate)){
		 return false;
	 }
	 SemesterDate o = (SemesterDate) other;
	 if(o.compareTo(this)==0){
		 return true;
	 }
	 else{
		 return false;
	 }
	 
	 
	 
 }
}
