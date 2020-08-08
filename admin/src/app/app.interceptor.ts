import {Injectable} from "@angular/core";
import {HttpClient, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {Observable} from "rxjs/index";
import {tap} from "rxjs/internal/operators";


@Injectable()
export class RefreshInterceptor implements HttpInterceptor{
  constructor(private http: HttpClient){

  }
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>>{
    return next.handle(req).pipe(
      tap(
        () => {},
        error => {
          console.log(error);
          if(error.status ===500 && error.error.message === 'refresh fail'){
            this.logout();
          }
        }
      )
    );
  }
  logout(){
    this.http.post('logout', {}).subscribe(
      () => {
        window.location.href = 'http://auth.security.liuyufeng.org:9090/logout?redirect_uri=http://admin.security.liuyuefeng.org:9001/'
      }
    );
  }
}
