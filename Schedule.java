
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.StringJoiner;


public class Schedule implements java.io.Serializable {
	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;
	private ArrayList<Major> majorsList;
	private ArrayList<Semester> semesters;
	public HashSet<Prereq> prereqs;
	private Major GER;





	//transient is for Serializable purposes.
	public transient ScheduleGUI schGUI;
	CourseList masterList;
	private int CLP;
	private Prefix languagePrefix;
	private int totalCoursesNeeded;
	private Semester priorSemester;

  


	//public static SemesterDate defaultFirstSemester; //TODO this should be removed after demos.
	public SemesterDate currentSemester;
	SemesterDate firstSemester;
	public String studentName;

	


	public static final boolean prereqsCanBeSatisfiedInSameSemester = false;






	//boolean reqsValid; // The set of requirements entailed by all majors is up to date

	public static Schedule testSchedule(){
		//	CourseList l = CourseList.testList();
		Driver.tryPickStartDate();
		Schedule result = new Schedule();
		result.readBlankPrior();
		return result;
	}


	/**
	 * Make a new schedule of semesters where firstSemester is the first shown semester 
	 * and currentSemester is the first semester that might be scheduled
	 * (assume that earlier semesters have passed and already have their courses fixed.)
	 * @param masterList
	 * @param firstYear
	 * @param currentSemester
	 */
	public Schedule(){
		//Majors and requirements
		this.majorsList= new ArrayList<Major>();
		this.prereqs = new HashSet<Prereq>();
		//this.masterList = masterList;
		readBlankPrior(); //loads prior courses, recalc firstSemester and sets as currentSemester as well,
		// and create default semesters.
		this.recalcGERMajor();
	}

	
	
	public Schedule(PriorData priorCourses){
		
		this.majorsList= new ArrayList<Major>();
		this.prereqs = new HashSet<Prereq>();

		readPrior(priorCourses);
		recalcGERMajor();
		updatePrereqs();
		updateReqs();
	}


	public void setScheduleGUI(ScheduleGUI schGUI){
		this.schGUI = schGUI;
	}

	/**
	 * May wipe all scheduleElements from the schedule!!!!
	 * @param firstSemester
	 */
	public void setFirstSemester(SemesterDate firstSemester){
		if(firstSemester.compareTo(this.firstSemester) == 0){
			return;
		}
		//Semesters
		this.semesters = new ArrayList<Semester>();
		//Set prior semester 
		priorSemester = new Semester (firstSemester.previousSemester(), this);
		priorSemester.isPriorSemester = true;
		SemesterDate s = firstSemester;
		for(int i = 0; i < 8 ; i ++){
			this.semesters.add(new Semester(s, this));
			s = s.nextSemester();
		}
		this.firstSemester = firstSemester;
	}

	public void readBlankPrior(){
		SemesterDate firstSemester = null;
		if(FileHandler.propertyGet(FileHandler.startSemester) != null){
			firstSemester = SemesterDate.readFrom(FileHandler.propertyGet(FileHandler.startSemester));
		}
		if( firstSemester == null){
			throw new RuntimeException("Tried to make a blank schedule before a default first semester was chosen");
		}
		setFirstSemester(firstSemester);
		setCurrentSemester(firstSemester);
		
	}

	public void readTestPrior(){
		readBlankPrior();

		this.setLanguagePrefix(new Prefix("SPN", "115"));

		//Class One 
		Course a = new Course(new Prefix("THA", 101), new SemesterDate(2016, SemesterDate.FALL), null, null, 4, "03");
		ScheduleCourse aa = new ScheduleCourse(a, this);
		this.addScheduleElement(aa,this.semesters.get(0));


		//Class Two
		Course b = new Course(new Prefix("MTH", 120), new SemesterDate(2016, SemesterDate.FALL), null, null, 4, "01");

		ScheduleCourse bb = new ScheduleCourse(b,this);
		this.addScheduleElement(bb , this.semesters.get(0));



		//Class Three
		Course d = new Course(new Prefix("PSY", 111), new SemesterDate(2016, SemesterDate.FALL), null, null,  4, "03");
		ScheduleCourse dd = new ScheduleCourse(d, this);
		this.addScheduleElement(dd, this.semesters.get(0));
		//result.semesters.add(b);


		this.setCLP(10);
	}


	public void readPrior(PriorData pd){
		this.studentName = pd.getStudentName();
		currentSemester = pd.getLatestDate();
		setFirstSemester(pd.getEarliestDate());


		
		//Add the courses
		for(Course c : pd.getAllCourses()){
			ScheduleCourse cc = new ScheduleCourse(c, this);
			if(!this.directAddScheduleElement(cc, c.semesterDate)){
				throw new RuntimeException("Could neither find nor make the semester for the course \n" + cc.getDisplayString() +"," + c.semesterDate);
			}
		}
		MainMenuBar.addImportScheduleOption();
		
	}

	/**
	 * Add the schedule element with no error checks and no updates
	 * used when loading prior courses.
	 * @param e
	 * @param d
	 * @return
	 */
	private boolean directAddScheduleElement(ScheduleElement e, SemesterDate d){
		ArrayList<Semester> allSemesters = this.getAllSemestersSorted();
		
		for(int i = 0; i < allSemesters.size(); i ++){
			
			Semester s  = allSemesters.get(i);
			if(s.semesterDate.compareTo(d) == 0){
				return s.directAdd(e);
			}
			if(s.semesterDate.compareTo(d) > 0){
				//we've passed where the semester should have been, so we need to make a 
				// new semester.
				
				//TODO replace this with addNewSemesterInsideSch method
				// (if the method name changes, it's the second method in the 
				// gui methods for adding semesters).
				int indexInSemesters = i-1;
				if(i == -1){
					return false;
				}
				Semester newSemester = new Semester(d, this);
				semesters.add(indexInSemesters, newSemester);
				return newSemester.directAdd(e);
			}
		}
		return false;
	}









	@SuppressWarnings("unused")
	private boolean ___StartMethodsUsedByGUI_________;

	/////////////////////////////////////////////
	/////////////////////////////////////////////
	/////////////////////////////////////////////
	//////////	Methods called by the GUI  //////
	/////////////////////////////////////////////
	/////////////////////////////////////////////
	/////////////////////////////////////////////
	/*		|		|		|		|
	 *		|		|		|		|
	 *		V		V		V		V
	 */


	///////////////////////////////
	///////////////////////////////
	//	Adding and removing semesters
	///////////////////////////////
	///////////////////////////////
	@SuppressWarnings("unused")
	private boolean ______addRemoveSemesters_________;
	
	public Semester addNewSemester(){
		SemesterDate last = semesters.get(semesters.size() - 1).getDate();
		Semester next = new Semester(last.nextSemester(), this);
		this.semesters.add(next);
		return next;
	}

