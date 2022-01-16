package be.lilab.uclouvain.cardiammonia.application.localization;

import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class LocalizationController {
	
	@Value("${cardiAmmonia.appLanguage}")
	private String appLanguage;
	
	@Value("${cardiAmmonia.availableLanguages}")
	private String[] availableLanguages;
	
	@Value("${cardiAmmonia.defaultLanguage}")
	private String defaultLanguage;
	
	@RequestMapping("/api/lang")
	public char[] getLocale() {
		
		System.out.println("getLocale was called!");
		return appLanguage.toCharArray();

	}
	
	@RequestMapping("/api/lang_default")
	public char[] getDefaultLocale() {
		
		return defaultLanguage.toCharArray();
		
	}
	
	@RequestMapping("/api/lang_all")
	public String[] getAvailableLanguages() {
		
		return availableLanguages;
		
	}
}
