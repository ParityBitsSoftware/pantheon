package org.paritybits.pantheon.common;

import org.junit.Test;
import org.paritybits.pantheon.common.FunctionMap.PutFunction;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class FunctionMapTest{

    @Test
    public void passthroughMethods() {
        //Test that collect behavior is as expected and that
        //With no function passed in the default replacement
        //is used.
        FunctionMap<String, Integer> functionMap =
                new FunctionMap<String, Integer>();
        assertTrue(functionMap.isEmpty());
        assertEquals(0, functionMap.size());
        Map<String, Integer> expectedMap = new HashMap<String, Integer>();
        for (int i = 0; i < 100; i++) {
            Integer expected = expectedMap.put(Integer.toString(i), i);
            assertEquals(expected, functionMap.put(Integer.toString(i), i));
        }
        assertEquals(expectedMap.entrySet(), functionMap.entrySet());
        assertFalse(functionMap.isEmpty());
        assertEquals(100, functionMap.size());
        assertEquals(Integer.valueOf(1), functionMap.get("1"));
        assertEquals(Integer.valueOf(1), functionMap.remove("1"));
        assertNull(functionMap.get("1"));
        assertEquals(Integer.valueOf(2), functionMap.put("2", -5));
        assertEquals(Integer.valueOf(-5), functionMap.get("2"));
        functionMap.clear();
        assertTrue(functionMap.isEmpty());
        assertEquals(0, functionMap.size());
    }

    @Test
    public void putUsesFunction() {
        FunctionMap<String, Integer> replacingMap =
                new FunctionMap<String, Integer>(
                        new PutFunction<Integer>() {
                            public Integer determineValue(Integer oldValue,
                                                          Integer newValue) {
                                return oldValue + newValue;
                            }
                        });
        assertNull(replacingMap.put("test", 10));
        assertEquals(Integer.valueOf(10), replacingMap.get("test"));
        assertEquals(Integer.valueOf(10), replacingMap.put("test", 5));
        assertEquals(Integer.valueOf(15), replacingMap.get("test"));
        assertEquals(Integer.valueOf(15), replacingMap.put("test", 30));
        assertEquals(Integer.valueOf(45), replacingMap.get("test"));

        PutFunction<Integer> multi = new PutFunction<Integer>() {
            public Integer determineValue(Integer oldValue, Integer newValue) {
                return oldValue * newValue;
            }
        };
        replacingMap.setKeyFunction("multi", multi);
        assertNull(replacingMap.put("multi", 10));
        assertEquals(Integer.valueOf(10), replacingMap.get("multi"));
        assertEquals(Integer.valueOf(10), replacingMap.put("multi", 5));
        assertEquals(Integer.valueOf(50), replacingMap.get("multi"));
        assertEquals(Integer.valueOf(50), replacingMap.put("multi", 30));
        assertEquals(Integer.valueOf(1500), replacingMap.get("multi"));

        //Check that the default is still value
        replacingMap.put("test2", 10);
        replacingMap.put("test2", 10);
        assertEquals(Integer.valueOf(20), replacingMap.get("test2"));
    }

    @Test
    public void passthroughWithSuppliedMap() throws Exception {
        Map<String, Integer> wrappedMap = new HashMap<String, Integer>();
        FunctionMap<String, Integer> functionMap =
                new FunctionMap<String, Integer>(wrappedMap);

        assertEquals(wrappedMap, functionMap);
        for (int i = 0; i < 100; i++) {
            wrappedMap.put("test-" + i, i);
            assertEquals(wrappedMap, functionMap);
        }
    }
}
