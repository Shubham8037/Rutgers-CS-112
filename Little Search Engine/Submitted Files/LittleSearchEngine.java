package lse;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 */
public class LittleSearchEngine {

    /**
     * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
     * an array list of all occurrences of the keyword in documents. The array list is maintained in
     * DESCENDING order of frequencies.
     */
    HashMap<String, ArrayList<Occurrence>> keywordsIndex;

    /**
     * The hash set of all noise words.
     */
    HashSet<String> noiseWords;

    /**
     * Creates the keyWordsIndex and noiseWords hash tables.
     */
    public LittleSearchEngine() {
        keywordsIndex = new HashMap<String, ArrayList<Occurrence>>(1000, 2.0f);
        noiseWords = new HashSet<String>(100, 2.0f);
    }

    /**
     * Scans a document, and loads all keywords found into a hash table of keyword occurrences
     * in the document. Uses the getKeyWord method to separate keywords from other words.
     *
     * @param docFile Name of the document file to be scanned and loaded
     * @return Hash table of keywords in the given document, each associated with an Occurrence object
     * @throws FileNotFoundException If the document file is not found on disk
     */
    public HashMap<String, Occurrence> loadKeywordsFromDocument(String docFile)
            throws FileNotFoundException {

        HashMap<String, Occurrence> keywordsMap = new HashMap<>();
        Scanner sc = new Scanner(new File(docFile));

        while (sc.hasNext()) {
            String word = sc.next();
            String keyword = getKeyword(word);

            if (keyword != null) {
                int frequency = 0;
                Occurrence existing = keywordsMap.get(keyword);
                if (existing != null) {
                    frequency = existing.frequency;
                }

                Occurrence updated = new Occurrence(docFile, ++frequency);
                keywordsMap.put(keyword, updated);
            }
        }

        return keywordsMap;
    }

    /**
     * Merges the keywords for a single document into the master keywordsIndex
     * hash table. For each keyword, its Occurrence in the current document
     * must be inserted in the correct place (according to descending order of
     * frequency) in the same keyword's Occurrence list in the master hash table.
     * This is done by calling the insertLastOccurrence method.
     *
     * @param kws Keywords hash table for a document
     */
    public void mergeKeywords(HashMap<String, Occurrence> kws) {

        for (String keyword : kws.keySet()) {
            Occurrence occToInsert = kws.get(keyword);
            ArrayList<Occurrence> occListInMaster = keywordsIndex
                    .computeIfAbsent(keyword, k -> new ArrayList<>());

            occListInMaster.add(occToInsert);
            insertLastOccurrence(occListInMaster);
        }
    }

    /**
     * Given a word, returns it as a keyword if it passes the keyword test,
     * otherwise returns null. A keyword is any word that, after being stripped of any
     * trailing punctuation, consists only of alphabetic letters, and is not
     * a noise word. All words are treated in a case-INsensitive manner.
     * <p>
     * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
     *
     * @param word Candidate word
     * @return Keyword (word without trailing punctuation, LOWER CASE)
     */
    public String getKeyword(String word) {
        if (isEmpty(word)) return null;
        word = word.toLowerCase();

        while (!Character.isAlphabetic(word.charAt(word.length() - 1))) {
            word = word.substring(0, word.length() - 1);
            if (isEmpty(word)) return null;
        }

        for (char character : word.toCharArray()) {
            if (!Character.isAlphabetic(character)) return null;
        }

        if (noiseWords.contains(word)) return null;

        return word;
    }

    private boolean isEmpty(String text) {
        return text == null || text.equals("");
    }

    /**
     * Inserts the last occurrence in the parameter list in the correct position in the
     * list, based on ordering occurrences on descending frequencies. The elements
     * 0..n-2 in the list are already in the correct order. Insertion is done by
     * first finding the correct spot using binary search, then inserting at that spot.
     *
     * @param occs List of Occurrences
     * @return Sequence of mid point indexes in the input list checked by the binary search process,
     * null if the size of the input list is 1. This returned array list is only used to test
     * your code - it is not used elsewhere in the program.
     */
    public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {

        int size = occs.size();
        if (size <= 1) return null;

        ArrayList<Integer> indices = new ArrayList<>();
        Occurrence occToInsert = occs.remove(size - 1);

        int highIndex = 0;
        int lowIndex = size - 2;

        do {
            int sum = highIndex + lowIndex;
            int mid = sum / 2;
            indices.add(mid);

            Occurrence occToCheck = occs.get(mid);
            if (occToInsert.frequency > occToCheck.frequency) {
                lowIndex = mid;
            } else if (occToInsert.frequency < occToCheck.frequency) {
                highIndex = mid + 1;
            } else {
                lowIndex = mid;
                break;
            }
        } while (highIndex < lowIndex);

        Occurrence occToCheck = occs.get(lowIndex);
        int offset = occToInsert.frequency > occToCheck.frequency ? 0 : 1;
        occs.add(lowIndex + offset, occToInsert);

        return indices;
    }

