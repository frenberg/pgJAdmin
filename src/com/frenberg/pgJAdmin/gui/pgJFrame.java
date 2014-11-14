/**
 * 
 */
package com.frenberg.pgJAdmin.gui;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import org.postgresql.util.PSQLException;

import com.frenberg.pgJAdmin.db.ConnectionManager;
import com.frenberg.pgJAdmin.db.QueryExecutor;
import com.frenberg.pgJAdmin.gui.CodeComplete.Mode;

/**
 * @author frenberg
 *
 */
public class pgJFrame extends JFrame {

    private static final long   serialVersionUID  = 3484293122472901955L;
    protected ConnectionManager connectionManager = new ConnectionManager();
    protected UndoManager       undoManager       = new UndoManager();
    protected UndoAction        undoAction;
    protected RedoAction        redoAction;
    protected JTextPane         queryTextPane;
    protected JTable            outputTable;
    protected CodeComplete      codeCompletetionDocumentListener;
    protected JFileChooser      filechooser       = new JFileChooser();

    /**
     * @param title
     * @throws HeadlessException
     */
    public pgJFrame(String title) throws HeadlessException {
        super(title);
        setBounds(100, 100, 800, 600);

        getContentPane().setLayout(new BorderLayout(0, 0));
        buildMenu();

        JSplitPane splitPane = buildGui();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getContentPane().add(splitPane, BorderLayout.CENTER);
        setVisible(true);
        splitPane.setDividerLocation(.5f);

        filechooser.setAcceptAllFileFilterUsed(false);
        filechooser.setFileFilter(new FileFilter() {

            @Override
            public String getDescription() {
                return "SQL files";
            }

            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }

                String extension = null;
                String s = f.getName();
                int i = s.lastIndexOf('.');

                if (i > 0 && i < s.length() - 1) {
                    extension = s.substring(i + 1).toLowerCase();
                }

                if (extension != null) {
                    if (extension.equals("sql")) {
                        return true;
                    } else {
                        return false;
                    }
                }

                return false;
            }
        });

    }

    protected void buildMenu() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        ConnectAction connectAction = new ConnectAction();
        JMenuItem mntmConnect = new JMenuItem(connectAction);
        fileMenu.add(mntmConnect);

        OpenAction openAction = new OpenAction();
        JMenuItem mntmOpen = new JMenuItem(openAction);
        fileMenu.add(mntmOpen);
        SaveAction saveAction = new SaveAction();
        JMenuItem mntmSave = new JMenuItem(saveAction);
        fileMenu.add(mntmSave);
        JMenuItem mntmClose = new JMenuItem("Close");
        mntmClose.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                // save changes
                System.exit(JFrame.EXIT_ON_CLOSE);

            }
        });
        fileMenu.add(mntmClose);

        JMenu editMenu = new JMenu("Edit");
        menuBar.add(editMenu);

        undoAction = new UndoAction();
        JMenuItem mntmUndo = new JMenuItem(undoAction);
        mntmUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMask()));
        redoAction = new RedoAction();
        JMenuItem mntmRedo = new JMenuItem(redoAction);
        mntmRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.SHIFT_DOWN_MASK
                | Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        editMenu.add(mntmUndo);
        editMenu.add(mntmRedo);

        JMenuItem mntmCut = new JMenuItem("Cut");
        editMenu.add(mntmCut);

        JMenuItem mntmCopy = new JMenuItem("Copy");
        editMenu.add(mntmCopy);

        JMenuItem mntmPaste = new JMenuItem("Paste");
        editMenu.add(mntmPaste);

        JMenu queryMenu = new JMenu("Query");
        menuBar.add(queryMenu);

        QueryAction queryAction = new QueryAction();
        JMenuItem mntmExecuteQuery = new JMenuItem(queryAction);
        mntmExecuteQuery.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMask()));

        queryMenu.add(mntmExecuteQuery);
        
        IndexAction indexAction = new IndexAction();
        JMenuItem mntmIndexTables = new JMenuItem(indexAction);
        queryMenu.add(mntmIndexTables);
        
    }

    protected JSplitPane buildGui() {
        JSplitPane splitPane = new JSplitPane();
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);

