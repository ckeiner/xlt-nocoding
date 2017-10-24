package com.xceptance.xlt.nocoding.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.xceptance.xlt.api.util.XltLogger;
import com.xceptance.xlt.nocoding.scriptItem.ScriptItem;
import com.xceptance.xlt.nocoding.scriptItem.StoreDefault;
import com.xceptance.xlt.nocoding.scriptItem.StoreItem;
import com.xceptance.xlt.nocoding.scriptItem.action.AbstractActionItem;
import com.xceptance.xlt.nocoding.scriptItem.action.LightWeigthAction;
import com.xceptance.xlt.nocoding.scriptItem.action.Request;
import com.xceptance.xlt.nocoding.scriptItem.action.response.Response;
import com.xceptance.xlt.nocoding.scriptItem.action.response.stores.AbstractResponseStore;
import com.xceptance.xlt.nocoding.scriptItem.action.response.stores.CookieStore;
import com.xceptance.xlt.nocoding.scriptItem.action.response.stores.HeaderStore;
import com.xceptance.xlt.nocoding.scriptItem.action.response.stores.RegExpStore;
import com.xceptance.xlt.nocoding.scriptItem.action.response.stores.XpathStore;
import com.xceptance.xlt.nocoding.scriptItem.action.response.validators.AbstractValidator;
import com.xceptance.xlt.nocoding.scriptItem.action.response.validators.CookieValidator;
import com.xceptance.xlt.nocoding.scriptItem.action.response.validators.HeaderValidator;
import com.xceptance.xlt.nocoding.scriptItem.action.response.validators.RegExpValidator;
import com.xceptance.xlt.nocoding.scriptItem.action.response.validators.XPathValidator;
import com.xceptance.xlt.nocoding.scriptItem.action.subrequest.AbstractSubrequest;
import com.xceptance.xlt.nocoding.scriptItem.action.subrequest.StaticSubrequest;
import com.xceptance.xlt.nocoding.scriptItem.action.subrequest.XHRSubrequest;
import com.xceptance.xlt.nocoding.util.Constants;

/**
 * Reads a yaml file, provided per constructor, and generates a testsuite out of the yaml file.
 * 
 * @author ckeiner
 */
public class YamlParser implements Parser
{

    /**
     * The path to the yaml file
     */
    final String pathToFile;

    public YamlParser(final String pathToFile)
    {
        this.pathToFile = pathToFile;
    }

    @Override
    public List<ScriptItem> parse() throws Exception
    {
        return parseThis();
    }

    public List<ScriptItem> parseThis() throws JsonParseException, IOException
    {
        final List<ScriptItem> scriptItems = new ArrayList<ScriptItem>();
        final File file = new File(pathToFile);
        final YAMLFactory factory = new YAMLFactory();
        final JsonParser parser = factory.createParser(file);

        int numberObject = 0;

        while (parser.nextToken() != null)
        {
            if (Constants.isPermittedListItem(parser.getText()))
            {
                numberObject++;
                XltLogger.runTimeLogger.info(numberObject + ".th ScriptItem: " + parser.getText());

                try
                {
                    if (parser.getText().equals(Constants.STORE))
                    {
                        scriptItems.addAll(handleStore(parser));
                    }
                    else if (parser.getText().equals(Constants.ACTION))
                    {
                        scriptItems.addAll(handleAction(parser));
                    }
                    else
                    {
                        scriptItems.addAll(handleDefault(parser));
                    }
                }
                // If the parser fails at any point, print the current position
                catch (final Exception e)
                {
                    throw new JsonParseException(e.getMessage() + parser.getText(), parser.getCurrentLocation(), e);
                }

                // TODO lass kommentare zu
                // parser.configure(Feature., state)
            }
            else if (parser.getCurrentToken() != null && parser.getCurrentToken().equals(JsonToken.FIELD_NAME))
            {
                // XltLogger.runTimeLogger.warn("No permitted list item: " + parser.getText() + logLineColumn(parser));
                // throw new com.google.gson.JsonParseException("No permitted list item: " + parser.getText() + logLineColumn(parser));

                // TODO the other methods dont work - NoSuchMethodError -> Dependency Issue but Pom says we use jackson-core 2.9.0
                throw new JsonParseException("No permitted list item: " + parser.getText(), parser.getCurrentLocation());
            }

        }

        return scriptItems;
    }

