# Summer-Research
I. Introduction
  a. Purpose (Add line number when finalized)
  b. Requirements
    Usable by faculty and students 
      including the old professors who are scared of computers
      Easy and quick - make a schedule from scratch in 5 min or less
      Not clunky
    Easy to mofidy a schedule
    Manual override for special circumstances
      Handle classes taken through abnormal means 
        both past (I don't need to satisfy that requirement) 
          and future (I will be able to take that even though you think I won't)
        what if a class taken abnormally was a prerequsite for another class?
      Variable strength of a warning, harder to override some things
    Present as many options as possible
      what is MayX, make your own major, ...
    Easy to integrate with MyFurman
    Display and save different possible scenarios
    Compare schedules
      hi-light changes
      to pdf for printing
    Allow the user to focus on future semesters
    Display remaining course requirements easily
    Easily find classes satisfying a requirement
    Should be able to specify a spot in a schedule as "any GER", "any TA," "HST 141," or "HST 141-02"
      different levels of generality.
II. Background
III. Main Object Descriptions
  a. Course (extends Prefix)
    An offered course that can be taken.
    Any of the fields other than prefix, sectionNumber, and semester may be unspecified (null).
    Main Constructors:
      Course(String line) - read the course information from one line of a file
      Course(Prefix prefix, Semester semester, String professor, int[] meetingDays, 
          Time labTime, Time examTime, int creditHours)
    Fields:
      Prefix prefix - the subject and number
        //To find prerequsites, use CourseList.getPrereqs(prefix)
      int sectionNumber
      int creditHours
      Semester semester
      
      int[] meetingDays - specified using the constants in the Time class, SUNDAY = 0
      Time labTime
      Time examTime
      String professor - the professor(s) teaching the course
      Other fields - (What else is there again?)
    Methods:
      overlaps(Course other) - returns true if this course and the other course are offered at the same time.
      displayString() - return the string that would be displayed to represent this course in a dropdown bar in the SchedulePanel
   i. Any superclasses or subclasses i.e. prefix 
     Prefix
       a class for keeping track of subject, number, and prerequsites of a course.
       Fields:
       String subject - for example, "MTH"
       int number - for example, "220"
       Prefix[] prerequsites
  b. CourseList
    Keeps track of all offered courses.
    Also keeps track of course prefixes and their prerequsites - ensures that you don't have two prefixes with the same number
      and subject but different prerequsites.
    Main Constructor: Reads in a file of all courses known to be offered (describe what you're doing rather than how.)
    Fields:
      courseList
      prefixPrerequsites - stores any prerequsites for a prefix
    Methods:
      add(Course c) - adds course to the end of the CourseList
      addAt(Course c, int i)-adds course to a specified location in CourseList
      removeCourse(Course c)-removes a speficifed course, returns course removed
      removeAtIndex(removes the course and specified index, returns that course)
      getSemester(int s)-goes through courseList and copies courses that are in given semster
      getGER(String[]) goes through courseList and copies courses that forfill that given GER
      setPrerequsites(Prefix p, Prefix[] prereqs) - update prefixPrerequeistes so future calls to getPrerqs will be accurate.
      getPrereqs(Prefix p) - find the prerequsites for this prefix
  
  c. Major
    A fixed collection of requirements
    Main Constructors:
      Major(String lines) - read a saved major in from the lines of a file
        this may be replaced by Major(String fileName)
    Fields:
      String Name - the name of the major, for example "Applied Mathematics"
      ArrayList<Requirement> requirements
    Methods:
      getRequirements()
      
  d. Requirement
    A requirement for courses necessary to a major, minor, GER, or other.
    Fields:
      int numberNeeded - if this is a "pick 2" or "pick 3 of" requirement, numberNeeded specifies how many. Otherwise, numberNeeded is 1.
      ArrayList<Prefix> satisfiers - the prefixes of the courses that can satisfy this requirement
      boolean[] taken - keeps track of which of the satisfiers have actually been taken
    Methods:
      isSatisfiedBy(Prefix) - check if satisfiers contains this prefix
      clearTaken() - set all the values in the taken array to false
      took(Prefix) - set the correct value in the taken array. If Prefix is not in satisfiers, do nothing.

      
  e. Schedule
    A list of available semesters with courses, prefixes, or requirements in those semesters. 
    Methods:
      CheckSatisfied(ArrayList<Requirement> reqs) - figure out which of these requirements are satisfied by this schedule
        be careful about the order in which you do this - if you took MTH360, and you have a requirement for
        360 or 460, we don't want to claim that 360 is satisfying one of your electives requirements, we want it to satisfy
        the 360/460 requirement.
      CheckOverlap() - check if any of the courses in the schedule overlap
      CheckDuplicates() - check if any of the courses are duplicates
      add(Course course, Semester semester)
      add(Prefix prefix, Semester semester) - this should also check if any courses fitting that prefix are offered that semester
      add(Requirement r, Semester semester) - this should also check if any courses fitting that requirement are offered tha semester
      
  
IV. GUI Description
  The main window of the GUI is used to create a schedule based on requirements
  There is an area for viewing the current schedule, an area for viewing requirements left to satisfy, and an area for 
    additional functions (adding a major/minor, declaring a semester for study away, so on)
  
V. Optimization
