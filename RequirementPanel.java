import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;


/**
 * Blurb written 7/26/2017
 * Last updated 7/26/2017
 * 
 * This class represents a draggable requirement
 * that can be found in a major panel and dragged into semesters.
 * 
 * 
 * RequirementPanel is in the GUI group of classes.
 *
 */
public class RequirementPanel extends JPanel implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	public Requirement req;
	public ScheduleGUI schGUI;
	static HashMap<String, String> toToolTip;
	int percentComplete; //this has to be stored locally so that we can
	// paint the right percentage of this panel grey.
	
	JLabel textLabel;//this is where the requirement name is displayed to the user.
	
	//MajorPanel m;
	
	
	public static int shortStringLength = 25;
	public static final Color GreyedOut = FurmanOfficial.grey(200);

	/**
	 * This sets up the special tool tips for the GERs. 
	 */
	static{
		toToolTip = new HashMap<String, String>(); 
		String GERshortStrings[] = {   "FYW",                           "WR",                        "NW/NWL",                                  "NW",        "NWL",                      "HB",             "HA",                  "TA",               "VP",                         "MR",                                "FL",               "UQ",                 "MB",            "NE",                                 "WC"};
		String GERfullName [] = {     "First Year Writing Seminar", "Writing-Research Intensive", "Natural World/Natural World (with Lab)", "Natural World", "Natural World (with lab)", "Human Behavior", "Historical Analysis", "Textual Analysis", "Visual and Performing Arts", "Mathematical and Formal Reasoning", "Foreign Language", "Ultimate Questions", "Mind and Body", "Humans and the Natural Environment", "World Cultures"};
		
		for(int i = 0; i<GERshortStrings.length; i++){
			toToolTip.put(GERshortStrings[i], GERfullName[i]);
		}
		
	}
	
	public RequirementPanel(Requirement req, ScheduleGUI schGUI){
		super();
		this.req = req;
		this.schGUI = schGUI;

		//Setup the functionality for what to do when a drag starts
		this.setTransferHandler(new RequirementPanelDragHandler());

		//Make sure any click on the requirementPanel 
		// will begin a dragEvent. Can't use the default
		// drag event from ComponentDragHandler because
		// we want special behavior on right clicks
		// (we want the option to examine the requirement)
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
		};
		this.addMouseListener(listener);
		this.setBackground(FurmanOfficial.darkPurple);
		update(req);
	}


	@Override 
	/**
	 * We override in order to paint the grey rectangle
	 * over part of this panel.
	 */
	public void paintComponent(Graphics g){
		super.paintComponent(g);


		//paint a grey block over the proportion of this
		int width = this.getWidth();
		int height = this.getHeight();
		//We want the rectangle like this
		// 
		//  (0,0)------|  -   -   -   -   |
		//  | -------- |                  |
		//  |__________|  _   _   _   (width, height)
		//
		//           (%Complete)

		Color c = g.getColor();
		g.setColor(GreyedOut);
		g.fillRect(0, 0, (width * percentComplete)/100, height);
		g.setColor(c);
	}



	/**
	 * Update the percent this component is shaded and the 
	 * textLabel
	 * 
	 * @param req
	 * @param m
	 */
	public void update(Requirement req){
		this.req =req;
		this.percentComplete =(int) Math.round(req.getStoredPercentComplete() * 100);
		
		//make labelText (seen on the draggable requirement panel)
		// as the first 20 characters of the shortString,
		// and capitalize the first letter.
		String labelText = req.shortString(shortStringLength);
		
		if(!labelText.trim().equals("")){//This keeps it from breaking if someone adds an accidental space.
			labelText = labelText.substring(0,1).toUpperCase() + labelText.substring(1);
		}
		if(labelText.length() > shortStringLength){
			labelText = labelText.substring(0,shortStringLength-3) + "...";
		}
		
		textLabel = new JLabel(labelText);
		textLabel.setForeground(Color.white);
		textLabel.setFont(FurmanOfficial.normalFont);
		
		//set the tooltip (showed when the mouse hovers on a requirement panel)
		// to be the display string (if it fits) or the short string(if it doesn't).
		
		
		
		
		
		
		String toolTipText = req.getDisplayString(); 
		if(toToolTip.containsKey(req.name)){
			toolTipText = toToolTip.get(req.name);
		}
		
	
		if(toolTipText.length() > 60){
			toolTipText = req.shortString(60);
		}
		
		textLabel.setToolTipText(toolTipText);
		textLabel.addMouseListener(ComponentDragHandler.passingAdapter());

		this.removeAll();
		this.add(textLabel);
	}

	
	public Requirement getRequirement(){
		return req;
	}


	
	
	
	
	
	
	
	
	////////////////////////////
	////////////////////////////
	//////Drag behavior
	////////////////////////////
	////////////////////////////
	@SuppressWarnings("unused")
	private boolean ___DragBehavior_________;

	/**
	 * This class specifies the actions that should happen when 
	 * the user begins to drag a requirement panel.
	 * 
	 *
	 */
	public class RequirementPanelDragHandler extends ComponentDragHandler{
		@Override
		public void initiateDrag(JComponent toBeDragged) {
			schGUI.dragStarted(req);
		}
		
		@Override
		public void afterDrop(Container source, JComponent dragged,
				boolean moveAction) {

			//	source.revalidate();
			//	source.repaint();
			schGUI.dragEnded();

		}
	}
	
	
	////////////////////////////
	////////////////////////////
	////// Popups when user right clicks
	////////////////////////////
	////////////////////////////
	@SuppressWarnings("unused")
	private boolean ___RightClickPopup_________;

	
	public void examineRequirementPopup(){
		schGUI.examineRequirement(this.req);
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
