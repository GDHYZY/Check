package GUI.Frame;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel;

import GUI.Listener.OptionListener;


public class OptionPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1613590806361203632L;
	private static OptionPanel optionPanel;
	
	static public OptionPanel instance(){
		if(optionPanel == null)
			optionPanel = new OptionPanel();
		return optionPanel;
	}

	public OptionPanel() {
		setLayout(new FlowLayout(FlowLayout.RIGHT, 30, 10));
		setSize(1366, 150);
		setOpaque(false);
		optionPanel = this;
		ImageButton jbtDataBase = new ImageButton("database");
		ImageButton jbtInportFiles = new ImageButton("inport");
		ImageButton jbtCheck = new ImageButton("check");
		ImageButton jbtConfig = new ImageButton("config");
		ImageButton jbtExportReport = new ImageButton("export");
		ImageButton jbtExit = new ImageButton("exit");

		jbtDataBase.setToolTipText("  保存到数据库  ");
		jbtInportFiles.setToolTipText("  导入文件  ");
		jbtCheck.setToolTipText("  开始检测  ");
		jbtConfig.setToolTipText("  设置  ");
		jbtExportReport.setToolTipText("  导出报告  ");
		jbtExit.setToolTipText("  退出  ");

		jbtDataBase.setPreferredSize(new Dimension(80, 80));
		jbtInportFiles.setPreferredSize(new Dimension(80, 80));
		jbtCheck.setPreferredSize(new Dimension(80, 80));
		jbtConfig.setPreferredSize(new Dimension(80, 80));
		jbtExportReport.setPreferredSize(new Dimension(80, 80));
		jbtExit.setPreferredSize(new Dimension(80, 80));

		OptionListener optionListener = new OptionListener(jbtDataBase,
				jbtInportFiles, jbtCheck, jbtConfig, jbtExportReport, jbtExit);
		jbtDataBase.addActionListener(optionListener);
		jbtInportFiles.addActionListener(optionListener);
		jbtCheck.addActionListener(optionListener);
		jbtConfig.addActionListener(optionListener);
		jbtExportReport.addActionListener(optionListener);
		jbtExit.addActionListener(optionListener);

		add(jbtDataBase);
		add(jbtInportFiles);
		add(jbtCheck);
		add(jbtConfig);
		add(jbtExportReport);
		add(jbtExit);
	}
}
