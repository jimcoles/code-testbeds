package org.jkcsoft.codetests.hackerrank.fraudcheck;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * This solution takes advantage of integer-valued sorting to track the median value
 * by computing which bucket the median is in. This approach would not work
 * if those were real (float) -valued.
 *
 * @author Jim Coles
 */
public class SolutionBuckets {

    // Complete the activityNotifications function below.
    static int activityNotifications(int[] expenditure, int trailingDays) {
        int numNotifs = 0;
        boolean isOddNum = trailingDays % 2 != 0;
        long markTime = System.currentTimeMillis();

        int medianRelIdx = isOddNum ? trailingDays / 2 : (trailingDays / 2)  - 1;
        Map<Integer, ExpBucket> buckets = new TreeMap<>();
        for (int idxExp = 0; idxExp < trailingDays; idxExp++) {
            int exp = expenditure[idxExp];
            incrExp(buckets, exp);
        }

        System.out.println("num buckets: " + buckets.size());

        for(int idxEvalDay = trailingDays; idxEvalDay < expenditure.length; idxEvalDay++) {
            int expEvalDay = expenditure[idxEvalDay];
            boolean shouldLog = shouldLog(expenditure, idxEvalDay);

            if (idxEvalDay > trailingDays) {
                int idxPopDay = idxEvalDay - trailingDays - 1;
                int idxPushDay = idxEvalDay - 1;
                decExp(buckets, expenditure[idxPopDay]);
                incrExp(buckets, expenditure[idxPushDay]);
            }
            // delete one value == previous leftmost (drops off the table); add current
            // using insertion sort
            float median = getMedianIndex(buckets, medianRelIdx, isOddNum);
            float maxAllowedExp = 2 * median;
            if (expEvalDay >= maxAllowedExp) {
                System.out.println(String.format("warning: on day %d, debit of %d >= 2 x median %.1f = %.1f",
                                                 idxEvalDay, expEvalDay, median, maxAllowedExp));
                numNotifs++;
            }

            if (shouldLog) {
                long nowTime = System.currentTimeMillis();
                long delta = nowTime - markTime;
                System.out.println(String.format("time delta [" + delta + "] progress %d num: %d",
                                                 idxEvalDay, numNotifs));
                markTime = nowTime;
            }

        }

        return numNotifs;
    }

    private static void incrExp(Map<Integer, ExpBucket> buckets, int exp) {
        ExpBucket expBucket = buckets.get(exp);
        if (expBucket == null) {
            expBucket = new ExpBucket(exp);
            buckets.put(expBucket.value, expBucket);
            expBucket.count = 1;
        }
        else
            expBucket.count++;
    }

    private static void decExp(Map<Integer, ExpBucket> buckets, int exp) {
        ExpBucket expBucket = buckets.get(exp);
        if (expBucket == null) {
            throw new IllegalArgumentException("should never decrement expenditure value");
        }
        else
            expBucket.count--;
    }

    private static boolean shouldLog(int[] expenditure, int idxEvalDay) {
        return expenditure.length <= 100 || idxEvalDay % (expenditure.length/100) == 0;
    }

    static class ExpBucket {
        int value;
        int count = 0;

        public ExpBucket(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "Bucket {val=" + value + " cnt=" + count + '}';
        }
    }

    static int getValAtIndex(Map<Integer, ExpBucket> buckets, int relativeIdx) {
        int medianValue = 0;
        int nextBucketStartIdx = 0;
        int thisBucketStartIdx = -1;
        for (Map.Entry<Integer, ExpBucket> bucketEntry : buckets.entrySet()) {
            nextBucketStartIdx += bucketEntry.getValue().count;
            if (relativeIdx >= thisBucketStartIdx && relativeIdx < nextBucketStartIdx) {
                medianValue = bucketEntry.getValue().value;
                break;
            }
            thisBucketStartIdx = nextBucketStartIdx;
        }
        return medianValue;
    }

    static float getMedianIndex(Map<Integer, ExpBucket> buckets, int relativeIdx, boolean isOddNum) {
        return isOddNum ?
            getValAtIndex(buckets, relativeIdx) :
            (getValAtIndex(buckets, relativeIdx) + getValAtIndex(buckets, relativeIdx + 1)) / 2.0f;
    }

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        String[] nd = scanner.nextLine().split(" ");

        int n = Integer.parseInt(nd[0]);

        int d = Integer.parseInt(nd[1]);

        int[] expenditure = new int[n];

        String[] expenditureItems = scanner.nextLine().split(" ");
        scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

        for (int i = 0; i < n; i++) {
            int expenditureItem = Integer.parseInt(expenditureItems[i]);
            expenditure[i] = expenditureItem;
        }

        int result = activityNotifications(expenditure, d);

        bufferedWriter.write(String.valueOf(result));
        bufferedWriter.newLine();

        bufferedWriter.close();

        scanner.close();
    }
}
