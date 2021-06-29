package com.aibel.mel.mel2aas.ottr;

import java.util.HashMap;
import java.util.Map;

public enum NS {
    OTTR("ottr", "http://ns.ottr.xyz/0.4/");

    public final String pf;
    public final String ns;

    NS(String prefix, String namespace) {
        this.pf = prefix;
        this.ns = namespace;
    }

    public static Map<String, String> getPrefixMap() {
        Map<String, String> map = new HashMap<String, String>();
        for (NS ns : values()) {
            map.put(ns.pf, ns.ns);
        }
        return map;
    }
}
