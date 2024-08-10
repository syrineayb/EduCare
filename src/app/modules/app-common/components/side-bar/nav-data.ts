export interface NavItem {
  routeLink: string;
  icon: string;
  label: string;
}

export interface NavbarData {
  [key: string]: NavItem[]; // Index signature to allow any string key with value of NavItem[]
  instructor: NavItem[];
  candidate: NavItem[];
  admin: NavItem[];
}

export const navbarData: NavbarData = {
  instructor: [
    {
      routeLink: '/instructor',
      icon: 'fas fa-tachometer-alt', // Dashboard icon
      label: 'Dashboard'
    },
    {
      routeLink: 'course-management',
      icon: 'fas fa-chalkboard-teacher', // Course management icon
      label: 'Course Management'
    },
    {
      routeLink: 'quiz-management',
      icon: 'fas fa-question-circle', // Quiz management icon
      label: 'Quiz Management'
    },
    {
      routeLink: 'video-conference',
      icon: 'fas fa-video', // Video conference icon
      label: 'Video Conference'
    },
    {
      routeLink: 'meeting-schedule-management',
      icon: 'fas fa-calendar-alt', // Meeting schedule icon
      label: 'Meeting Schedule'
    },
    {
      routeLink: 'forum',
      icon: 'far fa-comments', // Forum icon
      label: 'Forum' // Forum label
    },
    {
      routeLink: 'profile',
      icon: 'fas fa-user-cog', // Profile & settings icon
      label: 'Profile & Settings'
    }
  ],
  candidate: [
    {
      routeLink: '/candidate',
      icon: 'fas fa-tachometer-alt', // Dashboard icon
      label: 'Dashboard'
    },
    {
      routeLink: 'course-catalog', // Assuming 'course-catalog' is the route to the course catalog
      icon: 'fal fa-book',
      label: 'Course Catalog' // Update label to 'Course Catalog'
    },
    {
      routeLink: 'my-course',
      icon: 'fal fa-book',
      label: 'My Courses'
    },
    {
      routeLink: 'my-meeting-schedule',
      icon: 'fas fa-calendar-alt', // Meeting schedule icon
      label: 'My Meeting Schedule' // Changed label for Meetings
    },
    {
      routeLink: 'profile',
      icon: 'fal fa-user-edit', // Updated icon for Profile & Settings
      label: 'Profile & Settings'
    },
  ],
  admin: [
    {
      routeLink: '/admin',
      icon: 'fas fa-tachometer-alt', // Dashboard icon
      label: 'Dashboard'
    },
    {
      routeLink: 'account-management',
      icon: 'fal fa-users-cog', // Manage Accounts icon
      label: 'Manage Accounts'
    },
    {
      routeLink: 'topic-management',
      icon: 'fal fa-book', // Manage Topics icon
      label: 'Manage Topics'
    },
    {
      routeLink: 'profile',
      icon: 'fal fa-user-edit', // Updated icon for Profile & Settings
      label: 'Profile & Settings'
    },
  ]
};
