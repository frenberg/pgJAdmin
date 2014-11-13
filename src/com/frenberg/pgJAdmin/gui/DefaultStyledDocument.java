package com.frenberg.pgJAdmin.gui;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.SwingWorker;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import com.frenberg.pgJAdmin.utils.Keywords;

@SuppressWarnings("serial")
public class DefaultStyledDocument extends javax.swing.text.DefaultStyledDocument {

    final StyleContext cont      = StyleContext.getDefaultStyleContext();
    final AttributeSet attrBlue  = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.BLUE);
    final AttributeSet attrBlack = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.BLACK);
    final Pattern      pattern   = Pattern.compile(Keywords.getRegexString(), Pattern.CASE_INSENSITIVE);

    /*
     * (non-Javadoc)
     * @see javax.swing.text.AbstractDocument#remove(int, int)
     */
    @Override
    public void remove(int offset, int length) throws BadLocationException {
        super.remove(offset, length);

        String text = getText(0, getLength());
        int start = findLastNonWordChar(text, offset);
        if (start < 0)
            start = 0;
        int end = findFirstNonWordChar(text, offset);

        if (pattern.matcher(text.substring(start, end)).matches()) {
            setCharacterAttributes(start, end - start, attrBlue, false);
        } else {
            setCharacterAttributes(start, end - start, attrBlack, false);
        }

    }

    /*
     * (non-Javadoc)
     * @see javax.swing.text.AbstractDocument#insertString(int,
     * java.lang.String, javax.swing.text.AttributeSet)
     */
    @Override
    public void insertString(int offset, String string, AttributeSet a) throws BadLocationException {
        super.insertString(offset, string, attrBlack);

        new MySwingWorker(offset, string).execute();

    }

    class Keyword {
        int          start;
        int          end;

        public Keyword(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }

    }

    class MySwingWorker extends SwingWorker<List<Keyword>, Keyword> {

        int offset;
        String string;

        public MySwingWorker(int offset, String string) {
            this.offset = offset;
            this.string = string;
        }

        @Override
        protected List<Keyword> doInBackground() throws Exception {
            List<Keyword> keywords = new LinkedList<Keyword>();
            String text = getText(0, getLength());
            int start = findLastNonWordChar(text, offset);
            if (start < 0) {
                start = 0;
            }
            int end = findFirstNonWordChar(text, offset + string.length());
            
            int wordStart = start;
            int wordEnd = start;
            Keyword keyword;

            while (wordEnd <= end && !isCancelled()) {
                if (wordEnd == end || String.valueOf(text.charAt(wordEnd)).matches("\\W")) {
                    if (pattern.matcher(text.substring(wordStart, wordEnd)).matches()) {
                        keyword = new Keyword(wordStart, wordEnd - wordStart);
                        keywords.add(keyword);
                        publish(keyword);
                    }
                    wordStart = wordEnd;
                }
                wordEnd++;
            }
            
            return keywords;
        }

        @Override
        protected void process(List<Keyword> chunks) {
            for (Keyword keyword : chunks) {
                setCharacterAttributes(keyword.getStart(), keyword.getEnd(), attrBlue, false);
            }
        }

    }

    private int findLastNonWordChar(String text, int index) {
        while (--index >= 0) {
            if (String.valueOf(text.charAt(index)).matches("\\W")) {
                break;
            }
        }
        return index;
    }

    private int findFirstNonWordChar(String text, int index) {
        while (index < text.length()) {
            if (String.valueOf(text.charAt(index)).matches("\\W")) {
                break;
            }
            index++;
        }
        return index;
    }

}
