/**************************************************************************

 File:        all-files.component.ts
 Copyright:   (c) 2023 NazImposter

 Module-History:
 Date        Author                Reason
 30.11.2023  Sebastian Pitica      Added most of the functionality (key events, mouse events, handling of events, etc)
 01.12.2023  Sebastian Pitica      Patched some bugs in regards to selection and all related functionality, refactored some code, snackbar function
 14.12.2023  Sebastian Pitica      Added backend communication and paginator functionalities, added openIn parameter for parametrized all files component, and other changes
 15.12.2023  Sebastian Pitica      Removed test cases for allFsEntryJsons and added backend communication for fetching the data and getting the current user, added open functionality, added reset page index, added future todos
 16.12.2023  Sebastian Pitica      Updated set displayed fs entries components, and added function for go back button with updates on other functions as well for this, added updates in regards to target parent id field
 26.12.2023  Matei Rares           Added upload, download, rename, copy, move,drag file, back events and breadcrumb
 12.01.2023  Tudor Toporas         Fixed download
 13.01.2023  Matei Rares           Refixed download final

 **************************************************************************/

import {
  ChangeDetectorRef,
  Component,
  ElementRef,
  HostListener,
  Inject,
  Input, NgZone,
  OnDestroy,
  OnInit,
  QueryList,
  ViewChildren
} from '@angular/core';
import {FsEntryJson} from "../../models/fs-entry-json";
import {FsEntryComponent} from "../../shared/fs-entry/fs-entry.component";
import {ExtendedMouseEvent} from "../../models/extended-mouse-event";
import {BackendFSProvider} from "../../services/all-files/backend-fs-provider.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {ActivatedRoute} from "@angular/router";
import {Subscription} from "rxjs";
import {PaginatorCommunicationService} from "../../services/comunication/paginator-communication.service";
import {MAT_DIALOG_DATA, MatDialog} from "@angular/material/dialog";
import * as JSZip from 'jszip';
import {FileDialogComponent} from "../../shared/file-dialog/file-dialog.component";
import {RenameDialogComponent} from "../../shared/rename-dialog/rename-dialog.component";
import {QuestionDialog} from "../../shared/question-dialog/question-dialog.component";
import {SidebarSizeService} from "../../services/sidebarService/sidebar-size.service";


let ID = 0;
let INDEX = 1;
let PARENT_ID = 2;
let DEFAULT_PAGE_SIZE = 20;
let DEFAULT_PAGE_NUMBER = 1;

@Component({
  selector: 'app-all-files',
  templateUrl: './all-files.component.html',
  styleUrls: ['./all-files.component.scss'],
})
export class AllFilesComponent implements OnInit, OnDestroy {

  /////////////////////////////////////////////////

  protected readonly JSON = JSON;
  private pageChangeSubscription: Subscription;
  @ViewChildren(FsEntryComponent) fsEntries: QueryList<FsEntryComponent> | undefined;
  @Input() contextmenu = false;
  isProgressBarDisplayed = false;
  contextmenuX = 0;
  contextmenuY = 0;
  fsComponentTarget: FsEntryComponent | undefined;
  private firstSelectedFsComponent = new Array<number>();
  private secondSelectedFsComponent = new Array<number>();
  private currentUser: any;
  private currentPageNumber: number = DEFAULT_PAGE_NUMBER;
  private currentPageSize: number = DEFAULT_PAGE_SIZE;
  protected allFsEntryJsons: FsEntryJson[] = [];
  displayedFsEntryJsons: FsEntryJson[] = [];
  protected currentDirId: number = -1;
  protected parentDirId: number = -1;
  rootId: number = 1;

  /////////////////////////////////////////////////


