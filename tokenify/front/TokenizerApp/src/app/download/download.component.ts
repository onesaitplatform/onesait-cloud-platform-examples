import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TokenifyService } from '../tokenify.service';



@Component({
  selector: 'app-download',
  templateUrl: './download.component.html',
  styleUrls: ['./download.component.css']
})
export class DownloadComponent implements OnInit {
  constructor(private router: Router, public tokenifyService:TokenifyService ) { }
  nof:string;

  ngOnInit() {
    if (sessionStorage.getItem("pg")!="2") {
      if(sessionStorage.getItem("token")){
        this.router.navigate(['/core']);
      } else this.router.navigate(['core']);
    }
    this.nof = sessionStorage.getItem("nof")
  }

  returnTo(){
    this.router.navigate(['/']);
  }

  getTokenOnWindow() {
    if (!this.tokenifyService.se.includes("id")){
    window.open(this.tokenifyService.se);
    } else {
    window.open(this.tokenifyService.se1);
    }
  }

  downloadFile() {
    if (!this.tokenifyService.op.includes("id")) {
      window.open(this.tokenifyService.op);
    } else {
      window.open(this.tokenifyService.op1);
     }
  }

  goTo(url: string) {
    window.open(url);
  }
}
