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
public class WordEmbeddingPipe extends Pipe {

    private static final long serialVersionUID = 99L;
    private String prefix = "CWWE-";
    private transient WordEmbeddings wordEmbeddings = null;

    public WordEmbeddingPipe() {
    }

    public WordEmbeddingPipe(String prefix) {
        this.prefix = prefix;
    }

    public WordEmbeddingPipe(WordEmbeddings wordEmbeddings) {
        this.wordEmbeddings = wordEmbeddings;
    }

    public void setWordEmbeddings(WordEmbeddings wordEmbeddings) {
        this.wordEmbeddings = wordEmbeddings;
    }

    @Override
    public Instance pipe(Instance carrier) {
        if (wordEmbeddings != null) {
            TokenSequence ts = (TokenSequence) carrier.getData();
            for (int i = 0; i < ts.size(); i++) {
                Token t = ts.get(i);
                String text = t.getText();
                double[] embedding = wordEmbeddings.getEmbedding(text);
                for (int dim = 0; dim < embedding.length; dim++) {
                    String featureName = this.prefix + dim + "=";
                    double value = embedding[dim];
                    t.setFeatureValue(featureName, value);
                }
            }
        }
        return carrier;
    }
}
