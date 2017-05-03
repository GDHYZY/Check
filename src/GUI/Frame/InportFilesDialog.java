package GUI.Frame;

import java.awt.Font;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import IOModule.IOUnit;

public class InportFilesDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1116388016863724023L;
	private static InportFilesDialog inportFilesDialog;

	public static InportFilesDialog instance() {
		if (inportFilesDialog == null)
			inportFilesDialog = new InportFilesDialog();
		return inportFilesDialog;
	}

	public InportFilesDialog() {
		super(MainFrame.instance(), "导入文件", true);
		setLayout(null);
		setResizable(false);
//		setSize(400, 300);
		setLocationRelativeTo(null);
		setVisible(false);
		inportFilesDialog = this;
	}

	public void open() {
		
		JFrame f = new JFrame();
		JFileChooser jfc = new JFileChooser();
		jfc.setMultiSelectionEnabled(true);
		if (jfc.showOpenDialog(f) == JFileChooser.APPROVE_OPTION) 
		{
			File[] file =jfc.getSelectedFiles();
			String[] name = new String[file.length];
			for(int i=0;i<file.length;i++)
			{
				name[i]=file[i].getName();
			}
			String path=jfc.getSelectedFile().getParent();
			System.out.println(path);
			new IOUnit().readWord(path,name);
			JOptionPane.showMessageDialog(null, "文件已读入！");		
		}
		MainPanel.instance().refresh();
	}
}
