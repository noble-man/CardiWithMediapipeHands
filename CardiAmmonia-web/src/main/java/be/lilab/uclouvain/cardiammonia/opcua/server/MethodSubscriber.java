package be.lilab.uclouvain.cardiammonia.opcua.server;

import static com.google.common.collect.Lists.newArrayList;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaMonitoredItem;
import org.eclipse.milo.opcua.sdk.client.api.subscriptions.UaSubscription;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExtensionObject;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MonitoringMode;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.ContentFilter;
import org.eclipse.milo.opcua.stack.core.types.structured.EventFilter;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoredItemCreateRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.MonitoringParameters;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;
import org.eclipse.milo.opcua.stack.core.types.structured.SimpleAttributeOperand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import be.lilab.uclouvain.cardiammonia.opcua.client.CommunicationClient;

public abstract class MethodSubscriber {
	private Logger logger = LoggerFactory.getLogger(getClass());

	private final AtomicLong clientHandles = new AtomicLong(1L);

	CommunicationClient communicationClient;
	String methodName;
	public MethodSubscriber(CommunicationClient communicationClient, String methodName) throws InterruptedException, ExecutionException {
		this.communicationClient = communicationClient;
		this.methodName = methodName;
		subscribeToEvents(this.communicationClient.getClient());
	}
	
	private final static int SourceName_POS = 0;
	private final static int Message_POS = 1;
	
	public abstract void invoke(String[] inputValues);
	
	
	private void subscribeToEvents(OpcUaClient client) throws InterruptedException, ExecutionException  {
	    UaSubscription subscription = client.getSubscriptionManager()
	            .createSubscription(1000.0).get();

	        ReadValueId readValueId = new ReadValueId(
	            Identifiers.Server,
	            AttributeId.EventNotifier.uid(),
	            null,
	            QualifiedName.NULL_VALUE
	        );

	        // client handle must be unique per item
	        UInteger clientHandle = uint(clientHandles.getAndIncrement());

	        EventFilter eventFilter = new EventFilter(
	            new SimpleAttributeOperand[]{
	                new SimpleAttributeOperand(
                        Identifiers.BaseEventType,
                        new QualifiedName[]{new QualifiedName(0, "SourceName")},
                        AttributeId.Value.uid(),
                        null),
	                new SimpleAttributeOperand(
	                    Identifiers.BaseEventType,
	                    new QualifiedName[]{new QualifiedName(0, "Message")},
	                    AttributeId.Value.uid(),
	                    null),
	                new SimpleAttributeOperand(
	                    Identifiers.BaseEventType,
	                    new QualifiedName[]{new QualifiedName(0, "EventId")},
	                    AttributeId.Value.uid(),
	                    null),
	                new SimpleAttributeOperand(
	                    Identifiers.BaseEventType,
	                    new QualifiedName[]{new QualifiedName(0, "EventType")},
	                    AttributeId.Value.uid(),
	                    null),
	                new SimpleAttributeOperand(
	                    Identifiers.BaseEventType,
	                    new QualifiedName[]{new QualifiedName(0, "Severity")},
	                    AttributeId.Value.uid(),
	                    null),
	                new SimpleAttributeOperand(
                        Identifiers.BaseEventType,
                        new QualifiedName[]{new QualifiedName(0, "DisplayName")},
                        AttributeId.Value.uid(),
                        null),
	                new SimpleAttributeOperand(
                        Identifiers.BaseEventType,
                        new QualifiedName[]{new QualifiedName(0, "BrowseName")},
                        AttributeId.Value.uid(),
                        null),  
	                new SimpleAttributeOperand(
	                    Identifiers.BaseEventType,
	                    new QualifiedName[]{new QualifiedName(0, "Time")},
	                    AttributeId.Value.uid(),
	                    null)
	            },
	            new ContentFilter(null)
	        );

	        MonitoringParameters parameters = new MonitoringParameters(
	            clientHandle,
	            0.0,
	            ExtensionObject.encode(client.getSerializationContext(), eventFilter),
	            uint(10),
	            true
	        );

	        MonitoredItemCreateRequest request = new MonitoredItemCreateRequest(
	            readValueId,
	            MonitoringMode.Reporting,
	            parameters
	        );

	        List<UaMonitoredItem> items = subscription
	            .createMonitoredItems(TimestampsToReturn.Both, newArrayList(request)).get();

	        // do something with the value updates
	        UaMonitoredItem monitoredItem = items.get(0);

	        //final AtomicInteger eventCount = new AtomicInteger(0);

	        monitoredItem.setEventConsumer((item, vs) -> {
	            /*logger.info(
	                "Event Received from {}",
	                item.getReadValueId().getNodeId());
				*/
	            /*for (int i = 0; i < vs.length; i++) {
	                logger.info("\tvariant[{}]: {}", i, vs[i].getValue());
	            }*/
	            if (methodName.equals(vs[SourceName_POS].getValue().toString())){
	            	
	    	        ObjectMapper objectMapper = new ObjectMapper();
	    	        try {
	    	        	LocalizedText txt = (LocalizedText) vs[Message_POS].getValue();
						String[] inputValues= objectMapper.readValue(txt.getText(),String[].class);
		            	invoke(inputValues);
					} catch (JsonMappingException e) {
						e.printStackTrace();
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}
	            }

	            /*if (eventCount.incrementAndGet() == 30) {
	                future.complete(client);
	            }*/
	        });
	}
	
	public String getMethodName() {
		return methodName;
	}
	public CommunicationClient getCommunicationClient() {
		return communicationClient;
	}
}
