import { Component, OnInit } from '@angular/core';
import { UserService } from '../services/user.service';
import { User } from '../model/user';

@Component({
  selector: 'app-list-users',
  templateUrl: './list-users.component.html',
  styleUrls: ['./list-users.component.scss']
})
export class ListUsersComponent implements OnInit {

  users: any;
  currentUser: User|null = null;
  currentIndex = -1;
  title = '';

  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.retrieveUsers();
	this.userService.listen().subscribe((m:any)=>{
		if (m==="Refresh")
			this.refreshList();
	})
  }

  retrieveUsers(): void {
    this.userService.getAll()
      .subscribe(
        data => {
          this.users = data;
          //console.log(data);
        },
        error => {
          console.log(error);
        });
  }

  refreshList(): void {
    this.retrieveUsers();
    this.currentUser = null;
    this.currentIndex = -1;
  }


  setActiveUser(user: any, index: any): void {
    this.currentUser = user;
    this.currentIndex = index;
  }

}
