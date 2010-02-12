package hudson.plugins.systemloadaverage_monitor;


import hudson.model.Hudson;
import hudson.plugins.systemloadaverage_monitor.SystemLoadAverageMonitor.MonitorTask;

import java.io.IOException;
import java.util.List;

import org.jvnet.hudson.test.HudsonTestCase;
import org.jvnet.hudson.test.recipes.LocalData;
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
    @LocalData
    public void testOfExistingPlugin() throws IOException, SAXException, RuntimeException, InterruptedException {
        final ComputerPage computerPage = new ComputerPage();
        assertTrue("Get Load System Average failed",(Hudson.getInstance().getComputers())[0].getChannel().call(new MonitorTask()).equalsIgnoreCase("-1"));
        assertTrue("Table Column System Load Average doesn't exist", (computerPage.checkComputerTableColumn()>0));

    }
    @LocalData
    public void testOfGetSystemLoadAverage() throws IOException, SAXException, RuntimeException, InterruptedException {

        assertTrue("Get Load System Average failed",(Hudson.getInstance().getComputers())[0].getChannel().call(new MonitorTask()).equalsIgnoreCase("-1"));

    }
}