    private List<ScriptItem> handleStore(final JsonParser parser) throws IOException
    {
        // transform current parser to the content of the current node
        final List<ScriptItem> scriptItems = new ArrayList<ScriptItem>();
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectNode objectNode = mapper.readTree(parser);
        final JsonNode jsonNode = objectNode.get(Constants.STORE);
        final Iterator<JsonNode> iterator = jsonNode.elements();

        while (iterator.hasNext())
        {
            final JsonNode current = iterator.next();
            final Iterator<String> fieldName = current.fieldNames();

            while (fieldName.hasNext())
            {
                final String field = fieldName.next();
                final String textValue = current.get(field).textValue();
                scriptItems.add(new StoreItem(field, textValue));
                XltLogger.runTimeLogger.debug("Added " + field + "=" + textValue + " to parameters");
            }
        }
        return scriptItems;

    }

    private List<ScriptItem> handleAction(final JsonParser parser) throws IOException
    {
        // Initialize variables
        final List<ScriptItem> scriptItems = new ArrayList<ScriptItem>();
        String name = null;
        final List<AbstractActionItem> actionItems = new ArrayList<AbstractActionItem>();

        final ObjectMapper mapper = new ObjectMapper();
        final ObjectNode objectNode = mapper.readTree(parser);
        final Iterator<JsonNode> iterator = objectNode.elements();

        while (iterator.hasNext())
        {
            final JsonNode node = iterator.next();
            final Iterator<String> fieldNames = node.fieldNames();

            while (fieldNames.hasNext())
            {
                final String fieldName = fieldNames.next();

                switch (fieldName)
                {
                    case Constants.NAME:
                        name = node.get(fieldName).textValue();
                        XltLogger.runTimeLogger.debug("Actionname: " + name);
                        break;

                    case Constants.REQUEST:
                        XltLogger.runTimeLogger.debug("Request: " + node.get(fieldName).toString());
                        actionItems.add(handleRequest(node.get(fieldName)));
                        break;

                    case Constants.RESPONSE:
                        XltLogger.runTimeLogger.debug("Response: " + node.get(fieldName).toString());
                        actionItems.add(handleResponse(node.get(fieldName)));
                        break;

                    case Constants.SUBREQUESTS:
                        XltLogger.runTimeLogger.debug("Subrequests: " + node.get(fieldName).toString());
                        actionItems.addAll(handleSubrequest(node.get(fieldName)));
                        break;

                    default:
                        // We iterate over the field names, so there is no way we have an Array/Object Start
                        throw new JsonParseException("No permitted action item: " + node.get(fieldName).textValue(),
                                                     parser.getCurrentLocation());
                }
            }
        }

        final ScriptItem scriptItem = new LightWeigthAction(name, actionItems);
        scriptItems.add(scriptItem);
        return scriptItems;
    }

