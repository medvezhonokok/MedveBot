package com.medvebot.features;

/**
 * @author : medvezhonokok
 * @mailto : nocap239@gmail.com
 * @created : 21.06.2022, вторник
 **/

import java.io.IOException;
import java.util.*;

public class WordStatInput {
    public static String solve(ArrayList<String> words, Scanner2021 in, String answer) throws IOException {
        while (in.hasNextLine()) {
            String str = in.nextLine().toLowerCase(Locale.ROOT);
            str += " ";
            StringBuilder word = new StringBuilder();
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                if (Character.isLetter(c) ||
                        Character.getType(c) == Character.DASH_PUNCTUATION
                        || c == '\'') {
                    word.append(str.charAt(i));
                } else {
                    if (word.length() != 0) {
                        words.add(word.toString());
                    }
                    word = new StringBuilder();
                }
            }
        }

        Set<String> finAns = new LinkedHashSet<>(words);
        StringBuilder answerBuilder = new StringBuilder(answer);
        for (String finAn : finAns) {
            int count = 0;

            StringBuilder ans = new StringBuilder();
            ans.append(finAn).append(" ");
            for (String word : words) {
                if (Objects.equals(finAn, word)) {
                    count++;
                }
            }

            ans.append(count);
            answerBuilder.append(ans).append("\n");
        }
        answer = answerBuilder.toString();

        return answer;
    }
}