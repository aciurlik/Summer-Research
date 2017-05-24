

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Test {

	public static void main(String[] args){
	   JFrame frame = new JFrame();
	   frame.setVisible(true);
	   frame.setSize(2000, 2000);
	   
	   
	   
	   JPanel p = new JPanel();
	   p.setVisible(true);
	   
	   Schedule test = Schedule.testSchedule();
	   
	   
	   SchedulePanel s = new SchedulePanel(test);
	   
	  
	   s.setVisible(true);
	   
	  
	   p.add(s);
	  
	   
	   
	  
	  Requirement req = Requirement.testRequirement();
	  RequirementPanel tester= new RequirementPanel(req);
	  tester.setVisible(true);
	  p.add(tester);
	  
	  RequirementListPanel reqs = new RequirementListPanel(test);
	   reqs.add(tester);
	   reqs.add(tester);
	   reqs.setVisible(true);
	   p.add(reqs);
	  
	   frame.add(p);
	   frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	   
	  
	  
	   

}
}