  constructor(private sidebarNotification: SidebarSizeService,
              private backendFSProvider: BackendFSProvider,
              private snackBar: MatSnackBar,
              private route: ActivatedRoute,
              private communicationService: PaginatorCommunicationService,
              public dialog: MatDialog
  ) {
    this.pageChangeSubscription = this.communicationService.pageChange$.subscribe(({page, itemsPerPage}) => {
      this.updateDisplayedItemsPage(page, itemsPerPage);
    });
    this.communicationService.resetPageIndex();
    let navData!: string
    this.route.queryParams.subscribe(params => {
      this.breadCrumb=""
      navData = params["data"]
      this.backendFSProvider.getCurrentUser().subscribe(
        (currentUser: any) => {
          this.backendFSProvider.getRootFsEntryId(currentUser.userId).subscribe((entry: any) => {
            this.rootId = entry.entryId

            if (navData !== undefined) {
              this.backendFSProvider.searchFiles(navData, currentUser.userId).subscribe(
                (response: any) => {
                  const fsEntries: FsEntryJson[] = response
                  this.displayEntries(fsEntries)
                  this.displayedFsEntryJsons = this.allFsEntryJsons
                },
                console.log
              )
            } else if (navData === undefined) {
              let openInId = params["openIn"]
              if (openInId === undefined) {
                openInId = Number(this.route.snapshot.data['openIn']);
                this.populateUIWithContentFrom(openInId);

              } else {
                this.backendFSProvider.getFileProps(openInId).subscribe((file: any) => {
                  this.populateUIWithContentFrom(openInId);
                  this.updateBreadCrumb(file.name)
                }, console.log)
              }

            }
          }, console.log)

        });
    })

  }

  private sortFsEntryJsons(data: FsEntryJson[]) {
    return data.sort((a, b) => a.name < b.name ? -1 : 1)
      .sort((a, b) => a.isFile < b.isFile ? -1 : 1);
  }

  private refreshDisplayedItems() {
    this.updateDisplayedItemsPage(this.currentPageNumber, this.currentPageSize);
  }

  private updateDisplayedItemsPage(pageNumber: number, itemsPerPage: number) {
    this.currentPageNumber = pageNumber;
    this.currentPageSize = itemsPerPage;
    this.displayedFsEntryJsons = this.allFsEntryJsons.slice((pageNumber - 1) * itemsPerPage, pageNumber * itemsPerPage);
  }


  private onTotalItemsNumberChange(totalItems: number) {
    this.communicationService.updateTotalItems(totalItems);
  }

  unfocusFiles(event: MouseEvent) {
    event.preventDefault();
    this.contextmenu = false
  }

  navigatedData: any;

  ngOnInit(): void {

  }

  private populateUIWithContentFrom(openInId: number) {
    if (this.currentUser == undefined) {
      this.backendFSProvider.getCurrentUser().subscribe(
        (currentUser: any) => this.onUserGetSuccess(currentUser, openInId),
        console.log);
    } else {
      this.onUserGetSuccess(this.currentUser, openInId)
    }
  }

