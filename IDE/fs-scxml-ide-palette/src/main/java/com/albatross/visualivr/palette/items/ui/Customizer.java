/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.albatross.visualivr.palette.items.ui;

import javax.swing.JPanel;

/**
 *
 * @author joe
 */
public interface Customizer<T> {
    T getValue();
    JPanel getPanel();
}
