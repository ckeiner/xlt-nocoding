package com.xceptance.xlt.nocoding.scriptItem.action.response.validationMode;

import org.junit.Assert;

import com.xceptance.xlt.nocoding.util.Context;

public class CountValidator extends AbstractValidationMode
{
    private String count;

    public CountValidator(final String count)
    {
        this.count = count;
    }

    @Override
    public void execute(final Context context) throws Exception
    {
        // Resolve values
        resolveValues(context);
        final Integer count = Integer.parseInt(this.count);
        if (getExpressionToValidate() == null)
        {
            throw new IllegalStateException("Result list is null");
        }
        // Assert that the amount of results is the same as the specified one
        Assert.assertTrue("Expected " + this.count + " matches but found " + count.toString() + " matches",
                          count.equals(getExpressionToValidate().size()));
    }

    @Override
    protected void resolveValues(final Context context)
    {
        count = context.resolveString(count);
    }

}
