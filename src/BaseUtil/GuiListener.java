package BaseUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import CompareModule.CompareUnit;
import IOModule.IOUnit;
import TextVectorModule.TextVector;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;


public class GuiListener implements ActionListener 
{

	static JFrame f = new JFrame();
	IOUnit io = new IOUnit();
	CompareUnit cp = new CompareUnit();
    String[] text = null;//存储学生论文的字符串数组
	String[] name = null;//存储学生姓名等信息
	Object[] object=null;//存储学生论文相似的学生信息
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getActionCommand()=="importButton")
		{
			JOptionPane.showMessageDialog(null, "可以一次导入多个word文档，请将文档命名为：学号    姓名    论文题目！");		
			JFileChooser jfc = new JFileChooser();
			jfc.setMultiSelectionEnabled(true);
		
			if (jfc.showOpenDialog(f) == JFileChooser.APPROVE_OPTION) 
			{
				// 这里,弹出个对话框,可以选择要上传的文件,如果选择了,就把选择的文件的绝对路径打印出来,有了绝对路径,通过JTextField的settext就能设置进去了
//				System.out.println(jfc.getSelectedFile().getAbsolutePath());
				File[] f=jfc.getSelectedFiles();
				name=new String[f.length];
				for(int i=0;i<f.length;i++)
				{
					name[i]=f[i].getName();
				}
				String path=jfc.getSelectedFile().getParent();
				System.out.println(path);
				text=new String[f.length];
				io.readWord(path,name);
				JOptionPane.showMessageDialog(null, "文件已读入！");		
			}
		}
		
//		if(e.getActionCommand()=="余弦定理")
//		{
////			new ProgressMonitorExample();
//			try {
//				if(text != null && text.length > 0)
//					object=cp.CalculateSimilarityAlgrithm(text,name);
//			} catch (RowsExceededException e1) {
//				// TODO 自动生成的 catch 块
//				e1.printStackTrace();
//			} catch (WriteException e1) {
//				// TODO 自动生成的 catch 块
//				e1.printStackTrace();
//			} catch (IOException e1) {
//				// TODO 自动生成的 catch 块
//				e1.printStackTrace();
//			}
//			JOptionPane.showMessageDialog(null, "余弦定理算法查重完毕！");	
//		}
			
		if(e.getActionCommand()=="saveButton")
		{
			String filename;

			JFrame f=new JFrame();
			JFileChooser fc=new JFileChooser();
			JTextField tf=new JTextField();
			//设置默认的打开目录,如果不设的话按照window的默认目录(我的文档)
			//fc.setCurrentDirectory(new File("e:/"));

			int Selet=fc.showSaveDialog(f);
			if(Selet==JFileChooser.APPROVE_OPTION)
			{
				File file=fc.getSelectedFile();
				tf.setText("saving"+file.getName());  //获得文件名
				filename=file.getName()+".xls";
				String writePath=fc.getCurrentDirectory().getAbsolutePath()+"\\"+filename; // 文件保存路径

//				try {
//					io.Save(object,writePath);
//				} catch (WriteException e1) {
//					// TODO 自动生成的 catch 块
//					e1.printStackTrace();
//				} catch (IOException e1) {
//					// TODO 自动生成的 catch 块
//					e1.printStackTrace();
//				}
				//程序执行完毕后，出现一个对话框来提示
				JOptionPane.showMessageDialog(null, "查重结果已保存！");	
				System.exit(0);

			}else {
				tf.setText("Save Command cancel by user!");
				System.exit(0);
			}
		}

	}

}
