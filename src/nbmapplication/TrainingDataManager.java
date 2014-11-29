package nbmapplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TrainingDataManager {
	private String[] traningFileClassifications;//训练语料分类集合
    private File traningTextDir;//训练语料存放目录
    //private static String defaultPath = "D:\\TrainningSet";E:\\java_Eclipse\\NBM\\Sample;E:\java_Eclipse\NBM\Reduced
    private static String defaultPath = "E:\\java_Eclipse\\NBM\\Reduced";
    private static String[] alltext;
    public TrainingDataManager() throws IOException 
    {
        traningTextDir = new File(defaultPath);
        if (!traningTextDir.isDirectory()) 
        {
            throw new IllegalArgumentException("训练语料库搜索失败！ [" +defaultPath + "]");
        }
        this.traningFileClassifications = traningTextDir.list();//罗列出训练集的所有类别
        
        //alltext = gettext();
    }
    
    public String[] gettext() throws IOException{
    	String [] temp_alltext = new String[getTrainingFileCount()];
    	String []traningclassifiStrings = getTraningClassifications();

    	int num = 0;
    	for(int i = 0; i < traningclassifiStrings.length; i++){
    		File classDir = new File(traningTextDir.getPath() +File.separator +traningclassifiStrings[i]);
            String[] ret = classDir.list();

            for(int j = 0; j < ret.length; j++){
            	InputStreamReader isReader =new InputStreamReader(new FileInputStream(traningTextDir.getPath() + File.separator +traningclassifiStrings[i] + File.separator + ret[j]),"GBK");
                BufferedReader reader = new BufferedReader(isReader);
                String aline;
                StringBuilder sb = new StringBuilder();
                while ((aline = reader.readLine()) != null)
                {
                    sb.append(aline + " ");
                }
                isReader.close();
                reader.close();
                temp_alltext[num] = sb.toString();
                num++;
            }
    	}
    	return temp_alltext;
    }
    
    public String[] getalltext(){
    	return this.alltext;
    }
    
    public String[] getTraningClassifications() 
    {
        return this.traningFileClassifications;
    }
    /**
    * 根据训练文本类别返回这个类别下的所有训练文本路径（full path）
    * @param classification 给定的分类
    * @return 给定分类下所有文件的路径（full path）
    */
    public String[] getFilesPath(String classification) 
    {
        File classDir = new File(traningTextDir.getPath() +File.separator +classification);
        String[] ret = classDir.list();
        for (int i = 0; i < ret.length; i++) 
        {
            ret[i] = traningTextDir.getPath() +File.separator +classification +File.separator +ret[i];
        }
        return ret;
    }
    /**
    * 返回给定路径的文本文件内容
    * @param filePath 给定的文本文件路径
    * @return 文本内容
    * @throws java.io.FileNotFoundException
    * @throws java.io.IOException
    */
    /*public static []String getText(String[] filePath) throws FileNotFoundException,IOException 
    {
    	String [] all_text = new String[filePath.length];
        InputStreamReader isReader =new InputStreamReader(new FileInputStream(filePath),"GBK");
        BufferedReader reader = new BufferedReader(isReader);
        String aline;
        StringBuilder sb = new StringBuilder();
        while ((aline = reader.readLine()) != null)
        {
            sb.append(aline + " ");
        }
        isReader.close();
        reader.close();
        return sb.toString();
    }*/
    /**
    * 返回训练文本集中所有的文本数目
    * @return 训练文本集中所有的文本数目
    */
    public int getTrainingFileCount()
    {
        int ret = 0;
        for (int i = 0; i < traningFileClassifications.length; i++)
        {
            ret +=getTrainingFileCountOfClassification(traningFileClassifications[i]);
        }
        return ret;
    }
    /**
    * 返回训练文本集中在给定分类下的训练文本数目
    * @param classification 给定的分类
    * @return 训练文本集中在给定分类下的训练文本数目
    */
    public int getTrainingFileCountOfClassification(String classification)
    {
        File classDir = new File(traningTextDir.getPath() +File.separator +classification);
        return classDir.list().length;
    }
    /**
    * 返回给定分类中包含关键字／词的训练文本的数目
    * @param classification 给定的分类
    * @param key 给定的关键字／词
    * @return 给定分类中包含关键字／词的训练文本的数目
    */
    public int getCountContainKeyOfClassification(String classification,String key) throws FileNotFoundException, IOException 
    {
    	int index_begin = 0;
    	int index_end = 0;
    	if(classification.equals("IT")){
    		index_begin = 0;
    		index_end = 500;
    		}
    	else if(classification.equals("体育")){
    		index_begin = 500;
    		index_end = 1000;
    	}
    	else if(classification.equals("健康")){
    		index_begin = 1000;
    		index_end = 1500;
    	}
    	else if(classification.equals("军事")){
    		index_begin = 1500;
    		index_end = 2000;
    	}
    	else if(classification.equals("招聘")){
    		index_begin = 2000;
    		index_end = 2500;
    	}
    	else if(classification.equals("教育")){
    		index_begin = 2500;
    		index_end = 3000;
    	}
    	else if(classification.equals("文化")){
    		index_begin = 3000;
    		index_end = 3500;
    	}
    	else if(classification.equals("旅游")){
    		index_begin = 3500;
    		index_end = 4000;
    	}
    	else if(classification.equals("财经")){
    		index_begin = 4000;
    		index_end = 4500;
    	}
        int ret = 0;
        String [] text = getalltext();
       /* System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        
        	System.out.println("#-"+1+"-#"+text[0]);
        	System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        	System.out.println();
        	
        
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");*/
        //System.out.println(text[501]);
        //System.out.println(text.length);
        //System.out.println(index_begin+" "+index_end);
		for (int j = index_begin; j < index_end; j++) 
		{
		    if (text[j].contains(key)) 
		    {
		        ret++;
		    }
		}
        return ret;
    }
}
