import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints.Key;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class FileHandler implements ActionListener{
	static JFrame popUP = new JFrame();
	public static final String prereqMeaningsFile = MenuOptions.resourcesFolder + "PrereqMeanings.txt";
	public static final String courseListFolder = MenuOptions.resourcesFolder + "CourseCatologs";
	public static final String studentDataFile = MenuOptions.resourcesFolder + "SavedStudentData.txt";
	private static Properties p;

	static{
		p = new Properties();
		FileOutputStream output = null;
		File file = new File(MenuOptions.settingsDoc);
		boolean exists = file.exists();
		if(!exists){
			restoreDefaultSettings();
			try {
				output = new FileOutputStream(MenuOptions.settingsDoc);
				p.store(output, "New Settings");
				output.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			FileInputStream input = null;
			try {
				input = new FileInputStream(MenuOptions.settingsDoc);
				p.load(input);
				input.close();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}


	public static void propertySet(String setting, String value){
		p.setProperty(setting, value);
		FileOutputStream output;
		try {
			output = new FileOutputStream(MenuOptions.settingsDoc);
			p.store(output, "Edited Settings");
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static void GUICalledRestoreDefaultSettings(){
		int n = JOptionPane.showConfirmDialog(popUP, "Are you sure you would like to restore default settings?", "Restore Default Settings", JOptionPane.OK_CANCEL_OPTION);
		if(n==0){
			FileHandler.restoreDefaultSettings();
		}
	}
	
	
	private static void restoreDefaultSettings() {
		p.setProperty(MenuOptions.startUp, "true");
		
	}
	
	public static String getSavedStudentData(){
		File f = new File(studentDataFile);
		if(!f.exists()){
			return null;
		}
		try{
			return FileHandler.fileToString(new File(studentDataFile));
		}
		catch(Exception e){
			JOptionPane.showMessageDialog(null, "There was an issue loading your saved student data");
			e.printStackTrace();
			return null;
		}
	}
	
	public static void writeStudentData(String s){
		FileHandler.saveToFile(studentDataFile, s);
	}

	
	
	
	public static String propertyGet(String setting){
		String value = p.getProperty(setting);
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

			try(FileInputStream fis = new FileInputStream(MenuOptions.savedScheduleFolder+File.separator+chosenSched); ObjectInputStream ois = new ObjectInputStream(fis);) {
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


			try( FileOutputStream saveFile = new FileOutputStream(MenuOptions.savedScheduleFolder + File.separator + fileName + ".ser");
					ObjectOutputStream save = new ObjectOutputStream(saveFile); ) {


				save.writeObject(sch);	
				save.close();
				saveFile.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(popUP, "This schedule was not able to be saved. ");
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
			int n = JOptionPane.showOptionDialog(popUP, "Are you sure you want to delete "+ chosen, "Delete Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.OK_CANCEL_OPTION, null, null, null);

			if(n==0){
				File toDelete = new File (MenuOptions.savedScheduleFolder + chosen);
				toDelete.delete();


			}
		}
	}


	public static ArrayList<ImageIcon> getInstructions(File folder) {
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

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
