import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {MatCardModule} from '@angular/material/card';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material';
import {MatButtonModule} from '@angular/material/button';
import { RouterModule, Routes } from '@angular/router';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {MatExpansionModule} from '@angular/material/expansion';
import { HttpClientModule } from '@angular/common/http';
import {MatTableModule} from '@angular/material/table';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {MatSelectModule} from '@angular/material/select';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatRadioModule} from '@angular/material/radio';

import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoginComponent } from './login/login.component';
import { CoreComponent } from './core/core.component';
import { HeaderComponent } from './header/header.component';
import { FooterComponent } from './footer/footer.component';
import { OptionComponent } from './option/option.component';
import { DownloadComponent } from './download/download.component';
import { LoginService } from './login.service';


const appRoutes: Routes = [
  { path: 'core', component: CoreComponent },
  { path: 'option', component: OptionComponent },
  { path: 'download', component: DownloadComponent },
  { path: '', component: LoginComponent, pathMatch: 'full' },
];

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    CoreComponent,
    HeaderComponent,
    FooterComponent,
    OptionComponent,
    DownloadComponent,
  ],
  imports: [
    BrowserModule,
    FormsModule,
    MatProgressBarModule,
    MatExpansionModule,
    MatSelectModule,
    MatRadioModule,
    MatTableModule,
    BrowserAnimationsModule,
    MatCheckboxModule,
    MatSlideToggleModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    NgbModule,
    HttpClientModule,
    MatButtonModule,
    RouterModule.forRoot(
      appRoutes,
      { enableTracing: true }
    )
  ],
  providers: [LoginService],
  bootstrap: [AppComponent]
})
export class AppModule {
}
