package com.game.entity;

import com.game.controller.PlayerOrder;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class PlayerUtils {
    public static final Integer MAX_NAME_LENGTH = 12;
    public static final Integer MAX_TITLE_LENGTH = 30;
    public static final Integer MAX_EXPERIENCE = 10000000;
    public static final Date MIN_BIRTHDAY_DATE = new GregorianCalendar(2000, GregorianCalendar.JANUARY,1).getTime();
    public static final Date MAX_BIRTHDAY_DATE = new GregorianCalendar(3000, GregorianCalendar.JANUARY,1).getTime();
    public static final Long MIN_BIRTHDAY_LONG = MIN_BIRTHDAY_DATE.getTime();
    public static final Long MAX_BIRTHDAY_LONG = MAX_BIRTHDAY_DATE.getTime();
    public static final Integer MAX_LEVEL = (int) (Math.sqrt(2500 + 200 * MAX_EXPERIENCE) - 50) / 2;

    public static final Map<String, String> params = new HashMap<>();

    static {
        params.put("name","");
        params.put("title","");
        params.put("race","");
        params.put("profession","");
        params.put("after",Long.toString(MIN_BIRTHDAY_LONG));
        params.put("before",Long.toString(MAX_BIRTHDAY_LONG));
        params.put("banned","");
        params.put("minExperience","0");
        params.put("maxExperience",Integer.toString(MAX_EXPERIENCE));
        params.put("minLevel","0");
        params.put("maxLevel",Integer.toString(MAX_LEVEL));
        params.put("order", PlayerOrder.ID.getFieldName());
        params.put("pageNumber","0");
        params.put("pageSize","3");
    }

}
