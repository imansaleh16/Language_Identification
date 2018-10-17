package lang_id;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import cc.mallet.topics.LabeledLDA;
import utils.Utils;

public class LLDA {
	
	private final int I_GRAM = 4;
	
	private final String S_MODEL_DATA_FOLDER = "model_data/";
	private static final String S_DATA_FOLDER = "data/";
	private final String S_LLDA_OUT = S_DATA_FOLDER + "docsME.llda.4grams.predictions";
	private final String S_LLDA_TOPIC_KEYS = S_MODEL_DATA_FOLDER + "llda_topic_keys";
	private final String S_LLDA_MODEL = S_MODEL_DATA_FOLDER + "llda.model.ser";
	
	private final static String S_TRAIN = S_DATA_FOLDER + "docsMR.llda";
	private final static String S_TEST = S_DATA_FOLDER + "docsME.llda";
	private final String S_TEST_LABELS = S_DATA_FOLDER + "docsME.labels";
	
	private HashMap<String, HashMap<String, Integer>> hmshmsiConfusionMatrix = new HashMap<String, HashMap<String, Integer>>();
	private HashMap<Integer, String> hmisLangIndexes = new HashMap<Integer, String>();
	private HashMap<String, String> hmssLangNames = new HashMap<String, String>();
	
	public static void main(String[] args) throws IOException, ClassNotFoundException {
		LLDA llda_classifier = new LLDA();
		
		if(args[0].compareTo("ngrams") == 0)
		{
			System.out.println("Creating character grams for training file, please be patient ...");
			llda_classifier.prepareDataForLLDA(S_TRAIN);
			System.out.println("Creating character grams for test file, please be patient ...");
			llda_classifier.prepareDataForLLDA(S_TEST);
		}
		else if (args[0].compareTo("eval") == 0)
		{
			llda_classifier.loadLanguages();
			llda_classifier.calculatePrecisionRecallF1();
		}
		else{
			llda_classifier.loadLanguages();
			llda_classifier.getDocLanguage(args[0]);
		}
	}
	
	/*
	 * Puts train/test data in a format suitable for labeled LDA:
	 * 
	 * doc_1<\t>language<\t>character n grams
	 * doc_2<\t>language<\t>character n grams
	 * doc_3<\t>language<\t>character n grams
	 * ...
	 * 
	 */
	public void prepareDataForLLDA(String sTrainFile) throws IOException
	{
		BufferedReader brTrain = new BufferedReader(new FileReader(sTrainFile));
		BufferedWriter bwTrain = new BufferedWriter(new FileWriter(sTrainFile + "." + I_GRAM + "grams"));
		
		String sLine = "";
		
		String[] saTokens;
		
		while((sLine=brTrain.readLine())!=null)
		{
			saTokens = sLine.split("\t");
			bwTrain.write(saTokens[0] + "\t" + saTokens[1] + "\t");
			
			bwTrain.write(createNGrams(saTokens[2]) + "\n");
		}
		
		brTrain.close();
		bwTrain.close();
	}
	
	/*
	 * Create character n grams using the passed string.
	 */
	private String createNGrams(String sLine) {
		String sGram = "";
		String sOut = "";
		
		for(int i=0; i<sLine.length()-I_GRAM + 1; i++)
		{
			sGram = sLine.substring(i, i + I_GRAM);
			sOut += sGram.replaceAll("\\s", "0x20") + " ";
		}
		return sOut.trim();
	}

