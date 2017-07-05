import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class FileHandler {
	static JFrame popUP = new JFrame();
	public static final String prereqMeaningsFile = MenuOptions.resourcesFolder + "PrereqMeanings.txt";
	public static final String courseListFolder = MenuOptions.resourcesFolder + "CourseCatologs";

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
		
		ArrayList<String> scheduleNames = FileHandler.getScheduleNames(MenuOptions.savedScheduleFolder);
		if(!scheduleNames.isEmpty()){
		String[] finalSchedNames = new String[scheduleNames.size()];
		for(int i=0; i<finalSchedNames.length; i++){
			finalSchedNames[i]=scheduleNames.get(i);
		}
		String chosenSched = (String) JOptionPane.showInputDialog(popUP, instruct, header, JOptionPane.PLAIN_MESSAGE, null, finalSchedNames, finalSchedNames[0]);	
		return chosenSched;
	}
		else{
			JOptionPane.showMessageDialog(popUP, "You have no saved schedules");
			return null; 
		}
	}


	public static Schedule openSchedule(){
		String chosenSched = chooseSchedule("open");
		if(chosenSched !=null){

			FileInputStream fis = null;
			try {
				fis = new FileInputStream(MenuOptions.savedScheduleFolder+File.separator+chosenSched);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ObjectInputStream ois = null;
			try {
				ois = new ObjectInputStream(fis);
				Schedule result = (Schedule) ois.readObject();
				ois.close();
				return result;

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}
			finally{
				try {
					ois.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;

	}


	public static void saveSchedule(Schedule sch){
		String fileName = null;
		String schedName = (String)JOptionPane.showInputDialog(popUP, "Name your schedule", "Save Schedule", JOptionPane.PLAIN_MESSAGE, null, null, null);
		fileName=schedName;
		if(fileName!= null){
			if(FileHandler.getScheduleNames(MenuOptions.savedScheduleFolder).contains(fileName +".ser")){
				String[] options = {"Overwrite my old schedule", "Save both schedules"};
				int overwrite = (int)JOptionPane.showOptionDialog(popUP, "Would you like to overwrite an already existing schedule, or rename your current schedule?", "Save Schedule", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
				if(overwrite==1){
					String newName = (String)JOptionPane.showInputDialog(popUP, "Please choose a different name for your schedule", "Save Schedule", JOptionPane.PLAIN_MESSAGE, null, null, null);
					fileName=newName;
				}


			}


		}
		if(fileName != null){
			ObjectOutputStream save = null;
			FileOutputStream saveFile = null;
			try {
				saveFile = new FileOutputStream(MenuOptions.savedScheduleFolder + File.separator + fileName + ".ser");
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {

				save = new ObjectOutputStream(saveFile);
				save.writeObject(sch);	
				save.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

				System.out.println(e.getLocalizedMessage());
			}

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


	public static final String majorsFile = MenuOptions.resourcesFolder + "Majors";



	public static ListOfMajors getMajorsList(){
		ListOfMajors result = readMajorsFrom(new File(majorsFile));
		return result;
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
				Major major = Major.readFrom(f);
				//System.out.println(major.saveString());
				result.add(major);


			}
			catch(Exception e){
				e.printStackTrace();
				System.out.println("I'm skipping  " + f.getName());
			}

		}
		return result;
	}

	public static ArrayList<String> fileToString(File furmanCoursesFile){
		BufferedReader br = null;
		ArrayList<String> result = new ArrayList<String>();
		try {
			br = new BufferedReader(new FileReader(furmanCoursesFile));
			String nextLine = br.readLine();
			while (nextLine!=null){
				result.add(nextLine);
				nextLine=br.readLine();
			}
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("There was an issue reading from " + furmanCoursesFile.getAbsolutePath());
			e.printStackTrace();
		}

		return result;

	}


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

			CourseList.addCoursesIn(fileToString(semesterFile));
		}

	}


	public static void loadPrereqMeanings(String fileName){
		File f = new File(fileName);
		if(!f.exists()){
			try{
				f.createNewFile();
			}catch(Exception e){}
			CourseList.savedPrereqMeanings = new Hashtable<String, String>();
			savePrereqMeanings();
			return;
		}
		try{
			FileInputStream fis = new FileInputStream(fileName);
			ObjectInputStream in = new ObjectInputStream(fis);
			CourseList.savedPrereqMeanings = (Hashtable<String, String>) in.readObject();
			in.close();
		}catch(Exception e){
			System.out.println("There was an error loading the saved meanings");
			e.printStackTrace();
		}
	}


	public static void savePrereqMeanings(){
		try{
			FileOutputStream fos = new FileOutputStream(prereqMeaningsFile, false); //overwrite, don't append.
			ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(CourseList.savedPrereqMeanings);
			out.close();
		}
		catch(Exception e){
			System.out.println("There was an error saving the meanings of your prereq strings");
			e.printStackTrace();
		}
	}


	public static void deleteSchedule() {
		String chosen = chooseSchedule("delete");
		if(chosen != null){
			int n = JOptionPane.showOptionDialog(popUP, "Are you sure you want to delete "+ chosen, "Delete Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.OK_CANCEL_OPTION, null, null, null);

			if(n==0){
				File toDelete = new File (MenuOptions.savedScheduleFolder + chosen);
				toDelete.delete();

			}
		}
	}

}
