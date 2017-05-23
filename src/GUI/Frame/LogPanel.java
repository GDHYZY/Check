package GUI.Frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import BaseUtil.LogUnit;

public class LogPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4090036947233273308L;
	private static LogPanel logpanel;
	private JTextArea textarea;
	private JScrollPane jscrolPane;

	public static LogPanel instance() {
		if (logpanel == null)
			logpanel = new LogPanel();
		return logpanel;
	}
	
	public JTextArea getTextArea(){
		return textarea;
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
//		textarea.setLineWrap(true);
//		textarea.setWrapStyleWord(true);

		jscrolPane = new JScrollPane();
		jscrolPane.setBorder(null);
		jscrolPane.setOpaque(false);
		jscrolPane.getViewport().setOpaque(false);
		jscrolPane.setViewportView(textarea);
		add(jscrolPane,BorderLayout.CENTER);
		
		
		setVisible(true);
		
		
		final long timeInterval = 2000;  
		
		Runnable runnable = new Runnable(){
			@Override
			public void run(){
				while(true){
					ArrayList<String> logs = LogUnit.getSingleton().readLog();
					for (String s : logs){
						LogPanel.instance().getTextArea().append(s);
						LogPanel.instance().getTextArea().append("\r\n");
					}
					try {
						Thread.sleep(timeInterval);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		new Thread(runnable).start();
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

	public void reflush(){
		ArrayList<String> logs = LogUnit.getSingleton().readLog();
		for (String s : logs){
			LogPanel.instance().getTextArea().append(s);
			LogPanel.instance().getTextArea().append("\r\n");
		}
	}
}
