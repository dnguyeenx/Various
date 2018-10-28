/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.zeppelin.marinetrafficapi.utils;

/**
 *
 * @author dnguyeenx
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class APIClientGet
{

    /**
     * Constructor
     */
    private String URL = null;
    private URL url = null;
    private HttpURLConnection conn = null;

    private static final Log LOGGER = LogFactory.getLog(APIClientGet.class.getName());

    public APIClientGet(String URL)
    {
        this.URL = URL;
    }

    public void CreateURL()
    {
        try
        {
            this.url = new URL(this.URL);

        } catch (MalformedURLException ex)
        {
            LOGGER.warn(ex.getMessage());
        }
    }

    public void CreateConnection()
    {
        try
        {
            this.conn = (HttpURLConnection) this.url.openConnection();
            this.conn.setRequestMethod("GET");
        } catch (IOException ex)
        {
            LOGGER.warn(ex.getMessage());
        }
    }

    public int GetResponseCode() throws Exception
    {
        LOGGER.info("Response code: " + this.conn.getResponseCode());
        return this.conn.getResponseCode();
    }

    public String GetReponse() throws Exception
    {
        StringBuffer response = null;

        BufferedReader in = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        String inputLine;
        response = new StringBuffer();
        while ((inputLine = in.readLine()) != null)
        {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    public void Disconnect()
    {
        try
        {
            this.conn.disconnect();
        } catch (Exception ex)
        {
            LOGGER.warn(ex.getMessage());

        }
    }

}
