import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {FileManagerService} from '../file-manager.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  constructor(private router: Router, public fileManagerService: FileManagerService) {
  }

  ngOnInit() {
  }

  ght() {
    if (sessionStorage.getItem('pg') !== '0') {
      if (sessionStorage.getItem('token')) {
        this.router.navigate(['/core']);
        this.fileManagerService.fileUpload = false;
        this.fileManagerService.uploadFailed = false;
      } else {
        this.router.navigate(['core']);
      }
    }
  }

}
