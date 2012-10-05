package org.paritybits.pantheon.common;

import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FunctionsTest {

    @Test
    public void identityReturnsParam() {
        Function<Integer, Integer> identity = Functions.identity();
        assertNotNull(identity);
        for(int i = 0; i < 10; i++) {
            Integer x = RandomUtils.nextInt();
            assertEquals(x, identity.evaluate(x));
        }
    }

    @Test
    public void constantFunctionReturnsConstant() {
        Integer c = RandomUtils.nextInt();
        Function<Integer, Integer> constant = Functions.constant(c);
        assertNotNull(constant);
        for(int i = 0; i < 10; i++) {
            assertEquals(c, constant.evaluate(RandomUtils.nextInt()));
        }
    }

}
