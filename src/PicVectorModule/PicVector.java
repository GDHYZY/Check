package PicVectorModule;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.PicturesTable;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;

import BaseUtil.ReportData;

public class PicVector implements Runnable{
	private double m_Width = 128;
	private double m_Heigh = 128;
	private ReportData m_Report = null;
	private List<XWPFPictureData> m_XWPFPictureDatas = null;
	private HWPFDocument m_Doc = null;
	private PicturesTable m_PTable = null;
	
	//将图片转为统一大小 并取得灰度图
	private BufferedImage getGrayImage(byte[] bytes){
		assert(bytes != null);
		bytes = scale(bytes);
		ByteArrayInputStream inn = new ByteArrayInputStream(bytes);  
		BufferedImage image = null;
		BufferedImage grayImage = null;
		try {
			image = ImageIO.read(inn);		//读取统一大小后的图像流
			grayImage = grayImage(image);	//获取灰度图
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return grayImage;
	}
	
	//将图片转换成 m_Width * m_Heigh 的统一大小
	private byte[] scale(byte[] bytes) {
	    BufferedImage bufferedImage = null;
	    BufferedImage bufTarget = null;
	    try {
	        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
	        bufferedImage = ImageIO.read(bais);
	        double sx =  m_Width / bufferedImage.getWidth();
	        double sy =  m_Heigh / bufferedImage.getHeight();
	        int type = bufferedImage.getType();
	        if (type == BufferedImage.TYPE_CUSTOM) {
	            ColorModel cm = bufferedImage.getColorModel();
	            WritableRaster raster = cm.createCompatibleWritableRaster((int)m_Width, (int)m_Heigh);
	            boolean alphaPremultiplied = cm.isAlphaPremultiplied();
	            bufTarget = new BufferedImage(cm, raster, alphaPremultiplied, null);
	        } else {
	            bufTarget = new BufferedImage((int)m_Width, (int)m_Heigh, type);
	        }
	        Graphics2D g = bufTarget.createGraphics();
	        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	        g.drawRenderedImage(bufferedImage, AffineTransform.getScaleInstance(sx, sy));
	        g.dispose();
	         
	        if(bufTarget != null){
	            ByteArrayOutputStream baos = new ByteArrayOutputStream();
	            ImageIO.write(bufTarget, "jpeg", baos);
	            byte[] result = baos.toByteArray();
	            return result;
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
	private int colorToRGB(int alpha, int red, int green, int blue) {
		int newPixel = 0;
		newPixel += alpha;
		newPixel = newPixel << 8;
		newPixel += red;
		newPixel = newPixel << 8;
		newPixel += green;
		newPixel = newPixel << 8;
		newPixel += blue;
		return newPixel;
	}
	
	//转换成将原图转换为灰度图
	private BufferedImage grayImage(BufferedImage bufferedImage) throws Exception {
		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();
		
		BufferedImage grayBufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				// 计算灰度值
				final int color = bufferedImage.getRGB(x, y);
				final int r = (color >> 16) & 0xff;
				final int g = (color >> 8) & 0xff;
				final int b = color & 0xff;
				int gray = (int) (0.3 * r + 0.59 * g + 0.11 * b);
				int newPixel = colorToRGB(255, gray, gray, gray);
				grayBufferedImage.setRGB(x, y, newPixel);
			}
		}
		return grayBufferedImage;
	}
	
	//求平均灰度
	private double getAverageGray(BufferedImage grayImage){
		double avg = 0.0;
		for(int j= 0 ; j < m_Width ; j++){  
	        for(int k = 0 ; k < m_Heigh; k++){  
	        	int rgb = grayImage.getRGB(j, k);
	        	avg += rgb;
	        }  
	    }  
		avg /= m_Width * m_Heigh;	
		return avg;
	}
	
	//根据平均灰度，将图片信息存入到int数组中，每个像素占一个int的一个位
	ArrayList<Integer> getGrayList(BufferedImage grayImage, double avg){
		ArrayList<Integer> graylist = new ArrayList<Integer>();
		int key = 0;
		int val = 0;
		int lease = 0;		//用来排除掉白色的像素点,存到数组的最后
		for(int j= 0 ; j < m_Width ; j++){  
	        for(int k = 0 ; k < m_Heigh; k++){  
	        	int rgb = grayImage.getRGB(j, k);
	        	if (rgb==-1){
	        		++lease;
	        	}
	        	if (rgb > avg){
	        		val |= (1 << key);
	        	}
	        	if (++key >= Integer.SIZE){
        			key = 0;
        			graylist.add(val);
        			val = 0;
        		}
	        }  
	    }
		graylist.add(lease);
		return graylist.isEmpty() ? null : graylist;
	}
	
	//从doc文档中获取图片
	private ArrayList<int[]> getImageFromDOC(){
		ArrayList<int[]> res = new ArrayList<int[]>();
		int length = m_Doc.characterLength();
		for (int i = 0; i < length; i++) {
			Range range = new Range(i, i + 1, m_Doc);

			CharacterRun cr = range.getCharacterRun(0);
			if (m_PTable.hasPicture(cr)) {
				Picture pic = m_PTable.extractPicture(cr, false);
				
				//获取灰度图
				byte[] bt = pic.getContent();
				BufferedImage grayImage = getGrayImage(bt);
				
				//计算平均灰度值
				double avg = getAverageGray(grayImage); 
				
				//将每个像素点的灰度信息与平均灰度对比后，保存在int的每一位里。 
				ArrayList<Integer> graylist = getGrayList(grayImage, avg);
				
				int[] tmp = new int[graylist.size()];
				for (int l = 0; l < graylist.size(); ++l){
					tmp[l] = graylist.get(l);
				}
				res.add(tmp);
			}
		}
		return res.isEmpty() ? null : res;
	}
	
	//从docx文档中获取图片
	private ArrayList<int[]> getImageFromDOCX() {
		ArrayList<int[]> res = new ArrayList<int[]>();
		for (XWPFPictureData pic : m_XWPFPictureDatas) {
			// 获取灰度图
			byte[] bt = pic.getData();
			BufferedImage grayImage = getGrayImage(bt);

			// 计算平均灰度值
			double avg = getAverageGray(grayImage);

			// 将每个像素点的灰度信息与平均灰度对比后，保存在int的每一位里。
			ArrayList<Integer> graylist = getGrayList(grayImage, avg);

			int[] tmp = new int[graylist.size()];
			for (int l = 0; l < graylist.size(); ++l) {
				tmp[l] = graylist.get(l);
			}
			res.add(tmp);
		}
		return res.isEmpty() ? null : res;
	}
	
	private ArrayList<int[]> getPictureHash(){
		ArrayList<int[]> res = null;
		if (m_PTable != null){		//说明是doc文档
			res = getImageFromDOC();
		}
		if(m_XWPFPictureDatas != null){ //说明是docx文档
			res = getImageFromDOCX();
		}
		return res;
	}
	
	public PicVector() {
		// TODO Auto-generated constructor stub
	}
	
	public PicVector(HWPFDocument doc, int index, ReportData[] reports, PicturesTable ptable){
		assert(doc != null);
		assert(index >= 0);
		assert(reports != null);
		m_Report = reports[index];
		m_PTable = ptable;
		m_Doc = doc;
	}
	
	public PicVector(int index, ReportData[] reports, List<XWPFPictureData> picList){
		assert(index >= 0);
		assert(reports != null);
		m_Report = reports[index];
		m_XWPFPictureDatas = picList;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		ArrayList<int[]> pichash = getPictureHash();
		m_Report.PicHash = m_Report.toPicHashString(pichash);
	}
}
