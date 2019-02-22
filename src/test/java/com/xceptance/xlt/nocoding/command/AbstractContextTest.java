package com.xceptance.xlt.nocoding.command;

import java.util.Arrays;

import org.junit.After;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.xceptance.xlt.api.util.XltProperties;
import com.xceptance.xlt.nocoding.util.context.Context;
import com.xceptance.xlt.nocoding.util.context.DomContext;
import com.xceptance.xlt.nocoding.util.context.LightWeightContext;
import com.xceptance.xlt.nocoding.util.context.RequestContext;
import com.xceptance.xlt.nocoding.util.storage.DataStorage;

/**
 * Abstract class for all tests, which need a {@link Context}.
 *
 * @author ckeiner
 */
@RunWith(Parameterized.class)
public abstract class AbstractContextTest
{
    protected final Context<?> context;

    @Parameters(name = "{index}: {0}")
    public static Iterable<Context<?>[]> data()
    {
        return Arrays.asList(new Context<?>[][]
            {
              {
                new LightWeightContext(XltProperties.getInstance())
              },
              {
                new DomContext(XltProperties.getInstance())
              },
              {
                new RequestContext(XltProperties.getInstance())
              }
            });
    }

    public AbstractContextTest(final Context<?> context)
    {
        this.context = context;
    }

    @After
    public void clearStorage()
    {
        final DataStorage storage = context.getDataStorage();
        storage.getDefaultCookies().clear();
        storage.getDefaultHeaders().clear();
        storage.getDefaultItems().clear();
        storage.getDefaultParameters().clear();
        storage.getDefaultStatics().clear();
        storage.getVariables().clear();
        context.getWebClient().getCookieManager().clearCookies();

        if (context instanceof LightWeightContext)
        {
            ((LightWeightContext) context).setSgmlPage(null);
        }
        else if (context instanceof RequestContext)
        {
            ((RequestContext) context).setSgmlPage(null);
        }
    }
}
