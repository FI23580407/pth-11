package org.failsafe.di.ui.trans.steps.relp;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.failsafe.di.trans.steps.relp.RelpProducerMeta;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.ui.core.ConstUI;
import org.pentaho.di.ui.core.FormDataBuilder;
import org.pentaho.di.ui.core.gui.GUIResource;
import org.pentaho.di.ui.trans.step.BaseStepDialog;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.layout.FormAttachment;
import org.pentaho.di.ui.core.widget.ComboVar;
import org.pentaho.di.ui.core.widget.TextVar;
import static org.pentaho.di.ui.core.WidgetUtils.formDataBelow;
import static org.pentaho.di.ui.core.WidgetUtils.createFieldDropDown;

public class RelpProducerDialog extends BaseStepDialog implements StepDialogInterface {

	private static final Class<?> PKG = RelpProducerMeta.PKG;

	private RelpProducerMeta meta;

	private ModifyListener lsMod;

	private static final int MEDIUM_WIDTH_CONTROL = 250;
	private static final int margin = Const.MARGIN;
	private static final int INPUT_WIDTH = 350;

	private CTabFolder wTabFolder;

	private TextVar wRelpServer;
	private TextVar wRelpPort;
	private TextVar wReadTimeout;
	private TextVar wWriteTimeout;
	private TextVar wConnectionTimeout;
	private TextVar wReconnectInterval;
	private TextVar wConnectionMaxRetries;

	private TextVar wBatchSize;
	private ComboVar wMessageTimeStampField;
	private ComboVar wMessageSeverityField;
	private ComboVar wMessageAppNameField;
	private ComboVar wMessageHostNameField;
	private ComboVar wMessageFacilityField;
	private ComboVar wMessageField;

	public RelpProducerDialog(Shell parent, Object baseStepMeta, TransMeta transMeta, String stepname) {
		super(parent, (BaseStepMeta) baseStepMeta, transMeta, stepname);
		this.meta = (RelpProducerMeta) baseStepMeta;
	}

