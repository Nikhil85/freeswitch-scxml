package com.albatross.visualivr.palette.items.ui;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import org.openide.util.NbBundle;

/**
 *
 * @author joe
 */
public class ExpressionBuilder {

    private final DefaultComboBoxModel operators;
    private final DefaultComboBoxModel leftVars;
    private final DefaultComboBoxModel rightVars;
    private final JComboBox leftSideExp;
    private final JComboBox rightSideExp;
    private final JComboBox ops;
    private final static String DEFAULT_VARIABLE_MESSAGE = "-value-";
    private final static String DEFAULT_OP_MESSAGE = "-op-";

    public ExpressionBuilder() {
        String opsList = NbBundle.getMessage(ExpressionBuilder.class, "jexl.operators");
        operators = new DefaultComboBoxModel(opsList.split("\\s+"));
        operators.insertElementAt(DEFAULT_OP_MESSAGE, 0);

        leftVars = new DefaultComboBoxModel();
        leftVars.insertElementAt(DEFAULT_VARIABLE_MESSAGE, 0);
        
        rightVars = new DefaultComboBoxModel();
        rightVars.insertElementAt(DEFAULT_VARIABLE_MESSAGE, 0);

        leftSideExp = new JComboBox(leftVars);
        rightSideExp = new JComboBox(rightVars);
        ops = new JComboBox(operators);
        
        leftSideExp.setEditable(true);
        rightSideExp.setEditable(true);
        
    }

    public JComboBox getLeftSideExp() {
        return leftSideExp;
    }

    public JComboBox getOps() {
        return ops;
    }

    public JComboBox getRightSideExp() {
        return rightSideExp;
    }
    
    /**
     * TODO maybe to many exits.
     *  
     **/
    public String getExpression() {

        StringBuilder builder = new StringBuilder();

        Object left = leftSideExp.getSelectedItem();

        if (left != null && !left.equals(DEFAULT_VARIABLE_MESSAGE)) {
            builder.append(left);

        } else {
            return builder.toString();
        }

        Object op = ops.getSelectedItem();

        if (op != null && !op.equals(DEFAULT_OP_MESSAGE)) {
            builder.append(" ").append(op);

        } else {
            return builder.toString();
        }

        Object right = rightSideExp.getSelectedItem();

        if (right != null && !right.equals(DEFAULT_VARIABLE_MESSAGE)) {
            builder.append(" ").append(right);

        } else {
            return builder.toString();
        }
        
        return builder.toString();
    }

    public void reset() {
        leftSideExp.setSelectedItem(DEFAULT_VARIABLE_MESSAGE);
        rightSideExp.setSelectedItem(DEFAULT_VARIABLE_MESSAGE);
        ops.setSelectedItem(DEFAULT_OP_MESSAGE);
    }
}
