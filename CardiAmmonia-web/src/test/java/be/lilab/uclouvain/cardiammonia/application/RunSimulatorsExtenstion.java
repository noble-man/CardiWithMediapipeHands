package be.lilab.uclouvain.cardiammonia.application;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

import java.awt.Robot;

public class RunSimulatorsExtenstion  implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {

    private static boolean started = false;

    @Override
    public void beforeAll(ExtensionContext context) {
        if (!started) {
            started = true;
            // Your "before all tests" startup logic goes here
            // The following line registers a callback hook when the root test context is shut down
			new Thread(){ public void run(){ CycloneSimulator.main(null); }}.start();
			new Thread(){ public void run(){ DispensingSimulator.main(null); }}.start();
			new Thread(){ public void run(){ QCSimulator.main(null); }}.start();
            
            context.getRoot().getStore(GLOBAL).put("any unique name", this);
            
        }
    }

    @Override
    public void close() {
        // Your "after all tests" logic goes here
    }
    
}