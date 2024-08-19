/*************************************************************************

 File:       rename-dialog.component.ts
 Copyright:   (c) 2023 NazImposter

 Module-History:
 Date        Author                Reason
 25.12.2023  Matei Rares         Create component and title as input

 **************************************************************************/
import {Component, HostListener, Inject} from '@angular/core';
import {MatDialogRef} from "@angular/material/dialog";
import {MAT_DIALOG_DATA} from "@angular/material/dialog";

@Component({
  selector: 'app-rename-dialog',
  templateUrl: './rename-dialog.component.html',
  styleUrls: ['./rename-dialog.component.scss']
})
export class RenameDialogComponent {
  inputValue: string = '';
  title: string = '';
  constructor(public dialogRef: MatDialogRef<RenameDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: { title: string }
  ) {

    this.title = data.title;
  }
  @HostListener('window:keydown', ['$event'])
  handleKeyboardEvent(event: KeyboardEvent) {
    if (event.key === 'Enter') {
      this.onAccept();
    }
  }
  onAccept(): void {
    this.dialogRef.close({ result: 'accept', data: this.inputValue });
  }

  onCancel(): void {
    this.dialogRef.close({ result: 'cancel', data: null });
  }
}
