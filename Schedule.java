import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;


public class Schedule {
	ArrayList<Major> majorsList;
	ArrayList<Semester> semesters;


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

		//Semesters
		this.semesters = new ArrayList<Semester>();
		for(int i = 0;i < 8 ; i ++){
			this.semesters.add(new Semester(firstSemester, this));
			firstSemester = firstSemester.next();
		}

	}


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




	public void replaceElement(Semester s, ScheduleElement oldElement , ScheduleElement newElement){
		s.replace(oldElement, newElement);
		if(this.reqsFulfilledValid){
			//keep the reqsFufilled set valid
			updateRequirementsSatisfied(newElement);
		}
		if(this.reqsValid){
			//Keep the requirements valid. No use doing the work if they aren't valid.
			for(Requirement r : oldElement.getRequirementsFulfilled()){
				r.numFinished --;
			}
			for(Requirement r : newElement.getRequirementsFulfilled()){
				r.numFinished ++;
			}
		}

	}


	public void checkErrorsWhenAdding(ScheduleElement e, Semester s){
		checkPrerequsites(e, s.semesterDate);
		s.checkOverlap(e);
		checkDuplicates(e);
	}
	public void added(ScheduleElement e, Semester s){
		updateRequirementsSatisfied(e);
		if(reqsValid){
			//keep them valid
			for (Requirement r : e.getRequirementsFulfilled()){
				r.numFinished ++;
			}
		}
	}
	public void checkErrorsWhenRemoving(ScheduleElement e, Semester s){
		//TODO
	}

	public void remove(ScheduleElement e, Semester s){
		s.remove(e);
		if(reqsFulfilledValid){
			if(reqsValid){
				//Keep reqs valid
				for (Requirement r : e.getRequirementsFulfilled()){
					r.numFinished -= 1;
				}
			}
		}
		else{
			reqsValid = false;
		}
	}

	public ArrayList<ScheduleElement> allElements(){
		ArrayList<ScheduleElement> result = new ArrayList<ScheduleElement>();
		for(Semester s : semesters){
			result.addAll(s.getElements());
		}
		return result;
	}
	
	public ArrayList<Requirement> allRequirements(){
		ArrayList<Requirement> result = new ArrayList<Requirement>();
		for(Major m : this.majorsList){
			result.addAll(m.reqList);
		}
		return result;
	}


	/**
	 * Check every element to see if it has all the prereqs it needs.
	 */
	public void checkAllPrerequsites(){

		HashSet<Prefix> taken = new HashSet<Prefix>();
		for(Semester s : semesters){
			for (ScheduleElement e : s.getElements()){
				boolean success = masterList.checkPrereqsShallow(e.getPrefix(), taken);
				if(!success){
					HashSet<Prefix> needed = masterList.missingPrereqsShallow(e.getPrefix(), taken);
					//throw new PrerequsiteException(needed,e);
				}
			}
		}
	}


	public void checkPrerequsites(ScheduleElement e, SemesterDate sD){
		Prefix p = e.getPrefix();
		if(p == null){
			return;
		}
		else{
			HashSet<Prefix> taken = new HashSet<Prefix>();
			for(Semester s : semesters){
				// Allow courses taken before or in the same semester.
				if(s.semesterDate.compareTo(sD) < 1){
					for (ScheduleElement earlier : s.getElements()){
						Prefix earlierPrefix = earlier.getPrefix();
						taken.add(earlierPrefix);
					}
				}
			}
			HashSet<Prefix> needed = masterList.missingPrereqsDeep(p, taken);
			if(!needed.isEmpty()){
				//throw new PrerequsiteException(needed, e);
			}

		}
	}



	/**
	 * Check all semesters for duplicates.
	 * If one is found, throw an exception.
	 */
	public void checkDuplicates(){
		for( ScheduleElement element : allElements()){
			checkDuplicates(element);
		}
	}
	public void checkDuplicates(ScheduleElement e){
		for(ScheduleElement e1 : allElements()){
			if(e1 == e){
				continue;
			}
			if(e1.isDuplicate(e) || e.isDuplicate(e1)){
				//throw new DuplicateException(e1, e);
			}
		}
	}





	/**
	 * Check all semesters to see if any elements in them
	 * have overlapping times (are taken at the same time)
	 */
	public void checkOverlap(){
		for(Semester s : semesters){
			s.checkOverlap(null);
		}
	}



	/**
	 * Forces a full update of all of the requirements
	 *  based on the majors currently included 
	 *  and the courses currently taken 
	 *  
	 */
	public void updateReqs(){
		if(! reqsFulfilledValid){
			updateAllReqsFulfilled();
		}
		
		for(Requirement r : this.allRequirements()){
			r.numFinished = 0;
		}
		//update all requirements satisfied by this schedule element.
		for(ScheduleElement e : allElements()){
			for (Requirement r : getRequirementsSatisfied(e)){
				r.numFinished ++;
			}
		}
		reqsValid = true;
	}
	
	public void updateRequirement(Requirement r){
		if(! reqsFulfilledValid){
			updateAllReqsFulfilled();
		}
		r.numFinished = 0;
		for(ScheduleElement e : allElements()){
			if(r.isSatisfiedBy(e)){
				r.numFinished ++;
			}
		}
	}
	

	public int totalRequirementsLeft(){
		if(!reqsValid){
			updateReqs();
		}
		int result = 0;
		for(Requirement r : this.getUniqueRequirementsList()){
			if(r.numFinished < r.numToChoose){
				result += r.numToChoose - r.numFinished;
			}
		}
		return result;
	}

	

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
			
		}
	}
	private void updateRequirementsSatisfied(Requirement r){
		//TODO
	}
	
	private void updateRequirementsSatisfied(Course c){
		c.clearReqsSatisfied();
		ArrayList<Requirement> allSatisfied = new ArrayList<Requirement>();
		for(Major m : this.majorsList){
			for(Requirement r : m.reqList){
				if(r.isSatisfiedBy(c)){
					allSatisfied.add(r);
				}
			}
		}
		//We don't want to add any requirement with a double dip issue, those
		// should be handled by the user.
		HashSet<Integer> doubleDipIssues = new HashSet<Integer>();
		HashSet<Integer> doubleDipNumbers = new HashSet<Integer>();
		for(Requirement r : allSatisfied){
			if(! doubleDipNumbers.add(r.doubleDipNumber)){
				doubleDipIssues.add(r.doubleDipNumber);
			}
		}
		//allow any doubleDip numbers that use the default DDN
		// because you're allowed to double dip from requirements with the 
		// default number.
		for(Integer i : doubleDipIssues){
			if(i%Major.MajorDDNRange == Requirement.defaultDDN){
				doubleDipIssues.remove(i);
			}
		}
		//Any time there's a question of which requirement a course should satisfy,
		// ignore it and leave it up to the user (and the GUI) to specify.
		for (Requirement r : allSatisfied){
			if(! doubleDipIssues.contains(r.doubleDipNumber)){
				c.satisfies(r);
			}
		}
	}


	/**
	 * Go through the list of courses and tell each one
	 * which current requirements it can satisfy.
	 */
	public void updateAllReqsFulfilled(){
		for (ScheduleElement e : allElements()){
			updateRequirementsSatisfied(e);
		}
		reqsFulfilledValid = true;
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

	/**
	 * Return an up-to-date list of all requirements from any major,
	 * but remove duplicate requirements (according to Requirement.equals)
	 * @return
	 */
	public ArrayList<Requirement> getUniqueRequirementsList(){
		if(! reqsValid){
			updateReqs();
		}
		HashSet<Requirement> result = new HashSet<Requirement>(this.allRequirements());
		return new ArrayList<Requirement>(result);
	}

	public void addMajor(Major newMajor){
		majorsList.add(newMajor);
		//Tell the courses which requirements they satisfy
		reqsFulfilledValid = false;
		//Tell the new requirements if some taken course satisfies them.
		reqsValid = false;
	}


	public void removeMajor(Major major) {
		majorsList.remove(major);
		reqsValid = false;
		reqsFulfilledValid = false;
	}



	public int getCreditHoursComplete(){
		int result = 0;
		for (ScheduleElement e : allElements()){
			if(e instanceof Course){
				result += ((Course)e).creditHours;
			}
		}
		return result;
	}

	
	public void addRequirementElement(Requirement req, Semester sem) {
		sem.add(req);
	}

	public void addScheduleElement(ScheduleElement element, Semester sem) {
		sem.add(element);
	}



	public ArrayList<Major> filterAlreadyChosenMajors(ArrayList<Major> collectionOfMajors ) {
		collectionOfMajors.removeAll(this.majorsList);
		return collectionOfMajors;

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
