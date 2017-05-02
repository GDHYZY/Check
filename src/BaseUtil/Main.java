package BaseUtil;

import java.awt.Font;
import java.util.Enumeration;

import javax.swing.UIManager;

import GUI.Frame.LoginDialog;
import GUI.Frame.MainFrame;


public class Main {
	public static void main(String[] args) {
		// 设置默认字体
		Font font = new Font("微软雅黑",Font.PLAIN, 14);
		Enumeration<Object> keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof javax.swing.plaf.FontUIResource)
				UIManager.put(key, font);
		}
		try {
			Thread td = new Thread(new LoginDialog());
			td.start();
			td.wait();				
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
		}
		new Thread(new MainFrame()).start();
	}
}
