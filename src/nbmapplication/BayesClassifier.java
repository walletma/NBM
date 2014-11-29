package nbmapplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

public class BayesClassifier {
	private TrainingDataManager tdm;//ѵ����������
    //private String trainnigDataPath;//ѵ����·��
    private static double zoomFactor = 10.0D;
    
    private static String[] traningFileClassifications;//ѵ�����Ϸ��༯��
    //private static String defaultPath = "E:\\java_Eclipse\\NBM2\\Reduced";E:\java_Eclipse\����\�ѹ�����ѵ���ĵ�
    private static String defaultPath = "E:\\java_Eclipse\\����\\�ѹ�����ѵ���ĵ�";
    private static File traningTextDir;//ѵ�����ϴ��Ŀ¼
    private static IndexHashMap[] indexHashMaps;
    private static HashMap<String,Integer> class_text_count = new HashMap<String,Integer>();
    private static double numofalltext = 0.0d;
    private static int V = 0;//��¼ѵ���������дʵ�������
    private static double N = 0.0d;//��¼ѵ����һ���м�����
    private static HashMap<String,Integer> word_num_of_classHashMap = new HashMap<String,Integer>();
    
	public static void install(){
		traningTextDir = new File(defaultPath);
        if (!traningTextDir.isDirectory()) 
        {
            throw new IllegalArgumentException("ѵ�����Ͽ�����ʧ�ܣ� [" +defaultPath + "]");
        }
        traningFileClassifications = traningTextDir.list();//���г�ѵ�������������
	}
    
    public static void installhashmap() throws IOException{
    	indexHashMaps = new IndexHashMap[traningFileClassifications.length];
    	NlpirMethod.Nlpir_init();
    	for(int i = 0; i < traningFileClassifications.length; i++){
    		
    		indexHashMaps[i] = new IndexHashMap();
    		int temp_num_word = 0;
    		
    		indexHashMaps[i].classname = traningFileClassifications[i];//�洢����
    		indexHashMaps[i].hashMap = new HashMap<String,Integer>();
    		
    		File classDir = new File(traningTextDir.getPath() +File.separator +traningFileClassifications[i]);
            String[] ret = classDir.list();
            numofalltext = numofalltext + ret.length;
            class_text_count.put(traningFileClassifications[i], ret.length);
            for(int j = 0; j < ret.length; j++){
            	InputStreamReader isReader =new InputStreamReader(new FileInputStream(traningTextDir.getPath() + File.separator +traningFileClassifications[i] + File.separator + ret[j]),"GBK");
                BufferedReader reader = new BufferedReader(isReader);
                String aline;
                StringBuilder sb = new StringBuilder();
                while ((aline = reader.readLine()) != null)
                {
                    sb.append(aline + " ");
                }
                isReader.close();
                reader.close();
                
                
                String []temp = NlpirMethod.NLPIR_ParagraphProcess(sb.toString(), 0).split(" ");
                
                
                System.out.println(sb.toString());
                System.out.println(traningFileClassifications[i]);
                temp = DropStopWords(temp);
                temp_num_word = temp_num_word + temp.length;
                for(int ii = 0; ii < temp.length; ii++){
                	if(indexHashMaps[i].hashMap.get(temp[ii]) == null){
                		indexHashMaps[i].hashMap.put(temp[ii], 1);
                	}
                	else{
                		int tem = indexHashMaps[i].hashMap.get(temp[ii]);
                		tem = tem + 1;
                		indexHashMaps[i].hashMap.put(temp[ii],tem);	
                	}           	
                }
            }
            N = N + temp_num_word;
            word_num_of_classHashMap.put(traningFileClassifications[i],temp_num_word);//��¼�������һ���ж��ٴ�
    	}
    	
    	for(int i = 0; i < traningFileClassifications.length; i++){
    		V = V + indexHashMaps[i].hashMap.size();//����ѵ�����Ĵʵ�������
    	}
    	
    }
    public BayesClassifier() throws IOException //--------------------------------------�ڱ�Ҷ˹��������Ĺ��캯���г�ʼ������ѵ�������ʵ��
    {
        tdm =new TrainingDataManager();
    }
    
    public static String[] DropStopWords(String[] oldWords)//---------------------------------------ȥ��ͣ�ô�
    {
        Vector<String> v1 = new Vector<String>();
        for(int i=0;i<oldWords.length;++i)
        {
            if(StopWordsHandler.IsStopWord(oldWords[i])==false)
            {//����ͣ�ô�
                v1.add(oldWords[i]);
            }
        }
        String[] newWords = new String[v1.size()];//��vector������ת�����ַ��������Ա��������
        v1.toArray(newWords);
        return newWords;
    }
    
