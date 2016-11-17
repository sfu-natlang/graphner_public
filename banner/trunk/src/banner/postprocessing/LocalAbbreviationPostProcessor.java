package banner.postprocessing;

import java.util.Set;

import banner.postprocessing.ExtractAbbrev.AbbreviationPair;
import banner.tokenization.SimpleTokenizer;
import banner.tokenization.Tokenizer;
import banner.types.Mention;
import banner.types.EntityType;
import banner.types.Sentence;
import banner.types.Mention.MentionType;

public class LocalAbbreviationPostProcessor implements PostProcessor {
	private ExtractAbbrev extractAbbrev;

	public LocalAbbreviationPostProcessor() {
		extractAbbrev = new ExtractAbbrev();
	}

	private void processAbbreviation(Mention formFound, String formNotFound) {
		Sentence sentence = formFound.getSentence();
		EntityType type = formFound.getEntityType();
		int charIndex = sentence.getText().indexOf(formNotFound);
		if (charIndex == -1)
			return;
		int start = sentence.getTokenIndex(charIndex, true);
		int end = sentence.getTokenIndex(charIndex + formNotFound.length(), false);
		if (start == end)
			return;
		Mention newMention = new Mention(sentence, start, end, type, formFound.getMentionType(), formFound.getProbability());
		boolean overlaps = false;
		for (Mention mention : sentence.getMentions())
			overlaps |= mention.overlaps(newMention);
		if (!overlaps)
			sentence.addMention(newMention);
	}

	public void postProcess(Sentence sentence) {
		Set<AbbreviationPair> abbreviationPairs = extractAbbrev.extractAbbrPairs(sentence.getText());
		if (abbreviationPairs.size() > 0) {
			for (AbbreviationPair abbreviation : abbreviationPairs) {
				Mention shortMention = null;
				Mention longMention = null;
				for (Mention mention : sentence.getMentions()) {
					if (abbreviation.getShortForm().equals(mention.getText()))
						shortMention = mention;
					if (abbreviation.getLongForm().equals(mention.getText()))
						longMention = mention;
				}
				if (shortMention == null) {
					if (longMention != null)
						processAbbreviation(longMention, abbreviation.getShortForm());
				} else {
					if (longMention == null)
						processAbbreviation(shortMention, abbreviation.getLongForm());
				}
			}
		}
	}

	public static void main(String[] args) {
		Tokenizer tokenizer = new SimpleTokenizer();
		LocalAbbreviationPostProcessor pp = new LocalAbbreviationPostProcessor();

		Sentence s1 = new Sentence("1", "1", "DiGeorge syndrome (DGS) is a a developmental field defect.");
		tokenizer.tokenize(s1);
		s1.addMention(new Mention(s1, 0, 2, EntityType.getType("Disease"), MentionType.Found));
		System.out.println("Mentions before PP:");
		for (Mention m : s1.getMentions()) {
			System.out.println("\t" + m.getText());
		}
		pp.postProcess(s1);
		System.out.println("Mentions after PP:");
		for (Mention m : s1.getMentions()) {
			System.out.println("\t" + m.getText());
		}

		Sentence s2 = new Sentence("1", "1", "DiGeorge\nsyndrome (DGS) is a a developmental field defect.");
		tokenizer.tokenize(s2);
		s2.addMention(new Mention(s2, 3, 4, EntityType.getType("Disease"), MentionType.Found));
		System.out.println("Mentions before PP:");
		for (Mention m : s2.getMentions()) {
			System.out.println("\t" + m.getText());
		}
		pp.postProcess(s2);
		System.out.println("Mentions after PP:");
		for (Mention m : s2.getMentions()) {
			System.out.println("\t" + m.getText());
		}
	}
}
