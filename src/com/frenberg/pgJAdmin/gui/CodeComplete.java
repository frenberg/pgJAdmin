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

import com.frenberg.pgJAdmin.utils.Keywords;

public class CodeComplete implements DocumentListener {
    protected JTextPane  textPane;
    private List<String> words = new ArrayList<String>(20);

    public static enum Mode {
        INSERT, COMPLETION
    };

    private Mode mode = Mode.INSERT;

    public CodeComplete(JTextPane textPane) {
        super();
        this.textPane = textPane;
        this.words = Keywords.getKeywords();
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
            if (match.startsWith(prefix)) { // TODO check if this check is
                                            // needed?
                // A completion is found
                String completion = match.substring(pos - w);
                // Are we typing in upper case?
                if (Character.isUpperCase(content.substring(w + 1).codePointAt(prefix.length() - 1))) {
                    completion = completion.toUpperCase();
                }
                // We cannot modify Document from within notification,
                // so we submit a task that does the change later
                SwingUtilities.invokeLater(new CompletionTask(completion, pos + 1));
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

    /**
     * @param words
     *            MUST be a sorted list of words!
     */
    public void appendSortedListOfWordsToAutoComplete(List<String> words) {
        for (int index1 = 0, index2 = 0; index2 < words.size(); index1++) {
            if (index1 == this.words.size() || 0 < this.words.get(index1).compareTo(words.get(index2))) {
                this.words.add(index1, words.get(index2++));
            }
        }

    }

    private class CompletionTask implements Runnable {
        String completion;
        int    position;

        CompletionTask(String completion, int position) {
            this.completion = completion;
            this.position = position;
        }

        public void run() {
            // textPane.insert(completion, position);
            try {
                StyledDocument doc = textPane.getStyledDocument();
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
