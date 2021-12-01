Feature: Search a city name

  Scenario Outline: Enter a city name in the search bar.
    Given I type the city name "<city_name>"
    And I click on "search" on the "Main Activity" page
    Then the "<result>" on the "Main Activity" page should contain "weather information"

  Examples:
    | city_name    |
    | London       |
    | Kuala Lumpur |
    | Sydney       |
    | Tokyo        |