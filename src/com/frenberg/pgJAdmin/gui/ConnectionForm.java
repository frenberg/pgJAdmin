package com.frenberg.pgJAdmin.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class ConnectionForm extends JPanel {
	private boolean useValues = false;
	private JTextField userField;
	private JTextField passwordField;
	private JTextField connectionStringField;
	private JTextField schemaField;

	public ConnectionForm() {
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);

		// User
		JLabel lblUser = new JLabel("User:");
		springLayout.putConstraint(SpringLayout.NORTH, lblUser, 10, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, lblUser, 10, SpringLayout.WEST, this);
		add(lblUser);

		userField = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, userField, 4, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, userField, 101, SpringLayout.EAST, lblUser);
		add(userField);
		userField.setColumns(20);

		// Password
		JLabel lblPassword = new JLabel("Password:");
		springLayout.putConstraint(SpringLayout.NORTH, lblPassword, 18, SpringLayout.SOUTH, lblUser);
		springLayout.putConstraint(SpringLayout.WEST, lblPassword, 0, SpringLayout.WEST, lblUser);
		add(lblPassword);

		passwordField = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, passwordField, 6, SpringLayout.SOUTH, userField);
		springLayout.putConstraint(SpringLayout.EAST, userField, 0, SpringLayout.EAST, passwordField);
		springLayout.putConstraint(SpringLayout.WEST, passwordField, 70, SpringLayout.EAST, lblPassword);
		springLayout.putConstraint(SpringLayout.EAST, passwordField, -10, SpringLayout.EAST, this);
		add(passwordField);
		passwordField.setColumns(10);

		// Connection String		
		JLabel lblConnectionString = new JLabel("Connection string:");
		springLayout.putConstraint(SpringLayout.NORTH, lblConnectionString, 18, SpringLayout.SOUTH, lblPassword);
		springLayout.putConstraint(SpringLayout.WEST, lblConnectionString, 0, SpringLayout.WEST, lblUser);
		add(lblConnectionString);

		connectionStringField = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, connectionStringField, 6, SpringLayout.SOUTH, passwordField);
		springLayout.putConstraint(SpringLayout.WEST, connectionStringField, 0, SpringLayout.WEST, userField);
		springLayout.putConstraint(SpringLayout.EAST, connectionStringField, -10, SpringLayout.EAST, this);
		add(connectionStringField);
		connectionStringField.setColumns(10);

		
		// Schema
		schemaField = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, schemaField, 6, SpringLayout.SOUTH, connectionStringField);
		springLayout.putConstraint(SpringLayout.EAST, schemaField, -10, SpringLayout.EAST, this);
		add(schemaField);
		schemaField.setColumns(10);

		JLabel lblSchema = new JLabel("Schema:");
		springLayout.putConstraint(SpringLayout.WEST, schemaField, 81, SpringLayout.EAST, lblSchema);
		springLayout.putConstraint(SpringLayout.NORTH, lblSchema, 6, SpringLayout.NORTH, schemaField);
		springLayout.putConstraint(SpringLayout.WEST, lblSchema, 0, SpringLayout.WEST, lblUser);
		add(lblSchema);

		
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
		springLayout.putConstraint(SpringLayout.SOUTH, btnSave, -10, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, btnSave, 0, SpringLayout.EAST, userField);
		add(btnSave);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				closeDialog();
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, btnCancel, 0, SpringLayout.NORTH, btnSave);
		springLayout.putConstraint(SpringLayout.EAST, btnCancel, -6, SpringLayout.WEST, btnSave);
		add(btnCancel);
	
	}

	public JTextField getUserField() {
		return userField;
	}

	public JTextField getPasswordField() {
		return passwordField;
	}

	public JTextField getConnectionStringField() {
		return connectionStringField;
	}
	
	public JTextField getSchemaField() {
		return schemaField;
	}
	
	public boolean useValues() {
		return useValues;
	}
	
	private void closeDialog() {
		JDialog dlg = (JDialog) SwingUtilities.getWindowAncestor(this);
		dlg.setVisible(false);
		
	}
}
