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
public class BrownCusterPipe extends Pipe {

    private static final long serialVersionUID = 99999L;
    private String prefix = "brown-";
    private transient BrownClusters brownClusters = null;

    public BrownCusterPipe() {
    }

    public BrownCusterPipe(String prefix) {
        this.prefix = prefix;
    }

    public BrownCusterPipe(BrownClusters brownClusters) {
        this.brownClusters = brownClusters;
    }

    public void setBrownClusters(BrownClusters brownClusters) {
        this.brownClusters = brownClusters;
    }

    @Override
    public Instance pipe(Instance carrier) {
        if (brownClusters != null) {
            TokenSequence ts = (TokenSequence) carrier.getData();
            for (int i = 0; i < ts.size(); i++) {
                Token t = ts.get(i);
                String text = t.getText();

                String[] paths = brownClusters.getPrefixes(text);
                for (int j = 0; j < paths.length; j++) {
                    String featureName = this.prefix + j + "=" + paths[j];
                    String value = paths[j];
                    t.setFeatureValue(featureName, 1);
                    t.setProperty("brown-" + j, value);
                }
            }
        }
        return carrier;
    }
}
