
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


	private ArrayList<Course> listOfCourses = new ArrayList<Course>();

	private Dictionary<Prefix, Prefix[]> prereqs ;
	
	public Hashtable<String, Requirement> GERRequirements;



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
		this.GERRequirements = new Hashtable<String, Requirement>();
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
	
	public ArrayList<Requirement> allGERRequirements(){
		ArrayList<Requirement> result = new ArrayList<Requirement>();
		for(String key : this.GERRequirements.keySet()){
			Requirement next = this.GERRequirements.get(key);
			next.setName(key);
			result.add(next);
		}
		return result;
	}
	
	public Major getGERMajor(){
		Major m = new Major("GER");
		for(Requirement r : allGERRequirements()){
			m.addRequirement(r);
		}
		return m;
	}
	public Requirement getGERRequirement(String code){
		return this.GERRequirements.get(code);
	}
	
	public void courseSatisfiesGER(String GERs, Course c){
		String[] allGERs = GERs.split(" ");
		for(String s : allGERs){
			Requirement old = this.GERRequirements.get(s);
			if(old == null){
				GERRequirements.put(s, new Requirement(new Prefix[]{c.coursePrefix},1));
			}
			else{
				//Add this to the requirement
				old.addChoice(c.getPrefix());
			}
		}
	}


	public  ArrayList<Course> getCoursesIn(Semester s){
		return onlyThoseIn(listOfCourses,s);
	}

	public ArrayList<Course> onlyThoseSatisfying(Iterable<Course> input, Requirement r){
		ArrayList<Course> result = new ArrayList<Course>();
		for(Course c : input){
			if(r.isSatisfiedBy(c)){
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
					//Also, see if this course satisfies any GERs.
					String GERs = data.get(14);
					if(!GERs.equals("")){
						courseSatisfiesGER(data.get(14), lastCourse);
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
					}
				}
				line = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	public static void main(String[] args){
		CourseList c = CourseList.readAll(); //CourseList.testList();
		
		for(Requirement r : c.allGERRequirements()){
			System.out.println(r.name + "," + r.saveAsJSON());
		}
		
		/*for(Course cour : c.listOfCourses){
			System.out.println(cour.saveString());
		}*/

	}


}
