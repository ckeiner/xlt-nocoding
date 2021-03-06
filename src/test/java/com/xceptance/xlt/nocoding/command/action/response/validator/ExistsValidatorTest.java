package com.xceptance.xlt.nocoding.command.action.response.validator;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.xceptance.xlt.nocoding.util.context.Context;

/**
 * Tests {@link ExistsValidator}
 *
 * @author ckeiner
 */
public class ExistsValidatorTest extends ValidationMethodTest
{

    public ExistsValidatorTest(final Context<?> context)
    {
        super(context);
    }

    /**
     * Verifies {@link ExistsValidator} validates that the result list has an item in it
     *
     * @throws Exception
     */
    @Test
    public void testExistsValidator() throws Exception
    {
        final List<String> result = new ArrayList<>();
        result.add("test");
        final AbstractValidator method = new ExistsValidator();
        method.setExpressionToValidate(result);
        method.execute(context);
    }

    /**
     * Verifies {@link ExistsValidator} throws an {@link AssertionError} if the result list is null
     *
     * @throws Exception
     */
    @Test(expected = AssertionError.class)
    public void testExistsValidatorNull() throws Exception
    {
        final AbstractValidator method = new ExistsValidator();
        method.setExpressionToValidate(null);
        method.execute(context);
    }

    /**
     * Verifies {@link ExistsValidator} throws an {@link AssertionError} if the result list is empty
     *
     * @throws Exception
     */
    @Test(expected = AssertionError.class)
    public void testExistsValidatorEmptyList() throws Exception
    {
        final AbstractValidator method = new ExistsValidator();
        method.setExpressionToValidate(new ArrayList<String>());
        method.execute(context);
    }

}
