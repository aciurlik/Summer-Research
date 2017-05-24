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
	

	public SchedulePanel(Schedule sch) {

		super();
		
		this.sch=sch;
		this.setBackground(Color.yellow);
		//This will be deleted once we set it relative to the whole. 
		this.setPreferredSize(new Dimension(1000, 500));
		
		
		scrollPanel.setBackground(Color.yellow);//Same as Schedule Panel

		scrollPanel.setLayout(new GridLayout(1, numberOfRegularSemesters+1, spaceConstant, spaceConstant));//+1 For button
	
		//This looks at the schedule given and goes through and adds course to each semester
		
		
		
		Collections.sort(sch.semesters);
	
		
		for (int i=0; i<this.sch.semesters.size(); i++){
			String fresh = "Freshman";
			String soph = "Sophmore";
			String jun= "Junior";
			String sen = "Senior";
			Color firstAndThirdYear = Color.blue;
			Color secondAndFourthYear = Color.green;
			SemesterPanel semester = null;
			switch(i/3){
			
			//Perhaps add a SemesterDate element to retrieve which one?
			//Does this not happen because it does not give this as a parameter?
			//Add the display function for the course
			
			
			case 0: 
				semester = new SemesterPanel(fresh, this.sch.semesters.get(i) , firstAndThirdYear);
				break;

			
			case 1:
				semester = new SemesterPanel(soph, this.sch.semesters.get(i), secondAndFourthYear);
				break;
			
			
			case 2:
				semester = new SemesterPanel(jun, this.sch.semesters.get(i), firstAndThirdYear);
				break;
				
				
			case 3: 
				semester = new SemesterPanel(sen, this.sch.semesters.get(i),secondAndFourthYear);
				break;
			
				
			}
		
			scrollPanel.add(semester);
			



		}
		
		//Took add Button Panel from here and put it at the top
		addExtraSemesterButtonPanel.setPreferredSize(new Dimension(100, 100)); //Arbitrary size smaller than scroll Panel set to same color
		addExtraSemesterButtonPanel.setBackground(scrollPanel.getBackground());
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
		String extraSemesterClassTitle = "Super Senior";
		Color extraSemesterColor = Color.pink;
		
		scrollPanel.setLayout(new GridLayout(1, numberOfRegularSemesters+buttonPress+1, spaceConstant, spaceConstant));
		scrollPanel.remove(addExtraSemesterButtonPanel);
		
		
		SemesterPanel semester = new SemesterPanel(extraSemesterClassTitle, newSemester , extraSemesterColor);
		semester.setPreferredSize(new Dimension(500, 0));
		semester.setBackground(Color.pink);
		scrollPanel.add(semester);
		

		addSemesterButton.setPreferredSize(new Dimension(50, 50)); //Arbitrary size

		//addExtraSemesterButtonPanel.add(addSemesterButton);
		scrollPanel.add(addExtraSemesterButtonPanel);
		
	
			
		this.revalidate();
		this.repaint();
		
		
		
		
	}

}


