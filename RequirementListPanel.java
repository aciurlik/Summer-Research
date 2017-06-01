import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

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
	public Driver d;
	//public Schedule schedule;


	public int layoutCounter;
	public static final int gridHeight = 3;
	// the layout locations handle the first few requirementpanels.
	// it may be safely changed, so long as the last entry is at the end of a column.

	public RequirementListPanel(Schedule s, Driver d){
		//this.schedule = s;
		this.d = d;

		//Put the main RequirementList panel, called inner, inside a scroll pane.
		this.inner = new JPanel();
		this.inner.setLayout(new GridBagLayout());
		this.inner.setBackground(Color.white);
		this.scroll = new  JScrollPane(inner);

		this.setLayout(new BorderLayout());

		//put all the requirement panels into inner.


		scroll.setPreferredSize(new Dimension(800,200));
		this.add(scroll, BorderLayout.CENTER);


		update(s);
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

		ArrayList<Major> majors = schedule.getMajors();
		int heightCounter = 1;


		for(Major m : majors){

			MajorPanel majorPanel = new MajorPanel(m, d);



			gbc.gridx = 0;
			gbc.gridy = heightCounter;
			heightCounter++;
			this.inner.add(majorPanel, gbc);
		}
	}


	public static void main(String[] args){
		// Testing the requirementList panel for the first time
		RequirementListPanel p = new RequirementListPanel(Schedule.testSchedule(), null);

		JFrame frame = new JFrame();
		frame.getContentPane().add(p);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);

	}

}
