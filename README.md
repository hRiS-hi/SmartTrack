# 📋SmartTrack: Intelligent Attendance Management System

A modern, GUI-based attendance management system built with Scala and ScalaFX that allows Class Representatives (CRs) to submit attendance records and students to check their attendance status.

## Features

- **Student View**: Check attendance status by roll number
- **CR View** (Password Protected):
  - Faculty Management: Add and view faculty members
  - Absentee Record: Submit attendance records
- **Email Notifications**: Automatic email alerts to faculty members
- **MongoDB Integration**: Persistent storage of attendance and faculty data

## Technical Stack

- **Language**: Scala 2.13.11
- **GUI Framework**: ScalaFX 21.0.0-R32
- **Database**: MongoDB
- **Email**: Jakarta Mail API
- **Build Tool**: sbt

## Core Components

### 1. `AttendanceGUI.scala`
The main application file that implements the GUI interface using ScalaFX.
- Creates the main window with two initial buttons:
  - Student View: For checking attendance
  - CR Login: Password-protected access
- After CR authentication, provides access to:
  - Faculty Management: Add and view faculty members
  - Absentee Record: Submit attendance records
- Implements student attendance checking functionality
- Handles CR authentication and attendance submission
- Uses modern UI components like ComboBox, TextArea, and Alert dialogs

### 2. `AttendanceRecord.scala`
Model class for attendance records with MongoDB integration.
- Defines the structure of attendance records
- Handles date/time formatting
- Provides methods to:
  - Save attendance records to MongoDB
  - Retrieve records by roll number
  - Format dates for display

### 3. `Faculty.scala`
Model class for faculty members with MongoDB integration.
- Manages faculty information storage
- Provides methods to:
  - Add new faculty members
  - Retrieve faculty by course code
  - List all faculty members

### 4. `MongoConnection.scala`
Handles database connectivity and operations.
- Establishes connection to MongoDB
- Provides database access methods
- Manages collection operations

### 5. `mail.scala`
Handles email functionality using Jakarta Mail API.
- Configures SMTP settings for Gmail
- Sends attendance notifications to faculty
- Handles email errors gracefully

## Database Structure

### Database Name
`attendance_db`

### Collections

#### 1. `faculty` Collection
Stores faculty member information.

**Attributes:**
- `name` (String): Faculty member's name
- `email` (String): Faculty member's email address
- `courses` (Array): List of course codes taught by the faculty

**Example Document:**
```json
{
  "name": "Dr. John Doe",
  "email": "john.doe@example.com",
  "courses": ["22AIE312", "22AIE313"]
}
```

#### 2. `attendance` Collection
Stores attendance records.

**Attributes:**
- `courseCode` (String): Course code for which attendance is recorded
- `facultyName` (String): Name of the faculty member
- `facultyEmail` (String): Email of the faculty member
- `date` (DateTime): Date and time of the attendance record
- `absentRollNumbers` (Array): List of absent student roll numbers
- `submittedBy` (String): Name of the CR who submitted the record
- `timeSlot` (String): Time slot of the class (e.g., "9:00-9:50")

**Example Document:**
```json
{
  "courseCode": "22AIE312",
  "facultyName": "Dr. John Doe",
  "facultyEmail": "john.doe@example.com",
  "date": ISODate("2024-05-29T09:00:00Z"),
  "absentRollNumbers": ["22", "45", "67"],
  "submittedBy": "CR",
  "timeSlot": "9:00-9:50"
}
```

## Time Slots

The system uses the following time slots:
1. 9:00-9:50
2. 10:00-10:50
3. 11:00-11:50
4. 12:00-12:50
5. 14:00-14:50
6. 15:00-15:50
7. 16:00-16:50

## Setup Instructions

1. **Prerequisites**
   - Java 21 or later
   - MongoDB installed and running
   - sbt installed

2. **Configuration**
   - MongoDB should be running on default port (27017)
   - Update email settings in `mail.scala` with your Gmail credentials
   - Default CR password is "admin123" (can be changed in `AttendanceGUI.scala`)

3. **Running the Application**
   ```bash
   sbt run
   ```

## Security Notes

1. **CR Authentication**
   - Default password is "admin123"
   - Password is stored in plain text (should be hashed in production)

2. **Email Security**
   - Uses Gmail SMTP with App Password
   - TLS encryption enabled
   - Credentials should be stored securely in production

## Error Handling

The system handles various error scenarios:
- Invalid roll numbers
- Network connectivity issues
- Email sending failures
- Database connection problems

## Future Improvements

1. **Security Enhancements**
   - Password hashing
   - Secure credential storage
   - Session management

2. **Features**
   - Student registration
   - Course management
   - Attendance statistics
   - Export functionality

3. **UI Improvements**
   - Dark mode
   - Responsive design
   - Better error messages

## Contributing

Feel free to submit issues and enhancement requests! 
