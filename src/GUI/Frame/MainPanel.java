package GUI.Frame;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import BaseUtil.GlobalData;
import GUI.Listener.TableListener;


public class MainPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4090036947302273308L;
	private static MainPanel mainPanel;
	private DataTable table;
	private JScrollPane jscrolPane;
	private String dataType;

	public static MainPanel instance() {
		if (mainPanel == null)
			mainPanel = new MainPanel();
		return mainPanel;
	}

	public MainPanel() {
		mainPanel = this;
		setOpaque(false);
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(0, 0, 20, 10));
		table = new DataTable(null, null);
		jscrolPane = new JScrollPane();
		jscrolPane.setBorder(null);
		jscrolPane.setOpaque(false);
		jscrolPane.getViewport().setOpaque(false);
		add(jscrolPane);
		showAllCheckList();
	}

	@Override
	public void paint(Graphics g) {
		ImageIcon icon = new ImageIcon(this.getClass().getResource(
				"/images/scrollpane.png"));
		Image img = icon.getImage();
		g.drawImage(img, jscrolPane.getX(), jscrolPane.getY(),
				jscrolPane.getWidth(), jscrolPane.getHeight(), this);
		super.paint(g);
	}

	public DataTable getTable() {
		return table;
	}
	
	public void showAllCheckList(){
		dataType = "CheckList";
		Object[] head = { "标题","段落数", "字数", "相似度", "日期" };
		showData(GlobalData.getSingleton().getCheckList(), head);
		JPopupMenu menu = new JPopupMenu();
		JMenuItem checkout = new JMenuItem("    查看报告    ");
		menu.add(checkout);
		TableListener tableListener = new TableListener(menu);
		table.addMouseListener(tableListener);
		table.addMouseMotionListener(tableListener);
		checkout.addActionListener(tableListener);
	}

	public void showAllDataBaseData() {
		dataType = "DataBaseContent";
		Object[] head = { "标题", "段落数", "字数", "日期" };
		showData(GlobalData.getSingleton().getDataBaseContent(), head);
		
		JPopupMenu menu = new JPopupMenu();
		JMenuItem delete = new JMenuItem("    删除项目    ");
		menu.add(delete);
		TableListener tableListener = new TableListener(menu);
		table.addMouseListener(tableListener);
		table.addMouseMotionListener(tableListener);
		delete.addActionListener(tableListener);
	}

	public void showData(Object[][] data, Object[] head) {
		table.removeAll();
		table = new DataTable(data, head);
		jscrolPane.setViewportView(table);
	}

	public void refresh() {
		if (dataType.equals("CheckList")) {
			showAllCheckList();
		} else if (dataType.equals("DataBaseContent")) {
			showAllDataBaseData();
		}
	}
}
