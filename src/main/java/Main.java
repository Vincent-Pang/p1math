import rx.Observable;
import java.util.*;

public class Main
{
    public static void main(String[] args)
    {
        System.out.println("p1math");

        long startTime = System.nanoTime();

        // both method1 and method2 have same result, but method1 is faster
        // faster
        method1();

        // slower
        //method2();

        long endTime = System.nanoTime() - startTime;

        System.out.println();
        System.out.println("Elapsed Time = " + endTime + " nano sec");
        System.out.println("Elapsed Time = " + endTime / 1000 + " micro sec");
        System.out.println("Elapsed Time = " + endTime / 1000 / 1000+ " milli sec");
    }

    private static void method1()
    {
        final int RESULT = 111;

        Observable<Integer> ab$ = create20To99();
        Observable<Integer> cd$ = create20To99();
        Observable<Integer> ef$ = create20To99();
        Observable<Integer> gh$ = create20To99();

        ab$
            .subscribe(ab ->
            {
                cd$
                    .filter(cd -> containAllDifferentDigit(ab, cd)) // filter in when ab, cd are all different
                    .subscribe(cd ->
                    {
                        ef$
                            .filter(ef -> ef == ab - cd)    // filter in ab - cd == ef
                            .filter(ef -> containAllDifferentDigit(ab, cd, ef)) // filter in when ab, cd, ef are all different
                            .subscribe(ef ->
                            {
                                gh$
                                    .filter(gh -> RESULT == ef + gh)   // filter in ef + gh == 111
                                    .filter(gh -> containAllDifferentDigit(ab, cd, ef, gh)) // filter in when ab, cd, ed, gh are all different
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

    private static void method2()
    {
        final int RESULT = 111;

        Observable<Integer> ab$ = create20To99();
        Observable<Integer> cd$ = create20To99();
        Observable<Integer> ef$ = create20To99();
        Observable<Integer> gh$ = create20To99();

        ab$.join(cd$
                , integer -> Observable.never()
                , integer -> Observable.never()
                , (ab, cd) ->
                {
                    // join all combination of ab and cd
                    int[] tuple = {ab, cd};
                    return tuple;
                })
            .filter(Main::containAllDifferentDigit) // filter in when ab, cd are all different
            .join(ef$
                , integer -> Observable.never()
                , integer -> Observable.never()
                , (abcd, ef) ->
                {
                    // join the above result with ef
                    int[] tuple = {abcd[0], abcd[1], ef};
                    return tuple;
                })
            .filter(tuple -> tuple[0] - tuple[1] == tuple[2]) // filter in ab - cd == ef
            .filter(Main::containAllDifferentDigit) // filter in when ab, cd, ef are all different
            .join(gh$
                , integer -> Observable.never()
                , integer -> Observable.never()
                , (abcdef, gh) ->
                {
                    // join the above result with gh
                    int[] tuple = {abcdef[0], abcdef[1], abcdef[2], gh};
                    return tuple;
                })
            .filter(tuple -> tuple[2] + tuple[3] == RESULT) // filter in ef + gh == 111
            .filter(Main::containAllDifferentDigit) // filter in when ab, cd, ed, gh are all different
            .subscribe(tuple ->
            {
                System.out.println("-----");
                System.out.println("ab = " + tuple[0]);
                System.out.println("cd = " + tuple[1]);
                System.out.println("ef = " + tuple[2]);
                System.out.println("gh = " + tuple[3]);
                System.out.println("ab - cd + gh = " + (tuple[0] - tuple[1] + tuple[3]));
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
