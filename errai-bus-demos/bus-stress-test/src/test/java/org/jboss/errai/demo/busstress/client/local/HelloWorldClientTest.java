package org.jboss.errai.demo.busstress.client.local;

import org.jboss.errai.bus.client.tests.AbstractErraiTest;
import org.jboss.errai.ioc.client.api.Bootstrapper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;

public class HelloWorldClientTest extends AbstractErraiTest {

  @Override
  public String getModuleName() {
    return "org.jboss.errai.demo.busstress.App";
  }

  @Override
  protected void gwtSetUp() throws Exception {
    super.gwtSetUp();

    // We need to bootstrap the IoC container manually because GWTTestCase
    // doesn't call onModuleLoad() for us.
    Bootstrapper bootstrapper = GWT.create(Bootstrapper.class);
    bootstrapper.bootstrapContainer();
  }

  public void testSendMessage() throws Exception {
    ErraiIocTestHelper.afterBusInitialized(new Runnable() {
      @Override
      public void run() {
        final StressTestClient client = ErraiIocTestHelper.instance.client;
        assertNotNull(client);

        // send a message using the bus (it is now initialized)
        client.messageInterval.setValue(10);
        client.messageSize.setValue(100);
        client.onStartButtonClick(null);

        // wait a few seconds, then check that the server response caused a DOM update
        new Timer() {
          @Override
          public void run() {
            client.stopIfRunning();
            assertTrue("Expected at least one message received; got " + client.messageRecvCount,
                Integer.parseInt(client.messageRecvCount.getText()) > 0);
            finishTest();
          }
        }.schedule(2000);

      }
    });
    delayTestFinish(20000);
  }
}