  private onUserGetSuccess(data: any, openInId: number) {
    this.currentUser = data;
    if (this.currentUser) {
      openInId == -1 ? this.setFsEntriesComponentsFromRootForUId(this.currentUser.userId) : this.setFsEntriesComponentsFromDirWithId(openInId);
    }
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

  private setFsEntriesComponentsFromDirWithId(dirId: number) {
    if (dirId === -1) {
      this.setFsEntriesComponentsFromRootForUId(this.currentUser.userId);
      return;
    }

    this.parentDirId = this.currentDirId; //todo remove this line with what backend will deliver when will be the case
    this.currentDirId = dirId; //todo watch out for this as well

    this.toggleProgressBar()
    this.backendFSProvider.getFsEntries(dirId).subscribe((fsEntries: any) => {
        this.displayEntries(fsEntries)
        this.toggleProgressBar()
      },
      console.log)
  }

  private setFsEntriesComponentsFromRootForUId(userId: number) {
    this.parentDirId = -1 //todo watch out for this as well
    this.currentDirId = -1 //todo watch out for this as well

    this.toggleProgressBar()

    this.backendFSProvider.getRootFsEntryId(userId).subscribe((data: any) => {
        this.backendFSProvider.getFsEntries(data.entryId).subscribe((homeFsEntries: any) => {
            this.displayEntries(homeFsEntries)
            this.toggleProgressBar()
          },
          console.log)
      },
      console.log)
  }


  private displayEntries(fsEntries: FsEntryJson[]) {
    this.convertToFsEntryJsons(fsEntries);
    this.refreshDisplayedItems();
    this.onTotalItemsNumberChange(this.allFsEntryJsons.length)
  }

  private displayProgressBar() {
    this.isProgressBarDisplayed = true;
  }

  private hideProgressBar() {
    this.isProgressBarDisplayed = false;
  }

  toggleProgressBar() {
    this.isProgressBarDisplayed = !this.isProgressBarDisplayed;
  }

  ngOnDestroy(): void {
    this.pageChangeSubscription.unsubscribe();
  }

  @HostListener('document:keydown', ['$event'])
  handleKeyboardEvent(event: KeyboardEvent): void {
    const focusedElement = document.activeElement;
    const isInputFocused = focusedElement instanceof HTMLInputElement;
    if (isInputFocused) {
      return;
    }
    const selectedComponents = this.getSelectedComponents();
    const keyActions: { [key: string]: () => void } = {
      'a': () => event.ctrlKey && this.onCtrlA(),
      'ArrowUp': () => this.handleArrowKeyWrap(-1, selectedComponents),
      'ArrowDown': () => this.handleArrowKeyWrap(1, selectedComponents),
      'Delete': () => this.handleDelete(selectedComponents),
      'Enter': () => this.handleEnter(selectedComponents),
    };
    if ((keyActions as { [key: string]: () => void })[event.key]) { //if key is in keyActions
      (keyActions as { [key: string]: () => void })[event.key](); //call the function
      event.preventDefault();
    }
  }

  private openSnackBar(message: string, action: string) {
    this.snackBar.open(message, action);
  }

  private handleArrowKeyWrap(direction: number, selectedComponents: FsEntryComponent[]): void {
    if (selectedComponents.length > 1) {
      this.removeSelectionForAllFsComponentsExcept(this.firstSelectedFsComponent[ID]);
    }
    this.handleArrowKey(direction);
  }

  private handleArrowKey(direction: number) {
    if (this.fsEntries == undefined) return;
    const fsEntriesArray = this.fsEntries.toArray();
    const currentIndex = fsEntriesArray.findIndex(fsEntry => fsEntry.isSelected);
    const totalEntries = fsEntriesArray.length;
    let newIndex = direction === 1 ? 0 : totalEntries - 1;
    if (currentIndex !== -1) {
      fsEntriesArray[currentIndex].isSelected = false;
      newIndex = (currentIndex + direction + totalEntries) % totalEntries;
    }
    const newSelectedEntry = fsEntriesArray[newIndex];
    newSelectedEntry.isSelected = true;
    this.firstSelectedFsComponent[ID] = <number>newSelectedEntry.entryId;
    this.firstSelectedFsComponent[INDEX] = <number>newSelectedEntry.index;
    this.firstSelectedFsComponent[PARENT_ID] = <number>newSelectedEntry.parentId;
  }

  private handleDelete(selectedComponents: FsEntryComponent[]): void {
    if (selectedComponents.length == 0) {
      this.openSnackBar("Nothing selected for delete", "OK");
      return
    }
    //todo if length >1 delete multiple
    const dialogRef = this.dialog.open(QuestionDialog, {
      width: '500px',
      height: '100px',
      data: {title: "Move to trash", question: "Send this files to trash ?"}
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result === undefined || result === "cancel") {
      } else if (result === "accept") {
        selectedComponents.forEach((elem, index) => {
          // this.onDelete(new ExtendedMouseEvent(undefined, `{"target": "${elem.entryId}", "index":"${elem.index}", "parentTarget":"${elem.parentId}"}`));
          this.backendFSProvider.deleteEntry(elem.entryId!).subscribe(
            (data: any) => {
              if (index === 0) {
                let dir = this.currentDirId === -1 ? this.rootId : this.currentDirId

                this.setFsEntriesComponentsFromDirWithId(dir)
              }

            },
            console.log
          )
        });
      }
    });
  }

