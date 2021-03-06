package com.xceptance.xlt.nocoding.command.action.response.validator;

import java.io.Serializable;
import java.util.List;

import com.xceptance.xlt.nocoding.util.context.Context;

/**
 * The abstract class for every response item that specifies how to validate the {@link #expressionToValidate}.
 *
 * @author ckeiner
 */
public abstract class AbstractValidator implements Serializable
{

    /**
     * The expression which is to be validated
     */
    private List<String> expressionToValidate;

    /**
     * Executes the validation method. Therefore, this method should use at least one assertion.
     *
     * @param context
     *            The {@link Context} to use
     */
    public abstract void execute(Context<?> context);

    public List<String> getExpressionToValidate()
    {
        return expressionToValidate;
    }

    public void setExpressionToValidate(final List<String> expressionToValidate)
    {
        this.expressionToValidate = expressionToValidate;
    }

    /**
     * Resolves values.
     *
     * @param context
     *            The {@link Context} to use
     */
    protected abstract void resolveValues(final Context<?> context);

}
