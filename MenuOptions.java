


public class MenuOptions {
	/**
	 * Blurb written 7/18/2017
	 * Last updated 7/18/2017
	 * 
	 * All buttons in the GUI should reference a String in MenuOptions. 
	 * If you add a new button/GUI Element string it should be placed in this class
	 * in the appropriate place. Add a comment in the appropriate location if you 
	 * borrow a String that has already been written, this will act as a place holder. 
	 * The spacing/structure of this class should mirror the location in the GUI. The more 
	 * indented the String is the the farther it is down the tree (for example if it is a submenu of a 
	 * submenu) Look to the outline to find the broader categories. 
	 * 
	 */
	
	
	//////////////////////////
	//Both Additions and Menu
	//////////////////////////
	@SuppressWarnings("unused")
	private int _______Both_Addititions_And_Menu_______________;
	
		//Furman Advantage
		public static final String FurmanAdvantage = "The Furman Advantage";
			public static final  String exploreStudyAway = "Explore Study Away";
			public static final  String addResearch = "Explore Research";
			public static final  String exploreInternship = "Explore Internships";
			public static final  String addMayX = "Add MayX";
			public static final  String addSummerClass = "Add Summer Session";
		
			public static final  String addMajor = "Add Major";
			public static final  String addMinor = "Add Minor";
			public static final  String addTrack = "Add Track";

	///////////////////
	//Only Menu Bar 
	/////////////////////
		@SuppressWarnings("unused")
		private int ______________Menu_Bar_______________;
		//Furman Advantage Header used in both
		
			//Furman Advantage submenu
			public static final String MayX = "MayX";
			//MayX submenu
				public static final  String exploreMayX = "Explore MayX Opportunities";
				//Add MayX is in both Additions, and Menu Bar
			public static final String 	StudyAbroad = "Study Abroad";
				//Explore Study Abroad is in both Additions and Menu Bar
			//Add Summer Class is in both A&MB
			//Explore Internships is in both A&MB
			
		
		//File 
		public static final String file = "File"; //Kept lowercase so it didn't interfere with File class
	 		//File Submenu
			public static final  String newSchedule = "New Schedule";
				public static final String newBlankSchedule = "Blank Schedule";
				public static final String newLoadedSchedule = "Use Last Import";
			public static final  String openSchedule = "Open Schedule";
			public static final  String saveSchedule = "Save Schedule";
			public static final  String printSchedule = "Print Schedule";
			public static String deleteSchedule = "Delete Schedule";
			public static String checkAllErrors = "Check all Errors";
			public static String loadPriorCourses = "Import Prior Courses";
						public static String student = "Student";
							public static String dragAndDrop = "Drag and Drop via MyFurman";
								public static String studentDnD = "studentDnD";
						public static String advisor = "Advisor";
							public static String downloadcsv = "Import via csv";
							//public static String dragAndDrop = "Drag and drop via MyFurman"; //This needs a special 
						    //actionCommand (advisorDnD) to give different instructions to the user. 
								public static String advisorDnD = "advisorDnD";
		
		//Edit 
		public static final String Edit = "Edit";
			//Edit submenu
			public static final String Major = "Major";
			public static final String Minor = "Minor";
			public static final String Track = "Track";
			//All of the add Major/Minor/Track are all in both A&MB
			
			
			
	
		//Settings
		public static String settings = "Settings";
			//Setting submenu
				public static String changeSettings = "Change Settings";
					//Used when user clicks change Settings
					public static String saveChanges = "Save Changes";
					//Cancel is located in supriseMe (String is used in both)

					////
					//Setting utilization
					//////
					public static String startUp = "Show Start Up";
						//Commands within StartUpMenu 
						public static String previous = "Previous";
						public static String next = "Next";
						public static String finish = "Finish";
					public static String restoreDefault = "Restore Default Settings";
		

		//Help 
		public static final  String help ="Help";
				// submenus in help
				public static String viewStartUp = "View Start Up Guide";
				public static String examineRequirementHelp = "Requirement Help";
				public static String findACourse = "Find a Course";
	
	////////////////////////
	///// Only AdditionsPanel
	////////////////////////
	@SuppressWarnings("unused")
	private int ____________Only_Additions_Panel_______________;
		//The Furman Advantage Header used in both
		public static final String MajorMinor = "Major/Minor";
				
	///////////////////
	////Panels
	////////////////////
	@SuppressWarnings("unused")
	private int ____________Panels_______________;
		//Schedule Element Panel
		public static final  String addCourseWithRequirement = "Choose a course";

		//Requirement panel
		public static final String examineRequirementRightClick = "Examine Requirement";
	
		//Semester Panel

			//Directly Semester Panel
			public static final  String deleteSemester = "x";
			public static final  String hideSemester = "-";
			public static final  String showSemester = "+";
		
			//Options
			public static final String Options = "Options";
				//Options submenu
				public static final  String addInstruct = "Add a Course";
				public static final  String addStudyAway = "Mark as Study Away";
				public static final  String removeStudyAway = "Remove Study Away";
				public static final  String addNotes = "Add Note"; //The add/remove buttons automatically interchange depending on the state of the Semester.
				public static final  String removeNotes = "Remove Note";
				public static final  String supriseMe = "Surprise Me!";
					//Suprise Me 
					public static final  String Want = "I want it";
					public static final  String Challenge = "Take the Challenge";
					public static final  String tryAgain = "Try Again";
					public static final  String Cancel = "Cancel";//This is also used in Settings
					
	///////////////////
	////Popups
	////////////////////			
	@SuppressWarnings("unused")
	private int _____________Popups_________________;	
		
		//Course chooser
		public static final String showCourseFiltersText = "Show Filters";
		public static final String hideCourseFiltersText = "Hide Filters";
	
	/////////////////
	//Driver 
	/////////////////
	@SuppressWarnings("unused")
	private int _____________Driver_________________;
		public static boolean nimbusLoaded; 

		public static String confirm = "Yes, I understand";

	
		
		public static boolean isNimbusLoaded() {
			return nimbusLoaded;
		}
		public static void setNimbusLoaded(boolean isLoaded) {
			nimbusLoaded = isLoaded;
		}
	
	}




