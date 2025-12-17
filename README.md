# Live Scoring System Software for Robotics Competitions

This repository contains the source code for a live scoring system designed for robotics competitions. The system allows real-time tracking and display of scores, team performance, and match statistics.

## Setup Guide

### Live Scoring System introduction
The Live Scoring System is a web-based application that provides real-time updates on scores and statistics during robotics competitions. It is designed to be user-friendly and accessible from various devices, including desktops, tablets, and smartphones.

**Who uses the Live Scoring System?**
- **Event Organizers:** To manage and display scores during competitions, ensuring smooth operation and engagement.
- **Scorekeepers:** To input scores and update match information in real-time.
- **Teams and Spectators:** To view live scores and match statistics as shown on large screens at the event.

### Hardware Requirements
To run the Live Scoring System effectively, the following hardware is recommended:
**Displays**:
- Large monitors or projectors for displaying scores to the audience, preferably with HDMI and 27-inch or larger screens for better visibility.
- For this software version, at least one display for each field of the event is recommended for on-field live scoring and timer.

**Tables**:
- Android and iOS tablets are both compatible with the FTC-Live software. Recommended tablet specifications include:
  - Screen Size: 9 inches or larger
  - Operating System: Android 8.0+ or iOS 12.0+
  - Connectivity: Wi-Fi capability for network access
  - App: Using Chrome browser for best compatibility
  
**Network**:
- Use a dedicated local network to connect your scoring devices, ensure a secure and stable connection.
- To ensure security for the system, preventing outside access, it is recommended to use a local network and create policies to restrict access to only authorized devices.
- Use a wired connection (Ethernet) whenever possible. Note: The real-time Scoring Referee
  tablets are wireless devices.

### System Requirements and Operation Architecture
The Live Scoring System is designed to operate on Windows and macOS platforms. Below are the system requirements and operation architecture:

**System Requirements**:
- Operating System: Windows 10 or macOS 10.15 (Catalina) or later
- Web Browser: Google Chrome (latest version recommended)
- 4 GB RAM minimum (8 GB recommended)
- 500 MB of free disk space
- Ethernet adapter (recommended for wired connections)

**Tablets Requirements**:
- Operating System: Android 8.0+ or iOS 12.0+
- Web Browser: Google Chrome (latest version recommended)
- Wi-Fi capability for network access

The Live Scoring System in real-life could be operated flexibly in different architectures based on the event's scale and requirements. These above-mentioned hardware and system requirements ensure optimal performance and reliability during competitions.

**Operation Architecture**:

Most commonly used cofiguration is as follows:
![Operation Architecture](docs/image/operation-archtecture.jpg)

1. **Scoring server laptop**: This laptop hosts the Live Scoring System application and manages all scoring data. It should be connected to the local network via Ethernet for stability.
2. **Field display monitors**: These monitors are connected to the scoring server laptop and display real-time scores and match information to the audience.
3. **Referee tablets**: These tablets are used by referees to input scores and update match information. They connect to the scoring server laptop via the local network, preferably using Wi-Fi.
4. **Network switch/router**: A dedicated network switch or router is used to create a local network for the scoring system, ensuring secure and reliable communication between devices.
