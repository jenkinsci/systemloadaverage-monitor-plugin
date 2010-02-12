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

public class SystemLoadAverageMonitor extends NodeMonitor {

    @Override
    public final String getColumnCaption() {
        // Hide this column from non-admins
        return Hudson.getInstance().hasPermission(Hudson.ADMINISTER) ? super
                .getColumnCaption() : null;
    }

    @Extension
    public static final AbstractNodeMonitorDescriptor<String> DESCRIPTOR = new AbstractNodeMonitorDescriptor<String>() {
        protected String monitor(Computer c) throws IOException,
                InterruptedException {
            return c.getChannel().call(new MonitorTask());
        }

        public SystemLoadAverageMonitor newInstance(StaplerRequest req,
                JSONObject formData) throws FormException {
            return new SystemLoadAverageMonitor();
        }

        public String getDisplayName() {
            return "System Load Average";
        }
    };

    final static class MonitorTask implements
            Callable<String, RuntimeException>{
        private static final long serialVersionUID = 1L;

        /**
        * Detect the System Load Average.
        */
        public String call() {
            final OperatingSystemMXBean opsysMXbean = ManagementFactory
                    .getOperatingSystemMXBean();
            return new Long((long) opsysMXbean.getSystemLoadAverage())
                    .toString();
        }
    }
}
