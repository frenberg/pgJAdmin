package com.frenberg.pgJAdmin.gui;

import javax.swing.text.Document;
import javax.swing.text.StyledEditorKit;

public class QueryEditorKit extends StyledEditorKit {

    private static final long serialVersionUID = 1L;

    @Override
    public Document createDefaultDocument() {
        return new DefaultStyledDocument();
    }

}
