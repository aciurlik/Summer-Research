


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Scanner;
/**
 * This is a static class that can hold and filter the full list of courses. 
 * All actual Furman courses used by the program should be in listOfCourses.
 * 
 * This class also handles the prereq data for each course (technically each prefix, 
 * because prereq depends on prefix not course).
 * 
 * The most important methods for the data side are
 * getPrereqs(Prefix), getGERMajor(languagePrefix, majorType), and the
 * course getters. 
 * 
 * If you're a coder and you want to modify or add prereqs, check out the methods
 * getPrereq(prefix) and addToPrereqMeanings(), and possibly the whole section 
 * prereqUtilityMethods.
 * 
 * CourseList is in the DATA group of classes.
 * 
 * 
 *
 */

public class CourseList implements java.io.Serializable  {
	private static final long serialVersionUID = 1L;
	//Numbering is dependent on the difficulty of completing the GERs associated with this degree type
	//The higher the number the more classes are needed and therefore if there are two majors of different degree types
	//added, the one with the higer degree Type value will dictate the GER list

	

	public static final int defaultCreditHours = 4;
	public static final boolean nwAndNwlAreOne = true; //for Dr. Bouzarth,
	// makes the NW requirement and NWL requirement into one requirement.
	// It's not well tested what happens if you set this to false.


	private static ArrayList<Course> listOfCourses; //the fundamental field of this class,
	// holds all courses imported from furman data.
	
	
	//These two fields store prereqs and their meanings-
	//raw prereqs are imported from the course file downloaded from myFurman,
	// but these raw prereqs are sometimes ambiguous, so 
	// the hashtable savedPrereqMeanings associates each raw prereq
	// with a requirement string (see the requirement class for details of how
	// to generate a requirement's string).
	private static Hashtable<Prefix, String> rawPrereqs;
	static Hashtable<String, String> savedPrereqMeanings;
	
	//This Hashtable associates each GER string (i.e., "HB" or "NWL")
	// with the set of prefixes that can count towards it.
	public static Hashtable<String, HashSet<Prefix>> GERRequirements;
	
	
	
	
	
	
	
	
	
	
	///////////////
	/////////The next few lines of code deal with
	///////// language prerequisites, which have 
	///////// underspecified prereqs in a MyFurman
	///////// course download. Because they are
	///////// underspecified, we use a hashMap
	///////// (flPrereqs) to directly associate
	///////// language prefixes to prereq 
	///////// requirements. 
	//////////////
	
	
	// the list of all subjects which are languages.
	// languagePrefixes is used in the code, lp is just used to initialize languagePrefixes.
	public static HashSet<String> languagePrefixes;
	public static final String[] lpArray = {"FRN", "LTN", "SPN", "GRK", "GRM", "CHN", "JPN"};

	//This hashMap handles the special cases of languages, which have 
	// underspecified prereqs when you just read the course list from myFurman.
	// Each language prefix, like CHN-110, is associated with a requirement
	// whichis that prefix's prereq, like "1 of (CHN-101, CHN-110.PL)".
	public static HashMap<Prefix, Requirement> flPrereqs;




