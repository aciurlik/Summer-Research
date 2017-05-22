import java.util.ArrayList;


public class Semester implements Comparable<Semester>{
	public SemesterDate sD;
	public ArrayList<ScheduleElement> elements;
	
	public Semester(SemesterDate sD){
		elements = new ArrayList<ScheduleElement>();
		this.sD = sD;
	}
	
	public ArrayList<ScheduleElement> getElements(){
		return elements;
	}
	
	public SemesterDate getDate(){
		return sD;
	}
	
	public void checkOverlap(){
		for (int i = 0; i < elements.size() ; i ++){
			for(int j = i; j < elements.size() ; j ++){
				
				if(elements.get(i) instanceof Course && elements.get(j)instanceof Course){
					throw new OverlapException(elements.get(i), elements.get(j));
				}
			}
		}
	}
	
	public int compareTo(Semester other){
		return this.sD.compareTo(other.sD);
	}
}
