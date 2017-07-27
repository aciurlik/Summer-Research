# Summer-Research

Quick start for a coder:
    Check out the code, there should be reasonably good comments. If you want to turn the java code into a .jar file, we used
    eclipse Luna and did File --> Export --> Java --> RunnableJAR file, with Driver as the main class. If you have another way to 
    turn code into a .jar, you can use that.

I. Introduction
  a. Purpose 
     This program is intended to be a useful tool for advising, so that Furman advisors can spend less time scheduling courses
     and more time planning life pathways.
  b. Goals
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
    Should be able to specify a placeholder in a schedule using different levels of generality, 
        as in "any GER", "any TA," "HST 141," or "HST 141-02"
II. Background
    The objects in this program are divided into three major groups - the Data group, the File group, and the GUI group.
    Data objects handle the actual schedule - the main class in this group is the Schedule class. This group includes classes
        like Requirement, ScheduleElement, and Schedule. Only Schedule should be allowed to request user events from
        the GUI group, other classes should simply inform Schedule that user intervention may be required.
    File groups handle collection of raw data from furman. Currently they use files to do so. File classes should be 
         the ONLY classes that use files in any way - all other classes can only use strings provided by file classes.
         The main class in this group is the FileHandler class, but it includes other classes like CourseList and
         ListOfMajors.
    GUI objects handle interactions with the user, and the drawing of the GUI. The ScheduleGUI class is the main class in this 
        group, but a close second is Driver (the class that contains the main method for the program). Most classes in this group
        are directly linked to some class in thg DATA group, and rely on getters from the data classes. 
        WARNING - WARNING -  NO MEMBER OF THIS GROUP may directly modify any member of the DATA group - WARNING - WARNING
        all modifications to the schedule should be passed to the SchdeuleGUI, which
        may then inform schedule that a user has requested a change.
III. Data Group Object Descriptions
  a. Schedule
    A list of available semesters, each of which contains ScheduleElements
    Also holds the list of majors that the user has chosen, and handles GER requirements.
    This class is the go-between for the Data side and the GUI side

  b. ScheduleElement
    Anything that can be added to a semester's plans.
    Includes Course, Prefix, and Requirement.
    
  c. Major
    A fixed collection of requirements.
    Tracks, GERs, Minors, and the collection of unsatisfied Prereqs are all represented with Major objects.
      
  d. Requirement
    Represents a collection of needed courses by the course's prefixes (i.e., "MTH-120")
    Every requirement has a number to choose, and a list of choices. The choices may themselves be requirements.
    There is a subclass TerminalRequirement that represents a requirement with only 1 choice.
    Requirements have their own specification language that includes strings like "MTH-150", "3 of (MTH-110, MTH-120, MTH-130),"
    and "1 of (2 of (MTH-145, MTH-120), MTH-150)." For a more detailed explanation, see the saving and reading tutorials in
    the Requirement class and the TerminalRequirement class.
    Requirements can determine their completion status given a list of ScheduleElements.
    
    

      
 
  
IV. GUI group object descriptions
  a. ScheduleGUI 
      Communicates with the Data group via a Schedule object.
      Represents one open window.
      Handles updating all subcomponents based on the Schedule's data, by using the update() method.
  b. SchedulePanel
      Represents the top part of the GUI, where semesters are displayed.
  c. MajorListPanel
      Represents the bottom right part of the GUI, where majors are listed out.
  d. AdditionsPanel / BellTower
      The two panels to the left of the MajorListPanel, one which is a picture of the belltower and fills as you 
      complete your requirements, the other which includes lots of high-impact buttons for freshman
  e. MainMenuBar
      The MenuBar at the top of the program.
    
V. File group object descriptions
  a. FileHandler
      Handles all files and collecting data, including images, course catalogs, saved schedules, and major files.
      All data is currently stored in the two folder "Resources" and "UserData."
  b. CourseList
      The list of available courses. May be replaced with a database system later. Includes methods to filter the course list
      (getCoursesSatisfying(Predicate<Course>) is the most general)
  c. ListOfMajors
      The list of all available majors.
  
VI. Optimization

