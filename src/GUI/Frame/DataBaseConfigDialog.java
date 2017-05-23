package GUI.Frame;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import BaseUtil.GlobalData;
import GUI.Listener.DataBaseConfigListener;
import GUI.Listener.TableListener;


public class DataBaseConfigDialog extends JDialog {
	private static final long serialVersionUID = 5016876596940564305L;
	private static DataBaseConfigDialog databaseconfigDialog;
	private DataTable table;
	private JScrollPane jscrolPane;

	public static DataBaseConfigDialog instance() {
		if (databaseconfigDialog == null)
			databaseconfigDialog = new DataBaseConfigDialog();
		return databaseconfigDialog;
	}

	public DataBaseConfigDialog() {
		super(MainFrame.instance(), "数据库设置", true);
		setLayout(null);
		setSize(350, 400);
		setLocationRelativeTo(null);
		databaseconfigDialog = this;	
		
		table = new DataTable(null, null);
		jscrolPane = new JScrollPane();
		jscrolPane.setBorder(null);
		jscrolPane.setOpaque(false);
		jscrolPane.getViewport().setOpaque(false);
		add(jscrolPane);
		jscrolPane.setBounds(10, 10, 200, 350);

		showAllDB();
		
	}

	public void showData(Object[][] data, Object[] head) {
		table.removeAll();
		table = new DataTable(data, head);
		jscrolPane.setViewportView(table);
	}
	
	public void showAllDB(){
		Object[] head = { "数据库名" };
		showData(GlobalData.getSingleton().getDataBaseList(), head);
		
		JPopupMenu menu = new JPopupMenu();
		JMenuItem link = new JMenuItem("    连接数据库  ");
		JMenuItem delete = new JMenuItem("    删除数据库    ");
		JMenuItem clear = new JMenuItem("    清空数据库    ");
		menu.add(link);
		menu.add(delete);
		menu.add(clear);
		TableListener tableListener = new TableListener(table,menu);
		table.addMouseListener(tableListener);
		table.addMouseMotionListener(tableListener);
		link.addActionListener(tableListener);
		delete.addActionListener(tableListener);
		clear.addActionListener(tableListener);
		
		JButton create = new JButton("创建数据库");
		JButton cancel = new JButton("取消");
		create.setBounds(230, 100, 100, 30);
		cancel.setBounds(230, 200, 100, 30);
		add(create);
		add(cancel);
		DataBaseConfigListener databaseconfigListener = new DataBaseConfigListener(
				GlobalData.getSingleton().m_DataBase.m_CurrentDataBase, create, cancel);
		create.addActionListener(databaseconfigListener);
		cancel.addActionListener(databaseconfigListener);
	}
	
	public void open() {
		setVisible(true);
		showAllDB();
	}
}
