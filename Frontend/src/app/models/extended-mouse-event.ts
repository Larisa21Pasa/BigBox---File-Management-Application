/**************************************************************************

   File:       extended-mouse-event.ts
   Copyright:   (c) 2023 NazImposter

   Module-History:
   Date         Author                Reason
   30.11.2023   Sebastian Pitica      Basic structure with properties for target id and index
   16.12.2023   Sebastian Pitica      Added property for parent target id

 **************************************************************************/


export class ExtendedMouseEvent {
  constructor(public originalEvent: MouseEvent | undefined, public additionalInfo: string) {
  }

  get targetId(): number {
    return this.additionalInfo ? JSON.parse(this.additionalInfo).target : undefined;
  }

  get targetIndex(): number {
    return this.additionalInfo ? JSON.parse(this.additionalInfo).index : undefined;
  }

  get targetParentId(): number {
    return this.additionalInfo ? JSON.parse(this.additionalInfo).parentTarget : undefined;
  }

}
