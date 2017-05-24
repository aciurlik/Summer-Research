

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
/**
 * 
 * @author drivers, aciurlik
 * This will read in a file and call the course constructor to create a course object for one listed in the file
 * these courses will then be placed in an array list. Modeled after Maze.java
 *
 */

public class CourseList {

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
public  ArrayList<Course> getCoursesIn(Semester s){//I think we should go with a semester object, it seems simpler to me than using ints.
	ArrayList<Course> SemesterList = new ArrayList<Course>();
	for(Course course : CourseList){
		if ( course.getSemester().equals(s)){
			SemesterList.add(course);
		}

	}
	return SemesterList;
}
public  ArrayList<Course> getCoursesSatisfying(Requirement r){
	ArrayList<Course> GERList = new ArrayList<Course>();
	for(Course course : CourseList){
		if (r.isSatisfiedBy(course)){//If this was a time object change == to .equals s 
			GERList.add(course);
		}

	}
	return GERList;
}

public HashSet<Prefix> getPrerequsites(Prefix p){
	//TODO
	return new HashSet<Prefix>();
}
}