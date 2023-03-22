# Decentralized Twitter

This is a system where we create a twitter like app. One `Peer` connects to a `Registry`, and any other `Peer` that connects to the `Registry` will receive any messages sent by any `Peer`.

This project was originally created for my distributed systems class. I then refactored it in my advanced programming class.

## 💻 Technology & Concepts

### `Socket Programming`

### `Threads`

### `Testing`

### `Refactoring`

### `Distributed Systems Design`

- I got this project from my distributed systems class. This was a solo project.

- You can run this code, but it won't work without a Registry setup on another machine and at least 1 other peer.
- This is a large system and sadly by the time of submission the code will not be 100% running and bug free.
- I will keep working on this as the history is documented by git with time and date so that I can further refactor so that I can show this off to employers

## 🏃 Running The Registry

1. Make sure you have Java JDK 17.
2. Navigate outside of the `registry/` folder.
3. Compile the entire folder of `registry` with `javac registry/*.java`
4. Run with `java registry.Registry`
   - If this doesn't work, try `java registry/Registry`

## 🏃 Running the Peer(s)\

1. Make sure you have Java JDK 17.
   To be completed...

## 📝 Changelog

### `Version 1.2`

Preparing this code base for use in my resume. Demonstrates strong programming skills.

- Removed `Main.java` and renamed `Iteration3Solution.java` to contain the short piece of code.
- Added System updates with print statements
- `Main.java` renamed and separated methods to do only what they're named to do.
- `NetworkHandler.java` now contains proper comments and proper error handling.


### `Version 1.1`

This project was refactored and cleaned up using [Refactoring Guru's](https://refactoring.guru/refactoring) code smells and solutions.

- Added settings file
- Large Class: Removed non cohesive methods from Main.java
- Long Methods: Separated, the GenerateReport method into smaller methods
- Long Params: Shortened the length of parameters by passing objects instead
- Removed Dead Code
- Removed Duplicate Code
- Overuse of Primitive Types caused a lot of confusion. Thus, creating objects and getter/setters simplifies the system.
  - picture

### `Version 1`

This project was completed after the CPSC 559 class ended.

