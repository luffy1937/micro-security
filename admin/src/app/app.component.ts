import { Component } from '@angular/core';
import { HttpClient } from "@angular/common/http";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'angula';
  authenticated=false;
  credentials = {username: "liuyuefeng", password: "123"}
  order = {};
  constructor(private http: HttpClient){
    this.http.get('me').subscribe(
      data => {
        if(data){
          this.authenticated = true;
        }
        if(!this.authenticated){
          window.location.href =
            //认证服务器
            'http://auth.security.liuyuefeng.org:9090/oauth/authorize?'+
            //授权码认证
            'response_type=code&' +
            //客户端clientId
            'client_id=admin&' +
            //客户端服务9001
            'redirect_uri=http://admin.security.liuyuefeng.org:9001/oauth/callback&' +
            //记录授权前位置，授权完成后，据此回到
            'state=abc'
        }
      }
    );

  }

  getOrder(){
    this.http.get("api/order/orders/1").subscribe(
      data => {
        this.order = data;
      },
      () => {
        alert('get order fail');
      }
    );
  }

  login(){
    this.http.post("login", this.credentials).subscribe(()=>{
      this.authenticated = true;
    }, () =>{
      alert('login fail');
    });
  }

  logOut() {
    this.http.post('logout', this.credentials).subscribe(
      () => {
       /* this.authenticated = false;*/
       //退出时，也在认证服务器登出
        window.location.href="http://auth.security.liuyuefeng.org:9090/logout"

      }
      ,() => {alert('logout fail');}
    );
  }
}
