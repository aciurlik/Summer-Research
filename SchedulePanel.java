


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
	ArrayList<SemesterPanel> singleSemesterList = new ArrayList<SemesterPanel>();
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
			//Inelegant, is there a better way?  Why is it messing up the order? 
			String fall = "Fall Semester";
			String spring = "Spring Semester";
			String MayXSummer = "May X and Summer";
			String fresh = "Freshman";
			String soph = "Sophmore";
			String jun= "Junior";
			String sen = "Senior";
			//SingleSemester semester = null;
			if (i==0){
				SemesterPanel semester = new SemesterPanel(fresh, fall);
				semester.setBackground(Color.green);
				semester.setPreferredSize(new Dimension(500, 0)); //This sets it as .45 of scroll display to show two semesters and a bit more
				scrollPanel.add(semester);
				singleSemesterList.add(semester);

			}
			if (i==1){
				SemesterPanel semester = new SemesterPanel(fresh, spring);
				semester.setBackground(Color.green);
				semester.setPreferredSize(new Dimension(500, 0));
				scrollPanel.add(semester);
				singleSemesterList.add(semester);

			}
			if (i==2){
				SemesterPanel semester = new SemesterPanel(fresh, MayXSummer);
				semester.setPreferredSize(new Dimension(500, 0));
				semester.setBackground(Color.green);
				scrollPanel.add(semester);
				singleSemesterList.add(semester);

			}
			if (i==3){
				SemesterPanel semester = new SemesterPanel(soph, fall);
				semester.setPreferredSize(new Dimension(500, 0));
				semester.setBackground(Color.blue);
				scrollPanel.add(semester);
				singleSemesterList.add(semester);

			}
			if (i==4){
				SemesterPanel semester = new SemesterPanel(soph, spring);
				semester.setPreferredSize(new Dimension(500, 0));
				semester.setBackground(Color.blue);
				scrollPanel.add(semester);
				singleSemesterList.add(semester);

			}
			if (i==5){
				SemesterPanel semester = new SemesterPanel(soph, MayXSummer);
				semester.setPreferredSize(new Dimension(500, 0));
				semester.setBackground(Color.blue);
				scrollPanel.add(semester);
				singleSemesterList.add(semester);

			}
			if (i==6){
				SemesterPanel semester = new SemesterPanel(jun, fall);
				semester.setPreferredSize(new Dimension(500, 0));
				semester.setBackground(Color.green);
				scrollPanel.add(semester);
				singleSemesterList.add(semester);

			}
			if (i==7){
				SemesterPanel semester = new SemesterPanel(jun, spring);
				semester.setPreferredSize(new Dimension(500, 0));
				semester.setBackground(Color.green);
				scrollPanel.add(semester);
				singleSemesterList.add(semester);

			}
			if (i==8){
				SemesterPanel semester = new SemesterPanel(jun, MayXSummer);
				semester.setPreferredSize(new Dimension(500, 0));
				semester.setBackground(Color.green);
				scrollPanel.add(semester);
				singleSemesterList.add(semester);

			}
			if (i==9){
				SemesterPanel semester = new SemesterPanel(sen, fall);
				semester.setPreferredSize(new Dimension(500, 0));
				semester.setBackground(Color.blue);
				scrollPanel.add(semester);
				singleSemesterList.add(semester);

			}
			if (i==10){
				SemesterPanel semester = new SemesterPanel(sen, spring);
				semester.setPreferredSize(new Dimension(500, 0));
				semester.setBackground(Color.blue);
				scrollPanel.add(semester);
				singleSemesterList.add(semester);

			}
			if (i==11){
				SemesterPanel semester = new SemesterPanel(sen, MayXSummer);
				semester.setPreferredSize(new Dimension(500, 0));
				semester.setBackground(Color.blue);
				scrollPanel.add(semester);
				singleSemesterList.add(semester);


			}	



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
		
		
		scrollPanel.setLayout(new GridLayout(1, numberOfRegularSemesters+buttonPress+1, spaceConstant, spaceConstant));
		scrollPanel.remove(addButtonPanel);
	
		scrollPanel.remove(addExtraSemesterButtonPanel);
		

		if (buttonPress%3==0){
			String extraSemester = "Fall";
			SemesterPanel semester = new SemesterPanel("Super Senior", extraSemester);
			semester.setPreferredSize(new Dimension(500, 0));
			semester.setBackground(Color.pink);
			scrollPanel.add(semester);
			singleSemesterList.add(semester);

		}
		if (buttonPress%3==1){
			String extraSemester = "Spring";
			SemesterPanel semester = new SemesterPanel("Super Senior", extraSemester);
			semester.setPreferredSize(new Dimension(500, 0));
			semester.setBackground(Color.pink);
			scrollPanel.add(semester);
			singleSemesterList.add(semester);

		}

		if (buttonPress%3==2){
			String extraSemester = "MayX and Summer";
			SemesterPanel semester = new SemesterPanel("Super Senior", extraSemester);
			semester.setPreferredSize(new Dimension(500, 0));
			semester.setBackground(Color.pink);
			scrollPanel.add(semester);
			singleSemesterList.add(semester);


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
		
		
		//Shows Semester being added to list, this means the problem is with updating GUI
		System.out.println(singleSemesterList.size());
	}

}




