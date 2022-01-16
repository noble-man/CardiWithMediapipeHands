package be.lilab.uclouvain.cardiammonia.opcua.client;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.sdk.client.api.identity.IdentityProvider;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.slf4j.LoggerFactory;

import be.lilab.uclouvain.cardiammonia.opcua.client.impl.DefaultCommunicationClient;

public class CommunicationClientFactory {
	public enum CommProtocol{
		OPCUA,
		TCP
	}

	private String serverUrl = null;//"127.0.0.1:12686";
	private String serverPath = null;//"/milo";
	private String rootNodeUrl = null;//"CycloneKardio/General/"; 
	private CommProtocol commProtocol;
	private CommunicationClientFactory() {
		
	}
	
	public static CommunicationClientFactory get() {
		return new  CommunicationClientFactory ();
	}

	/**
	 * 
	 * @param serverUrl {server ip} :{port}/serverPath or server {name}:{port}/serverPath. Example  127.0.0.1:12686/milo
	 * @return
	 */
	public CommunicationClientFactory setServerUrlAndPath(String serverUrlAndPath) {
		int serverPathIndex = serverUrlAndPath.lastIndexOf("/");
		
		this.serverUrl = serverUrlAndPath.substring(0, serverPathIndex).replaceFirst("opc.tcp://", "").replaceFirst("opc.https://", "");
		this.serverPath = serverUrlAndPath.substring(serverPathIndex);
		return this;
	}

	/**
	 * 
	 * @param serverUrl {server ip} :{port} or server {name}:{port}. Example  127.0.0.1:12686
	 * @return
	 */
	public CommunicationClientFactory setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
		return this;
	}
	public CommunicationClientFactory setRootNodeUrl(String rootNodeUrl) {
		this.rootNodeUrl = rootNodeUrl;
		return this;
	}
	public CommunicationClientFactory setTCPProtocol() {
		this.commProtocol = CommProtocol.TCP;
		return this;
	}
	public CommunicationClientFactory setOpcUaProtocol() {
		this.commProtocol = CommProtocol.OPCUA;
		return this;
	}
	public CommunicationClientFactory setProtocol(CommProtocol commProtocol) {
		this.commProtocol = commProtocol;
		return this;
	}

	private void validate() {
		if (serverPath==null)
			throw new RuntimeException("Cannot create an OpcUa client without a server path.");
			
		if (this.commProtocol==null) {
			this.commProtocol = CommProtocol.OPCUA;
		}
		
		if (commProtocol==CommProtocol.OPCUA) {
			if (this.serverUrl==null)
				throw new RuntimeException("Cannot create an OpcUa client without a server url.");
			if (this.rootNodeUrl==null)
				throw new RuntimeException("Cannot create an OpcUa client without a Root Node Url to connect to.");
		}
		if (this.commProtocol==CommProtocol.TCP)
			throw new UnsupportedOperationException("The tcp protocol is not supported yet for communication with machines.");
	}
	
	public CommunicationClient build() throws Exception {
		validate();
		String endPointUrl = serverUrl+serverPath;
		if (commProtocol==CommProtocol.OPCUA)
			endPointUrl = "opc.tcp://"+endPointUrl;
		else
			endPointUrl = "opc.https://"+endPointUrl;
			
		
		return new DefaultCommunicationClient(createOpcUaClient(endPointUrl), this.rootNodeUrl);
	}
	
	public CommunicationClient buildAndConnect() throws Exception {
		CommunicationClient client = build();
		client.connect();
		return client;
	}

	private OpcUaClient createOpcUaClient(String endpointUrl) throws Exception //done
	{
		Path securityTempDir = Paths.get(System.getProperty("java.io.tmpdir"), "security");
		Files.createDirectories(securityTempDir);
		if (!Files.exists(securityTempDir)) {
			throw new Exception("unable to create security dir: " + securityTempDir);
		}

		LoggerFactory.getLogger(getClass())
		.info("security temp dir: {}", securityTempDir.toAbsolutePath());

		KeyStoreLoader loader = new KeyStoreLoader().load(securityTempDir);

		return OpcUaClient.create(
				endpointUrl,
				endpoints ->
				endpoints.stream()
				.filter(e -> true)
				.findFirst(),
				configBuilder ->
				configBuilder
				.setApplicationName(LocalizedText.english("eclipse milo opc-ua client"))
				.setApplicationUri("urn:eclipse:milo:examples:client")
				.setCertificate(loader.getClientCertificate())
				.setKeyPair(loader.getClientKeyPair())
				.setIdentityProvider(new AnonymousProvider())
				.setRequestTimeout(uint(5000))
				.build()
				);
	}

	public CommunicationClientFactory setServerPath(String serverPath) {
		this.serverPath = serverPath;
		return this;
	}
	
/*	private Predicate<EndpointDescription> endpointFilter() {
	      return e -> true;
	}
	private SecurityPolicy getSecurityPolicy() {
        return SecurityPolicy.None;
    }

    private IdentityProvider getIdentityProvider() {
        return new AnonymousProvider();
    }
*/
}
