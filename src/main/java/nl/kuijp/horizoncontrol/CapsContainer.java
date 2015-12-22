package nl.kuijp.horizoncontrol;

import java.util.Hashtable;
import java.util.Vector;

class CapsContainer {

    protected Hashtable<Integer, CapabilityInfo> infoMap;
    protected Vector<Integer> orderedList;

    public CapsContainer() {
        infoMap = new Hashtable<>(64, (float) 0.25);
        orderedList = new Vector<>(32, 8);
    }

    public void add(int code, String vendor, String name, String desc) {
        Integer key = code;
        infoMap.put(key, new CapabilityInfo(code, vendor, name, desc));
    }

    public boolean enable(CapabilityInfo other) {
        Integer key = other.getCode();
        CapabilityInfo capinfo = infoMap.get(key);
        if (capinfo == null)
            return false;

        boolean enabled = capinfo.enableIfEquals(other);
        if (enabled)
            orderedList.addElement(key);

        return enabled;
    }

    public int numEnabled() {
        return orderedList.size();
    }

    public int getByOrder(int idx) {
        int code;
        try {
            code = (orderedList.elementAt(idx));
        } catch (ArrayIndexOutOfBoundsException e) {
            code = 0;
        }
        return code;
    }
}

