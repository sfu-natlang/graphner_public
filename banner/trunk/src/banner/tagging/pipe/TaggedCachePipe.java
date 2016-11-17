package banner.tagging.pipe;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import banner.tokenization.SimpleTokenizer;
import banner.types.Sentence;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import cc.mallet.types.TokenSequence;

public class TaggedCachePipe extends Pipe {

	private static final long serialVersionUID = 396388570676307651L;
	private String prefix;
	private int expectedNumCaches;
	private transient List<Map<List<String>, double[]>> caches;
	private SimpleTokenizer tok = new SimpleTokenizer();

	public TaggedCachePipe(String prefix, List<String> cacheFilenames) {
		this.prefix = prefix;
		loadCaches(cacheFilenames);
	}

	public void loadCaches(List<String> cacheFilenames) {
		expectedNumCaches = cacheFilenames.size();
		System.err.println("Number of caches: " + expectedNumCaches);
		caches = new ArrayList<Map<List<String>, double[]>>(expectedNumCaches);
		try {
			for (String cacheFilename : cacheFilenames) {
				ObjectInputStream input;
				input = new ObjectInputStream(new GZIPInputStream(new FileInputStream(cacheFilename)));
				@SuppressWarnings("unchecked")
				Map<String, double[]> cache = (Map<String, double[]>) input.readObject();
				caches.add(getCache(cache));
				input.close();
			}
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Map<List<String>, double[]> getCache(Map<String, double[]> cache) {
		Map<List<String>, double[]> cache2 = new HashMap<List<String>, double[]>();
		for (String stringText : cache.keySet()) {
			cache2.put(tok.getTokens(stringText), cache.get(stringText));
		}
		System.err.println("size()=" + cache2.size());
		return cache2;
	}

	@Override
	public Instance pipe(Instance carrier) {
		if (expectedNumCaches == 0) {
			if (caches != null) {
				throw new IllegalStateException("Model was trained without tagged caches, but number of caches is " + caches.size());
			}
		} else {
			if (caches == null) {
				throw new IllegalStateException("Model was trained with " + expectedNumCaches + " tagged caches, but caches is null");
			} else if (caches.size() != expectedNumCaches) {
				throw new IllegalStateException("Model was trained with " + expectedNumCaches + " tagged caches, but number of caches is " + caches.size());
			}
		}

		Sentence sentence = (Sentence) carrier.getSource();
		String sentenceText = sentence.getText();
		TokenSequence ts = (TokenSequence) carrier.getData();

		for (int cacheIndex = 0; cacheIndex < caches.size(); cacheIndex++) {
			double[] scores = caches.get(cacheIndex).get(tok.getTokens(sentenceText));
			if (scores == null) {
				String message = "Scores is null for sentence text \"" + sentenceText + "\" and cache index " + cacheIndex;
				throw new IllegalArgumentException(message);
			}
			if (ts.size() != scores.length) {
				String message = "Number of scores " + scores.length;
				message += " does not match number of tokens " + ts.size() + "for cache index " + cacheIndex;
				throw new IllegalArgumentException(message);
			}
			for (int i = 0; i < ts.size(); i++) {
				cc.mallet.types.Token token = ts.get(i);
				String featureName = prefix + ":" + Integer.toString(cacheIndex) + "=";
				double score = transform(scores[i]);
				token.setFeatureValue(featureName, score);
			}
		}
		return carrier;
	}

	private double transform(double score) {
		return score;
		// return Math.pow(score, 0.2);
	}
}
