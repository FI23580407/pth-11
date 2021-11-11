package org.failsafe.relp.producer.test;

import org.junit.Ignore;
import org.junit.Test;
import com.teragrep.rlp_01.RelpBatch;
import com.teragrep.rlp_01.RelpConnection;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeoutException;

import com.cloudbees.syslog.Facility;
import com.cloudbees.syslog.Severity;
import com.cloudbees.syslog.SyslogMessage;

public class QuickTest {

	@Test
	@Ignore
	public void test() {
		
		System.out.println("Hello World");
        final String serverHostname = "127.0.0.1";
        final int serverPort = 601;
        RelpConnection relpConnection = new RelpConnection();;

        openConnection(relpConnection, serverHostname, serverPort); // connection helper method

        RelpBatch relpBatch = new RelpBatch(); // create new relpBatch

        // Craft syslog message
        SyslogMessage syslog = new SyslogMessage()
                .withTimestamp(new Date().getTime())
                .withSeverity(Severity.WARNING)
                .withAppName("appName")
                .withHostname("hostName")
                .withFacility(Facility.USER)
                .withMsg("Hello RELP World!");


        relpBatch.insert(syslog.toRfc5424SyslogMessage().getBytes(StandardCharsets.UTF_8)); // insert one message

        boolean notSent = true;
        while (notSent) { // retry until sent

            try {
                relpConnection.commit(relpBatch); // send batch
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
            }

            if (!relpBatch.verifyTransactionAll()) { // failed batch
                relpBatch.retryAllFailed(); // re-queue failed events
                relpConnection.tearDown(); // teardown connection
                openConnection(relpConnection, serverHostname, serverPort); // reconnect
            }
            else { // successful batch
                notSent = false;
            }
        }
    }

    private static void openConnection(RelpConnection relpConnection,
                                       String serverHostname,
                                       int serverPort) {
        // connect helper method
        boolean connected = false;
        while (!connected) {
            try {
                connected = relpConnection.connect(serverHostname, serverPort);  // connect
            } catch (IOException|TimeoutException e) { // error happened during the connect
                e.printStackTrace();
                relpConnection.tearDown(); // retry with clean connection
            }

            if (!connected) {
                // reconnect after an interval
                try {
                    Thread.sleep(500); // reconnect interval
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
	
}
