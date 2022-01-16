import { Component, OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-batches',
  templateUrl: './batches.component.html',
  styleUrls: ['./batches.component.scss']
})
export class BatchesComponent implements OnInit {

  constructor(private modalService: NgbModal) { }

  ngOnInit(): void {
  }
  
  viewDetails(content: any) {
    this.modalService.open(content, { size: 'lg' });
  }
  
  addSubBatch(content: any) {
    this.modalService.open(content, { size: 'md' });
  }

}
