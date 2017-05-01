package GUI.Frame;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import listener.TableListener;
import dao.BookingDao;
import dao.FoodDao;
import dao.RoomDao;
import dao.RoomTypeDao;

public class MainPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4090036947302273308L;
	private static MainPanel mainPanel;
	private DataTable table;
	private JScrollPane jscrolPane;
	private String dataType;

	public static MainPanel instance() {
		if (mainPanel == null)
			mainPanel = new MainPanel();
		return mainPanel;
	}

	public MainPanel() {
		mainPanel = this;
		setOpaque(false);
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(0, 0, 20, 10));
		table = new DataTable(null, null);
		jscrolPane = new JScrollPane();
		jscrolPane.setBorder(null);
		jscrolPane.setOpaque(false);
		jscrolPane.getViewport().setOpaque(false);
		add(jscrolPane);
		showRoomsBookedData();
	}

	@Override
	public void paint(Graphics g) {
		ImageIcon icon = new ImageIcon(this.getClass().getResource(
				"/images/scrollpane.png"));
		Image img = icon.getImage();
		g.drawImage(img, jscrolPane.getX(), jscrolPane.getY(),
				jscrolPane.getWidth(), jscrolPane.getHeight(), this);
		super.paint(g);
	}

	public DataTable getTable() {
		return table;
	}

	public void showAllRoomsData() {
		dataType = "AllRooms";
		Object[] head = { "����", "��������", "����¥��", "����۸�", "���ӷ���" };
		showData(RoomDao.instance().getRoomsData(), head);
		JMenuItem add = new JMenuItem("    ��ӷ���    ");
		JMenuItem addRooms = new JMenuItem("    �������    ");
		JMenuItem delete = new JMenuItem("    ɾ������    ");
		JPopupMenu menu = new JPopupMenu();
		menu.add(add);
		menu.add(addRooms);
		menu.add(delete);
		TableListener tableListener = new TableListener(menu);
		table.addMouseListener(tableListener);
		table.addMouseMotionListener(tableListener);
		add.addActionListener(tableListener);
		addRooms.addActionListener(tableListener);
		delete.addActionListener(tableListener);
	}

	public void showExpiredRoomsData() {
		dataType = "ExpiredRooms";
		Object[] head = { "����", "��������", "��������", "��ʼʱ��", "����ʱ��" };
		showData(RoomDao.instance().getExpiredRoomsData(), head);
		JPopupMenu menu = new JPopupMenu();
		JMenuItem checkout = new JMenuItem("    �������    ");
		JMenuItem addHours = new JMenuItem("    �������    ");
		menu.add(checkout);
		menu.add(addHours);
		TableListener tableListener = new TableListener(menu);
		table.addMouseListener(tableListener);
		table.addMouseMotionListener(tableListener);
		checkout.addActionListener(tableListener);
		addHours.addActionListener(tableListener);
	}

	public void showRoomTypesData() {
		dataType = "RoomTypes";
		Object[] head = { "ID", "��������", "����۸�", "���ӷ���" };
		showData(RoomTypeDao.instance().getRoomTypesData(), head);
		JMenuItem add = new JMenuItem("    �������    ");
		JMenuItem edit = new JMenuItem("    �༭����    ");
		JMenuItem delete = new JMenuItem("    ɾ������    ");
		JPopupMenu menu = new JPopupMenu();
		menu.add(add);
		menu.add(edit);
		menu.add(delete);
		TableListener tableListener = new TableListener(menu);
		table.addMouseListener(tableListener);
		table.addMouseMotionListener(tableListener);
		add.addActionListener(tableListener);
		edit.addActionListener(tableListener);
		delete.addActionListener(tableListener);
	}

	public void showRoomsBookedData() {
		dataType = "RoomsBooked";
		Object[] head = { "����", "��������", "�绰����", "Ԥ��ʱ��", "Ԥ��ʱ��" };
		showData(BookingDao.instance().getBookingsData(), head);
		JMenuItem take = new JMenuItem("    ��ͨ����    ");
		JMenuItem delete = new JMenuItem("    ɾ������    ");
		JPopupMenu menu = new JPopupMenu();
		menu.add(take);
		menu.add(delete);
		TableListener tableListener = new TableListener(menu);
		table.addMouseListener(tableListener);
		table.addMouseMotionListener(tableListener);
		take.addActionListener(tableListener);
		delete.addActionListener(tableListener);
	}

	public void showRoomTakenData() {
		dataType = "RoomsTaken";
		Object[] head = { "����", "��������", "��������", "��ʼʱ��", "����ʱ��" };
		showData(RoomDao.instance().getRoomsTakenData(), head);
		JPopupMenu menu = new JPopupMenu();
		JMenuItem buyFood = new JMenuItem("    ��Ӿ�ˮ    ");
		JMenuItem checkout = new JMenuItem("    �������    ");
		JMenuItem addHours = new JMenuItem("    �������    ");
		menu.add(buyFood);
		menu.add(checkout);
		menu.add(addHours);
		TableListener tableListener = new TableListener(menu);
		table.addMouseListener(tableListener);
		table.addMouseMotionListener(tableListener);
		buyFood.addActionListener(tableListener);
		checkout.addActionListener(tableListener);
		addHours.addActionListener(tableListener);
	}

	public void showFoodsData() {
		dataType = "FoodsData";
		Object[] head = { "ID", "ʳƷ����", "ʳƷ����", "ʳƷ����", "ʣ������" };
		showData(FoodDao.instance().getFoodsData(), head);
		JPopupMenu menu = new JPopupMenu();
		JMenuItem checkout = new JMenuItem("    ���ʳƷ    ");
		JMenuItem addHours = new JMenuItem("    ɾ��ʳƷ    ");
		menu.add(checkout);
		menu.add(addHours);
		TableListener tableListener = new TableListener(menu);
		table.addMouseListener(tableListener);
		table.addMouseMotionListener(tableListener);
		checkout.addActionListener(tableListener);
		addHours.addActionListener(tableListener);
	}

	public void showData(Object[][] data, Object[] head) {
		table.removeAll();
		table = new DataTable(data, head);
		jscrolPane.setViewportView(table);
	}

	public void refresh() {
		if (dataType.equals("AllRooms")) {
			showAllRoomsData();
		} else if (dataType.equals("RoomTypes")) {
			showRoomTypesData();
		} else if (dataType.equals("RoomsBooked")) {
			showRoomsBookedData();
		} else if (dataType.equals("RoomsTaken")) {
			showRoomTakenData();
		} else if (dataType.equals("ExpiredRooms")) {
			showExpiredRoomsData();
		} else if (dataType.equals("FoodsData")) {
			showFoodsData();
		}
	}
}
