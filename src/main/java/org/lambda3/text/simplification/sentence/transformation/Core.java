/*
 * ==========================License-Start=============================
 * sentence_simplification : Core
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

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a core sentence
 */
public class Core {

    public List<Integer> start = new ArrayList<>();
    public List<Integer> end = new ArrayList<>();
    public List<String> prefix = new ArrayList<>();
    public List<String> postfix = new ArrayList<>();

    public List<Integer> getStart() {
        return start;
    }

    public void setStart(Integer st) {
        start.add(st);
    }

    public List<Integer> getEnd() {
        return end;
    }

    public void setEnd(Integer e) {
        start.add(e);
    }

    public List<String> getPrefix() {
        return prefix;
    }

    public void setPrefix(String pre) {
        prefix.add(pre);
    }

    public List<String> getPostfix() {
        return postfix;
    }

    public void setPostfix(String post) {
        postfix.add(post);
    }

}
