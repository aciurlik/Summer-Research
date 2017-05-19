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
        both past and future
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
    Should be able to specify a spot as "any GER", "any TA," "HST 141," or "HST 141-02".
II. Backround
III. Main Object Descriptions
  a. Course
    A course that can be taken.
    Main Constructors:
      Course(String) - read the course information from one line of a file
      Course(prefix, courseTime, professor, 
    Fields:
      
      The following fields may be replaced by Prefix:
      subject - i.e., MTH
      number - i.e., the 220 in MTH 220.
      prefix - the subject and number
      
      sectionNumber
      
      The following fields may be replaced by courseTime in the future:
      semester
      year
      meetingDays
      labs
      examTime
      courseTime - specifies all meeting times (including weekly meetings, labs, exams, ...) and used to check for overlap.
      
      professor - the professor(s) teaching the course
      prerequsites - the courses that are prerequsites to this course
      Other fields - (What else is there again?)
    Methods:
      overlaps(Course other) - returns true if this course and the other course are offered at the same time.
      
   i. Any subclasses i.e. prefix 
     CourseTime
       a class for keeping track of which times are reserved by a course. This class tests to see
       if two courses overlap.
     Prefix
       a class for keeping track of subject and number of a course.
  b. CourseList
    Main Constructor: Reads in a file and creates courses and adds them to an ArrayList
    Methods:
      add(Course c) - adds course to the end of the CourseList
      addAt(Course c, int i)-adds course to a specified location in CourseList
      removeCourse(Course c)-removes a speficifed course, returns course removed
      removeAtIndex(removes the course and specified index, returns that course)
      getSemester(int s)-goes through courseList and copies courses that are in given semster
      getGER(String[]) goes through courseList and copies courses that forfill that given GER
  
  c. Major
  d. Requirement
  e. Schedule
  
IV. GUI Description
V. Optimization
