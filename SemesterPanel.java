import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.Icon;
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
	JPanel topPanel;
	JPanel hidePanel;
	JLabel fallSpring;
	JButton deleteSemesterButton;
	Driver d;
	Semester sem;
	private JButton changeCourse;
	final static int height=300;

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
		this.hidePanel.setBackground(FurmanOfficial.grey(50));
		JButton showSemester = new JButton(MenuOptions.showSemester);
		showSemester.setActionCommand(MenuOptions.showSemester);
		showSemester.setPreferredSize(new Dimension(15, 15));
		showSemester.setFont(FurmanOfficial.normalFont);
		showSemester.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){

				show();

			}
		}
				);

		hidePanel.add(showSemester);


		fallSpring = new JLabel("", JLabel.CENTER);
		deleteSemesterButton = new JButton(MenuOptions.deleteSemester);
		deleteSemesterButton.setActionCommand(MenuOptions.deleteSemester);
		deleteSemesterButton.addActionListener(this);
		deleteSemesterButton.setEnabled(false);

		JButton hideSem = new JButton(MenuOptions.hideSemester);
		hideSem.setPreferredSize(new Dimension(15,15));
		hideSem.addActionListener(this);
		deleteSemesterButton.setPreferredSize(new Dimension(15, 15));


		JPanel PanelforButtons = new JPanel();
		PanelforButtons.setBackground(defaultPanel.getBackground());
		PanelforButtons.setOpaque(false);

		PanelforButtons.add(deleteSemesterButton);
		PanelforButtons.add(hideSem);

		JLabel FallSpring = new JLabel();
		FallSpring.setFont(FurmanOfficial.bigHeaderFont);

		topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		topPanel.add(PanelforButtons, BorderLayout.WEST);

		topPanel.setBackground(defaultPanel.getBackground());
		topPanel.add(fallSpring, BorderLayout.CENTER);

		this.setLayout(new GridLayout(1, 1, 0, 0));
		this.setPreferredSize(new Dimension(300,height));
		this.add(defaultPanel);
		this.updatePanel();
	}

	public void show(){
		remove(hidePanel);
		add(defaultPanel);
		localRepaint();
	}
	public void hide(){
		remove(defaultPanel);
		add(hidePanel);
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
			if(topPanel != null){
				topPanel.setBackground(c);
			}
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
		if(e.getActionCommand().equals(MenuOptions.deleteSemester)){
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
		fallSpring.setText(season);

		topPanel.setBackground(defaultPanel.getBackground());

		defaultPanel.add(topPanel, BorderLayout.CENTER);
		//Add all Schedule elements
		for (ScheduleElement e : this.sem.elements){
			ScheduleElementPanel element = new ScheduleElementPanel(e, this);
			defaultPanel.add(element);
			element.updatePanel();
		}
		//Adds Drop Spaces 
		/**
		 * 
		 * if(sem.elements.size()==4){
			JLabel dropLabel = newDropLabel();
			defaultPanel.add(dropLabel);
		}
		 */

		if(sem.semesterDate.sNumber==SemesterDate.SUMMERONE || sem.semesterDate.sNumber==SemesterDate.SUMMERTWO){
			normalNumberofClasses = 2;
		}

		int DropsNeeded = (normalNumberofClasses - sem.elements.size());
		if(sem.semesterDate.sNumber != SemesterDate.MAYX ){
			JLabel dropLabel = newDropLabel();
			defaultPanel.add(dropLabel);
			for (int i= 0; i<DropsNeeded-1; i++){
				JLabel emptyLabel = new JLabel();
				defaultPanel.add(emptyLabel);
			}



		}

		//Adds special buttons to MayX 
		//	if(sem.semesterDate.sNumber == SemesterDate.MAYX || sem.semesterDate.sNumber == SemesterDate.SUMMERONE || sem.semesterDate.sNumber==SemesterDate.SUMMERTWO){
		deleteSemesterButton.setEnabled(true);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(2,1,5,5));
		buttonPanel.setBackground(defaultPanel.getBackground());

		if(sem.semesterDate.sNumber == SemesterDate.MAYX){
			changeCourse = makeAddChangeButton(MenuOptions.changeInstruct);
		}

		if(sem.semesterDate.sNumber == SemesterDate.SUMMERONE || sem.semesterDate.sNumber == SemesterDate.SUMMERTWO || sem.semesterDate.sNumber == SemesterDate.FALL 
				|| sem.semesterDate.sNumber == SemesterDate.SPRING){
			changeCourse = makeAddChangeButton(MenuOptions.addInstruct);
		}
		JButton supriseMe = new JButton(MenuOptions.supriseMe);
		if(sem.schedule.masterList.getCoursesIn(sem).size()==0){
			supriseMe.setEnabled(false);
			changeCourse.setEnabled(false);
		}
		supriseMe.setActionCommand(MenuOptions.supriseMe);
		supriseMe.addActionListener(this);

		//	buttonPanel.add(supriseMe);
		//buttonPanel.add(changeCourse);
		defaultPanel.add(supriseMe);
		defaultPanel.add(changeCourse);

		//	defaultPanel.add(buttonPanel);

		//	}


	}

	public JLabel newDropLabel(){
		JLabel dropLabel = new JLabel(addAClass);
		dropLabel.setTransferHandler(new SemesterPanelDropHandler());
		dropLabel.setFont(FurmanOfficial.normalFont);
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


	public void dragStarted(ScheduleElement e){
		if(this.canTake(e)){
			this.setHilighted(true);
		}
	}
	public void dragEnded(){
		this.setHilighted(false);
	}

	public void setHilighted(boolean b){
		if(b){
			this.setBackground(FurmanOfficial.bouzarthDarkPurple);
		}
		else{
			this.setBackground(semesterColor(this.sem));
		}
	}
	/**
	 * This method is used to determine whether this panel should
	 * hilight itself while the user is dragging e.
	 * @param e
	 * @return
	 */
	public boolean canTake(ScheduleElement e){
		//Turn this into a requirement.
		Requirement r;
		if(e instanceof Requirement){
			r = (Requirement) e;
		}
		else if (e instanceof Course){
			r = new TerminalRequirement(e.getPrefix());
		}
		else{
			r = null;
		}
		if(r == null){
			return false;
		}
		//Figure out if this semester has an instance of this requirement.
		else{
			return this.canTake(r);
		}
	}
	public boolean canTake(Requirement r){
		return !this.sem.getCoursesSatisfying(r).isEmpty();
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


		@Override
		public boolean canImport(Component c) {
			if(c instanceof RequirementPanel){
				return true;
			}
			else if(c instanceof ScheduleElementPanel){
				return true;
			}
			return false;
		}
	}



}



