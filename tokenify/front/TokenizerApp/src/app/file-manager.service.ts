import {Injectable} from '@angular/core';
import {environment} from '../environments/environment';
import {HttpClient, HttpHeaders} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class FileManagerService {
  env = environment;
  public file: File;
  fileUpload: boolean;
  uploadFailed: boolean;
  nameFileUploaded;

  constructor(protected http: HttpClient) {
    this.fileUpload = false;
  }

  postFile(fileToUpload: File) {

    const httpOptions = {
      headers: new HttpHeaders({
        'Authorization': 'Bearer ' + environment.token
      })
    };

    const endpoint = environment.uploadUrl;
    const formData: FormData = new FormData();
    this.nameFileUploaded = fileToUpload.name;
    formData.append('file', fileToUpload, fileToUpload.name);
    this.http.post(
      environment.uploadUrl, formData, httpOptions
    ).subscribe(
      res => {
        environment.fileToken = res.toString();
        console.log('Correct upload file', res);

      }, err => {
        environment.fileToken = err.error.text;
        console.log('Correct upload file', err);
        if (err.statusText === 'Created') {
          setTimeout(() => {
            this.uploadFailed = false;
            this.fileUpload = true;
            sessionStorage.setItem('nof', fileToUpload.name);
          }, 2000);
        } else if (err.statusText === 'Unauthorized') {
          setTimeout(() => {
            this.uploadFailed = true;
          }, 2000);
        }
      }
    );
  }
  // NOTE:  We leave these services, because they can be used in the future
  async modifyFile(fileToken: String, fileUpadted: File) {
    const httpOptions = {
      headers: new HttpHeaders({
        'Authorization': 'Bearer ' + environment.token
      })
    };
    const body = new FormData();
    body.append('repository', '');
    body.append('file', fileUpadted);
    await this.http.put(
      environment.uploadUrl + '/' + fileToken, body, httpOptions
    ).subscribe(
      res => {
        console.log('File modified');
      }, err => {
        console.log(err);
      }
    );
  }

  async deleteFile(fileToken: String, fileUpadted: File) {
    const httpOptions = {
      headers: new HttpHeaders({
        'Authorization': 'Bearer ' + environment.token
      })
    };
    await this.http.delete(
      environment.uploadUrl + '/' + fileToken, httpOptions
    ).subscribe(
      res => {
        console.log('File deleted');
      }, err => {
        console.log(err);
      }
    );
  }

}
