package nbmapplication;

import java.io.IOException;

public class PriorProbability {	
	private static TrainingDataManager tdm; 
    /**
    * �������
    *  c �����ķ���
    *  ���������µ��������
     * @throws IOException 
    */
	public PriorProbability() throws IOException{
		tdm =new TrainingDataManager();
	}
    public static float calculatePc(String c)
    {
        float ret = 0F;
        float Nc = tdm.getTrainingFileCountOfClassification(c);//��BayesClassifier����һ��forѭ���������������������ķ������~NC����ĳ��forѭ����
                                                               //��һ�����c���������ĵ���
        float N = tdm.getTrainingFileCount();
        ret = Nc / N;//NΪѵ�����е��ĵ�����
        System.out.println("&&&&&&&&&&&&&&&&&&"+N);
        return ret;
    }
}
