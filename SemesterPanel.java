package scheduler;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class SemesterPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int classCounter = 0;
	private int requirementNumber=0;
	
	public SemesterPanel(String classTitle, String semesterTitle){
		super();
		Schedule studentSchedule = new Schedule();
		this.setVisible(true);
		this.setLayout(new GridLayout(7, 1, 5, 5));
		this.setBackground(Color.blue);
		JLabel ClassTitle = new JLabel(classTitle);
		this.add(ClassTitle);
		JLabel FallSpring = new JLabel(semesterTitle, JLabel.CENTER);
		this.add(FallSpring);
		
		
		for (int i = 0; i<4; i++){
			// if there exists a requirement filled then add a requirement or a class? add one 
			//WHAT about AP credit where does this go? 
			if(studentSchedule.hasNextRequirement(requirementNumber)){
			 //add a requirement to this list
				String prefix = studentSchedule.getRequirement(requirementNumber);
				requirementNumber++;
				inSemesterRequirementPanel requirementPanel = new inSemesterRequirementPanel(prefix);
				this.add(requirementPanel);
				classCounter++;
				if(classCounter == 4){
					JLabel extraCourseSpace = new JLabel("DROP NEW COURSE HERE");
					this.add(extraCourseSpace);
				}
				}
			
			// if counter is at 4 then add a new spot 
			
		else{
			JLabel CourseSpace = new JLabel("DROP NEW COURSE HERE");
			this.add(CourseSpace);
		}


	}

	}

	
	
	public int getClassCounter() {
		return classCounter;
	}


	

	public int getRequirementNumber() {
		return requirementNumber;
	}

	
}
	
 