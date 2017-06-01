import java.util.regex.Matcher;
import java.util.regex.Pattern;



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
	
	public SemesterDate(String season, int year){
		this.year = year;
		this.sNumber = toSNumber(season);
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
	public static SemesterDate fromFurman(String semesterString){
		//Examples:
		// May Experience 2017 - Day
		// Fall 2017 - Day
		Matcher m = Pattern.compile("\\d+").matcher(semesterString);
		if(!m.find()){
			throw new RuntimeException("Can't make a semester from the string" + semesterString);
		}
		String yearString = m.group();
		String semesterName = semesterString.substring(0, m.start());
		
		return new SemesterDate(Integer.parseInt(yearString), toSNumber(semesterName));
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
		else{
			return new SemesterDate(this.year,(this.sNumber + 1)%4 );
		}
	}

	public String getSeason(int p){
		String[] season = {null, "Spring", "Fall", "Summer", "MayX", "Other"};


		return season[p];
	}
	public static int toSNumber(String season){
		switch(season.toUpperCase().replaceAll(" ", "")){
		case "FALL":
			return FALL;
		case "SPRING":
			return SPRING;
		case "MAYX:":
		case "MAY-X":
		case "MAYEXPERIENCE":
			return MAYX;
		case "SUMMER":
			return SUMMER;
		default:
			return OTHER;
		}
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
