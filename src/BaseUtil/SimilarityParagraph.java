package BaseUtil;

import java.util.ArrayList;

public class SimilarityParagraph {
	public int m_TargetParagraph = 0;
	public int m_SampleParagraph = 0;
	public int m_WordNumber = 0;
	public double m_Similarity = 0;
	public ArrayList<String> m_SimilarityText = null;
	public ArrayList<Integer> m_SimilarityposList = null;
	
	public SimilarityParagraph(){
		m_SimilarityposList = new ArrayList<Integer>();
		m_SimilarityText = new ArrayList<String>();
	}
}
