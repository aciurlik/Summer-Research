import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


public class AdditionsPanel extends JPanel implements ActionListener{
	/**
	 * Blurb Written: 7/19/2017
	 * Last Updated:  7/21/2017 
	 *  This panel holds buttons for high-impact functionality that should
 	 * always be visible (as opposed to buttons being hidden in a menu, 
 	 * or only becoming visible after some other action).
 	 * 
 	 * As of 7/18/2017, it is the panel on the left of the GUI and includes
 	 * 	add a Major/Track/Minor, add MayX or Summer, and explore other
 	 *  options from the Furman Advantage (these last just open webpages.)
 	 *   
	 */

	public String AdditionsHeader = new String(MenuOptions.FurmanAdvantage);
	public String classAddition = new String(MenuOptions.MajorMinor);
	
	public int optionsNumber = 8; //Number of Buttons
	public int headerNumber = 2;  //Number of Headers (The Furman Advantage. Major/Minor)
	public int spacingConstant = 0;// Kept this way to line up smaller than the bellTower
	
	ScheduleGUI schGUI;
	
	//Buttons As Seen in Panel
	private JButton ExploreStudyAwayButton;
	private JButton ExploreResearchButton;
	private JButton ExploreIntershipsButton;
	private JButton AddMayXButton;
	private JButton AddSummerClassButton;
	
	private JButton AddMinorButton;
	private JButton AddMajorButton;
	private JButton AddTrackButton;


	public AdditionsPanel(ScheduleGUI schGUI){
		//Sets layout, and style of the Panel
		super();
		this.schGUI=schGUI;
		this.setLayout((new GridLayout(optionsNumber+headerNumber, 1, spacingConstant, spacingConstant)));
		this.setBackground(FurmanOfficial.bouzarthGrey);
		JLabel header = new JLabel(AdditionsHeader);
		header.setHorizontalAlignment(JLabel.CENTER);
		header.setFont(FurmanOfficial.smallHeaderFont);
		this.add(header);



		
		this.ExploreStudyAwayButton = this.addButton(MenuOptions.exploreStudyAway);
		ExploreStudyAwayButton.setActionCommand(MenuOptions.exploreStudyAway);

		this.ExploreResearchButton = this.addButton(MenuOptions.addResearch);
		ExploreResearchButton.setActionCommand(MenuOptions.addResearch);
		
		this.ExploreIntershipsButton =this.addButton(MenuOptions.exploreInternship);
		ExploreIntershipsButton.setActionCommand(MenuOptions.exploreInternship);
		
		this.AddMayXButton = this.addButton(MenuOptions.addMayX);
		AddMayXButton.setActionCommand(MenuOptions.addMayX);
		
		this.AddSummerClassButton = this.addButton(MenuOptions.addSummerClass);
		AddSummerClassButton.setActionCommand(MenuOptions.addSummerClass);


		//Major/Minor Heading 
		JLabel classAdditions = new JLabel(classAddition);
		classAdditions.setHorizontalAlignment(JLabel.CENTER);
		classAdditions.setFont(FurmanOfficial.smallHeaderFont);
		this.add(classAdditions);

	
		this.AddMinorButton = this.addButton(MenuOptions.addMinor);
		AddMinorButton.setActionCommand(MenuOptions.addMinor);
		
		this.AddMajorButton = this.addButton(MenuOptions.addMajor);
		AddMajorButton.setActionCommand(MenuOptions.addMajor);
		
		this.AddTrackButton =this.addButton(MenuOptions.addTrack);
		AddTrackButton.setActionCommand(MenuOptions.addTrack);

	}

	
	/**
	 * 
	 * @param What you want to display on the button
	 * @return A button, that is uniform with the others on AdditionsPanel
	 */
	public JButton addButton(String s){
		int buttonWidth = 153;
		int buttonHeight =20; 
		//Formats Button Panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setOpaque(true);
		buttonPanel.setBackground(FurmanOfficial.bouzarthGrey);
	
		//Formats Button
		JButton button = new JButton(s);
		button.setFont(FurmanOfficial.normalFont);
		button.setForeground(Color.white);
		button.setHorizontalTextPosition(SwingConstants.LEFT);
		button.setBorderPainted(false);
		button.setBackground(FurmanOfficial.buttonPurple);
		button.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
		button.setOpaque(false);
		
		
		button.addActionListener(this);
		buttonPanel.add(button);
		this.add(buttonPanel);
		return button;


	}


	@Override
	public void actionPerformed(ActionEvent e) {
		//For add Major, Minor, Track
		if(e.getActionCommand().equals(MenuOptions.addMajor) || e.getActionCommand().equals(MenuOptions.addMinor)|| e.getActionCommand().equals(MenuOptions.addTrack)){
			schGUI.GUIMajorPopUP(e.getActionCommand());
		}
		//Explore Buttons, goes to outside links
		if((e.getActionCommand().equals(MenuOptions.exploreInternship)) || (e.getActionCommand().equals(MenuOptions.addResearch))||(e.getActionCommand().equals(MenuOptions.exploreStudyAway))){

			schGUI.GUIOutsideLink(e.getActionCommand());
		}
		if(e.getActionCommand().equals(MenuOptions.addSummerClass)){
			schGUI.addSummerSession();
		}
		if(e.getActionCommand().equals(MenuOptions.addMayX)){
			schGUI.addMayX();
		}


	}
	/**
	 * Recommended Testing
	 * Go through and check to make sure each button gives the expected result.
	 * 
	 * Bounds Test: 
	 * In addition when one adds a major/minor/track that should be removed from the 
	 * list the next time that command in pressed. When all of either of these three have been 
	 * added to the list the pop-up should display but give the user no viable choices. When one of 
	 * these majors/minor/tracks is deleted from the RequirementList Panel it's name should reappear 
	 * in the popUp add window. 
	 */

}
