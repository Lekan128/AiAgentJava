package org.example.summerize;

public class SimpleSummariser {

    /*
    //Apache OpenNLP + simple frequency scoring
    public static String summarize(String text, int maxSentences) throws Exception {
        // Load sentence detector model
        SentenceModel model = new SentenceModel(new FileInputStream("en-sent.bin"));
        SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);

        String[] sentences = sentenceDetector.sentDetect(text);

        // Simple word frequency scoring
        Map<String, Integer> freq = new HashMap<>();
        for (String sentence : sentences) {
            for (String word : sentence.toLowerCase().replaceAll("[^a-z ]", "").split("\\s+")) {
                if (!word.isBlank()) {
                    freq.put(word, freq.getOrDefault(word, 0) + 1);
                }
            }
        }

        // Score each sentence by sum of word frequencies
        List<String> ranked = Arrays.asList(sentences);
        ranked.sort((a, b) -> score(b, freq) - score(a, freq));

        // Take top N
        return String.join(" ", ranked.subList(0, Math.min(maxSentences, ranked.size())));
    }

    private static int score(String sentence, Map<String, Integer> freq) {
        int score = 0;
        for (String word : sentence.toLowerCase().replaceAll("[^a-z ]", "").split("\\s+")) {
            score += freq.getOrDefault(word, 0);
        }
        return score;
    }*/

    public static String summarise(String s){
        if (s == null) return null;
        int length = 800;
        //Todo: find a better way to summarise

        if (s.length() < 800) return s;
        return s.substring(0, 800).replace("\n", " ") + "...";
    }
}
