/*
 * ==========================License-Start=============================
 * sentence_simplification : TransformerTest
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class TransformerTest {

    @Test
    void testSimplificationWithSimpleSentence() throws SentenceSimplifyingException {
        Transformer t = new Transformer();

        String text = "He plays basketball, a sport he participated in as a member of his high school’s varsity team.";

        List<String> expectedCores = Collections.singletonList(
                "he plays basketball ."
        );

        List<String> expectedContexts = Collections.singletonList(
                "basketball is a sport he participated in as a member of his high school 's varsity team ."
        );

        CoreContextSentence simplified = t.simplify(text);

        List<String> cores = new ArrayList<>();
        List<String> contexts = new ArrayList<>();


        for (Tree core : simplified.getCore()) {
            cores.add(SentenceUtils.listToString(core.yield()));
        }
        for (Tree context : simplified.getContext()) {
            contexts.add(SentenceUtils.listToString(context.yield()));
        }

        Assertions.assertIterableEquals(expectedCores, cores);
        Assertions.assertIterableEquals(expectedContexts, contexts);
    }

}