/*
 * ==========================License-Start=============================
 * sentence_simplification : ParticipialPhraseExtractor
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
 * Class for extracting participial phrases that are segregated through punctuation
 *
 * @author christina
 */
public class ParticipialPhraseExtractor {

    /**
     * extracts participial phrases that are offset by commas from the input sentence and transforms them into stand-alone context sentences,
     * returns true if such a participial phrase was found in the input sentence
     *
     * @param coreContextSentence
     * @param parse
     * @param isOriginal
     * @param contextNumber
     * @return
     */
    public static boolean extractPresentAndPastParticiples(CoreContextSentence coreContextSentence, Tree parse, boolean isOriginal, int contextNumber) {

        String sentence = SentenceUtils.listToString(parse.yield());
        boolean isSplit = false;
        boolean isPresent = SentenceProcessor.isPresent(coreContextSentence.getOriginal());

        for (Tree t : parse) {
            if (t.label().value().equals("NP")) {
                if (t.getChildrenAsList().size() >= 3) {
                    for (int i = 0; i < t.getChildrenAsList().size() - 2; i++) {
                        if (t.getChild(i).label().value().equals("NP") && t.getChild(i + 1).label().value().equals(",") && t.getChild(i + 2).label().value().equals("VP") && i == t.getChildrenAsList().size() - 3) {
                            if (t.getChild(i + 2).getChild(0).label().value().equals("VBN") || (t.getChild(i + 2).getChild(0).label().value().equals("ADVP") && t.getChild(i + 2).getChild(1).label().value().equals("VBN"))) {
                                List<LabeledWord> label = t.getChild(i).labeledYield();
                                boolean number = SentenceProcessor.isSingular(label.get(label.size() - 1));
                                String aux = SentenceProcessor.setAux(number, isPresent);
                                String phrase = SentenceUtils.listToString(t.getChild(i).yield()) + aux + SentenceUtils.listToString(t.getChild(i + 2).yield()) + " .";
                                String phraseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 2).yield());

                                SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                isSplit = true;
                            }
                        }
                    }
                }

                if (t.getChildrenAsList().size() >= 4) {
                    for (int i = 0; i < t.getChildrenAsList().size() - 3; i++) {
                        if (t.getChild(i).label().value().equals("NP") && t.getChild(i + 1).label().value().equals(",") && t.getChild(i + 2).label().value().equals("VP") && t.getChild(i + 3).label().value().equals(",")) {
                            if (t.getChild(i + 2).getChild(0).label().value().equals("VBN") || (t.getChild(i + 2).getChild(0).label().value().equals("ADVP") && t.getChild(i + 2).getChild(1).label().value().equals("VBN"))) {
                                List<LabeledWord> label = t.getChild(i).labeledYield();
                                boolean number = SentenceProcessor.isSingular(label.get(label.size() - 1));
                                String aux = SentenceProcessor.setAux(number, isPresent);
                                String phrase = SentenceUtils.listToString(t.getChild(i).yield()) + aux + SentenceUtils.listToString(t.getChild(i + 2).yield()) + " .";
                                String phraseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 2).yield()) + " ,";

                                SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                isSplit = true;
                            }
                        }

                        if (t.getChild(i).label().value().equals("NP") && t.getChild(i + 1).label().value().equals("PP") && t.getChild(i + 2).label().value().equals(",") && t.getChild(i + 3).label().value().equals("VP") && i == t.getChildrenAsList().size() - 4) {
                            if (t.getChild(i + 3).getChild(0).label().value().equals("VBN") || (t.getChild(i + 3).getChild(0).label().value().equals("ADVP") && t.getChild(i + 3).getChild(1).label().value().equals("VBN"))) {
                                List<LabeledWord> label = t.getChild(i).labeledYield();
                                boolean number = SentenceProcessor.isSingular(label.get(label.size() - 1));
                                String aux = SentenceProcessor.setAux(number, isPresent);
                                String phrase = SentenceUtils.listToString(t.getChild(i).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 1).yield()) + aux + SentenceUtils.listToString(t.getChild(i + 2).yield()) + " .";
                                String phraseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 3).yield());

