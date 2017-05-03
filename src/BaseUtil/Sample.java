package BaseUtil;

import java.util.ArrayList;


public class Sample{	
	public ReportData m_Sampledata = null;						//相似报告
	public int m_WordNumber = 0;								//相似词数量
	public double m_Similarity = 0;								//相似度
	public boolean m_PictureSimilarity = false;					//是否有图片相似
	public ArrayList<Integer> m_SimilarityPosList = null;		//相似内容坐标数组
	public ArrayList<SimilarityParagraph> m_ParagraphSimilarity = null;		//相似段落
	
	public Sample(){
		m_SimilarityPosList = new ArrayList<Integer>();
		m_ParagraphSimilarity = new ArrayList<SimilarityParagraph>();
	}
}