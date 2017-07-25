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
 * The two main methods are readFromWebsiteDraggedData and readFromCSV. 
 * General use is:
 * 	PriorData p = new PriorData();
 *  p.readFromWebsiteDraggedData(a string with data dragged in from furman's website.);
 *  schedule.readPrior(p);
 * 
 * 
 * It is a FILE side class, as opposed to DATA or GUI. 
 *
 */
public class PriorData implements Serializable{

	private static final long serialVersionUID = 1L;
	ArrayList<Course> courseList;
	SemesterDate earliestDate;
	SemesterDate latestDate;
	Prefix languagePrefix;
	String[][] data;
	String studentName;



	public PriorData(){
		earliestDate =  new SemesterDate(100000,1);
		latestDate = new SemesterDate(0, 1);
		courseList = new ArrayList<Course>();
		languagePrefix = null;

	}

	
	
	/**
	 * Read from the text given by a drag from furman's website
	 * 
	 * Throws lots of possible runtime exceptions
	 * @param text
	 * @return
	 */
	public void readFromWebsiteDraggedData(String text){
		//String name = 
		//First, find the index of the ID/Name.
		Matcher idMatch = Pattern.compile("\\d{5,20}").matcher(text); //at least 5 digits from the ID 
		//(this prevents matching on years or other digits in the text, and gives an unambiguous
		// start to the string.
		if(!idMatch.find()){
			throw new RuntimeException( "We couldn't find your ID number in the text.");
		}
		
		//put startIndex in a location where the next instance of the string 'course'
		// can only be a column header.
		int startIndex = idMatch.start();
		String name = (text.substring(idMatch.end()+1, text.indexOf("\n", startIndex)));
		studentName = name.trim();
		int advisorSkipLineIndex = text.indexOf("Download course", startIndex);
		if(advisorSkipLineIndex != -1){
			startIndex = text.indexOf("\n", advisorSkipLineIndex) + 1;
		}
		text = text.substring(startIndex);
		
		
		
		
		//Find the first column header, either "Course/Section and Title" or "course" depending on advisor view or student view.
		Matcher headersStartMatcher = Pattern.compile("Course/Section and Title|course").matcher(text);
		if(!headersStartMatcher.find()){
			throw new RuntimeException(  "We couldn't find the table headers in the text"
					+ "\n We're looking for a table where the first column is either"
					+ "\n 'Course/Section and Title' or 'course.' If you don't see that, "
					+ "\n you may be on the wrong page. Make sure you're at your unofficial transcript!");
		}
		
		
		int headersStart = headersStartMatcher.start();
		//Find last column header, which is always 'global awareness'.
		int headersEnd = text.indexOf("global awareness", headersStart);
		if(headersEnd == -1){
			throw new RuntimeException( "We couldn't find the last column 'global awareness' in the text "
					+ "\n   if the last column doesn't say 'global awareness', "
					+ "\n   then you may be on the wrong page. Make sure you're at your unofficial transcript!");
		}
		headersEnd += 16; // go to the end of the column headers, but don't include the newline 
		// (we don't want to think there are more headers than there actually are in the next step.)
		
		
		//Collect the headers.
		String[] headers = text.substring(headersStart, headersEnd).split("\t");
		if(headers.length < 2){
			headers = text.substring(headersStart, headersEnd).split("\n");
		}
		for(int i = 0; i  < headers.length ; i ++){
			headers[i] = headers[i].trim();
		}

		int numCols = headers.length;

		
		//Find the start and end of the data section
		int dataStart = headersEnd + 1; //add the newline
		int dataEnd = text.indexOf("Total Earned Credits");
		if(dataEnd == -1){
			throw new RuntimeException( "We couldn't find your GPA in the text"
					+ "\n Be sure to highlight all the data including your GPA! ");
		}


		
		

		String[] data = text.substring(dataStart, dataEnd).split("\n");
		int numRows = data.length / numCols;
		String[][] csvResult = new String[numRows + 1][];
		csvResult[0] = headers;
		
		for(int i = 0; i < numRows ; i ++){
			int rowStartIndex = i * numCols;
			String[] row = new String[numCols];
			for(int j = 0; j < numCols ; j ++){
				row[j] = data[rowStartIndex + j];
			}
			csvResult[i+1] = row;
		}
		readFromCSV(csvResult);
	}


	/**
	 * Try to read from this prior course data.
	 *  If you just find a course or two that you
	 * can't read, just show a popup informing the user that you're skipping
	 * that course. Throws fatal errors though.
	 * 
	 * @param s
	 * @return
	 */
	public void readFromCSV(String[][] s){

		String[] headers = s[0];
		int termIndex = -1;
		int courseStringIndex = -1;
		int creditsIndex = -1;
		int gradeIndex = -1;
		for(int i = 0; i < headers.length ; i ++){
			if(headers[i].toUpperCase().equals("TERM")){
				termIndex = i;
			}
			else if(headers[i].toUpperCase().contains("COURSE")){
				courseStringIndex = i;
			}
			else if(headers[i].toUpperCase().contains("CREDITS")){
				creditsIndex = i;
			}
			else if (headers[i].toUpperCase().equals("GRADE") || 
					headers[i].toUpperCase().equals("FINAL GRADE")){
				gradeIndex = i;
			}
		}

		if(termIndex == -1){
			throw new RuntimeException( "Course-term column not found in " + Arrays.toString(headers));
		}
		if(courseStringIndex == -1){
			throw new RuntimeException( "subject and section column not found in " + Arrays.toString(headers));
		}
		if(creditsIndex == -1){
			throw new RuntimeException( "Course-credits column not found in " + Arrays.toString(headers));
		}
		if(gradeIndex == -1){
			throw new RuntimeException( "Course grade column not found in " + Arrays.toString(headers));
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
			//System.out.println(Arrays.toString(row));
			try{
				//Collect relevant string data
				String courseString = row[courseStringIndex].trim();
				String creditsString = row[creditsIndex].trim();
				String termString = row[termIndex].trim();
				String gradeString = row[gradeIndex].trim();
				//remove spaces from these fields
				creditsString = creditsString.replaceAll("\\s+", "");
				gradeString = gradeString.replaceAll("\\s+", "");

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
				

				
				//Semester / term
				SemesterDate takenDate = readSemesterDate(termString);
				//takenDate may be null
				if(takenDate != null){
					if(takenDate.sNumber == SemesterDate.OTHER || takenDate.year < 1500 || takenDate.year > 2500){
						throw new RuntimeException("Could not make the semester from string \""+ termString + "\"");
					}
				}

				//credits
				int credits= CourseList.getCoursesCreditHours(p);
				//If credits data exists, contains a number, and isn't a recently planned course
				// (because planned courses 
				if( creditsString!=null && creditsString.matches(".*\\d.*") ){
					//System.out.println((int)(Double.parseDouble(creditsString)));
					credits = (int)(Double.parseDouble(creditsString));
					if(latestDate.equals(takenDate) && credits == 0){
						credits = CourseList.getCoursesCreditHours(p);
					}
				}
				


				Course c = null;
				if(takenDate != null){
					c = new Course(p, takenDate, null, null, credits, section);
				}
				else{
					c = new Course(p, earliestDate.previous(), null, null, credits, section);
				}
				c.setName(title);
				
				//skip failing grades (this will still let a course be added if it has no grade)
				// X was used to redact grades at one point.
				if(isPassing(gradeString)){
					courseList.add(c);
				}
				else{
					System.out.println("Not adding " + Arrays.toString(row));
				}
			}
			catch(Exception e){
				System.out.println("Started catch" + Arrays.toString(row));
				System.out.println(Arrays.toString(row));
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
		data = s;
	}
	
	private SemesterDate readSemesterDate(String s){
		SemesterDate result = null;
		try{
			result = SemesterDate.readFromFurman(s);
		} catch(Exception e){
			//if this read fails, throw the exception
			result = SemesterDate.fromFurman(s);
		}
		return result;
	}

	public void setLanguagePrefix(Prefix p){
		CourseList.FLRequirement(p, Major.BM);
		CourseList.FLRequirement(p, Major.BA);
		CourseList.FLRequirement(p, Major.BS);
		languagePrefix = p;
	}

	public SemesterDate getEarliestDate(){
		checkIsCorrupted();
		return earliestDate;
	}
	public SemesterDate getLatestDate(){
		checkIsCorrupted();
		return latestDate;
	}

	public Prefix getLanguagePrefix(){
		checkIsCorrupted();
		return languagePrefix;
	}

	public String getStudentName() {
		checkIsCorrupted();
		return studentName;
	}

	public ArrayList<Course> getAllCourses(){
		return courseList;
	}
	
	public String dataToString(){
		StringBuilder result = new StringBuilder();
		for(String[] s : data){
			for(String t : s){
				result.append(t + "\t");
			}
			result.append("\n");
		}
		return result.toString();
	}
	
	public boolean isCorrupted(){
		return data==null;
	}
	
	public void checkIsCorrupted(){
		if(isCorrupted()){
			throw new RuntimeException("The data you were trying to read from never loaded correctly");
		}
	}
	
	
	/**
	 * Check if this is a passing grade and the course 
	 * can be used in the schedule. If a student fails a course,
	 * for planning purposes it's like they never took it in the first place.
	 * 
	 * Because taken courses can't be removed, we want to err on the side of
	 * not adding passing courses rather than adding a failing course, so
	 * this method checks if the grade is known to be a passing grade 
	 * (not known to be a failing grade) and marks unknowns as failing grades.
	 * @param grade
	 * @return
	 */
	public boolean isPassing(String grade){
		if(grade == null || grade.equals("")){
			return true;
		}
		for(char c : grade.toCharArray()){
			if("ABCDSP X".contains(c + "")){
				return true;
			}
		}
		return false;
	}
	
	
	
	
	
	public void testReading(){
		
	}





}
