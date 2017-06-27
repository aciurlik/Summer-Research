import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;


public class Schedule {
	private ArrayList<Major> majorsList;
	private ArrayList<Semester> semesters;
	public HashSet<Requirement> prereqs;
	private Major GER;
	Driver d; 
	CourseList masterList;
	private int CLP; 
	private Prefix languagePrefix;
	private int totalCoursesNeeded;
	private Semester priorSemester;
	
	private SemesterDate firstSemester;
	public static SemesterDate defaultFirstSemester; //TODO this should be removed after demos.
	private SemesterDate currentSemester;
	private String name;






	public static boolean prereqsCanBeSatisfiedInSameSemester = true;






	//boolean reqsValid; // The set of requirements entailed by all majors is up to date


	public static Schedule testSchedule(){
	//	CourseList l = CourseList.testList();
		Schedule result = new Schedule();
		result.readFromPrior();
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
		this.prereqs = new HashSet<Requirement>();
		//this.masterList = masterList;


		readFromPrior(); //loads first semester and current semester, among others.

		//Semesters
		this.semesters = new ArrayList<Semester>();
		//Sets prior semester as SummerTwo of the same year as the Fall of the First Year, First Semester.
		Semester s = new Semester (new SemesterDate(firstSemester.year, firstSemester.sNumber-1), this);
		for(int i = 0;i < 9 ; i ++){
			if(i==0){
				s.isAP=true;
				priorSemester =s;
			}
			else{
				s = new Semester(firstSemester, this);
				this.semesters.add(s);
				firstSemester = firstSemester.next();
			}


		}
		this.recalcGERMajor();

	}

	public void setDriver(Driver d){
		this.d = d;
	}
	

	public void readFromPrior(){
		//readFromTestPrior();
		readBlankTestPrior();
	}
	
	public void readBlankTestPrior(){
		if(defaultFirstSemester!= null){
			firstSemester = defaultFirstSemester;
		}
		else{
			firstSemester =  new SemesterDate(2016, SemesterDate.FALL);
		}
	}
	
