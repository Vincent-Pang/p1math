import rx.Observable;

import java.util.*;

public class Main
{
    public static void main(String[] args)
    {
	    // write your code here
        System.out.println("hello");

        final int RESULT = 111;

        Observable<Integer> ab$ = create20To99();
        Observable<Integer> cd$ = create20To99();
        Observable<Integer> ef$ = create20To99();
        Observable<Integer> gh$ = create20To99();

        ab$
            .subscribe(ab ->
            {
                cd$
                    .filter(cd -> containAllDifferentDigit(ab, cd))
                    .subscribe(cd ->
                    {
                        ef$
                            .filter(ef -> ef == ab - cd)
                            .filter(ef -> containAllDifferentDigit(ab, cd, ef))
                            .subscribe(ef ->
                            {
                                gh$
                                    .filter(gh -> RESULT == ab - cd + gh)
                                    .filter(gh -> containAllDifferentDigit(ab, cd, ef, gh))
                                    .subscribe(gh ->
                                    {
                                        System.out.println("-----");
                                        System.out.println("ab = " + ab);
                                        System.out.println("cd = " + cd);
                                        System.out.println("ef = " + ef);
                                        System.out.println("gh = " + gh);
                                        System.out.println("ab - cd + gh = " + (ab - cd + gh));
                                    });
                            });
                    });
            });
    }

    private static Observable<Integer> create20To99()
    {
        return Observable.range(20, 80) // gen 20 to 99
                .filter(v -> (v / 10) != (v % 10))  // filter out 22, 33, 44 ...
                .filter(v -> (v / 10) != 1 && (v % 10) != 1);   // filter out 1x or x1
    }

    private static boolean containAllDifferentDigit(int... numbers)
    {
        Set<Integer> allDigits = new HashSet<>();

        Arrays.stream(numbers)
            .forEach( v -> allDigits.addAll(intToDigits(v)) );

        int numOfDigit = Arrays.stream(numbers)
                            .map(Main::numberOfDigit)
                            .sum();

        return allDigits.size() == numOfDigit;
    }

    private static List<Integer> intToDigits(int num)
    {
        List<Integer> result = new ArrayList<>();

        int absNum = Math.abs(num);

        while (absNum > 0)
        {
            result.add(absNum % 10);
            absNum /= 10;
        }

        return result;
    }

    private static int numberOfDigit(int num)
    {
        int absNum = Math.abs(num);

        return (int)(Math.log10(absNum) + 1);
    }
}
