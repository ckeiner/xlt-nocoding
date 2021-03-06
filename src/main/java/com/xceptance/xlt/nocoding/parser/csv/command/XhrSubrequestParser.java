package com.xceptance.xlt.nocoding.parser.csv.command;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

import com.xceptance.xlt.nocoding.command.action.AbstractActionSubItem;
import com.xceptance.xlt.nocoding.command.action.subrequest.XhrSubrequest;
import com.xceptance.xlt.nocoding.parser.csv.CsvConstants;
import com.xceptance.xlt.nocoding.parser.csv.command.action.RequestParser;
import com.xceptance.xlt.nocoding.parser.csv.command.action.ResponseParser;

/**
 * The class for parsing a Xhr Subrequest item from a {@link CSVRecord}.
 *
 * @author ckeiner
 */
public class XhrSubrequestParser
{

    /**
     * Extracts the information needed for a Xhr Subrequest from the {@link CSVRecord} and creates a
     * {@link XhrSubrequest} out of it.
     *
     * @param record
     *            The CSVRecord that describes the Xhr Subrequest
     * @return A <code>XhrSubrequest</code> with the parsed data
     */
    public XhrSubrequest parse(final CSVRecord record)
    {
        String name = null;
        final List<AbstractActionSubItem> actionItems = new ArrayList<>(2);
        // Get the name if it is mapped
        if (record.isMapped(CsvConstants.NAME))
        {
            name = record.get(CsvConstants.NAME);
        }
        // Parse the request
        actionItems.add(new RequestParser().parse(record));
        // Parse the response
        actionItems.add(new ResponseParser().parse(record));
        // Create the action
        final XhrSubrequest xhrSubrequest = new XhrSubrequest(name, actionItems);
        // Return the action
        return xhrSubrequest;
    }

}
