package GUI.Listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import frame.BookRoomDialog;
import frame.ConfigDialog;
import frame.LoginFrame;
import frame.MainFrame;
import frame.MainPanel;
import frame.TakeRoomDialog;

public class OptionListener implements ActionListener{
	JButton jbtDataBase;
	JButton jbtInportFiles;
	JButton jbtCheck;
	JButton jbtConfig;
	JButton jbtExportReport;
	JButton jbtExit;

	public OptionListener(JButton jbtDataBase, JButton jbtInportFiles,
			JButton jbtCheck, JButton jbtConfig, JButton jbtExportReport,
			JButton jbtExit) {
		super();
		this.jbtDataBase = jbtDataBase;
		this.jbtInportFiles = jbtInportFiles;
		this.jbtCheck = jbtCheck;
		this.jbtConfig = jbtConfig;
		this.jbtExportReport = jbtExportReport;
		this.jbtExit = jbtExit;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == jbtDataBase){
			BookRoomDialog.instance().open();
		}else if(e.getSource() == jbtInportFiles){
			TakeRoomDialog.instance().open();
		}else if(e.getSource() == jbtCheck){
			MainPanel.instance().refresh();
		}else if(e.getSource() == jbtConfig){
			ConfigDialog.instance().open();
		}else if(e.getSource() == jbtExportReport){
			LoginFrame.instance().open();
			MainFrame.instance().dispose();
		}else if(e.getSource() == jbtExit){
			System.exit(0);
		}
	}
}
