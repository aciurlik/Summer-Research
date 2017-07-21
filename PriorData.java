import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;


/**
 * 
 * This class holds all the prior data about a student that we might get from myFurman.
 * 
 * It is a file side class.
 *
 */
public class PriorData implements Serializable{

	private static final long serialVersionUID = 1L;
	ArrayList<Course> courseList;
	SemesterDate earliestDate;
	SemesterDate latestDate;
	Prefix languagePrefix;
	String[][] data;



	public PriorData(){
		earliestDate =  new SemesterDate(100000,1);
		latestDate = new SemesterDate(0, 1);
		courseList = new ArrayList<Course>();
		languagePrefix = null;

	}


	/**
	 * Read in the info from Furman's text string, copied from myFurman.
	 * Sets language prefix, first semester and creates prior semester,
	 * 
	 * 
	 * s should be in the form 
	 * 
	 * colmName \t colmName \t colmName \t ...
	 * r1c1
	 * r1c2
	 * r1c3
	 * .
	 * .
	 * .
	 * r2c1
	 * r2c2
	 * .
	 * .
	 * .
	 * rncn
	 * 
	 * @param s
	 */
	public void readFromWebsiteDraggedData(String s){

		String[] lines = s.split("\n");
		String[] headers = lines[0].split("\t");

		ArrayList<ArrayList<String>> resultList = new ArrayList<ArrayList<String>>();

		int numCols = headers.length;
		int row = 0; //the first data row is index 0.
		for(;(row+1) * numCols - 1 < lines.length ; row ++){
			ArrayList<String> rowList = new ArrayList<String>();
			int startIndex = row * numCols + 1;
			for(int i = 0 ; i < numCols ; i ++){
				rowList.add(lines[startIndex + i]);
			}
			resultList.add(rowList);
		}

		String[][] result = new String[resultList.size() + 1][];
		result[0] = headers;
		for(int i = 1 ; i < resultList.size() + 1 ; i ++){
			ArrayList<String> rowList = resultList.get(i-1);
			result[i] = rowList.toArray(new String[rowList.size()]);
		}

		readFromCSV(result);
	}


