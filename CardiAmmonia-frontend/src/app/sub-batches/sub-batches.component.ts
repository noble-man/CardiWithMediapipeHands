import { Component, OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-sub-batches',
  templateUrl: './sub-batches.component.html',
  styleUrls: ['./sub-batches.component.scss']
})
export class SubBatchesComponent implements OnInit {
	
  elements: any = [];	 

  constructor(private modalService: NgbModal) { }

/*
  ngOnInit() {
    for (let i = 1; i <= 11; i++) {
      this.elements.push({
        id: i,
        first: {nick: 'Nick ' + i, name: 'Name ' + i},
        last: 'Name ' + i,
        handle: 'Handle ' + i
      });
    }
  }
  */
  ngOnInit() {}
  
    addProduction(productionId:string){ console.log("add production: " + productionId);}

  viewProduction(productionId:string){ 
	   
  }

  updateProduction(productionId:string){ console.log("update production: " + productionId);}

  deleteProduction(productionId:string){ console.log("delete production: " + productionId);}

  startProduction(productionId:string){
	
  }
  
   viewDetails(content: any) {
    this.modalService.open(content, { size: 'lg' });
  }	
 
}




