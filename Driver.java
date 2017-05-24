

import java.awt.Color;
import java.io.FileNotFoundException;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Driver {
	public CourseList masterList;
	
	public Driver(){
		CourseList masterList = new CourseList();
		this.masterList = masterList;
		int[] meetingDays = {Time.MONDAY, Time.WEDNESDAY, Time.FRIDAY};
		Course a = new Course(new Prefix("MTH", 220), new SemesterDate(2017, SemesterDate.FALL), "Fray", meetingDays, 4, 02);
		masterList.add(a);
	}

	
	public static void main(String[] args){
	
		
		
	   JFrame frame = new JFrame();
	  
	   //frame.setSize(2000, 2000);
	   
	   
	   
	   JPanel p = new JPanel();
	//   p.setVisible(true);
	   
	   Schedule test = Schedule.testSchedule();
	   
	   
	   SchedulePanel s = new SchedulePanel(test);
	   
	  
	  // s.setVisible(true);
	   
	  
	   p.add(s);
	  
	  Requirement req = Requirement.testRequirement();
	  RequirementPanel tester= new RequirementPanel(req);
	 // tester.setVisible(true);
	  p.add(tester);
	  
	  RequirementListPanel reqs = new RequirementListPanel(test);
	   reqs.add(tester);
	   reqs.add(tester);
	  // reqs.setVisible(true);
	   p.add(reqs);
	  
	   frame.add(p);
	   frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	   frame.pack();
	   frame.setVisible(true);
	    
	   
	  
	  
	   

}
}