

/**
 * A class that represents a time interval between startTime and endTime.
 * startTime must be less than endTime, so
 * starTime.compareTo(endTime) <= 0 should always be true.
 * 
 * @author dannyrivers
 *
 */
public class Interval<T extends Comparable<T>> implements Comparable<Interval<T>>, java.io.Serializable{
	T start;
	T end;

	public Interval(T start, T end){
		this.start = start;
		this.end = end;
	}

	/**
	 * Check if these two intervals overlap.
	 * 
	 * If they just touch, they don't overlap.
	 * @param other
	 * @return
	 */
	public boolean overlaps(Interval<T> other){
		//Imagine other as a fixed time interval on a number line, 
		// with earlier times to the left. We'll imagine two scenarios:
		// either this is entirely to the right of otherStartTime or 
		// this starts to the left of otherStartTime.


		//thisStartTime is after otherStartTime, 
		// so we are to the right of the fixed other
		if(this.start.compareTo(other.start) >= 0){
			//if otherEndTime is to the right of thisStartTime
			// then there is overlap.
			//Otherwise, overlap is impossible.
			if(this.start.compareTo(other.end) < 0){
				// Alternatively, this code makes it so overlaps occur
				// when intervals just touch. other.end.compareTo(this.start) >= 0){
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
			if(this.end.compareTo(other.start) > 0){
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
	public int compareTo(Interval<T> o) {
		int first = this.start.compareTo(o.start);
		if(first == 0){
			return this.end.compareTo(o.end);
		}
		else{
			return first;
		}
	}
	/**
	 * Check if other is contained within this interval.
	 * 
	 * includeEndpoints is used for strict containment, as in the following example.
	 * 
	 * if includeEndpoints, then (3,8).contains( 3 ) is true,
	 *  if not includeEndpoints, then (3,8).contains( 3 ) is false,
	 * @param other
	 * @param includeEndpoints
	 * @return
	 */
	public boolean contains(T other, boolean includeEndpoints){
		if(includeEndpoints){
			return (this.start.compareTo(other)<=0 && this.end.compareTo(other)>=0);
		}
		else{
			return (this.start.compareTo(other)<0 && this.end.compareTo(other)>0);
		}
	}
	
	/**
	 * check if the other interval is contained in this one.
	 * 
	 * If includeEndpoints is false, then both endpoints of other must be 
	 * 		inside this. For example, (3,7) would not be contained in (3,8)
	 * 		because the 3 is not strictly contained.
	 * If includeEndpoints is true, then (3,7) would be contained in (3,8).
	 * @param other
	 * @return
	 */
	public boolean contains(Interval<T> other, boolean includeEndpoints){
		return (this.contains(other.start, includeEndpoints) && this.contains(other.end, includeEndpoints)); 
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

	public String toString(){
		return String.format("[%s,%s]", start.toString(), end.toString());
	}


}
