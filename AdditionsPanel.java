import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

public class AdditionsPanel extends JPanel implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String AdditionsHeader = new String("The Furman Advantage");
	public String classAddition = new String("Major/Minor");
	public int optionsNumber = 8;
	public int headerNumber = 2;
	Driver d;
	private JButton AddMajorButton;
	private JButton AddMayXButton;
	private JButton AddTrackButton;
	private JButton AddMinorButton;
	private JButton ExploreResearchButton;
	private JButton ExploreIntershipsButton;
	private JButton AddSummerClassButton;
	private JButton ExploreStudyAwayButton;


	public AdditionsPanel( Driver d){

		super();
		this.d=d;
		this.setLayout((new GridLayout(optionsNumber+headerNumber, 1, 3, 3)));
		this.setBackground(FurmanOfficial.bouzarthGrey);
		JLabel header = new JLabel(AdditionsHeader);
		header.setHorizontalAlignment(JLabel.CENTER);
		header.setFont(FurmanOfficial.smallHeaderFont);
		this.add(header);



		//Explore Study Away
		this.ExploreStudyAwayButton = this.addButton(MenuOptions.exploreStudyAway);
		ExploreStudyAwayButton.setActionCommand(MenuOptions.exploreStudyAway);

		//Explore Research
		this.ExploreResearchButton = this.addButton(MenuOptions.addResearch);
		ExploreResearchButton.setActionCommand(MenuOptions.addResearch);

		//Explore Internships
		this.ExploreIntershipsButton =this.addButton(MenuOptions.addInternship);
		ExploreIntershipsButton.setActionCommand(MenuOptions.addInternship);

		//Add May X
		this.AddMayXButton = this.addButton(MenuOptions.addMayX);
		AddMayXButton.setActionCommand(MenuOptions.addMayX);

		//Add Summer Class
		this.AddSummerClassButton = this.addButton(MenuOptions.addSummerClass);
		AddSummerClassButton.setActionCommand(MenuOptions.addSummerClass);


		//Major/Minor Heading 
		JLabel classAdditions = new JLabel(classAddition);
		classAdditions.setHorizontalAlignment(JLabel.CENTER);
		classAdditions.setFont(FurmanOfficial.smallHeaderFont);
		this.add(classAdditions);

		//Add Minor
		this.AddMinorButton = this.addButton(MenuOptions.addMinor);
		AddMinorButton.setActionCommand(MenuOptions.addMinor);
		//Add Major
		this.AddMajorButton = this.addButton(MenuOptions.addMajor);
		AddMajorButton.setActionCommand(MenuOptions.addMajor);
		//Add Track
		this.AddTrackButton =this.addButton(MenuOptions.addTrack);
		AddTrackButton.setActionCommand(MenuOptions.addTrack);

	}

	public JButton addButton(String s){
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(this.getBackground());

		JButton button = new JButton(s);
		button.setFont(FurmanOfficial.normalFont);
		button.setForeground(Color.white);
		button.setHorizontalTextPosition(SwingConstants.LEFT);
		button.setBackground(FurmanOfficial.darkPurple);
		button.setBorderPainted(false);
		button.setPreferredSize(new Dimension(153, 20));
		button.setOpaque(true);
		button.addActionListener(this);
		button.setBorder(new BevelBorder(BevelBorder.RAISED));

		buttonPanel.add(button);
		this.add(buttonPanel);
		return button;


	}


	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(MenuOptions.addMajor) || e.getActionCommand().equals(MenuOptions.addMinor)|| e.getActionCommand().equals(MenuOptions.addTrack)){
			d.GUIPopUP(e.getActionCommand());

		}
		if((e.getActionCommand().equals(MenuOptions.addInternship)) || (e.getActionCommand().equals(MenuOptions.addResearch))||(e.getActionCommand().equals(MenuOptions.exploreStudyAway))){

			d.GUIOutsideLink(e.getActionCommand());

		}
		if(e.getActionCommand().equals(MenuOptions.addSummerClass)){
			d.GUIChooseSummerSession();

		}
		if(e.getActionCommand().equals(MenuOptions.addMayX)){
			d.GUIYearsPopUP(e.getActionCommand());
		}


	}

}
