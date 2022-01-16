package be.lilab.uclouvain.cardiammonia.opcua.server;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

public class ServerVariablesBuilder {

	public static interface Variable{
		String getVariableName();
		boolean isScalar();
		default boolean isMethod() {return false;};
		String getVariableType();
	}
	public static interface ScalarVariable extends Variable{
		NodeId getScalarType();
		Object getValue();
		default boolean isScalar() {return true;};
	}
	public static interface MapVariable extends Variable{
		Map<String, Variable> getValue();
		default boolean isScalar() {return false;};
	}
	public static interface MethodVariable extends Variable{
		Map<String, Variable> getInputParameters();
		Map<String, Variable> getOutputParameters();
		default boolean isMethod() {return true;};
		default boolean isScalar() {return false;};
	}
	public static ServerVariablesBuilder  get() {
		return new ServerVariablesBuilder (null, null, null);
	}
	
	String variableName;
	String variableType;

	Map<String, Variable> variables = new HashMap<>();
	ServerVariablesBuilder parentBuilder;
	private ServerVariablesBuilder(String variableName, String variableType, ServerVariablesBuilder variableBuilder) {
		this.variableName = variableName;
		this.variableType = variableType;
		this.parentBuilder = variableBuilder;
	}
	
	public ServerVariablesBuilder declareScalarVariable(String variableName, NodeId type, Object value) {
		ScalarVariable var = new ScalarVariableImpl(variableName, type, value);
		variables.put(variableName, var);
		return this;
	}
	
	public ServerVariablesBuilder declareUIntVariable(String variableName, int value) {
		return declareScalarVariable(variableName, Identifiers.UInt32, org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint(value));
	}
	public ServerVariablesBuilder declareIntVariable(String variableName, int value) {
		return declareScalarVariable(variableName, Identifiers.Integer, value);
	}
	public ServerVariablesBuilder declareLongVariable(String variableName, long value) {
		return declareScalarVariable(variableName, Identifiers.UInt64, org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ulong(value));
	}
	public ServerVariablesBuilder declareStringVariable(String variableName, String value) {
		return declareScalarVariable(variableName, Identifiers.String, value);
	}
	public ServerVariablesBuilder declareFloatVariable(String variableName, float value) {
		return declareScalarVariable(variableName, Identifiers.Float, value);
	}
	public ServerVariablesBuilder declareDoubleVariable(String variableName, double value) {
		return declareScalarVariable(variableName, Identifiers.Double, value);
	}
	public ServerVariablesBuilder declareBooleanVariable(String variableName, boolean value) {
		return declareScalarVariable(variableName, Identifiers.Boolean, value);
	}
	public ServerVariablesBuilder declareDateTimeVariable(String variableName, DateTime value) {
		return declareScalarVariable(variableName, Identifiers.DateTime, value);
	}
	public ServerVariablesBuilder declareMapVariable(String variableName, String variableType) {
		ServerVariablesBuilder var = new ServerVariablesBuilder(variableName, variableType, this);
		return var;
	}
	public MethodVariablesBuilder declareMethod(String methodName) {
		return new MethodVariablesBuilder(this, methodName);
	}
	
	public static class MethodVariablesBuilder {
		ServerVariablesBuilder parentBuilder;
		MethodVariableImpl method;
		public MethodVariablesBuilder(ServerVariablesBuilder parentBuilder, String methodName) {
			this.parentBuilder = parentBuilder;
			this.method = new MethodVariableImpl(methodName, new HashMap<>(), new HashMap<>());
		}
		
