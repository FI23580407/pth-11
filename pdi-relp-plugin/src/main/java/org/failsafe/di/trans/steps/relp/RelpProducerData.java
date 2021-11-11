package org.failsafe.di.trans.steps.relp;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

import com.teragrep.rlp_01.RelpBatch;
import com.teragrep.rlp_01.RelpConnection;


public class RelpProducerData extends BaseStepData implements StepDataInterface {

	RowMetaInterface outputRowMeta;

	// RelpConnection object
	RelpConnection relpConnection;
	RelpBatch relpBatch;

	// Connection Timeouts
	int readTimeout;
	int writeTimeout;
	int connectionTimeout;
	int reconnectInterval;
	int connectionMaxRetries;

	String relpServer;
	int relpPort;
	int batchSize;
	int batchCount = 0; //Keeps track of current number of items in batch
	String hostName;
	
	String source; // Value of source field

	// Message field
	int messageFieldNr;
	ValueMetaInterface messageFieldMeta;
	boolean messageIsString;

	// messageTimeStamp field
	int messageTimeStampFieldNr;
	ValueMetaInterface messageTimeStampFieldMeta;
	boolean messageTimeStampIsString;

	// messageSeverity field
	int messageSeverityFieldNr;
	ValueMetaInterface messageSeverityFieldMeta;
	boolean messageSeverityIsString;

	// messageAppName field
	int messageAppNameFieldNr;
	ValueMetaInterface messageAppNameFieldMeta;
	boolean messageAppNameIsString;

	// messageHostName field
	int messageHostNameFieldNr;
	ValueMetaInterface messageHostNameFieldMeta;
	boolean messageHostNameIsString;

	// messageFacility field
	int messageFacilityFieldNr;
	ValueMetaInterface messageFacilityFieldMeta;
	boolean messageFacilityIsString;

}
