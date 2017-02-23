/*
 * ==========================License-Start=============================
 * sentence_simplification : SentenceProcessor
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

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.LabeledWord;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.trees.Tree;
import org.lambda3.text.simplification.sentence.analysis.RepresentationGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Class providing several methods for transforming an input sentence
 */
public class SentenceProcessor {

    private static final String singularPresent = " is ";
    private static final String singularPast = " was ";
    private static final String pluralPresent = " are ";
    private static final String pluralPast = " were ";

    private List<String> loc;
    private List<Integer> positions;

    public SentenceProcessor() {
        this.loc = new ArrayList<>();
        this.positions = new ArrayList<>();
    }

    /**
     * returns the positions of the tokens to eliminate from the input sentence to create the compressed core
     *
     * @param initialSentence
     * @param clauseToDelete
     * @return positions of the constituents to remove from the input sentence for creating the core
     */
    public static int[] matchSentences(String initialSentence, String clauseToDelete) {

        int[] startEndOfDelete = new int[2];
        String[] clauseTokens = clauseToDelete.split(" ");
        String[] originalTokens = initialSentence.split(" ");

        int endOfString = 0;
        int startOfString = 0;

        for (int i = 0; i < originalTokens.length; ++i) {
            int end = 0;
            if (originalTokens[i].equals(clauseTokens[0])) {
                int j = 0;
                while (j < clauseTokens.length && originalTokens[i + j].equals(clauseTokens[j])) {
                    end = i + j++;
                }
                if (end - i == clauseTokens.length - 1) {
                    endOfString = end;
                    startOfString = i;
                }
            }
        }

        startEndOfDelete[0] = startOfString;
        startEndOfDelete[1] = endOfString;

        return startEndOfDelete;
    }

    /**
     * updates the set of context sentences and the positions which are to be deleted from the input for creating the core
     *
     * @param relClause
     * @param clauseToDelete      to delete
     * @param sentence
     * @param coreContextSentence
     * @param isOriginal
     * @param contextNumber
     */
    public static void updateSentence(String relClause, String clauseToDelete, String sentence, CoreContextSentence coreContextSentence, boolean isOriginal, int contextNumber) {

        if (isOriginal) {
            if (contextNumber < 0) {
                int[] del = SentenceProcessor.matchSentences(sentence, clauseToDelete);
                if (relClause != null && !relClause.equals("")) {
                    SentenceProcessor.updateContext(relClause, coreContextSentence);
                }
                SentenceProcessor.updateDelete(del, coreContextSentence);

            } else {
                sentence = sentence.replace(clauseToDelete, "");

                Tree contextTree = RepresentationGenerator.parse(RepresentationGenerator.tokenize(sentence));
                coreContextSentence.getCoreNew().remove(contextNumber);
                if (!sentence.isEmpty()) {
                    coreContextSentence.getCoreNew().add(contextNumber, contextTree);
                }
            }

        } else {
            sentence = sentence.replace(clauseToDelete, "");
            Tree contextTree = RepresentationGenerator.parse(RepresentationGenerator.tokenize(sentence));
            coreContextSentence.getContext().remove(contextNumber);
            if (!sentence.isEmpty()) {
                coreContextSentence.getContext().add(contextNumber, contextTree);
            }
        }
    }

    /**
     * adds a new context sentence to the list of accompanying context sentences
     *
     * @param clause   sentence
     * @param sentence
     */
    public static void updateContext(String clause, CoreContextSentence sentence) {

        List<CoreLabel> tokens = RepresentationGenerator.tokenize(clause);
        Tree parse = RepresentationGenerator.parse(tokens);
        sentence.getContext().add(parse);

    }

    /**
     * updates the positions of the tokens which are to be deleted from the input sentence when creating the core
     *
     * @param del      end end position of the tokens to delete
     * @param sentence
     */
    public static void updateDelete(int[] del, CoreContextSentence sentence) {

        boolean[] delete = sentence.getDelete();
        for (int j = 0; j < delete.length; j++) {
            if (!delete[j] && del[0] <= j && j <= del[1]) {
                delete[j] = true;
            }
        }
        sentence.setDelete(delete);
    }

