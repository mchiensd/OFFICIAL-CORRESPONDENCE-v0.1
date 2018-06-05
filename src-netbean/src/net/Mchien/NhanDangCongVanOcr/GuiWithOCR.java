/**
 * Copyright @ 2008 Quan Nguyen
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

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOImage;
import javax.swing.*;

import net.Mchien.NhanDangCongVan.components.JImageLabel;

public class GuiWithOCR extends  GuiWithScan {

    private OcrWorker ocrWorker;
    protected String selectedPSM = "3"; // 3 - Fully automatic page segmentation, but no OSD (default)
    protected String selectedOEM = "3"; // Default, based on what is available
    protected boolean tessLibEnabled;
    
    private final static Logger logger = Logger.getLogger(GuiWithOCR.class.getName());

    @Override
    void jMenuItemOCRActionPerformed(java.awt.event.ActionEvent evt) {
        if (jImageLabel.getIcon() == null) {
            JOptionPane.showMessageDialog(this, bundle.getString("Please_load_an_image."), APP_NAME, JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Rectangle rect = ((JImageLabel) jImageLabel).getRect();

        if (rect != null) {
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

               
                performOCR(iioImageList, inputfilename, imageIndex, rect);
            } catch (RasterFormatException rfe) {
                logger.log(Level.SEVERE, rfe.getMessage(), rfe);
                JOptionPane.showMessageDialog(this, rfe.getMessage(), APP_NAME, JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        } else {
            performOCR(iioImageList, inputfilename, imageIndex, null);
        }
    }

    
    void jMenuItemOCRAllActionPerformed(java.awt.event.ActionEvent evt) {
        if (this.jImageLabel.getIcon() == null) {
            JOptionPane.showMessageDialog(this, bundle.getString("Please_load_an_image."), APP_NAME, JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        this.jButtonOCR.setVisible(false);
      
        performOCR(iioImageList, inputfilename, -1, null);
    }

    /**
     * Perform OCR on images represented by IIOImage.
     *
     * @param iioImageList list of IIOImage
     * @param inputfilename input filename
     * @param index Index of page to be OCRed: -1 for all pages
     * @param rect region of interest
     */
    void performOCR(final List<IIOImage> iioImageList, String inputfilename, final int index, Rectangle rect) {
        if (curLangCode.trim().length() == 0) {
            JOptionPane.showMessageDialog(this, bundle.getString("Please_select_a_language."), APP_NAME, JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        jLabelStatus.setText(bundle.getString("OCR_running..."));
        jProgressBar1.setIndeterminate(true);
        jProgressBar1.setString(bundle.getString("OCR_running..."));
        jProgressBar1.setVisible(true);
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        getGlassPane().setVisible(true);
        this.jButtonOCR.setEnabled(false);
        this.jMenuItemOCR.setEnabled(false);
     

        OCRImageEntity entity = new OCRImageEntity(iioImageList, inputfilename, index, rect, curLangCode);
       

        // instantiate SwingWorker for OCR
        ocrWorker = new OcrWorker(entity);
        ocrWorker.execute();
    }
    OcrWorker1 ocrWorker1;
    @Override
    void performOCR1(final List<IIOImage> iioImageList, String inputfilename, final int index, Rectangle rect) {
        if (curLangCode.trim().length() == 0) {
            JOptionPane.showMessageDialog(this, bundle.getString("Please_select_a_language."), APP_NAME, JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        jLabelStatus.setText(bundle.getString("OCR_running..."));
        jProgressBar1.setIndeterminate(true);
        jProgressBar1.setString(bundle.getString("OCR_running..."));
        jProgressBar1.setVisible(true);
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        getGlassPane().setVisible(true);
        this.jButtonOCR.setEnabled(false);
        this.jMenuItemOCR.setEnabled(false);
     

        OCRImageEntity entity = new OCRImageEntity(iioImageList, inputfilename, index, rect, curLangCode);
       

        // instantiate SwingWorker for OCR
        ocrWorker1 = new OcrWorker1(entity);
        ocrWorker1.execute();
    }

    OcrWorker2 ocrWorker2;
     @Override
    void performOCR2(final List<IIOImage> iioImageList, String inputfilename, final int index, Rectangle rect) {
        if (curLangCode.trim().length() == 0) {
            JOptionPane.showMessageDialog(this, bundle.getString("Please_select_a_language."), APP_NAME, JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        jLabelStatus.setText(bundle.getString("OCR_running..."));
        jProgressBar1.setIndeterminate(true);
        jProgressBar1.setString(bundle.getString("OCR_running..."));
        jProgressBar1.setVisible(true);
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        getGlassPane().setVisible(true);
        this.jButtonOCR.setEnabled(false);
        this.jMenuItemOCR.setEnabled(false);
     

        OCRImageEntity entity = new OCRImageEntity(iioImageList, inputfilename, index, rect, curLangCode);
       

        // instantiate SwingWorker for OCR
        ocrWorker2 = new OcrWorker2(entity);
        ocrWorker2.execute();
    }
  

    /**
     * A worker class for managing OCR process.
     */
    class OcrWorker extends SwingWorker<Void, String> {

        OCRImageEntity entity;
        List<File> workingFiles;
        List<IIOImage> imageList; // Option for Tess4J

        OcrWorker(OCRImageEntity entity) {
            this.entity = entity;
        }

        @Override
        protected Void doInBackground() throws Exception {
            String lang = entity.getLanguage();

            if (!tessLibEnabled) {
                OCR<File> ocrEngine = new OCRFiles(tessPath);
                ocrEngine.setDatapath(datapath);
                ocrEngine.setPageSegMode(selectedPSM);
                ocrEngine.setLanguage(lang);
                workingFiles = entity.getClonedImageFiles();

                for (int i = 0; i < workingFiles.size(); i++) {
                    if (!isCancelled()) {
                        String result = ocrEngine.recognizeText(workingFiles.subList(i, i + 1), entity.getInputfilename());
                        publish(result); // interim result
                        
                    }
                }
            } else {
                OCR<IIOImage> ocrEngine = new OCRImages(tessPath); // for Tess4J
                ocrEngine.setDatapath(datapath);
                ocrEngine.setPageSegMode(selectedPSM);
                ocrEngine.setLanguage(lang);
                imageList = entity.getSelectedOimages();

                for (int i = 0; i < imageList.size(); i++) {
                    if (!isCancelled()) {
                        String result = ocrEngine.recognizeText(imageList.subList(i, i + 1), entity.getInputfilename(), entity.getRect());
                        publish(result); // interim result
                    }
                }
            }

            return null;
        }

        @Override
        protected void process(List<String> results) {
            for (String str : results) {
                jTextArea1.append(str);
                jTextArea1.setCaretPosition(jTextArea1.getDocument().getLength());
            }
        }

        @Override
        protected void done() {
            jProgressBar1.setIndeterminate(false);

            try {
                get(); // dummy method
                jLabelStatus.setText(bundle.getString("OCR_completed."));
                jProgressBar1.setString(bundle.getString("OCR_completed."));
            } catch (InterruptedException ignore) {
                logger.log(Level.WARNING, ignore.getMessage(), ignore);
            } catch (java.util.concurrent.ExecutionException e) {
                String why;
                Throwable cause = e.getCause();
                if (cause != null) {
                    if (cause instanceof IOException) {
                        why = bundle.getString("Cannot_find_Tesseract._Please_set_its_path.");
                    } else if (cause instanceof FileNotFoundException) {
                        why = bundle.getString("An_exception_occurred_in_Tesseract_engine_while_recognizing_this_image.");
                    } else if (cause instanceof OutOfMemoryError) {
                        why = cause.getMessage();
                    } else if (cause instanceof ClassCastException) {
                        why = cause.getMessage();
                        why += "\nConsider converting the image to binary or grayscale before OCR again.";
                    } else {
                        why = cause.getMessage();
                    }
                } else {
                    why = e.getMessage();
                }

                logger.log(Level.SEVERE, why, e);
                jLabelStatus.setText(null);
                jProgressBar1.setString(null);
                JOptionPane.showMessageDialog(null, why, "OCR Operation", JOptionPane.ERROR_MESSAGE);
            } catch (java.util.concurrent.CancellationException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
                jLabelStatus.setText("OCR " + bundle.getString("canceled"));
                jProgressBar1.setString("OCR " + bundle.getString("canceled"));
            } finally {
                getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                getGlassPane().setVisible(false);
                jButtonOCR.setVisible(true);
                jButtonOCR.setEnabled(true);
                jMenuItemOCR.setEnabled(true);
               
            

                // clean up temporary image files
                if (workingFiles != null) {
                    for (File tempImageFile : workingFiles) {
                        tempImageFile.delete();
                    }
                }
            }
        }
    }
    
        class OcrWorker1 extends SwingWorker<Void, String> {

        OCRImageEntity entity;
        List<File> workingFiles;
        List<IIOImage> imageList; // Option for Tess4J

        OcrWorker1(OCRImageEntity entity) {
            this.entity = entity;
        }

        @Override
        protected Void doInBackground() throws Exception {
            String lang = entity.getLanguage();

            if (!tessLibEnabled) {
                OCR<File> ocrEngine = new OCRFiles(tessPath);
                ocrEngine.setDatapath(datapath);
                ocrEngine.setPageSegMode(selectedPSM);
                ocrEngine.setLanguage(lang);
                workingFiles = entity.getClonedImageFiles();

                for (int i = 0; i < workingFiles.size(); i++) {
                    if (!isCancelled()) {
                        String result = ocrEngine.recognizeText(workingFiles.subList(i, i + 1), entity.getInputfilename());
                        publish(result); // interim result
                    }
                }
            } else {
                OCR<IIOImage> ocrEngine = new OCRImages(tessPath); // for Tess4J
                ocrEngine.setDatapath(datapath);
                ocrEngine.setPageSegMode(selectedPSM);
                ocrEngine.setLanguage(lang);
                imageList = entity.getSelectedOimages();

                for (int i = 0; i < imageList.size(); i++) {
                    if (!isCancelled()) {
                        String result = ocrEngine.recognizeText(imageList.subList(i, i + 1), entity.getInputfilename(), entity.getRect());
                        publish(result); // interim result
                    }
                }
            }

            return null;
        }

        @Override
        protected void process(List<String> results) {
            jTextArea2.setText("");
            for (String str : results) {
                
                    jTextArea2.append(" "+ str.replace("\n", "-").replace("CONG", "").replace("CỌNG", "").replace("CỘNG", "").replace("CO", "").replace("CỌ", " ").replace("CỘ", " ").replace("  ", "-").replace("-", "").replace('', ' '));
              jTextArea2.append(" - ");
            }
           
        }

        @Override
        protected void done() {
            jProgressBar1.setIndeterminate(false);

            try {
                get(); // dummy method
                jLabelStatus.setText(bundle.getString("OCR_completed."));
                jProgressBar1.setString(bundle.getString("OCR_completed."));
            } catch (InterruptedException ignore) {
                logger.log(Level.WARNING, ignore.getMessage(), ignore);
            } catch (java.util.concurrent.ExecutionException e) {
                String why;
                Throwable cause = e.getCause();
                if (cause != null) {
                    if (cause instanceof IOException) {
                        why = bundle.getString("Cannot_find_Tesseract._Please_set_its_path.");
                    } else if (cause instanceof FileNotFoundException) {
                        why = bundle.getString("An_exception_occurred_in_Tesseract_engine_while_recognizing_this_image.");
                    } else if (cause instanceof OutOfMemoryError) {
                        why = cause.getMessage();
                    } else if (cause instanceof ClassCastException) {
                        why = cause.getMessage();
                        why += "\nConsider converting the image to binary or grayscale before OCR again.";
                    } else {
                        why = cause.getMessage();
                    }
                } else {
                    why = e.getMessage();
                }

                logger.log(Level.SEVERE, why, e);
                jLabelStatus.setText(null);
                jProgressBar1.setString(null);
                JOptionPane.showMessageDialog(null, why, "OCR Operation", JOptionPane.ERROR_MESSAGE);
            } catch (java.util.concurrent.CancellationException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
                jLabelStatus.setText("OCR " + bundle.getString("canceled"));
                jProgressBar1.setString("OCR " + bundle.getString("canceled"));
            } finally {
                getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                getGlassPane().setVisible(false);
                jButtonOCR.setVisible(true);
                jButtonOCR.setEnabled(true);
                jMenuItemOCR.setEnabled(true);
               
            

                // clean up temporary image files
                if (workingFiles != null) {
                    for (File tempImageFile : workingFiles) {
                        tempImageFile.delete();
                    }
                }
            }
        }
    }
        
        
class OcrWorker2 extends SwingWorker<Void, String> {

        OCRImageEntity entity;
        List<File> workingFiles;
        List<IIOImage> imageList; // Option for Tess4J

        OcrWorker2(OCRImageEntity entity) {
            this.entity = entity;
        }

        @Override
        protected Void doInBackground() throws Exception {
            String lang = entity.getLanguage();

            if (!tessLibEnabled) {
                OCR<File> ocrEngine = new OCRFiles(tessPath);
                ocrEngine.setDatapath(datapath);
                ocrEngine.setPageSegMode(selectedPSM);
                ocrEngine.setLanguage(lang);
                workingFiles = entity.getClonedImageFiles();

                for (int i = 0; i < workingFiles.size(); i++) {
                    if (!isCancelled()) {
                        String result = ocrEngine.recognizeText(workingFiles.subList(i, i + 1), entity.getInputfilename());
                       
                            publish(result); // interim result
                       
                       
                    }
                }
            } else {
                OCR<IIOImage> ocrEngine = new OCRImages(tessPath); // for Tess4J
                ocrEngine.setDatapath(datapath);
                ocrEngine.setPageSegMode(selectedPSM);
                ocrEngine.setLanguage(lang);
                imageList = entity.getSelectedOimages();

                for (int i = 0; i < imageList.size(); i++) {
                    if (!isCancelled()) {
                        String result = ocrEngine.recognizeText(imageList.subList(i, i + 1), entity.getInputfilename(), entity.getRect());
                        publish(result); // interim result
                    }
                }
            }

            return null;
        }

        @Override
        protected void process(List<String> results) {
           // jTextArea3.setText("");
            for (String str : results) {
                if(str.contains("Số")){
                    jTextArea3.setText("");
                    jTextArea3.append(str.split("\n")[0]);break;
                }else{
                   // jTextArea3.append(str.replace("\n","aaaaaaa "));
                }
            
            }
           
        }

        @Override
        protected void done() {
            jProgressBar1.setIndeterminate(false);

            try {
                get(); // dummy method
                jLabelStatus.setText(bundle.getString("OCR_completed."));
                jProgressBar1.setString(bundle.getString("OCR_completed."));
            } catch (InterruptedException ignore) {
                logger.log(Level.WARNING, ignore.getMessage(), ignore);
            } catch (java.util.concurrent.ExecutionException e) {
                String why;
                Throwable cause = e.getCause();
                if (cause != null) {
                    if (cause instanceof IOException) {
                        why = bundle.getString("Cannot_find_Tesseract._Please_set_its_path.");
                    } else if (cause instanceof FileNotFoundException) {
                        why = bundle.getString("An_exception_occurred_in_Tesseract_engine_while_recognizing_this_image.");
                    } else if (cause instanceof OutOfMemoryError) {
                        why = cause.getMessage();
                    } else if (cause instanceof ClassCastException) {
                        why = cause.getMessage();
                        why += "\nConsider converting the image to binary or grayscale before OCR again.";
                    } else {
                        why = cause.getMessage();
                    }
                } else {
                    why = e.getMessage();
                }

                logger.log(Level.SEVERE, why, e);
                jLabelStatus.setText(null);
                jProgressBar1.setString(null);
                JOptionPane.showMessageDialog(null, why, "OCR Operation", JOptionPane.ERROR_MESSAGE);
            } catch (java.util.concurrent.CancellationException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
                jLabelStatus.setText("OCR " + bundle.getString("canceled"));
                jProgressBar1.setString("OCR " + bundle.getString("canceled"));
            } finally {
                getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                getGlassPane().setVisible(false);
                jButtonOCR.setVisible(true);
                jButtonOCR.setEnabled(true);
                jMenuItemOCR.setEnabled(true);
               
            

                // clean up temporary image files
                if (workingFiles != null) {
                    for (File tempImageFile : workingFiles) {
                        tempImageFile.delete();
                    }
                }
            }
        }
    }
OcrWorker3 ocrWorker3;
    @Override
 void performOCR3(final List<IIOImage> iioImageList, String inputfilename, final int index, Rectangle rect) {
        if (curLangCode.trim().length() == 0) {
            JOptionPane.showMessageDialog(this, bundle.getString("Please_select_a_language."), APP_NAME, JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        jLabelStatus.setText(bundle.getString("OCR_running..."));
        jProgressBar1.setIndeterminate(true);
        jProgressBar1.setString(bundle.getString("OCR_running..."));
        jProgressBar1.setVisible(true);
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        getGlassPane().setVisible(true);
        this.jButtonOCR.setEnabled(false);
        this.jMenuItemOCR.setEnabled(false);
     

        OCRImageEntity entity = new OCRImageEntity(iioImageList, inputfilename, index, rect, curLangCode);
       

        // instantiate SwingWorker for OCR
        ocrWorker3 = new OcrWorker3(entity);
        ocrWorker3.execute();
    }
  

    /**
     * A worker class for managing OCR process.
     */
  
        
        
class OcrWorker3 extends SwingWorker<Void, String> {

        OCRImageEntity entity;
        List<File> workingFiles;
        List<IIOImage> imageList; // Option for Tess4J

        OcrWorker3(OCRImageEntity entity) {
            this.entity = entity;
        }

        @Override
        protected Void doInBackground() throws Exception {
            String lang = entity.getLanguage();

            if (!tessLibEnabled) {
                OCR<File> ocrEngine = new OCRFiles(tessPath);
                ocrEngine.setDatapath(datapath);
                ocrEngine.setPageSegMode(selectedPSM);
                ocrEngine.setLanguage(lang);
                workingFiles = entity.getClonedImageFiles();

                for (int i = 0; i < workingFiles.size(); i++) {
                    if (!isCancelled()) {
                        String result = ocrEngine.recognizeText(workingFiles.subList(i, i + 1), entity.getInputfilename());
                       
                            publish(result); // interim result
                       
                       
                    }
                }
            } else {
                OCR<IIOImage> ocrEngine = new OCRImages(tessPath); // for Tess4J
                ocrEngine.setDatapath(datapath);
                ocrEngine.setPageSegMode(selectedPSM);
                ocrEngine.setLanguage(lang);
                imageList = entity.getSelectedOimages();

                for (int i = 0; i < imageList.size(); i++) {
                    if (!isCancelled()) {
                        String result = ocrEngine.recognizeText(imageList.subList(i, i + 1), entity.getInputfilename(), entity.getRect());
                        publish(result); // interim result
                    }
                }
            }

            return null;
        }

        @Override
        protected void process(List<String> results) {
           // jTextArea3.setText("");
            for (String str : results) {
                if(str.contains("ngày")){
                    jTextArea4.setText("");
                    jTextArea4.append(str.split("\n")[0]);break;
                }else{
                   // jTextArea4.setText("");
                   // jTextArea4.append("Không nhận dạng được");
                }
            
            }
           
        }

        @Override
        protected void done() {
            jProgressBar1.setIndeterminate(false);

            try {
                get(); // dummy method
                jLabelStatus.setText(bundle.getString("OCR_completed."));
                jProgressBar1.setString(bundle.getString("OCR_completed."));
            } catch (InterruptedException ignore) {
                logger.log(Level.WARNING, ignore.getMessage(), ignore);
            } catch (java.util.concurrent.ExecutionException e) {
                String why;
                Throwable cause = e.getCause();
                if (cause != null) {
                    if (cause instanceof IOException) {
                        why = bundle.getString("Cannot_find_Tesseract._Please_set_its_path.");
                    } else if (cause instanceof FileNotFoundException) {
                        why = bundle.getString("An_exception_occurred_in_Tesseract_engine_while_recognizing_this_image.");
                    } else if (cause instanceof OutOfMemoryError) {
                        why = cause.getMessage();
                    } else if (cause instanceof ClassCastException) {
                        why = cause.getMessage();
                        why += "\nConsider converting the image to binary or grayscale before OCR again.";
                    } else {
                        why = cause.getMessage();
                    }
                } else {
                    why = e.getMessage();
                }

                logger.log(Level.SEVERE, why, e);
                jLabelStatus.setText(null);
                jProgressBar1.setString(null);
                JOptionPane.showMessageDialog(null, why, "OCR Operation", JOptionPane.ERROR_MESSAGE);
            } catch (java.util.concurrent.CancellationException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
                jLabelStatus.setText("OCR " + bundle.getString("canceled"));
                jProgressBar1.setString("OCR " + bundle.getString("canceled"));
            } finally {
                getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                getGlassPane().setVisible(false);
                jButtonOCR.setVisible(true);
                jButtonOCR.setEnabled(true);
                jMenuItemOCR.setEnabled(true);
               
            

                // clean up temporary image files
                if (workingFiles != null) {
                    for (File tempImageFile : workingFiles) {
                        tempImageFile.delete();
                    }
                }
            }
        }
    }


OcrWorker4 ocrWorker4;
    @Override
 void performOCR4(final List<IIOImage> iioImageList, String inputfilename, final int index, Rectangle rect) {
        if (curLangCode.trim().length() == 0) {
            JOptionPane.showMessageDialog(this, bundle.getString("Please_select_a_language."), APP_NAME, JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        jLabelStatus.setText(bundle.getString("OCR_running..."));
        jProgressBar1.setIndeterminate(true);
        jProgressBar1.setString(bundle.getString("OCR_running..."));
        jProgressBar1.setVisible(true);
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        getGlassPane().setVisible(true);
        this.jButtonOCR.setEnabled(false);
        this.jMenuItemOCR.setEnabled(false);
     

        OCRImageEntity entity = new OCRImageEntity(iioImageList, inputfilename, index, rect, curLangCode);
       

        // instantiate SwingWorker for OCR
        ocrWorker4 = new OcrWorker4(entity);
        ocrWorker4.execute();
    }
  

    /**
     * A worker class for managing OCR process.
     */
  
        
        
class OcrWorker4 extends SwingWorker<Void, String> {

        OCRImageEntity entity;
        List<File> workingFiles;
        List<IIOImage> imageList; // Option for Tess4J

        OcrWorker4(OCRImageEntity entity) {
            this.entity = entity;
        }

        @Override
        protected Void doInBackground() throws Exception {
            String lang = entity.getLanguage();

            if (!tessLibEnabled) {
                OCR<File> ocrEngine = new OCRFiles(tessPath);
                ocrEngine.setDatapath(datapath);
                ocrEngine.setPageSegMode(selectedPSM);
                ocrEngine.setLanguage(lang);
                workingFiles = entity.getClonedImageFiles();

                for (int i = 0; i < workingFiles.size(); i++) {
                    if (!isCancelled()) {
                        String result = ocrEngine.recognizeText(workingFiles.subList(i, i + 1), entity.getInputfilename());
                       
                            publish(result); // interim result
                       
                       
                    }
                }
            } else {
                OCR<IIOImage> ocrEngine = new OCRImages(tessPath); // for Tess4J
                ocrEngine.setDatapath(datapath);
                ocrEngine.setPageSegMode(selectedPSM);
                ocrEngine.setLanguage(lang);
                imageList = entity.getSelectedOimages();

                for (int i = 0; i < imageList.size(); i++) {
                    if (!isCancelled()) {
                        String result = ocrEngine.recognizeText(imageList.subList(i, i + 1), entity.getInputfilename(), entity.getRect());
                        publish(result); // interim result
                    }
                }
            }

            return null;
        }

        @Override
        protected void process(List<String> results) {
           // jTextArea3.setText("");
            for (String str : results) {
                if(str.contains("V/")){
                    jTextArea5.setText("");
                    jTextArea5.append(str.replace("\n", " "));
                }else{
                   // jTextArea4.setText("");
                   // jTextArea4.append("Không nhận dạng được");
                }
            
            }
           
        }

        @Override
        protected void done() {
            jProgressBar1.setIndeterminate(false);

            try {
                get(); // dummy method
                jLabelStatus.setText(bundle.getString("OCR_completed."));
                jProgressBar1.setString(bundle.getString("OCR_completed."));
            } catch (InterruptedException ignore) {
                logger.log(Level.WARNING, ignore.getMessage(), ignore);
            } catch (java.util.concurrent.ExecutionException e) {
                String why;
                Throwable cause = e.getCause();
                if (cause != null) {
                    if (cause instanceof IOException) {
                        why = bundle.getString("Cannot_find_Tesseract._Please_set_its_path.");
                    } else if (cause instanceof FileNotFoundException) {
                        why = bundle.getString("An_exception_occurred_in_Tesseract_engine_while_recognizing_this_image.");
                    } else if (cause instanceof OutOfMemoryError) {
                        why = cause.getMessage();
                    } else if (cause instanceof ClassCastException) {
                        why = cause.getMessage();
                        why += "\nConsider converting the image to binary or grayscale before OCR again.";
                    } else {
                        why = cause.getMessage();
                    }
                } else {
                    why = e.getMessage();
                }

                logger.log(Level.SEVERE, why, e);
                jLabelStatus.setText(null);
                jProgressBar1.setString(null);
                JOptionPane.showMessageDialog(null, why, "OCR Operation", JOptionPane.ERROR_MESSAGE);
            } catch (java.util.concurrent.CancellationException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
                jLabelStatus.setText("OCR " + bundle.getString("canceled"));
                jProgressBar1.setString("OCR " + bundle.getString("canceled"));
            } finally {
                getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                getGlassPane().setVisible(false);
                jButtonOCR.setVisible(true);
                jButtonOCR.setEnabled(true);
                jMenuItemOCR.setEnabled(true);
               
            

                // clean up temporary image files
                if (workingFiles != null) {
                    for (File tempImageFile : workingFiles) {
                        tempImageFile.delete();
                    }
                }
            }
        }
    }


OcrWorker5 ocrWorker5;
    @Override
 void performOCR5(final List<IIOImage> iioImageList, String inputfilename, final int index, Rectangle rect) {
        if (curLangCode.trim().length() == 0) {
            JOptionPane.showMessageDialog(this, bundle.getString("Please_select_a_language."), APP_NAME, JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        jLabelStatus.setText(bundle.getString("OCR_running..."));
        jProgressBar1.setIndeterminate(true);
        jProgressBar1.setString(bundle.getString("OCR_running..."));
        jProgressBar1.setVisible(true);
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        getGlassPane().setVisible(true);
        this.jButtonOCR.setEnabled(false);
        this.jMenuItemOCR.setEnabled(false);
     

        OCRImageEntity entity = new OCRImageEntity(iioImageList, inputfilename, index, rect, curLangCode);
       

        // instantiate SwingWorker for OCR
        ocrWorker5 = new OcrWorker5(entity);
        ocrWorker5.execute();
    }
  

    /**
     * A worker class for managing OCR process.
     */
  
        
        
class OcrWorker5 extends SwingWorker<Void, String> {

        OCRImageEntity entity;
        List<File> workingFiles;
        List<IIOImage> imageList; // Option for Tess4J

        OcrWorker5(OCRImageEntity entity) {
            this.entity = entity;
        }

        @Override
        protected Void doInBackground() throws Exception {
            String lang = entity.getLanguage();

            if (!tessLibEnabled) {
                OCR<File> ocrEngine = new OCRFiles(tessPath);
                ocrEngine.setDatapath(datapath);
                ocrEngine.setPageSegMode(selectedPSM);
                ocrEngine.setLanguage(lang);
                workingFiles = entity.getClonedImageFiles();

                for (int i = 0; i < workingFiles.size(); i++) {
                    if (!isCancelled()) {
                        String result = ocrEngine.recognizeText(workingFiles.subList(i, i + 1), entity.getInputfilename());
                       
                            publish(result); // interim result
                       
                       
                    }
                }
            } else {
                OCR<IIOImage> ocrEngine = new OCRImages(tessPath); // for Tess4J
                ocrEngine.setDatapath(datapath);
                ocrEngine.setPageSegMode(selectedPSM);
                ocrEngine.setLanguage(lang);
                imageList = entity.getSelectedOimages();

                for (int i = 0; i < imageList.size(); i++) {
                    if (!isCancelled()) {
                        String result = ocrEngine.recognizeText(imageList.subList(i, i + 1), entity.getInputfilename(), entity.getRect());
                        publish(result); // interim result
                    }
                }
            }

            return null;
        }

        @Override
        protected void process(List<String> results) {
            jTextArea1.setText("");
            for (String str : results) {
                    jTextArea1.append(str);break;   
            }
            int indexKinhGui =0, indexNoiDung =0, indexNguoiNhan=0,indexNguoiNhantemp=0;
            for(int i=0; i< jTextArea1.getText().split("\n").length; i++){
            
                if(jTextArea1.getText().split("\n")[i].contains("Kinh gửi")|| jTextArea1.getText().split("\n")[i].contains("Kính gửi")){
                  indexKinhGui = i;
                }
                
                 if(jTextArea1.getText().split("\n")[i].contains("Nơi nhân")|| jTextArea1.getText().split("\n")[i].contains("Nơi nhận")){
                  indexNguoiNhan = i;
                  indexNguoiNhantemp = i;
                }
                 
                 if(jTextArea1.getText().split("\n")[i].contains("Như trên")){
                  indexNguoiNhan = i-1;
                  indexNguoiNhantemp = i-1;
                }
                 
            }
            
            for(int i=0; i<10; i++){
                 if(jTextArea1.getText().split("\n")[indexKinhGui].contains("-") || jTextArea1.getText().split("\n")[indexKinhGui].contains("~") || jTextArea1.getText().split("\n")[indexKinhGui].length()<2){
                jTextArea6.append(jTextArea1.getText().split("\n")[indexKinhGui].replace("~", "-").replace("Kinh gửi", "").replace("Kính gửi", "").replace(":", "")+"\n");
                indexKinhGui++;
                
            }
                 else{
                      if(!jTextArea1.getText().split("\n")[indexKinhGui].contains("-") || !jTextArea1.getText().split("\n")[indexKinhGui].contains("~")){
                jTextArea6.append(jTextArea1.getText().split("\n")[indexKinhGui].replace("Kinh gửi", "").replace("Kính gửi", "").replace(":", ""));
                break;
            }
                 
                 }
                i++;
                
            }
           
             for(int i=0; i<10; i++){
                 if(jTextArea1.getText().split("\n")[indexKinhGui+1].contains("-")|| jTextArea1.getText().split("\n")[indexKinhGui+1].contains("=") || jTextArea1.getText().split("\n")[indexKinhGui+1].contains("~") || jTextArea1.getText().split("\n")[indexKinhGui+1].length()<1){
                jTextArea7.append(jTextArea1.getText().split("\n")[indexNguoiNhan+1].replace("~", "-").replace("Kinh gửi", "").replace("Kính gửi", "").replace(":", "").replace("=", "-")+"\n");
                indexNguoiNhan++;
                
            }
                 else{
                     
                jTextArea7.append(jTextArea1.getText().split("\n")[indexNguoiNhan].replace("Nơi nhan", "").replace("Nơi nhận", "").replace(":", ""));
                
            
                 
                 }
                i++;
                
            }
             String temp = jTextArea1.getText();
                jTextArea1.setText("");
             for(int i= indexKinhGui+1; i<indexNguoiNhantemp;i++){
              
                 jTextArea1.append(temp.split("\n")[i]+"\n");
             }
             
           
        }

        @Override
        protected void done() {
            jProgressBar1.setIndeterminate(false);

            try {
                get(); // dummy method
                jLabelStatus.setText(bundle.getString("OCR_completed."));
                jProgressBar1.setString(bundle.getString("OCR_completed."));
            } catch (InterruptedException ignore) {
                logger.log(Level.WARNING, ignore.getMessage(), ignore);
            } catch (java.util.concurrent.ExecutionException e) {
                String why;
                Throwable cause = e.getCause();
                if (cause != null) {
                    if (cause instanceof IOException) {
                        why = bundle.getString("Cannot_find_Tesseract._Please_set_its_path.");
                    } else if (cause instanceof FileNotFoundException) {
                        why = bundle.getString("An_exception_occurred_in_Tesseract_engine_while_recognizing_this_image.");
                    } else if (cause instanceof OutOfMemoryError) {
                        why = cause.getMessage();
                    } else if (cause instanceof ClassCastException) {
                        why = cause.getMessage();
                        why += "\nConsider converting the image to binary or grayscale before OCR again.";
                    } else {
                        why = cause.getMessage();
                    }
                } else {
                    why = e.getMessage();
                }

                logger.log(Level.SEVERE, why, e);
                jLabelStatus.setText(null);
                jProgressBar1.setString(null);
                JOptionPane.showMessageDialog(null, why, "OCR Operation", JOptionPane.ERROR_MESSAGE);
            } catch (java.util.concurrent.CancellationException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
                jLabelStatus.setText("OCR " + bundle.getString("canceled"));
                jProgressBar1.setString("OCR " + bundle.getString("canceled"));
            } finally {
                getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                getGlassPane().setVisible(false);
                jButtonOCR.setVisible(true);
                jButtonOCR.setEnabled(true);
                jMenuItemOCR.setEnabled(true);
               
            

                // clean up temporary image files
                if (workingFiles != null) {
                    for (File tempImageFile : workingFiles) {
                        tempImageFile.delete();
                    }
                }
            }
        }
    }


OcrWorker6 ocrWorker6;
    @Override
 void performOCR6(final List<IIOImage> iioImageList, String inputfilename, final int index, Rectangle rect) {
        if (curLangCode.trim().length() == 0) {
            JOptionPane.showMessageDialog(this, bundle.getString("Please_select_a_language."), APP_NAME, JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        jLabelStatus.setText(bundle.getString("OCR_running..."));
        jProgressBar1.setIndeterminate(true);
        jProgressBar1.setString(bundle.getString("OCR_running..."));
        jProgressBar1.setVisible(true);
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        getGlassPane().setVisible(true);
        this.jButtonOCR.setEnabled(false);
        this.jMenuItemOCR.setEnabled(false);
     

        OCRImageEntity entity = new OCRImageEntity(iioImageList, inputfilename, index, rect, curLangCode);
       

        // instantiate SwingWorker for OCR
        ocrWorker6 = new OcrWorker6(entity);
        ocrWorker6.execute();
    }
  

    /**
     * A worker class for managing OCR process.
     */
  
        
        
class OcrWorker6 extends SwingWorker<Void, String> {

        OCRImageEntity entity;
        List<File> workingFiles;
        List<IIOImage> imageList; // Option for Tess4J

        OcrWorker6(OCRImageEntity entity) {
            this.entity = entity;
        }

        @Override
        protected Void doInBackground() throws Exception {
            String lang = entity.getLanguage();

            if (!tessLibEnabled) {
                OCR<File> ocrEngine = new OCRFiles(tessPath);
                ocrEngine.setDatapath(datapath);
                ocrEngine.setPageSegMode(selectedPSM);
                ocrEngine.setLanguage(lang);
                workingFiles = entity.getClonedImageFiles();

                for (int i = 0; i < workingFiles.size(); i++) {
                    if (!isCancelled()) {
                        String result = ocrEngine.recognizeText(workingFiles.subList(i, i + 1), entity.getInputfilename());
                       
                            publish(result); // interim result
                       
                       
                    }
                }
            } else {
                OCR<IIOImage> ocrEngine = new OCRImages(tessPath); // for Tess4J
                ocrEngine.setDatapath(datapath);
                ocrEngine.setPageSegMode(selectedPSM);
                ocrEngine.setLanguage(lang);
                imageList = entity.getSelectedOimages();

                for (int i = 0; i < imageList.size(); i++) {
                    if (!isCancelled()) {
                        String result = ocrEngine.recognizeText(imageList.subList(i, i + 1), entity.getInputfilename(), entity.getRect());
                        publish(result); // interim result
                    }
                }
            }

            return null;
        }

        @Override
        protected void process(List<String> results) {
           // jTextArea3.setText("");
            for (String str : results) {
               
                    jTextArea6.setText("");
                    jTextArea6.append(str.replace("\n", " "));
               
            
            }
           
        }

        @Override
        protected void done() {
            jProgressBar1.setIndeterminate(false);

            try {
                get(); // dummy method
                jLabelStatus.setText(bundle.getString("OCR_completed."));
                jProgressBar1.setString(bundle.getString("OCR_completed."));
            } catch (InterruptedException ignore) {
                logger.log(Level.WARNING, ignore.getMessage(), ignore);
            } catch (java.util.concurrent.ExecutionException e) {
                String why;
                Throwable cause = e.getCause();
                if (cause != null) {
                    if (cause instanceof IOException) {
                        why = bundle.getString("Cannot_find_Tesseract._Please_set_its_path.");
                    } else if (cause instanceof FileNotFoundException) {
                        why = bundle.getString("An_exception_occurred_in_Tesseract_engine_while_recognizing_this_image.");
                    } else if (cause instanceof OutOfMemoryError) {
                        why = cause.getMessage();
                    } else if (cause instanceof ClassCastException) {
                        why = cause.getMessage();
                        why += "\nConsider converting the image to binary or grayscale before OCR again.";
                    } else {
                        why = cause.getMessage();
                    }
                } else {
                    why = e.getMessage();
                }

                logger.log(Level.SEVERE, why, e);
                jLabelStatus.setText(null);
                jProgressBar1.setString(null);
                JOptionPane.showMessageDialog(null, why, "OCR Operation", JOptionPane.ERROR_MESSAGE);
            } catch (java.util.concurrent.CancellationException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
                jLabelStatus.setText("OCR " + bundle.getString("canceled"));
                jProgressBar1.setString("OCR " + bundle.getString("canceled"));
            } finally {
                getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                getGlassPane().setVisible(false);
                jButtonOCR.setVisible(true);
                jButtonOCR.setEnabled(true);
                jMenuItemOCR.setEnabled(true);
               
            

                // clean up temporary image files
                if (workingFiles != null) {
                    for (File tempImageFile : workingFiles) {
                        tempImageFile.delete();
                    }
                }
            }
        }
    }

OcrWorker7 ocrWorker7;
    @Override
 void performOCR7(final List<IIOImage> iioImageList, String inputfilename, final int index, Rectangle rect) {
        if (curLangCode.trim().length() == 0) {
            JOptionPane.showMessageDialog(this, bundle.getString("Please_select_a_language."), APP_NAME, JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        jLabelStatus.setText(bundle.getString("OCR_running..."));
        jProgressBar1.setIndeterminate(true);
        jProgressBar1.setString(bundle.getString("OCR_running..."));
        jProgressBar1.setVisible(true);
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        getGlassPane().setVisible(true);
        this.jButtonOCR.setEnabled(false);
        this.jMenuItemOCR.setEnabled(false);
     

        OCRImageEntity entity = new OCRImageEntity(iioImageList, inputfilename, index, rect, curLangCode);
       

        // instantiate SwingWorker for OCR
        ocrWorker7 = new OcrWorker7(entity);
        ocrWorker7.execute();
    }
  

    /**
     * A worker class for managing OCR process.
     */
  
        
        
class OcrWorker7 extends SwingWorker<Void, String> {

        OCRImageEntity entity;
        List<File> workingFiles;
        List<IIOImage> imageList; // Option for Tess4J

        OcrWorker7(OCRImageEntity entity) {
            this.entity = entity;
        }

        @Override
        protected Void doInBackground() throws Exception {
            String lang = entity.getLanguage();

            if (!tessLibEnabled) {
                OCR<File> ocrEngine = new OCRFiles(tessPath);
                ocrEngine.setDatapath(datapath);
                ocrEngine.setPageSegMode(selectedPSM);
                ocrEngine.setLanguage(lang);
                workingFiles = entity.getClonedImageFiles();

                for (int i = 0; i < workingFiles.size(); i++) {
                    if (!isCancelled()) {
                        String result = ocrEngine.recognizeText(workingFiles.subList(i, i + 1), entity.getInputfilename());
                       
                            publish(result); // interim result
                       
                       
                    }
                }
            } else {
                OCR<IIOImage> ocrEngine = new OCRImages(tessPath); // for Tess4J
                ocrEngine.setDatapath(datapath);
                ocrEngine.setPageSegMode(selectedPSM);
                ocrEngine.setLanguage(lang);
                imageList = entity.getSelectedOimages();

                for (int i = 0; i < imageList.size(); i++) {
                    if (!isCancelled()) {
                        String result = ocrEngine.recognizeText(imageList.subList(i, i + 1), entity.getInputfilename(), entity.getRect());
                        publish(result); // interim result
                    }
                }
            }

            return null;
        }

        @Override
        protected void process(List<String> results) {
           // jTextArea3.setText("");
            for (String str : results) {
               
                    jTextArea1.setText("");
                    jTextArea1.append(str);
               
            
            }
           
        }

        @Override
        protected void done() {
            jProgressBar1.setIndeterminate(false);

            try {
                get(); // dummy method
                jLabelStatus.setText(bundle.getString("OCR_completed."));
                jProgressBar1.setString(bundle.getString("OCR_completed."));
            } catch (InterruptedException ignore) {
                logger.log(Level.WARNING, ignore.getMessage(), ignore);
            } catch (java.util.concurrent.ExecutionException e) {
                String why;
                Throwable cause = e.getCause();
                if (cause != null) {
                    if (cause instanceof IOException) {
                        why = bundle.getString("Cannot_find_Tesseract._Please_set_its_path.");
                    } else if (cause instanceof FileNotFoundException) {
                        why = bundle.getString("An_exception_occurred_in_Tesseract_engine_while_recognizing_this_image.");
                    } else if (cause instanceof OutOfMemoryError) {
                        why = cause.getMessage();
                    } else if (cause instanceof ClassCastException) {
                        why = cause.getMessage();
                        why += "\nConsider converting the image to binary or grayscale before OCR again.";
                    } else {
                        why = cause.getMessage();
                    }
                } else {
                    why = e.getMessage();
                }

                logger.log(Level.SEVERE, why, e);
                jLabelStatus.setText(null);
                jProgressBar1.setString(null);
                JOptionPane.showMessageDialog(null, why, "OCR Operation", JOptionPane.ERROR_MESSAGE);
            } catch (java.util.concurrent.CancellationException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
                jLabelStatus.setText("OCR " + bundle.getString("canceled"));
                jProgressBar1.setString("OCR " + bundle.getString("canceled"));
            } finally {
                getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                getGlassPane().setVisible(false);
                jButtonOCR.setVisible(true);
                jButtonOCR.setEnabled(true);
                jMenuItemOCR.setEnabled(true);
               
            

                // clean up temporary image files
                if (workingFiles != null) {
                    for (File tempImageFile : workingFiles) {
                        tempImageFile.delete();
                    }
                }
            }
        }
    }

OcrWorker8 ocrWorker8;
    @Override
 void performOCR8(final List<IIOImage> iioImageList, String inputfilename, final int index, Rectangle rect) {
        if (curLangCode.trim().length() == 0) {
            JOptionPane.showMessageDialog(this, bundle.getString("Please_select_a_language."), APP_NAME, JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        jLabelStatus.setText(bundle.getString("OCR_running..."));
        jProgressBar1.setIndeterminate(true);
        jProgressBar1.setString(bundle.getString("OCR_running..."));
        jProgressBar1.setVisible(true);
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        getGlassPane().setVisible(true);
        this.jButtonOCR.setEnabled(false);
        this.jMenuItemOCR.setEnabled(false);
     

        OCRImageEntity entity = new OCRImageEntity(iioImageList, inputfilename, index, rect, curLangCode);
       

        // instantiate SwingWorker for OCR
        ocrWorker8 = new OcrWorker8(entity);
        ocrWorker8.execute();
    }
  

    /**
     * A worker class for managing OCR process.
     */
  
        
        
class OcrWorker8 extends SwingWorker<Void, String> {

        OCRImageEntity entity;
        List<File> workingFiles;
        List<IIOImage> imageList; // Option for Tess4J

        OcrWorker8(OCRImageEntity entity) {
            this.entity = entity;
        }

        @Override
        protected Void doInBackground() throws Exception {
            String lang = entity.getLanguage();

            if (!tessLibEnabled) {
                OCR<File> ocrEngine = new OCRFiles(tessPath);
                ocrEngine.setDatapath(datapath);
                ocrEngine.setPageSegMode(selectedPSM);
                ocrEngine.setLanguage(lang);
                workingFiles = entity.getClonedImageFiles();

                for (int i = 0; i < workingFiles.size(); i++) {
                    if (!isCancelled()) {
                        String result = ocrEngine.recognizeText(workingFiles.subList(i, i + 1), entity.getInputfilename());
                       
                            publish(result); // interim result
                       
                       
                    }
                }
            } else {
                OCR<IIOImage> ocrEngine = new OCRImages(tessPath); // for Tess4J
                ocrEngine.setDatapath(datapath);
                ocrEngine.setPageSegMode(selectedPSM);
                ocrEngine.setLanguage(lang);
                imageList = entity.getSelectedOimages();

                for (int i = 0; i < imageList.size(); i++) {
                    if (!isCancelled()) {
                        String result = ocrEngine.recognizeText(imageList.subList(i, i + 1), entity.getInputfilename(), entity.getRect());
                        publish(result); // interim result
                    }
                }
            }

            return null;
        }

        @Override
        protected void process(List<String> results) {
       
            for (String str : results) {
               
                    jTextArea7.setText("");
                    jTextArea7.append(str);
               
            
            }
           
        }

        @Override
        protected void done() {
            jProgressBar1.setIndeterminate(false);

            try {
                get(); // dummy method
                jLabelStatus.setText(bundle.getString("OCR_completed."));
                jProgressBar1.setString(bundle.getString("OCR_completed."));
            } catch (InterruptedException ignore) {
                logger.log(Level.WARNING, ignore.getMessage(), ignore);
            } catch (java.util.concurrent.ExecutionException e) {
                String why;
                Throwable cause = e.getCause();
                if (cause != null) {
                    if (cause instanceof IOException) {
                        why = bundle.getString("Cannot_find_Tesseract._Please_set_its_path.");
                    } else if (cause instanceof FileNotFoundException) {
                        why = bundle.getString("An_exception_occurred_in_Tesseract_engine_while_recognizing_this_image.");
                    } else if (cause instanceof OutOfMemoryError) {
                        why = cause.getMessage();
                    } else if (cause instanceof ClassCastException) {
                        why = cause.getMessage();
                        why += "\nConsider converting the image to binary or grayscale before OCR again.";
                    } else {
                        why = cause.getMessage();
                    }
                } else {
                    why = e.getMessage();
                }

                logger.log(Level.SEVERE, why, e);
                jLabelStatus.setText(null);
                jProgressBar1.setString(null);
                JOptionPane.showMessageDialog(null, why, "OCR Operation", JOptionPane.ERROR_MESSAGE);
            } catch (java.util.concurrent.CancellationException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
                jLabelStatus.setText("OCR " + bundle.getString("canceled"));
                jProgressBar1.setString("OCR " + bundle.getString("canceled"));
            } finally {
                getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                getGlassPane().setVisible(false);
                jButtonOCR.setVisible(true);
                jButtonOCR.setEnabled(true);
                jMenuItemOCR.setEnabled(true);
               
            

                // clean up temporary image files
                if (workingFiles != null) {
                    for (File tempImageFile : workingFiles) {
                        tempImageFile.delete();
                    }
                }
            }
        }
    }

OcrWorker9 ocrWorker9;
    @Override
 void performOCR9(final List<IIOImage> iioImageList, String inputfilename, final int index, Rectangle rect) {
        if (curLangCode.trim().length() == 0) {
            JOptionPane.showMessageDialog(this, bundle.getString("Please_select_a_language."), APP_NAME, JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        jLabelStatus.setText(bundle.getString("OCR_running..."));
        jProgressBar1.setIndeterminate(true);
        jProgressBar1.setString(bundle.getString("OCR_running..."));
        jProgressBar1.setVisible(true);
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        getGlassPane().setVisible(true);
        this.jButtonOCR.setEnabled(false);
        this.jMenuItemOCR.setEnabled(false);
     

        OCRImageEntity entity = new OCRImageEntity(iioImageList, inputfilename, index, rect, curLangCode);
       

        // instantiate SwingWorker for OCR
        ocrWorker9 = new OcrWorker9(entity);
        ocrWorker9.execute();
    }
  

    /**
     * A worker class for managing OCR process.
     */
  
        
        
class OcrWorker9 extends SwingWorker<Void, String> {

        OCRImageEntity entity;
        List<File> workingFiles;
        List<IIOImage> imageList; // Option for Tess4J

        OcrWorker9(OCRImageEntity entity) {
            this.entity = entity;
        }

        @Override
        protected Void doInBackground() throws Exception {
            String lang = entity.getLanguage();

            if (!tessLibEnabled) {
                OCR<File> ocrEngine = new OCRFiles(tessPath);
                ocrEngine.setDatapath(datapath);
                ocrEngine.setPageSegMode(selectedPSM);
                ocrEngine.setLanguage(lang);
                workingFiles = entity.getClonedImageFiles();

                for (int i = 0; i < workingFiles.size(); i++) {
                    if (!isCancelled()) {
                        String result = ocrEngine.recognizeText(workingFiles.subList(i, i + 1), entity.getInputfilename());
                       
                            publish(result); // interim result
                       
                       
                    }
                }
            } else {
                OCR<IIOImage> ocrEngine = new OCRImages(tessPath); // for Tess4J
                ocrEngine.setDatapath(datapath);
                ocrEngine.setPageSegMode(selectedPSM);
                ocrEngine.setLanguage(lang);
                imageList = entity.getSelectedOimages();

                for (int i = 0; i < imageList.size(); i++) {
                    if (!isCancelled()) {
                        String result = ocrEngine.recognizeText(imageList.subList(i, i + 1), entity.getInputfilename(), entity.getRect());
                        publish(result); // interim result
                    }
                }
            }

            return null;
        }

        @Override
        protected void process(List<String> results) {
       
            for (String str : results) {
               
                    jTextArea7.setText("");
                    jTextArea7.append(str);
               
            
            }
           
        }

        @Override
        protected void done() {
            jProgressBar1.setIndeterminate(false);

            try {
                get(); // dummy method
                jLabelStatus.setText(bundle.getString("OCR_completed."));
                jProgressBar1.setString(bundle.getString("OCR_completed."));
            } catch (InterruptedException ignore) {
                logger.log(Level.WARNING, ignore.getMessage(), ignore);
            } catch (java.util.concurrent.ExecutionException e) {
                String why;
                Throwable cause = e.getCause();
                if (cause != null) {
                    if (cause instanceof IOException) {
                        why = bundle.getString("Cannot_find_Tesseract._Please_set_its_path.");
                    } else if (cause instanceof FileNotFoundException) {
                        why = bundle.getString("An_exception_occurred_in_Tesseract_engine_while_recognizing_this_image.");
                    } else if (cause instanceof OutOfMemoryError) {
                        why = cause.getMessage();
                    } else if (cause instanceof ClassCastException) {
                        why = cause.getMessage();
                        why += "\nConsider converting the image to binary or grayscale before OCR again.";
                    } else {
                        why = cause.getMessage();
                    }
                } else {
                    why = e.getMessage();
                }

                logger.log(Level.SEVERE, why, e);
                jLabelStatus.setText(null);
                jProgressBar1.setString(null);
                JOptionPane.showMessageDialog(null, why, "OCR Operation", JOptionPane.ERROR_MESSAGE);
            } catch (java.util.concurrent.CancellationException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
                jLabelStatus.setText("OCR " + bundle.getString("canceled"));
                jProgressBar1.setString("OCR " + bundle.getString("canceled"));
            } finally {
                getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                getGlassPane().setVisible(false);
                jButtonOCR.setVisible(true);
                jButtonOCR.setEnabled(true);
                jMenuItemOCR.setEnabled(true);
               
            

                // clean up temporary image files
                if (workingFiles != null) {
                    for (File tempImageFile : workingFiles) {
                        tempImageFile.delete();
                    }
                }
            }
        }
    }
}