    double calcProd(String[] X, String Cj) throws IOException//------------------------------����������ı���������terms�ڸ����ķ���Ci�еķ�����������
    {
    	double ncv = word_num_of_classHashMap.get(Cj);
        double ret = 0.0D;
        // ��������������
        
       // ClassConditionalProbability temp = new ClassConditionalProbability();      
        for (int i = 0; i <X.length; i++)//��ƪ������X.length����~����Ƭ���µ��ı�����������ά��ΪX.lengthά
        {
            String Xi = X[i];
            double nc = 0.0d;
            //��Ϊ�����С~���������֮ǰ�Ŵ�15��~�Դ����׼ȷ��~������ս������Ӱ��~��Ϊ����ֻ�ǱȽϸ��ʵĴ�С
           // System.out.println(i+"^^^^^^^^^^"+ret);
            //System.out.println("zheli");
            for(int ii = 0; ii < indexHashMaps.length; ii++){
            	if(indexHashMaps[ii].classname.equals(Cj) ){
            		
            		if(indexHashMaps[ii].hashMap.get(Xi) == null){
            			nc = 1.0d;
            		}
            		else nc = indexHashMaps[ii].hashMap.get(Xi)+1.0d;
            		
            		ret =ret + Math.log(nc/(ncv + V ));
            	}
            }
            /*//ret *=((double)(temp.calculatePxc(Xi, Cj)))*zoomFactor;//��X.length���ִ�~һ����һ���ʽ��д���~������*/
        }
        // �ٳ����������
        System.out.println(ret+"********************************");
        ret =ret + Math.log(word_num_of_classHashMap.get(Cj)/N);
        //PriorProbability temp_priorprobability = new PriorProbability();
        //ret *= temp_priorprobability.calculatePc(Cj);
        System.out.println(ret);
        return ret;
    }
    
    @SuppressWarnings("unchecked")
    public String classify(String text) throws IOException//----------------------------------------����������text���з���
    {
    	NlpirMethod.Nlpir_init();
        String[] terms = null;
        terms= NlpirMethod.NLPIR_ParagraphProcess(text, 0).split(" ");//���ķִʴ���
                                                                      //(NlpirMethod.NLPIR_ParagraphProcess(sSrc, 0)���طִʺ���ַ���
                                                                      //ͨ��.split(" ")��������ַ��������Ա����������
        terms = DropStopWords(terms);//ȥ��ͣ�ôʣ�����Ӱ�����

        String[] Classes = tdm.getTraningClassifications();//��ò��洢ѵ�����е���������Ա��������
        double probility = 0.0D;
        //System.out.println("�ߵ���һ��");
        
        List<ClassifyResult> crs = new ArrayList<ClassifyResult>();//������~��ClassifyResult���ж����������������~���÷��ʹ洢�����������Ա�������
        for (int i = 0; i <Classes.length; i++)
        {
            String Ci = Classes[i];//��i������(forѭ��ÿ�δ����������µ�һ����ѵ�����е���ķ�����������~һ����Classes.length����)
            //System.out.println("�ߵ���һ��");
            probility = calcProd(terms, Ci);//����������ı���������terms�ڸ����ķ���Ci�еķ�����������
            //System.out.println("�ߵ���һ��");
            //���������
            ClassifyResult cr = new ClassifyResult();
            cr.classification = Ci;//����
            cr.probility = probility;//�ؼ����ڷ������������
            System.out.println("In process.");
            System.out.println(Ci + "��" + probility);
            crs.add(cr);
        }
        //�������ʽ����������
        java.util.Collections.sort(crs,new Comparator()//���ؼ�����������������÷����������ʽ��������Եõ������ʵ����
        {
            public int compare(final Object o1,final Object o2)
            {
                final ClassifyResult m1 = (ClassifyResult) o1;
                final ClassifyResult m2 = (ClassifyResult) o2;
                final double ret = m1.probility - m2.probility;
                if (ret < 0)
                {
                    return 1;
                } 
                else
                {
                    return -1;
                }
            }
        });
        //���ظ������ķ���
        return crs.get(0).classification;
    }
    
