import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

public class TimeTest {

	@Test
	public void test() {
		String[] tests = new String[]{
				"10:0AM", 
				"10:00AM",
		};

		Time p = Time.tryRead(tests[0]);
		Time q = Time.tryRead(tests[1]);
		//10:0 same as 10:00

		////Clock Time Equals check
		assertTrue(p.equals(q));



		////////////////////////////////
		///////////COMBINE ERROR CHECK
		///////////////////////////////

		//Combining two times with the same clock time gives you the the same clock time
		assertTrue(p.combine(q, p).equals(p));
		assertTrue(p.combine(q, p).equals(q));
		assertTrue(p.combine(q, p).clockTime().equals(p.clockTime()));
		assertTrue(p.combine(q, p).clockTime().equals(q.clockTime()));

		Time start = new Time(2018, 1, 2, 3, 4, 5);
		Time finish = new Time(2016, 5, 2 ,3, 4, 7);
		//This asserts that the if all the fields are filled for both times then the combined time 
		//is the first time
		assertFalse(p.equals(start.combine(start, p)));
		assertTrue(start.equals(start.combine(start, finish)));
		assertTrue(finish.equals(start.combine(finish, start)));

		//Checks that the unused fields are replaced  by the used fields. 
		Time unfinishedone = new Time(2016, 1, 2, Time.UNUSED, Time.UNUSED, Time.UNUSED);
		Time unfinishedtwo = new Time(Time.UNUSED, Time.UNUSED, Time.UNUSED, 3, 4,5);
		assertTrue(unfinishedone.combine(unfinishedone, unfinishedtwo).equals(new Time(2016, 1,2,3,4,5)));
		assertTrue(unfinishedone.combine(unfinishedone, unfinishedtwo).clockTime().equals(new Time(2016, 1,2,3,4,5).clockTime()));

		/////////////////////////
		////////////isALLUNUSED CHECK
		/////////////////////////
		Time unsed = new Time(Time.UNUSED,Time.UNUSED,Time.UNUSED,Time.UNUSED,Time.UNUSED,Time.UNUSED);
		assertTrue(unsed.isAllUnused());
		assertFalse(start.isAllUnused());


	

		////////////////////////
		///////////FindMidPoint Time
		/////////////////////////
		Time s = new Time(2018, 5, 2, 3, 4, 5);
		Time f = new Time(2016, 5, 2 ,3, 4, 7);
		assertTrue(s.findMidPoint(f).equals(new Time(2017, 5, 2 ,3, 4, 6)));
		System.out.println(start.findMidPoint(finish));

		////////////////////////
		/////////SimpleString
		////////////////////////
		assertTrue(start.simpleString().equals( "1/2/2018 3:4:5"));


		///////////////////////
		/////////TryRead
		////////////////////////
		Time testRead = Time.tryRead("5/3/2016 10:0A");
		Time testReader =new Time(2016, 5,3,10, 0, Time.UNUSED);
		assertTrue(testRead.clockTime().equals(testReader.clockTime()));
		assertTrue(testRead.equals(testReader));



		////////////////////////
		/////////MeetingDaysFrom
		////////////////////////

		int[] test = {Time.MONDAY, Time.WEDNESDAY, Time.THURSDAY, Time.FRIDAY, Time.TUESDAY};
		Time.meetingDaysFrom("MWRFT").equals(test);
		
		
		
		/////////////////////////
		//////////CompareTo and Minutesto and isAM
		////////////////////////
		Time a = new Time(2017, 6,5,13,15,61);
		Time b = new Time(2017, 6,5,4,3,1);
		assertTrue(a.compareTo(b)==1);
		assertTrue(b.compareTo(a)==-1);
		assertTrue(b.compareTo(b)==0);
		
		
		assertTrue(b.minutesUntil(a)==13+(60*(13-4)));
		assertTrue(a.isAM()==false);
		assertTrue(b.isAM());
		
		
		///////////////////////
		/////////TimetoSeconds=TimeFromSeconds
		///////////////////////
		long sec = start.toSec();
		//System.out.println(start.clockTime());
		assertTrue(Time.timeFromSeconds(sec).clockTime().equals(start.clockTime()));
		
	


	}
}

