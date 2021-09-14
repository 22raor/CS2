package anagrams;
public class AlgorithmL {
    public static void main(String[] args) {
    	String a = "abcd";


        System.out.println(a.toCharArray());
        while (true) {
        	
            int j = a.toCharArray().length - 2;
            while (a.toCharArray()[j] >= a.toCharArray()[j+1]) {
                if (j == 0) System.exit(0);
                j--;
            }
            int l = a.toCharArray().length - 1;
            while (a.toCharArray()[j] >= a.toCharArray()[l]) {
                l--;
            }
            swap(a.toCharArray(), j, l);

            int k = j + 1;
            l = a.toCharArray().length - 1;
            while (k < l) {
                swap(a.toCharArray(), k, l);
                k++;
                l--;
            }
            System.out.println(a.toCharArray());
        }
    }

    private static void swap(char[] data, int i, int j) {
        char temp = data[i];
        data[i] = data[j];
        data[j] = temp;
    }
}