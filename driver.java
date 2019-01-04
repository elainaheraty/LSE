package lse;

//import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class driver {
	static Scanner stdin = new Scanner(System.in);

	public static void main(String[] args)
		throws IOException {
			System.out.print("Enter documents file name => ");
			String docsFile = stdin.nextLine();
			//Scanner scDocs = new Scanner(new File(docsFile));

			System.out.print("\nEnter noise file name => ");
			String noiseWordsFile = stdin.nextLine();
			//Scanner scNoise = new Scanner(new File(noiseWordsFile));
			LittleSearchEngine lse = new LittleSearchEngine();
			lse.makeIndex(docsFile, noiseWordsFile);
			System.out.print("\nEnter the key words, or hit return to quit => ");
			String [] words = stdin.nextLine().split(" ");
			if (words.length == 2) {
				System.out.println("Documents found = " + lse.top5search(words[0], words[1]));
			}
			else {
				System.out.println("Retry");				
			}
		}

}
