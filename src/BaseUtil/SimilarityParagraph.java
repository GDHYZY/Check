package BaseUtil;

import java.util.ArrayList;

public class SimilarityParagraph {
	public int m_TargetParagraph = 0;							//待测报告相似段落
	public int m_SampleParagraph = 0;							//相似报告相似段落
	public int m_WordNumber = 0;								//相似段落中的相同词个数
	public double m_Similarity = 0;								//相似段落相似度
	public ArrayList<String> m_SimilarityText = null;			//相似内容数组
	public ArrayList<Integer> m_SimilarityposList = null;		//相似内容坐标数组
	
	public SimilarityParagraph(){
		m_SimilarityposList = new ArrayList<Integer>();
		m_SimilarityText = new ArrayList<String>();
	}
}
