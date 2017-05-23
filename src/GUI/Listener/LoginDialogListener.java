package GUI.Listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import BaseUtil.GlobalData;
import GUI.Frame.LoginDialog;
import GUI.Frame.MainFrame;


public class LoginDialogListener extends MouseAdapter implements ActionListener {

	private JComboBox jcblist;
	private JButton ensure;
	private JButton create;
	private JButton cancel;

	public LoginDialogListener(JComboBox jcblist, JButton create, JButton ensure, JButton cancel ) {
		this.jcblist = jcblist;
		this.ensure = ensure;
		this.create = create;
		this.cancel = cancel;
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == cancel ) {
			System.exit(0);
		} else if (e.getSource() == create) {
			String value = JOptionPane.showInputDialog( "输入数据库名称" );
			if (value != null){
				GlobalData.getSingleton().m_DataBase.CreateOneDataBase(value);
				LoginDialog.instance().open();
			}
		} else if (e.getSource() == ensure){
			//连入数据库
			String name = jcblist.getSelectedItem().toString();
			GlobalData.getSingleton().m_DataBase.CreateandConnectDataBase(name);
			LoginDialog.instance().dispose();
			MainFrame.instance().open();
		}
	}
}
