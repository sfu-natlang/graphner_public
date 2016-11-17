/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package banner.chemdner.pipe;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import banner.chemdner.utils.InFile;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import cc.mallet.types.Token;
import cc.mallet.types.TokenSequence;

/**
 * This class was partially copied from Joseph Turian's work (NerACL2010) and provides
 * access to the browncluster model that was previously built
 *
 * @author Tsendee
 */
public class BrownClusters{
    
    private static final long serialVersionUID = 9999L;
    
    String prefix = "brown-";

    public Vector<Boolean> isLowercaseBrownClustersByResource = null;
    public Vector<String> resources = null;
    public Vector<Hashtable<String, String>> wordToPathByResource = null;
    public final int[] prefixLengths = {4, 6, 10, 20};

    public BrownClusters(Vector<String> pathsToClusterFiles, Vector<Integer> thresholds, Vector<Boolean> isLowercaseBrownClusters) {
        isLowercaseBrownClustersByResource = new Vector<Boolean>();
        wordToPathByResource = new Vector<Hashtable<String, String>>();
        resources = new Vector<String>();
        for (int i = 0; i < pathsToClusterFiles.size(); i++) {
            Hashtable<String, String> h = new Hashtable<String, String>();
            System.out.println("Reading the Brown clusters resource: " + pathsToClusterFiles.elementAt(i));
            InFile in = new InFile(pathsToClusterFiles.elementAt(i));
            String line = in.readLine();
            int wordsAdded = 0;
            while (line != null) {
                StringTokenizer st = new StringTokenizer(line);
                String path = st.nextToken();
                String word = st.nextToken();
                int occ = Integer.parseInt(st.nextToken());
                if (occ >= thresholds.elementAt(i)) {
                    h.put(word, path);
                    wordsAdded++;
                }
                line = in.readLine();
            }
            System.out.println(wordsAdded + " words added");
            wordToPathByResource.addElement(h);
            isLowercaseBrownClustersByResource.addElement(isLowercaseBrownClusters.elementAt(i));
            resources.addElement(pathsToClusterFiles.elementAt(i));
            in.close();
        }
    }

    public String[] getPrefixes(String w) {
        Vector<String> v = new Vector<String>();
        for (int j = 0; j < wordToPathByResource.size(); j++) {
            String word = w;
            if (isLowercaseBrownClustersByResource.elementAt(j)) {
                word = word.toLowerCase();
            }
            Hashtable<String, String> wordToPath = wordToPathByResource.elementAt(j);
            if (wordToPath != null && wordToPath.containsKey(word)) {
                String path = wordToPath.get(word);
                v.addElement("resource" + j + ":" + path.substring(0, Math.min(path.length(), prefixLengths[0])));
                for (int i = 1; i < prefixLengths.length; i++) {
                    if (prefixLengths[i - 1] < path.length()) {
                        v.addElement("resource" + j + ":" + path.substring(0, Math.min(path.length(), prefixLengths[i])));
                    }
                }
            }
        }
        String[] res = new String[v.size()];
        for (int i = 0; i < v.size(); i++) {
            res[i] = v.elementAt(i);
        }
        return res;
    }

    public static void printArr(String[] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.print(" " + arr[i]);
        }
        System.out.println("");
    }


//	public static void printOovData(Vector<LinkedVector> data){
//		int totalTokens=0;
//		HashMap<String,Boolean> tokensHash=new HashMap<String,Boolean>();
//		HashMap<String,Boolean> tokensHashIC=new HashMap<String,Boolean>();
//		for(int i=0;i<data.size();i++)
//			for(int j=0;j<data.elementAt(i).size();j++)
//			{
//				String form=((NEWord)data.elementAt(i).get(j)).form;
//				totalTokens++;
//				tokensHash.put(form,true);
//				tokensHashIC.put(form.toLowerCase(),true);
//			}
//		System.out.println("Data statistics:");
//		System.out.println("\t\t- Total tokens with repetitions ="+ totalTokens);
//		System.out.println("\t\t- Total unique tokens  ="+ tokensHash.size());
//		System.out.println("\t\t- Total unique tokens ignore case ="+ tokensHashIC.size());
//		for(int resourceId=0;resourceId<wordToPathByResource.size();resourceId++){
//			Hashtable<String, String> wordToPath = wordToPathByResource.elementAt(resourceId);
//			System.out.println("\t* OOV statistics for the resource: "+resources.elementAt(resourceId)+"(covers "+wordToPath.size()+" unique tokens)");
//			int oovCaseSensitive=0;
//			int oovAfterLowercasing=0;
//			HashMap<String,Boolean> oovCaseSensitiveHash=new HashMap<String, Boolean>();
//			HashMap<String,Boolean> oovAfterLowercasingHash=new HashMap<String, Boolean>();
//			for(int i=0;i<data.size();i++)
//				for(int j=0;j<data.elementAt(i).size();j++)
//				{
//					String form=((NEWord)data.elementAt(i).get(j)).form;
//					if(!wordToPath.containsKey(form)){
//						oovCaseSensitive++;
//						oovCaseSensitiveHash.put(form, true);
//					}
//					if((!wordToPath.containsKey(form))&&(!wordToPath.containsKey(form.toLowerCase()))){
//						oovAfterLowercasing++;
//						oovAfterLowercasingHash.put(form.toLowerCase(), true);
//					}
//				}
//			System.out.println("\t\t- Total OOV tokens, Case Sensitive ="+ oovCaseSensitive);
//			System.out.println("\t\t- OOV tokens, no repetitions, Case Sensitive ="+ oovCaseSensitiveHash.size());
//			System.out.println("\t\t- Total OOV tokens even after lowercasing  ="+ oovAfterLowercasing);
//			System.out.println("\t\t- OOV tokens even after lowercasing, no repetition  ="+ oovAfterLowercasingHash.size());
//		}
//		
//	}
//	public static void main(String[] args){
//		Vector<String> resources=new Vector<String>();
//		resources.addElement("Data/BrownHierarchicalWordClusters/brownBllipClusters");
//		Vector<Integer> thres=new Vector<Integer>();
//		thres.addElement(5);
//		Vector<Boolean> lowercase=new Vector<Boolean>();	
//		lowercase.addElement(false);
//		init(resources,thres,lowercase);
//		System.out.println("finance ");
//		printArr(getPrefixes("finance"));
//		System.out.println("help");
//                printArr(getPrefixes("help"));
//		System.out.println("resque ");
//                printArr(getPrefixes("resque"));
//		System.out.println("assist ");
//                printArr(getPrefixes("assist"));
//		System.out.println("assistance ");
//                printArr(getPrefixes("assistance"));
//		System.out.println("guidance ");
//                printArr(getPrefixes("guidance"));
//	}
}