	public void readFromCSV(String[][] s){
		data = s;


		String[] headers = s[0];
		int termIndex = -1;
		int courseStringIndex = -1;
		int creditsIndex = -1;
		int gradeIndex = -1;
		for(int i = 0; i < headers.length ; i ++){
			if(headers[i].equals("term")){
				termIndex = i;
			}
			else if(headers[i].toUpperCase().contains("COURSE")){
				courseStringIndex = i;
			}
			else if(headers[i].toUpperCase().contains("CREDITS")){
				creditsIndex = i;
			}
			else if (headers[i].toUpperCase().equals("GRADE")){
				gradeIndex = i;
			}
		}

		if(termIndex == -1){
			throw new RuntimeException("Course-term column not found" + Arrays.toString(headers));
		}
		if(courseStringIndex == -1){
			throw new RuntimeException("subject and section column not found" + Arrays.toString(headers));
		}
		if(creditsIndex == -1){
			throw new RuntimeException("Course-credits column not found" + Arrays.toString(headers));
		}
		if(gradeIndex == -1){
			throw new RuntimeException("Course grade column not found" + Arrays.toString(headers));
		}	





		//First, go through the data and 
		// find freshman year (the earliest found date),
		// and current semester (the latest found date).
		//so we can correctly set prior course's start dates.
		for(int i = 1 ; i < s.length ; i ++){
			String[] row = s[i];
			SemesterDate takenDate = readSemesterDate(row[termIndex]);
			if(takenDate != null){
				if(takenDate.compareTo(earliestDate) < 0){
					earliestDate = takenDate;
				}
				else if(takenDate.compareTo(latestDate) > 0){
					latestDate = takenDate;
				}
			}
		}
		

		//Add each of the prior courses to the schedule
		// skip the course if an error occurs.
		courseList = new ArrayList<Course>();
		for(int i = 1 ; i < s.length ; i ++){
			String[] row = s[i];
			try{
				//Collect relevant string data
				String courseString = row[courseStringIndex].trim();
				String creditsString = row[creditsIndex].trim();
				String termString = row[termIndex].trim();
				String gradeString = row[gradeIndex].trim();

				//Turn the strings into objects

				//Prefix, title, and section number
				String title = null;
				String section = null;
				int firstSpace = courseString.indexOf(" ");
				String prefixString = courseString.substring(0, firstSpace);
				Prefix p = Prefix.readFrom(prefixString);
				String numString = p.getNumber();
				boolean examineTitleForLanguagePrefix = false;
				if(numString.contains("PL")){
					if(numString.compareTo("PL.110") > 0){
						String number = numString.substring(numString.indexOf(".") + 1);
						try{
							setLanguagePrefix(new Prefix(p.getSubject(), number)); 
						}
						catch(Exception e){
							examineTitleForLanguagePrefix = true;
						}
					}
				}
				int secondSpace = courseString.indexOf(" ", firstSpace + 1);
				if(secondSpace == firstSpace + 2){
					title = courseString.substring(secondSpace);
					section = courseString.substring(firstSpace + 1, secondSpace);
				}
				else{
					title = courseString.substring(firstSpace);
				}

				if(examineTitleForLanguagePrefix){
					//Take the first instance of a number found in the title.
					Matcher m = Pattern.compile("\\d+").matcher(title);
					boolean found = m.find();
					if(found){
						this.setLanguagePrefix(new Prefix(p.getSubject(), m.group()));
					}
				}

				//credits
				int credits= CourseList.getCoursesCreditHours(p);
				//if(!" ".equals(creditsString)&&! "".equals(creditsString)&& !(creditsString==null)){
				//	System.out.println((int)(Double.parseDouble(creditsString)));
				//	credits = (int)(Double.parseDouble(creditsString));
				//}
				
				
				
				//Semester / term
				SemesterDate takenDate = readSemesterDate(row[termIndex]);


				Course c = null;
				if(takenDate != null){
					c = new Course(p, takenDate, null, null, credits, section);
				}
				else{
					c = new Course(p, earliestDate.previous(), null, null, credits, section);
				}
				c.setName(title);
				courseList.add(c);
			}
			catch(Exception e){
				if(FurmanOfficial.masterIsAround){
					throw e;
				}
				String courseString = Arrays.toString(row);
				JOptionPane.showMessageDialog(null, 
						    "We couldn't read in the course \n" 
						+    courseString 
						+ "\n due to something unexpected in the data. "
						+ "\n If this course satisfies any requirements, you can drag"
						+ "\n those requirements into the semester where you took this course.");

			}
		}

	}
	
	private SemesterDate readSemesterDate(String s){
		SemesterDate result = null;
		try{
			result = SemesterDate.readFromFurman(s);
		} catch(Exception e){
			result = SemesterDate.fromFurman(s);
		}
		return result;
	}

	public void setLanguagePrefix(Prefix p){
		CourseList.FLRequirement(p, CourseList.BM);
		CourseList.FLRequirement(p, CourseList.BA);
		CourseList.FLRequirement(p, CourseList.BS);
		languagePrefix = p;
	}

	public SemesterDate getEarliestDate(){
		if(earliestDate == null){
			throw new RuntimeException("Tried to get null earliest date");
		}
		return earliestDate;
	}
	public SemesterDate getLatestDate(){
		if(latestDate == null){
			throw new RuntimeException("Tried to get null latest date");
		}
		return latestDate;
	}

	public Prefix getLanguagePrefix(){
		if(languagePrefix == null){
			throw new RuntimeException("Tried to get null language prefix");
		}
		return languagePrefix;
	}

	public ArrayList<Course> getAllCourses(){
		return courseList;
	}


}
