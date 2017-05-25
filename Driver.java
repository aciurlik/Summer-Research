import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Driver {

	public static void main(String[] args){
		JFrame frame = new JFrame();
		
		
		
		 	//Put the SchedulePanel and RequirementsPanel inside p.
		JPanel p = new JPanel();

		Schedule test = Schedule.testSchedule();
		SchedulePanel s = new SchedulePanel(test);
		p.add(s);
		frame.pack();

		Requirement req = Requirement.testRequirement();
		RequirementPanel tester= new RequirementPanel(req);
		p.add(tester);
		frame.pack();
		
		RequirementListPanel reqs = new RequirementListPanel(test);
		reqs.add(tester);
		reqs.add(tester);
		p.add(reqs);
		
		////
		MenuLookDemo menu = new MenuLookDemo();
		frame.setJMenuBar(menu.createMenuBar());
	    frame.setContentPane(menu.createContentPane());
		
	
		
		frame.pack();

		frame.add(p);
		
		
	
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);


	}
}
	
		
	