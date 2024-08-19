
/**************************************************************************

   File:       change-password.ts
   Copyright:   (c) 2023 NazImposter

   Module-History:
   Date         Author                Reason
   12.12.2023   Pasa Larisa      Basic structure

 **************************************************************************/

export interface ChangePasswordRequest {
    currentPassword:string;
    newPassword:string;
    confirmationPassword:string;
  }
  