import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {environment} from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class TokenifyService {
  private resultado;
  private pass: string;
  public fields: string[];
  public value;
  favoriteTk: string;
  se: string;
  op: string;
  se1: string;
  op1: string;
  fieldOk: boolean;

  constructor(protected http: HttpClient) {
  }

  async putTokenifyLinks(idFile: string, flags: bigint[], method: string) {
    switch (this.favoriteTk) {
      case 'Format-preserving encryption (FPE)':
        method = 'FPE';
        break;
      case 'AES Encrpytion (AES)':
        method = 'AES';
        break;
      case 'Random map (MAP)':
        method = 'MAP';
        break;
    }
    const httpOptions = {
      headers: new HttpHeaders({
        // tslint:disable-next-line:max-line-length
        'Authorization': 'Bearer ' + environment.token,
        'Accept': '*/*'
      })
    };

    let bodyp = {};
    let body = {};
    bodyp['user'] = environment.userName;
    bodyp['USER_TOKEN'] = 'Bearer ' + environment.token;
    bodyp['file_id'] = idFile;
    bodyp['method'] = method;
    bodyp['flags'] = this.value;
    body['params'] = bodyp;

    await this.http.post(
      environment.tokenfyUrl, body, httpOptions
    ).subscribe(
      res => {
        let resp = res['body']['results']['msg'][0]['data'];
        this.getLinkOut(resp);
        console.log('Correct: links obtained successful ');

      }, err => {
        this.resultado = null;
        console.log(err);
      }
    );
  }

  async listTokenifyFields(idFile: String) {
    const httpOptions = {
      headers: new HttpHeaders({
        // tslint:disable-next-line:max-line-length
        'Authorization': 'Bearer ' + environment.token,
        'Accept': '*/*'
      })
    };

    const bodys = {
      params: {
        user: environment.userName,
        USER_TOKEN: 'Bearer ' + environment.token,
        file_id: idFile
      }
    };

    let bodyp = {};
    let body = {};
    bodyp['user'] = environment.userName;
    bodyp['USER_TOKEN'] = 'Bearer ' + environment.token;
    bodyp['file_id'] = idFile;
    bodyp['separator'] = ',';
    body['params'] = bodyp;


    await this.http.post(
      environment.fieldUrl, body, httpOptions
    ).subscribe(
      res => {
        let resp = res['body']['results']['msg'][0]['data'];
        this.getFields(resp);
        this.fieldOk = true;
      }, err => {
        console.log(err);
      }
    );

    return this.resultado;
  }

  private getFields(resp) {

    let jsonF = JSON.stringify(resp);
    jsonF = jsonF.substring(jsonF.indexOf('\'fields\': '), jsonF.indexOf('}'));
    jsonF = jsonF.substring(jsonF.indexOf('['));
    jsonF = jsonF.replace(/'/g, '');
    jsonF = jsonF.replace(/ /g, '');
    jsonF = jsonF.replace('\\n', '');
    jsonF = jsonF.replace('[', '').replace(']', '');

    this.fields = jsonF.split(',');
    console.log('body', this.fields);

  }

  private getLinkOut(resp) {

    var data = this.cleanResponse(resp);

    this.getSecret(data);

    this.getOutput(data);
  }

  private getOutput(data) {
    var output = data.split(',')[2];
    output = output.replace('\'output\': ', '');
    output = output.replace('\'', '');
    output = output.replace('\'', '');
    output = output.replace(' ', '');
    output = output.replace(' ', '');
    console.log(output);
    this.op = output;
    this.op1 = output;
  }

  private getSecret(data) {
    var secret = data.split(',');
    secret = secret[0].replace('secret\': \'', '');
    secret = secret.replace('\'', '');
    this.se = secret;
    this.se1 = secret;
    console.log(secret);
  }

  cleanResponse(dataComplete: string) {
    var data = dataComplete.replace('\n(True, \'{"message":"Disconnected"}\')\n', ' ');
    data = data.replace('\'', '');
    data = data.replace('Info - IotBrokerClient will be soon deprecated, please use DigitalClient instead\n', '');
    data = data.replace('{', '');
    data = data.replace('}', '');
    return data;
  }


}
