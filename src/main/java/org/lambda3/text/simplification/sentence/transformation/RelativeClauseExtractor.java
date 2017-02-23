/*
 * ==========================License-Start=============================
 * sentence_simplification : RelativeClauseExtractor
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
 * Class for extracting non-restrictive relative clauses
 */
public class RelativeClauseExtractor {

    /**
     * extracts non-restrictive relative clauses from the input sentence and transforms them into stand-alone context sentences,
     * returns true if a non-restrictive relative clause was found in the input sentence
     *
     * @param coreContextSentence
     * @param parse
     * @param isOriginal
     * @param contextNumber
     * @return
     */
    public static boolean extractNonRestrictiveRelativeClauses(CoreContextSentence coreContextSentence, Tree parse, boolean isOriginal, int contextNumber) {

        String sentence = SentenceUtils.listToString(parse.yield());
        boolean isSplit = false;

        for (Tree t : parse) {

            for (int i = 0; i < t.getChildrenAsList().size() - 1; i++) {
                if (t.getChild(i).label().value().equals(",") && t.getChild(i + 1).label().value().equals("SBAR")) {
                    if (t.getChild(i + 1).getChild(0).label().value().equals("WHADVP") && t.getChild(i + 1).getChild(0).getChild(0).label().value().equals("WRB") && t.getChild(i + 1).getChild(1).label().value().equals("S") && t.getChild(i + 1).getChild(0).getChild(0).getChild(0).label().value().equals("where")) {
                        String relClause = "There " + SentenceUtils.listToString(t.getChild(i + 1).getChild(1).yield()) + " .";
                        String relClauseToDelete = "";

                        if (i == t.getChildrenAsList().size() - 2) {
                            relClauseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 1).yield());
                        } else if (t.getChild(i + 2).label().value().equals(",")) {
                            relClauseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 1).yield()) + " ,";
                        }

                        SentenceProcessor.updateSentence(relClause, relClauseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                        isSplit = true;
                    }
                }
            }

            for (int i = 0; i < t.getChildrenAsList().size() - 2; i++) {
                if (t.getChild(i).label().value().equals("NP") && t.getChild(i + 1).label().value().equals(",") && t.getChild(i + 2).label().value().equals("SBAR")) {
                    if (t.getChild(i + 2).getChild(0).label().value().equals("WP$") && t.getChild(i + 2).getChild(1).label().value().equals("S")) {
                        if (t.getChild(i + 2).getChild(0).getChild(0).label().value().equals("whose")) {
                            String relClause = SentenceUtils.listToString(t.getChild(i).yield()) + "'s " + SentenceUtils.listToString(t.getChild(i + 2).getChild(1).yield()) + " .";
                            String relClauseToDelete = "";

                            if (i == t.getChildrenAsList().size() - 3) {
                                relClauseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 2).yield());
                            } else if (t.getChild(i + 3).label().value().equals(",")) {
                                relClauseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 2).yield()) + " ,";
                            }

                            SentenceProcessor.updateSentence(relClause, relClauseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                            isSplit = true;
                        }
                    }

                    if (t.getChild(i + 2).getChild(0).label().value().equals("WHNP") && t.getChild(i + 2).getChild(1).label().value().equals("S")) {
                        if ((t.getChild(i + 2).getChild(0).getChild(0).label().value().equals("WDT") && t.getChild(i + 2).getChild(0).getChild(0).getChild(0).label().value().equals("which")) ||
                                (t.getChild(i + 2).getChild(0).getChild(0).label().value().equals("WP") && (t.getChild(i + 2).getChild(0).getChild(0).getChild(0).label().value().equals("who") || t.getChild(i + 2).getChild(0).getChild(0).getChild(0).label().value().equals("whom")))) {
                            List<LabeledWord> label = t.getChild(i).labeledYield();
                            String att = "";

                            if (label.get(label.size() - 1).tag().value().equals("CD")) {
                                att = SentenceUtils.listToString(t.getChild(i).ancestor(3, parse).yield());
                            } else {
                                att = SentenceUtils.listToString(t.getChild(i).yield());
                            }

                            String relClause = att + " " + SentenceUtils.listToString(t.getChild(i + 2).getChild(1).yield()) + " .";
                            String relClauseToDelete = "";

                            if (i == t.getChildrenAsList().size() - 3) {
                                relClauseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 2).yield());
                            } else if (t.getChild(i + 3).label().value().equals(",")) {
                                relClauseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 2).yield()) + " ,";
                            }

                            SentenceProcessor.updateSentence(relClause, relClauseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                            isSplit = true;
                        }

                        if (t.getChild(i + 2).getChild(0).getChild(0).label().value().equals("DT") && t.getChild(i + 2).getChild(0).getChild(1).label().value().equals("WHPP")) {
                            if (t.getChild(i + 2).getChild(0).getChild(1).getChild(1).label().value().equals("WHNP")) {
                                if (t.getChild(i + 2).getChild(0).getChild(1).getChild(1).getChild(0).label().value().equals("WP") && t.getChild(i + 2).getChild(0).getChild(1).getChild(1).getChild(0).getChild(0).label().value().equals("whom")) {
                                    String relClause = SentenceUtils.listToString(t.getChild(i + 2).getChild(0).yield()) + " the " + SentenceUtils.listToString(t.getChild(i).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 2).getChild(1).yield()) + " .";
                                    relClause = relClause.replace("whom", "");
                                    String relClauseToDelete = "";

                                    if (i == t.getChildrenAsList().size() - 3) {
                                        relClauseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 2).yield());
                                    } else if (t.getChild(i + 3).label().value().equals(",")) {
                                        relClauseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 2).yield()) + " ,";
                                    }

                                    SentenceProcessor.updateSentence(relClause, relClauseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                    isSplit = true;
                                }
                            }
                        }

                        if (t.getChild(i + 2).getChild(0).getChild(0).label().value().equals("WP$") && t.getChild(i + 2).getChild(0).getChild(0).getChild(0).label().value().equals("whose")) {
                            String relClause = SentenceUtils.listToString(t.getChild(i).yield()) + "'s " + SentenceUtils.listToString(t.getChild(i + 2).yield()) + " .";
                            relClause = relClause.replace("whose", "");
                            String relClauseToDelete = "";

                            if (i == t.getChildrenAsList().size() - 3) {
                                relClauseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 2).yield());
                            } else if (t.getChild(i + 3).label().value().equals(",")) {
                                relClauseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 2).yield()) + " ,";
                            }

                            SentenceProcessor.updateSentence(relClause, relClauseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                            isSplit = true;
                        }
                    }

                    if (t.getChild(i + 2).getChild(0).label().value().equals("WHPP") && t.getChild(i + 2).getChild(1).label().value().equals("S")) {
                        if ((t.getChild(i + 2).getChild(0).getChild(0).label().value().equals("IN") || t.getChild(i + 2).getChild(0).getChild(0).label().value().equals("TO")) && t.getChild(i + 2).getChild(0).getChild(1).label().value().equals("WHNP")) {
                            if ((t.getChild(i + 2).getChild(0).getChild(1).getChild(0).label().value().equals("WDT") && t.getChild(i + 2).getChild(0).getChild(1).getChild(0).getChild(0).label().value().equals("which")) ||
                                    (t.getChild(i + 2).getChild(0).getChild(1).getChild(0).label().value().equals("WP") && (t.getChild(i + 2).getChild(0).getChild(1).getChild(0).getChild(0).label().value().equals("who") || t.getChild(i + 2).getChild(0).getChild(1).getChild(0).getChild(0).label().value().equals("whom")))) {
                                String relClause = SentenceUtils.listToString(t.getChild(i + 2).getChild(0).getChild(0).yield()) + " " + SentenceUtils.listToString(t.getChild(i).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 2).getChild(1).yield()) + " .";
                                String relClauseToDelete = "";

                                if (i == t.getChildrenAsList().size() - 3) {
                                    relClauseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 2).yield());
                                } else if (t.getChild(i + 3).label().value().equals(",")) {
                                    relClauseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 2).yield()) + " ,";
                                }

                                SentenceProcessor.updateSentence(relClause, relClauseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                isSplit = true;
                            }
                        }
                    }
                }
            }

            for (int i = 0; i < t.getChildrenAsList().size() - 3; i++) {
                if (t.getChild(i).label().value().equals("NP") && t.getChild(i + 1).label().value().equals("PP") && t.getChild(i + 2).label().value().equals(",") && t.getChild(i + 3).label().value().equals("SBAR")) {
                    if (t.getChild(i + 3).getChild(0).label().value().equals("WHNP") && t.getChild(i + 3).getChild(1).label().value().equals("S")) {
                        if ((t.getChild(i + 3).getChild(0).getChild(0).label().value().equals("WDT") && t.getChild(i + 3).getChild(0).getChild(0).getChild(0).label().value().equals("which")) ||
                                (t.getChild(i + 3).getChild(0).getChild(0).label().value().equals("WP") && (t.getChild(i + 3).getChild(0).getChild(0).getChild(0).label().value().equals("who") || t.getChild(i + 3).getChild(0).getChild(0).getChild(0).label().value().equals("whom")))) {
                            String relClause = SentenceUtils.listToString(t.getChild(i).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 1).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 3).getChild(1).yield()) + " .";
                            String relClauseToDelete = "";

                            if (i == t.getChildrenAsList().size() - 4) {
                                relClauseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 3).yield());
                            } else if (t.getChild(i + 4).label().value().equals(",")) {
                                relClauseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 3).yield()) + " ,";
                            }

                            SentenceProcessor.updateSentence(relClause, relClauseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                            isSplit = true;
                        }
                    } else if (t.getChild(i + 3).getChild(0).label().value().equals("WHPP") && t.getChild(i + 3).getChild(1).label().value().equals("S")) {
                        if (t.getChild(i + 3).getChild(0).getChild(0).label().value().equals("IN") && t.getChild(i + 3).getChild(0).getChild(1).label().value().equals("WHNP")) {
                            if ((t.getChild(i + 3).getChild(0).getChild(1).getChild(0).label().value().equals("WDT") && t.getChild(i + 3).getChild(0).getChild(1).getChild(0).getChild(0).label().value().equals("which")) ||
                                    (t.getChild(i + 3).getChild(0).getChild(1).getChild(0).label().value().equals("WP") && (t.getChild(i + 3).getChild(0).getChild(1).getChild(0).getChild(0).label().value().equals("who") || t.getChild(i + 3).getChild(0).getChild(1).getChild(0).getChild(0).label().value().equals("whom")))) {
                                String relClause = SentenceUtils.listToString(t.getChild(i + 3).getChild(0).getChild(0).yield()) + " " + SentenceUtils.listToString(t.getChild(i).yield()) + " " + SentenceUtils.listToString(t.getChild(i + 3).getChild(1).yield()) + " .";
                                String relClauseToDelete = "";

                                if (i == t.getChildrenAsList().size() - 4) {
                                    relClauseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 3).yield());
                                } else if (t.getChild(i + 4).label().value().equals(",")) {
                                    relClauseToDelete = ", " + SentenceUtils.listToString(t.getChild(i + 3).yield()) + " ,";
                                }

                                SentenceProcessor.updateSentence(relClause, relClauseToDelete.trim(), sentence, coreContextSentence, isOriginal, contextNumber);
                                isSplit = true;
                            }
                        }
                    }
                }
            }
        }
        return isSplit;
    }
}