    /**
     * adds a new core sentence
     *
     * @param phrase sentence
     * @param sen
     */
    public static void addCore(String phrase, CoreContextSentence sen) {

        phrase = phrase.replace(". .", ".");
        phrase = phrase.replace("...", ".");
        Tree t = RepresentationGenerator.parse(RepresentationGenerator.tokenize(phrase));
        ArrayList<String> coreSen = new ArrayList<String>();

        for (Tree tree : sen.getCoreNew()) {
            String s = SentenceUtils.listToString(tree.yield());
            coreSen.add(s);
        }

        boolean included = false;
        for (String s : coreSen) {
            if (s.equals(phrase)) {
                included = true;
                break;
            }
        }

        if (!included) {
            sen.getCoreNew().add(t);
        }

    }

    /**
     * reduces the input sentence to its core components
     *
     * @param delete
     * @param sentence
     */
    public static void produceCore(boolean[] delete, CoreContextSentence sentence) {

        Tree original = sentence.getOriginal();
        String inputSentence = SentenceUtils.listToString(original.yield());
        String[] tokensOriginal = inputSentence.split(" ");
        String core = "";

        for (int i = 0; i < delete.length; i++) {
            if (!delete[i]) {
                core = core + " " + tokensOriginal[i];
            }
        }

        if (!core.isEmpty()) {
            if (!core.matches(" .")) {
                if (core.endsWith(",")) {
                    core = core.replace(",", "");
                } else if (core.endsWith(", .")) {
                    core = core.replace(", .", ".");
                }
                sentence.getCore().add(RepresentationGenerator.parse(RepresentationGenerator.tokenize(core.trim())));
            }
        }

    }

    /**
     * updates the array containing the positions of the components to delete from the input sentence when creating the core
     *
     * @param n
     * @param sentence
     */
    public static void deleteTokenInOriginal(int n, CoreContextSentence sentence) {

        boolean[] del = sentence.getDelete();
        del[n] = true;
        sentence.setDelete(del);

    }

