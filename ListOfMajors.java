import java.util.ArrayList;

public class ListOfMajors extends ArrayList<Major>{
	
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
				Major.readFrom("Math-BS\n" +
						"REQ:1 of [MTH-250]; 	 0 Completed; DDN:1\n" +
						"REQ:1 of [MTH-260]; 	 0 Completed; DDN:1\n" +
						"REQ:1 of [MTH-450, MTH-350]; 	 0 Completed; DDN:1\n" +
						"REQ:1 of [MTH-460, MTH-360]; 	 0 Completed; DDN:1\n"+
						"REQ:7 of [MTH-160, MTH-250, MTH-320, MTH-335, MTH-337, MTH-340, MTH-341, MTH-350, MTH-360, MTH-420, MTH-435, MTH-450, MTH-450, MTH-451, MTH-460, MTH-461, MTH-504, MTH-151]; 	 0 Completed; DDN:2\n" +
						"REQ:2 of [BIO-111, CHM-110, CHM-115, CHM-120, EES-115, EES-112, EES-113, PHY-111, PHY-112, PSY-320, SUS-120]; 	 0 Completed; DDN:3")
		};
		//	Major.readFrom(),
		//	Major.readFrom()
				
		
		
		for(Major m : list){
			result.add(m);
			System.out.println(result.add(m));
			
		}
		
		return result;
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
	
	public Major removeMajor(Major m){
		completeMajorsList.remove(m);
		return m;
	}
	
	public Major removeAtIndex(int i){
		Major m = completeMajorsList.remove(i);
		return m;
	}
	
	public ArrayList<String> nameForPopup(Iterable<Major> m){
		ArrayList<String> list = new ArrayList<String>();
		for (Major major: m){
			list.add(major.name);
		
		}
		return list;
		
		
	}
	

	
}
