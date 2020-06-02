import com.spring.factoryLoader.Spring1FactoryInitImpl;
import com.spring.factoryLoader.Spring2FactoryInitImpl;
import com.spring.factoryLoader.SpringFactoryLoader;
import com.spring.factoryLoader.ISpringFactoryInitializer
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
