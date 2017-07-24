import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;


/**
 * This class represents a draggable requirement, 
 * for example the 
 * @author dannyrivers
 *
 */
public class RequirementPanel extends JPanel implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Requirement req;
	public ScheduleGUI d;
	int percentComplete;
	JLabel shown;
	MajorPanel m;
	
	public static int shortStringLength = 25;

	public static final Color GreyedOut = FurmanOfficial.grey(200);
	public static final Color background = FurmanOfficial.bouzarthDarkPurple;

	public RequirementPanel(Requirement req, ScheduleGUI d, MajorPanel m){
		super();
		this.req = req;
		this.d = d;
		this.m=m;

		//Setup the functionality for what to do when a drag starts
		this.setTransferHandler(new RequirementPanelDragHandler());

		//Make sure any click on the requirementPanel 
		// will begin a dragEvent.
		MouseListener listener =  new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e)
			{
				if(SwingUtilities.isRightMouseButton(e)){
					showPopup(e);
					return;
				}
				JComponent c = (JComponent) e.getSource();
				TransferHandler handler = c.getTransferHandler();
				handler.exportAsDrag(c, e, TransferHandler.MOVE);
			}
			@Override
			public void mouseReleased(MouseEvent e){
				if(e.isPopupTrigger()){
					showPopup(e);
				}
			}
		};
		this.addMouseListener(listener);

		this.setBackground(FurmanOfficial.official);

		update(req, m);

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



	public void update(Requirement req, MajorPanel m){
		this.req =req;
		this.m=m;

		//The full text (displayed on hover over) will be
		// chopped down to size in order to fit in the requirement panel
		String fullText = "";


		this.percentComplete =(int) Math.round(req.getStoredPercentComplete() * 100);
		
		/*
		 * This section would add (25%) to the requirement's text
		if(percentComplete > 0 && percentComplete < 100){
			fullText = "(" + percentComplete + "%)" ;
			fullText += req.storedCoursesLeft + "left\n";
		}
		*/
		
		
		fullText += req.shortString(shortStringLength);
		String labelText = fullText;
		if(labelText.length() > shortStringLength){
			labelText = labelText.substring(0,shortStringLength-3) + "...";
		}
		shown = new JLabel(labelText);
		shown.setForeground(Color.white);
		shown.setFont(FurmanOfficial.normalFont);
		//if(!m.major.name.equals("GER")){
		String toolTipText = req.getDisplayString(); 
		if(toolTipText.length() > 60){
			toolTipText = req.shortString(60);
		}
		shown.setToolTipText(toolTipText);
		//}
		shown.addMouseListener(ComponentDragHandler.passingAdapter());


		this.removeAll();
		this.add(shown);

	}


	public Requirement getRequirement(){
		return req;
	}


	
	
	////////////////////////////
	////////////////////////////
	////////////////////////////
	////////////////////////////
	////////////////////////////

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
	
	
	////////////////////////////
	////////////////////////////
	////// Popups when user right clicks
	////////////////////////////
	////////////////////////////

	
	public void examineRequirementPopup(){
		d.GUIExamineRequirement(this.req);
	}
	
	public void showPopup(MouseEvent e){
		JPopupMenu reqMenu = new RequirementPopupMenu();
		reqMenu.show(e.getComponent(), e.getX(), e.getY());
	}
	
	private class RequirementPopupMenu extends JPopupMenu{
		private static final long serialVersionUID = 1L;
		public RequirementPopupMenu(){
			
			JMenuItem item = new JMenuItem(MenuOptions.examineRequirementRightClick);
			item.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					examineRequirementPopup();
				}
			});
			add(item);
			
			
		}
		
	}




}
