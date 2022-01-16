package be.lilab.uclouvain.cardiammonia.application.production;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import be.lilab.uclouvain.cardiammonia.application.authentication.MessageResponse;
import be.lilab.uclouvain.cardiammonia.application.user.User;
import be.lilab.uclouvain.cardiammonia.application.user.UserService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/production")

public class ProductionController {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private ProductionJobManagerBean productionJobManagerBean;
	
	@Autowired
	private CommandLineRunner schedulingRunner;
	
	@Autowired
	private ProductionService productionService;
	@Autowired
	private ProductionLogService productionLogService;

	private String getCurrentDateTimeMS() {
		Date dNow = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
		String datetime = ft.format(dNow);
		return datetime;
	}
	
	@Value("${cardiAmmonia.recipe}")
	private String recipeId;

	@PreAuthorize("hasAuthority('START_SUBBATCH')")
	@PostMapping("/start")
	public ResponseEntity<?> startProduction(Principal principal) {

		System.out.println("Start Production");
		String productionId=getCurrentDateTimeMS();
		Production p = new Production(productionId, recipeId, principal.getName());
		productionService.addProduction(p);
		try {
			logger.info("starting a new production with id: ", productionId);
			schedulingRunner.run(new String[]{productionId});
            return ResponseEntity.status(HttpStatus.OK).body(productionId);

		} catch (Exception e) {
			logger.debug(e.toString());
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Could not start a new production"));
		}
	}
	
	@RequestMapping("/{id}")
	@PreAuthorize("hasAuthority('START_SUBBATCH')")
	public ResponseEntity<Production> getProduction(@PathVariable String id) {
		
		//String getMessage = productionJobManagerBean.getProductionJob(id).get().ping();
		Production production = productionService.getProduction(id).get();
		production.getLogs().stream().forEach(log->log.setProduction(null));
		//productionLogService.findByProduction(production);
        return ResponseEntity.status(HttpStatus.OK).body(production);
		/*try {
			Optional<Production> foundProduction = productionService.getProduction(id);
            if (foundProduction.isPresent()) {
                return ResponseEntity.status(HttpStatus.OK).body(foundProduction.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }*/

	}

/*	@RequestMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Production> getProductionLog(@PathVariable String id) {
		try {
			Optional<Production> foundProduction = productionService.getProduction(id);
            if (foundProduction.isPresent()) {
                return ResponseEntity.status(HttpStatus.OK).body(foundProduction.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

	}
	*/


}
