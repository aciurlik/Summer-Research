
import java.util.ArrayList;


public class Major {
	ArrayList<Requirement> reqList;
	
	public Major(){
		this.reqList = new ArrayList<Requirement>();
	}
	public static Major testMajor(){
		Major result = new Major();
		result.reqList.add(Requirement.testRequirement());
		return result;
	}
}