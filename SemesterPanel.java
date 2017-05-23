

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
	JPanel defaultPanel = new JPanel();
	
	
	
	
	
	public SemesterPanel(String classTitle, String semesterTitle, Color c){
		//change Parameter to semesterDate and do schedule.getclass title semesterDate.getSeason()
	
		super();

		this.setVisible(true);
		defaultPanel.setLayout(new GridLayout(columnNumber, 1, 5, 5));
		this.setBackground(c);
		defaultPanel.setBackground(this.getBackground());
		
		JLabel ClassTitle = new JLabel(classTitle);
		defaultPanel.add(ClassTitle);
		
		JLabel FallSpring = new JLabel(semesterTitle, JLabel.CENTER);
		defaultPanel.add(FallSpring);
		
		
	
		
		
	
		Semester studentSemester = new Semester(new SemesterDate(2018, SemesterDate.FALL));
		
		
		ArrayList<ScheduleElement> listOfClasses = studentSemester.getElements();
		for (int i=0; i< listOfClasses.size(); i++){
			ScheduleElement currentElement =(ScheduleElement) listOfClasses.get(i);
			
			inSemesterRequirementPanel requirementPanel = new inSemesterRequirementPanel(currentElement);
			defaultPanel.add(requirementPanel);
			classCounter++;
			if(classCounter == 4){
				JLabel extraCourseSpace = new JLabel("DROP NEW COURSE HERE");
				defaultPanel.add(extraCourseSpace);
			}
			
		
		// if counter is at 4 then add a new spot 
	else{
		JLabel CourseSpace = new JLabel("DROP NEW COURSE HERE");
		defaultPanel.add(CourseSpace);
	}
		}
		
		
		
	
		JButton deleteSemester= new JButton("-");
		defaultPanel.add(deleteSemester);
		deleteSemester.addActionListener(this);
		this.setLayout(new GridLayout(1, 1, 0, 0));
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
		
	
	
		hide.add(showSemester);
		showSemester.repaint(1);
		hide.repaint(1);

		this.add(hide);
		this.repaint(1);
		
}
	
	/**
	 *  private class SemesterPanelDropHandler extends PanelDropHandler{

				@Override
				public void recievedDrop(Container receiver, Component draggedItem) {
					receiver = (SemesterPanel) receiver;
					draggedItem = (RequirementPanel) draggedItem;
					reciever.addReq(draggedItem.getRequirement());
					reciever.semester.addScheduleElement(draggedItem.getRequirement());
					
					Update GUI 
					
					
					
					
				
						  		}
					
					
				}
	 
				}
	 * 
	 *
	 */

	 
}
	



	
	


	



	
 