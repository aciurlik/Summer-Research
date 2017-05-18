package scheduler;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
/**
 * 
 * @author drivers, aciurlik
 * This will read in a file and call the course constructor to create a course object for one listed in the file
 * these courses will then be placed in an array list. Modeled after Maze.java
 *
 */

public class CourseList {
	private static final String String = null;
	private static ArrayList<Course> CourseList = new ArrayList<Course>();
	
	public CourseList (String fname) throws FileNotFoundException{
		 Scanner sc = new Scanner(new File(fname));	
		 //This would read the file and scrape the 
		// for(Every line representing a course){
		 //  CourseList.add(new Course(The data points read from the file would be funneled into the parameters));
		 sc.close();
}

public static boolean add(Course c){
	
	return CourseList.add(c);
}

public static void addAt(Course c, int i){
	CourseList.add(i, c);
}

public static Course removeCourse(Course c){
	 CourseList.remove(c);
	 return c;
}

public static Course removeAtIndex(int i){
	Course c = CourseList.remove(i);
	return c;
	
}
public  ArrayList<Course> getSemester(int s){//Would this be an integer with two parts, a semester object, a time object?
    ArrayList<Course> SemesterList = new ArrayList<Course>();
	for(Course course : CourseList){
		if ( course.getSemester()==s){//If this was a time object change == to .equals s 
			SemesterList.add(course);
		}
	
}
	return SemesterList;
}
public  ArrayList<Course> getGER(Requirement r){//Would this be an integer with two parts, a semester object, a time object?
    ArrayList<Course> GERList = new ArrayList<Course>();
	for(Course course : CourseList){
		if (r.getRequirement(course)){//If this was a time object change == to .equals s 
			GERList.add(course);
		}
	
}
	return GERList;
}

}
