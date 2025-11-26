import java.util.Scanner;

public class ReverseCompare {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int num1 = sc.nextInt();
        int num2 = sc.nextInt();

        int rev1 = reverseDigits(num1);
        int rev2 = reverseDigits(num2);

        String s = compareSymbol(rev1, rev2);
        if (s.equals(">")) {
            System.out.println(num2 +
                    " " +
                    "<" +
                    " " + num1);
        } else {
            System.out.println(num1 +
                    " " +
                    compareSymbol(rev1, rev2) +
                    " " + num2);
        }
    }

    // تابعی برای برعکس کردن ارزش مکانی عدد ۳ رقمی
    public static int reverseDigits(int n) {
        int hundreds = n / 100;
        int tens = (n / 10) % 10;
        int units = n % 10;

        return units * 100 + tens * 10 + hundreds;
    }

    // تابعی برای چاپ علامت مقایسه
    public static String compareSymbol(int a, int b) {
        if (a > b) return ">";
        else if (a < b) return "<";
        else return "=";
    }
}