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
		
		
		JFrame frame = new JFrame();
		frame.setLayout(new BorderLayout());
				
		//Adds the menu bar
		MainMenuBar menu = new MainMenuBar();
		frame.setJMenuBar(menu.createMenuBar());
	    frame.setContentPane(menu.createContentPane());
		
	      //Adds Additions Panel
		AdditionsPanel add = new AdditionsPanel();
		frame.add(add, BorderLayout.LINE_START);
	
		
			
		
		frame.pack();

		sch = new SchedulePanel(test, this);
		frame.add(sch, BorderLayout.LINE_END);
		reqs = new RequirementListPanel(test);
		frame.add(reqs, BorderLayout.PAGE_END);
		
		
	
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);

	}

	public static void main(String[] args){
		new Driver();


	}
}
	
		
	
