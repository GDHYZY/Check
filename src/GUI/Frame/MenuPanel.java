package GUI.Frame;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel;

import GUI.Listener.MenuListener;


public class MenuPanel extends JPanel {
	/**
	 * MenuPanel
	 */
	private static final long serialVersionUID = -2703153422697822701L;
	private static MenuPanel menuPanel;

	public static MenuPanel instance() {
		if (menuPanel == null)
			menuPanel = new MenuPanel();
		return menuPanel;
	}

	public MenuPanel() {
		setOpaque(false);
		setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 15));
		setPreferredSize(new Dimension(150, 300));

		ImageButton jbtCheckList, jbtDBList;
		jbtCheckList = new ImageButton("menu", "待查列表", 16);
		jbtDBList = new ImageButton("menu", "数据库样本", 16);

		jbtCheckList.setPreferredSize(new Dimension(180, 50));
		jbtDBList.setPreferredSize(new Dimension(180, 50));

		MenuListener menuListener = new MenuListener(jbtCheckList, jbtDBList);
		jbtCheckList.addActionListener(menuListener);
		jbtDBList.addActionListener(menuListener);
	
		add(jbtCheckList);
		add(jbtDBList);
	}
}