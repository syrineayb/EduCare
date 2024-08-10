import { Component, OnInit } from '@angular/core';
import { ForumService } from '../../../../services/forum/forum.service';
import { ToastrService } from 'ngx-toastr';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';
import { VgApiService } from '@videogular/ngx-videogular/core';
import { DiscussionResponse } from '../../../../models/discussion/discussion-response';
import { DiscussionPageResponse } from '../../../../models/discussion-page-response';
import { MessageRequest } from '../../../../models/message/message-request';
import { MessageResponse } from '../../../../models/message/message-response';
import { AnswerResponse } from '../../../../models/answer/answer-response';
import { AnswerRequest } from '../../../../models/answer/answer-request';
import { UserResponse } from '../../../../models/account/user-response';
import { AuthenticationService } from '../../../../services/auth/authentication.service';
import {MessagePageResponse} from "../../../../models/message/message-page-response";

@Component({
    selector: 'app-forum',
    templateUrl: './forum.component.html',
    styleUrls: ['./forum.component.css']
})
export class ForumComponent implements OnInit {
    messages: MessageResponse[] = [];
    newDiscussion: MessageRequest = {
        subject: '',
        messageText: '',
        recordFile: undefined,
        imageFile: undefined,
        videoFile: undefined,
        pdfFile: undefined
    };
    currentPage = 0;
    totalPages = 0;
    pageSize = 4; // Number of discussions per page
    pages: number[] = [];

    api: VgApiService = new VgApiService();
    updatedAnswer: AnswerResponse = {
        id: 0,
        discussionId: 0,
        messageContent: '',
        messageText: '', // Ensure 'MessageText' property is included
        authorId: 0,
        authorFullName: '',
        createdAt: '',
        updatedAt: ''
    };

    isSubjectDisabled = true;
    showAddDiscussionForm = false;
    forum: { id: number; title: string } | undefined;
    showVideo = false;
    showImage = false;
    showPdf = false;
    showAudio = false;
    recording = false;
    url: SafeUrl | null = null;
    error: string | null = null;
    currentUser: UserResponse | null = null;
    newAnswer: AnswerRequest = { messageContent: '' };
    showAddAnswerInput = false;
    selectedAnswerId: number | null = null;
    selectedDiscussionId: number | null = null;
    showUpdateAnswerInput = false;

    constructor(
        private forumService: ForumService,
        private toastr: ToastrService,
        private domSanitizer: DomSanitizer,
        private authService: AuthenticationService
    ) {}

    ngOnInit(): void {
        this.loadDiscussions(); // Load first page with 4 discussions
        this.authService.getCurrentUser().subscribe(
            currentUser => {
                this.currentUser = currentUser;
            },
            error => {
                console.error('Error fetching current user:', error);
            }
        );
    }

    // Discussion methods
    loadDiscussions(): void {
        this.forumService.getInstructorLessonDiscussions(this.currentPage, this.pageSize)
            .subscribe(
                (response: MessagePageResponse) => {
                    this.messages = response.content || [];
                    this.totalPages = response.totalPages || 0;
                    this.pages = Array.from({ length: this.totalPages }, (_, i) => i + 1);
                },
                (error) => {
                    console.error('Error fetching messages', error);
                }
            );
    }

    updateDiscussion(discussion: MessageResponse): void {
        const formData = new FormData();
        formData.append('subject', discussion.subject);
        formData.append('text', discussion.messageText);

        this.forumService.updateMessage(discussion.id, formData).subscribe(
            (response: MessageResponse) => {
                this.showSuccessMessage('Discussion updated successfully');
                discussion.subject = response.subject ?? '';
                discussion.editMode = false;
            },
            (error: any) => {
                console.error('Error updating discussion:', error);
                this.showErrorMessage('Failed to update discussion');
            }
        );
    }

    deleteDiscussion(discussionId: number): void {
        this.forumService.deleteDiscussion(discussionId).subscribe(
            () => {
                this.showSuccessMessage("Discussion deleted successfully");
                this.loadDiscussions();
            },
            error => {
                this.showErrorMessage("Error deleting discussion");
                console.error('Error deleting discussion:', error);
            }
        );
    }

    toggleAnswers(discussion: MessageResponse): void {
        discussion.showAnswers = !discussion.showAnswers;

        if (discussion.showAnswers && discussion.answers?.length === 0) {
            this.forumService.getAnswersByDiscussion(discussion.id).subscribe(
                (answers: AnswerResponse[]) => {
                    discussion.answers = answers;
                },
                (error: any) => {
                    this.showErrorMessage('Failed to fetch answers');
                    console.error('Error fetching answers:', error);
                }
            );
        }
    }

    // Answer methods
    toggleAddAnswerInput(discussionId: number): void {
        this.showAddAnswerInput = !this.showAddAnswerInput;
        this.selectedDiscussionId = discussionId;
    }

    submitAnswer(): void {
        if (this.selectedDiscussionId !== null) {
            this.forumService.createAnswer(this.selectedDiscussionId, this.newAnswer).subscribe(
                (response: AnswerResponse) => {
                    this.showSuccessMessage('Answer created successfully');
                    if (this.selectedDiscussionId !== null) {
                        this.fetchAnswersForDiscussion(this.selectedDiscussionId);
                    }
                    this.resetAnswerForm();
                },
                (error) => {
                    this.showErrorMessage('Error creating answer');
                }
            );
        } else {
            this.showErrorMessage('No discussion selected');
        }
    }

