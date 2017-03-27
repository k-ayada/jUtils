package pub.ayada.genutils.db.oraObjsPOJO;

import java.util.ArrayList;

import pub.ayada.genutils.db.oraObjsPOJO.OWNER;

public class OBJECTS {
    private ArrayList<OWNER> OBJS = new ArrayList<OWNER>();

	public OBJECTS() {
	}

	public OBJECTS(ArrayList<OWNER> oBJS) {
		super();
		OBJS = oBJS;
	}

	public ArrayList<OWNER> getOBJS() {
		return OBJS;
	}

	public void setOBJS(ArrayList<OWNER> oBJS) {
		OBJS = oBJS;
	}

	public void addOwner(OWNER obj) {
		this.OBJS.add(obj);
	}

}
