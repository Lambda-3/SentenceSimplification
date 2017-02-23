/*
 * ==========================License-Start=============================
 * sentence_simplification : Punctuation
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

import java.util.ArrayList;
import java.util.List;

/**
 * Class for decomposing sentences incorporating selected punctuation (colons, semicolons, parentheses)
 */
public class Punctuation {

    /**
     * splits sentences that are separated by a colon or semicolon into two individual core sentences
     * returns true if the input sentence is composed of two sentences separated by a colon or semicolon
     *
     * @param coreContextSentence
     * @param parse
     * @param isOriginal
     * @param contextNumber
     * @return
     */
    public static boolean splitAtColon(SentenceProcessor sp, CoreContextSentence coreContextSentence, Tree parse, boolean isOriginal, int contextNumber) {

        String sentence = SentenceUtils.listToString(parse.yield());
        boolean isSplit = false;

        for (Tree t : parse) {
            if (t.getChildrenAsList().size() >= 3) {
                for (int i = 0; i < t.getChildrenAsList().size() - 2; i++) {
                    if (t.getChild(i).label().value().equals("S") && t.getChild(i + 1).label().value().equals(":") && t.getChild(i + 2).label().value().equals("S")) {
                        String phrase2 = SentenceUtils.listToString(t.getChild(i + 2).yield()) + " .";
                        String phraseToDelete = SentenceUtils.listToString(t.getChild(i + 1).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 2).yield());
                        String orig = SentenceUtils.listToString(parse.yield());
                        SentenceProcessor.addCore(phrase2, coreContextSentence);
                        String phrase1 = orig.replace(phraseToDelete, "");
                        String[] phrase1Tokens = phrase1.split(" ");
                        Integer pos = phrase1Tokens.length;
                        sp.addPos(pos - 2);

                        SentenceProcessor.updateSentence("", phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                        isSplit = true;
                    }
                }
            }
        }
        return isSplit;
    }


