


import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ScheduleElementPanel extends JPanel {
	private Requirement r;
	private ScheduleElement s;
	
	public ScheduleElementPanel(ScheduleElement s) { 
		super();
		this.s=s;
		if(s instanceof Requirement){
		r=(Requirement)s;
		}
		
		else{
		r=null;
	
				}
		this.updatePanel();
	
	}

	
	
		public void updatePanel(){ //This can be taken out later
			JLabel requirementLabel = new JLabel(s.getDisplayString());
			this.add(requirementLabel);
			if(s instanceof Requirement){
				dropDownRequirment();
			}
			//If course is dropped then no dropDown Panel is needed
		
		}
		
		public void dropDownRequirment(){
			JComboBox  requirmentDropDown = new JComboBox();
			Driver coursesSatisfy = new Driver();
			ArrayList listOfCourses = coursesSatisfy.masterList.getCoursesSatisfying(this.r);
			for( int i = 0; i< listOfCourses.size(); i++){
				requirmentDropDown.addItem(listOfCourses.get(i));
				
			}
			this.add(requirmentDropDown);
			
		}
}
		
 		
	


		




