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
 *
 * @author Tsendee
 */
public class WordVectorClasses{
    
    private static final long serialVersionUID = 99991L;
    
    String prefix = "WVC-";

    public Vector<Boolean> isLowercaseTokensByResource = null;
    public Vector<String> resources = null;
    public Vector<Hashtable<String, String>> wordClassesByResource = null;

    public WordVectorClasses(Vector<String> pathsToClusterFiles, Vector<Boolean> isLowercaseBrownClusters) {
        isLowercaseTokensByResource = new Vector<Boolean>();
        wordClassesByResource = new Vector<Hashtable<String, String>>();
        resources = new Vector<String>();
        for (int i = 0; i < pathsToClusterFiles.size(); i++) {
            Hashtable<String, String> h = new Hashtable<String, String>();
            System.out.println("Reading the word vector class resource: " + pathsToClusterFiles.elementAt(i));
            InFile in = new InFile(pathsToClusterFiles.elementAt(i));
            String line = in.readLine();
            int wordsAdded = 0;
            while (line != null) {
                StringTokenizer st = new StringTokenizer(line);
                String token = st.nextToken();
                String tclass = st.nextToken();
                h.put(token, tclass);
                wordsAdded++;
                line = in.readLine();
            }
            System.out.println(wordsAdded + " words added");
            wordClassesByResource.addElement(h);
            isLowercaseTokensByResource.addElement(isLowercaseBrownClusters.elementAt(i));
            resources.addElement(pathsToClusterFiles.elementAt(i));
            in.close();
        }
    }

    public String[] getTClass(String w) {
        Vector<String> v = new Vector<String>();
        for (int j = 0; j < wordClassesByResource.size(); j++) {
            String word = w;
            if (isLowercaseTokensByResource.elementAt(j)) {
                word = word.toLowerCase();
            }
            Hashtable<String, String> tclasses = wordClassesByResource.elementAt(j);
            if (tclasses != null && tclasses.containsKey(word)) {
                String tclass = tclasses.get(word);
                v.addElement("resource" + j + ":" + tclass);
            }
        }
        String[] res = new String[v.size()];
        for (int i = 0; i < v.size(); i++) {
            res[i] = v.elementAt(i);
        }
        return res;
    }

}
