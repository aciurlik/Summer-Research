
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Scanner;
/**
 * 
 * @author drivers, aciurlik
 * This will read in a file and call the course constructor to create a course object for one listed in the file
 * these courses will then be placed in an array list. Modeled after Maze.java
 *
 */

public class CourseList  {
	//Numbering is dependent on the difficulty of completing the GERs associated with this degree type
	//The higher the number the more classes are needed and therefore if there are two majors of different degree types

	//added the one with the higer degree Type value will dictate the GER list

	public static final int BA = 1;
	public static final int BS = 2;
	public static final int BM = 0;
	public static final int None = 4;


	private ArrayList<Course> listOfCourses = new ArrayList<Course>();

	private Dictionary<Prefix, Prefix[]> prereqs ;

	public Hashtable<String, HashSet<Prefix>> GERRequirements;



	public static CourseList testList(){


		/*Course[] list = new Course[]{
				Course.readFrom("MTH-220-02;Fray;[1, 3, 5];10:30:A,50;20/6/2017 11:30:0;4;2017-2"),
				Course.readFrom("MTH-250-02;Fray;[1, 3, 5];10:30:A,50;20/6/2018 11:30:0;4;2018-4"),
				Course.readFrom("MTH-360-02;Fray;[1, 3, 5];11:30:A,50;20/6/2018 13:30:0;4;2018-2"),
				Course.readFrom("MTH-350-02;Fray;[1, 3, 5];11:30:A,50;20/6/2019 11:30:0;4;2019-2"),
				Course.readFrom("MTH-460-02;Fray;[1, 3, 5];12:30:A,50;20/6/2018 13:30:0;4;2018-2"),
				Course.readFrom("MTH-450-02;Fray;[1, 3, 5];12:30:A,50;20/6/2018 13:30:0;4;2018-2"),
				Course.readFrom("MTH-504-02;Fray;[1, 3, 5];1:30:P,50;20/6/2018 13:30:0;4;2018-2"),
				Course.readFrom("MTH-151-02;Fray;[1, 3, 5];1:30:P,50;20/6/2018 13:30:0;4;2018-2")
		};

		for(Course c : list){
			result.add(c);
		}
		 */

		//result.addCoursesIn(new File("Mayx2017.csv"));
		//result.addCoursesIn(new File("Fall2017.csv"));
		return readAll();
	}

	
	public CourseList (){
		this.listOfCourses = new ArrayList<Course>();
		this.prereqs = new Hashtable<Prefix, Prefix[]>();
		this.GERRequirements = new Hashtable<String, HashSet<Prefix>>();
	}

	public static CourseList readAll(){
		CourseList result = new CourseList();
		File f = new File("CourseCatologs");
		for ( File semesterFile : f.listFiles(new FilenameFilter(){

			@Override
			public boolean accept(File dir, String name) {
				if(name.contains(".csv")){
					return true;
				}
				return false;
			}

		})){
			result.addCoursesIn(semesterFile);
		}
		return result;
	}




	public Prefix[] getPrereqsShallow(Prefix p){
		if(p == null){
			return new Prefix[0];
		}
		Prefix[] result = this.prereqs.get(p);
		if(result == null){
			result = new Prefix[0];
		}
		return result;
	}

	/**
	 * Find all the prerequsites of this prefix using a breadth first
	 * search.
	 * @param p
	 * @return
	 */
	public HashSet<Prefix> getPrereqsDeep(Prefix p){
		HashSet<Prefix> result = new HashSet<Prefix>();
		HashSet<Prefix> newList = new HashSet<Prefix>();
		for( Prefix x : getPrereqsShallow(p)){
			newList.add(x);
		}
		if(p == null){
			return result;
		}
		//Each iteration, add newList to result and
		// find all the new prereqs implied.
		while(!newList.isEmpty()){
			//replacement new list
			HashSet<Prefix> repNewList = new HashSet<Prefix>();
			for(Prefix x : newList){
				//if result already contains this piece of newList,
				// we shouldn't add its prereqs (or we might get into an infinite
				// loop if both MTH 220 requires MTH 110, 
				// and MTH 110 requires MTH 220.
				if(result.add(x)){
					for(Prefix y : getPrereqsShallow(x)){
						repNewList.add(y);
					}
				}
			}
			newList = new HashSet<Prefix>(repNewList);
			repNewList = new HashSet<Prefix>();
		}
		return result;
	}

