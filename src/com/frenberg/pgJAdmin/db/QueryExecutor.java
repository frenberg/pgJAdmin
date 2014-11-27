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
    static final ImmutableSortedMap<Integer, String> TYPES = new ImmutableSortedMap.Builder<Integer, String>(Ordering.natural())
            .put(-16, "LONGNVARCHAR")
            .put(-15, "NCHAR")
            .put(-9, "NVARCHAR")
            .put(-8, "ROWID")
            .put(-7, "BIT")
            .put(-6, "TINYINT")
            .put(-5, "BIGINT")
            .put(-4, "LONGVARBINARY")
            .put(-3, "VARBINARY")
            .put(-2, "BINARY")
            .put(-1, "LONGVARCHAR")
            .put(0, "NULL")
            .put(1, "CHAR")
            .put(2, "NUMERIC")
            .put(3, "DECIMAL")
            .put(4, "INTEGER")
            .put(5, "SMALLINT")
            .put(6, "FLOAT")
            .put(7, "REAL")
            .put(8, "DOUBLE")
            .put(12, "VARCHAR")
            .put(16, "BOOLEAN")
            .put(70, "DATALINK")
            .put(91, "DATE")
            .put(92, "TIME")
            .put(93, "TIMESTAMP")
            .put(1111, "OTHER")
            .put(2000, "JAVA_OBJECT")
            .put(2001, "DISTINCT")
            .put(2002, "STRUCT")
            .put(2003, "ARRAY")
            .put(2004, "BLOB")
            .put(2005, "CLOB")
            .put(2006, "REF")
            .put(2009, "SQLXML")
            .put(2011, "SQLXML")
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
		ResultSet rs;
		ResultSetMetaData metaData;
		DefaultTableModel tableModel = new DefaultTableModel();
		if (!"".equals(query)) {
		    if (this.connection.isValid(1)) {
		        Statement stmt = this.connection.createStatement();

				boolean hasResulset = stmt.execute(query);
				int updateCount = stmt.getUpdateCount();


				while (hasResulset || (updateCount != -1)) {
					if (hasResulset){
						rs = stmt.getResultSet();
						if (rs != null) {
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
							rs.close();
						}
					}
					hasResulset = stmt.getMoreResults();
					updateCount = stmt.getUpdateCount();
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
