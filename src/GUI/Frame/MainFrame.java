package GUI.Frame;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.xml.bind.annotation.XmlElementDecl.GLOBAL;

import BaseUtil.GlobalData;

public class MainFrame extends JFrame implements Runnable {
	/**
	 * MainFrame
	 */
	private static final long serialVersionUID = -5988513125942516733L;
	private static MainFrame mainFrame;

	public static MainFrame instance() {
		if (mainFrame == null)
			mainFrame = new MainFrame();
		return mainFrame;
	}

	public MainFrame() {
		setTitle("报告查重系统-"+ GlobalData.getSingleton().m_DataBase.m_CurrentDataBase);
		mainFrame = this;
//		setUndecorated(true);
		// setAlwaysOnTop(true);
		setSize(new Dimension(1024, 700));
		setMinimumSize(new Dimension(800, 600));

		Container container = getContentPane();
		container.setLayout(new BorderLayout());
//		setExtendedState(JFrame.MAXIMIZED_BOTH);
		container.add(new OptionPanel(), BorderLayout.NORTH);
		container.add(new MenuPanel(), BorderLayout.WEST);
		container.add(new MainPanel(), BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(new ImageIcon(this.getClass().getResource(
				"/images/icon.png")).getImage());

		((JPanel) this.getContentPane()).setOpaque(false);
		BackgroundPanel background = new BackgroundPanel();
		getLayeredPane().add(background, new Integer(Integer.MIN_VALUE));
	}

	public void open() {
		setVisible(true);
	}

	@Override
	public void run() {
		instance();
		setVisible(true);
	}
}
