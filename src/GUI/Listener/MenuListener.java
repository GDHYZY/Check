package GUI.Listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import GUI.Frame.MainPanel;

public class MenuListener implements ActionListener {

	private JButton jbtCheckList, jbtDBList;

	public MenuListener(JButton jbtCheckList, JButton jbtDBList) {
		this.jbtCheckList = jbtCheckList;
		this.jbtDBList = jbtDBList;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == jbtCheckList) {
			MainPanel.instance().showAllCheckList();
		} else if (e.getSource() == jbtDBList) {
			MainPanel.instance().showAllDataBaseData();
		} 
	}

}