	static String[] flClassNumbers = 
		    {		"110", 			"115", 			"120", 						"201", 								"202", 						"210", 							"214",						"215", 										"220", 						"221", 						"222", 											"230", 						"231","232","233","234","235","240", 								"270",		   					"310", 	   "320", 		"331", 				    "332", 					"333", 					"334"};
	private static String[][] languagePrereqsArray = {
			{"CHN", "CHN-PL.110",   null,           "(CHN-110, CHN-PL.120)", 	"(CHN-120, CHN-PL.201)", 			"(CHN-201, CHN-PL.202)", 	null, 		  					null,						null, 										null, 						null, 						null, 											null, 						null, null, null, null, null, null, 								null,							null, 		null, 		null, 					null, 					null, 					null},
			{"FRN", "FRN-PL.110",  "FRN-PL.115",    "(FRN-110)", 				"(FRN-115, FRN-120, FRN-PL.201)", 	null, 						"(FRN-201, FRN-PL.210+)", 		"(FRN-201, FRN-PL.210+)",	"(FRN-201, FRN-PL.210+)",                   "(FRN-201)", 	            "(FRN-201)",             	null, 										    null, 						null, null, null, null, null, null, 								null,							null, 		null, 		null, 					null, 					null, 					null },
			{"GRK", "GRK-PL.110",   null,			"(GRK-110)", 				"(GRK-120, GRK-PL.201)",	 		null, 						null, 							null,						null, 										null, 						null, 						null, 											null, 						null, null, null, null, null, null, 								null,							"GRK-301", 	"GRK-301", "GRK-301", 				"GRK-301", 				"GRK-301", 				null },
			{"GRM", "GRM-PL.110",   null, 	        "(GRM-PL.110, GRM-PL.120)", "(GRM-120, GRM-PL.201)", 	        null, 						"(GRM-201, GRM-PL.210+)", 		null,						"(GRM->200, GRM-PL.210+)", 					"(GRM->200, GRM-PL.220+)", 	null, 						"(GRM->120, GRM-PL.210+)",                      "(GRM->200, GRM-PL.200+)", 	null, null, null, null, null, null, 								null,							null, 		null, 		null, 					null, 					null, 					null},
			{"JPN", "JPN-PL.110",   null, 			"(JPN-110, JPN-PL.120)", 	"(JPN-120, JPN-PL.201)", 			"(JPN-201, JPN-PL.202)", 	null, 							null,						null, 										null, 						null, 						null, 											null, 						null, null, null, null, null, null, 								null,							null, 		null, 		null, 					null, 					null, 					null},
			{"LTN", "LTN-PL.110",   null, 			"LTN-110", 					"(LTN-120, LTN-PL.201)", 			null, 						null, 							null,						null, 										null, 						null, 						null, 											null, 						null, null, null, null, null, null, 								null,							null, 		null, 		"(LTN-301, LTN-PL.UL)", "(LTN-301, LTN-PL.UL)", "(LTN-301, LTN-PL.UL)", "(LTN-301, LTN-PL.UL)"},
			{"SPN", "SPN-PL.110",   "SPN-PL.115", 	"SPN-110", 					"(SPN-115, SPN-120, SPN-PL.201)", 	null, 						"(SPN-201, SPN-PL.210+)", 		null,						"(SPN-201, SPN-PL.210+, SPN-PL.215)", 		"(SPN-215, SPN-PL.220)",    null, 						null, 											null, 						null, null, null, null, null, "(SPN-215, SPN-PL.220+)",	            "(SPN-215, SPN-PL.220)",		null, 		null, 		null, 					null, 					null, 					null}

	};
	

	/**
	 * This static block loads languagePrereqs
	 * by initializing languagePrefixes based on lpArray  and 
	 * filling flPrereqs based on languagePrereqsArray.
	 */
	static {
		
		languagePrefixes = new HashSet<String>();
		for(String s: lpArray){
			languagePrefixes.add(s);
		}
		
		
		//based on languageReqs above
		flPrereqs = new HashMap<Prefix, Requirement>();
		for(int i= 0; languagePrereqsArray.length>i; i++){ //subjects
			for(int n = 0; flClassNumbers.length>n; n++){ //prefix numbers that correspond
				Requirement req= null;
				if(languagePrereqsArray[i][n+1] != null){ //refers to the corresponding place in the table that will become the newPreReqs. 
					req = Requirement.readFrom(languagePrereqsArray[i][n+1]);
					Prefix p = new Prefix(languagePrereqsArray[i][0], flClassNumbers[n]);
					flPrereqs.put(p, req );
				}
			}
		}
	}

	
	



	
	
	
	
	
	
	


	/////////////////////////
	/////////////////////////
	////Prereq stuff
	/////////////////////////
	/////////////////////////
	@SuppressWarnings("unused")
	private boolean ___Prereqs_________;



