import java.util.HashMap;
import java.util.Map;


public class Text2Vec {
	private int g_paragraph; //段落
	private String g_sentence;
	private int front,last;
	VectorConsine vec;
	
	public Text2Vec(){
		g_paragraph = 0;
		front = last = 0;
		vec = new VectorConsine();
		g_sentence = "";
	}
	
	private boolean isEndofSentence(char ch){
		return ( ch=='.' || ch=='。' || ch=='、' || ch==';' || ch=='\n');
	}
	
	
	private void nextSentence(String text){
		while(!isEndofSentence(text.charAt(last))){
			++last;
		}
	}
	
	private Map<Object, Integer> nextParagraph(String text){
		Map<Object, Integer> res = new HashMap<Object, Integer>();
		while(last < text.length() && text.charAt(last) !='\n'){
			nextSentence(text);
			g_sentence = text.substring(front,last);
			res = vec.MergeVector(res, vec.buildWordsVector(g_sentence));
			front = last+1;
			last+=1;
		}
		last+=1;
		g_paragraph+=1;
		//存数据库
		return res;
	}
	
	public void start(String text){
		Map<Object, Integer> res = new HashMap<Object, Integer>();
		while(last < text.length()){
			res = vec.MergeVector(res, nextParagraph(text));
		}
		System.out.println(text + "has "+ g_paragraph + "paragraphs and the vec is " + res);
		//存数据库
	}
}
