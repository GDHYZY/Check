package TextVectorModule;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import BaseUtil.ReportData;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;


public class TextVector implements Runnable {
	private int m_Paragraph = 0; //段落
	private int m_Front = 0, m_Last = 0;
	private String m_Sentence = null;
	private String m_Doc = "";
	private ReportData m_Reporte = null;
	private int m_Level = 100;
	
	//判断无意义字符
	private boolean isMeanless(char ch) {
		return (ch == ' ' || ch == '\t' || ch == '\n' || ch == '.' || ch == '。'
				|| ch == '、' || ch == ';' || ch == ',' || ch == '，'
				|| ch == '\t' || ch == '\r' || ch=='\'');
	}

	// 下一段
	private void nextSentence(String text) {
		while (!isEndofSentence(text.charAt(m_Last))) {
			++m_Last;
		}
	}

	// 判断句子结束
	private boolean isEndofSentence(char ch) {
		return (ch == '.' || ch == '。' || ch == '、' || ch == ';' || ch == '\n');
	}
	
	//合并两个向量
	public Map<String, Integer> MergeVector(Map<String, Integer> vc1,Map<String, Integer> vc2){
		if(vc1 == null)
			return vc2;
		else if(vc2 == null){
			return vc1;
		}
		else{
			Map<String,Integer> res = new HashMap<String, Integer>();
			Iterator<Map.Entry<String, Integer>> it = vc1.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry<String, Integer> entry = it.next();
				String key = entry.getKey();
				int value = entry.getValue();
				if(vc2.get(key) != null){
					value += vc2.get(key);
				}
				res.put(key, value);
			}
			it = vc2.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry<String, Integer> entry = it.next();
				String key = entry.getKey();
				int value = entry.getValue();
				if(vc1.get(key) != null){
					continue;
				}
				res.put(key, value);
			}
			return res;
		}
	}
	
	private boolean isEndofParagraph(char ch){
		return (ch == '\n' || ch == '\r' || ch=='\t');
	}
	
	private void _nextParagraph (String text) {
		while (m_Front < text.length() && isEndofParagraph(text.charAt(m_Front))) {
			m_Front++;
			m_Last = m_Front;
		}
		while (m_Last < text.length() && !isEndofParagraph(text.charAt(m_Last))) {
			++m_Last;
		}
	}
	
	//构建段落向量余弦
	private Map<String, Integer> nextParagraph(String text){
		Map<String, Integer> res = new HashMap<String, Integer>();	
		if (m_Last < text.length()){
			int begin = m_Front; 
			m_Sentence = "";
			while (m_Last < text.length() && m_Sentence.length() < m_Level){
				_nextParagraph(text);	
				m_Sentence += text.substring(m_Front,m_Last).trim();
				++m_Last;	
				m_Front = m_Last;
			}
			res = MergeVector(res, _BuildWordsVector(m_Sentence));
			m_Paragraph+=1;
			m_Reporte.ParagraphHash.put(m_Paragraph, res.toString());
			m_Reporte.ParagraphNum = m_Paragraph;	
			m_Reporte.ParagraphMsg.put(m_Paragraph,new int[]{begin, m_Last-1});
		}
		return res;
	}
	
	//建立文档的词频空间向量
	private Map<String, Integer> _BuildWordsVector(String doc){
		Map<String, Integer> WordsVector = null;
		if(doc != null && doc.trim().length() > 0){
			WordsVector = new HashMap<String, Integer>();
			StringReader sr = new StringReader(doc);
			
			IKSegmenter ik = new IKSegmenter(sr, true);
			Lexeme lex = null;
			try {
				while ((lex = ik.next())!= null){
					String s = lex.getLexemeText();
					Integer iv = WordsVector.get(s);
					if (iv != null){
						iv += 1;
					} else {
						iv = 1;
					}
					WordsVector.put(s, iv);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ik = null;
			lex = null;
		}
		return WordsVector;
	}
	
	public TextVector(){
	}
	
	public TextVector(String doc, int index, ReportData[] reports){
		assert(! doc.isEmpty());
		assert(index >= 0);
		assert(reports != null);
		m_Reporte = reports[index];
		m_Doc = doc;
		m_Paragraph = 0;
		m_Front = m_Last = 0;
		m_Sentence = "";
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub		
		Map<String, Integer> res = new HashMap<String, Integer>();
		while(m_Last < m_Doc.length()){
			res = MergeVector(res, nextParagraph(m_Doc));
		}
		m_Reporte.TextHash = res.toString();
	}
}
