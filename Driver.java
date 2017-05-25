import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Driver {

	SchedulePanel sch;
	RequirementListPanel reqs;




	public Driver(){
		//Make data
		Schedule test = Schedule.testSchedule();




		//Put the SchedulePanel and RequirementsPanel inside p.
		JPanel p = new JPanel();

		sch = new SchedulePanel(test, this);
		p.add(sch);

		reqs = new RequirementListPanel(test);

		p.add(reqs);

		JFrame frame = new JFrame();
		//Adds the menu bar
		MainMenuBar menu = new MainMenuBar();
		frame.setJMenuBar(menu.createMenuBar());
		frame.setContentPane(menu.createContentPane());



		frame.pack();

		frame.add(p);



		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);

	}

	public static void main(String[] args){
		new Driver();


	}
}



