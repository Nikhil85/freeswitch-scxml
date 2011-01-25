/*
 * SoftPhoneDialog.java
 *
 * Created on Dec 9, 2010, 2:28:36 PM
 */
package com.albatross.visualivr.simulator.ui;

import com.telmi.msc.freeswitch.DTMFMessage;
import com.telmi.msc.freeswitch.FSEvent;
import com.telmi.msc.freeswitch.FSEventName;
import java.util.Queue;
import javax.swing.JButton;

/**
 *
 * @author jocke
 */
public class SoftPhoneDialog extends javax.swing.JDialog {

    private Queue<FSEvent> events;

    public SoftPhoneDialog(Queue<FSEvent> events) {
        this.events = events;
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        phoneButton1 = new javax.swing.JButton();
        phoneButton2 = new javax.swing.JButton();
        phoneButton3 = new javax.swing.JButton();
        phoneButton5 = new javax.swing.JButton();
        phoneButton4 = new javax.swing.JButton();
        phoneButton6 = new javax.swing.JButton();
        phoneButton9 = new javax.swing.JButton();
        phoneButton8 = new javax.swing.JButton();
        phoneButton7 = new javax.swing.JButton();
        phoneButtonPound = new javax.swing.JButton();
        phoneButtonStar = new javax.swing.JButton();
        hangupButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        phoneButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/albatross/visualivr/simulator/ui/phone-button-background-50x50.png"))); // NOI18N
        phoneButton1.setText("1");
        phoneButton1.setBorder(null);
        phoneButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        phoneButton1.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/com/albatross/visualivr/simulator/ui/phone-button-background-pressed-50x50.png"))); // NOI18N
        phoneButton1.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/com/albatross/visualivr/simulator/ui/phone-button-background-over-50x50.png"))); // NOI18N
        phoneButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dtmfPressed(evt);
            }
        });

        phoneButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/albatross/visualivr/simulator/ui/phone-button-background-50x50.png"))); // NOI18N
        phoneButton2.setText("2");
        phoneButton2.setBorder(null);
        phoneButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        phoneButton2.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/com/albatross/visualivr/simulator/ui/phone-button-background-pressed-50x50.png"))); // NOI18N
        phoneButton2.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/com/albatross/visualivr/simulator/ui/phone-button-background-over-50x50.png"))); // NOI18N
        phoneButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dtmfPressed(evt);
            }
        });

        phoneButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/albatross/visualivr/simulator/ui/phone-button-background-50x50.png"))); // NOI18N
        phoneButton3.setText("3");
        phoneButton3.setBorder(null);
        phoneButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        phoneButton3.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/com/albatross/visualivr/simulator/ui/phone-button-background-pressed-50x50.png"))); // NOI18N
        phoneButton3.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/com/albatross/visualivr/simulator/ui/phone-button-background-over-50x50.png"))); // NOI18N
        phoneButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dtmfPressed(evt);
            }
        });

        phoneButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/albatross/visualivr/simulator/ui/phone-button-background-50x50.png"))); // NOI18N
        phoneButton5.setText("5");
        phoneButton5.setBorder(null);
        phoneButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        phoneButton5.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/com/albatross/visualivr/simulator/ui/phone-button-background-pressed-50x50.png"))); // NOI18N
        phoneButton5.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/com/albatross/visualivr/simulator/ui/phone-button-background-over-50x50.png"))); // NOI18N
        phoneButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dtmfPressed(evt);
            }
        });

        phoneButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/albatross/visualivr/simulator/ui/phone-button-background-50x50.png"))); // NOI18N
        phoneButton4.setText("4");
        phoneButton4.setBorder(null);
        phoneButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        phoneButton4.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/com/albatross/visualivr/simulator/ui/phone-button-background-pressed-50x50.png"))); // NOI18N
        phoneButton4.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/com/albatross/visualivr/simulator/ui/phone-button-background-over-50x50.png"))); // NOI18N
        phoneButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dtmfPressed(evt);
            }
        });

        phoneButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/albatross/visualivr/simulator/ui/phone-button-background-50x50.png"))); // NOI18N
        phoneButton6.setText("6");
        phoneButton6.setBorder(null);
        phoneButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        phoneButton6.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/com/albatross/visualivr/simulator/ui/phone-button-background-pressed-50x50.png"))); // NOI18N
        phoneButton6.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/com/albatross/visualivr/simulator/ui/phone-button-background-over-50x50.png"))); // NOI18N
        phoneButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dtmfPressed(evt);
            }
        });

        phoneButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/albatross/visualivr/simulator/ui/phone-button-background-50x50.png"))); // NOI18N
        phoneButton9.setText("9");
        phoneButton9.setBorder(null);
        phoneButton9.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        phoneButton9.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/com/albatross/visualivr/simulator/ui/phone-button-background-pressed-50x50.png"))); // NOI18N
        phoneButton9.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/com/albatross/visualivr/simulator/ui/phone-button-background-over-50x50.png"))); // NOI18N
        phoneButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dtmfPressed(evt);
            }
        });

        phoneButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/albatross/visualivr/simulator/ui/phone-button-background-50x50.png"))); // NOI18N
        phoneButton8.setText("8");
        phoneButton8.setBorder(null);
        phoneButton8.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        phoneButton8.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/com/albatross/visualivr/simulator/ui/phone-button-background-pressed-50x50.png"))); // NOI18N
        phoneButton8.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/com/albatross/visualivr/simulator/ui/phone-button-background-over-50x50.png"))); // NOI18N
        phoneButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dtmfPressed(evt);
            }
        });

        phoneButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/albatross/visualivr/simulator/ui/phone-button-background-50x50.png"))); // NOI18N
        phoneButton7.setText("7");
        phoneButton7.setBorder(null);
        phoneButton7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        phoneButton7.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/com/albatross/visualivr/simulator/ui/phone-button-background-pressed-50x50.png"))); // NOI18N
        phoneButton7.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/com/albatross/visualivr/simulator/ui/phone-button-background-over-50x50.png"))); // NOI18N
        phoneButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dtmfPressed(evt);
            }
        });

        phoneButtonPound.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/albatross/visualivr/simulator/ui/phone-button-background-50x50.png"))); // NOI18N
        phoneButtonPound.setText("#");
        phoneButtonPound.setBorder(null);
        phoneButtonPound.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        phoneButtonPound.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/com/albatross/visualivr/simulator/ui/phone-button-background-pressed-50x50.png"))); // NOI18N
        phoneButtonPound.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/com/albatross/visualivr/simulator/ui/phone-button-background-over-50x50.png"))); // NOI18N
        phoneButtonPound.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dtmfPressed(evt);
            }
        });

        phoneButtonStar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/albatross/visualivr/simulator/ui/phone-button-background-50x50.png"))); // NOI18N
        phoneButtonStar.setText("*");
        phoneButtonStar.setBorder(null);
        phoneButtonStar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        phoneButtonStar.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/com/albatross/visualivr/simulator/ui/phone-button-background-pressed-50x50.png"))); // NOI18N
        phoneButtonStar.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/com/albatross/visualivr/simulator/ui/phone-button-background-over-50x50.png"))); // NOI18N
        phoneButtonStar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dtmfPressed(evt);
            }
        });

        hangupButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/albatross/visualivr/simulator/ui/phone-button-hangup-50x50.png"))); // NOI18N
        hangupButton.setBorder(null);
        hangupButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        hangupButton.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/com/albatross/visualivr/simulator/ui/phone-button-hangup-pressed-50x50.png"))); // NOI18N
        hangupButton.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/com/albatross/visualivr/simulator/ui/phone-button-hangup-over-50x50.png"))); // NOI18N
        hangupButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hangupButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(phoneButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(phoneButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(phoneButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(phoneButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(phoneButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(phoneButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(phoneButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(phoneButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(phoneButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(phoneButtonPound, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(phoneButtonStar, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(hangupButton, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(53, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(phoneButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(phoneButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(phoneButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(phoneButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(phoneButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(phoneButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(phoneButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(phoneButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(phoneButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(phoneButtonPound, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(phoneButtonStar, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(hangupButton, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(59, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void dtmfPressed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dtmfPressed

        JButton button = (JButton) evt.getSource();
        char[] toCharArray = button.getText().toCharArray();
        DTMFMessage dtmf = DTMFMessage.valueOfChar(toCharArray[0]);
        events.offer(FSEvent.getInstance(dtmf));
    }//GEN-LAST:event_dtmfPressed

    private void hangupButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hangupButtonActionPerformed
        events.offer(FSEvent.getInstance(FSEventName.CHANNEL_HANGUP));
    }//GEN-LAST:event_hangupButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton hangupButton;
    private javax.swing.JButton phoneButton1;
    private javax.swing.JButton phoneButton2;
    private javax.swing.JButton phoneButton3;
    private javax.swing.JButton phoneButton4;
    private javax.swing.JButton phoneButton5;
    private javax.swing.JButton phoneButton6;
    private javax.swing.JButton phoneButton7;
    private javax.swing.JButton phoneButton8;
    private javax.swing.JButton phoneButton9;
    private javax.swing.JButton phoneButtonPound;
    private javax.swing.JButton phoneButtonStar;
    // End of variables declaration//GEN-END:variables
}
