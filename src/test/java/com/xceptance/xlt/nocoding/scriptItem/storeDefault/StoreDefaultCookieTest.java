package com.xceptance.xlt.nocoding.scriptItem.storeDefault;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.xceptance.xlt.nocoding.scriptItem.ScriptItem;
import com.xceptance.xlt.nocoding.util.Constants;

public class StoreDefaultCookieTest extends StoreDefaultTest
{

    @Test
    public void singleStore() throws Throwable
    {
        final ScriptItem store = new StoreDefaultCookie("name_1", "value_1");
        Assert.assertTrue(context.getDefaultCookies().get("name_1").isEmpty());
        store.execute(context);
        Assert.assertTrue(context.getDefaultCookies().get("name_1") != null && !context.getDefaultCookies().get("name_1").isEmpty());
        Assert.assertEquals("value_1", context.getDefaultCookies().get("name_1").get(0));
    }

    @Test
    public void deleteStore() throws Throwable
    {
        ScriptItem store = new StoreDefaultCookie("name_1", "value_1");
        Assert.assertTrue(context.getDefaultCookies().get("name_1").isEmpty());
        store.execute(context);
        Assert.assertTrue(context.getDefaultCookies().get("name_1") != null && !context.getDefaultCookies().get("name_1").isEmpty());
        Assert.assertEquals("value_1", context.getDefaultCookies().get("name_1").get(0));
        store = new StoreDefaultCookie("name_1", Constants.DELETE);
        store.execute(context);
        Assert.assertTrue(context.getDefaultCookies().get("name_1").isEmpty());
    }

    @Test
    public void deleteAllDefaultCookies() throws Throwable
    {
        final List<ScriptItem> store = new ArrayList<ScriptItem>();
        store.add(new StoreDefaultCookie("name_1", "value"));
        store.add(new StoreDefaultCookie("name_2", "value"));
        store.add(new StoreDefaultCookie("name_3", "value"));
        store.add(new StoreDefaultCookie("name_4", "value"));
        store.add(new StoreDefaultCookie("name_5", "value"));
        Assert.assertTrue(context.getDefaultCookies().get("param_1").isEmpty());
        int i = 1;
        for (final ScriptItem scriptItem : store)
        {
            scriptItem.execute(context);
            Assert.assertEquals("value", context.getDefaultCookies().get("name_" + i).get(0));
            i++;
        }
        final ScriptItem deleteIt = new StoreDefaultCookie(Constants.COOKIES, Constants.DELETE);
        deleteIt.execute(context);
        Assert.assertTrue(context.getDefaultCookies().getItems().isEmpty());
    }

}
