import java.util.ArrayList;


public class Schedule {
	ArrayList<Requirement> reqList;
	
	
	public static Schedule testSchedule(){
		Schedule result = new Schedule();
		result.reqList.add(Requirement.testRequirement());
		result.reqList.add(Requirement.testRequirement());
		return result;
	}
	
	public Schedule(){
		this.reqList = new ArrayList<Requirement>();
	}
	
	
	
	
	
	
	
	
	
	public ArrayList<Requirement> getRequirementsList(){
		return reqList;
	}
	
	public int getCreditHoursComplete(){
		return 5;
	}
}
