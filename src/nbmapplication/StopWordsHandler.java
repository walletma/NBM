package nbmapplication;

public class StopWordsHandler {
	private static String stopWordsList[] ={"��", "����","Ҫ","�Լ�","֮","��","��","��","��","��","��","��","Ӧ","��",
		                                    "ĳ","��","��","��","λ","��","һ","��","��","��","��","��","��","��"," ",
		                                    ",","��","��","?","!","��","��","��","��","��","1","2","3","4","5","6",
		                                    "7","8","9","0","��","��",".","&amp","&nbsp"};//����ͣ�ô�
    public static boolean IsStopWord(String word)
    {
        for(int i=0;i<stopWordsList.length;++i)
        {
            if(word.equalsIgnoreCase(stopWordsList[i]))
                return true;
        }
        return false;
    }
}