	/**
	 * See if this hashSet of prefixes has all the immediate
	 * prereqs for main. If not, return false.
	 * @param p
	 * @param taken
	 * @return
	 */
	//public boolean checkPrereqsShallow(Prefix main, HashSet<Prefix> taken){
	//	return missingPrereqsShallow(main, taken).isEmpty();
	//}

	/**
	 * See if this hashSet of prefixes has all the prereqs necessary for
	 * the main prefix. If not, return false.
	 * @param main
	 * @param taken
	 * @return
	 */
	//public boolean checkPrereqsDeep(Prefix main, HashSet<Prefix> taken){
	//	return missingPrereqsDeep(main, taken).isEmpty();
	//}
	/**
	 * Find the entire set of prereqs that would need to be taken to 
	 * allow main to be taken.
	 * @param main
	 * @param taken
	 * @return
	 */
	public HashSet<Prefix> missingPrereqsDeep(Prefix main, HashSet<Prefix> taken){
		HashSet<Prefix> result = new HashSet<Prefix>();
		HashSet<Prefix> allNeeded = getPrereqsDeep(main);
		for (Prefix p : allNeeded){
			if(!taken.contains(p)){
				result.add(p);
			}
		}
		return result;
	}


	public HashSet<Prefix> missingPrereqsShallow(Prefix main, HashSet<Prefix> taken){
		HashSet<Prefix> result = new HashSet<Prefix>();
		Prefix[] allNeeded = getPrereqsShallow(main);
		for (Prefix p : allNeeded){
			if(!taken.contains(p)){
				result.add(p);
			}
		}
		return result;
	}








	public boolean add(Course c){
		return listOfCourses.add(c);
	}

	public void addAt(Course c, int i){
		listOfCourses.add(i, c);
	}

	public Course removeCourse(Course c){

		listOfCourses.remove(c);
		return c;
	}


	public Course removeAtIndex(int i){

		Course c = listOfCourses.remove(i);
		return c;
	}


	public static String getDegreeTypeString(int i){
		if(i == BS){
			return "BS";
		}
		if(i == BA){
			return "BA";
		}
		if(i == BM){
			return "BM";
		}
		return "null";
	}

	public static int getDegreeTypeNumber(String s){
		if(s.equals("BM")){
			return CourseList.BM;
		}
		if(s.equals("BS")){
			return CourseList.BS;
		}
		if(s.equals("BA")){
			return CourseList.BA;
		}
		return -1;
	}

	/**
	 * Return only those members of input which are in the given semester.
	 * @param input
	 * @param s
	 * @return
	 */
	public ArrayList<Course> onlyThoseIn(Iterable<Course> input, Semester s){
		ArrayList<Course> SemesterList = new ArrayList<Course>();

		for(Course course : input){
			if ( course.getSemester().compareTo(s.getDate()) == 0){

				SemesterList.add(course);
			}
		}
		return SemesterList;
	}


