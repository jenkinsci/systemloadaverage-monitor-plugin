package hudson.plugins.systemloadaverage_monitor;

import hudson.Extension;
import hudson.model.Computer;
import hudson.model.Hudson;
import hudson.node_monitors.AbstractNodeMonitorDescriptor;
import hudson.node_monitors.NodeMonitor;
import hudson.remoting.Callable;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;


/**
 * Monitors the system load average to this slave (unix slaves only).
 * @author Stefan Brausch
 */

/**
 * This class provides an additional SystemLoadAverage column in the node page.
 * It may only be seen by administrators.
 */
public class SystemLoadAverageMonitor extends NodeMonitor {

    /** {@inheritDoc} */
    @Override
    public final String getColumnCaption() {
        // Hide this column from non-admins
        return Hudson.getInstance().hasPermission(Hudson.ADMINISTER) ? super
                .getColumnCaption() : null;
    }

    /**
     * Descriptor for the Monitor.
     */
    @Extension
    public static final AbstractNodeMonitorDescriptor<String> DESCRIPTOR = new AbstractNodeMonitorDescriptor<String>() {

        /** {@inheritDoc} */
        protected String monitor(Computer c) throws IOException,
                InterruptedException {
            return c.getChannel().call(new MonitorTask());
        }

        /** {@inheritDoc} */
        public SystemLoadAverageMonitor newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            return new SystemLoadAverageMonitor();
        }

        /** {@inheritDoc} */
        public String getDisplayName() {
            return "System Load Average";
        }
    };

    /**
     * Task which returns the SystemLoadAverage.
     */
    static final class MonitorTask implements Callable<String, RuntimeException> {
        private static final long serialVersionUID = 1L;

        /**
        * Detect the System Load Average.
        */
        public String call() {
            final OperatingSystemMXBean opsysMXbean = ManagementFactory
                    .getOperatingSystemMXBean();
            return String.format("%04.1f", opsysMXbean.getSystemLoadAverage());
        }
    }
}
