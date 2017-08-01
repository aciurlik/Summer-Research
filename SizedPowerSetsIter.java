import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Blurb written 8/1/2017
 * Last updated 8/1/2017
 * 
 * This gives a list of all possible permutations of predetermined subset.
 * Great for looking at Requirements and subRequirements. 
 * 
 *
 * @param <T>
 */
public class SizedPowerSetsIter<T> implements Iterator, java.io.Serializable {
	ArrayList<T> data;
	ArrayList<Integer> currentSubset;
	/**
	 * This gives all possible subsets of a given size
	 * @param input //The list of elements
	 * @param size //size of the subset. 
	 */
	public SizedPowerSetsIter(Iterable<T> input, int size){
		data = new ArrayList<T>();
		currentSubset = new ArrayList<Integer>(size);
		for(T datum : input){
			data.add(datum);
		}
		if(data.size() < size){
			throw new RuntimeException("Tried to make a powersetIter of size" + size + " on an iterable of size " + data.size());
		}
		for(int i = 0; i < size ; i ++){
			currentSubset.add(i);
		}
		currentSubset.set(size-1,  size - 2);
		//this ensures that the first call to next, which increments and then returns, 
		// will not 
	}
	@Override
	public boolean hasNext() {
		return incrementIndex() != -1;
	}
	/**
	 * This increments through the set. 
	 */
	@Override
	public HashSet<T> next() {
		// Shift currentSubset, then return the new result.
		int i = incrementIndex();
		int startPointer = currentSubset.get(i);
		startPointer ++; //increment the one that needs to be incremented.
		// after incrementing one pointer, every pointer to its right
		// should be shifted as far left as possible.
		for(int j = i; j < currentSubset.size() ; j ++){
			currentSubset.set(j, startPointer);
			startPointer ++;
		}
		return peek();
	}
	@Override
	public void remove() {
		
	}
	
	/**
	 * Find the index of currentSubset that needs to be incremented.
	 * If you think of the values in currentSubset as arrows pointing at
	 * the data, this is the last pointer that has a piece of unpointed
	 * data to its right.
	 */
	public int incrementIndex(){
		int dataIndex = data.size() - 1;
		int subsetIndex = currentSubset.size() - 1;
		while(subsetIndex >= 0 && currentSubset.get(subsetIndex) == dataIndex){
			dataIndex --;
			subsetIndex--;
		}
		return subsetIndex;
	}
	
	public HashSet<T> peek(){
		HashSet<T> result = new HashSet<>();
		for(int i : currentSubset){
			result.add(data.get(i));
		}
		return result;
	}
	
	public static void main(String[] args){
		int[] toTest = new int[]{1,2,3,4,5,6,7,8,9,10};
		ArrayList<Integer> data = new ArrayList<Integer>();
		for(int i : toTest){
			data.add(i);
		}
		SizedPowerSetsIter<Integer> testSet = new SizedPowerSetsIter<Integer>(data,3);
		
		for(int i = 0; i < 100 ; i ++){
			System.out.println(testSet.next());
		}
		
		while(testSet.hasNext()){
			System.out.println(testSet.next());
		}
	}

}