	/**
	 * Get the GER major associated with the given type, where
	 * type is one of Major.BA, Major.BS, or Major.BM
	 * 
	 * see
	 * http://www.furman.edu/academics/academics/academic-resources/Pages/General-Education-Requirements.aspx
	 * for the source of most of our information. 
	 *
	 * @param MajorType
	 * @return
	 */
	public Major getGERMajor(int majorType){
		Major m = new Major("GER");

		
		
		outerloop:
		for(String key : GERRequirements.keySet()){
			//Special cases first, then default behavior.
			// default behavior is to add every prefix in the 
			// list to each requirement, and to do nothing to 
			// double dip numbers (if double dip numbers exist).
			
			Requirement r = new Requirement();
			r.setName(key);
			boolean includeDefaultPrefixes = true;
			
			
			
			switch(key){
			case "MR":
				// "Students with a BS must complete this requirement with a calculus course"
				// "Students with a BM do not need to fulfill this requirement."
				switch ( majorType){
				case Major.BS:
					//For BS, the MR requirement is predefined
					r = (Requirement)Requirement.readFrom("1 of (MTH 150, MTH 145)");
					includeDefaultPrefixes = false;
					break;
				case Major.BM:
					// For BM, there is no MR requirement.
					continue outerloop;
				case Major.BA:
				default:
					//For BA and any unknown majorType, 
					// you can take any of the MR courses.
					includeDefaultPrefixes = true;

				}
				break;
			case "NW":
				/* From the Furman GER checklist, NW requires:
				 * 		"Two courses, at least one with a 
				 * 		separate labratory component."
				 * 
				 * To handle this, we make 2 requirements, one of which
				 * 		is NW and the other is NWL. You need one course from
				 * 		each requirement, and they aren't allowed to double dip.
				 * 		Every prefix in the NWL requirement also satisfies 
				 * 		the NW requirement.
				 * 
				 * In addition, "Students seeking the Bachelor of Music degree 
				 * must complete only one course to meet this requirement, 
				 * while Bachelor of Science degree candidates must complete 
				 * this requirement in courses appropriate for majors in 
				 * the natural science disciplines. "
				 * 
				 */
				switch(majorType){
				case Major.BS:
					// only courses "appropriate for majors in the 
					// natural science disciplines," which currently (6/11/2017) means
					//
					// "courses numbered 110 or greater in Biology, Chemistry, 
					//  Earth and Environmental Science, Neuroscience, Physics, 
					//  "Sustainability Science."
             /*
            	r.choices.clear();
				      r.choices.add(new Prefix("CHM", 110));
				      r.choices.add(new Prefix("CHM", 115));
				      r.choices.add(new Prefix("CHM", 120));
				      r.choices.add(new Prefix("EES", 112));
				      r.choices.add(new Prefix("EES", 113));
				      r.choices.add(new Prefix("EES", 115));
				      r.choices.add(new Prefix("PHY", 111));
				      r.choices.add(new Prefix("PHY", 112));
				      r.choices.add(new Prefix("PSY", 320));
				      r.choices.add(new Prefix("SUS", 120));   
            */
					for(Prefix p : GERRequirements.get("NWL")){
						if(p.getNumber().compareTo("110") >= 0){
							r.choices.add(new TerminalRequirement(p));
            }
					}
					for(Prefix p : GERRequirements.get("NW")){
						if(p.getNumber().compareTo("110") >= 0){
							r.choices.add(new TerminalRequirement(p));
						}
					}
					includeDefaultPrefixes = false;
					break;
				case Major.BA:
				case Major.BM:
				default:
					for(Prefix p : GERRequirements.get("NWL")){
						r.choices.add(new TerminalRequirement(p));
					}
				}
				//TODO set the double dip number of NW and NWL to be different.
				// r.setDoubleDipNumber
				break;
			case "NWL":
				//Music majors don't need this requirement.
				// they only need the NW requirement.
				if(majorType== Major.BM){
					continue outerloop;
				}
				break;
			case "HB":
				//every major type needs 2 hb.
				r.numToChoose = 2;
				break;
			case "WC":
			case "NE":
				// TODO set doubleDipNumber differently from all the others


			}
			if(includeDefaultPrefixes){
				for(Prefix p : GERRequirements.get(key)){
					r.choices.add(new TerminalRequirement(p));
				}
			}
			m.addRequirement(r);
		}
		return m;
	}


