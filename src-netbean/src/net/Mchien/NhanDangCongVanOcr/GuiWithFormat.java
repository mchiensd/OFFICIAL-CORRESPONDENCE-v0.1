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

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import net.Mchien.NhanDangCongVan.utilities.TextUtilities;
import net.Mchien.NhanDangCongVanOcr.components.FontDialog;

public class GuiWithFormat extends GuiWithImage {

    private final String strSelectedCase = "selectedCase";
    private final String strChangeCaseX = "changeCaseX";
    private final String strChangeCaseY = "changeCaseY";
    private ChangeCaseDialog changeCaseDlg;

    private final static Logger logger = Logger.getLogger(GuiWithFormat.class.getName());

    @Override
    void changeUILanguage(final Locale locale) {
        super.changeUILanguage(locale);

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (changeCaseDlg != null) {
                    changeCaseDlg.changeUILanguage(locale);
                }
            }
        });
    }

  
    void jCheckBoxMenuWordWrapActionPerformed(java.awt.event.ActionEvent evt) {
        
    }

    
    void jMenuItemFontActionPerformed(java.awt.event.ActionEvent evt) {
       
    }

   
    void jMenuItemChangeCaseActionPerformed(java.awt.event.ActionEvent evt) {
       
    }

    @Override
    void quit() {
        if (changeCaseDlg != null) {
            prefs.put(strSelectedCase, changeCaseDlg.getSelectedCase());
            prefs.putInt(strChangeCaseX, changeCaseDlg.getX());
            prefs.putInt(strChangeCaseY, changeCaseDlg.getY());
        }
        super.quit();
    }

    /**
     * Changes letter case.
     *
     * @param typeOfCase The type that the case should be changed to
     */
    public void changeCase(String typeOfCase) {
       
    }

    /**
     * Removes extra line breaks.
     *
     * @param evt
     */
  
    void jMenuItemRemoveLineBreaksActionPerformed(java.awt.event.ActionEvent evt) {
       
    }
}
