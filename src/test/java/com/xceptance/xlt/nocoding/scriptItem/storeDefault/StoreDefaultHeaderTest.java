package com.xceptance.xlt.nocoding.scriptItem.storeDefault;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.xceptance.xlt.nocoding.scriptItem.ScriptItem;
import com.xceptance.xlt.nocoding.util.Constants;

/**
 * Tests {@link StoreDefaultHeader}
 * 
 * @author ckeiner
 */
public class StoreDefaultHeaderTest extends StoreDefaultTest
{
    /**
     * Verifies {@link StoreDefaultHeader} can store one default header
     * 
     * @throws Throwable
     */
    @Test
    public void singleStore() throws Throwable
    {
        final ScriptItem store = new StoreDefaultHeader("header_1", "value");
        Assert.assertNull(context.getDefaultHeaders().get("header_1"));
        store.execute(context);
        Assert.assertEquals("value", context.getDefaultHeaders().get("header_1"));
    }

    /**
     * Verifies {@link StoreDefaultHeader} can delete a specified header
     * 
     * @throws Throwable
     */
    @Test
    public void deleteStore() throws Throwable
    {
        ScriptItem store = new StoreDefaultHeader("header_1", "value");
        Assert.assertNull(context.getDefaultHeaders().get("header_1"));
        store.execute(context);
        Assert.assertEquals("value", context.getDefaultHeaders().get("header_1"));
        store = new StoreDefaultHeader("header_1", Constants.DELETE);
        store.execute(context);
        Assert.assertNull(context.getDefaultHeaders().get("header_1"));
    }

    /**
     * Verifies {@link StoreDefaultHeader} can delete all headers
     * 
     * @throws Throwable
     */
    @Test
    public void deleteAllDefaultHeaders() throws Throwable
    {
        final List<ScriptItem> store = new ArrayList<ScriptItem>();
        store.add(new StoreDefaultHeader("header_1", "value"));
        store.add(new StoreDefaultHeader("header_2", "value"));
        store.add(new StoreDefaultHeader("header_3", "value"));
        store.add(new StoreDefaultHeader("header_4", "value"));
        store.add(new StoreDefaultHeader("header_5", "value"));
        Assert.assertNull(context.getDefaultHeaders().get("header_1"));
        int i = 1;
        for (final ScriptItem scriptItem : store)
        {
            scriptItem.execute(context);
            Assert.assertEquals("value", context.getDefaultHeaders().get("header_" + i));
            i++;
        }
        final ScriptItem deleteIt = new StoreDefaultHeader(Constants.HEADERS, Constants.DELETE);
        deleteIt.execute(context);
        Assert.assertNull(context.getDefaultHeaders().get("header_1"));
        Assert.assertTrue(context.getDefaultHeaders().getItems().isEmpty());
    }

    /**
     * Verifies headers stored via {@link StoreDefaultHeader} are stored case insensitive
     * 
     * @throws Throwable
     */
    @Test
    public void storeCaseInsensitiveHeaders() throws Throwable
    {
        final StoreDefault item1 = new StoreDefaultHeader("heAder_1", "heAder_1");
        final StoreDefault item2 = new StoreDefaultHeader("header_1", "header_1");
        Assert.assertNull(context.getDefaultHeaders().get("header_1"));
        item1.execute(context);
        Assert.assertEquals("heAder_1", context.getDefaultHeaders().get("header_1"));
        item2.execute(context);
        Assert.assertEquals("header_1", context.getDefaultHeaders().get("header_1"));
    }

}
