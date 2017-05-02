package GUI.Listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import BaseUtil.GlobalData;
import GUI.Frame.DataBaseConfigDialog;
import GUI.Frame.MainFrame;

public class DataBaseConfigListener implements ActionListener {
	private JButton create, cancel;
	private String DataBasename;
	
	public DataBaseConfigListener(String dbname, JButton create, JButton cancel) {
		super();
		this.DataBasename = dbname;
		this.create = create;
		this.cancel = cancel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == cancel) {
			GlobalData.getSingleton().m_DataBase.CreateandConnectDataBase(DataBasename);
			DataBaseConfigDialog.instance().dispose();
		} else if (e.getSource() == create) {
			//创建新的数据库
			String value = JOptionPane.showInputDialog( "输入数据库名称" );
			if (value != null){
				GlobalData.getSingleton().m_DataBase.CreateOneDataBase(value);
				DataBaseConfigDialog.instance().open();
			}
		}
	}
}
