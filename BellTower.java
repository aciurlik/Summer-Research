import java.awt.Dimension;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * GUI CLASS
 * 
 * Blurb Written: 7/19/2017
 * Last Updated:  7/27/2017
 * 
 * This is the graphic at the bottom, west side of the ScheduleGUI. It consists of 
 * the Image of the BellTower loaded from the FileHandler.
 * It fills with purple (a rectangle is placed on top of the image) as the user
 * fulfills requirements. ( the amount filled represents the percentage done, subject to roundoff error)
 * When all the requirements are considered satisfied (and the user has at least one major
 * in his/her schedule) the bellTower will display a message describing all of the current user
 * errors in the schedule, or in the case of no errors a celebratory fireworks picture.  
 *  
 */
public class BellTower extends JPanel implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Schedule sch;
	JPanel layerPanel ; // the purple panel that fills up over the belltower image
	ImageIcon bellTowerIcon ;
	ImageIcon fireworkIcon; //displayed on successful completion of the schedule
	JLabel belltowerLabel;
	JLabel fireworkLabel;

	boolean wasComplete; //This is used to prevent the congrats/Error popUp from occurring multiple times
	//after the user has originally complete the schedule. This will switch back to false if the user does
	//an action that renders the schedule incomplete again. If they then were to recomplete the schedule 
	//wasComplete would be false thus the popUp would occur. 
	
	public static final int imageWidth = 100;
	public static final int imageHeight = 300;

	public BellTower(Schedule sch) {
		super();
		this.sch=sch;
		
		wasComplete = false;
		layerPanel = new JPanel();

		bellTowerIcon = FileHandler.getBellTower();
		belltowerLabel = new JLabel(bellTowerIcon);
		belltowerLabel.add(layerPanel);

		fireworkIcon = FileHandler.getFireworks();
		fireworkLabel = new JLabel(fireworkIcon);

		this.add(belltowerLabel);
		this.setPreferredSize(new Dimension(imageWidth, imageHeight));
		this.setBackground(FurmanOfficial.bouzarthGrey);
	}
	
	/**
	 * Allows the BellTower to change with the schedule attached to the current scheduleGUI.  
	 * @param s
	 */
	public void setSchedule(Schedule s){
		this.sch=s;
	}

	/**
	 * This method fills up the purple on the schedule in accordance to the amount 
	 * their schedule is complete, and displays a dialog message if the user
	 * completes all of their requirements. 
	 */
	public void update(){

		//Fills in the purple based on the % done the schedule is 
		double percentDone= sch.getPercentDone();
		int newHeight = (int)(percentDone * bellTowerIcon.getIconHeight()) - 1;
		layerPanel.setSize(bellTowerIcon.getIconWidth(), newHeight);
		layerPanel.setLocation(0, bellTowerIcon.getIconHeight()-newHeight);
		layerPanel.setBackground(FurmanOfficial.lightPurple);


		//If you complete all the requirements in your schedule and have at least one major,
		// a popup will give you either a list of errors that are still present in your schedule
		// or a some fireworks of congrats if your schedule is perfect
		boolean nowComplete = sch.isComplete(); //This method checks errors and that you have a major

		if(nowComplete && (!wasComplete)){
			if(sch.checkAllErrors().isEmpty()){
				JOptionPane.showMessageDialog(null, 
						fireworkLabel,
						"Congratulations on having a complete, error free schedule!", 
						JOptionPane.INFORMATION_MESSAGE);
			}
			else{
				JOptionPane.showMessageDialog(null, 
						"You've finished your requirements, but there are still some errors left",
						"Finished Requirements", 
						JOptionPane.INFORMATION_MESSAGE,
						ScheduleGUI.icon);

				sch.schGUI.GUICheckAllErrors();
			}
		}
		wasComplete = nowComplete;


	}

	/**
	 * Test cases:
	 * The BellTower should rise in accordance with the percentage of requirements selected.
	 * Once all the requirements are completed, and at least one major is added a popup should either
	 * display all of the errors still existing in the schedule, and then give major notes. If there
	 * are no errors present then the bellTower should display festive fireworks. Once the schedule is 
	 * deemed complete the popup should not reappear unless the user alters thier schedule so it becomes incomplete
	 * and then complete once again.  
	 */
}