	public Semester addNewSemesterInsideSch(int year, int season) {
		SemesterDate inside = new SemesterDate(year, season);
		Semester toAdd = new Semester(inside, this);
		this.semesters.add(toAdd);
		Collections.sort(semesters);
		return toAdd;
	}

	public void removeSemester(Semester sem) {
		this.semesters.remove(sem);
		Collections.sort(semesters);
		updatePrereqs();
		updateReqs();
	}



	///////////////////////////////
	///////////////////////////////
	//	Adding and removing Elements
	///////////////////////////////
	///////////////////////////////
	@SuppressWarnings("unused")
	private boolean ______addRemoveElements_________;


	/**
	 * In semester S, remove ScheduleElement e.
	 * @param e
	 * @param s
	 */
	public boolean removeElement (ScheduleElement e, Semester s){
		if(this.checkErrorsWhenRemoving(e, s)){
			return false;
		}
		if(s.remove(e)){
			updatePrereqs();
			updateReqs();
			return true;
		}
		System.out.println("Remove didn't work");
		return false;
	}

	public boolean addScheduleElement(ScheduleElement element, Semester sem) {
		if(this.checkErrorsWhenAdding(element, sem)){
			System.out.println("add didn't work");
			return false;
		}

		if(sem.add(element)){
			//updateRequirementsSatisfied(element);
			updatePrereqs();
			updateReqs();
			return true;
		}
		System.out.println("add didn't work");


		return false;
	}



	/**
	 * Replace the old element with new element.
	 * Assumes that oldSemester contains oldElement and
	 * that newSemester does NOT contain newElement.
	 * @param oldElement
	 * @param newElement
	 * @param oldSemester
	 * @param newSemester
	 * @return
	 */
	public boolean replace(ScheduleElement oldElement, ScheduleElement newElement, Semester oldSemester, Semester newSemester){
		if(this.checkErrorsWhenReplacing(oldSemester, newSemester, oldElement, newElement)){

			return false;
		}

		//Perform the addition and removal
		if(!oldSemester.remove(oldElement)){
			System.out.println("Replace didn't work");
			return false;
		}
		if(!newSemester.add(newElement)){
			System.out.println("Replace didn't work");
			return false;
		}
		//update things.
		if(oldElement != newElement){
			updatePrereqs();
			updateReqs();
		}
		return true;
	}

	///////////////////////////////
	///////////////////////////////
	//	Adding and removing Majors
	///////////////////////////////
	///////////////////////////////
	@SuppressWarnings("unused")
	private boolean ______addRemoveMajors_________;


	public void addMajor(Major newMajor){
		majorsList.add(newMajor);
		if(!newMajor.name.equals("GER")){
			recalcGERMajor();
		}
		updateReqs();
		updateTotalCoursesNeeded();
	}


	public void removeMajor(Major major) {
		majorsList.remove(major);
		if(!major.name.equals("GER")){
			recalcGERMajor();
		}
		updatePrereqs();//courses might be removed?
		updateReqs();
		updateTotalCoursesNeeded();
	}

	public int determineGER(){
		int highestDegree = -1;
		for(Major m: this.majorsList){
			if(!m.name.equals("GER")){
				if(m.getChosenDegree() > highestDegree){
					highestDegree=m.getChosenDegree();
				}

			}
		}
		return highestDegree;
	}
	
	///////////////////////////////
	///////////////////////////////
	//	Completion Status
	///////////////////////////////
	///////////////////////////////
	
	@SuppressWarnings("unused")
	private boolean ______completionStatus_________;

	/**
	 * Return a number between 0 and 1 representing the completion level.
	 * @param iconHeight
	 * @return
	 */
	public double getPercentDone() {
		int leftToDo = this.estimatedCoursesLeft();
		int done = totalCoursesNeeded-leftToDo;
		double result = (done*1.0)/totalCoursesNeeded;
		return result;
	}

	public boolean isComplete(){
		if(getPercentDone() > 1.0 - 0.000000000001){
			if(majorsList.size() >= 1){
				return true;
			}
		}
		return false;
	}





	/*				^		^		^
	 * 				|		|		|
	 * 				|		|		|
	/////////////////////////////////////////////
	/////////////////////////////////////////////
	/////////////////////////////////////////////
	//////////	Methods called by the GUI  //////
	/////////////////////////////////////////////
	/////////////////////////////////////////////
	/////////////////////////////////////////////
	 */

	@SuppressWarnings("unused")
	private boolean ___EndMethodsUsedByGUI_________;





















	///////////////////////////////
	///////////////////////////////
	//	Nice getters
	///////////////////////////////
	///////////////////////////////
	@SuppressWarnings("unused")
	private boolean ___GettersAndSetters_________;
	
	/**
	 * This does not include prior Semester, because that is not apart of this.semesters
	 * @return The first real (not prior Semester)
	 */
	public Semester getStartSemester(){
		//TODO is the sort necessary?
		Collections.sort(this.semesters);
		return this.semesters.get(0);
	}

	

	public Semester getPriorSemester() {
		return priorSemester;
	}




	public SemesterDate getCurrentSemester() {
		return currentSemester;
	}


	public void setCurrentSemester(SemesterDate currentSemester) {
		this.currentSemester = currentSemester;
	}



	/**
	 * Find the list of all ScheduleElements in any semester of this Schedule.
	 * Will be sorted based on the time the element was scheduled.
	 * @return
	 */
	public ArrayList<ScheduleElement> getAllElementsSorted(){
		ArrayList<ScheduleElement> result = new ArrayList<ScheduleElement>();
		for(Semester s : this.getAllSemestersSorted()){
			result.addAll(s.getElements());
		}
		return result;
	}


	/**
	 * Find the list of all requirements in any major of this schedule.
	 * May include duplicate requirements if two majors share a requirement.
	 * Includes prereq requirements too.
	 * 
	 * Doesn't update requirements
	 * 		it can't, because it would cause an infinite loop.
	 * @return
	 */
	public ArrayList<Requirement> getAllRequirements(){
		ArrayList<Requirement> result = getAllRequirementsMinusPrereqs();
		for(Prereq p : prereqs){
			result.add(p.getRequirement());
		}
		return result;
	}
	public ArrayList<Requirement> getAllRequirementsMinusPrereqs(){
		ArrayList<Requirement> result = new ArrayList<Requirement>();
		for(Major m : this.majorsList){
			result.addAll(m.reqList);
		}
		result.addAll(GER.reqList);
		return result;
	}


	/**
	 * Return the list of semesters sorted in chronological order.
	 * PriorSemester is earlier than all other semesters.
	 */
	public ArrayList<Semester> getAllSemestersSorted(){
		ArrayList<Semester> allSemesters = new ArrayList<Semester>();
		allSemesters.add(priorSemester);
		Collections.sort(this.semesters);
		allSemesters.addAll(this.semesters);
		return allSemesters;
	}
	

