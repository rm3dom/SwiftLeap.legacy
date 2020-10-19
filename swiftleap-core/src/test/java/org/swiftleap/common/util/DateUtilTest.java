package org.swiftleap.common.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

public class DateUtilTest {

    private int getDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    private int getYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }


    @Test
    public void testMake() {
        Assert.assertEquals(55, DateUtil.make("55").getMinutes());
        Assert.assertEquals(1, DateUtil.make("155").getHours());
        Assert.assertEquals(11, DateUtil.make("1155").getHours());
        Assert.assertEquals(55, DateUtil.make("1155").getMinutes());
        Assert.assertEquals(55, DateUtil.make("11h55").getMinutes());
        Assert.assertEquals(11, DateUtil.make("11:55").getHours());
        Assert.assertEquals(11, DateUtil.make("11:55:01").getHours());
        Assert.assertEquals(1, DateUtil.make("11:55:01").getSeconds());

        Assert.assertEquals(2000, getYear(DateUtil.make("2000/01/01")));
        Assert.assertEquals(2000, getYear(DateUtil.make("20000101")));
        Assert.assertEquals(2000, getYear(DateUtil.make("01/01/2000")));

        Assert.assertEquals(2, getDay(DateUtil.make("2000/01/02")));
        Assert.assertEquals(2, getDay(DateUtil.make("20000102")));
        Assert.assertEquals(2, getDay(DateUtil.make("02/01/2000")));
    }
}
