/**************************************************************************
 File:        home.component.ts
 Copyright:   (c) 2023 NazImposter

 Module-History:
 Date        Author                Reason
 01.12.2023  Pasa Larisa          Refresh token testing functionality by rendering users restricted resource
 14.12.2023  Sebastian Pitica      Added functionalities for the component with paginator and backend communication
 16.12.2023  Sebastian Pitica      Added the functionality to display the recent files and folders for the current user, communication with the paginator and other utils functions
 27.12.2023  Matei Rares           Added functionality for upload file and create folder
 **************************************************************************/
import {Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Router} from '@angular/router';
import {AuthService} from 'src/app/services/auth_service/auth.service';
import {BackendFSProvider} from "../../services/all-files/backend-fs-provider.service";
import {PaginatorCommunicationService} from "../../services/comunication/paginator-communication.service";
import {Subscription} from "rxjs";
import {FsEntryJson} from "../../models/fs-entry-json";
import {FileDialogComponent} from "../../shared/file-dialog/file-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {RenameDialogComponent} from "../../shared/rename-dialog/rename-dialog.component";

let DEFAULT_PAGE_SIZE = 10;
let DEFAULT_PAGE_NUMBER = 1;

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit, OnDestroy {

  /* CONSTRUCTOR */
  constructor(
    public dialog: MatDialog,
    private router: Router,
    private backendFSProvider: BackendFSProvider,
    private communicationService: PaginatorCommunicationService,
    private service: AuthService) {
    this.backendFSProvider.getCurrentUser().subscribe((user: any) => {
        this.currentUser = user;
        this.setFSEntriesComponentsForUser(user.userId);

        this.backendFSProvider.getRootFsEntryId(this.currentUser.userId).subscribe((entry:any) => {
          this.userRootEntryId = entry.entryId
        })
      },
      console.log);

    this.pageChangeSubscription = this.communicationService.pageChange$.subscribe(({page, itemsPerPage}) => {
      this.updateDisplayedItemsPage(page, itemsPerPage);
    });

    this.communicationService.resetPageIndex();
  }

  /* VARIABLES */
  users: any[] = [];
  private currentUser: any;
  private pageChangeSubscription: Subscription;
  private allFsEntryJsons: FsEntryJson[] = []
  displayedFsEntryJsons: FsEntryJson[] = [];
  private currentPageNumber: number = DEFAULT_PAGE_NUMBER;
  private currentPageSize: number = DEFAULT_PAGE_SIZE;
  protected userRootEntryId: number = -1;

  /* FUNCTIONS */
  private updateDisplayedItemsPage(page: any, itemsPerPage: any) {
    this.displayedFsEntryJsons = this.allFsEntryJsons.slice((page - 1) * itemsPerPage, page * itemsPerPage);
  }

  private onTotalItemsNumberChange(totalItems: number) {
    this.communicationService.updateTotalItems(totalItems);
  }

  private setFSEntriesComponentsForUser(userId: number) {
    this.backendFSProvider.getRecentsFsEntries(userId).subscribe((fsEntryJsons: any) => {
        this.displayEntries(fsEntryJsons);
      },
      console.log);
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
    }).filter((fsEntryJson: FsEntryJson) => fsEntryJson.isInTrash == 0))
  }

  private sortFsEntryJsons(data: FsEntryJson[]) {
    return data.sort((a, b) => a.name < b.name ? -1 : 1)
      .sort((a, b) => a.isFile < b.isFile ? -1 : 1);
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

  ngOnDestroy(): void {
    this.pageChangeSubscription.unsubscribe();
  }

  ngOnInit() {
  }

  /////////////////////////////////////////////////


  openFile(item: any) {

    this.router.navigate(['/all-files'], {queryParams: {openIn: item.entryId}});//todo
  }

  @ViewChild('fileInput', {static: false}) fileInput!: ElementRef;

  addFile() {
    const inputElement: HTMLInputElement = this.fileInput.nativeElement;
    inputElement.click();
  }


  openRenameDialog(width: string, height: string, title: string) {
    return this.dialog.open(RenameDialogComponent, {width: width, height: height, data: {title: title}});
  }

  createFolder(): void {
    const dialogRef = this.openFileDialog("500px", "400px", "Select folder", this.currentUser.userId)
    dialogRef.afterClosed().subscribe(result => {
      if (result === undefined || result.result === "cancel") {
      } else if (result.result === "accept") {
        var newParentID = result.selectedEntryID === -1 ? this.userRootEntryId : result.selectedEntryID;

        const dialogRef2 = this.openRenameDialog("500px", "100px", "Name the folder")
        dialogRef2.afterClosed().subscribe(result2 => {
          if (result2 === undefined || result2.result === "cancel") {
          } else if (result2.result === "accept") {
            this.backendFSProvider.createFolder(newParentID, result2.data).subscribe((data: any) => {
              },
 console.log)
          }
        });
      }
    });
  }


  openFileDialog(width: string, height: string, title: string, currentUserId: number, currentItemID?: number) {
    return this.dialog.open(FileDialogComponent, {
      width: width,
      height: height,
      data: {title: title, currentUserId: currentUserId, currentItemID: currentItemID}
    });
  }

  onUploadFiles(event: any): void {
    const dialogRef = this.openFileDialog("500px", "400px", "Select folder", this.currentUser.userId)
    dialogRef.afterClosed().subscribe(result => {
      if (result === undefined || result.result === "cancel") {
      } else if (result.result === "accept") {
        let newParent = result.selectedEntryID === -1 ? this.userRootEntryId : result.selectedEntryID
        const fileList: FileList = event.target.files;
        if (fileList && fileList.length > 0) {
          for (let i = 0; i < fileList.length; i++) {
            this.backendFSProvider.uploadFile(newParent, fileList[i]).subscribe((data: any) => {
            }, console.log)
          }
        }
      }
    });
  }


}
