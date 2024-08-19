/*************************************************************************

 File:        app.component.ts
 Copyright:   (c) 2023 NazImposter

 Module-History:
 Date        Author                Reason
 20.11.2023  Matei Rares           Create component and navigation cases
 14.12.2023  Sebastian Pitica      Added paginator functionality specific elements

 **************************************************************************/

import {Component, OnDestroy, OnInit} from '@angular/core';
import {NavigationEnd, Router} from '@angular/router';
import {startWith, Subscription} from "rxjs";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit, OnDestroy {
  displayTopbar: boolean = false;
  displayWelcomeTopbar:boolean=false;
  displaySidebar: boolean = false;
  displayContact: boolean = false;
  routerSubscription!: Subscription;
  displayPaginator: boolean = false;

  constructor(private router: Router) {
  }

  ngOnInit(): void {
    this.routerSubscription = this.router.events.subscribe((event) => {

      if (event instanceof NavigationEnd) {

        switch (event.url) {
          case "/login":
          case "/sign-up":
          case "/welcome":
            this.controlDisplayComponents(false, false, true, false,true);
            break;
          case "/my-account":
            this.controlDisplayComponents(true, false, false, false,false);

            break;
          case "/all-files":
          case "/home":
          case "/deleted-files":
            this.controlDisplayComponents(true, true, false, true,false);
            break;
          default:
            if(event.url.startsWith("/all-files")){
              this.controlDisplayComponents(true, true, false, true,false);

            }
            else{
            this.controlDisplayComponents(false, false, false, false,false);}

            break;
        }
      }
    });
  }

  ngOnDestroy(): void {
    this.routerSubscription.unsubscribe()
  }

  controlDisplayComponents(topbar: boolean, sidebar: boolean, contact: boolean, paginator: boolean,welcomebar:boolean) {
    this.displayTopbar = topbar;
    this.displaySidebar = sidebar;
    this.displayContact = contact
    this.displayPaginator = paginator;
    this.displayWelcomeTopbar =welcomebar;


  }


  onToggle($event: boolean) {
    this.displaySidebar = $event;
  }
}
