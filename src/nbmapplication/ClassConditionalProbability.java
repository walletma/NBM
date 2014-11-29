package nbmapplication;

import java.io.FileNotFoundException;
import java.io.IOException;

public class ClassConditionalProbability {
	private static TrainingDataManager tdm; 
    private static final float M = 0F;
    
    public ClassConditionalProbability() throws IOException{
    	tdm = new TrainingDataManager();
    }
    /*
    * ��������������
    *  x �������ı�����
    *  c �����ķ���
    *  ���ظ��������µ�����������
    */
    public static float calculatePxc(String x, String c) throws FileNotFoundException, IOException 
    {
        float ret = 0F;
        //System.out.println("һ��");
        float Nxc = tdm.getCountContainKeyOfClassification(c, x);
        float Nc = tdm.getTrainingFileCountOfClassification(c);//����ѵ���ı������ڸ��������µ�ѵ���ı���Ŀ ��500ƪ
        float V = tdm.getTraningClassifications().length;//������Ŀ 9
        //System.out.println("����");
        ret = (Nxc + 1) / (Nc + M + V); //���м�Ȩ����~�Ա������0�������
        //System.out.println("####################"+(Nc + M + V));
        return ret;
    }
}
