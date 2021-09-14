package anagrams;
import java.util.Scanner;
import java.util.stream.Stream;

public class OneLineAnagram {

	public static void main(String[] args) {
		//The following line finds all anagrams of an input String


	Stream.iterate( new Scanner(System.in).next() ,  x -> x.length() != 1, x -> x.substring(1, x.length())).
	
	
	forEach(System.out::println);
	
	//flatMap(x -> Stream.of(x, "hi"))
	//Changing public key			
				
				

	}

}
