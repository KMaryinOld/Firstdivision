package com.airsoft.goodwin.Linen;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class LinenHistoryRecord {
    public static final int LINEN_TYPE_RETURN = 0;
    public static final int LINEN_TYPE_RECEIPT = 1;

    public int type;
    public Calendar date;
    public Map<Integer, Integer> items;

    public LinenHistoryRecord() {
        items = new HashMap<>();
    }
}
