


import java.util.ArrayList;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ScheduleElementPanel extends JPanel {
	private Requirement r;
	private ScheduleElement s;

	public ScheduleElementPanel(ScheduleElement s){
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


	public void updatePanel(){
		JLabel requirementLabel = new JLabel(s.getDisplayString());
		this.add(requirementLabel);


	}

	public void dropDownGetRequirmentsFulfilled(){
		ArrayList<Requirement> requirmentsFulfilled = r.getRequirementsFulfilled();
		for(int i=0; i<requirmentsFulfilled.size(); i++){
			Requirement singleReq = requirmentsFulfilled.get(i);

		}
		JComboBox petList = new JComboBox();
		this.add(petList);
	}
}



