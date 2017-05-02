package GUI.Frame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JPanel;


public class BackgroundPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5419654428856923445L;

	public BackgroundPanel() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(0, 0, (int) screenSize.getWidth(),
				(int) screenSize.getHeight());
	}

	public void paint(Graphics g) {
		super.paint(g);
		Dimension screenSize = MainFrame.instance().getContentPane().getSize();
		setBounds(0, 0, (int) screenSize.getWidth(),
				(int) screenSize.getHeight());
		String copyright = "CopyRight 2017";
		g.drawImage(new ImageIcon(this.getClass().getResource("/images/bg.jpg")).getImage(),
				0, 0, getWidth(), getHeight(), this);
		
		g.setFont(new Font("黑体", Font.PLAIN, 16));
		g.setColor(Color.GRAY);
		g.drawString(copyright, 10, getHeight() - 10);
		MainFrame.instance().repaint();
	}
}
