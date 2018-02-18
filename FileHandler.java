

import java.awt.Image;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;


/**
 * 
 * Blurb Written:7/27/2017
 * Last updated: 8/1/2017
 * 
 * 
 * 
 * 
 * This class handles all file operations, including locating files and 
 * reading from files. This way if we change how our data is stored at any point,
 * as long as the underlying data is not changed, 
 * one need only change this class in order to read all the new data correctly.
 * 
 * For example, if we put data on a webserver, this class could contain
 * methods to check the server for new data and perform updates.
 * 
 * This class is in the FILE group of classes.
 * 
 * FileHandler should be the ONLY CLASS that imports or uses File. 
 * If another class needs to get data of some sort, there should be a method
 * in this class to find and collect the necessary data. FileHandler does not 
 * need to process the data in any way.
 * 
 * FileHandler also stores the settings for the rest of the program in its
 * Properties field.
 * 
 * 
 * Note: File names are in FileHandler, Button Names are in MenuOptions, and FurmanOffical holds
 * color, fonts, formatting information. 
 *
 */
public class FileHandler{
	public static final String resourcesFolder = "Resources" + File.separator;
	public static final String userDataFolder = "UserData" + File.separator;
	public static String startSemester = "Set First Semester";
	public static final String bellTowerImageFile = resourcesFolder + "bellTower.jpg";
	public static final String fireworksImageFile = resourcesFolder + "fireworks.jpg";
	public static final String prereqMeaningsFile = resourcesFolder + "PrereqMeanings.txt";
	public static final String courseListFolder = resourcesFolder + "CourseCatologs";
	public static final String studentDataFile = userDataFolder + "SavedStudentData.txt";
	public static final String savedScheduleFolder = userDataFolder + "SavedSchedule" + File.separator;
	public static final String startUpFolder = resourcesFolder + "StartUpSlides";
	public static final String settingsDoc = userDataFolder +  "Settings";
	public static final String testScheduleSource = resourcesFolder +  "tutorial.ser";
	public static final String testScheduleDestination = savedScheduleFolder + "tutorial.ser";


	private static Properties properties;
	static ListOfMajors listOfMajors = null;


