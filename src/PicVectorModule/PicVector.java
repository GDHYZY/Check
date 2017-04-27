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
	
	int[] m_log = {-1,-1,-1,-1,-1,-1,-1,-1,-1,1073741823,-6,-1,-1,4194303,-2048,-1,-1,131071,-32768,-1,-1,57343,-524288,-1,-1,1042431,-2097152,-1,-1,134213887,-16777216,-1,-1,-1985,-67108857,-1,-1,-1009,-268434945,-1,-1,-509,-1073737729,-1,-1,-255,-2147467265,-1,2147483647,-128,32767,-2,1073741823,-64,1900543,-4,268435455,-32,33554431,-8,134217727,-8,134217702,-32,67108863,-1686110216,536866022,-64,33554431,-1085276162,2147413354,-128,16777215,-1293090817,-1643158,-255,-2139095041,-1158987777,-2135636,-509,-1069547521,-487866369,-5018625,-1009,-534773761,-15489025,-6479873,-1985,-267386881,-301313,-3014657,-3841,-133693441,-76545,-1048577,-7681,-133955585,-10849,-1,-14337,-66977793,-3225,-1,-24577,-33423361,-1289,-1,-1,-16711681,-289,-1,-1,-16744449,-161,-1,-65537,-8372225,-97,-1,-198657,-8372225,-1,-1,-197633,-8380417,-1,-1,-459009,-276819969,-1,-1,-458881,-1612705793,-1,-1,-919753,2146437119,-1,-1,-1968297,1945896959,-1,-1,-1966259,805045247,-1,-1,-4064123,-587332609,-1,-1,-4063429,-251723265,-1,-1,-8126737,-302054913,-1,-1,-8127649,-75529729,16777215,-1006632962,-16515105,-117473025,2097151,-1006632976,-16515073,-207634177,1048575,-1006632992,-16252929,-25182081,262143,-1013710976,-16252929,-35659649,131071,-1014759680,-33031169,-6299521,65535,-1014759936,-33030161,-12586945,32767,-1014760448,-66584657,-25169857,-33521665,-1014760448,-66061313,-7866305,-8372225,-1014761469,-66062271,-1985,-4177921,-1014761465,-66060329,-3672001,-2082801,-1014763505,-66060561,-5506017,-2081777,-1014763489,-133169153,-5506017,-1042417,-1014767585,-133169153,-5243361,-2033,-1014767553,-132120577,-4194785,-4081,-1014767553,-132121089,-1966305,-8161,-1014767553,-132121089,-7471329,31,-1014824960,-132121089,-655457,63,-1014824960,-266338817,-1704049,63,-1014824960,-266338337,-2883617,127,-1014824960,-266338593,-49,255,-1014824960,-266338721,-3014673,1023,-1014824960,-267387201,-2752513,16383,-1014824960,-267386913,-2752529,-1,-1006632961,-268173313,-2752529,-1,-1006632961,-267911169,-1048625,4095,-1010368512,-267386881,-33,1023,-1014824960,-266338369,-4063345,255,-1014824960,-266340417,-225,127,-1014824960,-266338321,-131297,63,-1014824960,-266339393,-8257761,31,-1014824960,-132122557,-262625,31,-1014824960,-132120681,-262625,-8177,-1014775745,-132121113,-993,-4081,-1014767553,-132120593,-13370337,-2033,-1014767553,-132120593,-7342049,-1042417,-1014767585,-132120577,-1050593,-2090993,-1014763489,-132120833,-526273,-2082737,-1014763505,-66060545,-4033,-4177921,-1014763513,-66060545,-4033,-8372225,-1014761469,-66060289,-8065,-33521665,-1014760448,-66061441,-8065,65535,-1014760448,-32505985,-16257,65535,-1014759936,-33030657,-117456641,131071,-1014759680,-33030349,-159416065,262143,-1014759552,-16252937,-142638849,1048575,-1010827296,-16252993,-142671361,4194303,-1006632968,-16252929,-260111873,33554431,-1006632961,-8127489,-805436417,-1,-1,-8126465,-402783233,-1,-1,-3932161,-470022145,-1,-1,-3933185,-537393153,-1,-1,-1967145,-136310785,-1,-1,-1966129,-8384513,-1,-1,-918041,-8380417,-1,-1,-398141,-8372225,-1,-1,-393700,-8372225,-1,-1,-196953,-8355841,-33,-1,-196689,-16711681,-17,-1,-65549,-16711681,-1949,-1,-5,-33423361,-2201,-1,-16385,-66846721,-25281,-1,-28673,-133693441,-103553,-1,-15361,-267386881,-133377,-524289,-7681,-534773761,-1181697,-557057,-3969,-1069547521,-76681217,-1646593,-2017,-2139095041,161931263,-2692793,-1017,16777215,-357105665,-545862,-509,33554431,-382205954,-666694,-256,67108863,-306184196,1073683258,-128,134217727,268435448,268433530,-64,268435455,-16,67108863,-16,536870911,-32,8355839,-8,2147483647,-64,950271,-4,-1,-128,32767,-1,-1,-253,-2147467265,-1,-1,-505,-536866817,-1,-1,-993,-134217217,-1,-1,-1921,-33554429,-1,-1,67105791,-8388608,-1,-1,258047,-2097152,-1,-1,32767,-262144,-1,-1,524287,-16384,-1,-1,16777215,-256,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,9011};
	int[] m_name = {-1,-1,-1,-1,-1,-1,-1,-33423361,-1,-1,-1,-33553409,-1,-1,-1,-65533,-1,-1,536870911,-256,-1,-1,8388607,-2,-1,-1,-33488897,-1,-1,-1,-523489,-1,-1,-249,-4089,-1,-1,536869895,-14400,-1040385,-24577,16776195,-64520,-523265,-1040385,503578369,-7938,-62337,2013274111,-1007149088,32507967,-7268,1006665727,-251674116,-17203185,-912,469893119,-1073709553,1058831879,-2,202375167,2131754944,-2147475520,-1,33554431,65273848,-1048564,-1,-1,100696063,-503565825,-1,-1,-1007157185,2130967807,-8,-1,-520109953,-524161,-2047,-1,-125830145,-8065,-1017,-1,-65011713,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-3073,-1,-1,-1,-31745,-1,-1,-1,-65281,-1,-1,-1,-8065,-1,-114689,-1,-121,-1,-253953,2147483647,-8,-1,1073483775,-256,-1,-1,335414271,-8,-17,-1,-234897153,-1,-49,-1,-15959937,-31745,-245,16383,0,0,-2048,131071,-1040435168,14679071,-4096,-1,-58783999,-8,-1,1073741823,-7872272,-786433,-1,1073741823,-524036,-486014977,-1,-1,-4097,-132120577,-1,-1,-1,-25165825,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,67108863,-2,-1,-1,134217727,-4,-1,-1,134217727,-8,-1,-1,67108863,-16,-1,-1,33554431,-8,-1,-1,16777215,-1,-1,-8257537,-2143289345,-1,-1,-33292289,-535822337,-1,-1,-66846721,-268369921,-1,-1,-33292289,-67106817,-1,-1,-33292289,-33554305,-1,-1,-16515073,-16261113,-1,-1,2143551487,-3932288,-1,-1,63045631,-2031632,-1,-1,-1073676289,-917505,-1,-1,-133693441,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,536870911,-4,-1,-1,536870911,-8,-1,-1,536870911,-16,-1040385,-1,268435455,-8,-260097,-1,134217727,-1,-3969,-1,-469794305,-117440513,-225,-1,1644134911,-264255496,-29,8388607,4190264,2080408056,-60,67108863,521666560,127,-128,-1,-238552833,7,-512,-1,1618081795,-523520,-1,1073741823,-1069776897,-402653185,-1,32767,-25229312,-2013265921,-1,-67092481,-30721,134217727,-4,-1,-31745,536870911,-8,-1,-63489,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1966081,-1,-1,-1,-8323073,-1,-1,-1,-16760833,-1,-1,-1,-4186113,-1,-1,-1,-522241,-1,-1,-1,-130049,-113,-1,-1,-65025,-57,-1,-1,-16129,-15,-1,-1,2147475583,-4,-1,-1,134215711,-1,-1,-1,-1065353697,-1,-58720257,-1,-67076337,-1,33554431,-64,-1048575,-1,134217727,0,-1024,-1,1073741823,-262144,-16,-1,-1,2147483647,-4,-1,-1,536870911,-2,-1,-1,-2080374785,-1,-1,-1,-528482305,-301989889,-1,-1,-132120577,67108863,-512,-1,-58720257,-1,-1023,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-98305,-1,-61,268435455,-4177922,-1,-63,1073741823,-1073479936,2147483647,-64,-1,-532709345,1073741823,-8,-1,-4135997,268435007,-983042,-1,-458751,-2080375281,-983041,1073741823,-125958208,-266338553,-2031617,16777215,-4192512,-67076128,-8372225,-134086689,1014497283,116728,-8388608,-2097145,260176911,-509826,-1,-131041,66846816,-33284224,-1,1073741823,2097148,-2088976,-1,134217727,33046527,-229380,-1,-1056964609,-536903649,-1,-1,2097151,-256,-1,-1,4194303,-16,-1,-1,-1065353217,-1,-1,-1,-1,-1,-1,-1,10878};
	
	//将图片转为统一大小 并取得灰度图
	private BufferedImage getGrayImage(byte[] bytes){
		assert(bytes != null);
		bytes = scale(bytes);
		if (bytes == null)
			return null;
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
	    BufferedImage bufTarget = null;
	    try {
	        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
	        BufferedImage bufferedImage = ImageIO.read(bais);
	        if (bufferedImage == null)
	        	return null;
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
	
	//获取两个数二进制位数不同的个数
		private int countBitDiff(int m, int n) {  
	        int ans = m^n;  
	        int count = 0;  
	        while(ans != 0){  
	            ans &= (ans -1);  
	            count++;  
	        }  
	        return count;  
	    } 
		
		//求图片哈希的汉明距离
		private double getHammingDistance(int[] a, int[] b){
			double hamming = 0.0;
			int len = a.length -1;
			for (int i = 0; i < len ; ++i){
				int num = countBitDiff(a[i], b[i]);
				hamming += num;
			}
			hamming = hamming /(len * Integer.SIZE - Math.min(a[len], b[len]));   // 求汉明距离
			return hamming;
		}
	
	//从doc文档中获取图片
	private ArrayList<int[]> getImageFromDOC(){
		ArrayList<int[]> res = new ArrayList<int[]>();
		int length = m_Doc.characterLength();
		PicturesTable m_PTable = m_Doc.getPicturesTable();
		for (int i = 0; i < length; i++) {
			Range range = new Range(i, i + 1, m_Doc);

			CharacterRun cr = range.getCharacterRun(0);
			if (m_PTable.hasPicture(cr)) {
				Picture pic = m_PTable.extractPicture(cr, false);
				
				//获取灰度图
				byte[] bt = pic.getContent();
				BufferedImage grayImage = getGrayImage(bt);
				if (grayImage == null)
					continue;
				//计算平均灰度值
				double avg = getAverageGray(grayImage); 
				
				//将每个像素点的灰度信息与平均灰度对比后，保存在int的每一位里。 
				ArrayList<Integer> graylist = getGrayList(grayImage, avg);
				
				int[] tmp = new int[graylist.size()];
				for (int l = 0; l < graylist.size(); ++l){
					tmp[l] = graylist.get(l);
				}
				if (getHammingDistance(m_name, tmp) <= 0.05 || getHammingDistance(m_log, tmp) <= 0.05)
					continue;
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
			if (grayImage == null)
				continue;
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
		if (m_Doc != null){		//说明是doc文档
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
	
	public PicVector(HWPFDocument doc, int index, ReportData[] reports){
		assert(doc != null);
		assert(index >= 0);
		assert(reports != null);
		m_Report = reports[index];
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
