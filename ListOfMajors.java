import java.util.ArrayList;

public class ListOfMajors extends ArrayList{
	
	private ArrayList<Major> listOfMajors = new ArrayList();
	
	public static ListOfMajors testList(){
		ListOfMajors result = new ListOfMajors();
		Major[] list = new Major[]{
			Major.readFrom("Math-BS\n" +
					"REQ:1 of [MTH-250]; 	 0 Completed; DDN:1\n" +
					"REQ:1 of [MTH-260]; 	 0 Completed; DDN:1\n" +
					"REQ:1 of [ MTH-450, MTH-350]; 	 0 Completed; DDN:1\n" +
					"REQ:1 of [ MTH-460, MTH-360]; 	 0 Completed; DDN:1\n"+
					"REQ:7 of [ MTH-160, MTH-250,  MTH-320,  MTH-335,  MTH-337,  MTH-340,  MTH-341,  MTH-350,  MTH-360,  MTH-420,  MTH-435,  MTH-450,  MTH-450,  MTH-451,  MTH-460,  MTH-461,  MTH-504, MTH-151]; 	 0 Completed; DDN:2\n" +
					"REQ:2 of [ BIO-111, CHM-110,  CHM-115, CHM-120, EES-115, EES-112, EES-113, PHY-111, PHY-112, PSY-320, SUS-120]; 0 Completed; DDN:3 \n"
),
		//	Major.readFrom(),
		//	Major.readFrom()
				
		};
		for(Major m : list){
			result.add(m);
		}
		
		return result;
	}

	public ListOfMajors(){
		this.listOfMajors = new ArrayList<Major>();
		
	}
	
	
	public boolean add(Major m){
	 
		return listOfMajors.add(m);
		
	}

}
