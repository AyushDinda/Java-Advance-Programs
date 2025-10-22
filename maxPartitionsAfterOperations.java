class Solution {

    public int maxPartitionsAfterOperations(String s, int k) {
        int n = s.length();

        // left[i] = {numPartitionsTillHere, bitmaskOfDistinct, countOfDistinct}
        int[][] left = new int[n][3];
        // right[i] = same as above but from the right side
        int[][] right = new int[n][3];

        /*
         * Step 1: Build prefix partition info
         * ----------------------------------
         * As we move left → right, we keep track of how many partitions have been formed,
         * the bitmask of characters in the current segment, and how many distinct chars
         * are in this ongoing segment.
         */
        int partitions = 0;
        int mask = 0;     // bitmask of current segment’s characters
        int distinct = 0; // number of distinct chars in current segment

        for (int i = 0; i < n - 1; i++) {
            int bit = 1 << (s.charAt(i) - 'a');
            // if new character for this segment
            if ((mask & bit) == 0) {
                distinct++;
                // if within k distinct limit, keep extending
                if (distinct <= k) {
                    mask |= bit;
                } else {
                    // exceeded k, start a new partition
                    partitions++;
                    mask = bit;
                    distinct = 1;
                }
            }
            // store state at i+1
            left[i + 1][0] = partitions;
            left[i + 1][1] = mask;
            left[i + 1][2] = distinct;
        }

        /*
         * Step 2: Build suffix partition info
         * -----------------------------------
         * Similar logic, but traverse right → left.
         */
        partitions = 0;
        mask = 0;
        distinct = 0;

        for (int i = n - 1; i > 0; i--) {
            int bit = 1 << (s.charAt(i) - 'a');
            if ((mask & bit) == 0) {
                distinct++;
                if (distinct <= k) {
                    mask |= bit;
                } else {
                    partitions++;
                    mask = bit;
                    distinct = 1;
                }
            }
            right[i - 1][0] = partitions;
            right[i - 1][1] = mask;
            right[i - 1][2] = distinct;
        }

        /*
         * Step 3: Combine prefix + suffix information
         * -------------------------------------------
         * Now we consider changing s[i] (or not) and estimate the total partitions.
         * We merge info from left[i] (prefix) and right[i] (suffix) to see how many
         * partitions can be formed after the optimal single character change.
         */
        int maxPartitions = 0;

        for (int i = 0; i < n; i++) {
            // basic segments from left + right sides
            int total = left[i][0] + right[i][0] + 2;

            // combined mask and distinct count from both sides
            int combinedMask = left[i][1] | right[i][1];
            int combinedDistinct = Integer.bitCount(combinedMask);

            /*
             * Now adjust total partitions based on the possibility of changing s[i]:
             * - If both sides already have k distinct chars, and combined < 26,
             *   changing s[i] could create an extra partition (+1)
             * - Otherwise, if combined distincts fit within k after one more char,
             *   we might merge one partition (-1)
             */
            if (left[i][2] == k && right[i][2] == k && combinedDistinct < 26) {
                total++;
            } else if (Math.min(combinedDistinct + 1, 26) <= k) {
                total--;
            }

            maxPartitions = Math.max(maxPartitions, total);
        }

        return maxPartitions;
    }
}
