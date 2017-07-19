import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ListOfMajors {

	/**
	 * 
	 */
	
	static

	private ArrayList<Major> completeMajorsList = new ArrayList<Major>();


	
	
	public ArrayList<Major> getCompleteMajorsList() {
		return completeMajorsList;
	}





	public ListOfMajors(){
		this.completeMajorsList = new ArrayList<Major>();

	}


	public boolean add(Major m){
		return completeMajorsList.add(m);


	}

	public void addAt(Major m, int i){

		completeMajorsList.add(i, m);
	}




	public int getSize() {
		return completeMajorsList.size();
	}

	public Major get(int i){
		Major m = completeMajorsList.get(i);

		return m;
	}


	/**
	 *	public Major removeMajor(Major m){
		completeMajorsList.remove(m);
		return m;
	}

	public Major removeAtIndex(int i){
		Major m = completeMajorsList.remove(i);
		return m;
	} 
	 *
	 */


	public ArrayList<String> nameForPopup(Iterable<Major> m){
		ArrayList<String> list = new ArrayList<String>();
		for (Major major: m){
			list.add(major.name);

		}
		return list;


	}


	public ArrayList<Major> getGUIMajors() {
		ArrayList<Major> majorGUI = new ArrayList<Major>();
		for(Major m: completeMajorsList){
			if (m.isType(Major.NORMAL_MAJOR)){
				majorGUI.add(m);
			}

		}

		return majorGUI;

	}


	public ArrayList<Major> getGUIMinor() {
		ArrayList<Major> minorGUI = new ArrayList<Major>();
		for(Major m: completeMajorsList){
			if (m.isType(Major.MINOR)){
				minorGUI.add(m);
			}

		}
		return minorGUI;
	}


	public ArrayList<Major> getGUITrack() {
		ArrayList<Major> trackGUI = new ArrayList<Major>();
		for(Major m: completeMajorsList){
			if (m.isType(Major.TRACK)){
				trackGUI.add(m);
			}

		}
		return trackGUI;
	}


	public  Major getMajor(String s){
		int wantedMajor =0;
		for(int i = 0; i<completeMajorsList.size(); i++){
			if(completeMajorsList.get(i).name.equals(s)){
				wantedMajor=i;
			}
		}
		return completeMajorsList.get(wantedMajor);

	}

	public static void main(String[] args){
		ListOfMajors l = FileHandler.getMajorsList();
		for(Major m : l.completeMajorsList){
			System.out.println("Start of Major:");
			System.out.println(m.saveString());
			System.out.println("\n");
		}
		
	}
	/*
	 * 
Math-BS
REQ:{Number to Choose: {1} Choices: {{MTH-250}} DDN: {0} Requirement Name:{null}}
REQ:{Number to Choose: {1} Choices: {{MTH-260}} DDN: {0} Requirement Name:{null}}
REQ:{Number to Choose: {1} Choices: {{ MTH-350}{MTH-450}} DDN: {0} Requirement Name:{null}}
REQ:{Number to Choose: {1} Choices: {{ MTH-360}{MTH-460}} DDN: {0} Requirement Name:{null}}
REQ:{Number to Choose: {7} Choices: {{ MTH-151}{ MTH-250}{ MTH-320}{ MTH-335}{ MTH-337}{ MTH-340}{ MTH-341}{ MTH-350}{ MTH-360}{ MTH-420}{ MTH-435}{ MTH-450}{ MTH-450}{ MTH-451}{ MTH-460}{ MTH-461}{ MTH-504}{MTH-160}} DDN: {0} Requirement Name:{null}}
REQ:{Number to Choose: {2} Choices: {{ CHM-110}{ CHM-115}{ CHM-120}{ EES-112}{ EES-113}{ EES-115}{ PHY-111}{ PHY-112}{ PSY-320}{ SUS-120}{BIO-111}} DDN: {0} Requirement Name:{null}}

Math-BA
REQ:{Number to Choose: {1} Choices: {{MTH-250}} DDN: {0} Requirement Name:{null}}
REQ:{Number to Choose: {1} Choices: {{MTH-260}} DDN: {0} Requirement Name:{null}}
REQ:{Number to Choose: {1} Choices: {{ MTH-350}{MTH-450}} DDN: {0} Requirement Name:{null}}
REQ:{Number to Choose: {1} Choices: {{ MTH-360}{MTH-460}} DDN: {0} Requirement Name:{null}}
REQ:{Number to Choose: {7} Choices: {{ MTH-151}{ MTH-250}{ MTH-320}{ MTH-335}{ MTH-337}{ MTH-340}{ MTH-341}{ MTH-350}{ MTH-360}{ MTH-420}{ MTH-435}{ MTH-450}{ MTH-450}{ MTH-451}{ MTH-460}{ MTH-461}{ MTH-504}{MTH-160}} DDN: {0} Requirement Name:{null}}
REQ:{Number to Choose: {2} Choices: {{ CHM-110}{ CHM-115}{ CHM-120}{ EES-112}{ EES-113}{ EES-115}{ PHY-111}{ PHY-112}{ PSY-320}{ SUS-120}{BIO-111}} DDN: {0} Requirement Name:{null}}

Applied Math-BS
REQ:{Number to Choose: {1} Choices: {{CSC-121}} DDN: {0} Requirement Name:{null}}
REQ:{Number to Choose: {1} Choices: {{CSC-122}} DDN: {0} Requirement Name:{null}}
REQ:{Number to Choose: {1} Choices: {{MTH-160}} DDN: {0} Requirement Name:{null}}
REQ:{Number to Choose: {1} Choices: {{MTH-250}} DDN: {0} Requirement Name:{null}}
REQ:{Number to Choose: {1} Choices: {{MTH-260}} DDN: {0} Requirement Name:{null}}
REQ:{Number to Choose: {1} Choices: {{ MTH-360}{ MTH-450}{ MTH-460}{MTH-350}} DDN: {0} Requirement Name:{null}}
REQ:{Number to Choose: {4} Choices: {{ BIO-340}{ BIO-440}{ BIO-445}{ CHM-310}{ CHM-330}{ CHM-340}{ CSC-343}{ CSC-461}{ ECN-331}{ ECN-345}{ ECN-346}{ ECN-475}{ MTH-320}{ MTH-330}{ MTH-335}{ MTH-337}{ MTH-340}{ MTH-341}{ MTH-435}{ PHY-311}{ PHY-312}{ PHY-312}{ PHY-321}{ PHY-322}{ PHY-421}{ PHY-421}{ PHY-441}{ PHY-442}{ PHY-451}{MTH-255}} DDN: {0} Requirement Name:{null}}
REQ:{Number to Choose: {2} Choices: {{ CHM-110}{ CHM-115}{ CHM-120}{ EES-112}{ EES-113}{ EES-115}{ PHY-111}{ PHY-112}{ PSY-320}{ SUS-120}{BIO-111}} DDN: {0} Requirement Name:{null}}

Math-Economics-BA
REQ:{Number to Choose: {1} Choices: {{ECN-111}} DDN: {0} Requirement Name:{null}}
REQ:{Number to Choose: {1} Choices: {{ECN-331}} DDN: {0} Requirement Name:{null}}
REQ:{Number to Choose: {1} Choices: {{ECN-345}} DDN: {0} Requirement Name:{null}}
REQ:{Number to Choose: {1} Choices: {{ECN-346}} DDN: {0} Requirement Name:{null}}
REQ:{Number to Choose: {1} Choices: {{ECN-475}} DDN: {0} Requirement Name:{null}}
REQ:{Number to Choose: {1} Choices: {{MTH-151}} DDN: {0} Requirement Name:{null}}
REQ:{Number to Choose: {1} Choices: {{MTH-160}} DDN: {0} Requirement Name:{null}}
REQ:{Number to Choose: {1} Choices: {{MTH-250}} DDN: {0} Requirement Name:{null}}
REQ:{Number to Choose: {2} Choices: {{ MTH-340}{ MTH-341}{ MTH-450}{MTH-260}} DDN: {0} Requirement Name:{null}}
REQ:{Number to Choose: {1} Choices: {{ MTH-255}{ MTH-260}{ MTH-335}{ MTH-340}{ MTH-341}{ MTH-360}{ MTH-450}{MTH-337}} DDN: {0} Requirement Name:{null}}

Math-Economics-BS
REQ:{Number to Choose: {1} Choices: {{ECN-111}} DDN: {0} Requirement Name:{null}}
REQ:{Number to Choose: {1} Choices: {{ECN-331}} DDN: {0} Requirement Name:{null}}
REQ:{Number to Choose: {1} Choices: {{ECN-345}} DDN: {0} Requirement Name:{null}}
REQ:{Number to Choose: {1} Choices: {{ECN-346}} DDN: {0} Requirement Name:{null}}
REQ:{Number to Choose: {1} Choices: {{ECN-475}} DDN: {0} Requirement Name:{null}}
REQ:{Number to Choose: {1} Choices: {{MTH-151}} DDN: {0} Requirement Name:{null}}
REQ:{Number to Choose: {1} Choices: {{MTH-160}} DDN: {0} Requirement Name:{null}}
REQ:{Number to Choose: {1} Choices: {{MTH-250}} DDN: {0} Requirement Name:{null}}
REQ:{Number to Choose: {2} Choices: {{ MTH-340}{ MTH-341}{ MTH-450}{MTH-260}} DDN: {0} Requirement Name:{null}}
REQ:{Number to Choose: {1} Choices: {{ MTH-255}{ MTH-260}{ MTH-335}{ MTH-340}{ MTH-341}{ MTH-360}{ MTH-450}{MTH-337}} DDN: {0} Requirement Name:{null}}

	 */




}
