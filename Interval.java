
public class Interval<T extends Comparable<T>> implements Comparable<Interval<T>>{

	T start;
	T end;
	
	public Interval(T start, T end){
		this.start = start;
		this.end = end;
	}
	
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
			if(other.end.compareTo(this.start) >= 0){
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
			if(this.end.compareTo(other.start) >= 0){
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
