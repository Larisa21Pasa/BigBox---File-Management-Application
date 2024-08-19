/**************************************************************************

   File:       my-account.ts
   Copyright:   (c) 2023 NazImposter

   Module-History:
   Date         Author                Reason
   03.12.2023   Pasa Larisa      Basic structure

 **************************************************************************/

import {Plans} from "./welcome-plans";

export interface MyAccount {
    email:string;
    username:string;
    active:boolean;
    plan: Plans;
  }
