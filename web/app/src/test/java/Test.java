/*
 * Licensed Materials - Property of PwC
 * (c) Copyright Pwc Corp. 2020. All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with PwC Corp.
 */

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by byang059 on 8/6/20
 */
public class Test {
    public static void main(String[] args) {
        List<Integer> integers = Arrays.asList(1, 3, 4, 2, 5, 6, 9, 7, 11, 14, 15, 19);
        for (int i = 0; i < integers.size(); i++) {
            final Integer numberA = integers.get(i);
            for (int j = i + 1; j < integers.size(); j++) {
                final Integer numberB = integers.get(j);
            }
        }
    }
}
