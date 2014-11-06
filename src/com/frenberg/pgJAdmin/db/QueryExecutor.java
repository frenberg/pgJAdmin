package com.frenberg.pgJAdmin.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.table.DefaultTableModel;

public class QueryExecutor {

	private Connection connection;
	private Pattern pattern = Pattern.compile("select|returning", Pattern.CASE_INSENSITIVE);
	private HashMap<Integer, String> types;

	public QueryExecutor(ConnectionManager cm) throws SQLException {
		populateTypeMap();
		connection = cm.getConnection();
	}

	public QueryExecutor(Connection connection) {
		populateTypeMap();
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
		if (!"".equals(query) && this.connection.isValid(1)) {
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

		}

		return tableModel;
	}
	
	private String getDataTypeFromType(final Integer javaSqlType) {
		return types.get(javaSqlType);
	}
	
	private void populateTypeMap() {
		types = new HashMap<Integer, String>();
		types.put(new Integer(-16), "LONGNVARCHAR");
		types.put(new Integer(-15), "NCHAR");
		types.put(new Integer(-9), "NVARCHAR");
		types.put(new Integer(-8), "ROWID");
		types.put(new Integer(-7), "BIT");
		types.put(new Integer(-6), "TINYINT");
		types.put(new Integer(-5), "BIGINT");
		types.put(new Integer(-4), "LONGVARBINARY");
		types.put(new Integer(-3), "VARBINARY");
		types.put(new Integer(-2), "BINARY");
		types.put(new Integer(-1), "LONGVARCHAR");
		types.put(new Integer(0), "NULL");
		types.put(new Integer(1), "CHAR");
		types.put(new Integer(2), "NUMERIC");
		types.put(new Integer(3), "DECIMAL");
		types.put(new Integer(4), "INTEGER");
		types.put(new Integer(5), "SMALLINT");
		types.put(new Integer(6), "FLOAT");
		types.put(new Integer(7), "REAL");
		types.put(new Integer(8), "DOUBLE");
		types.put(new Integer(12), "VARCHAR");
		types.put(new Integer(16), "BOOLEAN");
		types.put(new Integer(70), "DATALINK");
		types.put(new Integer(91), "DATE");
		types.put(new Integer(92), "TIME");
		types.put(new Integer(93), "TIMESTAMP");
		types.put(new Integer(1111), "OTHER");
		types.put(new Integer(2000), "JAVA_OBJECT");
		types.put(new Integer(2001), "DISTINCT");
		types.put(new Integer(2002), "STRUCT");
		types.put(new Integer(2003), "ARRAY");
		types.put(new Integer(2004), "BLOB");
		types.put(new Integer(2005), "CLOB");
		types.put(new Integer(2006), "REF");
		types.put(new Integer(2009), "SQLXML");
		types.put(new Integer(2011), "SQLXML");
		
	}
}
