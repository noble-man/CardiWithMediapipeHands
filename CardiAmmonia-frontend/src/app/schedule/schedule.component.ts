import { Component, OnInit } from '@angular/core';
import { ProductionService } from '../services/production.service';
import { Production } from '../model/production';
//import { timer, Subscription } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-schedule',
  templateUrl: './schedule.component.html',
  styleUrls: ['./schedule.component.scss']
})
export class ScheduleComponent implements OnInit {

  productions: Production[] = [];

  constructor(private productionService: ProductionService, private router: Router) { }

  ngOnInit(): void {
  }

  getAllProductions(){

	console.log("get all productions: ");
	
  }
  addProduction(productionId:string){ console.log("add production: " + productionId);}

  viewProduction(productionId:string){ 
	
	    this.productionService.getProductionDetails(productionId)
	      .subscribe(
	        response => {
	          console.log("production details: " + response);
	          this.router.navigate(['/production/' + productionId]);
	        },
	        error => {
	          console.log(error);
	        });
	
	}

  updateProduction(productionId:string){ console.log("update production: " + productionId);}

  deleteProduction(productionId:string){ console.log("delete production: " + productionId);}

  startProduction(productionId:string){
	
	console.log("start production: " + productionId);
	this.router.navigate(['/production/' + productionId]);
 }	

}
