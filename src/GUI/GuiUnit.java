package GUI;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;


public class GuiUnit {

	JRadioButton rabut1,rabut2;
	JPanel panelup,paneldown,panelmiddle;
	JFrame mFrame;
	GuiListener action = new GuiListener();
	
	public void Show() {
	    	  mFrame=new JFrame();	
	    	  mFrame.getContentPane().setLayout(null);
	    	  mFrame.setTitle("报告查重系统"); 
	    	 
	    	  
	    	  JMenuBar menubar=new JMenuBar();
	    	  mFrame.setJMenuBar(menubar);
	    	  
	    	  JMenu menu=new JMenu("操作");
	    	  menubar.add(menu);
  
	    	  Component horizontalStrut = Box.createHorizontalStrut(20);
	    	  horizontalStrut.setBounds(75, 136, 20, 1);
	    	  mFrame.getContentPane().add(horizontalStrut);
	    	  
	    	  JLabel label = new JLabel("报告查重系统");
	    	  label.setFont(new Font("TimesRoman", Font.PLAIN, 21));
	    	  label.setForeground(Color.MAGENTA);
	    	  label.setBackground(Color.YELLOW);
	    	  label.setBounds(60, 10, 315, 58);
	    	  mFrame.getContentPane().add(label);
	    	  
	    	  JLabel label_1 = new JLabel("选择算法");
	    	  label_1.setBounds(20, 147, 110, 15);
	    	  mFrame.getContentPane().add(label_1);
	    	  	    	  
	    	  final JRadioButton radioButton_1 = new JRadioButton("余弦定理");
	    	  radioButton_1.setActionCommand("余弦定理");
	    	  //radioButton_1.addActionListener(action);
	    	  radioButton_1.setBounds(200, 143, 100, 23);
	    	  mFrame.getContentPane().add(radioButton_1);
	    	  
	   	  
	    	  
	    	  ButtonGroup group = new ButtonGroup();  	
	    	  group.add(radioButton_1);
	    	  
	    	  JLabel label_2 = new JLabel("上传文件");
	    	  label_2.setBounds(37, 69, 70, 15);
	    	  mFrame.getContentPane().add(label_2);
	    	  
	    	  final JButton button = new JButton("Upload");
	    	  button.setForeground(Color.BLACK);
	    	  button.setBounds(152, 65, 81, 36);
	    	  button.setActionCommand("importButton");
	    	 // button.addActionListener(action);
	    	  mFrame.getContentPane().add(button);
	    	  
	    	  JLabel label_3 = new JLabel("保存结果");
	    	  label_3.setBounds(41, 218, 81, 15);
	    	  mFrame.getContentPane().add(label_3);
	    	  
	    	  final JButton button_1 = new JButton("Save");
	    	  button_1.setForeground(Color.BLACK);
	    	  button_1.setBounds(152, 201, 81, 36);
	    	  button_1.setActionCommand("saveButton");
	    	 // button_1.addActionListener(action);
	    	  mFrame.getContentPane().add(button_1); 
	    	  
//	    	  JMenuItem menuItem1=new JMenuItem("报告查重");
//	    	  menuItem1.addActionListener(new ActionListener() {
//				@Override
//				public void actionPerformed(ActionEvent arg0) {
					
//					JOptionPane.showMessageDialog(null, "可选择多个文件导入");	
					 button.addActionListener(action);   
					 radioButton_1.addActionListener(action);
					 button_1.addActionListener(action);
//				}
//			});
	    	  
//	    	  menu.add(menuItem1);
	    	
	    	  mFrame.setSize(400,300); 
	    	  mFrame.setVisible(true);
	    	  mFrame.setResizable(false);
	    	  mFrame.setDefaultCloseOperation(mFrame.EXIT_ON_CLOSE);
	    
	}
}