	/**
	 * doesn't update the requirements (this would cause an infinite loop, 
	 * updating a requirement means knowing all requirements, which can't happen unless
	 * you get all majors.)
	 * @return
	 */
	public ArrayList<Major> getMajors(){
		ArrayList<Major> result = new ArrayList<Major>();
		if(GER != null){
			result.add(GER);
		}
		if(prereqs.size() > 0){
			Major prereqsM = new Major("Prereqs");
			prereqsM.chosenDegree = -1;
			HashSet<Requirement> uniquePrereqs = new HashSet<Requirement>();
			for(Prereq p : prereqs){
				if(!p.getRequirement().getStoredIsComplete()){
					uniquePrereqs.add(p.getRequirement());
				}
			}
			for(Requirement r : uniquePrereqs){
				prereqsM.addRequirement(r);
			}
			Collections.sort(prereqsM.reqList);
			if(uniquePrereqs.size() > 0){
				result.add(prereqsM);
			}
		}
		result.addAll(this.majorsList);
		return result;
	}



	public void setLanguagePrefix(Prefix languagePrefix) {
		//The language classes prereq diagram looks like this:
		// arrows a --> b can be read as "a needs b"
		//  201 --> 120 --> 110
		// 					 
		//           115
		
		
		//String[] Language = {"110", "120", "201"};
		
		Integer.parseInt(languagePrefix.getNumber()); 
		//if the prefix can't be made into a terminal req of the form
		// FRN > 201, then we need to know sooner rather than later.
		
		this.languagePrefix = languagePrefix;
		recalcGERMajor();
		
		/**
		 * This was used to add the prereqs for language placement instead we added
		 * that placement class to the prereqs of the highest language class. 
		 * 
		 * //Figure out the index of the given prefixe's number,
			// so if you were given 120 savedLocation = 1.
		int savedLocation = -1;
		for(int i=0; i<Language.length; i++){
			if(languagePrefix.getNumber().equals(Language[i])){
				savedLocation=i;
			}
		}
		 * 
		 * //add all the things before it to your prior courses, so if you got
		// placed in 120, we'll add 110 to your prior courses,
		// and if you got placed in 201, we'll add 120 and 110 to your prior courses.
		if(savedLocation != -1){
			for(int p=0; p<savedLocation; p++){
				Course c= new Course(new Prefix(languagePrefix.getSubject(), "PL."+Language[p]), priorSemester.semesterDate, null, null, 
						0, null);
				ScheduleCourse cc = new ScheduleCourse(c, this);
				//cc.setTaken(true);
				addScheduleElement(cc,priorSemester);
			}
		}
		//Assume that if the prefix isn't in Language, then it's higher than 201.
		else if((!languagePrefix.getNumber().equals("115"))){
			for(int p=0; p<Language.length; p++){
				Course c= new Course(new Prefix(languagePrefix.getSubject(), "PL."+ Language[p]), this.getAllSemestersSorted().get(0).semesterDate, null, null, 
						0, null);
				ScheduleCourse cc = new ScheduleCourse(c, this);
				//	cc.setTaken(true);
				priorSemester.add(cc);
			}
		}
		 * 
		 
		 * 
		 */
		
	}

























	///////////////////////////////
	///////////////////////////////
	//	General error checks for GUI events
	///////////////////////////////
	///////////////////////////////
	
	@SuppressWarnings("unused")
	private boolean ___GeneralErrorChecksForGUIEvents_________;
	
	public boolean userOverride(ScheduleError s){
		if(this.schGUI != null){
			return(schGUI.userOverrideError(s));
		}
		else{
			return true;
		}
	}



	public boolean checkErrorsWhenReplacing(Semester oldS, Semester newS, ScheduleElement oldElement, ScheduleElement newElement){
		if(this.checkPrerequsitesReplacing(oldS, newS, oldElement, newElement)){
			return true;
		}


		if(newElement == oldElement){
			if (checkDuplicates(newElement, true, false)){
				return true;
			}
		}
		else{
			if (checkDuplicates(newElement, false, false)){
				return true;
			}
		}


		if(newS.checkOverlap(newElement)){
			return true;
		}

		return false;
	}


	/**
	 * Return true if an error is found.
	 * @param e
	 * @param s
	 * @return
	 */
	public boolean checkErrorsWhenAdding(ScheduleElement e, Semester s){
		if(checkPrerequsitesAdding(e, s.semesterDate)){
			return true;
		}
		if(s.checkOverlap(e)){
			return true;
		}
		if(checkDuplicates(e,false, false)){
			return true;
		}
		if(e instanceof Requirement){
			if(checkOptimismError((Requirement)e)){
				return true;
			}
		}
		return false;
	}

	public boolean checkErrorsWhenRemoving(ScheduleElement e, Semester s){
		return(this.checkPrerequsitesRemoving(e, s.getDate()));
	}



	///////////////////////////////
	///////////////////////////////
	//	Prerequisite error checking
	///////////////////////////////
	///////////////////////////////
	
	@SuppressWarnings("unused")
	private boolean ______prereqErrorChecking_________;

	/**
	 * Check every element to see if it has all the prereqs it needs.
	 * Returns a list of the following form
	 * [ [e1, needed1] , [e2, needed2], [e3, ...]
	 * or null if no prerequsite issues were found.
	 */
	public ArrayList<ScheduleError> checkAllPrerequsites(){
		ArrayList<ScheduleError> result = new ArrayList<ScheduleError>();
		ArrayList<ScheduleElement> taken = new ArrayList<ScheduleElement>();
		//Go through semesters one at a time so that you efficiently
		// build the list of prior-taken elements.
		for(Semester s : getAllSemestersSorted()){
			ArrayList<ScheduleElement> inSemester = new ArrayList<ScheduleElement>();
			for(ScheduleElement e : s.getElements()){
				inSemester.add(e);
			}
			if(this.prereqsCanBeSatisfiedInSameSemester){
				taken.addAll(inSemester);
			}
			for (ScheduleElement e : inSemester){
				Prereq needed = CourseList.getPrereq(e.getPrefix());
				if(needed != null){
					needed.updateOn(taken);
					boolean complete = needed.getRequirement().getStoredIsComplete();
					if(!complete){
						ScheduleError prereq = new ScheduleError(ScheduleError.preReqError);
						prereq.setOffendingCourse(e);
						prereq.setNeededCourses(needed.getRequirement());
						prereq.setOffendingSemester(s);
						result.add(prereq);
					}
				}
			}
			if(!this.prereqsCanBeSatisfiedInSameSemester){
				taken.addAll(inSemester);
			}
		}
		return result;
	}


	/**
	 * Find the Prereq needed for this prefix if taken at the given
	 * time. Return the Prereq after it has been updated based on the
	 * correct set of schedule elements.
	 * 
	 * May return null.
	 * @param p
	 * @param sD
	 * @return
	 */
	public Prereq prereqNeededFor(Prefix p, SemesterDate sD){
		if(p == null){
			return null;
		}
		else{
			ArrayList<ScheduleElement> taken = elementsTakenBefore(sD);
			if(this.prereqsCanBeSatisfiedInSameSemester){
				taken.addAll(elementsTakenIn(sD));
			}
			Prereq needed = CourseList.getPrereq(p);
			if(needed != null){
				needed.updateOn(taken);
			}
			return needed;
		}
	}
	

