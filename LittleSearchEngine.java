package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile)
	throws FileNotFoundException {		
		// create a HasMap with words of the document as the keys and name of the document along with how many
		// time the word occurred as the value.
		HashMap<String,Occurrence> keyWords = new HashMap<String,Occurrence> ();
		// Read the file one line at a time.
		Scanner sc = new Scanner(new File(docFile));
		while (sc.hasNext()) {
			// split the line into individual words	
			String [] words = sc.next().split(" ");
			for(int i=0; i<words.length; i++) {
				String word = getKeyword(words[i]);
				if(word != null){
					Occurrence ocr = keyWords.get(word);
					if( ocr != null) {
						// word exist update frequency
						ocr.frequency++;
					}
					else { 
						// word does not exist add new word
						Occurrence newOcr = new Occurrence(docFile, 1);
						keyWords.put(word, newOcr);
					}
				}
			}
		}
		sc.close();
		//System.out.println("loadKeywordsFromDocument: keyWords = " + keyWords.toString());
		return keyWords;
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
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		// search keywordsIndex for each kws word.		
		for(Map.Entry<String, Occurrence> entry : kws.entrySet()){
			String word = entry.getKey();
			Occurrence newOcr = entry.getValue();
			ArrayList<Occurrence> ocrArray = keywordsIndex.get(word);
			if(ocrArray != null) {
				// found kws key word in keywordsIndex hash table. 
				// Append the new occurrence
				ocrArray.add(newOcr);
				insertLastOccurrence(ocrArray);				
			}
			else {
				// Did not find kws key word in keywordsIndex hash table.
				// simply create a new occurrence array and add it to keywordsIndex.
				ArrayList<Occurrence> newOcrArray = new ArrayList<Occurrence> ();
				newOcrArray.add(newOcr);
				keywordsIndex.put(word, newOcrArray);
			}
		}
		//System.out.println("mergeKeywords: keyworsIndex = " + keywordsIndex.toString());
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		//String str1 = word.replaceAll("\\p{Punct}","");
		String str1 = word.toLowerCase();
    	String [] tokens = str1.split("[^a-z]");
    	int wordCount = 0;
    	String str2 = "";
    	for (int i=0; i<tokens.length; i++) {
    	  	if(tokens[i].length() > 0) {
    	  		wordCount++;
    	  		str2 += tokens[i];
    	  	}
    	}
    	if(wordCount == 1) {
			if(!noiseWords.contains(str2)) {	
				//System.out.println("getKeyword for " + word + " returns " + str2);
				return str2;
			}
		}
		//System.out.println("getKeyword for " + word + " returns null");
		return null;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		int nSize = occs.size();
		if(nSize <= 1) {
			return null;
		}
		ArrayList<Integer> midValArrayList = new ArrayList<Integer> ();
		int start = 0;
		// remove the last element from the array list
		Occurrence lastOcr = occs.remove(nSize - 1);
		int end = occs.size() - 1;
		int mid = 0;
		while ( start <= end) {
			mid = (start + end) / 2;
			midValArrayList.add(mid);
			Occurrence ocr = occs.get(mid);
			if(ocr.frequency == lastOcr.frequency) {
				break;
			}
			else if (ocr.frequency < lastOcr.frequency) {
				end = mid - 1;
			}
			else {
				start = mid + 1;
				mid++;
			}
		}
		occs.add(mid, lastOcr);
	//	System.out.println("insertLastOccurrence: sorted occurrence = " + occs.toString());
	//	System.out.println("insertLastOccurrence: mid values = " + midValArrayList.toString());
		return midValArrayList;
	}
	
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
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
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
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
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, returns null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		ArrayList<String> docs = new ArrayList<String> (5);
		ArrayList<Occurrence> ocr1List = keywordsIndex.get(kw1);
		int ocr1Size = 0;
		if(ocr1List != null) {
			ocr1Size = ocr1List.size();
		}
		int ocr2Size = 0;
		ArrayList<Occurrence> ocr2List = keywordsIndex.get(kw2);
		if(ocr2List != null) {
			ocr2Size = ocr2List.size();
		}
		Occurrence ocr1 = null;
		Occurrence ocr2 = null;
		int i = 0;
		int j = 0;
		while(docs.size() < 5 ) {
			if((i < ocr1Size) && (j < ocr2Size )) {
				ocr1 = ocr1List.get(i);
				ocr2 = ocr2List.get(j);
				if(ocr1.frequency >= ocr2.frequency) {
					if(!docs.contains(ocr1.document)) {
						docs.add(ocr1.document);
					}
					i++;
				}
				else if(ocr2.frequency > ocr1.frequency) {
					if(!docs.contains(ocr2.document)) {
						docs.add(ocr2.document);
					}
					j++;
				}
			}
			else if(i < ocr1Size){
				ocr1 = ocr1List.get(i);
				if(!docs.contains(ocr1.document)) {
					docs.add(ocr1.document);
				}
				i++;
			}
			else if(j < ocr2Size){
				ocr2 = ocr2List.get(j);
				if(!docs.contains(ocr2.document)) {
					docs.add(ocr2.document);
				}
				j++;
			}
			else {
				break;
			}
		}
		return docs;	
	}
}
