package GUI.Listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import BaseUtil.GlobalData;
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
			GlobalData.getSingleton().saveIntoDataBase();
			SaveDataDialog.instance().dispose();
			JOptionPane.showMessageDialog(null, "保存完毕");
		}
	}
}
