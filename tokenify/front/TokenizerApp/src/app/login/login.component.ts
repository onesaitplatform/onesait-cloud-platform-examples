import {Component, Injectable, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {environment} from '../../environments/environment';
import 'rxjs-compat/add/operator/map';
import {LoginService} from '../login.service';
import {FileManagerService} from '../file-manager.service';
import {NgbAlertConfig} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  providers: [NgbAlertConfig]
})

@Injectable()
export class LoginComponent implements OnInit {
  env = environment;
  username: string;
  psw: string;
  submitLogin = false;

  constructor(private router: Router, public loginService: LoginService, public fileManagerService: FileManagerService, alertConfig: NgbAlertConfig) {
    alertConfig.type = 'success';
    alertConfig.dismissible = false;
  }

  ngOnInit() {
    sessionStorage.removeItem('token');
    sessionStorage.removeItem('userName');
    sessionStorage.removeItem('pg');
    sessionStorage.removeItem('nof');
    this.fileManagerService.fileUpload = false;
    this.fileManagerService.uploadFailed = false;
  }

  login() {
    environment.token = null;
    this.submitLogin = true;
    if (this.username && this.psw) {
      this.loginService.getToken(this.username, this.psw);
    }
  }

  skipGuess() {

    this.loginService.getToken('anonymous', 'Anonymous2019!');
  }


}
