import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.TransferHandler;


/**
 * This class represents a draggable requirement, 
 * for example the 
 * @author dannyrivers
 *
 */
public class RequirementPanel extends JPanel {
	public Requirement req;
	public Color FurmanDarkPurple = new Color(43, 12, 86);
	public Color FurmanLightPurple = new Color(79, 33, 112);
	public Color FurmanGray = new Color(96, 96, 91);
	
	public RequirementPanel(Requirement req){
		super();
		this.req = req;
		
		//Setup the functionality for what to do when a drag starts
		this.setTransferHandler(new RequirementPanelDragHandler());
		
		
		//Make sure any click on the requirementPanel 
		// will begin a dragEvent.
		MouseListener listener = ComponentDragHandler.getDragListener();
		this.addMouseListener(listener);
		
		JLabel shown = new JLabel(req.getDisplayString());
		shown.setForeground(Color.white);
	
		
		
		if(this.req.isComplete()){
			this.setBackground(FurmanGray);
		}
		else{
			this.setBackground(FurmanDarkPurple);
		}
		
		this.add(shown);
		
	}
	
	
	public Requirement getRequirement(){
		return req;
	}
	
	
	
	/**
	 * This class specifies the actions that should happen when 
	 * the user begins to drag a requirement panel.
	 * 
	 * @author dannyrivers
	 *
	 */
	public class RequirementPanelDragHandler extends ComponentDragHandler{

		@Override
		public void initiateDrag(JComponent toBeDragged) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterDrop(Container source, JComponent dragged,
				boolean moveAction) {

		//	source.revalidate();
		//	source.repaint();
			
			

		}
		
		

	}
}
