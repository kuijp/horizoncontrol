package nl.kuijp.horizoncontrol;

import java.util.HashMap;
import java.util.Map;

public class Keys {

    public static final int KEY_POWER = 0xe000;
    public static final int KEY_OK = 0xe001;
    public static final int KEY_BACK = 0xe002;
    public static final int KEY_CHAN_UP = 0xe006;
    public static final int KEY_CHAN_DWN = 0xe007;
    public static final int KEY_HELP = 0xe009;
    public static final int KEY_MENU = 0xe00a;
    public static final int KEY_GUIDE = 0xe00b;
    public static final int KEY_INFO = 0xe00e;
    public static final int KEY_TEXT = 0xe00f;
    public static final int KEY_MENU1 = 0xe011;
    public static final int KEY_MENU2 = 0xe015;
    public static final int KEY_DPAD_UP = 0xe100;
    public static final int KEY_DPAD_DOWN = 0xe101;
    public static final int KEY_DPAD_LEFT = 0xe102;
    public static final int KEY_DPAD_RIGHT = 0xe103;
    public static final int KEY_NUM_0 = 0xe300;
    public static final int KEY_NUM_1 = 0xe301;
    public static final int KEY_NUM_2 = 0xe302;
    public static final int KEY_NUM_3 = 0xe303;
    public static final int KEY_NUM_4 = 0xe304;
    public static final int KEY_NUM_5 = 0xe305;
    public static final int KEY_NUM_6 = 0xe306;
    public static final int KEY_NUM_7 = 0xe307;
    public static final int KEY_NUM_8 = 0xe308;
    public static final int KEY_NUM_9 = 0xe309;
    public static final int KEY_PAUSE = 0xe400;
    public static final int KEY_STOP = 0xe402;
    public static final int KEY_RECORD = 0xe403;
    public static final int KEY_FWD = 0xe405;
    public static final int KEY_RWD = 0xe407;
    public static final int KEY_MENU3 = 0xef00;
    public static final int KEY_TIMESHIFT_INFO = 0xef06;    // TIMESHIFT INFO
    public static final int KEY_POWER2 = 0xef15;  // POWER
    public static final int KEY_ID = 0xef16;    // ID
    public static final int KEY_RC_PAIR = 0xef17;  // RC PAIRING
    public static final int KEY_TIMINGS = 0xef19;  // TIMINGS
    public static final int KEY_ONDEMAND = 0xef28;
    public static final int KEY_DVR = 0xef29;
    public static final int KEY_TV = 0xef2a;
    public static Map<String, Integer> keyMap = new HashMap();

    static {
        keyMap.put("POWER", KEY_POWER);
        keyMap.put("OK", KEY_OK);
        keyMap.put("BACK", KEY_BACK);
        keyMap.put("CHANNEL_UP", KEY_CHAN_UP);
        keyMap.put("CHANNEL_DOWN", KEY_CHAN_DWN);
        keyMap.put("HELP", KEY_HELP);
        keyMap.put("MENU", KEY_MENU);
        keyMap.put("GUIDE", KEY_GUIDE);
        keyMap.put("INFO", KEY_INFO);
        keyMap.put("TEXT", KEY_TEXT);
        keyMap.put("MENU1", KEY_MENU1);
        keyMap.put("MENU2", KEY_MENU2);
        keyMap.put("DPAD_UP", KEY_DPAD_UP);
        keyMap.put("DPAD_DOWN", KEY_DPAD_DOWN);
        keyMap.put("DPAD_LEFT", KEY_DPAD_LEFT);
        keyMap.put("DPAD_RIGHT", KEY_DPAD_RIGHT);
        keyMap.put("0", KEY_NUM_0);
        keyMap.put("1", KEY_NUM_1);
        keyMap.put("2", KEY_NUM_2);
        keyMap.put("3", KEY_NUM_3);
        keyMap.put("4", KEY_NUM_4);
        keyMap.put("5", KEY_NUM_5);
        keyMap.put("6", KEY_NUM_6);
        keyMap.put("7", KEY_NUM_7);
        keyMap.put("8", KEY_NUM_8);
        keyMap.put("9", KEY_NUM_9);
        keyMap.put("PAUSE", KEY_PAUSE);
        keyMap.put("STOP", KEY_STOP);
        keyMap.put("RECORD", KEY_RECORD);
        keyMap.put("FWD", KEY_FWD);
        keyMap.put("RWD", KEY_RWD);
        keyMap.put("MENU3", KEY_MENU3);
        keyMap.put("TS_INFO", KEY_TIMESHIFT_INFO);
        keyMap.put("POWER2", KEY_POWER2);
        keyMap.put("ID", KEY_ID);
        keyMap.put("RC_PAIR", KEY_RC_PAIR);
        keyMap.put("TIMINGS", KEY_TIMINGS);
        keyMap.put("ONDEMAND", KEY_ONDEMAND);
        keyMap.put("DVR", KEY_DVR);
        keyMap.put("TV", KEY_TV);
    }

    public static  int getByName(final String name) {
        return keyMap.get(name.toUpperCase());
    }
}
