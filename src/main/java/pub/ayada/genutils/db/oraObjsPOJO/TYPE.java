package pub.ayada.genutils.db.oraObjsPOJO;

import java.util.ArrayList;

public class TYPE {
	private String NAME;
	private ArrayList<String> OBJS = new ArrayList<String>();

	public TYPE() {
	}

	public TYPE(String TypeName) {
		NAME = TypeName;
	}

	public TYPE(String TypeName, ArrayList<String> oBJS) {
		super();
		NAME = TypeName;
		OBJS = oBJS;
	}

	public TYPE(ArrayList<String> objs) {
		OBJS = objs;
	}

	public String getNAME() {
		return NAME;
	}

	public void setNAME(String nAME) {
		NAME = nAME;
	}

	public ArrayList<String> getObjs() {
		return OBJS;
	}

	public void setObjs(ArrayList<String> objs) {
		OBJS = objs;
	}

	public void addObj(String obj) {
		this.OBJS.add(obj);
	}

	public void addAllObj(ArrayList<String> objs) {
		this.OBJS.addAll(objs);
	}

	public boolean isEmpty() {
		if (this.OBJS == null || this.OBJS.size() > 0)
			return false;
		return true;
	}

}
