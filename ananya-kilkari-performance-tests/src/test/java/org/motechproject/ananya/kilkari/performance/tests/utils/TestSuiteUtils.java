package org.motechproject.ananya.kilkari.performance.tests.utils;

import com.clarkware.junitperf.LoadTest;
import com.clarkware.junitperf.TestMethodFactory;
import junit.framework.Test;
import junit.framework.TestSuite;

public class TestSuiteUtils {


    public static TestSuite createTestSuite(int maxUsers, Class testClass, String methodName) {

        TestSuite testSuite = new TestSuite();
        Test testCase = new TestMethodFactory(testClass, methodName);
        Test loadTest = new LoadTest(testCase, maxUsers);
        testSuite.addTest(loadTest);
        return testSuite;
    }
}