  private handleEnter(selectedComponents: FsEntryComponent[]): void {
    if (selectedComponents.length > 1) {
      this.removeSelectionForAllFsComponentsExcept(this.firstSelectedFsComponent[ID]);
      return;
    }
    if (this.firstSelectedFsComponent[ID] !== undefined) {
      this.onOpen(new ExtendedMouseEvent(undefined, `{"target": "${this.firstSelectedFsComponent[ID]}", "index":"${this.firstSelectedFsComponent[INDEX]}", "parentTarget":"${this.firstSelectedFsComponent[PARENT_ID]}"}`));
    } else {
      this.openSnackBar("Nothing selected for open", "OK");
    }
  }

  private onCtrlA(): void {
    if (this.fsEntries !== undefined) {
      let newSelected = !this.fsEntries.toArray().every((fsEntry) => fsEntry.isSelected);
      this.fsEntries.forEach((element) => (element.isSelected = newSelected));
    }
  }

  private getSelectedComponents(): FsEntryComponent[] {
    return this.fsEntries?.filter(fsEntry => fsEntry.isSelected) || [];
  }

  private removeSelectionForAllFsComponentsExcept(id: number) {
    const selectedComponentsLength = this.getSelectedComponents().length;
    this.fsEntries?.forEach((fsEntry) => {
      if (fsEntry.entryId != id) {
        fsEntry.isSelected = false;
      } else {
        if (selectedComponentsLength == 1) {
          fsEntry.isSelected = !fsEntry.isSelected;
        } else {
          fsEntry.isSelected = true;
        }
      }
    });
  }

  onRightClick(event: ExtendedMouseEvent) {
    this.removeSelectionForAllFsComponentsExcept(event.targetId);
    if (event.originalEvent !== undefined) {
      event.originalEvent.stopPropagation()
      this.fsComponentTarget = this.getFsEntryComponentById(event.targetId);
      this.enableContextMenu(event.originalEvent);
    }
  }

  onLeftClick(event: ExtendedMouseEvent) {
    if (event.originalEvent == undefined) return;

    event.originalEvent.preventDefault();
    this.disableContextMenu();
    this.disableUpdateChoice();

    if (event.originalEvent.ctrlKey) {
    } else this.removeSelectionForAllFsComponentsExcept(event.targetId);

    if (event.originalEvent.shiftKey) {
      this.handleShiftClick(event);
    } else {
      this.handleNormalClick(event);
    }
  }

  private handleShiftClick(event: ExtendedMouseEvent) {
    if (this.firstSelectedFsComponent[ID]) {
      this.secondSelectedFsComponent[ID] = event.targetId;
      this.secondSelectedFsComponent[INDEX] = event.targetIndex;
      this.secondSelectedFsComponent[PARENT_ID] = event.targetParentId;
      this.selectRangeOfFsComponents();
    } else {
      this.firstSelectedFsComponent[ID] = event.targetId;
      this.firstSelectedFsComponent[INDEX] = event.targetIndex;
      this.firstSelectedFsComponent[PARENT_ID] = event.targetParentId;
    }
  }

  private handleNormalClick(event: ExtendedMouseEvent) {
    const elem = this.fsEntries?.find((elem) => elem.entryId == event.targetId);
    if (elem == undefined) return;

    //if the element is already selected deselect it, select it otherwise
    elem.isSelected ? this.firstSelectedFsComponent[ID] = <number>elem.entryId : delete this.firstSelectedFsComponent[ID];
    elem.isSelected ? this.firstSelectedFsComponent[INDEX] = <number>elem.index : delete this.firstSelectedFsComponent[INDEX];
    elem.isSelected ? this.firstSelectedFsComponent[PARENT_ID] = <number>elem.parentId : delete this.firstSelectedFsComponent[PARENT_ID];

    //if there is a multi selection and the user deselects the firstSelected element choose the secondSelected element as firstSelected
    let selectedComponents = this.getSelectedComponents();
    if (this.firstSelectedFsComponent[ID] === undefined && this.firstSelectedFsComponent[INDEX] === undefined && selectedComponents.length > 0) {
      this.firstSelectedFsComponent[ID] = <number>selectedComponents[0].entryId;
      this.firstSelectedFsComponent[INDEX] = <number>selectedComponents[0].index;
      this.firstSelectedFsComponent[PARENT_ID] = <number>selectedComponents[0].parentId;
    }

    delete this.secondSelectedFsComponent[ID];
    delete this.secondSelectedFsComponent[INDEX];
    delete this.secondSelectedFsComponent[PARENT_ID];
  }