	/**
	 * This static block tries to load the existing settings
	 * from the settingsDoc file.
	 * 
	 * If the file is not found, it makes a new settings
	 * and saves a new file.
	 */
	static{
		properties = new Properties();
		File file = new File(settingsDoc);
		makeFolder(userDataFolder);
		makeFolder(savedScheduleFolder);
		if(!file.exists()){
			//if the file isn't found, make one with
			// the default setting
			restoreDefaultSettings();
			saveProperties();
		}
		else{
			// if the file is found, load the settings.
			try(FileInputStream input = new FileInputStream(settingsDoc);) {	
				properties.load(input);
				input.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * set this setting to the specified value.
	 * @param setting
	 * @param value
	 */
	public static void propertySet(String setting, String value){
		properties.setProperty(setting, value);
		saveProperties();
		
	}
	/**
	 * This asks the user if they are sure they want to 
	 * restore Default Settings, as of now that
	 * just means the startUp Menu will load at the opening
	 * of the program. 
	 */
	public static void requestRestoreDefaultSettings(){
		int n = JOptionPane.showConfirmDialog(null, "Are you sure you would like to restore default settings?", "Restore Default Settings", JOptionPane.OK_CANCEL_OPTION);
		if(n==0){
			FileHandler.restoreDefaultSettings();
		}
	}
	
	/**
	 * This resets all of the properties, in settings. 
	 */
	private static void restoreDefaultSettings() {
		properties.setProperty(MenuOptions.startUp, "true");
		//There is no "default to Semesters
		saveProperties();
	}

	/**
	 * This updates and stores the changes made to the settings file. 
	 */
	private static void saveProperties(){
		try(FileOutputStream output = new FileOutputStream(settingsDoc);) {
			properties.store(output, "Edited Settings");
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This returns the PriorData given from student's unofficial Transcript.
	 * @return
	 */
	public static PriorData getSavedStudentData(){
		File f = new File(studentDataFile);
		if(!f.exists()){
			return null;
		}
		try{
			return (PriorData)FileHandler.readObjectFromFile(studentDataFile);
		}
		catch(Exception e){
			JOptionPane.showMessageDialog(null, 
				    "<html><p>The data you were trying to read from never loaded correctly. <br>" 
				+ "<br> If this is your first time reading in the data, please try again. <br>"
				+ "If you were trying to open a previously loaded schedule, <br>"
				+ "please delete the 'SavedStudentData.txt file in your UserData folder, <br> and reload that schedule from scratch.</p></html>");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * This saves student's data as a file. 
	 * @param d
	 */
	public static void writeStudentData(PriorData d){
		try{
			FileHandler.saveObjectToFile(studentDataFile, d);
		}catch(IOException e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Was not able to save the loaded data");
		}
	}

	/**
	 * Return the loaded data, or else null.
	 * No error checks
	 * @return
	 */
	public static String[][] importCSVStudentData(){
		JOptionPane.showMessageDialog(null, "To import a student's prior courses and placements, "
					+ "\n  1) Go to MyFurman. "
					+ "\n  2) Navigate to the student's course credit summary. "
					+ "\n  3) Click 'Download course credit summary to Excel via Email'"
					+ "\n  4) Go to your email, download, and save that file."
					+ "\n  5) The next window will allow you to navigate through the files on "
					+ "\n your computer and choose the one you just saved."
					+ "\n  6)Click Open after you've selected the desired file.");
		JFileChooser fc = new JFileChooser(
				Paths.get(".").toAbsolutePath().normalize().toString()){
			private static final long serialVersionUID = 1L;
			@Override
			public boolean accept(File f){
				return f.isDirectory() || (f.isFile() && ".csv".equals(getExtension(f)));
			}
		};
		int userChoice = fc.showOpenDialog(null);
		javax.swing.filechooser.FileFilter f = new javax.swing.filechooser.FileFilter(){
			@Override
			public boolean accept(File f){
				return f.isFile() && ".csv".equals(getExtension(f));
			}
			@Override
			public String getDescription() {
				return "csv files";
			}
		};
		fc.addChoosableFileFilter(f);
		fc.setFileFilter(f);
		if(userChoice != JFileChooser.APPROVE_OPTION){
			return null;
		}
		File file = fc.getSelectedFile();
		ArrayList<String> fullString = FileHandler.fileToStrings(file);
		String[][] result = new String[fullString.size()][];
		for(int i = 0; i < fullString.size() ; i ++){
			ArrayList<String> splitLine = parseAdvisorImportCSVLine(fullString.get(i));
			result[i] = splitLine.toArray(new String[splitLine.size()]);
		}
		return result;

	}

	/**
	 * Returns the extension of a file. Example 
	 * ".ser" 
	 * @param f
	 * @return
	 */
	public static String getExtension(File f){
		if(!f.isFile()){
			return null;
		}
		String name = f.getName();
		int i = name.lastIndexOf('.');
		if(i == -1){
			throw new RuntimeException("File " + name + " has no extension");
		}
		return name.substring(i);
	}



	/**
	 * Returns a property of a specific setting.
	 * @param setting
	 * @return String associated with this setting.
	 */
	public static String propertyGet(String setting){
		String value = properties.getProperty(setting);
		return value;
	}


/**
 * This is shown to the user every time they need to pick a schedule out of a list. 
 * @param s, tells what action prompted this method
 * @return File name of schedule chosen. 
 */
	public static String chooseSchedule(String s){
		String header = "";
		String instruct = "";
		if(s.equals("delete")){
			instruct = "Choose a schedule to delete";
			header = "Delete Schedule";

		}
		if(s.equals("open")){
			instruct = "Choose a schedule to open";
			header = "Open Schedule";
		}
		if(s.equals("compareOne")){
			instruct = "Choose your first schedule to compare";
			header = "Compare Schedules";	
		}
		if(s.equals("compareTwo")){
			instruct = "Choose your second schedule to compare";
			header = "Compare Schedules";
		}

		ArrayList<String> scheduleNames = FileHandler.getScheduleNames(savedScheduleFolder);
		if(!scheduleNames.isEmpty()){
			String[] finalSchedNames = new String[scheduleNames.size()];
			for(int i=0; i<finalSchedNames.length; i++){
				finalSchedNames[i]=scheduleNames.get(i);
			}
			String chosenSched = (String) JOptionPane.showInputDialog(null, instruct, header, JOptionPane.PLAIN_MESSAGE, null, finalSchedNames, finalSchedNames[0]);	
			return chosenSched;
		}
		else{
			JOptionPane.showMessageDialog(null, "You have no saved schedules");
			return null; 
		}
	}


/**
 * 
 * @return Schedule that the user has chosen to open. 
 */
	public static Schedule openSchedule(){
		String chosenSched = chooseSchedule("open");
		if(chosenSched !=null){
			try(FileInputStream fis = new FileInputStream(savedScheduleFolder+File.separator+chosenSched);
					ObjectInputStream ois = new ObjectInputStream(fis);) {
				Schedule result = (Schedule) ois.readObject();
				ois.close();
				return result;
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "There was an error in opening your schedule");
				e.printStackTrace();
			}
		}
		return null;
	}

/**
 * Allows user to save schedule, if a user picks a non-unique name 
 * their old schedule will be overwritten, thus the user is warned and 
 * allowed to rename the schedule they are trying to save. 
 * @param sch
 */
	public static void saveSchedule(Schedule sch){
		boolean uniqueName = false;//asks until user gives permission to overwrite, or chooses unique name. 
		String fileName = null;
		String schedName = (String)JOptionPane.showInputDialog(null, "Name your schedule", "Save Schedule", JOptionPane.PLAIN_MESSAGE, null, null, null);
		fileName=schedName;
		if(fileName!= null){
			while(!uniqueName){
				if(FileHandler.getScheduleNames(savedScheduleFolder).contains(fileName +".ser")){
					String[] options = {"Overwrite my old schedule", "Save both schedules"};
					int overwrite = (int)JOptionPane.showOptionDialog(null, "Would you like to overwrite an already existing schedule, or rename your current schedule?", "Save Schedule", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
					if(overwrite==1){
						String newName = (String)JOptionPane.showInputDialog(null, "Please choose a different name for your schedule", "Save Schedule", JOptionPane.PLAIN_MESSAGE, null, null, null);
						fileName=newName;
					}
					if(overwrite == 0){
						uniqueName=true;
					}
				}
				else{
					uniqueName=true;
				}
			}
		}
		if(fileName != null){
			try{
				saveObjectToFile(savedScheduleFolder + fileName + ".ser", sch);
			}catch (IOException e) {
				JOptionPane.showMessageDialog(null, "This schedule was not able to be saved. ");
				e.printStackTrace();
			}
		}
	}


	/**
	 * This gets all of the schedule's names that are saved in a file. 
	 * @param FolderName
	 * @return ArrayList of these names. 
	 */
	public static ArrayList<String> getScheduleNames(String FolderName){
		ArrayList<String> scheduleNames = new ArrayList<String>();
		File folder = new File(FolderName);
		for (File f: folder.listFiles(
				new FileFilter(){
					@Override

					public boolean accept(File pathname) {

						String fullName = pathname.getAbsolutePath();
						int i = fullName.lastIndexOf('.');
						if(i <= 0){
							return false;
						}
						String extension = fullName.substring(i+1);
						return pathname.isFile() && extension.equals("ser"); //denotes a seralized schedule. 
					}
				}
				)){


			scheduleNames.add(f.getName());


		}


		return scheduleNames;
	}


	public static final String majorsFile = resourcesFolder + "Majors";


/**
 * This checks to see if the ListOfMajors have been read
 * if not it will read and return the folder containing
 * the list of all of Furman's majors. 
 * @return
 */
	public static ListOfMajors getMajorsList(){
		if(listOfMajors == null){
			listOfMajors = readMajorsFrom(new File(majorsFile));
		}
		return listOfMajors;
	}

	/**
	 * Creates the ListOfMajors
	 * @param folder
	 * @return ListOFMajors Object. Which is an arrayList of Majors
	 */
	public static ListOfMajors readMajorsFrom(File folder){
		ListOfMajors result = new ListOfMajors();
		for (File f: folder.listFiles(

				new FileFilter(){

					@Override

					public boolean accept(File pathname) {

						String fullName = pathname.getAbsolutePath();
						int i = fullName.lastIndexOf('.');
						if(i <= 0){
							return false;
						}
						String extension = fullName.substring(i+1);
						return pathname.isFile() && extension.equals("mjr");
					}

				}
				)){

			try{
				Major major = Major.readFrom(fileToString(f));
				result.add(major);
			}
			catch(Exception e){
				e.printStackTrace();
				System.out.println("I'm skipping  " + f.getName()); //In case of error it will alert which file does not fit correct format. 
			}

		}
		return result;
	}


	/**
	 * Return the contents of this file as a list of strings, where
	 * each element of the list is one line.
	 * @param f
	 * @return
	 */
	public static ArrayList<String> fileToStrings(File f){

		ArrayList<String> result = new ArrayList<String>();
		try(BufferedReader br = new BufferedReader(new FileReader(f));) {
			String nextLine = br.readLine();
			while (nextLine!=null){
				result.add(nextLine);
				nextLine=br.readLine();
			}
			br.close();
		} catch (Exception e) {
			System.out.println("There was an issue reading from " + f.getAbsolutePath());
			e.printStackTrace();
		}


		return result;
	}

	/**
	 * Return the contents of this file as one large string.
	 * @param f
	 * @return
	 */
	public static String fileToString(File f){
		StringBuilder result = new StringBuilder();
		try(BufferedReader br = new BufferedReader(new FileReader(f));) {

			String nextLine = br.readLine();
			while (nextLine!=null){
				result.append(nextLine);
				result.append("\n");
				nextLine=br.readLine();
			}
			br.close();
		} catch (Exception e) {
			System.out.println("There was an issue reading from " + f.getAbsolutePath());
			e.printStackTrace();
		}

		return result.toString();
	}



	/**
	 * Find all course files and tell CourseList
	 * (the static class) to read the data in each 
	 * of these files.
	 */
	public static void readAllCourses() {
		File f = new File(courseListFolder);
		for ( File semesterFile : f.listFiles(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name) {
				if(name.contains(".csv")){
					return true;
				}
				return false;
			}

		})){

			CourseList.addCoursesIn(fileToStrings(semesterFile));
		}

	}



	/**
	 * load the prereq meanings hashtable from 
	 * the file, and set the appropriate 
	 * static field in CourseList.
	 * See CourseList savedPrereqMeanings
	 * for an explanation of how prereqMeanings
	 * are used.
	 */
	public static void loadPrereqMeanings(){
		loadPrereqMeanings(prereqMeaningsFile);
	}
	private static void loadPrereqMeanings(String fileName){
		File f = new File(fileName);
		if(!f.exists()){
			try{
				f.createNewFile();
			}catch(Exception e){}
			CourseList.savedPrereqMeanings = new Hashtable<String, String>();
			savePrereqMeanings();
			return;
		}
		try(FileInputStream fis = new FileInputStream(fileName);
				ObjectInputStream in = new ObjectInputStream(fis);){

			CourseList.savedPrereqMeanings = (Hashtable<String, String>) in.readObject();
			in.close();
			fis.close();
		}catch(Exception e){
			System.out.println("There was an error loading the saved meanings");
			e.printStackTrace();
		}

	}


	public static void savePrereqMeanings(){
		try (FileOutputStream fos = new FileOutputStream(prereqMeaningsFile, false); //overwrite, don't append.
				ObjectOutputStream out = new ObjectOutputStream(fos);){

			out.writeObject(CourseList.savedPrereqMeanings);
			out.close();
			fos.close();
		}
		catch(Exception e){
			System.out.println("There was an error saving the meanings of your prereq strings");
			e.printStackTrace();
		}

	}

/**
 * This allows a user to delete a schedule from the UserData. 
 */
	public static void deleteSchedule() {
		String chosen = chooseSchedule("delete");
		if(chosen != null){
			int n = JOptionPane.showOptionDialog(null, "Are you sure you want to delete "+ chosen, "Delete Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.OK_CANCEL_OPTION, null, null, null);
			if(n==0){
				File toDelete = new File (savedScheduleFolder + chosen);
				boolean deleted = toDelete.delete();
				if(!deleted){
					JOptionPane.showMessageDialog(null, "Error: Unable to delete schedule");
				}

			}
		}
	}


/**
 * This creates a panel that allows the
 * user to change their settings. This is made 
 * new each time, so that the information is always
 * up to date. 
 */
	public static void showSetting() {
		new SettingsPanel();

	}




/**
 * This was used when re-saving majors, not used now
 * could become necessary if the saveString of majors
 * is altered. 
 */
//	public void saveMe(Major m, File f){
//		try(BufferedWriter bf = new BufferedWriter(new FileWriter(f));) {
//			bf.write(m.saveString());
//			bf.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
	/////////////////////////////////
	/////////////////////////////////
	///// Image Loading
	/////////////////////////////////
	/////////////////////////////////
	@SuppressWarnings("unused")
	private boolean ___ImageLoading_________;
	
	private static ArrayList<ImageIcon> instructions;
	private static ImageIcon bellTower;
	private static ImageIcon fireworks;
	

	
	/**
	 * This is image that is to be shown in all dialog boxes. 
	 * @return
	 */
	public static ImageIcon getDialogBoxImage(){
		return new ImageIcon(FileHandler.resourcesFolder + "FurmanOfficialAcademicLogo.jpg");
	}

	/**
	 * This gets the slides for the startUpMenu
	 * @return
	 */
	public static ArrayList<ImageIcon> getStartUpSlides() {
		if(instructions == null){
			return loadInstructions();
		}
		return instructions;
	}
	
	/**
	 * 
	 * @return Image used by the BellTower class
	 */
	public static ImageIcon getBellTower(){
		if(bellTower == null){
			return loadBellTower();
		}
		return bellTower;
	}
	
	/**
	 * 
	 * @return The picture used to congratulate the 
	 * user for a good schedule. 
	 */
	public static ImageIcon getFireworks(){
		if(fireworks == null){
			return loadFireworks();
		}
		return fireworks;
	}
	
	/**
	 * This loads all the images together. Not used currently
	 */
	public void loadImages(){
		loadBellTower();
		loadFireworks();
		loadInstructions();
	}
	
	/**
	 * Scales and returns bellTower
	 * @return
	 */
	private static ImageIcon loadBellTower() {
		ImageIcon original = new ImageIcon(bellTowerImageFile);
		Image image = original.getImage();
		Image newImage = image.getScaledInstance(
				BellTower.imageWidth, 
				BellTower.imageHeight ,
				java.awt.Image.SCALE_SMOOTH);
		bellTower = new ImageIcon(newImage);
		return bellTower;
	}

	/**
	 * Loads fireWork image.
	 * @return
	 */
	private static ImageIcon loadFireworks() {
		fireworks =  new ImageIcon(fireworksImageFile);
		return fireworks;
	}
		
	/**
	 * Loads and scales startUp Images, the 
	 * scaled images will be saved once finalized to save time. 
	 * It gets and returns all of the images in this StartUpSlides. folder. 
	 * @return
	 */
	private static ArrayList<ImageIcon> loadInstructions(){
		File folder = new File(startUpFolder);
		ArrayList<ImageIcon> result = new ArrayList<ImageIcon>();
		
		File[] files = folder.listFiles(new FileFilter(){
			@Override
			public boolean accept(File pathname) {
				String fullName = pathname.getAbsolutePath();
				int i = fullName.lastIndexOf('.');
				if(i <= 0){
					return false;
				}
				return pathname.isFile();
			}});
		Arrays.sort(files);
		
		for (File f: files){
			try{
				ImageIcon image = new ImageIcon(f.toString());
			//	if(f.toString().equals("1.png")){

					//This was used to scale when we had slides. This may not be necessary if we use GIFs. 
			//		Image img = image.getImage();
			//		double scalar = 2;
			//		Image newimg = img.getScaledInstance((int)scalar* 380, (int)scalar* 280, java.awt.Image.SCALE_SMOOTH);
			//		image = new ImageIcon(newimg);
			//	}
				result.add(image);

			}
			catch(Exception e){
				e.printStackTrace();
				System.out.println("I'm skipping  " + f.getName());
			}
		}
		instructions = result;
		return result;
	}


	
	
	/////////////////////////////////
	/////////////////////////////////
	///// Utility methods
	/////////////////////////////////
	/////////////////////////////////
	@SuppressWarnings("unused")
	private boolean ___UtilityMethods_________;
	
	
	/**
	 * This makes a folder out of given name if it does
	 * not already exist. If the savedScheduleFolder is made,
	 * it also transfers the tutorial schedule to the savedScheduleFolder.  
	 * @param folderName
	 */
	public static void makeFolder(String folderName){
		File folder = new File(folderName);
		if(!folder.exists()){
			folder.mkdir();
			if(folderName.equals(savedScheduleFolder)){
				copyFileUsingStream(new File(testScheduleSource), new File(testScheduleDestination));

			}
		}
	}
	
	/**
	 * This is all used for Requirement Testing purposes
	 */
	public static String testResultsFolder = "ProgrammerTests" + File.separator;
	public static String requirementTestsFile = "RequirementResults.txt";
	public static Object loadRequirementTestResults() throws ClassNotFoundException, IOException{
		makeFolder(testResultsFolder);
		return readObjectFromFile(testResultsFolder + requirementTestsFile);
	}
	public static void saveRequirementTestResults(Serializable results) throws IOException{
		saveObjectToFile(testResultsFolder + requirementTestsFile, results);
	}
	
	
	
	/**
	 * USUSED METHOD
	 * @param fileName saves the data to a file. (Could be used for giving print version to the user in file form in
	 * the future)
	 * @param data
	 */

	private static void saveToFile(String fileName, String data){
		try(FileWriter fw = new FileWriter(fileName); BufferedWriter b = new BufferedWriter(fw);){
			b.write(data);
		}
		catch(Exception e){
			e.printStackTrace();
		}

	}

	
	/**
	 * This saves an object to a file. This is used for schedules. 
	 * @param fileName
	 * @param data
	 * @throws IOException
	 */
	private static void saveObjectToFile(String fileName, Serializable data) throws IOException{
		try( FileOutputStream saveFile = new FileOutputStream(fileName); 
				ObjectOutputStream save = new ObjectOutputStream(saveFile); ) {
			save.writeObject(data);	
			save.close();
			saveFile.close();
		} catch (IOException e) {
			throw e;
		}
	}

	/**
	 * This is used when a schedule is opened. 
	 * @param fileName
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private static Object readObjectFromFile(String fileName) throws IOException, ClassNotFoundException{
		try(FileInputStream fis = new FileInputStream(fileName);
				ObjectInputStream ois = new ObjectInputStream(fis);) {
			Object result = ois.readObject();
			ois.close();
			return result;
		}
	}

	/**
	 * This transfers one file from one destination to another. In this version it
	 * clones and transfers the tutoiral.ser to the userData so that the program always has a schedule
	 * to open when it originally open. The user will have the option to delete the tutorial. 
	 * @param source
	 * @param dest
	 */
	private static void copyFileUsingStream(File source, File dest) {
		try(InputStream is = new FileInputStream(source); OutputStream os = new FileOutputStream(dest);) {
			byte [] buffer = new byte[1024];
			int length;
			while((length= is.read(buffer)) >0){
				os.write(buffer, 0, length);
			}
			is.close();
			os.close();
		}
		catch (Exception e1) {
			e1.printStackTrace();
		}
	}	
	

	/////////////////////////////////
	/////////////////////////////////
	///// CSV parsing
	/////////////////////////////////
	/////////////////////////////////
	@SuppressWarnings("unused")
	private boolean ___CSVParsing_________;
	
	/**
	 * Split this string based on its commas, ignoring commas inside
	 * quotes.
	 * 
	 * Most of the code was found online, we added the hash set of ignore characters.
	 * @param CSVText
	 * @return
	 */
	public static ArrayList<String> parseCSVLine(String csvLine, HashSet<Character> ignoreCharacters){
		ArrayList<String> result = new ArrayList<>();
		if (csvLine == null || csvLine.isEmpty()) {
			return result;
		}
		StringBuilder chunk = new StringBuilder();
		boolean inQuotes = false;
		char prevChar = 0; //could be used to check if you have a '/' inside quotes.

		for (char ch : csvLine.toCharArray()) {
			if (inQuotes) {
				if (ch == '"') {
					inQuotes = false;
				} 
				else {
					chunk.append(ch);
					prevChar = ch;
				}
			} else { //if you're not in quotes
				if (ch == '"') {
                    inQuotes = true;
                } 
				else if (ch == ',') {
                    result.add(chunk.toString());
                    chunk = new StringBuilder();
                } 
				else if (ignoreCharacters.contains(ch)) {
                    //ignore LF characters
                    continue;
                }
				else if (ch == '\n') {
                    //the end, break!
                    break;
                } 
				else {
                    chunk.append(ch);
                }
            }
        } 
        result.add(chunk.toString());
        return result;
    }
	
	/**
	 * Convert a line in a CSV file into an arrayList of strings.
	 */
	public static ArrayList<String> parseCSVLine(String csvLine){
		HashSet<Character> ignoreCharacters = new HashSet<Character>();
		ignoreCharacters.add('\r');
		return parseCSVLine(csvLine, ignoreCharacters);
	}
	
	/**
	 * Almost the same as parsing as CSV, but you have to ignore an '=' just
	 * before quotations in some cases. Might see data of the form:
	 * 
	 * data, data, ="9999", "data", data, ="2016", data
	 * 
	 * @param csvLine
	 * @return
	 */
	public static ArrayList<String> parseAdvisorImportCSVLine(String csvLine){
		HashSet<Character> ignoreCharacters = new HashSet<Character>();
		ignoreCharacters.add('\r');
		ignoreCharacters.add('='); //some lines are of the form :
		// ="99999", ="2017D2","asdfas","asdfasd"
		return parseCSVLine(csvLine, ignoreCharacters);
	}

}




