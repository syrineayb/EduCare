// navbar.component.ts

import { Component, OnInit } from '@angular/core';
import { TopicService } from "../../services/topic/topic.service";
import { TopicPageResponse } from "../../models/topic/topic-page-response";
import { TopicResponse } from "../../models/topic/topic-response";
import {Router} from "@angular/router";

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  searchedTopics: TopicResponse[] | undefined = []; // Initialize as an empty array
  searchKeyword: string = ''; // Variable to store the search keyword
  isDropdownOpen: boolean = false;
  topics: TopicResponse[] | undefined = []; // Initialize as an empty array
  isAuthenticated: boolean = false; // Flag to track user's authentication status


  constructor(    private router: Router,
    private topicService: TopicService) { }

  ngOnInit(): void {
   // this.loadAllTopics(); // Load all topics automatically on component initialization
    this.loadAllTopicTitles();
  }
  loadAllTopicTitles(): void {
    this.topicService.getAllTopicTitles().subscribe({
      next: (titles: string[]) => {
        // Check if topics is defined before mapping
        if (this.topics) {
          this.topics = titles.map((title, index) => ({ id: index + 1, title: title } as TopicResponse)); // Increment id starting from 1
          console.log('Loaded topic titles:', this.topics);
        }
      },
      error: (error) => {
        console.error('Error loading topic titles:', error);
        // Handle error loading topic titles
      }
    });
  }

  // Method to search topics containing the search keyword
  searchTopics(): void {
    if (this.searchKeyword.trim() !== '') {
      this.topicService.findTopicsByTitleContaining(this.searchKeyword).subscribe({
        next: (response) => {
          this.searchedTopics = response;
          console.log(response);
        },
        error: (error) => {
          console.error('Error searching topics:', error);
        }
      });
    } else {
      this.searchedTopics = [];
    }
  }

  // Method to load all topics
  loadAllTopics(): void {
    const page = 0; // Default page number
    const size = 10; // Default page size

    this.topicService.getTopicsInBatch(page, size).subscribe({
      next: (response) => {
        this.topics = response;
        console.log(response);
      },
      error: (error) => {
        console.error('Error loading topics:', error);
        // Handle error loading topics
      }
    });
  }


  // Method to handle click event on "LEARN" button
  loadAllTopicsOnClick(): void {
    this.loadAllTopics();
  }

  showDropdown() {
    this.isDropdownOpen = true;
  }

  hideDropdown() {
    this.isDropdownOpen = false;
  }
  toggleDropdown(): void {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  onCardClick(topicId: number): void {
    console.log(this.isAuthenticated);
    this.router.navigate(['/courses', topicId]);
  }
}

