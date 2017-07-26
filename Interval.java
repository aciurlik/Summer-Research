

/**
 * Blurb written: 7/26/2017
 * Last updated: 7/26/2017
 * 
 * A class that represents an interval over an ordered metric space
 * 		(which just means the things in the space have a compareTo(other) and a
 * 		distanceFrom(other) method)
 * 
 * A good example is integers - in this case, intervals might be 
 * 	(3,7) or (3,5).
 * 
 * If an interval's start is less than its end, it is a valid interval. 
 * If its end is less than its start, then it is an invalid interval.
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
	 * by default, if they just touch, they don't overlap.
	 * @param other
	 * @return
	 */
	public boolean overlaps(Interval<T> other){
		return overlaps(other, false);
	}
	
	public boolean overlaps(Interval<T> other, boolean includeEndpoints){
		Interval<T> intersection = this.intersection(other);
		boolean result = intersection.isValid();
		if(includeEndpoints){
			return result;
		}
		else{
			return result && !intersection.isPoint();
		}
	}
	
	/**
	 * Return the intersection of this interval and other,
	 * where the intersection may not be a valid interval.
	 * For example, intersection (3, 5) and (3, 4) is (3,4),
	 * but intersection (3,5) and (2,2) is (3, 2) which is an 
	 * invalid interval.
	 * 
	 * If either this or other is invalid, then the result will also be invalid
	 * The proof is that, given any interval x and objects a and b, 
	 * then if x.intersection(a, b) = (c,d)  then c >= a and d <= b.
	 * If (a,b) is invalid to begin with, then so is (c,d).
	 * @param other
	 * @return
	 */
	public Interval<T> intersection(Interval<T> other){
		return new Interval<T>(max(this.start, other.start), min(this.end, other.end));
	}
	public T max(T t1, T t2){
		if(t1.compareTo(t2) > 0){
			return t1;
		}
		else{
			return t2;
		}
	}
	public T min(T t1, T t2){
		if(t1.compareTo(t2) < 0){
			return t1;
		}
		else{
			return t2;
		}
	}
	
	public boolean isValid(){
		return this.start.compareTo(this.end) <= 0;
	}
	
	public boolean isPoint(){
		return this.start.compareTo(this.end) == 0;
	}


	/**
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
	 * Check if this object is contained within this interval.
	 * 
	 * includeEndpoints is used for strict containment, as in the following example.
	 * 
	 * if includeEndpoints, then (3,8).contains( 3 ) is true,
	 *  if not includeEndpoints, then (3,8).contains( 3 ) is false,
	 * @param other
	 * @param includeEndpoints
	 * @return
	 */
	public boolean contains(T object, boolean includeEndpoints){
		if(includeEndpoints){
			return (this.start.compareTo(object)<=0 && this.end.compareTo(object)>=0);
		}
		else{
			return (this.start.compareTo(object)<0 && this.end.compareTo(object)>0);
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
	

	public String toString(){
		return String.format("(%s,%s)", start.toString(), end.toString());
	}
	
	
	public static void testIntervals(){
		
		//overlaps test
		testIntersections();
	}
	
	/**
	 * Test all possible cases for intersections,
	 * where the first interval is fixed and the second interval
	 * may consist of points inside, outside,
	 */
	public static void testIntersections(){

		int left = 3;
		int left2 = 4;
		int start = 5;
		
		int in = 6;
		int in2 = 7;
		
		int end = 8;
		int right = 9;
		int right2 = 10;
		
		
		//Test all combinations of newInterval( starts[i], ends[j])
		int[] starts = {left, start, in, end, right};
		int[] ends = {left, left2, start, in, in2, end, right, right2};
		
		Interval<Integer> t = new Interval<Integer>(start, end);
		
		for(int i : starts){
			for(int j : ends){
				Interval<Integer> test = new Interval<Integer> (i, j);
				Interval<Integer> intersection = test.intersection(t);
				System.out.println(test + ", intersection is " + intersection + "\t vd?:" + intersection.isValid() + " pt?:" + intersection.isPoint());
			}
		}
	}

	public static void main(String[] args){
		testIntervals();
	}


}