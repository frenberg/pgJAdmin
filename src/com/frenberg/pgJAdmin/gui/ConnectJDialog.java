package com.frenberg.pgJAdmin.gui;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.frenberg.pgJAdmin.db.ConnectionManager;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

@SuppressWarnings("serial")
public class ConnectJDialog extends JDialog {

	private boolean useValues = false;
	private JTextField userField;
	private JTextField passwordField;
	private JTextField hostField;
	private JTextField portField;
	private JTextField databaseField;
	private JTextField schemaField;

	public ConnectJDialog(JFrame owner) {
		super(owner);
		setResizable(false);
		setTitle("Connect to database");
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

		JPanel panel = new JPanel();
		getContentPane().add(panel);
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("117px"),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("24px:grow"),
				ColumnSpec.decode("116px"),
				ColumnSpec.decode("86px"),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("75px"),},
			new RowSpec[] {
				FormFactory.UNRELATED_GAP_ROWSPEC,
				RowSpec.decode("28px"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("28px"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("28px"),
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("28px"),
				RowSpec.decode("29px"),
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				RowSpec.decode("29px"),}));

		// User
		JLabel lblUser = new JLabel("User:");
		panel.add(lblUser, "2, 2, left, center");

		// Password
		JLabel lblPassword = new JLabel("Password:");
		panel.add(lblPassword, "2, 4, left, center");

		// Hostname
		JLabel lblHost = new JLabel("Host:");
		panel.add(lblHost, "2, 6, left, center");
		
		// Port
		JLabel lblPort = new JLabel("Port:");
		panel.add(lblPort, "2, 8, left, center");
		
		// Database
		JLabel lblConnectionString = new JLabel("Database:");
		panel.add(lblConnectionString, "2, 9, left, center");

		// Schema
		JLabel lblSchema = new JLabel("Schema:");
		panel.add(lblSchema, "2, 10, left, center");

		// Actions
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				useValues = true;
				closeDialog();

			}
		});
		panel.add(btnSave, "8, 12, left, top");
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				closeDialog();
			}
		});
		panel.add(btnCancel, "6, 12, left, top");

		JButton btnTestConnection = new JButton("Test Connection");
		btnTestConnection.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				testConnection();
			}
		});
		panel.add(btnTestConnection, "2, 12, 3, 1, left, top");

		userField = new JTextField();
		panel.add(userField, "4, 2, 5, 1, fill, top");
		userField.setColumns(10);

		passwordField = new JTextField();
		panel.add(passwordField, "4, 4, 5, 1, fill, top");
		passwordField.setColumns(10);

		hostField = new JTextField();
		panel.add(hostField, "4, 6, 5, 1, fill, top");
		hostField.setColumns(10);
		
		portField = new JTextField();
		panel.add(portField, "4, 8, 5, 1, fill, top");
		portField.setColumns(10);

		databaseField = new JTextField();
		panel.add(databaseField, "4, 9, 5, 1, fill, top");
		databaseField.setColumns(10);

		schemaField = new JTextField();
		panel.add(schemaField, "4, 10, 5, 1, fill, top");
		schemaField.setColumns(10);

		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		setSize(450, 260);
		setLocationRelativeTo(owner);

	}

	public String getUser() {
		return userField.getText();
	}

	public void setUser(String user) {
		this.userField.setText(user);
	}

	public String getPassword() {
		return passwordField.getText();
	}

	public void setPassword(String password) {
		this.passwordField.setText(password);
	}

	public String getHost() {
		return hostField.getText();
	}

	public void setHost(String host) {
		this.hostField.setText(host);
	}

	public String getPort() {
		return portField.getText();
	}

	public void setPort(String port) {
		this.portField.setText(port);
	}

	public String getDatabase() {
		return databaseField.getText();
	}

	public void setDatabase(String database) {
		this.databaseField.setText(database);
	}

	public String getSchema() {
		return schemaField.getText();
	}

	public void setSchema(String schema) {
		this.schemaField.setText(schema);
	}

	public boolean useValues() {
		return useValues;
	}

	private void closeDialog() {
		setVisible(false);
	}

	private void testConnection() {
		ConnectionManager cm = new ConnectionManager();
		try {
			if (cm.testConnection(getUser(), getPassword(), getHost(), getPort(),
					getDatabase(), getSchema())) {
				JOptionPane.showMessageDialog(this, "Connection successful",
						"Testing connection", JOptionPane.PLAIN_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this, "Connection failed",
						"Testing connection", JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, e.getMessage(),
			        "Testing connection", JOptionPane.ERROR_MESSAGE);

		}

	}
}
