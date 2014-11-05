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
import javax.swing.SpringLayout;

import com.frenberg.pgJAdmin.db.ConnectionManager;

@SuppressWarnings("serial")
public class ConnectJDialog extends JDialog {

	private boolean useValues = false;
	private JTextField passwordField;
	private JTextField userField;
	private JTextField connectionStringField;
	private JTextField schemaField;
	
	public ConnectJDialog(JFrame owner) {
		super(owner);
		setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

		JPanel panel = new JPanel();
		getContentPane().add(panel);

		SpringLayout springLayout = new SpringLayout();
		panel.setLayout(springLayout);

		// User
		JLabel lblUser = new JLabel("User:");
		springLayout.putConstraint(SpringLayout.WEST, lblUser, 10, SpringLayout.WEST, panel);
		panel.add(lblUser);

		// Password
		JLabel lblPassword = new JLabel("Password:");
		springLayout.putConstraint(SpringLayout.WEST, lblPassword, 10, SpringLayout.WEST, panel);
		panel.add(lblPassword);

		// Connection String		
		JLabel lblConnectionString = new JLabel("Connection string:");
		springLayout.putConstraint(SpringLayout.NORTH, lblConnectionString, 84, SpringLayout.NORTH, panel);
		springLayout.putConstraint(SpringLayout.SOUTH, lblPassword, -18, SpringLayout.NORTH, lblConnectionString);
		panel.add(lblConnectionString);

		
		// Schema
		JLabel lblSchema = new JLabel("Schema:");
		springLayout.putConstraint(SpringLayout.NORTH, lblSchema, 18, SpringLayout.SOUTH, lblConnectionString);
		springLayout.putConstraint(SpringLayout.WEST, lblSchema, 10, SpringLayout.WEST, panel);
		panel.add(lblSchema);

		
		// Actions
		JButton btnSave = new JButton("Save");
		springLayout.putConstraint(SpringLayout.EAST, btnSave, -10, SpringLayout.EAST, panel);
		btnSave.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				useValues = true;
				closeDialog();
				
			}
		});
		panel.add(btnSave);
		
		JButton btnCancel = new JButton("Cancel");
		springLayout.putConstraint(SpringLayout.NORTH, btnCancel, 0, SpringLayout.NORTH, btnSave);
		springLayout.putConstraint(SpringLayout.EAST, btnCancel, -6, SpringLayout.WEST, btnSave);
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				closeDialog();
			}
		});
		panel.add(btnCancel);
		
		JButton btnTestConnection = new JButton("Test Connection");
		springLayout.putConstraint(SpringLayout.WEST, btnTestConnection, 10, SpringLayout.WEST, panel);
		springLayout.putConstraint(SpringLayout.NORTH, btnSave, 0, SpringLayout.NORTH, btnTestConnection);
		springLayout.putConstraint(SpringLayout.SOUTH, btnTestConnection, -10, SpringLayout.SOUTH, panel);
		btnTestConnection.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				testConnection();
			}
		});
		panel.add(btnTestConnection);
		
		passwordField = new JTextField();
		springLayout.putConstraint(SpringLayout.WEST, passwordField, 60, SpringLayout.EAST, lblPassword);
		springLayout.putConstraint(SpringLayout.EAST, passwordField, 0, SpringLayout.EAST, btnSave);
		panel.add(passwordField);
		passwordField.setColumns(10);
		
		userField = new JTextField();
		springLayout.putConstraint(SpringLayout.WEST, userField, 91, SpringLayout.EAST, lblUser);
		springLayout.putConstraint(SpringLayout.SOUTH, userField, -170, SpringLayout.SOUTH, panel);
		springLayout.putConstraint(SpringLayout.EAST, userField, -10, SpringLayout.EAST, panel);
		springLayout.putConstraint(SpringLayout.NORTH, lblUser, 6, SpringLayout.NORTH, userField);
		springLayout.putConstraint(SpringLayout.NORTH, passwordField, 6, SpringLayout.SOUTH, userField);
		panel.add(userField);
		userField.setColumns(10);
		
		connectionStringField = new JTextField();
		springLayout.putConstraint(SpringLayout.WEST, connectionStringField, 133, SpringLayout.WEST, panel);
		springLayout.putConstraint(SpringLayout.EAST, lblConnectionString, -6, SpringLayout.WEST, connectionStringField);
		springLayout.putConstraint(SpringLayout.SOUTH, passwordField, -6, SpringLayout.NORTH, connectionStringField);
		springLayout.putConstraint(SpringLayout.EAST, connectionStringField, -10, SpringLayout.EAST, panel);
		springLayout.putConstraint(SpringLayout.NORTH, connectionStringField, 78, SpringLayout.NORTH, panel);
		panel.add(connectionStringField);
		connectionStringField.setColumns(10);
		
		schemaField = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, schemaField, 6, SpringLayout.SOUTH, connectionStringField);
		springLayout.putConstraint(SpringLayout.WEST, schemaField, 71, SpringLayout.EAST, lblSchema);
		springLayout.putConstraint(SpringLayout.EAST, schemaField, 0, SpringLayout.EAST, btnSave);
		panel.add(schemaField);
		schemaField.setColumns(10);
				
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		setSize(450, 230);
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
