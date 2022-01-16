import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';
import {Subscription} from 'rxjs';
import {debounceTime} from 'rxjs/operators';
import {NgbAlert} from '@ng-bootstrap/ng-bootstrap';
import { NotificationService } from '../services/notification.service';

/*interface Alert {
  type: string;
  message: string;
}
*/
/*const ALERTS: Alert[] = [
  {
    type: 'success',
    message: 'This is an success alert',
  }, {
    type: 'info',
    message: 'This is an info alert',
  }, {
    type: 'warning',
    message: 'This is a warning alert',
  }, {
    type: 'danger',
    message: 'This is a danger alert',
  }, {
    type: 'primary',
    message: 'This is a primary alert',
  }, {
    type: 'secondary',
    message: 'This is a secondary alert',
  }, {
    type: 'light',
    message: 'This is a light alert',
  }, {
    type: 'dark',
    message: 'This is a dark alert',
  }
];*/

@Component({
  selector: 'notifications',
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.scss']
})
export class NotificationsComponent implements OnInit, OnDestroy {

    private subscription!: Subscription;
    message: any;

    constructor(private alertService: NotificationService) { }

    ngOnInit() {
        this.subscription = this.alertService.getMessage().subscribe(message => { 
            this.message = message; 
console.log(message);
        });
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }
/*
	
  private _success = new Subject<string>();
  staticAlertClosed = false;
  notificationMessage = "";

  alerts: Alert[] = [];

  @ViewChild('staticAlert', { static: false })
    staticAlert!: NgbAlert;
  @ViewChild('selfClosingAlert', { static: false })
    selfClosingAlert!: NgbAlert;
	
  constructor() { }

  ngOnInit(): void {
	
	//setTimeout(() => this.staticAlert.close(), 2000);

    this._success.subscribe(message => this.notificationMessage = message);

	console.log("dans onInit");

    this._success.pipe(debounceTime(2000)).subscribe(() => {
	console.log("self closing: "+ this.selfClosingAlert);
      if (this.selfClosingAlert) {
        this.selfClosingAlert.close();
      }
    });	
  }

  addNotification(stateName:string){
	
	switch (stateName) {
   		case 'CYC_START_SUB_BATCH':
       		//this.alerts.push({type:"info", message: "Sub batch production started!"});
			this._success.next("START_SUB_BATCH: NH13 production started!");
       	break;
   		case 'CYC_ACTIVITYREADY':
       		//this.alerts.push({type:"success", message: "Beamning complete. Activity ready in target!"});
			this._success.next("ACTIVITYREADY: activity ready in target!");
       	break;
   		case 'CYC_COMPLETED':
       		this._success.next("COMPLETE: irradiation is complete! ");
       	break;
   		default: 
       	// 
       	break;
	}
	
  }

  close(alert: Alert) {
    this.alerts.splice(this.alerts.indexOf(alert), 1);
  }

  public changeNotificationMessage() { this._success.next(`${new Date()} - Message successfully changed.`); }
*/
}
