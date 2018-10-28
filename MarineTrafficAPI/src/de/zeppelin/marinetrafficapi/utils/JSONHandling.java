/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.zeppelin.marinetrafficapi.utils;

/**
 *
 * @author dnguyeenx
 * https://www.finning.com/en_CA/products/new/power-systems/electric-power-generation/_jcr_content.feed.json
 *
 */
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JSONHandling
{

    private static final Log LOGGER = LogFactory.getLog(JSONHandling.class.getName());

    private String json;
    private JsonObject MainJsonObject;
    private JsonArray AllModels;

    public JSONHandling()
    {

    }

    public void ReadUrl(String urlString) throws Exception
    {
        BufferedReader reader = null;
        try
        {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder buffer = new StringBuilder();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
            {
                buffer.append(chars, 0, read);
            }

            this.json = buffer.toString();
        } finally
        {
            if (reader != null)
            {
                reader.close();
            }
        }
    }

    public void ParseJson()
    {
        try
        {
            this.MainJsonObject = new JsonParser().parse(this.json).getAsJsonObject();

        } catch (JsonSyntaxException ex)
        {
            LOGGER.warn(ex.getMessage());
        }
    }

    public void GetModels()
    {
        try
        {
            LOGGER.info("Get all models");
            this.AllModels = this.MainJsonObject.getAsJsonArray("models");
            LOGGER.info(this.AllModels.size() + " elements have been found");

        } catch (Exception ex)
        {
            LOGGER.warn(ex.getMessage());
        }

    }

    public Map<String, String> GetAllUrls()
    {

        Map<String, String> ModelUrls = new HashMap<>();
        try
        {
            for (JsonElement model : AllModels)
            {
                JsonObject tmp = model.getAsJsonObject();
                ModelUrls.put(tmp.get("idmodel").getAsString(), tmp.get("detail_url").getAsString());

            }

        } catch (Exception ex)
        {
            LOGGER.warn(ex.getMessage());

        }

        return ModelUrls;
    }

}