                                SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                isSplit = true;
                            }
                        }

                        if (t.getChild(i).label().value().equals("NP") && t.getChild(i + 1).label().value().equals("VP") && t.getChild(i + 2).label().value().equals(",") && t.getChild(i + 3).label().value().equals("VP") && i == t.getChildrenAsList().size() - 4) {
                            if (t.getChild(i + 3).getChild(0).label().value().equals("VBN") || (t.getChild(i + 3).getChild(0).label().value().equals("ADVP") && t.getChild(i + 3).getChild(1).label().value().equals("VBN"))) {
                                List<LabeledWord> label = t.getChild(i).labeledYield();
                                boolean number = SentenceProcessor.isSingular(label.get(label.size() - 1));
                                String aux = SentenceProcessor.setAux(number, isPresent);
                                String phrase = "This" + aux + "when being " + SentenceUtils.listToString(t.getChild(i + 3).yield()) + " .";
                                String phraseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 3).yield());

                                SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                isSplit = true;
                            }
                        }
                    }
                }

                if (t.getChildrenAsList().size() >= 5) {
                    for (int i = 0; i < t.getChildrenAsList().size() - 4; i++) {
                        if (t.getChild(i).label().value().equals("NP") && t.getChild(i + 1).label().value().equals("PP") && t.getChild(i + 2).label().value().equals(",") && t.getChild(i + 3).label().value().equals("VP") && t.getChild(i + 4).label().value().equals(",")) {
                            if (t.getChild(i + 3).getChild(0).label().value().equals("VBN") || (t.getChild(i + 3).getChild(0).label().value().equals("ADVP") && t.getChild(i + 3).getChild(1).label().value().equals("VBN"))) {
                                List<LabeledWord> label = t.getChild(i).labeledYield();
                                boolean number = SentenceProcessor.isSingular(label.get(label.size() - 1));
                                String aux = SentenceProcessor.setAux(number, isPresent);
                                String phrase = SentenceUtils.listToString(t.getChild(i).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 1).yield()) + aux + SentenceUtils.listToString(t.getChild(i + 3).yield()) + " .";
                                String phraseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 3).yield()) + " ,";

                                SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                isSplit = true;
                            }
                        }

                        if (t.getChild(i).label().value().equals("NP") && t.getChild(i + 1).label().value().equals("VP") && t.getChild(i + 2).label().value().equals(",") && t.getChild(i + 3).label().value().equals("VP") && t.getChild(i + 4).label().value().equals(",")) {
                            if (t.getChild(i + 3).getChild(0).label().value().equals("VBN") || (t.getChild(i + 3).getChild(0).label().value().equals("ADVP") && t.getChild(i + 3).getChild(1).label().value().equals("VBN"))) {
                                List<LabeledWord> label = t.getChild(i).labeledYield();
                                boolean number = SentenceProcessor.isSingular(label.get(label.size() - 1));
                                String aux = SentenceProcessor.setAux(number, isPresent);

                                String phrase = "This" + aux + "when being " + SentenceUtils.listToString(t.getChild(i + 3).yield()) + " .";
                                String phraseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 3).yield()) + " ,";

                                SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                isSplit = true;
                            }
                        }
                    }
                }
            }

            if (t.getChildrenAsList().size() >= 2) {
                for (int i = 0; i < t.getChildrenAsList().size() - 1; i++) {
                    if (t.getChild(i).label().value().equals(",") && t.getChild(i + 1).label().value().equals("VP")) {
                        if (t.getChild(i + 1).getChild(0).label().value().equals("VBG")) {
                            if (i == t.getChildrenAsList().size() - 2) {
                                String aux = SentenceProcessor.setAux(true, isPresent);
                                String phrase = "This" + aux + "when " + SentenceUtils.listToString(t.getChild(i + 1).yield()) + " .";
                                String phraseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 1).yield());

                                SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                isSplit = true;
                            }
                            for (int j = i + 1; j < t.getChildrenAsList().size() - 3; j++) {
                                if (!(t.getChild(j + 1).label().value().equals(",") && t.getChild(j + 2).label().value().equals("CC") && t.getChild(j + 3).label().value().equals("VP"))) {
                                    String aux = SentenceProcessor.setAux(true, isPresent);
                                    String phrase = "This" + aux + "when " + SentenceUtils.listToString(t.getChild(i + 1).yield()) + " .";
                                    String phraseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 1).yield());

                                    SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                    isSplit = true;
                                }
                            }
                        }
                    }
                    if (t.getChild(i).label().value().equals(",") && t.getChild(i + 1).label().value().equals("S")) {
                        if (t.getChild(i + 1).getChild(0).label().value().equals("VP") && t.getChild(i + 1).getChild(0).getChild(0).label().value().equals("VBG")) {
                            String aux = SentenceProcessor.setAux(true, isPresent);
                            String phrase = "This" + aux + "when " + SentenceUtils.listToString(t.getChild(i + 1).yield()) + " .";
                            String phraseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 1).yield());

                            SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                            isSplit = true;
                        }
                        if (t.getChild(i + 1).getChildrenAsList().size() >= 2) {
                            if (t.getChild(i + 1).getChild(0).label().value().equals("ADVP") && t.getChild(i + 1).getChild(1).label().value().equals("VP")) {
                                if (t.getChild(i + 1).getChild(0).getChild(0).label().value().equals("RB") && t.getChild(i + 1).getChild(1).getChild(0).label().value().equals("VBG")) {
                                    String aux = SentenceProcessor.setAux(true, isPresent);
                                    String phrase = "This" + aux + "when " + SentenceUtils.listToString(t.getChild(i + 1).yield()) + " .";
                                    String phraseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 1).yield());

                                    SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                    isSplit = true;
                                }
                            }
                        }
                        if (t.getChild(i + 1).getChild(0).label().value().equals("VP")) {
                            if (t.getChild(i + 1).getChild(0).getChildrenAsList().size() >= 2) {
                                if (t.getChild(i + 1).getChild(0).getChild(0).label().value().equals("ADVP") && t.getChild(i + 1).getChild(0).getChild(1).label().value().equals("VBG")) {
                                    String aux = SentenceProcessor.setAux(true, isPresent);
                                    String phrase = "This" + aux + "when " + SentenceUtils.listToString(t.getChild(i + 1).yield()) + " .";
                                    String phraseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 1).yield());

                                    SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                    isSplit = true;
                                }
                            }
                            if (t.getChild(i + 1).getChild(0).getChildrenAsList().size() >= 4) {
                                if (t.getChild(i + 1).getChild(0).getChild(0).label().value().equals("VP") && t.getChild(i + 1).getChild(0).getChild(1).label().value().equals(",") && t.getChild(i + 1).getChild(0).getChild(2).label().value().equals("CC") && t.getChild(i + 1).getChild(0).getChild(3).label().value().equals("VP")) {
                                    if (t.getChild(i + 1).getChild(0).getChild(0).getChild(0).label().value().equals("VBG") && t.getChild(i + 1).getChild(0).getChild(3).getChild(0).label().value().equals("VBG")) {
                                        String aux = SentenceProcessor.setAux(true, isPresent);
                                        String phrase = "This" + aux + "when " + SentenceUtils.listToString(t.getChild(i + 1).yield()) + " .";
                                        String phraseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 1).yield());

                                        SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                        isSplit = true;
                                    }
                                }
                            }
                            if (t.getChild(i + 1).getChild(0).getChildrenAsList().size() >= 3) {
                                if (t.getChild(i + 1).getChild(0).getChild(0).label().value().equals("VP") && t.getChild(i + 1).getChild(0).getChild(1).label().value().equals("CC") && t.getChild(i + 1).getChild(0).getChild(2).label().value().equals("VP")) {
                                    if (t.getChild(i + 1).getChild(0).getChild(0).getChild(0).label().value().equals("VBG") && t.getChild(i + 1).getChild(0).getChild(2).getChild(0).label().value().equals("VBG")) {
                                        String aux = SentenceProcessor.setAux(true, isPresent);
                                        String phrase = "This" + aux + "when " + SentenceUtils.listToString(t.getChild(i + 1).yield()) + " .";
                                        String phraseToDelete = SentenceUtils.listToString(t.getChild(i + 1).yield());

                                        SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                        isSplit = true;
                                    }
                                }
                            }
                            if (t.getChild(i + 1).getChild(0).getChildrenAsList().size() >= 6) {
                                if (t.getChild(i + 1).getChild(0).getChild(0).label().value().equals("VP") && t.getChild(i + 1).getChild(0).getChild(1).label().value().equals(",") && t.getChild(i + 1).getChild(0).getChild(2).label().value().equals("VP") && t.getChild(i + 1).getChild(0).getChild(3).label().value().equals(",") && t.getChild(i + 1).getChild(0).getChild(4).label().value().equals("CC") && t.getChild(i + 1).getChild(0).getChild(5).label().value().equals("VP")) {
                                    if (t.getChild(i + 1).getChild(0).getChild(0).getChild(0).label().value().equals("VBG") && t.getChild(i + 1).getChild(0).getChild(2).getChild(0).label().value().equals("VBG") && t.getChild(i + 1).getChild(0).getChild(5).getChild(0).label().value().equals("VBG")) {
                                        String aux = SentenceProcessor.setAux(true, isPresent);
                                        String phrase = "This" + aux + "when " + SentenceUtils.listToString(t.getChild(i + 1).yield()) + " .";
                                        String phraseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 1).yield());

                                        SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                        isSplit = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            for (int i = 0; i < t.getChildrenAsList().size(); i++) {
                if (t.getChild(i).label().value().equals("S") && i == t.getChildrenAsList().size() - 1 && !t.getChild(i).ancestor(1, parse).label().value().equals("SBAR") && !t.getChild(i).ancestor(1, parse).label().value().equals("PP") && !t.getChild(i).ancestor(1, parse).label().value().equals("VP")) {
                    if (t.getChild(i).getChild(0).label().value().equals("VP") && (t.getChild(i).getChild(0).getChild(0).label().value().equals("VBN") || t.getChild(i).getChild(0).getChild(0).label().value().equals("VBG"))) {
                        String aux = SentenceProcessor.setAux(true, isPresent);
                        String phrase = "";
                        if (t.getChild(i).getChild(0).getChild(0).label().value().equals("VBN")) {
                            phrase = "This" + aux + "when being " + SentenceUtils.listToString(t.getChild(i).yield()) + " .";
                        } else {
                            phrase = "This" + aux + "when " + SentenceUtils.listToString(t.getChild(i).yield()) + " .";
                        }
                        String phraseToDelete = SentenceUtils.listToString(t.getChild(i).yield());
                        String[] tokensToDelete = phraseToDelete.split(" ");
                        String[] tokens = sentence.split(" ");

                        int tokensToDeleteCount = tokensToDelete.length;
                        int tokensCount = tokens.length;

                        boolean extract = true;
                        List<Tree> a = coreContextSentence.getAttribution();
                        for (Tree tr : a) {
                            String attr = SentenceUtils.listToString(tr.yield());
                            String ph = SentenceUtils.listToString(t.getChild(0).yield());
                            if (attr.contains(ph)) {
                                extract = false;
                            }
                        }

                        if (extract && (tokensCount - tokensToDeleteCount > 4)) {
                            SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                            isSplit = true;
                        }
                    }

                    if (t.getChild(i).getChild(0).label().value().equals("VP") && (t.getChild(i).getChild(0).getChild(0).label().value().equals("ADVP") && (t.getChild(i).getChild(0).getChild(1).label().value().equals("VBN") || t.getChild(i).getChild(0).getChild(1).label().value().equals("VBG")))) {
                        String aux = SentenceProcessor.setAux(true, isPresent);
                        String phrase = "";
                        if (t.getChild(i).getChild(0).getChild(1).label().value().equals("VBN")) {
                            phrase = "This" + aux + "when being " + SentenceUtils.listToString(t.getChild(i).yield()) + " .";
                        } else {
                            phrase = "This" + aux + "when " + SentenceUtils.listToString(t.getChild(i).yield()) + " .";
                        }
                        String phraseToDelete = SentenceUtils.listToString(t.getChild(i).yield());
                        String[] tokensToDelete = phraseToDelete.split(" ");
                        String[] tokens = sentence.split(" ");

                        int tokensToDeleteCount = tokensToDelete.length;
                        int tokensCount = tokens.length;

                        boolean extract = true;
                        List<Tree> a = coreContextSentence.getAttribution();
                        for (Tree tr : a) {
                            String attr = SentenceUtils.listToString(tr.yield());
                            String ph = SentenceUtils.listToString(t.getChild(0).yield());
                            if (attr.contains(ph)) {
                                extract = false;
                            }
                        }

                        if (extract && (tokensCount - tokensToDeleteCount > 4)) {
                            SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                            isSplit = true;
                        }
                    }
                }
            }

            for (int i = 0; i < t.getChildrenAsList().size() - 1; i++) {
                if (t.getChild(i).label().value().equals("S") && t.getChild(i + 1).label().value().equals(",") && !t.getChild(i).ancestor(1, parse).label().value().equals("SBAR") && !t.getChild(i).ancestor(1, parse).label().value().equals("PP") && !t.getChild(i).ancestor(1, parse).label().value().equals("VP")) {
                    if (t.getChild(i).getChild(0).label().value().equals("VP") && (t.getChild(i).getChild(0).getChild(0).label().value().equals("VBN") || t.getChild(i).getChild(0).getChild(0).label().value().equals("VBG"))) {
                        String aux = SentenceProcessor.setAux(true, isPresent);
                        String phrase = "";
                        if (t.getChild(i).getChild(0).getChild(0).label().value().equals("VBN")) {
                            phrase = "This" + aux + "when being " + SentenceUtils.listToString(t.getChild(i).yield()) + " .";
                        } else {
                            phrase = "This" + aux + "when " + SentenceUtils.listToString(t.getChild(i).yield()) + " .";
                        }
                        String phraseToDelete = SentenceUtils.listToString(t.getChild(i).yield()) + " , ";
                        String[] tokensToDelete = phraseToDelete.split(" ");
                        String[] tokens = sentence.split(" ");

                        int tokensToDeleteCount = tokensToDelete.length;
                        int tokensCount = tokens.length;

                        boolean extract = true;
                        List<Tree> a = coreContextSentence.getAttribution();
                        for (Tree tr : a) {
                            String attr = SentenceUtils.listToString(tr.yield());
                            String ph = SentenceUtils.listToString(t.getChild(0).yield());
                            if (attr.contains(ph)) {
                                extract = false;
                            }
                        }

                        if (extract && (tokensCount - tokensToDeleteCount > 4)) {
                            SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                            isSplit = true;
                        }
                    }

                    if (t.getChild(i).getChildrenAsList().size() >= 2) {
                        if (t.getChild(i).getChild(0).label().value().equals("ADVP") && t.getChild(i).getChild(1).label().value().equals("VP") && (t.getChild(i).getChild(1).getChild(0).label().value().equals("VBN") || t.getChild(i).getChild(1).getChild(0).label().value().equals("VBG"))) {
                            String aux = SentenceProcessor.setAux(true, isPresent);
                            String phrase = "";
                            if (t.getChild(i).getChild(1).getChild(0).label().value().equals("VBN")) {
                                phrase = "This" + aux + "when being " + SentenceUtils.listToString(t.getChild(i).yield()) + " .";
                            } else {
                                phrase = "This" + aux + "when " + SentenceUtils.listToString(t.getChild(i).yield()) + " .";
                            }
                            String phraseToDelete = SentenceUtils.listToString(t.getChild(i).yield()) + " , ";
                            String[] tokensToDelete = phraseToDelete.split(" ");
                            String[] tokens = sentence.split(" ");

                            int tokensToDeleteCount = tokensToDelete.length;
                            int tokensCount = tokens.length;

                            boolean extract = true;
                            List<Tree> a = coreContextSentence.getAttribution();
                            for (Tree tr : a) {
                                String attr = SentenceUtils.listToString(tr.yield());
                                String ph = SentenceUtils.listToString(t.getChild(0).yield());
                                if (attr.contains(ph)) {
                                    extract = false;
                                }
                            }

                            if (extract && (tokensCount - tokensToDeleteCount > 5)) {
                                SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                isSplit = true;
                            }
                        }
                    }

                    if (t.getChild(i).getChild(0).label().value().equals("VP") && t.getChild(i).getChild(0).getChild(0).label().value().equals("ADVP") && (t.getChild(i).getChild(0).getChild(1).label().value().equals("VBN") || t.getChild(i).getChild(0).getChild(1).label().value().equals("VBG"))) {
                        String aux = SentenceProcessor.setAux(true, isPresent);
                        String phrase = "";
                        if (t.getChild(i).getChild(0).getChild(1).label().value().equals("VBN")) {
                            phrase = "This" + aux + "when being " + SentenceUtils.listToString(t.getChild(i).yield()) + " .";
                        } else {
                            phrase = "This" + aux + "when " + SentenceUtils.listToString(t.getChild(i).yield()) + " .";
                        }
                        String phraseToDelete = SentenceUtils.listToString(t.getChild(i).yield()) + " , ";
                        String[] tokensToDelete = phraseToDelete.split(" ");
                        String[] tokens = sentence.split(" ");

                        int tokensToDeleteCount = tokensToDelete.length;
                        int tokensCount = tokens.length;

                        boolean extract = true;
                        List<Tree> a = coreContextSentence.getAttribution();
                        for (Tree tr : a) {
                            String attr = SentenceUtils.listToString(tr.yield());
                            String ph = SentenceUtils.listToString(t.getChild(0).yield());
                            if (attr.contains(ph)) {
                                extract = false;
                            }
                        }

                        if (extract && (tokensCount - tokensToDeleteCount > 4)) {
                            SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                            isSplit = true;
                        }
                    }
                }
            }

            if (t.label().value().equals("S") && !t.ancestor(1, parse).label().value().equals("SBAR") && !t.ancestor(1, parse).label().value().equals("PP") && !t.ancestor(1, parse).label().value().equals("VP")) {
                if (t.getChild(0).label().value().equals("VP")) {
                    if (t.getChild(0).getChildrenAsList().size() >= 3) {
                        if (t.getChild(0).getChild(0).label().value().equals("VP") && t.getChild(0).getChild(1).label().value().equals("CC") && t.getChild(0).getChild(2).label().value().equals("VP")) {
                            if ((t.getChild(0).getChild(0).getChild(0).label().value().equals("VBG") || t.getChild(0).getChild(0).getChild(1).label().value().equals("VBG")) && (t.getChild(0).getChild(2).getChild(0).label().value().equals("VBG") || t.getChild(0).getChild(2).getChild(1).label().value().equals("VBG"))) {
                                String aux = SentenceProcessor.setAux(true, isPresent);
                                String phrase = "This" + aux + "when " + SentenceUtils.listToString(t.yield()) + " .";
                                String phraseToDelete = SentenceUtils.listToString(t.yield());
                                String[] tokensToDelete = phraseToDelete.split(" ");
                                String[] tokens = sentence.split(" ");

                                int tokensToDeleteCount = tokensToDelete.length;
                                int tokensCount = tokens.length;

                                boolean extract = true;
                                List<Tree> a = coreContextSentence.getAttribution();
                                for (Tree tr : a) {
                                    String attr = SentenceUtils.listToString(tr.yield());
                                    String ph = SentenceUtils.listToString(t.getChild(0).yield());
                                    if (attr.contains(ph)) {
                                        extract = false;
                                    }
                                }

                                if (extract && (tokensCount - tokensToDeleteCount > 4)) {
                                    SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                    isSplit = true;
                                }
                            }
                            if ((t.getChild(0).getChild(0).getChild(0).label().value().equals("VBN") || t.getChild(0).getChild(0).getChild(1).label().value().equals("VBN")) && (t.getChild(0).getChild(2).getChild(0).label().value().equals("VBN") || t.getChild(0).getChild(2).getChild(1).label().value().equals("VBN"))) {
                                String aux = SentenceProcessor.setAux(true, isPresent);
                                String phrase = "This" + aux + "when being " + SentenceUtils.listToString(t.yield()) + " .";
                                String phraseToDelete = SentenceUtils.listToString(t.yield());
                                String[] tokensToDelete = phraseToDelete.split(" ");
                                String[] tokens = sentence.split(" ");

                                int tokensToDeleteCount = tokensToDelete.length;
                                int tokensCount = tokens.length;

                                boolean extract = true;
                                List<Tree> a = coreContextSentence.getAttribution();
                                for (Tree tr : a) {
                                    String attr = SentenceUtils.listToString(tr.yield());
                                    String ph = SentenceUtils.listToString(t.getChild(0).yield());
                                    if (attr.contains(ph)) {
                                        extract = false;
                                    }
                                }

                                if (extract && (tokensCount - tokensToDeleteCount > 5)) {
                                    SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                    isSplit = true;
                                }
                            }
                        }
                    }
                    if (t.getChild(0).getChildrenAsList().size() >= 4) {
                        if (t.getChild(0).getChild(0).label().value().equals("VP") && t.getChild(0).getChild(1).label().value().equals(",") && t.getChild(0).getChild(2).label().value().equals("CC") && t.getChild(0).getChild(3).label().value().equals("VP")) {
                            if ((t.getChild(0).getChild(0).getChild(0).label().value().equals("VBG") || t.getChild(0).getChild(0).getChild(1).label().value().equals("VBG")) && (t.getChild(0).getChild(3).getChild(0).label().value().equals("VBG") || t.getChild(0).getChild(3).getChild(1).label().value().equals("VBG"))) {
                                String aux = SentenceProcessor.setAux(true, isPresent);
                                String phrase = "This" + aux + "when " + SentenceUtils.listToString(t.yield()) + " .";
                                String phraseToDelete = SentenceUtils.listToString(t.yield());
                                String[] tokensToDelete = phraseToDelete.split(" ");
                                String[] tokens = sentence.split(" ");

                                int tokensToDeleteCount = tokensToDelete.length;
                                int tokensCount = tokens.length;

                                boolean extract = true;
                                List<Tree> a = coreContextSentence.getAttribution();
                                for (Tree tr : a) {
                                    String attr = SentenceUtils.listToString(tr.yield());
                                    String ph = SentenceUtils.listToString(t.getChild(0).yield());
                                    if (attr.contains(ph)) {
                                        extract = false;
                                    }
                                }

                                if (extract && (tokensCount - tokensToDeleteCount > 4)) {
                                    SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                    isSplit = true;
                                }
                            }
                            if ((t.getChild(0).getChild(0).getChild(0).label().value().equals("VBN") || t.getChild(0).getChild(0).getChild(1).label().value().equals("VBN")) && (t.getChild(0).getChild(3).getChild(0).label().value().equals("VBN") || t.getChild(0).getChild(3).getChild(1).label().value().equals("VBN"))) {
                                String aux = SentenceProcessor.setAux(true, isPresent);
                                String phrase = "This" + aux + "when being " + SentenceUtils.listToString(t.yield()) + " .";
                                String phraseToDelete = SentenceUtils.listToString(t.yield());
                                String[] tokensToDelete = phraseToDelete.split(" ");
                                String[] tokens = sentence.split(" ");

                                int tokensToDeleteCount = tokensToDelete.length;
                                int tokensCount = tokens.length;

                                boolean extract = true;
                                List<Tree> a = coreContextSentence.getAttribution();
                                for (Tree tr : a) {
                                    String attr = SentenceUtils.listToString(tr.yield());
                                    String ph = SentenceUtils.listToString(t.getChild(0).yield());
                                    if (attr.contains(ph)) {
                                        extract = false;
                                    }
                                }

                                if (extract && (tokensCount - tokensToDeleteCount > 5)) {
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
