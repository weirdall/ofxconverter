/*
 * OFXConverterView.java
 */

package ofxconverter;

import java.awt.Dimension;
import java.awt.event.ComponentListener;
import org.jdesktop.application.Action;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.table.TableColumn;
import ofxconverter.gui.FileTableModel;
import ofxconverter.gui.FileTableModel.Column;
import ofxconverter.gui.editor.CheckBoxCellEditor;
import ofxconverter.gui.editor.ComboBoxCellEditor;
import ofxconverter.gui.renderer.CheckBoxCellRenderer;
import ofxconverter.gui.renderer.ComboBoxCellRenderer;
import ofxconverter.module.input.Bank;
import ofxconverter.module.input.IngPostbank;
import ofxconverter.module.input.Rabobank;
import ofxconverter.module.output.Ofx;
import ofxconverter.structure.BankStatement;
import ofxconverter.util.CheckFile;

/**
 * The application's main frame.
 */
public class OFXConverterView extends FrameView {

    private static final String EOL = System.getProperty("line.separator");
    ImageIcon icon = null;

    private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault() );

    public synchronized String getDate(){

        String returnDate = null;
        Date date = new Date();
        if( date != null ){
            returnDate = dateFormat.format(date);
        }
        return returnDate;

    }

    public OFXConverterView(SingleFrameApplication app) {

        super(app);


        java.net.URL imageURL = OFXConverterView.class.getResource("resources/icon.png");
        if (imageURL != null) {
            icon = new ImageIcon(imageURL);
            this.getFrame().setIconImage(icon.getImage());
            this.getFrame().setMinimumSize( new Dimension( 550, 400 ) );
        }

        initComponents();

    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = OFXConverterApp.getApplication().getMainFrame();
            aboutBox = new OFXConverterAboutBox(mainFrame);
            aboutBox.setIconImage(icon.getImage());
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        OFXConverterApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        processButton = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        fileTable = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        logTextField = new javax.swing.JTextArea();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem openMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();

        mainPanel.setName("mainPanel"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ofxconverter.OFXConverterApp.class).getContext().getResourceMap(OFXConverterView.class);
        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        processButton.setText(resourceMap.getString("processButton.text")); // NOI18N
        processButton.setEnabled(false);
        processButton.setName("processButton"); // NOI18N
        processButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                processButtonMouseClicked(evt);
            }
        });

        jTabbedPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTabbedPane1.setName("tabs"); // NOI18N
        jTabbedPane1.setOpaque(true);

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        Object[] header = { resourceMap.getString("FileTable.header[0]"),
            resourceMap.getString("FileTable.header[1]"),
            resourceMap.getString("FileTable.header[2]") };

        tableModel = new FileTableModel( header );
        tableModel.setButton(processButton);
        fileTable.setModel( tableModel );
        // These are the combobox values
        ArrayList<String> list = new ArrayList<String>();

        // Add an empty line
        list.add("");

        for(FileType fileType: FileType.values() ){
            if( !fileType.isGeneral() ){
                list.add( fileType.getName() );
            }
        }

        String[] values = new String[]{};

        values = list.toArray( values );

        for( Column column: Column.values() ){

            TableColumn col = fileTable.getColumnModel().getColumn(column.ordinal());

            switch( column ){
                case CHECKBOX:
                col.setMinWidth(55);
                col.setMaxWidth(55);
                col.setCellEditor(new CheckBoxCellEditor());
                col.setCellRenderer(new CheckBoxCellRenderer());
                break;
                case TEXT:
                break;
                case COMBOBOX:
                // Set the combobox editor on the 1st visible column
                col.setCellEditor(new ComboBoxCellEditor(values));

                // If the cell should appear like a combobox in its
                // non-editing state, also set the combobox renderer
                col.setCellRenderer(new ComboBoxCellRenderer(values));
                col.setMinWidth(110);
                col.setMaxWidth(110);
                break;
            }
        }
        fileTable.setFillsViewportHeight(true);
        fileTable.setName("fileTable"); // NOI18N
        jScrollPane1.setViewportView(fileTable);

        jTabbedPane1.addTab(resourceMap.getString("jScrollPane1.TabConstraints.tabTitle"), jScrollPane1); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        logTextField.setColumns(20);
        logTextField.setRows(5);
        logTextField.setEnabled(false);
        logTextField.setName("logTextField"); // NOI18N
        jScrollPane2.setViewportView(logTextField);

        jTabbedPane1.addTab(resourceMap.getString("jScrollPane2.TabConstraints.tabTitle"), jScrollPane2); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(441, Short.MAX_VALUE)
                .addComponent(processButton)
                .addContainerGap())
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(processButton)
                .addContainerGap())
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName(resourceMap.getString("tabs.AccessibleContext.accessibleName")); // NOI18N

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        openMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openMenuItem.setText(resourceMap.getString("openMenuItem.text")); // NOI18N
        openMenuItem.setName("openMenuItem"); // NOI18N
        openMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(openMenuItem);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(ofxconverter.OFXConverterApp.class).getContext().getActionMap(OFXConverterView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setComponent(mainPanel);
        setMenuBar(menuBar);
    }// </editor-fold>//GEN-END:initComponents

    private void openMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openMenuItemActionPerformed
        JFileChooser open = new JFileChooser();
        open.setControlButtonsAreShown(true);
        open.setDialogType(JFileChooser.OPEN_DIALOG);

        open.setDoubleBuffered(true);

        open.setMultiSelectionEnabled(true);
        open.setFileSelectionMode(JFileChooser.FILES_ONLY);
        open.setCurrentDirectory(null);

        int result = open.showOpenDialog(null);
        if( result == JFileChooser.APPROVE_OPTION ){
            File [] files = open.getSelectedFiles();
            CheckFile checkFile = new CheckFile();

            // TODO: have error icon when ioException was thrown
            // TODO: have warning icon when the file has not been properly identified

            int rowCount = fileTable.getRowCount() - 1;

            for ( File file: files ){
                if( checkFile.isValid(file) ){

                    FileHandler fileHandler = new FileHandler( file, checkFile.getFileType(), checkFile.hasHeader() );

                    if( checkFile.getFileType().isGeneral() ){
                        tableModel.addRow( new Object[] { false, file.getAbsolutePath(), fileHandler } );
                        tableModel.setCellEditable(false, rowCount, FileTableModel.Column.CHECKBOX.ordinal() );
                        rowCount++;                        
                    }
                    else{
                        tableModel.addRow( new Object[] { true, file.getAbsolutePath(), fileHandler } );
                        rowCount++;
                    }


                }else{
                    logTextField.append( getDate() + " File " + file.getName() + " has not been identified" + EOL );
                }
            }

        }
    }//GEN-LAST:event_openMenuItemActionPerformed

    private void processButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_processButtonMouseClicked
        // TODO: move this bank selection code to another class/method
         for( int i = 0; i < tableModel.getRowCount(); i++ ){
            Object object = tableModel.getValueAt(i, FileTableModel.Column.CHECKBOX.ordinal());
            if( object instanceof Boolean ){
                Boolean checked = (Boolean) object;
                if( !checked ){
                    continue;
                }
            }

            object = tableModel.getObjectAt( i, FileTableModel.Column.COMBOBOX.ordinal() );

            if( object instanceof FileHandler ){

                FileHandler fileHandler = (FileHandler) object;

                if( fileHandler.getFile() != null ){

                    Bank bank = null;

                    bank = Bank.getBank( fileHandler.getType() );
                    
                    if( bank != null ){
                        BankStatement bankStatement = bank.readFile( fileHandler.getFile() );

                        Ofx ofxWriter = new Ofx();
                        if( ofxWriter.createXmlFile( fileHandler.getFile(), bankStatement ) ){
                            tableModel.setCellEditable(false, i, Column.CHECKBOX.ordinal());
                            tableModel.setCellEditable(false, i, Column.COMBOBOX.ordinal());
                            tableModel.setValueAt(false, i, Column.CHECKBOX.ordinal());
                            tableModel.setValueAt("done", i, Column.CHECKBOX.ordinal());
                        }
                        if( bankStatement.getFailedStrings().length() > 0 ){
                            if( fileHandler.isHasHeader() ){
                                logTextField.append( "Skipped header: " );
                            }
                            logTextField.append( bankStatement.getFailedStrings() + EOL );
                        }
                    }
                }
                else{
                    fileHandler.setFileError( true );
                }

            }
        }
    }//GEN-LAST:event_processButtonMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable fileTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea logTextField;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JButton processButton;
    // End of variables declaration//GEN-END:variables

    private FileTableModel tableModel = null;

    private JDialog aboutBox;

}
