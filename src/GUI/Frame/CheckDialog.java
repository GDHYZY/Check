package GUI.Frame;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import GUI.Listener.CheckListener;

public class CheckDialog extends JDialog{
	/**
	 * 
	 */
	private static final long serialVersionUID = -111638321863724023L;
	private static CheckDialog checkdialog;
	private JCheckBox jcbPic;
	
	public static CheckDialog instance() {
		if (checkdialog == null)
			checkdialog = new CheckDialog();
		return checkdialog;
	}

	public CheckDialog() {
		super(MainFrame.instance(), "准备检测", true);
		checkdialog = this;
		setLayout(null);
		setResizable(false);
		setSize(250, 250);
		
		jcbPic = new JCheckBox("有图检测");
		JButton ensure = new JButton("确定");
		JButton cancel = new JButton("取消");
		
		jcbPic.setBounds(30, 50, 120, 35);
		ensure.setBounds(30, 150, 70, 40);
		cancel.setBounds(150, 150, 70, 40);
		
		add(jcbPic);
		add(ensure);
		add(cancel);
		
		CheckListener checklistener = new CheckListener(jcbPic, ensure, cancel);
		ensure.addActionListener(checklistener);
		cancel.addActionListener(checklistener);
		
		setLocationRelativeTo(null);
	}

	public void open() {
		setVisible(true);
	}
}
