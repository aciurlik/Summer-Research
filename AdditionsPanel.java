import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class AdditionsPanel extends JPanel implements ActionListener{
		public String AdditionsHeader = new String("The Furman Advantage");
		public String classAddition = new String("Major/Minor");
		public int optionsNumber = 8;
		public int headerNumber = 2;
		public int buttonsFontSize = 11;
		public int headerFontSize = 14;
		Driver d;
		private JButton AddMajorButton;
		private JButton AddMayXButton;
		private JButton AddTrackButton;
		private JButton AddMinorButton;
		private JButton ExploreResearchButton;
		private JButton ExploreIntershipsButton;
		private JButton AddSummerClassButton;
		private JButton ExploreStudyAwayButton;
	;
	
	public AdditionsPanel( Driver d){
		
		super();
		this.d=d;
		this.setLayout((new GridLayout(optionsNumber+headerNumber+2, 1, 3, 3)));
		JLabel empty = new JLabel("          ");
		this.add(empty);
		this.setBackground(Color.white);
		JLabel header = new JLabel(AdditionsHeader);
		header.setHorizontalAlignment(JLabel.CENTER);
		header.setFont(FurmanOfficial.getFont(headerFontSize));
		this.add(header);
		
		
		
		//Explore Study Away
		this.ExploreStudyAwayButton = this.addButton(MenuOptions.addStudyAway);
		ExploreStudyAwayButton.setActionCommand(MenuOptions.addStudyAway);
		
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
		classAdditions.setFont(FurmanOfficial.getFont(headerFontSize));
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
		
		this.add(empty);
		
	}
	
	public JButton addButton(String s){
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(Color.white);
		
		JButton button = new JButton(s);
		button.setFont(FurmanOfficial.getFont(buttonsFontSize));
		button.setForeground(Color.white);
		button.setHorizontalTextPosition(SwingConstants.LEFT);
		button.setBackground(FurmanOfficial.darkPurple);
		button.setBorderPainted(false);
		button.setPreferredSize(new Dimension(153, 20));
		button.setOpaque(true);
		button.addActionListener(this);
		
		buttonPanel.add(button);
		this.add(buttonPanel);
		return button;
		
	
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(MenuOptions.addMajor) || e.getActionCommand().equals(MenuOptions.addMinor)|| e.getActionCommand().equals(MenuOptions.addTrack)){
			d.GUIPopUP(e.getActionCommand());
			
		}
		if((e.getActionCommand().equals(MenuOptions.addInternship)) || (e.getActionCommand().equals(MenuOptions.addResearch))||(e.getActionCommand().equals(MenuOptions.addStudyAway))){
			
			d.GUIOutsideLink(e.getActionCommand());
			
		}
		if(e.getActionCommand().equals(MenuOptions.addSummerClass)|| (e.getActionCommand().equals(MenuOptions.addMayX))){
			d.GUIYearsPopUP(e.getActionCommand());
		
		}
		
		
	}

}
