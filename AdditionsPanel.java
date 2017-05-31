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
		public String classAddition = new String("MAJOR/MINOR");
		public int optionsNumber = 8;
		public int headerNumber = 2;
		public int buttonsFontSize = 14;
		public int headerFontSize = 14;
		public Color FurmanDarkPurple = new Color(43, 12, 86);
		public Color FurmanLightPurple = new Color(79, 33, 112);
		public Color FurmanGray = new Color(96, 96, 91);
		Driver d;
		public String addMajor = "Add Major";
		public String addMinor = "Add Minor";
		public String addTrack = "Add Track";
		public String addInternship = "Explore Internships";
	
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
		this.addButton(MenuOptions.addMayX);
		this.addButton(MenuOptions.addSummerClass);
		this.addButton(MenuOptions.addStudyAway);
		this.addButton(MenuOptions.addInternship);
		this.addButton(MenuOptions.addResearch);
		JLabel classAdditions = new JLabel(classAddition);
		classAdditions.setHorizontalAlignment(JLabel.CENTER);
		classAdditions.setFont(FurmanOfficial.getFont(headerFontSize));
		this.add(classAdditions);
		this.addButton(MenuOptions.addMinor);
		this.addButton(MenuOptions.addMajor);
		this.addButton(MenuOptions.addTrack);
		this.add(empty);
		
	}
	
	public void addButton(String s){
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(Color.white);
		
		JButton button = new JButton(s);
		button.setFont(FurmanOfficial.getFont(buttonsFontSize));
		button.setForeground(Color.white);
		button.setHorizontalTextPosition(SwingConstants.LEFT);
		button.setBackground(FurmanDarkPurple);
		button.setBorderPainted(false);
		//button.setPreferredSize(new Dimension(153, 20));
		button.setOpaque(true);
		button.addActionListener(this);
		
		buttonPanel.add(button);
		this.add(buttonPanel);
		
	
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(MenuOptions.addMajor) || e.getActionCommand().equals(MenuOptions.addMinor)|| e.getActionCommand().equals(MenuOptions.addTrack)){
			d.GUIPopUP(e.getActionCommand());
			
		}
		if(e.getActionCommand().equals(MenuOptions.addInternship)){
			try {
				Desktop.getDesktop().browse(new URL("http://www.furman.edu/sites/internship/FindingInternships/Pages/default.aspx").toURI());
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
		}
		
		
	}

}
