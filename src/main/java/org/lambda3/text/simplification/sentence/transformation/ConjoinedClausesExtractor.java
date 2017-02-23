/*
 * ==========================License-Start=============================
 * sentence_simplification : ConjoinedClausesExtractor
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
 * Class for decomposing a variety of conjoined clauses
 */
public class ConjoinedClausesExtractor {

    /**
     * separates clauses conjoined by one of the conjunctions "and, or, but" (without a preceding comma) into separate core sentences,
     * returns true if a conjoined clause of this type was found in the input sentence
     *
     * @param coreContextSentence
     * @param parse
     * @param isOriginal
     * @param contextNumber
     * @return
     */
    public static boolean infixAndOrButSplit(SentenceProcessor sp, CoreContextSentence coreContextSentence, Tree parse, boolean isOriginal, int contextNumber) {

        String sentence = SentenceUtils.listToString(parse.yield());
        boolean isSplit = false;

        for (Tree t : parse) {
            if (t.label().value().equals("S") && !t.ancestor(1, parse).label().value().equals("SBAR")) {
                if (t.getChildrenAsList().size() >= 3) {
                    for (int i = 0; i < t.getChildrenAsList().size() - 2; i++) {
                        if (t.getChild(i).label().value().equals("S") && t.getChild(i + 1).label().value().equals("CC") && t.getChild(i + 2).label().value().equals("S")) {
                            if (t.getChild(i + 1).getChild(0).label().value().equals("and") || t.getChild(i + 1).getChild(0).label().value().equals("or") || t.getChild(i + 1).getChild(0).label().value().equals("but")) {
                                String phrase2 = SentenceUtils.listToString(t.getChild(i + 1).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 2).yield()) + " .";
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
            }
        }
        return isSplit;
    }


    /**
     * separates clauses conjoined by one of the conjunctions "and, or, but" (with a preceding comma) into separate core sentences,
     * returns true if a conjoined clause of this type was found in the input sentence
     *
     * @param coreContextSentence
     * @param parse
     * @param isOriginal
     * @param contextNumber
     * @return
     */
    public static boolean infixCommaAndOrButSplit(SentenceProcessor sp, CoreContextSentence coreContextSentence, Tree parse, boolean isOriginal, int contextNumber) {

        String sentence = SentenceUtils.listToString(parse.yield());
        boolean isSplit = false;

        for (Tree t : parse) {
            if (t.label().value().equals("S")) {
                if (t.getChildrenAsList().size() >= 4) {
                    for (int i = 0; i < t.getChildrenAsList().size() - 2; i++) {
                        if (t.getChild(i).label().value().equals("S") && t.getChild(i + 1).label().value().equals(",") && t.getChild(i + 2).label().value().equals("CC") && t.getChild(i + 3).label().value().equals("S")) {
                            if (t.getChild(i + 2).getChild(0).label().value().equals("and") || t.getChild(i + 2).getChild(0).label().value().equals("or") || t.getChild(i + 2).getChild(0).label().value().equals("but")) {
                                String phrase2 = SentenceUtils.listToString(t.getChild(i + 2).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 3).yield()) + " .";
                                String phraseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 2).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 3).yield());
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
            }
        }
        return isSplit;
    }


    /**
     * extracts subordinate clauses starting with the conjunction "when" that are not placed at the start of the input sentence
     * from the input sentence and transforms them into stand-alone context sentences,
     * returns true if a conjoined clause of this type was found in the input sentence
     *
     * @param coreContextSentence
     * @param parse
     * @param isOriginal
     * @param contextNumber
     * @return
     */
    public static boolean infixWhenSplit(CoreContextSentence coreContextSentence, Tree parse, boolean isOriginal, int contextNumber) {

        String sentence = SentenceUtils.listToString(parse.yield());
        boolean isSplit = false;
        boolean isPresent = SentenceProcessor.isPresent(coreContextSentence.getOriginal());
        String aux2 = SentenceProcessor.setAux(true, isPresent);

        for (Tree t : parse) {
            if (t.label().value().equals("VP") || t.label().value().equals("NP")) {
                for (int i = 0; i < t.getChildrenAsList().size() - 1; i++) {
                    if (t.getChild(i).label().value().equals(",") && t.getChild(i + 1).label().value().equals("SBAR")) {
                        if (t.getChild(i + 1).getChild(0).label().value().equals("WHADVP") && t.getChild(i + 1).getChild(1).label().value().equals("S")) {
                            for (int j = 0; j < t.getChild(i + 1).getChild(0).getChildrenAsList().size(); j++) {
                                if (t.getChild(i + 1).getChild(0).getChild(j).label().value().equals("WRB") && t.getChild(i + 1).getChild(0).getChild(j).getChild(0).label().value().equals("when")) {
                                    for (int k = 0; k < t.getChild(i + 1).getChild(1).getChildrenAsList().size() - 1; k++) {
                                        if (t.getChild(i + 1).getChild(1).getChild(k).label().value().equals("NP")) {
                                            for (int l = k + 1; l < t.getChild(i + 1).getChild(1).getChildrenAsList().size(); l++) {
                                                if (t.getChild(i + 1).getChild(1).getChild(l).label().value().equals("VP")) {
                                                    String aux;
                                                    if (t.getChild(i + 1).getChild(1).getChild(l).getChild(0).label().value().equals("VBP") || t.getChild(i + 1).getChild(1).getChild(l).getChild(0).label().value().equals("VBZ")) {
                                                        aux = " is ";
                                                    } else {
                                                        aux = " was ";
                                                    }

                                                    String phrase = "This" + aux + SentenceUtils.listToString(t.getChild(i + 1).yield()) + " .";
                                                    String phraseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 1).yield());

                                                    String[] tokensToDelete = phraseToDelete.split(" ");
                                                    String[] tokens = sentence.split(" ");

                                                    int tokensToDeleteCount = tokensToDelete.length;
                                                    int tokensCount = tokens.length;

                                                    if (tokensCount - tokensToDeleteCount > 3) {
                                                        SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                                        isSplit = true;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (t.label().value().equals("VP")) {
                if (t.getChildrenAsList().size() >= 2) {
                    for (int i = 0; i < t.getChildrenAsList().size() - 1; i++) {
                        if (t.getChild(i).label().value().equals("NP") && t.getChild(i + 1).label().value().equals("SBAR")) {
                            if (t.getChild(i + 1).getChildrenAsList().size() >= 2) {
                                if (t.getChild(i + 1).getChild(0).label().value().equals("WHADVP") && t.getChild(i + 1).getChild(1).label().value().equals("S")) {
                                    if (t.getChild(i + 1).getChild(0).getChild(0).label().value().equals("WRB") && t.getChild(i + 1).getChild(0).getChild(0).getChild(0).label().value().equals("when")) {
                                        String phrase = "This " + aux2 + SentenceUtils.listToString(t.getChild(i + 1).yield()) + " .";
                                        String phraseToDelete = SentenceUtils.listToString(t.getChild(i + 1).yield());

                                        SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                        isSplit = true;
                                    }
                                }
                            }
                            if (t.getChild(i + 1).getChildrenAsList().size() >= 3) {
                                if (t.getChild(i + 1).getChild(1).label().value().equals("WHADVP") && t.getChild(i + 1).getChild(2).label().value().equals("S")) {
                                    if (t.getChild(i + 1).getChild(1).getChild(0).label().value().equals("WRB") && t.getChild(i + 1).getChild(1).getChild(0).getChild(0).label().value().equals("when")) {
                                        String phrase = "This " + aux2 + SentenceUtils.listToString(t.getChild(i + 1).yield()) + " .";
                                        String phraseToDelete = SentenceUtils.listToString(t.getChild(i + 1).yield());

                                        SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                        isSplit = true;
                                    }
                                }
                            }
                        }
                    }
                }
                if (t.getChildrenAsList().size() >= 3) {
                    for (int i = 0; i < t.getChildrenAsList().size() - 2; i++) {
                        if (t.getChild(i).label().value().equals("NP") && t.getChild(i + 1).label().value().equals("PP") && t.getChild(i + 2).label().value().equals("SBAR")) {
                            if (t.getChild(i + 2).getChildrenAsList().size() >= 2) {
                                if (t.getChild(i + 2).getChild(0).label().value().equals("WHADVP") && t.getChild(i + 2).getChild(1).label().value().equals("S")) {
                                    if (t.getChild(i + 2).getChild(0).getChild(0).label().value().equals("WRB") && t.getChild(i + 2).getChild(0).getChild(0).getChild(0).label().value().equals("when")) {
                                        String phrase = "This " + aux2 + SentenceUtils.listToString(t.getChild(i + 2).yield()) + " .";
                                        String phraseToDelete = SentenceUtils.listToString(t.getChild(i + 2).yield());

                                        SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                        isSplit = true;
                                    }
                                }
                            }
                            if (t.getChild(i + 2).getChildrenAsList().size() >= 3) {
                                if (t.getChild(i + 2).getChild(1).label().value().equals("WHADVP") && t.getChild(i + 2).getChild(2).label().value().equals("S")) {
                                    if (t.getChild(i + 2).getChild(1).getChild(0).label().value().equals("WRB") && t.getChild(i + 2).getChild(1).getChild(0).getChild(0).label().value().equals("when")) {
                                        String phrase = "This " + aux2 + SentenceUtils.listToString(t.getChild(i + 2).yield()) + " .";
                                        String phraseToDelete = SentenceUtils.listToString(t.getChild(i + 2).yield());

                                        SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                        isSplit = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return isSplit;
    }


    /**
     * extracts subordinate clauses starting with the conjunction "when" that are placed at the start of the input sentence
     * from the input sentence and transforms them into stand-alone context sentences,
     * returns true if a conjoined clause of this type was found in the input sentence
     *
     * @param coreContextSentence
     * @param parse
     * @param isOriginal
     * @param contextNumber
     * @return
     */
    public static boolean initialWhenSplit(CoreContextSentence coreContextSentence, Tree parse, boolean isOriginal, int contextNumber) {

        String sentence = SentenceUtils.listToString(parse.yield());
        boolean isSplit = false;

        for (Tree t : parse) {
            if (t.label().value().equals("S")) {
                if (t.getChildrenAsList().size() >= 2) {
                    for (int i = 0; i < t.getChildrenAsList().size() - 1; i++) {
                        if (t.getChild(i).label().value().equals("SBAR") && t.getChild(i + 1).label().value().equals(",")) {
                            if (t.getChild(i).getChild(0).label().value().equals("WHADVP") && t.getChild(i).getChild(1).label().value().equals("S")) {
                                if (t.getChild(i).getChild(0).getChild(0).label().value().equals("WRB") && t.getChild(i).getChild(0).getChild(0).getChild(0).label().value().equals("when")) {
                                    String aux = "";
                                    for (int j = 0; j < t.getChild(i).getChild(1).getChildrenAsList().size() - 1; j++) {
                                        if (t.getChild(i).getChild(1).getChild(j).label().value().equals("NP")) {
                                            for (int k = j + 1; k < t.getChild(i).getChild(1).getChildrenAsList().size(); k++) {
                                                if (t.getChild(i).getChild(1).getChild(k).label().value().equals("VP")) {
                                                    if (t.getChild(i).getChild(1).getChild(k).getChild(0).label().value().equals("VBP") || t.getChild(i).getChild(1).getChild(k).getChild(0).label().value().equals("VBZ")) {
                                                        aux = "is ";
                                                    } else {
                                                        aux = "was ";
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    String phrase = "This " + aux + SentenceUtils.listToString(t.getChild(i).yield()) + " .";
                                    String phraseToDelete = SentenceUtils.listToString(t.getChild(i).yield()) + " ,";

                                    String[] tokensToDelete = phraseToDelete.split(" ");
                                    String[] tokens = sentence.split(" ");

                                    int tokensToDeleteCount = tokensToDelete.length;
                                    int tokensCount = tokens.length;

                                    if (tokensCount - tokensToDeleteCount > 3) {
                                        SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                        isSplit = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return isSplit;
    }


    /**
     * extracts subordinate clauses starting with one of the conjunctions "though, although, because" that are placed at the start of the input sentence
     * from the input sentence and transforms them into stand-alone context sentences,
     * returns true if a conjoined clause of this type was found in the input sentence
     *
     * @param coreContextSentence
     * @param parse
     * @param isOriginal
     * @param contextNumber
     * @return
     */
    public static boolean initialThoughAlthoughBecauseSplit(CoreContextSentence coreContextSentence, Tree parse, boolean isOriginal, int contextNumber) {

        String sentence = SentenceUtils.listToString(parse.yield());
        boolean isPresent = SentenceProcessor.isPresent(coreContextSentence.getOriginal());
        boolean isSplit = false;

        for (Tree t : parse) {
            if (t.label().value().equals("S")) {
                for (int i = 0; i < t.getChildrenAsList().size() - 1; i++) {
                    if (t.getChild(i).label().value().equals("SBAR") && t.getChild(i + 1).label().value().equals(",")) {
                        if (t.getChild(i).getChild(0).label().value().equals("IN") && t.getChild(i).getChild(1).label().value().equals("S")) {
                            if (t.getChild(i).getChild(0).getChild(0).label().value().equals("Because") || t.getChild(i).getChild(0).getChild(0).label().value().equals("Though") || t.getChild(i).getChild(0).getChild(0).label().value().equals("Although")) {
                                String aux = SentenceProcessor.setAux(true, isPresent);
                                String phrase = "This " + aux + SentenceUtils.listToString(t.getChild(i).yield()) + " .";
                                String phraseToDelete = SentenceUtils.listToString(t.getChild(i).yield()) + " ,";

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
     * extracts subordinate clauses starting with one of the conjunctions "before, after"
     * from the input sentence and transforms them into stand-alone context sentences,
     * returns true if a conjoined clause of this type was found in the input sentence
     *
     * @param coreContextSentence
     * @param parse
     * @param isOriginal
     * @param contextNumber
     * @return
     */
    public static boolean infixPPSAfterBeforeSplit(CoreContextSentence coreContextSentence, Tree parse, boolean isOriginal, int contextNumber) {

        String sentence = SentenceUtils.listToString(parse.yield());
        boolean isSplit = false;

        for (Tree t : parse) {
            if (t.label().value().equals("VP") || t.label().value().equals("NP")) {
                for (int i = 0; i < t.getChildrenAsList().size(); i++) {
                    if (t.getChild(i).label().value().equals("PP")) {
                        if (i == 0) {
                            for (int n = 0; n < t.getChild(i).getChildrenAsList().size() - 1; n++) {
                                if (t.getChild(i).getChild(n).label().value().equals("IN") && t.getChild(i).getChild(n + 1).label().value().equals("S")) {
                                    if (t.getChild(i).getChild(n).getChild(0).label().value().equals("after") || t.getChild(i).getChild(n).getChild(0).label().value().equals("before")) {
                                        String aux;
                                        if (t.getChild(i).getChild(0).label().value().equals("VBP") || t.getChild(i).getChild(0).label().value().equals("VBZ")) {
                                            aux = " is ";
                                        } else {
                                            aux = " was ";
                                        }

                                        String phrase = "This" + aux + SentenceUtils.listToString(t.getChild(i).yield()) + " .";
                                        String phraseToDelete = SentenceUtils.listToString(t.getChild(i).yield());
                                        String[] tokensToDelete = phraseToDelete.split(" ");
                                        String[] tokens = sentence.split(" ");

                                        int tokensToDeleteCount = tokensToDelete.length;
                                        int tokensCount = tokens.length;

                                        if (tokensCount - tokensToDeleteCount > 3) {
                                            SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                            isSplit = true;
                                        }
                                    }
                                }
                            }
                        } else if (i > 0) {
                            if (!t.getChild(i - 1).label().value().equals(",")) {
                                for (int n = 0; n < t.getChild(i).getChildrenAsList().size() - 1; n++) {
                                    if (t.getChild(i).getChild(n).label().value().equals("IN") && t.getChild(i).getChild(n + 1).label().value().equals("S")) {
                                        if (t.getChild(i).getChild(n).getChild(0).label().value().equals("after") || t.getChild(i).getChild(n).getChild(0).label().value().equals("before")) {
                                            String aux;
                                            if (t.getChild(i).getChild(0).label().value().equals("VBP") || t.getChild(i).getChild(0).label().value().equals("VBZ")) {
                                                aux = " is ";
                                            } else {
                                                aux = " was ";
                                            }

                                            String phrase = "This" + aux + SentenceUtils.listToString(t.getChild(i).yield()) + " .";
                                            String phraseToDelete = SentenceUtils.listToString(t.getChild(i).yield());
                                            String[] tokensToDelete = phraseToDelete.split(" ");
                                            String[] tokens = sentence.split(" ");

                                            int tokensToDeleteCount = tokensToDelete.length;
                                            int tokensCount = tokens.length;

                                            if (tokensCount - tokensToDeleteCount > 3) {
                                                SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                                isSplit = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return isSplit;
    }


    /**
     * extracts subordinate clauses starting with one of the conjunctions "before, after"
     * from the input sentence and transforms them into stand-alone context sentences,
     * returns true if a conjoined clause of this type was found in the input sentence
     *
     * @param coreContextSentence
     * @param parse
     * @param isOriginal
     * @param contextNumber
     * @return
     */
    public static boolean infixCommaPPAfterBeforeSplit(CoreContextSentence coreContextSentence, Tree parse, boolean isOriginal, int contextNumber) {

        String sentence = SentenceUtils.listToString(parse.yield());
        boolean isSplit = false;

        for (Tree t : parse) {
            if (t.label().value().equals("VP") || t.label().value().equals("NP")) {
                for (int n = 0; n < t.getChildrenAsList().size() - 2; n++) {
                    if (t.getChild(n).label().value().equals(",") && t.getChild(n + 1).label().value().equals("PP") && !t.getChild(n + 2).label().value().equals(",")) {
                        for (int m = 0; m < t.getChild(n + 1).getChildrenAsList().size(); m++) {
                            if (t.getChild(n + 1).getChild(m).label().value().equals("IN")) {
                                if (t.getChild(n + 1).getChild(m).getChild(0).label().value().equals("after") || t.getChild(n + 1).getChild(m).getChild(0).label().value().equals("before")) {
                                    String aux;
                                    if (t.getChild(0).label().value().equals("VBP") || t.getChild(0).label().value().equals("VBZ")) {
                                        aux = " is ";
                                    } else {
                                        aux = " was ";
                                    }

                                    String phrase = "This" + aux + SentenceUtils.listToString(t.getChild(n + 1).yield()) + " .";
                                    String phraseToDelete = SentenceUtils.listToString(t.getChild(n + 1).yield());
                                    String[] tokensToDelete = phraseToDelete.split(" ");
                                    String[] tokens = sentence.split(" ");

                                    int tokensToDeleteCount = tokensToDelete.length;
                                    int tokensCount = tokens.length;

                                    if (tokensCount - tokensToDeleteCount > 3) {
                                        SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                        isSplit = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return isSplit;
    }


    /**
     * extracts subordinate clauses starting with one of the conjunctions "before, after"
     * from the input sentence and transforms them into stand-alone context sentences,
     * returns true if a conjoined clause of this type was found in the input sentence
     *
     * @param coreContextSentence
     * @param parse
     * @param isOriginal
     * @param contextNumber
     * @return
     */
    public static boolean infixPPAfterBeforeSplit(CoreContextSentence coreContextSentence, Tree parse, boolean isOriginal, int contextNumber) {

        String sentence = SentenceUtils.listToString(parse.yield());
        boolean isSplit = false;

        for (Tree t : parse) {
            if (t.label().value().equals("VP") || t.label().value().equals("NP")) {
                for (int i = 0; i < t.getChildrenAsList().size(); i++) {
                    if (t.getChild(i).label().value().equals("PP")) {
                        if (i == 0) {
                            for (int n = 0; n < t.getChild(i).getChildrenAsList().size() - 1; n++) {
                                if (t.getChild(i).getChild(n).label().value().equals("IN") && t.getChild(i).getChild(n + 1).label().value().equals("NP")) {
                                    if (t.getChild(i).getChild(n).getChild(0).label().value().equals("after") || t.getChild(i).getChild(n).getChild(0).label().value().equals("before")) {
                                        for (int m = 0; m < t.getChild(i).getChild(n + 1).getChildrenAsList().size(); m++) {
                                            if (t.getChild(i).getChild(n + 1).getChild(m).label().value().equals("VP")) {
                                                String aux;
                                                if (t.getChild(i).getChild(0).label().value().equals("VBP") || t.getChild(i).getChild(0).label().value().equals("VBZ")) {
                                                    aux = " is ";
                                                } else {
                                                    aux = " was ";
                                                }

                                                String phrase = "This" + aux + SentenceUtils.listToString(t.getChild(i).yield()) + " .";
                                                String phraseToDelete = SentenceUtils.listToString(t.getChild(i).yield());
                                                String[] tokensToDelete = phraseToDelete.split(" ");
                                                String[] tokens = sentence.split(" ");

                                                int tokensToDeleteCount = tokensToDelete.length;
                                                int tokensCount = tokens.length;

                                                if (tokensCount - tokensToDeleteCount > 3) {
                                                    SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                                    isSplit = true;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else if (i > 0) {
                            if (!t.getChild(i - 1).label().value().equals(",")) {
                                for (int n = 0; n < t.getChild(i).getChildrenAsList().size() - 1; n++) {
                                    if (t.getChild(i).getChild(n).label().value().equals("IN") && t.getChild(i).getChild(n + 1).label().value().equals("NP")) {
                                        if (t.getChild(i).getChild(n).getChild(0).label().value().equals("after") || t.getChild(i).getChild(n).getChild(0).label().value().equals("before")) {
                                            for (int m = 0; m < t.getChild(i).getChild(n + 1).getChildrenAsList().size(); m++) {
                                                if (t.getChild(i).getChild(n + 1).getChild(m).label().value().equals("VP")) {
                                                    String aux;
                                                    if (t.getChild(i).getChild(0).label().value().equals("VBP") || t.getChild(i).getChild(0).label().value().equals("VBZ")) {
                                                        aux = " is ";
                                                    } else {
                                                        aux = " was ";
                                                    }

                                                    String phrase = "This" + aux + SentenceUtils.listToString(t.getChild(i).yield()) + " .";
                                                    String phraseToDelete = SentenceUtils.listToString(t.getChild(i).yield());
                                                    String[] tokensToDelete = phraseToDelete.split(" ");
                                                    String[] tokens = sentence.split(" ");

                                                    int tokensToDeleteCount = tokensToDelete.length;
                                                    int tokensCount = tokens.length;

                                                    if (tokensCount - tokensToDeleteCount > 3) {
                                                        SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                                        isSplit = true;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return isSplit;
    }


    /**
     * extracts subordinate clauses starting with one of the conjunctions "before, after"
     * from the input sentence and transforms them into stand-alone context sentences,
     * returns true if a conjoined clause of this type was found in the input sentence
     *
     * @param coreContextSentence
     * @param parse
     * @param isOriginal
     * @param contextNumber
     * @return
     */
    public static boolean infixSBARAfterBeforeSplit(CoreContextSentence coreContextSentence, Tree parse, boolean isOriginal, int contextNumber) {

        String sentence = SentenceUtils.listToString(parse.yield());
        boolean successful = false;
        boolean isPresent = SentenceProcessor.isPresent(coreContextSentence.getOriginal());
        boolean isSplit = false;

        for (Tree t : parse) {
            if (t.label().value().equals("VP") || t.label().value().equals("NP")) {
                for (int n = 0; n < t.getChildrenAsList().size() - 1; n++) {
                    if (t.getChild(n).label().value().equals("NP")) {
                        for (int i = 1; i < t.getChildrenAsList().size(); i++) {
                            if (t.getChild(i).label().value().equals("SBAR")) {
                                if (!t.getChild(i - 1).label().value().equals(",")) {
                                    if (t.getChild(i).getChild(0).label().value().equals("IN") && t.getChild(i).getChild(1).label().value().equals("S")) {
                                        if (t.getChild(i).getChild(0).getChild(0).label().value().equals("after") || t.getChild(i).getChild(0).getChild(0).label().value().equals("before")) {
                                            for (int k = 0; k < t.getChild(i).getChild(1).getChildrenAsList().size() - 1; k++) {
                                                if (t.getChild(i).getChild(1).getChild(k).label().value().equals("NP")) {
                                                    for (int l = k + 1; l < t.getChild(i).getChild(1).getChildrenAsList().size(); l++) {
                                                        if (t.getChild(i).getChild(1).getChild(l).label().value().equals("VP") && successful == false) {
                                                            successful = true;
                                                            String aux;
                                                            if (t.getChild(i).getChild(1).getChild(l).getChild(0).label().value().equals("VBP") || t.getChild(i).getChild(1).getChild(l).getChild(0).label().value().equals("VBZ")) {
                                                                aux = " is ";
                                                            } else {
                                                                aux = " was ";
                                                            }

                                                            String phrase = "This" + aux + SentenceUtils.listToString(t.getChild(i).yield()) + " .";
                                                            String phraseToDelete = SentenceUtils.listToString(t.getChild(i).yield());
                                                            String[] tokensToDelete = phraseToDelete.split(" ");
                                                            String[] tokens = sentence.split(" ");

                                                            int tokensToDeleteCount = tokensToDelete.length;
                                                            int tokensCount = tokens.length;

                                                            if (tokensCount - tokensToDeleteCount > 3) {
                                                                SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                                                isSplit = true;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (t.label().value().equals("S")) {
                if (t.getChildrenAsList().size() >= 2) {
                    for (int i = 0; i < t.getChildrenAsList().size() - 1; i++) {
                        if (t.getChild(i).label().value().equals("SBAR") && t.getChild(i + 1).label().value().equals(",")) {
                            if ((t.getChild(i).getChild(0).label().value().equals("IN") && t.getChild(i).getChild(1).label().value().equals("S")) ||
                                    ((t.getChild(i).getChild(0).label().value().equals("RB") || t.getChild(i).getChild(0).label().value().equals("ADVP")) && t.getChild(i).getChild(1).label().value().equals("IN") && t.getChild(i).getChild(2).label().value().equals("S"))) {
                                String aux = SentenceProcessor.setAux(true, isPresent);
                                String phrase = "This" + aux + SentenceUtils.listToString(t.getChild(i).yield()) + " .";
                                String phraseToDelete = SentenceUtils.listToString(t.getChild(i).yield()) + " ,";

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
     * extracts subordinate clauses starting with one of the conjunctions "as, since" that are not placed at the start of the input sentence
     * from the input sentence and transforms them into stand-alone context sentences,
     * returns true if a conjoined clause of this type was found in the input sentence
     *
     * @param coreContextSentence
     * @param parse
     * @param isOriginal
     * @param contextNumber
     * @return
     */
    public static boolean infixAsSinceSplit(CoreContextSentence coreContextSentence, Tree parse, boolean isOriginal, int contextNumber) {

        String sentence = SentenceUtils.listToString(parse.yield());
        boolean isPresent = SentenceProcessor.isPresent(coreContextSentence.getOriginal());
        boolean isSplit = false;

        for (Tree t : parse) {
            if (t.label().value().equals("NP") || t.label().value().equals("VP") || t.label().value().equals("S")) {
                for (int i = 0; i < t.getChildrenAsList().size(); i++) {
                    if (t.getChild(i).label().value().equals("SBAR")) {
                        if ((i == 0) && t.getChild(i).getChild(0).label().value().equals("IN") && t.getChild(i).getChild(1).label().value().equals("S")) {
                            if (t.getChild(i).getChild(0).getChild(0).label().value().equals("as") || t.getChild(i).getChild(0).getChild(0).label().value().equals("since") || t.getChild(i).getChild(0).getChild(0).label().value().equals("while")) {
                                for (int j = 0; j < t.getChild(i).getChild(1).getChildrenAsList().size() - 1; j++) {
                                    if (t.getChild(i).getChild(1).getChild(j).label().value().equals("NP")) {
                                        for (int k = j + 1; k < t.getChild(i).getChild(1).getChildrenAsList().size(); k++) {
                                            if (t.getChild(i).getChild(1).getChild(k).label().value().equals("VP")) {
                                                String aux;
                                                if (t.getChild(i).getChild(1).getChild(k).getChild(0).label().value().equals("VBP") || t.getChild(i).getChild(1).getChild(k).getChild(0).label().value().equals("VBZ")) {
                                                    aux = " is ";
                                                } else {
                                                    aux = " was ";
                                                }

                                                String phrase = "This" + aux + SentenceUtils.listToString(t.getChild(i).yield()) + " .";
                                                String phraseToDelete = SentenceUtils.listToString(t.getChild(i).yield());
                                                String[] tokensToDelete = phraseToDelete.split(" ");
                                                String[] tokens = sentence.split(" ");

                                                int tokensToDeleteCount = tokensToDelete.length;
                                                int tokensCount = tokens.length;

                                                if (tokensCount - tokensToDeleteCount > 4) {
                                                    SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                                    isSplit = true;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else if (i > 0) {
                            if (!t.getChild(i - 1).label().value().equals(",") && t.getChild(i).getChild(0).label().value().equals("IN") && t.getChild(i).getChild(1).label().value().equals("S")) {
                                if (t.getChild(i).getChild(0).getChild(0).label().value().equals("as") || t.getChild(i).getChild(0).getChild(0).label().value().equals("since") || t.getChild(i).getChild(0).getChild(0).label().value().equals("while")) {
                                    for (int j = 0; j < t.getChild(i).getChild(1).getChildrenAsList().size() - 1; j++) {
                                        if (t.getChild(i).getChild(1).getChild(j).label().value().equals("NP")) {
                                            for (int k = j + 1; k < t.getChild(i).getChild(1).getChildrenAsList().size(); k++) {
                                                if (t.getChild(i).getChild(1).getChild(k).label().value().equals("VP")) {
                                                    String aux;
                                                    if (t.getChild(i).getChild(1).getChild(k).getChild(0).label().value().equals("VBP") || t.getChild(i).getChild(1).getChild(k).getChild(0).label().value().equals("VBZ")) {
                                                        aux = " is ";
                                                    } else {
                                                        aux = " was ";
                                                    }

                                                    String phrase = "This" + aux + SentenceUtils.listToString(t.getChild(i).yield()) + " .";
                                                    String phraseToDelete = SentenceUtils.listToString(t.getChild(i).yield());
                                                    String[] tokensToDelete = phraseToDelete.split(" ");
                                                    String[] tokens = sentence.split(" ");

                                                    int tokensToDeleteCount = tokensToDelete.length;
                                                    int tokensCount = tokens.length;

                                                    if (tokensCount - tokensToDeleteCount > 3) {
                                                        SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                                        isSplit = true;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                for (int i = 0; i < t.getChildrenAsList().size() - 1; i++) {
                    if (t.getChildrenAsList().size() >= 2) {
                        if (t.getChild(i).label().value().equals("SBAR") && t.getChild(i + 1).label().value().equals(",")) {
                            if ((i == 0) && t.getChild(i).getChild(0).label().value().equals("IN") && t.getChild(i).getChild(1).label().value().equals("S")) {
                                if (t.getChild(i).getChild(0).getChild(0).label().value().equals("as") || t.getChild(i).getChild(0).getChild(0).label().value().equals("since") || t.getChild(i).getChild(0).getChild(0).label().value().equals("while")) {
                                    for (int j = 0; j < t.getChild(i).getChild(1).getChildrenAsList().size() - 1; j++) {
                                        if (t.getChild(i).getChild(1).getChild(j).label().value().equals("NP")) {
                                            for (int k = j + 1; k < t.getChild(i).getChild(1).getChildrenAsList().size(); k++) {
                                                if (t.getChild(i).getChild(1).getChild(k).label().value().equals("VP")) {
                                                    String aux;
                                                    if (t.getChild(i).getChild(1).getChild(k).getChild(0).label().value().equals("VBP") || t.getChild(i).getChild(1).getChild(k).getChild(0).label().value().equals("VBZ")) {
                                                        aux = " is ";
                                                    } else {
                                                        aux = " was ";
                                                    }

                                                    String phrase = "This" + aux + SentenceUtils.listToString(t.getChild(i).yield()) + " .";
                                                    String phraseToDelete = SentenceUtils.listToString(t.getChild(i).yield()) + " ,";
                                                    String[] tokensToDelete = phraseToDelete.split(" ");
                                                    String[] tokens = sentence.split(" ");
                                                    int tokensToDeleteCount = tokensToDelete.length;
                                                    int tokensCount = tokens.length;

                                                    if (tokensCount - tokensToDeleteCount > 4) {
                                                        SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                                        isSplit = true;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (t.getChildrenAsList().size() >= 3) {
                    for (int i = 0; i < t.getChildrenAsList().size() - 2; i++) {
                        if (t.getChild(i).label().value().equals(":") && t.getChild(i + 1).label().value().equals("SBAR") && t.getChild(i + 2).label().value().equals(",")) {
                            if (t.getChild(i + 1).getChild(0).label().value().equals("IN") && t.getChild(i + 1).getChild(1).label().value().equals("S")) {
                                if (t.getChild(i + 1).getChild(0).getChild(0).label().value().equals("as") || t.getChild(i + 1).getChild(0).getChild(0).label().value().equals("since") || t.getChild(i + 1).getChild(0).getChild(0).label().value().equals("while")) {
                                    String aux = SentenceProcessor.setAux(true, isPresent);
                                    String phrase = "This " + aux + SentenceUtils.listToString(t.getChild(i + 1).yield()) + " .";
                                    String phraseToDelete = SentenceUtils.listToString(t.getChild(i + 1).yield()) + " ,";

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


    /**
     * extracts subordinate clauses starting with one of the conjunctions "though, although, because" that are not placed at the start of the input sentence
     * from the input sentence and transforms them into stand-alone context sentences,
     * returns true if a conjoined clause of this type was found in the input sentence
     *
     * @param coreContextSentence
     * @param parse
     * @param isOriginal
     * @param contextNumber
     * @return
     */
    public static boolean infixBecauseThoughAlthoughSplit(CoreContextSentence coreContextSentence, Tree parse, boolean isOriginal, int contextNumber) {

        String sentence = SentenceUtils.listToString(parse.yield());
        boolean isPresent = SentenceProcessor.isPresent(coreContextSentence.getOriginal());
        boolean isSplit = false;

        for (Tree t : parse) {
            if (t.label().value().equals("NP") || t.label().value().equals("VP") || t.label().value().equals("S") || t.label().value().equals("ADVP")) {
                for (int i = 0; i < t.getChildrenAsList().size() - 1; i++) {
                    if (t.getChild(i).label().value().equals(",") && t.getChild(i + 1).label().value().equals("SBAR")) {
                        if ((t.getChild(i + 1).getChild(0).label().value().equals("IN") && t.getChild(i + 1).getChild(1).label().value().equals("S"))) {
                            if (t.getChild(i + 1).getChild(0).getChild(0).label().value().equals("because") || t.getChild(i + 1).getChild(0).getChild(0).label().value().equals("though") || t.getChild(i + 1).getChild(0).getChild(0).label().value().equals("although")) {
                                String aux = SentenceProcessor.setAux(true, isPresent);
                                String phrase = "This" + aux + SentenceUtils.listToString(t.getChild(i + 1).yield()) + " .";
                                String phraseToDelete = "";

                                if (i == t.getChildrenAsList().size() - 2) {
                                    phraseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 1).yield());
                                } else {
                                    phraseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 1).yield()) + " ,";
                                }

                                if (!sentence.equals(phrase)) {
                                    SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                    isSplit = true;
                                }
                            }
                        }
                        if (t.getChild(i + 1).getChildrenAsList().size() >= 3) {
                            if ((t.getChild(i + 1).getChild(0).label().value().equals("RB") || t.getChild(i + 1).getChild(0).label().value().equals("ADVP")) && t.getChild(i + 1).getChild(1).label().value().equals("IN") && t.getChild(i + 1).getChild(2).label().value().equals("S")) {
                                if (t.getChild(i + 1).getChild(1).getChild(0).label().value().equals("because") || t.getChild(i + 1).getChild(1).getChild(0).label().value().equals("though") || t.getChild(i + 1).getChild(1).getChild(0).label().value().equals("although")) {
                                    String aux = SentenceProcessor.setAux(true, isPresent);
                                    String phrase = "This" + aux + SentenceUtils.listToString(t.getChild(i + 1).yield()) + " .";
                                    String phraseToDelete = "";

                                    if (i == t.getChildrenAsList().size() - 2) {
                                        phraseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 1).yield());
                                    } else {
                                        phraseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 1).yield()) + " ,";
                                    }

                                    if (!sentence.equals(phrase)) {
                                        SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                        isSplit = true;
                                    }
                                }
                            }
                        }
                        //else if (t.getChild(i).getChild(0).label().value().equals("RB") && t.getChild(i).getChild(1).label().value().equals("IN") && t.getChild(i).getChild(2).label().value().equals("S")) {}
                    }
                }
            }
        }
        return isSplit;
    }


    /**
     * extracts correlated clauses starting with "if"
     * from the input sentence and transforms them into stand-alone context sentences,
     * returns true if a conjoined clause of this type was found in the input sentence
     *
     * @param coreContextSentence
     * @param parse
     * @param isOriginal
     * @param contextNumber
     * @return
     */
    public static boolean ifSplit(CoreContextSentence coreContextSentence, Tree parse, boolean isOriginal, int contextNumber) {

        String sentence = SentenceUtils.listToString(parse.yield());
        boolean isPresent = SentenceProcessor.isPresent(coreContextSentence.getOriginal());
        boolean isSplit = false;

        for (Tree t : parse) {
            if (t.getChildrenAsList().size() >= 2) {
                for (int i = 0; i < t.getChildrenAsList().size() - 1; i++) {
                    if (t.getChild(i).label().value().equals(",") && t.getChild(i + 1).label().value().equals("SBAR") && i == t.getChildrenAsList().size() - 2) {
                        if (t.getChild(i + 1).getChildrenAsList().size() >= 2) {
                            if (t.getChild(i + 1).getChild(0).label().value().equals("RB") && t.getChild(i + 1).getChild(1).label().value().equals("IN")) {
                                if (t.getChild(i + 1).getChild(1).getChild(0).label().value().equals("if")) {
                                    String aux = SentenceProcessor.setAux(true, isPresent);
                                    String phrase = "This" + aux + SentenceUtils.listToString(t.getChild(i + 1).yield()) + " .";
                                    String phraseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 1).yield());

                                    SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                    isSplit = true;
                                }
                            }
                        }
                        if (t.getChild(i + 1).getChild(0).label().value().equals("IN")) {
                            if (t.getChild(i + 1).getChild(0).getChild(0).label().value().equals("if")) {
                                String aux = SentenceProcessor.setAux(true, isPresent);
                                String phrase = "This" + aux + SentenceUtils.listToString(t.getChild(i + 1).yield()) + " .";
                                String phraseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 1).yield());

                                SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                isSplit = true;
                            }
                        }
                    }
                }
            }

            if (t.getChildrenAsList().size() >= 3) {
                for (int i = 0; i < t.getChildrenAsList().size() - 2; i++) {
                    if (t.getChild(i).label().value().equals(",") && t.getChild(i + 1).label().value().equals("SBAR") && t.getChild(i + 2).label().value().equals(",")) {
                        if (t.getChild(i + 1).getChildrenAsList().size() >= 2) {
                            if (t.getChild(i + 1).getChild(0).label().value().equals("RB") && t.getChild(i + 1).getChild(1).label().value().equals("IN")) {
                                if (t.getChild(i + 1).getChild(1).getChild(0).label().value().equals("if")) {
                                    String aux = SentenceProcessor.setAux(true, isPresent);
                                    String phrase = "This" + aux + SentenceUtils.listToString(t.getChild(i + 1).yield()) + " .";
                                    String phraseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 1).yield()) + " ,";

                                    SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                    isSplit = true;
                                }
                            }
                        }
                        if (t.getChild(i + 1).getChild(0).label().value().equals("IN")) {
                            if (t.getChild(i + 1).getChild(0).getChild(0).label().value().equals("if")) {
                                String aux = SentenceProcessor.setAux(true, isPresent);
                                String phrase = "This" + aux + SentenceUtils.listToString(t.getChild(i + 1).yield()) + " .";
                                String phraseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 1).yield()) + " ,";

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
     * extracts subordinate clauses starting with "while" that is followed by a gerund
     * from the input sentence and transforms them into stand-alone context sentences,
     * returns true if a conjoined clause of this type was found in the input sentence
     *
     * @param coreContextSentence
     * @param parse
     * @param isOriginal
     * @param contextNumber
     * @return
     */
    public static boolean extractWhilePlusParticiple(CoreContextSentence coreContextSentence, Tree parse, boolean isOriginal, int contextNumber) {

        String sentence = SentenceUtils.listToString(parse.yield());
        boolean isSplit = false;
        boolean isPresent = SentenceProcessor.isPresent(coreContextSentence.getOriginal());
        String aux = SentenceProcessor.setAux(true, isPresent);

        for (Tree t : parse) {
            if (t.label().value().equals("PP")) {
                if (t.getChildrenAsList().size() >= 2) {
                    for (int i = 0; i < t.getChildrenAsList().size() - 1; i++) {
                        if (t.getChild(i).label().value().equals("IN") && t.getChild(i).getChild(0).label().value().equals("while") && t.getChild(i + 1).label().value().equals("S")) {
                            if (t.getChild(i + 1).getChild(0).label().value().equals("VP")) {
                                if (t.getChild(i + 1).getChild(0).getChildrenAsList().size() >= 2) {
                                    if (t.getChild(i + 1).getChild(0).getChild(0).label().value().equals("VBG") && (t.getChild(i + 1).getChild(0).getChild(1).label().value().equals("S") || t.getChild(i + 1).getChild(0).getChild(1).label().value().equals("NP"))) {

                                        String phrase = "This" + aux + SentenceUtils.listToString(t.yield()) + " .";
                                        String phraseToDelete = SentenceUtils.listToString(t.yield());
                                        String[] tokensToDelete = phraseToDelete.split(" ");
                                        String[] tokens = sentence.split(" ");

                                        int tokensToDeleteCount = tokensToDelete.length;
                                        int tokensCount = tokens.length;

                                        if (tokensCount - tokensToDeleteCount > 3) {
                                            SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                            isSplit = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return isSplit;
    }


    /**
     * extracts subordinate clauses starting with the conjunctions "so"
     * from the input sentence and transforms them into stand-alone context sentences,
     * returns true if a conjoined clause of this type was found in the input sentence
     *
     * @param coreContextSentence
     * @param parse
     * @param isOriginal
     * @param contextNumber
     * @return
     */
    public static boolean extractSo(CoreContextSentence coreContextSentence, Tree parse, boolean isOriginal, int contextNumber) {

        String sentence = SentenceUtils.listToString(parse.yield());
        boolean isSplit = false;

        for (Tree t : parse) {
            if (t.getChildrenAsList().size() >= 3) {
                for (int i = 0; i < t.getChildrenAsList().size() - 2; i++) {
                    if (t.getChild(i).label().value().equals(",") && t.getChild(i + 1).label().value().equals("IN") && t.getChild(i + 2).label().value().equals("S")) {
                        if (t.getChild(i + 1).getChild(0).label().value().equals("so")) {
                            String phrase = SentenceUtils.listToString(t.getChild(i + 1).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 2).yield()) + " .";
                            String phraseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 1).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 2).yield());

                            SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                            isSplit = true;
                        }
                    }
                }
            }
        }
        return isSplit;
    }
}
