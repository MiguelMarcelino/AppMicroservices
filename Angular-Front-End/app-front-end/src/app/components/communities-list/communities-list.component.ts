import { Component, OnInit } from '@angular/core';
import { CommunityModel } from 'src/app/models/community.model';
import { CommunitiesService } from 'src/app/services/controllers/communities-controller.service.ts.service';

@Component({
  selector: 'app-communities-list',
  templateUrl: './communities-list.component.html',
  styleUrls: ['./communities-list.component.scss']
})
export class CommunitiesListComponent implements OnInit {

  communities: CommunityModel[];

  constructor(
    private communitiesService: CommunitiesService
    ) 
  { }

  ngOnInit(): void {
    this.getCommunityList();
  }

  getCommunityList(): void {
    this.communitiesService.getAll().subscribe((communitiesList) => {
      this.communities = communitiesList;
    })
  }
}