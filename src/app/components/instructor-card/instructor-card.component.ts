import { Component, OnInit } from '@angular/core';
import { InstructorService } from "../../services/instructor/instructor.service";
import { InstructorPageResponse } from "../../models/instructor/instructor-page-response";

@Component({
  selector: 'app-instructor-list',
  templateUrl: './instructor-card.component.html',
  styleUrls: ['./instructor-card.component.css']
})
export class InstructorCardComponent implements OnInit {
  instructors: InstructorPageResponse = {};

  constructor(private instructorService: InstructorService) { }

  ngOnInit(): void {
    this.loadInstructors();
  }

  loadInstructors(): void {
    this.instructorService.findAllInstructors().subscribe({
      next: (response: InstructorPageResponse) => {
        this.instructors = response;
      },
      error: (error) => {
        console.error('Error fetching instructors:', error);
      }
    });
  }

 /* extractGitHubUsername(githubUrl: string): string {
    // Assuming GitHub URLs are in the format "https://github.com/username"
    const parts = githubUrl.split('/');
    return parts[parts.length - 1]; // Extracts the last part of the URL, which is the username
  }

  extractLinkedInUsername(linkedinUrl: string): string {
    // Assuming LinkedIn URLs are in the format "https://www.linkedin.com/in/username"
    const parts = linkedinUrl.split('/');
    const username = parts[parts.length - 1]; // Extracts the last part of the URL, which is the username
    // Removing any query parameters if present
    const usernameParts = username.split('?');
    return usernameParts[0]; // Extracts the username part before any query parameters
  }

  */

}
