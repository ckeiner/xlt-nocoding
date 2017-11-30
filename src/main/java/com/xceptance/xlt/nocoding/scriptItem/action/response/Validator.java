package com.xceptance.xlt.nocoding.scriptItem.action.response;

import com.xceptance.xlt.api.util.XltLogger;
import com.xceptance.xlt.nocoding.scriptItem.action.response.selector.AbstractSelector;
import com.xceptance.xlt.nocoding.scriptItem.action.response.selector.RegexpSelector;
import com.xceptance.xlt.nocoding.scriptItem.action.response.validationMode.AbstractValidationMode;
import com.xceptance.xlt.nocoding.scriptItem.action.response.validationMode.ExistsValidator;
import com.xceptance.xlt.nocoding.util.Context;

/**
 * Uses an {@link AbstractSelector} and {@link AbstractValidationMode} to validate the result of the selection.
 * 
 * @author ckeiner
 */
public class Validator extends AbstractResponseItem
{

    /**
     * The name of the validation
     */
    private final String validationName;

    /**
     * The selector to use
     */
    private final AbstractSelector selector;

    /**
     * The validation method
     */
    private AbstractValidationMode mode;

    /**
     * Creates an instance of {@link Validator} that sets the {@link #validationName}, {@link #selector} and {@link #mode}.
     * 
     * @param validationName
     *            The name of the validation
     * @param selector
     *            The selector to use
     * @param mode
     *            The validation method
     */
    public Validator(final String validationName, final AbstractSelector selector, final AbstractValidationMode mode)
    {
        this.validationName = validationName;
        this.selector = selector;
        this.mode = mode;
    }

    /**
     * Executes the validator by setting {@link #group} if it is specified and {@link #selector} is a
     * {@link RegexpSelector}. The executes the {@link #selector}. Then, if {@link #mode} is null, it sets it to
     * {@link ExistsValidator}. Finally, it sets {@link AbstractValidationMode#setExpressionToValidate(java.util.List)} with
     * {@link AbstractSelector#getResult()} and executes the {@link #mode}.
     */
    @Override
    public void execute(final Context context) throws Exception
    {
        // Execute the selector
        selector.execute(context);

        // If we don't have a mode, then we simply want to confirm the existence of a solution
        if (mode == null)
        {
            mode = new ExistsValidator();
        }
        // Set the result of the execution as expression to validate in Validator
        mode.setExpressionToValidate(selector.getResult());
        // Try to validate and catch any Exception and AssertionErrors so the validationName can be added to the
        // Exception/AssertionError
        try
        {
            // Validate the solution of the selector
            mode.execute(context);
        }
        catch (final Exception e)
        {
            final String message = "\"" + validationName + "\" could not validate: " + e.getMessage();
            XltLogger.runTimeLogger.error(message);
            throw new Exception(message, e);
        }
        catch (final AssertionError e)
        {
            final String message = "\"" + validationName + "\" could not validate: " + e.getMessage();
            XltLogger.runTimeLogger.error(message);
            throw new AssertionError(message, e);
        }
    }

    public String getValidationName()
    {
        return validationName;
    }

    public AbstractSelector getSelector()
    {
        return selector;
    }

    public AbstractValidationMode getMode()
    {
        return mode;
    }

}