	/**
	 * Make a requirement for all the prerequsites for this course.
	 *      if p is null or the prefix has no requirement, then
	 * 		return null.
	 * @param p
	 * @return
	 */
	public static Prereq getPrereq(Prefix p){
		if(p == null){
			return null;
		}
		String originalRequirementString = rawPrereqs.get(p);
		if(originalRequirementString == null){
			// no raw prereq, so no prereq for this prefix.
			return null;
		}
		String ourVersion = savedPrereqMeanings.get(originalRequirementString);
		Requirement result = new Requirement();
		boolean isPlaceableCourse = isPlaceableCourse(p);
		if(ourVersion != null){
			result = Requirement.readFrom(ourVersion);
		}
		else{
			//if a raw prereq exists but the saved meaning doesn't, then we need
			// to try to figure out what the raw prereq means.
			
			
			//first, try to read this rawPrereq as a requirement.
			try{
				result = Requirement.readFromFurmanPrereqs(originalRequirementString);
				ourVersion = result.saveString();
				addPrereqMeaning(originalRequirementString, ourVersion);
			}catch (Exception e){
				
				//Second, handle special strings like 'appropriate placement'
				// If successful, the resulting requirement will not be 
				// saved in savedPrereqMeanings, so every specialString prereq
				// should pass through this code every time someone
				// asks for the prereq.

				//the string "any first year writing seminar" is common
				if(originalRequirementString.equals("any first year writing seminar")){
					result = new Requirement();
					TerminalRequirement t = TerminalRequirement.readFrom("FYW>0");
					t.setName("Any FYW");
					result.addChoice(t);
				}
				//the string "audition required" is common
				else if(originalRequirementString.equals("audition required")){
					result = new Requirement();
					result.addChoice(new TerminalRequirement(new Prefix("Audition", p.getSubject() + "-" + p.getNumber())));
				}
				//the string "appropriate placement" is common
				else if(originalRequirementString.toUpperCase().equals("APPROPRIATE PLACEMENT")){
					isPlaceableCourse = true;
				}
				else{
					//If none of the special strings happened, we 
					//should ask the user and save their choice.
					// This may mean that result == null.
					result = askUserToDefine(p, originalRequirementString);
				}
			}
		}
		if(isPlaceableCourse){
			//If you can place into result, if result.numToChoose == 1 then 
			// you can just add the placementRequirement, otherwise make a new
			// requirement which is "either result, or placement."
			Requirement placementRequirement = new TerminalRequirement(new Prefix(p.getSubject()  ,  "PL."+p.getNumber()));
			if(result == null){
				result =  placementRequirement;
			}
			else if(result.numToChoose == 1){
				result.addChoice(placementRequirement);
			}
			else{
				Requirement result2 = new Requirement();
				result2.addChoice(result);
				result2.addChoice(placementRequirement);
				result = result2;
			}
		}

		return new Prereq(result, p);

	}


