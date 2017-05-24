import java.awt.Container;
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
	
	public RequirementPanel(Requirement req){
		super();
		this.req = req;
		
		//Setup the functionality for what to do when a drag starts
		this.setTransferHandler(new RequirementPanelDragHandler());
		
		
		//Make sure any click on the requirementPanel 
		// will begin a dragEvent.
		MouseListener listener = ComponentDragHandler.getDragListener();
		this.addMouseListener(listener);
		
		
		this.add(new JLabel(req.getDisplayString()));
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
			source.revalidate();
			source.repaint();
		}
		
		

	}
}
