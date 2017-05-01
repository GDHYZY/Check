package GUI.Frame;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import listener.BookRoomListener;
import dao.ConfigDao;
import dao.RoomTypeDao;
import entity.RoomType;

public class DataBaseDialog extends JDialog {
	/**
	 * DataBaseDialog
	 */
	private static final long serialVersionUID = -2103628463004265908L;
	private static DataBaseDialog dataBaseDialog;

	public static DataBaseDialog instance() {
		if (dataBaseDialog == null)
			dataBaseDialog = new DataBaseDialog();
		return dataBaseDialog;
	}

	public DataBaseDialog() {
		super(MainFrame.instance(), "数据库管理", true);
		setLayout(null);
		setResizable(false);
		setSize(400, 200);

		dataBaseDialog = this;

		JLabel jlbLable = new JLabel("是否将无重复文件存入数据库?");
		JButton ensure = new JButton("确定");
		JButton cancel = new JButton("取消");

		jlbLable.setBounds(80, 25, 90, 35);
		ensure.setBounds(100, 100, 90, 40);
		cancel.setBounds(210, 100, 90, 40);

		BookRoomListener bookRoomListener = new BookRoomListener(jcbType,
				jtfPhoneNumber, jtfDuration, jcbTake, cancel);
		ensure.addActionListener(bookRoomListener);
		cancel.addActionListener(bookRoomListener);
		jtfPhoneNumber.addActionListener(bookRoomListener);

		add(jlbLable);
		add(ensure);
		add(cancel);
		setLocationRelativeTo(null);
	}

	public void open() {
		jtfPhoneNumber.setText("");
		jtfDuration.setText(String.valueOf(ConfigDao.instance().getConfig()
				.getHoursTaken()));
		jcbType.removeAllItems();
		for (RoomType item : RoomTypeDao.instance().getRoomTypeList()) {
			jcbType.addItem(item);
		}
		jcbTake.setSelected(false);
		setVisible(true);
	}
}