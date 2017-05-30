import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;


public class Schedule {
	ArrayList<Major> majorsList;
	ArrayList<Requirement> reqsList;
	ArrayList<Semester> semesters;


	CourseList masterList;

	boolean reqListValid;

	public static Schedule testSchedule(){

		CourseList l = CourseList.testList();
		Schedule result = new Schedule(l, new SemesterDate(2017, SemesterDate.FALL), null);

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
		this.reqsList = new ArrayList<Requirement>();
		reqListValid = false;

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


	public void checkErrorsWhenAdding(ScheduleElement e, Semester s){
		checkPrerequsites(e, s.semesterDate);
		s.checkOverlap(e);
		checkDuplicates(e);
	}
	public void added(ScheduleElement e, Semester s){
		//Make sure this course is accurate on which reqs it satisfies
		if (e instanceof Course){
			updateRequirementsSatisfied((Course)e);
		}
		// tell each such requirement that it got one more satisfaction.
		for (Requirement r : e.getRequirementsFulfilled()){
			r.numFinished ++;
		}
		reqListValid = false;

	}
	public void checkErrorsWhenRemoving(ScheduleElement e, Semester s){

	}
	public void removed(ScheduleElement e, Semester s){
		for (Requirement r : e.getRequirementsFulfilled()){
			r.numFinished -= 1;
		}
	}

	public ArrayList<ScheduleElement> allElements(){
		ArrayList<ScheduleElement> result = new ArrayList<ScheduleElement>();
		for(Semester s : semesters){
			result.addAll(s.getElements());
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
					throw new PrerequsiteException(needed,e);
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
				throw new PrerequsiteException(needed, e);
			}

		}
	}



	/**
	 * Check all semesters for duplicates.
	 * If one is found, throw an exception.
	 */
	public void checkDuplicates(){
		ArrayList<ScheduleElement> elements = allElements();
		for (int i = 0; i < elements.size() ; i ++){
			checkDuplicates(elements.get(i));
		}
	}
	public void checkDuplicates(ScheduleElement e){
		for(ScheduleElement e1 : allElements()){
			if(e1 == e){
				continue;
			}
			if(e1.isDuplicate(e) || e.isDuplicate(e1)){
				throw new DuplicateException(e1, e);
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
	 *  Assumes that all courses have up-to-date requirementsSatisfied lists.
	 */
	public void updateReqList(){
		for(Requirement r : reqsList){
			r.numFinished = 0;
		}
		for(Semester s : semesters){
			for(ScheduleElement e : s.getElements()){
				//update all requirements satisfied by this schedule element.
				for (Requirement r : getRequirementsSatisfied(e)){
					r.numFinished ++;
				}
			}
		}
		Collections.sort(reqsList);;
		reqListValid = true;
	}

	public void updateRequirement(Requirement r){
		r.numFinished = 0;
		ArrayList<ScheduleElement> all = allElements();
		for(ScheduleElement e : all){
			if(r.isSatisfiedBy(e)){
				r.numFinished ++;
			}
		}
	}

	public ArrayList<Requirement> getRequirementsSatisfied(ScheduleElement e){
		return e.getRequirementsFulfilled();
	}

	/**
	 * Tells this course the list of requirements that it satisfied.
	 * 		this list is a subset of the requirements implied by the set
	 * 		of majors.
	 * @param c
	 */
	public void updateRequirementsSatisfied(Course c){
		c.clearReqsSatisfied();
		ArrayList<Requirement> allSatisfied = new ArrayList<Requirement>();
		for(Requirement r : reqsList){
			if(r.isSatisfiedBy(c)){
				allSatisfied.add(r);
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
	public void updateAllCourseRequirementsSatisfied(){
		for(Semester s : semesters){
			for(ScheduleElement e : s.getElements()){
				if(e instanceof Course){
					e = (Course)e;
					updateRequirementsSatisfied((Course)e);
				}
			}
		}
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

	public ArrayList<Requirement> getRequirementsList(){
		if(! reqListValid){
			updateReqList();
		}
		return reqsList;
	}

	public void addMajor(Major newMajor){
		//Use sets to prevent duplicate requirements.
		HashSet<Requirement> set = new HashSet<Requirement>();
		majorsList.add(newMajor);
		for (Major m : majorsList){
			for (Requirement r : m.reqList){
				set.add(r);
			}
		}
		reqsList = new ArrayList<Requirement>(set);
		Collections.sort(reqsList);
		//Tell the courses which requirements they satisfy
		updateAllCourseRequirementsSatisfied();
		//Tell the new requirements if some taken course satisfies them.
		reqListValid = false;
	}

	public int getCreditHoursComplete(){
		System.out.println(allElements().toString());
		int result = 0;
		for (ScheduleElement e : allElements()){
			if(e instanceof Course){
				result += ((Course)e).creditHours;
			}
		}
		return result;
	}

	public void addElement(Requirement req, Semester sem) {
		sem.add(req);



	}
}
