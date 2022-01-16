package be.lilab.uclouvain.cardiammonia.application.production;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;

import be.lilab.uclouvain.cardiammonia.application.RunSimulatorsExtenstion;
import be.lilab.uclouvain.cardiammonia.application.TestUtils;
import be.lilab.uclouvain.cardiammonia.application.user.User;

@ExtendWith(SpringExtension.class)
@ExtendWith({RunSimulatorsExtenstion.class})//Required to run the simulators, or else the server will not start and the tests will fail.

@SpringBootTest
@AutoConfigureMockMvc
public class TestProduction {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ProductionService productionService;

	@Autowired
	private ProductionLogService ProductionLogService;

/*	@BeforeAll
	public static void createTestUsers() throws Exception {
		TestUtils.createTestUsers(mvc);
	}    
*/
	@Test
	public void testStartProductionWillStartProductionJob() throws Exception {
		/**
		 * Basic test for starting a production run: A job will be created in the background and it wil insert records in the ProductionLog.
		 * This test starts a production and then test after 2 seconds that there are at least 2 records in the ProductionLog.
		 * Hypothesis: this test assumes that in 2 seconds, the ProductionJob will write 2 production logs.
		 */
		//TestUtils.createTestUsers(mvc);
		String token = TestUtils.getJWTToken(mvc, "nurse","12345678");//work as nurse

		ResultActions action= TestUtils.post(mvc, token, "/api/production/start", "");
		MvcResult result = action.andExpect(status().isOk()).andReturn();

		String productionId = result.getResponse().getContentAsString();
		
		assertNotNull(productionId);
		Thread.sleep(32000);//wait for 32 seconds to give the production job enough time to produce production logs.
		
		action = TestUtils.get(mvc, token, "/api/production/"+productionId);
		result = action.andExpect(status().isOk()).andReturn();

		String response = result.getResponse().getContentAsString();
		
		ObjectMapper objectMapper = new ObjectMapper();

		Production prod = objectMapper.readValue(response,Production.class);
		assertEquals(30, prod.getLogs().size());
		//assertTrue(prod.getLogs().size()==30);//This is the number of log records that are recorded in the overall cyclone states. Check regularly this number if this test fails
//        ProductionLog[] productionLogList = objectMapper.readValue(response,ProductionLog[].class);
//		assertTrue(productionLogList.length>2);
	}
}
