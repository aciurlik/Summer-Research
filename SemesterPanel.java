import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SemesterPanel extends JPanel implements ActionListener{



	private int classCounter = 0;
	private int requirementNumber=0;
	private int columnNumber = 9; //This classTitle, semesterTitle, 6 classes, button
	private int normalNumberofClasses = 4;
	public int seasonFontSize = 16;
	public int dropLabelFontSize = 12;
	private String addAClass = "Drop a requirement here";
	private String classTitle;
	JPanel defaultPanel = new JPanel();
	JPanel hidePanel;
	Driver d;
	Semester sem;
	private JButton changeCourse;

	public int preferredHeight = 300;



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
		showSemester.setFont(FurmanOfficial.getFont(12));
		showSemester.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){

				show();

			}
		}
				);

		hidePanel.add(showSemester);

		this.setLayout(new GridLayout(1, 1, 0, 0));
		this.setPreferredSize(new Dimension(300,500));
		this.add(defaultPanel);
		this.updatePanel();
	}

	public void show(){
		remove(hidePanel);
		add(defaultPanel);
		localRepaint();
	}
	public void hide(){
		add(hidePanel);
		remove(defaultPanel);
		localRepaint();
	}
	public void localRepaint(){
		revalidate();
		repaint();
		this.d.schP.revalidate();
		this.d.schP.repaint();
	}

	@Override
	public void setBackground(Color c){
		super.setBackground(c);
		if(defaultPanel != null){

			defaultPanel.setBackground(c);
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
		if(e.getActionCommand().equals(MenuOptions.removeInstruct)){
			d.GUIRemoveSemester(this);
		}
		else if(e.getActionCommand().equals(MenuOptions.changeInstruct) || e.getActionCommand().equals(MenuOptions.addInstruct)){
			d.addCourseDialogBox(e.getActionCommand(), this.sem);
		}
		else if(e.getActionCommand().equals(MenuOptions.supriseMe)){
			d.GUISupriseWindow(this.sem);
		}



		else{
			hide();
		}

	}

	public Color semesterColor(Semester s){
		int ColorNum = 0;
		if(s.getDate().sNumber<SemesterDate.FALL){
			ColorNum = s.getDate().year;
		}
		else{
			ColorNum = s.getDate().year-1;
		}
		if(ColorNum%2==0){
			return FurmanOfficial.grey(50);

		}
		else{
			return FurmanOfficial.lightPurple(50);
		}
	}

	@Override 
	public Dimension getPreferredSize(){
		int minWidth = 0;
		for(Component c : this.getComponents()){
			int newWidth = c.getPreferredSize().width;
			if(newWidth > minWidth){
				minWidth = newWidth;
			}
		}
		return new Dimension(minWidth, preferredHeight);
	}


	//Redraw this panel based on the semester sem.
	public void updatePanel(){

		defaultPanel.removeAll();
		defaultPanel.setBackground(this.semesterColor(this.sem));

		//Figure out the season and add it

		SemesterDate d = sem.getDate();
		String season = d.getSeason(d.sNumber);


		if(season == null){
			season="Error";
		}

		season +=  " " + sem.semesterDate.year;
		JLabel FallSpring = new JLabel(season, JLabel.CENTER);
		FallSpring.setFont(FurmanOfficial.getFont(seasonFontSize));
		defaultPanel.add(FallSpring);

		//Add all Schedule elements
		for (ScheduleElement e : this.sem.elements){
			ScheduleElementPanel element = new ScheduleElementPanel(e, this);
			defaultPanel.add(element);
			element.updatePanel();
		}
		//Adds Drop Spaces 
		if(sem.elements.size()==4){
			JLabel dropLabel = newDropLabel();
			defaultPanel.add(dropLabel);
		}

		if(sem.semesterDate.sNumber==SemesterDate.SUMMERONE || sem.semesterDate.sNumber==SemesterDate.SUMMERTWO){
			normalNumberofClasses = 2;
		}

		int DropsNeeded = (normalNumberofClasses - sem.elements.size());
		if(sem.semesterDate.sNumber != SemesterDate.MAYX ){

			for (int i= 0; i<DropsNeeded; i++){
				JLabel dropLabel = newDropLabel();
				defaultPanel.add(dropLabel);
			}
			if(sem.semesterDate.sNumber != SemesterDate.SUMMERONE && sem.semesterDate.sNumber != SemesterDate.SUMMERTWO){
				//Add button to hide Semester
				JButton deleteSemester= new JButton("Hide Semester");
				defaultPanel.add(deleteSemester);
				deleteSemester.addActionListener(this);
			}

		}

		//Adds special buttons to MayX 
		if(sem.semesterDate.sNumber == SemesterDate.MAYX || sem.semesterDate.sNumber == SemesterDate.SUMMERONE || sem.semesterDate.sNumber==SemesterDate.SUMMERTWO){
			JButton removeCourse = new JButton(MenuOptions.removeInstruct);
			removeCourse.setActionCommand(MenuOptions.removeInstruct);
			removeCourse.addActionListener(this);

			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new FlowLayout());
			buttonPanel.setBackground(defaultPanel.getBackground());

			if(sem.semesterDate.sNumber == SemesterDate.MAYX){
				changeCourse = makeAddChangeButton(MenuOptions.changeInstruct);
			}
			if(sem.semesterDate.sNumber == SemesterDate.SUMMERONE || sem.semesterDate.sNumber == SemesterDate.SUMMERTWO){
				changeCourse = makeAddChangeButton(MenuOptions.addInstruct);
			}

			JButton supriseMe = new JButton(MenuOptions.supriseMe);
			supriseMe.setActionCommand(MenuOptions.supriseMe);
			supriseMe.addActionListener(this);

			buttonPanel.add(supriseMe);
			buttonPanel.add(changeCourse);
			buttonPanel.add(removeCourse);

			defaultPanel.add(buttonPanel);

		}



	}

	public JLabel newDropLabel(){
		JLabel dropLabel = new JLabel(addAClass);
		dropLabel.setTransferHandler(new SemesterPanelDropHandler());
		dropLabel.setFont(FurmanOfficial.getFont(dropLabelFontSize));
		return dropLabel;
	}



	public JButton makeAddChangeButton(String s){
		JButton alter = new JButton(s);
		alter.setActionCommand(s);
		alter.addActionListener(this);

		return alter;
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