	/**

	 * Used to tell if the prereq should include a placement into the course
	 * 
	 * currently (7/23/2017) just used for FL, but if extend the prefix
	 * "subj-PL.num" will automatically be added to the prereq for this
	 * prefix when you call getPrereq(p).

	 * @param p
	 * @return true if it should include, false otherwise. 
	 */
	public static boolean isPlaceableCourse(Prefix p){
		if(languagePrefixes.contains(p.getSubject())){
			return true;
		}
		return false;

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
	@SuppressWarnings("unused")
	private boolean ______PrereqMeanings_________;

	public static boolean addRawPrereq(Course c, String prereqString){
		rawPrereqs.put(c.getPrefix(), prereqString);
		return true;
	}



	/**
	 * This method lets the user define a raw prereq in terms of 
	 * a requirement that follows the syntax specified in the Requirement class.
	 * 
	 * It includes some user-friendly behavior, like checking for errors in typing.
	 * 
	 * To quit, you can type s (lowercase only), Skip (any case), or quit (any case).
	 * 
	 * Because we don't expect users to learn the syntax for requirements, this method
	 * only does anything if masterIsAround.
	 * 
	 * @param p
	 * @param originalRequirementString
	 * @return
	 */
	public static Requirement askUserToDefine(Prefix p, String originalRequirementString){
		
		if(!FurmanOfficial.masterIsAround){
			return null;
		}
		
		System.out.print("\nI need help here (" + p + "). Furman says it needs \n\t\""
				+ originalRequirementString 
				+"\"\n What does that requirement mean?\n>>>");
		Requirement result = null;
		
		
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
					scan.close();
					return result;
				}
				System.out.print(e.getMessage() + "\n" + originalRequirementString + "\n>>>");
				userInput = scan.nextLine();
				valid = false;
			}
		}
		addPrereqMeaning(originalRequirementString, userInput);
		scan.close();
		return result;

	}

	/**
	 * Add the new meaning, and automatically save.
	 * @param originalString
	 * @param ourMeaning
	 */
	public static void addPrereqMeaning(String originalString, String ourMeaning){
		savedPrereqMeanings.put(originalString,ourMeaning);
		FileHandler.savePrereqMeanings();
	}
	
	//////////////////////////////
	//////////////////////////////
	/////  Prereq utility methods
	//////////////////////////////
	//////////////////////////////
	// These methods help you see
	// which raw prereqs are still left
	// to define, and see exactly what definition
	// is currently being used for
	// the known raw prereqs.
	@SuppressWarnings("unused")
	private boolean ______PrereqUtilityMethods_________;

	
	


	public static void viewKnownPrereqs(){
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

	/**
	 * If masterIsAround, calling this method will call up the 
	 * user-interface in the console to let you add prereq meanings.
	 */
	public static void addToPrereqMeanings(){
		for(Prefix p : rawPrereqs.keySet()){
			getPrereq(p);
		}
	}
	public static ArrayList<String> allUnknownPrereqs(){
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

	public static void setPrereqMeaning(String theirString, String ourString){
		savedPrereqMeanings.put(theirString, ourString);
		FileHandler.savePrereqMeanings();
	}


	//////////////////////////////
	//////////////////////////////
	/////  Filter methods
	//////////////////////////////
	//////////////////////////////
	@SuppressWarnings("unused")
	private boolean ___FilterMethods_________;

	/**
	 * Return only those members of input which are in the given semester.
	 * @param input
	 * @param s
	 * @return
	 */
	public static ArrayList<Course> onlyThoseIn(Iterable<Course> input, Semester s){
		ArrayList<Course> SemesterList = new ArrayList<Course>();

		for(Course course : input){
			if ( course.getSemester().compareTo(s.getDate()) == 0){
				
				SemesterList.add(course);
			}
		}
		return SemesterList;
	}

	public static  ArrayList<Course> getCoursesIn(Semester s){
		if(s.isAP){
			return new ArrayList<Course>();
		}
		ArrayList<Course> result =  onlyThoseIn(listOfCourses,s);
		return result;
	}

	public static ArrayList<Course> onlyThoseSatisfying(Iterable<Course> input, Requirement r){
		ArrayList<Course> result = new ArrayList<Course>();
		for(Course c : input){
			if(r.isSatisfiedBy(c.getPrefix())){
				result.add(c);
			}
		}
		return result;
	}
	public  static ArrayList<Course> getCoursesSatisfying(Requirement r){
		return onlyThoseSatisfying(listOfCourses,r);
	}

	/**
	 * Returns an empty list if 
	 * @param input
	 * @param p
	 * @return
	 */
	public static ArrayList<Course> onlyThoseSatisfyingPrefix(Iterable<Course> input, Prefix p){

		ArrayList<Course> result = new ArrayList<Course>();
		for(Course c: input){

			if(c.getPrefix()!= null && c.getPrefix().equals(p)){

				result.add(c);
			}
		}
		return result;
	}

	public static ArrayList<Course> getCoursesSatisfying(Prefix p){
		return onlyThoseSatisfyingPrefix(listOfCourses, p);
	}

	/**
	 * Return the 
	 * @param p
	 * @return
	 */
	public static int getCoursesCreditHours(Prefix p){
		if(p!= null && p.getNumber().contains("PL.")){ //Placement courses don't give credits
			return 0;
		}
		if(getCoursesSatisfying(p).isEmpty()){
			return defaultCreditHours;
		}
		return getCoursesSatisfying(p).get(0).creditHours;
	}





	//////////////////////////////
	//////////////////////////////
	/////  GER stuff
	//////////////////////////////
	//////////////////////////////
	@SuppressWarnings("unused")
	private boolean ___GERStuff_________;

	
	/**
	 * Go from degree type numbers to degree type strings, i.e.
	 * 1 -> "BS"
	 * @param i
	 * @return
	 */
	public static String getDegreeTypeString(int i){
		if(i == Major.BS){
			return "BS";
		}
		if(i == Major.BA){
			return "BA";
		}
		if(i == Major.BM){
			return "BM";
		}
		return "null";
	}



	/**
	 * Go from degree strings to degree numbers, i.e.
	 * "BM" --> 1
	 * 
	 * s must be 2 characters
	 * @param s
	 * @return
	 */
	public static int getDegreeTypeNumber(String s){
		if(s.equals("BM")){
			return Major.BM;
		}
		if(s.equals("BS")){
			return Major.BS;
		}
		if(s.equals("BA")){
			return Major.BA;
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
	public static Major getGERMajor(Prefix forignLang,int majorType){
		Major m = new Major("GER");
		ArrayList<Requirement> allReqs = new ArrayList<Requirement>();
		outerloop:
			for(String key : GERRequirements.keySet()){
				//Special cases first, then default behavior.
				// default behavior is to add every prefix in the 
				// list to each requirement, and to do nothing to 
				// double dip numbers (if double dip numbers exist).
				// and to set the name of the requirement based on the key.


				//Default stuff:
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
					case Major.BS:

						//For BS, the MR requirement is predefined
						r = (Requirement)Requirement.readFrom("1 of (MTH 150, 2 of (MTH 120, MTH 145))");
						r.setName("MR");
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
					} //end switch
					break;
					//The following cases are handled explicitly at the end.
				case"FL":
					continue outerloop;
				case "NW":
					switch(majorType){
					case Major.BS:
						newNumToChoose = 2;
						//only courses numbered 110 or greater
						for(Prefix p : GERRequirements.get("NWL")){
							if(p.getNumber().compareTo("110") >= 0){
								r.addChoice(new TerminalRequirement(p));
							}
						}
						for(Prefix p : GERRequirements.get("NW")){							if(p.getNumber().compareTo("110") >= 0){
								r.addChoice(new TerminalRequirement(p));
							}
						}
						includeDefaultPrefixes = false;
						break;
						//Add the NWL requirements to the NW requirements.
					case Major.BA:
						newNumToChoose = 2;
					case Major.BM:
					default:
						for(Prefix p : GERRequirements.get("NWL")){
							r.addChoice(new TerminalRequirement(p));
						}
					}
					break;
				case "NWL":
					//Music majors don't need this requirement TODO check if it's NW or NWL that they don't need.
					if(majorType == Major.BM){
						continue outerloop;
					}
					if(majorType == Major.BS){
						for(Prefix p : GERRequirements.get("NWL")){
							if(p.getNumber().compareTo("110") >= 0){
								r.addChoice(new TerminalRequirement(p));
							}
						}
						includeDefaultPrefixes = false;
					}
					break;
				case "HB":
					//every major type needs 2 hb.					newNumToChoose = 2;
					break;
				case "WC":
				case "NE":

				} //end switch
				if(includeDefaultPrefixes){
					for(Prefix p : GERRequirements.get(key)){
						r.addChoice(new TerminalRequirement(p));
					}
				}
				r.setNumToChoose(newNumToChoose);
				m.addRequirement(r);
			}

		//Special behavior for foreign language and FYW 
		m.addRequirement(FLRequirement(forignLang ,majorType));
		m.addRequirement(FYWRequirement());

		//collect nw, nwl, or nw/nwl into the list naturalWorldReqs.

		Requirement nwl = m.getRequirement("NWL");
		Requirement nw = m.getRequirement("NW");
		m.removeRequirement(nwl);
		m.removeRequirement(nw);
		ArrayList<Requirement> naturalWorldReqs = new ArrayList<Requirement>();
		if(nwAndNwlAreOne){
			if(majorType == Major.BM){

				naturalWorldReqs.add(nw);

			}
			else{
				Requirement nwnwl = new Requirement();
				nwnwl.addChoice(nw);
				nwnwl.addChoice(nwl);
				nwnwl.setNumToChoose(2);
				nwnwl.setName("NW/NWL");

				naturalWorldReqs.add(nwnwl);

			}
		}
		else{
			naturalWorldReqs.add(nw);
			naturalWorldReqs.add(nwl);
		}

		// Make WC and NE enemies 
		Requirement wc = m.getRequirement("WC");
		Requirement ne = m.getRequirement("NE");
		RequirementGraph.putEdge(wc, ne);

		//sort the GER list
		String[] beforeNW = {"FYW", "WR"};
		String[] afterNW = {"HB","HA","TA","VP","MR","FL","UQ","MB","NE", "WC"};
		for(String s : beforeNW){
			Requirement holder = m.getRequirement(s);
			m.removeRequirement(holder);
			m.addRequirement(holder);
		}
		for(Requirement r : naturalWorldReqs){
			m.addRequirement(r);
		}
		for(String s : afterNW){
			Requirement holder = m.getRequirement(s);
			if(holder != null){
				m.removeRequirement(holder);
				m.addRequirement(holder);
			}
		}

		m.setChosenDegree(majorType);
		return m;
	}

	
	
	/**
	 * Used by courseChooser to display the right string to the user
	 * when a course satisfies the NW/NWL requirement.
	 * @param p
	 * @return
	 */
	public static boolean isNWL(Prefix p){
		return GERRequirements.get("NWL").contains(p);
	}


	/*
	 * When this method is used, the user can't see which courses
	 * satisfy the NW requirement and which courses satisfy the NWL requirement.
	 * 
	 * 
	public static Requirement NWRequirement(int majorType){
		//From the Furman GER checklist, NW requires:
		// 		"Two courses, at least one with a 
		// 		separate labratory component.
		// 
		// In addition, "Students seeking the Bachelor of Music degree 
		// must complete only one course to meet this requirement, 
		// while Bachelor of Science degree candidates must complete 
		// this requirement in courses appropriate for majors in 
		// the natural science disciplines. "
		Requirement nw = new Requirement();
		Requirement nwl = new Requirement();
		Requirement result = new Requirement();
		result.setName("NW/NWL");
		switch(majorType){
		case CourseList.BS:
			// only courses "appropriate for majors in the 
			// natural science disciplines," which currently (6/11/2017) means
			//
			// "courses numbered 110 or greater in Biology, Chemistry, 
			//  Earth and Environmental Science, Neuroscience, Physics, 
			//  "Sustainability Science."
			// We represent this by r = 2 of (2 NWs, 1 NWL).
			for(Prefix p : GERRequirements.get("NWL")){
				if(p.getNumber().compareTo("110") >= 0){
					nw.addRequirement(new TerminalRequirement(p));
					nwl.addRequirement(new TerminalRequirement(p));
				}
			}
			for(Prefix p : GERRequirements.get("NW")){
				if(p.getNumber().compareTo("110") >= 0){
					nw.addRequirement(new TerminalRequirement(p));
				}
			}
			nw.setNumToChoose(2);
			nwl.setNumToChoose(1);
			result.addRequirement(nw);
			result.addRequirement(nwl);
			result.setNumToChoose(2);
			return result;
		case CourseList.BA:
			//Handled like a normal GER requirement, just have to mix the
			// NWL and NW requirements into the result.
			for(Prefix p : GERRequirements.get("NWL")){
					nw.addRequirement(new TerminalRequirement(p));
					nwl.addRequirement(new TerminalRequirement(p));
			}
			for(Prefix p : GERRequirements.get("NW")){
					nw.addRequirement(new TerminalRequirement(p));
			}
			result.addRequirement(nw);
			result.addRequirement(nwl);
			result.setNumToChoose(2);
			return result;
		case CourseList.BM:
			//Only need one course for this requirement, check if it has to be a lab?
			for(Prefix p : GERRequirements.get("NW")){
				result.addRequirement(new TerminalRequirement(p));
			}
			return result;
		default:
			throw new RuntimeException("Tried to get a NW requirement for major type " + majorType);
		}
	}
	 */

	
	public static Requirement FYWRequirement(){
		Requirement result = new Requirement();
		result.addChoice(TerminalRequirement.readFrom("FYW>0"));
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
	public static void setCourseSatisfiesGER(String GERs, Course c){
		String[] allGERs = GERs.trim().split(" ");

		for(String s : allGERs){
			HashSet<Prefix> old = GERRequirements.get(s);
			if(old == null){
				old = new HashSet<Prefix>();
				GERRequirements.put(s, old);
			}
			old.add(c.getPrefix());
		}
	}




	/**
	 * Get the FL requirement based on this degree type and language prefix.
	 * Language prefix specifies the placement that the student got, so
	 * something like "FRN-201" or "FRN-201.PL", and may be null. 
	 * The requirement would then be to get FRN-201 or higher, or else
	 * to get up to some standard level in a non-FRN language.
	 * 
	 * @param languagePrefix
	 * @param degreeType
	 * @return
	 */
	public static Requirement FLRequirement(Prefix languagePrefix, int degreeType){
		int standard=0;
		Requirement	r= new Requirement();
		r.setName("FL");
		if(degreeType == Major.BS){
			standard =120;
		}
		if(degreeType == Major.BA || degreeType == Major.BM){
			standard=201;
		}

		String[] languages = {"GRK", "LTN", "JPN", "FRN", "SPN", "CHN", "GRK"};
		String subj = "";

		//if you've got a prefix like FRN-201, and it's greater than or equal to
		// standard, then add it to the list of requirements.
		if (languagePrefix != null){
			subj = languagePrefix.getSubject();
			int number = Integer.parseInt(languagePrefix.getNumber());
			if(number < standard){
				number = standard;
			}
			r.addChoice(TerminalRequirement.readFrom(subj + ">=" + number));
		}

		//For every language that isn't the same as your languagePrefix, add the standard requirement.
		for(String language : languages){
			if(!language.equals(subj)){
				r.addChoice(TerminalRequirement.readFrom(language + ">=" + standard));
			}

		}

		return r;
	}


	//////////////////////////////
	//////////////////////////////
	/////  Reading
	//////////////////////////////
	//////////////////////////////
	@SuppressWarnings("unused")
	private boolean ___Reading_________;


	
	/**
	 * Initialize and load all the courses, prereqs, and static 
	 * 		fields in CourseList.
	 * Only needs to be called once per program initiation - 
	 * 		called from Driver while loading files.
	 *  
	 */
	public static void loadAllCourses(){
		listOfCourses = new ArrayList<Course>();
		GERRequirements = new Hashtable<String, HashSet<Prefix>>();
		rawPrereqs = new Hashtable<Prefix, String>();
		savedPrereqMeanings = new Hashtable<String, String>();
		FileHandler.readAllCourses();
		FileHandler.loadPrereqMeanings();
		
	}


	/**
	 * Given a list of strings of the form found in a downloaded course catalog
	 * (see the Course Catalogs folder in resources for an example)
	 * convert this list into a list of courses.
	 * 
	 * last updated 7/25/2017
	 * 
	 * @param fileLines
	 */
	public static void addCoursesIn(ArrayList<String> fileLines){
		String lastSectionNumber = "";
		Course lastCourse = null;
		Course duplicateCourse =null;

		//skip the first line of field names

		int index = 1;
		//Read in each course
		while(index < fileLines.size()){
			ArrayList<String> data = SaverLoader.parseCSVLine(fileLines.get(index));
			String sectionNumber = data.get(1);
			//Check if we've found a new course
			if(! sectionNumber.equals( lastSectionNumber)){
				//If it's a new course, make a new one.
				lastSectionNumber = sectionNumber;
				if(lastCourse != null){
					listOfCourses.add(lastCourse);
				}
				lastCourse = Course.readFromFurmanData(data);
				if(data.get(0).contains("Summer")){
					if(lastCourse.meetingTime==null || lastCourse.meetingTime[0].isAllUnused()  ){
						Course newDuplicateCourse = Course.readFromFurmanData(data);
						if( newDuplicateCourse != duplicateCourse){
							if(duplicateCourse != null){



								listOfCourses.add(duplicateCourse);	
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
					addRawPrereq(lastCourse, prerequsitesString.trim());
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
			index++;
		}
		if(lastCourse != null){
			listOfCourses.add(lastCourse);

		}
		if(duplicateCourse != null){
			listOfCourses.add(duplicateCourse);
		}
	}
	
	


	//////////////////////////////
	//////////////////////////////
	/////  Testing
	//////////////////////////////
	//////////////////////////////
	@SuppressWarnings("unused")
	private boolean ___Testing_________;



	public static void main(String[] args){
		viewKnownPrereqs();

	}

}