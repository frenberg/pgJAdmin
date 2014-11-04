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

public class QueryExecutor {

	private Connection connection;
	private Pattern pattern = Pattern.compile("select|returning");

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
					columnNames.add(metaData.getColumnName(i));
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
}
