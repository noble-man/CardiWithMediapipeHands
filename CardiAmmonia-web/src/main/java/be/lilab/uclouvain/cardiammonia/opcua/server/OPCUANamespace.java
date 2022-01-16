/*
 * Copyright (c) 2019 the Eclipse Milo Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package be.lilab.uclouvain.cardiammonia.opcua.server;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ushort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.milo.opcua.sdk.core.AccessLevel;
import org.eclipse.milo.opcua.sdk.core.Reference;
import org.eclipse.milo.opcua.sdk.core.ValueRanks;
import org.eclipse.milo.opcua.sdk.server.Lifecycle;
import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.api.DataItem;
import org.eclipse.milo.opcua.sdk.server.api.DataTypeDictionaryManager;
import org.eclipse.milo.opcua.sdk.server.api.ManagedNamespaceWithLifecycle;
import org.eclipse.milo.opcua.sdk.server.api.MonitoredItem;
import org.eclipse.milo.opcua.sdk.server.api.methods.AbstractMethodInvocationHandler;
import org.eclipse.milo.opcua.sdk.server.model.nodes.objects.BaseEventTypeNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaFolderNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaMethodNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaVariableNode;
import org.eclipse.milo.opcua.sdk.server.util.SubscriptionModel;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.ByteString;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExtensionObject;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.structured.Argument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import be.lilab.uclouvain.cardiammonia.opcua.server.ServerVariablesBuilder.MethodVariable;
import be.lilab.uclouvain.cardiammonia.opcua.server.ServerVariablesBuilder.ScalarVariable;
import be.lilab.uclouvain.cardiammonia.opcua.server.ServerVariablesBuilder.Variable;

public class OPCUANamespace extends ManagedNamespaceWithLifecycle {

	private final String namespaceURI;// = "urn:eclipse:milo:cyclo-kardio";

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private volatile Thread eventThread;

	private final DataTypeDictionaryManager dictionaryManager;

	private final SubscriptionModel subscriptionModel;
	private final Map<String, ServerVariablesBuilder.Variable> variables;
	private final String rootUrl;
	
	
	public OPCUANamespace(OpcUaServer server, String namespaceURI, String rootUrl, Map<String, ServerVariablesBuilder.Variable> variables) {
		super(server, namespaceURI);
		this.namespaceURI = namespaceURI;
		this.variables = variables;
		this.rootUrl = rootUrl;
		subscriptionModel = new SubscriptionModel(server, this);
		dictionaryManager = new DataTypeDictionaryManager(getNodeContext(), namespaceURI);

		getLifecycleManager().addLifecycle(dictionaryManager);
		getLifecycleManager().addLifecycle(subscriptionModel);

		getLifecycleManager().addStartupTask(this::createAndAddNodes);

		getLifecycleManager().addLifecycle(new Lifecycle() {
			@Override
			public void startup() {
			}

			@Override
			public void shutdown() {
				try {
					eventThread.interrupt();
					eventThread.join();
				} catch (InterruptedException ignored) {
					// ignored
				}
			}
		});
	}
	
	private UaFolderNode createRootFolderNodes(String rootUrl) {
		String[] str = rootUrl.split("/");
		String fullName = null;
		UaFolderNode parentNode = null;
		for (String qualifiedName:str ) {
			if (fullName==null)
				fullName = qualifiedName;
			else
				fullName = fullName + "/" + qualifiedName;
			NodeId folderNodeId = newNodeId(fullName);
			UaFolderNode folderNode = new UaFolderNode(getNodeContext(), folderNodeId, newQualifiedName(qualifiedName),
					LocalizedText.english(qualifiedName));
			getNodeManager().addNode(folderNode);
			if (parentNode==null) {//This is the root node.
				// Make sure our new folder shows up under the server's Objects folder.
				folderNode.addReference(new Reference(folderNode.getNodeId(), Identifiers.Organizes,
						Identifiers.ObjectsFolder.expanded(), false));
			}
			else {
				parentNode.addOrganizes(folderNode);
			}
			parentNode = folderNode;
		}
		return parentNode;
	}

	private void declareVariables(UaFolderNode parentNode, Map<String, Variable> variables) throws Exception {
		for (Variable var: variables.values()){
			Variant variant = null;
			String name = var.getVariableName();
			NodeId dataTypeId = null;
			if (var.isMethod()){//This is a method variable
				ServerVariablesBuilder.MethodVariable methodVar = (ServerVariablesBuilder.MethodVariable)var;

				UaMethodNode methodNode = UaMethodNode.builder(getNodeContext())
						.setNodeId(newNodeId(parentNode.getNodeId().getIdentifier() +"/"+ name)).setBrowseName(newQualifiedName(name))
						.setDisplayName(new LocalizedText(null, name))
						.setDescription(LocalizedText.english(name)).build();

				DummyMethodHandler dummyHandler = new DummyMethodHandler(methodNode, methodVar);
				methodNode.setInputArguments(dummyHandler.getInputArguments());
				methodNode.setOutputArguments(dummyHandler.getOutputArguments());

				methodNode.setInvocationHandler(dummyHandler);
				getNodeManager().addNode(methodNode);

				methodNode.addReference(new Reference(methodNode.getNodeId(), Identifiers.HasComponent,
						parentNode.getNodeId().expanded(), false));

				//STOP. Go back
				continue;
			}
			else if (var.isScalar()) {
				ServerVariablesBuilder.ScalarVariable scalarVar = (ServerVariablesBuilder.ScalarVariable)var;
				variant = new Variant(scalarVar.getValue());
				dataTypeId = scalarVar.getScalarType();
			}
			else {//This is a struct. register it and then define the variable
				ServerVariablesBuilder.MapVariable mapVar = (ServerVariablesBuilder.MapVariable)var;

				//dataTypeId = ExpandedNodeId.parse(String.format("nsu=%s;s=%s",namespaceURI,"DataType."+mapVar.getVariableType()))
				//						.toNodeIdOrThrow(getServer().getNamespaceTable());
				//NodeId binaryEncodingId = ExpandedNodeId.parse(String.format("nsu=%s;s=%s", namespaceURI, "DataType."+mapVar.getVariableType()+".BinaryEncoding"))
				//						.toNodeIdOrThrow(getServer().getNamespaceTable());
				
				
				// At a minimum, custom types must have their codec registered.
				// If clients don't need to dynamically discover types and will
				// register the codecs on their own then this is all that is
				// necessary.
				// The dictionary manager will add a corresponding DataType Node to
				// the AddressSpace.

				Map<String, Object> initMapValue = new HashMap<>();
				for(Variable v:mapVar.getValue().values()) {
					ScalarVariable scalarVar = (ScalarVariable)v;
					initMapValue.put(scalarVar.getVariableName(), scalarVar.getValue());
				}
				
				
				GenericMapUaStruct mapStructValue = new GenericMapUaStruct(namespaceURI, mapVar.getVariableType(), initMapValue);
				dataTypeId = mapStructValue.getTypeId().toNodeIdOrThrow(getServer().getNamespaceTable());
				NodeId binaryEncodingId = mapStructValue.getBinaryEncodingId().toNodeIdOrThrow(getServer().getNamespaceTable());
				
				dictionaryManager.registerStructureCodec(new GenericMapUaStruct.CustomCodec(mapVar.getValue(), namespaceURI, mapVar.getVariableType()).asBinaryCodec(), 
						mapStructValue.getTypeName(),	
						dataTypeId,
						binaryEncodingId);


				ExtensionObject xo = ExtensionObject.encodeDefaultBinary(getServer().getSerializationContext(), mapStructValue, binaryEncodingId);
				variant = new Variant(xo);

			}
			UaVariableNode node = new UaVariableNode.UaVariableNodeBuilder(getNodeContext()) 
					.setNodeId(newNodeId(parentNode.getNodeId().getIdentifier() +"/"+ name)).setAccessLevel(AccessLevel.READ_WRITE)
					.setUserAccessLevel(AccessLevel.READ_WRITE).setBrowseName(newQualifiedName(name))
					.setDisplayName(LocalizedText.english(name)).setDataType(dataTypeId)
					.setTypeDefinition(Identifiers.BaseDataVariableType).build();

			node.setValue(new DataValue(variant));

			node.getFilterChain().addLast(new AttributeLoggingFilter(AttributeId.Value::equals));

			getNodeManager().addNode(node);
			parentNode.addOrganizes(node);		
			
		};

	}
    
	private void createAndAddNodes()  {
		UaFolderNode rootFolderNode = createRootFolderNodes(rootUrl);
		try {
			declareVariables(rootFolderNode, variables);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

    private class DummyMethodHandler extends AbstractMethodInvocationHandler{
        private final Logger logger = LoggerFactory.getLogger(getClass());

    	MethodVariable method;
    	Argument[] inputArgs;
    	Argument[] outputArgs;
        private final OpcUaServer server;

    	
		public DummyMethodHandler(UaMethodNode methodNode, MethodVariable method) {
			super(methodNode);
			this.method = method;
	        this.server = methodNode.getNodeContext().getServer();
			//Create input arguments
			List<Argument> inputArgsList = new ArrayList<>();
			for (Variable x:method.getInputParameters().values()) {
				ServerVariablesBuilder.ScalarVariable paramVar = (ServerVariablesBuilder.ScalarVariable)x;
				inputArgsList.add(new Argument(paramVar.getVariableName(), paramVar.getScalarType(), ValueRanks.Scalar, null, new LocalizedText(paramVar.getVariableName())));
			}
			inputArgs = inputArgsList.toArray(new Argument[] {});
			//Create output arguments
			List<Argument> outputArgsList = new ArrayList<>();
			for (Variable x:method.getOutputParameters().values()) {
				ServerVariablesBuilder.ScalarVariable paramVar = (ServerVariablesBuilder.ScalarVariable)x;
				outputArgsList.add(new Argument(paramVar.getVariableName(), paramVar.getScalarType(), ValueRanks.Scalar, null, new LocalizedText(paramVar.getVariableName())));
			}
			outputArgs = outputArgsList.toArray(new Argument[] {});

		}

		@Override
		public Argument[] getInputArguments() {
			return inputArgs;
		}

		@Override
		public Argument[] getOutputArguments() {
			return outputArgs;
		}

		@Override
		protected Variant[] invoke(InvocationContext invocationContext, Variant[] inputValues) throws UaException {
	        logger.debug("Invoking "+method.getVariableName()+" method of objectId={}", invocationContext.getObjectId());

	        BaseEventTypeNode eventNode = server.getEventFactory().createEvent(
	                new NodeId(0, UUID.randomUUID()),
	                Identifiers.BaseEventType
	            );

            eventNode.setBrowseName(new QualifiedName(0, method.getVariableName()));
            eventNode.setDisplayName(LocalizedText.english(method.getVariableName()));
            eventNode.setEventId(ByteString.of(new byte[]{0, 1, 2, 3}));
            eventNode.setEventType(Identifiers.BaseEventType);
            eventNode.setSourceNode(getNode().getNodeId());
            eventNode.setSourceName(getNode().getDisplayName().getText());
            eventNode.setTime(DateTime.now());
            eventNode.setReceiveTime(DateTime.NULL_VALUE);

            String inputValuesJson = Arrays.stream(inputValues).map(a-> String.format("\"%s\"", (a.getValue() instanceof DateTime )?((DateTime)a.getValue()).getUtcTime():a.getValue().toString()))
    		        .reduce((a, b) -> String.format("%s,%s", a, b)).get();
            eventNode.setMessage(LocalizedText.english("["+inputValuesJson+"]"));
            
            eventNode.setSeverity(ushort(2));

            server.getEventBus().post(eventNode);

            eventNode.delete();

			List<Variant> outputArgsList = new ArrayList<>();
			for (Variable outVar:method.getOutputParameters().values()) {
				ServerVariablesBuilder.ScalarVariable paramVar = (ServerVariablesBuilder.ScalarVariable)outVar;
				outputArgsList.add(new Variant(paramVar.getValue()));
			}
			return outputArgsList.toArray(new Variant[] {});
		}
    }

	@Override
	public void onDataItemsCreated(List<DataItem> dataItems) {
		subscriptionModel.onDataItemsCreated(dataItems);
	}

	@Override
	public void onDataItemsModified(List<DataItem> dataItems) {
		subscriptionModel.onDataItemsModified(dataItems);
	}

	@Override
	public void onDataItemsDeleted(List<DataItem> dataItems) {
		subscriptionModel.onDataItemsDeleted(dataItems);
	}

	@Override
	public void onMonitoringModeChanged(List<MonitoredItem> monitoredItems) {
		subscriptionModel.onMonitoringModeChanged(monitoredItems);
	}
}
