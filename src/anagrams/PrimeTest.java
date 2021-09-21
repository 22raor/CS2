package anagrams;

public class PrimeTest {

	public static void main(String[] args) {
		for(int i = 1; i< 101; i++) {
			if(isPrime(i)) {
				System.out.println(i);
			}
		}

	}
	
	public static boolean isPrime(int num) {
		if(num ==2 || num==3 || num==5 || num==7) {
			return true;
		}
		if(num % 2 == 0 || num%3 == 0 || num%5 ==0 || num%7 == 0) {
			return false;
		}
		
		return true;
	}

}
