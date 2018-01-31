package com.smartg.swing.combobox;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.UIManager;

/**
 * This JComboBox implementation will filter list values as you type. Attention!
 * This class will change behavior of all JComboBox objects, because it calls
 * <code>UIManager.put("ComboBox.noActionOnKeyNavigation", true);</code> Don't
 * use getModel().addElement(obj);, use addItem(obj), same rule applies to
 * removing of items.
 *
 * @author andro
 *
 * @param <E>
 */
public class AutoCompleteComboBox<E> extends JComboBox<E> {

    private static final long serialVersionUID = -8403430376204294634L;

    private boolean ignoreAction;

    /**
     * We have to intercept ActionEvents, so we make our own list.
     */
    private List<ActionListener> actionListeners = new ArrayList<>();

    /**
     * This model contains all elements, the original model - only filtered
     * elements.
     */
    private DefaultComboBoxModel<E> model;

    public AutoCompleteComboBox() {
        this(new DefaultComboBoxModel<E>());
    }

    public AutoCompleteComboBox(E[] items) {
        this(new DefaultComboBoxModel<>(items));
    }

    public AutoCompleteComboBox(Vector<E> items) {
        this(new DefaultComboBoxModel<>(items));
    }

    public AutoCompleteComboBox(ComboBoxModel<E> aModel) {
        super(aModel);
        this.model = new DefaultComboBoxModel<>();
        int size = aModel.getSize();
        for (int i = 0; i < size; i++) {
            this.model.addElement(aModel.getElementAt(i));
        }
        setEditable(true);
        UIManager.put("ComboBox.noActionOnKeyNavigation", true);

        super.addActionListener(new ActionHandler());
        getEditor().getEditorComponent().addKeyListener(new KeyHandler());
    }

    @Override
    public void setSelectedItem(Object anObject) {
        if (!isPopupVisible()) {
            super.setSelectedItem(anObject);
        }
    }

    @Override
    public void addActionListener(ActionListener l) {
        if (!actionListeners.contains(l)) {
            actionListeners.add(l);
        }
    }

    @Override
    public void removeActionListener(ActionListener l) {
        actionListeners.remove(l);
    }

    private void fireActionEvent(ActionEvent e) {
        for (ActionListener l : actionListeners) {
            l.actionPerformed(e);
        }
    }

    @Override
    public void addItem(E item) {
        super.addItem(item);
        this.model.addElement(item);
    }

    @Override
    public void removeItem(Object anObject) {
        super.removeItem(anObject);
        this.model.removeElement(anObject);
    }

    @Override
    public void removeItemAt(int anIndex) {
        super.removeItemAt(anIndex);
        this.model.removeElementAt(anIndex);
    }

    @Override
    public void removeAllItems() {
        super.removeAllItems();
        this.model.removeAllElements();
    }

    protected void filterValues(String text) {
        String s = text.toLowerCase();
        ignoreAction = true;
        DefaultComboBoxModel<E> superModel = (DefaultComboBoxModel<E>) getModel();
        superModel.removeAllElements();
        int size = model.getSize();
        for (int i = 0; i < size; i++) {
            E element = model.getElementAt(i);
            String t = String.valueOf(element).toLowerCase();
            if (t.startsWith(s)) {
                superModel.addElement(element);
            }
        }
        ignoreAction = false;
    }

    /**
     * During the original ComboBoxModel is filled with values, JComboBox fires
     * ActionEvents, which we intercept here.
     *
     * @author andro
     *
     */
    private final class ActionHandler implements ActionListener {

        @Override
		public void actionPerformed(ActionEvent e) {
            if (!ignoreAction) {
                fireActionEvent(e);
            }
        }
    }

    private final class KeyHandler extends KeyAdapter {

        private String value = "";

        @Override
        public void keyReleased(KeyEvent e) {
            int keyCode = e.getKeyCode();
            if (keyCode == KeyEvent.VK_ENTER) {
                Object selectedItem = getSelectedItem();
                if (selectedItem != null) {
                    String entry = selectedItem.toString();
                    fireActionEvent(new ActionEvent(AutoCompleteComboBox.this, ActionEvent.ACTION_PERFORMED, entry));
                }
                return;
            }
            JTextField editor = (JTextField) getEditor().getEditorComponent();
            String text = editor.getText();
            if (text.equalsIgnoreCase(value)) {
                return;
            }
            value = text;
            filterValues(text);
            setPopupVisible(true);
            ((JTextField) getEditor().getEditorComponent()).setText(text);
        }
    }

    public static void main(String[] args) {
        String[] values = {"Contact", "Company", "User", "UserGroup", "GroupAssign", "Campaignee", "Template",
            "CampaignStep", "MtgLead", "Lead", "Stage", "StageMove", "Status", "ProductInterest",
            "ProductInterestBundle", "SubscriptionPlan", "CProgram", "ProductCategoryAssign", "Product",
            "ProductOption", "ProductOptValue", "ProductCategory", "CreditCard", "RecurringOrder",
            "RecurringOrderWithContact", "ContactGroupAssign", "ContactGroup", "ContactGroupCategory", "LeadSource",
            "LeadSourceCategory", "Campaign", "Invoice", "Affiliate", "AffResource", "Referral", "InvoiceItem",
            "InvoicePayment", "PayPlan", "PayPlanItem", "Payment", "CCharge", "Job", "JobRecurringInstance",
            "OrderItem", "ActionSequence", "ContactAction", "Ticket", "TicketStage", "TicketType", "DataFormTab",
            "DataFormGroup", "DataFormField", "Expense", "LeadSourceExpense", "LeadSourceRecurringExpense",
            "FileBox", "SavedFilter",};

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        AutoCompleteComboBox<String> comboBox = new AutoCompleteComboBox<>(values);
        frame.getContentPane().add(comboBox);
        frame.pack();
        frame.setVisible(true);
    }

}
