
import java.util.HashSet;


public class PrerequsiteException extends SchedulingException{
	HashSet<Prefix> needed;
	ScheduleElement courseWithPrereqs;
	public PrerequsiteException(HashSet<Prefix> needed, ScheduleElement courseWithPrereqs){
		this.needed = needed;
		this.courseWithPrereqs = courseWithPrereqs;
	}
}