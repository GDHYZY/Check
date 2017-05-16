package GUI.Frame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import BaseUtil.LogUnit;

public class ProgressMonitorBar extends JPanel implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static ProgressMonitor pbar;
	static String Things = "";
	static int Number = 0;
	static int Begin = 0;
	static int Now = 0;
	Timer timer;
	
	public ProgressMonitorBar(String s, int n) {
		Things = s;
		Number = n;
		Begin = LogUnit.getSingleton().getLogNumber();
		Now = 0;
		timer = null;
		pbar = null;
		
		pbar = new ProgressMonitor(this, "请稍后",
				s+". . .", 0, n);
		// Fire a timer every once in a while to update the progress

		timer = new Timer(800, this);
		timer.start();
	}
	
	

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		SwingUtilities.invokeLater(new Update());
	}

	class Update implements Runnable {
		public void run() {
			if (Now >= Number){
				timer.stop();
				JOptionPane.showMessageDialog(null, Things+"完毕！");		
				MainPanel.instance().refresh();
			}
			int number = LogUnit.getSingleton().getLogNumber();
			Now = number - Begin;
			pbar.setProgress(Now);
			pbar.setNote(Things+"..."+String.format("%.2f", 1.0*Now/Number*100)+"%");
		}
	}
	
}
