# Baccaratio Documentation

## Overview

Baccaratio is a backend service built with Spring Boot, designed to simulate the casino game Baccarat. It allows players to place bets, play rounds, and track their chip balances. This service is RESTful, making it easy to integrate with front-end applications.

## Setup

### Prerequisites

- Java JDK 11 or later (the backend was written in Java 17)
- Maven
- Angular

### Running the Service

1. Clone the repository.
2. Navigate to the project directory.
3. Run the application using Maven:

```shell 
mvn spring-boot:run
```

The service will start and be available at `http://localhost:8080`.

## API Endpoints

#### Base URL

All URLs referenced in the documentation have the base path `/baccarat`.

#### Cross-Origin Resource Sharing (CORS)

The API supports cross-origin requests from `http://localhost:4200`.

### Place a Bet

-   **Endpoint**: `/baccarat/bet/{type}/{amount}`
-   **Method**: `POST`
-   **URL Params**:
    -   `type=[string]` (Options: "player", "banker", "tie")
    -   `amount=[integer]` (The amount of chips to bet)
-   **Success Response**:
    -   **Code**: 200
    -   **Content**: `"Bet placed on: {type} with amount: {amount}"`
-   **Error Response**:
    -   **Code**: 400 BAD REQUEST
    -   **Content**: `"Invalid bet type."` or `"Insufficient chips."`

### Play Round

-   **Endpoint**: `/baccarat/play`
-   **Method**: `GET`
-   **Success Response**:
    -   **Code**: 200
    -   **Content**: `"Round result: {result}. Your bet was: {betType}. Remaining chips: {chips}."`
-   **Notes**:
    -   Initiates a round of Baccarat based on the current bet.

### Get Last Result

-   **Endpoint**: `/baccarat/result`
-   **Method**: `GET`
-   **Success Response**:
    -   **Code**: 200
    -   **Content**: `"Last result: {lastResult}. Your bet was: {betType}. Remaining chips: {chips}."`

## Gameplay Mechanics

Players can place bets on the "player", "banker", or a "tie". The game proceeds to draw cards for both the player and banker according to Baccarat rules. The outcome is determined by comparing total points, aiming to get as close to 9 as possible.

## Player Management

Players start with an initial chip count. Bets are deducted at the start of a round, and winnings are added to the player's chip count at the end of the round, according to the outcome and type of bet placed.