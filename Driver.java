import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Driver {

	Schedule sch;
	SchedulePanel schP;
	RequirementListPanel reqs;




	public Driver(){


		//Make data

		Schedule test = Schedule.testSchedule();		
		sch=test;

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

		schP = new SchedulePanel(test, this);
		frame.add(schP, BorderLayout.LINE_END);
		reqs = new RequirementListPanel(test);
		frame.add(reqs, BorderLayout.PAGE_END);

		this.update();



		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);

	}

	public void GUIRequirementPanelDropped(RequirementPanel r, SemesterPanel semesterP) {
		sch.addElement(r.req, semesterP.sem);
		this.update();

	}


	public void GUIScheduleElementPanelDropped(ScheduleElementPanel p, SemesterPanel semesterPanel) {
		// TODO Auto-generated method stub

	}

	public void GUISemesterPanelAdded(){
		sch.addNewSemester();
		this.update();

	}

	public void updateAll(){
		schP.update(sch);
		//	reqs.update(sch);
	}

	public void repaintAll(){
		schP.revalidate();
		schP.repaint();

		reqs.revalidate();
		reqs.repaint();

	}

	public void update() {
		updateAll();
		repaintAll(); 



	}


	public static void main(String[] args){
		new Driver();


	}






}



