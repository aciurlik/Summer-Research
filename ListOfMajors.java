import java.util.ArrayList;

public class ListOfMajors {

	private ArrayList<Major> completeMajorsList = new ArrayList<Major>();






	public static ListOfMajors testList(){
		ListOfMajors result = new ListOfMajors();
		Major[] list = new Major[]{
				Major.readFrom("Math-BS\n" +
						"REQ:1 of [MTH-250]; 	 0 Completed; DDN:1\n" +
						"REQ:1 of [MTH-260]; 	 0 Completed; DDN:1\n" +
						"REQ:1 of [MTH-450, MTH-350]; 	 0 Completed; DDN:1\n" +
						"REQ:1 of [MTH-460, MTH-360]; 	 0 Completed; DDN:1\n"+
						"REQ:7 of [MTH-160, MTH-250, MTH-320, MTH-335, MTH-337, MTH-340, MTH-341, MTH-350, MTH-360, MTH-420, MTH-435, MTH-450, MTH-450, MTH-451, MTH-460, MTH-461, MTH-504, MTH-151]; 	 0 Completed; DDN:2\n" +
						"REQ:2 of [BIO-111, CHM-110, CHM-115, CHM-120, EES-115, EES-112, EES-113, PHY-111, PHY-112, PSY-320, SUS-120]; 	 0 Completed; DDN:3"),
				Major.readFrom("Math-BA\n" +
						"REQ:1 of [MTH-250]; 	 0 Completed; DDN:1\n" +
						"REQ:1 of [MTH-260]; 	 0 Completed; DDN:1\n" +
						"REQ:1 of [MTH-450, MTH-350]; 	 0 Completed; DDN:1\n" +
						"REQ:1 of [MTH-460, MTH-360]; 	 0 Completed; DDN:1\n"+
						"REQ:7 of [MTH-160, MTH-250, MTH-320, MTH-335, MTH-337, MTH-340, MTH-341, MTH-350, MTH-360, MTH-420, MTH-435, MTH-450, MTH-450, MTH-451, MTH-460, MTH-461, MTH-504, MTH-151]; 	 0 Completed; DDN:2\n" +
						"REQ:2 of [BIO-111, CHM-110, CHM-115, CHM-120, EES-115, EES-112, EES-113, PHY-111, PHY-112, PSY-320, SUS-120]; 	 0 Completed; DDN:3"),
				Major.readFrom("Applied Math-BS\n" +
						"REQ:1 of [CSC-121]; 	 0 Completed; DDN:1\n" +
						"REQ:1 of [CSC-122]; 	 0 Completed; DDN:1\n" +
						"REQ:1 of [MTH-160]; 	 0 Completed; DDN:1\n" +
						"REQ:1 of [MTH-250]; 	 0 Completed; DDN:1\n" +
						"REQ:1 of [MTH-260]; 	 0 Completed; DDN:1\n" +
						"REQ:1 of [MTH-350, MTH-360, MTH-450, MTH-460]; 	 0 Completed; DDN:2\n" +
						"REQ:4 of [MTH-255, MTH-335, MTH-337, MTH-340, MTH-341, MTH-435, BIO-340, BIO-440, BIO-445, CSC-343, CSC-461, CHM-310, CHM-330, CHM-340, ECN-331, ECN-345, ECN-346, ECN-475, MTH-320, MTH-330, PHY-311, PHY-312, PHY-321, PHY-312, PHY-322, PHY-421, PHY-421, PHY-441, PHY-442, PHY-451]; 	 0 Completed; DDN:2\n" +
						//Only two of these can be non math, or later listed math
						"REQ:2 of [BIO-111, CHM-110, CHM-115, CHM-120, EES-115, EES-112, EES-113, PHY-111, PHY-112, PSY-320, SUS-120]; 	 0 Completed; DDN:3"),
				Major.readFrom("Math-Economics-BA\n" +
						"REQ:1 of [ECN-111]; 	 0 Completed; DDN:1\n" +
						"REQ:1 of [ECN-331]; 	 0 Completed; DDN:1\n" +
						"REQ:1 of [ECN-345]; 	 0 Completed; DDN:1\n" +
						"REQ:1 of [ECN-346]; 	 0 Completed; DDN:1\n" +
						"REQ:1 of [ECN-475]; 	 0 Completed; DDN:1\n" +
						"REQ:1 of [MTH-151]; 	 0 Completed; DDN:1\n" +
						"REQ:1 of [MTH-160]; 	 0 Completed; DDN:1\n" +
						"REQ:1 of [MTH-250]; 	 0 Completed; DDN:1\n" +
						//Two more ECN courses numbered 201 or greater not ECN 225, or ECN-503
						"REQ:2 of [MTH-260, MTH-340, MTH-341, MTH-450]; 	 0 Completed; DDN:2\n" +
						"REQ:1 of [MTH-337, MTH-255, MTH-260, MTH-335, MTH-340, MTH-341, MTH-360, MTH-450]; 	 0 Completed; DDN:3"),
				Major.readFrom("Math-Economics-BS\n" +
						"REQ:1 of [ECN-111]; 	 0 Completed; DDN:1\n" +
						"REQ:1 of [ECN-331]; 	 0 Completed; DDN:1\n" +
						"REQ:1 of [ECN-345]; 	 0 Completed; DDN:1\n" +
						"REQ:1 of [ECN-346]; 	 0 Completed; DDN:1\n" +
						"REQ:1 of [ECN-475]; 	 0 Completed; DDN:1\n" +
						"REQ:1 of [MTH-151]; 	 0 Completed; DDN:1\n" +
						"REQ:1 of [MTH-160]; 	 0 Completed; DDN:1\n" +
						"REQ:1 of [MTH-250]; 	 0 Completed; DDN:1\n" +
						//Two more ECN courses numbered 201 or greater not ECN 225, or ECN-503
						"REQ:2 of [MTH-260, MTH-340, MTH-341, MTH-450]; 	 0 Completed; DDN:2\n" +
						"REQ:1 of [MTH-337, MTH-255, MTH-260, MTH-335, MTH-340, MTH-341, MTH-360, MTH-450]; 	 0 Completed; DDN:3" +
						"REQ:2 of [BIO-111, CHM-110, CHM-115, CHM-120, EES-115, EES-112, EES-113, PHY-111, PHY-112, PSY-320, SUS-120]; 	 0 Completed; DDN:3"),
				

		};
		//Requirement.readFrom("2 of [BIO-111, CHM-110, CHM-115, CHM-120, EES-115, EES-112, EES-113, PHY-111, PHY-112, PSY-320, SUS-120]");
		//	Major.readFrom(),
		//	Major.readFrom()

		for(Major m : list){
			result.add(m);
		}
		return result;
	}


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
			//if (m.major)){
			majorGUI.add(m);
			//	}

		}
		
		return majorGUI;

	}


	public Major[] getGUIMinor() {
		ArrayList<Major> majorGUI = new ArrayList<Major>();
		for(Major m: completeMajorsList){
			if (m.minor){
				majorGUI.add(m);
			}

		}
		return (Major[]) majorGUI.toArray();
	}


	public Major[] getGUITrack() {
		ArrayList<Major> majorGUI = new ArrayList<Major>();
		for(Major m: completeMajorsList){
			if (m.track){
				majorGUI.add(m);
			}

		}
		return (Major[]) majorGUI.toArray();
	}





}