	public void readFromTestPrior(){
		readBlankTestPrior();
		
		
		this.setLanguagePrefix(new Prefix("SPN", "115"));

		//Class One 
		Course a = new Course(new Prefix("THA", 101), new SemesterDate(2016, SemesterDate.FALL), null, null, 4, "03");
		ScheduleCourse aa = new ScheduleCourse(a, this);
		aa.setTaken(true);
		this.addScheduleElement(aa,this.semesters.get(0));


		//Class Two
		Course b = new Course(new Prefix("MTH", 120), new SemesterDate(2016, SemesterDate.FALL), null, null, 4, "01");

		ScheduleCourse bb = new ScheduleCourse(b,this);
		bb.setTaken(true);
		this.addScheduleElement(bb , this.semesters.get(0));



		//Class Three
		Course d = new Course(new Prefix("PSY", 111), new SemesterDate(2016, SemesterDate.FALL), null, null,  4, "03");
		ScheduleCourse dd = new ScheduleCourse(d, this);
		dd.setTaken(true);
		this.addScheduleElement(dd, this.semesters.get(0));
		//result.semesters.add(b);


		this.setCLP(10);
	}
	
	
	
	
	
	
	

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
	public Semester addNewSemester(){
		SemesterDate last = semesters.get(semesters.size() - 1).getDate();
		Semester next = new Semester(last.next(), this);
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
			return false;
		}
		if(sem.add(element)){
			updateRequirementsSatisfied(element);
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



	///////////////////////////////
	///////////////////////////////
	//	General error checks for GUI events
	///////////////////////////////
	///////////////////////////////
	public boolean userOverride(ScheduleError s){
		if(this.d != null){
			return(d.userOverrideError(s));
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
		return false;
	}

	public boolean checkErrorsWhenRemoving(ScheduleElement e, Semester s){
		return(this.checkPrerequsitesRemoving(e, s));
	}





















	///////////////////////////////
	///////////////////////////////
	//	Nice getters
	///////////////////////////////
	///////////////////////////////
	/**
	 * This does not include prior Semester, because that is not apart of this.semesters
	 * @return The first real (not prior Semester)
	 */
	public Semester getStartSemester(){
		Collections.sort(this.semesters);
		return this.semesters.get(0);
	}



	/**
	 * Find the list of all ScheduleElements in any semester of this Schedule.
	 * @return
	 */
	public ArrayList<ScheduleElement> getAllElements(){
		ArrayList<ScheduleElement> result = new ArrayList<ScheduleElement>();
		for(Semester s : this.getAllSemesters()){
			result.addAll(s.getElements());
		}
		return result;
	}

	/**
	 * Find the list of all requirements in any major of this schedule.
	 * May include duplicate requirements if two majors share a requirement.
	 * Can't updte requirements at the same time, because it would cause an infinite loop.
	 * @return
	 */
	public ArrayList<Requirement> getAllRequirements(){
		ArrayList<Requirement> result = new ArrayList<Requirement>();
		for(Major m : this.majorsList){
			result.addAll(m.reqList);
		}
		
		result.addAll(prereqs);
		result.addAll(GER.reqList);
		return result;
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

	/**
	 * This has the prior semester first, and then the "regular" scheduled Semesters second, 
	 * prior should always be in the first place/ 
	 * 
	 */

	public ArrayList<Semester> getAllSemesters(){
		ArrayList<Semester> allSemesters = new ArrayList<Semester>();
		allSemesters.add(priorSemester);
		allSemesters.addAll(this.semesters);
		return allSemesters;

	}

	public void setLanguagePrefix(Prefix languagePrefix) {
		String[] Language = {"110", "120", "201"};
		this.languagePrefix = languagePrefix;
		recalcGERMajor();
		int savedLocation = -1;
		for(int i=0; i<Language.length; i++){
			if(languagePrefix.getNumber().equals(Language[i])){
				savedLocation=i;
			}
		}
		if(savedLocation != -1){
			for(int p=0; p<savedLocation; p++){
				Course c= new Course(new Prefix(languagePrefix.getSubject(), Language[p]), this.getAllSemesters().get(0).semesterDate, null, null, 
						0, null);
				ScheduleCourse cc = new ScheduleCourse(c, this);
				cc.setTaken(true);
				priorSemester.add(cc);
			}

		}
		if((savedLocation == -1) && (!languagePrefix.getNumber().equals("115"))){
			for(int p=0; p<Language.length; p++){
				Course c= new Course(new Prefix(languagePrefix.getSubject(), Language[p]), this.getAllSemesters().get(0).semesterDate, null, null, 
						0, null);
				ScheduleCourse cc = new ScheduleCourse(c, this);
				cc.setTaken(true);
				priorSemester.add(cc);
			}
		}



	}





	private void recalcGERMajor(){
		int type = this.determineGER();
		if(type == -1){
			type = CourseList.BA;
		}
		this.GER =CourseList.getGERMajor(languagePrefix, type);
		updateTotalCoursesNeeded();
	}

	private void updateTotalCoursesNeeded(){
		totalCoursesNeeded = 0;
		ArrayList<ScheduleElement> empty = new ArrayList<ScheduleElement>();
		for(Requirement r : getAllRequirements()){
			totalCoursesNeeded += r.minMoreNeeded(empty, false);
		}
	}























	///////////////////////////////
	///////////////////////////////
	//	Prerequisite error checking
	///////////////////////////////
	///////////////////////////////

	/**
	 * Check every element to see if it has all the prereqs it needs.
	 * Returns a list of the following form
	 * [ [e1, needed1] , [e2, needed2], [e3, ...]
	 * or null if no prerequsite issues were found.
	 */
	public ArrayList<ScheduleError> checkAllPrerequsites(){
		ArrayList<ScheduleError> result = new ArrayList<ScheduleError>();
		ArrayList<ScheduleElement> taken = new ArrayList<ScheduleElement>();
		Collections.sort(semesters);
		//Go through semesters one at a time so that you efficiently
		// build the list of prior-taken elements.
		for(Semester s : getAllSemesters()){
			ArrayList<ScheduleElement> inSemester = new ArrayList<ScheduleElement>();
			for(ScheduleElement e : s.getElements()){
				inSemester.add(e);
			}
			if(this.prereqsCanBeSatisfiedInSameSemester){
				taken.addAll(inSemester);
			}
			for (ScheduleElement e : s.getElements()){
				inSemester.add(e);
				Requirement needed = CourseList.getPrereqsShallow(e.getPrefix());
				if(needed != null){
					boolean complete = needed.isComplete(taken, true);
					if(!complete){
						ScheduleError prereq = new ScheduleError(ScheduleError.preReqError);
						prereq.setOffendingCourse(e);
						prereq.setNeededCourses(needed);
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

	public boolean checkPrerequsitesAdding(ScheduleElement e, SemesterDate sD){
		Requirement needed = prereqsNeededFor(e.getPrefix(), sD);
		if(needed == null){
			return false; //no errors found
		}
		if(!needed.storedIsComplete()){

			ScheduleError preReq = new ScheduleError(ScheduleError.preReqError);
			preReq.setOffendingCourse(e);
			preReq.setNeededCourses(needed);
			//	preReq.setInstructions(e.getDisplayString() + " needs prerequisite(s)" + needed.toString());

			return(!this.userOverride(preReq));
			//throw new PrerequsiteException(needed, e);
		}
		return false;
	}
	
	/**
	 * Find the prereqs needed for this prefix if taken at the given
	 * time.
	 * @param p
	 * @param sD
	 * @return
	 */
	public Requirement prereqsNeededFor(Prefix p, SemesterDate sD){
		if(p == null){
			return null;
		}
		else{
			ArrayList<ScheduleElement> taken = elementsTakenBefore(sD);
			if(this.prereqsCanBeSatisfiedInSameSemester){
				taken.addAll(elementsTakenIn(sD));
			}
			Requirement needed = CourseList.getPrereqsShallow(p);
			if(needed != null){
				needed.updateAllStoredValues(taken);
			}
			return needed;
		}
	}

	public boolean checkPrerequsitesRemoving(ScheduleElement e, Semester s){
		Prefix currentP = e.getPrefix();
		for(Semester other : this.semesters){
			if(other.getDate().compareTo(s.getDate()) >= 0 ){
				for(ScheduleElement oElement : s.getElements()){
					Requirement needed = prereqsNeededFor(oElement.getPrefix(),other.semesterDate);
					if(needed == null){
						return false;
					}
					if(needed.isSatisfiedBy(currentP)){
						ScheduleError preReq = new ScheduleError(ScheduleError.preReqError);
						preReq.setOffendingCourse(e);
						preReq.setNeededCourses(needed);
						//		preReq.setInstructions(e.getDisplayString() + " needs prerequisit(s) " + needed.toString());
						return(!this.userOverride(preReq));
					}
				}
			}
		}
		return false;
	}

	/**
	 * If an error is found, return true.
	 * @param oldSem
	 * @param newSem
	 * @param oldE
	 * @param newE
	 * @return
	 */
	public boolean checkPrerequsitesReplacing(Semester oldSem, Semester newSem, 
			ScheduleElement oldE, ScheduleElement newE){
		//If the elements are equal, we're really moving the time we take it.
		// Check to see if anything happening before the new placement but
		// after the old placement now has an unfilled prereq.
		if(newE.equals(oldE)){
			//If we're moving the course backward in time 
			// we might no longer satisfy its prereqs.
			if(oldSem.semesterDate.compareTo(newSem.semesterDate) >= 1){
				Requirement stillNeeded = prereqsNeededFor(oldE.getPrefix(), newSem.semesterDate);
				
				if(stillNeeded != null && !stillNeeded.storedIsComplete()){
					ScheduleError preReq = new ScheduleError(ScheduleError.preReqError);
					preReq.setOffendingCourse(newE);
					preReq.setNeededCourses(stillNeeded);
					//	preReq.setInstructions(newE.toString() + " has prerequisite " + missing.toString());
					return((!this.userOverride(preReq)));
				}
				else{
					return false;
				}
			}
			//If we're moving the course forward in time. 
			//The only courses that need to be checked are those in between the new position and
			//the old position of the moving course, this is because any other course 
			//has already had an error thrown, and therefore has been checked by the user. 
			
			//prefixes between oldSem and newSem.
			// In the following code, new refers to the new location for the element, and 
			// to the semester who's date comes later. (2020 is new, 2018 is old).
			ArrayList<ScheduleElement> beforeNew = this.elementsTakenBefore(newSem.semesterDate);
			ArrayList<ScheduleElement> afterOld = this.elementsTakenAfter(oldSem.semesterDate);
			ArrayList<ScheduleElement> old = this.elementsTakenIn(oldSem);
			if(this.prereqsCanBeSatisfiedInSameSemester){
				afterOld.addAll(old);
			}
			afterOld.retainAll(beforeNew);
			ArrayList<ScheduleElement> intersection = afterOld;
			
			//for each element we're jumping over, check if
			// we satisfied one of that element's prereqs.
			HashSet<Prefix> elementsThatUsedTheMovingElement = new HashSet<Prefix>();
			for(ScheduleElement p : intersection){
				Requirement r = CourseList.getPrereqsShallow(p.getPrefix());
				if(r != null && r.isSatisfiedBy(newE)){
					elementsThatUsedTheMovingElement.add(p.getPrefix());
				}
			}
			if(!elementsThatUsedTheMovingElement.isEmpty()){
				ScheduleError preReq = new ScheduleError(ScheduleError.preReqError);
				preReq.setNeededCourses(new Requirement(elementsThatUsedTheMovingElement
						.toArray(new Prefix[elementsThatUsedTheMovingElement.size()]),1));
				preReq.setOffendingCourse(newE);
				return(!this.userOverride(preReq));
			}
			return false;

		}
		//If the element's aren't the same,
		// we're really just doing an
		// add and a remove.
		else{
			return (checkPrerequsitesRemoving(oldE, oldSem) ||checkPrerequsitesAdding(newE, newSem.semesterDate));
		}
	}


	/**
	 * All prefixes taken before or including this semester.
	 * @param sd
	 * @return
	 */
	public ArrayList<ScheduleElement> elementsTakenBefore(SemesterDate sd){
		ArrayList<ScheduleElement> taken = new ArrayList<ScheduleElement>();
		for(Semester s : semesters){
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
		for(Semester s : semesters){
			// Allow courses taken before or in the same semester.
			if(s.semesterDate.compareTo(sd) > 0){
				taken.addAll(elementsTakenIn(s));
			}
		}
		return taken;
	}
	public ArrayList<ScheduleElement> elementsTakenIn(SemesterDate sD){
		ArrayList<ScheduleElement> taken = new ArrayList<ScheduleElement>();
		for(Semester s : semesters){
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
		for(ScheduleElement element : getAllElements()){
			if(checkDuplicates(element, true, true)){
				ScheduleError duplicate = new ScheduleError(ScheduleError.duplicateError);
				duplicate.setOffendingCourse(element);
				result.add(duplicate);


			}
		}
		return(result);
	}

	public boolean checkDuplicates(ScheduleElement e, boolean alreadyAdded, boolean isAll){
		int exactDuplicateCount = 1;
		if(alreadyAdded){
			exactDuplicateCount = 0;
		}

		for(ScheduleElement e1 : getAllElements()){
			if(e1 == e){
				exactDuplicateCount++;
				if(exactDuplicateCount > 1){
					ScheduleElement[] result = {e, e1};
					if(e1.isDuplicate(e) || e.isDuplicate(e1)){
						ScheduleElement[] results = {e, e1};
						ScheduleError duplicate = new ScheduleError(ScheduleError.duplicateError);
						//	duplicate.setInstructions(results[0].getDisplayString() + " duplicates " + results[1]	);
						duplicate.setElementList(results);
						if(isAll == false){
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
				duplicate.setElementList(result);
				//	duplicate.setInstructions(result[0].getDisplayString() + " duplicates " + result[1]	);
				if(isAll == false){
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
	//	Course overlap error checking
	///////////////////////////////
	///////////////////////////////

	/**
	 * Check all semesters to see if any elements in them
	 * have overlapping times (are taken at the same time)
	 * This method will not cause any errors to be displayed to the user.
	 */
	public ArrayList<ScheduleError> checkOverlap(){
		ArrayList<ScheduleError> result = new ArrayList<ScheduleError>();
		for(Semester s : semesters){
			result.addAll(s.checkAllOverlap());
		}
		return result;
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


	/**
	 * Forces a full update of all of the requirements,
	 * ensuring that all majors know which relevant 
	 * courses have been taken .
	 *  
	 */
	private void updateReqs(){
		//This list cannot be a set because we need duplicate requirements
		// to potentially be satisfied twice.
		ArrayList<ScheduleElement> allTakenElements = getAllElements();
		ArrayList<Requirement> reqList = this.getAllRequirements();
		for(Requirement r : reqList){
			updateRequirement(r, reqList, allTakenElements);
		}
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
	public void updateRequirement(Requirement r, ArrayList<Requirement> reqList, ArrayList<ScheduleElement> allTakenElements){
		//For each requirement, find all the schedule elements that satisfy it
		// (this prevents enemy requirements from both seeing the same course)s

		//Courses that don't have enemies, and exclude courses that do have enemies
		ArrayList<ScheduleElement> satisficers = new ArrayList<ScheduleElement>();
		for(ScheduleElement e : allTakenElements){
			if(e.getRequirementsFulfilled(reqList).contains(r)){
				satisficers.add(e);
			}
		}

		r.updateAllStoredValues(satisficers);
	}


	/**
	 * Get the number of requirements still left to put in the schedule.
	 * @return
	 */

	public int estimatedCoursesLeft(){
		int counter = 0;
		ArrayList<ScheduleElement> courseEst = this.getAllElements();
		for(Requirement n: this.getAllRequirements()){

			if((n.usesCreditHours)){

				counter += n.storedCoursesLeft()/4;

			}


			else{

				counter += n.storedCoursesLeft();

			}

		}
		return counter;
	}


	/**
	 * Find all the requirements that this ScheduleElement satisfies.
	 * @param e
	 * @return
	 */
	public ArrayList<Requirement> getRequirementsSatisfied(ScheduleElement e){
		return e.getRequirementsFulfilled(getAllRequirements());
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
	public void updateRequirementsSatisfied(ScheduleElement e){
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

	public boolean dontPlayNice(Requirement r1, Requirement r2){
		return !RequirementGraph.doesPlayNice(r1, r2);
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
			for(Requirement r : prereqs){
				prereqsM.addRequirement(r);
			}
			result.add(prereqsM);
		}
		result.addAll(this.majorsList);
		return result;
	}


	public void updatePrereqs(){
		prereqs = new HashSet<Requirement>();
		for(ScheduleElement e : getAllElements()){
			Requirement r = CourseList.getPrereqsShallow(e.getPrefix());
			if(r != null){
				prereqs.add(r);
			}
		}
	}





	public int getCreditHoursComplete(){
		int result = 0;
		for (Semester s : this.getAllSemesters()){
			result = result + s.getCreditHours();
		}
		return result;
	}

	public SemesterDate getStartDate(){
		return this.semesters.get(0).semesterDate;
	}
	public ArrayList<Semester> getSemesters(){
		return this.semesters;
	}







	public ArrayList<Major> filterAlreadyChosenMajors(ArrayList<Major> collectionOfMajors ) {
		collectionOfMajors.removeAll(this.getMajors());
		return collectionOfMajors;

	}

	public ArrayList<ScheduleCourse> filterAlreadyChosenCourses(ArrayList<ScheduleCourse> collectionOfCourses){
		collectionOfCourses.removeAll(this.getAllElements());
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
		if(this.d != null){
			//Ask the user to pick for us.
			return d.GUIResolveConflictingRequirements(reqs, majors, c);
		}
		else{
			//If we try to resolve conflicts on our own, this is where we would do it.
			return new HashSet<Requirement>();
		}
	}


	public  ArrayList<ScheduleError> checkAllErrors(){
		ArrayList<ScheduleError> allErrors = new ArrayList<ScheduleError>();
		for(Semester s: this.semesters){
			if(s.checkAllOverlap()!=null){
				allErrors.addAll(s.checkAllOverlap());
			}
			if(s.checkOverload(true, null)==true){
				ScheduleError overload = new ScheduleError(ScheduleError.overloadError);
				overload.setOffendingSemester(s);
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




	public String printString(){
		StringBuilder result = new StringBuilder();
		result.append(name + "\n");
		for(Semester s: this.semesters){
			result.append(s.semesterDate.toString() + "\n");
			
			
		}
		
		return result.toString();
		
		
	}

	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	
}
