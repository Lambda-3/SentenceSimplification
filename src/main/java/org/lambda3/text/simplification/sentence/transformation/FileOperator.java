/*
 * ==========================License-Start=============================
 * sentence_simplification : FileOperator
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

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class for reading the input NL text file and writing the output file
 */
public class FileOperator {

    /**
     * reads the input file
     *
     * @param file containing NL text
     * @return list of lines of text from the input file
     * @throws FileNotFoundException
     */
    public List<String> readFile(File file) throws FileNotFoundException {

        List<String> sentences = new ArrayList<>();
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);

        try {
            String line;
            while ((line = br.readLine()) != null) {
                sentences.add(line);
            }
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return sentences;
    }


    /**
     * writes the output file
     *
     * @param sentences List of simplified sentences
     * @param file      output file
     * @throws IOException
     */
    public void writeFile(List<CoreContextSentence> sentences, File file) throws IOException {

        FileWriter writer = new FileWriter(file);

        for (CoreContextSentence s : sentences) {
            try {
                writer.write("original sentence: " + s.getInput() + "\n");
                int counterCore = 0;

                for (Tree t : s.getCore()) {

                    String sentenceString = SentenceUtils.listToString(t.yield());

                    if (!sentenceString.equals("If")) {
                        String str = "";

                        if (!sentenceString.endsWith(".") && !sentenceString.endsWith(". ''")) {
                            str = sentenceString + " .";
                        } else {
                            str = sentenceString;
                        }

                        writer.write("core sentence: " + str.substring(0, 1).toUpperCase() + str.substring(1) + "\n");
                        for (String tCon : s.getContextWithNumber()) {
                            if (tCon != null) {
                                String cStr = "" + counterCore;
                                if (tCon.endsWith(cStr)) {
                                    tCon = tCon.replace(". " + cStr, ".");
                                    writer.write("context sentence: " + tCon.substring(0, 1).toUpperCase() + tCon.substring(1) + "\n");
                                }
                            }
                        }
                    }
                }

                Collections.reverse(s.getCoreNew());
                int counter = 1;

                for (Tree t : s.getCoreNew()) {
                    String str = SentenceUtils.listToString(t.yield());
                    writer.write("core sentence: " + str.substring(0, 1).toUpperCase() + str.substring(1) + "\n");

                    for (String tCon : s.getContextWithNumber()) {
                        if (tCon != null) {
                            String cStr = "" + counter;
                            if (tCon.endsWith(cStr)) {
                                tCon = tCon.replace(". " + cStr, ". ");
                                writer.write("context sentence: " + tCon.substring(0, 1).toUpperCase() + tCon.substring(1) + "\n");
                            }
                        }
                    }
                    counter++;
                }
                writer.write("\n");
                writer.write("\n");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        writer.flush();
        writer.close();
    }


    /**
     * writes the sentences that have not been processed by the framework to a file - for evaluation purposes
     *
     * @param eliminatedSentences
     * @param file
     * @throws IOException
     */
    public void writeFileSentencesToDelete(ArrayList<Integer> eliminatedSentences, File file) throws IOException {

        FileWriter writer = new FileWriter(file);
        try {
            for (Integer i : eliminatedSentences) {
                writer.write(i.toString());
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        writer.flush();
        writer.close();
    }

}
