/*
 * Copyright (c) 2019 the Eclipse Milo Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package be.lilab.uclouvain.cardiammonia.opcua.client.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExpandedNodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExtensionObject;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.CallMethodRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.CallMethodResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import be.lilab.uclouvain.cardiammonia.opcua.client.CommunicationClient;
import be.lilab.uclouvain.cardiammonia.opcua.server.GenericMapUaStruct;
import be.lilab.uclouvain.cardiammonia.opcua.server.ServerVariablesBuilder.Variable;

/*@Component
@PropertySource("classpath:application.properties")
*/public class DefaultCommunicationClient implements CommunicationClient {

	private OpcUaClient client;
	private String rootNode;

	private Logger logger = LoggerFactory.getLogger(getClass());

	public DefaultCommunicationClient(OpcUaClient client, String rootNode) {
		this.client = client;
		this.rootNode = rootNode.endsWith("/")?rootNode.substring(0, rootNode.length()-1):rootNode;
	}
	
	@Override
	public void connect() throws InterruptedException, ExecutionException {
		client.connect().get();		
	}

	@Override
	public boolean writeParameter(String param, Object value) throws Exception 
	{
		List<NodeId> nodeIds = ImmutableList.of(new NodeId(2, rootNode+"/" + param));
		//List<NodeId> nodeIds = ImmutableList.of(new NodeId(2, "CycloneKardio/General/Int32"));

		Variant v = new Variant(value);//Boolean

		// don't write status or timestamps
		DataValue dv = new DataValue(v, null, null);

		// write asynchronously....
		CompletableFuture<List<StatusCode>> f =
				client.writeValues(nodeIds, ImmutableList.of(dv));

		// ...but block for the results so we write in order
		List<StatusCode> statusCodes = f.get(); //get is blocking. jvm is waiting.
		StatusCode status = statusCodes.get(0);

		if (status.isGood()) {
			;//logger.info("Wrote '{}' to nodeId={}", v, nodeIds.get(0));

		}
		else {
			logger.debug(status.toString());
		}

		//future.complete(client);
		return status.isGood();

	}


	@Override
	public Integer readIntParameter (String param) throws InterruptedException, ExecutionException
	{
		Object val = readParameter(param);
		if (val==null) {//status is not good
			return null;
		}
		return cast(Integer.class, val);
	}

	@Override
	public UInteger readUIntParameter (String param) throws InterruptedException, ExecutionException
	{
		Object val = readParameter(param);
		if (val==null) {//status is not good
			return null;
		}
		return cast(UInteger.class, val);
	}
	
	@Override
	public String readStringParameter (String param) throws InterruptedException, ExecutionException
	{
		Object val = readParameter(param);
		if (val==null) {//status is not good
			return null;
		}
		return cast(String.class, val);
	}

	private <T> T cast(Class<T> type, Object value){
		try {
			return type.cast(value);
		}
		catch(ClassCastException e) {
			logger.error("Parameter value it not a value of type: "+type.getCanonicalName());
		}
		return null;
	}
	@Override
 	public Boolean readBooleanParameter (String param) throws InterruptedException, ExecutionException
	{
		Object val = readParameter(param);
		if (val==null) {//status is not good
			return null;
		}
		
		return cast(Boolean.class, val);
	}

	@Override
	public GenericMapUaStruct readMapParameter(String param) throws InterruptedException, ExecutionException
	{
		Object val = readParameter(param);
		if (val==null) {//status is not good
			return null;
		}
		
		ExtensionObject xo = cast(ExtensionObject.class, val);
			
		GenericMapUaStruct decoded = (GenericMapUaStruct) xo.decode(
            client.getSerializationContext()
        );
		return decoded;
		
	}

	@Override
	public Object readParameter(String param) throws InterruptedException, ExecutionException {
		NodeId nodeIds = new NodeId(2, this.rootNode+"/" + param);
		CompletableFuture<DataValue> f = client.readValue(0.0, TimestampsToReturn.Both, nodeIds);
		DataValue value = f.get();
		StatusCode status = value.getStatusCode();
		if (status.isGood()) {
			;//logger.info(param + "={}", value.getValue().getValue());
		}
		else {
			logger.debug(status.toString());
			return null;
		}
		return value.getValue().getValue();

	}
	/**
	 * This method calls the overloaded method with the rootNode parameter. It passes the root node value passed in the constructor.
	 * @param methodName
	 * @param input
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws UaException
	 */
	@Override
	public Variant[] callMethod(String methodName, Object... input) throws InterruptedException, ExecutionException, UaException {
		return callMethod(this.rootNode, methodName, input);
	}
	/**
	 * 
	 * @param rootNode: The root node that contains the method. this string value must not end with a '/'. Example: "CycloneKardio/General"
	 * @param methodName: The mthod to call
	 * @param input: the list of values to pass as parameters. No checking on the parameters is performed. Passing wrong parameters may resutls in an unpredicted error.
	 * @return the list of output parameters values following the same order of definition as defined in the method. This method cannot return the name of the variable Therefore, it is the responsibility of the caller to determine the name of the returned variable.
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws UaException
	 */
	@Override
	public Variant[] callMethod(String rootNode, String methodName, Object... input) throws InterruptedException, ExecutionException, UaException {
		String rootNodeUpdated = rootNode.endsWith("/")?rootNode.substring(0, rootNode.length()-1):rootNode;
	    NodeId objectId = NodeId.parse("ns=2;s="+rootNodeUpdated);
	    NodeId methodId = NodeId.parse("ns=2;s="+rootNodeUpdated+"/"+methodName);

	    Variant [] varinatInput = new Variant[input.length];
	    for (int i=0; i<input.length; i++)
	    	varinatInput[i] = new Variant(input[i]);

	    CallMethodRequest request = new CallMethodRequest(
	        objectId,
	        methodId,
	        varinatInput
	    );
	   
	    CallMethodResult result = client.call(request).get();
	    StatusCode statusCode = result.getStatusCode();
	    if (statusCode.isGood()) {
	        return result.getOutputArguments();
	    } 
	    
	    StatusCode[] inputArgumentResults = result.getInputArgumentResults();
	    for (int i = 0; i < inputArgumentResults.length; i++) {
	        logger.error("inputArgumentResults[{}]={}", i, inputArgumentResults[i]);
	    }
	    throw new UaException(statusCode);
	}

	@Override
	public OpcUaClient getClient() {
		return client;
	}

    public void registerMapType(Map<String, Variable> mapDefintion, String namespaceURI, String typeName) {
        NodeId binaryEncodingId =     	ExpandedNodeId.parse(String.format(
    	        "nsu=%s;s=%s",
    	        namespaceURI,
    	        "DataType."+typeName+".BinaryEncoding"
    	    ))
        	.toNodeId(client.getNamespaceTable())
            .orElseThrow(() -> new IllegalStateException("namespace not found"));

        // Register codec with the client DataTypeManager instance
        client.getDataTypeManager().registerCodec(
            binaryEncodingId,
            new GenericMapUaStruct.CustomCodec(mapDefintion, namespaceURI, typeName).asBinaryCodec()
        );
    }

}
