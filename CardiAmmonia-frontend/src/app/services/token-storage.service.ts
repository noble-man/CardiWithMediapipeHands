import { Injectable } from '@angular/core';
import * as CONSTANTS from '../utils/globals';

@Injectable({
  providedIn: 'root'
})
export class TokenStorageService {

  constructor() { }

  signOut(): void {
    window.sessionStorage.clear();
  }

  public saveToken(token: string): void {
    window.sessionStorage.removeItem(CONSTANTS.TOKEN_KEY);
    window.sessionStorage.setItem(CONSTANTS.TOKEN_KEY, token);
  }

  public getToken(): string | null {
    return window.sessionStorage.getItem(CONSTANTS.TOKEN_KEY);
  }

  public saveUser(user: any): void {
    window.sessionStorage.removeItem(CONSTANTS.USER_KEY);
    window.sessionStorage.setItem(CONSTANTS.USER_KEY, JSON.stringify(user));
  }

  public getUser(): any {
    const user = window.sessionStorage.getItem(CONSTANTS.USER_KEY);
    if (user) {
      return JSON.parse(user);
    }

    return {};
  }
}