    /**
     * extracts constructs included in parentheses (indicated by PRN tags) from the input sentence and transforms them into stand-alone context sentences,
     * returns true if such a PRN construct was found in the input sentence
     *
     * @param coreContextSentence
     * @param parse
     * @param isOriginal
     * @param contextNumber
     * @return
     */
    public static boolean extractParentheses(CoreContextSentence coreContextSentence, Tree parse, boolean isOriginal, int contextNumber) {

        String sentence = SentenceUtils.listToString(parse.yield());
        boolean isSplit = false;
        boolean isPresent = SentenceProcessor.isPresent(coreContextSentence.getOriginal());
        String aux = SentenceProcessor.setAux(true, isPresent);

        for (Tree t : parse) {

            if (t.label().value().equals("PRN")) {
                if (t.getChild(0).label().value().equals(":") && (t.getChild(1).label().value().equals("ADVP") || t.getChild(1).label().value().equals("PP"))) {
                    String phrase = "This " + aux + SentenceUtils.listToString(t.yield()) + " .";
                    phrase = phrase.replace("--", "");
                    String phraseToDelete = SentenceUtils.listToString(t.yield());

                    SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                    isSplit = true;
                }

                if (t.getChild(0).label().value().equals(":") && t.getChild(1).label().value().equals("SBAR")) {
                    if (t.getChild(1).getChild(0).label().value().equals("WHADVP") && t.getChild(1).getChild(1).label().value().equals("S")) {
                        if (t.getChild(1).getChild(0).getChild(0).label().value().equals("WRB") && t.getChild(1).getChild(0).getChild(0).getChild(0).label().value().equals("where")) {
                            String phrase = "There " + SentenceUtils.listToString(t.getChild(1).getChild(1).yield()) + " .";
                            phrase = phrase.replace("--", "");
                            String phraseToDelete = SentenceUtils.listToString(t.yield());

                            SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                            isSplit = true;
                        }
                    }
                }

                if (t.getChild(0).label().value().equals(":") && t.getChild(1).label().value().equals("S")) {
                    String phrase = SentenceUtils.listToString(t.yield()) + " .";
                    phrase = phrase.replace("--", "");
                    String phraseToDelete = SentenceUtils.listToString(t.yield());

                    SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                    isSplit = true;
                }
            }

            if (t.label().value().equals("NP")) {
                if (t.getChildrenAsList().size() >= 2) {
                    if (t.getChild(0).label().value().equals("NP") && t.getChild(1).label().value().equals("PRN")) {
                        if (t.getChild(1).getChildrenAsList().size() >= 3) {
                            if (t.getChild(1).getChild(0).label().value().equals("-LRB-") && t.getChild(1).getChild(1).label().value().equals("CC") && t.getChild(1).getChild(1).getChild(0).label().value().equals("or")) {
                                String phrase = SentenceUtils.listToString(t.getChild(0).yield()) + aux + SentenceUtils.listToString(t.getChild(1).yield()) + " .";
                                phrase = phrase.replace("-LRB-", "");
                                phrase = phrase.replace("-RRB-", "");
                                phrase = phrase.replace("or", "");
                                String phraseToDelete = SentenceUtils.listToString(t.getChild(1).yield());

                                SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                isSplit = true;
                            }

                            if (t.getChild(1).getChildrenAsList().size() >= 4) {
                                if (t.getChild(1).getChild(0).label().value().equals(",") && t.getChild(1).getChild(1).label().value().equals("CC") && t.getChild(1).getChild(1).getChild(0).label().value().equals("or") && t.getChild(1).getChild(2).label().value().equals("NP") && t.getChild(1).getChild(3).label().value().equals(",")) {
                                    String phrase = SentenceUtils.listToString(t.getChild(0).yield()) + aux + SentenceUtils.listToString(t.getChild(1).yield()) + " .";
                                    phrase = phrase.replace(",", "");
                                    phrase = phrase.replace(",", "");
                                    phrase = phrase.replace("or", "");
                                    String phraseToDelete = SentenceUtils.listToString(t.getChild(1).yield());

                                    SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                    isSplit = true;
                                }
                            }
                        }
                    }
                }
            }

            if (t.getChildrenAsList().size() >= 2) {
                for (int i = 0; i < t.getChildrenAsList().size() - 1; i++) {
                    if (t.getChild(i).label().value().equals(":") && (t.getChild(i + 1).label().value().equals("ADVP") || t.getChild(i + 1).label().value().equals("PP"))) {
                        ArrayList<Tree> tr = new ArrayList<Tree>();
                        tr.add(0, t.getChild(i + 1));
                        tr.addAll(t.getChild(i + 1).siblings(parse));
                        for (int j = 0; j <= i; j++) {
                            tr.remove(0);
                        }
                        String ph = "";
                        for (Tree tree : tr) {
                            ph = ph + SentenceUtils.listToString(tree.yield()) + " ";
                        }

                        String phrase = "This " + aux + SentenceUtils.listToString(t.getChild(i + 1).yield()) + " " + ph + " .";
                        phrase = phrase.replace("--", "");
                        String phraseToDelete = SentenceUtils.listToString(t.getChild(i).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 1).yield());

                        SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                        isSplit = true;
                    }

                    if (t.getChild(i).label().value().equals(":") && t.getChild(i + 1).label().value().equals("ADJP")) {
                        ArrayList<Tree> tr = new ArrayList<Tree>();
                        tr.add(0, t.getChild(i + 1));
                        tr.addAll(t.getChild(i + 1).siblings(parse));
                        for (int j = 0; j <= i; j++) {
                            tr.remove(0);
                        }
                        String ph = "";
                        for (Tree tree : tr) {
                            ph = ph + SentenceUtils.listToString(tree.yield()) + " ";
                        }

                        String phrase = "This " + aux + "with " + SentenceUtils.listToString(t.getChild(i + 1).yield()) + " " + ph + " .";
                        phrase = phrase.replace("--", "");
                        String phraseToDelete = SentenceUtils.listToString(t.getChild(i).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 1).yield()) + ph.replace("--", "");

                        SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                        isSplit = true;
                    }

                    if (t.getChild(i).label().value().equals("NP") && t.getChild(i + 1).label().value().equals("PRN")) {
                        if (t.getChild(i + 1).getChild(0).label().value().equals(":") && t.getChild(i + 1).getChild(1).label().value().equals("VP")) {
                            List<LabeledWord> label = t.getChild(i).labeledYield();
                            boolean isSingular = SentenceProcessor.isSingular(label.get(label.size() - 1));
                            String aux2 = SentenceProcessor.setAux(isSingular, isPresent);

                            String phrase = SentenceUtils.listToString(t.getChild(i).yield()) + aux2 + SentenceUtils.listToString(t.getChild(i + 1).yield()) + " .";
                            phrase = phrase.replace("--", "");
                            String phraseToDelete = SentenceUtils.listToString(t.getChild(i + 1).yield());

                            SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                            isSplit = true;
                        }
                        if (t.getChild(i + 1).getChild(0).label().value().equals(":") && t.getChild(i + 1).getChild(1).label().value().equals("NP")) {
                            List<LabeledWord> label = t.getChild(i).labeledYield();
                            boolean isSingular = SentenceProcessor.isSingular(label.get(label.size() - 1));
                            String aux2 = SentenceProcessor.setAux(isSingular, isPresent);
                            String pronoun = "";
                            if (isSingular) {
                                pronoun = "This ";
                            } else {
                                pronoun = "These ";
                            }

                            String phrase = pronoun + aux2 + SentenceUtils.listToString(t.getChild(i + 1).yield()) + " .";
                            phrase = phrase.replace("--", "");
                            String phraseToDelete = SentenceUtils.listToString(t.getChild(i + 1).yield());

                            SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                            isSplit = true;
                        }
                        if (t.getChild(i + 1).getChild(0).label().value().equals(":") && t.getChild(i + 1).getChild(1).label().value().equals("SBAR")) {
                            if (t.getChild(i + 1).getChild(1).getChild(0).label().value().equals("WHNP") && t.getChild(i + 1).getChild(1).getChild(1).label().value().equals("S")) {
                                if ((t.getChild(i + 1).getChild(1).getChild(0).getChild(0).label().value().equals("WP") || t.getChild(i + 1).getChild(1).getChild(0).getChild(0).label().value().equals("WDT")) && (t.getChild(i + 1).getChild(1).getChild(0).getChild(0).getChild(0).label().value().equals("who") || t.getChild(i + 1).getChild(1).getChild(0).getChild(0).getChild(0).label().value().equals("which"))) {
                                    String phrase = SentenceUtils.listToString(t.getChild(i).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 1).getChild(1).getChild(1).yield()) + " .";
                                    String phraseToDelete = SentenceUtils.listToString(t.getChild(i + 1).yield());

                                    SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                    isSplit = true;
                                }
                                if (t.getChild(i + 1).getChild(1).getChild(0).getChild(0).label().value().equals("WP") && t.getChild(i + 1).getChild(1).getChild(0).getChild(0).getChild(0).label().value().equals("whom")) {
                                    String phrase = SentenceUtils.listToString(t.getChild(i + 1).getChild(1).getChild(1).yield()) + " " + SentenceUtils.listToString(t.getChild(i).yield()) + " .";
                                    String phraseToDelete = SentenceUtils.listToString(t.getChild(i + 1).yield());

                                    SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                    isSplit = true;
                                }
                                if (t.getChild(i + 1).getChild(1).getChild(0).getChildrenAsList().size() >= 2) {
                                    if (t.getChild(i + 1).getChild(1).getChild(0).getChild(0).label().value().equals("DT") && t.getChild(i + 1).getChild(1).getChild(0).getChild(1).label().value().equals("WHPP")) {
                                        String phrase = SentenceUtils.listToString(t.getChild(i + 1).getChild(1).getChild(0).getChild(0).yield()) + " " + SentenceUtils.listToString(t.getChild(i).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 1).getChild(1).getChild(1).yield()) + " .";
                                        String phraseToDelete = SentenceUtils.listToString(t.getChild(i + 1).yield());

                                        SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                        isSplit = true;
                                    }
                                }
                                if (t.getChild(i + 1).getChild(1).getChild(0).getChild(0).label().value().equals("WHPP")) {
                                    String phrase = SentenceUtils.listToString(t.getChild(i + 1).getChild(1).getChild(0).getChild(0).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 1).getChild(1).getChild(1).yield()) + " .";
                                    String phraseToDelete = SentenceUtils.listToString(t.getChild(i + 1).yield());

                                    SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                    isSplit = true;
                                }
                            }
                        }
                    }
                }
            }

            if (t.label().value().equals("NP") && t.getChildrenAsList().size() >= 3) {
                for (int i = 0; i < t.getChildrenAsList().size() - 2; i++) {
                    if (t.getChild(i).label().value().equals("NP") && t.getChild(i + 1).label().value().equals(":") && t.getChild(i + 2).label().value().equals("NP") && i == t.getChildrenAsList().size() - 3) {
                        if (t.getChild(i + 1).getChild(0).label().value().equals("--")) {
                            List<LabeledWord> label = t.getChild(i + 2).labeledYield();
                            boolean isSingular = SentenceProcessor.isSingular(label.get(label.size() - 1));
                            String aux2 = SentenceProcessor.setAux(isSingular, isPresent);
                            String phrase = SentenceUtils.listToString(t.getChild(i).yield()) + aux2 + SentenceUtils.listToString(t.getChild(i + 2).yield()) + " .";
                            String phraseToDelete = SentenceUtils.listToString(t.getChild(i + 1).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 2).yield());

                            SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                            isSplit = true;
                        }
                    }
                }
            }

            if (t.label().value().equals("NP") && t.getChildrenAsList().size() >= 5) {
                for (int i = 0; i < t.getChildrenAsList().size() - 4; i++) {
                    if (t.getChild(i).label().value().equals("NP") && t.getChild(i + 1).label().value().equals(":") && t.getChild(i + 2).label().value().equals("NP") && t.getChild(i + 3).label().value().equals("CC") && t.getChild(i + 4).label().value().equals("NP") && i == t.getChildrenAsList().size() - 5) {
                        if (t.getChild(i + 1).getChild(0).label().value().equals("--")) {
                            String aux2 = SentenceProcessor.setAux(false, isPresent);
                            String phrase = SentenceUtils.listToString(t.getChild(i).yield()) + aux2 + SentenceUtils.listToString(t.getChild(i + 2).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 3).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 4).yield()) + " .";
                            String phraseToDelete = SentenceUtils.listToString(t.getChild(i + 1).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 2).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 3).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 4).yield());

                            SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                            isSplit = true;
                        }
                    }
                }
            }
        }
        return isSplit;
    }


    /**
     * removes bracketed content from the input sentence without transforming it into stand-alone context sentences,
     * returns true if bracketed content was found in the input sentence
     *
     * @param coreContextSentence
     * @param parse
     * @param isOriginal
     * @param contextNumber
     * @return
     */
    public static boolean removeBrackets(CoreContextSentence coreContextSentence, Tree parse, boolean isOriginal, int contextNumber) {

        String sentence = SentenceUtils.listToString(parse.yield());
        boolean isSplit = false;
        boolean isPresent = SentenceProcessor.isPresent(coreContextSentence.getOriginal());
        String aux = SentenceProcessor.setAux(true, isPresent);

        for (Tree t : parse) {

            if (t.label().value().equals("PRN")) {
                if (t.getChildrenAsList().size() >= 3) {
                    for (int i = 0; i < t.getChildrenAsList().size() - 2; i++) {
                        if (t.getChild(i).label().value().equals("-LRB-") && t.getChild(i + 1).label().value().equals("S") && t.getChild(i + 2).label().value().equals("-RRB-")) {
                            String phrase = SentenceUtils.listToString(t.getChild(i + 1).yield()) + " .";
                            String phraseToDelete = SentenceUtils.listToString(t.getChild(i).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 1).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 2).yield());

                            SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                            isSplit = true;
                        }
                        if (t.getChild(i).label().value().equals("-LRB-") && t.getChild(i + 1).label().value().equals("CD") && t.getChild(i + 2).label().value().equals("-RRB-")) {
                            String phrase = "This " + aux + SentenceUtils.listToString(t.getChild(i + 1).yield()) + " .";
                            String phraseToDelete = SentenceUtils.listToString(t.getChild(i).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 1).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 2).yield());
                            String phraseToCompare = SentenceUtils.listToString(t.getChild(i + 1).yield()) + " .";

                            if (!sentence.equals("This is " + phraseToCompare.trim()) && !sentence.equals("This was " + phraseToCompare.trim())) {
                                SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                isSplit = true;
                            }
                        }
                        if (t.getChild(i).label().value().equals("-LRB-") && (t.getChild(i + 1).label().value().equals("ADVP") || t.getChild(i + 1).label().value().equals("PP") || t.getChild(i + 1).label().value().equals("VP"))) {
                            String phrase = "This " + aux + SentenceUtils.listToString(t.yield()) + " .";
                            phrase = phrase.replace("-LRB-", "");
                            phrase = phrase.replace("-RRB-", "");
                            String phraseToDelete = SentenceUtils.listToString(t.yield());
                            phrase = SentenceProcessor.collapseWhitespace(phrase);

                            if (!sentence.equals(phrase)) {
                                SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                isSplit = true;
                            }
                        }
                        if (t.getChild(i).label().value().equals("-LRB-") && t.getChild(i + 1).label().value().equals("NP") && t.getChild(i + 2).label().value().equals("-RRB-")) {
                            if (t.getChild(i + 1).getChild(0).label().value().equals("CD") && t.getChild(i + 1).getChildrenAsList().size() == 1) {
                                String phrase = "This " + aux + " in " + SentenceUtils.listToString(t.getChild(i + 1).yield()) + " .";
                                String phraseToDelete = SentenceUtils.listToString(t.getChild(i).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 1).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 2).yield());

                                phrase = SentenceProcessor.collapseWhitespace(phrase);

                                if (!sentence.equals(phrase)) {
                                    SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                    isSplit = true;
                                }
                            }
                        }
                    }
                }
            }

            if (t.getChildrenAsList().size() >= 5) {
                for (int i = 0; i < t.getChildrenAsList().size() - 4; i++) {
                    if (t.getChild(i).label().value().equals("NP") && t.getChild(i + 1).label().value().equals("-LRB-") && t.getChild(i + 2).label().value().equals("CC") && t.getChild(i + 3).label().value().equals("NP") && t.getChild(i + 4).label().value().equals("-RRB-")) {
                        if (t.getChild(i + 2).getChild(0).label().value().equals("and")) {
                            String phrase = "This " + aux + " as well as " + SentenceUtils.listToString(t.getChild(i + 3).yield()) + " .";
                            String phraseToDelete = SentenceUtils.listToString(t.getChild(i + 1).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 2).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 3).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 4).yield());
                            phrase = SentenceProcessor.collapseWhitespace(phrase);

                            if (!sentence.equals(phrase)) {
                                SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                isSplit = true;
                            }
                        }
                    }
                }
            }

            if (t.getChildrenAsList().size() >= 2) {
                for (int k = 0; k < t.getChildrenAsList().size() - 1; k++) {
                    if (t.label().value().equals("NP") && t.getChild(k).label().value().equals("NP") && t.getChild(k + 1).label().value().equals("PRN")) {
                        if (t.getChild(k + 1).getChildrenAsList().size() >= 3) {
                            for (int i = 0; i < t.getChild(k + 1).getChildrenAsList().size() - 2; i++) {
                                if (t.getChild(k + 1).getChild(i).label().value().equals("-LRB-") && (t.getChild(k + 1).getChild(i + 1).label().value().equals("NP") || t.getChild(k + 1).getChild(i + 1).label().value().equals("NP")) && t.getChild(k + 1).getChild(i + 2).label().value().equals("-RRB-")) {
                                    List<LabeledWord> label = t.getChild(k).labeledYield();
                                    boolean isSingular = SentenceProcessor.isSingular(label.get(label.size() - 1));
                                    String aux2 = SentenceProcessor.setAux(isSingular, isPresent);
                                    String phrase = SentenceUtils.listToString(t.getChild(k + 1).getChild(i + 1).yield()) + aux2 + SentenceUtils.listToString(t.getChild(k).yield()) + " .";
                                    String phraseToDelete = SentenceUtils.listToString(t.getChild(k + 1).getChild(i).yield()) + " " + SentenceUtils.listToString(t.getChild(k + 1).getChild(i + 1).yield()) + " " + SentenceUtils.listToString(t.getChild(k + 1).getChild(i + 2).yield());

                                    SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                    isSplit = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return isSplit;
    }
}
