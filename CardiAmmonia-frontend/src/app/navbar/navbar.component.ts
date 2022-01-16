import { Component, OnInit } from '@angular/core';
import { TokenStorageService } from '../services/token-storage.service';
import { UserService } from '../services/user.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {
	
  //currentUrl= window.location.href

  constructor(private userService: UserService) { }

  ngOnInit(): void {
//	console.log(window.location.href);
	
  }
  logout(): void {
    this.userService.logoutUser();
    window.location.reload();
  }
  //hasPermission(permission: string):boolean
}
