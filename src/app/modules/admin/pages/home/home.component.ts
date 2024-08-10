import { Component, OnInit } from '@angular/core';
import { AccountService } from "../../../../services/account/account.service";
import { UserStats } from "../../../../models/account/UserStats";
import { Chart } from "angular-highcharts";
import { AuthenticationService } from "../../../../services/auth/authentication.service";

@Component({
    selector: 'app-home',
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
    userStats: UserStats = {
        countnbrUsers: 0,
        countnbrActiveUsers: 0,
        countnbrStudents: 0,
        countnbrInstructors: 0
    };
    chart: Chart = new Chart({ /* pie chart configuration */ });
    lineChart: Chart = new Chart({ /* line chart configuration */ });
    username: string | null = '';

    constructor(
        private accountService: AccountService,
        private authService: AuthenticationService,
    ) { }

    ngOnInit(): void {
        this.username = this.authService.getUsername();
        this.getUserStats();
    }

    getUserStats(): void {
        this.accountService.getUserStats().subscribe(
            (stats: UserStats) => {
                this.userStats = stats;
                this.updateCharts();
            },
            (error) => {
                console.error('Error fetching user stats:', error);
            }
        );
    }

    updateCharts() {
        // Update the pie chart with user statistics data
        this.chart = new Chart({
            chart: {
                type: 'pie',
                height: 325
            },
            title: {
                text: 'User Distribution' // Updated title here
            },
            series: [{
                type: 'pie',
                data: [
                    { name: 'Users', y: this.userStats.countnbrUsers, color: '#6EC6CA' },
                    { name: 'Candidates', y: this.userStats.countnbrStudents, color: '#cac4ce' },
                    { name: 'Instructors', y: this.userStats.countnbrInstructors, color: 'var(--custom-color)' }
                ]
            }],
            credits: { enabled: false }
        });

        // Update the line chart with user activity data
        this.lineChart = new Chart({
            chart: {
                type: 'line',
                height: 325
            },
            title: {
                text: 'User Activity'
            },
            xAxis: {
                categories: ['Users', 'Active Users', 'Inactive Users']
            },
            yAxis: {
                title: {
                    text: 'Count'
                }
            },
            series: [{
                type: 'line',
                name: 'User Activity',
                data: [
                    ['Users', this.userStats.countnbrUsers],
                    ['Active Users', this.userStats.countnbrActiveUsers],
                    ['Inactive Users', this.userStats.countnbrUsers - this.userStats.countnbrActiveUsers]
                ]
            }],
            credits: { enabled: false }
        });
    }
}
