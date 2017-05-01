package BaseUtil;

import java.util.ArrayList;


public class Sample{	
	public ReportData m_Sampledata = null;
	public int m_WordNumber = 0;
	public double m_Similarity = 0;
	public boolean m_PictureSimilarity = false;
	public ArrayList<Integer> m_SimilarityPosList = null;
	public ArrayList<SimilarityParagraph> m_ParagraphSimilarity = null;
	
	public Sample(){
		m_SimilarityPosList = new ArrayList<Integer>();
		m_ParagraphSimilarity = new ArrayList<SimilarityParagraph>();
	}
}