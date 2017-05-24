
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

	private ArrayList<Course> listOfCourses = new ArrayList<Course>();
	
	
	
	public static CourseList testList(){
		CourseList result = new CourseList();
		Course[] list = new Course[]{
				Course.readFrom("MTH-220-02;Fray;[1, 3, 5];10:30:A,50;20/6/2017 11:30:0;4;2017-2"),
				Course.readFrom("MTH-330-02;Fray;[1, 3, 5];10:30:A,50;20/6/2018 11:30:0;4;2018-2"),
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
		return result;
	}

	public CourseList (){
		this.listOfCourses = new ArrayList<Course>();
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
		System.out.println(input);
		ArrayList<Course> SemesterList = new ArrayList<Course>();
		for(Course course : input){
			if ( course.getSemester().compareTo(s.getDate()) == 0){
				SemesterList.add(course);
			}
		}
		return SemesterList;
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
	
	public HashSet<Prefix> getPrerequsites(Prefix p){
		//TODO
		return new HashSet<Prefix>();
	}

}