    private Request handleRequest(final JsonNode node) throws IOException
    {
        String url = "";
        String method = null;
        String xhr = null;
        String encodeParameters = null;
        final List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        final Map<String, String> headers = new HashMap<String, String>();
        String body = null;
        String encodeBody = null;

        final Iterator<String> fieldNames = node.fieldNames();

        while (fieldNames.hasNext())
        {
            final String fieldName = fieldNames.next();

            switch (fieldName)
            {
                case Constants.URL:
                    url = node.get(fieldName).textValue();
                    // XltLogger.runTimeLogger.debug("URL: " + url);
                    break;

                case Constants.METHOD:
                    method = node.get(fieldName).textValue();
                    // XltLogger.runTimeLogger.debug("Method: " + method);
                    break;

                case Constants.XHR:
                    xhr = node.get(fieldName).textValue();
                    // XltLogger.runTimeLogger.debug("Xhr: " + xhr);
                    break;

                case Constants.ENCODEPARAMETERS:
                    encodeParameters = node.get(fieldName).textValue();
                    // XltLogger.runTimeLogger.debug("EncodeParameters: " + encodeParameters);
                    break;

                case Constants.PARAMETERS:
                    // Parameter Magic
                    parameters.addAll(handleParameters(node.get(fieldName)));
                    break;

                case Constants.HEADERS:
                    headers.putAll(handleHeaders(node.get(fieldName)));
                    break;

                case Constants.BODY:
                    body = node.get(fieldName).textValue();
                    // XltLogger.runTimeLogger.debug("Body: " + body);
                    break;

                case Constants.ENCODEBODY:
                    encodeBody = node.get(fieldName).textValue();
                    // XltLogger.runTimeLogger.debug("EncodeBody: " + encodeBody);
                    break;

                default:
                    throw new IOException("No permitted request item: " + fieldName);
            }
        }

        final Request request = new Request(url);
        request.setMethod(method);
        request.setXhr(xhr);
        request.setEncodeParameters(encodeParameters);
        request.setParameters(parameters);
        request.setHeaders(headers);
        request.setBody(body);
        request.setEncodeBody(encodeBody);

        XltLogger.runTimeLogger.info(request.toSimpleDebugString());

        return request;
    }

    private Map<String, String> handleHeaders(final JsonNode node) throws IOException
    {
        // headers are transformed to a JSONArray

        final Map<String, String> headers = new HashMap<String, String>();

        final Iterator<JsonNode> iterator = node.elements();

        while (iterator.hasNext())
        {
            final JsonNode current = iterator.next();
            final Iterator<String> fieldName = current.fieldNames();

            while (fieldName.hasNext())
            {
                final String field = fieldName.next();
                final String textValue = current.get(field).textValue();
                headers.put(field, textValue);
                XltLogger.runTimeLogger.debug("Added " + field + "=" + headers.get(field) + " to parameters");
            }
        }

        return headers;
    }

    private List<NameValuePair> handleParameters(final JsonNode node) throws IOException
    {
        // parameters are transformed to a JSONArray, thus we cannot directly use the fieldname iterator

        final List<NameValuePair> parameters = new ArrayList<NameValuePair>();

        final Iterator<JsonNode> iterator = node.elements();

        while (iterator.hasNext())
        {
            final JsonNode current = iterator.next();
            final Iterator<String> fieldName = current.fieldNames();

            while (fieldName.hasNext())
            {
                final String field = fieldName.next();
                String textValue = current.get(field).textValue();
                // TODO talk about this
                // Since parameters might be empty and the parser writes "null" we check for it and then remove it
                if (textValue == null || textValue.equals("null"))
                {
                    // numbers wont get read with "textValue" so we have to first figure out if it is a number
                    textValue = current.get(field).toString();
                    // if we still have null, the field really was null
                    if (textValue == null || textValue.equals("null"))
                    {
                        textValue = "";
                    }
                }
                parameters.add(new NameValuePair(field, textValue));
                XltLogger.runTimeLogger.debug("Added " + field + "=" + textValue + " to parameters");
            }
        }

        return parameters;
    }

    private Response handleResponse(final JsonNode node) throws IOException
    {

        String httpcode = null;
        final List<AbstractValidator> validators = new ArrayList<AbstractValidator>();
        final List<AbstractResponseStore> responseStore = new ArrayList<AbstractResponseStore>();

        final Iterator<String> fieldNames = node.fieldNames();

        while (fieldNames.hasNext())
        {
            final String fieldName = fieldNames.next();
            switch (fieldName)
            {
                case Constants.HTTPCODE:
                    // we have to use toString here
                    httpcode = node.get(fieldName).toString();
                    XltLogger.runTimeLogger.debug("Added Httpcode " + httpcode);
                    break;

                case Constants.VALIDATION:
                    validators.addAll(handleValidation(node.get(fieldName)));
                    XltLogger.runTimeLogger.debug("Added Validation");
                    break;

                case Constants.STORE:
                    responseStore.addAll(handleResponseStore(node.get(fieldName)));
                    XltLogger.runTimeLogger.debug("Added Validation");
                    break;

                default:
                    throw new IOException("No permitted response item: " + fieldName);
            }
        }

        final Response response = new Response(httpcode, responseStore, validators);
        return response;
    }

