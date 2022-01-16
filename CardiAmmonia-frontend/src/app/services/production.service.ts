import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Production } from '../model/production';
const API_URL = 'http://localhost:8080/api';
const httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  }
@Injectable({
  providedIn: 'root'
})
export class ProductionService {

  constructor(private httpClient: HttpClient) { }

  startProduction(): Observable<any> {
    return this.httpClient.post(API_URL + '/production/start', httpOptions)
    .pipe(
      catchError(this.errorHandler)
    )
  }  
  getProductionDetails(id:string): Observable<Production> {
    return this.httpClient.get<Production>(API_URL + '/production/'+id)
    .pipe(
      catchError(this.errorHandler)
    )
  }

  errorHandler(error:any) {
     let errorMessage = '';
     if(error.error instanceof ErrorEvent) {
       // Get client-side error
       errorMessage = error.error.message;
     } else {
       // Get server-side error
       errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
     }
     console.log(errorMessage);
     return throwError(errorMessage);
  }
}
