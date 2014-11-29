package nbmapplication;

import java.io.IOException;

public class PriorProbability {	
	private static TrainingDataManager tdm; 
    /**
    * 先验概率
    *  c 给定的分类
    *  给定条件下的先验概率
     * @throws IOException 
    */
	public PriorProbability() throws IOException{
		tdm =new TrainingDataManager();
	}
    public static float calculatePc(String c)
    {
        float ret = 0F;
        float Nc = tdm.getTrainingFileCountOfClassification(c);//在BayesClassifier中用一个for循环计算给定文章与所有类的分类概率~NC即是某次for循环中
                                                               //的一个类别c所包含的文档数
        float N = tdm.getTrainingFileCount();
        ret = Nc / N;//N为训练集中的文档总数
        System.out.println("&&&&&&&&&&&&&&&&&&"+N);
        return ret;
    }
}
