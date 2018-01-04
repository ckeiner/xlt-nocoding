package com.xceptance.xlt.nocoding.scriptItem.action.response.extractor.xpathExtractor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.WebResponse;
import com.xceptance.xlt.nocoding.scriptItem.action.response.extractor.ExtractorTest;
import com.xceptance.xlt.nocoding.util.MockWebResponse;
import com.xceptance.xlt.nocoding.util.context.DomContext;

public class HtmlXmlXpathExtractorTest extends ExtractorTest
{

    WebResponse xmlResponse;

    String xmlType = "text/xml";

    String xmlContent = "<data>" + " <employee>" + "   <name>John</name>" + "   <title>Manager</title>" + " </employee>" + " <employee>"
                        + "   <name>Sara</name>" + "   <title>Clerk</title>" + " </employee>" + "</data>";

    WebResponse maliciousXmlContentResponse;

    /**
     * {@link #xmlContent} without ending data tag
     */
    String maliciousXmlContent = "<data>" + " <employee>" + "   <name>John</name>" + "   <title>Manager</title>" + " </employee>"
                                 + " <employee>" + "   <name>Sara</name>" + "   <title>Clerk</title>" + " </employee>";

    /**
     * Verifies the extractor works
     */
    @Test
    public void testXPathExtraction()
    {
        context = new DomContext(context);
        mockObjects.loadHtmlPage();
        ((DomContext) context).setPage(mockObjects.getHtmlPage());
        final HtmlXmlXpathExtractor xpathExtractor = new HtmlXmlXpathExtractor(mockObjects.xPathString);
        xpathExtractor.execute(context);
        final List<String> results = xpathExtractor.getResult();
        Assert.assertEquals(mockObjects.xpathStringExpected, results.get(0));
    }

    /**
     * Verifies you can get XML Content
     * 
     * @throws MalformedURLException
     */
    @Test
    public void testGetByXPathWithXML() throws MalformedURLException
    {
        final String url = "http://www.xceptance.net";
        xmlResponse = new MockWebResponse(xmlContent, new URL(url), xmlType);
        context.setWebResponse(xmlResponse);
        final HtmlXmlXpathExtractor xpathResponse = new HtmlXmlXpathExtractor("//title");
        xpathResponse.execute(context);
        final List<String> list = xpathResponse.getResult();
        final String tit0 = list.get(0);
        final String tit1 = list.get(1);
        Assert.assertEquals("Manager", tit0);
        Assert.assertEquals("Clerk", tit1);
    }

    /**
     * Verifies an error is thrown when the Xml Content is faulty
     * 
     * @throws MalformedURLException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetByXPathWithMaliciousXml() throws MalformedURLException
    {
        final String url = "http://www.xceptance.net";
        maliciousXmlContentResponse = new MockWebResponse(maliciousXmlContent, new URL(url), xmlType);
        context.setWebResponse(maliciousXmlContentResponse);
        final HtmlXmlXpathExtractor xpathResponse = new HtmlXmlXpathExtractor("//title");
        xpathResponse.execute(context);
    }

}
