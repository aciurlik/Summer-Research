
public class TerminalRequirement extends Requirement {
	public Prefix p;
	public int number;
	
	
	public TerminalRequirement(Prefix p){
		this.p = p;
	}
	public TerminalRequirement(Prefix p, int number){
		this.p = p;
		this.number = number;
	}
	
	
	
	
	
	
	@Override
	public Requirement cloneRequirement(){
		return new TerminalRequirement(new Prefix(p.getSubject(), p.getNumber()), number);
	}
	
}
