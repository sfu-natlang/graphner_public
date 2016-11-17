package banner.eval.dataset;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.lang.NotImplementedException;

import banner.types.EntityType;
import banner.types.Mention;
import banner.types.Sentence;
import banner.types.Mention.MentionType;
import banner.util.SentenceBreaker;
import bioc.BioCAnnotation;
import bioc.BioCDocument;
import bioc.BioCLocation;
import bioc.BioCPassage;
import bioc.io.woodstox.ConnectorWoodstox;

public class BioCDataset extends Dataset {

	private SentenceBreaker breaker;

	public BioCDataset() {
		breaker = new SentenceBreaker();
	}

	@Override
	public void load(HierarchicalConfiguration config) {
		HierarchicalConfiguration localConfig = config.configurationAt(this.getClass().getPackage().getName());
		String filename = localConfig.getString("filename");

		try {
			ConnectorWoodstox connector = new ConnectorWoodstox();
			connector.startRead(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
			while (connector.hasNext()) {
				BioCDocument document = connector.next();
				String documentId = document.getID();
				// System.out.println("ID=" + documentId);
				for (BioCPassage passage : document.getPassages()) {
					processPassage(documentId, passage);
				}
				// System.out.println();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void processPassage(String documentId, BioCPassage passage) {
		// Get annotations for passage
		// Verify annotations have correct format
		List<BioCAnnotation> annotations = new LinkedList<BioCAnnotation>(passage.getAnnotations());
		for (BioCAnnotation annotation : annotations) {
			String annotationId = annotation.getID();
			List<BioCLocation> locations = annotation.getLocations();
			if (locations.size() != 1) {
				throw new IllegalArgumentException("Annotation must have exactly one location. Document=" + documentId + ", annotation=" + annotationId);
			}
			String type = annotation.getInfon("type");
			if (type == null) {
				throw new IllegalArgumentException("Annotation must have an infon element specifying the type. Document=" + documentId + ", annotation=" + annotationId);
			}
			String text = annotation.getText();
			int length = locations.get(0).getLength();
			if (text.length() != length) {
				throw new IllegalArgumentException("Annotation length must match length of text. Document=" + documentId + ", annotation=" + annotationId);
			}
		}

		// Process the passage text
		// System.out.println("Text=" + passage.getText());
		breaker.setText(passage.getText());
		int sentenceOffset = passage.getOffset();
		List<String> sentenceTexts = breaker.getSentences();
		for (int i = 0; i < sentenceTexts.size(); i++) {
			String sentenceText = sentenceTexts.get(i);
			// System.out.println("Text=" + sentenceText);
			String sentenceId = Integer.toString(i);
			if (sentenceId.length() < 2)
				sentenceId = "0" + sentenceId;
			sentenceId = documentId + "-" + sentenceId;
			Sentence sentence = new Sentence(sentenceId, documentId, sentenceText);
			tokenizer.tokenize(sentence);

			// Add the annotations for this sentence
			Iterator<BioCAnnotation> annotationIterator = annotations.iterator();
			while (annotationIterator.hasNext()) {
				BioCAnnotation annotation = annotationIterator.next();
				List<BioCLocation> locations = annotation.getLocations();
				int annotationOffset = locations.get(0).getOffset();

				// Check if this annotation is within the given sentence
				if (annotationOffset >= sentenceOffset && annotationOffset < sentenceOffset + sentenceText.length()) {
					String annotationId = annotation.getID();
					String text = annotation.getText();
					// System.out.println("\tAnnotation=" + text);
					int length = locations.get(0).getLength();
					if (length == 0) {
						System.out.println("WARNING: Annotation length must be greater than 0. Ignoring annotation=" + annotationId + ", document=" + documentId);
					} else {
						int beginOffset = annotationOffset - sentenceOffset;
						if (beginOffset + length > sentenceText.length()) {
							throw new IllegalArgumentException("Annotation " + annotationId + " spans two sentences in document " + documentId);
						}
						String comparisonText = sentenceText.substring(beginOffset, beginOffset + length);
						if (!text.equals(comparisonText)) {
							throw new IllegalArgumentException("Annotation text must match text from sentence. Document=" + documentId + ", annotation=" + annotationId
									+ " text according to location= \"" + comparisonText + "\"");
						}
						EntityType entityType = EntityType.getType(annotation.getInfon("type"));

						int start = sentence.getTokenIndexStart(beginOffset);
						if (start < 0) {
							throw new IllegalArgumentException("Start of annotation " + annotationId + " should be at index " + beginOffset + " in document " + documentId
									+ ", but does not match the start of any token");
						}
						int end = sentence.getTokenIndexEnd(annotationOffset + length - sentenceOffset);
						if (end < 0) {
							throw new IllegalArgumentException("End of mention annotation " + annotationId + " should be at index " + (beginOffset + length) + " in document " + documentId
									+ ", but does not match the end of any token");
						}
						end += 1; // this side is exclusive
						Mention mention = new Mention(sentence, start, end, entityType, MentionType.Required);
						sentence.addMention(mention);
					}
					annotationIterator.remove();
				}
			}
			sentenceOffset += sentenceText.length();
			sentences.add(sentence);
		}

		// Verify no annotations left
		if (annotations.size() > 0) {
			throw new IllegalArgumentException("Annotations not associated with passage. Document=" + documentId + ", annotations=" + annotations.size());
		}
	}

	@Override
	public List<Dataset> split(int n) {
		throw new NotImplementedException();
	}

}