//        DefaultStyledDocument doc = new DefaultStyledDocument();
//        queryTextPane = new JTextPane(doc);
        queryTextPane = new JTextPane();
        queryTextPane.setEditorKit(new QueryEditorKit());
        queryTextPane.getDocument().addUndoableEditListener(new MyUndoableEditListener());
        codeCompletetionDocumentListener = new CodeComplete(queryTextPane);
        queryTextPane.getDocument().addDocumentListener(codeCompletetionDocumentListener);
        InputMap im = queryTextPane.getInputMap();
        ActionMap am = queryTextPane.getActionMap();
        im.put(KeyStroke.getKeyStroke("ENTER"), "commit");
        am.put("commit", new SelectAutocompleteAction());

        // OUTPUT TEXTAREA
        outputTable = new JTable();
        outputTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // ADD QUERY TEXTAREA TO SCROLLPANE
        JScrollPane queryScrollPane = new JScrollPane(queryTextPane);
        splitPane.setLeftComponent(queryScrollPane);

        // ADD OUTPUT TEXTAREA TO OTHER SCROLLPANE
        JScrollPane outputScrollPane = new JScrollPane(outputTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        splitPane.setRightComponent(outputScrollPane);

        return splitPane;
    }

    protected void showConnectionDialog() {
        connectionManager.loadSettings();

        ConnectJDialog connectionDialog = new ConnectJDialog(this);
        connectionDialog.setUser(connectionManager.getUser());
        connectionDialog.setPassword(connectionManager.getPassword());
        if (connectionManager.getConnectionString() != null && !"".equals(connectionManager.getConnectionString())) {
            connectionDialog.setConnectionString(connectionManager.getConnectionString());
        } else {
            connectionDialog.setConnectionString("jdbc:postgresql://[host]:[port]/[database]");
        }
        connectionDialog.setSchema(connectionManager.getSchema());
        connectionDialog.setVisible(true); // this will halt and go to modality
                                           // mode

        if (connectionDialog.useValues()) {
            connectionManager.setUser(connectionDialog.getUser().trim());
            connectionManager.setPassword(connectionDialog.getPassword().trim());
            connectionManager.setConnectionString(connectionDialog.getConnectionString().trim());
            connectionManager.setSchema(connectionDialog.getSchema().trim());

            try {
                connectionManager.saveSettings();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                JOptionPane.showConfirmDialog(rootPane, e1.getMessage(), "Failed to save settings",
                        JOptionPane.OK_OPTION);
            }
            codeCompletetionDocumentListener.resetKeywords();
            new IndexAction().actionPerformed(new ActionEvent(pgJFrame.this, ActionEvent.ACTION_PERFORMED, "Index tables"));
        
        } else {
            System.err.println("Not saving values");
        }

        connectionDialog.dispose();
    }

    protected boolean extendCodeComplete() {
        List<String> sortedTables = null;
        try {
            sortedTables = connectionManager.getAvailableTables();
        } catch(Exception e) {
            return false;
        }
        codeCompletetionDocumentListener.appendSortedListOfWordsToAutoComplete(sortedTables);
        return true;
    }

    protected class MyUndoableEditListener implements UndoableEditListener {
        public void undoableEditHappened(UndoableEditEvent e) {
            // Remember the edit and update the menus (Unless its a style change (color keywords)
            if (!e.getEdit().getPresentationName().equals("style change")) {
                undoManager.addEdit(e.getEdit());
                undoAction.updateUndoState();
                redoAction.updateRedoState();
            }
        }
    }

    protected class UndoAction extends AbstractAction {
        private static final long serialVersionUID = -7585936496429419608L;

        public UndoAction() {
            super("Undo");
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                undoManager.undo();
            } catch (CannotUndoException ex) {
                System.out.println("Unable to undo: " + ex);
                ex.printStackTrace();
            }
            updateUndoState();
            redoAction.updateRedoState();

        }

        protected void updateUndoState() {
            if (undoManager.canUndo()) {
                setEnabled(true);
                putValue(Action.NAME, undoManager.getUndoPresentationName());
            } else {
                setEnabled(false);
                putValue(Action.NAME, "Undo");
            }
        }
    }

    protected class RedoAction extends AbstractAction {

        private static final long serialVersionUID = 7047804662988770565L;

        public RedoAction() {
            super("Redo");
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                undoManager.redo();
            } catch (CannotRedoException ex) {
                System.out.println("Unable to redo: " + ex);
                ex.printStackTrace();
            }
            updateRedoState();
            undoAction.updateUndoState();

        }

        protected void updateRedoState() {
            if (undoManager.canRedo()) {
                setEnabled(true);
                putValue(Action.NAME, undoManager.getRedoPresentationName());
            } else {
                setEnabled(false);
                putValue(Action.NAME, "Redo");
            }
        }

    }

    @SuppressWarnings("serial")
    protected class ConnectAction extends AbstractAction {

        public ConnectAction() {
            super("Connect");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            showConnectionDialog();
        }

    }

    @SuppressWarnings("serial")
    protected class QueryAction extends AbstractAction {

        public QueryAction() {
            super("Execute query");
        }

        @SuppressWarnings("rawtypes")
        @Override
        public void actionPerformed(ActionEvent e) {
            SwingWorker worker = new SwingWorker<DefaultTableModel, Void>() {
                @Override
                protected DefaultTableModel doInBackground() throws Exception {
                    DefaultTableModel m = null;
                    String query = (null != queryTextPane.getSelectedText()) ? queryTextPane.getSelectedText() : queryTextPane.getText();
                    
                    try {
                        QueryExecutor qe = new QueryExecutor(connectionManager);
                        m = qe.executeQuery(query);
                    } catch (Exception e1) {
                        if (e1 instanceof PSQLException) {
                            JOptionPane.showMessageDialog(pgJFrame.this, ((PSQLException) e1).getMessage());
                        } else {
                            // show connection dialog
                            new ConnectAction().actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
                                    "Connect"));
                        }
                    }
                    return m;
                }

                @Override
                protected void done() {
                    System.err.println("Thread done, getting result.");
                    try {
                        DefaultTableModel m = get();
                        if (null != m) {
                            outputTable.setModel(m);
                        }
                    } catch (InterruptedException ignore) {
                    } catch (java.util.concurrent.ExecutionException e) {
                        String why = null;
                        Throwable cause = e.getCause();
                        if (cause != null) {
                            why = cause.getMessage();
                        } else {
                            why = e.getMessage();
                        }
                        System.err.println("An error occured: " + why);
                    }

                }

            };
            worker.execute();

        }

    }

    @SuppressWarnings("serial")
    protected class SelectAutocompleteAction extends AbstractAction {

        public void actionPerformed(ActionEvent ev) {
            if (codeCompletetionDocumentListener.getMode() == Mode.COMPLETION) {
                int pos = queryTextPane.getSelectionEnd();
                try {
                    queryTextPane.getDocument().insertString(pos, " ", null);
                } catch (BadLocationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                queryTextPane.setCaretPosition(pos + 1);
                codeCompletetionDocumentListener.setMode(Mode.INSERT);
            } else {
                queryTextPane.replaceSelection("\n");
            }
        }
    }

    @SuppressWarnings("serial")
    protected class SaveAction extends AbstractAction {

        public SaveAction() {
            super("Save");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int returnVal = filechooser.showSaveDialog(pgJFrame.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = filechooser.getSelectedFile();

                FileWriter fileOutputStream;
                try {
                    fileOutputStream = new FileWriter(file);
                    BufferedWriter out = new BufferedWriter(fileOutputStream);
                    out.write(queryTextPane.getDocument().getText(0, queryTextPane.getDocument().getLength()));
                    out.close();
                } catch (IOException | BadLocationException e1) {
                    JOptionPane.showMessageDialog(pgJFrame.this, e1.getMessage(), "Could not save file", JOptionPane.PLAIN_MESSAGE);
                }

            }
        }

    }

    @SuppressWarnings("serial")
    protected class OpenAction extends AbstractAction {

        public OpenAction() {
            super("Open");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int returnVal = filechooser.showOpenDialog(pgJFrame.this);
            
            if (returnVal == JFileChooser.APPROVE_OPTION) {

                try {
                    File file = filechooser.getSelectedFile();
                    DefaultStyledDocument doc = (DefaultStyledDocument) queryTextPane.getDocument();
                    queryTextPane.getEditorKit().read(new FileInputStream(file), doc, 0);
                } catch(IOException | BadLocationException e1) {
                    JOptionPane.showMessageDialog(pgJFrame.this, e1.getMessage(), "Could not open file", JOptionPane.PLAIN_MESSAGE);
                }
            }
        }

    }
    
    
    @SuppressWarnings("serial")
    protected class IndexAction extends AbstractAction {
        
        public IndexAction() {
            super("Index tables");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            new SwingWorker<Boolean, Void>() {

                @Override
                protected Boolean doInBackground() throws Exception {
                    boolean result = extendCodeComplete();
                    if (result) {
                        System.err.println("Done indexing tables.");
                    } else {
                        System.err.println("Failed indexing tables.");
                    }
                    return true;
                }
                
            }.execute();
        }
        
    }

}
