package GUI.Frame;


import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

import BaseUtil.GlobalData;
import GUI.Listener.LoginDialogListener;


public class LoginDialog extends JDialog implements Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7635382088464340846L;
	private static LoginDialog loginDialog;
	private JComboBox jcbList;
	
	public static LoginDialog instance() {
		if (loginDialog == null)
			loginDialog = new LoginDialog();
		return loginDialog;
	}

	public LoginDialog() {
		setLayout(null);
		setSize(400, 250);
		setLocationRelativeTo(null);
		loginDialog = this;
		
		JLabel jlbList = new JLabel("数据库：");
		jcbList = new JComboBox();
		JButton create = new JButton("新建");
		JButton ensure = new JButton("连接");
		JButton cancel = new JButton("取消");
		
		jlbList.setBounds(60, 25, 100, 40);
		jcbList.setBounds(140, 25, 190, 35);
		
		create.setBounds(50, 100, 90, 40);
		ensure.setBounds(150, 100, 90, 40);
		cancel.setBounds(250, 100, 90, 40);

		LoginDialogListener LoginDialogListener = new LoginDialogListener(jcbList,
				create, ensure, cancel);
		create.addActionListener(LoginDialogListener);
		ensure.addActionListener(LoginDialogListener);
		cancel.addActionListener(LoginDialogListener);
		
		add(jlbList);
		add(jcbList);
		add(create);
		add(ensure);
		add(cancel);
		setLocationRelativeTo(null);
	}

	@SuppressWarnings("unchecked")
	public void open() {
		setTitle("登陆数据库");
		setIconImage(new ImageIcon(this.getClass().getResource(
				"/images/icon.png")).getImage());
		
		jcbList.removeAllItems();
		for (Object[] o : GlobalData.getSingleton().getDataBaseList()){
			jcbList.addItem(o[0]);
		}
		setVisible(true);
	}

	public void showMainFrame() {
		MainFrame.instance().open();
	}

	public void exit() {
		System.exit(0);
	}

	@Override
	public void run() {
		instance().open();
	}
}
