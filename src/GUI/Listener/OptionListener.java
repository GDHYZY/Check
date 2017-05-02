package GUI.Listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import GUI.Frame.DataBaseConfigDialog;
import GUI.Frame.InportFilesDialog;
import GUI.Frame.MainPanel;
import GUI.Frame.SaveDataDialog;

public class OptionListener implements ActionListener{
	JButton jbtDataBase;
	JButton jbtInportFiles;
	JButton jbtCheck;
	JButton jbtDataBaseConfig;
	JButton jbtExportReport;
	JButton jbtExit;

	public OptionListener(JButton jbtDataBase, JButton jbtInportFiles,
			JButton jbtCheck, JButton jbtdbConfig, JButton jbtExportReport,
			JButton jbtExit) {
		super();
		this.jbtDataBase = jbtDataBase;
		this.jbtInportFiles = jbtInportFiles;
		this.jbtCheck = jbtCheck;
		this.jbtDataBaseConfig = jbtdbConfig;
		this.jbtExportReport = jbtExportReport;
		this.jbtExit = jbtExit;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == jbtDataBase){
			SaveDataDialog.instance().open();
		}else if(e.getSource() == jbtInportFiles){
			InportFilesDialog.instance().open();
		}else if(e.getSource() == jbtCheck){
			MainPanel.instance().refresh();
		}else if(e.getSource() == jbtDataBaseConfig){
			DataBaseConfigDialog.instance().open();
		}else if(e.getSource() == jbtExportReport){
			
//			LoginFrame.instance().open();
//			MainFrame.instance().dispose();
		}else if(e.getSource() == jbtExit){
			System.exit(0);
		}
	}
}
