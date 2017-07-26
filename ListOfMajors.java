import java.util.ArrayList;

/**
 * Blurb written: 7/26/2017
 * Last updated: 7/26/2017
 * 
 * This class represents the entire list of majors. A listOfMajors is usually
 * created by the FileHandler method readMajorsFrom(File).
 * 
 * Every major used by the program should be loaded into completeMajorsList.
 * 
 * It includes a few filtering methods to get certain types of majors,
 *    like getting tracks.

 * 
 * ListOfMajors is in the DATA group of classes, and interfaces with the FILE group.
 *
 */
public class ListOfMajors {
	private ArrayList<Major> completeMajorsList = new ArrayList<Major>();

	public ArrayList<Major> getCompleteMajorsList() {
		return completeMajorsList;
	}
	public ListOfMajors(){
		this.completeMajorsList = new ArrayList<Major>();
	}
	
	////////////////////////
	////////////////////////
	/////List methods
	////////////////////////
	////////////////////////
	@SuppressWarnings("unused")
	private boolean ___ListMethods_________;
	
	public boolean add(Major m){
		return completeMajorsList.add(m);
	}

	public void addAt(Major m, int i){
		completeMajorsList.add(i, m);
	}

	public int getSize() {
		return completeMajorsList.size();
	}

	public Major get(int i){
		Major m = completeMajorsList.get(i);
		return m;
	}

	
	////////////////////////
	////////////////////////
	/////Getters
	////////////////////////
	////////////////////////
	@SuppressWarnings("unused")
	private boolean ___Getters_________;
	
	
	/**
	 * return an ordered list of the names of the majors in this iterable.
	 * @param m
	 * @return
	 */
	public ArrayList<String> selectNames(Iterable<Major> m){
		ArrayList<String> list = new ArrayList<String>();
		for (Major major: m){
			list.add(major.name);
		}
		return list;
	}
	
	


	/**
	 * Get the majors that have majorType = major
	 * (as opposed to track or minor)
	 * @return
	 */
	public ArrayList<Major> getGUIMajors() {
		ArrayList<Major> majorGUI = new ArrayList<Major>();
		for(Major m: completeMajorsList){
			if (m.isType(Major.NORMAL_MAJOR)){
				majorGUI.add(m);
			}
		}
		return majorGUI;
	}
	


	/**
	 * Get the majors that have majorType = minor
	 * @return
	 */
	public ArrayList<Major> getGUIMinor() {
		ArrayList<Major> minorGUI = new ArrayList<Major>();
		for(Major m: completeMajorsList){
			if (m.isType(Major.MINOR)){
				minorGUI.add(m);
			}

		}
		return minorGUI;
	}


	/**
	 * Get the majors that have majorType = track
	 * @return
	 */
	public ArrayList<Major> getGUITrack() {
		ArrayList<Major> trackGUI = new ArrayList<Major>();
		for(Major m: completeMajorsList){
			if (m.isType(Major.TRACK)){
				trackGUI.add(m);
			}

		}
		return trackGUI;
	}


	/**
	 * Find the first major that has the given name.
	 * 
	 * May return null if the name is not found.
	 * @param s
	 * @return
	 */
	public  Major getMajor(String s){
		if(s == null){
			return null;
		}
		for(Major m : completeMajorsList){
			if(s.equals(m.name)){
				return m;
			}
		}
		return null;
	}
}
