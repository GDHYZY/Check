package GUI.Listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;

import BaseUtil.GlobalData;
import CompareModule.CompareUnit;
import GUI.Frame.CheckDialog;
import GUI.Frame.MainPanel;
import GUI.Frame.SaveDataDialog;

public class CheckListener  implements ActionListener {
	private JButton ensure;
	private JButton cancel;
	private JCheckBox jcbPic;
	
	
	public CheckListener(JCheckBox jcbPic, JButton ensure, JButton cancel) {
		this.jcbPic = jcbPic;
		this.cancel = cancel;
		this.ensure = ensure;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == cancel) {
			CheckDialog.instance().dispose();
		} else if (e.getSource() == ensure){
			if (jcbPic.isSelected()){
				//有图模式
				GlobalData.getSingleton().m_noCheckPicture = false;
			} else {
				//无图模式
				GlobalData.getSingleton().m_noCheckPicture = true;
			}
			CheckDialog.instance().dispose();
			
			if (GlobalData.getSingleton().m_ExportData != null || !GlobalData.getSingleton().m_ExportData.isEmpty())
			{
				new CompareUnit().Compare();
			}
			JOptionPane.showMessageDialog(null, "检测完毕");
			MainPanel.instance().refresh();
		}
	}
}
