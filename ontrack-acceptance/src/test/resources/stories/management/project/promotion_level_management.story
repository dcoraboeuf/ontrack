Story: Managing the promotion levels

Scenario: The promotion level page must display the list of associated validation stamps

Given a promotion level PLM1PRJ/PLM1BRCH/PLM1 exists with description "Promotion level with validation stamps"
And a validation stamp PLM1PRJ/PLM1BRCH/PLM1VS1 exists with description "VS1"
And a validation stamp PLM1PRJ/PLM1BRCH/PLM1VS2 exists with description "VS2"
And the validation stamp PLM1PRJ/PLM1BRCH/PLM1VS1 is associated with PLM1
And the validation stamp PLM1PRJ/PLM1BRCH/PLM1VS2 is associated with PLM1
And the promotion level PLM1PRJ/PLM1BRCH/PLM1 is auto promoted
When I am on the promotion level page for PLM1PRJ/PLM1BRCH/PLM1
Then on the promotion level page, I see the validation stamp PLM1VS1
And on the promotion level page, I see the validation stamp PLM1VS2
And on the promotion level page, I see that the promotion level is autopromoted
