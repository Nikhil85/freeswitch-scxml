/*
 * AssignCustomizerPanel.java
 *
 * Created on 2011-jan-09, 17:34:39
 */
package com.albatross.visualivr.palette.items.ui;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.scxml.io.SCXMLSerializer;
import org.apache.commons.scxml.model.Action;
import org.apache.commons.scxml.model.Assign;

/**
 *
 * @author joe
 */
public class AssignCustomizerPanel extends javax.swing.JPanel {
    
    private String apos = "&apos;";
    
    /** Creates new form AssignCustomizerPanel */
    public AssignCustomizerPanel() {
        initComponents();
    }

    public void reset() {
        locationTextField.setText("");
        locationExprTextField.setText("");
    }

    public String getXml() {
        Assign assign = new Assign();

        String exp = locationExprTextField.getText();

        if (exp != null && !exp.trim().isEmpty()) {
            assign.setLocation(locationTextField.getText());

        } else {
            assign.setName((String) nameComboBox.getSelectedItem());
            exp = (String) valueComboBox.getSelectedItem();
        }

        assign.setExpr(exp);

        List<Action> actions = new ArrayList<Action>();
        actions.add(assign);

        StringBuffer buffer = new StringBuffer();
        SCXMLSerializer.serializeActions(buffer, actions, "");
        
        return buffer.toString().replaceAll(apos, "'");
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nameComboBox = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        nameLabel = new javax.swing.JLabel();
        locationLabel = new javax.swing.JLabel();
        locationTextField = new javax.swing.JTextField();
        locationExprTextField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        valueComboBox = new javax.swing.JComboBox();

        nameComboBox.setEditable(true);

        jLabel1.setText(org.openide.util.NbBundle.getMessage(AssignCustomizerPanel.class, "AssignCustomizerPanel.jLabel1.text")); // NOI18N

        nameLabel.setText(org.openide.util.NbBundle.getMessage(AssignCustomizerPanel.class, "AssignCustomizerPanel.nameLabel.text")); // NOI18N

        locationLabel.setText(org.openide.util.NbBundle.getMessage(AssignCustomizerPanel.class, "AssignCustomizerPanel.locationLabel.text")); // NOI18N

        locationTextField.setText(org.openide.util.NbBundle.getMessage(AssignCustomizerPanel.class, "AssignCustomizerPanel.locationTextField.text")); // NOI18N

        locationExprTextField.setText(org.openide.util.NbBundle.getMessage(AssignCustomizerPanel.class, "AssignCustomizerPanel.locationExprTextField.text")); // NOI18N

        jLabel4.setText(org.openide.util.NbBundle.getMessage(AssignCustomizerPanel.class, "AssignCustomizerPanel.jLabel4.text")); // NOI18N

        valueComboBox.setEditable(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(nameLabel)
                    .addComponent(locationLabel))
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(nameComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(locationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(valueComboBox, 0, 233, Short.MAX_VALUE)
                    .addComponent(locationExprTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 233, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nameLabel)
                    .addComponent(jLabel1)
                    .addComponent(valueComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(locationLabel)
                    .addComponent(locationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(locationExprTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField locationExprTextField;
    private javax.swing.JLabel locationLabel;
    private javax.swing.JTextField locationTextField;
    private javax.swing.JComboBox nameComboBox;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JComboBox valueComboBox;
    // End of variables declaration//GEN-END:variables
}