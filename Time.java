/**
 * A handy time class
 * @author dannyrivers
 *
 */
public class Time implements Comparable<Time>{
	
	
	public static final int SUNDAY = 0;
	public static final int MONDAY = 1;
	public static final int TUESDAY = 2;
	public static final int WEDNESDAY = 3;
	public static final int THURSDAY = 4;
	public static final int FRIDAY = 5;
	public static final int SATURDAY = 6;
	
	public static final String[] daysConversion = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
	
	int day; //(1) thru (numDays)
	int month; // 1 thru 12
	int year; // regular year, as in 2017 right now.
	int hours; // military time, so 0 thru 23
	int minutes; //0 thru 59
	int seconds; //0 thru 59
	
	
	public final static int[] daysInMonth = {31,28,31,30,31,30,31,31,30,31,30,31};
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
		this(year, month, day, hours, minutes, 0);
	}
	
	/**
	 * Assumes military time for hours
	 * @param hours
	 * @param minutes
	 * @param seconds
	 */
	public Time(int hours, int minutes, int seconds){
		this(2000, 1, 1, hours, minutes, seconds);
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
		this(2000, 1, 1, Time.toMilitary(hours, AM), minutes, seconds);
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
	
	
	/**
	 * Return a new time that is the specified number
	 * of minutes after this time.
	 * @param minutes
	 * @return
	 */
	public Time addMinutes(int minutes){
		Time next = new Time(this);
		int totalMinutes = this.minutes + minutes;
		next.minutes = totalMinutes % 60;
		int totalHours = this.hours + totalMinutes / 60;
		next.hours = totalHours % 24;
		return next.addDays(totalHours / 24);
	}
	public Time addDays(int days){
		Time t = new Time(this);
		for (int i = 0; i < days ; i ++){
			t.nextDay();
		}
		return t;
	}
	private void nextDay(){
		if(this.day < daysInMonth[this.month - 1]){
			this.day += 1;
			return;
		}
		else{
			//the exceptional case of february
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
	public void setYear(int year){
		this.year = year;
	}
	public void setMonth(int month){
		if(month > 12 || month < 1){
			throw new RuntimeException(month + "is an invalid month");
		}
	}
	
	public int dayOfWeek(){
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
	public String toText(int dayOfWeek){
		return daysConversion[dayOfWeek];
	}
	public void advanceToNext(int dayOfWeek){
		if(dayOfWeek / 7 != 0){
			throw new RuntimeException("day " + dayOfWeek + " isn't a day of the week");
		}
		while(this.dayOfWeek() != dayOfWeek){
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
		int result = 0;
		int m = 1;
		while (m < this.month){
			result += daysInMonth[m-1];
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
		return (this.year % 4 == 0) && (this.year % 100 != 0); 
	}
	
	/**
	 * This method returns an estimated number of seconds since midnight 2000
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

		int yearsSince = this.year - 2000;
		int leapYears = yearsSince / 4;
		leapYears += - yearsSince / 100; // if divisible by 100, 
					//a year isn't a leap year.
		
		totalSec += yearsSince * 60 * 60 * 24 * 365; //every year has 365 days
		totalSec += leapYears * 60 * 60 * 24 * 1;    //but leap years add an extra day
		
		//If it is currently a leap year then we shouldn't have added the 
		// extra day from this year (it will be handled in future calculations)
		if(this.isLeapYear()){
			totalSec -= 60 * 60 * 24;
		}
		
		totalSec += this.daysThisYear() * 60 * 60 * 24;
		totalSec += (this.hours) * 60 * 60;
		totalSec += this.minutes * 60;
		totalSec += this.seconds;
		
		return totalSec;
				
	}
	
	public String clockTime(){
		return this.hours + ":" + this.minutes;
	}
	
	public boolean isAM(){
		return (this.hours >=12);
	}
	
	/**
	 * Rounds down if seconds don't match up.
	 * @param other
	 * @return
	 */
	public int minutesUntil(Time other){
		return (int)((other.toSec() - this.toSec()) / 60); 
	}
	@Override
	public int compareTo(Time that) {
		
		if(this.toSec() > that.toSec()){
			return 1;
		}
		if(this.toSec() < that.toSec()){
			return -1;
		}
		return 0;
	}
	
	public String toString(){
	return this.day + "/" + this.month + "/" + this.year + " " 
	+ this.hours + ":" + this.minutes + ":" + this.seconds;
	}
	
	public static Time readFrom(String s){
		String[] split = s.split(" ");
		String[] larges = split[0].split("/");
		String[] smalls = split[1].split(":");
		int day = Integer.parseInt(larges[0]);
		int month = Integer.parseInt(larges[1]);
		int year = Integer.parseInt(larges[2]);
		int hours = Integer.parseInt(smalls[0]);
		int minutes = Integer.parseInt(smalls[1]);
		int seconds = Integer.parseInt(smalls[2]);
		return new Time(year, month, day, hours, minutes, seconds);
	}
	
	
	public static void main(String[] args){
		
		
		Time t = new Time(2003,1,31,10,30);
		Time s = new Time(2003,1,31,11,0);
		Time v = new Time(2004,3,1,0,0);
		
		System.out.println(t.minutesUntil(s));
		
		
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
