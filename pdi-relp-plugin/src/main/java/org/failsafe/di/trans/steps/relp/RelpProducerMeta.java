package org.failsafe.di.trans.steps.relp;

import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.injection.Injection;
import org.pentaho.di.core.injection.InjectionSupported;
import org.pentaho.di.core.util.serialization.BaseSerializingMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

@Step(id = "RelpProducer", name = "RelpProducer.Name", categoryDescription = "i18n:org.pentaho.di.trans.step:BaseStep.Category.Streaming", description = "RelpProducer.Description", i18nPackageName = "org.failsafe.di.trans.steps.relp", image = "RelpProducer.svg")
@InjectionSupported(localizationPrefix = "RelpProducerMeta.INJECTION.")
public class RelpProducerMeta extends BaseSerializingMeta implements StepMetaInterface {

	public static final Class<?> PKG = RelpProducerMeta.class;
	public static final int DEFAULT_READ_TIMEOUT = 15000;
	public static final int DEFAULT_WRITE_TIMEOUT = 2000;
	public static final int DEFAULT_CONNECTION_TIMEOUT = 5000;
	public static final int DEFAULT_RECONNECT_INTERVAL = 500;
	public static final int DEFAULT_BATCH_SIZE = 1024;
	public static final int DEFAULT_CONNECTION_MAX_RETRIES = 10;
	public static final int MAX_LEN_APP_NAME = 48;
	public static final int MAX_LEN_HOST_NAME = 255;
	public static final String DEFAULT_SOURCE = "source";


	// ///////////
	// FIELDS //
	// /////////

	@Injection(name = "RELP_SERVER")
	private String relpServer = null;

	@Injection(name = "RELP_PORT")
	private String relpPort = null;

	@Injection(name = "MESSAGE_TIME_STAMP_FIELD")
	private String messageTimeStampField = null;

	@Injection(name = "MESSAGE_SEVERITY_FIELD")
	private String messageSeverityField = null;

	@Injection(name = "MESSAGE_APP_NAME_FIELD")
	private String messageAppNameField = null;

	@Injection(name = "MESSAGE_HOST_NAME_FIELD")
	private String messageHostNameField = null;

	@Injection(name = "MESSAGE_FACILITY_FIELD")
	private String messageFacilityField = null;

	@Injection(name = "MESSAGE_FIELD")
	private String messageField = null;

	@Injection(name = "READ_TIMEOUT")
	private String readTimeout = null;

	@Injection(name = "WRITE_TIMEOUT")
	private String writeTimeout = null;

	@Injection(name = "CONNECTION_TIMEOUT")
	private String connectionTimeout = null;

	@Injection(name = "RECONNECT_INTERVAL")
	private String reconnectInterval = null;

	@Injection(name = "BATCH_SIZE")
	private String batchSize = null;

	@Injection(name = "CONNECTION_MAX_RETRIES")
	private String connectionMaxRetries = null;

	@Override
	public void setDefault() {
		readTimeout = "" + DEFAULT_READ_TIMEOUT;
		writeTimeout = "" + DEFAULT_WRITE_TIMEOUT;
		connectionTimeout = "" + DEFAULT_CONNECTION_TIMEOUT;
		reconnectInterval = "" + DEFAULT_RECONNECT_INTERVAL;
		batchSize = "" + DEFAULT_BATCH_SIZE;
		connectionMaxRetries = "" + DEFAULT_CONNECTION_MAX_RETRIES;

	}


	@Override
	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
			TransMeta transMeta, Trans trans) {
		return new RelpProducer(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	@Override
	public StepDataInterface getStepData() {
		return new RelpProducerData();
	}

	public String getRelpServer() {
		return relpServer;
	}

	public void setRelpServer(String relpServer) {
		this.relpServer = relpServer;
	}

	public String getRelpPort() {
		return relpPort;
	}

	public void setRelpPort(String relpPort) {
		this.relpPort = relpPort;
	}

	public String getMessageTimeStampField() {
		return messageTimeStampField;
	}

	public void setMessageTimeStampField(String messageTimeStampField) {
		this.messageTimeStampField = messageTimeStampField;
	}

	public String getMessageSeverityField() {
		return messageSeverityField;
	}

	public void setMessageSeverityField(String messageSeverityField) {
		this.messageSeverityField = messageSeverityField;
	}

	public String getMessageAppNameField() {
		return messageAppNameField;
	}

	public void setMessageAppNameField(String messageAppNameField) {
		this.messageAppNameField = messageAppNameField;
	}

	public String getMessageHostNameField() {
		return messageHostNameField;
	}

	public void setMessageHostNameField(String messageHostNameField) {
		this.messageHostNameField = messageHostNameField;
	}

	public String getMessageFacilityField() {
		return messageFacilityField;
	}

	public void setMessageFacilityField(String messageFacilityField) {
		this.messageFacilityField = messageFacilityField;
	}

	public String getMessageField() {
		return messageField;
	}

	public void setMessageField(String messageField) {
		this.messageField = messageField;
	}

	public String getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(String readTimeout) {
		this.readTimeout = readTimeout;
	}

	public String getWriteTimeout() {
		return writeTimeout;
	}

	public void setWriteTimeout(String writeTimeout) {
		this.writeTimeout = writeTimeout;
	}

	public String getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(String connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public String getReconnectInterval() {
		return reconnectInterval;
	}

	public void setReconnectInterval(String reconnectInterval) {
		this.reconnectInterval = reconnectInterval;
	}

	public String getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(String batchSize) {
		this.batchSize = batchSize;
	}
	
	public String getConnectionMaxRetries() {
		return connectionMaxRetries;
	}

	public void setConnectionMaxRetries(String connectionMaxRetries) {
		this.connectionMaxRetries = connectionMaxRetries;
	}

	@Override
	public boolean supportsErrorHandling() {
		return true;
	}
}
