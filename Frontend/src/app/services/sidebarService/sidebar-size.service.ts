import { Injectable } from '@angular/core';
import {Subject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class SidebarSizeService {
  private subjectSize = new Subject<void>();

  sizeChanged$ = this.subjectSize.asObservable();

  notifySidebar() {
    this.subjectSize.next();
  }
}
