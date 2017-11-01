package com.xceptance.xlt.nocoding.scriptItem.action.response.validators;

import java.util.List;

import org.junit.Assert;

import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.xceptance.xlt.nocoding.util.Context;

public class CookieValidator extends AbstractValidator
{
    protected String cookie;

    protected String text;

    public CookieValidator(final String validationName, final String cookie)
    {
        this(validationName, cookie, null);
    }

    public CookieValidator(final String validationName, final String cookie, final String text)
    {
        super(validationName);
        this.cookie = cookie;
        this.text = text;
    }

    @Override
    public void execute(final Context context) throws Exception
    {
        final WebResponse webResponse = context.getWebResponse();
        // Resolve variables
        resolveValues(context);

        // If true, this throws an Exception if no cookie is found
        boolean throwException = true;
        // Get all headers
        final List<NameValuePair> headers = webResponse.getResponseHeaders();
        // For each header,
        for (final NameValuePair header : headers)
        {
            // Search for the Set-Cookie header
            if (header.getName().equals("Set-Cookie"))
            {
                // And verify if this is the correct cookie by
                // grabbing the cookie name
                final int equalSignPosition = header.getValue().indexOf("=");
                final String cookieName = header.getValue().substring(0, equalSignPosition);
                // and comparing it with the input name
                if (cookieName.equals(cookie))
                {
                    // Finally check if the text attribute is specified
                    if (text != null)
                    {
                        // If it is, assert that the cookie content is the same as the text attribute
                        // by getting the cookie content, that is until the first semicolon
                        final int semicolonPosition = header.getValue().indexOf(";");
                        // Content starts after the equal sign (position+1) and ends before the semicolon
                        Assert.assertEquals("Content did not match",
                                            text,
                                            header.getValue().substring(cookie.length() + 1, semicolonPosition));
                    }
                    // At last, set throwException to false, so we know, that we found our specified cookie.
                    throwException = false;
                    break;
                }
            }
        }

        if (throwException)
        {
            throw new Exception("Did not find specified cookie");
        }

    }

    private void resolveValues(final Context context)
    {
        String resolvedValue = context.resolveString(cookie);
        cookie = resolvedValue;
        if (text != null)
        {
            resolvedValue = context.resolveString(text);
            text = resolvedValue;
        }

    }

}
