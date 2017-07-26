import java.awt.Dimension;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class BellTower extends JPanel implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Schedule sch;
	JPanel layerPanel ;
	ImageIcon bellTowerIcon ;
	ImageIcon fireworkIcon;
	JLabel belltowerLabel;

	JLabel pictureLabel;

	boolean wasComplete; //This is used to prevent the congrats/Error popUp from occurring multiple times
	//after the user has originally complete the schedule. This will switch back to false if the user does
	//an action that renders the schedule incomplete again. If they then were to recomplete the schedule 
	//wasComplete would be false thus the popUp would occur. 

	/**
	 * GUI CLASS
	 * 
	 * Blurb Written: 7/19/2017
	 * Last Updated:  7/19/2017
	 * 
	 * This is the graphic at the bottom, west side of the ScheduleGUI. It consists of Image of the BellTower loaded from the FileHandler
	 * It fills with purple (the rectangle is placed on top of the image) the  as the user
	 * fulfills his/her requirements. (Roughly represents the percentage done)
	 * When all the requirements are considered satisfied (and the user has at least one major
	 * in his/her schedule) the bellTower will display a message describing all of the current user
	 * errors in the schedule, or in the case of no errors a celebratory fireworks picture.  
	 * @param sch The schedule that is set in scheduleGUI. 
	 */

	public BellTower(Schedule sch) {
		super();
		this.sch=sch;
		wasComplete = false;


		layerPanel = new JPanel();
		bellTowerIcon = FileHandler.makeBellTower();

		fireworkIcon = FileHandler.makeFireWorks();
		pictureLabel = new JLabel(fireworkIcon);

		//Belltower icon and scaling
		int givenHeight = 300;
		int givenWidth =100;
		Image image = bellTowerIcon.getImage();
		Image newImage = image.getScaledInstance(givenWidth, givenHeight , java.awt.Image.SCALE_SMOOTH);
		bellTowerIcon = new ImageIcon(newImage);
		belltowerLabel = new JLabel(bellTowerIcon);
		belltowerLabel.add(layerPanel);

		this.add(belltowerLabel);
		this.setPreferredSize(new Dimension(100, 300));
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
	 * This fills up the purple on the schedule in accordance to the amount 
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


		//If you complete all the requirements in your schedule and have at least one major a popup will give you either
		//a list of errors that are still present in your schedule, or a some fireworks of congrats if your schedule is perfect
		boolean nowComplete = sch.isComplete(); //This method checks errors and Major>1
		if(!nowComplete){
			wasComplete = false;
		}
		else{
			if(!wasComplete){
				wasComplete=true;
				if(sch.checkAllErrors().isEmpty()){
					JOptionPane.showMessageDialog(null, pictureLabel, "Congratulations on having a complete, error free schedule!", JOptionPane.INFORMATION_MESSAGE);
				}
				else{
					JOptionPane.showMessageDialog(null, "You've finished your requirements, but there are still some errors left", "Finished Requirements", JOptionPane.INFORMATION_MESSAGE, sch.schGUI.icon);

					sch.schGUI.GUICheckAllErrors(true);
				}
			}
		}

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




