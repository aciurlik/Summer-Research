package scheduler;


/*
 * 
 * https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/uiswing/examples/layout/GridBagLayoutDemoProject/src/layout/GridBagLayoutDemo.java
 * https://docs.oracle.com/javase/tutorial/uiswing/layout/gridbag.html
 * 
 
package scheduler;
 
/*
 * GridBagLayoutDemo.java requires no other files.
 */
 
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


public class SchedulePanel implements ActionListener{
	JPanel singleSemester = new JPanel(); 
	
	public static void main(String[] args){
	//Frame that covers the whole of the screen
	JFrame scheduleFrame= new JFrame();
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	scheduleFrame.setSize(screenSize);

	//Schedule Panel that is added to the Frame
	JPanel schedulePanel = new JPanel();
	schedulePanel.setVisible(true);
	scheduleFrame.add(schedulePanel);
    //Set Layout for semester Panel onto schedule Panel
	schedulePanel.setLayout(new GridBagLayout());
	GridBagConstraints c = new GridBagConstraints();
	//Creates and "formats" semesterPanel
	JPanel semesterPanel = new JPanel();
	semesterPanel.setBackground(Color.yellow);
    semesterPanel.setVisible(true);
    c.anchor=GridBagConstraints.NORTHEAST;//In theory this is supposed to anchor it at the top right corner
    c.ipady=(int)(screenSize.getHeight() * .45);//Approximate percentage of the height we want to take
    c.ipadx=(int)(screenSize.getWidth() * .8);//Approx. % of the width of screen 
    schedulePanel.add(semesterPanel,c);
    //Creates the 12 "Semester" Panels and adds them to the schedule Panel
    semesterPanel.setLayout(new GridLayout(1,12, 5, 5));
    int singSemesterWidth= (int)(screenSize.getWidth() * .33);
    
    for(int i=0; i<12; i++){
    JPanel singleSemester = new JPanel(); 
    Dimension singSemesterSize = new Dimension(singSemesterWidth, 0); //I think formatting took care the height automatically
    singleSemester.setPreferredSize(singSemesterSize);
    singleSemester.setLayout(new GridLayout(7, 1, 5, 10));
    singleSemester.setBackground(Color.green);
    semesterPanel.add(singleSemester);
  
    //Adds What class you're in
    if(i < 3){
    	JLabel freshman = new JLabel("Freshman");
    	singleSemester.add(freshman);
    	
    }
    if (i>2 && i<6){
    	JLabel sophomore = new JLabel("Sophomore");
    	singleSemester.add(sophomore);
    	
    }
    if (i>5 && i<9){
    	JLabel junior = new JLabel("Junior");
    	singleSemester.add(junior);
    	
    }
    if (i>8 && i<12){
    	JLabel senior = new JLabel("Senior");
    	singleSemester.add(senior);
    	
    }
    //Adds the Semester
    if(i%3==0){
    	JLabel fallSemester = new JLabel("Fall Semester");
    	fallSemester.setHorizontalAlignment(JLabel.CENTER);
    	singleSemester.add(fallSemester);
    	
 
    }
    if(i%3==1){
    	JLabel springSemester = new JLabel("Spring Semester");
    	springSemester.setHorizontalAlignment(JLabel.CENTER);
    	singleSemester.add(springSemester);
    	
    }
    if(i%3==2){
    	JLabel MayXSummer = new JLabel  ("May X and Summer Sessions");
    	MayXSummer.setHorizontalAlignment(JLabel.CENTER);
    	singleSemester.add(MayXSummer);
   
    }
    
    for(int p=0; p<4; p++){
    	JLabel newClass = new JLabel("Add Class Here: __________________________________________________");
    	newClass.setHorizontalAlignment(JLabel.CENTER);
    	singleSemester.add(newClass);
    }
   // JLabel OverloadClass = new JLabel("Overload Class_____________________________________________________");
  //  OverloadClass.setHorizontalAlignment(JLabel.CENTER);
   // singleSemester.add(OverloadClass);
    JButton addButton = new JButton("+");
    singleSemester.add(addButton);
    addButton.addActionListener(new SchedulePanel());
    
    }
    JScrollPane scrollPane = new JScrollPane(semesterPanel);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
    int scrollHeight= (int)(screenSize.getHeight() * .40);
    int scrollWidth= (int)(screenSize.getWidth() * .75);
    scrollPane.setBounds(40, 30, scrollWidth, scrollHeight);
    JPanel contentPane = new JPanel(null);
    contentPane.add(scrollPane);
    scheduleFrame.setContentPane(contentPane);

    

    scheduleFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    scheduleFrame.setVisible(true);
    
    
  
	
	
	

	}
	@Override
	public void actionPerformed(ActionEvent e) {
		
		
	}
}
	

    