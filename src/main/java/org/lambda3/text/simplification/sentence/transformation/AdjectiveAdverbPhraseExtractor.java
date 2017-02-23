/*
 * ==========================License-Start=============================
 * sentence_simplification : AdjectiveAdverbPhraseExtractor
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

import edu.stanford.nlp.ling.LabeledWord;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.trees.Tree;

import java.util.List;

/**
 * Class for extracting adjective and adverb phrases that are set off by commas
 */
public class AdjectiveAdverbPhraseExtractor {

    /**
     * extracts adjective phrases that are delimited by punctuation from the input sentence and transforms them into stand-alone context sentences,
     * returns true if such an adjective phrase was found in the input sentence
     *
     * @param coreContextSentence
     * @param parse
     * @param isOriginal
     * @param contextNumber
     * @return
     */
    public static boolean extractAdjectivePhrases(CoreContextSentence coreContextSentence, Tree parse, boolean isOriginal, int contextNumber) {

        String sentence = SentenceUtils.listToString(parse.yield());
        boolean isSplit = false;
        boolean isPresent = SentenceProcessor.isPresent(coreContextSentence.getOriginal());
        String aux2 = SentenceProcessor.setAux(true, isPresent);

        for (Tree t : parse) {
            if (t.getChildrenAsList().size() >= 5) {
                for (int i = 0; i < t.getChildrenAsList().size() - 4; i++) {
                    if (t.getChild(i).label().value().equals("NP") && t.getChild(i + 1).label().value().equals("PP") && t.getChild(i + 2).label().value().equals(",") && t.getChild(i + 3).label().value().equals("ADJP") && t.getChild(i + 4).label().value().equals(",")) {
                        if (t.getChild(i + 3).getChildrenAsList().size() >= 2) {
                            int n = i + 3;
                            boolean isEnum = checkForEnum(t, n);

                            if (!isEnum) {
                                List<LabeledWord> label = t.getChild(i).labeledYield();
                                boolean isSingular = SentenceProcessor.isSingular(label.get(label.size() - 1));
                                String aux = SentenceProcessor.setAux(isSingular, isPresent);

                                String phrase = SentenceUtils.listToString(t.getChild(i).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 1).yield()) + aux + SentenceUtils.listToString(t.getChild(i + 3).yield()) + " .";
                                String phraseToDelete = SentenceUtils.listToString(t.getChild(i + 2).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 3).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 4).yield());

                                SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                isSplit = true;
                            }
                        }
                    }
                }
            }

            if (t.getChildrenAsList().size() >= 4) {
                for (int i = 0; i < t.getChildrenAsList().size() - 3; i++) {
                    if (t.getChild(i).label().value().equals("NP") && t.getChild(i + 1).label().value().equals(",") && t.getChild(i + 2).label().value().equals("ADJP") && t.getChild(i + 3).label().value().equals(",")) {
                        if (t.getChild(i + 2).getChildrenAsList().size() >= 2) {
                            int n = i + 2;
                            boolean isEnum = checkForEnum(t, n);
                            if (!isEnum) {
                                List<LabeledWord> label = t.getChild(i).labeledYield();
                                boolean isSingular = SentenceProcessor.isSingular(label.get(label.size() - 1));
                                String aux = SentenceProcessor.setAux(isSingular, isPresent);

                                String phrase = SentenceUtils.listToString(t.getChild(i).yield()) + aux + SentenceUtils.listToString(t.getChild(i + 2).yield()) + " .";
                                String phraseToDelete = SentenceUtils.listToString(t.getChild(i + 1).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 2).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 3).yield());

                                SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                isSplit = true;
                            }
                        }
                    } else if (t.getChild(i).label().value().equals("NP") && t.getChild(i + 1).label().value().equals("PP") && t.getChild(i + 2).label().value().equals(",") && t.getChild(i + 3).label().value().equals("ADJP") && i == t.getChildrenAsList().size() - 4) {
                        if (t.getChild(i + 3).getChildrenAsList().size() >= 2) {
                            int n = i + 3;
                            boolean isEnum = checkForEnum(t, n);
                            if (!isEnum) {
                                List<LabeledWord> label = t.getChild(i).labeledYield();
                                boolean isSingular = SentenceProcessor.isSingular(label.get(label.size() - 1));
                                String aux = SentenceProcessor.setAux(isSingular, isPresent);

                                String phrase = SentenceUtils.listToString(t.getChild(i).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 1).yield()) + aux + SentenceUtils.listToString(t.getChild(i + 3).yield()) + " .";
                                String phraseToDelete = SentenceUtils.listToString(t.getChild(i + 2).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 3).yield());

                                SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                isSplit = true;
                            }
                        }
                    }
                }
            }

            if (t.getChildrenAsList().size() >= 3) {
                for (int i = 0; i < t.getChildrenAsList().size() - 2; i++) {
                    if (t.getChild(i).label().value().equals("NP") && t.getChild(i + 1).label().value().equals(",") && t.getChild(i + 2).label().value().equals("ADJP") && i == t.getChildrenAsList().size() - 3) {
                        if (t.getChild(i + 2).getChildrenAsList().size() >= 2) {
                            int n = i + 2;
                            boolean isEnum = checkForEnum(t, n);
                            if (!isEnum) {
                                List<LabeledWord> label = t.getChild(i).labeledYield();
                                boolean isSingular = SentenceProcessor.isSingular(label.get(label.size() - 1));
                                String aux = SentenceProcessor.setAux(isSingular, isPresent);

                                String phrase = SentenceUtils.listToString(t.getChild(i).yield()) + aux + SentenceUtils.listToString(t.getChild(i + 2).yield()) + " .";
                                String phraseToDelete = SentenceUtils.listToString(t.getChild(i + 1).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 2).yield());

                                SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                isSplit = true;
                            }
                        }
                    }
                }
            }

            if (t.getChildrenAsList().size() >= 2) {
                for (int i = 0; i < t.getChildrenAsList().size() - 1; i++) {
                    if (t.getChild(i).label().value().equals("S") && t.getChild(i + 1).label().value().equals(",")) {
                        if (t.getChild(i).getChild(0).label().value().equals("ADJP")) {
                            String phrase = "This " + aux2 + " when being " + SentenceUtils.listToString(t.getChild(i).yield()) + " .";
                            String phraseToDelete = SentenceUtils.listToString(t.getChild(i).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 1).yield());

                            SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                            isSplit = true;
                        }
                        if (t.getChild(i).getChildrenAsList().size() >= 2) {
                            if (t.getChild(i).getChild(0).label().value().equals("ADVP") && t.getChild(i).getChild(1).label().value().equals("ADJP")) {
                                String phrase = "This " + aux2 + " when being " + SentenceUtils.listToString(t.getChild(i).yield()) + " .";
                                String phraseToDelete = SentenceUtils.listToString(t.getChild(i).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 1).yield());

                                SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                isSplit = true;
                            }
                        }
                    }
                }
            }
        }
        return isSplit;
    }


    /**
     * extracts adverb phrases that are delimited by punctuation from the input sentence and transforms them into stand-alone context sentences,
     * returns true if such an adverb phrase was found in the input sentence
     *
     * @param coreContextSentence
     * @param parse
     * @param isOriginal
     * @param contextNumber
     * @return
     */
    public static boolean extractAdverbPhrases(CoreContextSentence coreContextSentence, Tree parse, boolean isOriginal, int contextNumber) {

        String sentence = SentenceUtils.listToString(parse.yield());
        boolean isSplit = false;
        boolean isPresent = SentenceProcessor.isPresent(coreContextSentence.getOriginal());
        String aux = SentenceProcessor.setAux(true, isPresent);

        for (Tree t : parse) {
            if (t.label().value().equals("S")) {
                if (t.getChildrenAsList().size() >= 2) {
                    if (t.getChild(0).label().value().equals("ADVP") && t.getChild(1).label().value().equals(",")) {
                        String phrase = "This" + aux + SentenceUtils.listToString(t.getChild(0).yield()) + " .";
                        String phraseToDelete = SentenceUtils.listToString(t.getChild(0).yield()) + " ,";

                        if (!(t.label().value().equals("S") && t.getChild(0).label().value().equals("NP") && t.getChild(1).label().value().equals("VP") && t.getChild(2).label().value().equals(".")
                                && t.getChild(1).getChild(1).label().value().equals("ADVP") && t.getChild(1).getChildrenAsList().size() == 2)) {
                            SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                            isSplit = true;
                        }
                    }
                }
            }

            for (int i = 0; i < t.getChildrenAsList().size() - 2; i++) {
                if (t.getChild(i).label().value().equals(",") && t.getChild(i + 1).label().value().equals("ADVP") && t.getChild(i + 2).label().value().equals(",")) {
                    String phrase = "This" + aux + SentenceUtils.listToString(t.getChild(i + 1).yield()) + " .";
                    String phraseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 1).yield()) + " ,";

                    SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                    isSplit = true;
                }
            }

            for (int i = 0; i < t.getChildrenAsList().size() - 1; i++) {
                if (t.getChild(i).label().value().equals(",") && t.getChild(i + 1).label().value().equals("ADVP") && i == t.getChildrenAsList().size() - 2) {
                    String phrase = "This" + aux + SentenceUtils.listToString(t.getChild(i + 1).yield()) + " .";
                    String phraseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 1).yield());

                    SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                    isSplit = true;
                }
            }
        }
        return isSplit;
    }


    /**
     * checks whether the identified phrase represents an enumeration of phrases
     *
     * @param t
     * @param n
     * @return
     */
    private static boolean checkForEnum(Tree t, int n) {

        for (int i = n + 1; i < t.getChildrenAsList().size(); i++) {
            if (t.getChild(i).label().value().equals("CC") && (t.getChild(i).getChild(0).label().value().equals("and") || t.getChild(i).getChild(0).label().value().equals("or"))) {
                return true;
            }
        }

        return false;
    }
}
