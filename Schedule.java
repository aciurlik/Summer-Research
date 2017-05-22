package scheduler;

public class Schedule {
	int counter=0;

	public String getRequirement(int requirementNumber) {
		// TODO Auto-generated method stub
		return "Hey Look I work! :D #MasterProgrammer";
	}

	public boolean hasNextRequirement(int requirementNumber) {
		if(counter<3){  //put at four to test extra class addition 
		counter++;
		return true;
		
		}
		else{
			return false;
	}

}
}
