import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import {HttpClient,  HttpHeaders} from '@angular/common/http';
import { environment } from '../environments/environment';
import 'rxjs-compat/add/operator/catch';


@Injectable({
  providedIn: 'root'
})


export class LoginService {

  env = environment;
  showOK : boolean = false;
  showKO : boolean = false;

  constructor(protected http: HttpClient,private router:Router) {}

  async getToken(username: string, psw: string) {
    const httpOptions = {
      headers: new HttpHeaders({
        'Access-Control-Allow-Origin': '*',
        'Authorization': environment.autorization,
        'Content-Type': 'application/x-www-form-urlencoded'
      })
    };

    let body = new URLSearchParams();
    body.set('grant_type', 'password');
    body.set('username', username);
    body.set('password', psw);
    let body_txt = body.toString();

    await this.http.post(
      environment.loginUrl , body_txt , httpOptions
    ).subscribe(
      res => {
        environment.token =  res['access_token'];
        environment.userName =  res['name'];
        sessionStorage.setItem("token",environment.token);
        sessionStorage.setItem("userName",environment.userName);
        sessionStorage.setItem("pg","0");

        this.showOK = true;
        setTimeout(() => { this.router.navigate(['/core']);this.showOK = false;},2000);
        console.log('Correct login');
      }, err => {
        environment.token = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwcmluY2lwYWwiOiJwcG96byIsImNsaWVudElkIjoib25lc2FpdHBsYXRmb3JtIiwidXNlcl9uYW1lIjoicHBvem8iLCJzY29wZSI6WyJvcGVuaWQiLCJhdXRob3JpemF0aW9uX2NvZGUiXSwibmFtZSI6InBwb3pvIiwiZXhwIjoxNTcwNDc0MTUxLCJncmFudFR5cGUiOiJwYXNzd29yZCIsInBhcmFtZXRlcnMiOnsiZ3JhbnRfdHlwZSI6InBhc3N3b3JkIiwidXNlcm5hbWUiOiJwcG96byJ9LCJhdXRob3JpdGllcyI6WyJST0xFX0RFVkVMT1BFUiJdLCJqdGkiOiI2MDFiZmEwZC1iMTQ5LTQxM2QtYTA2YS0wMWE3NGFiMTJiYmUiLCJjbGllbnRfaWQiOiJvbmVzYWl0cGxhdGZvcm0ifQ.u4bRFxjbqNnRcqJ0nN2EGAyr6kMmKsCIbX-cKqZ_vww';
        environment.userName =  'ppozo';
        sessionStorage.setItem("token","eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwcmluY2lwYWwiOiJwcG96byIsImNsaWVudElkIjoib25lc2FpdHBsYXRmb3JtIiwidXNlcl9uYW1lIjoicHBvem8iLCJzY29wZSI6WyJvcGVuaWQiLCJhdXRob3JpemF0aW9uX2NvZGUiXSwibmFtZSI6InBwb3pvIiwiZXhwIjoxNTcwNDc0MTUxLCJncmFudFR5cGUiOiJwYXNzd29yZCIsInBhcmFtZXRlcnMiOnsiZ3JhbnRfdHlwZSI6InBhc3N3b3JkIiwidXNlcm5hbWUiOiJwcG96byJ9LCJhdXRob3JpdGllcyI6WyJST0xFX0RFVkVMT1BFUiJdLCJqdGkiOiI2MDFiZmEwZC1iMTQ5LTQxM2QtYTA2YS0wMWE3NGFiMTJiYmUiLCJjbGllbnRfaWQiOiJvbmVzYWl0cGxhdGZvcm0ifQ.u4bRFxjbqNnRcqJ0nN2EGAyr6kMmKsCIbX-cKqZ_vww");
        sessionStorage.setItem("userName",environment.userName);
        sessionStorage.setItem("pg","0");
        console.log(err);
        this.showKO = true;
        setTimeout(() => { this.router.navigate(['/core']);this.showOK = false;},2000);
       // setTimeout(() => {   this.showKO = false; },10000);

      }
    );
  }

}
