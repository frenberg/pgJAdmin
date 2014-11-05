package com.frenberg.pgJAdmin.gui;

import java.awt.Color;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

@SuppressWarnings("serial")
public class DefaultStyledDocument extends javax.swing.text.DefaultStyledDocument {


    final StyleContext cont = StyleContext.getDefaultStyleContext();
    final AttributeSet attr = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.BLUE);
    final AttributeSet attrBlack = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.BLACK);
	
    
	/* (non-Javadoc)
	 * @see javax.swing.text.AbstractDocument#remove(int, int)
	 */
	@Override
	public void remove(int offs, int len) throws BadLocationException {
		// TODO Auto-generated method stub
		super.remove(offs, len);
		
		String text = getText(0, getLength());
		int before = findLastNonWordChar(text, offs);
		if (before < 0)
			before = 0;
		int after = findFirstNonWordChar(text, offs);

		if (text.substring(before, after).matches("(\\W)*(asc|create|desc|from|insert\\ into|join|limit|on|or|order\\ by|select|table|update|values|where)")) {
			setCharacterAttributes(before, after - before, attr, false);
		} else {
			setCharacterAttributes(before, after - before, attrBlack, false);
		}
		
	}

	/* (non-Javadoc)
	 * @see javax.swing.text.AbstractDocument#insertString(int, java.lang.String, javax.swing.text.AttributeSet)
	 */
	@Override
	public void insertString(int offs, String str, AttributeSet a)
			throws BadLocationException {
		// TODO Auto-generated method stub
		super.insertString(offs, str, a);

        String text = getText(0, getLength());
        int before = findLastNonWordChar(text, offs);
        if (before < 0) before = 0;
        int after = findFirstNonWordChar(text, offs + str.length());
        int wordL = before;
        int wordR = before;

        while (wordR <= after) {
            if (wordR == after || String.valueOf(text.charAt(wordR)).matches("\\W")) {
           		if (text.substring(wordL, wordR).matches("(\\W)*(asc|create|desc|from|insert\\ into|join|limit|on|or|order\\ by|select|table|update|values|where)"))
                    setCharacterAttributes(wordL, wordR - wordL, attr, false);
                else
                    setCharacterAttributes(wordL, wordR - wordL, attrBlack, false);
                wordL = wordR;
            }
            wordR++;
        }
    }
	
    private int findLastNonWordChar (String text, int index) {
        while (--index >= 0) {
            if (String.valueOf(text.charAt(index)).matches("\\W")) {
                break;
            }
        }
        return index;
    }

    private int findFirstNonWordChar (String text, int index) {
        while (index < text.length()) {
            if (String.valueOf(text.charAt(index)).matches("\\W")) {
                break;
            }
            index++;
        }
        return index;
    }

}