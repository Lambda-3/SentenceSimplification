/*
 * ==========================License-Start=============================
 * sentence_simplification : SentenceSeparator
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

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import org.lambda3.text.simplification.sentence.transformation.CoreContextSentence;
import org.lambda3.text.simplification.sentence.transformation.FileOperator;
import org.lambda3.text.simplification.sentence.transformation.Transformer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for decomposing an input NL text into individual sentences.
 * This class constitutes "stage 1" of the simplification framework.
 */
public class SentenceSeparator {

    private static final StanfordCoreNLP NLP_PIPELINE = new StanfordCoreNLP("CoreNLP.properties");

    private static final String SPACE = " ";

    /**
     * takes the input NL text as its first parameter and outputs the simplified version in the file specified in the second argument
     *
     * @param args: input file, output file
     * @throws IOException when input or output files cannot be read or written
     */
    public static void main(String[] args) throws IOException {

        String input = args[0]; //"data/Wikipedia/Eval/baseball/bugs";
        String output = args[1]; //"data/Wikipedia/Eval/baseball/baseballResultBugs";

        List<String> sen = splitIntoSentences(new File(input));

        List<CoreContextSentence> simplified = new Transformer().simplify(sen);

        try {
            FileOperator fo = new FileOperator();
            fo.writeFile(simplified, new File(output));
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Splits the input text into a list of its sentences.
     *
     * @param text A longer text that will be split into its sentences
     * @return A list of strings for all sentences inside the given text
     */
    public static List<String> splitIntoSentences(String text) {

        Annotation document = new Annotation(text);
        NLP_PIPELINE.annotate(document);

        List<CoreMap> s = document.get(SentencesAnnotation.class);
        List<String> sen = new ArrayList<>();

        for (CoreMap c : s) {
            int counter = 0;
            for (@SuppressWarnings("unused") CoreLabel token : c.get(TokensAnnotation.class)) {
                counter++;
            }
            if (counter < 100) {
                String input = c.toString();
                input = input.replaceAll("\\[(.*?)\\]", "");
                input = input.trim();
                if (!input.isEmpty()) {
                    sen.add(input);
                }
            }
        }
        return sen;
    }

    /**
     * Splits NL text from the given file into single sentences.
     *
     * @param file file containing NL text
     * @return list of individual sentences
     * @throws FileNotFoundException
     */
    public static List<String> splitIntoSentences(File file) throws FileNotFoundException {
        StringBuilder sb = new StringBuilder();
        try (FileReader fr = new FileReader(file);
             BufferedReader br = new BufferedReader(fr)) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append(SPACE);
            }
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return splitIntoSentences(sb.toString());
    }

}
