
import javax.swing.JLabel;
import javax.swing.JPanel;

public class inSemesterRequirementPanel extends JPanel {
	private Requirement r;
	
	public inSemesterRequirementPanel(Requirement r){
		super();
		JLabel requirementLabel = new JLabel(r.getDisplayString());
		this.add(requirementLabel);
		
	}

	

}
