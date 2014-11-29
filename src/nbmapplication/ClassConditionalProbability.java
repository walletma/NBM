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
    * 计算类条件概率
    *  x 给定的文本属性
    *  c 给定的分类
    *  返回给定条件下的类条件概率
    */
    public static float calculatePxc(String x, String c) throws FileNotFoundException, IOException 
    {
        float ret = 0F;
        //System.out.println("一步");
        float Nxc = tdm.getCountContainKeyOfClassification(c, x);
        float Nc = tdm.getTrainingFileCountOfClassification(c);//返回训练文本集中在给定分类下的训练文本数目 各500篇
        float V = tdm.getTraningClassifications().length;//类别的数目 9
        //System.out.println("二步");
        ret = (Nxc + 1) / (Nc + M + V); //进行加权处理~以避免出现0这种情况
        //System.out.println("####################"+(Nc + M + V));
        return ret;
    }
}
