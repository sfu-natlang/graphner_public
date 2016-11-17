/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package banner.chemdner.pipe;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import cc.mallet.types.Token;
import cc.mallet.types.TokenSequence;

/**
 *
 * @author Tsendee
 */
public class WordVectorClassPipe extends Pipe {

    private static final long serialVersionUID = 991L;
    private String prefix = "WVC-";
    private transient WordVectorClasses tClasses = null;

    public WordVectorClassPipe() {
    }

    public WordVectorClassPipe(String prefix) {
        this.prefix = prefix;
    }

    public WordVectorClassPipe(WordVectorClasses tClasses) {
        this.tClasses = tClasses;
    }

    public void setWordVectorClasses(WordVectorClasses tClasses) {
        this.tClasses = tClasses;
    }

    @Override
    public Instance pipe(Instance carrier) {
        if (tClasses != null) {
            TokenSequence ts = (TokenSequence) carrier.getData();
            for (int i = 0; i < ts.size(); i++) {
                Token t = ts.get(i);
                String text = t.getText();

                String[] tclasses = tClasses.getTClass(text);
                for (int j = 0; j < tclasses.length; j++) {
                    String featureName = this.prefix + j + "=" + tclasses[j];
                    String value = tclasses[j];
                    t.setFeatureValue(featureName, 1);
                    t.setProperty(prefix + j, value);
                }
            }
        }
        return carrier;
    }
}
