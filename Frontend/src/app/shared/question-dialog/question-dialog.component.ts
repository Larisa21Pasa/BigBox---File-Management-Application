/*************************************************************************

 File:       question-dialog.component.ts
 Copyright:   (c) 2023 NazImposter

 Module-History:
 Date        Author                Reason
 25.12.2023  Matei Rares         Create component and question as input

 **************************************************************************/
import {Component, HostListener, Inject} from '@angular/core';
import {MatDialogRef} from "@angular/material/dialog";
import {MAT_DIALOG_DATA} from "@angular/material/dialog";

@Component({
  selector: 'app-rename-dialog',
  templateUrl: './question-dialog.component.html',
  styleUrls: ['./question-dialog.component.scss']
})
export class QuestionDialog {
  inputValue: string = '';
  title: string = '';
  question:string=""
  constructor(public dialogRef: MatDialogRef<QuestionDialog>,
              @Inject(MAT_DIALOG_DATA) public data: { question:string }
  ) {
    this.question =data.question;
  }

  onAccept(): void {
    this.dialogRef.close("accept");
  }
  @HostListener('window:keydown', ['$event'])
  handleKeyboardEvent(event: KeyboardEvent) {
    if (event.key === 'Enter') {
      this.onAccept();
    }
  }
  onCancel(): void {
    this.dialogRef.close("cancel");
  }
}
