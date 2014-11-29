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
	private TrainingDataManager tdm;//训练集管理器
    //private String trainnigDataPath;//训练集路径
    private static double zoomFactor = 10.0D;
    
    private static String[] traningFileClassifications;//训练语料分类集合
    //private static String defaultPath = "E:\\java_Eclipse\\NBM2\\Reduced";E:\java_Eclipse\语料\搜狗语料训练文档
    private static String defaultPath = "E:\\java_Eclipse\\语料\\搜狗语料训练文档";
    private static File traningTextDir;//训练语料存放目录
    private static IndexHashMap[] indexHashMaps;
    private static HashMap<String,Integer> class_text_count = new HashMap<String,Integer>();
    private static double numofalltext = 0.0d;
    private static int V = 0;//记录训练集中所有词的种类数
    private static double N = 0.0d;//记录训练集一共有几个词
    private static HashMap<String,Integer> word_num_of_classHashMap = new HashMap<String,Integer>();
    
	public static void install(){
		traningTextDir = new File(defaultPath);
        if (!traningTextDir.isDirectory()) 
        {
            throw new IllegalArgumentException("训练语料库搜索失败！ [" +defaultPath + "]");
        }
        traningFileClassifications = traningTextDir.list();//罗列出训练集的所有类别
	}
    
    public static void installhashmap() throws IOException{
    	indexHashMaps = new IndexHashMap[traningFileClassifications.length];
    	NlpirMethod.Nlpir_init();
    	for(int i = 0; i < traningFileClassifications.length; i++){
    		
    		indexHashMaps[i] = new IndexHashMap();
    		int temp_num_word = 0;
    		
    		indexHashMaps[i].classname = traningFileClassifications[i];//存储类名
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
            word_num_of_classHashMap.put(traningFileClassifications[i],temp_num_word);//记录下这个类一共有多少词
    	}
    	
    	for(int i = 0; i < traningFileClassifications.length; i++){
    		V = V + indexHashMaps[i].hashMap.size();//所有训练集的词的种类数
    	}
    	
    }
    public BayesClassifier() throws IOException //--------------------------------------在贝叶斯分类器类的构造函数中初始化管理训练集类的实例
    {
        tdm =new TrainingDataManager();
    }
    
    public static String[] DropStopWords(String[] oldWords)//---------------------------------------去除停用词
    {
        Vector<String> v1 = new Vector<String>();
        for(int i=0;i<oldWords.length;++i)
        {
            if(StopWordsHandler.IsStopWord(oldWords[i])==false)
            {//不是停用词
                v1.add(oldWords[i]);
            }
        }
        String[] newWords = new String[v1.size()];//将vector集合类转化成字符串数组以便后续操作
        v1.toArray(newWords);
        return newWords;
    }
    
    double calcProd(String[] X, String Cj) throws IOException//------------------------------计算给定的文本属性向量terms在给定的分类Ci中的分类条件概率
    {
    	double ncv = word_num_of_classHashMap.get(Cj);
        double ret = 0.0D;
        // 类条件概率连乘
        
       // ClassConditionalProbability temp = new ClassConditionalProbability();      
        for (int i = 0; i <X.length; i++)//这篇文章有X.length个词~即此片文章的文本特征向量的维度为X.length维
        {
            String Xi = X[i];
            double nc = 0.0d;
            //因为结果过小~因此在连乘之前放大15倍~以此提高准确度~这对最终结果并无影响~因为我们只是比较概率的大小
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
            /*//ret *=((double)(temp.calculatePxc(Xi, Cj)))*zoomFactor;//有X.length个分词~一个词一个词进行处理~即连乘*/
        }
        // 再乘以先验概率
        System.out.println(ret+"********************************");
        ret =ret + Math.log(word_num_of_classHashMap.get(Cj)/N);
        //PriorProbability temp_priorprobability = new PriorProbability();
        //ret *= temp_priorprobability.calculatePc(Cj);
        System.out.println(ret);
        return ret;
    }
    
    @SuppressWarnings("unchecked")
    public String classify(String text) throws IOException//----------------------------------------对所给文章text进行分类
    {
    	NlpirMethod.Nlpir_init();
        String[] terms = null;
        terms= NlpirMethod.NLPIR_ParagraphProcess(text, 0).split(" ");//中文分词处理
                                                                      //(NlpirMethod.NLPIR_ParagraphProcess(sSrc, 0)返回分词后的字符串
                                                                      //通过.split(" ")将其存入字符串数组以便后续操作）
        terms = DropStopWords(terms);//去掉停用词，以免影响分类

        String[] Classes = tdm.getTraningClassifications();//获得并存储训练集中的所有类别以便后续操作
        double probility = 0.0D;
        //System.out.println("走到这一步");
        
        List<ClassifyResult> crs = new ArrayList<ClassifyResult>();//分类结果~在ClassifyResult类中定义所需的数据类型~利用泛型存储此类型数据以便输出结果
        for (int i = 0; i <Classes.length; i++)
        {
            String Ci = Classes[i];//第i个分类(for循环每次处理所给文章到一个类训练集中的类的分类条件概率~一共有Classes.length个类)
            //System.out.println("走到这一步");
            probility = calcProd(terms, Ci);//计算给定的文本属性向量terms在给定的分类Ci中的分类条件概率
            //System.out.println("走到这一步");
            //保存分类结果
            ClassifyResult cr = new ClassifyResult();
            cr.classification = Ci;//分类
            cr.probility = probility;//关键字在分类的条件概率
            System.out.println("In process.");
            System.out.println(Ci + "：" + probility);
            crs.add(cr);
        }
        //对最后概率结果进行排序
        java.util.Collections.sort(crs,new Comparator()//重载集合类的排序函数对所得分类条件概率进行排序以得到最大概率的类别
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
        //返回概率最大的分类
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
    	/*String sSrc="微软公司提出以446亿美元的价格收购雅虎中国网2月1日报道 美联社消息，"
    			+ "微软公司提出以446亿美元现金加股票的价格收购搜索网站雅虎公司。微软提出"
    			+ "以每股31美元的价格收购雅虎。微软的收购报价较雅虎1月31日的收盘价19.18"
    			+ "美元溢价62%。微软公司称雅虎公司的股东可以选择以现金或股票进行交易。微软"
    			+ "和雅虎公司在2006年底和2007年初已在寻求双方合作。而近两年，雅虎一直处"
    			+ "于困境：市场份额下滑、运营业绩不佳、股价大幅下跌。对于力图在互联网市场有"
    			+ "所作为的微软来说，收购雅虎无疑是一条捷径，因为双方具有非常强的互补性。(小桥)";

    	BayesClassifier classifier = new BayesClassifier();//构造Bayes分类器
        String result = classifier.classify(sSrc);//进行分类
        System.out.println("此项属于["+result+"]");*/
        
		double num_text = 13410.0d;
		double num_t = 0.0D;
    	//File file = new File("E:\\java_Eclipse\\SogouC.reduced\\Reduced");
		File file = new File("E:\\java_Eclipse\\语料\\搜狗语料测试文档");
    	String []filepathStrings = file.list();
    	
    	for(int i = 0; i <filepathStrings.length; i++){
    		//File file2 = new File("E:\\java_Eclipse\\SogouC.reduced\\Reduced\\" + filepathStrings[i]);
    		File file2 = new File("E:\\java_Eclipse\\语料\\搜狗语料测试文档\\" + filepathStrings[i]);
    		String []filepathStrings2 = file2.list();
    		for(int j = 0; j < filepathStrings2.length; j++){
    			//InputStreamReader isReader =new InputStreamReader(new FileInputStream("E:\\java_Eclipse\\SogouC.reduced\\Reduced\\"+filepathStrings[i] + "\\" + filepathStrings2[j]),"GBK");
    			InputStreamReader isReader =new InputStreamReader(new FileInputStream("E:\\java_Eclipse\\语料\\搜狗语料测试文档\\"+filepathStrings[i] + "\\" + filepathStrings2[j]),"GBK");
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
    			BayesClassifier classifier = new BayesClassifier();//构造Bayes分类器
    			String result = classifier.classify(sSrc);//进行分类
    			System.out.println("待分类文本" + filepathStrings[i] + "此项属于["+result+"]");
    			if(filepathStrings[i].equals("IT")){
    				if(result.equals("IT"))
    					num_t++;
    				}
    			else if(filepathStrings[i].equals("财经")){
    				if(result.equals("财经"))
    					num_t++;
    				}
    			else if(filepathStrings[i].equals("健康")){
    				if(result.equals("健康"))
    					num_t++;
    				}
    			else if(filepathStrings[i].equals("教育")){
    				if(result.equals("教育"))
    					num_t++;
    				}
    			else if(filepathStrings[i].equals("军事")){
    				if(result.equals("军事"))
    					num_t++;
    				}
    			else if(filepathStrings[i].equals("旅游")){
    				if(result.equals("旅游"))
    					num_t++;
    				}
    			else if(filepathStrings[i].equals("体育")){
    				if(result.equals("体育"))
    					num_t++;
    				}
               else if(filepathStrings[i].equals("文化")){
            	   if(result.equals("文化"))
            		   num_t++;
            	   }
               else if(filepathStrings[i].equals("招聘")){
            	   if(result.equals("招聘"))
            		   num_t++;
            	   }
    			}
    		}
    	double accuracy = num_t/num_text;
    	System.out.println("正确率为：" + accuracy);
    	System.out.println(num_text);
		
	}
}
