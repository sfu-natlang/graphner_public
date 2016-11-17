/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package banner.chemdner.pipe;

import banner.chemdner.utils.StringUtil;
import edu.ucdenver.ccp.nlp.biolemmatizer.BioLemmatizer;
import edu.ucdenver.ccp.nlp.biolemmatizer.LemmataEntry;
import java.util.Collection;

/**
 *
 * @author Tsendee
 */
public class BioLemmatiserWrapper implements dragon.nlp.tool.Lemmatiser {

    BioLemmatizer bioLemmatizer = null;

    public BioLemmatiserWrapper() {
        bioLemmatizer = new BioLemmatizer();
    }

    public String lemmatize(String token, String pos) {
        String lemma = "*";
        LemmataEntry lemmataEntry = bioLemmatizer.lemmatizeByRules(StringUtil.unicodeHandler(token), pos);
        if (lemmataEntry.lemmasAndCategories.values().size() > 1) {
            System.out.println("====================================");
            System.out.println("Multiple lemmas for token: " + token);
            System.out.println("Lemmas: ");
            lemmataEntry.lemmasToString();
        }
        for (String value : lemmataEntry.lemmasAndCategories.values()) {
            lemma = value;
            break;
        }
        return lemma;
    }

    @Override
    public String lemmatize(String token) {
        String lemma = "*";
        LemmataEntry lemmataEntry = bioLemmatizer.lemmatizeByRules(lemma, null);
        // TODO: many possible output figure out how to represent it
        for (String value : lemmataEntry.lemmasAndCategories.values()) {
            lemma = value;
        }
        return lemma;
    }

    @Override
    public String lemmatize(String string, int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String stem(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
