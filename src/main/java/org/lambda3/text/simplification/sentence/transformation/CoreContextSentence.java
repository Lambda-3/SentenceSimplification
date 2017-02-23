/*
 * ==========================License-Start=============================
 * sentence_simplification : CoreContextSentence
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

import edu.stanford.nlp.trees.Tree;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a core and its associated context sentences
 */
public class CoreContextSentence {

    private Tree original;
    private String input;
    private List<Tree> core = new ArrayList<>();
    private List<Tree> context = new ArrayList<>();
    private List<Tree> coreNew = new ArrayList<>();
    private List<Tree> attribution = new ArrayList<>();
    private List<String> contextWithNumber = new ArrayList<>();

    private boolean[] delete;

    public boolean[] getDelete() {
        return delete;
    }

    public void setDelete(boolean[] delete) {
        this.delete = delete;
    }

    public Tree getOriginal() {
        return this.original;
    }

    public void setOriginal(Tree original) {
        this.original = original;
    }

    public String getInput() {
        return this.input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public List<Tree> getCore() {
        return this.core;
    }

    public void setCore(List<Tree> core) {
        this.core = core;
    }

    public List<Tree> getContext() {
        return this.context;
    }

    public void setContext(List<Tree> context) {
        this.context = context;
    }

    public List<Tree> getCoreNew() {
        return this.coreNew;
    }

    public void setCoreNew(List<Tree> coreNew) {
        this.coreNew = coreNew;
    }

    public List<Tree> getAttribution() {
        return this.attribution;
    }

    public void setAttribution(List<Tree> attribution) {
        this.attribution = attribution;
    }

    public List<String> getContextWithNumber() {
        return this.contextWithNumber;
    }

    public void setConWithNumber(List<String> contextWithNumber) {
        this.contextWithNumber = contextWithNumber;
    }
}
