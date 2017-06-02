
public class OverlapException extends SchedulingException {
	ScheduleElement i;
	ScheduleElement j;

	/**
	 * thrown when SemesterElements i and j overlap
	 * @param i
	 * @param j
	 */
	public OverlapException(ScheduleElement i, ScheduleElement j){
		super();
		this.i = i;
		this.j = j;
	}


}
