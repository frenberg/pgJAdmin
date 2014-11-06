package com.frenberg.pgJAdmin.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class CodeComplete implements DocumentListener {
	protected JTextPane textPane;
	private final List<String> words;

	public static enum Mode {
		INSERT, COMPLETION
	};

	private Mode mode = Mode.INSERT;

	public CodeComplete(JTextPane textPane) {
		super();
		this.textPane = textPane;
		this.words = new ArrayList<String>(20);
		this.words.add("asc");
		this.words.add("create");
		this.words.add("desc");
		this.words.add("from");
		this.words.add("insert into");
		this.words.add("join");
		this.words.add("limit");
		this.words.add("on");
		this.words.add("or");
		this.words.add("order by");
		this.words.add("select");
		this.words.add("table");
		this.words.add("update");
		this.words.add("values");
		this.words.add("where");
	}

	@Override
	public void insertUpdate(DocumentEvent ev) {
		if (ev.getLength() != 1) {
			return;
		}

		int pos = ev.getOffset();
		String content = null;
		try {
			content = textPane.getText(0, pos + 1);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		// Find where the word starts
		int w;
		for (w = pos; w >= 0; w--) {
			if (!Character.isLetter(content.charAt(w))) {
				break;
			}
		}
		if (pos - w < 2) {
			// Too few chars
			return;
		}

		String prefix = content.substring(w + 1).toLowerCase();
		int n = Collections.binarySearch(words, prefix);
		if (n < 0 && -n <= words.size()) {
			String match = words.get(-n - 1);
			if (match.startsWith(prefix)) { //TODO check if this check is needed?
				// A completion is found
				String completion = match.substring(pos - w);
				// We cannot modify Document from within notification,
				// so we submit a task that does the change later
				SwingUtilities.invokeLater(new CompletionTask(completion,
						pos + 1));
			}
		} else {
			// Nothing found
			mode = Mode.INSERT;
		}
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		// TODO Auto-generated method stub

	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	private class CompletionTask implements Runnable {
		String completion;
		int position;

		CompletionTask(String completion, int position) {
			this.completion = completion;
			this.position = position;
		}

		public void run() {
			// textPane.insert(completion, position);
			try {
				StyledDocument doc = textPane.getStyledDocument();
//				doc.insertString(position, completion, null);
				StyleContext context = new StyleContext();
				// build a style
				Style style = context.addStyle("test", null);
				// set some style properties
				StyleConstants.setForeground(style, Color.BLUE);
				
				doc.insertString(position, completion, style);
				
				
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			textPane.setCaretPosition(position + completion.length());
			textPane.moveCaretPosition(position);
			mode = Mode.COMPLETION;
		}
 	}

}
