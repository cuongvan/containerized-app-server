package all;

import end2end.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    BuildAppTest.class,
    CreateAppTests.class,
})
public class EndToEndTests {

}