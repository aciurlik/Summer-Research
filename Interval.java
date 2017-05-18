

/**
 * A class that represents a time interval between startTime and endTime.
 * startTime must be less than endTime, so
 * starTime.compareTo(endTime) <= 0 should always be true.
 * 
 * @author dannyrivers
 *
 */
public class Interval<T> implements Comparable<Interval<T>>{
	Time startTime;
	Time endTime;
	
	public Interval(Time startTime, Time endTime){
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public boolean overlaps(Interval<T> other){
		//Imagine other as a fixed time interval on a number line, 
		// with earlier times to the left. We'll imagine two scenarios:
		// either this is entirely to the right of otherStartTime or 
		// this starts to the left of otherStartTime.
		
		
		//thisStartTime is after otherStartTime, 
		// so we are to the right of the fixed other
		if(this.startTime.compareTo(other.startTime) >= 0){
			//if otherEndTime is to the right of thisStartTime
			// then there is overlap.
			//Otherwise, overlap is impossible.
			if(other.endTime.compareTo(this.startTime) >= 0){
				return true;
			}
			else{
				return false;
			}
		}
		else{
			//thisStartTime is before otherStartTime, 
			// so we are to the left of the fixed other
			//if thisEndTime is to the right of otherStartTime, 
			// then there is overlap.
			//Otherwise, overlap is impossible.
			if(this.endTime.compareTo(other.startTime) >= 0){
				return true;
			}
			else{
				return false;
			}
		}
	}


	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 * 
	 * Compares based on start time first, smaller start times, and 
	 *   in the case of ties compares end times, with small 
	 *   end times first.
	 */
	@Override
	public int compareTo(Interval o) {
		int first = this.startTime.compareTo(o.startTime);
		if(first == 0){
			return this.endTime.compareTo(o.endTime);
		}
		else{
			return first;
		}
	}
	
	public static void main(String[] args){
		Time t1 = new Time(2000, 1, 1, 0,0);
		Time t2 = new Time(2000, 1, 2, 0,0);
		Time t3 = new Time(2000, 1, 3, 0,0);
		Time t4 = new Time(2000, 1, 4, 0,0);
		
		Interval<Time> t = new Interval<Time>(t2, t4);
		Interval<Time> v = new Interval<Time>(t1, t1);
		System.out.println(v.overlaps(t));
	}
	

}
