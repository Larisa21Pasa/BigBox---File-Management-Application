/*************************************************************************

 File:       file-dialog.component.ts
 Copyright:   (c) 2023 NazImposter

 Module-History:
 Date        Author                Reason
 25.12.2023  Matei Rares         Added folder,open folder,breadcrumb, goback, select

 **************************************************************************/


import {FlatTreeControl} from '@angular/cdk/tree';
import {Component, HostListener, Inject, Input, OnInit} from '@angular/core';
import {MatTreeFlatDataSource, MatTreeFlattener, MatTreeModule} from '@angular/material/tree';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {BackendFSProvider} from "../../services/all-files/backend-fs-provider.service";


interface TreeNode {
  entryId: number;
  name: string;
  isFile: boolean;
  parent: number;
  children?: TreeNode[];
}

@Component({
  selector: 'app-file-dialog',
  templateUrl: './file-dialog.component.html',
  styleUrls: ['./file-dialog.component.css'],
})
export class FileDialogComponent {
  currentUserId!: number
  displayedEntries: any[]=[]
  ignoreFileID!:number
  constructor(
    private backendFSProvider: BackendFSProvider,
    public dialogRef: MatDialogRef<FileDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { title: string; currentUserId: number,currentItemID:number }
  ) {
    this.ignoreFileID=data.currentItemID

    this.currentUserId = data.currentUserId
    this.backendFSProvider.getRootFsEntryId(this.currentUserId).subscribe((data1: any) => {
        this.backendFSProvider.getFsEntries(data1.entryId).subscribe((data2: any) => {

          this.displayedEntries = data2

        }, console.log)


      },
      console.log)
  }
  @HostListener('window:keydown', ['$event'])
  handleKeyboardEvent(event: KeyboardEvent) {
    if (event.key === 'Enter') {
      this.acceptDialog();
    }
  }
  openFolder(entryid: number,entryname:string,parentid:number) {
    this.backendFSProvider.getFsEntries(entryid).subscribe((data2: any) => {
      this.navigationHistory.push(parentid)
      this.navigationHistoryNames.push(entryname)
      this.updateBreadCrumb()
      this.displayedEntries = data2

    }, console.log)
  }

  breadCrumb!: string
  protected navigationHistory: number[] = [-1];
  protected navigationHistoryNames: string[] = ["root"];

  updateBreadCrumb() {
    this.breadCrumb = "";
    if (this.navigationHistoryNames.at(-1) !== "root") {
      for (let nav of this.navigationHistoryNames) {
        if (nav === "root") continue;
        this.breadCrumb += nav + "/";
      }
    } else {
      this.breadCrumb += "";
    }

  }

  goBack() {

    if (this.navigationHistory.length > 1) {
      this.navigationHistoryNames.pop()
      this.updateBreadCrumb()
      var id = this.navigationHistory.pop()

      this.backendFSProvider.getFsEntries(id!).subscribe((data2: any) => {

        this.displayedEntries = data2

      }, console.log)
    }
    else{
      this.selectedFolderID=-1
    }
  }

  selectedFolderID: number=-1

  selectFolder(folderID:number){
    this.selectedFolderID=folderID
  }

  acceptDialog(): void {
    this.dialogRef.close({result: 'accept', selectedEntryID: this.selectedFolderID});
  }

  cancelDialog(): void {
    this.dialogRef.close({result: 'cancel', selectedEntryID: null});
  }


}








