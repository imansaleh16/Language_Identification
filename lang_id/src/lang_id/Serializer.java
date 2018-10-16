package lang_id;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import com.google.common.collect.BiMap;

public abstract class Serializer {
	public static void serializeDictionary(Object dic, String sOutFile) throws IOException
	{
		//FSTObjectOutput o = new FSTObjectOutput(new FileOutputStream(new File(sOutFile)));
		ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(new File(sOutFile)));
		o.writeObject(dic);
		o.close();
	}
	
	@SuppressWarnings("unchecked")
	public static HashMap<String, HashMap<String, Integer>> deserialize_hm_s_hmsi(String sFile) throws IOException
	{
		HashMap<String, HashMap<String, Integer>> hmshmsi = null;
		//FSTObjectInput i = new FSTObjectInput(new FileInputStream(new File(S_MODEL_DATA_FOLDER + S_LANG_NGRAM_COUNTS)));
		ObjectInputStream i = new ObjectInputStream(new FileInputStream(new File(sFile)));
		try {
			hmshmsi = (HashMap<String, HashMap<String, Integer>>)i.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		i.close();
		return hmshmsi;
	}
	
	
	@SuppressWarnings("unchecked")
	public static HashMap<String, Integer> deserialize_hm_s_i(String sFile) throws IOException
	{
		HashMap<String, Integer> hmsi = null;
		//FSTObjectInput i = new FSTObjectInput(new FileInputStream(new File(S_MODEL_DATA_FOLDER + S_LANG_TOTAL_TOKENS_COUNTS)));
		ObjectInputStream i = new ObjectInputStream(new FileInputStream(new File(sFile)));
		try {
			hmsi = (HashMap<String, Integer>)i.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		i.close();
		return hmsi;
	}
	
	@SuppressWarnings("unchecked")
	public static BiMap<String, Integer> deserialize_bm_s_i(String sFile) throws IOException
	{
		BiMap<String, Integer> bmsi = null;
		ObjectInputStream i = new ObjectInputStream(new FileInputStream(new File(sFile)));
		try {
			bmsi = (BiMap<String, Integer>)i.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		i.close();
		return bmsi;
	}
	
	@SuppressWarnings("unchecked")
	public static HashMap<String, Double> deserialize_hm_s_d(String sFile) throws IOException
	{
		HashMap<String, Double> hmsd = null;
		//FSTObjectInput i = new FSTObjectInput(new FileInputStream(new File(S_MODEL_DATA_FOLDER + S_LANG_PROP)));
		ObjectInputStream i = new ObjectInputStream(new FileInputStream(new File(sFile)));
		try {
			hmsd = (HashMap<String, Double>)i.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		i.close();
		return hmsd;
	}
	
	@SuppressWarnings("unchecked")
	public static HashMap<String, String> deserialize_hm_s_s(String sFile) throws IOException
	{
		HashMap<String, String> hmss = null;
		//FSTObjectInput i = new FSTObjectInput(new FileInputStream(new File(S_MODEL_DATA_FOLDER + S_LANG_NAMES)));
		ObjectInputStream i = new ObjectInputStream(new FileInputStream(new File(sFile)));
		try {
			hmss = (HashMap<String, String>)i.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		i.close();
		return hmss;
	}
}
