

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

		//Make data
		Schedule test = Schedule.testSchedule();
		

		
		//Start the GUI
		
		JPanel p = new JPanel();

		SchedulePanel s = new SchedulePanel(test);
		p.add(s);

		RequirementListPanel reqs = new RequirementListPanel(test);
		p.add(reqs);
		
		

		JFrame frame = new JFrame();
		frame.add(p);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);



	}
}