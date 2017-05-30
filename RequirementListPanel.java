import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

/**
 * This panel is the list of requirements that can be added to the
 * schedule.
 * 
 * The requirementsList will be given to it by a schedule
 *   rather than have the requirementList stored in two places.
 * 
 * @author dannyrivers
 *
 */
public class RequirementListPanel extends JPanel{
	public JScrollPane scroll;
	public JPanel inner;
	public JPanel infoPanel;
	//public Schedule schedule;
	public JLabel creditHoursLabel;
	public JLabel reqsLeftLabel;

	public int layoutCounter;
	public static final int gridHeight = 3;
	// the layout locations handle the first few requirementpanels.
	// it may be safely changed, so long as the last entry is at the end of a column.
	public Color FurmanDarkPurple = new Color(43, 12, 86);
	public Color FurmanLightPurple = new Color(79, 33, 112);
	public Color FurmanGray = new Color(96, 96, 91);

	public RequirementListPanel(Schedule s){
		//this.schedule = s;


		//Put the main RequirementList panel, called inner, inside a scroll pane.
		this.inner = new JPanel();
		this.inner.setLayout(new GridBagLayout());
		this.inner.setBackground(Color.white);
		this.scroll = new  JScrollPane(inner);

		// Make the first row of the inner panel, with labels
		// "Requirements" and "Credit Hours"
		this.infoPanel = new JPanel();
		creditHoursLabel = new JLabel();
		reqsLeftLabel = new JLabel();
		this.infoPanel.add(reqsLeftLabel);
		this.infoPanel.add(creditHoursLabel);
		this.infoPanel.setBackground(inner.getBackground());

		this.setLayout(new BorderLayout());
		this.add(infoPanel, BorderLayout.NORTH);

		//put all the items, including infoPanel and the requirement panels,
		// into inner.
		update(s);


		scroll.setPreferredSize(new Dimension(800,200));
		this.add(scroll);



	}

	/**
	 * Find the next location to place a component in the gridbag.
	 * @return
	 */
	public int[] nextLocation(){
		int[] result = {layoutCounter/3,layoutCounter%3};
		layoutCounter ++;
		return result;

	}

	/**
	 * Update the requirementPanels displayed based on 
	 * the the majors in the schedule. 
	 */
	public void update(Schedule schedule){
		this.inner.removeAll();

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = gbc.CENTER;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.insets = new Insets(3,3,3,3);

		this.inner.add(infoPanel);

		int reqsLeft = 0;
		ArrayList<Major> majors = schedule.getMajors();
		int heightCounter = 1;
		for(Major m : majors){
			JPanel p = new JPanel();
			p.add(new JLabel(m.name));
			p.add(new JSeparator(SwingConstants.HORIZONTAL));
			ArrayList<Requirement> reqList = new ArrayList<Requirement>(m.reqList);
			Collections.sort(reqList);
			for(Requirement r : reqList){
				if(r.numFinished < r.numToChoose){
					reqsLeft += r.numToChoose - r.numFinished;
				}
				p.add(new RequirementPanel(r));
			}
			gbc.gridx = 0;
			gbc.gridy = heightCounter;
			heightCounter++;
			this.inner.add(p, gbc);
		}

		this.creditHoursLabel.setText(this.getCHText() + (230 - schedule.getCreditHoursComplete()));
		this.reqsLeftLabel.setText(this.getReqsText() + reqsLeft);
	}
	/**
	 * Produce the text for the credit hours label
	 */
	public String getCHText(){
		return "Credit Hours Left: ";
	}

	public String getReqsText(){
		return "Requirements Left: "; 
	}


	public static void main(String[] args){
		// Testing the requirementList panel for the first time
		RequirementListPanel p = new RequirementListPanel(Schedule.testSchedule());



		JFrame frame = new JFrame();
		frame.getContentPane().add(p);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);

	}

}
