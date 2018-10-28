/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.zeppelin.marinetrafficapi.DBConnector;

/**
 *
 * @author dnguyeenx
 */
public class DBQueries
{

    public static final String SELECT_VERS = "SELECT VERSION()";
    //public static final String SELECT_SIMLIST = "SELECT DISTINCT trim(SIMLIST_ICCID) FROM SIMLIST";
    public static final String SELECT_SIMLIST = "select distinct iccid from devices where iccid like '893%'";
    public static final String ZPL_POSITION_DATA = "INSERT INTO ZPL_POSITION_DATA "
            + "(PD_SHIP, PD_SPEED, PD_LON, PD_LAT, PD_TIMESTAMP)"
            + "VALUES (?, ?, ?, ? , to_timestamp(?,'YYYY-MM-DDThh24:MI:SSZ'))";
    public static final String ZPL_SHIP_ENGINES = "INSERT INTO ZPL_SHIP_ENGINES "
            + "(SE_MMSI, SE_SHIP_NAME, SE_ENGINE1_ID, SE_ENGINE1_NAME, SE_ENGINE2_ID, SE_ENGINE2_NAME, SE_ENGINE3_ID,SE_ENGINE3_NAME)"
            + "VALUES (?, ?, ?, ? , ?, ? , ?, ?)";
    public static final String ZPL_SHIPS_PER_OWNER = "INSERT INTO ZPL_SHIPS_PER_OWNER "
            + "(SO_OWNER_NAME, SO_SHIP_NAME)"
            + "VALUES (?, ?)";
    public static final String ZPL_ENGINES_INFO = "INSERT INTO ZPL_ENGINES_INFO "
            + "(EI_MODEL_ID, EI_MIN_RATING, EI_MAX_RATING, EI_VOLTAGE, EI_FREQUENCY, EI_SPEED)"
            + "VALUES (?, ?, ?, ? , ?, ?)";

}
