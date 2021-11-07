package cc.nnproject.json;

public abstract class AbstractJSON {
	
	public abstract void clear();
	public abstract int size();
	public abstract String toString();
	protected abstract String build();
	public final String format() {
		if(!JSON.build_functions) return "";
		else return format(0);
	}
	protected abstract String format(int l);

}
