import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;


public class Schedule {
	ArrayList<Major> majorsList;
	ArrayList<Requirement> reqsList;
	ArrayList<Semester> semesters;
	
	boolean reqListValid;
	
	public static Schedule testSchedule(){
		Schedule result = new Schedule();
		result.majorsList.add(Major.testMajor());
		return result;
	}
	
	public Schedule(){
		this.majorsList= new ArrayList<Major>();
		this.semesters = new ArrayList<Semester>();
		this.reqsList = new ArrayList<Requirement>();
		reqListValid = false;
	}
	
	
	public Semester addNewSemester(){
		SemesterDate last = semesters.get(semesters.size() - 1).getDate();
		Semester next = new Semester(last.next());
		this.semesters.add(next);
		return next;
	}
	
	public void checkAllPrerequsites(){
		for(Semester s : semesters){
			for (ScheduleElement e : s.getElements()){
				checkPrerequsites(e, s.sD);
			}
		}
	}
	public void checkPrerequsites(ScheduleElement e, SemesterDate sD){
		Prefix p = e.getPrefix();
		if(p == null){
			return;
		}
		else{
			HashSet<Prefix> needed = CourseList.getPrerequsites(p);
			for(Semester s : semesters){
				if(s.sD.compareTo(sD) < 1){
					for (ScheduleElement earlier : s.getElements()){
						Prefix earlierPrefix = earlier.getPrefix();
						if(earlierPrefix != null){
							if(needed.contains(earlierPrefix)){
								needed.remove(earlierPrefix);
							}
						}
					}
				}
			}
			if(! needed.isEmpty()){
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
			for(int j = 0; j < elements.size() ; j ++){
				ScheduleElement e1 = elements.get(i);
				ScheduleElement e2 = elements.get(j);
				if(e1.isDuplicate(e2) || e2.isDuplicate(e1)){

				}
			}
		}
	}
	
	public ArrayList<ScheduleElement> allElements(){
		ArrayList<ScheduleElement> result = new ArrayList<ScheduleElement>();
		for(Semester s : semesters){
			result.addAll(s.getElements());
		}
		return result;
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
	 * Check all semesters for overlap in the semester
	 */
	public void checkOverlap(){
		for(Semester s : semesters){
			s.checkOverlap();
		}
	}
	
	/**
	 * Forces a full update of all of the requirements.
	 */
	public void updateReqList(){
		HashSet<Integer> doubleDipNumbers = new HashSet<Integer>();
		for(Requirement r : reqsList){
			doubleDipNumbers.add(r.doubleDipNumber);
			r.numFinished = 0;
		}
		for(Semester s : semesters){
			for(ScheduleElement e : s.getElements()){
				Prefix p = e.getPrefix();
				//update all requirements satisfied by this schedule element.
				for (Requirement r : getRequirementsSatisfied(e)){
					r.numFinished ++;
				}
			}
		}
	}
	
	public ArrayList<Requirement> getRequirementsSatisfied(ScheduleElement e){
		return e.getRequirementsSatisfied();
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
		updateAllCourseRequirementsSatisfied();
	}
	
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
		for (Requirement r : allSatisfied){
			if(! doubleDipIssues.contains(r.doubleDipNumber)){
				c.satisfies(r);
			}
		}
	}
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
	
	public ArrayList<Requirement> getRequirementsList(){
		if(! reqListValid){
			updateReqList();
		}
		return reqsList;
	}
	
	public int getCreditHoursComplete(){
		return 5;
	}
}
