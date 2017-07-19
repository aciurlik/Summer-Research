import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.ImageView;



public class ScheduleGUI{ 

	/**
	 * This is in charge of handling all of the GUI. It is the only 
	 * aspect of the GUI that sends information to the schedule. 
	 * 
	 */

	Schedule sch;
	SchedulePanel schP;
	RequirementListPanel reqs;

	int season;

	JList<Integer> pickYears;
	String seasonName;
	Integer[] yearsDialog;
	static ImageIcon icon;

	String instructYear = "Please pick a year you would like to add a ";
	String headInstructYear = "Pick a year";
	String instructCourse = "Please pick the course you would like to add to your ";
	String headInstructCourse = "Pick a course";
	String summerOverload = "You need to delete a course before you can add another";
	JFrame frame; 

	Course[] coursesDialog;

	ScheduleElement beingDragged;

	public static int defaultPixelWidth = 300;

	ListOfMajors l;
	BellTower b;
	//PrintWriter pW;







	public ScheduleGUI(Schedule sch) {

		//popUP = new JFrame();
		icon = new ImageIcon(MenuOptions.resourcesFolder + "BellTower(T).png");
		l = FileHandler.getMajorsList();
		
		
		
		this.sch = sch;
		sch.setDriver(this);

		frame = new JFrame();
		//Adds the menu bar
		MainMenuBar menu = new MainMenuBar(this);
		menu.setFont(FurmanOfficial.normalFont);
		frame.setJMenuBar(menu);
		frame.setContentPane(menu.createContentPane());

		JPanel stackPanel = new JPanel();
		stackPanel.setLayout(new BorderLayout());

		
		//Adds Additions Panel and belltower
		AdditionsPanel extras = new AdditionsPanel(this);
		JPanel left = new JPanel();
		left.setBackground(FurmanOfficial.bouzarthGrey);
		BellTower belltowerLabel = new BellTower(sch);
		this.b=belltowerLabel;
		left.add(belltowerLabel);
		left.add(extras);
		stackPanel.add(left, BorderLayout.WEST);

		schP = new SchedulePanel(sch, this);
		//schP.setPreferredSize(new Dimension(500, 500));

		stackPanel.add(schP, BorderLayout.NORTH);
		
		
		
		long one = System.currentTimeMillis();
		reqs = new RequirementListPanel(sch, this);
		long two = System.currentTimeMillis();
		
		
		stackPanel.add(reqs, BorderLayout.CENTER);
		
		
		
		JScrollPane scroll = new JScrollPane();
		scroll.getViewport().add(stackPanel);
		long three = System.currentTimeMillis();
		//scroll.setPreferredSize(new Dimension(stackPanel.getPreferredSize()));
		frame.add(scroll);

		this.update();

		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.pack();
		frame.setVisible(true);
		long four = System.currentTimeMillis();
		/**
		 * 
		 * 	System.out.println("Time intervals");
		System.out.println(two-one);
		System.out.println(three-two);
		System.out.println(four-three);
		 * 
		 */
	
	}


	/**
	 * Create a new, blank schedule to work from.
	 */
	public void GUINewSchedule() {
		//CourseList l = sch.masterList;
		//This creates a Semester with that matches the current schedule Course List and starting Semester Date
		Schedule current = new Schedule();

		//TODO make sure nothing else needs to be set
		setSchedule(current);
		this.update();
	}

	public void setSchedule(Schedule current) {
		sch=current;
		this.sch.setDriver(this);
		this.b.setSchedule(current);
	}


