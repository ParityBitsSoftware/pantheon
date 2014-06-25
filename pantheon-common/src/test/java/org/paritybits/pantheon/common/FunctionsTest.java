package org.paritybits.pantheon.common;

import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;

import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FunctionsTest {

    @Test
    public void constantFunctionReturnsConstant() {
        Integer c = RandomUtils.nextInt();
        Function<Integer, Integer> constant = Functions.constant(c);
        assertNotNull(constant);
        for(int i = 0; i < 10; i++) {
            assertEquals(c, constant.apply(RandomUtils.nextInt()));
        }
    }

}
