/*
 * 
 * https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/uiswing/examples/layout/GridBagLayoutDemoProject/src/layout/GridBagLayoutDemo.java
 * https://docs.oracle.com/javase/tutorial/uiswing/layout/gridbag.html
 * 
package scheduler;
/*
 * GridBagLayoutDemo.java requires no other files.
 */
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;



public class SchedulePanel extends JPanel implements ActionListener{
	/**
	 * 
	 */
	private Schedule sch;
	private int numberOfRegularSemesters=12;
	private int spaceConstant=5;
	private int buttonPress=0;
//	private int buttonCounter=0;
//	private int doubleCounter=1;
//	private int removalCount=0;
	JPanel scrollPanel = new JPanel();
	JPanel addExtraSemesterButtonPanel = new JPanel();
	JButton addSemesterButton = new JButton("+");
	//public Color FurmanDarkPurple = new Color(43, 12, 86);
	//public Color FurmanLightPurple = new Color(79, 33, 112);
	//public Color FurmanGray = new Color(96, 96, 91);
	public Color FurmanDarkPurpleAlpha = new Color(43,12,86,50);
	public Color FurmanGrayAlpha = new Color(96, 96, 91, 50);
	public Color FurmanLightPurpleAlpha = new Color(79, 33, 112, 50);
	Driver d;
	

	public SchedulePanel(Schedule sch, Driver d) {

		super();
		
		this.d = d;
		this.sch=sch;
		this.setBackground(Color.white);
		//This will be deleted once we set it relative to the whole. 
		this.setPreferredSize(new Dimension(1000, 500));
		
		
		scrollPanel.setBackground(Color.white);//Same as Schedule Panel

		scrollPanel.setLayout(new GridLayout(1, numberOfRegularSemesters+1, spaceConstant, spaceConstant));//+1 For button
	
		//This looks at the schedule given and goes through and adds course to each semester
		
		
		
		Collections.sort(sch.semesters);
	
		
		for (int i=0; i<this.sch.semesters.size(); i++){
			SemesterPanel semester = null;
			switch(i/2){
			
			//Perhaps add a SemesterDate element to retrieve which one?
			//Does this not happen because it does not give this as a parameter?
			//Add the display function for the course
			
			
			case 0: 
				semester = new SemesterPanel( this.sch.semesters.get(i) ,  FurmanLightPurpleAlpha, this.d);
				break;

			
			case 1:
				semester = new SemesterPanel( this.sch.semesters.get(i), FurmanGrayAlpha, this.d);
				break;
			
			
			case 2:
				semester = new SemesterPanel( this.sch.semesters.get(i), FurmanLightPurpleAlpha, this.d);
				break;
				
				
			case 3: 
				semester = new SemesterPanel( this.sch.semesters.get(i), FurmanGrayAlpha, this.d);
				break;
			
				
			}
		
			scrollPanel.add(semester);
			



		}
		
		//Took add Button Panel from here and put it at the top
		addExtraSemesterButtonPanel.setPreferredSize(new Dimension(100, 100)); //Arbitrary size smaller than scroll Panel set to same color
		addExtraSemesterButtonPanel.setBackground(FurmanLightPurpleAlpha);
		JButton addSemester = new JButton("+");
		addSemester.setPreferredSize(new Dimension(50, 50)); //Arbitrary size
		addExtraSemesterButtonPanel.add(addSemester);
		scrollPanel.add(addExtraSemesterButtonPanel);
		addSemester.addActionListener(this);




		JScrollPane scrollPane = new JScrollPane(scrollPanel);
		scrollPane.setPreferredSize(this.getPreferredSize());
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		this.add(scrollPane); 

	}


	@Override
	public void actionPerformed(ActionEvent e) {

		
		Semester newSemester = sch.addNewSemester();

		scrollPanel.setLayout(new GridLayout(1, numberOfRegularSemesters+buttonPress+1, spaceConstant, spaceConstant));
		scrollPanel.remove(addExtraSemesterButtonPanel);
		
		
		SemesterPanel semester = new SemesterPanel(newSemester , FurmanLightPurpleAlpha, d);
		semester.setPreferredSize(new Dimension(500, 0));
		scrollPanel.add(semester);
		

		addSemesterButton.setPreferredSize(new Dimension(50, 50)); //Arbitrary size
		

		//addExtraSemesterButtonPanel.add(addSemesterButton);
		scrollPanel.add(addExtraSemesterButtonPanel);
		
	
			
		this.revalidate();
		this.repaint();
		
		
		
		
	}

}


