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
public class RequirementGraph {
	//We will use the edge list format for storing this graph, because
	// it will probably be sparse.
	static Hashtable<Requirement, HashSet<Requirement>> edges = new Hashtable<Requirement, HashSet<Requirement>>();
	
	/**
	 * Declares that r1 can't play nice with r2.
	 * 
	 * Add both edges.
	 * @param r1
	 * @param r2
	 */
	public static void putEdge(Requirement r1, Requirement r2){
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
	 * Check if r1 and r2 can share a course or not
	 */
	public static boolean doesPlaysNice(Requirement r1, Requirement r2){
		HashSet<Requirement> outEdges = edges.get(r1);
		if(outEdges == null){
			return true;
		}
		if(outEdges.contains(r2)){
			return false;
		}
		return true;
	}
}
