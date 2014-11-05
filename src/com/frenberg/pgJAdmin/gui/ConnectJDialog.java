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
	private JTextField passwordField;
	private JTextField userField;
	private JTextField connectionStringField;
	private JTextField schemaField;
	
	public ConnectJDialog(JFrame owner) {
		super(owner);
		setTitle("Connect to database");
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

		JPanel panel = new JPanel();
		getContentPane().add(panel);
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("117px"),
				FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("24px"),
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
				RowSpec.decode("29px"),}));

		// User
		JLabel lblUser = new JLabel("User:");
		panel.add(lblUser, "2, 2, left, center");

		// Password
		JLabel lblPassword = new JLabel("Password:");
		panel.add(lblPassword, "2, 4, left, center");

		// Connection String		
		JLabel lblConnectionString = new JLabel("Connection string:");
		panel.add(lblConnectionString, "2, 6, left, center");

		
		// Schema
		JLabel lblSchema = new JLabel("Schema:");
		panel.add(lblSchema, "2, 8, left, center");

		
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
		panel.add(btnSave, "8, 10, left, top");
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				closeDialog();
			}
		});
		panel.add(btnCancel, "6, 10, left, top");
		
		JButton btnTestConnection = new JButton("Test Connection");
		btnTestConnection.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				testConnection();
			}
		});
		panel.add(btnTestConnection, "2, 10, 3, 1, left, top");
		
		passwordField = new JTextField();
		panel.add(passwordField, "4, 4, 5, 1, fill, top");
		passwordField.setColumns(10);
		
		userField = new JTextField();
		panel.add(userField, "4, 2, 5, 1, fill, top");
		userField.setColumns(10);
		
		connectionStringField = new JTextField();
		panel.add(connectionStringField, "4, 6, 5, 1, fill, top");
		connectionStringField.setColumns(10);
		
		schemaField = new JTextField();
		panel.add(schemaField, "4, 8, 5, 1, fill, top");
		schemaField.setColumns(10);
				
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		setSize(450, 230);
		setLocationRelativeTo(owner);
		setResizable(false);
		

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

	public String getConnectionString() {
		return connectionStringField.getText();
	}

	public void setConnectionString(String connectionString) {
		this.connectionStringField.setText(connectionString);
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
		if (cm.testConnection(getUser(), getPassword(), getConnectionString(), getSchema())) {
			JOptionPane.showMessageDialog(this, "Connection established", "Testing connection", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this, "Connection failed", "Testing connection", JOptionPane.ERROR_MESSAGE);
		}
		
	}
}
