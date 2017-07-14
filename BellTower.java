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
	JPanel overlap ;
	
	ImageIcon icon ;
	ImageIcon firework;
	JLabel belltowerLabel;
	JPanel endPicturePanel;
	Image fireWork;
	ImageIcon fires;
	JLabel pictureLabel;

	boolean wasComplete;



	public BellTower(Schedule sch) {
		super();

		wasComplete = false;


		overlap = new JPanel();
		icon = new ImageIcon(MenuOptions.resourcesFolder + "bellTower.jpg");
		
		
		firework = new ImageIcon(MenuOptions.resourcesFolder + "fireworks.jpg");
		endPicturePanel  = new JPanel();
		fireWork = firework.getImage();
		fires = new ImageIcon(fireWork);
		pictureLabel = new JLabel(fires);
		endPicturePanel.add(pictureLabel);
		
		
		
		this.sch= sch;
		int givenHeight = 300;
		int givenWidth =100;
		
		//Belltower icon and scaling
		Image image = icon.getImage();
		Image newImage = image.getScaledInstance(givenWidth, givenHeight , java.awt.Image.SCALE_SMOOTH);
		icon = new ImageIcon(newImage);

		belltowerLabel = new JLabel(icon);
		belltowerLabel.add(overlap);

		this.add(belltowerLabel);
		this.setPreferredSize(new Dimension(100, 300));
		this.setBackground(FurmanOfficial.bouzarthGrey);
	}


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
		int newHeight = (int)(percentDone * icon.getIconHeight()) - 1;
		overlap.setSize(icon.getIconWidth(), newHeight);
		overlap.setLocation(0, icon.getIconHeight()-newHeight);
		overlap.setBackground(FurmanOfficial.officialAlpha);

		
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
					JOptionPane.showMessageDialog(null, endPicturePanel , "Congradulations on having a complete, error free schedule!", JOptionPane.INFORMATION_MESSAGE);
				}
				else{
					JOptionPane.showMessageDialog(null, "You've finished your requirements, but there are still some errors left", "Finished Requirements", JOptionPane.INFORMATION_MESSAGE, sch.d.icon);

					sch.d.GUICheckAllErrors(true);
				}
			}
		}
		
	}
}




