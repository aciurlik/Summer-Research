import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Blurb written: 7/27/2017
 * Last updated: 7/27/2017
 * 
 * A handy time class.
 * 
 * Stores year, month, day, hour, min, and second.
 * Year is the number of years since 2000, so the 0 time
 * (the time using 0 seconds) is Jan 1 2000 at the start of the day.
 * 
 *
 */
public class Time implements Comparable<Time>, java.io.Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int SUNDAY = 0;
	public static final int MONDAY = 1;
	public static final int TUESDAY = 2;
	public static final int WEDNESDAY = 3;
	public static final int THURSDAY = 4;
	public static final int FRIDAY = 5;
	public static final int SATURDAY = 6;



	public static final int secsInMin = 60;
	public static final int secsInHour = 60 * secsInMin;
	public static final int secsInDay = 24 * secsInHour;
	public static final int secsInNormalYear = 365 * secsInDay;

	public static final String[] daysConversion = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

	public static final int UNUSED = -1;
	public static final int AM = 0;
	public static final int PM = 1;


	public static final char[] dayCodes = {'U', 'M', 'T', 'W', 'R', 'F', 'S'};
	public static HashMap<Character, Integer> reverseDayCodes;
	static {
		reverseDayCodes = new HashMap<Character, Integer>();
		for(int i = 0; i < dayCodes.length ; i ++){
			reverseDayCodes.put(dayCodes[i], i);
		}
	}

	//If a field is unused, it will have this value.
	// However, some methods have undefined behavior if particular fields are unused.
	// For example, if t doesn't use days, and you say t.addMinutes(5000), t may have a day.
	//When necessary (for example, calculating total seconds) unused values are taken from the time
	// Jan 1 2000 12:00:00 AM 

	//However, if the month is unused, treat the day as a day of week.


	int day; //(1) thru (numDays)
	int month; // 1 thru 12
	int year; // regular year, as in 2017 right now.
	int hours; // military time, so 0 thru 23
	int minutes; //0 thru 59
	int seconds; //0 thru 59

	/**
	 * Copy constructor
	 * @param t
	 */
	public Time(Time t){
		this.year = t.year;
		this.month = t.month;
		this.day = t.day;
		this.hours = t.hours;
		this.minutes = t.minutes;
		this.seconds = t.seconds;
	}
	/**
	 * Complete constructor
	 * @param year
	 * @param month
	 * @param day
	 * @param hours
	 * @param minutes
	 * @param seconds
	 */
	public Time(int year, int month, int day, int hours, int minutes, int seconds){
		this.year = year;
		this.month = month;
		this.day = day;
		this.hours = hours;
		this.minutes = minutes;
		this.seconds = seconds;
	}
	/**
	 * Short constructor
	 * @param year
	 * @param month
	 * @param day
	 * @param hours
	 */
	public Time(int year, int month, int day, int hours, int minutes){
		this(year, month, day, hours, minutes, UNUSED);
	}

	/**
	 * Assumes military time for hours
	 * @param hours
	 * @param minutes
	 * @param seconds
	 */
	public Time(int hours, int minutes, int seconds){
		this(UNUSED, UNUSED, UNUSED, hours, minutes, seconds);
	}
	/**
	 * Uses normal hours, so 2:30 PM is 
	 *   hours = 2, AM = false, minutes = 30, seconds  = 0.
	 * @param hours
	 * @param AM
	 * @param minutes
	 * @param seconds
	 */
	public Time(int hours, boolean AM,  int minutes, int seconds ){
		this( Time.toMilitary(hours, AM), minutes, seconds);
	}


	/////////////////////
	/////////////////////
	///// Utilities
	/////////////////////
	/////////////////////
	@SuppressWarnings("unused")
	private boolean ___Utilities_________;

	

	/**
	 * This method returns an estimated number of seconds since midnight jan1 2000
	 * This estimate does not take into account leap seconds, 
	 * but it will take into account leap years.
	 * The point is to have a consistent value across all
	 * instances of the Time class.
	 * @return
	 */
	public long toSec(){
		//60 sec in a min
		//3600 sec in an hour
		// 60 * 60 * 24 sec in a day
		// 60 * 60 * 24 * 365 sec in a year (or 366 for leap year)
		long totalSec = 0;


		//TODO there is an issue calculating negative leap years.
		// It's taking away one day to many if the time is before 
		// 2000 (negative yearsSince)
		// and is during a leap year.

		if(this.year != UNUSED){
			int yearsSince = this.year - 2000;
			int leapYears = yearsSince / 4;
			leapYears += - yearsSince / 100; // if divisible by 100, 
			//a year isn't a leap year.

			totalSec += yearsSince *secsInNormalYear; //every year has 365 days
			totalSec += leapYears * secsInDay;    //but leap years add an extra day

			//If it is currently a leap year then we shouldn't have added the 
			// extra day from this year (it will be handled in future calculations)
			if(this.isLeapYear()){
				totalSec -= secsInDay;
			}
		}
		if(this.month != UNUSED && this.day != UNUSED){
			totalSec += this.daysThisYear() *secsInDay;
		}
		else if(this.month == UNUSED && this.day != UNUSED){
			totalSec += this.day * secsInDay;
		}
		if(this.hours != UNUSED){
			totalSec += (this.hours) * secsInHour;
		}
		if(this.minutes != UNUSED){
			totalSec += this.minutes * secsInMin;
		}
		if(this.seconds != UNUSED){
			totalSec += this.seconds;
		}

		return totalSec;
	}
	/**
	 * Given a number of seconds, return a time that is that many seconds
	 * from midnight jan 1 2000.
	 * @param seconds
	 * @return
	 */
	public static Time timeFromSeconds(long seconds) {
		int	resultSecs = (int) (seconds%secsInMin);
		int totalMinutes = (int) (seconds/secsInMin);
		int resultMinutes = totalMinutes%60;
		int totalHours = totalMinutes/60;
		int resultHours = totalHours%24;
		int totalDays = totalHours/24;

		Time  startTime = new Time(2000, 1, 1, resultHours, resultMinutes, resultSecs);
		return(startTime.addDays(totalDays));
	}
	
	@Override
	public int compareTo(Time that) {
		long result = this.toSec() - that.toSec();
		if(Integer.MIN_VALUE < result  && result < Integer.MAX_VALUE){
			return (int)result;
		}
		if(result > 0){
			return Integer.MAX_VALUE;
		}
		if(result < 0){
			return Integer.MIN_VALUE;
		}
		return 0;
	}
	/**
	 * Number of complete minutes until the other time.
	 * So 4:43:10 is 1 min from 4:45:0 because there are only 110 seconds between them.
	 * @param other
	 * @return
	 */
	public int minutesUntil(Time other){
		return (int)((other.toSec() - this.toSec()) / 60); 
	}

	


	/**
	 * Given a code of the form "MWF" make the correct
	 * meeting days list.
	 * @param dayCode
	 * @return
	 */
	public static int[] meetingDaysFrom(String dayCode){
		char[] days = dayCode.toCharArray();
		int[] result = new int[days.length];
		for(int i = 0; i < result.length ; i ++){
			result[i] = reverseDayCodes.get(days[i]);
		}
		return result;
	}
	

	public final static int[] daysInMonth = {31,28,31,30,31,30,31,31,30,31,30,31};
	public static int daysInMonth(int month){
		return daysInMonth[month - 1];
	}
	

	/**
	 * Return the day code for this day, one of
	 * U, M, T, W, R, F, S
	 * @param day
	 * @return
	 */
	public static String dayCode(int day){
		return "" + dayCodes[day];
	}


	public boolean isAllUnused(){
		if(this.day==Time.UNUSED && this.hours==Time.UNUSED && this.minutes==Time.UNUSED && this.month==Time.UNUSED && this.seconds==Time.UNUSED && this.year==Time.UNUSED){
			return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object Time){
		if(Time instanceof Time){
			Time other = (Time)Time;
			if(other.year==this.year && other.month == this.month && other.day==this.day && other.hours==this.hours
					&& other.minutes==this.minutes && other.seconds == this.seconds){
				return true;
			}
			
				
		}
		return false;
	}
	
	
	/////////////////////
	/////////////////////
	///// Time Operations
	/////////////////////
	/////////////////////
	// These methods return new times
	//
	//
	@SuppressWarnings("unused")
	private boolean ___TimeOperations_________;
	/**
	 * Return a time with the same hour, min, and sec as this time, with
	 * year, month, and day unused.
	 * @return
	 */
	public Time dateless(){
		return new Time(UNUSED, UNUSED, UNUSED, this.hours, this.minutes, this.seconds);
	}
	
	
	/**
	 * Find the time exactly between these two times.
	 * @param start
	 * @param end
	 * @return
	 */
	public static Time findMidPoint(Time start, Time end){
		int avgSec = (int)((start.toSec() + end.toSec()))/2;
		return(timeFromSeconds(avgSec));
	}
	

	/**
	 * Given two times, create a new time like this:
	 * If both times say Unused for a field, the new time has Unused for that field.
	 * If both times say used for a field, use the first time's value.
	 * If one time uses a field and the other doesn't, keep the used one.
	 * @param date
	 * @param time
	 * @return
	 */
	public static Time combine(Time date, Time time){
		return new Time(
				combine(date.year, time.year),
				combine(date.month, time.month),
				combine(date.day, time.day),
				combine(date.hours, time.hours),
				combine(date.minutes, time.minutes),
				combine(date.seconds, time.seconds)
				);
	}

	private static int combine(int v1, int v2){
		if(v1 == UNUSED){
			return v2;
		}
		return v1;
	}


	/**
	 * Return a new time that is the specified number
	 * of minutes after this time.
	 * 
	 * @param minutes
	 * @return
	 */
	public Time addMinutes(int minutes){
		Time next = new Time(this);
		int totalMinutes = this.minutes + minutes;
		next.minutes = totalMinutes % 60;
		int totalHours = this.hours + totalMinutes / 60;
		next.hours = totalHours % 24;

		if(! (this.day  == UNUSED)){
			return next.addDays(totalHours / 24);
		}
		else{
			return next;
		}
	}
	public Time addDays(int days){
		Time t = new Time(this);
		for (int i = 0; i < days ; i ++){
			t.nextDay();
		}
		return t;
	}
	private void nextDay(){
		if(this.month == UNUSED){
			this.day = (this.day + 1)%7;
			return;
		}
		if(this.day == UNUSED){
			this.day = 1;
			return;
		}
		if(this.day < daysInMonth(this.month)){
			this.day += 1;
			return;
		}
		else{
			//the exceptional case of February
			if(this.isLeapYear() && this.month == 2 && this.day == 28){
				this.day = 29;
				return;
			}
			//we go to the next month
			this.month = this.month + 1;
			this.day = 1;
			if(this.month > 12){
				this.month = 1;
				this.year = this.year + 1;
			}
		}
	}
	public Time addYears(int years){
		return new Time(this.year + years, this.month, 
				this.day, this.hours, this.minutes, this.seconds);
	}
	
	
	
	/////////////////////
	/////////////////////
	///// GettersAndSetters
	/////////////////////
	/////////////////////
	@SuppressWarnings("unused")
	private boolean ___GettersAndSetters_________;
	
	public void setYear(int year){
		this.year = year;
	}
	public void setMonth(int month){
		if(month > 12 || month < 1){
			throw new RuntimeException(month + "is an invalid month");
		}
		this.month = month;
	}

	/**
	 * Return the day of the week (0 = sunday) 
	 * for this time. If the date is unused, 
	 * the day is the day of week. Otherwise, it must be calculated.
	 * @return
	 */
	public int getDayOfWeek(){
		if(this.month == UNUSED || this.year == UNUSED || this.day == UNUSED){
			this.day=0;
		}
		if(this.month==UNUSED && this.year==UNUSED){
			return this.day;
		}
		//jan 1 2000 was a saturday = 6
		int result =  (int)(((this.toSec() / (60 * 60 * 24)) + 6 )%7);
		if(result < 0){
			result = result + 7;
		}
		return result;
	}
	
	/**
	 * Given an integer day of week, return the normal name of that day.
	 * For example, given 0 return "Sunday."
	 * @param dayOfWeek
	 * @return
	 */
	public String dayOfWeekToText(int dayOfWeek){
		return daysConversion[dayOfWeek];
	}
	/**
	 * given a day of the week, say Friday, advance this time to the next
	 * Friday.
	 * @param dayOfWeek
	 */
	public void advanceToNext(int dayOfWeek){
		if(dayOfWeek / 7 != 0){
			throw new RuntimeException("day " + dayOfWeek + " isn't a day of the week");
		}
		while(this.getDayOfWeek() != dayOfWeek){
			this.nextDay();
		}
	}



	/**
	 * Find the number of days that have passed 
	 * since the start of this year,
	 * taking into account leap years.
	 * for example, if t1 = 5/17/2012, then it should return 
	 * 138 = 31 + 29 + 31 + 30 + 16 
	 * where we only add 16 because today hasn't passed yet.
	 * @return
	 */
	public int daysThisYear(){
		if(this.month == UNUSED || this.day == UNUSED){
			throw new RuntimeException( "Tried to find the day of week for an unspecified month/day: " + this.toString());
		}
		int result = 0;
		int m = 1;
		while (m < this.month){
			result += daysInMonth(m);
			m += 1;
		}
		if(m >= 3 && this.isLeapYear() ){
			//we added February's days already, m = 2
			result += 1;
		}
		result += this.day - 1;
		return result;
	}
	public boolean isLeapYear(){
		if(this.year == UNUSED){
			return false;
		}
		return (this.year % 4 == 0) && (this.year % 100 != 0); 
	}

	public boolean isAM(){
		return (fromMilitary(this.hours)[1] == AM);
	}
	
	
	/////////////////////
	/////////////////////
	///// String methods
	/////////////////////
	/////////////////////
	@SuppressWarnings("unused")
	private boolean ___StringMethods_________;
	
	public String clockTime(){
		int[] americanHours = fromMilitary(hours);
		String result = americanHours[0] + ":";
		if(this.minutes == UNUSED){
			result += "00";
		}
		else{
			result += String.format("%02d" , this.minutes);
		}
		if(this.seconds != UNUSED){
			result += ":" + String.format("%02d", this.seconds);
		}
		if(americanHours[1] == AM){
			result += "AM";
		}
		else{
			result += "PM";
		}
		return result;
	}
	
	public static int toMilitary(int hours, boolean AM){
		if(hours == 12 ){
			if(AM) return 0;
			return 12;
		}
		if(AM){
			return hours;
		}
		return hours + 12;
	}
	public static int[] fromMilitary(int hours){
		if(hours == 0){
			return new int[]{12, AM};
		}
		else if(0 < hours && hours < 12){
			return new int[]{hours, AM};
		}
		else if(hours == 12){
			return new int[]{12, PM};
		}
		else if(12 < hours && hours < 24){
			return new int[]{hours - 12, PM};
		}
		else{
			return new int[]{UNUSED,AM};
		}
	}

	public String toString(){
		return simpleString();
	}
	/**
	 * Tries to find the time in this string.
	 * Note - will use american dates before other dates. So
	 * 05/03/2017 is may 3rd, not march 5th.
	 * 
	 * Does not support seconds. Requires the string A or P at the end of
	 * a time.
	 * @param s
	 * @return
	 */
	public static Time tryRead(String s){
		try{
			return Time.readFrom(s);
		}catch(Exception e){

		}
		boolean dateSuccessful = false;
		int newYear = UNUSED;
		int newMonth = UNUSED;
		int newDay = UNUSED;

		//Find any strings of the form 55/23/1332, 4/2/12, or other similar constructs.
		String possibleDividers = "[ /\\-]";
		Pattern pattern = Pattern.compile("\\d{1,2}?" + possibleDividers + "\\d{1,2}?" + possibleDividers + "\\d{2,4}");
		Matcher matcher = pattern.matcher(s);
		if (matcher.find()) {
			do {
				String possible = matcher.group();
				String[] split = possible.split("\\D");
				int day = Integer.parseInt(split[1]);
				int month = Integer.parseInt(split[0]);
				int year = Integer.parseInt(split[2]);
				if(year < 500){
					if(year < 50){
						year = year + 2000;
					}
					else{
						year = year + 1900;
					}
				}
				if(month < 1 || month > 12){
					month = UNUSED;
					dateSuccessful = false;
					continue;
				}
				if(day < 0 || day > daysInMonth(month)){
					if(day != UNUSED){
						continue;
					}
				}
				newDay = day;
				newMonth = month;
				newYear = year;
				dateSuccessful = true;
			} while (matcher.find(matcher.start(0) + 1) && !dateSuccessful);
		}

		//Find any strings of the form dd:ddAM or dd:ddPM, or 

		boolean timeSuccessful = false;
		int newHour = UNUSED;
		int newMinute = UNUSED;
		pattern = Pattern.compile("\\d{1,2}:\\d{1,2}[AP]");
		matcher = pattern.matcher(s.toUpperCase());
		if (matcher.find()) {
			do {
				String possible = matcher.group();
				String[] halfs = possible.split(":");
				int possibleHour = Integer.parseInt(halfs[0]);
				int j = halfs[1].indexOf("A");
				if(j != -1){
					// we found AM
					newHour = toMilitary(possibleHour, true);
					newMinute = Integer.parseInt(halfs[1].substring(0, j));
				}
				else {

					int k = halfs[1].indexOf("P");
					if(k != -1){
						// we found PM
						newHour = toMilitary(possibleHour, false);
						newMinute = Integer.parseInt(halfs[1].substring(0, k));
					}
					else{
						timeSuccessful = false;
						continue;
					}
				}
				if(newHour > 23 || newHour < 0){
					newHour = UNUSED;
					timeSuccessful = false;
					continue;
				}
				if(newMinute > 60 || newMinute < 0){
					newMinute = UNUSED;
					timeSuccessful = false;
					continue;
				}
				timeSuccessful = true;
			} while (matcher.find(matcher.start(0) + 1) && !timeSuccessful);
		}

		if(!dateSuccessful && !timeSuccessful){
			//System.out.println(s);
		}


		return new Time(newYear, newMonth, newDay, newHour, newMinute, UNUSED);
	}

	public String simpleString(){
		return this.month + "/" + this.day + "/" + this.year + " " 
				+ this.hours + ":" + this.minutes + ":" + this.seconds;
	}
	
	/**
	 * Read from a string of the form
	 * "year/month/day hour:min:sec"
	 * @param s
	 * @return
	 */
	public static Time readFrom(String s){
		String[] split = s.split(" ");
		String[] larges = split[0].split("/");
		String[] smalls = split[1].split(":");
		int day = Integer.parseInt(larges[1]);
		int month = Integer.parseInt(larges[0]);
		int year = Integer.parseInt(larges[2]);
		int hours = Integer.parseInt(smalls[0]);
		int minutes = Integer.parseInt(smalls[1]);
		int seconds = Integer.parseInt(smalls[2]);
		return new Time(year, month, day, hours, minutes, seconds);
	}
	
	
	/////////////////////
	/////////////////////
	///// Testing
	/////////////////////
	/////////////////////
	@SuppressWarnings("unused")
	private boolean ___Testing_________;


	



	public static void testReading(){
		String[] tests = new String[]{
				"10:0AM", 
				"10:00AM",
		};
		for(String s: tests){
			Time t = Time.tryRead(s);
			System.out.println("TEN TEST");
			System.out.println(t.clockTime());

		}

	}
	public static void main(String[] args){
		Time t = Time.tryRead("11:00AM");
		Time s = Time.tryRead("04/28/18");
		Time p = Time.tryRead("10:00AM");

		testReading();
		//	System.out.println(p.clockTime());
		//	System.out.println(t);
		//	System.out.println(s);


		//		double year = 31536000;
		//		double day = 60 * 60 * 24;
		//		long v1 = t.toSec();
		//		long v2 = s.toSec();
		//		long v3 = v.toSec();
		//		//double day = 60 * 60 * 24;
		//		System.out.println(v1+ "\n" + v2  + "\n" + v3 + "\n");
		//		System.out.println(t.toSec() / day + "\n" + 
		//				   s . toSec() / day  + "\n" + v.toSec() / day + "\n");
		//		System.out.println((v3 - v2) / day);
		//		
		//		
		//		


		//		t = new Time(1999, 1, 1, 0,0,0);
		//		for (int i = 0; i < 366 * 2 ; i ++){
		//			long t1 = t.toSec();
		//			t.nextDay();
		//			long t2 = t.toSec();
		//			if(t2 - t1 != 60 * 60 * 24){
		//				System.out.println(t);
		//			}
		//		}



	}


}