  private selectRangeOfFsComponents() {
    const startIndex = this.firstSelectedFsComponent[INDEX];
    const endIndex = this.secondSelectedFsComponent[INDEX];
    if (startIndex === undefined || endIndex === undefined || !this.fsEntries) return;
    const [start, end] = [Math.min(startIndex, endIndex), Math.max(startIndex, endIndex)];
    this.fsEntries.forEach((fsEntry, index) => {
      fsEntry.isSelected = (index >= start && index <= end);
    });
  }

  private getFsEntryComponentById(number: number): FsEntryComponent | undefined {
    return this.fsEntries?.find((fsEntry) => fsEntry.entryId == number);
  }

  onOpen(event: ExtendedMouseEvent) {
    let item!: any
    for (let elem of this.displayedFsEntryJsons) {
      if (elem.entryId == event.targetId) {
        item = elem;
      }
    }

    if (item.isFile) {
      this.downloadAction(item.entryId)
    } else {
      this.navigationHistoryNames.push(item.name);
      this.updateBreadCrumb()
      this.navigationHistory.push(event.targetParentId);

      this.disableContextMenu();
      this.setFsEntriesComponentsFromDirWithId(event.targetId);
    }
  }

  private onDelete(extendedMouseEvent: ExtendedMouseEvent) {
    this.disableContextMenu();
    const dialogRef = this.dialog.open(QuestionDialog, {
      width: '500px', height: '100px',
      data: {title: "Move to trash", question: "Send this files to trash ?"}
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result === undefined || result === "cancel") {
      } else if (result === "accept") {
        let [_, entryID, parentID] = extendedMouseEvent.additionalInfo.split(" ");
        const id: number = parseInt(entryID.replace("\"", ""))
        for (let i = 0; i < this.displayedFsEntryJsons.length; i++) {
          if (this.displayedFsEntryJsons[i].entryId === id) {
            this.displayedFsEntryJsons.splice(i, 1)
          }
        }
        this.backendFSProvider.deleteEntry(id).subscribe(
          console.log, console.log)
      }
    })
  }

  private disableContextMenu() {
    this.contextmenu = false;
  }

  private enableContextMenu(event: MouseEvent) {
    event.preventDefault();
    this.contextmenuX = event.clientX
    this.contextmenuY = event.clientY
    this.contextmenu = true;
  }

  protected readonly event = event;

  onOptionSelect(event: string) {
    this.disableContextMenu();
    let [action, entryId, parentId] = event.split(" ");
    const entryID: number = parseInt(entryId.replace("\"", ""))
    const parentID: number = parseInt(entryId.replace("\"", ""))
    switch (action) {
      case "Open":
        this.onOpen(new ExtendedMouseEvent(undefined, `{"target": "${entryID}", "index":"", "parentTarget":"${parentID}"}`));
        break;
      case "Delete":
        this.onDelete(new ExtendedMouseEvent(undefined, `{"target": "${entryID}", "index":"", "parentTarget":"${parentID}"}`));
        break;
      case "Rename":
        this.renameAction(entryID, parentID);
        break;
      case "Move":
        this.moveAction(entryID, parentID);
        break;
      case "Make_a_copy":
        this.copyAction(entryID, parentID);
        break;
      case "Description":
        this.descriptionAction(entryID, parentID);
        break;
      case "Download":
        this.downloadAction(entryID, parentID);
        break;
    }
  }


  ///////////////////////////////////////////////////////////////
  protected navigationHistory: number[] = [-1];
  protected navigationHistoryNames: string[] = ["root"];
  protected mouseEnter = false;
  protected breadCrumb: string = "";

  updateBreadCrumb(name?: any) {
    this.breadCrumb = "";

    if (this.navigationHistoryNames.at(-1) !== "root") {
      for (let nav of this.navigationHistoryNames) {
        if (nav === "root") continue;
        this.breadCrumb += nav + "/";
      }
    } else {
      this.breadCrumb += "";
    }

    if (name !== undefined) {
      this.breadCrumb += "/" + name
    }
  }

  handleMouseUp(event: MouseEvent): void {
    if (event.button === 3 && this.mouseEnter) {
      event.preventDefault();
      this.goBack()
    }
  }

  goBack(): void {
    if (this.navigationHistory.at(-1) === -1) {
      this.navigationHistoryNames.pop();
      this.updateBreadCrumb()
      this.setFsEntriesComponentsFromRootForUId(this.currentUser.userId);
      return;
    } else {
      this.navigationHistoryNames.pop();
      this.updateBreadCrumb()
      let id = this.navigationHistory.pop();
      this.setFsEntriesComponentsFromDirWithId(id!);
    }
  }

