import { Component, HostListener } from '@angular/core';
import { UserService } from './services/user.service';

import { TranslateService } from '@ngx-translate/core';
import { LanguageService } from './services/language.service';
import { PointAndClickService } from './services/point-and-click.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})

export class AppComponent {
	
  private roles: string[] = [];
 
  username?: string;

  constructor(private userService: UserService, 
				public translate: TranslateService, 
				public languageService: LanguageService,
				public pointAndClickService: PointAndClickService) {

									
			
				  	this.languageService.getAllAvailableLanguages().subscribe(
						
								data => {	
									
									this.translate.addLangs(JSON.stringify(data).split(","));

									console.log("data:"+ data);
								},
								error => {
									
									console.log("une erreur: " + error);
								}
						
					);
			
    		
			
					this.languageService.getDefaultLanguage().subscribe(
			
								data => {
									
									this.translate.setDefaultLang(JSON.stringify(data).substring(1,3));
								},
								error => {
									
									console.log("une erreur: " + error);
								}
			
			
					);
	}

  ngOnInit(): void {

    if (this.isLoggedIn()) {
		this.username = this.userService.getCurrentUser().username;
	}
	
	this.languageService.getLanguage().subscribe(
        				data => {
          					
							var lang: string = JSON.stringify(data).substring(1,3);
									
					
							this.translate.use(lang);
						
          						},
        				error => {
          					console.log("une erreur: " + error);
        				});
        				
        				
    
	/*MOVE THE MOUSE ACROSS THE SCREEN AS A SINE WAVE.
	var robot = require("robotjs");
	
	// Speed up the mouse.
	robot.setMouseDelay(2);
	
	var twoPI = Math.PI * 2.0;
	var screenSize = robot.getScreenSize();
	var height = (screenSize.height / 2) - 10;
	var width = screenSize.width;
	
	for (var x = 0; x < width; x++)
	{
	    let y = height * Math.sin((twoPI * x) / width) + height;
	    robot.moveMouse(x, y);
	} */
	     
      
     
        				
  }

  isLoggedIn():boolean{
	return this.userService.isLoggedIn();
  }

  logout(): void {
    this.userService.logoutUser();
    window.location.reload();
  }
  
  @HostListener('window:PinchGesturePerformed', ['$event.detail'])
  onPinchGesturePerformed(val:any){
	//console.log(val);
	
    this.pointAndClickService.sendCoords(val.x, val.y).subscribe(
        				data => {
          					
							var resp: string = JSON.stringify(data).substring(1,3);
									
					
							//this.translate.use(lang);
						
          						},
        				error => {
          					console.log("une erreur: " + error);
        				});
  }
  
  
  @HostListener('window:ClickGesturePerformed', ['$event.detail'])
  onClickGesturePerformed(val:any){
	console.log(val);
	
    this.pointAndClickService.sendClick(val.x, val.y).subscribe(
        				data => {
          					
							var resp: string = JSON.stringify(data).substring(1,3);
									
					
							//this.translate.use(lang);
						
          						},
        				error => {
          					console.log("une erreur: " + error);
        				});
        				
  }
  
}
