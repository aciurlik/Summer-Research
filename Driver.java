import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;


public class Driver{ 

	Schedule sch;
	static Schedule testSchedule;
	SchedulePanel schP;
	RequirementListPanel reqs;
	int season;
	JFrame popUP = new JFrame();
	JList<Integer> pickYears;
	String seasonName;
	Integer[] yearsDialog;
	ImageIcon icon = new ImageIcon("src/BellTower(T).png");
	String instructYear = "Please pick a year you would like to add a ";
	String headInstructYear = "Pick a year";
	String instructCourse = "Please pick the course you would like to add to your ";
	String headInstructCourse = "Pick a course";
	Course[] coursesDialog;
	String summerOverload = "You need to delete a course before you can add another";
	ScheduleElement beingDragged;
	ListOfMajors l = ListOfMajors.readFrom(new File("Majors"));
	bellTower b;

	public static Driver testDriver(){
		Driver results = new Driver();
		testSchedule = Schedule.testSchedule();	

		testSchedule.setDriver(results);
		results.GUIAddMajor(results.l.getMajor("Psychology"));
		testSchedule.setDriver(results);
		return results;
	}





	public Driver() {

		//Make data
		sch = Schedule.testSchedule();
		sch.setDriver(this);



		JFrame frame = new JFrame();
		frame.setLayout(new BorderLayout());

		//Adds the menu bar
		MainMenuBar menu = new MainMenuBar(this);
		menu.setFont(FurmanOfficial.normalFont);
		frame.setJMenuBar(menu);
		frame.setContentPane(menu.createContentPane());

		//Adds Additions Panel and belltower
		AdditionsPanel extras = new AdditionsPanel(this);
		JPanel left = new JPanel();
		left.setBackground(FurmanOfficial.bouzarthGrey);
		bellTower belltowerLabel = new bellTower(sch);
		this.b=belltowerLabel;
		left.add(belltowerLabel);
		left.add(extras);
		frame.add(left, BorderLayout.WEST);

		schP = new SchedulePanel(sch, this);
		//schP.setPreferredSize(new Dimension(500, 500));

		frame.add(schP, BorderLayout.NORTH);

		reqs = new RequirementListPanel(sch, this);
		frame.add(reqs, BorderLayout.CENTER);

		this.update();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);

	}

	public void GUINewSchedule() {
		CourseList l = CourseList.testList();
		Collections.sort(sch.semesters);
		//This creates a Semester with that matches the current schedule Course List and starting Semester Date
		Schedule current = new Schedule(l, sch.semesters.get(0).semesterDate, null);
		setSchedule(current);
		this.update();

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

		JLabel instruct = new JLabel("The course "+ c.getPrefix() + " satisfies some requirements that don't want to share.\n"
				+ "Which requirements should it satisfy?");

		problems.add(instruct, BorderLayout.NORTH);
		JPanel stack = new JPanel();

		for(int i = 0; i<enemies.size(); i++){
			JCheckBox combattingReqs = new JCheckBox(enemies.get(i).getDisplayString() + " (" +  majors.get(i).name + ")" );
			stack.add(combattingReqs);
			userOptions.add(combattingReqs);
		}


		problems.add(stack);
		JOptionPane.showMessageDialog(popUP,  problems, "Combatting Requirements", JOptionPane.INFORMATION_MESSAGE,  icon );

		for(int i=0; i<userOptions.size(); i++){
			if(userOptions.get(i).isSelected()){
				result.add(enemies.get(i));
			}

		}

		return result;
	}



	private void setSchedule(Schedule current) {
		sch=current;
		this.sch.setDriver(this);
	}


	public void GUIRequirementPanelDropped(RequirementPanel r, SemesterPanel semesterP) {
		sch.addScheduleElement(r.req, semesterP.sem);
		this.update();

	}


	public void GUIScheduleElementPanelDropped(ScheduleElementPanel p, SemesterPanel newSemesterPanel) {
		Semester old = p.container.sem;
		if(p.getElement() instanceof ScheduleCourse){

			Requirement r=	new Requirement(new Prefix[]{p.getElement().getPrefix()}, 1);
			sch.replaceElement(old, p.getElement(), r);
			sch.moveElement(r, old, newSemesterPanel.sem);
			//This requirement is not removing itself
			old.remove(r);
			this.update();
		}
		else{
			sch.moveElement(p.getElement(), old, newSemesterPanel.sem);
			this.update();
		}
	}


	public void GUISemesterPanelAdded(){
		sch.addNewSemester();
		this.update();

	}

	public void GUIRemoveElement(ScheduleElementPanel e, SemesterPanel semesterPanel) {
		sch.removeElement(e.getElement(), semesterPanel.sem);
		this.update();

	}


	public void GUIElementChanged(SemesterPanel container, ScheduleElementPanel toChange, ScheduleElement newValue){
		Semester s = container.sem;
		ScheduleElement old = toChange.getElement();
		sch.replaceElement(s, old, newValue);
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
			GUIChooseMajorDegreeType(m);
		}
		if(m.notes != null){
			//show the user the notes, and let them know that this is the last time they'll see 
			// these notes.
			String message = "Notes for " + m.name + " (can be displayed by performing a full check of your schedule)";
			String title = "Notes for " + m.name;
			JOptionPane.showMessageDialog(popUP, message + "\n\n" + m.notes, title, JOptionPane.INFORMATION_MESSAGE);

		}
		this.sch.addMajor(m);
		this.update();
	}

	public void GUIChooseMajorDegreeType(Major m){
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

			String GERNeeded = (String)JOptionPane.showInputDialog(popUP, instructions,  header, JOptionPane.PLAIN_MESSAGE, icon, choices, "cat" );

			int MajorType = 0;
			if(GERNeeded == null){
				return;
			}
			MajorType=CourseList.getDegreeTypeNumber(GERNeeded);
			//this.sch.removeMajor(sch.masterList.getGERMajor(0));
			//this.sch.addAtMajor(sch.masterList.getGERMajor(MajorType), 0);
			m.setChosenDegree(MajorType);
		}
	}

	public void GUIPopUP(String s){
		new PopUpChooser(s, this, sch);
	}

	public ArrayList<Major> GUIRemoveDuplicates(ArrayList<Major> collectionOfMajors) {
		return sch.filterAlreadyChosenMajors(collectionOfMajors);


	}
	public void GUIOutsideLink(String actionCommand) {
		try{
			if(actionCommand.equals(MenuOptions.addInternship)){

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

	public void GUIRemoveSemester(SemesterPanel semesterPanel) {
		sch.removeSemester(semesterPanel.sem);
		this.update();

	}

	public void GUIRemoveMajor(MajorPanel p) {
		sch.removeMajor(p.major);
		this.update();

	}



	public void GUIChooseSummerSession() {
		String[] summerChoice = {MenuOptions.summerSessionOne, MenuOptions.summerSessionTwo};
		String c = (String)JOptionPane.showInputDialog(popUP, "Choose Summer Session" , "Summer Session" , JOptionPane.PLAIN_MESSAGE, icon, summerChoice, "Dr. Fray");
		GUIYearsPopUP(c);
	}

	public void GUIYearsPopUP(String actionCommand){
		if(actionCommand.equals(MenuOptions.summerSessionOne)){
			season= SemesterDate.SUMMERONE;
			seasonName = MenuOptions.summerSessionOne;
		}
		if(actionCommand.equals(MenuOptions.summerSessionTwo)){
			season = SemesterDate.SUMMERTWO;
			seasonName = MenuOptions.summerSessionTwo;
		}

		if(actionCommand.equals(MenuOptions.addMayX)){
			season= SemesterDate.MAYX;
			seasonName = MenuOptions.mayX;
		}




		//Gets available years
		ArrayList<Integer> availableYears = new ArrayList<Integer>();
		Collections.sort(sch.semesters);

		int last = (sch.semesters.size()-1);
		int end = sch.semesters.get(last).semesterDate.year;

		for(int i=  sch.semesters.get(1).semesterDate.year; i<= end; i++){

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

	public void dragStarted(ScheduleElement e){
		this.schP.dragStarted(e);
	}

	public void dragEnded(){
		this.schP.dragEnded();
	}




	public void createYearDialogBox(String s){
		Integer y = (Integer)JOptionPane.showInputDialog(popUP, instructYear + s,  headInstructYear, JOptionPane.PLAIN_MESSAGE, icon, yearsDialog, "cat" );
		if((y != null) && (y !=0)){
			Semester addedSemester = sch.addNewSemesterInsideSch(y,season);
			this.update();
			addCourseDialogBox(addedSemester);
		}
	}

	public void addCourseDialogBox(Semester s){	
		ArrayList<Course> addCourses = new ArrayList<Course>();
		addCourses = CourseList.testList().getCoursesIn(s);
		if(addCourses.size()==0){
			ImageIcon icon = new ImageIcon("src/BellTower(T).png");
			JOptionPane.showMessageDialog(popUP, "Classes have not yet been added to the "+ s.semesterDate.getSeason(s.semesterDate.sNumber)+ " "+ s.semesterDate.year + " semester", "No classes",JOptionPane.INFORMATION_MESSAGE,  icon  );
		}
		if(addCourses.size()>0){
			Course[] toAdd = new Course[addCourses.size()];
			for(int i = 0; i<addCourses.size(); i++){
				toAdd[i]= addCourses.get(i);
			}
			if(addCourses.size()>0){
				Course c = (Course)JOptionPane.showInputDialog(popUP, instructCourse + s.semesterDate.getSeason(s.semesterDate.sNumber),  headInstructCourse, JOptionPane.PLAIN_MESSAGE, icon, toAdd, "cat" );
				if((c != null) && (c instanceof Course)){
					//Removes all courses that have already been added in case of MayX 
					if(s.semesterDate.sNumber == (SemesterDate.MAYX)){
						s.elements.clear();
					}

					sch.addScheduleElement(new ScheduleCourse(c, this.sch), s);
					this.update();

				}	
			}
		}
	}



	public Course GUIChooseCourse(Course[] finallistOfCourses, String action) {
		Course[] toAdd = finallistOfCourses;

		if(toAdd.length>0){
			Course c = (Course)JOptionPane.showInputDialog(popUP, action , action , JOptionPane.PLAIN_MESSAGE, icon, toAdd, "Dr. Fray");

			if(c!=null && c instanceof Course){
				return c;

			}
		}
		return null;

	}





	public void GUIRemoveMajorDialogBox(String actionCommand) {
		ArrayList<Major> removeMajor = new ArrayList<Major>();
		removeMajor = sch.majorsList;

		//Make Note to test This
		if(actionCommand.equals(MenuOptions.removeMinor)){
			for(Major m : removeMajor){
				if(!m.isType(Major.MINOR)){
					removeMajor.remove(m);

				}
			}
		}
		if(actionCommand.equals(MenuOptions.removeTrack)){
			for(Major m : removeMajor){
				if(!m.isType(Major.TRACK)){
					removeMajor.remove(m);

				}
			}
		}
		Major[] toRemove = new Major[removeMajor.size()];
		if(removeMajor.size() != 0){
			for(int i = 0; i<removeMajor.size(); i++){
				toRemove[i] = removeMajor.get(i);
			}

			Major m = (Major) JOptionPane.showInputDialog(popUP, actionCommand, actionCommand, JOptionPane.PLAIN_MESSAGE,icon, toRemove, "Cat" );
			if((m != null) && (m instanceof Major)){
				sch.removeMajor(m);
				this.update();
			}
		}
	}

	public void GUISupriseWindow(Semester s) {

		new SupriseMe(sch, s, this);



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
		JOptionPane.showMessageDialog(popUP, "This semester is marked as Study Away, please drag in any requirements you will furfill while abroad.", "Study abroad",JOptionPane.INFORMATION_MESSAGE,  icon  );

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



	public boolean userRequestError(ScheduleError s){
		String header=null;
		//instruct will be displayed as the main warning message
		String instruct= null; 
		if(s.error.equals(ScheduleError.overloadError)){
			header = "Overload Error";
			instruct="Adding " + s.offendingCourse.getDisplayString() + " exceeds this semester's overload limit of " + s.overloadLimit;
		}
		if(s.error.equals(ScheduleError.overlapError)){
			header = "Overlap Error";
			instruct = (s.elementList[0].getDisplayString() + "\n    overlaps \n" + s.elementList[1].getDisplayString());
			ArrayList<String> issueStrings = new ArrayList<String>();
			if(s.meetingOverlap){
				issueStrings.add("meeting times");
			}
			if(s.examOverlap){
				issueStrings.add( "exams");
			}
			if(s.labOverlap){
				issueStrings.add("labs");
			}
			instruct += "\nIsses in :" + issueStrings.toString();
		}
		if(s.error.equals(ScheduleError.preReqError)){
			header = "Prerequisites Error";
			instruct = s.offendingCourse.getDisplayString() + " needs prerequisite(s) " + s.neededCourses.toString();
		}
		//if(s.error.equals(ScheduleError.preReqErrorPrefix)){
		//	header = "Prerequisites Error";
		//instruct = s.offendingCourse.getDisplayString() + " had prerequisite " + s.missingCourse.toString();
		//}
		if(s.error.equals(ScheduleError.duplicateError)){
			header = "Duplicate Error";
			instruct = s.elementList[0].getDisplayString() + " duplicates " +s.elementList[1].getDisplayString();
		}
		Object[] options = {"Ignore", "Cancel"};
		int n = JOptionPane.showOptionDialog(popUP, instruct, header, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, icon, options, options[0]);
		return (n==JOptionPane.OK_OPTION);

	}


	public void GUICheckAllErrors() {
		ArrayList<ScheduleError> allErrors =sch.checkAllErrors();
		String result = new String();
		if(!allErrors.isEmpty()){
			for(ScheduleError s : allErrors){
				if(s.error.equals(ScheduleError.overlapError)){
					result = result + s.elementList[0].shortString()+ " overlaps " + s.elementList[1].shortString() + "\n";
				}
				if(s.error.equals(ScheduleError.overloadError)){
					result = result + s.offendingSemester.semesterDate.getSeason(s.offendingSemester.semesterDate.sNumber)+ "  " + s.offendingSemester.semesterDate.year + "  exceeds its overload limit of " + s.offendingSemester.getOverloadLimit() + " \n";
				}
				if(s.error.equals(ScheduleError.preReqError)){
					result = result + s.offendingCourse.shortString() + " needs " + s.neededCourses.toString() + "\n";
				}
				if(s.error.equals(ScheduleError.duplicateError)){
					result = result + s.offendingCourse.shortString() + " is a duplicate course \n";
				}
			}
			if(result.length() < 2){
				result = "Your Schedule had no errors! You're a pretty savy scheduler";
			}
			JOptionPane.showMessageDialog(popUP,  result, "All Errors", JOptionPane.INFORMATION_MESSAGE,  icon );
		}
		else{
			JOptionPane.showMessageDialog(popUP, "You have no errors!", "All Errors", JOptionPane.INFORMATION_MESSAGE,  icon );
		}
		String majorNotes = "";
		boolean hasNotes = false;
		for(Major m : sch.majorsList){
			if(m.notes != null){
				majorNotes += "Notes for " + m.name + "\n";
				majorNotes += m.notes + "\n\n";
				hasNotes = true;
			}
		}

		if(hasNotes){
			JOptionPane.showMessageDialog(popUP, majorNotes, "Notes for all majors", JOptionPane.INFORMATION_MESSAGE);
		}
	}










	public void updateAll(){
		b.update();
		schP.update(sch);
		reqs.update(sch);

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


	public static void main(String[] args){
		//new Driver();
		testDriver();


	}










}






















