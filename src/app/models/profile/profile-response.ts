export interface ProfileResponse {
  profileId?: number;
  firstName?: string;
  lastName?: string;
  email?: string;
  profileImage?: Blob;
  description?: string;
  phoneNumber?: string;
  country?: string;
  currentJob?: string;
  experience?: number;
  createdAt?:string;
  linkedInUrl?:string
  githubUrl?:string
  twitterUrl?:string
  gender?:string
}
