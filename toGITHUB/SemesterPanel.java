import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

/**
 * 
 * 
 *  Blurb written: 7/28/2017
 *  Last updated: 7/28/2017
 *  
 * This class is the panel that represents a semester, which (in the GUI) is the thing that 
 * requirements can be dropped into. It holds extra functionality in the SemestermenuBar class. 
 * It updates based on various attributes of the semester. 
 *
 */
public class SemesterPanel extends JPanel implements ActionListener, DocumentListener, MouseListener{

	
	
	

	private int defaultNumRows = 9; //The SemesterPanelOptions + the SemesterDate + 7 Classes
	//(Dropping a 7th class will cause the semesterPanel to restructure to two columns. 
	JPanel defaultPanel;
	JPanel PanelforButtons;
	JPanel topPanel;
	JPanel hidePanel;
	JLabel semesterDateLabel;
	JButton deleteSemesterButton;
	ScheduleGUI schGUI;
	Semester sem;
	final static int width = 300; 
	final static int height=300;
	JTextArea notes; //If a semester contains notes, this is used. 
	JPanel menuPanel;
	final static int NimbusWidth = 40;
	final static int NimbusHeight = 20;



	


	public SemesterPanel(Semester sem, ScheduleGUI d){

		//Sets up the panel that will hold one semester
		super();
		this.sem=sem;
		this.schGUI = d;
		this.addMouseListener(this);
		this.notes  = new JTextArea();


		//Setup the defaultPanel, the panel which is visible whenever this
		// semester is not hidden.
		defaultPanel = new JPanel();
		defaultPanel.setLayout(new GridLayout(defaultNumRows, 1, 5, 5));
		defaultPanel.setTransferHandler(new SemesterPanelDropHandler());



		//Setup the hidePanel, the panel which is visible if this semester is hidden.
		// This panel includes a button to show the semester again.
		this.hidePanel = new JPanel();
		this.hidePanel.setBackground(FurmanOfficial.grey(50));
		JButton showSemester = new JButton(MenuOptions.showSemester);
		showSemester.setToolTipText("Show Semester");
	
		showSemester.setFont(FurmanOfficial.normalFont);
		showSemester.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				show(); //The semester currently hidden.
			}
		}
				);
		hidePanel.add(showSemester);


		semesterDateLabel = new JLabel("", JLabel.CENTER);
		deleteSemesterButton = new JButton(MenuOptions.deleteSemester);
		deleteSemesterButton.setToolTipText("Delete Semester");
		deleteSemesterButton.addActionListener(this);
		deleteSemesterButton.setEnabled(false);


		JButton hideSem = new JButton(MenuOptions.hideSemester);
		hideSem.setToolTipText("Hide Semester");
		hideSem.addActionListener(this);
		
		
		if(MenuOptions.nimbusLoaded){//The button format of Nimbus makes it so the button
			//must be bigger to the text on the button can be seen. 
			showSemester.setPreferredSize(new Dimension(NimbusWidth+2, NimbusHeight));
			showSemester.setMargin(new Insets(1,1,1,1));
			deleteSemesterButton.setPreferredSize(new Dimension(NimbusWidth, NimbusHeight));
			deleteSemesterButton.setMargin(new Insets(1,1,1,1));
			hideSem.setPreferredSize(new Dimension(NimbusWidth, NimbusHeight));
			hideSem.setMargin(new Insets(1,1,1,1));
		}

		PanelforButtons = new JPanel();
		PanelforButtons.setBackground(defaultPanel.getBackground());
		PanelforButtons.setOpaque(false);

		PanelforButtons.add(deleteSemesterButton);
		PanelforButtons.add(hideSem);

		topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		topPanel.add(PanelforButtons, BorderLayout.WEST);
		topPanel.setOpaque(false);
		topPanel.setBackground(defaultPanel.getBackground());
		

		menuPanel = new JPanel();
		menuPanel.setOpaque(false);
		menuPanel.setLayout(new BorderLayout());
		topPanel.add(menuPanel, BorderLayout.EAST);

		this.setLayout(new GridLayout(1, 1, 0, 0)); //This keeps the SemesterPanel from sliding under 
		//the MajorListPanel. 
		this.setPreferredSize(new Dimension(width,height));
		this.add(defaultPanel);
		this.updatePanel(sem);
	}

	/**
	 * This displays the semesterPanel back
	 * in its original form. The size changes 
	 * back to display all the classes. 
	 */
	public void show(){
		remove(hidePanel);
		add(defaultPanel);
		localRepaint();
	}
	
	/**
	 * This makes the SemesterPanel into
	 * a sliver, does not display any information. 
	 */
	public void hide(){
		remove(defaultPanel);
		add(hidePanel);
		localRepaint();
	}
	
	/**
	 * Makes it so any changes to/in semseterPanel
	 * only have to be made to the SchedulePanel. 
	 */
	public void localRepaint(){
		revalidate();
		repaint();
		this.schGUI.schP.revalidate(); 
		this.schGUI.schP.repaint();//Repaints the whole schedule panel
	}

	/**
	 * Keeps the coloring consistent in this panel
	 */
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



	/**
	 * Used by scheduleElement to get its container's semester. 
	 * @return The semester associated with this Panel. 
	 */
	public Semester getSemester(){
		return sem;
	}


	/**
	 * This will hide, and delete the semester 
	 * for the user. The hide command can be 
	 * handled internally because it does not change 
	 * the data (the semester still exists), however
	 * delete is performed by the schGUI because
	 * the semester is removed from the data. So if the 
	 * semester was added back in through adding semesters
	 * at the end of schedulePanel, all the data
	 * associated with the deleted semesterPanel (Like courses
	 * scheduled in it) would be lost. 
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals(MenuOptions.deleteSemester)){
			schGUI.removeSemester(this);
		}
		else if (command.equals(MenuOptions.hideSemester)){
			hide();
		}
	}

	/**
	 * 
	 * @param s that this Panel is displaying. 
	 * @return Color associated with type of semester. 
	 */
	public Color semesterColor(Semester s){
		if(s.studyAway){
			return FurmanOfficial.officialGrey;
		}
		if(s.isTaken()){
			return FurmanOfficial.grey(170); //Lighter gray
		}
		//This creates the alternate light gray, and purple coloring for
		//normal semesters. 
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
	
	/**
	 * Used by Swing classes, sets the width of Semester panel
	 * equal to the width of the things inside of it. This is how the
	 * semester resizes based on what it contains. 
	 */

	@Override 
	public Dimension getPreferredSize(){
		int minWidth = 0;
		for(Component c : this.getComponents()){
			int newWidth = c.getPreferredSize().width;
			if(newWidth > minWidth){
				minWidth = newWidth;
			}
		}
		return new Dimension(minWidth, height);
	}


	/**
	 * Redraws the semesterPanel, based on the data of the semester. 
	 * @param sem
	 */
	public void updatePanel(Semester sem){

		this.sem = sem;
		menuPanel.removeAll();
		defaultPanel.removeAll();
		defaultPanel.setBackground(this.semesterColor(this.sem));
		topPanel.setBackground(defaultPanel.getBackground());

		//Figure out the season and add it
		SemesterDate d = sem.getDate();
		String season = d.getUserString();
		
		//This is a label that is given at the top 
		//of the Panel. 
		semesterDateLabel.setText(season);
		semesterDateLabel.setForeground(Color.black);
	
		defaultPanel.add(topPanel, BorderLayout.CENTER);
		//Add all Schedule elements
		defaultPanel.add(semesterDateLabel);

		//Semester Option MenuBar
		SemesterMenuBar menu = new SemesterMenuBar(this);
		menuPanel.add(menu, BorderLayout.NORTH);

		//This adds the elements in the Semester. 
		for (ScheduleElement e : this.sem.elements){
			ScheduleElementPanel element = new ScheduleElementPanel(e, this);
			defaultPanel.add(element);
			element.updatePanel();
		}

		JLabel dropLabel = newDropLabel(); //Drop a Requirement Here
		if(sem.semesterDate.sNumber != SemesterDate.MAYX && !sem.isTaken()){
			defaultPanel.add(dropLabel); //No label for Taken & May 
		
		}
		if(sem.studyAway || sem.isTaken()){
			dropLabel.setForeground(Color.white);
			semesterDateLabel.setForeground(Color.white);
			//Change label to tell its a Study Away
			if(sem.studyAway){
				semesterDateLabel.setText("Study Away " + SemesterDate.getSeason(sem.semesterDate.sNumber)+ " "+ sem.semesterDate.year);
			}
		}
		
		// Puts this at the bottom of the semesterPanel
		if(sem.hasNotes()){
			notes.setText(sem.notes);
			JScrollPane scrollPane = new JScrollPane(notes); 
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			defaultPanel.add(scrollPane);
			notes.getDocument().addDocumentListener(this);
		}
		topPanel.setBackground(this.semesterColor(this.sem));
		defaultPanel.setBackground(this.semesterColor(this.sem));

		//This adjusts the title, and takes away the optionsMenu
		if(this.sem.isPriorSemester){
			semesterDateLabel.setText("Prior Courses");
			topPanel.remove(menuPanel);
		}
		if(sem.semesterDate.sNumber == SemesterDate.MAYX || sem.semesterDate.sNumber == SemesterDate.SUMMERONE || sem.semesterDate.sNumber==SemesterDate.SUMMERTWO ){
			deleteSemesterButton.setEnabled(true);
		}
		else{
			deleteSemesterButton.setEnabled(false);
		}
	}
	

	/**
	 * This is allows a SemesterPanel to be deleted. 
	 * Only the last one of the set can be deleted, and 
	 * you have to have at least one semester, so the first
	 * one is never set to deletable. 
	 * @param canBeDeleted
	 */
	public void setDeletable(boolean canBeDeleted){
		deleteSemesterButton.setEnabled(canBeDeleted);
	}


	/**
	 * This creates a special label 
	 * @return
	 */
	public JLabel newDropLabel(){
		String addAClass = "Drop a requirement here";
		JLabel dropLabel = new JLabel(addAClass);
		dropLabel.setTransferHandler(new SemesterPanelDropHandler());
		dropLabel.setFont(FurmanOfficial.normalFont);
		return dropLabel;
	}
	

	/**
	 * If the user starts dragging element e,
	 * you may need to highlight if you can recieve
	 * e as a drop.
	 * @param e
	 */
	public void dragStarted(ScheduleElement e){
		if(this.canTake(e)){
			this.setHilighted(true);
		}
	}
	/**
	 * When a drag ends, any semester who was highlighted
	 * should stop highlighting.
	 */
	public void dragEnded(){
		this.setHilighted(false);
	}


	/**
	 * This is probably unused method. 
	 * @param s
	 * @return
	 */
//	public JButton makeAddChangeButton(String s){
	//	JButton alter = new JButton(s);
	//	alter.setActionCommand(s);
	//	alter.addActionListener(this);
	///	return alter;
//	}

	
	
	
	/**
	 * This lets the scheduleElement be added to a semesterPanel
	 * @param s
	 */
	public void addElement(Component s){
		//This makes it so that a requirement panel cannot be dropped into a taken Semester
		if(this.sem.isTaken()){
			throw new RuntimeException("This exception is thrown so that "
					+ "\nan element dragged into a taken semester will not"
					+ "\nbe accepted.");
		}
		if(s instanceof RequirementPanel){
			RequirementPanel r = (RequirementPanel) s;
			schGUI.requirementPanelDropped(r, this);
		}
		else{
			ScheduleElementPanel p = (ScheduleElementPanel) s;
			schGUI.scheduleElementPanelDropped(p, this);
		}
	}
	
	/**
	 * calls schGUI to remove this elements from the schedule. 
	 * @param e
	 */
	public void removeElement(ScheduleElementPanel e){
		schGUI.removeElement(e, this);
	}

	/**
	 * Change the color of this semesterPanel based on whether 
	 * we want to highlight it or not.
	 * @param b
	 */
	public void setHilighted(boolean b){
		if(b){
			this.setBackground(FurmanOfficial.darkPurple);
		}
		else{
			this.setBackground(semesterColor(this.sem));
		}
	}
	
	/**
	 * This evaluates if this scheduleElement can be put in this semester. For highlighting purposes
	 * @param e
	 * @return true, if this scheduleElement can be taken in this semester, false if there
	 * is no such requirement in that semester.
	 */
	public boolean canTake(ScheduleElement e){
		if(this.sem.isTaken()){
			return false;
		}
		//Turn this into a requirement.
		Requirement r;
		if(e instanceof Requirement){
			r = (Requirement) e;
		}
		else if (e instanceof  ScheduleCourse){
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
	
	/**
	 * This evaluates if this requirement can be put in this semester. For highlighting purposes. 
	 * @param r
	 * @return true, if this requirement can be taken in this semester, false if there
	 * is no such requirement in that semester. 
	 */
	public boolean canTake(Requirement r){
		ArrayList<Course> coursesSatisfying = CourseList.getCoursesSatisfying(
				CourseList.inSemester(this.sem)
				.and(CourseList.satisfiesRequirement(r))
				.and( CourseList.inSchedule(this.sem.schedule).negate() )
				);
		return !coursesSatisfying.isEmpty();
	}

	/**
	 * This allows the Semester Panel to receive ScheduleElements. 
	 * it adds the element depended on its type. 
	 */
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

	/**
	 * These three update a Semester's notes as they 
	 * are written by the user. 
	 */
	@Override
	public void insertUpdate(DocumentEvent e) {
		this.schGUI.saveTextToSemesterNotes(e, sem);	
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		this.schGUI.saveTextToSemesterNotes(e, sem);
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		this.schGUI.saveTextToSemesterNotes(e, sem);
	}
	
	
	/**
	 * This sets the focus of the Semester 
	 * when clicked (so that the focus does 
	 * not stay on the notes when they are created. 
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		this.requestFocusInWindow();
	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}
}




