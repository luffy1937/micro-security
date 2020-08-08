import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
//./npm install ngx-cookie-service --save
import {CookieService} from "ngx-cookie-service";
import { AppComponent } from './app.component';
import {FormsModule} from "@angular/forms";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {RefreshInterceptor} from "./app.interceptor";

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule
  ],
  providers: [
    CookieService,
    {provide: HTTP_INTERCEPTORS, useClass: RefreshInterceptor, multi: true}],
  bootstrap: [AppComponent]
})
export class AppModule { }
