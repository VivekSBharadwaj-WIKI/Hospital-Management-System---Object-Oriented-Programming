# 🏥 Hospital Management System

A comprehensive, production-ready Hospital Management System built with Java demonstrating advanced Object-Oriented Programming (OOP) concepts. This system manages all aspects of hospital operations including patient care, staff management, billing, pharmacy, laboratory services, and emergency department operations.

![Java](https://img.shields.io/badge/Java-17+-orange.svg)
![OOP](https://img.shields.io/badge/OOP-Concepts-blue.svg)
![Lines of Code](https://img.shields.io/badge/Lines%20of%20Code-1800+-green.svg)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)

## 📋 Table of Contents

- [Features](#features)
- [OOP Concepts Demonstrated](#oop-concepts-demonstrated)
- [System Architecture](#system-architecture)
- [Class Hierarchy](#class-hierarchy)
- [Installation](#installation)
- [Usage](#usage)
- [Code Structure](#code-structure)
- [Key Components](#key-components)
- [Design Patterns](#design-patterns)
- [Contributing](#contributing)
- [License](#license)

## ✨ Features

### Core Functionality
- **Patient Management**: Complete patient registration, medical history tracking, and health records
- **Staff Management**: Doctor, nurse, and administrator management with role-based access
- **Appointment System**: Schedule, reschedule, and cancel appointments with real-time availability
- **Billing & Insurance**: Comprehensive billing system with insurance claim processing
- **Pharmacy Management**: Inventory tracking, prescription dispensing, and stock alerts
- **Laboratory Services**: Test ordering, result management, and reporting
- **Emergency Department**: Patient triage system with priority-based queue management
- **Room Management**: Track room occupancy, maintenance, and patient admissions
- **Financial Reporting**: Revenue tracking, payment processing, and financial analytics

### Advanced Features
- Password authentication with SHA-256 hashing
- Multi-level staff hierarchy with different access levels
- Prescription management with refill tracking
- Vital signs monitoring and medication logs
- Performance review system for staff
- Low stock alerts for pharmacy inventory
- Comprehensive report generation
- Insurance policy management and claims processing

## 🎯 OOP Concepts Demonstrated

### 1. **Encapsulation**
```java
private String passwordHash;
protected String employeeId;

public boolean authenticate(String password) {
    return passwordHash.equals(hashPassword(password));
}
```
- Private fields with controlled access through getters/setters
- Data hiding and information security

### 2. **Inheritance**
```
Person (Abstract)
├── MedicalStaff (Abstract)
│   ├── Doctor
│   ├── Nurse
│   └── Administrator
└── Patient
```
- Multi-level inheritance hierarchy
- Code reusability and logical organization
- `extends` keyword for class inheritance

### 3. **Polymorphism**
```java
Person person1 = new Doctor(...);
Person person2 = new Patient(...);
System.out.println(person1.getType()); // "Doctor"
System.out.println(person2.getType()); // "Patient"
```
- Method overriding
- Runtime polymorphism
- Dynamic method dispatch

### 4. **Abstraction**
```java
abstract class Person implements Identifiable {
    public abstract String getType();
}
```
- Abstract classes and methods
- Interface-based abstraction
- Hiding implementation details

### 5. **Interfaces**
```java
interface Billable {
    double calculateCharges();
    Map<String, Object> generateInvoice();
}
```
- Multiple interface implementation
- `Identifiable`, `Authenticatable`, `Billable`, `Schedulable`, `Reportable`
- Contract-based programming

### 6. **Composition**
```java
class Hospital {
    private Map<String, Doctor> doctors;
    private Map<String, Room> rooms;
    private EmergencyDepartment emergencyDept;
}
```
- "Has-a" relationship
- Strong ownership between objects
- Lifecycle management

### 7. **Aggregation**
```java
class Doctor {
    private List<Patient> patients; // Doctor has patients
}
```
- Loose coupling between objects
- Independent lifecycle of aggregated objects

### 8. **Association**
```java
class Appointment {
    private Patient patient;
    private Doctor doctor;
}
```
- Objects work together but remain independent
- Bi-directional relationships

### 9. **Exception Handling**
```java
try {
    hospital.admitPatient(patient, doctor, RoomType.ICU, "Emergency");
} catch (InsufficientResourceException e) {
    System.err.println("No ICU beds available: " + e.getMessage());
}
```
- Custom exception classes
- Try-catch-finally blocks
- Proper error propagation

### 10. **Static Members & Methods**
```java
private static int patientCounter = 5000;

private static synchronized String generatePatientId() {
    return "PAT" + (++patientCounter);
}
```
- Class-level variables
- Synchronized ID generation
- Shared state across instances

### 11. **Enumerations**
```java
enum BloodGroup {
    A_POSITIVE("A+"), O_NEGATIVE("O-");
    private final String value;
}
```
- Type-safe constants
- Enhanced readability
- Built-in methods

### 12. **Collections Framework**
```java
Map<String, Doctor> doctors = new HashMap<>();
List<Appointment> appointments = new ArrayList<>();
```
- HashMap for key-value storage
- ArrayList for ordered collections
- Stream API for data processing

### 13. **Lambda Expressions & Streams**
```java
doctors.values().stream()
    .filter(d -> d.getSpecialization().equals("Cardiology"))
    .collect(Collectors.toList());
```
- Functional programming
- Data filtering and transformation
- Modern Java features (Java 8+)

## 🏗️ System Architecture

```
┌─────────────────────────────────────────┐
│   Hospital Management System (HMS)       │
├─────────────────────────────────────────┤
│  ┌─────────────┐  ┌─────────────┐      │
│  │  Hospital   │  │  Insurance  │      │
│  │             │  │  Provider   │      │
│  └─────────────┘  └─────────────┘      │
└─────────────────────────────────────────┘
         │                    │
    ┌────┴────┐         ┌────┴────┐
    ▼         ▼         ▼         ▼
┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐
│ Staff  │ │Patient │ │ Rooms  │ │Policy  │
│Management│ │ Care  │ │ & Beds │ │Claims  │
└────────┘ └────────┘ └────────┘ └────────┘
    │         │         │         │
    ▼         ▼         ▼         ▼
┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐
│Doctors │ │Medical │ │Admission│ │Billing │
│Nurses  │ │Records │ │Discharge│ │Payment │
│Admins  │ │Prescr. │ │         │ │        │
└────────┘ └────────┘ └────────┘ └────────┘
```

## 📊 Class Hierarchy

### Core Classes (25+)

#### **Person Hierarchy**
- `Person` (Abstract) - Base class for all people
  - `MedicalStaff` (Abstract) - Base for hospital staff
    - `Doctor` - Medical doctors with specializations
    - `Nurse` - Nursing staff with certifications
    - `Administrator` - Hospital administrators
  - `Patient` - Hospital patients

#### **Medical Services**
- `Appointment` - Patient-doctor appointments
- `MedicalRecord` - Patient medical history
- `Prescription` - Medication prescriptions
- `LabTest` - Laboratory test results
- `Admission` - Patient hospital admissions

#### **Facilities**
- `Room` - Hospital rooms and beds
- `Laboratory` - Lab facilities and tests
- `Pharmacy` - Medication inventory
- `EmergencyDepartment` - Emergency services

#### **Financial**
- `Bill` - Patient billing
- `InsurancePolicy` - Insurance coverage
- `InsuranceProvider` - Insurance companies

#### **Data Classes**
- `Address` - Physical addresses
- `ContactInfo` - Contact information
- `Inventory` - Stock management

#### **Management**
- `Hospital` - Main hospital entity
- `HospitalManagementSystem` - System controller

### Interfaces
- `Identifiable` - Objects with unique IDs
- `Authenticatable` - Login functionality
- `Billable` - Billing capabilities
- `Schedulable` - Appointment scheduling
- `Reportable` - Report generation

### Enumerations
- `BloodGroup` - Blood types
- `AppointmentStatus` - Appointment states
- `RoomType` - Types of hospital rooms
- `PrescriptionStatus` - Prescription states
- `PaymentMethod` - Payment options

### Custom Exceptions
- `ValidationException` - Data validation errors
- `AuthenticationException` - Login failures
- `ResourceNotFoundException` - Missing resources
- `InsufficientResourceException` - Unavailable resources

## 🚀 Installation

### Prerequisites
- Java Development Kit (JDK) 17 or higher
- Any Java IDE (IntelliJ IDEA, Eclipse, VS Code) or command line

### Steps

1. **Clone the repository**
```bash
git clone https://github.com/yourusername/hospital-management-system.git
cd hospital-management-system
```

2. **Compile the code**
```bash
javac HospitalManagementDemo.java
```

3. **Run the application**
```bash
java HospitalManagementDemo
```

## 💻 Usage

### Basic Example

```java
// Create a hospital
Hospital hospital = new Hospital("City General Hospital", address, contact);

// Register a doctor
Doctor doctor = new Doctor("John", "Doe", birthDate, "Male", 
                          address, contact, "DOC001", "Cardiology", 
                          150000.0, joinDate, "Cardiologist", 
                          "MCI12345", 1500.0);
hospital.registerDoctor(doctor);

// Register a patient
Patient patient = new Patient("Jane", "Smith", birthDate, "Female",
                             address, contact, BloodGroup.O_POSITIVE, 
                             "INS123");
hospital.registerPatient(patient);

// Schedule an appointment
Appointment apt = hospital.scheduleAppointment(patient, doctor, 
                                              dateTime, "Checkup");

// Create prescription
List<Map<String, Object>> meds = new ArrayList<>();
// Add medications...
Prescription prescription = doctor.prescribeMedication(patient, meds);

// Admit patient
Admission admission = hospital.admitPatient(patient, doctor, 
                                           RoomType.PRIVATE, "Surgery");

// Generate bill
Bill bill = new Bill(patient);
bill.addConsultationFee(doctor);
bill.addRoomCharges(admission);
bill.processPayment(PaymentMethod.CARD);
```

### Advanced Features

#### Emergency Department Triage
```java
EmergencyDepartment ed = new EmergencyDepartment(20);
hospital.setupEmergencyDepartment(20);
ed.triagePatient(patient, 2, "Chest pain"); // Severity 1-5
```

#### Insurance Claim Processing
```java
InsuranceProvider provider = new InsuranceProvider("HealthCare Plus", 
                                                   policyTypes);
InsurancePolicy policy = provider.createPolicy(patient, "Premium", 
                                              500000.0, 15000.0);
Map.Entry<Boolean, Double> claim = provider.processClaim(policyNumber, bill);
```

#### Laboratory Tests
```java
Laboratory lab = new Laboratory("Central Lab", "Ground Floor");
lab.addTestType("Blood Test", 500.0, 2);
LabTest test = lab.orderTest(patient, "Blood Test", doctor);
lab.completeTest(test, "Results here", "Tech Name");
```

#### Pharmacy Management
```java
Pharmacy pharmacy = new Pharmacy("Main Pharmacy", "First Floor");
pharmacy.addMedication("Aspirin", 100, 50.0, 20);
Map<String, Object> sale = pharmacy.dispenseMedication(prescription, 
                                                       "Pharmacist");
List<Inventory> lowStock = pharmacy.getLowStockItems();
```

## 📁 Code Structure

```
hospital-management-system/
├── HospitalManagementDemo.java    # Main file with all classes
├── README.md                       # This file
├── .gitignore                      # Git ignore file
└── docs/                          # Documentation (optional)
    ├── class-diagrams/
    ├── sequence-diagrams/
    └── use-cases/
```

## 🔑 Key Components

### 1. Patient Management
- Complete patient registration with personal and medical information
- Medical history tracking with diagnoses and treatments
- Vital signs monitoring and medication logs
- Allergy and chronic condition management

### 2. Staff Management
- Role-based access control (Doctor, Nurse, Administrator)
- Authentication system with password hashing
- Shift scheduling and management
- Performance review system

### 3. Appointment System
- Real-time slot availability checking
- Appointment scheduling, rescheduling, and cancellation
- Status tracking (Scheduled, Completed, Cancelled, No Show)
- Consultation notes and follow-up management

### 4. Billing System
- Itemized billing with multiple charge types
- Tax calculation and discount application
- Multiple payment methods support
- Insurance claim integration

### 5. Room Management
- Different room types (General, Private, ICU, Emergency, Operation)
- Occupancy tracking and maintenance scheduling
- Daily charge calculation
- Amenities management

### 6. Pharmacy
- Medication inventory with stock levels
- Prescription dispensing with validation
- Low stock alerts and reorder notifications
- Sales reporting and revenue tracking

### 7. Laboratory
- Test catalog management
- Test ordering and result tracking
- Technician assignment
- Report generation

### 8. Emergency Department
- Priority-based patient triage (1-5 severity levels)
- Queue management for waiting patients
- Bed availability tracking
- Critical case identification

## 🎨 Design Patterns

### 1. **Singleton Pattern**
```java
// ID generation ensures unique identifiers
private static synchronized String generatePatientId()
```

### 2. **Factory Pattern**
```java
// Creating different types of staff
public void registerDoctor(Doctor doctor)
public void registerNurse(Nurse nurse)
```

### 3. **Strategy Pattern**
```java
// Different payment methods
public boolean processPayment(PaymentMethod method)
```

### 4. **Observer Pattern**
```java
// Low stock alerts
public List<Inventory> getLowStockItems()
```

### 5. **Builder Pattern**
```java
// Complex object construction
Bill bill = new Bill(patient);
bill.addConsultationFee(doctor);
bill.addRoomCharges(admission);
bill.applyDiscount(10);
```

## 📈 Features Breakdown

| Feature | Classes Involved | Key Capabilities |
|---------|-----------------|------------------|
| Patient Care | Patient, Doctor, MedicalRecord, Prescription | Medical history, diagnoses, prescriptions |
| Appointments | Appointment, Doctor, Patient | Scheduling, status tracking, notes |
| Admissions | Admission, Room, Patient, Doctor | Room allocation, discharge, duration tracking |
| Billing | Bill, Patient, InsurancePolicy | Itemized charges, payments, insurance claims |
| Pharmacy | Pharmacy, Inventory, Prescription | Stock management, dispensing, alerts |
| Laboratory | Laboratory, LabTest, Doctor, Patient | Test ordering, results, reporting |
| Emergency | EmergencyDepartment, Patient | Triage, queue management, priority handling |
| Staff | Doctor, Nurse, Administrator | Authentication, scheduling, reviews |
| Reporting | Hospital, Laboratory, Pharmacy | Financial, operational, statistical reports |

## 🧪 Testing

### Manual Testing Workflow

1. **Create Hospital Instance**
   - Initialize with address and contact
   - Setup departments (Emergency, Lab, Pharmacy)

2. **Register Staff**
   - Add doctors with specializations
   - Add nurses with certifications
   - Add administrators

3. **Register Patients**
   - Complete patient profile
   - Add medical history
   - Set allergies and conditions

4. **Test Core Workflows**
   - Schedule appointments
   - Admit patients
   - Order lab tests
   - Dispense medications
   - Generate bills
   - Process insurance claims

5. **Verify Reports**
   - Hospital statistics
   - Financial reports
   - Staff performance
   - Patient summaries

## 🔒 Security Features

- **Password Hashing**: SHA-256 encryption for all passwords
- **Access Control**: Role-based permissions
- **Data Validation**: Input validation for all user data
- **Exception Handling**: Graceful error management
- **Audit Trail**: Timestamps on all records

## 📊 Sample Output

```
================================================================================
HOSPITAL MANAGEMENT SYSTEM - DEMONSTRATION
================================================================================

✓ Appointment scheduled: {appointmentId=APT30001, patient=Amit Patel, ...}
✓ Prescription created: PRX20001
✓ Medical record created: Record REC10001: Mild Hypertension by Dr. Rajesh Kumar
✓ Lab test completed: {testId=TEST80001, patientName=Amit Patel, ...}
✓ Patient admitted: {admissionId=ADM40001, patient=Amit Patel, ...}
✓ Vital signs recorded: {bloodPressure=140/90, heartRate=78, ...}
✓ Bill generated:
  Subtotal: ₹4080.00
  Total: ₹4334.00
✓ Insurance claim approved: ₹3467.20
  Patient pays: ₹866.80
✓ Medication dispensed: ₹80.00

================================================================================
HOSPITAL REPORT
================================================================================
HOSPITALID: HOSP1
HOSPITALNAME: City General Hospital
TOTALDOCTORS: 1
TOTALNURSES: 1
TOTALPATIENTS: 2
OCCUPANCYRATE: 16.67%
...
```

## 🤝 Contributing

Contributions are welcome! Here's how you can help:

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/AmazingFeature
   ```
3. **Commit your changes**
   ```bash
   git commit -m 'Add some AmazingFeature'
   ```
4. **Push to the branch**
   ```bash
   git push origin feature/AmazingFeature
   ```
5. **Open a Pull Request**

### Contribution Guidelines
- Follow Java naming conventions
- Add comments for complex logic
- Include unit tests for new features
- Update documentation as needed
- Maintain OOP principles

## 📝 Future Enhancements

- [ ] Database integration (MySQL/PostgreSQL)
- [ ] RESTful API implementation
- [ ] Web-based UI (Spring Boot + React)
- [ ] Mobile application
- [ ] Real-time notifications
- [ ] Analytics dashboard
- [ ] Multi-language support
- [ ] Telemedicine integration
- [ ] Electronic Health Records (EHR) compliance
- [ ] Automated report generation

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Your Name**
- GitHub: [@yourusername](https://github.com/yourusername)
- LinkedIn: [Your Profile](https://linkedin.com/in/yourprofile)
- Email: your.email@example.com

## 🙏 Acknowledgments

- Inspired by real-world hospital management systems
- Built as a comprehensive OOP demonstration project
- Implements industry-standard design patterns
- Follows Java best practices and conventions

## 📞 Support

For support, email your.email@example.com or open an issue on GitHub.

---

⭐ **Star this repository** if you found it helpful!

💡 **Fork it** to build your own version!

🐛 **Report bugs** to help improve the project!