	/**
	 * Given the collection of GERs that this course satisfies
	 * 		i.e., MTH 110-01 ... "NWL NW WC"
	 * add this course to the requirement for each GER.
	 * @param GERs
	 * @param c
	 */
	public void setCourseSatisfiesGER(String GERs, Course c){
		String[] allGERs = GERs.trim().split(" ");


		for(String s : allGERs){
			HashSet<Prefix> old = this.GERRequirements.get(s);
			if(old == null){
				old = new HashSet<Prefix>();
				GERRequirements.put(s, old);
			}
			old.add(c.getPrefix());
		}
	}


	public  ArrayList<Course> getCoursesIn(Semester s){
		return onlyThoseIn(listOfCourses,s);
	}

	public ArrayList<Course> onlyThoseSatisfying(Iterable<Course> input, Requirement r){
		ArrayList<Course> result = new ArrayList<Course>();
		for(Course c : input){
			if(r.isSatisfiedBy(c.getPrefix())){
				result.add(c);
			}
		}
		return result;
	}
	public  ArrayList<Course> getCoursesSatisfying(Requirement r){
		return onlyThoseSatisfying(this.listOfCourses,r);
	}

	


	public void addCoursesIn(File furmanCoursesFile){
		String lastSectionNumber = "";
		Course lastCourse = null;
		Course duplicateCourse =null;
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(furmanCoursesFile));
			//skip the first line of field names

			br.readLine();

			String line = br.readLine();

			//Read in each course
			while(line != null){
				ArrayList<String> data = SaverLoader.parseCSVLine(line);
				String sectionNumber = data.get(1);
				//Check if we've found a new course
				if(! sectionNumber.equals( lastSectionNumber)){
					//If it's a new course, make a new one.
					lastSectionNumber = sectionNumber;
					if(lastCourse != null){
						this.add(lastCourse);
					}
					lastCourse = Course.readFromFurmanData(data);
					if(data.get(0).contains("Summer")){
						if(lastCourse.meetingTime==null || lastCourse.meetingTime[0].isAllUnused()  ){
							Course newDuplicateCourse = Course.readFromFurmanData(data);
							if( newDuplicateCourse != duplicateCourse){
								if(duplicateCourse != null){
									this.add(duplicateCourse);
									
								}
								duplicateCourse=newDuplicateCourse;


							}

							duplicateCourse.semester = new SemesterDate(duplicateCourse.semester.year, SemesterDate.SUMMERTWO);
							
						}
					}
					//Also, see if this course satisfies any GERs.
					String GERs = data.get(14);
					if(!GERs.equals("")){
						setCourseSatisfiesGER(data.get(14), lastCourse);
					}
				}
				//If this isn't a new course, then it's either a lab or an exam.
				else{
					String InstructionalMethod = data.get(5);
					Time[] times = Course.readTimesFrom(data);
					Time totalStart = Time.combine(times[0], times[1]);
					if(InstructionalMethod.equals("EXAM")){
						//set examTime = startTime thru endTime, no dates.
						Time totalEnd = Time.combine(times[2], times[3]);
						lastCourse.setExamTime(new Time[]{totalStart, totalEnd});
					}
					else{
						//Set labTime = startTime thru endTime, no dates.
						lastCourse.setLabTime(new Time[]{times[1], times[3]});
						String meetingTimesString = data.get(9);
						if(!meetingTimesString.equals("")){
							lastCourse.setLabDay(Time.meetingDaysFrom(meetingTimesString)[0]);
						}
					}
				}
				line = br.readLine();
			}
			br.close();
			if(lastCourse != null){
				this.add(lastCourse);
				
			}
			if(duplicateCourse != null){
				this.add(duplicateCourse);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
	}

	public static void main(String[] args){
		CourseList c = CourseList.readAll(); //CourseList.testList();

		for(Prefix p : c.GERRequirements.get("NWL")){
			
		}
		/*for(Course cour : c.listOfCourses){
			System.out.println(cour.saveString());
		}*/

	}


}
