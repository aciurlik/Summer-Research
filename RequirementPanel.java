import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.TransferHandler;
import javax.swing.border.BevelBorder;


/**
 * This class represents a draggable requirement, 
 * for example the 
 * @author dannyrivers
 *
 */
public class RequirementPanel extends JPanel {
	public Requirement req;
	public Driver d;
	int percentComplete;
	JLabel shown;

	public static final Color GreyedOut = FurmanOfficial.grey(200);
	public static final Color background = FurmanOfficial.bouzarthDarkPurple;

	public RequirementPanel(Requirement req, Driver d){
		super();
		this.req = req;
		this.d = d;

		//Setup the functionality for what to do when a drag starts
		this.setTransferHandler(new RequirementPanelDragHandler());

		//Make sure any click on the requirementPanel 
		// will begin a dragEvent.
		MouseListener listener = ComponentDragHandler.getDragListener();
		this.addMouseListener(listener);
		if(MenuOptions.UIType){
			this.setBackground(FurmanOfficial.nimbus);
		}
		else{
			this.setBackground(background);
		}

		update(req);

	}


	@Override 
	public void paintComponent(Graphics g){
		super.paintComponent(g);


		//paint a grey block over the proportion of this
		int width = this.getWidth();
		int height = this.getHeight();
		//We want the rectangle like this
		// 
		//  (0,0)------|         (width,0)
		//  |          |
		//  |__________|          (width, height)
		//

		Color c = g.getColor();
		g.setColor(GreyedOut);
		g.fillRect(0, 0, (width * percentComplete)/100, height);
		g.setColor(c);


	}



	public void update(Requirement req){
		this.req =req;


		//The full text (displayed on hover over) will be
		// chopped down to size in order to fit in the requirement panel
		String fullText = "";


		this.percentComplete =(int) Math.round(req.storedPercentComplete * 100);
		if(percentComplete > 0 && percentComplete < 100){
			fullText = "(" + percentComplete + "%)" ;
			fullText += req.storedCoursesLeft + "left\n";
		}
		fullText += req.getDisplayString();



		int numChars = 20;
		String labelText = fullText;
		if(labelText.length() > numChars){
			labelText = labelText.substring(0,numChars-3) + "...";
		}
		shown = new JLabel(labelText);
		shown.setForeground(Color.white);
		shown.setFont(FurmanOfficial.normalFont);
		shown.setToolTipText(req.choices.toString());
		shown.addMouseListener(ComponentDragHandler.passingAdapter());


		this.removeAll();
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
			d.dragStarted(req);
		}

		@Override
		public void afterDrop(Container source, JComponent dragged,
				boolean moveAction) {

			//	source.revalidate();
			//	source.repaint();
			d.dragEnded();

		}



	}




}
