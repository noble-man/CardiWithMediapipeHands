package be.lilab.uclouvain.cardiammonia.opcua.server;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ulong;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.junit.jupiter.api.Test;

class ServerVariablesBuilderTest {

	@Test
	void testDeclareScalarVariables() {
		/*Types:
		Identifiers.String;
		Identifiers.DateTime; org.eclipse.milo.opcua.stack.core.types.builtin.DateTime.now()
		Identifiers.UInt32; org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint(32)
		Identifiers.UInt64; org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.ulong(64l)
		Identifiers.Boolean; True/False
		Identifiers.Float; 
		*/

		Map<String, ServerVariablesBuilder.Variable> vars = ServerVariablesBuilder.get().declareScalarVariable("var1", Identifiers.UInt64, ulong(64L)).declareScalarVariable("var2", Identifiers.String, "hello").collect();
		assertEquals(2, vars.size());
		assertNotNull(vars.get("var1"));
	}


	@Test
	void testDeclareMapVariable() {
		Map<String, ServerVariablesBuilder.Variable> vars = ServerVariablesBuilder.get()
				.declareScalarVariable("var1", Identifiers.UInt64, ulong(64L))
				.declareMapVariable("myMap","CustomMap")
					.declareScalarVariable("mapVar1", Identifiers.UInt64, ulong(64L))
					.declareScalarVariable("mapVar2", Identifiers.String, "test")
				.build()
				.collect();
		assertEquals(2, vars.size());
		assertNotNull(vars.get("myMap"));
		ServerVariablesBuilder.MapVariable myMap = (ServerVariablesBuilder.MapVariable)vars.get("myMap");
		assertEquals(2, myMap.getValue().size());
		assertNotNull(myMap.getValue().get("mapVar1"));
		
	}

	@Test 
	void testDecareMethod() {
		Map<String, ServerVariablesBuilder.Variable> vars = ServerVariablesBuilder.get()
				.declareMethod("myMethod")
					.declareIntInput("input1", 1)
					.declareIntOutput("output1", 2)
					.build()
				.collect();
		assertEquals(1, vars.size());
		assertNotNull(vars.get("myMethod"));
		ServerVariablesBuilder.MethodVariable method = (ServerVariablesBuilder.MethodVariable) vars.get("myMethod");
		assertEquals("myMethod", method.getVariableName());
		assertEquals(1, method.getInputParameters().size());
		assertEquals(1, method.getOutputParameters().size());
		
	}
	@Test
	void deleteThis() {
		String[] strings = { "foo", "bar", "baz" };
		Optional<String> result = Arrays.stream(strings).map(a-> String.format("\"%s\"", a))
		        .reduce((a, b) -> String.format("%s,%s", a, b));
		System.out.println("["+result.get()+"]");
	}
}
