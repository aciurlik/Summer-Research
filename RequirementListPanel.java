import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;


import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


/**
 * Blurb Written: before 7/1/2017
 * Last Updated: 7/26/2017
 * 
 * This panel holds all the major panels, the credit hours countdown, and
 * the checkAllErrors button.
 * 
 * RequirementListPanel is in the GUI group of classes.
 * 
 */

public class RequirementListPanel extends JPanel implements ActionListener{

	
	public JPanel inner; //holds the major panels
	public JScrollPane scroll; //holds inner
	

	public ScheduleGUI schGUI;
	

	JPanel scrollPanel = new JPanel();
	JPanel creditsCountdownPanel; 
	public JLabel creditsCountdownLabel;
	public static String cHText = "        Credit Hours: ";
	public static String chInformText = " planned of 128";
	
	//These were once placed near the creditsCountdown panel, but got
	//    removed once we discovered how difficult it can be to get an
	//    accurate estimate of the number of courses needed to take.
	//It was mentioned that it would be nice to put them back in because
	//    students seemed to enjoy seeing the countdown of courses too.
	//public JLabel reqsLeftLabel;
	//public String reqsText = "Estimated Courses Left: ";
	//public JLabel clpLeftLabel;
	//public String clpText =  "     CLPs Left:   ";
	//public Schedule schedule;

	
	public RequirementListPanel(Schedule s, ScheduleGUI schGUI){

		//this.schedule = s;
		this.schGUI = schGUI;
		this.setBackground(FurmanOfficial.grey(60));
		
		//Put the panel that holds majorPanels, called inner, inside a scroll pane.
		this.inner = new JPanel();
		this.inner.setLayout(new GridBagLayout());
		this.inner.setBackground(Color.white);
		this.scroll = new  JScrollPane(inner);
		this.setLayout(new BorderLayout());


		this.setPreferredSize(new Dimension(700, 150));
		//inner.setPreferredSize(new Dimension(700, 150));
		//scroll.setPreferredSize(new Dimension(700,150));
		
		this.add(scroll, BorderLayout.CENTER);

		
		this.creditsCountdownPanel = new JPanel();
		creditsCountdownPanel.setLayout(new BorderLayout());
		creditsCountdownLabel = new JLabel();
		creditsCountdownLabel.setFont(FurmanOfficial.smallHeaderFont);
		//reqsLeftLabel = new JLabel();
		//reqsLeftLabel.setFont(FurmanOfficial.bigHeaderFont);
		//clpLeftLabel = new JLabel();
		//clpLeftLabel.setFont(FurmanOfficial.bigHeaderFont);
		JButton checkAllErrorsButton = new JButton(MenuOptions.checkAllErrors);
		checkAllErrorsButton.addActionListener(this);
		
		
		//this.infoPanel.add(reqsLeftLabel);
		this.creditsCountdownPanel.add(creditsCountdownLabel);
		//this.infoPanel.add(clpLeftLabel);
		this.creditsCountdownPanel.add(checkAllErrorsButton, BorderLayout.EAST);
		
		this.creditsCountdownPanel.setBackground(this.getBackground());
		this.add(creditsCountdownPanel, BorderLayout.NORTH);
		
		
		update(s);
	}


	/**
	 * Update the major panels and 
	 * the credits counter
	 */
	public void update(Schedule schedule){
		
		
		this.inner.removeAll();
		GridBagConstraints gbc = new GridBagConstraints();
		//gbc.anchor = gbc.CENTER;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.insets = new Insets(3,3,3,3);
		gbc.anchor = GridBagConstraints.LINE_START;

		schedule.checkUpdateReqs();
		
		ArrayList<Major> majors = schedule.getMajors();
		int heightCounter = 1;
		for(Major m : majors){
			MajorPanel majorPanel = new MajorPanel(m, schGUI);
			gbc.gridx = 0;
			gbc.gridy = heightCounter;
			heightCounter++;
			this.inner.add(majorPanel, gbc);
		
		}
		
		String creditsCountdownText = RequirementListPanel.cHText 
				+ Math.max(0, schedule.getCreditHoursComplete()) 
				+ RequirementListPanel.chInformText;
		this.creditsCountdownLabel.setText(creditsCountdownText);
		//this.reqsLeftLabel.setText(this.reqsText + Math.max(0, schedule.estimatedCoursesLeft()));
		//this.clpLeftLabel.setText(this.clpText + Math.max(0, 32 - schedule.getCLP()));
	}
	
	
	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(MenuOptions.checkAllErrors)){

			schGUI.checkAllErrors();

		}
	}
	
	
	
	

}

