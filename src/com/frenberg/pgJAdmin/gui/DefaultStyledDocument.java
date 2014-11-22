package com.frenberg.pgJAdmin.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingWorker;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import com.frenberg.pgJAdmin.utils.Keywords;

@SuppressWarnings("serial")
public class DefaultStyledDocument extends
javax.swing.text.DefaultStyledDocument {

    final StyleContext cont = StyleContext.getDefaultStyleContext();
    final AttributeSet attrKeyword = cont.addAttribute(cont.getEmptySet(),
            StyleConstants.Foreground, Color.BLUE);
    final AttributeSet attrDefault = cont.addAttribute(cont.getEmptySet(),
            StyleConstants.Foreground, Color.BLACK);
    final AttributeSet attrComment = cont.addAttribute(cont.getEmptySet(),
            StyleConstants.Foreground, Color.ORANGE);
    final Pattern keywordPattern = Pattern.compile("(\\W)*" + Keywords.getRegexString(),
            Pattern.CASE_INSENSITIVE);
    final Pattern commentPattern = Pattern.compile("\\-\\-.*");
    final Pattern blockCommentPattern = Pattern.compile("\\/\\*.*?\\*\\/",
            Pattern.DOTALL);
    Font customFont = null;

    /*
     * (non-Javadoc)
     *
     * @see
     * javax.swing.text.DefaultStyledDocument#getFont(javax.swing.text.AttributeSet
     * )
     */
    @Override
    public Font getFont(AttributeSet attr) {

        if (customFont == null) {
            InputStream in = getClass().getResourceAsStream("/SourceCodePro-Regular.ttf");
            try {
                customFont = Font.createFont(Font.TRUETYPE_FONT, in);
            } catch (FontFormatException | IOException e) {
                System.err.println(e.getMessage());
                System.err.println("Fallback to default monospaced font");
                customFont = new Font("Monospaced", Font.PLAIN, StyleConstants.getFontSize(attr));
            }
        }

        return customFont.deriveFont(Font.PLAIN, StyleConstants.getFontSize(attr));
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.text.AbstractDocument#remove(int, int)
     */
    @Override
    public void remove(int offset, int length) throws BadLocationException {
        super.remove(offset, length);

        String text = getText(0, getLength());
        int start = findLastNonWordChar(text, offset);
        int end = findFirstNonWordChar(text, offset);

        if (keywordPattern.matcher(text.substring(start, end)).matches()) {
            setCharacterAttributes(start, end - start, attrKeyword, true);
        } else {
            setCharacterAttributes(start, end - start, attrDefault, true);
        }

        Matcher matcher;
        matcher = commentPattern.matcher(text);
        while (matcher.find()) {
            setCharacterAttributes(matcher.start(),
                    matcher.end() - matcher.start(), attrComment, true);
        }

        matcher = blockCommentPattern.matcher(text);
        while (matcher.find()) {
            setCharacterAttributes(matcher.start(),
                    matcher.end() - matcher.start(), attrComment, true);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.text.AbstractDocument#insertString(int,
     * java.lang.String, javax.swing.text.AttributeSet)
     */
    @Override
    public void insertString(int offset, String string, AttributeSet a)
            throws BadLocationException {
        super.insertString(offset, string, attrDefault);

        String text = getText(0, getLength());
        new KeywordSwingWorker(offset, string, text).execute();

    }

    class Keyword {
        int start;
        int end;

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

    class KeywordSwingWorker extends SwingWorker<List<Keyword>, Keyword> {

        int offset;
        String string;
        final String text;

        public KeywordSwingWorker(int offset, String string, String text) {
            this.offset = offset;
            this.string = string;
            this.text = text;
        }

        @Override
        protected List<Keyword> doInBackground() throws Exception {
            List<Keyword> keywords = new LinkedList<Keyword>();
            int start = findLastNonWordChar(text, offset);
            int end = findFirstNonWordChar(text, offset + string.length());

            int wordStart = start;
            int wordEnd = start;
            Keyword keyword;

            while (wordEnd <= end && !isCancelled()) {
                if (String.valueOf(text.charAt(wordEnd)).matches("\\W")) {
                    if (keywordPattern.matcher(
                            text.substring(wordStart, wordEnd)).matches()) {
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
                setCharacterAttributes(keyword.getStart(), keyword.getEnd(),
                        attrKeyword, false);
            }
        }

        @Override
        protected void done() {
            // TODO Auto-generated method stub
            super.done();
            Matcher matcher;
            matcher = commentPattern.matcher(text);
            while (matcher.find()) {
                setCharacterAttributes(matcher.start(),
                        matcher.end() - matcher.start(), attrComment, true);
            }

            matcher = blockCommentPattern.matcher(text);
            while (matcher.find()) {
                setCharacterAttributes(matcher.start(),
                        matcher.end() - matcher.start(), attrComment, true);
            }
        }

    }

    private int findLastNonWordChar(String text, int index) {
        while (--index >= 0) {
            if (String.valueOf(text.charAt(index)).matches("\\W")) {
                break;
            }
        }
        return index < 0 ? 0 : index;
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
