import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SemesterPanel extends JPanel implements ActionListener{



	private int classCounter = 0;
	private int requirementNumber=0;
	private int columnNumber = 9; //This classTitle, semesterTitle, 6 classes, button
	private int normalNumberofClasses = 4;
	private String addAClass = "Drop a requirement here";
	private String classTitle;
	JPanel defaultPanel = new JPanel();
	JPanel hidePanel;
	Driver d;
	Semester sem;



	public SemesterPanel(Semester sem, Driver d){

		//Sets up the panel that will hold one semester
		super();
		this.sem=sem;
		this.d = d;


		//Setup the defaultPanel, the panel which is visible whenever this
		// semester is not hidden.
		defaultPanel.setLayout(new GridLayout(columnNumber, 1, 5, 5));
		defaultPanel.setTransferHandler(new SemesterPanelDropHandler());

		//Setup the hidePanel, the panel which is visbile if this semester is hidden.
		// This panel includes a button to show the semester again.
		this.hidePanel = new JPanel();
		this.hidePanel.setBackground(FurmanOfficial.lightPurple(255));
		String showText = "Show Semester";
		JButton showSemester = new JButton(showText);
		showSemester.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){

				remove(hidePanel);
				add(defaultPanel);
				revalidate();
				repaint();
				getParent().getParent().revalidate();
				getParent().getParent().repaint();
			}
		}
				);

		hidePanel.add(showSemester);

		this.setLayout(new GridLayout(1, 1, 0, 0));
		this.setPreferredSize(new Dimension(500,500));
		this.add(defaultPanel);
		this.updatePanel(false);
	}

	@Override
	public void setBackground(Color c){
		super.setBackground(c);
		if(defaultPanel != null){
			defaultPanel.setBackground(this.getBackground());
		}
	}


	public int getClassCounter() {
		return classCounter;
	}

	public Semester getSemester(){
		return sem;
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

		this.getParent().getParent().revalidate();
		this.getParent().getParent().repaint();



	}



	//Redraw this panel based on the semester sem.
	public void updatePanel(boolean repaint){

		defaultPanel.removeAll();


		//Add the classTitle (Freshman, Sophomore)
		JLabel ClassTitle = new JLabel(classTitle);
		defaultPanel.add(ClassTitle);

		//Figure out the season and add it
		SemesterDate d = sem.getDate();
		String season = d.getSeason(d.sNumber);
		if(season.equals("MayX") || season.equals("Summer")){
			season="MayX/Summer";
		}
		if(season.equals("Other") || season.equals(null)){
			season="Error";
		}
		season += "-" + d.year;
		JLabel FallSpring = new JLabel(season, JLabel.CENTER);
		defaultPanel.add(FallSpring);

		//Add all Schedule elements
		for (ScheduleElement e : this.sem.elements){
			ScheduleElementPanel element = new ScheduleElementPanel(e, this);
			defaultPanel.add(element);
			element.updatePanel();
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

		}



		//Add button to hide Semester
		JButton deleteSemester= new JButton("Hide Semester");
		defaultPanel.add(deleteSemester);
		deleteSemester.addActionListener(this);

		//Repaint 
		if(repaint){
			defaultPanel.revalidate();
			defaultPanel.repaint();
			this.getParent().revalidate();
			this.getParent().repaint();
			this.revalidate();
			this.repaint();
		}
	}





	public void addElement(Component s){
		if(s instanceof RequirementPanel){
			RequirementPanel r = (RequirementPanel) s;
			d.GUIRequirementPanelDropped(r, this);
		}
		else{
			ScheduleElementPanel p = (ScheduleElementPanel) s;
			d.GUIScheduleElementPanelDropped(p, this);
		}
	}

	public void removeElement(ScheduleElementPanel e){
		d.GUIRemoveElement(e, this);
		//this.updatePanel(true);
	}

	private class SemesterPanelDropHandler extends PanelDropHandler{

		@Override
		public void recievedDrop(Container receiver, Component draggedItem) {
			if(draggedItem instanceof RequirementPanel){
				RequirementPanel d =  (RequirementPanel) draggedItem;
				addElement(d);
			}
			else{
				ScheduleElementPanel p = (ScheduleElementPanel) draggedItem;
				addElement(p);
			}
			
		}

	}


}



