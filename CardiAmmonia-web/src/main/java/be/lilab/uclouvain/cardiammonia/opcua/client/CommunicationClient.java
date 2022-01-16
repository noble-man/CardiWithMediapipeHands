/*
 * Copyright (c) 2019 the Eclipse Milo Authors
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package be.lilab.uclouvain.cardiammonia.opcua.client;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;

import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;

import be.lilab.uclouvain.cardiammonia.opcua.server.GenericMapUaStruct;
import be.lilab.uclouvain.cardiammonia.opcua.server.ServerVariablesBuilder.Variable;


public interface CommunicationClient {
	
	/**
	 * Writes the given value in the parameter (or variable) on the server. 
	 * The data type of the value must match the expected data type in the namespace. 
	 * 
	 * @param param The name of the variable or parameter to write to.
	 * @param value The value to write to the variable or parameter.
	 * @return a boolean indicating whether the {@link StatusCode} of the operation is good.
	 * @throws ExecutionException or InterruptedException if the status codes could not be retrieved. 
	 * 
	 */
    boolean writeParameter(String param, Object value)throws Exception;

    /**
	 * Reads the value of the given parameter (or variable) on the server. 
	 * The data type of the value to be read must be castable to {@link Integer}. 
	 * 
	 * @param param The name of the variable or parameter to read from.
	 * 
	 * @return an {@link Integer} object containing the value read from the server or null if the {@link StatusCode} of the operation isn't good or if the cast failed. 
	 * @throws ExecutionException or InterruptedException if the status codes could not be retrieved.  
	 * 
	 */
    Integer readIntParameter(String param) throws InterruptedException, ExecutionException;

	
    /**
   	 * Reads the value of the given parameter (or variable) on the server. 
   	 * The data type of the value to be read must be castable to {@link String}. 
   	 * 
   	 * @param param The name of the variable or parameter to read from.
   	 * 
   	 * @return a {@link String} object containing the value read from the server or null if the {@link StatusCode} of the operation isn't good or if the cast failed. 
   	 * @throws ExecutionException or InterruptedException if the status codes could not be retrieved.  
   	 * 
   	 */
    String readStringParameter(String param) throws InterruptedException, ExecutionException;
    
    /**
   	 * Reads the value of the given parameter (or variable) on the server. 
   	 * The data type of the value to be read must be castable to {@link Boolean}. 
   	 * 
   	 * @param param The name of the variable or parameter to read from.
   	 * 
   	 * @return a {@link Boolean} object containing the value read from the server or null if the {@link StatusCode} of the operation isn't good or if the cast failed. 
   	 * @throws ExecutionException or InterruptedException if the status codes could not be retrieved.  
   	 * 
   	 */
	Boolean readBooleanParameter(String param) throws InterruptedException, ExecutionException;
	
	
	/**
   	 * Reads the value of the given parameter (or variable) on the server. 
   	 * The data type of the value to be read must be castable to {@link GenericMapUaStruct}. 
   	 * 
   	 * @param param The name of the variable or parameter to read from.
   	 * 
   	 * @return a {@link GenericMapUaStruct} object containing the value read from the server or null if the {@link StatusCode} of the operation isn't good or if the cast failed. 
   	 * @throws ExecutionException or InterruptedException if the status codes could not be retrieved.  
   	 * 
   	 */
	GenericMapUaStruct readMapParameter(String param)throws InterruptedException, ExecutionException;

	
	/**
	 * Reads the value of the given parameter (or variable) on the server. 
	 * The data type of the value to be read must match the expected data type in the namespace. 
	 * 
	 * @param param The name of the variable or parameter to read from.
	 * 
	 * @return the value read from the server or null if the {@link StatusCode} of the operation isn't good. 
	 * @throws ExecutionException or InterruptedException if the status codes could not be retrieved.  
	 * 
	 */
	Object readParameter(String param) throws InterruptedException, ExecutionException;

	void connect() throws InterruptedException, ExecutionException;
	
	
	OpcUaClient getClient();

	/**
	 * Reads the value of the given parameter (or variable) on the server. 
	 * The data type of the value to be read must be castable to {@link UInteger}. 
	 * 
	 * @param param The name of the variable or parameter to read from.
	 * 
	 * @return an {@link UInteger} object containing the value read from the server or null if the {@link StatusCode} of the operation isn't good or if the cast failed. 
	 * @throws ExecutionException or InterruptedException if the status codes could not be retrieved.  
	 * 
	 */
	UInteger readUIntParameter(String param) throws InterruptedException, ExecutionException;

	
	void registerMapType(Map<String, Variable> mapDefintion, String namespaceURI, String typeName);

	/**
	 * This method calls the overloaded method with the rootNode parameter. It passes the root node value passed in the constructor.
	 * @param methodName
	 * @param input
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws UaException
	 */
	Variant[] callMethod(String methodName, Object... input) throws InterruptedException, ExecutionException, UaException ;
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
	Variant[] callMethod(String rootNode, String methodName, Object... input) throws InterruptedException, ExecutionException, UaException;

}
