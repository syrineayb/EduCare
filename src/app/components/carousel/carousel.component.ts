import { Component } from '@angular/core';

@Component({
  selector: 'app-carousel',
  templateUrl: './carousel.component.html',
  styleUrls: ['./carousel.component.css']
})
export class CarouselComponent {
  carouselItems = [
    {
      imageUrl: '/assets/img/study-online.jpg',
      title: 'Explore New Horizons',
      subtitle: 'Discover Exciting Learning Opportunities',
      description: 'Expand your knowledge and skills with our wide range of online courses. Whether you\'re looking to learn a new language, enhance your coding skills, or delve into the world of digital marketing, we\'ve got you covered.'
    },
    {
      imageUrl: '/assets/img/video-call.jpg',
      title: 'Learn From Anywhere',
      subtitle: 'Access Education From the Comfort of Your Home',
      description: 'Experience the convenience of online learning. With our platform, you can access high-quality educational content anytime, anywhere. Join thousands of learners worldwide who are achieving their learning goals with us.'
    },
    {
      imageUrl: '/assets/img/graduation.jpg',
      title: 'Master Your Skills',
      subtitle: 'Unlock Your Potential',
      description: 'Take your expertise to the next level with our advanced courses designed to help you master your craft. Whether you\'re a seasoned professional or just starting your journey, our courses will empower you to achieve your goals.'
    },
   /* {
      imageUrl: '/assets/img/graduation.jpg',
      title: 'Explore Cutting-Edge Technologies',
      subtitle: 'Stay Ahead of the Curve',
      description: 'Stay updated with the latest trends and technologies in your field. Our expert-led courses cover a wide range of topics, including artificial intelligence, blockchain, cloud computing, and more. Join us and stay ahead of the curve!'
    }

    */
  ];
}
