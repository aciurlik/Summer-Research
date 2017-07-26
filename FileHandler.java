
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
import java.util.Hashtable;
import java.util.Properties;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;


/**
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
	public static final String startUpFolder = resourcesFolder + "StartUpSlides" + File.separator;
	public static final String settingsDoc = userDataFolder +  "Settings";

	public static final String testScheduleSource = resourcesFolder +  "tutorial.ser";
	public static final String testScheduleDestination = savedScheduleFolder + "tutorial.ser";


	private static Properties properties;
	static ListOfMajors listOfMajors = null;


	/**
	 * This static block tries to load the existing settings
	 * 
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
	public static void requestRestoreDefaultSettings(){
		int n = JOptionPane.showConfirmDialog(null, "Are you sure you would like to restore default settings?", "Restore Default Settings", JOptionPane.OK_CANCEL_OPTION);
		if(n==0){
			FileHandler.restoreDefaultSettings();
		}
	}
	private static void restoreDefaultSettings() {
		properties.setProperty(MenuOptions.startUp, "true");
		saveProperties();
	}

	private static void saveProperties(){
		try(FileOutputStream output = new FileOutputStream(settingsDoc);) {
			properties.store(output, "Edited Settings");
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static PriorData getSavedStudentData(){
		File f = new File(studentDataFile);
		if(!f.exists()){
			return null;
		}
		try{
			return (PriorData)FileHandler.readObjectFromFile(studentDataFile);
		}
		catch(Exception e){
			JOptionPane.showMessageDialog(null, "There was an issue loading your saved student data");
			e.printStackTrace();
			return null;
		}
	}

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
			ArrayList<String> splitLine = SaverLoader.parseAdvisorImportCSVLine(fullString.get(i));
			result[i] = splitLine.toArray(new String[splitLine.size()]);
		}
		return result;

	}

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




	public static String propertyGet(String setting){
		String value = properties.getProperty(setting);
		return value;
	}



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



	public static Schedule openSchedule(){
		String chosenSched = chooseSchedule("open");
		if(chosenSched !=null){
			try(FileInputStream fis = new FileInputStream(savedScheduleFolder+File.separator+chosenSched);
					ObjectInputStream ois = new ObjectInputStream(fis);) {
				Schedule result = (Schedule) ois.readObject();
				ois.close();
				return result;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}


	public static void saveSchedule(Schedule sch){
		boolean uniqueName = false;
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
				System.out.println(e.getLocalizedMessage());
			}
		}
	}






	private static void saveToFile(String fileName, String data){
		try(FileWriter fw = new FileWriter(fileName); BufferedWriter b = new BufferedWriter(fw);){
			b.write(data);
		}
		catch(Exception e){
			e.printStackTrace();
		}

	}

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

	private static Object readObjectFromFile(String fileName) throws IOException, ClassNotFoundException{
		try(FileInputStream fis = new FileInputStream(fileName);
				ObjectInputStream ois = new ObjectInputStream(fis);) {
			Object result = ois.readObject();
			ois.close();
			return result;
		}
	}

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
						return pathname.isFile() && extension.equals("ser");
					}
				}
				)){


			scheduleNames.add(f.getName());


		}


		return scheduleNames;
	}


	public static final String majorsFile = resourcesFolder + "Majors";



	public static ListOfMajors getMajorsList(){
		if(listOfMajors == null){
			listOfMajors = readMajorsFrom(new File(majorsFile));
		}
		return listOfMajors;
	}

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
				System.out.println("I'm skipping  " + f.getName());
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
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
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


	public static ImageIcon getBelltowerImage(){
		return new ImageIcon(FileHandler.resourcesFolder + "FurmanOfficialAcademicLogo.jpg");
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


	public static ArrayList<ImageIcon> getInstructions() {
		File folder = new File(resourcesFolder + "StartUpSlides");
		ArrayList<ImageIcon> result = new ArrayList<ImageIcon>();

		for (File f: folder.listFiles(

				new FileFilter(){

					@Override

					public boolean accept(File pathname) {

						String fullName = pathname.getAbsolutePath();
						int i = fullName.lastIndexOf('.');
						if(i <= 0){
							return false;
						}

						return pathname.isFile();
					}

				}
				)){

			try{
				ImageIcon image = new ImageIcon(f.toString());
				Image img = image.getImage();
				double scalar = 2;
				Image newimg = img.getScaledInstance((int)scalar* 380, (int)scalar* 280, java.awt.Image.SCALE_SMOOTH);
				image = new ImageIcon(newimg);


				result.add(image);

			}
			catch(Exception e){
				e.printStackTrace();
				System.out.println("I'm skipping  " + f.getName());
			}


		}


		return result;
	}


	public static void showSetting() {
		SettingsPanel sp = new SettingsPanel();

	}





	public void saveMe(Major m, File f){

		try(BufferedWriter bf = new BufferedWriter(new FileWriter(f));) {
			bf.write(m.saveString());
			bf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	public static ImageIcon makeBellTower() {
		return new ImageIcon(bellTowerImageFile);
	}

	public static ImageIcon makeFireWorks() {
		// TODO Auto-generated method stub
		return  new ImageIcon(fireworksImageFile);
	}
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
	 * This transfers one file from one destination to another. In this version it
	 * clones and transfers the test.ser to the userData so that the program always has a schedule
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
}
