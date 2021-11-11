package org.failsafe.di.trans.steps.relp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.util.Utils;
import com.teragrep.rlp_01.RelpConnection;

import com.teragrep.rlp_01.RelpBatch;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeoutException;
import java.util.UUID;

import com.cloudbees.syslog.Facility;
import com.cloudbees.syslog.SDElement;
import com.cloudbees.syslog.Severity;
import com.cloudbees.syslog.SyslogMessage;

/**
 * @author kpoddar
 *
 */
public class RelpProducer extends BaseStep implements StepInterface {

	private static final Class<?> PKG = RelpProducer.class;

	private RelpProducerMeta meta;
	private RelpProducerData data;

	public RelpProducer(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta,
			Trans trans) {
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	@Override
	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
		meta = (RelpProducerMeta) smi;
		data = (RelpProducerData) sdi;
		return super.init(smi, sdi);
	}

	@Override
	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException {

		Object[] row = getRow();
		if (row == null) {
			if (data.batchCount > 0) {
				try {
					sendData();
				} catch (RelpConnectionException e) {
					throw new KettleException(e);
				}
			}

			setOutputDone();
			return false;
		}

		RowMetaInterface inputRowMeta = getInputRowMeta();

		if (first) {
			first = false;
			data.outputRowMeta = getInputRowMeta().clone();
			meta.getFields(data.outputRowMeta, getStepname(), null, null, this);

			int numErrors = 0;

			// Initialize data object
			data.relpServer = environmentSubstitute(meta.getRelpServer());
			data.relpPort = Integer.valueOf(environmentSubstitute(meta.getRelpPort()));
			data.readTimeout = Integer.valueOf(environmentSubstitute(meta.getReadTimeout()));
			data.writeTimeout = Integer.valueOf(environmentSubstitute(meta.getWriteTimeout()));
			data.connectionTimeout = Integer.valueOf(environmentSubstitute(meta.getConnectionTimeout()));
			data.reconnectInterval = Integer.valueOf(environmentSubstitute(meta.getReconnectInterval()));
			data.batchSize = Integer.valueOf(environmentSubstitute(meta.getBatchSize()));
			data.connectionMaxRetries = Integer.valueOf(environmentSubstitute(meta.getConnectionMaxRetries()));

			// messageField
			String messageField = environmentSubstitute(meta.getMessageField());

			if (Utils.isEmpty(messageField)) {
				logError(BaseMessages.getString(PKG, "RelpProducerStep.Log.MessageFieldNameIsNull")); //$NON-NLS-1$
				numErrors++;
			}
			data.messageFieldNr = inputRowMeta.indexOfValue(messageField);
			if (data.messageFieldNr < 0) {
				logError(BaseMessages.getString(PKG, "RelpProducerStep.Log.CouldntFindField", messageField)); //$NON-NLS-1$
				numErrors++;
			}
			if (!inputRowMeta.getValueMeta(data.messageFieldNr).isString()) {
				logError(BaseMessages.getString(PKG, "RelpProducerStep.Log.FieldNotValid", messageField)); //$NON-NLS-1$
				numErrors++;
			}
			data.messageIsString = inputRowMeta.getValueMeta(data.messageFieldNr).isString();
			data.messageFieldMeta = inputRowMeta.getValueMeta(data.messageFieldNr);

			// messageTimeStampField
			String messageTimeStampField = environmentSubstitute(meta.getMessageTimeStampField());

			if (Utils.isEmpty(messageTimeStampField)) {
				logError(BaseMessages.getString(PKG, "RelpProducerStep.Log.MessageTimeStampFieldNameIsNull")); //$NON-NLS-1$
				numErrors++;
			}
			data.messageTimeStampFieldNr = inputRowMeta.indexOfValue(messageTimeStampField);
			if (data.messageTimeStampFieldNr < 0) {
				logError(BaseMessages.getString(PKG, "RelpProducerStep.Log.CouldntFindField", messageTimeStampField)); //$NON-NLS-1$
				numErrors++;
			}
			if (!inputRowMeta.getValueMeta(data.messageTimeStampFieldNr).isDate()
					&& !inputRowMeta.getValueMeta(data.messageTimeStampFieldNr).isString()) {
				logError(BaseMessages.getString(PKG, "RelpProducerStep.Log.FieldNotValid", messageTimeStampField)); //$NON-NLS-1$
				numErrors++;
			}
			data.messageTimeStampIsString = inputRowMeta.getValueMeta(data.messageTimeStampFieldNr).isString();
			data.messageTimeStampFieldMeta = inputRowMeta.getValueMeta(data.messageTimeStampFieldNr);

			// messageSeverityField
			String messageSeverityField = environmentSubstitute(meta.getMessageSeverityField());

			if (Utils.isEmpty(messageSeverityField)) {
				logError(BaseMessages.getString(PKG, "RelpProducerStep.Log.MessageSeverityFieldNameIsNull")); //$NON-NLS-1$
				numErrors++;
			}
			data.messageSeverityFieldNr = inputRowMeta.indexOfValue(messageSeverityField);
			if (data.messageSeverityFieldNr < 0) {
				logError(BaseMessages.getString(PKG, "RelpProducerStep.Log.CouldntFindField", messageSeverityField)); //$NON-NLS-1$
				numErrors++;
			}
			if (!inputRowMeta.getValueMeta(data.messageSeverityFieldNr).isString()) {
				logError(BaseMessages.getString(PKG, "RelpProducerStep.Log.FieldNotValid", messageSeverityField)); //$NON-NLS-1$
				numErrors++;
			}
			data.messageSeverityIsString = inputRowMeta.getValueMeta(data.messageSeverityFieldNr).isString();
			data.messageSeverityFieldMeta = inputRowMeta.getValueMeta(data.messageSeverityFieldNr);

			// messageAppNameField
			String messageAppNameField = environmentSubstitute(meta.getMessageAppNameField());

			if (Utils.isEmpty(messageAppNameField)) {
				logError(BaseMessages.getString(PKG, "RelpProducerStep.Log.MessageAppNameFieldNameIsNull")); //$NON-NLS-1$
				numErrors++;
			}
			data.messageAppNameFieldNr = inputRowMeta.indexOfValue(messageAppNameField);
			if (data.messageAppNameFieldNr < 0) {
				logError(BaseMessages.getString(PKG, "RelpProducerStep.Log.CouldntFindField", messageAppNameField)); //$NON-NLS-1$
				numErrors++;
			}
			if (!inputRowMeta.getValueMeta(data.messageAppNameFieldNr).isString()) {
				logError(BaseMessages.getString(PKG, "RelpProducerStep.Log.FieldNotValid", messageAppNameField)); //$NON-NLS-1$
				numErrors++;
			}
			data.messageAppNameIsString = inputRowMeta.getValueMeta(data.messageAppNameFieldNr).isString();
			data.messageAppNameFieldMeta = inputRowMeta.getValueMeta(data.messageAppNameFieldNr);

			// messageHostNameField
			String messageHostNameField = environmentSubstitute(meta.getMessageHostNameField());

			if (Utils.isEmpty(messageHostNameField)) {
				logError(BaseMessages.getString(PKG, "RelpProducerStep.Log.MessageHostNameFieldNameIsNull")); //$NON-NLS-1$
				numErrors++;
			}
			data.messageHostNameFieldNr = inputRowMeta.indexOfValue(messageHostNameField);
			if (data.messageHostNameFieldNr < 0) {
				logError(BaseMessages.getString(PKG, "RelpProducerStep.Log.CouldntFindField", messageHostNameField)); //$NON-NLS-1$
				numErrors++;
			}
			if (!inputRowMeta.getValueMeta(data.messageHostNameFieldNr).isString()) {
				logError(BaseMessages.getString(PKG, "RelpProducerStep.Log.FieldNotValid", messageHostNameField)); //$NON-NLS-1$
				numErrors++;
			}
			data.messageHostNameIsString = inputRowMeta.getValueMeta(data.messageHostNameFieldNr).isString();
			data.messageHostNameFieldMeta = inputRowMeta.getValueMeta(data.messageHostNameFieldNr);

			// messageFacilityField
			String messageFacilityField = environmentSubstitute(meta.getMessageFacilityField());

			if (Utils.isEmpty(messageFacilityField)) {
				logError(BaseMessages.getString(PKG, "RelpProducerStep.Log.MessageFacilityFieldNameIsNull")); //$NON-NLS-1$
				numErrors++;
			}
			data.messageFacilityFieldNr = inputRowMeta.indexOfValue(messageFacilityField);
			if (data.messageFacilityFieldNr < 0) {
				logError(BaseMessages.getString(PKG, "RelpProducerStep.Log.CouldntFindField", messageFacilityField)); //$NON-NLS-1$
				numErrors++;
			}
			if (!inputRowMeta.getValueMeta(data.messageFacilityFieldNr).isString()) {
				logError(BaseMessages.getString(PKG, "RelpProducerStep.Log.FieldNotValid", messageFacilityField)); //$NON-NLS-1$
				numErrors++;
			}
			data.messageIsString = inputRowMeta.getValueMeta(data.messageFacilityFieldNr).isString();
			data.messageFacilityFieldMeta = inputRowMeta.getValueMeta(data.messageFacilityFieldNr);

			// Hostname where RELPSender is running
			try {
				data.hostName = InetAddress.getLocalHost().getHostName();
			} catch (UnknownHostException e) {
				logError(BaseMessages.getString(PKG, "RelpProducerStep.ErrorInStepRunning"), e);
				// e.printStackTrace();
			}

			// Initialize the source value
			data.source = meta.DEFAULT_SOURCE;

			if (numErrors > 0) {
				setErrors(numErrors);
				stopAll();
				return false;
			}

			// Setup connection to RELP here
			// Initialize RelpConnection:
			if (data.relpConnection == null) {
				logBasic(BaseMessages.getString(PKG, "RelpProducerStep.CreateRelpConnection.Message", data.relpServer,
						data.relpPort));
				data.relpConnection = new RelpConnection();

				data.relpConnection.setConnectionTimeout(data.connectionTimeout);
				data.relpConnection.setReadTimeout(data.readTimeout);
				data.relpConnection.setWriteTimeout(data.writeTimeout);
				// openConnection(data.relpConnection, data.relpServer, data.relpPort,
				// data.reconnectInterval); // connection
				try {
					openConnection(data.relpConnection, data.relpServer, data.relpPort, data.reconnectInterval); // connection
				} catch (RelpConnectionException exc) {
					logError(BaseMessages.getString(PKG, "RelpProducerStep.ErrorInStepRunning"), exc);
					throw new KettleException(exc);
				}

			}
			data.relpBatch = new RelpBatch();

		} // End of first row specific processing

		// Process row
		try {
			// Get message components

			String message = data.messageFieldMeta.getString(row[data.messageFieldNr]);
			Date messageTimeStamp = data.messageTimeStampFieldMeta.getDate(row[data.messageTimeStampFieldNr]);
			String messageSeverity = data.messageSeverityFieldMeta.getString(row[data.messageSeverityFieldNr]);
			String messageAppName = data.messageAppNameFieldMeta.getString(row[data.messageAppNameFieldNr]);
			String messageHostName = data.messageHostNameFieldMeta.getString(row[data.messageHostNameFieldNr]);
			String messageFacility = data.messageFacilityFieldMeta.getString(row[data.messageFacilityFieldNr]);

			// AppName max 48 characters
			if ((messageAppName != null) && (messageAppName.length() > RelpProducerMeta.MAX_LEN_APP_NAME)) {

				throw new KettleException(BaseMessages.getString(PKG, "RelpProducerStep.ValueExceedsMaxLength",
						"App Name", RelpProducerMeta.MAX_LEN_APP_NAME, messageAppName.length()));
			}
			// Host Name max 255 characters
			if ((messageHostName != null) && (messageHostName.length() > RelpProducerMeta.MAX_LEN_HOST_NAME)) {

				throw new KettleException(BaseMessages.getString(PKG, "RelpProducerStep.ValueExceedsMaxLength",
						"Host Name", RelpProducerMeta.MAX_LEN_APP_NAME, messageHostName.length()));
			}

			// Create syslog message
			SyslogMessage syslog = new SyslogMessage().withTimestamp(messageTimeStamp)
					.withSeverity(Severity.fromLabel(messageSeverity)).withAppName(messageAppName)
					.withHostname(messageHostName).withFacility(Facility.fromLabel(messageFacility)).withMsg(message);

			SDElement event_id_48577 = new SDElement("event_id@48577").addSDParam("hostname", data.hostName)
					.addSDParam("uuid", UUID.randomUUID().toString()).addSDParam("source", data.source)
					.addSDParam("unixtime", Long.toString(System.currentTimeMillis()));
			SDElement origin_48577 = new SDElement("origin@48577").addSDParam("hostname", data.hostName);
			syslog = syslog.withSDElement(event_id_48577).withSDElement(origin_48577);
			// Step logging
			if (isRowLevel()) {
				logRowlevel(data.messageFieldMeta.getString(row[data.messageFieldNr]));
			}
			// Add row to the batch
			data.relpBatch.insert(syslog.toRfc5424SyslogMessage().getBytes(StandardCharsets.UTF_8));
			data.batchCount++;
			// If batch has enough items, send it. Without batching, the speed is about 10
			// rows per second, with 1024 batch size, it is about 1200 per second
			if (data.batchCount >= data.batchSize) {
				sendData();
			}

			// If we are here assume all rows were sent succesfully. If the batch send
			// supports error reporting at
			// individual row level, then we can improve the error reporting by sending
			// error rows to putError
			// Update the Step metrics
			incrementLinesOutput();

			// Pass the input row through
			putRow(data.outputRowMeta, row);

		} catch (RelpConnectionException exc) {
			logError(BaseMessages.getString(PKG, "RelpProducerStep.ErrorInStepRunning"), exc);
			throw new KettleException(exc);
		} catch (KettleException e) {
			if (!getStepMeta().isDoingErrorHandling()) {
				logError(BaseMessages.getString(PKG, "RelpProducerStep.ErrorInStepRunning"), e);
				setErrors(1);
				stopAll();
				setOutputDone();
				return false;
			}
			// This reports the issues with row data such as a field length exceeding
			putError(getInputRowMeta(), row, 1, e.toString(), null, getStepname());
		}

		return true;
	}

