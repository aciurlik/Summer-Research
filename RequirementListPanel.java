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
public class RequirementListPanel extends JPanel implements ActionListener{
	public JScrollPane scroll;
	public JPanel inner;
	public ScheduleGUI d;
	

	JPanel scrollPanel = new JPanel();
	JPanel infoPanel; //reqs left, credit hours, and CLPs
	public JLabel creditHoursLabel;
	public String cHText = "     Credit Hours Left: ";
	public JLabel reqsLeftLabel;
	public String reqsText = "Estimated Courses Left: ";
	public JLabel clpLeftLabel;
	public String clpText =  "     CLPs Left:   ";
	//public Schedule schedule;


	public int layoutCounter;
	public static final int gridHeight = 3;
	// the layout locations handle the first few requirementpanels.
	// it may be safely changed, so long as the last entry is at the end of a column.

	public RequirementListPanel(Schedule s, ScheduleGUI d){
		
		
		//this.schedule = s;
		this.d = d;
		this.setBackground(FurmanOfficial.grey(60));
		//Put the main RequirementList panel, called inner, inside a scroll pane.
		this.inner = new JPanel();
		this.inner.setLayout(new GridBagLayout());
		this.inner.setBackground(Color.white);
		inner.setPreferredSize(new Dimension(700, 150));
		this.scroll = new  JScrollPane(inner);

		this.setLayout(new BorderLayout());

	
		//put all the requirement panels into inner.

		
		//scroll.setPreferredSize(new Dimension(700,150));
		this.add(scroll, BorderLayout.CENTER);

		
		this.infoPanel = new JPanel();
		creditHoursLabel = new JLabel();
		creditHoursLabel.setFont(FurmanOfficial.bigHeaderFont);
		reqsLeftLabel = new JLabel();
		reqsLeftLabel.setFont(FurmanOfficial.bigHeaderFont);
		clpLeftLabel = new JLabel();
		clpLeftLabel.setFont(FurmanOfficial.bigHeaderFont);
		JButton checkAllErrors = new JButton(MenuOptions.checkAllErrors);
		checkAllErrors.addActionListener(this);
		
		
		this.infoPanel.add(reqsLeftLabel);
		this.infoPanel.add(creditHoursLabel);
		this.infoPanel.add(clpLeftLabel);
		this.infoPanel.add(checkAllErrors);
		this.infoPanel.setBackground(this.getBackground());
		this.add(infoPanel, BorderLayout.NORTH);
		
		
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
		//gbc.anchor = gbc.CENTER;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.insets = new Insets(3,3,3,3);
		gbc.anchor = GridBagConstraints.LINE_START;

		schedule.checkUpdateReqs();
		
		ArrayList<Major> majors = schedule.getMajors();
		//System.out.println(majors.size());
		int heightCounter = 1;
		for(Major m : majors){
			MajorPanel majorPanel = new MajorPanel(m, d);
			gbc.gridx = 0;
			gbc.gridy = heightCounter;
			heightCounter++;
			this.inner.add(majorPanel, gbc);
		
		}
		
		this.creditHoursLabel.setText(this.cHText + Math.max(0, (128 - schedule.getCreditHoursComplete())));
		this.reqsLeftLabel.setText(this.reqsText + Math.max(0, schedule.estimatedCoursesLeft()));
		this.clpLeftLabel.setText(this.clpText + Math.max(0, 32 - schedule.getCLP()));
		
		
		
		
	}
	
	
	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(MenuOptions.checkAllErrors)){
			d.GUICheckAllErrors(true);
		}
	}
	
	
	
	

}