	public String open() {

		Shell parent = getParent();
		Display display = parent.getDisplay();

		shell = new Shell(parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.MIN);
		props.setLook(shell);
		setShellImage(shell, meta);

		lsMod = me -> meta.setChanged();

		changed = meta.hasChanged();

		shell.setLayout(formLayout());
		shell.setText(BaseMessages.getString(PKG, "RelpProducer.Name"));

		// Stepname line
		wlStepname = new Label(shell, SWT.RIGHT);
		wlStepname.setText(BaseMessages.getString(PKG, "System.Label.StepName"));
		props.setLook(wlStepname);
		wlStepname.setLayoutData(new FormDataBuilder().left(0, margin).top(0, margin).result());

		wStepname = new Text(shell, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		wStepname.setText(stepname);
		props.setLook(wStepname);
		wStepname.addModifyListener(lsMod);
		wStepname.setLayoutData(
				new FormDataBuilder().left(0, margin).top(wlStepname, margin).width(MEDIUM_WIDTH_CONTROL).result());

		Label wIcon = new Label(shell, SWT.RIGHT);
		props.setLook(wIcon);

		wIcon.setImage(getStepPluginImage(stepMeta, ConstUI.LARGE_ICON_SIZE, ConstUI.LARGE_ICON_SIZE));
		wIcon.setLayoutData(new FormDataBuilder().top(0, margin).right(100, -margin).result());

		Label wHoriz = new Label(shell, SWT.HORIZONTAL | SWT.SEPARATOR);
		props.setLook(wHoriz);

		FormData fdHoriz = new FormDataBuilder().top(wIcon, margin).left(0, margin).right(100, -margin).result();
		fdHoriz.height = 2;
		wHoriz.setLayoutData(fdHoriz);
//Start of tabbed display
		wTabFolder = new CTabFolder(shell, SWT.BORDER);
		props.setLook(wTabFolder, Props.WIDGET_STYLE_TAB);
		wTabFolder.setSimple(false);
		wTabFolder.setUnselectedCloseVisible(true);

		// ////////////
		// BUTTONS //
		// //////////
		wOK = new Button(shell, SWT.PUSH);
		wOK.setText(BaseMessages.getString(PKG, "System.Button.OK"));
		wCancel = new Button(shell, SWT.PUSH);
		wCancel.setText(BaseMessages.getString(PKG, "System.Button.Cancel"));

		wCancel.setLayoutData(new FormDataBuilder().bottom(100, -margin).right(100, -margin).result());
		wOK.setLayoutData(new FormDataBuilder().bottom(100, -margin).right(wCancel, -margin).result());

		// wTabFolder.setLayoutData(
		// new FormDataBuilder().left( 0, 0 ).top( wHoriz, margin * 2 ).right( 100, 0
		// ).bottom( wOK, -margin ).result() );

		// Bottom separator
		Label bottomSeparator = new Label(shell, SWT.HORIZONTAL | SWT.SEPARATOR);
		props.setLook(bottomSeparator);
		FormData fdBottomSeparator = new FormData();
		fdBottomSeparator.height = 2;
		fdBottomSeparator.left = new FormAttachment(0, 0);
		fdBottomSeparator.bottom = new FormAttachment(wCancel, -15);
		fdBottomSeparator.right = new FormAttachment(100, 0);
		bottomSeparator.setLayoutData(fdBottomSeparator);

		FormData fdTabFolder = new FormData();
		fdTabFolder.left = new FormAttachment(0, 0);
		fdTabFolder.top = new FormAttachment(wHoriz, 15);
		fdTabFolder.bottom = new FormAttachment(bottomSeparator, -15);
		fdTabFolder.right = new FormAttachment(100, 0);
		wTabFolder.setLayoutData(fdTabFolder);

		buildConnectionTab();
		buildMessageSetupTab();

		// //////////////////
		// Std Listeners //
		// ////////////////
		addStandardListeners();

		wTabFolder.setSelection(0);

		// Set the shell size, based upon previous time...
		setSize();

		populateFields();

		meta.setChanged(changed);

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return stepname;
	}

	private void addStandardListeners() {
		// Add listeners
		lsOK = e -> ok();
		lsCancel = e -> cancel();
		lsMod = me -> meta.setChanged();

		wOK.addListener(SWT.Selection, lsOK);
		wCancel.addListener(SWT.Selection, lsCancel);

		lsDef = new SelectionAdapter() {
			public void widgetDefaultSelected(SelectionEvent e) {
				ok();
			}
		};

		wStepname.addSelectionListener(lsDef);
		wRelpServer.addModifyListener(lsMod);
		wRelpPort.addModifyListener(lsMod);
		wMessageTimeStampField.addModifyListener(lsMod);
		wMessageSeverityField.addModifyListener(lsMod);
		wMessageAppNameField.addModifyListener(lsMod);
		wMessageHostNameField.addModifyListener(lsMod);
		wMessageFacilityField.addModifyListener(lsMod);
		wMessageField.addModifyListener(lsMod);
		wReadTimeout.addModifyListener(lsMod);
		wWriteTimeout.addModifyListener(lsMod);
		wConnectionTimeout.addModifyListener(lsMod);
		wReconnectInterval.addModifyListener(lsMod);
		wBatchSize.addModifyListener(lsMod);
		wConnectionMaxRetries.addModifyListener(lsMod);

		// window close
		shell.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				cancel();
			}
		});
	}

	public void populateFields() {
		// Set control values here (always wrap in Const.NVL, as the controls dont like
		// null values)
		// control.setText = Const.NVL( meta.getProperty(), "" );
		wRelpServer.setText(Const.NVL(meta.getRelpServer(), ""));
		wRelpPort.setText(Const.NVL(meta.getRelpPort(), ""));
		wMessageTimeStampField.setText(Const.NVL(meta.getMessageTimeStampField(), ""));
		wMessageSeverityField.setText(Const.NVL(meta.getMessageSeverityField(), ""));
		wMessageAppNameField.setText(Const.NVL(meta.getMessageAppNameField(), ""));
		wMessageHostNameField.setText(Const.NVL(meta.getMessageHostNameField(), ""));
		wMessageFacilityField.setText(Const.NVL(meta.getMessageFacilityField(), ""));
		wMessageField.setText(Const.NVL(meta.getMessageField(), ""));
		wReadTimeout.setText(Const.NVL(meta.getReadTimeout(), ""));
		wWriteTimeout.setText(Const.NVL(meta.getWriteTimeout(), ""));
		wConnectionTimeout.setText(Const.NVL(meta.getConnectionTimeout(), ""));
		wReconnectInterval.setText(Const.NVL(meta.getReconnectInterval(), ""));
		wBatchSize.setText(Const.NVL(meta.getBatchSize(), ""));
		wConnectionMaxRetries.setText(Const.NVL(meta.getConnectionMaxRetries(), ""));

	}

	public void applyToMeta(RelpProducerMeta meta) {
		// Assign any changes to a RelpProducerMeta here from the control values
		// meta.setProperty( control.getText() )
		meta.setRelpServer(wRelpServer.getText());
		meta.setRelpPort(wRelpPort.getText());
		meta.setMessageTimeStampField(wMessageTimeStampField.getText());
		meta.setMessageSeverityField(wMessageSeverityField.getText());
		meta.setMessageAppNameField(wMessageAppNameField.getText());
		meta.setMessageHostNameField(wMessageHostNameField.getText());
		meta.setMessageFacilityField(wMessageFacilityField.getText());
		meta.setMessageField(wMessageField.getText());
		meta.setReadTimeout(wReadTimeout.getText());
		meta.setWriteTimeout(wWriteTimeout.getText());
		meta.setConnectionTimeout(wConnectionTimeout.getText());
		meta.setReconnectInterval(wReconnectInterval.getText());
		meta.setBatchSize(wBatchSize.getText());
		meta.setConnectionMaxRetries(wConnectionMaxRetries.getText());

	}

	private void cancel() {
		stepname = null;
		meta.setChanged(changed);
		dispose();
	}

	private void ok() {
		applyToMeta(meta);
		stepname = wStepname.getText();
		dispose();
	}

	private Image getStepPluginImage(StepMeta stepMeta, int x, int y) {
		PluginInterface plugin = PluginRegistry.getInstance().getPlugin(StepPluginType.class,
				stepMeta.getStepMetaInterface());
		String id = plugin.getIds()[0];
		if (id != null) {
			return GUIResource.getInstance().getImagesSteps().get(id).getAsBitmapForSize(Display.getCurrent(), x, y);
		}
		return null;
	}

	public FormLayout formLayout() {
		FormLayout fl = new FormLayout();
		fl.marginWidth = Const.FORM_MARGIN;
		fl.marginHeight = Const.FORM_MARGIN;
		return fl;
	}

	private void buildConnectionTab() {
		CTabItem wConnectionTab = new CTabItem(wTabFolder, SWT.NONE);
		wConnectionTab.setText(BaseMessages.getString(PKG, "RelpProducerDialog.ConnectionTab"));

		Composite wConnectionComp = new Composite(wTabFolder, SWT.NONE);
		props.setLook(wConnectionComp);
		FormLayout ConnectionLayout = new FormLayout();
		ConnectionLayout.marginHeight = 15;
		ConnectionLayout.marginWidth = 15;
		wConnectionComp.setLayout(ConnectionLayout);

		Label wlRelpServer = new Label(wConnectionComp, SWT.LEFT);
		props.setLook(wlRelpServer);
		wlRelpServer.setText(BaseMessages.getString(PKG, "RelpProducerDialog.RelpServer"));
		FormData fdlRelpServer = new FormData();
		fdlRelpServer.left = new FormAttachment(0, 0);
		fdlRelpServer.top = new FormAttachment(0, 0);
		fdlRelpServer.right = new FormAttachment(0, INPUT_WIDTH);
		wlRelpServer.setLayoutData(fdlRelpServer);

		wRelpServer = new TextVar(transMeta, wConnectionComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wRelpServer);
//	    wRelpServer.addModifyListener( lsMod );
		FormData fdRelpServer = new FormData();
		fdRelpServer.left = new FormAttachment(0, 0);
		fdRelpServer.top = new FormAttachment(wlRelpServer, 5);
		fdRelpServer.right = new FormAttachment(0, INPUT_WIDTH);
		wRelpServer.setLayoutData(fdRelpServer);

		Label wlRelpPort = new Label(wConnectionComp, SWT.LEFT);
		props.setLook(wlRelpPort);
		wlRelpPort.setText(BaseMessages.getString(PKG, "RelpProducerDialog.RelpPort"));
		FormData fdlRelpPort = new FormData();
		fdlRelpPort.left = new FormAttachment(0, 0);
		fdlRelpPort.top = new FormAttachment(wRelpServer, 0);
		fdlRelpPort.right = new FormAttachment(0, INPUT_WIDTH);
		wlRelpPort.setLayoutData(fdlRelpPort);

		wRelpPort = new TextVar(transMeta, wConnectionComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wRelpPort);
//	    wRelpPort.addModifyListener( lsMod );
		FormData fdRelpPort = new FormData();
		fdRelpPort.left = new FormAttachment(0, 0);
		fdRelpPort.top = new FormAttachment(wlRelpPort, 5);
		fdRelpPort.right = new FormAttachment(0, INPUT_WIDTH);
		wRelpPort.setLayoutData(fdRelpPort);

		// Read Timeout
		Label wlReadTimeout = new Label(wConnectionComp, SWT.LEFT);
		props.setLook(wlReadTimeout);
		wlReadTimeout.setText(BaseMessages.getString(PKG, "RelpProducerDialog.ReadTimeout"));
		FormData fdlReadTimeout = new FormData();
		fdlReadTimeout.left = new FormAttachment(0, 0);
		fdlReadTimeout.top = new FormAttachment(wRelpPort, 10);
		fdlReadTimeout.right = new FormAttachment(50, 0);
		wlReadTimeout.setLayoutData(fdlReadTimeout);

		wReadTimeout = new TextVar(transMeta, wConnectionComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wReadTimeout);
//	    wReadTimeout.addModifyListener( lsMod );
		FormData fdReadTimeout = new FormData();
		fdReadTimeout.left = new FormAttachment(0, 0);
		fdReadTimeout.top = new FormAttachment(wlReadTimeout, 5);
		fdReadTimeout.right = new FormAttachment(0, INPUT_WIDTH);
		wReadTimeout.setLayoutData(fdReadTimeout);

		// Write Timeout
		Label wlWriteTimeout = new Label(wConnectionComp, SWT.LEFT);
		props.setLook(wlWriteTimeout);
		wlWriteTimeout.setText(BaseMessages.getString(PKG, "RelpProducerDialog.WriteTimeout"));
		FormData fdlWriteTimeout = new FormData();
		fdlWriteTimeout.left = new FormAttachment(0, 0);
		fdlWriteTimeout.top = new FormAttachment(wReadTimeout, 10);
		fdlWriteTimeout.right = new FormAttachment(50, 0);
		wlWriteTimeout.setLayoutData(fdlWriteTimeout);

		wWriteTimeout = new TextVar(transMeta, wConnectionComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wWriteTimeout);
//	    wWriteTimeout.addModifyListener( lsMod );
		FormData fdWriteTimeout = new FormData();
		fdWriteTimeout.left = new FormAttachment(0, 0);
		fdWriteTimeout.top = new FormAttachment(wlWriteTimeout, 5);
		fdWriteTimeout.right = new FormAttachment(0, INPUT_WIDTH);
		wWriteTimeout.setLayoutData(fdWriteTimeout);

		// Connection Timeout
		Label wlConnectionTimeout = new Label(wConnectionComp, SWT.LEFT);
		props.setLook(wlConnectionTimeout);
		wlConnectionTimeout.setText(BaseMessages.getString(PKG, "RelpProducerDialog.ConnectionTimeout"));
		FormData fdlConnectionTimeout = new FormData();
		fdlConnectionTimeout.left = new FormAttachment(0, 0);
		fdlConnectionTimeout.top = new FormAttachment(wWriteTimeout, 10);
		fdlConnectionTimeout.right = new FormAttachment(50, 0);
		wlConnectionTimeout.setLayoutData(fdlConnectionTimeout);

		wConnectionTimeout = new TextVar(transMeta, wConnectionComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wConnectionTimeout);
//	    wConnectionTimeout.addModifyListener( lsMod );
		FormData fdConnectionTimeout = new FormData();
		fdConnectionTimeout.left = new FormAttachment(0, 0);
		fdConnectionTimeout.top = new FormAttachment(wlConnectionTimeout, 5);
		fdConnectionTimeout.right = new FormAttachment(0, INPUT_WIDTH);
		wConnectionTimeout.setLayoutData(fdConnectionTimeout);

		// Reconnect Interval
		Label wlReconnectInterval = new Label(wConnectionComp, SWT.LEFT);
		props.setLook(wlReconnectInterval);
		wlReconnectInterval.setText(BaseMessages.getString(PKG, "RelpProducerDialog.ReconnectInterval"));
		FormData fdlReconnectInterval = new FormData();
		fdlReconnectInterval.left = new FormAttachment(0, 0);
		fdlReconnectInterval.top = new FormAttachment(wConnectionTimeout, 10);
		fdlReconnectInterval.right = new FormAttachment(50, 0);
		wlReconnectInterval.setLayoutData(fdlReconnectInterval);

		wReconnectInterval = new TextVar(transMeta, wConnectionComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wReconnectInterval);
//	    wReconnectInterval.addModifyListener( lsMod );
		FormData fdReconnectInterval = new FormData();
		fdReconnectInterval.left = new FormAttachment(0, 0);
		fdReconnectInterval.top = new FormAttachment(wlReconnectInterval, 5);
		fdReconnectInterval.right = new FormAttachment(0, INPUT_WIDTH);
		wReconnectInterval.setLayoutData(fdReconnectInterval);

		// Connection MaxRetries
		Label wlConnectionMaxRetries = new Label(wConnectionComp, SWT.LEFT);
		props.setLook(wlConnectionMaxRetries);
		wlConnectionMaxRetries.setText(BaseMessages.getString(PKG, "RelpProducerDialog.ConnectionMaxRetries"));
		FormData fdlConnectionMaxRetries = new FormData();
		fdlConnectionMaxRetries.left = new FormAttachment(0, 0);
		fdlConnectionMaxRetries.top = new FormAttachment(wReconnectInterval, 10);
		fdlConnectionMaxRetries.right = new FormAttachment(50, 0);
		wlConnectionMaxRetries.setLayoutData(fdlConnectionMaxRetries);

		wConnectionMaxRetries = new TextVar(transMeta, wConnectionComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wConnectionMaxRetries);
//	    wConnectionMaxRetries.addModifyListener( lsMod );
		FormData fdConnectionMaxRetries = new FormData();
		fdConnectionMaxRetries.left = new FormAttachment(0, 0);
		fdConnectionMaxRetries.top = new FormAttachment(wlConnectionMaxRetries, 5);
		fdConnectionMaxRetries.right = new FormAttachment(0, INPUT_WIDTH);
		wConnectionMaxRetries.setLayoutData(fdConnectionMaxRetries);

		// Add composite to the tab
		FormData fdConnectionComp = new FormData();
		fdConnectionComp.left = new FormAttachment(0, 0);
		fdConnectionComp.top = new FormAttachment(0, 0);
		fdConnectionComp.right = new FormAttachment(100, 0);
		fdConnectionComp.bottom = new FormAttachment(100, 0);
		wConnectionComp.setLayoutData(fdConnectionComp);
		wConnectionComp.layout();
		wConnectionTab.setControl(wConnectionComp);
	}

	private void buildMessageSetupTab() {
		CTabItem wMessageSetupTab = new CTabItem(wTabFolder, SWT.NONE);
		wMessageSetupTab.setText(BaseMessages.getString(PKG, "RelpProducerDialog.MessageSetupTab"));

		Composite wMessageSetupComp = new Composite(wTabFolder, SWT.NONE);
		props.setLook(wMessageSetupComp);
		FormLayout MessageSetupLayout = new FormLayout();
		MessageSetupLayout.marginHeight = 15;
		MessageSetupLayout.marginWidth = 15;
		wMessageSetupComp.setLayout(MessageSetupLayout);

		// BatchSize
		Label wlBatchSize = new Label(wMessageSetupComp, SWT.LEFT);
		props.setLook(wlBatchSize);
		wlBatchSize.setText(BaseMessages.getString(PKG, "RelpProducerDialog.BatchSize"));
		FormData fdlBatchSize = new FormData();
		fdlBatchSize.left = new FormAttachment(0, 0);
		fdlBatchSize.top = new FormAttachment(0, 10);
		fdlBatchSize.right = new FormAttachment(50, 0);
		wlBatchSize.setLayoutData(fdlBatchSize);

		wBatchSize = new TextVar(transMeta, wMessageSetupComp, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
		props.setLook(wBatchSize);
//	    wBatchSize.addModifyListener( lsMod );
		FormData fdBatchSize = new FormData();
		fdBatchSize.left = new FormAttachment(0, 0);
		fdBatchSize.top = new FormAttachment(wlBatchSize, 5);
		fdBatchSize.right = new FormAttachment(0, INPUT_WIDTH);
		wBatchSize.setLayoutData(fdBatchSize);

		// MessageTimeStampField
		Label wlMessageTimeStampField = new Label(wMessageSetupComp, SWT.LEFT);
		props.setLook(wlMessageTimeStampField);
		wlMessageTimeStampField.setText(BaseMessages.getString(PKG, "RelpProducerDialog.MessageTimeStampField"));
		FormData fdlMessageTimeStampField = new FormData();
		fdlMessageTimeStampField.left = new FormAttachment(0, 0);
		fdlMessageTimeStampField.top = new FormAttachment(wBatchSize, 10);
		fdlMessageTimeStampField.right = new FormAttachment(50, 0);
		wlMessageTimeStampField.setLayoutData(fdlMessageTimeStampField);

		wMessageTimeStampField = createFieldDropDown(wMessageSetupComp, props, meta,
				formDataBelow(wlMessageTimeStampField, INPUT_WIDTH, 5));

		// MessageSeverityField
		Label wlMessageSeverityField = new Label(wMessageSetupComp, SWT.LEFT);
		props.setLook(wlMessageSeverityField);
		wlMessageSeverityField.setText(BaseMessages.getString(PKG, "RelpProducerDialog.MessageSeverityField"));
		FormData fdlMessageSeverityField = new FormData();
		fdlMessageSeverityField.left = new FormAttachment(0, 0);
		fdlMessageSeverityField.top = new FormAttachment(wMessageTimeStampField, 10);
		fdlMessageSeverityField.right = new FormAttachment(50, 0);
		wlMessageSeverityField.setLayoutData(fdlMessageSeverityField);

		wMessageSeverityField = createFieldDropDown(wMessageSetupComp, props, meta,
				formDataBelow(wlMessageSeverityField, INPUT_WIDTH, 5));

		// MessageAppNameField
		Label wlMessageAppNameField = new Label(wMessageSetupComp, SWT.LEFT);
		props.setLook(wlMessageAppNameField);
		wlMessageAppNameField.setText(BaseMessages.getString(PKG, "RelpProducerDialog.MessageAppNameField"));
		FormData fdlMessageAppNameField = new FormData();
		fdlMessageAppNameField.left = new FormAttachment(0, 0);
		fdlMessageAppNameField.top = new FormAttachment(wMessageSeverityField, 10);
		fdlMessageAppNameField.right = new FormAttachment(50, 0);
		wlMessageAppNameField.setLayoutData(fdlMessageAppNameField);

		wMessageAppNameField = createFieldDropDown(wMessageSetupComp, props, meta,
				formDataBelow(wlMessageAppNameField, INPUT_WIDTH, 5));

		// MessageHostNameField
		Label wlMessageHostNameField = new Label(wMessageSetupComp, SWT.LEFT);
		props.setLook(wlMessageHostNameField);
		wlMessageHostNameField.setText(BaseMessages.getString(PKG, "RelpProducerDialog.MessageHostNameField"));
		FormData fdlMessageHostNameField = new FormData();
		fdlMessageHostNameField.left = new FormAttachment(0, 0);
		fdlMessageHostNameField.top = new FormAttachment(wMessageAppNameField, 10);
		fdlMessageHostNameField.right = new FormAttachment(50, 0);
		wlMessageHostNameField.setLayoutData(fdlMessageHostNameField);

		wMessageHostNameField = createFieldDropDown(wMessageSetupComp, props, meta,
				formDataBelow(wlMessageHostNameField, INPUT_WIDTH, 5));

		// MessageFacilityField
		Label wlMessageFacilityField = new Label(wMessageSetupComp, SWT.LEFT);
		props.setLook(wlMessageFacilityField);
		wlMessageFacilityField.setText(BaseMessages.getString(PKG, "RelpProducerDialog.MessageFacilityField"));
		FormData fdlMessageFacilityField = new FormData();
		fdlMessageFacilityField.left = new FormAttachment(0, 0);
		fdlMessageFacilityField.top = new FormAttachment(wMessageHostNameField, 10);
		fdlMessageFacilityField.right = new FormAttachment(50, 0);
		wlMessageFacilityField.setLayoutData(fdlMessageFacilityField);

		wMessageFacilityField = createFieldDropDown(wMessageSetupComp, props, meta,
				formDataBelow(wlMessageFacilityField, INPUT_WIDTH, 5));

		// Message Field
		Label wlMessageField = new Label(wMessageSetupComp, SWT.LEFT);
		props.setLook(wlMessageField);
		wlMessageField.setText(BaseMessages.getString(PKG, "RelpProducerDialog.MessageField"));
		FormData fdlMessageField = new FormData();
		fdlMessageField.left = new FormAttachment(0, 0);
		fdlMessageField.top = new FormAttachment(wMessageFacilityField, 10);
		fdlMessageField.right = new FormAttachment(50, 0);
		wlMessageField.setLayoutData(fdlMessageField);

		wMessageField = createFieldDropDown(wMessageSetupComp, props, meta,
				formDataBelow(wlMessageField, INPUT_WIDTH, 5));

		// Add composite to the tab
		FormData fdMessageSetupComp = new FormData();
		fdMessageSetupComp.left = new FormAttachment(0, 0);
		fdMessageSetupComp.top = new FormAttachment(0, 0);
		fdMessageSetupComp.right = new FormAttachment(100, 0);
		fdMessageSetupComp.bottom = new FormAttachment(100, 0);
		wMessageSetupComp.setLayoutData(fdMessageSetupComp);
		wMessageSetupComp.layout();
		wMessageSetupTab.setControl(wMessageSetupComp);
	}

}