	/**
	 * Check if any prereq errors happen as a result of adding (not replacing or removing)
	 * element e to the schedule at date SD.
	 * @param e
	 * @param sD
	 * @return
	 */
	public boolean checkPrerequsitesAdding(ScheduleElement e, SemesterDate sD){
		//The only possible errors are if e itself has a prereq - 
		// we can't mess up any other courses by just adding.
		Prereq needed = prereqNeededFor(e.getPrefix(), sD);
		//needed will have correct storedMinMoreNeeded values at this point.
		if(needed == null){
			return false; //no errors found
		}
		if(!needed.getRequirement().getStoredIsComplete()){
			ScheduleError preReq = new ScheduleError(ScheduleError.preReqError);
			preReq.setOffendingCourse(e);
			preReq.setNeededCourses(needed.getRequirement());
			//	preReq.setInstructions(e.getDisplayString() + " needs prerequisite(s)" + needed.toString());
			return(!this.userOverride(preReq));
			//throw new PrerequsiteException(needed, e);
		}
		return false;
	}

	/**
	 * Check if any prereq errors happen as a result of removing (not adding or replacing) 
	 * element e to the schedule at date SD.
	 * @param e
	 * @param s
	 * @return
	 */
	public boolean checkPrerequsitesRemoving(ScheduleElement e, SemesterDate SD){
		//A remove might mess up a course after e, but can't hurt courses
		// before e.
		Prefix currentP = e.getPrefix();
		if(currentP == null){
			return false; //null prefix can't satisfy any prereqs.
		}
		ArrayList<ScheduleElement> taken = new ArrayList<ScheduleElement>();
		for(Semester other : this.getAllSemestersSorted()){
			//if this semester is after s
			if( (prereqsCanBeSatisfiedInSameSemester && other.getDate().compareTo(SD) == 0 )
					||  other.getDate().compareTo(SD) > 0 ){
				//add all the elements in this semester into taken, leaving out e.
				for(ScheduleElement laterElement : other.elements){
					if(other.getDate().compareTo(SD) != 0 || (!laterElement.equals(e))){
						taken.add(laterElement);
					}
				}
				//compare the prereqs needed if you include e to the
				// prereqs needed if you leave e out. If they're different,
				// throw a prereq error.
				ArrayList<ScheduleElement> causes = new ArrayList<ScheduleElement>();
				for(ScheduleElement laterElement  : other.elements){
					Prereq needed = CourseList.getPrereq(laterElement.getPrefix());
					if(needed == null){
						continue;
					}
					needed.updateOn(taken);
					int neededWithoutE = needed.getRequirement().getStoredNumberLeft();
					taken.add(e);
					needed.updateOn(taken);
					int neededWithE = needed.getRequirement().getStoredCoursesLeft();
					taken.remove(taken.size() - 1);
					
					if(neededWithoutE != neededWithE){
						causes.add(laterElement);
					}
				}
				for( int i = 0; i < causes.size() ; i ++){
					ScheduleError preReq = new ScheduleError(ScheduleError.preReqError);
					preReq.setOffendingCourse(causes.get(i));
					preReq.setNeededCourses(new TerminalRequirement(e.getPrefix()));
					//		preReq.setInstructions(e.getDisplayString() + " needs prerequisit(s) " + needed.toString());
					if(!this.userOverride(preReq)){
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * If an error is found, return true.
	 * @param fromSem
	 * @param toSem
	 * @param oldE
	 * @param newE
	 * @return
	 */
	public boolean checkPrerequsitesReplacing(Semester fromSem, Semester toSem, 
			ScheduleElement oldE, ScheduleElement newE){
		Prefix newP = newE.getPrefix();
		Prefix oldP = oldE.getPrefix();
		
		
		//If the prefixes aren't the same,
		// then as far as prereqs care,
		// we're just doing an add and a remove.
		if(newP == null || oldP == null || !(newP.equals(oldP))){
			return (checkPrerequsitesRemoving(oldE, fromSem.getDate()) ||checkPrerequsitesAdding(newE, toSem.semesterDate));
		}
		//If the prefixes are equal, we're really moving the time we take this prefix.
		else{
			if(fromSem.semesterDate.compareTo(toSem.semesterDate) == 0){
				return false;// no errors.
			}
			//If we're moving the course backward in time 
			// we might no longer satisfy its prereqs.
			if(fromSem.semesterDate.compareTo(toSem.semesterDate) > 0){
				Prereq stillNeeded = prereqNeededFor(newP, toSem.semesterDate);
				Prereq neededBefore = prereqNeededFor(oldP, fromSem.semesterDate);
				if(stillNeeded == null){
					 return false;
				}
				int stillNeededNum = stillNeeded.getRequirement().getStoredNumberLeft();
				int neededBeforeNum = neededBefore.getRequirement().getStoredNumberLeft();

				if(stillNeededNum != neededBeforeNum){
					ScheduleError preReq = new ScheduleError(ScheduleError.preReqError);
					preReq.setOffendingCourse(newE);
					preReq.setNeededCourses(stillNeeded.getRequirement());
					//	preReq.setInstructions(newE.toString() + " has prerequisite " + missing.toString());
					return((!this.userOverride(preReq)));
				}
				else{
					return false;
				}
			}
			//If we're moving the course forward in time. 
			//the only courses that need to be checked are those in between the new position and
			//the old position of the moving course, these are the only
			//courses that might loose a prereq by this operation. 

			//prefixes between oldSem and newSem.
			// In the following code, new refers to the new location for the element, and 
			// to the semester who's date comes later. (2020 is new, 2018 is old).
			ArrayList<ScheduleElement> beforeTo = this.elementsTakenBefore(toSem.semesterDate);
			ArrayList<ScheduleElement> afterFrom = this.elementsTakenAfter(fromSem.semesterDate);
			ArrayList<ScheduleElement> inFrom = this.elementsTakenIn(fromSem);
			if(this.prereqsCanBeSatisfiedInSameSemester){
				afterFrom.addAll(inFrom);
			}
			afterFrom.retainAll(beforeTo);
			ArrayList<ScheduleElement> intersection = afterFrom;

			//for each element we're jumping over, check if
			// we satisfied one of that element's prereqs.
			HashSet<ScheduleElement> elementsThatUsedTheMovingElement = new HashSet<ScheduleElement>();
			for(ScheduleElement e : intersection){
				Prereq p = CourseList.getPrereq(e.getPrefix());
				if(p != null && p.getRequirement().isSatisfiedBy(newE)){
					elementsThatUsedTheMovingElement.add(e);
				}
			}
			for(ScheduleElement e : elementsThatUsedTheMovingElement){
				ScheduleError preReq = new ScheduleError(ScheduleError.preReqError);
				preReq.setOffendingCourse(e);
				preReq.setNeededCourses(new TerminalRequirement(newP));
				if(!this.userOverride(preReq)){
					return true;
				}
			}
			return false;
		}
	}


	/**
	 * All prefixes taken before or including this semester.
	 * @param sd
	 * @return
	 */
	public ArrayList<ScheduleElement> elementsTakenBefore(SemesterDate sd){
		ArrayList<ScheduleElement> taken = new ArrayList<ScheduleElement>();
		for(Semester s : this.getAllSemestersSorted()){
			// Allow courses taken before or in the same semester.
			if(s.semesterDate.compareTo(sd) < 0){
				taken.addAll(elementsTakenIn(s));
			}
		}
		return taken;
	}
	/**
	 * All prefixes scheduled strictly after this semester.
	 * @param sd
	 * @return
	 */
	public ArrayList<ScheduleElement> elementsTakenAfter(SemesterDate sd){
		ArrayList<ScheduleElement> taken = new ArrayList<ScheduleElement>();
		for(Semester s : this.getAllSemestersSorted()){
			// Allow courses taken before or in the same semester.
			if(s.semesterDate.compareTo(sd) > 0){
				taken.addAll(elementsTakenIn(s));
			}
		}
		return taken;
	}
	public ArrayList<ScheduleElement> elementsTakenIn(SemesterDate sD){
		ArrayList<ScheduleElement> taken = new ArrayList<ScheduleElement>();
		for(Semester s : this.getAllSemestersSorted()){
			// Allow courses taken before or in the same semester.
			if(s.semesterDate.compareTo(sD) == 0){
				taken.addAll(elementsTakenIn(s));
			}
		}
		return taken;
	}
	public ArrayList<ScheduleElement> elementsTakenIn(Semester s){
		ArrayList<ScheduleElement> result = new ArrayList<ScheduleElement>();
		result.addAll(s.getElements());
		return result;
	}



















	///////////////////////////////
	///////////////////////////////
	//	Duplicate error checking
	///////////////////////////////
	///////////////////////////////
	@SuppressWarnings("unused")
	private boolean ______duplicateErrorChecking_________;


	/**
	 * Check all semesters for duplicates.
	 * If one is found, throw an exception.
	 */
	public boolean  checkDuplicates(){
		ArrayList<ScheduleError> result = checkAllDuplicates();
		for(ScheduleError s: result){
			if(checkDuplicates(s.getOffendingCourse(), true, false) == false){
				return true;
			}
		}
		return false;
	}

	public ArrayList<ScheduleError> checkAllDuplicates(){
		ArrayList<ScheduleError> result = new ArrayList<ScheduleError>();
		for(ScheduleElement element : getAllElementsSorted()){
			if(checkDuplicates(element, true, true)){
				ScheduleError duplicate = new ScheduleError(ScheduleError.duplicateError);
				duplicate.setOffendingCourse(element);
				result.add(duplicate);


			}
		}
		return(result);
	}

	public boolean checkDuplicates(ScheduleElement e, boolean alreadyAdded, boolean hideUserOverride){
		int exactDuplicateCount = 1;
		if(alreadyAdded){
			exactDuplicateCount = 0;
		}

		for(ScheduleElement e1 : getAllElementsSorted()){
			if(e1 == e){
				exactDuplicateCount++;
				if(exactDuplicateCount > 1){
					ScheduleElement[] result = {e, e1};
					if(e1.isDuplicate(e) || e.isDuplicate(e1)){
						ScheduleElement[] results = {e, e1};
						ScheduleError duplicate = new ScheduleError(ScheduleError.duplicateError);
						duplicate.setOffendingCourse(results[0]);
						//	duplicate.setInstructions(results[0].getDisplayString() + " duplicates " + results[1]	);
						duplicate.setElementList(results);
						if(hideUserOverride == false){
							return(!this.userOverride(duplicate));
						}
						else{
							return(true);
						}
					}
				}
				continue;
			}
			if(e1.isDuplicate(e) || e.isDuplicate(e1)){
				ScheduleElement[] result = {e, e1};
				ScheduleError duplicate = new ScheduleError(ScheduleError.duplicateError);
				duplicate.setOffendingCourse(result[0]);
				duplicate.setElementList(result);
				//	duplicate.setInstructions(result[0].getDisplayString() + " duplicates " + result[1]	);
				if(hideUserOverride == false){
					return (!this.userOverride(duplicate));
				}
				else{
					return true;
				}
			}
		}
		return false;
	}

	
	

	///////////////////////////////
	///////////////////////////////
	//	Optimism error
	///////////////////////////////
	///////////////////////////////
	@SuppressWarnings("unused")
	private boolean ______optimismErrorChecking_________;



	/**
	 * Check if adding r to the schedule would implicitly assume
	 * some kind of optimal future behavior on the user's part.
	 * 
	 * In this particular method, it checks if r is an 'at least' requirement,
	 * like "5 of A-Z with at least 3 of A-K." If, when we drag r into the schedule,
	 * the only course that could be taken to further the completion of r is in the
	 * subset (A-K is a subset of A-Z), then we're subtly requiring the user to
	 * take an A-K class when they're seeing a different requirement in the schedule.
	 * 
	 * This method assumes that r
	 * is not yet in getAllElementsSorted().
	 * @param r
	 * @return
	 */
	public boolean checkOptimismError(Requirement r){
		if(r.getStoredIsComplete()){
			return false;
		}
		//If adding this requirement to the schedule would entail a leap of faith
		ArrayList<Requirement> pairs = r.atLeastRequirementPairs();
		if(pairs.isEmpty()){
			return false;
		}
		Requirement superset = pairs.get(0);
		Requirement subset = pairs.get(1);
		ArrayList<ScheduleElement> allCurrentElements = this.getAllElementsSorted();

		//First, if subset is already complete then we're not
		// assuming any optimal behavior - we're just scheduling a
		// superset-only course.
		if(subset.isComplete(allCurrentElements, false)){
			//We've already completed subset, so r isn't making the schedule
			// assume any optimal behavior. 
			return false;
		}


		//At this point, we know that r has at least one subsetOnly course to schedule.
		//The next test checks if adding r would force us take a subset-only course,
		// or if we might get away by just taking a superset-only course.
		//replace instances of r with instances of superset so that the call to 
		//minMoreNeeded will work.
		for(int i = 0; i < allCurrentElements.size() ; i ++){
			if(r.equals(allCurrentElements.get(i))){
				allCurrentElements.set(i,  superset);
			}
		}
		if(superset.minMoreNeeded(allCurrentElements, false) > subset.getOriginalNumberNeeded()){
			//We still need more classes to fill out superset-only, so we can 
			// pretend that this requirement stands for a member of superset
			// rather than standing for a member of subset-only.
			return false;
		}

		//We are now left with only one possibility:
		// The only class that this requirement could count for is
		// one of the classes in subset - if we say nothing, then
		// this requirement will act as a planned member of subset.
		// So we should alert the user to this and ask them what they think.
		ScheduleError optimismError = new ScheduleError(ScheduleError.optimisticSchedulerError);
		optimismError.setOptimisticRequirement(r);
		return (! schGUI.userOverrideError(optimismError));

	}




	///////////////////////////////
	///////////////////////////////
	//	Course overlap error checking
	///////////////////////////////
	///////////////////////////////
	@SuppressWarnings("unused")
	private boolean ______courseOverlapErrorChecking_________;

	/**
	 * Check all semesters to see if any elements in them
	 * have overlapping times (are taken at the same time)
	 * This method will not cause any errors to be displayed to the user.
	 */
	public ArrayList<ScheduleError> checkOverlap(){
		ArrayList<ScheduleError> result = new ArrayList<ScheduleError>();
		//This check intentionally does not check for overlap in PriorSemester
		for(Semester s : semesters){
			result.addAll(s.checkAllOverlap());
		}
		return result;
	}


	
	
	
	///////////////////////////////
	///////////////////////////////
	//	Updating methods
	///////////////////////////////
	///////////////////////////////
	@SuppressWarnings("unused")
	private boolean ___Updates_________;

	private void recalcGERMajor(){
		int type = this.determineGER();
		if(type == -1){
			type = Major.BA;
		}
		this.GER =CourseList.getGERMajor(languagePrefix, type);
		updateTotalCoursesNeeded();
	}

	private void updateTotalCoursesNeeded(){
		totalCoursesNeeded = 0;
		
		for(Requirement r : getAllRequirements()){
			
			totalCoursesNeeded += r.getOriginalCoursesNeeded();
		}
	}

	///////////////////////////////
	///////////////////////////////
	//	Keeping requirements up to date
	///////////////////////////////
	///////////////////////////////
	// Two main booleans are used here:
	// reqsValid and reqsFulfilledValid.
	// 
	// reqsValid tells if all the 
	// requirements in all majors are up-to-date
	// and know how many scheduled elements are currently
	// satisfying them
	//
	// reqsFulfilledValid tells if each scheduleElement knows
	// which requirements it satisfies.
	@SuppressWarnings("unused")
	private boolean ______updateRequirements_________;


	/**
	 * Forces a full update of all of the requirements,
	 * ensuring that all majors know which relevant 
	 * courses have been taken.
	 * 
	 * Also updates the prereqs 
	 *  
	 */
	private void updateReqs(){
		//This list cannot be a set because we need duplicate requirements
		// to potentially be satisfied twice.
		ArrayList<ScheduleElement> allTakenElements = getAllElementsSorted();
		//Same issue here.
		ArrayList<Requirement> reqList = this.getAllRequirementsMinusPrereqs();
		ArrayList<ArrayList<Requirement>> reqsFulfilled = new ArrayList<ArrayList<Requirement>> ();
		for(ScheduleElement e: allTakenElements){
			if(e instanceof ScheduleCourse){
				if (((ScheduleCourse) e).c.name.contains("Roman")){
					ArrayList<Requirement> filter = e.filterEnemyRequirements(reqList);
					System.out.println("updateReqs " + filter);
					System.out.println("updateReqs" + filter.size() );
				}
			}
			reqsFulfilled.add(e.filterEnemyRequirements(reqList));
		}
		for(Requirement r : reqList){
			updateRequirement(r, allTakenElements, reqsFulfilled);
		}

		updatePrereqs();
	}

	/**
	 * check if anything needs to be updated, and update if it does.
	 */
	public void checkUpdateReqs(){
		/*if(!reqsValid){*/
		updateReqs();
		//}
	}

	/**
	 * Count the number of currently placed schedule elements
	 * satisfying this requirement.
	 * @param r
	 */
	public void updateRequirement(Requirement r, 
			 ArrayList<ScheduleElement> allTakenElements, ArrayList<ArrayList<Requirement>> reqsFulfilled){
		//These last two are passed in for speed issues. 
		//For each requirement, find all the schedule elements that satisfy it
		// (this prevents enemy requirements from both seeing the same course)s
		
		//Courses that don't have enemies, and exclude courses that do have enemies
		ArrayList<ScheduleElement> satisficers = new ArrayList<ScheduleElement>();

		for(int i=0; i<allTakenElements.size();i++){
			ScheduleElement e = allTakenElements.get(i);
			if(reqsFulfilled.get(i).contains(r)){
				satisficers.add(e);
			}
		}
		r.updateAllStoredValues(satisficers);
	}


	/**
	 * Get an estimate of the number of scheduleElements still needed for this
	 * schedule to be complete. This is probably an overestimate (it can 
	 * be done in fewer steps) because some elements may be able to satisfy
	 * multiple requirements at once.
	 * @return
	 */
	public int estimatedCoursesLeft(){
		int counter = 0;
		for(Requirement r: this.getAllRequirements()){
			counter += r.getStoredCoursesLeft();
		}
		return counter;
	}

	/**
	 * /**
	 * Tells this scheduleElement the list of requirements that it satisfies.
	 * 		this list is a subset of the requirements list implied by the set
	 * 		of majors. So, if an element satisfies the MTH-elective requirement and
	 * 		the MTH-GER requirement, and the schedule  (at this point) has the
	 * 		GER major but not the math major, then after calling this method
	 * 		on element e, e.getRequirementsSatisfied() will return {MTH_GER}.
	 * 
	 * @param e
	 */
	/*public void updateRequirementsSatisfied(ScheduleElement e){
		if(e instanceof Course){
			updateRequirementsSatisfied((Course) e);
		}
		if(e instanceof Requirement){
			updateRequirementsSatisfied((Requirement)e);
		}
	}
	private void updateRequirementsSatisfied(Requirement r){
		//TODO fill this out.
	}

	private void updateRequirementsSatisfied(Course c){
		ArrayList<Requirement> allSatisfied = new ArrayList<Requirement>();
		for(Requirement r : this.getAllRequirements()){
			if(r.isSatisfiedBy(c.getPrefix())){
				allSatisfied.add(r);
			}
		}
		// If this course satisfies two requirements that don't play well with
		// each other, then we don't want to add them to the list.
		// Rather, the user will be prompted to tell us which one they want
		// to be satisfied.
		ArrayList<Requirement> toRemove = new ArrayList<Requirement>();
		for(int i = 0; i < allSatisfied.size() ; i ++){
			for(int j = i+1 ; j < allSatisfied.size(); j ++){
				if(this.dontPlayNice(allSatisfied.get(i), allSatisfied.get(j))){
					toRemove.add(allSatisfied.get(i));
					toRemove.add(allSatisfied.get(j));
				}
			}
		}
	}
	 */

	public boolean dontPlayNice(Requirement r1, Requirement r2){
		return !RequirementGraph.doesPlayNice(r1, r2);
	}


	public void updatePrereqs(){
		//Make sure prereqs accurately reflect the currently scheduled elements.
		prereqs = new HashSet<Prereq>();
		for(ScheduleElement e : getAllElementsSorted()){
			//Taken courses don't need prereqs loaded.
			if(e instanceof ScheduleCourse){
				if(((ScheduleCourse) e).getSemester().compareTo(currentSemester)<0){
					continue;
				}
			}
			Prereq p = CourseList.getPrereq(e.getPrefix());
			if(p != null){
				prereqs.add(p);
			}
		}

		//Actually update the prereqs.
		for(Prereq p : prereqs){
			updatePrereq(p);
		}
	}

	public void updatePrereq(Prereq p){
		ArrayList<ScheduleElement> elementsBefore = this.elementsBefore(p.getPrefix(), prereqsCanBeSatisfiedInSameSemester);
		p.updateOn(elementsBefore);
	}






	//TODO use streams
	public ArrayList<Major> filterAlreadyChosenMajors(ArrayList<Major> collectionOfMajors ) {
		collectionOfMajors.removeAll(this.getMajors());
		return collectionOfMajors;
	}

	public ArrayList<ScheduleCourse> filterAlreadyChosenCourses(ArrayList<ScheduleCourse> collectionOfCourses){
		collectionOfCourses.removeAll(this.getAllElementsSorted());
		return collectionOfCourses;
	}


	public boolean SemesterAlreadyExists(SemesterDate semesterDate) {
		for(Semester s: this.semesters){
			if(s.semesterDate.equals(semesterDate)){
				return true;
			}
		}
		return false;
	}


	/**
	 * Find the set of requirements that this course should satisfy, given the
	 * list of requirement enemies that are trying to be fulfilled by this course.
	 * @param enemies
	 * @param c
	 * @return
	 */
	public HashSet<Requirement> resolveConflictingRequirements(HashSet<Requirement> enemies, Course c){
		HashSet<Requirement> result = new HashSet<Requirement>();
		ArrayList<Requirement> reqs = new ArrayList<Requirement>();
		ArrayList<Major> majors = new ArrayList<Major>();
		//associate each requirement with its major.
		for(Requirement r : enemies){
			boolean found = false;
			for(Major m : this.getMajors()){
				if(found != true){
					if(m.reqList.contains(r)){
						found = true;
						reqs.add(r);
						majors.add(m);
					}
				}
			}
			if(found == false){
				throw new RuntimeException("I couldn't find the major for this requirement" + r);
			}
		}
		if(this.schGUI != null){
			//Ask the user to pick for us.
			return schGUI.GUIResolveConflictingRequirements(reqs, majors, c);
		}
		else{
			//If we try to resolve conflicts on our own, this is where we would do it.
			return new HashSet<Requirement>();
		}
	}


	public  ArrayList<ScheduleError> checkAllErrors(){
		ArrayList<ScheduleError> allErrors = new ArrayList<ScheduleError>();
		//Don't check for errors in PriorSemester
		for(Semester s: this.semesters){
			ArrayList<ScheduleError> overlap = s.checkAllOverlap();
			if(overlap!=null){
				allErrors.addAll(s.checkAllOverlap());
			}
			ScheduleError overload = s.checkOverload(null);
			if(overload != null){
				allErrors.add(overload);	
			}
		}
		allErrors.addAll(this.checkAllPrerequsites());
		allErrors.addAll(this.checkAllDuplicates());
		return allErrors;
	}



	public ArrayList<ScheduleCourse> getCoursesInSemester(Semester s) {
		ArrayList<Course> toFinal = CourseList.getCoursesIn(s);
		ArrayList<ScheduleCourse> scheduleCoursesInSemester = new ArrayList<ScheduleCourse>();
		for(Course c: toFinal){
			ScheduleCourse sc = new ScheduleCourse(c, this);
			scheduleCoursesInSemester.add(sc);
		}
		return scheduleCoursesInSemester;
	}




	public String printScheduleString(){
		StringBuilder result = new StringBuilder();
		//Add Majors

		if(!this.majorsList.isEmpty()){
			result.append("<table style = 'width: 100%'> <tr>");
			result.append( "<th> Major:</th> " +
					" <th>Notes:</th> " +
					"</tr>");
			for(Major m: this.majorsList){
				result.append("<tr Align='left'> <td>" + m.name + "</td>  ");
				if(m.notes != null){
					result.append("<td>" + m.notes + "</td>");
				}
				result.append("</tr>");

			}
			result.append("</table>");
		}

		result.append("<center> <h2> Schedule </h2> </center> ");
		//Adds all the scheduleElements from each major
		for(Semester s: this.getAllSemestersSorted()){
			result.append("\n");
			if(s.isPriorSemester){
				result.append("<b>Prior Courses: </b>" + "\n");
			}
			else{
				result.append("<b>" + s.semesterDate.toString() + ": </b> \n");
			}
			if(s.studyAway){
				result.append("<b> STUDY AWAY SEMESTER </b>\n");
			}
			for(ScheduleElement se : s.elements){
				String prefix = "  ";
				if(se instanceof Requirement){
					prefix = "  Scheduled one course of: ";
					result.append(prefix + se.shortString(1000) + "\n");
				}
				else{
					result.append(se.getDisplayString() + "\n");
				}
			}
			if(s.elements.isEmpty()){
				result.append("Nothing scheduled for this semester \n");
			}
			if(s.hasNotes()){
				result.append("<b> Notes: </b>" +  s.notes + "\n");
			}

		}
		result.append("\n");
		//If any Errors Prints them 
		if(!schGUI.getErrorStrings().equals("")){
			result.append("<b> Scheduling Errors: </b> <br>" + schGUI.getErrorStrings());
		}
		//Things left CLPS, Estimated Courses Left, CrditHours
		result.append("\n <h3>The Final Countdown: </h3>");
		result.append("<b> Estimated Courses Left: </b> " + Math.max(0, this.estimatedCoursesLeft()) + "\n");
		result.append("<b> Credit Hours Left:</b> " +  Math.max(0, (128 - this.getCreditHoursComplete())) + "\n");




		String toResult = result.toString().replaceAll("&", "&amp;");
		return toResult.replaceAll("\n", "<br>");



	}



	public String printRequirementString(){
		SemesterDate defaultPrior = new SemesterDate(1995, SemesterDate.OTHER);
		ArrayList<ScheduleElement> allOrderedElements = new ArrayList<ScheduleElement>();
		ArrayList<SemesterDate> coorespondingDates = new ArrayList<SemesterDate>();
		for(Semester s: this.getAllSemestersSorted()){
			for(ScheduleElement se: s.elements){
				allOrderedElements.add(se);
				if(s.isPriorSemester){
					coorespondingDates.add(defaultPrior);

				}
				else {
					coorespondingDates.add(s.semesterDate);
				}
			}
		}
		StringBuilder result = new StringBuilder();
		result.append("<h2><center> Degree Checklist \n </center></h2>");
		result.append("<b> General Education Requirements </b>");

		Hashtable<ScheduleElement, HashSet<Requirement>> elementsSatisfy = new Hashtable<ScheduleElement, HashSet<Requirement>>();
		for(ScheduleElement e : this.getAllElementsSorted()){
			elementsSatisfy.put(e, new HashSet<Requirement>(e.filterEnemyRequirements(this.getAllRequirements())));
		}
		for(Major m: this.getMajors()){
			result.append("\n");
			result.append("<b>" + m.name + "</b>");
			ArrayList<Requirement> sortedReq = new ArrayList<Requirement>(m.reqList);
			Collections.sort(sortedReq);
			for(Requirement r: sortedReq){
				String rDisplay = r.shortString(10000) + " -";
				if(rDisplay.length()<=30){
					String spaces = new String (new char[30-rDisplay.length()]).replace("\0", " ");
					rDisplay = rDisplay + spaces;

				}
				result.append("\n <b>" +  rDisplay + "</b>" );

				boolean isComplete = r.getStoredIsComplete();
				if(!isComplete){
					int  coursesNeeded =  r.minMoreNeeded(getAllElementsSorted(), false);
					if(coursesNeeded == 1){
						result.append("<b><font color = '#F75D59'>" + coursesNeeded + " Course Needed </b></font>	\n");
					}
					if(coursesNeeded >1){
						result.append("<b><font color = '#F75D59'>" + coursesNeeded + " Courses Needed </b></font> \n");
					}
				}
				int counter = 0;

				ArrayList<Integer> satisfiedSEPointers = new ArrayList<Integer>();
				for(int i=0; i<this.getAllElementsSorted().size(); i++){
					ScheduleElement se = allOrderedElements.get(i);


					if(elementsSatisfy.get(se).contains(r)){
						satisfiedSEPointers.add(i);
					}
				}

				ArrayList<Integer> finalList = trimSEList(satisfiedSEPointers, allOrderedElements, r);
				for(int p=0; p<finalList.size(); p++){
					ScheduleElement se = allOrderedElements.get(finalList.get(p));
					if(counter ==0 && !isComplete){
						result.append("Partially Satisfied by: \n");
					}
					else if(counter == 0 && isComplete){
						result.append("Satisfied by: \n");
					}


					StringJoiner joiner = new StringJoiner("\n");
					StringBuilder part = new StringBuilder();

					//Different strings for requirements
					String priorIndent = "   ";
					part.append(priorIndent);
					if(se instanceof Requirement){
						part.append("Scheduled  " + se.shortString(10000).trim());


					}
					else{
						part.append(se.getDisplayString().trim());
					}

					//When was this thing taken?
					if(coorespondingDates.get(finalList.get(p)).equals(defaultPrior)){
						part.append(", " + "Taken before Furman \n");
					}
					else{
						part.append(", " + coorespondingDates.get(finalList.get(p)).toString() + "\n");
					}
					joiner.add(part.toString());
					counter++;
					result.append(joiner.toString());
				}





			}

		}


		String toResult = result.toString().replaceAll("&", "&amp;");
		return toResult.replaceAll("\n", "<br>");
	}


	private ArrayList<Integer> trimSEList(ArrayList<Integer> satisfiedSEPointers, ArrayList<ScheduleElement> allOrderedElements, Requirement r) {
		ArrayList<ScheduleElement> toCompleteR = new ArrayList<ScheduleElement>();
		for(int i: satisfiedSEPointers){
			toCompleteR.add(allOrderedElements.get(i));
		}
		for(int i = 0; i<satisfiedSEPointers.size(); i++){
			ScheduleElement toRemove = allOrderedElements.get(satisfiedSEPointers.get(i));
			toCompleteR.remove(i);
			if(!r.isComplete(toCompleteR, false)){
				toCompleteR.add(i, toRemove);
			}
			else{

				satisfiedSEPointers.remove(i);
				i--;
			}

		}
		return satisfiedSEPointers;



	}


	public int getCLP() {
		return CLP;
	}
	public void setCLP(int cLP) {
		CLP = cLP;
	}
	public Prefix getLanguagePrefix() {
		return languagePrefix;
	}





	public int getCreditHoursComplete(){
		int result = 0;
		for (Semester s : this.getAllSemestersSorted()){
			result = result + s.getCreditHours();
		}
		return result;
	}

	public SemesterDate getStartDate(){
		return this.getStartSemester().semesterDate;
	}
	public ArrayList<Semester> getSemesters(){
		return this.semesters;
	}

	/**
	 * used when loading a schedule from a file, in case the details of the
	 * majors changed since the schedule was saved. Probably will be unused
	 * in the final version, but it does offer a neat strategy for seniors
	 * to hold onto majors that were valid when they were freshmen, but are
	 * no longer technically offered.
	 */
	public void reloadMajors() {
		ListOfMajors m = FileHandler.getMajorsList();
		ArrayList<Major> newMajorsList = new ArrayList<Major>();
		for(Major major: this.majorsList){
			Major refreshed = m.getMajor(major.name);
			refreshed.setChosenDegree(major.chosenDegree);
			newMajorsList.add(refreshed);


		}

		setMajorsList(newMajorsList);

	}

	/**
	 * find the first (temporal first) scheduled instance of this prefix,
	 * and return the list of all elements before it.
	 * 
	 * If includeElementsInSameSemester, then the element with prefix p will
	 * also be included in the list.
	 * 
	 * returns empty array list if this prefix isn't scheduled.
	 * 
	 * @param e
	 * @param includeElementsInSameSemester
	 * @return
	 */
	public ArrayList<ScheduleElement> elementsBefore(Prefix p, boolean includeElementsInSameSemester){
		ArrayList<ScheduleElement> result = new ArrayList<ScheduleElement>();
		SemesterDate firstScheduledTime = earliestInstanceOf(p);
		if(firstScheduledTime == null){
			return result;
		}
		result.addAll(this.elementsTakenBefore(firstScheduledTime));
		if(includeElementsInSameSemester){
			result.addAll(this.elementsTakenIn(firstScheduledTime));
		}
		return result;
	}

	public SemesterDate earliestInstanceOf(Prefix p){
		if(p == null){
			return null;
		}
		for(Semester s : this.getAllSemestersSorted()){
			for(ScheduleElement e : s.getElements()){
				if(p.equals(e.getPrefix())){
					return s.getDate();
				}
			}
		}
		return null;
	}

	public void setMajorsList(ArrayList<Major> majors){
		this.majorsList= majors;
		this.recalcGERMajor();
		this.updatePrereqs();
		this.updateReqs();
		this.updateTotalCoursesNeeded();

	}



	//Collect all the elements before that semester date.


	public Major getGER() {
		return GER;
	}


	public void setGER(Major gER) {
		GER = gER;
	}

}

