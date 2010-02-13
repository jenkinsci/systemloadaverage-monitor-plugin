package hudson.plugins.systemloadaverage_monitor;


import hudson.model.Hudson;
import hudson.plugins.systemloadaverage_monitor.SystemLoadAverageMonitor.MonitorTask;
import hudson.security.HudsonPrivateSecurityRealm;
import hudson.security.LegacyAuthorizationStrategy;

import java.io.IOException;
import java.util.List;

import org.jvnet.hudson.test.HudsonTestCase;
import org.xml.sax.SAXException;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;


/**
 * @author Stefan Brausch
 */
public class PluginTest extends HudsonTestCase {

    private class ComputerPage {

        private final HtmlPage computerPage;

        public ComputerPage() throws IOException, SAXException {
            this.computerPage = webClient.goTo("computer");
        }

        public int checkComputerTableColumn() {
            final HtmlElement computerTable = computerPage.getHtmlElementById("computers");
            List<? extends Object> tableHeaders = computerTable.getByXPath("//th/a[contains(text(),'System Load Average')]");
            return tableHeaders.size();
        }
    }



    private WebClient webClient;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        webClient = createWebClient();
    }

    public void testOfExistingPlugin() throws IOException, SAXException, RuntimeException, InterruptedException {
        final ComputerPage computerPage = new ComputerPage();
        assertTrue("Table Column System Load Average doesn't exist", (computerPage.checkComputerTableColumn()>0));
    }

    public void testOfNonExistingPluginWithSecurity() throws IOException, SAXException, RuntimeException, InterruptedException {
        hudson.setSecurityRealm(new HudsonPrivateSecurityRealm(false));
        hudson.setAuthorizationStrategy(new LegacyAuthorizationStrategy());
        final ComputerPage computerPage = new ComputerPage();
        assertTrue("Table Column System Load Average should not be shown", (computerPage.checkComputerTableColumn()==0));
    }

    public void testOfGetSystemLoadAverage() throws IOException, SAXException, RuntimeException, InterruptedException {
        final String loadAverageAsString = (Hudson.getInstance().getComputers())[0].getChannel().call(new MonitorTask());
        final float load = Float.parseFloat(loadAverageAsString);
        assertTrue("Get Load System Average failed. Return Value is " + load, load >= -1);
    }
}

