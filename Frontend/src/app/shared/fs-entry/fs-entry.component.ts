/**************************************************************************

 File:        fs-entry.component.ts
 Copyright:   (c) 2023 NazImposter

 Module-History:
 Date        Author                Reason
 30.11.2023  Sebastian Pitica      Added functionalities for handling mouse events and emitting events to parent component
 01.12.2023  Sebastian Pitica      Added isInTrash and ownerId properties

 **************************************************************************/

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FsEntryJson} from "../../models/fs-entry-json";
import {ExtendedMouseEvent} from "../../models/extended-mouse-event";

@Component({
  selector: 'app-fs-entry',
  templateUrl: './fs-entry.component.html',
  styleUrls: ['./fs-entry.component.scss']
})
export class FsEntryComponent implements OnInit {

  /////////////////////////////////////////////////

  @Input({required: true}) data: string | undefined;
  @Input() contextmenu: boolean = false;
  @Input({required: true}) index: number | undefined;
  @Output("rclick") wasRightClicked = new EventEmitter<ExtendedMouseEvent>();
  @Output("lclick") wasLeftClicked = new EventEmitter<ExtendedMouseEvent>();
  @Output("open") wasDoubleLeftClicked = new EventEmitter<ExtendedMouseEvent>();
  name: string | undefined;
  createdDate: Date | undefined;
  isFile: boolean | undefined;
  size: number | undefined;
  entryId: number | undefined;
  parentId: number | undefined;
  isInTrash: number | undefined;
  isSelected: boolean = false;
  entryDescription: string | undefined;
  isHover: boolean = false;
  protected readonly console = console;

  /////////////////////////////////////////////////

  onRightClick(event: MouseEvent) {
    this.isSelected = false;
    this.wasRightClicked.emit(new ExtendedMouseEvent(event, '{"target": "' + this.entryId + '", "index":"' + this.index + '", "parentTarget":"' + this.parentId + '"}'));
  }

  onLeftClick(event: MouseEvent) {
    if (event.ctrlKey) this.isSelected = !this.isSelected;
    this.wasLeftClicked.emit(new ExtendedMouseEvent(event, '{"target": "' + this.entryId + '", "index":"' + this.index + '", "parentTarget":"' + this.parentId + '"}'));
  }

  onOpen(event: MouseEvent) {
    this.wasDoubleLeftClicked.emit(new ExtendedMouseEvent(event, '{"target": "' + this.entryId + '", "index":"' + this.index + '", "parentTarget":"' + this.parentId + '"}'));
  }

  ngOnInit(): void {
    if (this.data) {
      const jsonData = JSON.parse(this.data) as FsEntryJson;
      this.name = jsonData.name;
      this.createdDate = jsonData.createdDate as Date;
      this.isFile = jsonData.isFile as boolean;
      this.size = jsonData.isFile ? jsonData.size : undefined;
      this.entryId = jsonData.entryId as number;
      this.parentId = jsonData.parentId as number;
      this.isInTrash = jsonData.isInTrash as number;
      this.entryDescription = jsonData.entryDescription as string;
    } else {
      console.error("data is undefined");
    }
  }


  onMouseover(event: MouseEvent) {
    this.isHover = true;
  }

  onMouseout(event: MouseEvent) {
    this.isHover = false;
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
