import java.util.HashSet;
import java.util.Hashtable;


/**
 * Blurb written: Before 7/1/2017
 * Last Updated: 7/26/2017
 * 
 * This class stores which requirements are able to 
 * work nicely with each other.
 * 
 * For example, no course may satisfy both the 
 * MTH elective requirement and the Applied math elective requirement 
 * at the same time. These two requirements do not work nicely with each
 * other - here we will call such requirements 'enemies'. This class stores
 * the 'are enemies' relation.
 * 
 * To test if two requirements are enemies, use the method doesPlayNice(R1, R2).
 * 
 * 
 * We think about this class as a graph where requirements r1 and r2 are
 * adjacent to iff r1 is enemies with r2.
 * 
 * There is a special case - some requirements are loners, which means they are
 * enemies by default. If a requirement is a loner, then the interpretation of 
 * the graph changes for that requirement. Now, any requirements it IS adjacent
 * to are NOT enemies - any requirements it's NOT adjacent to ARE enemies.
 * 
 * 
 * 
 * This class is in the FILE group of classes, and interfaces with the DATA group.
 * 
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
	 * If either r1 or r2 is a loner, instead declares that they can play nice.
	 * 
	 * Forces an undirected graph by putting in both edges
	 * r1-r2 and r2-r1.
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
	 * except those that it is adjacent to in the graph.
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
	 * Return a hash set of all the requirements in reqs that have an 
	 * enemy also in reqs.
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
