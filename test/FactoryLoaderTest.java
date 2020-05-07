import com.spring.appcontext.SpringFactoryLoader;
import com.spring.appcontext.init.ISpringFactoryInitializer;
import com.spring.appcontext.init.Spring1FactoryInitImpl;
import com.spring.appcontext.init.Spring2FactoryInitImpl;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class FactoryLoaderTest {

    @Test
    public void loadFactory() {
        List<ISpringFactoryInitializer> factories = SpringFactoryLoader.loadFactory(ISpringFactoryInitializer.class, null);
        Assert.assertEquals(2, factories.size());
        Assert.assertTrue(factories.get(0) instanceof Spring1FactoryInitImpl);
        Assert.assertTrue(factories.get(1) instanceof Spring2FactoryInitImpl);
    }
}
