import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SemesterPanel extends JPanel implements ActionListener{
	
	private Semester sem;

	private int classCounter = 0;
	private int requirementNumber=0;
	private int columnNumber = 9; //This classTitle, semesterTitle, 6 classes, button
	private int normalNumberofClasses = 4;
	private String addAClass = "DROP A CLASS A HERE";
	private String classTitle;
	JPanel defaultPanel = new JPanel();
	JPanel hidePanel;
	
	
	




	public SemesterPanel(String classTitle, Semester sem, Color c){

		//Sets up the panel that will hold one semester
		super();
		this.classTitle=classTitle;
		this.sem=sem;
		
		//Setup the defaultPanel, the panel which is visible whenever this
		// semester is not hidden.
		defaultPanel.setLayout(new GridLayout(columnNumber, 1, 5, 5));
		this.setBackground(c); //This allows the schedule Panel to control the color
		defaultPanel.setBackground(this.getBackground());
		defaultPanel.setTransferHandler(new SemesterPanelDropHandler());

		//Setup the hidePanel, the panel which is visbile if this semester is hidden.
		// This panel includes a button to show the semester again.
		this.hidePanel = new JPanel();
		this.hidePanel.setBackground(Color.pink);
		String showText = "Show Semester";
		JButton showSemester = new JButton(showText);
		showSemester.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				remove(hidePanel);
				add(defaultPanel);
				revalidate();
				repaint();
			}
		}
		);

		hidePanel.add(showSemester);
		
		this.setLayout(new GridLayout(1, 1, 0, 0));
		this.add(defaultPanel);
		this.updatePanel();

	}



	public int getClassCounter() {
		return classCounter;
	}


	public int getRequirementNumber() {
		return requirementNumber;
	}



	@Override
	public void actionPerformed(ActionEvent e) {

		//This method is called when you click the button to hide a semester.
		this.remove(defaultPanel);
		this.add(hidePanel);
		this.revalidate();
		this.repaint();

	}
	

	
	
	
	
	
	
	//Redraw this panel based on the semester sem.
	public void updatePanel(){
		defaultPanel.removeAll();
		
		JLabel ClassTitle = new JLabel(classTitle);
		defaultPanel.add(ClassTitle);
		
		String season = sem.getDate().getSeason(sem.getDate().sNumber);
		if(season.equals("MayX") || season.equals("Summer")){
			season="MayX/Summer";
		}
		if(season.equals("Other") || season.equals(null)){
			season="Error";
		}
		
		JLabel FallSpring = new JLabel(season, JLabel.CENTER);
		defaultPanel.add(FallSpring);
				
		for (int i=0; i<this.sem.elements.size(); i++){
			ScheduleElementPanel element = new ScheduleElementPanel(this.sem.elements.get(i));
			defaultPanel.add(element);	
		}
		
		if(sem.elements.size()==4){
			JLabel dropLabel = new JLabel(addAClass);
			dropLabel.setTransferHandler(new SemesterPanelDropHandler());
			defaultPanel.add(dropLabel);
		}
		//Adds Drop Spaces 
		int DropsNeeded = (normalNumberofClasses - sem.elements.size());
		for (int i= 0; i<DropsNeeded; i++){
			JLabel dropLabel = new JLabel(addAClass);
			defaultPanel.add(dropLabel);
			
		
		
		//Repaint 
		defaultPanel.revalidate();
		defaultPanel.repaint();
		this.revalidate();
		this.repaint();
		}
		
		//Add button to hide Semester
		JButton deleteSemester= new JButton("-");
		defaultPanel.add(deleteSemester);
		deleteSemester.addActionListener(this);
	}
	
	
	
	
	
	public void addElement(ScheduleElement e){
		sem.add(e);
		ScheduleElementPanel requirementPanel = new ScheduleElementPanel(e);
		updatePanel();
	}
	
	private class SemesterPanelDropHandler extends PanelDropHandler{

		@Override
		public void recievedDrop(Container receiver, Component draggedItem) {
			try{
				RequirementPanel d =  (RequirementPanel) draggedItem;
				addElement(d.getRequirement());
			}catch(Exception e){
				ScheduleElementPanel p = (ScheduleElementPanel) draggedItem;
				addElement(p.getElement());
			}
		}


	}


}

