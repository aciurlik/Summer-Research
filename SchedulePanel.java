


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

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;



public class SchedulePanel extends JPanel implements ActionListener{
	/**
	 * 
	 */
	
	private int numberOfRegularSemesters=12;
	private int spaceConstant=5;
	private int buttonPress=0;
	JPanel scrollPanel = new JPanel();
	JPanel addButtonPanel = new JPanel();
	JPanel addExtraSemesterButtonPanel = new JPanel();
	JButton addSemester = new JButton("+");

	public SchedulePanel() {

		super();

		this.setVisible(true);
		this.setBackground(Color.yellow);
		//This will be deleted once we set it relative to the whole. 
		this.setPreferredSize(new Dimension(1000, 500));
		
		
		scrollPanel.setVisible(true);
		scrollPanel.setBackground(Color.yellow);//Same as Schedule Panel

		scrollPanel.setLayout(new GridLayout(1, numberOfRegularSemesters+1, spaceConstant, spaceConstant));//+1 For button
	


		for (int i=0; i<numberOfRegularSemesters; i++){
			String fall = "Fall Semester";
			String spring = "Spring Semester";
			String MayXSummer = "May X and Summer";
			String fresh = "Freshman";
			String soph = "Sophmore";
			String jun= "Junior";
			String sen = "Senior";
			Color firstAndThirdYear = Color.blue;
			Color secondAndFourthYear = Color.green;
			SemesterPanel semester = null;
			switch(i){
			
			//Perhaps add a SemesterDate element to retrieve which one?
			//Does this not happen because it does not give this as a parameter?
			//Add the display function for the course
			case 0: 
				semester = new SemesterPanel(fresh, fall, firstAndThirdYear);
				break;

			
			case 1:
				semester = new SemesterPanel(fresh, spring, firstAndThirdYear);
				break;
			
			
			case 2:
				semester = new SemesterPanel(fresh, MayXSummer, firstAndThirdYear);
				break;
				
				
			case 3: 
				semester = new SemesterPanel(soph, fall,secondAndFourthYear);
				break;
			
				
			case 4:
				semester = new SemesterPanel(soph, spring, secondAndFourthYear);
				break;
				
			case 5:
				semester = new SemesterPanel(soph, MayXSummer, secondAndFourthYear);
				break;
				
			
			case 6:
				semester = new SemesterPanel(jun, fall, firstAndThirdYear);
				break;
	
				
			case 7: 
				semester = new SemesterPanel(jun, spring, firstAndThirdYear);
				break;
			
			case 8:
				semester = new SemesterPanel(jun, MayXSummer, firstAndThirdYear);
				break;
			
			case 9: 
				semester = new SemesterPanel(sen, fall, secondAndFourthYear);
				break;
			
			case 10:
				semester = new SemesterPanel(sen, spring, secondAndFourthYear);
				break;
			
			case 11:
				semester = new SemesterPanel(sen, MayXSummer,secondAndFourthYear);
				break;
				
			}
		
			semester.setPreferredSize(new Dimension(500, 0));
			scrollPanel.add(semester);
			



		}
		
		//Took add Button Panel from here and put it at the top
		addButtonPanel.setPreferredSize(new Dimension(100, 100)); //Arbitrary size smaller than scroll Panel set to same color
		addButtonPanel.setBackground(scrollPanel.getBackground());
		JButton addSemester = new JButton("+");
		addSemester.setPreferredSize(new Dimension(50, 50)); //Arbitrary size

		addButtonPanel.add(addSemester);
		scrollPanel.add(addButtonPanel);
		addSemester.addActionListener(this);




		JScrollPane scrollPane = new JScrollPane(scrollPanel);
		scrollPane.setPreferredSize(this.getPreferredSize());
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		this.add(scrollPane); 

	}


	@Override
	public void actionPerformed(ActionEvent e) {
		Color extraSemesterColor = Color.pink;
		
		scrollPanel.setLayout(new GridLayout(1, numberOfRegularSemesters+buttonPress+1, spaceConstant, spaceConstant));
		scrollPanel.remove(addButtonPanel);
	
		scrollPanel.remove(addExtraSemesterButtonPanel);
		

		if (buttonPress%3==0){
			String extraSemester = "Fall";
			SemesterPanel semester = new SemesterPanel("Super Senior", extraSemester, extraSemesterColor);
			semester.setPreferredSize(new Dimension(500, 0));
			semester.setBackground(Color.pink);
			scrollPanel.add(semester);
		

		}
		if (buttonPress%3==1){
			String extraSemester = "Spring";
			SemesterPanel semester = new SemesterPanel("Super Senior", extraSemester, extraSemesterColor);
			semester.setPreferredSize(new Dimension(500, 0));
			semester.setBackground(Color.pink);
			scrollPanel.add(semester);
			

		}

		if (buttonPress%3==2){
			String extraSemester = "MayX and Summer";
			SemesterPanel semester = new SemesterPanel("Super Senior", extraSemester, extraSemesterColor);
			semester.setPreferredSize(new Dimension(500, 0));
			scrollPanel.add(semester);
			


		}
		
		
		buttonPress++;

		addExtraSemesterButtonPanel.setPreferredSize(new Dimension(100, 100));
		addExtraSemesterButtonPanel.setBackground(Color.green);
		
		addSemester.setPreferredSize(new Dimension(50, 50)); //Arbitrary size

		addExtraSemesterButtonPanel.add(addSemester);
		scrollPanel.add(addExtraSemesterButtonPanel);
		addSemester.addActionListener(this);
	
		scrollPanel.invalidate();
		scrollPanel.getParent().invalidate();
		this.invalidate();
		scrollPanel.repaint();
		this.repaint();
		
		
		
		
	}

}