    /**
     * checks whether the original sentence is in present tense
     *
     * @param tree
     * @return true if the original sentence is in present tense
     */
    public static boolean isPresent(Tree tree) {

        for (Tree t : tree) {
            if (t.label().value().equals("S") || t.label().value().equals("SINV")) {
                for (int i = 0; i < t.getChildrenAsList().size(); i++) {
                    if (t.getChild(i).label().value().equals("VP")) {
                        if (t.getChild(i).getChild(0).label().value().equals("VBZ") || t.getChild(i).getChild(0).label().value().equals("VBP")) {
                            if (t.getChild(i).getChild(0).label().value().equals("VBP")) {
                                if (t.getChild(i).getChildrenAsList().size() >= 2) {
                                    if (t.getChild(i).getChild(1).label().value().equals("VP") && t.getChild(i).getChild(1).getChild(0).label().value().equals("VBN")) {
                                        return false;
                                    }
                                }
                            }
                            return true;
                        }
                        if (t.getChild(i).getChild(0).label().value().equals("VBD")) {
                            return false;
                        }
                        if (t.getChild(i).getChild(0).label().value().equals("VP")) {
                            if (t.getChild(i).getChild(0).getChild(0).label().value().equals("VBZ") || t.getChild(i).getChild(0).getChild(0).label().value().equals("VBP")) {
                                if (t.getChild(i).getChild(0).getChild(0).label().value().equals("VBP")) {
                                    if (t.getChild(i).getChild(0).getChildrenAsList().size() >= 1) {
                                        if (t.getChild(i).getChild(0).getChild(1).label().value().equals("VP") && t.getChild(i).getChild(0).getChild(1).getChild(0).label().value().equals("VBN")) {
                                            return false;
                                        }
                                    }
                                }
                                return true;
                            }
                            if (t.getChild(i).getChild(0).getChild(0).label().value().equals("VBD")) {
                                return false;
                            }
                        }
                    }
                }
                if (t.getChild(0).label().value().equals("S") || t.getChild(0).label().value().equals("SINV")) {
                    for (int i = 0; i < t.getChild(0).getChildrenAsList().size(); i++) {
                        if (t.getChild(0).getChild(i).label().value().equals("VP")) {
                            if (t.getChild(0).getChild(i).getChild(0).label().value().equals("VBZ") || t.getChild(0).getChild(i).getChild(0).label().value().equals("VBP")) {
                                if (t.getChild(0).getChild(i).getChild(0).label().value().equals("VBP")) {
                                    if (t.getChild(0).getChild(i).getChildrenAsList().size() >= 2) {
                                        if (t.getChild(0).getChild(i).getChild(1).label().value().equals("VP") && t.getChild(0).getChild(i).getChild(1).getChild(0).label().value().equals("VBN")) {
                                            return false;
                                        }
                                    }
                                }
                                return true;
                            }
                            if (t.getChild(0).getChild(i).getChild(0).label().value().equals("VBD")) {
                                return false;
                            }
                            if (t.getChild(0).getChild(i).getChild(0).label().value().equals("VP")) {
                                if (t.getChild(0).getChild(i).getChild(0).getChild(0).label().value().equals("VBZ") || t.getChild(0).getChild(i).getChild(0).getChild(0).label().value().equals("VBP")) {
                                    if (t.getChild(0).getChild(i).getChild(0).getChild(0).label().value().equals("VBP")) {
                                        if (t.getChild(0).getChild(i).getChild(0).getChildrenAsList().size() >= 2) {
                                            if (t.getChild(0).getChild(i).getChild(0).getChild(1).label().value().equals("VP") && t.getChild(0).getChild(i).getChild(0).getChild(1).getChild(0).label().value().equals("VBN")) {
                                                return false;
                                            }
                                        }
                                    }
                                    return true;
                                }
                                if (t.getChild(0).getChild(i).getChild(0).getChild(0).label().value().equals("VBD")) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * checks whether the phrase to which an extracted constituent refers is singular
     *
     * @param label
     * @return true if the antecedent is singular
     */
    public static boolean isSingular(LabeledWord label) {

        return !(!label.tag().value().equals("NN") && !label.tag().value().equals("NNP") && !label.tag().value().equals("PRP"));

    }

    /**
     * sets the auxiliary verb which connects an extracted component and its antecedent
     *
     * @param singular
     * @param present
     * @return auxiliary verb
     */
    public static String setAux(boolean singular, boolean present) {
        String aux;

        if (singular && present) {
            aux = singularPresent;
        } else if (singular) {
            aux = singularPast;
        } else if (present) {
            aux = pluralPresent;
        } else {
            aux = pluralPast;
        }

        return aux;
    }

    /**
     * removes unnecessary whitespace from a sentence
     *
     * @param value
     * @return string with dispensable whitespace removed
     */
    public static String collapseWhitespace(String value) {
        return value.replaceAll("\\s+", " ");
    }

    public List<String> getLoc() {
        return this.loc;
    }

    public List<Integer> getPositions() {
        return this.positions;
    }

    public void setPositions(List<Integer> newPositions) {
        this.positions = newPositions;
    }

    /**
     * further simplifies a context sentence, when appropriate
     *
     * @param sentence
     * @return true if a context sentence has been transformed
     */
    public boolean pruneContextSentences(CoreContextSentence sentence) {

        int n = 0;
        List<Tree> currentContext = sentence.getContext();
        boolean[] isPruned = new boolean[currentContext.size()];
        boolean prune = false;

        boolean isPrunedRelClauseNonRestrictive = false;
        boolean isPrunedInitialPPs = false;
        boolean isPrunedAppositivePPs = false;
        boolean isPrunedADJPappositives = false;
        boolean isPrunedNPappositives = false;
        boolean isPrunedVPappositives = false;
        boolean isPrunedAccording = false;
        boolean isPrunedIncluding = false;
        boolean isPrunedProperNouns = false;
        boolean isPrunedInfixWhen = false;
        boolean isPrunedInfixAsSince = false;
        boolean isPrunedInfixCommaPPAfterBefore = false;
        boolean isPrunedInfixPPAfterBefore = false;
        boolean isPrunedInfixPPSAfterBefore = false;
        boolean isPrunedSBARAfterBefore = false;
        boolean isPrunedInitialWhen = false;
        boolean isPrunedInitialThoughAlthoughBecause = false;
        boolean isPrunedInfixBecauseThoughAlthough = false;
        boolean isPrunedInfixAndOrBut = false;
        boolean isPrunedCommaAndOrBut = false;
        boolean isPrunedADVPappositives = false;
        boolean isPrunedNPPappositives = false;
        boolean isPrunedGerundAfterWhile = false;
        boolean isPrunedGerundAfterComma = false;
        boolean isPrunedGerundSentenceStart = false;
        boolean isPrunedByGerund = false;
        boolean isPrunedColon = false;
        boolean isPrunedParentheses = false;
        boolean isPrunedBrackets = false;
        boolean isPrunedIf = false;
        boolean isPrunedFromTo = false;
        boolean isPrunedSo = false;
        boolean isPrunedToDo = false;
        boolean isPrunedOr = false;

        while (n < currentContext.size()) {

            if (n < sentence.getContext().size()) {
                isPrunedRelClauseNonRestrictive = RelativeClauseExtractor.extractNonRestrictiveRelativeClauses(sentence, currentContext.get(n), false, n);
            }
            if (n < sentence.getContext().size()) {
                isPrunedInfixWhen = ConjoinedClausesExtractor.infixWhenSplit(sentence, currentContext.get(n), false, n);
            }
            //if (n < sentence.getContext().size()) {
            //isPrunedInfixAsSince = ConjoinedPhrasesExtractor.infixAsSinceSplit(sentence, currentContext.get(n), false, n);
            //}
            if (n < sentence.getContext().size()) {
                isPrunedInfixCommaPPAfterBefore = ConjoinedClausesExtractor.infixCommaPPAfterBeforeSplit(sentence, currentContext.get(n), false, n);
            }
            if (n < sentence.getContext().size()) {
                isPrunedInfixPPAfterBefore = ConjoinedClausesExtractor.infixPPAfterBeforeSplit(sentence, currentContext.get(n), false, n);
            }
            if (n < sentence.getContext().size()) {
                isPrunedInfixPPSAfterBefore = ConjoinedClausesExtractor.infixPPSAfterBeforeSplit(sentence, currentContext.get(n), false, n);
            }
            if (n < sentence.getContext().size()) {
                isPrunedSBARAfterBefore = ConjoinedClausesExtractor.infixSBARAfterBeforeSplit(sentence, currentContext.get(n), false, n);
            }
            if (n < sentence.getContext().size()) {
                isPrunedInitialWhen = ConjoinedClausesExtractor.initialWhenSplit(sentence, currentContext.get(n), false, n);
            }
            if (n < sentence.getContext().size()) {
                isPrunedInitialThoughAlthoughBecause = ConjoinedClausesExtractor.initialThoughAlthoughBecauseSplit(sentence, currentContext.get(n), false, n);
            }
            if (n < sentence.getContext().size()) {
                isPrunedInfixBecauseThoughAlthough = ConjoinedClausesExtractor.infixBecauseThoughAlthoughSplit(sentence, currentContext.get(n), false, n);
            }
            //if (n < sentence.getContext().size()) {
            //isPrunedOr = ConjoinedClausesExtractor.or(sentence, currentContext.get(n), false, n);
            //}
            if (n < sentence.getContext().size()) {
                isPrunedInfixAndOrBut = ConjoinedClausesExtractor.infixAndOrButSplit(this, sentence, currentContext.get(n), false, n);
            }
            if (n < sentence.getContext().size()) {
                isPrunedCommaAndOrBut = ConjoinedClausesExtractor.infixCommaAndOrButSplit(this, sentence, currentContext.get(n), false, n);
            }
            if (n < sentence.getContext().size()) {
                isPrunedIf = ConjoinedClausesExtractor.ifSplit(sentence, currentContext.get(n), false, n);
            }
            if (n < sentence.getContext().size()) {
                isPrunedSo = ConjoinedClausesExtractor.extractSo(sentence, currentContext.get(n), false, n);
            }

            if (n < sentence.getContext().size()) {
                isPrunedAccording = PrepositionalPhraseExtractor.extractAccording(sentence, currentContext.get(n), false, n);
            }
            if (n < sentence.getContext().size()) {
                isPrunedGerundAfterWhile = ConjoinedClausesExtractor.extractWhilePlusParticiple(sentence, currentContext.get(n), false, n);
            }

            //if (n < sentence.getContext().size()) {
            //isPrunedToDo = PrepositionalPhraseExtractor.extractToDo(sentence, currentContext.get(n), false, n);
            //}
            //initial PPs nicht in context sentences extrahieren (wegen rel clauses mit PP / anderes beispiel: in an effort to maintain ...)
            //if (n < sentence.getContext().size()) {
            //isPrunedInitialPPs = PrepositionalPhraseExtractor.extractInitialPPs(sentence, currentContext.get(n), false, n);
            //}
            if (n < sentence.getContext().size()) {
                isPrunedAppositivePPs = PrepositionalPhraseExtractor.extractInfixPPs(sentence, currentContext.get(n), false, n);
            }
            if (n < sentence.getContext().size()) {
                isPrunedFromTo = PrepositionalPhraseExtractor.extractFromTo(sentence, currentContext.get(n), false, n);
            }
            if (n < sentence.getContext().size()) {
                isPrunedADJPappositives = AdjectiveAdverbPhraseExtractor.extractAdjectivePhrases(sentence, currentContext.get(n), false, n);
            }
            if (n < sentence.getContext().size()) {
                isPrunedNPappositives = InitialNounPhraseExtractor.extractInitialParentheticalNounPhrases(sentence, currentContext.get(n), false, n);
            }
            //if (n < sentence.getContext().size()) {
            //isPrunedVPappositives = ParticipialPhraseExtractor.extractPresentAndPastParticiples(sentence, currentContext.get(n), false, n);
            //}
            //if (n < sentence.getContext().size()) {
            //isPrunedADVPappositives = AdjectiveAdverbPhraseExtractor.extractAdverbPhrases(sentence, currentContext.get(n), false, n);
            //}
            if (n < sentence.getContext().size()) {
                isPrunedNPPappositives = AppositivePhraseExtractor.extractNonRestrictiveAppositives(this, sentence, currentContext.get(n), false, n);
            }
            //if (n < sentence.getContext().size()) {
            //isPrunedIncluding = PrepositionalPhraseExtractor.extractIncluding(sentence, currentContext.get(n), false, n);
            //}

            if (n < sentence.getContext().size()) {
                isPrunedProperNouns = AppositivePhraseExtractor.extractRestrictiveAppositives(sentence, currentContext.get(n), false, n);
            }
            //if (n < sentence.getContext().size()) {
            //isPrunedColon = Punctuation.splitAtColon(sentence, currentContext.get(n), false, n);
            //}
            if (n < sentence.getContext().size()) {
                isPrunedParentheses = Punctuation.extractParentheses(sentence, currentContext.get(n), false, n);
            }
            if (n < sentence.getContext().size()) {
                isPrunedBrackets = Punctuation.removeBrackets(sentence, currentContext.get(n), false, n);
            }

            if (isPrunedRelClauseNonRestrictive || isPrunedInitialPPs || isPrunedAppositivePPs || isPrunedADJPappositives ||
                    isPrunedNPappositives || isPrunedVPappositives || isPrunedAccording || isPrunedIncluding || isPrunedProperNouns ||
                    isPrunedInfixWhen || isPrunedInfixAsSince || isPrunedInfixCommaPPAfterBefore || isPrunedInfixPPAfterBefore || isPrunedInfixPPSAfterBefore ||
                    isPrunedSBARAfterBefore || isPrunedInitialWhen || isPrunedInitialThoughAlthoughBecause || isPrunedInfixBecauseThoughAlthough ||
                    isPrunedInfixAndOrBut || isPrunedCommaAndOrBut || isPrunedADVPappositives || isPrunedNPPappositives || isPrunedGerundAfterWhile ||
                    isPrunedGerundAfterComma || isPrunedGerundSentenceStart || isPrunedByGerund || isPrunedColon || isPrunedParentheses || isPrunedBrackets || isPrunedIf
                    || isPrunedFromTo || isPrunedSo || isPrunedToDo || isPrunedOr) {
                isPruned[n] = true;
            }

            n++;
        }

        for (boolean anIsPruned : isPruned) {
            if (anIsPruned) {
                prune = true;
            }
        }
        return prune;
    }

    /**
     * is called if an input sentence is split into several core sentences
     *
     * @param sentence
     * @return true if a core sentence has been simplified
     */
    public boolean pruneCoreSentences(CoreContextSentence sentence) {
        int n = 0;
        List<Tree> currentCore = sentence.getCoreNew();
        List<String> s = new ArrayList<>();

        for (Tree t : currentCore) {
            s.add(SentenceUtils.listToString(t.yield()));
        }

        int m = 0;
        for (String string : s) {
            if (string.startsWith(", ")) {
                string = string.replaceFirst(",", "");
                currentCore.set(m, RepresentationGenerator.parse(RepresentationGenerator.tokenize(string)));
            }
            m++;
        }

        boolean[] isPruned = new boolean[currentCore.size()];
        boolean prune = false;

        boolean isPrunedRelClauseNonRestrictive = false;
        boolean isPrunedInitialPPs = false;
        boolean isPrunedAppositivePPs = false;
        boolean isPrunedADJPappositives = false;
        boolean isPrunedNPappositives = false;
        boolean isPrunedVPappositives = false;
        boolean isPrunedAccording = false;
        boolean isPrunedIncluding = false;
        boolean isPrunedProperNouns = false;
        boolean isPrunedInfixWhen = false;
        boolean isPrunedInfixAsSince = false;
        boolean isPrunedInfixCommaPPAfterBefore = false;
        boolean isPrunedInfixPPAfterBefore = false;
        boolean isPrunedInfixPPSAfterBefore = false;
        boolean isPrunedSBARAfterBefore = false;
        boolean isPrunedInitialWhen = false;
        boolean isPrunedInitialThoughAlthoughBecause = false;
        boolean isPrunedInfixBecauseThoughAlthough = false;
        boolean isPrunedInfixAndOrBut = false;
        boolean isPrunedCommaAndOrBut = false;
        boolean isPrunedADVPappositives = false;
        boolean isPrunedNPPappositives = false;
        boolean isPrunedGerundAfterWhile = false;
        boolean isPrunedGerundAfterComma = false;
        boolean isPrunedGerundSentenceStart = false;
        boolean isPrunedByGerund = false;
        boolean isPrunedColon = false;
        boolean isPrunedParentheses = false;
        boolean isPrunedBrackets = false;
        boolean isPrunedIf = false;
        boolean isPrunedFromTo = false;
        boolean isPrunedSo = false;
        boolean isPrunedToDo = false;
        boolean isPrunedOr = false;

        while (n < currentCore.size()) {

            if (n < sentence.getContext().size()) {
                isPrunedRelClauseNonRestrictive = RelativeClauseExtractor.extractNonRestrictiveRelativeClauses(sentence, currentCore.get(n), true, n);
            }
            if (n < sentence.getContext().size()) {
                isPrunedInfixWhen = ConjoinedClausesExtractor.infixWhenSplit(sentence, currentCore.get(n), true, n);
            }
            //if (n < sentence.getContext().size()) {
            //isPrunedInfixAsSince = ConjoinedPhrasesExtractor.infixAsSinceSplit(sentence, currentContext.get(n), false, n);
            //}
            if (n < sentence.getContext().size()) {
                isPrunedInfixCommaPPAfterBefore = ConjoinedClausesExtractor.infixCommaPPAfterBeforeSplit(sentence, currentCore.get(n), true, n);
            }
            if (n < sentence.getContext().size()) {
                isPrunedInfixPPAfterBefore = ConjoinedClausesExtractor.infixPPAfterBeforeSplit(sentence, currentCore.get(n), true, n);
            }
            if (n < sentence.getContext().size()) {
                isPrunedInfixPPSAfterBefore = ConjoinedClausesExtractor.infixPPSAfterBeforeSplit(sentence, currentCore.get(n), true, n);
            }
            if (n < sentence.getContext().size()) {
                isPrunedSBARAfterBefore = ConjoinedClausesExtractor.infixSBARAfterBeforeSplit(sentence, currentCore.get(n), true, n);
            }
            if (n < sentence.getContext().size()) {
                isPrunedInitialWhen = ConjoinedClausesExtractor.initialWhenSplit(sentence, currentCore.get(n), true, n);
            }
            if (n < sentence.getContext().size()) {
                isPrunedInitialThoughAlthoughBecause = ConjoinedClausesExtractor.initialThoughAlthoughBecauseSplit(sentence, currentCore.get(n), true, n);
            }
            if (n < sentence.getContext().size()) {
                isPrunedInfixBecauseThoughAlthough = ConjoinedClausesExtractor.infixBecauseThoughAlthoughSplit(sentence, currentCore.get(n), true, n);
            }
            //if (n < sentence.getContext().size()) {
            //isPrunedOr = ConjoinedClausesExtractor.or(sentence, currentCore.get(n), true, n);
            //}
            if (n < sentence.getContext().size()) {
                isPrunedInfixAndOrBut = ConjoinedClausesExtractor.infixAndOrButSplit(this, sentence, currentCore.get(n), true, n);
            }
            if (n < sentence.getContext().size()) {
                isPrunedCommaAndOrBut = ConjoinedClausesExtractor.infixCommaAndOrButSplit(this, sentence, currentCore.get(n), true, n);
            }
            if (n < sentence.getContext().size()) {
                isPrunedIf = ConjoinedClausesExtractor.ifSplit(sentence, currentCore.get(n), true, n);
            }
            if (n < sentence.getContext().size()) {
                isPrunedSo = ConjoinedClausesExtractor.extractSo(sentence, currentCore.get(n), true, n);
            }

            if (n < sentence.getContext().size()) {
                isPrunedAccording = PrepositionalPhraseExtractor.extractAccording(sentence, currentCore.get(n), true, n);
            }
            if (n < sentence.getContext().size()) {
                isPrunedGerundAfterWhile = ConjoinedClausesExtractor.extractWhilePlusParticiple(sentence, currentCore.get(n), true, n);
            }

            if (n < sentence.getContext().size()) {
                isPrunedToDo = PrepositionalPhraseExtractor.extractToDo(sentence, currentCore.get(n), true, n);
            }
            if (n < sentence.getContext().size()) {
                isPrunedInitialPPs = PrepositionalPhraseExtractor.extractInitialPPs(sentence, currentCore.get(n), true, n);
            }
            if (n < sentence.getContext().size()) {
                isPrunedAppositivePPs = PrepositionalPhraseExtractor.extractInfixPPs(sentence, currentCore.get(n), true, n);
            }
            if (n < sentence.getContext().size()) {
                isPrunedFromTo = PrepositionalPhraseExtractor.extractFromTo(sentence, currentCore.get(n), true, n);
            }
            if (n < sentence.getContext().size()) {
                isPrunedADJPappositives = AdjectiveAdverbPhraseExtractor.extractAdjectivePhrases(sentence, currentCore.get(n), true, n);
            }
            if (n < sentence.getContext().size()) {
                isPrunedNPappositives = InitialNounPhraseExtractor.extractInitialParentheticalNounPhrases(sentence, currentCore.get(n), true, n);
            }
            if (n < sentence.getContext().size()) {
                isPrunedVPappositives = ParticipialPhraseExtractor.extractPresentAndPastParticiples(sentence, currentCore.get(n), true, n);
            }
            //if (n < sentence.getContext().size()) {
            //isPrunedADVPappositives = AdjectiveAdverbPhraseExtractor.extractAdverbPhrases(sentence, currentCore.get(n), true, n);
            //}
            if (n < sentence.getContext().size()) {
                isPrunedNPPappositives = AppositivePhraseExtractor.extractNonRestrictiveAppositives(this, sentence, currentCore.get(n), true, n);
            }
            //if (n < sentence.getContext().size()) {
            //isPrunedIncluding = PrepositionalPhraseExtractor.extractIncluding(sentence, currentCore.get(n), true, n);
            //}

            if (n < sentence.getContext().size()) {
                isPrunedProperNouns = AppositivePhraseExtractor.extractRestrictiveAppositives(sentence, currentCore.get(n), true, n);
            }
            //if (n < sentence.getContext().size()) {
            //isPrunedColon = Punctuation.splitAtColon(sentence, currentCore.get(n), true, n);
            //}
            if (n < sentence.getContext().size()) {
                isPrunedParentheses = Punctuation.extractParentheses(sentence, currentCore.get(n), true, n);
            }
            if (n < sentence.getContext().size()) {
                isPrunedBrackets = Punctuation.removeBrackets(sentence, currentCore.get(n), true, n);
            }

            if (isPrunedRelClauseNonRestrictive || isPrunedInitialPPs || isPrunedAppositivePPs || isPrunedADJPappositives ||
                    isPrunedNPappositives || isPrunedVPappositives || isPrunedAccording || isPrunedIncluding || isPrunedProperNouns ||
                    isPrunedInfixWhen || isPrunedInfixAsSince || isPrunedInfixCommaPPAfterBefore || isPrunedInfixPPAfterBefore || isPrunedInfixPPSAfterBefore ||
                    isPrunedSBARAfterBefore || isPrunedInitialWhen || isPrunedInitialThoughAlthoughBecause || isPrunedInfixBecauseThoughAlthough ||
                    isPrunedInfixAndOrBut || isPrunedCommaAndOrBut || isPrunedADVPappositives || isPrunedNPPappositives || isPrunedGerundAfterWhile ||
                    isPrunedGerundAfterComma || isPrunedGerundSentenceStart || isPrunedByGerund || isPrunedColon || isPrunedParentheses || isPrunedBrackets || isPrunedIf
                    || isPrunedFromTo || isPrunedSo || isPrunedToDo || isPrunedOr) {
                isPruned[n] = true;
            }

            n++;
        }

        for (boolean anIsPruned : isPruned) {
            if (anIsPruned) {
                return true;
            }
        }

        return false;
    }

    /**
     * adds a position-ID to the specified array
     *
     * @param i
     */
    public void addPos(Integer i) {
        this.positions.add(i);
    }


    /**
     * adds LOCATION-tagged tokens to the specified array
     *
     * @param locations
     */
    public void addLoc(String locations) {
        this.loc.add(locations);
    }


}
