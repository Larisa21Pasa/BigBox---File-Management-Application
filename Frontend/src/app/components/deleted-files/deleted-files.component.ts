/**************************************************************************

 File:        deleted-files.component.ts
 Copyright:   (c) 2023 NazImposter

 Module-History:
 Date        Author                Reason
 14.12.2023  Sebastian Pitica      Created basic structure for the component with paginator and backend communication functionalities
 16.12.2023  Sebastian Pitica      Added the functionality to display the deleted files and folders for the current user, communication with the paginator and other utils functions
 26.12.2023  Matei Rares           Added restore, permanently delete,context menu and select table entry

 **************************************************************************/

import {Component, Input, OnDestroy, QueryList, ViewChildren} from '@angular/core';
import {BackendFSProvider} from "../../services/all-files/backend-fs-provider.service";
import {PaginatorCommunicationService} from "../../services/comunication/paginator-communication.service";
import {Subscription} from "rxjs";
import {FsEntryJson} from "../../models/fs-entry-json";
import {ExtendedMouseEvent} from "../../models/extended-mouse-event";
import {FsEntryComponent} from "../../shared/fs-entry/fs-entry.component";
import {RenameDialogComponent} from "../../shared/rename-dialog/rename-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {QuestionDialog} from "../../shared/question-dialog/question-dialog.component";
import {MatSnackBar} from "@angular/material/snack-bar";
import {SidebarSizeService} from "../../services/sidebarService/sidebar-size.service";

let DEFAULT_PAGE_SIZE = 10;
let DEFAULT_PAGE_NUMBER = 1;

@Component({
  selector: 'app-deleted-fs-entry',
  templateUrl: './deleted-files.component.html',
  styleUrls: ['./deleted-files.component.scss']
})
export class DeletedFilesComponent implements OnDestroy {
  private currentUser: any;
  private pageChangeSubscription: Subscription;
  private allFsEntryJsons: FsEntryJson[] = []
  displayedFsEntryJsons: FsEntryJson[] = [];
  private currentPageNumber: number = DEFAULT_PAGE_NUMBER;
  private currentPageSize: number = DEFAULT_PAGE_SIZE;

  constructor(
    private sidebarNotification:SidebarSizeService,
    private snackBar: MatSnackBar,
    public dialog: MatDialog,
    private backendFSProvider: BackendFSProvider,
    private communicationService: PaginatorCommunicationService
  ) {
    this.backendFSProvider.getCurrentUser().subscribe((user: any) => {
        this.currentUser = user;
        this.setFSEntriesComponentsForUser(user.userId);
      },
      console.log);

    this.pageChangeSubscription = this.communicationService.pageChange$.subscribe(({page, itemsPerPage}) => {
      this.updateDisplayedItemsPage(page, itemsPerPage);
    });

    this.communicationService.resetPageIndex();
  }

  private setFSEntriesComponentsForUser(userId: number) {
    this.backendFSProvider.getTrashFsEntries(userId).subscribe((fsEntryJsons: any) => {
        this.displayEntries(fsEntryJsons);
      },
      console.log);
  }

  ngOnDestroy(): void {
    this.pageChangeSubscription.unsubscribe();
  }

  private sortFsEntryJsons(data: FsEntryJson[]) {
    return data.sort((a, b) => a.name < b.name ? -1 : 1)
      .sort((a, b) => a.isFile < b.isFile ? -1 : 1);
  }

  private displayEntries(fsEntries: FsEntryJson[]) {
    this.convertToFsEntryJsons(fsEntries);
    this.refreshDisplayedItems();
    this.onTotalItemsNumberChange(this.allFsEntryJsons.length)
  }

  private convertToFsEntryJsons(jsonArray: any) {
    this.allFsEntryJsons = this.sortFsEntryJsons(jsonArray.map((json: any) => {
      return {
        name: json.name,
        createdDate: new Date(json.creationDate),
        isFile: Boolean(json.isFile),
        size: parseInt(json.size),
        entryId: parseInt(json.entryId),
        parentId: parseInt(json.parentId),
        isInTrash: this.convertDeleteStatus(json.deleteStatus),
        entryDescription: json.entryInformation
      };
    }).filter((fsEntryJson: FsEntryJson) => fsEntryJson.isInTrash == 1))
  }

  private convertDeleteStatus(deleteStatusString: string) {
    switch (deleteStatusString) {
      case "DELETED":
        return 1;
      case "NOT_DELETED":
        return 0;
      default:
        return -1;
    }
  }

  private refreshDisplayedItems() {
    this.updateDisplayedItemsPage(this.currentPageNumber, this.currentPageSize);
  }

  private updateDisplayedItemsPage(page: any, itemsPerPage: any) {
    this.displayedFsEntryJsons = this.allFsEntryJsons.slice((page - 1) * itemsPerPage, page * itemsPerPage);
  }

  private onTotalItemsNumberChange(totalItems: number) {
    this.communicationService.updateTotalItems(totalItems);
  }

  protected readonly JSON = JSON;

//////////////////////////////////////////////////////////


  handleRestore() {
    const dialogRef = this.dialog.open(QuestionDialog, {
      width: '500px',
      height: '100px',
      data: { question: "Do you want to recover " + this.currItem.name + " ?"}
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === undefined || result === "cancel") {
      } else if (result === "accept") {
        this.backendFSProvider.restoreEntry(this.currItem.entryId).subscribe((data: any) => {
            for (let i = 0; i < this.displayedFsEntryJsons.length; i++) {
              if (this.displayedFsEntryJsons[i].entryId === this.currItem.entryId) {
                this.displayedFsEntryJsons[i].isInTrash = 0;
                this.displayedFsEntryJsons.splice(i, 1)
              }
            }
          },
          console.log)
      }
    });
  }

  handleDelete() {
    const dialogRef = this.dialog.open(QuestionDialog, {
      width: '500px',
      height: '100px',
      data: {title: "Delete files", question: "Do you want to permanently delete " + this.currItem.name + " ?"}
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === undefined || result === "cancel") {
      } else if (result === "accept") {

        this.backendFSProvider.deleteEntry(this.currItem.entryId).subscribe((data: any) => {
            for (let i = 0; i < this.displayedFsEntryJsons.length; i++) {
              if (this.displayedFsEntryJsons[i].entryId === this.currItem.entryId) {
                this.displayedFsEntryJsons[i].isInTrash = 0;
                this.displayedFsEntryJsons.splice(i, 1)
              }

            }
            this.sidebarNotification.notifySidebar()

          },
          console.log)

      }
    });
  }

  selectedRow: number | null = null;

  onRowClick(event: MouseEvent, entry: FsEntryJson, index: number): void {
    this.currItem = entry
    this.selectedRow = index;
    this.showOptions = false;
    event.stopPropagation()
  }

  currItem: any

  dbClickRestore(event: MouseEvent, item: any): void {
    this.handleRestore()
    event.stopPropagation()
  }

  optionsX!: number;
  optionsY!: number;
  showOptions: boolean = false;

  openCustomContext(event: MouseEvent, entry: FsEntryJson, index: number) {
    this.currItem = entry
    this.selectedRow = index;
    this.optionsX = event.x
    this.optionsY = event.y
    this.showOptions = true;

    event.preventDefault()
    event.stopPropagation()
  }

  unfocusFiles(event: MouseEvent) {
    event.preventDefault();
    this.selectedRow = -1;
    this.showOptions = false;
  }
}
