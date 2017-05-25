import java.util.ArrayList;
import java.util.Collections;


public class Intervals<T extends Comparable<T>> {

	ArrayList<Interval<T>> intervals;

	public Intervals(){
		intervals = new ArrayList<Interval<T>>();
	}

	public Intervals(Interval<T>[] intervals){
		this();
		this.addIntervals(intervals);
	}

	public void addIntervals(Interval<T>[] toAdd){
		Collections.addAll(intervals, toAdd);
	}

	public void addInterval(Interval<T> toAdd){
		intervals.add(toAdd);
	}

	/**
	 * This can be made much faster if there is a 
	 * large number of intervals.
	 * @param other
	 * @return
	 */
	public boolean overlaps(Intervals<T> other){
		for (Interval<T> i : this.intervals){
			for (Interval<T> j : other.intervals){
				if(i.overlaps(j)){
					return true;
				}
			}
		}
		return false;
	}

	public static void main(String[] args){

		Time t1 = new Time(2000, 1, 1, 0,0);
		Time t2 = new Time(2000, 1, 2, 0,0);
		Time t3 = new Time(2000, 1, 3, 0,0);
		Time t4 = new Time(2000, 1, 4, 0,0);
		Time t5 = new Time(2000, 1, 5, 0,0);
		Time t6 = new Time(2000, 1, 6, 0,0);

		Interval<Time> i1 = new Interval<Time>(t1, t6);
		Interval<Time> i2 = new Interval<Time>(t2, t3);

		Interval<Time> i3 = new Interval<Time>(t4, t5);
		Interval<Time> i4 = new Interval<Time>(t6, t6);

		@SuppressWarnings("unchecked")
		Interval<Time>[] holder = (Interval<Time>[]) new Interval[2];

		holder[0] = i1;
		holder[1] = i2;
		Intervals<Time> c1 = new Intervals<Time>(holder);

		holder[0] = i3;
		holder[1] = i4;
		Intervals<Time> c2 = new Intervals<Time>(holder);

		System.out.println(c1.overlaps(c2));
		System.out.println(c2.overlaps(c1));



	}


}
