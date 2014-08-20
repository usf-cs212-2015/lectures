import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * <p>Project Euler Problem 1 is stated as follows:</p>
 *
 * <blockquote>
 * If we list all the natural numbers below 10 that are multiples
 * of 3 or 5, we get 3, 5, 6 and 9. The sum of these multiples is 23.
 *
 * Find the sum of all the multiples of 3 or 5 below 1000.
 * </blockquote>
 *
 * <p>When the max is 10, the solution is 23. When the max is 100, the
 * solution is 2318.</p>
 *
 * @see <a href="https://projecteuler.net/problem=1">
 *      https://projecteuler.net/problem=1</a>
 */
public class ProjectEuler01d {

    /*
     * Now, we make this code more robust by checking for exceptions, and
     * providing multiple ways to enter the maximum value.
     *
     * Note that we went from less than 15 to 40+ lines of code! This might be
     * too simple of a problem to justify this much extra work, but for large
     * projects you will benefit from the reusability of your code.
     */

    public static int sumMultiples(int max) {
        int sum = 0;

        for (int i = 0; i < max; i++) {
            if (((i % 3) == 0) || ((i % 5) == 0)) {
                sum += i;
            }
        }

        return sum;
    }

    public static void main(String[] args) {
        int max = -1;

        if (args.length > 0) {
            try {
                max = Integer.parseInt(args[0]);
            }
            catch (NumberFormatException ex) {
                max = -1;
            }
        }
        else {
            try (Scanner scanner = new Scanner(System.in)) {
                System.out.print("Enter positive integer: ");
                max = scanner.nextInt();
            }
            catch (NoSuchElementException ex) {
                max = -1;
            }
        }

        if (max < 0) {
            System.out.println("Maximum value must be a positive integer! " +
                    "Using default value of 10 instead.")
                    ;
            max = 10;
        }

        System.out.printf("The sum of multiples of 3 or 5 less than %d is %d.",
                max, sumMultiples(max));
    }
}