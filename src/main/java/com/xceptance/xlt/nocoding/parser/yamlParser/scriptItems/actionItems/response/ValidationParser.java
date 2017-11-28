package com.xceptance.xlt.nocoding.parser.yamlParser.scriptItems.actionItems.response;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.xceptance.xlt.api.util.XltLogger;
import com.xceptance.xlt.nocoding.parser.yamlParser.scriptItems.actionItems.response.selector.SelectorParser;
import com.xceptance.xlt.nocoding.parser.yamlParser.scriptItems.actionItems.response.validators.ValidationModeParser;
import com.xceptance.xlt.nocoding.scriptItem.action.response.AbstractResponseItem;
import com.xceptance.xlt.nocoding.scriptItem.action.response.Validator;
import com.xceptance.xlt.nocoding.scriptItem.action.response.selector.AbstractSelector;
import com.xceptance.xlt.nocoding.scriptItem.action.response.validationMode.AbstractValidationMode;
import com.xceptance.xlt.nocoding.util.Constants;
import com.xceptance.xlt.nocoding.util.ParserUtils;

/**
 * Parses the validation block to a {@link List}<{@link AbstractResponseItem}> which consists of {@link Validator}.
 * 
 * @author ckeiner
 */
public class ValidationParser
{

    /**
     * Parses the validation items in the response block to List<AbstractValidator> which consists of {@link Validator}.
     * 
     * @param node
     *            The node the item starts at
     * @return A List<AbstractValidator> which consists of {@link Validator}.
     * @throws IllegalArgumentException
     */
    public List<Validator> parse(final JsonNode node) throws IllegalArgumentException
    {
        // Verify that an array was used and not an object
        if (!(node instanceof ArrayNode))
        {
            throw new IllegalArgumentException("Expected ArrayNode in the validate block but was " + node.getClass().getSimpleName());
        }
        // Initialize variables
        final List<Validator> validator = new ArrayList<Validator>();
        String validationName = null;

        // Get an iterator over the elements
        final Iterator<JsonNode> iterator = node.elements();

        // Iterate over the elements
        while (iterator.hasNext())
        {
            // Get the next element
            final JsonNode current = iterator.next();
            // Get the fieldNames
            final Iterator<String> fieldName = current.fieldNames();

            // Iterate over the fieldNames
            while (fieldName.hasNext())
            {
                // The current fieldName is the name of the validation
                validationName = fieldName.next();
                String group = null;
                AbstractSelector selector = null;
                AbstractValidationMode validation = null;

                /*
                 * Substructure of a validation
                 */

                // Get the substructure
                final JsonNode validationContent = current.get(validationName);
                // Verify that an object was used and not an array
                if (!(validationContent instanceof ObjectNode))
                {
                    throw new IllegalArgumentException("Expected ObjectNode after the validation name, " + validationName + ", but was "
                                                       + node.getClass().getSimpleName());
                }

                // And get an iterator over the fieldNames
                final Iterator<String> name = validationContent.fieldNames();
                // Iterate over the fieldNames of the substructure
                while (name.hasNext())
                {
                    // Get the next fieldName
                    final String nextName = name.next();

                    // If it is a permitted selection mode
                    if (Constants.isPermittedSelectionMode(nextName))
                    {
                        // Parse the selector
                        selector = new SelectorParser(nextName).parse(validationContent);
                    }
                    // If it is a permitted validation mode
                    else if (Constants.isPermittedValidationMode(nextName))
                    {
                        // Parse the validation mode
                        validation = new ValidationModeParser(nextName).parse(validationContent);
                    }
                    // If it is group
                    else if (nextName.equals(Constants.GROUP))
                    {
                        // Store the value in group
                        group = ParserUtils.readValue(validationContent, nextName);
                    }
                    // If it is none of the above, nextName was not a permitted validation item
                    else
                    {
                        throw new IllegalArgumentException("Unknown Validation Item " + nextName);
                    }
                }
                // Add the new validator to the validator list
                validator.add(new Validator(validationName, selector, validation, group));
                // Print a debug statement
                XltLogger.runTimeLogger.debug("Added " + validationName + " to Validations");
            }
        }
        // Return all validations
        return validator;
    }

}
