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
 * This class is the panel that represents a semester, which (in the GUI) is the thing that 
 * requirements can be dropped into
 *
 */
public class SemesterPanel extends JPanel implements ActionListener, DocumentListener, MouseListener, java.io.Serializable{

	private static final long serialVersionUID = 1L;
	
	private int classCounter; //counts the number of classes in this semester
	// so that the layouts work well.
	private int defaultNumRows = 9; //This classTitle, semesterTitle, 6 classes, button
	private int normalNumberofClasses = 4;
	private String addAClass = "Drop a requirement here";
	JPanel defaultPanel = new JPanel();
	JPanel PanelforButtons;
	JPanel topPanel;
	JPanel hidePanel;
	JLabel fallSpring;
	JButton deleteSemesterButton;
	ScheduleGUI schGUI;
	Semester sem;
	final static int height=300;
	JTextArea notes;
	JPanel menuPanel;
	int NimbusWidth = 40;
	int NimbusHeight = 20;



	public int preferredHeight = 300;



	public SemesterPanel(Semester sem, ScheduleGUI d){

		//Sets up the panel that will hold one semester
		super();
		this.sem=sem;
		this.schGUI = d;

		this.addMouseListener(this);
		
		classCounter = 0;

		this.notes  = new JTextArea();


		//Setup the defaultPanel, the panel which is visible whenever this
		// semester is not hidden.
		defaultPanel.setLayout(new GridLayout(defaultNumRows, 1, 5, 5));
		defaultPanel.setTransferHandler(new SemesterPanelDropHandler());



		//Setup the hidePanel, the panel which is visbile if this semester is hidden.
		// This panel includes a button to show the semester again.
		this.hidePanel = new JPanel();
		this.hidePanel.setBackground(FurmanOfficial.grey(50));
		JButton showSemester = new JButton(MenuOptions.showSemester);
		showSemester.setActionCommand(MenuOptions.showSemester);
		showSemester.setToolTipText("Show Semester");
		if(MenuOptions.nimbusLoaded){
			showSemester.setPreferredSize(new Dimension(NimbusWidth+2, NimbusHeight));
			showSemester.setMargin(new Insets(1,1,1,1));
		}

		showSemester.setFont(FurmanOfficial.normalFont);
		showSemester.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){

				show();

			}
		}
				);

		hidePanel.setOpaque(false);
		hidePanel.add(showSemester);


		fallSpring = new JLabel("", JLabel.CENTER);
		deleteSemesterButton = new JButton(MenuOptions.deleteSemester);
		deleteSemesterButton.setToolTipText("Delete Semester");
		deleteSemesterButton.setActionCommand(MenuOptions.deleteSemester);
		deleteSemesterButton.addActionListener(this);
		deleteSemesterButton.setEnabled(false);


		JButton hideSem = new JButton(MenuOptions.hideSemester);
		hideSem.setToolTipText("Hide Semester");
		hideSem.setPreferredSize(new Dimension(15,15));
		hideSem.addActionListener(this);
		if(MenuOptions.nimbusLoaded){
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

		JLabel FallSpring = new JLabel();
		FallSpring.setFont(FurmanOfficial.bigHeaderFont);

		topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		topPanel.add(PanelforButtons, BorderLayout.WEST);
		topPanel.setOpaque(false);
		topPanel.setBackground(defaultPanel.getBackground());
		//	topPanel.add(fallSpring, BorderLayout.CENTER);

		menuPanel = new JPanel();
		menuPanel.setOpaque(false);
		menuPanel.setLayout(new BorderLayout());
		topPanel.add(menuPanel, BorderLayout.EAST);

		this.setLayout(new GridLayout(1, 1, 0, 0));
		this.setPreferredSize(new Dimension(300,height));
		this.add(defaultPanel);
		this.updatePanel(sem);
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
		this.schGUI.schP.revalidate();
		this.schGUI.schP.repaint();
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




	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(MenuOptions.deleteSemester)){
			schGUI.GUIRemoveSemester(this);
		}
		else{
			hide();
		}

	}

	public Color semesterColor(Semester s){
		if(s.studyAway){
			return FurmanOfficial.officialGrey;
		}
		if(s.isTaken()){
			return FurmanOfficial.grey(170);
		}
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

	public void updatePanel(Semester sem){

		this.sem = sem;
		JLabel dropLabel = newDropLabel();
		menuPanel.removeAll();


		defaultPanel.removeAll();
		defaultPanel.setBackground(this.semesterColor(this.sem));



		//Figure out the season and add it

		SemesterDate d = sem.getDate();
		String season = d.getUserString();
		
		
		fallSpring.setText(season);
		fallSpring.setForeground(Color.black);


		topPanel.setBackground(defaultPanel.getBackground());


		defaultPanel.add(topPanel, BorderLayout.CENTER);
		//Add all Schedule elements
		defaultPanel.add(fallSpring);


		SemesterMenuBar menu = new SemesterMenuBar(this);
		menuPanel.add(menu, BorderLayout.NORTH);



		for (ScheduleElement e : this.sem.elements){
			ScheduleElementPanel element = new ScheduleElementPanel(e, this);
			defaultPanel.add(element);
			element.updatePanel();
		}
		if(sem.semesterDate.sNumber==SemesterDate.SUMMERONE || sem.semesterDate.sNumber==SemesterDate.SUMMERTWO){
			normalNumberofClasses = 2;
		}
		
		

		int DropsNeeded = (normalNumberofClasses - sem.elements.size());

		if(sem.semesterDate.sNumber != SemesterDate.MAYX && !sem.isTaken()){

			defaultPanel.add(dropLabel);
			for (int i= 0; i<DropsNeeded-1; i++){
				JLabel emptyLabel = new JLabel();
				defaultPanel.add(emptyLabel);

			}
		}


		//Adds special buttons to MayX 




		if(sem.studyAway || sem.isTaken()){


			dropLabel.setForeground(Color.white);
			fallSpring.setForeground(Color.white);
			if(sem.studyAway){
				fallSpring.setText("Study Away " + sem.semesterDate.getSeason(sem.semesterDate.sNumber)+ " "+ sem.semesterDate.year);
			}


		}
		if(sem.hasNotes()){
			//defaultPanel.add(notes);
			notes.setText(sem.notes);
			JScrollPane scrollPane = new JScrollPane(notes); 
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			defaultPanel.add(scrollPane);
			notes.getDocument().addDocumentListener(this);
		}
		topPanel.setBackground(this.semesterColor(this.sem));
		defaultPanel.setBackground(this.semesterColor(this.sem));


		if(this.sem.isPriorSemester){
			fallSpring.setText("Prior Courses");
			topPanel.remove(menuPanel);
		}
		if(sem.semesterDate.sNumber == SemesterDate.MAYX || sem.semesterDate.sNumber == SemesterDate.SUMMERONE || sem.semesterDate.sNumber==SemesterDate.SUMMERTWO ){
			deleteSemesterButton.setEnabled(true);
		}

		else{
			deleteSemesterButton.setEnabled(false);
		}
	}

	public void setDeletable(boolean canBeDeleted){
		deleteSemesterButton.setEnabled(canBeDeleted);
	}



	public JLabel newDropLabel(){
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



	public JButton makeAddChangeButton(String s){
		JButton alter = new JButton(s);
		alter.setActionCommand(s);
		alter.addActionListener(this);
		return alter;
	}

	public void addElement(Component s){
		if(this.sem.isTaken()){
			throw new RuntimeException("This exception is thrown so that "
					+ "\nan element dragged into a taken semester will not"
					+ "\nbe accepted.");
		}
		if(s instanceof RequirementPanel){
			RequirementPanel r = (RequirementPanel) s;
			schGUI.GUIRequirementPanelDropped(r, this);
		}
		else{
			ScheduleElementPanel p = (ScheduleElementPanel) s;
			schGUI.GUIScheduleElementPanelDropped(p, this);
		}
	}

	public void removeElement(ScheduleElementPanel e){
		schGUI.GUIRemoveElement(e, this);
		//this.updatePanel(true);
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
	 * This method is used to determine whether this panel should
	 * hilight itself while the user is dragging e.
	 * @param e
	 * @return
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
	public boolean canTake(Requirement r){
		ArrayList<Course> coursesSatisfying = CourseList.getCoursesSatisfying(
				CourseList.inSemester(this.sem)
				.and(CourseList.satisfiesRequirement(r))
				.and( CourseList.inSchedule(this.sem.schedule).negate() )
				);
		ArrayList<ScheduleCourse> scheduleCoursesSatisfying = 
				ScheduleCourse.toScheduleCourses(coursesSatisfying, this.sem.schedule);
		//scheduleCoursesSatisfying.removeAll(sem.elements);
		return !coursesSatisfying.isEmpty();
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

	@Override
	public void insertUpdate(DocumentEvent e) {
		try {
			this.schGUI.GUITextBeingWritten(e, sem);
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}

	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		try {
			this.schGUI.GUITextBeingWritten(e, sem);
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}

	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		try {
			this.schGUI.GUITextBeingWritten(e, sem);
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}


	}

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




