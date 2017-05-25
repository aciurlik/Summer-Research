


import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;



public class ScheduleElementPanel extends JPanel {
	private int updateCount = 0;
	private Requirement r;
	private ScheduleElement s;


	//	Driver coursesSatisfy = new Driver();
	JComboBox  requirmentDropDown = new JComboBox();

	public ScheduleElementPanel(ScheduleElement s) {

		super();
		this.s=s;
		if(s instanceof Requirement){
			r=(Requirement)s;
		}

		else{

			r=null;
		}

	}

	public ScheduleElement getElement(){
		return s;
	}




	public void updatePanel(){ //This can be taken out later
		JLabel requirementLabel = new JLabel(s.getDisplayString());
		this.add(requirementLabel);
		if(s instanceof Requirement) {
			updateDropDown();
		}
	}
	//If course is dropped then no dropDown Panel is needed




	public void updateDropDown(){
		this.requirmentDropDown.removeAllItems();
		SemesterPanel container = (SemesterPanel)this.getParent().getParent();
		ArrayList<Course> listOfCourses = container.getSemester().getCoursesSatisfying(this.r);
		for( int i = 0; i< listOfCourses.size(); i++){
			requirmentDropDown.addItem(listOfCourses.get(i));
		}
		this.add(requirmentDropDown);

	}



}










