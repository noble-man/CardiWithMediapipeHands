package be.lilab.uclouvain.cardiammonia.opcua.server;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.UaSerializationException;
import org.eclipse.milo.opcua.stack.core.serialization.SerializationContext;
import org.eclipse.milo.opcua.stack.core.serialization.UaDecoder;
import org.eclipse.milo.opcua.stack.core.serialization.UaEncoder;
import org.eclipse.milo.opcua.stack.core.serialization.UaStructure;
import org.eclipse.milo.opcua.stack.core.serialization.codecs.GenericDataTypeCodec;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExpandedNodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.ULong;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import be.lilab.uclouvain.cardiammonia.opcua.server.ServerVariablesBuilder.ScalarVariable;
import be.lilab.uclouvain.cardiammonia.opcua.server.ServerVariablesBuilder.Variable;

public class GenericMapUaStruct implements UaStructure {

    public final ExpandedNodeId TYPE_ID;

    public  final ExpandedNodeId BINARY_ENCODING_ID;
    
    private String namespaceURI;
    private String typeName;
    private Map<String, Object> mapValue;
    public GenericMapUaStruct() {
    	this("emptynamespace","emptytype", new HashMap<String,Object>());
    }
    public GenericMapUaStruct(String namespaceURI, String typeName, Map<String, Object> mapValue) {
    	this.namespaceURI = namespaceURI;
    	this.typeName = typeName;
    	this.mapValue = mapValue;
    	TYPE_ID = ExpandedNodeId.parse(String.format(
    	        "nsu=%s;s=%s",
    	        namespaceURI,
    	        "DataType."+typeName
    	    ));
    	BINARY_ENCODING_ID = ExpandedNodeId.parse(String.format(
    	        "nsu=%s;s=%s",
    	        namespaceURI,
    	        "DataType."+typeName+".BinaryEncoding"
    	    ));
    }

    @Override
    public ExpandedNodeId getTypeId() {
        return TYPE_ID;
    }

    public String getTypeName() {
    	return typeName;
    }
    @Override
    public ExpandedNodeId getBinaryEncodingId() {
        return BINARY_ENCODING_ID;
    }

    @Override
    public ExpandedNodeId getXmlEncodingId() {
        // XML encoding not supported
        return ExpandedNodeId.NULL_VALUE;
    }

	public Map<String, Object> getValues() {
		return mapValue;
	}    

	public static class CustomCodec extends GenericDataTypeCodec<GenericMapUaStruct>{
		
		private Map<String, Variable> variables;
		private String namespaceURI;
		private String typeName;

		public CustomCodec(Map<String, Variable> variables, String namespaceURI, String typeName ) {
			this.variables = variables;
			this.namespaceURI = namespaceURI;
			this.typeName = typeName;
		}
	    
		@Override
		public GenericMapUaStruct decode(SerializationContext context,
	            UaDecoder decoder)
				throws UaSerializationException {
			Map<String, Object> values = new HashMap<>();
			for(Variable var:variables.values()) {
				ScalarVariable scalarVar = (ScalarVariable)var;
				Object val = null;
				if (scalarVar.getScalarType()==Identifiers.UInt32) {
					val = decoder.readUInt32(scalarVar.getVariableName());
				}
				else if (scalarVar.getScalarType()==Identifiers.Integer) {
					val = decoder.readInt32(scalarVar.getVariableName());
				}
				else if (scalarVar.getScalarType()==Identifiers.UInt64) {
					val = decoder.readUInt64(scalarVar.getVariableName());
				}
				else if (scalarVar.getScalarType()==Identifiers.String) {
					val = decoder.readString(scalarVar.getVariableName());
				}
				else if (scalarVar.getScalarType()==Identifiers.DateTime) {
					val = decoder.readDateTime(scalarVar.getVariableName());
				}
				else if (scalarVar.getScalarType()==Identifiers.Float) {
					val = decoder.readFloat(scalarVar.getVariableName());
				}
				else if (scalarVar.getScalarType()==Identifiers.Double) {
					val = decoder.readDouble(scalarVar.getVariableName());
				}
				else if (scalarVar.getScalarType()==Identifiers.Boolean) {
					val = decoder.readBoolean(scalarVar.getVariableName());
				}
				else {
					throw new RuntimeException("Unrecognized type in decoder. "+scalarVar.getScalarType());
				}
				values.put(scalarVar.getVariableName(), val);
			}

    		return new GenericMapUaStruct(namespaceURI, typeName, values);
    	}

		@Override
		public void encode(SerializationContext context, UaEncoder encoder, GenericMapUaStruct mapUaStruct)
				throws UaSerializationException {
			Map<String, Object> value = mapUaStruct.mapValue;
			for(Variable var:this.variables.values()) {
				ScalarVariable scalarVar = (ScalarVariable)var;
				Object val = null;
				if (scalarVar.getScalarType()==Identifiers.UInt32) {
					encoder.writeUInt32(scalarVar.getVariableName(), (UInteger) value.get(scalarVar.getVariableName()));
				}
				else if (scalarVar.getScalarType()==Identifiers.Integer) {
					encoder.writeInt32(scalarVar.getVariableName(),  (Integer) value.get(scalarVar.getVariableName()));
				}
				else if (scalarVar.getScalarType()==Identifiers.UInt64) {
					encoder.writeUInt64(scalarVar.getVariableName(), (ULong) value.get(scalarVar.getVariableName()));
				}
				else if (scalarVar.getScalarType()==Identifiers.String) {
					encoder.writeString(scalarVar.getVariableName(), (String) value.get(scalarVar.getVariableName()));
				}
				else if (scalarVar.getScalarType()==Identifiers.DateTime) {
					encoder.writeDateTime(scalarVar.getVariableName(), (DateTime) value.get(scalarVar.getVariableName()));
				}
				else if (scalarVar.getScalarType()==Identifiers.Float) {
					encoder.writeFloat(scalarVar.getVariableName(), (Float) value.get(scalarVar.getVariableName()));
				}
				else if (scalarVar.getScalarType()==Identifiers.Double) {
					encoder.writeDouble(scalarVar.getVariableName(), (Double) value.get(scalarVar.getVariableName()));
				}
				else if (scalarVar.getScalarType()==Identifiers.Boolean) {
					encoder.writeBoolean(scalarVar.getVariableName(), (Boolean) value.get(scalarVar.getVariableName()));
				}
				else {
					throw new RuntimeException("Unrecognized type in decoder. "+scalarVar.getScalarType());
				}
			}
		}

		@Override
		public Class<GenericMapUaStruct> getType() {
			return GenericMapUaStruct.class;
		}

/*		@SuppressWarnings("unchecked")
		@Override
		public Class<GenericMapUaStruct> getType() {
			return (Class<GenericMapUaStruct>) (new HashMap<String, Object>()).getClass();
		}
	*/	
	}
}