	private void sendData() throws RelpConnectionException {
		boolean allSent = false;
		while (!allSent) {
			try {
				data.relpConnection.commit(data.relpBatch);
			} catch (IllegalStateException | IOException | java.util.concurrent.TimeoutException e) {
				// System.out.println("RelpAppender.flush.commit> exception:");
				logError(BaseMessages.getString(PKG, "RelpProducerStep.ErrorInStepRunning"), e);
				data.relpConnection.tearDown();
				// this.connected = false;
				try {
					openConnection(data.relpConnection, data.relpServer, data.relpPort, data.reconnectInterval); // connection
				} catch (RelpConnectionException exc) {
					logError(BaseMessages.getString(PKG, "RelpProducerStep.ErrorInStepRunning"), exc);
					throw exc;
				}

			}
			// Check if everything has been sent, retry and reconnect if not.
			if (!data.relpBatch.verifyTransactionAll()) {
				data.relpBatch.retryAllFailed();
			} else {
				allSent = true;
			}
		}
		// Create new batch for next set of messages
		data.relpBatch = new RelpBatch();
		data.batchCount = 0;
	}

	private void openConnection(RelpConnection relpConnection, String serverHostname, int serverPort,
			int reconnectInterval) throws RelpConnectionException {
// connect helper method
		boolean connected = false;
		int retries = 0;
		while (!connected) {
			try {
				retries++;
				if (retries > data.connectionMaxRetries) {

					throw new RelpConnectionException(BaseMessages.getString(PKG,
							"RelpProducerStep.ConnectionRetriesExceeded", data.connectionMaxRetries));
				}
				connected = relpConnection.connect(serverHostname, serverPort); // connect
			} catch (IOException | TimeoutException e) { // error happened during the connect
				logError(BaseMessages.getString(PKG, "RelpProducerStep.ErrorInStepRunning"), e);
				relpConnection.tearDown(); // retry with clean connection
			}

			if (!connected) {
// reconnect after an interval
				try {
					logBasic(BaseMessages.getString(PKG, "RelpProducerStep.ReconnectWait", reconnectInterval));
					Thread.sleep(reconnectInterval); // reconnect interval
				} catch (InterruptedException e) {
					if (isDebug()) {
						logError(BaseMessages.getString(PKG, "RelpProducerStep.ErrorInStepRunning"), e);
					}
				}
			}

		}
	}

}
