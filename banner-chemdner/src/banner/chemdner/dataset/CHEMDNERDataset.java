/**
 *
 */
package banner.chemdner.dataset;

import banner.eval.dataset.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.configuration.HierarchicalConfiguration;

import banner.tokenization.Tokenizer;
import banner.types.Mention;
import banner.types.EntityType;
import banner.types.Sentence;
import banner.types.Token;
import banner.types.Mention.MentionType;
import java.io.*;

/**
 * @author Tsendee
 */
public class CHEMDNERDataset extends Dataset {

    public CHEMDNERDataset(Tokenizer tokenizer) {
        super();
        this.tokenizer = tokenizer;
    }

    public CHEMDNERDataset() {
        super();
    }

    @Override
    public void load(HierarchicalConfiguration config) {
        HierarchicalConfiguration localConfig = config.configurationAt(BC2GMDataset.class.getPackage().getName());
        String sentenceFilename = localConfig.getString("sentenceFilename");
        String mentionsFilename = localConfig.getString("mentionTestFilename");
        String alternateMentionsFilename = localConfig.getString("mentionAlternateFilename");
        load(sentenceFilename, mentionsFilename, alternateMentionsFilename);
    }

    public void load(String sentenceFilename, String mentionsFilename, String alternateMentionsFilename) {
        try {
            BufferedReader mentionTestFile = new BufferedReader(new InputStreamReader(
                      new FileInputStream(mentionsFilename), "UTF8"));
            HashMap<String, LinkedList<Tag>> tags = getTags(mentionTestFile);
            mentionTestFile.close();
            HashMap<String, LinkedList<Tag>> alternateTags = null;
            if (alternateMentionsFilename != null) {
                BufferedReader mentionAlternateFile = new BufferedReader(new InputStreamReader(
                      new FileInputStream(alternateMentionsFilename), "UTF8"));
                alternateTags = new HashMap<String, LinkedList<Tag>>(getAlternateTags(mentionAlternateFile));
                mentionAlternateFile.close();
            }


            BufferedReader sentenceFile = new BufferedReader(new InputStreamReader(
                      new FileInputStream(sentenceFilename), "UTF8"));
            String line = sentenceFile.readLine();
            while (line != null) {
                String[] splits = line.split("\t");
                String pmid = splits[0].trim();
                String textTitle = splits[1].trim();
                String textAbs = splits[2].trim();
                Sentence sentenceTitle = getSentence(pmid + "T", textTitle, tokenizer, tags);
                Sentence sentenceAbs = getSentence(pmid + "A", textAbs, tokenizer, tags);
                if (alternateTags != null) {
                    addAlternateMentions(sentenceTitle, alternateTags);
                    addAlternateMentions(sentenceAbs, alternateTags);
                }
                sentences.add(sentenceTitle);
                sentences.add(sentenceAbs);
                line = sentenceFile.readLine();
            }
            sentenceFile.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected HashMap<String, LinkedList<Tag>> getTags(BufferedReader tagFile) throws IOException {
        EntityType type = EntityType.getType("CHEMDNER");
        HashMap<String, LinkedList<Tag>> tags = new HashMap<String, LinkedList<Tag>>();

        String line = tagFile.readLine();
        while (line != null) {
            String[] split = line.split("\t");
            String segmentId = split[0] + split[1];
            LinkedList<Tag> tagList = tags.get(segmentId);
            if (tagList == null) {
                tagList = new LinkedList<Tag>();
            }
            Tag tag = new Tag(type, Integer.parseInt(split[2]), Integer.parseInt(split[3]));
            Iterator<Tag> tagIterator = tagList.iterator();
            boolean add = true;
            while (tagIterator.hasNext() && add) {
                Tag tag2 = tagIterator.next();
                // FIXME Determine what to do for when A.contains(B) or
                // B.contains(A)
                if (tag.contains(tag2)) {
                    tagIterator.remove();
                } // add = false;
                else if (tag2.contains(tag)) {
                    add = false;
                }
                // tagIterator.remove();
                // else
                // assert !tag.overlaps(tag2);
            }
            if (add) {
                tagList.add(tag);
                tags.put(segmentId, tagList);
            }
            line = tagFile.readLine();
        }
        return tags;
    }

    protected HashMap<String, LinkedList<Tag>> getAlternateTags(BufferedReader tagFile) throws IOException {
        HashMap<String, LinkedList<Tag>> tags = new HashMap<String, LinkedList<Tag>>();

        String line = tagFile.readLine();
        while (line != null) {
            String[] split = line.split(" |\\|");
            LinkedList<Tag> tagList = tags.get(split[0]);
            if (tagList == null) {
                tagList = new LinkedList<Tag>();
            }
            EntityType type = EntityType.getType("GENE");
            Tag tag = new Tag(type, Integer.parseInt(split[1]), Integer.parseInt(split[2]));
            tagList.add(tag);
            tags.put(split[0], tagList);
            line = tagFile.readLine();
        }
        return tags;
    }

    protected Sentence getSentence(String id, String sentenceText, Tokenizer tokenizer, HashMap<String, LinkedList<Tag>> tags) {
        Sentence sentence = new Sentence(id, null, sentenceText);
        tokenizer.tokenize(sentence);
        List<Token> tokens = sentence.getTokens();
        LinkedList<Tag> tagList = tags.get(id);
        if (tagList != null) {
            for (Tag tag : tagList) {
                int start = sentence.getTokenIndex(tag.start, true);
                assert start >= 0;
                int end = sentence.getTokenIndex(tag.end - 1, false);
                assert end >= start;
                sentence.addMention(new Mention(sentence, start, end + 1, tag.type, MentionType.Required));
            }
        }
        return sentence;
    }

    protected void addAlternateMentions(Sentence sentence, HashMap<String, LinkedList<Tag>> tags) {
        List<Token> tokens = sentence.getTokens();
        LinkedList<Tag> tagList = tags.get(sentence.getSentenceId());
        if (tagList != null) {
            for (Tag tag : tagList) {
                int start = sentence.getTokenIndex(tag.start, true);
                assert start >= 0;
                int end = sentence.getTokenIndex(tag.end - 1, false);
                assert end >= start;
                sentence.addMention(new Mention(sentence, start, end + 1, tag.type, MentionType.Allowed));
            }
        }
    }

    @Override
    public List<Dataset> split(int n) {
        List<Dataset> splitDatasets = new ArrayList<Dataset>();
        for (int i = 0; i < n; i++) {
            CHEMDNERDataset dataset = new CHEMDNERDataset(tokenizer);
            splitDatasets.add(dataset);
        }

        Random r = new Random();
        for (Sentence sentence : sentences) {
            int num = r.nextInt(n);
            splitDatasets.get(num).sentences.add(sentence);
        }
        return splitDatasets;
    }
}
