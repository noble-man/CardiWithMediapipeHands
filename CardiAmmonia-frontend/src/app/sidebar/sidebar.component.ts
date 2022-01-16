import { Component, OnInit } from '@angular/core';
import { UserService } from '../services/user.service';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {
	
  public menu_item_is_collapsed: Array<boolean> = [false, false]; 
  
  constructor(public userService: UserService) { }

  ngOnInit(): void {
  }

}
