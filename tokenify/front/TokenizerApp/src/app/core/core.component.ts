import {Component, OnInit, ViewChild} from '@angular/core';
import { HttpClient, HttpEventType } from '@angular/common/http';
import { Router } from '@angular/router';
import { TokenifyService } from '../tokenify.service';
import {FileManagerService} from '../file-manager.service';
import {environment} from '../../environments/environment';
import {MatTable} from '@angular/material';
import {PeriodicElement} from '../option/option.component';

export interface FileElement {
  fileId: string;
  name: string;
}


@Component({
  selector: 'app-core',
  templateUrl: './core.component.html',
  styleUrls: ['./core.component.css']
})

export class CoreComponent implements OnInit {
  fileToUpload:  File;
  pressBtn: boolean;
  sessionuser = sessionStorage.getItem('userName');
  FILE_ELEMENT_DATA: FileElement[] = [];
  

  @ViewChild(MatTable, null) table: MatTable<PeriodicElement[]>;
  displayedColumns: string[] = ['fileName'];

  constructor(private http: HttpClient, private router: Router, public fileManagerService: FileManagerService,public tokenifyService: TokenifyService ) {
  }

  dataSource = this.FILE_ELEMENT_DATA;

  handleFileInput(files: FileList) {
    this.fileToUpload = files.item(0);
    this.fileManagerService.fileUpload = false;
    this.fileManagerService.uploadFailed = false;
    this.pressBtn = false;
    this.tokenifyService.fieldOk = false;
  }
  onSubmit() {
      if (this.fileToUpload) this.pressBtn = true;
      const formData = new FormData();
      environment.fileToken = null;
      this.fileManagerService.postFile(this.fileToUpload);
      setTimeout(() => {
        this.FILE_ELEMENT_DATA.push({fileId: '' + (this.FILE_ELEMENT_DATA.length + 1) , name: this.fileToUpload.name });
        this.table.renderRows();
      }, 60000);
  }

  nextToList() {
    sessionStorage.setItem("pg","1");
    this.router.navigate(['/option']);
  }

  ngOnInit() {
    if (sessionStorage.getItem("pg")!="0") {
      if(sessionStorage.getItem("token")){
        this.router.navigate(['/core']);
        this.fileManagerService.fileUpload = false;
        this.fileManagerService.uploadFailed = false;
      } else this.router.navigate(['core']);
    }
  }

  uploadFile() {
  }

  logout() {
    this.router.navigate(['/']);
  }
}
