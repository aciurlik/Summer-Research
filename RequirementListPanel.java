import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

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
	public Schedule schedule;
	public JLabel creditHoursLabel;
	
	public int layoutCounter;
	public static final int gridHeight = 3;
	// the layout locations handle the first few requirementpanels.
	// it may be safely changed, so long as the last entry is at the end of a column.
	public int[][] gridBagLayoutLocations = { {0,1},{0,2},{1,1},{1,2},{2,0},{2,1},{2,2}};
	
	public RequirementListPanel(Schedule s){
		this.schedule = s;
		
		
		//Put the main RequirementList panel, called inner, inside a scroll pane.
		this.inner = new JPanel();
		this.inner.setLayout(new GridBagLayout());
		this.inner.setBackground(Color.RED);
		this.scroll = new  JScrollPane(inner);
		
		// Make the first row of the inner panel, with labels
		// "Requirements" and "Credit Hours"
		this.infoPanel = new JPanel();
		creditHoursLabel = new JLabel(this.getCHText());
		this.infoPanel.add(new JLabel("Requirements      "));
		this.infoPanel.add(creditHoursLabel);
		
		//put all the items, including infoPanel and the requirement panels,
		// into inner.
		update();
		
		
		scroll.setPreferredSize(new Dimension(400,100));
		this.add(scroll);
		
		
		
	}
	
	/**
	 * Find the next location to place a component in the gridbag.
	 * This allows for sorting and lets you skip over the first
	 * row in the first few colums (for the infoPanel).
	 * @return
	 */
	public int[] nextLocation(){
		int[] result = {-1,-1};
		if(layoutCounter < gridBagLayoutLocations.length){
			result = gridBagLayoutLocations[layoutCounter];
		}
		else{
			int lastPrespecifiedColumn = gridBagLayoutLocations[
			                   gridBagLayoutLocations.length - 1][0];
			int totalAdvances = layoutCounter - gridBagLayoutLocations.length;
			int[] holder = {(lastPrespecifiedColumn + 1) + totalAdvances /gridHeight, 
					totalAdvances % gridHeight};
			result = holder;
		}
		layoutCounter ++;
		return result;
		
	}
	
	/**
	 * Update the requirementPanels displayed based on 
	 * the sort of the schedule's requirementsList.
	 */
	public void update(){
		this.inner.removeAll();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = gbc.CENTER;
		gbc.gridx = 0;
		gbc.gridy = 0;
		
		//First, add the infoPanel back in.
		gbc.gridwidth = 2;
		this.inner.add(infoPanel, gbc);
		gbc.gridwidth = 1;
		
		//Then add each of the requirement panels.
		layoutCounter = 0;
		ArrayList<Requirement> reqList = schedule.getRequirementsList();
		Collections.sort(reqList);
		for (Requirement req : reqList){
			RequirementPanel p = new RequirementPanel(req);
			int[] nextLocation = nextLocation();
			gbc.gridx = nextLocation[0];
			gbc.gridy = nextLocation[1];
			this.inner.add(p,gbc);
		}
		
	}
	/**
	 * Produce the text for the credit hours label
	 */
	public String getCHText(){
		return "Credit Hours Completed: " + this.schedule.getCreditHoursComplete();
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
