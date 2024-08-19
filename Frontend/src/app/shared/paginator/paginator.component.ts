/*************************************************************************

 File:        paginator.component.ts
 Copyright:   (c) 2023 NazImposter

 Module-History:
 Date        Author                Reason
 13.12.2023  Sebastian Pitica      Basic Structure
 14.12.2023  Sebastian Pitica      Added paginator functionality with events subscription and event emit
 15.12.2023  Sebastian Pitica      Added resetPageIndex functionality

 **************************************************************************/

import {Component, OnDestroy, ViewChild, ViewChildren} from '@angular/core';
import {PaginatorCommunicationService} from "../../services/comunication/paginator-communication.service";
import {Subscription} from "rxjs";
import {MatPaginator} from "@angular/material/paginator";

@Component({
  selector: 'app-paginator',
  templateUrl: './paginator.component.html',
  styleUrls: ['./paginator.component.scss']
})
export class PaginatorComponent implements OnDestroy {
  @ViewChild('paginator') paginator: MatPaginator | undefined;
  totalItems: number = 0;
  private totalItemsSubscription: Subscription;
  private pageResetIndexSubscription: Subscription;

  constructor(private communicationService: PaginatorCommunicationService) {
    this.totalItemsSubscription = this.communicationService.totalItems$.subscribe((totalItems: number) => {
      this.onTotalItemsNumberChange(totalItems);
    });
    this.pageResetIndexSubscription = this.communicationService.pageResetIndex$.subscribe((reset: boolean) => {
      if (reset) {
        this.onResetPageIndex();
      }
    });
  }

  onPageEvent(event: any) {
    this.communicationService.updatePageChange(event.pageIndex + 1, event.pageSize);
  }

  onTotalItemsNumberChange(totalItems: number) {
    this.totalItems = totalItems;
  }

  onResetPageIndex() {
    this.paginator?.firstPage();
  }

  ngOnDestroy(): void {
    this.totalItemsSubscription.unsubscribe()
    this.pageResetIndexSubscription.unsubscribe()
  }
}
