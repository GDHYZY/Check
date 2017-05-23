package GUI.Listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import jxl.write.WriteException;
import BaseUtil.GlobalData;
import GUI.Frame.CheckDialog;
import GUI.Frame.DataBaseConfigDialog;
import GUI.Frame.InportFilesDialog;
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
			if (GlobalData.getSingleton().m_ExportData==null || GlobalData.getSingleton().m_ExportData.isEmpty()){
				JOptionPane.showMessageDialog(null, "未进行检测");
				return;
			}	
			JFrame f = new JFrame();
			JFileChooser jfc = new JFileChooser();
			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (jfc.showSaveDialog(f) == JFileChooser.APPROVE_OPTION) 
			{
				File file =jfc.getSelectedFile();
				String path = file.getPath();
				try {
					new IOUnit().Export(path);
				} catch (WriteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				JOptionPane.showMessageDialog(null, "导出检测结果成功");
			} else{
				return;
			}
		}else if(e.getSource() == jbtExit){
			System.exit(0);
		}
	}
}
