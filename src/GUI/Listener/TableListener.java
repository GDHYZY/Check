package GUI.Listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.table.DefaultTableModel;
import javax.xml.bind.annotation.XmlElementDecl.GLOBAL;

import jxl.write.WriteException;
import BaseUtil.ExportData;
import BaseUtil.GlobalData;
import BaseUtil.ReportData;
import GUI.Frame.BackgroundPanel;
import GUI.Frame.DataBaseConfigDialog;
import GUI.Frame.DataTable;
import GUI.Frame.MainFrame;
import GUI.Frame.MainPanel;
import IOModule.IOUnit;


public class TableListener extends MouseAdapter implements ActionListener {
	private DataTable table;
	private JPopupMenu menu;

	public TableListener(DataTable table, JPopupMenu menu){
		this.table = table;
		this.menu = menu;
	}
	
	public TableListener(JPopupMenu menu) {
		this.table = MainPanel.instance().getTable();
		this.menu = menu;
	}

	public void mousePressed(MouseEvent e) {
		if (table.getSelectedRow() < 0) {
			int modifiers = e.getModifiers();
			modifiers -= MouseEvent.BUTTON3_MASK;
			modifiers |= MouseEvent.BUTTON1_MASK;
			MouseEvent ne = new MouseEvent(e.getComponent(), e.getID(),
					e.getWhen(), modifiers, e.getX(), e.getY(),
					e.getClickCount(), false);
			table.dispatchEvent(ne);
		}
	}

	public void mouseReleased(MouseEvent e) {
		if ((e.getModifiers() & MouseEvent.BUTTON3_MASK) != 0
				&& !e.isControlDown() && !e.isShiftDown()) {
			menu.show(table, e.getX(), e.getY());
		}
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		if (table.getSelectedRow() < 0)
			return;
		String strAction = ((JMenuItem) e.getSource()).getText().trim();
		if (!isSure("<html>您确定要进行<b><font size=6> " + strAction
				+ " </font></b>操作吗？"))
			return;
		
		if (strAction.equals("导出检测结果")){
			String name = getSelectedValue(0);
			ArrayList<ExportData> eplist = GlobalData.getSingleton().m_ExportData;
			if (eplist == null || eplist.isEmpty()){
				JOptionPane.showMessageDialog(null, "未进行检测");
				return;
			}
			for (ExportData ep : eplist){
				if (ep.m_Target.Title == name){
					JFrame f = new JFrame();
					JFileChooser jfc = new JFileChooser();
					if (jfc.showOpenDialog(f) == JFileChooser.APPROVE_OPTION) 
					{
						File file =jfc.getSelectedFile();
						String Name = file.getName();
						String path=jfc.getSelectedFile().getParent();
						path += "\\"+Name;
						new IOUnit().Export(ep, path);
					} else{
						return;
					}
					break;
				}
			}
		} else if (strAction.equals("删除报告")){
			for (int row : table.getSelectedRows()) {
				ReportData rd = null;
				String title = getValue(row, 0);
				for (ReportData r : GlobalData.getSingleton().m_InputData){
					if (r.Title.equals(title)){
						rd = r;
						GlobalData.getSingleton().m_InputData.remove(rd);
						break;
					}
				}
				for (ExportData exd : GlobalData.getSingleton().m_ExportData){
					if (exd.m_Target == rd){
						GlobalData.getSingleton().m_ExportData.remove(e);
					}
				}
			}
			removeSelectedRows();
		} else if (strAction.equals("删除项目")){
			for (int row : table.getSelectedRows()) {
				GlobalData.getSingleton().m_DataBase.DeleteReportItem(getValue(row, 0));
			}
			removeSelectedRows();
		} else if (strAction.equals("连接数据库")){
			String name = getSelectedValue(0);
			GlobalData.getSingleton().m_DataBase.CreateandConnectDataBase(name);
			DataBaseConfigDialog.instance().dispose();
			MainFrame.instance().setTitle("报告查重系统-"+name);
		} else if (strAction.equals("删除数据库")){
			GlobalData.getSingleton().m_DataBase.InitDataBases();
			for (int row : table.getSelectedRows()) {
				GlobalData.getSingleton().m_DataBase.DeleteOneDataBase(getValue(row, 0));
			}
			removeSelectedRows();
		} else if (strAction.equals("清空数据库")){
			GlobalData.getSingleton().m_DataBase.InitDataBases();
			GlobalData.getSingleton().m_DataBase.ClearDataBase();
			DataBaseConfigDialog.instance().open();
		} 
		JOptionPane.showMessageDialog(null, strAction + "成功！");
	}

	private boolean isSure(String msg) {
		return (JOptionPane.showConfirmDialog(null, msg, "消息",
				JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION);
	}

	private String getValue(int row, int column) {
		DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
		return tableModel.getValueAt(row, column).toString();
	}

	private String getSelectedValue(int column) {
		if (table.getSelectedRow() < 0)
			return null;
		DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
		return tableModel.getValueAt(table.getSelectedRow(), column).toString();
	}

	private void removeSelectedRows() {
		if (table.getSelectedRow() < 0)
			return;
		DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
		boolean isEndSelect = false;
		if (table.getSelectedRows()[table.getSelectedRows().length - 1] == tableModel
				.getRowCount() - 1) {
			isEndSelect = true;
		}
		while (table.getSelectedRow() >= 0) {
			tableModel.removeRow(table.getSelectedRow());
		}
		if (isEndSelect && tableModel.getRowCount() > 0) {
			tableModel.removeRow(tableModel.getRowCount() - 1);
		}
		if (tableModel.getRowCount() <= 0) {
			MainPanel.instance().refresh();
		}
	}
	
	private void removeSelectedRow() {
		DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
		tableModel.removeRow(table.getSelectedRow());
	}

	
}
