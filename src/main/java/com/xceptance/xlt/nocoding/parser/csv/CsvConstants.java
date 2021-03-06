package com.xceptance.xlt.nocoding.parser.csv;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;

/**
 * This class has all important constants for parsing a CSV file
 *
 * @author ckeiner
 */
public class CsvConstants
{
    /*
     * Dynamic Parameters
     */
    public static final String XPATH_GETTER_PREFIX = "xpath";

    public static final String REGEXP_GETTER_PREFIX = "regexp";

    /**
     * Permitted header fields, without XPATH_GETTER_PREFIX and REGEXP_GETTER_PREFIX
     */
    private final static Set<String> PERMITTEDHEADERFIELDS = new HashSet<>();

    public static final String TYPE = "Type";

    public static final String TYPE_ACTION = "A";

    public static final String TYPE_STATIC = "S";

    public static final String TYPE_DEFAULT = TYPE_ACTION;

    public static final String TYPE_XHR_ACTION = "XA";

    public static final String NAME = "Name";

    public static final String URL = "URL";

    public static final String METHOD = "Method";

    public static final String PARAMETERS = "Parameters";

    public static final String RESPONSECODE = "ResponseCode";

    public static final String RESPONSECODE_DEFAULT = "200";

    public static final String XPATH = "XPath";

    public static final String REGEXP = "RegExp";

    public static final String TEXT = "Text";

    public static final String ENCODED = "Encoded";

    public static final String ENCODED_DEFAULT = "false";

    public static final CSVFormat CSV_FORMAT;

    static
    {
        PERMITTEDHEADERFIELDS.add(TYPE);
        PERMITTEDHEADERFIELDS.add(NAME);
        PERMITTEDHEADERFIELDS.add(URL);
        PERMITTEDHEADERFIELDS.add(METHOD);
        PERMITTEDHEADERFIELDS.add(PARAMETERS);
        PERMITTEDHEADERFIELDS.add(RESPONSECODE);
        PERMITTEDHEADERFIELDS.add(XPATH);
        PERMITTEDHEADERFIELDS.add(REGEXP);
        PERMITTEDHEADERFIELDS.add(TEXT);
        PERMITTEDHEADERFIELDS.add(ENCODED);

        CSV_FORMAT = CSVFormat.RFC4180.withIgnoreEmptyLines(true).withCommentMarker('#').withHeader().withIgnoreSurroundingSpaces(true);
    }

    public static boolean isPermittedHeaderField(final String s)
    {
        return PERMITTEDHEADERFIELDS.contains(s);
    }
}
