

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
	//added, the one with the higer degree Type value will dictate the GER list
	public static final int BA = 1;
	public static final int BS = 2;
	public static final int BM = 0;
	public static final int None = 4;
	
	public static final boolean masterIsNotAround = true;

	public static final String prereqMeaningsFile = MenuOptions.resourcesFolder + "PrereqMeanings.txt";
	public static final String courseListFolder = MenuOptions.resourcesFolder + "CourseCatologs";

	private ArrayList<Course> listOfCourses = new ArrayList<Course>();
	private Hashtable<Prefix, String> rawPrereqs;
	private Hashtable<String, String> savedPrereqMeanings;
	public Hashtable<String, HashSet<Prefix>> GERRequirements;



	public static CourseList testList(){
		return readAll();
	}


	public CourseList (){
		this.listOfCourses = new ArrayList<Course>();
		this.rawPrereqs = new Hashtable<Prefix, String>();
		this.savedPrereqMeanings = new Hashtable<String, String>();
		loadPrereqMeanings(this.prereqMeaningsFile);
		this.GERRequirements = new Hashtable<String, HashSet<Prefix>>();
	}










	//////////////////////////////
	//////////////////////////////
	/////  Prereq stuff
	//////////////////////////////
	//////////////////////////////



	/**
	 * Make a requirement for all the prerequsites for this course.
	 * if p is null or the prefix has no requirement, 
	 * 		return null.
	 * @param p
	 * @return
	 */
	public Requirement getPrereqsShallow(Prefix p){
		if(p == null){
			return null;
		}
		String originalRequirementString = this.rawPrereqs.get(p);
		if(originalRequirementString == null){
			return null;
		}
		String ourVersion = this.savedPrereqMeanings.get(originalRequirementString);
		if(ourVersion == null){
			//try to parse this requirement from the raw data
			try{
				Requirement r = Requirement.readFromFurmanPrereqs(originalRequirementString);
				ourVersion = r.saveString();
				this.addPrereqMeaning(originalRequirementString, ourVersion);
			}catch (Exception e){
				//Handle special strings
				// These will not be saved in savedPrereqMeanings.

				//"any first year writing seminar" is common
				if(originalRequirementString.equals("any first year writing seminar")){
					Requirement result = new Requirement();
					TerminalRequirement t = TerminalRequirement.readFrom("FYW>0");
					t.setName("Any FYW");
					result.addRequirement(t);
					return result;
				}

				//"appropriate placement" is common
				if(originalRequirementString.toUpperCase().equals("APPROPRIATE PLACEMENT")){
					Requirement result = new Requirement();
					result.addRequirement(new TerminalRequirement(new Prefix("Placement", p.getSubject() + "-" + p.getNumber())));
					return result;
				}
				//"audition required" is common
				if(originalRequirementString.equals("audition required")){
					Requirement result = new Requirement();
					result.addRequirement(new TerminalRequirement(new Prefix("Audition", p.getSubject() + "-" + p.getNumber())));
					return result;
				}
				//If none of the special strings happened, we 
				//should ask the user and save their choice.
				return askUserToDefine(p, originalRequirementString);
			}
		}
		
		return Requirement.readFrom(ourVersion);
	}











	//////////////////////////////
	//////////////////////////////
	/////  Prereq meanings
	//////////////////////////////
	//////////////////////////////
	//  This section handles saving and loading
	//  the translation of weird prerequsites, like
	//	"ACC 122, 133, MTH 150 or MTH 145" and
	//  into valid, unambiguous requirement strings.
	//

	public boolean addRawPrereq(Course c, String prereqString){
		this.rawPrereqs.put(c.getPrefix(), prereqString);
		return true;
	}



	public Requirement askUserToDefine(Prefix p, String originalRequirementString){
		System.out.print("\nI need help here (" + p + "). Furman says it needs \n\t\""
				+ originalRequirementString 
				+"\"\n What does that requirement mean?\n>>>");
		Requirement result = null;
		if(masterIsNotAround){
			return null;
			//result = new Requirement();
			//result.setName("Unknown:" + originalRequirementString);
			//result.addRequirement(TerminalRequirement.readFrom("SSS<0"));
			//return result;
		}
		Scanner scan = new Scanner(System.in);
		String userInput = scan.nextLine();

		boolean valid = false;
		while(!valid){
			try{
				result = Requirement.readFrom(userInput);	
				valid = true;
			}catch (Exception e){
				if(userInput.toUpperCase().equals("QUIT") || 
						userInput.toUpperCase().equals("SKIP") ||
						userInput.toUpperCase().equals("S")){
					System.out.println("Ok, I'll skip this one until next time");
					//If the user said to skip, do this:
					result = new Requirement();
					result.setName(originalRequirementString);
					return result;
				}
				System.out.print(e.getMessage() + "\n" + originalRequirementString + "\n>>>");
				userInput = scan.nextLine();
				valid = false;
			}
		}
		addPrereqMeaning(originalRequirementString, userInput);
		return result;

	}

	public void addPrereqMeaning(String originalString, String ourMeaning){
		savedPrereqMeanings.put(originalString,ourMeaning);
		savePrereqMeanings(this.prereqMeaningsFile);
	}

	public void loadPrereqMeanings(String fileName){
		File f = new File(fileName);
		if(!f.exists()){
			try{
				f.createNewFile();
			}catch(Exception e){}
			savedPrereqMeanings = new Hashtable<String, String>();
			savePrereqMeanings(fileName);
			return;
		}
		try{
			FileInputStream fis = new FileInputStream(fileName);
			ObjectInputStream in = new ObjectInputStream(fis);
			savedPrereqMeanings = (Hashtable<String, String>) in.readObject();
			in.close();
		}catch(Exception e){
			System.out.println("There was an error loading the saved meanings");
			e.printStackTrace();
		}
	}
	public void savePrereqMeanings(String fileName){
		try{
			FileOutputStream fos = new FileOutputStream(fileName, false); //overwrite, don't append.
			ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(savedPrereqMeanings);
			out.close();
		}
		catch(Exception e){
			System.out.println("There was an error saving the meanings of your prereq strings");
			e.printStackTrace();
		}
	}







	//////////////////////////////
	//////////////////////////////
	/////  List methods
	//////////////////////////////
	//////////////////////////////

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



	//////////////////////////////
	//////////////////////////////
	/////  Filter methods
	//////////////////////////////
	//////////////////////////////

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

	public  ArrayList<Course> getCoursesIn(Semester s){
		ArrayList<Course> result =  onlyThoseIn(listOfCourses,s);
		return result;
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




	//////////////////////////////
	//////////////////////////////
	/////  GER stuff
	//////////////////////////////
	//////////////////////////////


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
	public Major getGERMajor(Prefix forignLang,int majorType){
		Major m = new Major("GER");


		outerloop:
			for(String key : GERRequirements.keySet()){
				//Special cases first, then default behavior.
				// default behavior is to add every prefix in the 
				// list to each requirement, and to do nothing to 
				// double dip numbers (if double dip numbers exist).

				Requirement r = new Requirement();
				r.setName(key);
				int newNumToChoose = 1;
				boolean includeDefaultPrefixes = true;



				switch(key){
				case "MR":
					//System.out.println("Major Type" + majorType);
					// "Students with a BS must complete this requirement with a calculus course"
					// "Students with a BM do not need to fulfill this requirement."
					switch ( majorType){
					case CourseList.BS:

						//For BS, the MR requirement is predefined
						r = (Requirement)Requirement.readFrom("1 of (MTH 150, 2 of (MTH 120, MTH 145))");
						r.setName("MR");
						includeDefaultPrefixes = false;
						break;
					case CourseList.BM:

						// For BM, there is no MR requirement.
						continue outerloop;
					case CourseList.BA:


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
					case CourseList.BS:
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
								r.addRequirement(new TerminalRequirement(p));
							}
						}
						for(Prefix p : GERRequirements.get("NW")){
							if(p.getNumber().compareTo("110") >= 0){
								r.addRequirement(new TerminalRequirement(p));
							}
						}
						includeDefaultPrefixes = false;
						break;
					case CourseList.BA:
					case CourseList.BM:
					default:
						for(Prefix p : GERRequirements.get("NWL")){
							r.addRequirement(new TerminalRequirement(p));
						}
					}
					break;
				case "NWL":
					//Music majors don't need this requirement.
					// they only need the NW requirement.
					if(majorType== CourseList.BM){
						continue outerloop;
					}
					break;
				case "HB":
					//every major type needs 2 hb.
					newNumToChoose = 2;
					break;
				case "WC":
				case "NE":

				}
				if(includeDefaultPrefixes){
					for(Prefix p : GERRequirements.get(key)){
						r.addRequirement(new TerminalRequirement(p));
					}
				}
				
				r.setNumToChoose(newNumToChoose);

				if(!r.name.equals("FL")){
					m.addRequirement(r);
				}



			}

		m.addRequirement(FLRequirement(forignLang ,majorType));
		m.addRequirement(FYWRequirement());

		//make NW and NWL enemies
		Requirement nwl = m.getRequirement("NWL");
		Requirement nw = m.getRequirement("NW");
		RequirementGraph.putEdge(nwl, nw);
		// Make WC and NE enemies 
		Requirement wc = m.getRequirement("WC");
		Requirement ne = m.getRequirement("NE");
		RequirementGraph.putEdge(wc, ne);
		
		//put WC and NE at the end.
		m.removeRequirement(wc);
		m.removeRequirement(ne);
		m.addRequirement(wc);
		m.addRequirement(ne);
		
		return m;
	}

	public Requirement FYWRequirement(){
		Requirement result = new Requirement();
		result.addRequirement(TerminalRequirement.readFrom("FYW>0"));
		result.setName("FYW");
		return result;
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




	public Requirement FLRequirement(Prefix p, int degreeType){
		int standard=0;
		Requirement	r= new Requirement();
		r.setName("FL");
		if(degreeType == CourseList.BS){
			standard =120;
		}
		if(degreeType == CourseList.BA || degreeType == CourseList.BM){
			standard=201;
		}


		if (p != null){

			r.addRequirement(TerminalRequirement.readFrom(p.getSubject() + ">=" + p.getNumber()));

		}

		if(p==null || !p.getSubject().equals("GRK") ) {

			r.addRequirement(TerminalRequirement.readFrom("GRK" + ">=" + standard + "<=" + (standard+100)));
		}
		if(p==null || !p.getSubject().equals("LTN")){

			r.addRequirement(TerminalRequirement.readFrom("LTN" + ">=" + standard + "<=" + (standard+100)));
		}
		if(p==null || !p.getSubject().equals("JPN")){

			r.addRequirement(TerminalRequirement.readFrom("JPN" + ">=" + standard + "<=" + (standard+100)));
		}
		if(p==null || !p.getSubject().equals("FRN")){

			r.addRequirement(TerminalRequirement.readFrom("FRN" + ">=" + standard + "<=" + (standard+100)));
		}
		if(p==null || !p.getSubject().equals("SPN")){

			r.addRequirement(TerminalRequirement.readFrom("SPN" + ">=" + standard + "<=" + (standard+100)));
		}
		if(p==null || !p.getSubject().equals("CHN")){

			r.addRequirement(TerminalRequirement.readFrom("CHN" + ">=" + standard + "<=" + (standard+100)));
		}


		return r;
	}


	//////////////////////////////
	//////////////////////////////
	/////  Reading
	//////////////////////////////
	//////////////////////////////


	public static CourseList readAll(){
		CourseList result = new CourseList();
		File f = new File(courseListFolder);
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
					String prerequsitesString = data.get(16);
					if(!prerequsitesString.equals("")){
						this.addRawPrereq(lastCourse, prerequsitesString.trim());
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

	/**
	 * TODO Doesn't catch requirements that auto-read from strings of the form
	 * "MTH-110, MTH-220".
	 */
	public void viewKnownPrereqs(){
		for(Prefix key : rawPrereqs.keySet()){
			String theirs = rawPrereqs.get(key);
			String ours  = savedPrereqMeanings.get(theirs);
			if(ours != null){
				String keyString = key.toString();
				String tabs = "\t\t";
				if(keyString.length () > 7){
					tabs = "\t\t";
				}
				System.out.println( key.toString() + tabs + "theirs:" + theirs +  "\tours:" + ours);
			}
		}
	}

	public void addToPrereqMeanings(){
		for(Prefix p : rawPrereqs.keySet()){
			Requirement r = getPrereqsShallow(p);
		}
	}
	public ArrayList<String> allUnknownPrereqs(){
		ArrayList<String> result = new ArrayList<String>();
		for(Prefix p : rawPrereqs.keySet()){
			String originalString = rawPrereqs.get(p);
			if(originalString.toUpperCase().equals("APPROPRIATE PLACEMENT")
					|| originalString.equals("any first year writing seminar")
					|| originalString.equals("audition required")){
				continue;
			}
			if(savedPrereqMeanings.get(originalString) == null){
				result.add(p.toString() + ":\t" + originalString);
			}
		}
		return result;
	}

	//////////////////////////////
	//////////////////////////////
	/////  Testing
	//////////////////////////////
	//////////////////////////////

	public static void main(String[] args){
		CourseList c = CourseList.readAll(); //CourseList.testList();
		c.viewKnownPrereqs();
		/*for(String s : c.allUnknownPrereqs()){
			System.out.println(s);
		}*/
		/*for(Course cour : c.listOfCourses){
			System.out.println(cour.saveString());
		}*/

	}


}