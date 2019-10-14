// This file can be replaced during build by using the `fileReplacements` array.
// `ng build ---prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

import {PeriodicElement} from '../app/option/option.component';
export interface FileElement {
  fileId: string;
  name: string;
}



export const environment = {
  production: false,
  token: '',
  userName: '',
  autorization: 'Basic b25lc2FpdHBsYXRmb3JtOm9uZXNhaXRwbGF0Zm9ybQ==',
  loginUrl:  '/oauth-server/oauth/token',
  fileToken: '',
  uploadUrl: '/controlpanel/binary-repository',
  fieldUrl: '/api-manager/server/api/v1/tokenify/fields',
  dataFileID: '5d99f6b818b39b000cf1cc84',
  filePass: '',
  tokenfyUrl: '/api-manager/server/api/v1/tokenify/links/',
  haveFieldData: false,
  dataFile:  Blob
};

/*
 * In development mode, to ignore zone related error stack frames such as
 * `zone.run`, `zoneDelegate.invokeTask` for easier debugging, you can
 * import the following file, but please comment it out in production mode
 * because it will have performance impact when throw error
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
