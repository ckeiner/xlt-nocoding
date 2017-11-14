package com.xceptance.xlt.nocoding.scriptItem.action.response.selector;

import java.util.List;

import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.xceptance.xlt.nocoding.util.Context;

public class CookieSelector extends AbstractSelector
{

    public CookieSelector(final String selectionExpression)
    {
        super(selectionExpression);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void execute(final Context context)
    {
        // Resolve variables
        resolveValues(context);

        final List<NameValuePair> headers = context.getWebResponse().getResponseHeaders();
        // For each header,
        for (final NameValuePair header : headers)
        {
            // Search for the Set-Cookie header
            if (header.getName().equals("Set-Cookie"))
            {
                // Trim all whitespaces
                // And verify if this is the correct cookie by
                // grabbing the cookie name
                final int equalSignPosition = header.getValue().indexOf("=");
                String cookieName = header.getValue().substring(0, equalSignPosition);
                // Remove possible whitespaces at the beginning
                cookieName = cookieName.trim();
                // and comparing it with the input name
                if (cookieName.equals(getSelectionExpression()))
                {
                    // Get the content of the cookie, which is until the first semicolon
                    final int semicolonPosition = header.getValue().indexOf(";");
                    // Content starts after the equal sign (position+1) and ends before the semicolon
                    String cookieContent = header.getValue().substring(equalSignPosition + 1, semicolonPosition);
                    // Remove possible whitespaces at the beginning or end
                    cookieContent = cookieContent.trim();
                    addResult(cookieContent);
                    break;
                }
            }
        }

    }

}