	/**
	 * Ask the user to pick out some of the enemies that will be allowed to
	 * be satisfied by that course.
	 * @param enemies
	 * @param c
	 * @return
	 */
	public HashSet<Requirement> GUIResolveConflictingRequirements(ArrayList<Requirement> enemies, ArrayList<Major> majors, Course c){

		ArrayList<JCheckBox> userOptions =new ArrayList<JCheckBox>();
		HashSet<Requirement> result = new HashSet<Requirement>();

		JPanel problems = new JPanel();
		problems.setLayout(new BorderLayout());

		JLabel instruct = new JLabel(
				"The course "+ c.getPrefix() + " might satisfy the following requirements,"
						+ "\n   but it is not allowed to satisfy all of them at once. "
						+ "\nWhich requirements do you want it to satisfy?");

		problems.add(instruct, BorderLayout.NORTH);
		JPanel stack = new JPanel();

		for(int i = 0; i<enemies.size(); i++){
			JCheckBox combattingReqs = new JCheckBox(enemies.get(i).shortString(40) + " (" +  majors.get(i).name + ")" );
			stack.add(combattingReqs);
			userOptions.add(combattingReqs);
		}


		problems.add(stack);
		JOptionPane.showMessageDialog(null,  problems, "Combatting Requirements", JOptionPane.INFORMATION_MESSAGE,  icon );

		for(int i=0; i<userOptions.size(); i++){
			if(userOptions.get(i).isSelected()){
				result.add(enemies.get(i));
			}

		}

		return result;
	}




	/**
	 * When a requirement panel is dropped into a semester panel
	 * @param r
	 * @param semesterP
	 */
	public void GUIRequirementPanelDropped(RequirementPanel r, SemesterPanel semesterP) {
		sch.addScheduleElement(r.req, semesterP.sem);
		this.update();
	}



	/**
	 * When a schedule element panel is dropped into a semester panel
	 * @param p
	 * @param semesterPanel
	 */
	public void GUIScheduleElementPanelDropped(ScheduleElementPanel p, SemesterPanel newSemesterPanel) {

		Semester old = p.container.sem;
		if(p.getElement() instanceof ScheduleCourse){

			Requirement r=	new Requirement(new Prefix[]{p.getElement().getPrefix()}, 1);
			sch.replace(p.getElement(), r, old, newSemesterPanel.sem);
		}
		else{
			sch.replace(p.getElement(), p.getElement(), old, newSemesterPanel.sem);
		}
		this.update();
	}


	/**
	 * When the user clicks the  + button to add a new semester to the schedule
	 */
	public void GUISemesterPanelAdded(){
		sch.addNewSemester();
		this.update();
	}

	/**
	 * When a schedule element is removed from a semester panel 
	 * (not replaced or moved)
	 * @param e
	 * @param semesterPanel
	 */
	public void GUIRemoveElement(ScheduleElementPanel e, SemesterPanel semesterPanel) {
		sch.removeElement(e.getElement(), semesterPanel.sem);
		this.update();

	}


	/**
	 * When an element is changed from one value to another, for example, 
	 * a requirement becoming a course.
	 * @param container
	 * @param toChange
	 * @param newValue
	 */
	public void GUIElementChanged(SemesterPanel container, ScheduleElementPanel toChange, ScheduleElement newValue){
		Semester s = container.sem;
		ScheduleElement old = toChange.getElement();
		sch.replace(old, newValue, s, s);
		update();
	}


	public void GUIAddMajor(Major m) {
		boolean typeNeedsToBeChosen = true;
		if (m.majorType.equals(Major.MINOR)){
			typeNeedsToBeChosen = false;
		}
		if(m.majorType.equals(Major.TRACK) && m.degreeTypes.isEmpty()){
			typeNeedsToBeChosen = false;
		}
		if(m.degreeTypes.size()==1){
			m.setChosenDegree(m.degreeTypes.get(0));
			typeNeedsToBeChosen = false;
		}
		if(typeNeedsToBeChosen){
			if(!GUIChooseMajorDegreeType(m)){
				return;
			}
		}
		if(m.notes != null){
			//show the user the notes, and let them know that this is the last time they'll see 
			// these notes.
			String message = "Notes for " + m.name + " (can be displayed by performing a full check of your schedule)";
			String title = "Notes for " + m.name;
			JOptionPane.showMessageDialog(null, message + "\n\n" + m.notes, title, JOptionPane.INFORMATION_MESSAGE);

		}
		this.sch.addMajor(m);
		this.update();
	}


	/**
	 * If a major has an ambiguous degree type (BA, BS, BM, ...)
	 * this lets the user choose a type.
	 * @param m
	 */

