

import java.awt.Color;
import java.io.FileNotFoundException;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Driver {



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
