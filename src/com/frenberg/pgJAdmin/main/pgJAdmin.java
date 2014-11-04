package com.frenberg.pgJAdmin.main;

import javax.swing.SwingUtilities;

import com.frenberg.pgJAdmin.gui.pgJFrame;

public class pgJAdmin {

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				new pgJFrame("pgJAdmin");
			}
		});

	}

}
