/**************************************************************************

   File:       welcome-component.ts
   Copyright:   (c) 2023 NazImposter

   Module-History:
   Date         Author                Reason
   03.12.2023   Pasa Larisa      Basic structure

 **************************************************************************/

import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {AboutUs} from 'src/app/models/welcome-about-us';
import {Plans} from 'src/app/models/welcome-plans';
import {AuthService} from 'src/app/services/auth_service/auth.service';
import {FormControl, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-welcome',
  templateUrl: './welcome.component.html',
  styleUrls: ['./welcome.component.scss']
})
export class WelcomeComponent implements OnInit {
  /* CONSTRUCTOR */
  constructor(private service: AuthService, private route: Router) {
    localStorage.clear();
  }
  /* VARIABLES */
  plans: Plans[] = [
    {
      namePlan: "Basic",
      cloudStorage: "5 GB",
      planForClientType: "New",
      fitFor: "individuals with basic storage needs",
      description: "Our entry-level plan for new users who have basic storage requirements. Perfect for personal use and getting started with our services.",
    },
    {
      namePlan: "Standard",
      cloudStorage: "20 GB",
      planForClientType: "Regular",
      fitFor: "users with moderate storage needs",
      description: "A balanced plan for regular users with moderate storage needs. Ideal for individuals and small businesses looking for a reliable storage solution.",
    },
    {
      namePlan: "Premium",
      cloudStorage: "100 GB",
      planForClientType: "VIP",
      fitFor: "power users with extensive storage requirements",
      description: "Our top-tier plan for VIP users with extensive storage requirements. Enjoy premium features and a generous storage quota suitable for power users and businesses with high demands.",
    },
  ];
  aboutUs: AboutUs = {
    title: "Big Box",
    popularity: "Your Go-To Platform!",
    history: "Discover the world of Big Box, where innovation meets simplicity. A space that has been at your service, delivering top-notch solutions to users for years.",
    appScope: "From managing your files with ease to fostering collaboration across various sectors, Big Box caters to your needs, ensuring a seamless experience in finance, healthcare, education, and beyond!"
  };
  /* FORMS */
  contactForm = new FormGroup({
    email: new FormControl("", Validators.required),
    password: new FormControl("", Validators.required)
  });

  /* LOGS */
  /* FUNCTIONS */

  ngOnInit(): void { }


  /* It will be implemented just if is necessary */
  SendClientMessageContactSection() { }

}
