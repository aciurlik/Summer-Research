import java.util.ArrayList;


public class Prereq implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Requirement r;
	private Prefix p;
	
	public Prereq(Requirement r, Prefix p){
		this.r = r;
		this.p = p;
	}
	
	public Requirement getRequirement(){
		return this.r;
	}
	
	public Prefix getPrefix(){
		return this.p;
	}
	
	public void updateOn(ArrayList<ScheduleElement> taken){
		//remove any elements that have prefix p from taken.
		ArrayList<ScheduleElement> toRemove = new ArrayList<ScheduleElement>();
		for(ScheduleElement e : taken){
			if(e.getPrefix() != null && e.getPrefix().equals(p)){
				toRemove.add(e);
			}
		}
		taken.removeAll(toRemove);
		
		//Update on the new array list.
		this.r.updateAllStoredValues(taken);
	}
	
}
