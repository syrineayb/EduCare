import {Component} from '@angular/core';
import {TopicService} from "../../../../services/topic/topic.service";
import {TopicResponse} from "../../../../models/topic/topic-response";

@Component({
  selector: 'app-candidate-home',
  templateUrl: './candidate-home.component.html',
  styleUrls: ['./candidate-home.component.css']
})
export class CandidateHomeComponent {
  searchedTopics: TopicResponse[] | undefined = []; // Initialize as an empty array
  searchKeyword: string = ''; // Variable to store the search keyword
  constructor(private topicService: TopicService) { }
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
