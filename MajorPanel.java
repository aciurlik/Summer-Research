import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

public class MajorPanel extends JPanel {
	public  Major major;
	public Driver d;
	
	JPanel top;
	JPanel bottom;
	int nimbusWidth = 40;
	int nibusHeight = 20;

	public MajorPanel(Major m, Driver d){
		super();
		this.d=d;
		this.major = m;
		update(m);
	}




	public void update (Major m){
		ArrayList<Requirement> reqList = new ArrayList<Requirement>(m.reqList);
		Collections.sort(reqList);

		//Calculte the requirements left in this major
		int reqsLeft = 0;
		for(Requirement r : reqList){
			reqsLeft += r.storedCoursesLeft;
		}


		//Make this major's panel
		this.setLayout(new BorderLayout());

		//Holds all the things on the top
		top = new JPanel ();
		top.setLayout(new BorderLayout());
		top.setBorder(new CompoundBorder(new EmptyBorder(4, 4, 4, 4), new MatteBorder(0, 0, 1, 0, Color.BLACK)));


		//Holds the things at the top left (currently major name and # unscheduled)
		JPanel topLeftPanel = new JPanel(); 
		topLeftPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		JLabel topLeftLabel =new JLabel(m.name + "         " + Math.max(0, reqsLeft)  + " Unscheduled"); 
		topLeftLabel.setFont(FurmanOfficial.smallHeaderFont);
		topLeftPanel.add(topLeftLabel);

		//The panel at the top right (currently the remove button)
		JPanel topRightPanel = new JPanel();
		topRightPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton remove = new JButton("x");
		//Makes it so you can't exit GER's 
		if(m.name.equals("GER")){
			remove.setEnabled(false);

		}
		if(MenuOptions.UIType){
			remove.setBackground(FurmanOfficial.nimbus);
			remove.setMargin(new Insets(1,1,1,1));
			remove.setPreferredSize(new Dimension(nimbusWidth, nibusHeight));
		}
		else{
			remove.setBackground(FurmanOfficial.darkPurple);
			remove.setPreferredSize(new Dimension (20, 20));
		}
		
		remove.setForeground(Color.WHITE);
	
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
		//heightFlex.setLayout(new WrapLayout());
		for(Requirement r : reqList){
			heightFlex.add(new RequirementPanel(r,d));
		}
		bottom.add(heightFlex, BorderLayout.WEST);
		this.add(bottom, BorderLayout.CENTER);
	}


	/*
	public int getPreferredHeight(){
		int result = 0;
		result += top.getPreferredSize().height;
		result += bottom.getPreferredSize().height;
		return result;

	}
	 */

	public void removeSelf(){
		d.GUIRemoveMajor(this);

	}



}
