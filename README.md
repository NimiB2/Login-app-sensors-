# Login Activity Application

## Overview

The Login Activity Application is an interactive Android app designed to guide users through a series of tasks, each represented by a circle. Completing all tasks successfully transitions the user to the main activity.

## Features

- **Interactive Task Circles:** Each circle corresponds to a specific task. Tapping a circle reveals instructions; completing the task changes the circle's color to green.
- **Sensor-Based Tasks:**
  - **Temperature Task:** Detects ambient temperature exceeding 30°C.
  - **Light Task:** Monitors ambient light levels surpassing 100 lux.
  - **Motion Task:** Recognizes rapid back-and-forth movements along the Z-axis.
- **Voice Recognition Task:** Prompts users to say "Unlock" to complete the task.
- **Drag-and-Drop Task:** Involves dragging a specific circle to a designated position.
- **State Persistence:** Retains task completion states across sessions unless the app is fully exited.
- **Portrait Mode Lock:** The app interface remains in portrait orientation regardless of device rotation.


## Permissions

The app requires the following permissions:

- **Microphone Access:** For voice recognition tasks.
- **Sensors Access:** To monitor ambient temperature, light, and motion.

## Usage

- **Task Instructions:** Tap a circle to view the corresponding task instructions.
- **Completing Tasks:** Follow the on-screen instructions to complete each task.
- **Progress Tracking:** Completed tasks are indicated by green circles.
- **Transition to Main Activity:** Upon completing all tasks, the app navigates to the main activity.


---
