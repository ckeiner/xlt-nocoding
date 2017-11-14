package com.xceptance.xlt.nocoding.parser.yamlParser.scriptItems.actionItems.response.validators;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.xceptance.xlt.nocoding.scriptItem.action.response.validators.AbstractValidator;
import com.xceptance.xlt.nocoding.scriptItem.action.response.validators.CountValidator;
import com.xceptance.xlt.nocoding.scriptItem.action.response.validators.ExistsValidator;
import com.xceptance.xlt.nocoding.scriptItem.action.response.validators.MatchesValidator;
import com.xceptance.xlt.nocoding.scriptItem.action.response.validators.TextValidator;
import com.xceptance.xlt.nocoding.util.Constants;
import com.xceptance.xlt.nocoding.util.ParserUtils;

public class ValidationModeParser
{
    final Iterator<String> iterator;

    public ValidationModeParser(final Iterator<String> iterator)
    {
        this.iterator = iterator;
    }

    public AbstractValidator parse(final JsonNode node)
    {
        AbstractValidator validator = null;

        if (iterator.hasNext())
        {
            final String nextExpression = iterator.next();
            final String validationExpression = ParserUtils.readValue(node, nextExpression);
            if (nextExpression.equals(Constants.MATCHES))
            {
                String group = null;
                if (iterator.hasNext())
                {
                    group = ParserUtils.readValue(node, iterator.next());
                }
                validator = new MatchesValidator(validationExpression, group);
            }
            else if (nextExpression.equals(Constants.TEXT))
            {
                String group = null;
                if (iterator.hasNext())
                {
                    group = ParserUtils.readValue(node, iterator.next());
                }
                validator = new TextValidator(validationExpression, group);
            }
            else if (nextExpression.equals(Constants.COUNT))
            {
                if (iterator.hasNext())
                {
                    throw new IllegalArgumentException("Unexpected item " + iterator.next());
                }
                validator = new CountValidator(validationExpression);
            }
        }

        else
        {
            validator = new ExistsValidator();
        }

        return validator;
    }

}
