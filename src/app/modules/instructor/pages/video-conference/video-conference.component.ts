import { Component, OnInit, OnDestroy, HostListener } from '@angular/core';
import { Router } from "@angular/router";
import { ProfileService } from "../../../../services/profile/profile.service";
import { ProfileResponse } from "../../../../models/profile/profile-response";
import { ToastrService } from "ngx-toastr";

declare var JitsiMeetExternalAPI: any;

@Component({
  selector: 'app-video-conference',
  templateUrl: './video-conference.component.html',
  styleUrls: ['./video-conference.component.css']
})
export class VideoConferenceComponent implements OnInit, OnDestroy {
  domain: string = 'meet.jit.si';
  roomName: string = '';
  user: any;
  api: any;
  options: any;
  jitsiWindow: Window | null = null;

  isAudioMuted: boolean = false;
  isVideoMuted: boolean = false;
  isScreenSharing: boolean = false;
  showIframe: boolean = false;
  defaultImage: string = './assets/profile/default-profile-image.png';
  userProfile: ProfileResponse | undefined;
  userImageSrc: string | undefined;
  createRoomButtonDisabled: boolean = true;
  numberOfParticipants: number = 0;

  constructor(
    private router: Router,
    private profileService: ProfileService,
    private toastr: ToastrService
  ) {
  }

  ngOnInit(): void {
    this.roomName = '';
    this.profileService.getCurrentUserProfile().subscribe(
      (profile: ProfileResponse) => {
        this.userProfile = profile;
      },
      (error) => {
        console.error('Error fetching current user profile:', error);
      }
    );
    console.log(this.numberOfParticipants);
  }

  setupJitsiMeet(): void {
    this.options = {
      roomName: this.roomName,
      width: '100%',
      height: 500,
      configOverwrite: {
        prejoinPageEnabled: false
      },
      interfaceConfigOverwrite: {
        TILE_VIEW_MAX_COLUMNS: 8
      },
      userInfo: {
        displayName: this.userProfile ? `${this.userProfile.firstName} ${this.userProfile.lastName}` : '',
        avatar: this.userProfile ? this.userProfile.profileImage : ''
      }
    };

    this.jitsiWindow = window.open(
      `https://${this.domain}/${this.roomName}`,
      '_blank',
      'width=800,height=600'
    );

    if (this.jitsiWindow) {
      this.jitsiWindow.onload = () => {
        this.api = new JitsiMeetExternalAPI(this.domain, this.options);
        this.api.addEventListener('readyToClose', this.handleClose);
        this.api.addEventListener('participantLeft', this.handleParticipantLeft);
        this.api.addEventListener('participantJoined', this.handleParticipantJoined);
        this.api.addEventListener('videoConferenceJoined', this.handleVideoConferenceJoined);
        this.api.addEventListener('videoConferenceLeft', this.handleVideoConferenceLeft);
        this.api.addEventListener('audioMuteStatusChanged', this.handleMuteStatus);
        this.api.addEventListener('videoMuteStatusChanged', this.handleVideoStatus);


        this.getInitialParticipantsCount();

      };
    }
  }

  @HostListener('window:beforeunload')
  ngOnDestroy() {
    if (this.api) {
      this.api.dispose();
    }
    if (this.jitsiWindow) {
      this.jitsiWindow.close();
    }
    this.router.navigate(['/instructor/']);
  }

  handleClose = () => {
    console.log("handle close");
  }

  handleParticipantLeft = () => {
    this.numberOfParticipants--;
    console.log('Number of participants:', this.numberOfParticipants);
  }

  handleParticipantJoined = () => {
    this.numberOfParticipants++;
    console.log('Number of participants:', this.numberOfParticipants);
  }

  handleVideoConferenceJoined = () => {
    this.numberOfParticipants = this.api.getNumberOfParticipants();
    console.log('Number of participants:', this.numberOfParticipants);
  }

  handleVideoConferenceLeft = () => {
    this.numberOfParticipants = 0;
    console.log('Number of participants:', this.numberOfParticipants);
  }

  handleMuteStatus = (audio: any) => {
    console.log('handleMuteStatus', audio);
  }

  handleVideoStatus = (video: any) => {
    console.log('handleVideoStatus', video);
  }

  getParticipants() {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        resolve(this.getParticipants());
      }, 500);
    });
  }

  executeCommand(command: string) {
    if (this.api) {
      this.api.executeCommand(command);
      if (command === 'hangup') {
        this.router.navigate(['/instructor']);
      }
      if (command === 'toggleAudio') {
        this.isAudioMuted = !this.isAudioMuted;
      }
      if (command === 'toggleVideo') {
        this.isVideoMuted = !this.isVideoMuted;
      }
    }
  }

  changeRoom() {
    if (this.roomName.trim() !== '') {
      this.showIframe = true;
      this.setupJitsiMeet();
    } else {
      this.showWarningMessage('Please enter a room name');
    }
  }

  convertBlobToDataURL(blob: Blob): void {
    if (blob) {
      const reader = new FileReader();
      reader.onloadend = () => {
        this.userImageSrc = reader.result as string;
      };
      reader.readAsDataURL(blob);
    }
  }

  shouldDisableCreateRoomButton(): boolean {
    return this.roomName.trim() === '';
  }

  private showWarningMessage(message: string): void {
    this.toastr.warning(message, 'Warning', {
      positionClass: 'toast-center-center',
      toastClass: 'custom-toast-success',
    });
  }

  async getInitialParticipantsCount() {
    try {
      const participants = await this.api.getParticipantsInfo();
      this.numberOfParticipants = participants.length;
    } catch (error) {
      console.error('Error getting initial participants count:', error);
    }
  }
}