    private List<AbstractResponseStore> handleResponseStore(final JsonNode node) throws IOException
    {

        String variableName = null;
        final List<AbstractResponseStore> responseStore = new ArrayList<AbstractResponseStore>();
        // Go through every element
        final Iterator<JsonNode> iterator = node.elements();

        while (iterator.hasNext())
        {
            final JsonNode current = iterator.next();
            final Iterator<String> fieldName = current.fieldNames();

            while (fieldName.hasNext())
            {
                // Name of the variable
                variableName = fieldName.next();
                // The substructure
                final JsonNode storeContent = current.get(variableName);
                final Iterator<String> name = storeContent.fieldNames();
                // Iterate over the content
                while (name.hasNext())
                {
                    final String leftHandExpression = name.next();
                    switch (leftHandExpression)
                    {
                        case Constants.XPATH:
                            // Xpath Magic
                            responseStore.add(new XpathStore(variableName, storeContent.get(leftHandExpression).textValue()));
                            break;

                        case Constants.REGEXP:
                            final String pattern = storeContent.get(leftHandExpression).textValue();
                            String group = null;
                            // if we have another fieldName, this means the optional group is specified
                            if (name.hasNext())
                            {
                                group = storeContent.get(name.next()).textValue();
                            }
                            responseStore.add(new RegExpStore(variableName, pattern, group));
                            break;

                        case Constants.HEADER:
                            responseStore.add(new HeaderStore(variableName, storeContent.get(leftHandExpression).textValue()));
                            break;

                        case Constants.COOKIE:
                            responseStore.add(new CookieStore(variableName, storeContent.get(leftHandExpression).textValue()));
                            break;

                        default:
                            throw new IOException("No permitted response store item: " + leftHandExpression);
                    }
                }

                XltLogger.runTimeLogger.debug("Added " + variableName + " to ResponseStore");
            }
        }

        return responseStore;
    }

    private List<AbstractValidator> handleValidation(final JsonNode node) throws IOException
    {

        final List<AbstractValidator> validator = new ArrayList<AbstractValidator>();
        String validationName = null;

        final Iterator<JsonNode> iterator = node.elements();

        while (iterator.hasNext())
        {
            final JsonNode current = iterator.next();
            final Iterator<String> fieldName = current.fieldNames();

            while (fieldName.hasNext())
            {
                validationName = fieldName.next();
                // The substructure
                final JsonNode storeContent = current.get(validationName);
                final Iterator<String> name = storeContent.fieldNames();
                // Iterate over the content
                while (name.hasNext())
                {
                    final String leftHandExpression = name.next();
                    switch (leftHandExpression)
                    {
                        case Constants.XPATH:
                            final String xPathExpression = storeContent.get(leftHandExpression).textValue();
                            String matches = null;
                            String count = null;
                            // if we have another name, this means the optional text is specified
                            if (name.hasNext())
                            {
                                final String left = name.next();
                                if (left.equals(Constants.MATCHES))
                                {
                                    matches = storeContent.get(left).textValue();
                                }
                                else if (left.equals(Constants.COUNT))
                                {
                                    count = storeContent.get(left).textValue();
                                }
                            }
                            validator.add(new XPathValidator(validationName, xPathExpression, matches, count));

                            break;

                        case Constants.REGEXP:
                            final String pattern = storeContent.get(leftHandExpression).textValue();
                            String group = null;
                            String text = null;
                            // if we have another name, this means the optional text is specified
                            if (name.hasNext())
                            {
                                text = storeContent.get(name.next()).textValue();
                                // if we have yet another name, this is the optional group
                                if (name.hasNext())
                                {
                                    group = storeContent.get(name.next()).textValue();
                                }
                            }

                            validator.add(new RegExpValidator(validationName, pattern, text, group));
                            break;

                        case Constants.HEADER:
                            final String header = storeContent.get(leftHandExpression).textValue();
                            String textOrCountDecider = null;
                            String textOrCount = null;
                            if (name.hasNext())
                            {
                                textOrCountDecider = name.next();
                                textOrCount = storeContent.get(textOrCountDecider).textValue();
                            }
                            validator.add(new HeaderValidator(validationName, header, textOrCountDecider, textOrCount));
                            break;

                        case Constants.COOKIE:
                            final String cookieName = storeContent.get(leftHandExpression).textValue();
                            String cookieContent = null;

                            // If we have another name, it is the optional "matches" field
                            if (name.hasNext())
                            {
                                cookieContent = storeContent.get(name.next()).textValue();
                            }

                            validator.add(new CookieValidator(validationName, cookieName, cookieContent));
                            break;

                        default:
                            throw new IOException("No permitted validation item: " + leftHandExpression);
                    }
                }

                XltLogger.runTimeLogger.debug("Added " + validationName + " to Validations");
            }
        }
        // return new ArrayList<AbstractValidator>();
        return validator;
    }

