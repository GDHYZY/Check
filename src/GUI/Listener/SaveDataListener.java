package GUI.Listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import GUI.Frame.SaveDataDialog;


public class SaveDataListener implements ActionListener {

	private JButton cancel;

	public SaveDataListener(JButton cancel) {
		this.cancel = cancel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == cancel) {
			SaveDataDialog.instance().dispose();
		} else {
			
		}
	}
}