    /**
     * This method indexes all keywords found in all the input documents. When this
     * method is done, the keywordsIndex hash table will be filled with all keywords,
     * each of which is associated with an array list of Occurrence objects, arranged
     * in decreasing frequencies of occurrence.
     *
     * @param docsFile       Name of file that has a list of all the document file names, one name per line
     * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
     * @throws FileNotFoundException If there is a problem locating any of the input files on disk
     */
    public void makeIndex(String docsFile, String noiseWordsFile)
            throws FileNotFoundException {
        // load noise words to hash table
        Scanner sc = new Scanner(new File(noiseWordsFile));
        while (sc.hasNext()) {
            String word = sc.next();
            noiseWords.add(word);
        }

        // index all keywords
        sc = new Scanner(new File(docsFile));
        while (sc.hasNext()) {
            String docFile = sc.next();
            HashMap<String, Occurrence> kws = loadKeywordsFromDocument(docFile);
            mergeKeywords(kws);
        }
        sc.close();
    }

    /**
     * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
     * document. Result set is arranged in descending order of document frequencies. (Note that a
     * matching document will only appear once in the result.) Ties in frequency values are broken
     * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
     * also with the same frequency f1, then doc1 will take precedence over doc2 in the result.
     * The result set is limited to 5 entries. If there are no matches at all, result is null.
     *
     * @param kw1 First keyword
     * @param kw1 Second keyword
     * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
     * frequencies. The result size is limited to 5 documents. If there are no matches, returns null.
     */
    public ArrayList<String> top5search(String kw1, String kw2) {

        Map<String, Integer> doc2FreqMap = new TreeMap<>();

        for (String keyWord : new String[]{kw1, kw2}) {

            ArrayList<Occurrence> occurrences = keywordsIndex.get(keyWord);
            for (Occurrence occurrence : getTopFiveOccs(occurrences)) {

                String docName = occurrence.document;

                if (doc2FreqMap.containsKey(docName)) {
                    int existingFreq = doc2FreqMap.get(docName);
                    if (existingFreq < occurrence.frequency) {
                        doc2FreqMap.put(docName, occurrence.frequency);
                    }

                } else {
                    doc2FreqMap.put(docName, occurrence.frequency);
                }

            }

        }

        if (doc2FreqMap.size() == 0) return null;

        Map<String, Integer> sortedMap = sortMapByValue(doc2FreqMap, kw1, kw2);
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(sortedMap.entrySet());

        ArrayList<String> resultList = new ArrayList<>();
        for (int i = 0; i < sortedEntries.size(); i++) {
            if (i > 4) break;
            resultList.add(sortedEntries.get(i).getKey());
        }

        return resultList;
    }

    private ArrayList<Occurrence> getTopFiveOccs(ArrayList<Occurrence> occurrences) {

        ArrayList<Occurrence> topList = new ArrayList<>();
        if (occurrences == null) return topList;

        for (int i = 0; i < occurrences.size(); i++) {
            if (i > 4) break;
            topList.add(occurrences.get(i));
        }

        return topList;
    }

    private Map<String, Integer> sortMapByValue(Map<String, Integer> map, String kw1, String kw2) {

        List<Map.Entry<String, Integer>> entries = new ArrayList<>(map.entrySet());
        entries.sort((o1, o2) -> {
            int value2 = o2.getValue();
            int value1 = o1.getValue();

            if (value1 != value2) {
                return value2 - value1;
            } else {
                ArrayList<Occurrence> occurrences1 = keywordsIndex.get(kw1);

                Occurrence occOfKw1InDoc1 = findOccurrenceForDoc(occurrences1, o1.getKey());
                Occurrence occOfKw1InDoc2 = findOccurrenceForDoc(occurrences1, o2.getKey());

                if (occOfKw1InDoc1 == null || occOfKw1InDoc2 == null) {
                    if (occOfKw1InDoc1 == null && occOfKw1InDoc2 != null) return 1;
                    if (occOfKw1InDoc1 != null) return -1;
                    return 0;
                }

                return occOfKw1InDoc2.frequency - occOfKw1InDoc1.frequency;
            }
        });

        Map<String, Integer> sortedMap = new LinkedHashMap<>();

        for (Map.Entry<String, Integer> entry : entries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    private Occurrence findOccurrenceForDoc(ArrayList<Occurrence> occurrences, String docName) {
        for (Occurrence occurrence : occurrences) {
            if (occurrence.document.equals(docName)) {
                return occurrence;
            }
        }
        return null;
    }

}
