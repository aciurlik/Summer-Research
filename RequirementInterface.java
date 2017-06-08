import java.util.ArrayList;
import java.util.HashSet;


public interface RequirementInterface {
	public RequirementInterface cloneRequirement();
	public HashSet<Prefix> fastestCompletionSet(HashSet<Prefix> taken);
	public double percentComplete(HashSet<Prefix> taken);
}
