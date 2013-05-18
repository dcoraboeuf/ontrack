Story: Connecting (or not) to the application

Scenario: Not logged in

Given a user is defined with name "test01", full name "Test 01" and password "test01"
When I am on the home page
And I am not logged
Then I do not see my user name

Scenario: Signing in and out

Given a user is defined with name "test02", full name "Test 02" and password "test02"
When I am on the home page
And I am logged as "test02"
Then I see I am connected as "Test 02"
When I am not logged
Then I do not see my user name
