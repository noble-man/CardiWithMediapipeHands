import { Component, OnInit, ViewChild } from '@angular/core';
import { ProductionService } from '../services/production.service';
import { Production } from '../model/production';
import { timer, Subscription } from 'rxjs';

import { NotificationsComponent } from "../notifications/notifications.component";
import { NotificationService } from '../services/notification.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-production',
  templateUrl: './production.component.html',
  styleUrls: ['./production.component.scss']
})
export class ProductionComponent implements OnInit {

  message: string ='';
  productionId:string='';
  production: Production|null = null;

  activeAlamrs:number = 0 ;
  firstInterlock:string = '';

  stateDisplayName:string = '';
  productionIsComplete:boolean = true;

  @ViewChild(NotificationsComponent) notifications: any;

  constructor(private productionService: ProductionService, private notificationService: NotificationService, private modalService: NgbModal) { }


  ngOnInit(): void {
	

  }

  startProduction(productionId: string){

	console.log("clicked on start");
	
	this.productionIsComplete = false;
	
	this.productionService.startProduction().subscribe(
    	data => {
          	this.productionId = data;
		  	this.oberserableTimer();
          	console.log(data);
		},
        error => {
          	console.log(error);
        });;
  }

  refreshProductionInfo(): void{
    this.productionService.getProductionDetails(this.productionId)
      .subscribe(
        data => {
           this.production = data;
           console.log(data);
		
		   let stateName = this.production.logs[this.production.logs.length-1].stateName;

		   this.stateDisplayName = stateName.slice(4);
 
		   if(stateName == 'CYC_START_SUB_BATCH' || stateName == 'CYC_ACTIVITYREADY'){

				this.notifications.addNotification(stateName);//passer le message
				
				this.productionIsComplete = false;
			
			}
			
		   if (stateName == 'CYC_COMPLETED'){
			
				this.notifications.addNotification(stateName);
				
				this.productionIsComplete = true;
			}
			
				
		  
        },
        error => {
           console.log(error);
        });
  }

  oberserableTimer() {
    const source = timer(1000, 1000);
    const subscribe = source.subscribe(val => {
        this.refreshProductionInfo();
		if (this.production && this.production.logs.find(log=>log.stateName==="CYC_COMPLETED"))
			subscribe.unsubscribe();
		
    });

 }
 
  openSm(content: any) {
    this.modalService.open(content, { size: 'sm' });
  }

//TO DO
// unifier affichage 3 prods
//e.g. getStatus(machineName){} switch sur la machine



}
