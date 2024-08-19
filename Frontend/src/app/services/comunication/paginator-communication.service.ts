/**************************************************************************

 File:        paginator-communication.service.ts
 Copyright:   (c) 2023 NazImposter

 Module-History:
 Date        Author                Reason
 14.12.2023  Sebastian Pitica      Created basic structure for the service with communication functionalities
 15.12.2023  Sebastian Pitica      Added resetPageIndex functionality

 **************************************************************************/


import {Injectable} from "@angular/core";
import {BehaviorSubject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class PaginatorCommunicationService {
  private pageChangeSubject: BehaviorSubject<{ page: number; itemsPerPage: number }> = new BehaviorSubject<{
    page: number;
    itemsPerPage: number
  }>({
    page: 1,
    itemsPerPage: 10,
  });

  private totalItemsSubject: BehaviorSubject<number> = new BehaviorSubject<number>(0);
  private pageResetIndexSubject: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  pageChange$ = this.pageChangeSubject.asObservable();
  totalItems$ = this.totalItemsSubject.asObservable();
  pageResetIndex$ = this.pageResetIndexSubject.asObservable();

  updatePageChange(page: number, itemsPerPage: number) {
    this.pageChangeSubject.next({page, itemsPerPage});
  }

  updateTotalItems(totalItems: number) {
    this.totalItemsSubject.next(totalItems);
  }

  resetPageIndex() {
    this.pageResetIndexSubject.next(true);
  }
}
