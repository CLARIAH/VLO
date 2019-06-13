package eu.clarin.cmdi.vlo.exposure.postgresql.impl;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.clarin.cmdi.vlo.exposure.postgresql.*;
import eu.clarin.cmdi.vlo.exposure.postgresql.SearchResultHandler;
import eu.clarin.cmdi.vlo.exposure.models.PageView;
import eu.clarin.cmdi.vlo.exposure.models.SearchResult;
import eu.clarin.cmdi.vlo.config.VloConfig;
import eu.clarin.cmdi.vlo.exposure.postgresql.VloExposureException;

public class SearchResultHandlerImpl implements SearchResultHandler{
	
	private final static Logger logger = LoggerFactory.getLogger(SearchResultHandlerImpl.class);
	
	private final String table = "\"public\".\"SearchResults\"";
	
	@Override
	public boolean addSearchResult(VloConfig vloConfig, long queryId, SearchResult sr) throws VloExposureException{
		String sqlQuery = "INSERT INTO " + table + "(\"query_id\", record_id,  position, page) "
                + "VALUES(?,?,?,?)";
		
		boolean added = false;
        long id = 0;
        int affectedRows = -1;
        try{
        	PgDaoImp PgConn = new PgDaoImp(vloConfig);
    		Connection conn = PgConn.connect();
    		PreparedStatement pstmt = conn.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
    		
    		pstmt.setLong(1, queryId);
    		pstmt.setString(2, sr.getRecordId());
    		pstmt.setInt(3, sr.getPosition() );
            pstmt.setInt(4, sr.getPage());

            affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
            	added = true;
            }
            
            conn.close();
        } catch (SQLException ex) {
        	logger.error(ex.getMessage());      		
        }
        return added;
	}
}