	public static void main(String[]args) throws IOException{
		install();
		installhashmap();
		/*for(int i = 0; i < indexHashMaps.length; i++){
			System.out.println("==============================================================================================");
			System.out.println(indexHashMaps[i].classname);
			System.out.println(indexHashMaps[i].hashMap);
		}*/
		//System.exit(0);
    	/*String sSrc="΢��˾�����446����Ԫ�ļ۸��չ��Ż��й���2��1�ձ��� ��������Ϣ��"
    			+ "΢��˾�����446����Ԫ�ֽ�ӹ�Ʊ�ļ۸��չ�������վ�Ż���˾��΢�����"
    			+ "��ÿ��31��Ԫ�ļ۸��չ��Ż���΢����չ����۽��Ż�1��31�յ����̼�19.18"
    			+ "��Ԫ���62%��΢��˾���Ż���˾�Ĺɶ�����ѡ�����ֽ���Ʊ���н��ס�΢��"
    			+ "���Ż���˾��2006��׺�2007�������Ѱ��˫���������������꣬�Ż�һֱ��"
    			+ "���������г��ݶ��»�����Ӫҵ�����ѡ��ɼ۴���µ���������ͼ�ڻ������г���"
    			+ "����Ϊ��΢����˵���չ��Ż�������һ���ݾ�����Ϊ˫�����зǳ�ǿ�Ļ����ԡ�(С��)";

    	BayesClassifier classifier = new BayesClassifier();//����Bayes������
        String result = classifier.classify(sSrc);//���з���
        System.out.println("��������["+result+"]");*/
        
		double num_text = 13410.0d;
		double num_t = 0.0D;
    	//File file = new File("E:\\java_Eclipse\\SogouC.reduced\\Reduced");
		File file = new File("E:\\java_Eclipse\\����\\�ѹ����ϲ����ĵ�");
    	String []filepathStrings = file.list();
    	
    	for(int i = 0; i <filepathStrings.length; i++){
    		//File file2 = new File("E:\\java_Eclipse\\SogouC.reduced\\Reduced\\" + filepathStrings[i]);
    		File file2 = new File("E:\\java_Eclipse\\����\\�ѹ����ϲ����ĵ�\\" + filepathStrings[i]);
    		String []filepathStrings2 = file2.list();
    		for(int j = 0; j < filepathStrings2.length; j++){
    			//InputStreamReader isReader =new InputStreamReader(new FileInputStream("E:\\java_Eclipse\\SogouC.reduced\\Reduced\\"+filepathStrings[i] + "\\" + filepathStrings2[j]),"GBK");
    			InputStreamReader isReader =new InputStreamReader(new FileInputStream("E:\\java_Eclipse\\����\\�ѹ����ϲ����ĵ�\\"+filepathStrings[i] + "\\" + filepathStrings2[j]),"GBK");
    			BufferedReader reader = new BufferedReader(isReader);
    			String aline;
    			StringBuilder sb = new StringBuilder();
    			while ((aline = reader.readLine()) != null)
    			{
    				sb.append(aline + " ");
    				}
    			isReader.close();
    			reader.close();
    			String sSrc = sb.toString();
    			BayesClassifier classifier = new BayesClassifier();//����Bayes������
    			String result = classifier.classify(sSrc);//���з���
    			System.out.println("�������ı�" + filepathStrings[i] + "��������["+result+"]");
    			if(filepathStrings[i].equals("IT")){
    				if(result.equals("IT"))
    					num_t++;
    				}
    			else if(filepathStrings[i].equals("�ƾ�")){
    				if(result.equals("�ƾ�"))
    					num_t++;
    				}
    			else if(filepathStrings[i].equals("����")){
    				if(result.equals("����"))
    					num_t++;
    				}
    			else if(filepathStrings[i].equals("����")){
    				if(result.equals("����"))
    					num_t++;
    				}
    			else if(filepathStrings[i].equals("����")){
    				if(result.equals("����"))
    					num_t++;
    				}
    			else if(filepathStrings[i].equals("����")){
    				if(result.equals("����"))
    					num_t++;
    				}
    			else if(filepathStrings[i].equals("����")){
    				if(result.equals("����"))
    					num_t++;
    				}
               else if(filepathStrings[i].equals("�Ļ�")){
            	   if(result.equals("�Ļ�"))
            		   num_t++;
            	   }
               else if(filepathStrings[i].equals("��Ƹ")){
            	   if(result.equals("��Ƹ"))
            		   num_t++;
            	   }
    			}
    		}
    	double accuracy = num_t/num_text;
    	System.out.println("��ȷ��Ϊ��" + accuracy);
    	System.out.println(num_text);
		
	}
}
