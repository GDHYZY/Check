package BaseUtil;

import java.util.ArrayList;

public class ExportData {
	
	public class Sample{
	
		public class SimilarityParagraph{
			public int m_TargetParagraph = 0;
			public int m_SampleParagraph = 0;
			public double m_Similarity = 0;
			public ArrayList<String> m_SimilarityText = null;
			
			public SimilarityParagraph(){
				m_SimilarityText = new ArrayList<String>();
			}
		}
		
		public ReportData m_Sampledata = null;
		public double m_Similarity = 0;
		public boolean m_PictureSimilarity = false;
		public ArrayList<SimilarityParagraph> m_ParagraphSimilarity = null;
		
		public Sample(){
			m_ParagraphSimilarity = new ArrayList<SimilarityParagraph>();
		}
	}
	
	public ReportData m_Target = null;
	public double m_Similarity = 0;
	public ArrayList<Sample> m_Sample = null;
	
	public ExportData(){
		m_Sample = new ArrayList<Sample>();
	}
}
