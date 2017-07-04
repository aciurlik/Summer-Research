import java.util.HashSet;
import java.util.Hashtable;


/**
 * This class stores any case of requirements that are 
 * unable to work nicely with each other.
 * 
 * For example, no course may satisfy both the 
 * MTH elective requirement and the Applied math elective requirement 
 * at the same time. This class stores that relation - the 
 * "doesn't play nice with" relation.
 * 
 * We think about this as a graph where requirements r1 and r2 are
 * adjacent to iff r1 doesn't play nice with r2.
 * 
 * @author dannyrivers
 *
 */
public class RequirementGraph implements java.io.Serializable {
	//We will use the edge list format for storing this graph, because
	// it will probably be sparse.
	static Hashtable<Requirement, HashSet<Requirement>> edges = new Hashtable<Requirement, HashSet<Requirement>>();
	static HashSet<Requirement> loners = new HashSet<Requirement>();
	
	/**
	 * Declares that r1 can't play nice with r2.
	 * 
	 * Add both edges.
	 * @param r1
	 * @param r2
	 */
	public static void putEdge(Requirement r1, Requirement r2){
		if(r1.equals(r2)){
			System.out.println("You're trying make two requirements into enemies, but they are equal! "+ r1 + ", " + r2);
		}
		HashSet<Requirement> outEdges1 = edges.get(r1);
		HashSet<Requirement> outEdges2 = edges.get(r2);
		if(outEdges1 == null){
			outEdges1 = new HashSet<Requirement>();
			edges.put(r1, outEdges1);
		}
		if(outEdges2 == null){
			outEdges2 = new HashSet<Requirement>();
			edges.put(r2, outEdges2);
		}
		outEdges1.add(r2);
		outEdges2.add(r1);
	}
	
	/**
	 * Set it so that this requirement is enemies with all other requirements,
	 * except 
	 * @param r
	 */
	public static void makeLoner(Requirement r){
		loners.add(r);
	}
	
	public static boolean isLoner(Requirement r){
		return loners.contains(r);
	}
	
	/**
	 * Check if r1 and r2 can share a course or not
	 */
	public static boolean doesPlayNice(Requirement r1, Requirement r2){
		boolean result = true;
		if(isLoner(r1) || isLoner(r2)){
			result = !result;
		}
		HashSet<Requirement> outEdges = edges.get(r1);
		if(outEdges == null){
			return result;
		}
		if(outEdges.contains(r2)){
			return !result;
		}
		return result;
	}
	
	/**
	 * Looks for enemy requirements in this set.
	 * If found, it returns the first pair of enemies found.
	 * Otherwise returns an empty list.
	 * @param reqs
	 * @return
	 */
	public static HashSet<Requirement> enemiesIn(HashSet<Requirement> reqs){
		HashSet<Requirement> result = new HashSet<Requirement>();
		for(Requirement r : reqs){
			for(Requirement r2 : reqs){
				if(!doesPlayNice(r, r2)){
					result.add(r);
					result.add(r2);
				}
			}
		}
		return result;
	}
}
