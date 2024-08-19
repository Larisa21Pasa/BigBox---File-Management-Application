/*************************************************************************

 File:        sidebar.component.ts
 Copyright:   (c) 2023 NazImposter

 Module-History:
 Date        Author                Reason
 14.12.2023  Sebastian Pitica      Create component
 30.12.2023  Matei Rares           Added style request for storage
 **************************************************************************/

import {Component} from '@angular/core';
import {BackendFSProvider} from "../../services/all-files/backend-fs-provider.service";
import {AuthService} from "../../services/auth_service/auth.service";
import {Router} from "@angular/router";
import {SidebarSizeService} from "../../services/sidebarService/sidebar-size.service";

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent {
  currentStorage!: number
  maxStorage!: number
  formatedCurrent!:string
  formattedMax!:string
  constructor(
    private sidebarNotification:SidebarSizeService,
    private backendFSProvider: BackendFSProvider
  ) {
    this.sidebarNotification.sizeChanged$.subscribe(()=>{
      this.getSizes()
    })

   this.getSizes()
  }

  getSizes(){
    this.backendFSProvider.getCurrentUser().subscribe((data: any) => {
      this.backendFSProvider.getUserStorage(data.userId).subscribe((stor: any) => {
          this.currentStorage = stor.current
          this.formatedCurrent=this.formatStorage(this.currentStorage)
          this.maxStorage = stor.max
          this.formattedMax=this.formatStorage(this.maxStorage)
        },
        console.log
      )
    }, console.log)
  }



  get percentageFilled(): number {
    if(this.currentStorage === undefined ){
      return 0;
    }
    return (this.currentStorage / this.maxStorage) * 100;
  }

  formatStorage(num:number){
    if(num >1_000_000_000 ){
      num=Number((num/1_000_000_000).toFixed(1))
      return num+" GB"
    }
    else if(num>1_000_000){
      num=Number((num/1_000_000).toFixed(1))
      return num+" MB"
    }
    else if(num>1_000){
      num=Number((num/1_000).toFixed(1))
      return num + " KB"
    }
    return num + " B"
  }


}
