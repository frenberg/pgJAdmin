package com.frenberg.pgJAdmin.db;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ConnectionManager {

	protected String user;
	protected String password;
	protected String host;
	protected String port;
	protected String database;
	protected String schema = null;

	public ConnectionManager() {
		this.loadSettings();
	}
	
	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public String getHost() {
		return host;
	}

	public String getPort() {
		return port;
	}

	public String getDatabase() {
		return database;
	}

	public String getSchema() {
		return schema;
	}
	
	public void setUser(String user) {
		this.user = user;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public boolean testConnection() {
	    boolean response = false;
	    try {
	        if (testConnection(getUser(), getPassword(), getHost(), getPort(), getDatabase(), getSchema())) {
	        	response = true;
	        }
	    } catch(Exception e) {
	        
	    }
	    return response;
	}
	
	public boolean testConnection(String user, String password, String host, String port, String database, String schema) throws Exception {
		
		Connection testConnection = null;
		Properties testConnectionProperties = new Properties();
		testConnectionProperties.put("user", user);
		testConnectionProperties.put("password", password);
		String connectionString = "jdbc:postgresql://" + host + ":" + port + "/" + database;

		try {
	        DriverManager.setLoginTimeout(5);
			testConnection = DriverManager.getConnection(connectionString, testConnectionProperties);
			
			if (!"".equals(schema)) {
				Statement stmt = testConnection.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT count(schema_name) > 0 FROM information_schema.schemata WHERE schema_name = '" + schema + "'");
				if (rs.next() && !rs.getBoolean(1)) {
					throw new Exception("Invalid schema.");
				}
				
			}
		} catch (SQLException e) {
			return false;
		}
		testConnection.close();
		return true;
	}

	public Connection getConnection() throws SQLException {
		Connection conn = null;
		Properties connectionProps = new Properties();
		connectionProps.put("user", user);
		connectionProps.put("password", password);
		String connectionString = "jdbc:postgresql://" + host + ":" + port + "/" + database;

		// jdbc:postgresql://host:port/database
		DriverManager.setLoginTimeout(5);
		conn = DriverManager.getConnection(connectionString, connectionProps);

		if (!"".equals(schema)) {
			// try to set search path
			Statement stmt = conn.createStatement();
			stmt.execute("SET search_path = " + schema);
		}

		return conn;
	}

	public boolean loadSettings() {
		HashMap<String, String> settings = new HashMap<String, String>(6);

		String filePath = System.getProperty("user.home")
				+ System.getProperty("file.separator") + ".pgjadmin.xml";

		File file = new File(filePath);

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document doc;
		try {
			db = dbf.newDocumentBuilder();
			doc = db.parse(file);

			NodeList list = doc.getDocumentElement().getChildNodes();
			for (int i = 0; i < list.getLength(); i++) {
				if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
					settings.put(list.item(i).getNodeName(), list.item(i)
							.getTextContent());
				}
			}
		} catch (Exception e) {
			return false;
		}
		
		this.user = settings.get("user");
		this.password = settings.get("password");
//		this.connectionString = settings.get("connectionString");
		this.host = settings.get("host");
		this.port = settings.get("port");
		this.database = settings.get("database");
		this.schema = settings.get("schema");
		
		return true;
	}

	private String buildXMLString(HashMap<String, String> settings)
			throws ParserConfigurationException, TransformerException {

		Element el = null;

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.newDocument();
		Element root = doc.createElement("settings");

		el = doc.createElement("user");
		el.appendChild(doc.createTextNode(settings.get("user")));
		root.appendChild(el);

		el = doc.createElement("password");
		el.appendChild(doc.createTextNode(settings.get("password")));
		root.appendChild(el);

		el = doc.createElement("host");
		el.appendChild(doc.createTextNode(settings.get("host")));
		root.appendChild(el);

		el = doc.createElement("port");
		el.appendChild(doc.createTextNode(settings.get("port")));
		root.appendChild(el);

		el = doc.createElement("database");
		el.appendChild(doc.createTextNode(settings.get("database")));
		root.appendChild(el);

		el = doc.createElement("schema");
		el.appendChild(doc.createTextNode(settings.get("schema")));
		root.appendChild(el);

		doc.appendChild(root);

		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer t = tf.newTransformer();
		t.setOutputProperty(OutputKeys.INDENT, "yes");

		StringWriter sw = new java.io.StringWriter();
		StreamResult sr = new StreamResult(sw);
		t.transform(new DOMSource(doc), sr);
		return sw.toString();

	}

	public boolean saveSettings() throws IOException {
		HashMap<String, String> settings = new HashMap<String, String>(4);
		settings.put("user", this.user);
		settings.put("password", this.password);
		settings.put("host", this.host);
		settings.put("port", this.port);
		settings.put("database", this.database);
		settings.put("schema", this.schema);

		String filePath = System.getProperty("user.home") + System.getProperty("file.separator") + ".pgjadmin.xml";
		try {
			String xml = buildXMLString(settings);

			PrintWriter out = new PrintWriter(new File(filePath));
			out.write(xml);
			out.close();
			return true;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

		return false;
	}

	
	/**
	 * 
	 * @return
	 * @throws SQLException 
	 */
	public List<String> getAvailableTables() throws SQLException {
        List<String> tables = new ArrayList<String>();
        String schema = !"".equals(this.schema) ? this.schema : "public"; 
        
        Connection con = getConnection();
        Statement stmt = con.createStatement();
        
        ResultSet rs = stmt.executeQuery("SELECT tablename FROM pg_tables WHERE schemaname = '"+ schema + "' ORDER BY tablename");
        while (rs.next()) {
            tables.add(rs.getString(1));
        }
            
        
        return tables;
    }


}
