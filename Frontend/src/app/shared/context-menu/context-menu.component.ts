/*************************************************************************

 File:       context-menu.component.html
 Copyright:   (c) 2023 NazImposter

 Module-History:
 Date        Author                Reason
 01.12.2023  Sebastian Pitica      Added target reference required for context menu to work, added optionClicked event emitter and specific menu items
 14.12.2023  Sebastian Pitica      Patched display of context menu bug
 16.12.2023  Sebastian Pitica      Added description option for files and folders, added specific menu items for files and folders in trash, returned parent id of the target

 **************************************************************************/

import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {FsEntryComponent} from "../fs-entry/fs-entry.component";

@Component({
  selector: 'app-context-menu',
  templateUrl: './context-menu.component.html',
  styleUrls: ['./context-menu.component.scss']
})
export class ContextMenuComponent implements OnInit, OnChanges {
  @Input() x = 0;
  @Input() y = 0;
  @Input({required: true}) target: FsEntryComponent | undefined; //the target of the context menu
  @Output() optionClicked = new EventEmitter<string>();
  menuItems: string[] = [];

  ngOnInit(): void {
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.target != undefined) {

      if (this.target.isInTrash) {
        this.menuItems = ["Restore", "Delete"]
      } else {
        if (this.target.isFile) {
          this.menuItems = ["Download", "Rename", "Move", "Copy To","Delete", "Description"]
        } else {
          this.menuItems = ["Open",  "Rename", "Move","Delete", "Description"]
        }
      }
    }
  }

  onMenuItemClick(optionn: string) {
    let option:string=""
    switch (optionn){
      case "Copy To":
        option="Make_a_copy"
        break;
      case "Create A Folder":
        option="Create_a_folder"
        break;
      default:
        option=optionn
        break;
    }
    this.optionClicked.emit(option + " " + this.target?.entryId+" "+this.target?.parentId);
  }

}

