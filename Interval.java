

/**
 * A class that represents an interval between two elements 
 * from a set of comparable objects.
 * it should always be true that start.compareTo(end) <= 0.
 * 
 * @author dannyrivers
 *
 */
public class Interval<T extends Comparable<T>> implements java.io.Serializable{
	T start;
	T end;

	public Interval(T start, T end){
		this.start = start;
		this.end = end;
	}
	
	
	/**
	 * check if these two intervals overlap where 
	 * endpoints don't count as overlaping 
	 * so (3, 3) does not overlap (3, 7).
	 * @param other
	 * @return
	 */
	public boolean overlaps(Interval<T> other){
		return overlaps(other, false);
	}

	/**
	 * 
	 * TODO 
	 * 
	 * Replace this with an intersection finding method.
	 * 
	 * Intersection finder (written by Lindsay)
	 * Given L1, R1, L2, R2
	 * if (R2 < L1) or (R1 < L2):
	 *   return noIntersection
	 * else:
	 *   return [max(L1, L2), min(R1, R2)]
	 * 
	 * 
	 * 
	 * Check if these two intervals overlap.
	 * if includeEndpoints, then the intervals (1,3) and (3,7)
	 *   do overlap. Otherwise, they don't.
	 * Note that (3, 3) and (3, 7) may or may not overlap.
	 * @param other
	 * @return
	 */
	public boolean overlaps(Interval<T> other, boolean includeEndpoints){
		//Imagine other as a fixed interval on a number line, 
		// with earlier times to the left. We'll imagine two scenarios:
		// either this is entirely to the right of other.start or 
		// this starts to the left of other.start.


		//this.start is after other.start, 
		// so we are to the right of the fixed other
		if(this.start.compareTo(other.start) >= 0){
			//if other.end is to the right of this.start
			// then there is overlap.
			if(includeEndpoints){
				//case with =
				if(this.start.compareTo(other.end) <= 0)
					return true;
				else
					return false;
			}
			else{
				//case without =
				if(this.start.compareTo(other.end) < 0)
					return true;
				else
					return false;
			}
		}
		else{
			//thisStartTime is before otherStartTime, 
			// so we are to the left of the fixed other
			//if thisEndTime is to the right of otherStartTime, 
			// then there is overlap.
			if (includeEndpoints){
				//case with =
				if(this.end.compareTo(other.start) >= 0)
					return true;
				else
					return false;
			}
			else{
				//case without =
				if(this.end.compareTo(other.start) > 0)
					return true;
				else
					return false;
			}
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
