/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.zeppelin.marinetrafficapi;

/**
 *
 * @author dnguyeenx
 */
import de.zeppelin.marinetrafficapi.DBConnector.DBQueries;
import de.zeppelin.marinetrafficapi.DBConnector.PostgresConnector;
import de.zeppelin.marinetrafficapi.utils.APIClientGet;
import de.zeppelin.marinetrafficapi.utils.JSONHandling;
import de.zeppelin.marinetrafficapi.webcrawling.WebCrawling;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MarineTrafficAPI
{

    /**
     * @param args the command line arguments
     *
     * Example
     * https://services.marinetraffic.com/api/exportvesseltrack/ef54b62cc14acab9a992f4d85fb1ae231c9d7d20/v:2/period:daily/
     * fromdate:2016-08-01/todate:2016-08-31/protocol:csv/mmsi:241486000
     */
    private static final Log LOGGER = LogFactory.getLog(MarineTrafficAPI.class.getName());
    private static final String API_Prefix = "https://services.marinetraffic.com/api/exportvesseltrack/";
    private static final String SplitCharacter = "/";

    private String ClassPathString = null;
    private String CSVFilePath = null;
    private String HostIP = null;
    private String HostName = null;
    private String API_KEY = null;
    private String Periode = null;
    private String FromDate = null;
    private String ToDate = null;
    private String Protocol = null;
    private String MMSI = null;

    private void GetClassPath()
    {
        LOGGER.info("Loading class path.");
        this.ClassPathString = new File("").getAbsolutePath();
        LOGGER.info(this.ClassPathString);

    }

    private void LoadConfigFile()
    {
        try
        {
            LOGGER.info("Loading config file.");
            File configFile = new File(this.ClassPathString, "Configuration.xml");
            if (!(configFile.exists() && !configFile.isDirectory()))
            {
                LOGGER.info("Please check the class path. \n File not found" + configFile.getAbsolutePath());
                System.exit(0);
            }

            XMLConfiguration config = new XMLConfiguration(configFile);

            this.CSVFilePath = config.getString("CSVFilePath");
            this.API_KEY = config.getString("API_KEY");
            this.HostIP = config.getString("HostIP");
            this.HostName = config.getString("HostName");
            this.Periode = config.getString("Periode");
            this.FromDate = config.getString("FromDate");
            this.ToDate = config.getString("ToDate");
            this.Protocol = config.getString("Protocol");
            this.MMSI = config.getString("MMSI");

            LOGGER.info("Loading config file done !.");

        } catch (ConfigurationException ex)
        {
            LOGGER.warn(ex.getMessage());
            System.exit(0);
        }

    }

    private String CreateEndPoint(String mmsi)
    {
        String EndPoint = MarineTrafficAPI.API_Prefix + this.API_KEY + MarineTrafficAPI.SplitCharacter;
        EndPoint += "v:2" + MarineTrafficAPI.SplitCharacter;
        EndPoint += "periode:" + this.Periode + MarineTrafficAPI.SplitCharacter;
        EndPoint += "fromdate:" + this.FromDate + MarineTrafficAPI.SplitCharacter;
        EndPoint += "todate:" + this.ToDate + MarineTrafficAPI.SplitCharacter;
        EndPoint += "protocol:" + this.Protocol + MarineTrafficAPI.SplitCharacter;
        EndPoint += "mmsi:" + mmsi;
        LOGGER.info("Endpoint URL: " + EndPoint);
        return EndPoint;

    }

    public static void main(String[] args)
    {

        try
        {
            String HostPrefix = "https://www.finning.com";
            String jsonURL = "/en_CA/products/new/power-systems/electric-power-generation/_jcr_content.feed.json";
            JSONHandling getModels = new JSONHandling();
            getModels.ReadUrl(HostPrefix + jsonURL);
            getModels.ParseJson();
            getModels.GetModels();
            Map allModels = getModels.GetAllUrls();
            PostgresConnector DBConnect = new PostgresConnector("localhost", "zeppelin", "postgres", "linhtinh");
            DBConnect.ConnetionOpen();
            for (Iterator it = allModels.entrySet().iterator(); it.hasNext();)
            {
                Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
                LOGGER.info(entry.getKey() + " : " + entry.getValue());
                WebCrawling webcraw = new WebCrawling(HostPrefix + entry.getValue());
                webcraw.GetContent();
                HashMap<String, String> tmp = webcraw.GetRequiredInfo();

                try (PreparedStatement pstst = DBConnect.SetParameter(DBQueries.ZPL_ENGINES_INFO))
                {
                    pstst.setString(1, entry.getKey());
                    pstst.setString(2, tmp.get("Minimum Rating"));
                    pstst.setString(3, tmp.get("Maximum Rating"));
                    pstst.setString(4, tmp.get("Voltage"));
                    pstst.setString(5, tmp.get("Frequency"));
                    pstst.setString(6, tmp.get("Speed"));

                    pstst.execute();
                    LOGGER.info(pstst.toString());

                } catch (SQLException ex)
                {
                    LOGGER.warn(ex.getMessage());
                    System.exit(0);
                }

            }
            DBConnect.ConnectionClose();
//            File folder = new File("/home/dnguyeenx/Documents/Zeppelin");
//            File[] listOfFiles = folder.listFiles();
//
//            for (int i = 0; i < listOfFiles.length; i++)
//            {
//                if (listOfFiles[i].isFile())
//                {
//                    String FileName = listOfFiles[i].getName();
//                    String FilePath = listOfFiles[i].getAbsolutePath();
//                    LOGGER.info("File " + FileName);
//
//                    if (FileName.equals("ship_engines.csv"))
//                    {
//                        //Reading the CSV File
//                        CSVParser csvFileParser = CSVFormat.DEFAULT
//                                .withFirstRecordAsHeader()
//                                .withDelimiter(',')
//                                .parse(new FileReader(new File(FilePath)));
//
//                        Iterator<CSVRecord> tmp = csvFileParser.iterator();
//                        PostgresConnector DBConnect = new PostgresConnector("localhost", "zeppelin", "postgres", "linhtinh");
//                        DBConnect.ConnetionOpen();
//                        while (tmp.hasNext())
//                        {
//                            //mmsi,ship_name,engine1_id,engine1_name,engine2_id,engine2_name,engine3_id,engine3_name
//                            CSVRecord record = tmp.next();
//
//                            try (PreparedStatement pstst = DBConnect.SetParameter(DBQueries.ZPL_SHIP_ENGINES))
//                            {
//                                pstst.setString(1, record.get(0));
//                                pstst.setString(2, record.get(1));
//                                pstst.setString(3, record.get(2));
//                                pstst.setString(4, record.get(3));
//                                pstst.setString(5, record.get(4));
//                                pstst.setString(6, record.get(5));
//                                pstst.setString(7, record.get(6));
//                                pstst.setString(8, record.get(7));
//
//                                pstst.execute();
//                                LOGGER.info(pstst.toString());
//
//                            } catch (SQLException ex)
//                            {
//                                LOGGER.warn(ex.getMessage());
//                                System.exit(0);
//                            }
//
//                        }
//
//                        DBConnect.ConnectionClose();
//
//                    }
//
//                } else if (listOfFiles[i].isDirectory())
//                {
//                    LOGGER.info("Directory " + listOfFiles[i].getName());
//                }
//            }
//            MarineTrafficAPI client = new MarineTrafficAPI();
//            client.GetClassPath();
//            client.LoadConfigFile();
//            String[] MultiMMSI = client.MMSI.split(",");
//
//            for (String mmsi : MultiMMSI)
//            {
//                // Create the EndPoint URL 
//                String EndPoint = client.CreateEndPoint(mmsi);
//                // Call the API
//                APIClientGet apiclient = new APIClientGet(EndPoint);
//                apiclient.CreateURL();
//                apiclient.CreateConnection();
//                String reponse = apiclient.GetReponse();
//                LOGGER.info(reponse);
//  
//                if (200 == apiclient.GetResponseCode())
//                {
//
//                } 
//
//                apiclient.Disconnect();
//
//            }
        } catch (Exception ex)
        {
            LOGGER.warn(ex.getMessage());
        }

    }

}
