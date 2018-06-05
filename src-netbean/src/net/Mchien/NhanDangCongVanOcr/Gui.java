/**
 * Copyright @ 
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.Mchien.NhanDangCongVanOcr;

import net.Mchien.NhanDangCongVanOcr.components.SimpleFilter;
import net.Mchien.NhanDangCongVan.components.JImageLabel;
import net.Mchien.NhanDangCongVan.components.ImageIconScalable;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.DropTarget;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.*;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.text.*;
import java.util.List;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.undo.*;
import net.sourceforge.tess4j.ITessAPI;

import net.sourceforge.tess4j.util.ImageHelper;
import net.sourceforge.tess4j.util.ImageIOHelper;
import net.Mchien.NhanDangCongVan.util.FormLocalizer;
import net.Mchien.NhanDangCongVan.util.Utils;
import net.sourceforge.vietpad.inputmethod.VietKeyListener;

public class Gui extends JFrame {
    public boolean checkRectDrop = false;
    public Rectangle rectNguoigui, rectSo;
    public List<Rectangle> listRectImage ;
    public final float ZOOM_FACTOR = 1.25f;
    public static final String APP_NAME = "VietOCR";
    public static final String TESSERACT_PATH = "tesseract-ocr";
    public static final String TO_BE_IMPLEMENTED = "To be implemented in subclass";
    static final boolean MAC_OS_X = System.getProperty("os.name").startsWith("Mac");
    static final boolean WINDOWS = System.getProperty("os.name").toLowerCase().startsWith("windows");
    static final boolean LINUX = System.getProperty("os.name").equals("Linux");
    protected final File supportDir = new File(System.getProperty("user.home")
            + (MAC_OS_X ? "/Library/Application Support/" + APP_NAME : "/." + APP_NAME.toLowerCase()));
    static final String UTF8 = "UTF-8";
    static final String strUILanguage = "UILanguage";
    static final String TESSDATA = "tessdata";
    private static final String strLookAndFeel = "lookAndFeel";
    private static final String strWindowState = "windowState";
    private static final String strLangCode = "langCode";
    private static final String strTessDir = "TesseractDirectory";
    private static final String strMruList = "MruList";
    private static final String strFrameWidth = "frameWidth";
    private static final String strFrameHeight = "frameHeight";
    private static final String strFrameX = "frameX";
    private static final String strFrameY = "frameY";
    private static final String strCurrentDirectory = "currentDirectory";
    private static final String strOutputDirectory = "outputDirectory";
    private static final String strFontName = "fontName";
    private static final String strFontSize = "fontSize";
    private static final String strFontStyle = "fontStyle";
    private static final String strWordWrap = "wordWrap";
    private static final String strFilterIndex = "filterIndex";
    private static final String strSegmentedRegions = "SegmentedRegions";
    private static final String strSegmentedRegionsPara = "SegmentedRegionsPara";
    private static final String strSegmentedRegionsTextLine = "SegmentedRegionsTextLine";
    private static final String strSegmentedRegionsSymbol = "SegmentedRegionsSymbol";
    private static final String strSegmentedRegionsBlock = "SegmentedRegionsBlock";
    private static final String strSegmentedRegionsWord = "SegmentedRegionsWord";
    public final String EOL = System.getProperty("line.separator");
    static final Preferences prefs = Preferences.userRoot().node("/net/sourceforge/vietocr3");
    protected static String curLangCode = "eng";
    protected static String selectedUILang = "en";
    protected static Properties lookupISO639;
    protected static Properties lookupISO_3_1_Codes;
    private int filterIndex;
    private FileFilter[] fileFilters;
    protected Font font;
    private final Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
    protected int imageIndex;
    int imageTotal;
    List<ImageIconScalable> imageList;
    protected List<IIOImage> iioImageList;
    protected String inputfilename;
    protected ResourceBundle bundle;
    private String currentDirectory;
    private String outputDirectory;
    protected String tessPath;
    private String[] installedLanguageCodes;
    protected String[] installedLanguages;
    ImageIconScalable imageIcon;
    boolean isFitImageSelected;
    protected boolean wordWrapOn;
    protected float scaleX = 1f;
    protected float scaleY = 1f;
    int originalW, originalH;
    Point curScrollPos;
    private File textFile;
    private java.util.List<String> mruList = new java.util.ArrayList<String>();
    private String strClearRecentFiles;
    private boolean textChanged = true;
    private RawListener rawListener;
    private final String DATAFILE_SUFFIX = ".traineddata";
    protected final File baseDir = Utils.getBaseDir(Gui.this);
    protected String datapath;
    private static final int FONT_MIN_SIZE = 6;
    private static final int FONT_MAX_SIZE = 50;

    private final static Logger logger = Logger.getLogger(Gui.class.getName());

    /**
     * Creates new form.
     */
    public Gui() {
        try {
            UIManager.setLookAndFeel(prefs.get(strLookAndFeel, UIManager.getSystemLookAndFeelClassName()));
        } catch (Exception e) {
            // keep default LAF
            logger.log(Level.WARNING, e.getMessage(), e);
        }

        bundle = java.util.ResourceBundle.getBundle("net.sourceforge.vietocr.Gui");
        initComponents();

        if (MAC_OS_X) {
            new MacOSXApplication(Gui.this);

            // remove Exit menuitem
         
            this.jMenuFile.remove(this.jMenuItemExit);

            // remove About menuitem
          
        }

        getInstalledLanguagePacks();
        populateOCRLanguageBox();

        if (!supportDir.exists()) {
            supportDir.mkdirs();
        }

        KeyEventDispatcher dispatcher = new KeyEventDispatcher() {

            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    if (!jTextArea1.isFocusOwner() && e.isControlDown() && e.getKeyCode() == KeyEvent.VK_V) { // Don't catch this event inside the JTextArea
                        pasteImage(); // Paste image from clipboard
                        e.consume();
                    } else if (!jTextArea1.isFocusOwner() && e.isControlDown() && e.getKeyCode() == KeyEvent.VK_LEFT) {
                       
                    } else if (!jTextArea1.isFocusOwner() && e.isControlDown() && e.getKeyCode() == KeyEvent.VK_RIGHT) {
                       
                    } else if (e.getKeyCode() == KeyEvent.VK_F7) {
                      
                    } else if (e.isControlDown() && e.isShiftDown() && (e.getKeyCode() == KeyEvent.VK_EQUALS || e.getKeyCode() == KeyEvent.VK_ADD)) {
                     
                    } else if (e.isControlDown() && e.isShiftDown() && (e.getKeyCode() == KeyEvent.VK_MINUS || e.getKeyCode() == KeyEvent.VK_SUBTRACT)) {
                      
                    } else if (e.isControlDown() && (e.getKeyCode() == KeyEvent.VK_EQUALS || e.getKeyCode() == KeyEvent.VK_ADD)) {
                        
                    } else if (e.isControlDown() && (e.getKeyCode() == KeyEvent.VK_MINUS || e.getKeyCode() == KeyEvent.VK_SUBTRACT)) {
                        
                    } else if (e.isControlDown() && (e.getKeyCode() == KeyEvent.VK_1 || e.getKeyCode() == KeyEvent.VK_NUMPAD1)) {
                        jButtonActualSize.doClick();
                    } else if (e.isControlDown() && (e.getKeyCode() == KeyEvent.VK_2 || e.getKeyCode() == KeyEvent.VK_NUMPAD2)) {
                        
                    } else if (jTextArea1.isFocusOwner() && e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Z) {
                        m_undoAction.actionPerformed(null);
                    } else if (jTextArea1.isFocusOwner() && e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Y) {
                        m_redoAction.actionPerformed(null);
                    } else if (jTextArea1.isFocusOwner() && e.isControlDown() && e.getKeyCode() == KeyEvent.VK_X) {
                        actionCut.actionPerformed(null);
                    }
                }

                return false;
            }
        };
        DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(dispatcher);

