package GUI.Frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import BaseUtil.GlobalData;
import GUI.Listener.TableListener;

public class LogPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4090036947233273308L;
	private static LogPanel logpanel;
	private JTextArea textarea;

	public static LogPanel instance() {
		if (logpanel == null)
			logpanel = new LogPanel();
		return logpanel;
	}

	public LogPanel() {
		logpanel = this;
		setOpaque(false);
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(0, 0, 20, 10));
		setPreferredSize(new Dimension(150, 700));
		
		textarea = new JTextArea();
		textarea.setEditable(false);
		textarea.setSelectedTextColor(Color.RED);
		
		add(textarea,BorderLayout.CENTER);
		setVisible(true);
	}

	
	@Override
	public void paint(Graphics g) {
		ImageIcon icon = new ImageIcon(this.getClass().getResource(
				"/images/scrollpane.png"));
		Image img = icon.getImage();
		g.drawImage(img, textarea.getX(), textarea.getY(),
				textarea.getWidth(), textarea.getHeight(), this);
		super.paint(g);
	}

	public void refresh() {
		
	}

}
