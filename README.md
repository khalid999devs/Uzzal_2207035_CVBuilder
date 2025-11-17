# CV Builder - 2207035

# CV Builder - 2207035

A desktop application built with JavaFX that allows users to create professional CVs quickly and easily. The application provides an intuitive interface where users can input their personal information, work experience, education, skills, and projects, then preview and edit their CV before saving.

## Demo

![CV Builder Demo](demo.gif)

## Development Diary

### Initial Setup

- Established basic project structure with Maven
- Configured JavaFX dependencies and module settings
- Connected project to remote repository for version control

### Core Development

- Designed and implemented three main screens: dashboard, CV creation form, and preview interface
- Created controller classes to handle user interactions and data flow between views
- Developed data management system to persist CV information during the session
- Integrated file chooser functionality for profile photo uploads

### UI Enhancement Phase

- Implemented external CSS styling for consistent design across all screens
- Redesigned create-cv form with two-column layout for better space utilization
- Added responsive image handling for banner and profile photos
- Created custom styling classes for buttons, form fields, and sections
- Standardized padding and spacing across all views for visual consistency

### Validation and Polishing

- Added email validation using regex pattern matching
- Implemented phone number validation for Bangladesh format
- Removed success alerts to streamline user workflow
- Cleaned up code by removing unnecessary comments and unused imports
- Positioned Edit CV button as floating element in preview screen