	public boolean GUIChooseMajorDegreeType(Major m){
		if(m.degreeTypes.size()>1 || m.degreeTypes.size()==0){
			ArrayList<String> toAdd= new ArrayList<String>();
			String instructions = null;
			String header = null;


			for(int i = 0; i<m.degreeTypes.size(); i++){
				toAdd.add(CourseList.getDegreeTypeString(m.degreeTypes.get(i)));

			}



			if(m.degreeTypes.size()==0){
				toAdd.add(CourseList.getDegreeTypeString(CourseList.BS));
				toAdd.add(CourseList.getDegreeTypeString(CourseList.BA));
				toAdd.add(CourseList.getDegreeTypeString(CourseList.BM));
				instructions = "Your major was not given a degree type. Please look up your major and choose the appropriate option.";
				header = "WARNING";
			}

			String[] choices = new String[toAdd.size()];
			for(int p = 0; p<toAdd.size(); p ++){
				choices[p]=toAdd.get(p);


			}
			if(m.degreeTypes.size()>1){
				instructions = "What type of degree would you like for " + m.name ;
				header = "Degree Type";
			}

			String GERNeeded = (String)JOptionPane.showInputDialog(null, instructions,  header, JOptionPane.PLAIN_MESSAGE, icon, choices, "cat" );

			int MajorType = CourseList.BA;
			if(GERNeeded == null){
				return false;
			}

			MajorType=CourseList.getDegreeTypeNumber(GERNeeded);
			//this.sch.removeMajor(sch.masterList.getGERMajor(0));
			//this.sch.addAtMajor(sch.masterList.getGERMajor(MajorType), 0);
			m.setChosenDegree(MajorType);

		}
		return true;
	}

	/**
	 * A method for letting the user choose among a list of majors.
	 * The string determines whether it chooses between actual majors,
	 * minors, or tracks.
	 * @param s
	 */
	public void GUIPopUP(String s){
		new PopUpChooser(s, this, sch);
	}

