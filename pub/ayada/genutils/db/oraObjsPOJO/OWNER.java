package pub.ayada.genutils.db.oraObjsPOJO;

import java.util.ArrayList;

import pub.ayada.genutils.db.oraObjsPOJO.TYPE;

public class OWNER {
	private String NAME;
	private ArrayList<TYPE> OBJS = new ArrayList<TYPE>();

	public OWNER() {
	}

	public OWNER(String TypeName) {
		NAME = TypeName;
	}

	public OWNER(String TypeName, ArrayList<TYPE> Type) {
		NAME = TypeName;
		OBJS = Type;
	}

	public String getNAME() {
		return NAME;
	}

	public void setNAME(String nAME) {
		NAME = nAME;
	}

	public ArrayList<TYPE> getOBJS() {
		return OBJS;
	}

	public void setOBJS(ArrayList<TYPE> oBJS) {
		OBJS = oBJS;
	}

	public void addType(TYPE obj) {
		this.OBJS.add(obj);
	}

	public boolean isEmpty() {
		if (OBJS == null || OBJS.size() > 0)
			return false;
		return true;
	}

}
