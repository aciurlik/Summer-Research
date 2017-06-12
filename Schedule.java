import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;


public class Schedule {
	ArrayList<Major> majorsList;
	ArrayList<Semester> semesters;
	Driver d; 
	CourseList masterList;


	boolean reqsValid; // The set of requirements entailed by all majors is up to date
	boolean reqsFulfilledValid; // the return value of getRequirementsFulfilled 
	// is valid for all schedule elements

	public static Schedule testSchedule(){

		CourseList l = CourseList.testList();
		Schedule result = new Schedule(l, new SemesterDate(2016, SemesterDate.FALL), null);

		result.addMajor(Major.testMajor());


		//result.semesters.add(b);

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
	public Schedule(CourseList masterList, SemesterDate firstSemester, SemesterDate currentSemester){

		//Majors and requirements
		this.majorsList= new ArrayList<Major>();
		//add the GERs major
		reqsValid = false;
		reqsFulfilledValid = false;

		//Course list
		this.masterList = masterList;
		this.addMajor(masterList.getGERMajor(CourseList.BA));

		//Semesters
		this.semesters = new ArrayList<Semester>();
		for(int i = 0;i < 8 ; i ++){
			this.semesters.add(new Semester(firstSemester, this));
			firstSemester = firstSemester.next();
		}


	}

	public void setDriver(Driver d){
		this.d = d;
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
		this.reqsValid = false;
		Collections.sort(semesters);
	}



	///////////////////////////////
	///////////////////////////////
	//	Adding and removing Elements
	///////////////////////////////
	///////////////////////////////

	/**
	 * In semester s, replace oldElement with newElement.
	 * @param s
	 * @param oldElement
	 * @param newElement
	 */
	public boolean replaceElement(Semester s, ScheduleElement oldElement , ScheduleElement newElement){
		if	(checkErrorsWhenReplacing(s, s, oldElement, newElement)){
			return false;
		}
		if(s.replace(oldElement, newElement)){
			this.reqsValid = false;
		}
		return true;
	}
	/**
	 * In semester S, remove ScheduleElement e.
	 * @param e
	 * @param s
	 */
	public boolean removeElement (ScheduleElement e, Semester s){
		if(this.checkErrorsWhenRemoving(e, s)){
			return false;
		}
		s.remove(e);
		if(reqsFulfilledValid){
			//could make this more efficient by only updating 
			// requirements that return true for
			// isSatisfiedBy(e);
			this.reqsValid = false;
		}
		else{
			reqsValid = false;
		}
		return true;
	}

	public boolean addScheduleElement(ScheduleElement element, Semester sem) {
		if(this.checkErrorsWhenAdding(element, sem)){
			return false;
		}
		sem.add(element);
		updateRequirementsSatisfied(element);
		if(reqsValid){
			//could make this more efficient by only updating 
			// requirements that return true for
			// isSatisfiedBy(e);
			reqsValid = false;
		}
		reqsValid = false;
		return true;
	}


	public boolean moveElement(ScheduleElement element, Semester oldSem, Semester newSem){
		if(this.checkErrorsWhenReplacing(oldSem, newSem, element, element)){
			return false;
		}
		newSem.add(element);
		return true;

	}

	///////////////////////////////
	///////////////////////////////
	//	Adding and removing Majors
	///////////////////////////////
	///////////////////////////////


	public void addMajor(Major newMajor){
	addAtMajor(newMajor, majorsList.size());
	if(!newMajor.name.equals("GER")){
			majorsList.remove(0);
			addAtMajor(masterList.getGERMajor(this.determineGER()), 0);
		}
		//Tell the courses which requirements they satisfy
		reqsFulfilledValid = false;
		//Tell the new requirements if some taken course satisfies them.
		reqsValid = false;
	}

	public void addAtMajor(Major newMajor, int index){
		majorsList.add(index, newMajor);
		//Tell the courses which requirements they satisfy
		reqsFulfilledValid = false;
		//Tell the new requirements if some taken course satisfies them.
		reqsValid = false;
	}


	public void removeMajor(Major major) {
		majorsList.remove(major);
		reqsValid = false;
		reqsFulfilledValid = false;
		if(!major.name.equals("GER")){
			majorsList.remove(0);
			addAtMajor(masterList.getGERMajor(this.determineGER()), 0);
		}
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
			return(d.userRequestError(s.error, s.instructions));
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
			if (checkDuplicates(newElement, true)){
				return true;
			}
		}
		else{
			if (checkDuplicates(newElement, false)){
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
		if(checkDuplicates(e,false)){
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
	 * Find the list of all ScheduleElements in any semester of this Schedule.
	 * @return
	 */
	public ArrayList<ScheduleElement> getAllElements(){
		ArrayList<ScheduleElement> result = new ArrayList<ScheduleElement>();
		for(Semester s : semesters){
			result.addAll(s.getElements());
		}
		return result;
	}

	/**
	 * Find the list of all requirements in any major of this schedule.
	 * May include duplicate requirements if two majors share a requirement.
	 * @return
	 */
	public ArrayList<Requirement> getAllRequirements(){
		ArrayList<Requirement> result = new ArrayList<Requirement>();
		for(Major m : this.majorsList){
			result.addAll(m.reqList);
		}
		return result;
	}





























	///////////////////////////////
	///////////////////////////////
	//	Prerequisite error checking
	///////////////////////////////
	///////////////////////////////

	/**
	 * Check every element to see if it has all the prereqs it needs.
	 */
	public Object[] checkAllPrerequsites(){
		HashSet<Prefix> taken = new HashSet<Prefix>();
		for(Semester s : semesters){
			for (ScheduleElement e : s.getElements()){
				boolean success = masterList.missingPrereqsShallow(e.getPrefix(), taken).isEmpty();
				if(!success){
					HashSet<Prefix> needed = masterList.missingPrereqsShallow(e.getPrefix(), taken);
					Object[] result = new Object[]{e, needed};

				}
			}
		}
		return null;
	}

	public boolean checkPrerequsitesAdding(ScheduleElement e, SemesterDate sD){
		HashSet<Prefix> needed = prereqsNeededFor(e.getPrefix(), sD);
		if(!needed.isEmpty()){
			ScheduleError preReq = new ScheduleError(MenuOptions.preReqError);
			preReq.setOffendingCourse(e);
			preReq.setNeededCourses(needed);
			preReq.setInstructions(e.getDisplayString() + " needs prerequisite(s)" + needed.toString());
			return(!this.userOverride(preReq));

			//throw new PrerequsiteException(needed, e);
		}
		return false;
	}
	public HashSet<Prefix> prereqsNeededFor(Prefix p, SemesterDate sD){
		if(p == null){
			return new HashSet<Prefix>();
		}
		else{
			HashSet<Prefix> taken = prefixesTakenBefore(sD);
			taken.addAll(prefixesTakenIn(sD));
			HashSet<Prefix> needed = masterList.missingPrereqsDeep(p, taken);
			return needed;
		}
	}

	public boolean checkPrerequsitesRemoving(ScheduleElement e, Semester s){
		Prefix currentP = e.getPrefix();
		for(Semester other : this.semesters){
			if(other.getDate().compareTo(s.getDate()) >= 0 ){
				for(ScheduleElement oElement : s.getElements()){
					HashSet<Prefix> needed = prereqsNeededFor(oElement.getPrefix(),other.semesterDate);
					if(needed.contains(currentP)){
						ScheduleError preReq = new ScheduleError(MenuOptions.preReqError);
						preReq.setOffendingCourse(e);
						preReq.setNeededCourses(needed);
						preReq.setInstructions(e.getDisplayString() + " needs prerequisit(s) " + needed.toString());
						return(!this.userOverride(preReq));
					}
				}
			}
		}
		return false;
	}

	public boolean checkPrerequsitesReplacing(Semester oldSem, Semester newSem, 
			ScheduleElement oldE, ScheduleElement newE){
		//If one of the prefixes is null or 
		// if the prefixes are different, 
		// we're really just doing an
		// add and a remove.
		Prefix newP = newE.getPrefix();
		if(newP == null){
			return(checkPrerequsitesRemoving(oldE, oldSem));
			//	checkPrerequsitesFor(newE, newSem.semesterDate);
		}
		Prefix oldP = oldE.getPrefix();
		if(oldP == null){
			return checkPrerequsitesAdding(newE, newSem.semesterDate);
		}
		//If they're equal, we're really moving the time we take this class.
		// Check to see if anything happening before the new placement but
		// after the old placement now has a prerequsite not filled.
		if(newP.equals(oldP)){
			if(oldSem.semesterDate.compareTo(newSem.semesterDate) >= 1){
				HashSet<Prefix> taken  = this.prefixesTakenBefore(newSem.semesterDate);
				taken.addAll(this.prefixesTakenIn(newSem.semesterDate));
				HashSet<Prefix> missing =masterList.missingPrereqsShallow(oldE.getPrefix(), taken);
				if(!missing.isEmpty()){
					ScheduleError preReq = new ScheduleError(MenuOptions.preReqError);
					preReq.setOffendingCourse(newE);
					preReq.setNeededCourses(missing);
					preReq.setInstructions(newE.toString() + " has prerequisite " + missing.toString());
					return((this.userOverride(preReq)));
				}
			}
			for(Semester s : this.semesters){
				//prefixes between oldSem and newSem inclusive.
				HashSet<Prefix> beforeNew = this.prefixesTakenBefore(newSem.semesterDate);
				HashSet<Prefix> afterOld = this.prefixesTakenAfter(oldSem.semesterDate);
				HashSet<Prefix> old = this.prefixesTakenIn(oldSem);
				afterOld.addAll(old);
				afterOld.retainAll(beforeNew);
				for(Prefix p : afterOld){
					if(Arrays.asList(masterList.getPrereqsShallow(p)).contains(newP)){
						//throw new PrerequsiteException(new Prefix[]{newP}, p);
						ScheduleError preReq = new ScheduleError(MenuOptions.preReqError);
						preReq.setCourse(p);
						preReq.setMissingCourse(newP);
						preReq.setInstructions(p.toString() + " had prerequisite " + newP.toString() );
						return(!this.userOverride(preReq));
					}
				}
			}
		}
		else{
			return (checkPrerequsitesRemoving(oldE, oldSem) ||checkPrerequsitesAdding(newE, newSem.semesterDate));
		}
		return false;
	}


	/**
	 * All prefixes taken before or including this semester.
	 * @param sd
	 * @return
	 */
	public HashSet<Prefix> prefixesTakenBefore(SemesterDate sd){
		HashSet<Prefix> taken = new HashSet<Prefix>();
		for(Semester s : semesters){
			// Allow courses taken before or in the same semester.
			if(s.semesterDate.compareTo(sd) < 0){
				taken.addAll(prefixesTakenIn(s));
			}
		}
		return taken;
	}
	/**
	 * All prefixes scheduled strictly after this semester.
	 * @param sd
	 * @return
	 */
	public HashSet<Prefix> prefixesTakenAfter(SemesterDate sd){
		HashSet<Prefix> taken = new HashSet<Prefix>();
		for(Semester s : semesters){
			// Allow courses taken before or in the same semester.
			if(s.semesterDate.compareTo(sd) > 0){
				taken.addAll(prefixesTakenIn(s));
			}
		}
		return taken;
	}
	public HashSet<Prefix> prefixesTakenIn(SemesterDate sD){
		HashSet<Prefix> taken = new HashSet<Prefix>();
		for(Semester s : semesters){
			// Allow courses taken before or in the same semester.
			if(s.semesterDate.compareTo(sD) == 0){
				taken.addAll(prefixesTakenIn(s));
			}
		}
		return taken;
	}
	public HashSet<Prefix> prefixesTakenIn(Semester s){
		HashSet<Prefix> result = new HashSet<Prefix>();
		for(ScheduleElement e : s.getElements()){
			result.add(e.getPrefix());
		}
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
		for( ScheduleElement element : getAllElements()){
			if(checkDuplicates(element, true)){
				return checkDuplicates(element, true);
			}
		}
		return false;
	}


	public boolean checkDuplicates(ScheduleElement e, boolean alreadyAdded){
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
						ScheduleError duplicate = new ScheduleError(MenuOptions.duplicateError);
						duplicate.setInstructions(results[0].getDisplayString() + " duplicates " + results[1]	);
						duplicate.setDuplicateCourses(results);
						return(!this.userOverride(duplicate));
					}
				}
				continue;
			}
			if(e1.isDuplicate(e) || e.isDuplicate(e1)){
				ScheduleElement[] result = {e, e1};
				ScheduleError duplicate = new ScheduleError(MenuOptions.duplicateError);
				duplicate.setDuplicateCourses(result);
				duplicate.setInstructions(result[0].getDisplayString() + " duplicates " + result[1]	);
				return (!this.userOverride(duplicate));
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
	 */
	public boolean checkOverlap(){
		for(Semester s : semesters){
			if(s.checkOverlap(null)){
				return s.checkOverlap(null);
			}

		}
		return false;
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
	public void updateReqs(){
		if(! reqsFulfilledValid){
			updateAllReqsFulfilled();
		}
		//This cannot be a set because we need duplicate requirements
		// to potentially be satisfied twice.
		ArrayList<ScheduleElement> allTakenElements = getAllElements();
		for(Requirement r : this.getAllRequirements()){
			r.isComplete(allTakenElements, true);
			r.percentComplete(allTakenElements, true);
			r.minCoursesNeeded(allTakenElements,  true);
		}
		reqsValid = true;
	}

	/**
	 * Count the number of currently placed schedule elements
	 * satisfying this requirement.
	 * @param r
	 */
	public int updateRequirement(Requirement r){
		if(! reqsFulfilledValid){
			updateAllReqsFulfilled();
		}
		ArrayList<ScheduleElement> allTakenElements = getAllElements();
		r.isComplete(allTakenElements, true);
		r.percentComplete(allTakenElements, true);
		return r.minCoursesNeeded(allTakenElements,  true);
	}


	/**
	 * Get the number of requirements still left to put in the schedule.
	 * @return
	 */
	public int totalRequirementsLeft(){
		if(!reqsValid){
			updateReqs();
		}
		int result = 0;
		for(Requirement r : this.getUniqueRequirementsList()){
			result += r.storedCoursesLeft;
		}
		return result;
	}



	/**
	 * Find all the requirements that this ScheduleElement satisfies.
	 * @param e
	 * @return
	 */
	public ArrayList<Requirement> getRequirementsSatisfied(ScheduleElement e){
		if(!reqsFulfilledValid){
			this.updateRequirementsSatisfied(e);
		}
		return e.getRequirementsFulfilled();
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
		c.clearReqsSatisfied();
		ArrayList<Requirement> allSatisfied = new ArrayList<Requirement>();
		for(Major m : this.majorsList){
			for(Requirement r : m.reqList){
				if(r.isSatisfiedBy(c.getPrefix())){
					allSatisfied.add(r);
				}
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
		//TODO fill this in
		return false;
	}


	/**
	 * Go through the list of courses and tell each one
	 * which current requirements it can satisfy.
	 */
	public void updateAllReqsFulfilled(){
		for (ScheduleElement e : getAllElements()){
			updateRequirementsSatisfied(e);
		}
		reqsFulfilledValid = true;
	}



	/**
	 * Return an up-to-date list of all requirements from any major,
	 * but remove duplicate requirements (according to Requirement.equals)
	 * @return
	 */
	public ArrayList<Requirement> getUniqueRequirementsList(){
		if(! reqsValid){
			updateReqs();
		}
		HashSet<Requirement> result = new HashSet<Requirement>(this.getAllRequirements());
		return new ArrayList<Requirement>(result);
	}


	public ArrayList<Major> getMajors(){
		//Update major requirements.
		// This may need to be done even if requirementsList is valid,
		// because validating the requirements list only validates one
		// copy of each requirement. If multiple majors have the same 
		// requirement the old update method won't catch it.
		for (Major m : this.majorsList){
			for (Requirement r : m.reqList){
				updateRequirement(r);
			}
		}
		return this.majorsList;
	}





	public int getCreditHoursComplete(){
		//TODO replace with each semester's getCreditHours method
		int result = 0;
		for (ScheduleElement e : getAllElements()){
			if(e instanceof Course){
				result += ((Course)e).creditHours;
			}
		}
		return result;
	}





	public ArrayList<Major> filterAlreadyChosenMajors(ArrayList<Major> collectionOfMajors ) {
		collectionOfMajors.removeAll(this.majorsList);
		return collectionOfMajors;

	}

	public ArrayList<Course> filterAlreadyChosenCourses(ArrayList<Course> collectionOfCourses){
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







}
