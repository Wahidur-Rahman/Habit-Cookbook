# Habit Cookbook

Habit Cookbook is an application which strives to support the user in forming any given habit.

## Overview

Habit Cookbook associates an habit with one of the user’s routines. By sending a notification to the user asking for verification thirty minutes after the end of a routine, the user will be able to see whether this routine is an effective association for their habit. Eventually the user will be able to determine a routine which they fully associate with the habit, and the application will phase itself out in response to the user’s successful responses.

## How To Use

* You can create various routines with a start and end time.
* Each routine can have multiple associated habits.
* 30 minutes after the end time of the routine a notification will popup asking you whether the habit associated with that routine has been accomplished.
* Depending on the answer the notification rate will adjust accordingly.
* Ideally you should not set routines that are too close (eg 5 minutes) to the start and end times of another routine to prevent notifications being overwritten.

## Setup

To build this application you are required to install the latest version of Android Studio following the instructions [here] (https://developer.android.com/sdk/index.html).

For installation on an emulator you will also need to download the appropriate SDK via android studio for the emulator you wish to test on (by default one is provided for the latest android version). The application requires version 4.4 or higher. For installation on physical devices you must have developer mode on your android device enabled by following the instructions [here] (https://developer.android.com/tools/device.html).

## Notes

### Notification Phase-Out Rate

By default the alarms are set to repeat every 24 hours multiplied by a rate number. This rate number is determined by a ‘count’ associated with the routine. Initially the count is 1 but goes up each time the user responds positively to an ‘End of Routine’ post-notification where they are required to assess whether they have carried out their intended habits as shown below. When they respond negatively the count goes down by 1 (but never below 1 itself).

To edit how the rate is determined from the count, you can amend the snippet of code below in the AlarmSetUp.java class in the following file directory:

    habit-cookbook\app\src\main\java\com\example\wahidur\fragments\AlarmSetup.java

The relevant code is on line numbers 142-152 in the method labelled **_getRate()_**.

This snippet of code can easily be edited so that the notifications phase out at your preferred rate (if you are not familiar with java please feel free to ask us to edit it ourselves!). In the current form a given alarm set will occur once within 24 hours, and then at 48 hours and then finally at 72 hours where it will continue repeating every 72 hours. It is up to you to decide what level of repetition is appropriate for someone who has formed a habit i.e. once a week (rate = 7).

### JSON File Structure

The JSON file structure of each users routines and habits is given below and described. This structure is sent off to Google Analytics using a custom HitBuilder. It is set up to be sent whenever the user enters the Home Screen as all interactions that lead you to the Home Screen occur only when a change has occurred in the routine structure which immediately updates the JSON file and then sends the JSON String off.

Instances of this structure in the analytics for each user should be different from each iteration and therefore allow you to deduce which changes have occurred. If it is not, one possibility is the application crashed on the Home Page (hopefully won’t happen!) and then reloaded the Home Page so the JSON string at the instance was sent twice with no changes in between. The reason could be that they manually close the app in the task stack and then reopen it. This reloads the home screen and sends another JSON string off. The count in the JSON structure is also reset every time the user edits a routine. This is to prevent ‘cheating’ whereby a user could appear to have formed a habit at night by reaching the final rate number but in reality has done the habits during the day before changing the time to the night.

## Credit

This application was created by Wahidur Rahman, Alessandro Fael, and Alec Howells as part of their Apps Design course for the UCL MSc in Computer Science. It was created for Katarzyna Stawarz, as part of her research into methods to improve habit creation.
