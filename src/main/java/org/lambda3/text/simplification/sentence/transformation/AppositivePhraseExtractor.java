/*
 * ==========================License-Start=============================
 * sentence_simplification : AppositivePhraseExtractor
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
import org.lambda3.text.simplification.sentence.analysis.RepresentationGenerator;

import java.util.List;

/**
 * Class for extracting appositive phrases
 */
public class AppositivePhraseExtractor {

    /**
     * extracts non-restrictive appositive phrases from the input sentence and transforms them into stand-alone context sentences,
     * returns true if such an appositive phrase was found in the input sentence
     *
     * @param coreContextSentence
     * @param parse
     * @param isOriginal
     * @param contextNumber
     * @return
     */
    public static boolean extractNonRestrictiveAppositives(SentenceProcessor sp, CoreContextSentence coreContextSentence, Tree parse, boolean isOriginal, int contextNumber) {
        boolean isPresent = SentenceProcessor.isPresent(coreContextSentence.getOriginal());
        String sentence = SentenceUtils.listToString(parse.yield());
        boolean isSplit = false;

        for (Tree t : parse) {
            if (t.label().value().equals("NP")) {
                if (t.getChildrenAsList().size() >= 5) {
                    for (int i = 0; i < t.getChildrenAsList().size() - 4; i++) {
                        if (t.getChild(i).label().value().equals("NP") && t.getChild(i + 1).label().value().equals(",") && t.getChild(i + 2).label().value().equals("CC") && t.getChild(i + 3).label().value().equals("NP") && t.getChild(i + 4).label().value().equals(",")) {
                            if (t.getChild(i).getChild(0).label().value().equals("NNP") || t.getChild(i).getChild(0).label().value().equals("NNPS")) {
                                if (t.getChild(i + 2).getChild(0).label().value().equals("or")) {
                                    List<LabeledWord> label = t.getChild(i).labeledYield();
                                    boolean singular = SentenceProcessor.isSingular(label.get(label.size() - 1));
                                    String aux = SentenceProcessor.setAux(singular, isPresent);
                                    String phrase = SentenceUtils.listToString(t.getChild(i).yield()) + aux + SentenceUtils.listToString(t.getChild(i + 3).yield()) + " .";
                                    String phraseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 2).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 3).yield()) + " ,";

                                    SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                    isSplit = true;
                                }
                            }
                        }

                        if (t.getChild(i).label().value().equals("NP") && t.getChild(i + 1).label().value().equals(",") && t.getChild(i + 2).label().value().equals("RB") && t.getChild(i + 3).label().value().equals("NP") && t.getChild(i + 4).label().value().equals(",")) {
                            boolean isConjoinedNP = false;
                            for (int j = i + 4; j < t.getChildrenAsList().size(); j++) {
                                if (t.getChild(j).label().value().equals("CC") && (t.getChild(j).getChild(0).label().value().equals("and") || t.getChild(j).getChild(0).label().value().equals("or"))) {
                                    isConjoinedNP = true;
                                }
                            }

                            List<LabeledWord> label = t.getChild(i).labeledYield();

                            if (!isConjoinedNP) {
                                if (label.get(label.size() - 1).tag().value().equals("NNP") || label.get(label.size() - 1).tag().value().equals("NNPS")) {
                                    String part1 = "";
                                    boolean number = SentenceProcessor.isSingular(label.get(label.size() - 1));
                                    String aux = SentenceProcessor.setAux(number, isPresent);

                                    boolean isNPPP = false;
                                    if (t.getChild(i).getChildrenAsList().size() >= 2) {
                                        if (t.getChild(i).getChild(0).label().value().equals("NP") && t.getChild(i).getChild(1).label().value().equals("PP")) {
                                            part1 = SentenceUtils.listToString(t.getChild(i).getChild(1).getChild(1).yield());
                                            isNPPP = true;
                                        }
                                    }
                                    if (!isNPPP) {
                                        part1 = SentenceUtils.listToString(t.getChild(i).yield());
                                    }

                                    if (t.getChild(i + 3).getChildrenAsList().size() >= 2) {
                                        String phrase = part1 + aux + SentenceUtils.listToString(t.getChild(i + 2).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 3).yield()) + " .";
                                        String phraseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 2).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 3).yield()) + " ,";
                                        SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                        isSplit = true;
                                    }
                                } else {
                                    if (!label.get(label.size() - 1).tag().value().equals("CD")) {
                                        List<LabeledWord> label2;
                                        label2 = t.getChild(i + 3).labeledYield();
                                        boolean number = SentenceProcessor.isSingular(label2.get(label2.size() - 1));
                                        String aux = SentenceProcessor.setAux(number, isPresent);
                                        String part1 = SentenceUtils.listToString(t.getChild(i + 2).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 3).yield());
                                        String phrase = SentenceUtils.listToString(t.getChild(i).yield()) + aux + part1 + " .";
                                        String phraseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 2).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 3).yield()) + " ,";

                                        SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                        isSplit = true;
                                    }
                                }
                            }
                        }
                    }
                }

                if (t.getChildrenAsList().size() >= 4) {
                    for (int i = 0; i < t.getChildrenAsList().size() - 3; i++) {
                        if (t.getChild(i).label().value().equals("NP") && t.getChild(i + 1).label().value().equals(",") && t.getChild(i + 2).label().value().equals("RB") && t.getChild(i + 3).label().value().equals("NP") && i == t.getChildrenAsList().size() - 4) {
                            List<LabeledWord> label = t.getChild(i).labeledYield();

                            if (label.get(label.size() - 1).tag().value().equals("NNP") || label.get(label.size() - 1).tag().value().equals("NNPS")) {
                                String part1 = "";
                                boolean number = SentenceProcessor.isSingular(label.get(label.size() - 1));
                                String aux = SentenceProcessor.setAux(number, isPresent);

                                boolean isNPPP = false;
                                if (t.getChild(i).getChildrenAsList().size() >= 2) {
                                    if (t.getChild(i).getChild(0).label().value().equals("NP") && t.getChild(i).getChild(1).label().value().equals("PP")) {
                                        part1 = SentenceUtils.listToString(t.getChild(i).getChild(1).getChild(1).yield());
                                        isNPPP = true;
                                    }
                                }
                                if (!isNPPP) {
                                    part1 = SentenceUtils.listToString(t.getChild(i).yield());
                                }

                                if (t.getChild(i + 3).getChildrenAsList().size() >= 2) {
                                    String phrase = part1 + aux + SentenceUtils.listToString(t.getChild(i + 2).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 3).yield()) + " .";
                                    String phraseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 2).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 3).yield());

                                    SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                    isSplit = true;
                                }
                            }
                            if (!label.get(label.size() - 1).tag().value().equals("NNP")) {
                                if (!label.get(label.size() - 1).tag().value().equals("NNPS")) {
                                    if (!label.get(label.size() - 1).tag().value().equals("CD")) {
                                        List<LabeledWord> label2;
                                        label2 = t.getChild(i + 3).labeledYield();
                                        boolean number = SentenceProcessor.isSingular(label2.get(label2.size() - 1));
                                        String aux = SentenceProcessor.setAux(number, isPresent);
                                        String part1 = SentenceUtils.listToString(t.getChild(i + 2).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 3).yield());
                                        String phrase = SentenceUtils.listToString(t.getChild(i).yield()) + aux + part1 + " .";
                                        String phraseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 2).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 3).yield());

                                        SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                        isSplit = true;
                                    }
                                }
                            }
                        }

                        if (t.getChild(i).label().value().equals("NP") && t.getChild(i + 1).label().value().equals(",") && t.getChild(i + 2).label().value().equals("NP") && t.getChild(i + 3).label().value().equals(",")) {
                            boolean isConjoinedNP = false;
                            boolean location = false;
                            boolean loc1 = false;
                            boolean loc2 = false;
                            String np1 = SentenceUtils.listToString(t.getChild(i).yield());
                            String np2 = SentenceUtils.listToString(t.getChild(i + 2).yield());
                            for (String loc : sp.getLoc()) {
                                if (np1.contains(loc)) {
                                    loc1 = true;
                                }
                                if (np2.contains(loc)) {
                                    loc2 = true;
                                }
                            }
                            if (loc1 && loc2) {
                                location = true;
                            }
                            if (!location) {
                                for (int j = i + 3; j < t.getChildrenAsList().size(); j++) {
                                    if (t.getChild(j).label().value().equals("CC") && (t.getChild(j).getChild(0).label().value().equals("and") || t.getChild(j).getChild(0).label().value().equals("or"))) {
                                        isConjoinedNP = true;
                                    }
                                }

                                List<LabeledWord> label = t.getChild(i).labeledYield();

                                if (!isConjoinedNP) {
                                    if (label.get(label.size() - 1).tag().value().equals("NNP") || label.get(label.size() - 1).tag().value().equals("NNPS") || label.get(label.size() - 1).tag().value().equals("''")) {
                                        String part1 = "";
                                        boolean number = SentenceProcessor.isSingular(label.get(label.size() - 1));
                                        String aux = SentenceProcessor.setAux(number, isPresent);

                                        boolean isNPPP = false;
                                        if (t.getChild(i).getChildrenAsList().size() >= 2) {
                                            if (t.getChild(i).getChild(0).label().value().equals("NP") && t.getChild(i).getChild(1).label().value().equals("PP")) {
                                                part1 = SentenceUtils.listToString(t.getChild(i).getChild(1).getChild(1).yield());
                                                isNPPP = true;
                                            }
                                        }
                                        if (!isNPPP) {
                                            part1 = SentenceUtils.listToString(t.getChild(i).yield());
                                        }
                                        if (t.getChild(i + 2).getChildrenAsList().size() >= 2) {
                                            String phrase = part1 + aux + SentenceUtils.listToString(t.getChild(i + 2).yield()) + " .";
                                            String phraseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 2).yield()) + " ,";

                                            SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                            isSplit = true;
                                        }
                                    } else {
                                        if (!label.get(label.size() - 1).tag().value().equals("CD")) {
                                            List<LabeledWord> label2;
                                            label2 = t.getChild(i + 2).labeledYield();
                                            boolean number = SentenceProcessor.isSingular(label2.get(label2.size() - 1));
                                            String aux = SentenceProcessor.setAux(number, isPresent);
                                            String part1 = SentenceUtils.listToString(t.getChild(i + 2).yield());
                                            String phrase = part1 + aux + SentenceUtils.listToString(t.getChild(i).yield()) + " .";
                                            String phraseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 2).yield()) + " ,";

                                            SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                            isSplit = true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (t.getChildrenAsList().size() >= 3) {
                    for (int i = 0; i < t.getChildrenAsList().size() - 2; i++) {
                        if (t.getChild(i).label().value().equals("NP") && t.getChild(i + 1).label().value().equals(",") && t.getChild(i + 2).label().value().equals("NP") && i == t.getChildrenAsList().size() - 3) {
                            boolean location = false;
                            boolean loc1 = false;
                            boolean loc2 = false;
                            String np1 = SentenceUtils.listToString(t.getChild(i).yield());
                            String np2 = SentenceUtils.listToString(t.getChild(i + 2).yield());
                            for (String loc : sp.getLoc()) {
                                if (np1.contains(loc)) {
                                    loc1 = true;
                                }
                                if (np2.contains(loc)) {
                                    loc2 = true;
                                }
                            }
                            if (loc1 && loc2) {
                                location = true;
                            }

                            if (!location) {
                                List<LabeledWord> label = t.getChild(i).labeledYield();

                                if (label.get(label.size() - 1).tag().value().equals("NNP") || label.get(label.size() - 1).tag().value().equals("NNPS") || label.get(label.size() - 1).tag().value().equals("''")) {
                                    String part1 = "";
                                    boolean number = SentenceProcessor.isSingular(label.get(label.size() - 1));
                                    String aux = SentenceProcessor.setAux(number, isPresent);

                                    boolean isNPPP = false;
                                    if (t.getChild(i).getChildrenAsList().size() >= 2) {
                                        if (t.getChild(i).getChild(0).label().value().equals("NP") && t.getChild(i).getChild(1).label().value().equals("PP")) {
                                            part1 = SentenceUtils.listToString(t.getChild(i).getChild(1).getChild(1).yield());
                                            isNPPP = true;
                                        }
                                    }
                                    if (!isNPPP) {
                                        part1 = SentenceUtils.listToString(t.getChild(i).yield());
                                    }

                                    if (t.getChild(i + 2).getChildrenAsList().size() >= 2) {
                                        String phrase = part1 + aux + SentenceUtils.listToString(t.getChild(i + 2).yield()) + " .";
                                        String phraseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 2).yield());

                                        SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                        isSplit = true;
                                    }
                                } else {
                                    List<LabeledWord> label2;
                                    if (!label.get(label.size() - 1).tag().value().equals("CD")) {
                                        label2 = t.getChild(i + 2).labeledYield();

                                        if (label2.get(label2.size() - 1).tag().value().equals("NNP") || label2.get(label2.size() - 1).tag().value().equals("NNPS")) {
                                            boolean number = SentenceProcessor.isSingular(label2.get(label2.size() - 1));
                                            String aux = SentenceProcessor.setAux(number, isPresent);
                                            String part1 = SentenceUtils.listToString(t.getChild(i + 2).yield());
                                            String phrase = part1 + aux + SentenceUtils.listToString(t.getChild(i).yield()) + " .";
                                            String phraseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 2).yield());

                                            SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                            isSplit = true;
                                        } else {
                                            String part1 = SentenceUtils.listToString(t.getChild(i).yield());
                                            boolean number = SentenceProcessor.isSingular(label.get(label.size() - 1));
                                            String aux = SentenceProcessor.setAux(number, isPresent);
                                            String phrase = part1 + aux + SentenceUtils.listToString(t.getChild(i + 2).yield()) + " .";
                                            String phraseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 2).yield());

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
     * extracts restrictive appositive phrases from the input sentence and transforms them into stand-alone context sentences,
     * returns true if such an appositive phrase was found in the input sentence
     *
     * @param coreContextSentence
     * @param parse
     * @param isOriginal
     * @param contextNumber
     * @return
     */
    public static boolean extractRestrictiveAppositives(CoreContextSentence coreContextSentence, Tree parse, boolean isOriginal, int contextNumber) {

        String sentence = SentenceUtils.listToString(parse.yield());
        boolean isSplit = false;

        String nerString = RepresentationGenerator.ner(SentenceUtils.listToString(parse.yield()));
        String taggedString = RepresentationGenerator.posTag(SentenceUtils.listToString(parse.yield()));
        String[] tagTokens = taggedString.split(" ");
        String input = SentenceUtils.listToString(parse.yield());
        String[] inputTokens = input.split(" ");

        boolean isPresent = SentenceProcessor.isPresent(parse);
        String aux = SentenceProcessor.setAux(true, isPresent);

        String[] nerTokens = nerString.split(" ");
        for (int nerCounter = 0; nerCounter < nerTokens.length; nerCounter++) {
            int[] person = new int[2];
            if (nerTokens[nerCounter].endsWith("/PERSON") || nerTokens[nerCounter].endsWith("/ORGANIZATION")) {
                person[0] = nerCounter;
                nerCounter++;
                while (nerCounter < nerTokens.length && (nerTokens[nerCounter].endsWith("/PERSON") || nerTokens[nerCounter].endsWith("/ORGANIZATION"))) {
                    nerCounter++;
                }
                person[1] = nerCounter - 1;
            }

            if (person[0] > 0 &&
                    (tagTokens[person[0] - 1].endsWith("_NN") || tagTokens[person[0] - 1].endsWith("_NNP"))) {
                int tagEnd = person[0] - 1;
                int tagStart = tagEnd;
                while (tagStart >= 0 && (tagTokens[tagStart].endsWith("_NN") || tagTokens[tagStart].endsWith("_NNS") || tagTokens[tagStart].endsWith("_NNP") || tagTokens[tagStart].endsWith("_NNPS") || tagTokens[tagStart].equals("of_IN")
                        || tagTokens[tagStart].equals("'s_POS"))) {
                    tagStart--;
                }

                if (tagStart > 0) {
                    if (tagTokens[tagStart].equals("and_CC")) {
                        if (!nerTokens[tagStart - 1].endsWith("/PERSON")) {
                            while (tagStart >= 0 && (tagTokens[tagStart].endsWith("_NN") || tagTokens[tagStart].endsWith("_NNS") || tagTokens[tagStart].endsWith("_NNP") || tagTokens[tagStart].endsWith("_NNPS") || tagTokens[tagStart].equals("of_IN")
                                    || tagTokens[tagStart].equals("'s_POS") || tagTokens[tagStart].equals("and_CC"))) {
                                tagStart--;
                            }
                        }
                    }
                }

                while (tagStart >= 0 && (tagTokens[tagStart].endsWith("_JJ") || tagTokens[tagStart].endsWith("_CD") || tagTokens[tagStart].endsWith("_DT") || tagTokens[tagStart].endsWith("_PRP$"))) {
                    tagStart--;
                }

                tagStart++;

                for (int c = tagStart; c < tagEnd; c++) {
                    if (tagTokens[tagStart].equals("of_IN")) {
                        tagStart++;
                    } else {
                        if (tagTokens[c].equals("of_IN") && (tagTokens[c - 1].endsWith("_NN") || tagTokens[c - 1].endsWith("_NNS"))) {
                            tagStart = c + 1;
                        }
                    }
                }

                String tagPhrase = "";
                for (int c = tagStart; c <= tagEnd; c++) {
                    tagPhrase = tagPhrase + " " + tagTokens[c];
                }

                tagPhrase = tagPhrase.trim();
                String[] tagPhraseTokens = tagPhrase.split(" ");
                if (((tagPhraseTokens[0].endsWith("_DT") || tagPhraseTokens[0].endsWith("_CD") || tagPhraseTokens[0].endsWith("_PRP$")) && (tagPhraseTokens[1].endsWith("_NN") || tagPhraseTokens[1].endsWith("_NNS")) &&
                        tagPhraseTokens.length == 2)) {
                    tagEnd = 0;
                }

                boolean isDetOrPronoun = false;
                if (tagPhraseTokens[0].endsWith("_DT") || tagPhraseTokens[0].endsWith("_PRP$") || tagPhraseTokens[0].endsWith("_POS") ||
                        tagPhraseTokens[0].endsWith("_NNP") || tagPhraseTokens[0].endsWith("_NNPS")) {
                    isDetOrPronoun = true;
                }

                String det = "";

                if (!isDetOrPronoun) {
                    if (tagPhraseTokens[0].startsWith("a") || tagPhraseTokens[0].startsWith("e") || tagPhraseTokens[0].startsWith("i") || tagPhraseTokens[0].startsWith("o") || tagPhraseTokens[0].startsWith("u") ||
                            tagPhraseTokens[0].startsWith("A") || tagPhraseTokens[0].startsWith("E") || tagPhraseTokens[0].startsWith("I") || tagPhraseTokens[0].startsWith("O") || tagPhraseTokens[0].startsWith("U")) {
                        det = " an ";
                    } else {
                        det = " a ";
                    }
                }

                boolean detThe = false;
                if (tagPhraseTokens[0].equals("the_DT") || tagPhraseTokens[0].equals("The_DT")) {
                    if (tagPhraseTokens[1].startsWith("a") || tagPhraseTokens[1].startsWith("e") || tagPhraseTokens[1].startsWith("i") || tagPhraseTokens[1].startsWith("o") || tagPhraseTokens[1].startsWith("u") ||
                            tagPhraseTokens[1].startsWith("A") || tagPhraseTokens[1].startsWith("E") || tagPhraseTokens[1].startsWith("I") || tagPhraseTokens[1].startsWith("O") || tagPhraseTokens[1].startsWith("U")) {
                        det = " an ";
                    } else {
                        det = " a ";
                    }
                    detThe = true;
                    tagStart++;
                }

                if (tagEnd > 0) {
                    String sen = "";
                    for (int c = person[0]; c <= person[1]; c++) {
                        sen = sen + " " + inputTokens[c];
                    }
                    sen = sen + aux + det;
                    String phraseToDelete = "";
                    if (detThe) {
                        phraseToDelete = inputTokens[tagStart - 1];
                    }

                    for (int c = tagStart; c <= tagEnd; c++) {
                        sen = sen + " " + inputTokens[c];
                        phraseToDelete = phraseToDelete + " " + inputTokens[c];
                    }
                    sen = sen.replace("  ", " ").trim();
                    sen = sen + " .";

                    String phrase = sen;

                    SentenceProcessor.updateSentence(phrase, phraseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                    isSplit = true;
                }
            }
        }
        return isSplit;
    }

}