///////////////////////////////FILE_MAG////////////////////////////////


  renameAction(entryID: number, parentID: number) {
    const dialogRef = this.openRenameDialog("500px", "100px", "Rename")
    dialogRef.afterClosed().subscribe(result => {
      if (result === undefined || result.result === "cancel") {
      } else if (result.result === "accept") {
            let updatedEntry:any
            let index:number

            for (let i = 0; i < this.displayedFsEntryJsons.length; i++) {
              if (this.displayedFsEntryJsons[i].entryId === entryID) {
                 updatedEntry = {...this.displayedFsEntryJsons[i]};
                if(this.displayedFsEntryJsons[i].isFile){
                  if(result.data.includes(".")){
                    updatedEntry.name=result.data
                  }
                  else{
                    const oldName=this.displayedFsEntryJsons[i].name
                    const lastDotIndex = oldName.lastIndexOf(".");
                    const fileType = lastDotIndex !== -1 ? oldName.substring(lastDotIndex + 1) : '';
                    updatedEntry.name= fileType !== "" ?  result.data +"."+fileType : result.data;
                  }
                }
                else{
                  updatedEntry.name = result.data;
                }
                index=i;
                break;
              }
            }
        this.backendFSProvider.renameEntry(entryID, updatedEntry.name).subscribe((data: any) => {
            this.displayedFsEntryJsons[index] = updatedEntry;
          },
          console.log)

      }
    });

  }

  descriptionAction(entryID: number, parentID: number): void {
    const dialogRef = this.openRenameDialog("500px", "100px", "Update Description")
    dialogRef.afterClosed().subscribe(result => {
      if (result === undefined || result.result === "cancel") {
      } else if (result.result === "accept") {
        this.backendFSProvider.updateEntryInfo(entryID, result.data).subscribe((data: any) => {
            for (let i = 0; i < this.displayedFsEntryJsons.length; i++) {
              if (this.displayedFsEntryJsons[i].entryId === entryID) {
                const updatedEntry = {...this.displayedFsEntryJsons[i]};
                updatedEntry.entryDescription = result.data;
                this.displayedFsEntryJsons[i] = updatedEntry;
              }
            }
          },
          console.log
        )

      }
    });
  }


  moveAction(entryID: number, parentID: number): void {
    const dialogRef = this.openFileDialog("500px", "400px", "Select folder", this.currentUser.userId, entryID)
    dialogRef.afterClosed().subscribe(result => {
      if (result === undefined || result.result === "cancel") {
      } else if (result.result === "accept") {
        let currentDir=this.currentDirId === -1 ? this.rootId:this.currentDirId;
          var newParentID = result.selectedEntryID === -1 ? this.rootId : result.selectedEntryID;
        if (Number(newParentID) === Number(currentDir)) { //for some reason this.currentDirId e un number vazut ca un string aici
          this.openSnackBar("Trebuie sa alegi alt folder decat cel curent", "OK");
        } else {

          this.backendFSProvider.moveEntry(entryID, newParentID).subscribe((data: any) => {
              for (let i = 0; i < this.displayedFsEntryJsons.length; i++) {
                if (this.displayedFsEntryJsons[i].entryId === entryID) {
                  this.displayedFsEntryJsons.splice(i, 1)
                  break;
                }
              }
            },
            console.log
          )
        }
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

  copyAction(entryID: number, parentID: number): void {
    const dialogRef = this.openFileDialog("500px", "400px", "Select folder", this.currentUser.userId)
    dialogRef.afterClosed().subscribe(result => {
      if (result === undefined || result.result === "cancel") {
      } else if (result.result === "accept") {

        let currentDir=this.currentDirId === -1 ? this.rootId:this.currentDirId;
        var newParentID = result.selectedEntryID === -1 ? this.rootId : result.selectedEntryID;

        if (Number(newParentID) === Number(currentDir)) { //for some reason this.currentDirId e un number vazut ca un string aici
          this.openSnackBar("Trebuie sa alegi alt folder decat cel curent", "OK");
        } else {
          //console.log(entryID)
          this.backendFSProvider.copyEntry(entryID, newParentID).subscribe((data: any) => {
              //console.log(data)
              let currentDirectoryId = this.currentDirId === 1 ? this.rootId : this.currentDirId
              if (newParentID === currentDirectoryId) {
                this.setFsEntriesComponentsFromDirWithId(currentDirectoryId)
              }

            },
            console.log
          )
        }
      }
    });
  }

  openRenameDialog(width: string, height: string, title: string) {
    return this.dialog.open(RenameDialogComponent, {width: width, height: height, data: {title: title}});
  }

  createFolderAction(entryID: number, parentID: number): void {
    const dialogRef = this.openRenameDialog("500px", "100px", "Create a folder")
    dialogRef.afterClosed().subscribe(result => {
      if (result === undefined || result.result === "cancel") {
      } else if (result.result === "accept") {
        var folderID = this.currentDirId === -1 ? this.rootId : this.currentDirId;
        this.backendFSProvider.createFolder(folderID, result.data).subscribe((data: any) => {
            this.setFsEntriesComponentsFromDirWithId(folderID);
          },
          console.log
        )
      }
    });
  }


  downloadAction(entryID: number, parentID?: number): void {
    this.backendFSProvider.downloadEntry(entryID).subscribe((data: any) => {
        let name: string = ""
        for (let i = 0; i < this.displayedFsEntryJsons.length; i++) {
          if (this.displayedFsEntryJsons[i].entryId === entryID) {
            name = this.displayedFsEntryJsons[i].name
            break
          }
        }
        const byteCharacters = atob(data.content);
        const byteNumbers = new Array(byteCharacters.length);
        for (let i = 0; i < byteCharacters.length; i++) {
          byteNumbers[i] = byteCharacters.charCodeAt(i);
        }
        const byteArray = new Uint8Array(byteNumbers);
        const blob = new Blob([byteArray], {type: 'application/octet-stream'});
        const link = document.createElement('a');
        link.href = window.URL.createObjectURL(blob);
        link.download = name;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
      },
      console.log)
  }

  onUploadFiles(event: any): void {
    const fileList: FileList = event.target.files;
    this.uploadFunction(fileList)
  }


  @HostListener('drop', ['$event'])
  onDrop(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = false;
    const files = event.dataTransfer!.files;
    this.uploadFunction(files)
  }


  uploadFunction(files: FileList) {
    if (files && files.length > 0) {
      for (let i = 0; i < files.length; i++) {
        let newParent = this.currentDirId === -1 ? this.rootId : this.currentDirId
        this.backendFSProvider.uploadFile(newParent, files[i]).subscribe((data: any) => {

          this.setFsEntriesComponentsFromDirWithId(newParent);
          this.sidebarNotification.notifySidebar()
        }, console.log)
      }
    }
  }


  showUploadChoice: boolean = false;
  isDragging = false;

  toggleUploadChoice() {

    this.showUploadChoice = !this.showUploadChoice
  }

  disableUpdateChoice() {
    this.showUploadChoice = false;
  }

  @HostListener('dragover', ['$event'])
  onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = true;
  }

  @HostListener('dragleave', ['$event'])
  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragging = false;
  }


///////////////////////////////////////////////////////////////
}
