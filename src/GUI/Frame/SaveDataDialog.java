package GUI.Frame;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import GUI.Listener.SaveDataListener;

public class SaveDataDialog extends JDialog {
	/**
	 * SaveDataDialog
	 */
	private static final long serialVersionUID = -2103628463004265908L;
	private static SaveDataDialog saveDataDialog;

	public static SaveDataDialog instance() {
		if (saveDataDialog == null)
			saveDataDialog = new SaveDataDialog();
		return saveDataDialog;
	}

	public SaveDataDialog() {
		super(MainFrame.instance(), "数据库管理", true);
		setLayout(null);
		setResizable(false);
		setSize(400, 200);

		saveDataDialog = this;

		JLabel jlbLable = new JLabel("是否将无重复文件存入数据库?");
		JButton ensure = new JButton("确定");
		JButton cancel = new JButton("取消");

		jlbLable.setBounds(80, 25, 200, 35);
		ensure.setBounds(100, 100, 90, 40);
		cancel.setBounds(210, 100, 90, 40);

		SaveDataListener bookRoomListener = new SaveDataListener(cancel);
		ensure.addActionListener(bookRoomListener);
		cancel.addActionListener(bookRoomListener);

		add(jlbLable);
		add(ensure);
		add(cancel);
		setLocationRelativeTo(null);
	}

	public void open() {
		setVisible(true);
	}
}