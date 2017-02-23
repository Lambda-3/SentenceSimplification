/*
 * ==========================License-Start=============================
 * sentence_simplification : InitialNounPhraseExtractor
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

package org.lambda3.text.simplification.sentence.transformation;

import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.trees.Tree;

/**
 * Class for extracting lead noun phrases
 */
public class InitialNounPhraseExtractor {

    /**
     * extracts lead noun phrases from the input sentence and transforms them into stand-alone context sentences,
     * returns true if a lead noun phrase was found in the input sentence
     *
     * @param coreContextSentence
     * @param parse
     * @param isOriginal
     * @param contextNumber
     * @return
     */
    public static boolean extractInitialParentheticalNounPhrases(CoreContextSentence coreContextSentence, Tree parse, boolean isOriginal, int contextNumber) {

        String sentence = SentenceUtils.listToString(parse.yield());
        boolean isSplit = false;
        boolean isPresent = SentenceProcessor.isPresent(coreContextSentence.getOriginal());
        String aux = SentenceProcessor.setAux(true, isPresent);

        for (Tree t : parse) {
            if (t.label().value().equals("S")) {
                if (t.getChildrenAsList().size() >= 2) {
                    if (t.getChild(0).label().value().equals("NP") && t.getChild(1).label().value().equals(",")) {
                        if (t.getChildrenAsList().size() >= 5) {
                            if (!(t.getChild(2).label().value().equals("S") && t.getChild(3).label().value().equals(",") && t.getChild(4).label().value().equals("VP"))) {
                                String phrase = "This " + aux + SentenceUtils.listToString(t.getChild(0).yield()) + " .";
                                String phraseToDelete = SentenceUtils.listToString(t.getChild(0).yield()) + " " + SentenceUtils.listToString(t.getChild(1).yield());

                                SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                isSplit = true;
                            }
                        } else {
                            String phrase = "This " + aux + SentenceUtils.listToString(t.getChild(0).yield()) + " .";
                            String phraseToDelete = SentenceUtils.listToString(t.getChild(0).yield()) + " " + SentenceUtils.listToString(t.getChild(1).yield());

                            SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                            isSplit = true;
                        }
                    }
                }
                if (t.getChildrenAsList().size() >= 3) {
                    if (t.getChild(0).label().value().equals("ADVP") && t.getChild(1).label().value().equals("NP") && t.getChild(2).label().value().equals(",")) {
                        String phrase = "This " + aux + SentenceUtils.listToString(t.getChild(0).yield()) + " " + SentenceUtils.listToString(t.getChild(1).yield()) + " .";
                        String phraseToDelete = SentenceUtils.listToString(t.getChild(0).yield()) + " " + SentenceUtils.listToString(t.getChild(1).yield()) + " " + SentenceUtils.listToString(t.getChild(2).yield());

                        SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                        isSplit = true;
                    }
                }
            }
        }
        return isSplit;
    }

}
