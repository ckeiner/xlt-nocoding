package com.xceptance.xlt.nocoding.parser.csv;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.xceptance.xlt.nocoding.parser.Parser;
import com.xceptance.xlt.nocoding.parser.csv.scriptItems.ActionParser;
import com.xceptance.xlt.nocoding.parser.csv.scriptItems.StaticSubrequestParser;
import com.xceptance.xlt.nocoding.parser.csv.scriptItems.XhrSubrequestParser;
import com.xceptance.xlt.nocoding.scriptItem.ScriptItem;
import com.xceptance.xlt.nocoding.scriptItem.action.Action;
import com.xceptance.xlt.nocoding.scriptItem.action.subrequest.StaticSubrequest;

/**
 * Reads a CSV file, and generates a list filled with {@link ScriptItem}s out of the CSV file.
 * 
 * @author ckeiner
 */
public class CsvParser implements Parser
{

    /**
     * Parses the content of the file at <code>pathToFile</code> to a list of {@link ScriptItem}s
     * 
     * @param pathToFile
     *            The String that describes the path to the file
     * @return A list of {@link ScriptItem}s
     * @throws IOException
     *             if an I/O error occurs during creating the reader, parsing the file or closing the parser
     */
    @Override
    public List<ScriptItem> parse(final String pathToFile) throws IOException
    {
        // Initialize variables
        final List<ScriptItem> scriptItems = new ArrayList<ScriptItem>();
        Action lastAction = null;
        StaticSubrequest lastStatic = null;

        // Create a Reader based on the file
        final Reader reader = createReader(pathToFile);
        // Create a CSVParser based on the Reader
        final CSVParser parser = new CSVParser(reader, CsvConstants.CSV_FORMAT.withFirstRecordAsHeader());
        // Read all records
        final List<CSVRecord> records = parser.getRecords();
        // Try to validate all headers are allowed
        try
        {
            validateHeader(parser.getHeaderMap().keySet());
        }
        finally
        {
            // Close the parser
            parser.close();
        }

        for (final CSVRecord record : records)
        {
            // If the record is consistent, which means that all headers have a value
            if (record.isConsistent())
            {
                // Get the type of the record
                String type = CsvConstants.TYPE_DEFAULT;
                if (record.isMapped(CsvConstants.TYPE))
                {
                    type = record.get(CsvConstants.TYPE);
                }

                // Differentiate between Action, Static and Xhr Requests
                switch (type)
                {
                    case CsvConstants.TYPE_ACTION:
                        // Reset the last static subrequest
                        lastStatic = null;
                        // Set the new action as last action
                        lastAction = new ActionParser().parse(record);
                        // And add it to the scriptItems
                        scriptItems.add(lastAction);
                        break;

                    case CsvConstants.TYPE_STATIC:
                        // If there was no lastAction, throw an exception
                        if (lastAction == null)
                        {
                            throw new IllegalArgumentException("Static Type must be defined after an Action Type");
                        }
                        // If there wasn't a last static request
                        if (lastStatic == null)
                        {
                            // Read the static request and assign it to lastStatic
                            lastStatic = new StaticSubrequestParser().parse(record);
                            // Assign last static as actionItem of the lastAction
                            lastAction.getActionItems().add(lastStatic);
                        }
                        // If there was a last static request
                        else
                        {
                            // Simply add the url of the current static request to the lastStatic request, so they can be executed parallel
                            lastStatic.getUrls().addAll(new StaticSubrequestParser().parse(record).getUrls());
                        }
                        break;

                    case CsvConstants.TYPE_XHR_ACTION:
                        // Reset the last static subrequest
                        lastStatic = null;
                        // If there was no lastAction, throw an exception
                        if (lastAction == null)
                        {
                            throw new IllegalArgumentException("Xhr Action Type must be defined after an Action Type");
                        }
                        // Add the Xhr Subrequest to the action items of the lastAction
                        lastAction.getActionItems().add(new XhrSubrequestParser().parse(record));
                        break;

                    default:
                        // If the type is unknown, throw an error
                        throw new IllegalArgumentException("Unknown type: " + type);
                }
            }
        }
        // Return the scriptItems
        return scriptItems;
    }

    /**
     * Validates the headers are permitted
     * 
     * @param headers
     */
    private void validateHeader(final Set<String> headers)
    {
        // for each header
        for (final String header : headers)
        {
            // If the header is not in the permitted list
            if (!CsvConstants.isPermittedHeaderField(header))
            {
                // Verify the header either starts with the Regexp or Xpath getter prefix
                if (!(header.startsWith(CsvConstants.REGEXP_GETTER_PREFIX) || header.startsWith(CsvConstants.XPATH_GETTER_PREFIX)))
                {
                    throw new IllegalArgumentException(header + "isn't an allowed header!");
                }
            }
        }
    }

    @Override
    public List<String> getExtensions()
    {
        return Arrays.asList("csv");
    }

}
