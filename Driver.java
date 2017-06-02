import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class Driver{ 

	Schedule sch;
	SchedulePanel schP;
	RequirementListPanel reqs;
	int season;
	JFrame popUP = new JFrame();
	JList<Integer> pickYears;
	String seasonName;
	Integer[] yearsDialog;
	ImageIcon icon = new ImageIcon("src/Furman-logo.png");
	String instructYear = "Please pick a year you would like to add a ";
	String headInstructYear = "Pick a year";
	String instructCourse = "Please pick the course you would like to add to your ";
	String headInstructCourse = "Pick a course";
	Course[] coursesDialog;
	String summerOverload = "You need to delete a course before you can add another";






	public Driver() {

		//Belltower icon and scaling
		ImageIcon icon = new ImageIcon("src/bellTower.jpg");
		Image image = icon.getImage();
		Image newImage = image.getScaledInstance(100,400 , java.awt.Image.SCALE_SMOOTH);
		icon = new ImageIcon(newImage);
		JLabel belltowerLabel = new JLabel(icon);


		//Make data
		Schedule test = Schedule.testSchedule();		
		sch=test;


		JFrame frame = new JFrame();
		frame.setLayout(new BorderLayout());

		//Adds the menu bar
		MainMenuBar menu = new MainMenuBar(this);
		menu.setFont(FurmanOfficial.getFont(12));
		frame.setJMenuBar(menu);
		frame.setContentPane(menu.createContentPane());

		//Adds Additions Panel and belltower
		AdditionsPanel extras = new AdditionsPanel(this);
		JPanel left = new JPanel();
		left.add(belltowerLabel);
		left.add(extras);
		frame.add(left, BorderLayout.WEST);

		schP = new SchedulePanel(test, this);

		frame.add(schP, BorderLayout.NORTH);
		reqs = new RequirementListPanel(test, this);
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
		Schedule current = new Schedule(sch.masterList, sch.semesters.get(0).semesterDate, null);
		sch = current;
		this.update();

	}


	public void GUIRequirementPanelDropped(RequirementPanel r, SemesterPanel semesterP) {
		sch.addScheduleElement(r.req, semesterP.sem);
		this.update();
	}


	public void GUIScheduleElementPanelDropped(ScheduleElementPanel p, SemesterPanel semesterPanel) {
		sch.addScheduleElement(p.getElement(), semesterPanel.sem);
		this.update();
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
		sch.addMajor(m);
		this.update();

	}

	public void GUIPopUP(String s){
		new ExtrasAddList(s, this, sch);
	}

	public ArrayList<Major> GUIRemoveDuplicates(ArrayList<Major> collectionOfMajors) {
		return sch.filterAlreadyChosenMajors(collectionOfMajors);


	}
	public void GUIOutsideLink(String actionCommand) {
		try{
			if(actionCommand.equals(MenuOptions.addInternship)){

				Desktop.getDesktop().browse(new URL("http://www.furman.edu/sites/internship/FindingInternships/Pages/default.aspx").toURI());
			}
			if(actionCommand.equals(MenuOptions.addStudyAway)){
				Desktop.getDesktop().browse(new URL("https://studyaway.furman.edu/index.cfm?FuseAction=Programs.SimpleSearch").toURI());
			}
			if(actionCommand.equals(MenuOptions.addResearch)){
				Desktop.getDesktop().browse(new URL("http://www.furman.edu/sites/ur/Pages/default.aspx").toURI());
			}
			if(actionCommand.equals(MenuOptions.exploreMayX)){
				Desktop.getDesktop().browse(new URL("http://www.furman.edu/academics/may-experience/Pages/default.aspx").toURI());
			}
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
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

	public void createYearDialogBox(String s){
		Integer y = (Integer)JOptionPane.showInputDialog(popUP, instructYear + s,  headInstructYear, JOptionPane.PLAIN_MESSAGE, icon, yearsDialog, "cat" );
		if((y != null) && (y !=0)){
			Semester addedSemester = sch.addNewSemesterInsideSch(y,season);
			this.update();
			addCourseDialogBox(s, addedSemester);
		}
	}

	public void addCourseDialogBox(String season, Semester s){	
		ArrayList<Course> addCourses = new ArrayList<Course>();
		addCourses = CourseList.testList().getCoursesIn(s);
		Course[] toAdd = new Course[addCourses.size()];
		for(int i = 0; i<addCourses.size(); i++){
			toAdd[i]= addCourses.get(i);
		}
		if(addCourses.size()>0){
			Course c = (Course)JOptionPane.showInputDialog(popUP, instructCourse + season,  headInstructCourse, JOptionPane.PLAIN_MESSAGE, icon, toAdd, "cat" );
			if((c != null) && (c instanceof Course)){
				//Removes all courses that have already been added in case of MayX 
				if(season.equals(MenuOptions.changeInstruct)){
					s.elements.clear();
				}

				sch.addScheduleElement(c, s);
				this.update();

			}	
		}
	}

	public void GUIAddCourseWithRequirement(ScheduleElement s, SemesterPanel container, String action) {
		ArrayList<Course> listOfCourses = container.getSemester().getCoursesSatisfying((Requirement)s);
		Course[] toAdd = new Course[listOfCourses.size()];
		for(int i =0; i<listOfCourses.size(); i++){
			toAdd[i]=listOfCourses.get(i);
		}
		if(listOfCourses.size()>0){
			Course c = (Course)JOptionPane.showInputDialog(popUP, action , action , JOptionPane.PLAIN_MESSAGE, icon, toAdd, "Dr. Fray");
			if(c!=null && c instanceof Course){
				sch.replaceElement(container.sem, s, c);
				this.update();
			}
		}
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
			s.elements.clear();

		}
		if(s.semesterDate.sNumber == SemesterDate.SUMMERONE || s.semesterDate.sNumber == SemesterDate.SUMMERTWO){

			sch.addScheduleElement(c, s);
			this.update();
		}

	}

	public void SummerOverloadDialog(){
		JOptionPane.showMessageDialog(popUP, summerOverload, "Summer Overload", JOptionPane.INFORMATION_MESSAGE, icon);
	}
	public void updateAll(){
		schP.update(sch);
		reqs.update(sch);
	}

	public void repaintAll(){
		schP.revalidate();
		schP.repaint();

		reqs.revalidate();
		reqs.repaint();

	}



	public void update() {
		updateAll();
		repaintAll(); 

	}


	public static void main(String[] args){
		try{
			new Driver();
		}
		catch(SchedulingException e){
			e.printStackTrace();
		}


	}














}






















