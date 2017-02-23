/*
 * ==========================License-Start=============================
 * sentence_simplification : RepresentationGenerator
 *
 * Copyright © 2017 Lambda³
 *
 * GNU General Public License 3
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 * ==========================License-End==============================
 */

package org.lambda3.text.simplification.sentence.analysis;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.trees.Tree;

import java.io.StringReader;
import java.util.List;

/**
 * Class for converting an input NL sentence into a variety of representations that the transformation module can work with.
 * This class constitutes "stage 2" of the simplification framework.
 */
public class RepresentationGenerator {

    private static TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
    private static LexicalizedParser parser = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
    private static MaxentTagger tagger = new MaxentTagger("edu/stanford/nlp/models/pos-tagger/english-left3words/english-left3words-distsim.tagger");
    private static AbstractSequenceClassifier classifier = CRFClassifier.getClassifierNoExceptions("edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");

    /**
     * Tokenize the input sentence
     *
     * @param sentence One input sentence
     * @return tokenized sentence
     */
    public static List<CoreLabel> tokenize(String sentence) {

        Tokenizer<CoreLabel> tok = tokenizerFactory.getTokenizer(new StringReader(sentence));

        return tok.tokenize();
    }


    /**
     * Creates the constituency-based parse tree of the input sentence
     *
     * @param tokens sentence
     * @return parse tree
     */
    public static Tree parse(List<CoreLabel> tokens) {

        return parser.apply(tokens);

    }


    /**
     * Creates a named entity tagged version of the input sentence
     *
     * @param sentence input sentence
     * @return NE-tagged sentence
     */
    public static String ner(String sentence) {
        return classifier.classifyToString(sentence);
    }


    /**
     * POS tags the input sentence
     *
     * @param sentence input sentence
     * @return POS-tagged sentence
     */
    public static String posTag(String sentence) {
        return tagger.tagString(sentence);
    }
}