    private List<AbstractSubrequest> handleSubrequest(final JsonNode node) throws IOException
    {
        // TODO Auto-generated method stub
        final List<AbstractSubrequest> subrequest = new ArrayList<AbstractSubrequest>();

        final Iterator<JsonNode> iterator = node.elements();

        while (iterator.hasNext())
        {
            final JsonNode current = iterator.next();
            final Iterator<String> fieldName = current.fieldNames();

            // the type of subrequest
            while (fieldName.hasNext())
            {
                final String name = fieldName.next();
                switch (name)
                {
                    case Constants.XHR:
                        subrequest.add(handleXhrSubrequest(current.get(name)));
                        break;

                    case Constants.STATIC:
                        // System.out.println(current.get(name));
                        final List<String> urls = new ArrayList<String>();
                        final JsonNode staticUrls = current.get(name);

                        final Iterator<JsonNode> staticUrlsIterator = staticUrls.elements();
                        while (staticUrlsIterator.hasNext())
                        {
                            final String url = staticUrlsIterator.next().textValue();
                            urls.add(url);
                        }

                        subrequest.add(new StaticSubrequest(urls));
                        break;

                    default:
                        throw new IOException("No permitted subrequest item: " + name);
                }

            }

        }

        return subrequest;
    }

    private AbstractSubrequest handleXhrSubrequest(final JsonNode node) throws IOException
    {
        final Iterator<String> fieldNames = node.fieldNames();
        String name = null;
        Request request = null;
        Response response = null;

        while (fieldNames.hasNext())
        {
            final String fieldName = fieldNames.next();

            switch (fieldName)
            {
                case Constants.NAME:
                    name = node.get(fieldName).textValue();
                    XltLogger.runTimeLogger.debug("Actionname: " + name);
                    break;

                case Constants.REQUEST:
                    XltLogger.runTimeLogger.debug("Request: " + node.get(fieldName).toString());
                    request = handleRequest(node.get(fieldName));
                    // Set Xhr to true
                    request.setXhr("true");
                    break;

                case Constants.RESPONSE:
                    XltLogger.runTimeLogger.debug("Response: " + node.get(fieldName).toString());
                    response = handleResponse(node.get(fieldName));
                    break;

                // case Constants.SUBREQUESTS:
                // XltLogger.runTimeLogger.debug("Subrequests: " + node.get(fieldName).toString());
                // actionItems.addAll(handleSubrequest(node.get(fieldName)));
                // break;

                default:
                    throw new IOException("No permitted xhr subrequest item: " + fieldName);
            }
        }

        final AbstractSubrequest subrequest = new XHRSubrequest(name, request, response);
        return subrequest;
    }

    private List<ScriptItem> handleDefault(final JsonParser parser) throws IOException
    {
        // TODO handle parameter and other structures

        final List<ScriptItem> scriptItems = new ArrayList<ScriptItem>();
        final String variableName = parser.getText();
        final String value = parser.nextTextValue();

        scriptItems.add(new StoreDefault(variableName, value));
        return scriptItems;

    }

}
