Story: Exploring the projects

Scenario: List of projects on the home page
Meta:
@smoke true

Given a project TEST1 exists with description "Description for TEST1"
When I am on the home page
Then I see the TEST1 project with description "Description for TEST1"

Scenario: Creating a project only for administrators

When I am on the home page
And I am not logged
Then I cannot create a project

Scenario: Creating a project
Meta:
@prod false

Given the project PRJ1 does not exist
When I am on the home page
And I am logged as "admin"
And I create a PRJ1 project with description "Description for the PRJ1 project"
Then I am on the PRJ1 project page
When I close the project page
Then I see the PRJ1 project with description "Description for the PRJ1 project"