		public MethodVariablesBuilder declareInputParameter(String variableName, NodeId type, Object value) {
			ScalarVariable var = new ScalarVariableImpl(variableName, type, value);
			method.inputParameters.put(variableName, var);
			return this;
		}
		public MethodVariablesBuilder declareIntInput(String variableName, int value) {
			return declareInputParameter(variableName, Identifiers.UInt32, org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint(value));
		}
		public MethodVariablesBuilder declareLongInput(String variableName, long value) {
			return declareInputParameter(variableName, Identifiers.UInt64, org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ulong(value));
		}
		public MethodVariablesBuilder declareStringInput(String variableName, String value) {
			return declareInputParameter(variableName, Identifiers.String, value);
		}
		public MethodVariablesBuilder declareFloatInput(String variableName, float value) {
			return declareInputParameter(variableName, Identifiers.Float, value);
		}
		public MethodVariablesBuilder declareDoubleInput(String variableName, double value) {
			return declareInputParameter(variableName, Identifiers.Double, value);
		}
		public MethodVariablesBuilder declareBooleanInput(String variableName, boolean value) {
			return declareInputParameter(variableName, Identifiers.Boolean, value);
		}
		public MethodVariablesBuilder declareDateTimeInput(String variableName, DateTime value) {
			return declareInputParameter(variableName, Identifiers.DateTime, value);
		}

		
		public MethodVariablesBuilder declareOutputParameter(String variableName, NodeId type, Object value) {
			ScalarVariable var = new ScalarVariableImpl(variableName, type, value);
			method.outputParameters.put(variableName, var);
			return this;
		}
		public MethodVariablesBuilder declareIntOutput(String variableName, int value) {
			return declareOutputParameter(variableName, Identifiers.UInt32, org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint(value));
		}
		public MethodVariablesBuilder declareLongOutput(String variableName, long value) {
			return declareOutputParameter(variableName, Identifiers.UInt64, org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ulong(value));
		}
		public MethodVariablesBuilder declareStringOutput(String variableName, String value) {
			return declareOutputParameter(variableName, Identifiers.String, value);
		}
		public MethodVariablesBuilder declareFloatOutput(String variableName, float value) {
			return declareOutputParameter(variableName, Identifiers.Float, value);
		}
		public MethodVariablesBuilder declareDoubleOutput(String variableName, double value) {
			return declareOutputParameter(variableName, Identifiers.Double, value);
		}
		public MethodVariablesBuilder declareBooleanOutput(String variableName, boolean value) {
			return declareOutputParameter(variableName, Identifiers.Boolean, value);
		}
		public MethodVariablesBuilder declareDateTimeOutput(String variableName, DateTime value) {
			return declareOutputParameter(variableName, Identifiers.DateTime, value);
		}
		
		public ServerVariablesBuilder build() {
			parentBuilder.variables.put(method.getVariableName(), method);//Declare this method in the parent context
			return parentBuilder;
		}

	}
	
	public static class ScalarVariableImpl implements ScalarVariable{
		String variableName;
		NodeId type;
		Object value; 
		public ScalarVariableImpl(String variableName, NodeId type, Object value) {
			this.variableName = variableName;
			this.type = type;
			this.value = value;
		}
		@Override
		public String getVariableName() {
			return variableName;
		}
		@Override
		public String getVariableType() {
			return type.toString();
		}
		@Override
		public NodeId getScalarType() {
			return type;
		}
		@Override
		public Object getValue() {
			return value;
		}
	}
	public static class MapVariableImpl implements MapVariable{

		String variableName;
		String type;
		Map<String, Variable> variables;
		public MapVariableImpl(String variableName, String type, Map<String, Variable> variables) {
			this.variableName = variableName;
			this.type = type;
			this.variables = variables;
		}
		@Override
		public String getVariableName() {
			return variableName;
		}
		@Override
		public String getVariableType() {
			return type;
		}
		@Override
		public Map<String, Variable> getValue() {
			return variables;
		}
	}
	
	public static class MethodVariableImpl implements MethodVariable{

		String variableName;
		Map<String, Variable> inputParameters;
		Map<String, Variable> outputParameters;

		public MethodVariableImpl(String variableName, Map<String, Variable> inputParameters,
				Map<String, Variable> outputParameters) {
			super();
			this.variableName = variableName;
			this.inputParameters = inputParameters;
			this.outputParameters = outputParameters;
		}

		@Override
		public String getVariableName() {
			return variableName;
		}

		@Override
		public String getVariableType() {
			return "method";
		}

		@Override
		public Map<String, Variable> getInputParameters() {
			return inputParameters;
		}

		@Override
		public Map<String, Variable> getOutputParameters() {
			return outputParameters;
		}
	}

	public ServerVariablesBuilder build() {
		parentBuilder.variables.put(variableName, new MapVariableImpl(variableName, variableType, variables));//Declare this variables in the parent context
		return parentBuilder;
	}
	public Map<String, Variable> collect(){
		return variables;
	}
	

	
}
