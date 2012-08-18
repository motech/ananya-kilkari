package org.motechproject.ananya.kilkari.performance.tests.utils.runner;

import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class LoadRunner extends BlockJUnit4ClassRunner {

    public LoadRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    public Description getDescription() {
        return super.getDescription();
    }

    @Override
    public void run(RunNotifier notifier) {
        super.run(notifier);
    }

    @Override
    protected Statement methodBlock(final FrameworkMethod method) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                LoadTest loadTest = method.getAnnotation(LoadTest.class);
                TestSuite testSuite = TestSuiteUtils.createTestSuite(loadTest.concurrentUsers(), method.getMethod().getDeclaringClass(), method.getMethod().getName());
                TestResult testResult = TestRunner.run(testSuite);
                assertTrue(testResult.failureCount()==0);

            }
        };
    }

    private EachTestNotifier makeNotifier(FrameworkMethod method,
                                          RunNotifier notifier) {
        Description description = describeChild(method);
        return new EachTestNotifier(notifier, description);
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        return getTestClass().getAnnotatedMethods(LoadTest.class);
    }

    @Override
    protected void validateTestMethods(List<Throwable> errors) {
        validatePublicVoidNoArgMethods(LoadTest.class, false, errors);

    }

    @Override
    protected void validateConstructor(List<Throwable> errors) {

    }


}
