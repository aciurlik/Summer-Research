

import javax.swing.JLabel;
import javax.swing.JPanel;

public class inSemesterRequirementPanel extends JPanel {
	


	public inSemesterRequirementPanel(ScheduleElement element) {
			super();
			String labelDisplay = element.getDisplay();
			JLabel requirement = new JLabel(labelDisplay);
			this.add(requirement);
			
			
		}
	
	
	
	}

	


