/*
 * ==========================License-Start=============================
 * sentence_simplification : SentenceSeparatorTest
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

package org.lambda3.text.simplification.sentence.segmentation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
class SentenceSeparatorTest {

    @Test
    void testSplitSmallTextIntoSentences() {
        String text = "Stanford University was founded in 1885 by Leland and Jane Stanford, dedicated to Leland Stanford Jr, their only child. " +
                "The institution opened in 1891 on Stanford's previous Palo Alto farm. " +
                "Despite being impacted by earthquakes in both 1906 and 1989, the campus was rebuilt each time.";

        final List<String> expected = Arrays.asList(
                "Stanford University was founded in 1885 by Leland and Jane Stanford, dedicated to Leland Stanford Jr, their only child.",
                "The institution opened in 1891 on Stanford's previous Palo Alto farm.",
                "Despite being impacted by earthquakes in both 1906 and 1989, the campus was rebuilt each time."
        );
        final List<String> actual = SentenceSeparator.splitIntoSentences(text);

        Assertions.assertIterableEquals(expected, actual);
    }

}