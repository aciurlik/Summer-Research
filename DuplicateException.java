
public class DuplicateException extends SchedulingException {
	ScheduleElement e1;
	ScheduleElement e2;
	public DuplicateException(ScheduleElement e1, ScheduleElement e2){
		super();
		this.e1 = e1;
		this.e2 = e2;
	}

}
