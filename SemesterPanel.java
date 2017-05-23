

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SemesterPanel extends JPanel implements ActionListener{


	/**
	 * 
	 */
	private int buttonCounter = 0;
	private int classCounter = 0;
	private int requirementNumber=0;
	private int columnNumber = 9; //This classTitle, semesterTitle, 6 classes, button
	private int normalNumberofClasses = 4;
	private String deleteButton = "-";
	JPanel defaultPanel = new JPanel();
	




	public SemesterPanel(String classTitle, String semesterTitle, Color c){

		//Sets up the panel that will hold one semester
		super();
		this.setVisible(true);
		defaultPanel.setLayout(new GridLayout(columnNumber, 1, 5, 5));
		this.setBackground(c); //This allows the schedule Panel to control the color
		defaultPanel.setBackground(this.getBackground());

		//Header
		JLabel ClassTitle = new JLabel(classTitle);
		defaultPanel.add(ClassTitle);

		JLabel FallSpring = new JLabel(semesterTitle, JLabel.CENTER);
		defaultPanel.add(FallSpring);


	
	
		
		for (int i=0; i<=normalNumberofClasses; i++){
			
		
				JLabel CourseSpace = new JLabel("DROP NEW COURSE HERE");
				defaultPanel.add(CourseSpace);
			
		}




		JButton deleteSemester= new JButton(deleteButton);
		defaultPanel.add(deleteSemester);
		deleteSemester.addActionListener(this);
		this.setLayout(new GridLayout(1, 1, 0, 0)); //Fits Default/Hide Panel to cover SemesterPanel. 
		this.setVisible(true);
		this.add(defaultPanel);

	}



	public int getClassCounter() {
		return classCounter;
	}




	public int getRequirementNumber() {
		return requirementNumber;
	}







	@Override
	public void actionPerformed(ActionEvent e) {
		buttonCounter++;
		System.out.println(buttonCounter);

		String show = "Show Semester";
		this.remove(defaultPanel);
		JPanel hide = new JPanel();
		hide.setBackground(Color.pink);
		JButton showSemester = new JButton(show);
		showSemester.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				remove(hide);
				add(defaultPanel);
			}
		}
				);


		//TO DO: Figure out repaint glitch
		hide.add(showSemester);
		showSemester.repaint(1);
		hide.repaint(1);

		this.add(hide);
		this.repaint(1);

	}
	
	public void addElement(ScheduleElement e){
		
	}

	
	private class SemesterPanelDropHandler extends PanelDropHandler{

		@Override
		public void recievedDrop(Container receiver, Component draggedItem) {
			SemesterPanel r = (SemesterPanel) receiver;
			RequirementPanel d =  (RequirementPanel) draggedItem;
			r.addElement(d.getRequirement());
			if (r.semester.addScheduleElement(d.getRequirement())){
				inSemesterRequirementPanel requirementPanel = new inSemesterRequirementPanel(d.getRequirement());
				receiver.add(requirementPanel);

			}


		}

	}
}













