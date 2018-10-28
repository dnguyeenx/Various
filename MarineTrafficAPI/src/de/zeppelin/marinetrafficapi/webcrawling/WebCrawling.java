/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.zeppelin.marinetrafficapi.webcrawling;

/**
 *
 * @author dnguyeenx
 */
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import org.jsoup.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawling
{

    private String modelURL;
    private Document content;
    private final HashMap<String, String> ModelInfos = new HashMap<>();
    private final String[] props =
    {
        "Voltage", "Frequency", "Speed", "Minimum Rating", "Maximum Rating"
    };

    private static final Log LOGGER = LogFactory.getLog(WebCrawling.class.getName());

    public WebCrawling(String modelURL)
    {
        this.modelURL = modelURL;
    }

    public void GetContent()
    {
        try
        {
            this.content = Jsoup.connect(this.modelURL).get();

        } catch (IOException ex)
        {
            LOGGER.warn(ex.getMessage());
        }
    }

    public HashMap<String, String> GetRequiredInfo()
    {
        try
        {

            Elements elements = this.content.body().select("li.flextable-last-column");

            for (Element element : elements)
            {

                String key = element.getElementsByTag("strong").text();
                String value = element.select("span.unit-metric").text().replaceAll("\\s+","");

                if (Arrays.asList(props).contains(key))
                {
                    LOGGER.info(key + " : " + value);
                    this.ModelInfos.put(key, value);
                }

            }

        } catch (NumberFormatException ex)
        {
            LOGGER.warn(ex.getMessage());
        }

        return this.ModelInfos;
    }

}