    fetchAnswersForDiscussion(discussionId: number): void {
        this.forumService.getAnswersByDiscussion(discussionId).subscribe(
            (answers: AnswerResponse[]) => {
                const discussion = this.messages.find(discussion => discussion.id === discussionId);
                if (discussion) {
                    discussion.answers = answers;
                }
            },
            (error: any) => {
                this.showErrorMessage('Error fetching answers');
                console.error('Error fetching answers:', error);
            }
        );
    }

    toggleEditAnswer(discussionId: number, answerId: number): void {
        this.showUpdateAnswerInput = !this.showUpdateAnswerInput;
        this.selectedDiscussionId = discussionId;
        this.selectedAnswerId = answerId;

        const answer = this.getAnswerById(discussionId, answerId);
        if (answer) {
            this.updatedAnswer.messageContent = answer.messageContent;
        }
    }

    getAnswerById(discussionId: number, answerId: number): AnswerResponse | undefined {
        const discussion = this.messages.find(d => d.id === discussionId);
        if (discussion && discussion.answers) {
            return discussion.answers.find(a => a.id === answerId);
        }
        return undefined;
    }

    submitUpdatedAnswer(answerId: number): void {
        const discussion = this.messages.find(d => d.answers?.some(a => a.id === answerId));
        if (discussion) {
            const answer = discussion.answers?.find(a => a.id === answerId);
            if (answer) {
                const updatedAnswerRequest = {
                    messageContent: answer.messageContent
                };

                this.forumService.updateAnswer(answerId, updatedAnswerRequest).subscribe(
                    updatedAnswer => {
                        answer.messageContent = updatedAnswer.messageContent;
                        this.showSuccessMessage('Answer updated successfully');
                        this.showUpdateAnswerInput = false;
                    },
                    error => {
                        this.showErrorMessage('Failed to update answer');
                    }
                );
            }
        }
    }

    deleteAnswer(discussionId: number, answerId: number): void {
        if (confirm('Are you sure you want to delete this answer?')) {
            this.forumService.deleteAnswer(answerId).subscribe(
                () => {
                    const discussion = this.messages.find(d => d.id === discussionId);
                    if (discussion) {
                        discussion.answers = discussion.answers?.filter(a => a.id !== answerId) || [];
                    }
                    this.showSuccessMessage('Answer deleted successfully');
                },
                (error) => {
                    this.showErrorMessage('Error deleting answer');
                }
            );
        }
    }

    // Message methods
    toggleEditMode(discussion: MessageResponse): void {
        if (discussion.editMode) {
            // If already in edit mode, close it
            discussion.editMode = false;
            // Optionally clear any temporary updated subject
            // discussion.updatedSubject = undefined;
        } else {
            // Close other edit modes if needed
            this.messages.forEach(d => d.editMode = false);
            // Open edit mode for the current discussion
            discussion.editMode = true;
            // Optionally initialize updatedSubject with current subject
            // discussion.updatedSubject = discussion.subject;
        }
    }


    resetAnswerForm(): void {
        this.newAnswer = { messageContent: '' };
        this.showAddAnswerInput = false;
    }

    showErrorMessage(message: string): void {
        this.toastr.error(message, 'Error', {
            positionClass: 'toast-center-center',
            toastClass: 'custom-toast-error'
        });
    }

    showSuccessMessage(message: string): void {
        this.toastr.success(message, 'Success', {
            positionClass: 'toast-center-center',
            toastClass: 'custom-toast-success'
        });
    }

    sanitize(url: string): SafeUrl {
        return this.domSanitizer.bypassSecurityTrustUrl(url);
    }

    errorCallback(error: any): void {
        this.error = 'Can not access your microphone.';
        console.error('Error accessing media devices:', error);
    }

    processRecording(blob: Blob): void {
        this.url = this.domSanitizer.bypassSecurityTrustUrl(URL.createObjectURL(blob));
        console.log('Recorded audio URL:', this.url);
        this.newDiscussion.recordFile = new File([blob], 'recorded-audio.wav', { type: 'audio/wav' });
    }

    onPlayerReady(api: VgApiService): void {
        this.api = api;
        this.api.getDefaultMedia().subscriptions.loadedMetadata.subscribe(
            this.autoplay.bind(this)
        );
    }

    autoplay(): void {
        this.api.play();
    }

    toggleContent(contentType: string): void {
        switch (contentType) {
            case 'pdf':
                this.showPdf = !this.showPdf;
                break;
            case 'audio':
                this.showAudio = !this.showAudio;
                break;
            case 'video':
                this.showVideo = !this.showVideo;
                break;
            case 'image':
                this.showImage = !this.showImage;
                break;
            default:
                break;
        }
    }

    changePage(page: number, event: Event): void {
        event.preventDefault(); // Prevent default anchor tag behavior
        this.currentPage = page;
        this.loadDiscussions();
    }

    nextPage(event: Event): void {
        event.preventDefault(); // Prevent default anchor tag behavior
        if (this.currentPage < this.totalPages - 1) {
            this.currentPage++;
            this.loadDiscussions();
        }
    }

    prevPage(event: Event): void {
        event.preventDefault(); // Prevent default anchor tag behavior
        if (this.currentPage > 0) {
            this.currentPage--;
            this.loadDiscussions();
        }
    }

    hasNextPage(): boolean {
        return this.currentPage < this.totalPages - 1;
    }
}
