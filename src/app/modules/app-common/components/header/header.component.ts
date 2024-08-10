import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Router} from "@angular/router";
import {AuthenticationService} from "../../../../services/auth/authentication.service";
import {TopicResponse} from "../../../../models/topic/topic-response";
import {TopicService} from "../../../../services/topic/topic.service";
import {TokenService} from "../../services/token/token.service";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements  OnInit {
  @Input() sidebarActive = false;
  @Output() toggleSidebar = new EventEmitter();
  searchedTopics: TopicResponse[] | undefined = []; // Initialize as an empty array
  searchKeyword: string = '';
  constructor(private router: Router,
              public authService: AuthenticationService,
              private topicService: TopicService,
              private tokenService: TokenService
  ) {} // Inject the Router

  ngOnInit(): void {
  }
  logout(): void {
    this.authService.logout();
  }

  isCandidate(): boolean {
    // Assuming your AuthenticationService has a method to check if the user is a candidate
    return this.tokenService.isCandidate();
  }
  isAdmin() {
    return this.tokenService.isAdmin();
  }
  isInstructor(): boolean{
    return this.tokenService.isInstructor();
  }
  searchTopics(): void {
    if (this.searchKeyword.trim() !== '') {
      this.topicService.findTopicsByTitle(this.searchKeyword).subscribe({
        next: (response: TopicResponse[]) => {
          this.searchedTopics = response; // Assign the response directly to searchedTopics
          console.log(response);
        },
        error: (error) => {
          console.error('Error searching topics:', error);
          // Handle error searching topics
        }
      });
    } else {
      this.searchedTopics = []; // Clear searched topics array if search keyword is empty
    }
  }



}