//        // Assign F7 key to spellcheck
//        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0), "spellcheck");
//        getRootPane().getActionMap().put("spellcheck", new AbstractAction() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                jToggleButtonSpellCheck.doClick();
//            }
//        });
    }

    public static String getCurrentLocaleId() {
        if (lookupISO_3_1_Codes.containsKey(curLangCode)) {
            return lookupISO_3_1_Codes.getProperty(curLangCode);
        } else if (lookupISO_3_1_Codes.containsKey(curLangCode.substring(0, 3))) {
            return lookupISO_3_1_Codes.getProperty(curLangCode.substring(0, 3));
        } else {
            return null;
        }
    }

    @Override
    public List<Image> getIconImages() {
        List<Image> images = new ArrayList<Image>();
        images.add(new javax.swing.ImageIcon(getClass().getResource("/com/fatcow/icons/ocr_small.png")).getImage());
        images.add(new javax.swing.ImageIcon(getClass().getResource("/com/fatcow/icons/ocr.png")).getImage());
        return images;
    }

    /**
     * Adds Undo support to textarea via context menu.
     */
    private void addUndoSupport() {
        // Undo support
        rawListener = new RawListener();
        this.jTextArea1.getDocument().addUndoableEditListener(rawListener);
        undoSupport.addUndoableEditListener(new SupportListener());
        m_undo.discardAllEdits();
        updateUndoRedo();
        updateCutCopyDelete(false);
    }

    /**
     * Gets Tesseract's installed language data packs.
     */
    private void getInstalledLanguagePacks() {
        if (WINDOWS) {
            tessPath = new File(baseDir, TESSERACT_PATH).getPath();
            datapath = tessPath + "/4.00/tessdata";
        } else {
            tessPath = prefs.get(strTessDir, "/usr/bin");
            datapath = "/usr/share/tesseract-ocr/4.00/tessdata";
        }

        lookupISO639 = new Properties();
        lookupISO_3_1_Codes = new Properties();

        try {
            File tessdataDir = new File(tessPath, "4.00/" + TESSDATA);
            if (!tessdataDir.exists()) {
                String TESSDATA_PREFIX = System.getenv("TESSDATA_PREFIX");
                if (TESSDATA_PREFIX == null && !WINDOWS) { // if TESSDATA_PREFIX env var not set
                    if (tessPath.equals("/usr/bin")) { // default install path of Tesseract on Linux
                        TESSDATA_PREFIX = "/usr/share/tesseract-ocr/4.00/"; // default install path of tessdata on Linux
                    } else {
                        TESSDATA_PREFIX = "/usr/local/share/4.00/"; // default make install path of tessdata on Linux
                    }
                }
                tessdataDir = new File(TESSDATA_PREFIX, TESSDATA);
                datapath = tessdataDir.getPath();
            }

            installedLanguageCodes = tessdataDir.list(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(DATAFILE_SUFFIX) && !name.equals("osd.traineddata");
                }
            });
            Arrays.sort(installedLanguageCodes, Collator.getInstance());

            File xmlFile = new File(baseDir, "data/ISO639-3.xml");
            lookupISO639.loadFromXML(new FileInputStream(xmlFile));
            xmlFile = new File(baseDir, "data/ISO639-1.xml");
            lookupISO_3_1_Codes.loadFromXML(new FileInputStream(xmlFile));
        } catch (IOException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            JOptionPane.showMessageDialog(null, e.getMessage(), APP_NAME, JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        } finally {
            if (installedLanguageCodes == null) {
                installedLanguages = new String[0];
            } else {
                installedLanguages = new String[installedLanguageCodes.length];
            }
            for (int i = 0; i < installedLanguages.length; i++) {
                installedLanguageCodes[i] = installedLanguageCodes[i].replace(DATAFILE_SUFFIX, "");
                installedLanguages[i] = lookupISO639.getProperty(installedLanguageCodes[i], installedLanguageCodes[i]);
            }
        }
    }

    /**
     * Populates OCR Language box.
     */
    @SuppressWarnings("unchecked")
    private void populateOCRLanguageBox() {
        if (installedLanguageCodes == null) {
            JOptionPane.showMessageDialog(Gui.this, bundle.getString("Tesseract_is_not_found._Please_specify_its_path_in_Settings_menu."), APP_NAME, JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        DefaultComboBoxModel model = new DefaultComboBoxModel(installedLanguages);
        model.setSelectedItem(null);
        jComboBoxLang.setModel(model);
        jComboBoxLang.setSelectedItem(prefs.get(strLangCode, null));
        final JTextComponent textField = (JTextComponent) jComboBoxLang.getEditor().getEditorComponent();
        textField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                curLangCode = textField.getText();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                curLangCode = textField.getText();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // ignore
            }
        });
    }

    /**
     * Populates MRU List.
     */
    private void populateMRUList() {
        String[] fileNames = prefs.get(strMruList, "").split(File.pathSeparator);
        for (String fileName : fileNames) {
            if (!fileName.equals("")) {
                mruList.add(fileName);
            }
        }
        updateMRUMenu();
    }

    /**
     * Populates PopupMenu with spellcheck suggestions.
     *
     * @param p
     */
    void populatePopupMenuWithSuggestions(Point p) {
        // to be implemented in subclass
    }

    void repopulatePopupMenu() {
        popup.add(m_undoAction);
        popup.add(m_redoAction);
        popup.addSeparator();
        popup.add(actionCut);
        popup.add(actionCopy);
        popup.add(actionPaste);
        popup.add(actionDelete);
        popup.addSeparator();
        popup.add(actionSelectAll);
    }

    private String menuItemStringBuild(String text, String shorcut, int width) {
        AffineTransform at = new AffineTransform();
        FontRenderContext frc = new FontRenderContext(at, true, false);
        Font f = popup.getFont();

        String spaces = "";
        while ((int) (f.getStringBounds(text + spaces + shorcut, frc).getWidth()) < width) {
            spaces += " ";
        }

        return text + spaces + shorcut;
    }

    /**
     * Builds context menu for textarea.
     */
    private void populatePopupMenu() {
        final int POPUP_TEXT_WIDTH = 120;

        m_undoAction = new AbstractAction(menuItemStringBuild(bundle.getString("jMenuItemUndo.Text"), "CTRL+Z", POPUP_TEXT_WIDTH)) {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    m_undo.undo();
                } catch (CannotUndoException ex) {
                    System.err.println(bundle.getString("Unable_to_undo:_") + ex);
                }
                updateUndoRedo();
            }
        };

        popup.add(m_undoAction);

        m_redoAction = new AbstractAction(menuItemStringBuild(bundle.getString("jMenuItemRedo.Text"), "CTRL+Y", POPUP_TEXT_WIDTH)) {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    m_undo.redo();
                } catch (CannotRedoException ex) {
                    System.err.println(bundle.getString("Unable_to_redo:_") + ex);
                }
                updateUndoRedo();
            }
        };

        popup.add(m_redoAction);
        popup.addSeparator();

        actionCut = new AbstractAction(menuItemStringBuild(bundle.getString("jMenuItemCut.Text"), "CTRL+X", POPUP_TEXT_WIDTH)) {

            @Override
            public void actionPerformed(ActionEvent e) {
                jTextArea1.cut();
                updatePaste();
            }
        };

        popup.add(actionCut);

        actionCopy = new AbstractAction(menuItemStringBuild(bundle.getString("jMenuItemCopy.Text"), "CTRL+C", POPUP_TEXT_WIDTH)) {

            @Override
            public void actionPerformed(ActionEvent e) {
                jTextArea1.copy();
                updatePaste();
            }
        };

        popup.add(actionCopy);

        actionPaste = new AbstractAction(menuItemStringBuild(bundle.getString("jMenuItemPaste.Text"), "CTRL+V", POPUP_TEXT_WIDTH)) {

            @Override
            public void actionPerformed(ActionEvent e) {
                undoSupport.beginUpdate();
                jTextArea1.paste();
                undoSupport.endUpdate();
            }
        };

        popup.add(actionPaste);

        actionDelete = new AbstractAction(menuItemStringBuild(bundle.getString("jMenuItemDelete.Text"), "Del", POPUP_TEXT_WIDTH)) {

            @Override
            public void actionPerformed(ActionEvent e) {
                jTextArea1.replaceSelection(null);
            }
        };

        popup.add(actionDelete);
        popup.addSeparator();

        actionSelectAll = new AbstractAction(menuItemStringBuild(bundle.getString("jMenuItemSelectAll.Text"), "CTRL+A", POPUP_TEXT_WIDTH)) {

            @Override
            public void actionPerformed(ActionEvent e) {
                jTextArea1.selectAll();
            }
        };

        popup.add(actionSelectAll);
    }

    /**
     * Update MRU Submenu.
     */
    private void updateMRUMenu() {
        this.jMenuRecentFiles.removeAll();

        if (mruList.isEmpty()) {
            this.jMenuRecentFiles.add(bundle.getString("No_Recent_Files"));
        } else {
            Action mruAction = new AbstractAction() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    JMenuItem item = (JMenuItem) e.getSource();
                    String fileName = item.getText();

                    if (fileName.equals(strClearRecentFiles)) {
                        mruList.clear();
                        jMenuRecentFiles.removeAll();
                        jMenuRecentFiles.add(bundle.getString("No_Recent_Files"));
                    } else {
                        openFile(new File(fileName));
                    }
                }
            };

            for (String fileName : mruList) {
                JMenuItem item = this.jMenuRecentFiles.add(fileName);
                item.addActionListener(mruAction);
            }
            this.jMenuRecentFiles.addSeparator();
            strClearRecentFiles = bundle.getString("Clear_Recent_Files");
            JMenuItem jMenuItemClear = this.jMenuRecentFiles.add(strClearRecentFiles);
            jMenuItemClear.setMnemonic(bundle.getString("jMenuItemClear.Mnemonic").charAt(0));
            jMenuItemClear.addActionListener(mruAction);
        }
    }

    /**
     * Update MRU List.
     *
     * @param fileName
     */
    private void updateMRUList(String fileName) {
        if (mruList.contains(fileName)) {
            mruList.remove(fileName);
        }
        mruList.add(0, fileName);

        if (mruList.size() > 10) {
            mruList.remove(10);
        }

        updateMRUMenu();
    }

    /**
     * Updates the Undo and Redo actions
     */
    private void updateUndoRedo() {
        m_undoAction.setEnabled(m_undo.canUndo());
        m_redoAction.setEnabled(m_undo.canRedo());
    }

    /**
     * Updates the Cut, Copy, and Delete actions
     *
     * @param isTextSelected whether any text currently selected
     */
    private void updateCutCopyDelete(boolean isTextSelected) {
        actionCut.setEnabled(isTextSelected);
        actionCopy.setEnabled(isTextSelected);
        actionDelete.setEnabled(isTextSelected);
    }

    /**
     * Listens to raw undoable edits
     *
     */
    private class RawListener implements UndoableEditListener {

        /**
         * Description of the Method
         *
         * @param e Description of the Parameter
         */
        @Override
        public void undoableEditHappened(UndoableEditEvent e) {
            undoSupport.postEdit(e.getEdit());
        }
    }

    /**
     * Listens to undoable edits filtered by undoSupport
     *
     */
    private class SupportListener implements UndoableEditListener {

        /**
         * Description of the Method
         *
         * @param e Description of the Parameter
         */
        @Override
        public void undoableEditHappened(UndoableEditEvent e) {
            updateSave(true);
            m_undo.addEdit(e.getEdit());
            updateUndoRedo();
        }
    }

    /**
     * Updates the Paste action
     */
    private void updatePaste() {
        try {
            Transferable clipData = clipboard.getContents(clipboard);
            if (clipData != null) {
                actionPaste.setEnabled(clipData.isDataFlavorSupported(DataFlavor.stringFlavor));
            }
        } catch (OutOfMemoryError e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            JOptionPane.showMessageDialog(this, e.getMessage(), bundle.getString("OutOfMemoryError"), JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popup = new javax.swing.JPopupMenu();
        jFileChooser = new javax.swing.JFileChooser();
        jPopupMenuSegmentedRegions = new javax.swing.JPopupMenu();
        jCheckBoxMenuItemSymbol = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItemWord = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItemTextLine = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItemPara = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItemBlock = new javax.swing.JCheckBoxMenuItem();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanelTextArea = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jButtonOCR = new javax.swing.JButton();
        jButtonCancelOCR = new javax.swing.JButton();
        jButtonCancelOCR.setVisible(false);
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jLabelLanguage = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 0), new java.awt.Dimension(4, 32767));
        jComboBoxLang = new javax.swing.JComboBox();
        jButton5 = new javax.swing.JButton();
        jDesktopPane1 = new javax.swing.JDesktopPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();
        jButton2 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea4 = new javax.swing.JTextArea();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextArea5 = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTextArea6 = new javax.swing.JTextArea();
        jLabel7 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTextArea7 = new javax.swing.JTextArea();
        jLabel9 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        JImageLabel2 = new javax.swing.JLabel();
        jButton9 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButtonClear = new javax.swing.JButton();
        jPanelImage = new javax.swing.JPanel();
        jToolBar2 = new javax.swing.JToolBar();
        jButton10 = new javax.swing.JButton();
        jButtonActualSize = new javax.swing.JButton();
        jSplitPaneImage = new javax.swing.JSplitPane();
        jScrollPaneThumbnail = new javax.swing.JScrollPane();
        jScrollPaneThumbnail.getVerticalScrollBar().setUnitIncrement(20);
        jPanelThumb = new javax.swing.JPanel();
        jCheckBoxMenuWordWrap = new javax.swing.JCheckBox();
        jButtonPasteImage = new javax.swing.JButton();
        jScrollPaneImage = new javax.swing.JScrollPane();
        jScrollPaneImage.getVerticalScrollBar().setUnitIncrement(20);
        jScrollPaneImage.getHorizontalScrollBar().setUnitIncrement(20);
        jImageLabel = new JImageLabel();
        jPanelArrow = new javax.swing.JPanel();
        jPanelStatus = new javax.swing.JPanel();
        jLabelStatus = new javax.swing.JLabel();
        jLabelStatus.setVisible(false); // use jProgressBar instead for (more animation) task status
        jProgressBar1 = new javax.swing.JProgressBar();
        jProgressBar1.setVisible(false);
        jSeparatorDim = new javax.swing.JSeparator();
        jLabelDimension = new javax.swing.JLabel();
        jLabelDimensionValue = new javax.swing.JLabel();
        jSeparatorDimEnd = new javax.swing.JSeparator();
        jButtonSegmentedRegions = new javax.swing.JButton();
        jLabelScreenshotModeValue = new javax.swing.JLabel();
        jLabelPSMvalue = new javax.swing.JLabel();
        jSeparatorOEM = new javax.swing.JSeparator();
        jLabelOEMvalue = new javax.swing.JLabel();
        jSeparatorEnd = new javax.swing.JSeparator();
        jMenuBar2 = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItemOpen = new javax.swing.JMenuItem();
        jMenuRecentFiles = new javax.swing.JMenu();
        jMenuItemExit = new javax.swing.JMenuItem();
        jMenuCommand = new javax.swing.JMenu();
        jMenuItemOCR = new javax.swing.JMenuItem();
        jMenuImage = new javax.swing.JMenu();
        jMenuFilter = new javax.swing.JMenu();
        jMenuItemGrayscale = new javax.swing.JMenuItem();
        jMenuItemInvert = new javax.swing.JMenuItem();
        jMenuItemSharpen = new javax.swing.JMenuItem();
        jMenuItemAutocrop = new javax.swing.JMenuItem();
        jMenuItemCrop = new javax.swing.JMenuItem();
        jSeparator12 = new javax.swing.JPopupMenu.Separator();
        jMenuItemUndo = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jCheckBoxMenuItemSegmentedRegions = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItemSegmentedRegions.setSelected(prefs.getBoolean(strSegmentedRegions, false));

        currentDirectory = prefs.get(strCurrentDirectory, null);
        outputDirectory = prefs.get(strOutputDirectory, null);
        jFileChooser.setCurrentDirectory(currentDirectory == null ? null : new File(currentDirectory));
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("net/Mchien/NhanDangCongVanOcr/Gui"); // NOI18N
        jFileChooser.setDialogTitle(bundle.getString("jButtonOpen.ToolTipText")); // NOI18N
        FileFilter allImageFilter = new SimpleFilter("bmp;gif;jpg;jpeg;jp2;png;pnm;pbm;pgm;ppm;tif;tiff;pdf", bundle.getString("All_Image_Files"));
        FileFilter bmpFilter = new SimpleFilter("bmp", "Bitmap");
        FileFilter gifFilter = new SimpleFilter("gif", "GIF");
        FileFilter jpegFilter = new SimpleFilter("jpg;jpeg", "JPEG");
        FileFilter jpeg2000Filter = new SimpleFilter("jp2", "JPEG 2000");
        FileFilter pngFilter = new SimpleFilter("png", "PNG");
        FileFilter pnmFilter = new SimpleFilter("pnm;pbm;pgm;ppm", "PNM");
        FileFilter tiffFilter = new SimpleFilter("tif;tiff", "TIFF");

        FileFilter pdfFilter = new SimpleFilter("pdf", "PDF");
        FileFilter textFilter = new SimpleFilter("txt", bundle.getString("UTF-8_Text"));

        jFileChooser.setAcceptAllFileFilterUsed(false);
        jFileChooser.addChoosableFileFilter(allImageFilter);
        jFileChooser.addChoosableFileFilter(bmpFilter);
        jFileChooser.addChoosableFileFilter(gifFilter);
        jFileChooser.addChoosableFileFilter(jpegFilter);
        jFileChooser.addChoosableFileFilter(jpeg2000Filter);
        jFileChooser.addChoosableFileFilter(pngFilter);
        jFileChooser.addChoosableFileFilter(pnmFilter);
        jFileChooser.addChoosableFileFilter(tiffFilter);
        jFileChooser.addChoosableFileFilter(pdfFilter);
        jFileChooser.addChoosableFileFilter(textFilter);

        filterIndex = prefs.getInt(strFilterIndex, 0);
        fileFilters = jFileChooser.getChoosableFileFilters();
        if (filterIndex < fileFilters.length) {
            jFileChooser.setFileFilter(fileFilters[filterIndex]);
        }

        jCheckBoxMenuItemSymbol.setSelected(prefs.getBoolean(strSegmentedRegionsSymbol, false));
        jCheckBoxMenuItemSymbol.setText(bundle.getString("jCheckBoxMenuItemSymbol.Text")); // NOI18N
        jCheckBoxMenuItemSymbol.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItemSymbolActionPerformed(evt);
            }
        });
        jPopupMenuSegmentedRegions.add(jCheckBoxMenuItemSymbol);

        jCheckBoxMenuItemWord.setSelected(prefs.getBoolean(strSegmentedRegionsWord, false));
        jCheckBoxMenuItemWord.setText(bundle.getString("jCheckBoxMenuItemWord.Text")); // NOI18N
        jCheckBoxMenuItemWord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItemWordActionPerformed(evt);
            }
        });
        jPopupMenuSegmentedRegions.add(jCheckBoxMenuItemWord);

        jCheckBoxMenuItemTextLine.setSelected(prefs.getBoolean(strSegmentedRegionsTextLine, false));
        jCheckBoxMenuItemTextLine.setText(bundle.getString("jCheckBoxMenuItemTextLine.Text")); // NOI18N
        jCheckBoxMenuItemTextLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItemTextLineActionPerformed(evt);
            }
        });
        jPopupMenuSegmentedRegions.add(jCheckBoxMenuItemTextLine);

        jCheckBoxMenuItemPara.setSelected(prefs.getBoolean(strSegmentedRegionsPara, false));
        jCheckBoxMenuItemPara.setText(bundle.getString("jCheckBoxMenuItemPara.Text")); // NOI18N
        jCheckBoxMenuItemPara.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItemParaActionPerformed(evt);
            }
        });
        jPopupMenuSegmentedRegions.add(jCheckBoxMenuItemPara);

        jCheckBoxMenuItemBlock.setSelected(prefs.getBoolean(strSegmentedRegionsBlock, false));
        jCheckBoxMenuItemBlock.setText(bundle.getString("jCheckBoxMenuItemBlock.Text")); // NOI18N
        jCheckBoxMenuItemBlock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItemBlockActionPerformed(evt);
            }
        });
        jPopupMenuSegmentedRegions.add(jCheckBoxMenuItemBlock);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(APP_NAME);
        setIconImages(getIconImages());
        setLocationByPlatform(true);
        setMinimumSize(new java.awt.Dimension(500, 360));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        jSplitPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(-2, 0, 0, 0));
        jSplitPane1.setDividerLocation(600);
        jSplitPane1.setDividerSize(2);

        jPanelTextArea.setLayout(new java.awt.BorderLayout());

        jToolBar1.setRollover(true);

        jButtonOCR.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/fatcow/icons/ocr.png"))); // NOI18N
        jButtonOCR.setToolTipText(bundle.getString("jButtonOCR.ToolTipText")); // NOI18N
        jButtonOCR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOCRActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonOCR);

        jButtonCancelOCR.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/fatcow/icons/cancel.png"))); // NOI18N
        jButtonCancelOCR.setToolTipText(bundle.getString("jButtonCancelOCR.ToolTipText")); // NOI18N
        jButtonCancelOCR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelOCRActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonCancelOCR);
        jToolBar1.add(jSeparator1);

        jLabelLanguage.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabelLanguage.setLabelFor(jComboBoxLang);
        jLabelLanguage.setText(bundle.getString("jLabelLanguage.Text")); // NOI18N
        jLabelLanguage.setToolTipText(bundle.getString("jLabelLanguage.ToolTipText")); // NOI18N
        jToolBar1.add(jLabelLanguage);
        jToolBar1.add(filler1);

        jComboBoxLang.setEditable(true);
        jComboBoxLang.setMaximumSize(new java.awt.Dimension(100, 24));
        jComboBoxLang.setPreferredSize(new java.awt.Dimension(200, 20));
        jComboBoxLang.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxLangItemStateChanged(evt);
            }
        });
        jToolBar1.add(jComboBoxLang);

        jButton5.setFocusable(false);
        jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jToolBar1.add(jButton5);

        jPanelTextArea.add(jToolBar1, java.awt.BorderLayout.PAGE_START);

        jDesktopPane1.setBackground(new java.awt.Color(244, 244, 244));

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jDesktopPane1.add(jScrollPane1);
        jScrollPane1.setBounds(10, 280, 640, 270);

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        jTextArea2.setColumns(20);
        jTextArea2.setFont(new java.awt.Font("Monospaced", 0, 14)); // NOI18N
        jTextArea2.setRows(5);
        jScrollPane2.setViewportView(jTextArea2);

        jDesktopPane1.add(jScrollPane2);
        jScrollPane2.setBounds(70, 10, 580, 30);

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("NƠI GỬI");
        jDesktopPane1.add(jLabel2);
        jLabel2.setBounds(10, 20, 80, 16);

        jButton1.setText("Quét thủ công");
        jButton1.setActionCommand("Quét");
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jDesktopPane1.add(jButton1);
        jButton1.setBounds(660, 10, 110, 30);

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setText("SỐ CÔNG VĂN");
        jDesktopPane1.add(jLabel3);
        jLabel3.setBounds(10, 60, 100, 16);

        jScrollPane3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane3.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        jTextArea3.setColumns(20);
        jTextArea3.setFont(new java.awt.Font("Monospaced", 0, 14)); // NOI18N
        jTextArea3.setRows(5);
        jScrollPane3.setViewportView(jTextArea3);

        jDesktopPane1.add(jScrollPane3);
        jScrollPane3.setBounds(110, 50, 540, 30);

        jButton2.setText("Quét thủ công");
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jDesktopPane1.add(jButton2);
        jButton2.setBounds(660, 50, 110, 30);

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel4.setText("TGIAN- ĐĐIỂM");
        jDesktopPane1.add(jLabel4);
        jLabel4.setBounds(10, 100, 100, 16);

        jScrollPane4.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane4.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        jTextArea4.setColumns(20);
        jTextArea4.setFont(new java.awt.Font("Monospaced", 0, 14)); // NOI18N
        jTextArea4.setRows(5);
        jScrollPane4.setViewportView(jTextArea4);

        jDesktopPane1.add(jScrollPane4);
        jScrollPane4.setBounds(110, 90, 540, 30);

        jButton3.setText("Quét thủ công");
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jDesktopPane1.add(jButton3);
        jButton3.setBounds(660, 90, 110, 30);

        jButton4.setText("Quét thủ công");
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jDesktopPane1.add(jButton4);
        jButton4.setBounds(660, 130, 110, 30);

        jScrollPane5.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane5.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        jTextArea5.setColumns(20);
        jTextArea5.setFont(new java.awt.Font("Monospaced", 0, 14)); // NOI18N
        jTextArea5.setRows(5);
        jScrollPane5.setViewportView(jTextArea5);

        jDesktopPane1.add(jScrollPane5);
        jScrollPane5.setBounds(110, 130, 540, 30);

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel5.setText(" TIÊU CHÍ (V/v)");
        jDesktopPane1.add(jLabel5);
        jLabel5.setBounds(10, 140, 110, 16);

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel6.setText("NỘI DUNG CÔNG VĂN");
        jDesktopPane1.add(jLabel6);
        jLabel6.setBounds(260, 250, 160, 16);

        jScrollPane6.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane6.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        jTextArea6.setColumns(20);
        jTextArea6.setFont(new java.awt.Font("Monospaced", 0, 14)); // NOI18N
        jTextArea6.setRows(5);
        jScrollPane6.setViewportView(jTextArea6);

        jDesktopPane1.add(jScrollPane6);
        jScrollPane6.setBounds(180, 170, 470, 70);

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel7.setText("KÍNH GỬI (NƠI NHẬN)");
        jDesktopPane1.add(jLabel7);
        jLabel7.setBounds(10, 180, 160, 16);

        jButton6.setText("Quét thủ công");
        jButton6.setFocusable(false);
        jButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        jDesktopPane1.add(jButton6);
        jButton6.setBounds(660, 170, 110, 30);

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel8.setText("NGƯỜI NHẬN");
        jDesktopPane1.add(jLabel8);
        jLabel8.setBounds(10, 560, 160, 16);

        jScrollPane7.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane7.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        jTextArea7.setColumns(20);
        jTextArea7.setFont(new java.awt.Font("Monospaced", 0, 14)); // NOI18N
        jTextArea7.setRows(5);
        jScrollPane7.setViewportView(jTextArea7);

        jDesktopPane1.add(jScrollPane7);
        jScrollPane7.setBounds(20, 590, 250, 230);

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel9.setText("CON DẤU");
        jDesktopPane1.add(jLabel9);
        jLabel9.setBounds(310, 560, 70, 16);

        jButton7.setText("Quét thủ công");
        jButton7.setFocusable(false);
        jButton7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton7.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });
        jDesktopPane1.add(jButton7);
        jButton7.setBounds(660, 280, 110, 30);

        jButton8.setText("Quét thủ công");
        jButton8.setFocusable(false);
        jButton8.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton8.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });
        jDesktopPane1.add(jButton8);
        jButton8.setBounds(110, 560, 110, 30);

        JImageLabel2.setBackground(new java.awt.Color(255, 255, 255));
        JImageLabel2.setToolTipText("");
        jDesktopPane1.add(JImageLabel2);
        JImageLabel2.setBounds(300, 590, 520, 230);

        jButton9.setText("Quét thủ công");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });
        jDesktopPane1.add(jButton9);
        jButton9.setBounds(400, 560, 140, 30);

        jButton11.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButton11.setText("Thêm vào cơ sở dữ liệu");
        jDesktopPane1.add(jButton11);
        jButton11.setBounds(70, 840, 310, 60);

        jButtonClear.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButtonClear.setText("Xóa dữ liệu");
        jButtonClear.setToolTipText(bundle.getString("jButtonClear.ToolTipText")); // NOI18N
        jButtonClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClearActionPerformed(evt);
            }
        });
        jDesktopPane1.add(jButtonClear);
        jButtonClear.setBounds(420, 840, 360, 60);

        jPanelTextArea.add(jDesktopPane1, java.awt.BorderLayout.CENTER);

        jSplitPane1.setRightComponent(jPanelTextArea);
        jPanelTextArea.getAccessibleContext().setAccessibleName("");

        jPanelImage.setLayout(new java.awt.BorderLayout());

        jToolBar2.setFloatable(false);

        jButton10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/fatcow/icons/open.png"))); // NOI18N
        jButton10.setFocusable(false);
        jButton10.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton10.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar2.add(jButton10);

        jButtonActualSize.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/fatcow/icons/zoom_actual.png"))); // NOI18N
        jButtonActualSize.setToolTipText(bundle.getString("jButtonActualSize.ToolTipText")); // NOI18N
        jButtonActualSize.setEnabled(false);
        jButtonActualSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonActualSizeActionPerformed(evt);
            }
        });
        jToolBar2.add(jButtonActualSize);

        jPanelImage.add(jToolBar2, java.awt.BorderLayout.PAGE_START);

        jSplitPaneImage.setDividerLocation(120);

        jScrollPaneThumbnail.setPreferredSize(new java.awt.Dimension(120, 120));

        jPanelThumb.setLayout(new javax.swing.BoxLayout(jPanelThumb, javax.swing.BoxLayout.PAGE_AXIS));

        jCheckBoxMenuWordWrap.setEnabled(false);
        jPanelThumb.add(jCheckBoxMenuWordWrap);

        jButtonPasteImage.setEnabled(false);
        jButtonPasteImage.setFocusable(false);
        jButtonPasteImage.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonPasteImage.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jPanelThumb.add(jButtonPasteImage);

        jScrollPaneThumbnail.setViewportView(jPanelThumb);

        jSplitPaneImage.setLeftComponent(jScrollPaneThumbnail);

        jScrollPaneImage.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                jScrollPaneImageMouseWheelMoved(evt);
            }
        });

        jImageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jImageLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jImageLabelMouseEntered(evt);
            }
        });
        jScrollPaneImage.setViewportView(jImageLabel);

        jSplitPaneImage.setRightComponent(jScrollPaneImage);

        jPanelImage.add(jSplitPaneImage, java.awt.BorderLayout.CENTER);
        jSplitPaneImage.getLeftComponent().setMinimumSize(new Dimension());
        jSplitPaneImage.setDividerLocation(0);
        jSplitPaneImage.setDividerSize(0);

        jPanelArrow.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 5));
        jPanelImage.add(jPanelArrow, java.awt.BorderLayout.WEST);

        jSplitPane1.setLeftComponent(jPanelImage);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        jPanelStatus.setPreferredSize(new java.awt.Dimension(10, 22));
        jPanelStatus.setLayout(new javax.swing.BoxLayout(jPanelStatus, javax.swing.BoxLayout.LINE_AXIS));
        jPanelStatus.add(jLabelStatus);

        jProgressBar1.setMaximumSize(new java.awt.Dimension(146, 17));
        jProgressBar1.setStringPainted(true);
        jPanelStatus.add(jProgressBar1);

        jSeparatorDim.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparatorDim.setMaximumSize(new java.awt.Dimension(2, 14));
        jPanelStatus.add(Box.createHorizontalStrut(250));
        jPanelStatus.add(jSeparatorDim);
        jPanelStatus.add(Box.createHorizontalStrut(4));

        jLabelDimension.setText(bundle.getString("jLabelDimension.Text")); // NOI18N
        jLabelDimension.setToolTipText(bundle.getString("jLabelDimension.ToolTipText")); // NOI18N
        jPanelStatus.add(jLabelDimension);
        jPanelStatus.add(Box.createHorizontalStrut(4));

        jLabelDimensionValue.setText("0 × 0px  0bpp");
        jPanelStatus.add(jLabelDimensionValue);

        jSeparatorDimEnd.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparatorDimEnd.setMaximumSize(new java.awt.Dimension(2, 14));
        jPanelStatus.add(Box.createHorizontalStrut(4));
        jPanelStatus.add(jSeparatorDimEnd);

        jButtonSegmentedRegions.setText(bundle.getString("jButtonSegmentedRegions.Text")); // NOI18N
        jButtonSegmentedRegions.setToolTipText(bundle.getString("jButtonSegmentedRegions.ToolTipText")); // NOI18N
        jButtonSegmentedRegions.setMargin(new java.awt.Insets(2, 10, 2, 6));
        jButtonSegmentedRegions.setVisible(jCheckBoxMenuItemSegmentedRegions.isSelected());
        jButtonSegmentedRegions.setContentAreaFilled(false);
        jButtonSegmentedRegions.setBorderPainted(false);
        jButtonSegmentedRegions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSegmentedRegionsActionPerformed(evt);
            }
        });
        jPanelStatus.add(jButtonSegmentedRegions);
        jPanelStatus.add(Box.createHorizontalGlue());
        jPanelStatus.add(jLabelScreenshotModeValue);
        jPanelStatus.add(jLabelPSMvalue);

        jSeparatorOEM.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparatorOEM.setMaximumSize(new java.awt.Dimension(2, 14));
        jPanelStatus.add(Box.createHorizontalStrut(4));
        jPanelStatus.add(jSeparatorOEM);
        jPanelStatus.add(Box.createHorizontalStrut(4));
        jPanelStatus.add(jLabelOEMvalue);

        jSeparatorEnd.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparatorEnd.setMaximumSize(new java.awt.Dimension(2, 14));
        jPanelStatus.add(Box.createHorizontalStrut(4));
        jPanelStatus.add(jSeparatorEnd);
        jPanelStatus.add(Box.createHorizontalStrut(16));

        getContentPane().add(jPanelStatus, java.awt.BorderLayout.SOUTH);

        jMenuFile.setMnemonic(java.util.ResourceBundle.getBundle("net/Mchien/NhanDangCongVanOcr/Gui").getString("jMenuFile.Mnemonic").charAt(0));
        jMenuFile.setText(bundle.getString("jMenuFile.Text")); // NOI18N

        jMenuItemOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemOpen.setMnemonic(java.util.ResourceBundle.getBundle("net/Mchien/NhanDangCongVanOcr/Gui").getString("jMenuItemOpen.Mnemonic").charAt(0));
        jMenuItemOpen.setText(bundle.getString("jMenuItemOpen.Text")); // NOI18N
        jMenuItemOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemOpenActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemOpen);

        jMenuRecentFiles.setMnemonic(java.util.ResourceBundle.getBundle("net/Mchien/NhanDangCongVanOcr/Gui").getString("jMenuRecentFiles.Mnemonic").charAt(0));
        jMenuRecentFiles.setText(bundle.getString("jMenuRecentFiles.Text")); // NOI18N
        jMenuFile.add(jMenuRecentFiles);

        jMenuItemExit.setMnemonic(java.util.ResourceBundle.getBundle("net/Mchien/NhanDangCongVanOcr/Gui").getString("jMenuItemExit.Mnemonic").charAt(0));
        jMenuItemExit.setText(bundle.getString("jMenuItemExit.Text")); // NOI18N
        jMenuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExitActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemExit);

        jMenuBar2.add(jMenuFile);

        jMenuCommand.setMnemonic(java.util.ResourceBundle.getBundle("net/Mchien/NhanDangCongVanOcr/Gui").getString("jMenuCommand.Mnemonic").charAt(0));
        jMenuCommand.setText(bundle.getString("jMenuCommand.Text")); // NOI18N

        jMenuItemOCR.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemOCR.setMnemonic(java.util.ResourceBundle.getBundle("net/Mchien/NhanDangCongVanOcr/Gui").getString("jMenuItemOCR.Mnemonic").charAt(0));
        jMenuItemOCR.setText(bundle.getString("jMenuItemOCR.Text")); // NOI18N
        jMenuItemOCR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemOCRActionPerformed(evt);
            }
        });
        jMenuCommand.add(jMenuItemOCR);

        jMenuBar2.add(jMenuCommand);

        jMenuImage.setMnemonic(java.util.ResourceBundle.getBundle("net/Mchien/NhanDangCongVanOcr/Gui").getString("jMenuImage.Mnemonic").charAt(0));
        jMenuImage.setText(bundle.getString("jMenuImage.Text")); // NOI18N

        jMenuFilter.setMnemonic(java.util.ResourceBundle.getBundle("net/Mchien/NhanDangCongVanOcr/Gui").getString("jMenuFilter.Mnemonic").charAt(0));
        jMenuFilter.setText(bundle.getString("jMenuFilter.Text")); // NOI18N

        jMenuItemGrayscale.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemGrayscale.setMnemonic(java.util.ResourceBundle.getBundle("net/Mchien/NhanDangCongVanOcr/Gui").getString("jMenuItemGrayscale.Mnemonic").charAt(0));
        jMenuItemGrayscale.setText(bundle.getString("jMenuItemGrayscale.Text")); // NOI18N
        jMenuItemGrayscale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemGrayscaleActionPerformed(evt);
            }
        });
        jMenuFilter.add(jMenuItemGrayscale);

        jMenuItemInvert.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemInvert.setMnemonic(java.util.ResourceBundle.getBundle("net/Mchien/NhanDangCongVanOcr/Gui").getString("jMenuItemInvert.Mnemonic").charAt(0));
        jMenuItemInvert.setText(bundle.getString("jMenuItemInvert.Text")); // NOI18N
        jMenuItemInvert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemInvertActionPerformed(evt);
            }
        });
        jMenuFilter.add(jMenuItemInvert);

        jMenuItemSharpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemSharpen.setMnemonic(java.util.ResourceBundle.getBundle("net/Mchien/NhanDangCongVanOcr/Gui").getString("jMenuItemSharpen.Mnemonic").charAt(0));
        jMenuItemSharpen.setText(bundle.getString("jMenuItemSharpen.Text")); // NOI18N
        jMenuItemSharpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSharpenActionPerformed(evt);
            }
        });
        jMenuFilter.add(jMenuItemSharpen);

        jMenuImage.add(jMenuFilter);

        jMenuItemAutocrop.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemAutocrop.setMnemonic(java.util.ResourceBundle.getBundle("net/Mchien/NhanDangCongVanOcr/Gui").getString("jMenuItemAutocrop.Mnemonic").charAt(0));
        jMenuItemAutocrop.setText(bundle.getString("jMenuItemAutocrop.Text")); // NOI18N
        jMenuItemAutocrop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAutocropActionPerformed(evt);
            }
        });
        jMenuImage.add(jMenuItemAutocrop);

        jMenuItemCrop.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemCrop.setMnemonic(java.util.ResourceBundle.getBundle("net/Mchien/NhanDangCongVanOcr/Gui").getString("jMenuItemCrop.Mnemonic").charAt(0));
        jMenuItemCrop.setText(bundle.getString("jMenuItemCrop.Text")); // NOI18N
        jMenuItemCrop.setToolTipText("");
        jMenuItemCrop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCropActionPerformed(evt);
            }
        });
        jMenuImage.add(jMenuItemCrop);
        jMenuImage.add(jSeparator12);

        jMenuItemUndo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemUndo.setMnemonic(java.util.ResourceBundle.getBundle("net/Mchien/NhanDangCongVanOcr/Gui").getString("jMenuItemUndo.Mnemonic").charAt(0));
        jMenuItemUndo.setText(bundle.getString("jMenuItemUndo.Text")); // NOI18N
        jMenuItemUndo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemUndoActionPerformed(evt);
            }
        });
        jMenuImage.add(jMenuItemUndo);
        jMenuImage.add(jSeparator2);

        jCheckBoxMenuItemSegmentedRegions.setMnemonic(java.util.ResourceBundle.getBundle("net/Mchien/NhanDangCongVanOcr/Gui").getString("jCheckBoxMenuItemSegmentedRegions.Mnemonic").charAt(0));
        jCheckBoxMenuItemSegmentedRegions.setText(bundle.getString("jCheckBoxMenuItemSegmentedRegions.Text")); // NOI18N
        jCheckBoxMenuItemSegmentedRegions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItemSegmentedRegionsActionPerformed(evt);
            }
        });
        jMenuImage.add(jCheckBoxMenuItemSegmentedRegions);

        jMenuBar2.add(jMenuImage);

        setJMenuBar(jMenuBar2);
        if (LINUX) {
            setPreferredSize(new java.awt.Dimension(1340, 600));
        }

        // DnD support
        new DropTarget(this.jImageLabel, new FileDropTargetListener(Gui.this));
        new DropTarget(this.jTextArea1, new FileDropTargetListener(Gui.this));

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                quit();
            }

            @Override
            public void windowOpened(WindowEvent e) {
                updateSave(false);
                setExtendedState(prefs.getInt(strWindowState, Frame.NORMAL));
                populateMRUList();
                populatePopupMenu();
                addUndoSupport();
            }

            @Override
            public void windowActivated(WindowEvent e) {
                jButtonPasteImage.setEnabled(ImageHelper.getClipboardImage() != null);
            }
        });

        setSize(
            snap(prefs.getInt(strFrameWidth, 500), 300, screen.width),
            snap(prefs.getInt(strFrameHeight, 360), 150, screen.height));
        setLocation(
            snap(prefs.getInt(strFrameX, (screen.width - getWidth()) / 2),
                screen.x, screen.x + screen.width - getWidth()),
            snap(prefs.getInt(strFrameY, screen.y + (screen.height - getHeight()) / 3),
                screen.y, screen.y + screen.height - getHeight()));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Copies resources from Jar to support directory.
     *
     * @param helpFile
     * @throws IOException
     */
    private void copyFileFromJarToSupportDir(File helpFile) throws IOException {
        if (!helpFile.exists()) {
            final ReadableByteChannel input
                    = Channels.newChannel(ClassLoader.getSystemResourceAsStream(helpFile.getName()));
            final FileChannel output = new FileOutputStream(helpFile).getChannel();
            output.transferFrom(input, 0, 1000000L);
            output.close();
            input.close();
        }
    }

    
    void jMenuItemOCRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemOCRActionPerformed
        // to be implemented in subclass
    }//GEN-LAST:event_jMenuItemOCRActionPerformed
    protected static Locale getLocale(String selectedUILang) {
        return new Locale(selectedUILang);
    }

    /**
     * Displays About box.
     */
    void about() {
        try {
            Properties config = new Properties();
            config.loadFromXML(getClass().getResourceAsStream("config.xml"));
            String version = config.getProperty("Version");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            Date releaseDate = sdf.parse(config.getProperty("ReleaseDate"));

          
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private void jMenuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExitActionPerformed
        quit();
    }//GEN-LAST:event_jMenuItemExitActionPerformed

    /**
     * Quits and saves application preferences before exit.
     */
    void quit() {     
        System.exit(0);
    }

    private void jMenuItemOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemOpenActionPerformed
        if (jFileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            currentDirectory = jFileChooser.getCurrentDirectory().getPath();
            openFile(jFileChooser.getSelectedFile());

            for (int i = 0; i < fileFilters.length; i++) {
                if (fileFilters[i] == jFileChooser.getFileFilter()) {
                    filterIndex = i;
                    break;
                }
            }
        }
    }//GEN-LAST:event_jMenuItemOpenActionPerformed

    /**
     * Opens image or text file.
     *
     * @param selectedFile
     */
    public void openFile(final File selectedFile) {
        if (!selectedFile.exists()) {
            JOptionPane.showMessageDialog(this, bundle.getString("File_not_exist"), APP_NAME, JOptionPane.ERROR_MESSAGE);
            return;
        }
        // if text file, load it into textarea
        if (selectedFile.getName().endsWith(".txt")) {
            if (!promptToSave()) {
                return;
            }
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(selectedFile), StandardCharsets.UTF_8));
                this.jTextArea1.read(in, null);
                in.close();
                this.textFile = selectedFile;
                javax.swing.text.Document doc = this.jTextArea1.getDocument();
                if (doc.getText(0, 1).equals("\uFEFF")) {
                    doc.remove(0, 1); // remove BOM
                }
                doc.addUndoableEditListener(rawListener);
                updateMRUList(selectedFile.getPath());
                updateSave(false);
                this.jTextArea1.requestFocusInWindow();
            } catch (Exception e) {
                logger.log(Level.WARNING, e.getMessage(), e);
            }
            return;
        }

        jLabelStatus.setText(bundle.getString("Loading_image..."));
        jProgressBar1.setIndeterminate(true);
        jProgressBar1.setString(bundle.getString("Loading_image..."));
        jProgressBar1.setVisible(true);
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        getGlassPane().setVisible(true);
        this.jButtonOCR.setEnabled(false);
        this.jMenuItemOCR.setEnabled(false);
     

        SwingWorker loadWorker = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                iioImageList = ImageIOHelper.getIIOImageList(selectedFile);
                imageList = ImageIconScalable.getImageList(iioImageList);
                inputfilename = selectedFile.getPath();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get(); // dummy method                   
                    loadImage();
                    setTitle(selectedFile.getName() + " - " + APP_NAME);
                    updateMRUList(selectedFile.getPath());
                } catch (InterruptedException e) {
                    logger.log(Level.WARNING, e.getMessage(), e);
                } catch (java.util.concurrent.ExecutionException e) {
                    String why;
                    Throwable cause = e.getCause();
                    if (cause != null) {
                        if (cause instanceof OutOfMemoryError) {
                            why = bundle.getString("OutOfMemoryError");
                        } else {
                            why = cause.getMessage();
                        }
                    } else {
                        why = e.getMessage();
                    }
                    logger.log(Level.SEVERE, e.getMessage(), e);
                    JOptionPane.showMessageDialog(Gui.this, why, APP_NAME, JOptionPane.ERROR_MESSAGE);
                } finally {
                    jLabelStatus.setText(bundle.getString("Loading_completed"));
                    jProgressBar1.setString(bundle.getString("Loading_completed"));
                    jProgressBar1.setIndeterminate(false);
                    getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    getGlassPane().setVisible(false);
                    jButtonOCR.setEnabled(true);
                    jMenuItemOCR.setEnabled(true);
                   
                }
            }
        };

        loadWorker.execute();
    }

    /**
     * Loads image.
     */
    @SuppressWarnings("unchecked")
    void loadImage() {
        if (imageList == null) {
            JOptionPane.showMessageDialog(this, bundle.getString("Cannotloadimage"), APP_NAME, JOptionPane.ERROR_MESSAGE);
            return;
        }

        imageTotal = imageList.size();
        imageIndex = 0;
        scaleX = scaleY = 1f;
        isFitImageSelected = false;

        Integer[] pages = new Integer[imageTotal];
        for (int i = 0; i < imageTotal; i++) {
            pages[i] = i + 1;
        }
      

        displayImage();
        loadThumbnails();

        // clear undo buffer
        clearStack();

      
        this.jButtonActualSize.setEnabled(false);
      
       

        if (imageList.size() == 1) {
           
        } else {
            
        }

      

        setButton();
        Dropaaa();
        WorkerOpenFile();
        WorkerOpenFile2();
        WorkerOpenFile3();
        WorkerOpenFile4();
        WorkerOpenFile5();
         
     
    }

 void WorkerOpenFile(){ 

 Rectangle rect = ((JImageLabel) jImageLabel).getRect();

        if (rect != null) {
            checkRectDrop = true;
            try {
                ImageIcon ii = (ImageIcon) this.jImageLabel.getIcon();
                int offsetX = 0;
                int offsetY = 0;
                if (ii.getIconWidth() < this.jScrollPaneImage.getWidth()) {
                    offsetX = (this.jScrollPaneImage.getViewport().getWidth() - ii.getIconWidth()) / 2;
                }
                if (ii.getIconHeight() < this.jScrollPaneImage.getHeight()) {
                    offsetY = (this.jScrollPaneImage.getViewport().getHeight() - ii.getIconHeight()) / 2;
                }
//               
                rect = new Rectangle((int) ((rect.x - offsetX) * scaleX), (int) ((rect.y - offsetY) * scaleY), (int) (rect.width * scaleX), (int) (rect.height * scaleY));

               
                performOCR1(iioImageList, inputfilename, imageIndex, rect);
            } catch (RasterFormatException rfe) {
                logger.log(Level.SEVERE, rfe.getMessage(), rfe);
                JOptionPane.showMessageDialog(this, rfe.getMessage(), APP_NAME, JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        } else {
             rect = new Rectangle((int)(imageIcon.getIconWidth()*0.47),(int) (imageIcon.getIconHeight()*0.07));
            System.out.println(imageIcon.getIconWidth() + "X" +imageIcon.getIconHeight() );
             rectNguoigui = rect;
      
            performOCR1(iioImageList, inputfilename, imageIndex, rectNguoigui);
        } 
	   }
	   
	   
	//----------------------


void WorkerOpenFile2(){ 
   Rectangle rect = ((JImageLabel) jImageLabel).getRect();

        if (rect != null) {
            try {
                 checkRectDrop = true;
                ImageIcon ii = (ImageIcon) this.jImageLabel.getIcon();
                int offsetX = 0;
                int offsetY = 0;
                if (ii.getIconWidth() < this.jScrollPaneImage.getWidth()) {
                    offsetX = (this.jScrollPaneImage.getViewport().getWidth() - ii.getIconWidth()) / 2;
                }
                if (ii.getIconHeight() < this.jScrollPaneImage.getHeight()) {
                    offsetY = (this.jScrollPaneImage.getViewport().getHeight() - ii.getIconHeight()) / 2;
                }
//               
                rect = new Rectangle((int) ((rect.x - offsetX) * scaleX), (int) ((rect.y - offsetY) * scaleY), (int) (rect.width * scaleX), (int) (rect.height * scaleY));

               
                performOCR2(iioImageList, inputfilename, imageIndex, rect);
            } catch (RasterFormatException rfe) {
                logger.log(Level.SEVERE, rfe.getMessage(), rfe);
                JOptionPane.showMessageDialog(this, rfe.getMessage(), APP_NAME, JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        } else {
            // rect = new Rectangle((int) (imageIcon.getIconHeight()*0.04),(int)(imageIcon.getIconWidth()*0.1),(int)(imageIcon.getIconWidth()*0.40),(int) (imageIcon.getIconHeight()*0.05));
            //System.out.println(imageIcon.getIconWidth() + "X" +imageIcon.getIconHeight() );
           // rectSo = rect;
            for(int i =0; i<7;i++){
           
                if(listRectImage.get(i).getWidth() > imageIcon.getIconWidth()/2 ){
                     rect= new Rectangle(listRectImage.get(i).x -5, listRectImage.get(i).y-5, (int) ((listRectImage.get(i).width+10)*0.4),listRectImage.get(i).height+10);
                }
                else
                {
                     rect= new Rectangle(listRectImage.get(i).x -5, listRectImage.get(i).y-5,listRectImage.get(i).width+10,listRectImage.get(i).height+10);
                }
               
                performOCR2(iioImageList, inputfilename, imageIndex, rect);
                rect = null;
            }
            
        }
 
	   }
	
	   
	   void WorkerOpenFile3(){ 
		  Rectangle rect = ((JImageLabel) jImageLabel).getRect();

        if (rect != null) {
            try {
                 checkRectDrop = true;
                ImageIcon ii = (ImageIcon) this.jImageLabel.getIcon();
                int offsetX = 0;
                int offsetY = 0;
                if (ii.getIconWidth() < this.jScrollPaneImage.getWidth()) {
                    offsetX = (this.jScrollPaneImage.getViewport().getWidth() - ii.getIconWidth()) / 2;
                }
                if (ii.getIconHeight() < this.jScrollPaneImage.getHeight()) {
                    offsetY = (this.jScrollPaneImage.getViewport().getHeight() - ii.getIconHeight()) / 2;
                }
//               
                rect = new Rectangle((int) ((rect.x - offsetX) * scaleX), (int) ((rect.y - offsetY) * scaleY), (int) (rect.width * scaleX), (int) (rect.height * scaleY));

               
                performOCR3(iioImageList, inputfilename, imageIndex, rect);
            } catch (RasterFormatException rfe) {
                logger.log(Level.SEVERE, rfe.getMessage(), rfe);
                JOptionPane.showMessageDialog(this, rfe.getMessage(), APP_NAME, JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        } else {
            // rect = new Rectangle((int) (imageIcon.getIconHeight()*0.04),(int)(imageIcon.getIconWidth()*0.1),(int)(imageIcon.getIconWidth()*0.40),(int) (imageIcon.getIconHeight()*0.05));
            //System.out.println(imageIcon.getIconWidth() + "X" +imageIcon.getIconHeight() );
           // rectSo = rect;
            for(int i =0; i<5;i++){
           
                if(listRectImage.get(i).getWidth() > imageIcon.getIconWidth()/2 ){
                     rect= new Rectangle((int) ((listRectImage.get(i).width)*0.5)+listRectImage.get(i).x, listRectImage.get(i).y-5, (int) ((listRectImage.get(i).width)*0.5)+10,listRectImage.get(i).height+15);
                   
                }
                else
                {
                     rect= new Rectangle(listRectImage.get(i).x -10, listRectImage.get(i).y-10,listRectImage.get(i).width+15,listRectImage.get(i).height+15);
                    
                }
               
                performOCR3(iioImageList, inputfilename, imageIndex, rect);
                rect = null;
            }
            // performOCR2(iioImageList, inputfilename, imageIndex, rect);
        }
 
	   }
	   
	   
	   void WorkerOpenFile4(){ 
		    Rectangle rect = ((JImageLabel) jImageLabel).getRect();

        if (rect != null) {
            try {
                 checkRectDrop = true;
                ImageIcon ii = (ImageIcon) this.jImageLabel.getIcon();
                int offsetX = 0;
                int offsetY = 0;
                if (ii.getIconWidth() < this.jScrollPaneImage.getWidth()) {
                    offsetX = (this.jScrollPaneImage.getViewport().getWidth() - ii.getIconWidth()) / 2;
                }
                if (ii.getIconHeight() < this.jScrollPaneImage.getHeight()) {
                    offsetY = (this.jScrollPaneImage.getViewport().getHeight() - ii.getIconHeight()) / 2;
                }
//               
                rect = new Rectangle((int) ((rect.x - offsetX) * scaleX), (int) ((rect.y - offsetY) * scaleY), (int) (rect.width * scaleX), (int) (rect.height * scaleY));

               
                performOCR4(iioImageList, inputfilename, imageIndex, rect);
            } catch (RasterFormatException rfe) {
                logger.log(Level.SEVERE, rfe.getMessage(), rfe);
                JOptionPane.showMessageDialog(this, rfe.getMessage(), APP_NAME, JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        } else {
            // rect = new Rectangle((int) (imageIcon.getIconHeight()*0.04),(int)(imageIcon.getIconWidth()*0.1),(int)(imageIcon.getIconWidth()*0.40),(int) (imageIcon.getIconHeight()*0.05));
            //System.out.println(imageIcon.getIconWidth() + "X" +imageIcon.getIconHeight() );
           // rectSo = rect;
            for(int i =2; i<7;i++){
           
                if(listRectImage.get(i).getWidth() > imageIcon.getIconWidth()/2 ){
                     rect= new Rectangle(listRectImage.get(i).x -5, listRectImage.get(i).y-5, (int) ((listRectImage.get(i).width+10)*0.4),listRectImage.get(i).height+10);
                }
                else
                {
                     rect= new Rectangle(listRectImage.get(i).x -5, listRectImage.get(i).y-5,listRectImage.get(i).width+10,listRectImage.get(i).height+10);
                }
               
                performOCR4(iioImageList, inputfilename, imageIndex, rect);
                rect = null;
            }
            // performOCR2(iioImageList, inputfilename, imageIndex, rect);
        }
 
	   }
	   
	   
	   void WorkerOpenFile5(){ 
  Rectangle rect = ((JImageLabel) jImageLabel).getRect();

        if (rect != null) {
            try {
                 checkRectDrop = true;
                ImageIcon ii = (ImageIcon) this.jImageLabel.getIcon();
                int offsetX = 0;
                int offsetY = 0;
                if (ii.getIconWidth() < this.jScrollPaneImage.getWidth()) {
                    offsetX = (this.jScrollPaneImage.getViewport().getWidth() - ii.getIconWidth()) / 2;
                }
                if (ii.getIconHeight() < this.jScrollPaneImage.getHeight()) {
                    offsetY = (this.jScrollPaneImage.getViewport().getHeight() - ii.getIconHeight()) / 2;
                }
//               
                rect = new Rectangle((int) ((rect.x - offsetX) * scaleX), (int) ((rect.y - offsetY) * scaleY), (int) (rect.width * scaleX), (int) (rect.height * scaleY));

               
                performOCR5(iioImageList, inputfilename, imageIndex, rect);
            } catch (RasterFormatException rfe) {
                logger.log(Level.SEVERE, rfe.getMessage(), rfe);
                JOptionPane.showMessageDialog(this, rfe.getMessage(), APP_NAME, JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        } else {
            // rect = new Rectangle((int) (imageIcon.getIconHeight()*0.04),(int)(imageIcon.getIconWidth()*0.1),(int)(imageIcon.getIconWidth()*0.40),(int) (imageIcon.getIconHeight()*0.05));
            //System.out.println(imageIcon.getIconWidth() + "X" +imageIcon.getIconHeight() );
           // rectSo = rect;
           
                
               
                performOCR5(iioImageList, inputfilename, imageIndex, null);
              
            
        }  
 
	   }
	   
	   
	 
       
    /**
     * Displays image.
     */
    void displayImage() {
        imageIcon = imageList.get(imageIndex).clone();
        originalW = imageIcon.getIconWidth();
        originalH = imageIcon.getIconHeight();
        this.jLabelDimensionValue.setText(String.format("%s × %spx  %sbpp", originalW, originalH, ((BufferedImage) imageIcon.getImage()).getColorModel().getPixelSize()));

        if (this.isFitImageSelected) {
            // scale image to fit the scrollpane
            Dimension fitSize = fitImagetoContainer(originalW, originalH, jScrollPaneImage.getViewport().getWidth(), jScrollPaneImage.getViewport().getHeight());
            imageIcon.setScaledSize(fitSize.width, fitSize.height);
            setScale(fitSize.width, fitSize.height);
        } else if (Math.abs(scaleX - 1f) > 0.001f) {
            // scale image for zoom
            imageIcon.setScaledSize((int) (originalW / scaleX), (int) (originalH / scaleY));
        }

        jImageLabel.setIcon(imageIcon);
        this.jScrollPaneImage.getViewport().setViewPosition(curScrollPos = new Point());
        ((JImageLabel) jImageLabel).deselect();
        ((JImageLabel) jImageLabel).setSegmentedRegions(null);
        setSegmentedRegions();
//        jImageLabel.revalidate();
    }

    void setSegmentedRegions() {
        if (!this.jCheckBoxMenuItemSegmentedRegions.isSelected() || iioImageList == null || this.jButtonActualSize.isEnabled()) {
            ((JImageLabel) jImageLabel).setSegmentedRegions(null);
            return;
        }

        try {
            OCR<IIOImage> ocrEngine = new OCRImages(tessPath); // for Tess4J
            ocrEngine.setDatapath(datapath);
            HashMap<Color, List<Rectangle>> map = ((JImageLabel) jImageLabel).getSegmentedRegions();
            
            if (map == null) {
                map = new HashMap<Color, List<Rectangle>>();
            }

            IIOImage image = iioImageList.get(imageIndex);

            List<Rectangle> regions;

            if (jCheckBoxMenuItemBlock.isSelected()) {
                if (!map.containsKey(Color.GRAY)) {
                   // regions = ocrEngine.getSegmentedRegions(image, ITessAPI.TessPageIteratorLevel.RIL_BLOCK);
                   // map.put(Color.GRAY, regions);
                    List<Rectangle> a = new ArrayList<>();
                   if(a!=null){
                       a.add(rectSo);
                        map.put(Color.PINK, a);
                        rectSo = new Rectangle(0,0,0,0);
                   }
                   else{
                        rectSo = new Rectangle(0,0,0,0);
                        a.add(rectSo);
                        map.put(Color.PINK, a);
                   }
                   
                    
                    
                }
            } else {
                map.remove(Color.GRAY);
            }

            if (jCheckBoxMenuItemPara.isSelected()) {
                if (!map.containsKey(Color.GREEN)) {
                    regions = ocrEngine.getSegmentedRegions(image, ITessAPI.TessPageIteratorLevel.RIL_PARA);
                    map.put(Color.GREEN, regions);
                }
            } else {
                map.remove(Color.GREEN);
            }

            if (jCheckBoxMenuItemTextLine.isSelected()) {
                if (!map.containsKey(Color.RED)) {
                    regions = ocrEngine.getSegmentedRegions(image, ITessAPI.TessPageIteratorLevel.RIL_TEXTLINE);
                    
                   
                    map.put(Color.RED, regions);
                     listRectImage = regions;
                  //  for(Rectangle a: listRectImage){
                  //      a.setRect(a.x *1.1, a.y*1.1, a.width*1.1, a.height*1.1);
                  //  }
                }
            } else {
                map.remove(Color.RED);
            }

            if (jCheckBoxMenuItemWord.isSelected()) {
                if (!map.containsKey(Color.BLUE)) {
                    regions = ocrEngine.getSegmentedRegions(image, ITessAPI.TessPageIteratorLevel.RIL_WORD);
                    map.put(Color.BLUE, regions);
                }
            } else {
                map.remove(Color.BLUE);
            }

            if (jCheckBoxMenuItemSymbol.isSelected()) {
                if (!map.containsKey(Color.MAGENTA)) {
                    regions = ocrEngine.getSegmentedRegions(image, ITessAPI.TessPageIteratorLevel.RIL_SYMBOL);
                    map.put(Color.MAGENTA, regions);
                }
            } else {
                map.remove(Color.MAGENTA);
            }

            ((JImageLabel) jImageLabel).setSegmentedRegions(map);
            jImageLabel.repaint();
            jImageLabel.revalidate();
        } catch (Exception ex) {
            Logger.getLogger(Gui.class.getName()).log(Level.INFO, null, ex);
        }
    }

    void clearStack() {
        // to be implemented in subclass
    }

    /**
     * Save file action.
     *
     * @return
     */
    boolean saveAction() {
        if (textFile == null || !textFile.exists()) {
            return saveFileDlg();
        } else {
            return saveTextFile();
        }
    }

    /**
     * Displays save file dialog.
     *
     * @return
     */
    boolean saveFileDlg() {
        JFileChooser saveChooser = new JFileChooser(outputDirectory);
        FileFilter textFilter = new SimpleFilter("txt", bundle.getString("UTF-8_Text"));
        saveChooser.addChoosableFileFilter(textFilter);
        saveChooser.setFileFilter(textFilter);
        saveChooser.setDialogTitle(bundle.getString("Save_As"));
        if (textFile != null) {
            saveChooser.setSelectedFile(textFile);
        }

        if (saveChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            outputDirectory = saveChooser.getCurrentDirectory().getPath();
            File f = saveChooser.getSelectedFile();
            if (saveChooser.getFileFilter() == textFilter) {
                if (!f.getName().endsWith(".txt")) {
                    f = new File(f.getPath() + ".txt");
                }
                if (textFile != null && textFile.getPath().equals(f.getPath())) {
                    if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(
                            Gui.this,
                            String.format(bundle.getString("file_already_exist"), textFile.getName()),
                            bundle.getString("Confirm_Save_As"), JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE)) {
                        return false;
                    }
                } else {
                    textFile = f;
                }
            } else {
                textFile = f;
            }
            return saveTextFile();
        } else {
            return false;
        }
    }

    /**
     * Saves output text file.
     *
     * @return
     */
    boolean saveTextFile() {
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        getGlassPane().setVisible(true);

        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(textFile), UTF8));
            jTextArea1.write(out);
            out.close();
            updateMRUList(textFile.getPath());
            updateSave(false);
        } catch (OutOfMemoryError oome) {
            JOptionPane.showMessageDialog(this, oome.getMessage(), bundle.getString("OutOfMemoryError"), JOptionPane.ERROR_MESSAGE);
        } catch (FileNotFoundException fnfe) {
            showError(fnfe, fnfe.getMessage());
        } catch (Exception ex) {
            showError(ex, ex.getMessage());
        } finally {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    getGlassPane().setVisible(false);
                }
            });
        }

        return true;
    }

    /**
     * Displays a dialog to save changes.
     *
     * @return false if user canceled, true else
     */
    protected boolean promptToSave() {
        if (!textChanged) {
            return true;
        }
        switch (JOptionPane.showConfirmDialog(this,
                String.format(bundle.getString("Do_you_want_to_save_the_changes_to_"),
                        (textFile == null ? bundle.getString("Untitled") : textFile.getName())),
                APP_NAME, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE)) {
            case JOptionPane.YES_OPTION:
                return saveAction();
            case JOptionPane.NO_OPTION:
                return true;
            default:
                return false;
        }
    }

    /**
     * Updates the Save action.
     *
     * @param modified whether file has been modified
     */
    void updateSave(boolean modified) {
        if (textChanged != modified) {
            textChanged = modified;
           
           
            rootPane.putClientProperty("windowModified", modified);
            // see http://developer.apple.com/qa/qa2001/qa1146.html
        }
    }

    /**
     * Enables or disables page navigation buttons.
     */
    void setButton() {
        if (imageIndex == 0) {
           
        } else {
           
        }

        if (imageIndex == imageList.size() - 1) {
           
        } else {
            
        }
    }

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        jSplitPane1.setDividerLocation(jSplitPane1.getWidth() / 2);

        if (isFitImageSelected && imageIcon != null) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    ((JImageLabel) jImageLabel).deselect();
                    Dimension fitSize = fitImagetoContainer(originalW, originalH, jScrollPaneImage.getViewport().getWidth(), jScrollPaneImage.getViewport().getHeight());
                    fitImageChange(fitSize.width, fitSize.height);
                    setScale(fitSize.width, fitSize.height);
                }
            });
        }
    }//GEN-LAST:event_formComponentResized

    /**
     * Best fit image height and width calculation algorithm.
     *
     * http://www.karpach.com/Best-fit-calculations-algorithm.htm
     *
     * @param w
     * @param h
     * @param maxWidth
     * @param maxHeight
     */
    Dimension fitImagetoContainer(int w, int h, int maxWidth, int maxHeight) {
        float ratio = (float) w / h;

        w = maxWidth;
        h = (int) Math.floor(maxWidth / ratio);

        if (h > maxHeight) {
            h = maxHeight;
            w = (int) Math.floor(maxHeight * ratio);
        }

        return new Dimension(w, h);
    }

    /**
     * Sets image scale.
     *
     * @param width
     * @param height
     */
    void setScale(int width, int height) {
        scaleX = (float) originalW / width;
        scaleY = (float) originalH / height;
        if (scaleX > scaleY) {
            scaleY = scaleX;
        } else {
            scaleX = scaleY;
        }
    }

    void fitImageChange(final int width, final int height) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                imageIcon.setScaledSize(width, height);
                jScrollPaneImage.getViewport().setViewPosition(curScrollPos);
                jImageLabel.revalidate();
                jScrollPaneImage.repaint();
            }
        });
    }

    void jMenuItemAutocropActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAutocropActionPerformed
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }//GEN-LAST:event_jMenuItemAutocropActionPerformed

    void jMenuItemGrayscaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemGrayscaleActionPerformed
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }//GEN-LAST:event_jMenuItemGrayscaleActionPerformed

    void jMenuItemInvertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemInvertActionPerformed
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }//GEN-LAST:event_jMenuItemInvertActionPerformed

    void jMenuItemSharpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSharpenActionPerformed
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }//GEN-LAST:event_jMenuItemSharpenActionPerformed

    void jMenuItemUndoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemUndoActionPerformed
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }//GEN-LAST:event_jMenuItemUndoActionPerformed

    void Dropaaa(){
        
    }
    private void jCheckBoxMenuItemSegmentedRegionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemSegmentedRegionsActionPerformed
        this.jButtonSegmentedRegions.setVisible(this.jCheckBoxMenuItemSegmentedRegions.isSelected());
        setSegmentedRegions();
    }//GEN-LAST:event_jCheckBoxMenuItemSegmentedRegionsActionPerformed

    private void jCheckBoxMenuItemSymbolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemSymbolActionPerformed
        setSegmentedRegions();
    }//GEN-LAST:event_jCheckBoxMenuItemSymbolActionPerformed

    private void jCheckBoxMenuItemBlockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemBlockActionPerformed
        setSegmentedRegions();
    }//GEN-LAST:event_jCheckBoxMenuItemBlockActionPerformed

    private void jCheckBoxMenuItemParaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemParaActionPerformed
        setSegmentedRegions();
    }//GEN-LAST:event_jCheckBoxMenuItemParaActionPerformed

    private void jCheckBoxMenuItemWordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemWordActionPerformed
        setSegmentedRegions();
    }//GEN-LAST:event_jCheckBoxMenuItemWordActionPerformed

    private void jCheckBoxMenuItemTextLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemTextLineActionPerformed
        setSegmentedRegions();
    }//GEN-LAST:event_jCheckBoxMenuItemTextLineActionPerformed

    private void jButtonSegmentedRegionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSegmentedRegionsActionPerformed
        jPopupMenuSegmentedRegions.show(jButtonSegmentedRegions, 0, 0 - (int) jPopupMenuSegmentedRegions.getPreferredSize().getHeight());
    }//GEN-LAST:event_jButtonSegmentedRegionsActionPerformed

    void jMenuItemCropActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCropActionPerformed
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }//GEN-LAST:event_jMenuItemCropActionPerformed

    private void jComboBoxLangItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxLangItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            if (jComboBoxLang.getSelectedIndex() != -1) {
                curLangCode = installedLanguageCodes[jComboBoxLang.getSelectedIndex()];
            } else {
                curLangCode = jComboBoxLang.getSelectedItem().toString();
            }
            // Hide Viet Input Method submenu if selected OCR Language is not Vietnamese
            boolean vie = curLangCode.contains("vie") || curLangCode.contains("Vietnamese");
            VietKeyListener.setVietModeEnabled(vie);

        }
    }//GEN-LAST:event_jComboBoxLangItemStateChanged

    private void jButtonClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClearActionPerformed
        if (textFile == null || promptToSave()) {
            this.jTextArea1.setText(null);
            this.jTextArea2.setText(null);
            this.jTextArea3.setText(null);
            this.jTextArea4.setText(null);
            this.jTextArea5.setText(null);
            this.jTextArea6.setText(null);
            this.jTextArea7.setText(null);
            this.JImageLabel2.setIcon(null);
            updateSave(false);
        }
    }//GEN-LAST:event_jButtonClearActionPerformed

    private void jButtonCancelOCRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelOCRActionPerformed
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }//GEN-LAST:event_jButtonCancelOCRActionPerformed

    private void jButtonOCRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOCRActionPerformed
       // jMenuItemOCRActionPerformed(evt);
        WorkerOpenFile();
        WorkerOpenFile2();
        WorkerOpenFile3();
        WorkerOpenFile4();
        WorkerOpenFile5();
    }//GEN-LAST:event_jButtonOCRActionPerformed

    private void jButtonActualSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonActualSizeActionPerformed
         this.jButtonActualSize.setEnabled(false);
       
     
        ((JImageLabel) jImageLabel).deselect();
        setSegmentedRegions();
        fitImageChange(originalW, originalH);
        scaleX = scaleY = 1f;
        isFitImageSelected = false;
    }//GEN-LAST:event_jButtonActualSizeActionPerformed

    private void jScrollPaneImageMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_jScrollPaneImageMouseWheelMoved
        if (evt.isControlDown()) {
            final float delta = -0.12f * evt.getWheelRotation();

            if (delta <= 0) {
                // set minimum size to zoom
                if (imageIcon.getIconWidth() < 100) {
                    return;
                }
            } else // set maximum size to zoom
            {
                if (imageIcon.getIconWidth() > 10000) {
                    return;
                }
            }

            ((JImageLabel) jImageLabel).deselect();
            ((JImageLabel) jImageLabel).setSegmentedRegions(null);

            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    int width = imageIcon.getIconWidth();
                    int height = imageIcon.getIconHeight();
                    width += width * delta;
                    height += height * delta;
                    imageIcon.setScaledSize(width, height);

                    jImageLabel.revalidate();
                    jScrollPaneImage.repaint();

                    scaleX = originalW / (float) width;
                    scaleY = originalH / (float) height;

                }
            });

            isFitImageSelected = false;
            this.jButtonActualSize.setEnabled(true);
        }
    }//GEN-LAST:event_jScrollPaneImageMouseWheelMoved

    private void jImageLabelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jImageLabelMouseEntered
        if (!this.jImageLabel.isFocusOwner() && this.isActive()) {
            jImageLabel.requestFocusInWindow();
        }
    }//GEN-LAST:event_jImageLabelMouseEntered

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
       if (jImageLabel.getIcon() == null) {
            JOptionPane.showMessageDialog(this, bundle.getString("Please_load_an_image."), APP_NAME, JOptionPane.INFORMATION_MESSAGE);
            return;
        }

         Rectangle rect = ((JImageLabel) jImageLabel).getRect();

        if (rect != null) {
            checkRectDrop = true;
            try {
                ImageIcon ii = (ImageIcon) this.jImageLabel.getIcon();
                int offsetX = 0;
                int offsetY = 0;
                if (ii.getIconWidth() < this.jScrollPaneImage.getWidth()) {
                    offsetX = (this.jScrollPaneImage.getViewport().getWidth() - ii.getIconWidth()) / 2;
                }
                if (ii.getIconHeight() < this.jScrollPaneImage.getHeight()) {
                    offsetY = (this.jScrollPaneImage.getViewport().getHeight() - ii.getIconHeight()) / 2;
                }
//               
                rect = new Rectangle((int) ((rect.x - offsetX) * scaleX), (int) ((rect.y - offsetY) * scaleY), (int) (rect.width * scaleX), (int) (rect.height * scaleY));

               
                performOCR1(iioImageList, inputfilename, imageIndex, rect);
            } catch (RasterFormatException rfe) {
                logger.log(Level.SEVERE, rfe.getMessage(), rfe);
                JOptionPane.showMessageDialog(this, rfe.getMessage(), APP_NAME, JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        } else {
             rect = new Rectangle((int)(imageIcon.getIconWidth()*0.47),(int) (imageIcon.getIconHeight()*0.07));
            System.out.println(imageIcon.getIconWidth() + "X" +imageIcon.getIconHeight() );
             rectNguoigui = rect;
      
            performOCR1(iioImageList, inputfilename, imageIndex, rectNguoigui);
        }
       
       
       
       
       
       
       
       
       
       
       
       
       
        
        
        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if (jImageLabel.getIcon() == null) {
            JOptionPane.showMessageDialog(this, bundle.getString("Please_load_an_image."), APP_NAME, JOptionPane.INFORMATION_MESSAGE);
            return;
        }

         Rectangle rect = ((JImageLabel) jImageLabel).getRect();

        if (rect != null) {
            try {
                 checkRectDrop = true;
                ImageIcon ii = (ImageIcon) this.jImageLabel.getIcon();
                int offsetX = 0;
                int offsetY = 0;
                if (ii.getIconWidth() < this.jScrollPaneImage.getWidth()) {
                    offsetX = (this.jScrollPaneImage.getViewport().getWidth() - ii.getIconWidth()) / 2;
                }
                if (ii.getIconHeight() < this.jScrollPaneImage.getHeight()) {
                    offsetY = (this.jScrollPaneImage.getViewport().getHeight() - ii.getIconHeight()) / 2;
                }
//               
                rect = new Rectangle((int) ((rect.x - offsetX) * scaleX), (int) ((rect.y - offsetY) * scaleY), (int) (rect.width * scaleX), (int) (rect.height * scaleY));

               
                performOCR2(iioImageList, inputfilename, imageIndex, rect);
            } catch (RasterFormatException rfe) {
                logger.log(Level.SEVERE, rfe.getMessage(), rfe);
                JOptionPane.showMessageDialog(this, rfe.getMessage(), APP_NAME, JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        } else {
            // rect = new Rectangle((int) (imageIcon.getIconHeight()*0.04),(int)(imageIcon.getIconWidth()*0.1),(int)(imageIcon.getIconWidth()*0.40),(int) (imageIcon.getIconHeight()*0.05));
            //System.out.println(imageIcon.getIconWidth() + "X" +imageIcon.getIconHeight() );
           // rectSo = rect;
            for(int i =0; i<7;i++){
           
                if(listRectImage.get(i).getWidth() > imageIcon.getIconWidth()/2 ){
                     rect= new Rectangle(listRectImage.get(i).x -5, listRectImage.get(i).y-5, (int) ((listRectImage.get(i).width+10)*0.4),listRectImage.get(i).height+10);
                }
                else
                {
                     rect= new Rectangle(listRectImage.get(i).x -5, listRectImage.get(i).y-5,listRectImage.get(i).width+10,listRectImage.get(i).height+10);
                }
               
                performOCR2(iioImageList, inputfilename, imageIndex, rect);
                rect = null;
            }
            
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
         if (jImageLabel.getIcon() == null) {
            JOptionPane.showMessageDialog(this, bundle.getString("Please_load_an_image."), APP_NAME, JOptionPane.INFORMATION_MESSAGE);
            return;
        }

         Rectangle rect = ((JImageLabel) jImageLabel).getRect();

        if (rect != null) {
            try {
                 checkRectDrop = true;
                ImageIcon ii = (ImageIcon) this.jImageLabel.getIcon();
                int offsetX = 0;
                int offsetY = 0;
                if (ii.getIconWidth() < this.jScrollPaneImage.getWidth()) {
                    offsetX = (this.jScrollPaneImage.getViewport().getWidth() - ii.getIconWidth()) / 2;
                }
                if (ii.getIconHeight() < this.jScrollPaneImage.getHeight()) {
                    offsetY = (this.jScrollPaneImage.getViewport().getHeight() - ii.getIconHeight()) / 2;
                }
//               
                rect = new Rectangle((int) ((rect.x - offsetX) * scaleX), (int) ((rect.y - offsetY) * scaleY), (int) (rect.width * scaleX), (int) (rect.height * scaleY));

               
                performOCR3(iioImageList, inputfilename, imageIndex, rect);
            } catch (RasterFormatException rfe) {
                logger.log(Level.SEVERE, rfe.getMessage(), rfe);
                JOptionPane.showMessageDialog(this, rfe.getMessage(), APP_NAME, JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        } else {
            // rect = new Rectangle((int) (imageIcon.getIconHeight()*0.04),(int)(imageIcon.getIconWidth()*0.1),(int)(imageIcon.getIconWidth()*0.40),(int) (imageIcon.getIconHeight()*0.05));
            //System.out.println(imageIcon.getIconWidth() + "X" +imageIcon.getIconHeight() );
           // rectSo = rect;
            for(int i =0; i<5;i++){
           
                if(listRectImage.get(i).getWidth() > imageIcon.getIconWidth()/2 ){
                     rect= new Rectangle((int) ((listRectImage.get(i).width)*0.5)+listRectImage.get(i).x, listRectImage.get(i).y-5, (int) ((listRectImage.get(i).width)*0.5)+10,listRectImage.get(i).height+15);
                   
                }
                else
                {
                     rect= new Rectangle(listRectImage.get(i).x -10, listRectImage.get(i).y-10,listRectImage.get(i).width+15,listRectImage.get(i).height+15);
                    
                }
               
                performOCR3(iioImageList, inputfilename, imageIndex, rect);
                rect = null;
            }
            // performOCR2(iioImageList, inputfilename, imageIndex, rect);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
         if (jImageLabel.getIcon() == null) {
            JOptionPane.showMessageDialog(this, bundle.getString("Please_load_an_image."), APP_NAME, JOptionPane.INFORMATION_MESSAGE);
            return;
        }

         Rectangle rect = ((JImageLabel) jImageLabel).getRect();

        if (rect != null) {
            try {
                 checkRectDrop = true;
                ImageIcon ii = (ImageIcon) this.jImageLabel.getIcon();
                int offsetX = 0;
                int offsetY = 0;
                if (ii.getIconWidth() < this.jScrollPaneImage.getWidth()) {
                    offsetX = (this.jScrollPaneImage.getViewport().getWidth() - ii.getIconWidth()) / 2;
                }
                if (ii.getIconHeight() < this.jScrollPaneImage.getHeight()) {
                    offsetY = (this.jScrollPaneImage.getViewport().getHeight() - ii.getIconHeight()) / 2;
                }
//               
                rect = new Rectangle((int) ((rect.x - offsetX) * scaleX), (int) ((rect.y - offsetY) * scaleY), (int) (rect.width * scaleX), (int) (rect.height * scaleY));

               
                performOCR4(iioImageList, inputfilename, imageIndex, rect);
            } catch (RasterFormatException rfe) {
                logger.log(Level.SEVERE, rfe.getMessage(), rfe);
                JOptionPane.showMessageDialog(this, rfe.getMessage(), APP_NAME, JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        } else {
            // rect = new Rectangle((int) (imageIcon.getIconHeight()*0.04),(int)(imageIcon.getIconWidth()*0.1),(int)(imageIcon.getIconWidth()*0.40),(int) (imageIcon.getIconHeight()*0.05));
            //System.out.println(imageIcon.getIconWidth() + "X" +imageIcon.getIconHeight() );
           // rectSo = rect;
            for(int i =2; i<7;i++){
           
                if(listRectImage.get(i).getWidth() > imageIcon.getIconWidth()/2 ){
                     rect= new Rectangle(listRectImage.get(i).x -5, listRectImage.get(i).y-5, (int) ((listRectImage.get(i).width+10)*0.4),listRectImage.get(i).height+10);
                }
                else
                {
                     rect= new Rectangle(listRectImage.get(i).x -5, listRectImage.get(i).y-5,listRectImage.get(i).width+10,listRectImage.get(i).height+10);
                }
               
                performOCR4(iioImageList, inputfilename, imageIndex, rect);
                rect = null;
            }
            // performOCR2(iioImageList, inputfilename, imageIndex, rect);
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
         // TODO add your handling code here:
         if (jImageLabel.getIcon() == null) {
            JOptionPane.showMessageDialog(this, bundle.getString("Please_load_an_image."), APP_NAME, JOptionPane.INFORMATION_MESSAGE);
            return;
        }

         Rectangle rect = ((JImageLabel) jImageLabel).getRect();

        if (rect != null) {
            try {
                 checkRectDrop = true;
                ImageIcon ii = (ImageIcon) this.jImageLabel.getIcon();
                int offsetX = 0;
                int offsetY = 0;
                if (ii.getIconWidth() < this.jScrollPaneImage.getWidth()) {
                    offsetX = (this.jScrollPaneImage.getViewport().getWidth() - ii.getIconWidth()) / 2;
                }
                if (ii.getIconHeight() < this.jScrollPaneImage.getHeight()) {
                    offsetY = (this.jScrollPaneImage.getViewport().getHeight() - ii.getIconHeight()) / 2;
                }
//               
                rect = new Rectangle((int) ((rect.x - offsetX) * scaleX), (int) ((rect.y - offsetY) * scaleY), (int) (rect.width * scaleX), (int) (rect.height * scaleY));

               
                performOCR5(iioImageList, inputfilename, imageIndex, rect);
            } catch (RasterFormatException rfe) {
                logger.log(Level.SEVERE, rfe.getMessage(), rfe);
                JOptionPane.showMessageDialog(this, rfe.getMessage(), APP_NAME, JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        } else {
            // rect = new Rectangle((int) (imageIcon.getIconHeight()*0.04),(int)(imageIcon.getIconWidth()*0.1),(int)(imageIcon.getIconWidth()*0.40),(int) (imageIcon.getIconHeight()*0.05));
            //System.out.println(imageIcon.getIconWidth() + "X" +imageIcon.getIconHeight() );
           // rectSo = rect;
           
                
               
                performOCR5(iioImageList, inputfilename, imageIndex, null);
              
            
        }  
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
         if (jImageLabel.getIcon() == null) {
            JOptionPane.showMessageDialog(this, bundle.getString("Please_load_an_image."), APP_NAME, JOptionPane.INFORMATION_MESSAGE);
            return;
        }

         Rectangle rect = ((JImageLabel) jImageLabel).getRect();

        if (rect != null) {
            try {
                 checkRectDrop = true;
                ImageIcon ii = (ImageIcon) this.jImageLabel.getIcon();
                int offsetX = 0;
                int offsetY = 0;
                if (ii.getIconWidth() < this.jScrollPaneImage.getWidth()) {
                    offsetX = (this.jScrollPaneImage.getViewport().getWidth() - ii.getIconWidth()) / 2;
                }
                if (ii.getIconHeight() < this.jScrollPaneImage.getHeight()) {
                    offsetY = (this.jScrollPaneImage.getViewport().getHeight() - ii.getIconHeight()) / 2;
                }
//               
                rect = new Rectangle((int) ((rect.x - offsetX) * scaleX), (int) ((rect.y - offsetY) * scaleY), (int) (rect.width * scaleX), (int) (rect.height * scaleY));

               
                performOCR6(iioImageList, inputfilename, imageIndex, rect);
            } catch (RasterFormatException rfe) {
                logger.log(Level.SEVERE, rfe.getMessage(), rfe);
                JOptionPane.showMessageDialog(this, rfe.getMessage(), APP_NAME, JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        } else {
            
           
                
               
             
            
        }  
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
         if (jImageLabel.getIcon() == null) {
            JOptionPane.showMessageDialog(this, bundle.getString("Please_load_an_image."), APP_NAME, JOptionPane.INFORMATION_MESSAGE);
            return;
        }

         Rectangle rect = ((JImageLabel) jImageLabel).getRect();

        if (rect != null) {
            try {
                 checkRectDrop = true;
                ImageIcon ii = (ImageIcon) this.jImageLabel.getIcon();
                int offsetX = 0;
                int offsetY = 0;
                if (ii.getIconWidth() < this.jScrollPaneImage.getWidth()) {
                    offsetX = (this.jScrollPaneImage.getViewport().getWidth() - ii.getIconWidth()) / 2;
                }
                if (ii.getIconHeight() < this.jScrollPaneImage.getHeight()) {
                    offsetY = (this.jScrollPaneImage.getViewport().getHeight() - ii.getIconHeight()) / 2;
                }
//               
                rect = new Rectangle((int) ((rect.x - offsetX) * scaleX), (int) ((rect.y - offsetY) * scaleY), (int) (rect.width * scaleX), (int) (rect.height * scaleY));

               
                performOCR7(iioImageList, inputfilename, imageIndex, rect);
            } catch (RasterFormatException rfe) {
                logger.log(Level.SEVERE, rfe.getMessage(), rfe);
                JOptionPane.showMessageDialog(this, rfe.getMessage(), APP_NAME, JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        } else {
            
           
                
               
             
            
        }  
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
         // TODO add your handling code here:
         if (jImageLabel.getIcon() == null) {
            JOptionPane.showMessageDialog(this, bundle.getString("Please_load_an_image."), APP_NAME, JOptionPane.INFORMATION_MESSAGE);
            return;
        }

         Rectangle rect = ((JImageLabel) jImageLabel).getRect();

        if (rect != null) {
            try {
                 checkRectDrop = true;
                ImageIcon ii = (ImageIcon) this.jImageLabel.getIcon();
                int offsetX = 0;
                int offsetY = 0;
                if (ii.getIconWidth() < this.jScrollPaneImage.getWidth()) {
                    offsetX = (this.jScrollPaneImage.getViewport().getWidth() - ii.getIconWidth()) / 2;
                }
                if (ii.getIconHeight() < this.jScrollPaneImage.getHeight()) {
                    offsetY = (this.jScrollPaneImage.getViewport().getHeight() - ii.getIconHeight()) / 2;
                }
//               
                rect = new Rectangle((int) ((rect.x - offsetX) * scaleX), (int) ((rect.y - offsetY) * scaleY), (int) (rect.width * scaleX), (int) (rect.height * scaleY));

               
                performOCR8(iioImageList, inputfilename, imageIndex, rect);
            } catch (RasterFormatException rfe) {
                logger.log(Level.SEVERE, rfe.getMessage(), rfe);
                JOptionPane.showMessageDialog(this, rfe.getMessage(), APP_NAME, JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        } else {
            
           
                
               
             
            
        }  
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
       
         BufferedImage originalImage;
        if (iioImageList == null) {
            JOptionPane.showMessageDialog(this, bundle.getString("Please_load_an_image."), APP_NAME, JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Rectangle rect = ((JImageLabel) jImageLabel).getRect();
        if (rect == null) {
       
            
        // create a new rectangle with scale factors and offets factored in
        rect = new Rectangle((int) (this.jImageLabel.getIcon().getIconWidth()*0.7), (int) (this.jImageLabel.getIcon().getIconHeight()*0.82), (int) (this.jImageLabel.getIcon().getIconWidth()*0.4), (int) (this.jImageLabel.getIcon().getIconHeight()*0.25));
        }

        ImageIcon ii = (ImageIcon) this.jImageLabel.getIcon();
        int offsetX = 0;
        int offsetY = 0;
        if (ii.getIconWidth() < this.jScrollPaneImage.getWidth()) {
            offsetX = (this.jScrollPaneImage.getViewport().getWidth() - ii.getIconWidth()) / 2;
        }
        if (ii.getIconHeight() < this.jScrollPaneImage.getHeight()) {
            offsetY = (this.jScrollPaneImage.getViewport().getHeight() - ii.getIconHeight()) / 2;
        }

        // create a new rectangle with scale factors and offets factored in
        rect = new Rectangle((int) ((rect.x - offsetX) * scaleX), (int) ((rect.y - offsetY) * scaleY), (int) (rect.width * scaleX), (int) (rect.height * scaleY));

        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        getGlassPane().setVisible(true);
        
        originalImage = (BufferedImage) iioImageList.get(imageIndex).getRenderedImage();
        BufferedImage croppedImage = net.Mchien.NhanDangCongVan.util.ImageHelper.crop(originalImage, rect);
       
        imageIcon = new ImageIconScalable(croppedImage);
        JImageLabel2.setIcon(imageIcon);
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        getGlassPane().setVisible(false);
    }//GEN-LAST:event_jButton9ActionPerformed
  
    
     void performOCR1(final List<IIOImage> iioImageList, String inputfilename, final int index, Rectangle rect) {
     
    }
     void performOCR2(final List<IIOImage> iioImageList, String inputfilename, final int index, Rectangle rect) {
     
    }
     
      void performOCR3(final List<IIOImage> iioImageList, String inputfilename, final int index, Rectangle rect) {
     
    }
      void performOCR4(final List<IIOImage> iioImageList, String inputfilename, final int index, Rectangle rect) {
     
    }
     void performOCR5(final List<IIOImage> iioImageList, String inputfilename, final int index, Rectangle rect) {
     
    }
     
       void performOCR6(final List<IIOImage> iioImageList, String inputfilename, final int index, Rectangle rect) {
     
    }
       void performOCR7(final List<IIOImage> iioImageList, String inputfilename, final int index, Rectangle rect) {
     
    }
       void performOCR8(final List<IIOImage> iioImageList, String inputfilename, final int index, Rectangle rect) {
     
    }
       
          void performOCR9(final List<IIOImage> iioImageList, String inputfilename, final int index, Rectangle rect) {
     
    }
    void doChange(final boolean isZoomIn) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                int width = imageIcon.getIconWidth();
                int height = imageIcon.getIconHeight();

                if (isZoomIn) {
                    imageIcon.setScaledSize((int) (width * ZOOM_FACTOR), (int) (height * ZOOM_FACTOR));
                } else {
                    imageIcon.setScaledSize((int) (width / ZOOM_FACTOR), (int) (height / ZOOM_FACTOR));
                }

                jImageLabel.revalidate();
                jScrollPaneImage.repaint();

                if (isZoomIn) {
                    scaleX /= ZOOM_FACTOR;
                    scaleY /= ZOOM_FACTOR;
                } else {
                    scaleX *= ZOOM_FACTOR;
                    scaleY *= ZOOM_FACTOR;
                }
            }
        });
    }
    /**
     * Pastes image from clipboard.
     */
    void pasteImage() {
        try {
            Image image = ImageHelper.getClipboardImage();
            if (image != null) {
                File tempFile = File.createTempFile("tmp", ".png");
                ImageIO.write((BufferedImage) image, "png", tempFile);
                openFile(tempFile);
                tempFile.deleteOnExit();
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        }
    }

    /**
     * Loads thumbnails.
     */
    void loadThumbnails() {
        // to be implemented in subclass
    }

    /**
     * Changes locale of UI elements.
     *
     * @param locale
     */
    void changeUILanguage(final Locale locale) {
        if (locale.equals(Locale.getDefault())) {
            return; // no change in locale
        }
        Locale.setDefault(locale);
        bundle = java.util.ResourceBundle.getBundle("net.sourceforge.vietocr.Gui");

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                FormLocalizer localizer = new FormLocalizer(Gui.this, Gui.class);
                localizer.ApplyCulture(bundle);

                if (helptopicsFrame != null) {
                   
                }
                jFileChooser.setDialogTitle(bundle.getString("jButtonOpen.ToolTipText"));
                popup.removeAll();
                populatePopupMenu();
                updateMRUMenu();

               
            }
        });
    }

    /**
     * Shows a warning message
     *
     * @param e the exception to warn about
     * @param message the message to display
     */
    public void showError(Exception e, String message) {
        logger.log(Level.WARNING, e.getMessage(), e);
        JOptionPane.showMessageDialog(this, message, APP_NAME, JOptionPane.WARNING_MESSAGE);
    }

    private int snap(final int ideal, final int min, final int max) {
        final int TOLERANCE = 0;
        return ideal < min + TOLERANCE ? min : (ideal > max - TOLERANCE ? max : ideal);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        selectedUILang = prefs.get(strUILanguage, "en");
        Locale.setDefault(getLocale(selectedUILang));

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new Gui().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JLabel JImageLabel2;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    public javax.swing.JButton jButtonActualSize;
    public javax.swing.JButton jButtonCancelOCR;
    public javax.swing.JButton jButtonClear;
    public javax.swing.JButton jButtonOCR;
    private javax.swing.JButton jButtonPasteImage;
    private javax.swing.JButton jButtonSegmentedRegions;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemBlock;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemPara;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemSegmentedRegions;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemSymbol;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemTextLine;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemWord;
    private javax.swing.JCheckBox jCheckBoxMenuWordWrap;
    public javax.swing.JComboBox jComboBoxLang;
    private javax.swing.JDesktopPane jDesktopPane1;
    protected javax.swing.JFileChooser jFileChooser;
    public javax.swing.JLabel jImageLabel;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelDimension;
    private javax.swing.JLabel jLabelDimensionValue;
    private javax.swing.JLabel jLabelLanguage;
    protected javax.swing.JLabel jLabelOEMvalue;
    protected javax.swing.JLabel jLabelPSMvalue;
    protected javax.swing.JLabel jLabelScreenshotModeValue;
    protected javax.swing.JLabel jLabelStatus;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JMenu jMenuCommand;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenu jMenuFilter;
    private javax.swing.JMenu jMenuImage;
    private javax.swing.JMenuItem jMenuItemAutocrop;
    private javax.swing.JMenuItem jMenuItemCrop;
    private javax.swing.JMenuItem jMenuItemExit;
    private javax.swing.JMenuItem jMenuItemGrayscale;
    private javax.swing.JMenuItem jMenuItemInvert;
    protected javax.swing.JMenuItem jMenuItemOCR;
    private javax.swing.JMenuItem jMenuItemOpen;
    private javax.swing.JMenuItem jMenuItemSharpen;
    protected javax.swing.JMenuItem jMenuItemUndo;
    private javax.swing.JMenu jMenuRecentFiles;
    private javax.swing.JPanel jPanelArrow;
    private javax.swing.JPanel jPanelImage;
    private javax.swing.JPanel jPanelStatus;
    private javax.swing.JPanel jPanelTextArea;
    public javax.swing.JPanel jPanelThumb;
    private javax.swing.JPopupMenu jPopupMenuSegmentedRegions;
    protected javax.swing.JProgressBar jProgressBar1;
    public javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JScrollPane jScrollPane2;
    public javax.swing.JScrollPane jScrollPane3;
    public javax.swing.JScrollPane jScrollPane4;
    public javax.swing.JScrollPane jScrollPane5;
    public javax.swing.JScrollPane jScrollPane6;
    public javax.swing.JScrollPane jScrollPane7;
    public javax.swing.JScrollPane jScrollPaneImage;
    public javax.swing.JScrollPane jScrollPaneThumbnail;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator12;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JSeparator jSeparatorDim;
    private javax.swing.JSeparator jSeparatorDimEnd;
    private javax.swing.JSeparator jSeparatorEnd;
    private javax.swing.JSeparator jSeparatorOEM;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPaneImage;
    public javax.swing.JTextArea jTextArea1;
    public javax.swing.JTextArea jTextArea2;
    public javax.swing.JTextArea jTextArea3;
    public javax.swing.JTextArea jTextArea4;
    public javax.swing.JTextArea jTextArea5;
    public javax.swing.JTextArea jTextArea6;
    public javax.swing.JTextArea jTextArea7;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    protected javax.swing.JPopupMenu popup;
    // End of variables declaration//GEN-END:variables
    private final UndoManager m_undo = new UndoManager();
    protected final UndoableEditSupport undoSupport = new UndoableEditSupport();
    private Action m_undoAction, m_redoAction, actionCut, actionCopy, actionPaste, actionDelete, actionSelectAll;
    private final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    private JFrame helptopicsFrame;
}
