import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

/**
 * Blurb written 7/24/2017
 * Last updated 7/24/2017
 * 
 * This is the Panel that contains a single Major's requirements
 * the top part of the major holds all the information including
 * the name, the type it corresponds to (BA,BM,BS) and the 
 * estimated courses left unscheduled. It also has a button that 
 * allows the user to remove the major from the scheduleGUI. 
 *
 */
public class MajorPanel extends JPanel {
	
	public  Major major;
	public ScheduleGUI schGUI;

	JPanel top;
	JPanel bottom;
	int nimbusWidth = 40;
	int nibusHeight = 20;

	public MajorPanel(Major m, ScheduleGUI schGUI){
		super();
		this.schGUI=schGUI;
		this.major = m;
		update(m);
	}
	
	public void update (Major m){
		ArrayList<Requirement> reqList = new ArrayList<Requirement>(m.reqList);
		
		//Calculate the requirements left in this major
		int reqsLeft = 0;
		for(Requirement r : reqList){
			reqsLeft += r.getStoredCoursesLeft();
		}

		//Make this major's panel
		this.setLayout(new BorderLayout());

		//Holds aspects of the Major on the top
		top = new JPanel ();
		top.setLayout(new BorderLayout());
		top.setBorder(new CompoundBorder(new EmptyBorder(4, 4, 4, 4), new MatteBorder(0, 0, 1, 0, Color.BLACK)));
		//Holds the things at the top left (currently major name, BS/BA, and # unscheduled)
		JPanel topLeftPanel = new JPanel(); 
		topLeftPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		String degreeType = "";
		if(m.chosenDegree != -1){
			degreeType = "(" + CourseList.getDegreeTypeString(m.chosenDegree) + ")";
		}
		JLabel topLeftLabel =new JLabel(m.name + " " 
					+ degreeType + "        "
					+ Math.max(0, reqsLeft)  + " Unscheduled");
		topLeftLabel.setFont(FurmanOfficial.smallHeaderFont);
		topLeftPanel.add(topLeftLabel);

		//The panel at the top right (currently the remove button)
		JPanel topRightPanel = new JPanel();
		topRightPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton remove = new JButton("x");
		//Makes it so you can't exit GER's or prereqs
		if(m.name.equals("GER") || m.name.equals("Prereqs")){
			remove.setEnabled(false);
		}
		if(MenuOptions.UIType){//This must be adjusted so the text isn't cut off
			remove.setMargin(new Insets(1,1,1,1));
			remove.setPreferredSize(new Dimension(nimbusWidth, nibusHeight));
		}
		else{
			remove.setPreferredSize(new Dimension (20, 20));
		}
		remove.setForeground(Color.WHITE);
		remove.setBackground(FurmanOfficial.darkPurple);
		remove.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				removeSelf();
			}
		});
		topRightPanel.add(remove);
		top.add(topLeftPanel, BorderLayout.WEST);
		top.add(topRightPanel, BorderLayout.EAST);
		this.add(top, BorderLayout.NORTH);


		//Holds the requirements
		bottom = new JPanel();
		bottom.setLayout(new BorderLayout());
		JPanel heightFlex = new JPanel();
		//Adds a requirement panel for each listed in major file. 
		for(Requirement r : reqList){
			heightFlex.add(new RequirementPanel(r,schGUI, this));
		}
		bottom.add(heightFlex, BorderLayout.WEST);
		this.add(bottom, BorderLayout.CENTER);
	}

	
	public void removeSelf(){
		schGUI.GUIRemoveMajor(this);

	}



}
