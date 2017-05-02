package GUI.Listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;

import jxl.write.WriteException;
import GUI.Frame.CheckDialog;
import GUI.Frame.DataBaseConfigDialog;
import GUI.Frame.InportFilesDialog;
import GUI.Frame.MainPanel;
import GUI.Frame.SaveDataDialog;
import IOModule.IOUnit;

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
			CheckDialog.instance().open();
		}else if(e.getSource() == jbtDataBaseConfig){
			DataBaseConfigDialog.instance().open();
		}else if(e.getSource() == jbtExportReport){
			try {
				new IOUnit().Save();
			} catch (WriteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
//			LoginFrame.instance().open();
//			MainFrame.instance().dispose();
		}else if(e.getSource() == jbtExit){
			System.exit(0);
		}
	}
}
