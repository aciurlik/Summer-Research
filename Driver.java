

import java.awt.Color;
import java.io.FileNotFoundException;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Driver {
	
	SchedulePanel sch;
	RequirementListPanel reqs;
	

	
	public Driver(){
		//Make data
		Schedule test = Schedule.testSchedule();
		
		
		//Start the GUI
		
		JPanel p = new JPanel();

		sch = new SchedulePanel(test, this);
		p.add(sch);

		reqs = new RequirementListPanel(test);
		p.add(reqs);
		
		

		JFrame frame = new JFrame();
		frame.add(p);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);

	}


	public static void main(String[] args){
		new Driver();

	}
}