	/*
	 * Calculate precision, recall and F1 for labeled LDA output.
	 * Labeled LDA ouput has the following format:
	 * 
	 * doc_1<\t>lang_1_prob.<\t>lang_2_prob.<\t>...<\t>lang_44_prop
	 * doc_2<\t>lang_1_prob.<\t>lang_2_prob.<\t>...<\t>lang_44_prop
	 * doc_3<\t>lang_1_prob.<\t>lang_2_prob.<\t>...<\t>lang_44_prop
	 * ...
	 * 
	 */
	public void calculatePrecisionRecallF1() throws IOException
	{
		BufferedReader brLLDAOut = new BufferedReader(new FileReader(S_LLDA_OUT));
		BufferedReader brTestLabels = new BufferedReader(new FileReader(S_TEST_LABELS));
		
		BufferedWriter bw = new BufferedWriter(new FileWriter("llda_answer"));
		
		HashMap<String, Double> hmsdLangProbTest;
		HashMap<String, Integer> hmsiSubConfusionMatrix;
		
		String sLine;
		String sInstLang="";
		String sAnswer="";
		
		int iTotalCorrect = 0;
		int iTotalDocs = 0;
		
		hmssLangNames = Utils.loadLabels();
		
		while((sLine=brLLDAOut.readLine())!=null)
		{
			sInstLang = brTestLabels.readLine();
			
			iTotalDocs ++;
			
			hmsdLangProbTest = loadLLDAOutProbabilities(sLine);
			sAnswer = Utils.getMaxProb(hmsdLangProbTest);
			
			if(hmshmsiConfusionMatrix.containsKey(sInstLang))
			{
				hmsiSubConfusionMatrix = hmshmsiConfusionMatrix.get(sInstLang);
			}
			else{
				hmsiSubConfusionMatrix = new HashMap<String,Integer>();
			}
			
			if(hmsiSubConfusionMatrix.containsKey(sAnswer))
			{
				hmsiSubConfusionMatrix.put(sAnswer, hmsiSubConfusionMatrix.get(sAnswer)+1);
			}
			else
			{
				hmsiSubConfusionMatrix.put(sAnswer, 1);
			}
			
			hmshmsiConfusionMatrix.put(sInstLang, hmsiSubConfusionMatrix);
			
			if(sAnswer.compareTo(sInstLang)==0)
			{
				iTotalCorrect ++;
			}		
		}
		
		System.out.println("Correct = " + iTotalCorrect	+ ", Total = " + iTotalDocs);
		Utils.calculatePrecisionRecallF1(hmshmsiConfusionMatrix, hmisLangIndexes.size(), hmssLangNames);
		
		brLLDAOut.close();
		brTestLabels.close();
		bw.close();
	}

	/*
	 * Loads output labeled LDA probabilities in a hashmap, where key is 
	 * language code and value is language probability. The map is used to
	 * find the most probable language for a document.
	 */
	private HashMap<String, Double> loadLLDAOutProbabilities(String sLine) {
		HashMap<String, Double> hmsdLangProbTest = new HashMap<String, Double>();
		String[] saTokens = sLine.split("\t");
		
		for(int i=1; i<saTokens.length; i++)
		{
			//System.out.println(i + ", " + hmisLangIndexes.get(i-1) + ", " + saTokens[i]);
			hmsdLangProbTest.put(hmisLangIndexes.get(i-1), new Double(saTokens[i]));
		}
		return hmsdLangProbTest;
	}

	/*
	 * Loads language codes in a hashmap, where keys are language indexes and
	 * values are language codes.
	 */
	public void loadLanguages() throws IOException {
		BufferedReader brLanguagesKeys = new BufferedReader(new FileReader(S_LLDA_TOPIC_KEYS));
		String sLine;
		String[] saTokens;
		
		while((sLine=brLanguagesKeys.readLine())!=null)
		{
			saTokens = sLine.split("\t");
			hmisLangIndexes.put(new Integer(saTokens[0]).intValue(), saTokens[1]);
		}
		//System.out.println(hmisLangIndexes);
		brLanguagesKeys.close();
	}
	
	/*
	 * Get the language of an input document.
	 */
	public void getDocLanguage(String sDocPath) throws IOException, ClassNotFoundException
	{
		if(!new File(sDocPath).exists())
		{
			System.out.println("This file does not exist ...");
			return;
		}
		
		System.out.println("Processing file ...");
		
		BufferedReader br = new BufferedReader(new FileReader(sDocPath));
		BufferedWriter bw = new BufferedWriter(new FileWriter(sDocPath + "." + I_GRAM + "grams"));
		
		String sLine = "";
		String sDoc = "";
		String sAnswer = "";
		
		HashMap<String, Double> hmsdLangProbTest;
		
		while((sLine=br.readLine())!=null)
		{
			sDoc += sLine + " ";
		}
		br.close();
		
		bw.write("doc\t-1\t" + createNGrams(sDoc.trim()));
		bw.close();
		
		LabeledLDA.test(S_LLDA_MODEL, sDocPath + "." + I_GRAM + "grams");
		
		br = new BufferedReader(new FileReader(sDocPath + "." + I_GRAM + "grams.predictions"));
		
		hmsdLangProbTest = loadLLDAOutProbabilities(br.readLine());
		br.close();
		
		sAnswer = Utils.getMaxProb(hmsdLangProbTest);
		hmssLangNames = Utils.loadLabels();
		System.out.println(hmssLangNames.get(sAnswer));
	}
}
