import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ForumService } from '../../../../services/forum/forum.service';
import { DiscussionResponse } from '../../../../models/discussion/discussion-response';
import { AnswerResponse } from '../../../../models/answer/answer-response';
import { MessageRequest } from "../../../../models/message/message-request";
import { DiscussionRequest } from "../../../../models/discussion/discussion-request";
import { MessageResponse } from "../../../../models/message/message-response";
import {ToastrService} from "ngx-toastr";
import {DiscussionPageResponse} from "../../../../models/discussion-page-response";
import {VgApiService} from "@videogular/ngx-videogular/core";
import {DomSanitizer, SafeResourceUrl} from "@angular/platform-browser";

@Component({
  selector: 'app-forum',
  templateUrl: './forum.component.html',
  styleUrls: ['./forum.component.css']
})
export class ForumComponent {
 /* lessonId: number | undefined;
  showAddDiscussionForm: boolean = false;
  isSubjectDisabled: boolean = true;
  newDiscussion: {
    subject: string,
    message: MessageRequest,
    recordFile: File | undefined,
    imageFile: File | undefined,
    videoFile: File | undefined,
    pdfFile: File | undefined
  } = {
    subject: '',
    message: { text: '' },
    recordFile: undefined,
    imageFile: undefined,
    videoFile: undefined,
    pdfFile: undefined
  };
  discussions: DiscussionResponse[] = [];
  searchKeyword: string = '';
  answersCounts: { [key: number]: number } = {};
  forum: { id: number; title: string } | undefined;
    currentPage: number = 1; // Define currentPage property
    totalPages: number = 1; // Define totalPages property
    api: VgApiService = new VgApiService();
    audioUrl: string = ''; // Variable to store the audio URL
    safeAudioUrl: SafeResourceUrl = ''; // Variable to store the sanitized audio URL

    constructor(private route: ActivatedRoute,
              private forumService: ForumService,
              private toastr:ToastrService,
              private domSanitizer: DomSanitizer
  ) { }

  ngOnInit(): void {
    // Retrieve lessonId from route parameters
    this.route.params.subscribe(params => {
      this.lessonId = +params['lessonId']; // '+' converts string to number
    });

    // Initially disable the discussion form
    this.isSubjectDisabled = true;

    // Load discussions and their response counts
    this.loadDiscussions();
  }

  toggleAddDiscussionForm(): void {
    this.showAddDiscussionForm = !this.showAddDiscussionForm;
    if (this.showAddDiscussionForm) {
      this.isSubjectDisabled = false;
    }
  }

  submitDiscussion(): void {
    // Check if subject or message text is empty
    if (!this.newDiscussion.subject) {
      this.showErrorMessage('Subject is required.');
      return;
    }
    if (!this.newDiscussion.message || !this.newDiscussion.message.text) {
      this.showErrorMessage('Message is required.');
      return;
    }
    // Check if forum ID is available
    if (this.forum?.id) {
      const discussionRequest: DiscussionRequest = {
        subject: this.newDiscussion.subject,
        message: this.newDiscussion.message
      };

      // Create a FormData object to handle file uploads
      const formData = new FormData();
      formData.append('subject', this.newDiscussion.subject);
      formData.append('text', this.newDiscussion.message.text);
      if (this.newDiscussion.recordFile) {
        formData.append('recordFile', this.newDiscussion.recordFile, this.newDiscussion.recordFile.name);
      }
      if (this.newDiscussion.imageFile) {
        formData.append('imageFile', this.newDiscussion.imageFile, this.newDiscussion.imageFile.name);
      }
      if (this.newDiscussion.videoFile) {
        formData.append('videoFile', this.newDiscussion.videoFile, this.newDiscussion.videoFile.name);
      }
      if (this.newDiscussion.pdfFile) {
        formData.append('pdfFile', this.newDiscussion.pdfFile, this.newDiscussion.pdfFile.name);
      }

      // Submit the discussion with message and files
      this.forumService.createDiscussion(this.forum.id, discussionRequest).subscribe(
        (discussionResponse: DiscussionResponse) => {
          // Call the forum service method to create message with files
          this.forumService.createMessage(formData, discussionResponse.id).subscribe(
            (messageResponse: MessageResponse) => {
              this.showSuccessMessage('Discussion created successfully');
              this.resetDiscussionForm();
                if (this.forum && this.forum.id) {
                    this.fetchDiscussions(this.forum.id);
                }
            },
            (error: any) => {
              console.error('Error creating message:', error);
              this.showErrorMessage('Failed to create message');
            }
          );
        },
        (error: any) => {
          console.error('Error creating discussion:', error);
          this.showErrorMessage('Failed to create discussion');
        }
      );
    }
  }

  cancelDiscussion(): void {
    this.isSubjectDisabled = false;
  }
/!*  searchDiscussionsBySubject(): void {
    if (this.searchKeyword.trim() !== '') {
      this.forumService.searchDiscussionsBySubject(this.searchKeyword)
        .subscribe((data: DiscussionResponse[]) => {
          this.discussions = data;
          this.loadResponseCounts();
        }, error => {
          console.error('Error searching discussions by subject:', error);
        });
    }
  }

 *!/

  searchDiscussionsByLesson(): void {
    if (this.searchKeyword.trim() !== '') {
      this.forumService.searchDiscussionsByLesson(this.searchKeyword)
        .subscribe((data: DiscussionResponse[]) => {
          this.discussions = data;
          this.loadResponseCounts();
        }, error => {
          console.error('Error searching discussions by lesson:', error);
        });
    }
  }
    resetSearch(): void {
        this.searchKeyword = ''; // Clear the search keyword
        this.discussions = []; // Clear the discussions array
    }


    loadDiscussions(): void {
        if (this.lessonId) {
            this.forumService.getForumByLessonId(this.lessonId).subscribe(
                (forum: any) => {
                    this.forum = forum;
                    if (this.forum && this.forum.id) {
                        this.fetchDiscussions(this.forum.id);
                    }
                },
                (error: any) => {
                    console.error('Error loading forum:', error);
                }
            );
        }

        // Sanitize audio URLs
        this.discussions.forEach(discussion => {
            if (discussion.message.recordUrl) { // Check if recordUrl is defined
                this.sanitizeAudioUrl(discussion.message.recordUrl);
            }
        });
    }

    loadResponseCounts(): void {
    this.discussions.forEach(discussion => {
      this.getAnswersCountByDiscussion(discussion.id);
    });
  }

  getAnswersCountByDiscussion(discussionId: number): void {
    this.forumService.getAnswersCountByDiscussion(discussionId).subscribe(count => {
      // Store answers count in the object
      this.answersCounts[discussionId] = count;
    });
  }

  fetchAnswersForDiscussion(discussionId: number): void {
    this.forumService.getAnswersByDiscussion(discussionId).subscribe(
      (answers: AnswerResponse[]) => {
        const discussion = this.discussions.find(d => d.id === discussionId);
        if (discussion) {
          discussion.answers = answers;
        }
      },
      error => {
        console.error('Error fetching answers:', error);
      }
    );
  }

    fetchDiscussions(forumId: number, page: number = 1, size: number = 10): void {
        this.forumService.getAllDiscussionsByForumId(forumId, page, size).subscribe(
            (response: DiscussionPageResponse) => {
                if (response.content) {
                    this.discussions = response.content;
                } else {
                    this.discussions = []; // or any appropriate fallback value
                }
                // Access the correct properties from DiscussionPageResponse
                this.totalPages = response.totalPages ?? 0;
            },
            (error: any) => {
                console.error('Error fetching discussions:', error);
            }
        );
    }

  showSuccessMessage(message: string): void {
    this.toastr.success(message, 'Success', {
      positionClass: 'toast-center-center',
      toastClass: 'custom-toast-success'
    });
  }

  showErrorMessage(message: string): void {
    this.toastr.error(message, 'Error', {
      positionClass: 'toast-center-center',
      toastClass: 'custom-toast-error'
    });
  }

  resetDiscussionForm(): void {
    this.newDiscussion = {
      subject: '',
      messageText: '',
      recordFile: undefined,
      imageFile: undefined,
      videoFile: undefined,
      pdfFile: undefined
    };
  }

    toggleShowAnswers(discussion: DiscussionResponse): void {
        discussion.showAnswers = !discussion.showAnswers;
    }

    onFileSelected(event: any, fileType: string): void {
        const files = event.target.files;
        if (files && files.length > 0) {
            const file = files[0];
            switch (fileType) {
                case 'pdfFile':
                    this.newDiscussion.pdfFile = file;
                    break;
                case 'imageFile':
                    this.newDiscussion.imageFile = file;
                    break;
                case 'videoFile':
                    this.newDiscussion.videoFile = file;
                    break;
                case 'recordFile':
                    this.newDiscussion.recordFile = file;
                    break;
                // Add cases for other file types
                default:
                    break;
            }
        }
    }


    onPlayerReady(source: VgApiService): void {
        this.api = source;
        this.api.getDefaultMedia().subscriptions.loadedMetadata.subscribe(
            this.autoplay.bind(this)
        );
    }

    autoplay(): void {
        this.api.play();
    }

    sanitize(url: string) {
        console.log(url);
        return this.domSanitizer.bypassSecurityTrustUrl(url);
    }

    getSafeUrl(url: string): SafeResourceUrl {
        return this.domSanitizer.bypassSecurityTrustResourceUrl(url);
    }
    sanitizeAudioUrl(url: string): void {
        this.audioUrl = url;
        this.safeAudioUrl = this.domSanitizer.bypassSecurityTrustResourceUrl(url);
    }
*/
}
