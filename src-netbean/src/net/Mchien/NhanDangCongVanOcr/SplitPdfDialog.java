/**
 * Copyright @ 2009 Quan Nguyen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.Mchien.NhanDangCongVanOcr;

import net.Mchien.NhanDangCongVan.util.FormLocalizer;
import java.awt.event.*;
import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.*;
import net.Mchien.NhanDangCongVanOcr.components.SimpleFilter;

public class SplitPdfDialog extends javax.swing.JDialog {

    private SplitPdfArgs args;
    private int actionSelected = -1;
    protected ResourceBundle bundle;

    /** Creates new form SplitPdfDialog */
    public SplitPdfDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        disableBoxes(!this.jRadioButtonPages.isSelected());

        setLocationRelativeTo(getOwner());

        bundle = ResourceBundle.getBundle("net/sourceforge/vietocr/SplitPdfDialog");

        //  Handle escape key to hide the dialog
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action escapeAction =
                new AbstractAction() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        setVisible(false);
                    }
                };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", escapeAction);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jLabelInput = new javax.swing.JLabel();
        jLabelOutput = new javax.swing.JLabel();
        jTextFieldInputFile = new javax.swing.JTextField();
        jTextFieldOutputFile = new javax.swing.JTextField();
        jButtonInput = new javax.swing.JButton();
        jButtonOutput = new javax.swing.JButton();
        jRadioButtonPages = new javax.swing.JRadioButton();
        jLabelFrom = new javax.swing.JLabel();
        jTextFieldFrom = new NumericTextField();
        jLabelTo = new javax.swing.JLabel();
        jTextFieldTo = new NumericTextField();
        jRadioButtonFiles = new javax.swing.JRadioButton();
        jLabelNumPages = new javax.swing.JLabel();
        jTextFieldNumOfPages = new NumericTextField();
        jPanel2 = new javax.swing.JPanel();
        jButtonSplit = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("net/Mchien/NhanDangCongVanOcr/SplitPdfDialog"); // NOI18N
        setTitle(bundle.getString("this.Title")); // NOI18N
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 10, 20));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jLabelInput.setLabelFor(jTextFieldInputFile);
        jLabelInput.setText(bundle.getString("jLabelInput.Text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(jLabelInput, gridBagConstraints);

        jLabelOutput.setLabelFor(jTextFieldOutputFile);
        jLabelOutput.setText(bundle.getString("jLabelOutput.Text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(jLabelOutput, gridBagConstraints);

        jTextFieldInputFile.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(jTextFieldInputFile, gridBagConstraints);

        jTextFieldOutputFile.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(jTextFieldOutputFile, gridBagConstraints);

        jButtonInput.setText("…");
        jButtonInput.setToolTipText(bundle.getString("jButtonInput.ToolTipText")); // NOI18N
        jButtonInput.setPreferredSize(new java.awt.Dimension(30, 23));
        jButtonInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonInputActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        jPanel1.add(jButtonInput, gridBagConstraints);

        jButtonOutput.setText("…");
        jButtonOutput.setToolTipText(bundle.getString("jButtonOutput.ToolTipText")); // NOI18N
        jButtonOutput.setPreferredSize(new java.awt.Dimension(30, 23));
        jButtonOutput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOutputActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        jPanel1.add(jButtonOutput, gridBagConstraints);

        buttonGroup1.add(jRadioButtonPages);
        jRadioButtonPages.setSelected(true);
        jRadioButtonPages.setText(bundle.getString("jRadioButtonPages.Text")); // NOI18N
        jRadioButtonPages.setToolTipText(bundle.getString("jRadioButtonPages.ToolTipText")); // NOI18N
        jRadioButtonPages.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonPagesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jPanel1.add(jRadioButtonPages, gridBagConstraints);

        jLabelFrom.setLabelFor(jTextFieldFrom);
        jLabelFrom.setText(bundle.getString("jLabelFrom.Text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(10, 50, 0, 0);
        jPanel1.add(jLabelFrom, gridBagConstraints);

        jTextFieldFrom.setColumns(3);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 8, 0, 18);
        jPanel1.add(jTextFieldFrom, gridBagConstraints);

        jLabelTo.setLabelFor(jTextFieldTo);
        jLabelTo.setText(bundle.getString("jLabelTo.Text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 8);
        jPanel1.add(jLabelTo, gridBagConstraints);

        jTextFieldTo.setColumns(3);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 10);
        jPanel1.add(jTextFieldTo, gridBagConstraints);

        buttonGroup1.add(jRadioButtonFiles);
        jRadioButtonFiles.setText(bundle.getString("jRadioButtonFiles.Text")); // NOI18N
        jRadioButtonFiles.setToolTipText(bundle.getString("jRadioButtonFiles.ToolTipText")); // NOI18N
        jRadioButtonFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonFilesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(jRadioButtonFiles, gridBagConstraints);

        jLabelNumPages.setLabelFor(jTextFieldNumOfPages);
        jLabelNumPages.setText(bundle.getString("jLabelNumPages.Text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 8);
        jPanel1.add(jLabelNumPages, gridBagConstraints);

        jTextFieldNumOfPages.setColumns(3);
        jTextFieldNumOfPages.setText("50");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        jPanel1.add(jTextFieldNumOfPages, gridBagConstraints);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 16, 24));
        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        jButtonSplit.setText(bundle.getString("jButtonSplit.Text")); // NOI18N
        jButtonSplit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSplitActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonSplit);

        jButtonCancel.setText(bundle.getString("jButtonCancel.Text")); // NOI18N
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonCancel);

        getContentPane().add(jPanel2, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonInputActionPerformed
        JFileChooser filechooser = new JFileChooser();
        filechooser.setDialogTitle(bundle.getString("Open"));
        FileFilter pdfFilter = new SimpleFilter("pdf", "PDF");
        filechooser.addChoosableFileFilter(pdfFilter);
        filechooser.setAcceptAllFileFilterUsed(false);
        if (filechooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            this.jTextFieldInputFile.setText(filechooser.getSelectedFile().getPath());
        }
    }//GEN-LAST:event_jButtonInputActionPerformed

    private void jButtonOutputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOutputActionPerformed
        JFileChooser filechooser = new JFileChooser();
        filechooser.setDialogTitle(bundle.getString("Save"));
        FileFilter pdfFilter = new SimpleFilter("pdf", "PDF");
        filechooser.addChoosableFileFilter(pdfFilter);
        filechooser.setAcceptAllFileFilterUsed(false);
        if (filechooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            this.jTextFieldOutputFile.setText(filechooser.getSelectedFile().getPath());

            if (!this.jTextFieldOutputFile.getText().endsWith(".pdf")) {
                this.jTextFieldOutputFile.setText(this.jTextFieldOutputFile.getText() + ".pdf");
            }
        }
    }//GEN-LAST:event_jButtonOutputActionPerformed

    private void jButtonSplitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSplitActionPerformed
        SplitPdfArgs arguments = new SplitPdfArgs();
        arguments.setInputFilename(this.jTextFieldInputFile.getText());
        arguments.setOutputFilename(this.jTextFieldOutputFile.getText());
        arguments.setFromPage(this.jTextFieldFrom.getText());
        arguments.setToPage(this.jTextFieldTo.getText());
        arguments.setNumOfPages(this.jTextFieldNumOfPages.getText());
        arguments.setPages(this.jRadioButtonPages.isSelected());

        if (!new File(arguments.getInputFilename()).exists()) {
            JOptionPane.showMessageDialog(this, bundle.getString("File_not_exist"), bundle.getString("Error"), JOptionPane.ERROR_MESSAGE);
        } else if (arguments.getInputFilename().length() > 0 && arguments.getOutputFilename().length() > 0 &&
                ((this.jRadioButtonPages.isSelected() && arguments.getFromPage().length() > 0) ||
                (this.jRadioButtonFiles.isSelected() && arguments.getNumOfPages().length() > 0))) {

            Pattern regexNums = Pattern.compile("^\\d+$");

            if ((this.jRadioButtonPages.isSelected() && regexNums.matcher(arguments.getFromPage()).matches() && (arguments.getToPage().length() > 0 ? regexNums.matcher(arguments.getToPage()).matches() : true)) || (this.jRadioButtonFiles.isSelected() && regexNums.matcher(arguments.getNumOfPages()).matches())) {
                this.args = arguments;
                actionSelected = JOptionPane.OK_OPTION;
                this.setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, bundle.getString("Input_invalid"), bundle.getString("Error"), JOptionPane.ERROR_MESSAGE);
                actionSelected = JOptionPane.DEFAULT_OPTION;
            }
        } else {
            JOptionPane.showMessageDialog(this, bundle.getString("Input_incomplete"), bundle.getString("Error"), JOptionPane.ERROR_MESSAGE);
            actionSelected = JOptionPane.DEFAULT_OPTION;
        }
    }//GEN-LAST:event_jButtonSplitActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        actionSelected = JOptionPane.CANCEL_OPTION;
        setVisible(false);
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jRadioButtonPagesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonPagesActionPerformed
        disableBoxes(false);
    }//GEN-LAST:event_jRadioButtonPagesActionPerformed

    private void jRadioButtonFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonFilesActionPerformed
        disableBoxes(true);
    }//GEN-LAST:event_jRadioButtonFilesActionPerformed
    private void disableBoxes(boolean enabled) {
        this.jTextFieldNumOfPages.setEnabled(enabled);
        this.jTextFieldFrom.setEnabled(!enabled);
        this.jTextFieldTo.setEnabled(!enabled);
    }

    /**
     * Displays dialog.
     * @return 
     */
    public int showDialog() {
        setVisible(true);
        return actionSelected;
    }

    void changeUILanguage(final Locale locale) {
        Locale.setDefault(locale);
        bundle = ResourceBundle.getBundle("net/sourceforge/vietocr/SplitPdfDialog");

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                FormLocalizer localizer = new FormLocalizer(SplitPdfDialog.this, SplitPdfDialog.class);
                localizer.ApplyCulture(bundle);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonInput;
    private javax.swing.JButton jButtonOutput;
    private javax.swing.JButton jButtonSplit;
    private javax.swing.JLabel jLabelFrom;
    private javax.swing.JLabel jLabelInput;
    private javax.swing.JLabel jLabelNumPages;
    private javax.swing.JLabel jLabelOutput;
    private javax.swing.JLabel jLabelTo;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton jRadioButtonFiles;
    private javax.swing.JRadioButton jRadioButtonPages;
    private javax.swing.JTextField jTextFieldFrom;
    private javax.swing.JTextField jTextFieldInputFile;
    private javax.swing.JTextField jTextFieldNumOfPages;
    private javax.swing.JTextField jTextFieldOutputFile;
    private javax.swing.JTextField jTextFieldTo;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the args
     */
    public SplitPdfArgs getArgs() {
        return args;
    }

    class NumericTextField extends JTextField {

        @Override
        protected Document createDefaultModel() {
            return new NumericDocument();
        }

        private class NumericDocument extends PlainDocument {
            // The regular expression to match input against (zero or more digits)

            private final Pattern DIGITS = Pattern.compile("\\d*");

            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                // Only insert the text if it matches the regular expression
                if (str != null && DIGITS.matcher(str).matches()) {
                    super.insertString(offs, str, a);
                }
            }
        }
    }
}
