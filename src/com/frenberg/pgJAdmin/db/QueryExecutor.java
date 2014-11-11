package com.frenberg.pgJAdmin.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.table.DefaultTableModel;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;

public class QueryExecutor {

	private Connection connection;
	private Pattern pattern = Pattern.compile("select|returning", Pattern.CASE_INSENSITIVE);
    static final ImmutableSortedMap<Integer, String> TYPES = new ImmutableSortedMap.Builder<Integer, String>(Ordering.natural())
            .put(new Integer(-16), "LONGNVARCHAR")
            .put(new Integer(-15), "NCHAR")
            .put(new Integer(-9), "NVARCHAR")
            .put(new Integer(-8), "ROWID")
            .put(new Integer(-7), "BIT")
            .put(new Integer(-6), "TINYINT")
            .put(new Integer(-5), "BIGINT")
            .put(new Integer(-4), "LONGVARBINARY")
            .put(new Integer(-3), "VARBINARY")
            .put(new Integer(-2), "BINARY")
            .put(new Integer(-1), "LONGVARCHAR")
            .put(new Integer(0), "NULL")
            .put(new Integer(1), "CHAR")
            .put(new Integer(2), "NUMERIC")
            .put(new Integer(3), "DECIMAL")
            .put(new Integer(4), "INTEGER")
            .put(new Integer(5), "SMALLINT")
            .put(new Integer(6), "FLOAT")
            .put(new Integer(7), "REAL")
            .put(new Integer(8), "DOUBLE")
            .put(new Integer(12), "VARCHAR")
            .put(new Integer(16), "BOOLEAN")
            .put(new Integer(70), "DATALINK")
            .put(new Integer(91), "DATE")
            .put(new Integer(92), "TIME")
            .put(new Integer(93), "TIMESTAMP")
            .put(new Integer(1111), "OTHER")
            .put(new Integer(2000), "JAVA_OBJECT")
            .put(new Integer(2001), "DISTINCT")
            .put(new Integer(2002), "STRUCT")
            .put(new Integer(2003), "ARRAY")
            .put(new Integer(2004), "BLOB")
            .put(new Integer(2005), "CLOB")
            .put(new Integer(2006), "REF")
            .put(new Integer(2009), "SQLXML")
            .put(new Integer(2011), "SQLXML")
           .build();	
	
	
	public QueryExecutor(ConnectionManager cm) throws SQLException {
		connection = cm.getConnection();
	}

	public QueryExecutor(Connection connection) {
		this.connection = connection;
	}

	/**
	 * 
	 * Note, will wait 1 second for connection validation to timeout
	 * 
	 * @param query
	 * @return
	 * @throws Exception 
	 */
	public DefaultTableModel executeQuery(String query) throws Exception {
		ResultSet rs = null;
		ResultSetMetaData metaData;
		DefaultTableModel tableModel = new DefaultTableModel();
		if (!"".equals(query)) {
		    if (this.connection.isValid(1)) {
		        Statement stmt = this.connection.createStatement();
    			
    			// if query match select or returning then use executeQuery else just execute
    			Matcher m = pattern.matcher(query);
    			if (m.find()) {
    				rs = stmt.executeQuery(query);
    				metaData = rs.getMetaData();
    
    				// Names of columns
    				Vector<String> columnNames = new Vector<String>();
    				int columnCount = metaData.getColumnCount();
    				for (int i = 1; i <= columnCount; i++) {
    					columnNames.add(metaData.getColumnName(i) + " (" + getDataTypeFromType(metaData.getColumnType(i)) + ")");
    				}
    
    				// Data of the table
    				Vector<Vector<Object>> data = new Vector<Vector<Object>>();
    				while (rs.next()) {
    					Vector<Object> vector = new Vector<Object>();
    					for (int i = 1; i <= columnCount; i++) {
    						vector.add(rs.getObject(i));
    					}
    					data.add(vector);
    				}
    
    				tableModel.setDataVector(data, columnNames);
    			} else {
    				stmt.execute(query);
    			}
		    } else {
		        throw new Exception("No valid connection.");
		    }
		}

		return tableModel;
	}
	
	private String getDataTypeFromType(final Integer javaSqlType) {
        return TYPES.get(javaSqlType);
	}

}