	/**
	 * Show the user a webpage
	 * @param actionCommand
	 */
	public void GUIOutsideLink(String actionCommand) {
		try{
			if(actionCommand.equals(MenuOptions.exploreInternship)){

				Desktop.getDesktop().browse(new URL("http://www.furman.edu/sites/internship/FindingInternships/Pages/default.aspx").toURI());
			}
			if(actionCommand.equals(MenuOptions.exploreStudyAway)){
				Desktop.getDesktop().browse(new URL("https://studyaway.furman.edu/index.cfm?FuseAction=Programs.SimpleSearch").toURI());
			}
			if(actionCommand.equals(MenuOptions.addResearch)){
				Desktop.getDesktop().browse(new URL("http://www.furman.edu/sites/ur/Pages/default.aspx").toURI());
			}
			if(actionCommand.equals(MenuOptions.exploreMayX)){
				Desktop.getDesktop().browse(new URL("http://www.furman.edu/academics/may-experience/Pages/default.aspx").toURI());
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}

	}

	/**
	 * If the user clicks an x button on a semester.
	 * @param semesterPanel
	 */
	public void GUIRemoveSemester(SemesterPanel semesterPanel) {
		sch.removeSemester(semesterPanel.sem);
		this.update();

	}

	public void GUIRemoveMajor(MajorPanel p) {
		sch.removeMajor(p.major);
		this.update();

	}




	public void GUIChooseSummerSession() {
		String[] summerChoice = {SemesterDate.getSeason(SemesterDate.SUMMERONE), SemesterDate.getSeason(SemesterDate.SUMMERTWO)};
		String c = (String)JOptionPane.showInputDialog(null, "Choose Summer Session" , "Summer Session" , JOptionPane.PLAIN_MESSAGE, icon, summerChoice, "Dr. Fray");
		GUIYearsPopUP(c);
	}

	public void GUIYearsPopUP(String actionCommand){
		if(actionCommand.equals(SemesterDate.getSeason(SemesterDate.SUMMERONE))){
			season= SemesterDate.SUMMERONE;
			seasonName = SemesterDate.getSeason(SemesterDate.SUMMERONE);
		}
		if(actionCommand.equals(SemesterDate.getSeason(SemesterDate.SUMMERTWO))){
			season = SemesterDate.SUMMERTWO;
			seasonName = SemesterDate.getSeason(SemesterDate.SUMMERTWO);
		}

		if(actionCommand.equals(MenuOptions.addMayX)){
			season= SemesterDate.MAYX;
			seasonName = SemesterDate.getSeason(SemesterDate.MAYX);
		}




		//Gets available years
		ArrayList<Integer> availableYears = new ArrayList<Integer>();

		ArrayList<Semester> allSemesters = sch.getAllSemestersSorted();
		int last = (allSemesters.size()-1);
		int end = allSemesters.get(last).semesterDate.year;
		//Two first is Prior, second is odd one out
		for(int i=  allSemesters.get(2).semesterDate.year; i<= end; i++){
			if ((!sch.SemesterAlreadyExists(new SemesterDate(i, season)))){
				availableYears.add(i);
			}
		}


		yearsDialog = new Integer[availableYears.size()];
		for(int i=0; i<availableYears.size(); i++){
			yearsDialog[i]= availableYears.get(i);
		}

		if(availableYears.size()!=0){

			createYearDialogBox(seasonName);

		}


	}

	public void GUIExamineRequirement(Requirement r){
		//Make a new JFrame with info about this requirement.
		//Include the full definition as written in our code.
		String showText = r.examineRequirementString();
		JTextArea toShow = new JTextArea(showText);
		toShow.setEditable(false);
		JScrollPane pane = new JScrollPane(toShow);
		pane.setPreferredSize(new Dimension(toShow.getPreferredSize().width + 20,300));
		JOptionPane.showMessageDialog(null, pane, "Details of requirement " + r.shortString(RequirementPanel.shortStringLength), 
				JOptionPane.OK_OPTION, icon);
	}


	/**
	 * When adding a summer session, after choosing session 1 or 2, 
	 * this method lets the use choose which year they want to add it in
	 * 
	 * @param s
	 */
	public void createYearDialogBox(String s){
		Integer y = (Integer)JOptionPane.showInputDialog(null, instructYear + s,  headInstructYear, JOptionPane.PLAIN_MESSAGE, icon, yearsDialog, "cat" );
		if((y != null) && (y !=0)){
			Semester addedSemester = sch.addNewSemesterInsideSch(y,season);
			this.update();
			addCourseDialogBox(addedSemester);
		}
	}

	/**
	 * Ask the user which course in this semester they want to add.
	 * Used by MayX or Summer semesters, or the addCourse button of
	 * a semester.
	 * @param s
	 */
	public void addCourseDialogBox(Semester s){	
		ArrayList<ScheduleCourse> addCourses = new ArrayList<ScheduleCourse>();
		addCourses = sch.getCoursesInSemester(s);
		ArrayList<ScheduleCourse> toFinal =sch.filterAlreadyChosenCourses(addCourses);
		ScheduleCourse[] finalCourseList = new ScheduleCourse[toFinal.size()];
		for(int i = 0; i<toFinal.size(); i++){
			finalCourseList[i] = toFinal.get(i);
		}

		ScheduleCourse c = GUIChooseCourse(finalCourseList);

		if(c != null){
			//Removes all courses that have already been added in case of MayX 
			if(s.semesterDate.sNumber == (SemesterDate.MAYX)){
				s.elements.clear();
			}
			sch.addScheduleElement(c, s);
			this.update();
		}
	}



	public ScheduleCourse GUIChooseCourse(ScheduleCourse[] finalListOfCourses) {
		if(finalListOfCourses.length <= 0){
			ImageIcon icon = new ImageIcon(MenuOptions.resourcesFolder + "BellTower(T).png");
			JOptionPane.showMessageDialog(null, 
					"There were no courses to choose from. \n"
							+"If you have a course in mind, you can add a note to the semester,\n"
							+"or you can drag a requirement up to act as a place holder.", "No courses",JOptionPane.INFORMATION_MESSAGE,  icon  );
			return null;
		}
		try{
			ScheduleCourse c = CourseChooser.chooseCourse(finalListOfCourses, sch.getAllRequirements());
			return c;
		}
		catch (Exception e){
			e.printStackTrace();
			return null;
		}
		/*

		String reqsString = "";
		String[] displayed = new String[finalListOfCourses.length];
		ArrayList<Requirement> allReqs = sch.getAllRequirements();
		for(int i = 0; i < finalListOfCourses.length ; i ++){
			ScheduleCourse c = finalListOfCourses[i];
			int reqsFulfilled = 0;
			reqsString = "";
			for(Requirement r : allReqs){
				if(r.isSatisfiedBy(c.getPrefix())){
					String s = r.shortString();
					if(s.length()>7){
						reqsString = reqsString + s.substring(0, 7) + "... ";
					}
					else{
						int length = s.length();
						reqsString = reqsString + s + " ";
						while(length < 10){
							reqsString += " ";
							length ++;
						}
					}
					reqsFulfilled ++;
				}
			}
			String displayedString = "";
			String clockTime = "";
			if(c.c.meetingTime != null && c.c.meetingTime[0]!= null){
				clockTime = c.c.meetingTime[0].clockTime();
			}
			displayedString = String.format(
					"%-12s %-50s %3d   %s", 
					clockTime,
					c.getPrefix().toString() + " " +c.c.professor,
					reqsFulfilled,
					reqsString);
			displayed[i] = displayedString;
		}

		String chosenString = (String)JOptionPane.showInputDialog(null, message , message , JOptionPane.PLAIN_MESSAGE, icon, displayed, "Dr. Fray");
		int chosenIndex = 0;
		for(; chosenIndex < displayed.length ; chosenIndex ++){
			if(displayed[chosenIndex] == chosenString){
				break;
			}
		}
		if(chosenIndex == displayed.length){
			return null;
		}
		return finalListOfCourses[chosenIndex];
		 */
	}





	public void GUISupriseWindow(Semester s) {
		if(s.semesterDate.sNumber == (SemesterDate.MAYX)){
			//Tells Suprise Me to skip asking for the GER Req, chooses from all the classes. 
			new SupriseMe(sch, s, this, "Skip");
		}
		else{
			new SupriseMe(sch, s, this, null);
		}
	}

	public void GUIChallengeExcepted(Semester s, Course c){
		if(s.semesterDate.sNumber==SemesterDate.MAYX){
			//s.elements.clear();
			sch.addScheduleElement(new ScheduleCourse(c, this.sch), s);


		}
		else{

			sch.addScheduleElement(new ScheduleCourse(c, this.sch), s);

		}	

		this.update();
	}



	public void GUImakeSemesterStudyAway(Semester sem) {
		sem.setStudyAway(true);
		this.update();
		JOptionPane.showMessageDialog(null, "This semester is marked as Study Away, please drag in any requirements you will fulfill while abroad.", "Study abroad",JOptionPane.INFORMATION_MESSAGE,  icon  );

	}

	public void GUIremoveSemesterStudyAway(Semester sem) {

		sem.setStudyAway(false);
		this.update();
	}

	public void GUIaddNotes(Semester sem) {
		sem.setHasNotes(true);
		this.update();

	}


	public void GUITextBeingWritten(DocumentEvent e, Semester s) throws BadLocationException {
		int length = e.getDocument().getLength();
		String noteWritten = e.getDocument().getText(0, length);
		s.setNotes(noteWritten);


	}

	public void GUIremoveNotes(Semester sem) {
		sem.setHasNotes(false);
		sem.setNotes("");
		this.update();

	}







	/**
	 * A highly used method.
	 * Whenever there is a scheudling error, like classes overlapping,
	 * this method is called. The user is asked whether we should ignore
	 * the error. If the user overrides the error,
	 *  this method returns true, otherwise false.
	 * @param s
	 * @return
	 */
	public boolean userOverrideError(ScheduleError s){
		String header=headerFor(s);
		int maxElementStringLength = 100;//Use as many characters as necessary
		// for each of the schedule elements in the string.

		//instruct will be displayed as the main warning message
		String instruct= errorString(s, maxElementStringLength, true); 

		//Special cases for overload and duplicate - they get different strings here than when they're 
		// found in the checkAllErrors button
		if(s.error.equals(ScheduleError.overloadError)){
			instruct="Adding " + s.offendingCourse.shortString(maxElementStringLength) + " exceeds this semester's overload limit of " + s.overloadLimit + " credit hours ";
		}
		if(s.error.equals(ScheduleError.duplicateError)){
			instruct = s.elementList[0].getDisplayString() + " duplicates " +s.elementList[1].getDisplayString();
		}
		Object[] options = {"Ignore", "Cancel"};
		int n = JOptionPane.showOptionDialog(null, "<html><body>" + parseIntoReadableHTML(instruct, defaultPixelWidth) +"</body></html>", header, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, icon, options, options[0]);
		return (n==JOptionPane.OK_OPTION);

	}

	/**
	 * Given a schedule error, return the string 
	 * @param e
	 * @return
	 */
	private String errorString(ScheduleError e, int preferredMaxLength, boolean surroundHTML){
		String result = "";
		if(e.error.equals(ScheduleError.overlapError)){
			result = (e.elementList[0].shortString(preferredMaxLength) + "\n    overlaps " + e.elementList[1].shortString(preferredMaxLength));
			ArrayList<String> issueStrings = new ArrayList<String>();
			if(e.meetingOverlap){
				issueStrings.add("meeting times");
			}
			if(e.examOverlap){
				issueStrings.add( "exams");
			}
			if(e.labOverlap){
				issueStrings.add("labs");
			}
			result += "\nOverlaps in :" + issueStrings.toString();

		}
		else if(e.error.equals(ScheduleError.overloadError)){
			result = e.offendingSemester.semesterDate.getUserString() + "  exceeds its overload limit of " 
					+ e.offendingSemester.getOverloadLimit()  + " credit hours";
		}
		else if(e.error.equals(ScheduleError.preReqError)){
			result =  e.offendingCourse.shortString(preferredMaxLength) + " needs prerequsite(s): " + e.req.toString();
		}
		else if(e.error.equals(ScheduleError.duplicateError)){
			result = e.offendingCourse.shortString(preferredMaxLength) + " is a duplicate course";

		}
		else if(e.error.equals(ScheduleError.optimisticSchedulerError)){
			Requirement r = e.getOptimisticRequirement();
			ArrayList<Requirement> pair = r.atLeastRequirementPairs();
			Requirement subset = pair.get(1);

			result = "The requirement " 
					+ r.shortString(70)
					+ " must include at least " 
					+ r.indent(subset.getDisplayString(),  "   ")
					+ "Until you change this requirment into a course, "
					+ "\nwe will assume that it represents one of these.";

		}

		result = parseIntoReadableHTML(result, defaultPixelWidth);
		if(surroundHTML){
			result = "<html><body> " + result + "</html></body>";
		}
		
		return result;
	}

	private String headerFor(ScheduleError e){
		switch(e.error){
		case ScheduleError.overlapError:
			return "Overlap Error";
		case ScheduleError.duplicateError:
			return "Duplicate Error";
		case ScheduleError.overloadError:
			return "Overload Error";
		case ScheduleError.optimisticSchedulerError:
			return "Optimistic Planning";
		case ScheduleError.preReqError:
			return "Prereq Error";
		}
		return null;
	}


	public String getErrorString(){
		int elementPreferredMaxLength = 30;
		ArrayList<ScheduleError> allErrors =sch.checkAllErrors();
		String result = new String();
		if(!allErrors.isEmpty()){
			for(ScheduleError s : allErrors){
				result += errorString(s, elementPreferredMaxLength, false) + "\n";
			}
		}
		return result;
	}



	public void GUICheckAllErrors(boolean displayPopUp) {
		String errorString = getErrorString();
		String result = "<html><body>" + errorString;
		System.out.println(result);
		if(errorString.equals("")){
			result = "Your Schedule had no errors! You're a pretty savy scheduler";
		}
		result= result.replaceAll("\n", "<br>");
		JOptionPane.showMessageDialog(null,  result, "All Errors", JOptionPane.INFORMATION_MESSAGE,  icon );


		String majorNotes = "";
		boolean hasNotes = false;
		for(Major m : sch.getMajors()){
			if(m.notes != null){
				majorNotes += "Notes for " + m.name + "\n";
				majorNotes += m.notes + "\n\n";
				hasNotes = true;
			
			}
		}
		
		majorNotes.replaceAll("\n", "<br>");
		if(hasNotes && displayPopUp){
			JOptionPane.showMessageDialog(null, majorNotes, "Notes for all majors", JOptionPane.INFORMATION_MESSAGE);
		}
		

	}

	public void showExamineRequirementHelp(){
		JOptionPane.showMessageDialog(null, 
				"To see details about a requirement,"
						+ "\n  1) Right-click the requirement you want to examine"
						+ "\n  2) select \"" + MenuOptions.examineRequirementRightClick + "\" ");
	}
	public void showFindACourseHelp(){
		JOptionPane.showMessageDialog(null, 
				"To find a particular course, you can do one of the following:"
						+ "\n  1) If you know which semester(s) that course is offered, "
						+ "\n        click \"Options,\""
						+ "\n        and choose \"" + MenuOptions.addInstruct + "\""
						+ "\n  2) If you know of a requirement the course satisfies, you can"
						+ "\n        drag that requirement into a semester and click "
						+ "\n        \"" + MenuOptions.addCourseWithRequirement + "\""
						+ "\n  3) If the course is not in our database yet, you can"
						+ "\n        add a note to the semester where it is offered"
						+ "\n        by clicking \"Options\" and then \"" +MenuOptions.addNotes+"\"");
	}


	/**
	 * 
	 * @param s
	 * @param pixels
	 * @return
	 */
	public static String parseIntoReadableHTML(String s, int pixels){
		s = s.replaceAll("\n", "<br />");
		return  "<p style='width " + pixels + "px;'>" + s + "</p>";
	}






	public void dragStarted(ScheduleElement e){
		this.schP.dragStarted(e);

	}

	public void dragEnded(){
		this.schP.dragEnded();
	}

	public void updateAll(){
		schP.update(sch);
		reqs.update(sch);
		b.update();
	}

	public void repaintAll(){
		schP.revalidate();
		schP.repaint();

		reqs.revalidate();
		reqs.repaint();

		b.revalidate();
		b.repaint();

	}

	public void update() {
		updateAll();
		repaintAll(); 
		//System.out.println(this.sch.semesters.get(0).hasNotes);

	}





	public void GUIPrintSchedule(){
		JPanel options = new JPanel();
		ArrayList<JCheckBox> userOptions = new ArrayList<JCheckBox>();

		options.setLayout(new BorderLayout());
		JCheckBox ReqLayout = new JCheckBox("Requirement Layout");
		ReqLayout.setToolTipText("This is provides a checklist of all the requirements for your major, and GER. This will list out what has/hasn't been satisfied");
		userOptions.add(ReqLayout);
		JCheckBox ScheduleLayout = new JCheckBox("Schedule Layout");
		ScheduleLayout.setToolTipText("Diplays a the schedule created semester-by-semester. Includes scheduling errors ");
		userOptions.add(ScheduleLayout);



		JLabel instruct = new JLabel("Which format(s) would you like to use?           ");
		options.add(instruct, BorderLayout.NORTH);
		options.add(ReqLayout, BorderLayout.EAST);
		options.add(ScheduleLayout, BorderLayout.WEST);
		String finalPrint = new String();
		JOptionPane.showMessageDialog(null, options);
		boolean selectedScheduleLayout = userOptions.get(1).isSelected();
		boolean selectedReqLayout = userOptions.get(0).isSelected();

		//If they didn't select anything, the default is schedule layout.
		if( (!selectedScheduleLayout) && (!selectedReqLayout)){
			selectedScheduleLayout = true;
		}

		//Schedule
		if(selectedScheduleLayout){
			finalPrint = finalPrint + sch.printScheduleString() + "<br>";
		
		}
		//Reqs
		if(selectedReqLayout){
			finalPrint = finalPrint + sch.printRequirementString() + "<br>";
		}


		if(selectedScheduleLayout || selectedReqLayout){
			JTextPane schedulePrint = new JTextPane();
			schedulePrint.setFont(FurmanOfficial.normalFont);
			//schedulePrint.setWrapStyleWord(true);
			//Printable p = schedulePrint.getPrintable(null, null);
			Paper p = new Paper();
			schedulePrint.setPreferredSize(new Dimension((int) p.getWidth(), (int) p.getHeight()));
			//	schedulePrint.setLineWrap(true);
			schedulePrint.setContentType("text/html");
			//	System.out.println("<html><p>" + finalPrint + "</p>"+ Driver.getDisclaimer() +"</html>");
			schedulePrint.setText("<html><p>" + finalPrint + "</p>"+ Driver.getDisclaimer() +"</html>");

			schedulePrint.setEditable(false);
			//schedulePrint.setFont(FurmanOfficial.monospaced);
			JScrollPane scrollPane = new JScrollPane(schedulePrint);
			scrollPane.setPreferredSize(new Dimension(schedulePrint.getPreferredSize().width,500));
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			String[] choices= {"Print", "Cancel"};

			int userChoice = (int) JOptionPane.showOptionDialog(null, scrollPane, "Print Preview", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);

			if(userChoice == 0){
				try {
					System.out.println("This is what is sent to the printer");
					System.out.println(schedulePrint.getText());
					schedulePrint.print();
				} catch (PrinterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}



	public void GUISaveSchedule(){
		FileHandler.saveSchedule(this.sch);
	}


	public void addWindowListener(WindowListener w){
		frame.addWindowListener(w);
	}

	public void askMasterPassword(){
		String input = JOptionPane.showInputDialog("Knock knock \n \n...\n \n ... \n \n Password?");
		if(input == null){
			return;
		}
		if(input.toLowerCase().equals("a happy cat peers at you from its cloud")){
			//Doesn't repaint Driver, and can't repaint mainMenuBar, so if
			// there's a master-specific function in the menu bar then 
			// you have to make a new schedule to see it.
			FurmanOfficial.masterIsAround = true;
			JFrame debugScreen = new JFrame();
			JTextArea out = new JTextArea();
			System.setOut(new PrintStream(new OutputStream(){
				public void write(int b) throws IOException{
					out.append(String.valueOf( ( char )b ) );
				}

			}));

			debugScreen.setTitle("Debug area");
			debugScreen.add(out);
			debugScreen.pack();
			debugScreen.setVisible(true);
			debugScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		}
	}




	String importResult;
	public void importPriorCourses(){
		JTextArea importArea = new JTextArea("To import your prior courses, "
				+ "\n  1) Go to MyFurman. "
				+ "\n  2) Navigate to your unofficial transcript."
				+ "\n  3) You should see your name and ID in the top left corner,"
				+ "\n     and your cumulative GPA listed at the bottom."
				+ "\n  4) Hilight all the data between your name and your GPA and"
				+ "\n    drag it into this text box. "
				+ "\n  5) Click 'validate' to make sure the process worked!");
		JButton validate = new JButton("validate");
		validate.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				boolean success = trySetStudentData(importArea.getText());
				if(success){
					importResult = importArea.getText();
					importArea.setText("You're all set! The import went smoothly.");
				}
				else{
					importArea.setText("Something went wrong with the import. \n"
							+ "\nBe sure that you found your unofficial transcript on"
							+ "\n  myFurman, and that you hilighted the data from your"
							+ "\n  ID-Name to your GPA (and no more) ");
				}
			}
		});
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(new JScrollPane(importArea), BorderLayout.SOUTH);
		p.add(validate, BorderLayout.WEST);

		int chosen = JOptionPane.showOptionDialog(null,p, "Import your schedule",JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE,null, null, null);
		if(chosen == JOptionPane.OK_OPTION){
			try{
				if(importResult == null){
					importResult = importArea.getText();
				}
				this.sch.readPrior(importResult);
				FileHandler.writeStudentData(importResult);
				this.update();

			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	public static boolean trySetStudentData(String text){
		try{
			new Schedule(text);
			return true;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}














}






