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
	private String[] traningFileClassifications;//ѵ�����Ϸ��༯��
    private File traningTextDir;//ѵ�����ϴ��Ŀ¼
    //private static String defaultPath = "D:\\TrainningSet";E:\\java_Eclipse\\NBM\\Sample;E:\java_Eclipse\NBM\Reduced
    private static String defaultPath = "E:\\java_Eclipse\\NBM\\Reduced";
    private static String[] alltext;
    public TrainingDataManager() throws IOException 
    {
        traningTextDir = new File(defaultPath);
        if (!traningTextDir.isDirectory()) 
        {
            throw new IllegalArgumentException("ѵ�����Ͽ�����ʧ�ܣ� [" +defaultPath + "]");
        }
        this.traningFileClassifications = traningTextDir.list();//���г�ѵ�������������
        
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
    * ����ѵ���ı���𷵻��������µ�����ѵ���ı�·����full path��
    * @param classification �����ķ���
    * @return ���������������ļ���·����full path��
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
    * ���ظ���·�����ı��ļ�����
    * @param filePath �������ı��ļ�·��
    * @return �ı�����
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
    * ����ѵ���ı��������е��ı���Ŀ
    * @return ѵ���ı��������е��ı���Ŀ
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
    * ����ѵ���ı������ڸ��������µ�ѵ���ı���Ŀ
    * @param classification �����ķ���
    * @return ѵ���ı������ڸ��������µ�ѵ���ı���Ŀ
    */
    public int getTrainingFileCountOfClassification(String classification)
    {
        File classDir = new File(traningTextDir.getPath() +File.separator +classification);
        return classDir.list().length;
    }
    /**
    * ���ظ��������а����ؼ��֣��ʵ�ѵ���ı�����Ŀ
    * @param classification �����ķ���
    * @param key �����Ĺؼ��֣���
    * @return ���������а����ؼ��֣��ʵ�ѵ���ı�����Ŀ
    */
    public int getCountContainKeyOfClassification(String classification,String key) throws FileNotFoundException, IOException 
    {
    	int index_begin = 0;
    	int index_end = 0;
    	if(classification.equals("IT")){
    		index_begin = 0;
    		index_end = 500;
    		}
    	else if(classification.equals("����")){
    		index_begin = 500;
    		index_end = 1000;
    	}
    	else if(classification.equals("����")){
    		index_begin = 1000;
    		index_end = 1500;
    	}
    	else if(classification.equals("����")){
    		index_begin = 1500;
    		index_end = 2000;
    	}
    	else if(classification.equals("��Ƹ")){
    		index_begin = 2000;
    		index_end = 2500;
    	}
    	else if(classification.equals("����")){
    		index_begin = 2500;
    		index_end = 3000;
    	}
    	else if(classification.equals("�Ļ�")){
    		index_begin = 3000;
    		index_end = 3500;
    	}
    	else if(classification.equals("����")){
    		index_begin = 3500;
    		index_end = 4000;
    	}
    	else if(classification.equals("�ƾ�")){
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
