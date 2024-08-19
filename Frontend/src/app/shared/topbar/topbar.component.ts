/*************************************************************************

 File:        topbar.component.ts
 Copyright:   (c) 2023 NazImposter

 Module-History:
 Date        Author                Reason
 14.12.2023  Sebastian Pitica      Added toggle sidebar functionality and enter pressed event
 15.12.2023  Sebastian Pitica      Added future todos
 30.12.2023  Matei Rares           Added search functionality
 **************************************************************************/


import {Component, ElementRef, EventEmitter, OnDestroy, OnInit, Output, ViewChild} from '@angular/core';
import {NavigationEnd, Router} from '@angular/router';
import {AuthService} from 'src/app/services/auth_service/auth.service';
import {Subscription} from "rxjs";
import {BackendFSProvider} from "../../services/all-files/backend-fs-provider.service";

@Component({
  selector: 'app-topbar',
  templateUrl: './topbar.component.html',
  styleUrls: ['./topbar.component.scss']
})
export class TopbarComponent implements OnInit{
  toggleSideBar: boolean = true;
  @Output() onToggle = new EventEmitter<boolean>();
  routerSubscription!: Subscription;
  canToggle: boolean = true;
  public user_name!:string
  constructor(
    private backendFSProvider: BackendFSProvider,
    private authservice: AuthService,private router: Router) {
    this.backendFSProvider.getCurrentUser().subscribe((data: any) => {
      this.user_name=data.name
    }, console.log)  }

  ngOnInit(): void {
    this.routerSubscription = this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        switch (event.url) {
          case "/my-account":
            this.canToggle = false;
            break;
          default:
            this.canToggle = true;
            break;
        }
      }
    });
  }


  Logout() {
    this.authservice.Logout();

  }

  onToggleSideBar() {

    if(this.canToggle ==true){
      this.toggleSideBar = !this.toggleSideBar;
      this.onToggle.emit(this.toggleSideBar);
    }
  }
  @ViewChild('nameInput') nameInput!: ElementRef;

  onEnterPressed($event: any) {
    let fileName=$event.target.value

    this.router.navigate(['/all-files'],{queryParams:{data:fileName}});

  }
  onInputBlur() {
    this.nameInput.nativeElement.value = '';
  }
}
