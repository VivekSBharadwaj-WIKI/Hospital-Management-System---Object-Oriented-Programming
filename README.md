# 🏥 Hospital Management System – Java (OOP Intensive Project)

A **comprehensive Hospital Management System** implemented in **Java**, designed to demonstrate **all core and advanced Object-Oriented Programming (OOP) concepts** in a single, real-world application.

This project goes beyond toy examples — it models an actual hospital ecosystem including **patients, doctors, billing, insurance, pharmacy, laboratory, emergency services, and reporting**.

---

## 🚀 Project Highlights

- **1800+ lines of well-structured Java code**
- **25+ interrelated classes**
- Covers **ALL OOP principles** with real use cases
- Uses **modern Java features** (Collections, Streams, Lambdas)
- Suitable for:
  - OOP / Java academic projects
  - Resume & portfolio projects
  - Interview demonstrations
  - GitHub showcase

---

## 🧠 OOP Concepts Demonstrated

✔ **Encapsulation** – Private fields with controlled access  
✔ **Inheritance** – Multi-level hierarchy (Person → MedicalStaff → Doctor/Nurse/Admin)  
✔ **Polymorphism** – Runtime polymorphism & method overriding  
✔ **Abstraction** – Abstract classes & interfaces  
✔ **Interfaces** – Multiple interface implementation  
✔ **Composition** – Hospital contains Departments, Rooms, Staff  
✔ **Aggregation** – Doctor has Patients (loose coupling)  
✔ **Association** – Appointment links Patient & Doctor  
✔ **Exception Handling** – Custom exceptions with proper error handling  
✔ **Static Members** – Class-level ID generation & counters  
✔ **Enums** – Type-safe constants (BloodGroup, RoomType, etc.)  
✔ **Java Collections** – HashMap, ArrayList, List, Map  
✔ **Streams API** – filter(), map(), collect()  
✔ **Lambda Expressions** – Functional programming features  

---

## 🧱 Class Structure Overview

### Core Abstract Classes
- `Person` (Abstract Base Class)
- `MedicalStaff` (Abstract)

### Inheritance Hierarchy

```
Person (Abstract)
├── MedicalStaff (Abstract)
│   ├── Doctor
│   ├── Nurse
│   └── Administrator
└── Patient
```

### Key Domain Classes

**Medical Services:**
- `MedicalRecord` – Patient diagnosis and treatment history
- `Prescription` – Medication prescriptions with refill tracking
- `Appointment` – Patient-doctor appointment scheduling
- `Admission` – Hospital admission and discharge management
- `LabTest` – Laboratory test orders and results

**Facilities:**
- `Room` – Hospital rooms with occupancy tracking
- `Laboratory` – Lab facilities and test management
- `Pharmacy` – Medication inventory and dispensing
- `EmergencyDepartment` – Emergency triage and patient queue

**Financial:**
- `Bill` – Patient billing with itemized charges
- `InsuranceProvider` – Insurance company management
- `InsurancePolicy` – Policy coverage and claims processing

**Core Management:**
- `Hospital` – Main hospital entity with all departments
- `HospitalManagementSystem` – System-wide management and coordination

**Supporting Classes:**
- `Inventory` – Stock management for pharmacy
- `Address` – Physical address information
- `ContactInfo` – Contact details with validation

---

## 🔌 Interfaces Used

- **`Identifiable`** – Provides unique ID and type identification
- **`Authenticatable`** – Password authentication and management
- **`Billable`** – Billing and invoice generation
- **`Schedulable`** – Appointment scheduling capabilities
- **`Reportable`** – Report generation functionality

These ensure **loose coupling**, **flexibility**, and **clean design**.

---

## 🔗 Object Relationships

| Concept        | Example | Explanation |
|----------------|---------|-------------|
| **Composition** | Hospital → Departments, Rooms, Staff | Strong ownership, lifecycle dependency |
| **Aggregation** | Doctor → Patients | Loose coupling, independent lifecycle |
| **Association** | Appointment → Patient & Doctor | Objects work together but remain independent |

---

## ⚙️ Functional Modules

### 1. Patient Management
- Patient registration with complete profile
- Medical history tracking
- Vital signs monitoring
- Allergy and chronic condition management
- Medication logs

### 2. Doctor & Staff Management
- Doctor registration with specializations
- Nurse assignment and task management
- Administrator access control
- Staff authentication (SHA-256 hashing)
- Shift scheduling
- Performance reviews

### 3. Appointment System
- Real-time slot availability
- Appointment scheduling and rescheduling
- Status tracking (Scheduled, Completed, Cancelled, No Show)
- Consultation notes

### 4. Admission & Room Allocation
- Room type management (General, Private, ICU, Emergency, Operation)
- Patient admission and discharge
- Occupancy tracking
- Room maintenance scheduling
- Duration-based billing

### 5. Pharmacy & Inventory
- Medication stock management
- Prescription dispensing with validation
- Low stock alerts
- Sales tracking and reporting
- Automatic reorder level monitoring

### 6. Laboratory Services
- Test catalog management
- Test ordering by doctors
- Result recording by technicians
- Comprehensive lab reports
- Test cost tracking

### 7. Emergency Department
- Patient triage (Priority levels 1-5)
- Queue management
- Bed availability tracking
- Critical case identification
- Staff assignment

### 8. Billing & Payments
- Itemized billing (consultations, rooms, tests, medications)
- Tax calculation (5%)
- Discount application
- Multiple payment methods (Cash, Card, Insurance, UPI)
- Payment tracking

### 9. Insurance Management
- Policy creation and management
- Coverage amount tracking
- Claim processing (80% coverage)
- Claims history
- Policy renewal

### 10. System Reporting
- Hospital statistics
- Financial reports (revenue, collections)
- Occupancy rates
- Staff performance metrics
- Patient summaries

---

## 🧪 Java Features Used

### Collections Framework
- `ArrayList` – Dynamic patient lists, appointment tracking
- `HashMap` – Staff lookup by ID, room management
- `List` – Ordered collections for medical records
- `Map` – Key-value storage for inventory

### Enumerations
- `BloodGroup` – A+, A-, B+, B-, AB+, AB-, O+, O-
- `RoomType` – General, Private, ICU, Emergency, Operation
- `AppointmentStatus` – Scheduled, Completed, Cancelled, No Show
- `PrescriptionStatus` – Active, Completed, Discontinued
- `PaymentMethod` – Cash, Card, Insurance, UPI

### Streams API
```java
doctors.values().stream()
    .filter(d -> d.getSpecialization().equals("Cardiology"))
    .collect(Collectors.toList());
```
- `filter()` – Filter data based on conditions
- `map()` – Transform data
- `collect()` – Gather results into collections

### Lambda Expressions
```java
rooms.values().stream()
    .filter(r -> !r.isOccupied() && !r.isMaintenanceRequired())
    .findFirst()
    .orElse(null);
```

### Custom Exceptions
- `ValidationException` – Data validation errors
- `AuthenticationException` – Login/password failures
- `ResourceNotFoundException` – Missing resources
- `InsufficientResourceException` – Unavailable resources

All exceptions use proper `try-catch` blocks for robust error handling.

---

## ▶️ How to Run the Project

### Prerequisites
- Java JDK 8 or higher
- Command Line / Terminal

### Compile
```bash
javac HospitalManagementDemo.java
```

### Run
```bash
java HospitalManagementDemo
```

### Expected Output
The program runs a comprehensive demonstration showing:
- Hospital setup and configuration
- Staff registration (Doctors, Nurses, Administrators)
- Patient registration and management
- Appointment scheduling
- Medical records and prescriptions
- Lab test ordering and completion
- Patient admission and discharge
- Billing and insurance claims
- Emergency department operations
- System-wide reports and statistics

---

## 📋 Code Examples

### 1. Encapsulation Example
```java
public class Doctor extends MedicalStaff {
    private String passwordHash;  // Private field
    private double consultationFee;
    
    public boolean authenticate(String password) {
        return passwordHash.equals(hashPassword(password));
    }
    
    public double getConsultationFee() {
        return consultationFee;  // Controlled access
    }
}
```

### 2. Inheritance Example
```java
// Person is the base class
public abstract class Person implements Identifiable {
    protected String firstName, lastName;
    public abstract String getType();
}

// MedicalStaff extends Person
public abstract class MedicalStaff extends Person 
                                implements Authenticatable {
    protected String employeeId;
}

// Doctor extends MedicalStaff
public class Doctor extends MedicalStaff implements Schedulable {
    private String specialization;
    
    @Override
    public String getType() {
        return "Doctor";
    }
}
```

### 3. Polymorphism Example
```java
// Runtime polymorphism
Person person1 = new Doctor(...);
Person person2 = new Patient(...);
Person person3 = new Nurse(...);

// Different implementations called at runtime
System.out.println(person1.getType());  // "Doctor"
System.out.println(person2.getType());  // "Patient"
System.out.println(person3.getType());  // "Nurse"
```

### 4. Interface Implementation
```java
// Multiple interfaces
public class Patient extends Person implements Billable {
    @Override
    public double calculateCharges() {
        return bills.stream()
            .filter(bill -> !bill.isPaid())
            .mapToDouble(Bill::getTotalAmount)
            .sum();
    }
    
    @Override
    public Map<String, Object> generateInvoice() {
        // Generate detailed invoice
    }
}
```

### 5. Exception Handling
```java
try {
    Admission admission = hospital.admitPatient(
        patient, doctor, RoomType.ICU, "Emergency"
    );
} catch (InsufficientResourceException e) {
    System.err.println("No ICU beds available: " + e.getMessage());
} catch (ValidationException e) {
    System.err.println("Invalid data: " + e.getMessage());
}
```

### 6. Streams & Lambda
```java
// Find available doctors
List<Doctor> cardiologists = hospital.getDoctors().values().stream()
    .filter(d -> d.getSpecialization().equalsIgnoreCase("Cardiology"))
    .filter(d -> d.isAvailable(dateTime))
    .collect(Collectors.toList());

// Get low stock items
List<Inventory> lowStock = pharmacy.getInventory().values().stream()
    .filter(Inventory::needsReorder)
    .sorted(Comparator.comparing(Inventory::getQuantity))
    .collect(Collectors.toList());
```

---

## 🎯 Key Design Patterns

### 1. Singleton Pattern
```java
private static synchronized String generatePatientId() {
    return "PAT" + (++patientCounter);
}
```
Ensures unique ID generation across all instances.

### 2. Factory Pattern
```java
public void registerDoctor(Doctor doctor) {
    doctors.put(doctor.getEmployeeId(), doctor);
}

public void registerNurse(Nurse nurse) {
    nurses.put(nurse.getEmployeeId(), nurse);
}
```
Centralized object creation and management.

### 3. Strategy Pattern
```java
public boolean processPayment(PaymentMethod method) {
    // Different strategies for Cash, Card, Insurance, UPI
}
```

### 4. Builder Pattern
```java
Bill bill = new Bill(patient);
bill.addConsultationFee(doctor);
bill.addRoomCharges(admission);
bill.addTestCharges("Blood Test", 500.0);
bill.applyDiscount(10);
```
Step-by-step complex object construction.

---

## 📊 Sample Workflow

### Complete Patient Journey

```java
// 1. Register patient
Patient patient = new Patient("John", "Doe", birthDate, "Male",
                             address, contact, BloodGroup.O_POSITIVE, 
                             "INS123");
hospital.registerPatient(patient);

// 2. Schedule appointment
Appointment apt = hospital.scheduleAppointment(patient, doctor, 
                                              dateTime, "Checkup");

// 3. Doctor diagnoses and prescribes
MedicalRecord record = doctor.diagnose(patient, "Hypertension", 
                                      "Elevated BP detected");
Prescription presc = doctor.prescribeMedication(patient, medications);

// 4. Order lab tests
LabTest test = lab.orderTest(patient, "Blood Test", doctor);
lab.completeTest(test, "Normal ranges", "Technician");

// 5. Admit patient if needed
Admission admission = hospital.admitPatient(patient, doctor, 
                                           RoomType.PRIVATE, "Observation");

// 6. Nurse records vital signs
nurse.recordVitalSigns(patient, vitalSigns);
nurse.administerMedication(patient, "Aspirin", "75mg", LocalDateTime.now());

// 7. Generate bill
Bill bill = new Bill(patient);
bill.addConsultationFee(doctor);
bill.addRoomCharges(admission);
bill.addTestCharges("Blood Test", 500.0);

// 8. Process insurance claim
InsurancePolicy policy = provider.createPolicy(patient, "Premium", 
                                              500000.0, 15000.0);
Map.Entry<Boolean, Double> claim = provider.processClaim(
                                      policy.getPolicyNumber(), bill);

// 9. Patient pays remaining amount
bill.processPayment(PaymentMethod.CARD);

// 10. Discharge patient
hospital.dischargePatient(admission, "Recovered, continue medication");
```

---

## 📈 Statistics & Reporting

### Hospital Report
```
Total Doctors: 5
Total Nurses: 10
Total Patients: 150
Total Rooms: 50
Occupancy Rate: 78.5%
Active Appointments: 25
Laboratories: 2
Pharmacies: 1
```

### Financial Report
```
Period: 2024-01-01 to 2024-01-31
Total Revenue: ₹5,45,000
Total Paid: ₹4,20,000
Total Pending: ₹1,25,000
Collection Rate: 77.06%
```

### Doctor Statistics
```
Total Patients: 45
Total Consultations: 120
Total Appointments: 150
Specialization: Cardiology
```

---

## 🔒 Security Features

- **Password Hashing**: SHA-256 encryption for all passwords
- **Access Control**: Role-based permissions (Doctor, Nurse, Administrator)
- **Data Validation**: Input validation for phone, email, postal codes
- **Exception Handling**: Graceful error management
- **Audit Trail**: Timestamps on all records and transactions

---

## 🌟 Why This Project Stands Out

### 1. Real-World Complexity
Not a simple student project – models actual hospital operations with realistic business logic.

### 2. Complete OOP Coverage
Every major OOP concept is demonstrated with practical, meaningful examples.

### 3. Modern Java Features
Uses Java 8+ features including Streams, Lambdas, and functional programming paradigms.

### 4. Scalable Architecture
Well-structured code that can be extended with databases, APIs, or web interfaces.

### 5. Production-Ready Practices
- Proper exception handling
- Input validation
- Secure password storage
- Comprehensive documentation
- Clean code principles

---

## 🎓 Learning Outcomes

After studying this project, you will understand:

✅ How to design complex class hierarchies  
✅ When to use abstract classes vs interfaces  
✅ How to implement composition, aggregation, and association  
✅ Proper exception handling strategies  
✅ Working with Java Collections effectively  
✅ Using Streams API for data processing  
✅ Implementing design patterns in real applications  
✅ Building modular, maintainable code  
✅ Object-oriented analysis and design  

---

## 📚 Potential Extensions

### Database Integration
- Connect to MySQL/PostgreSQL
- Implement data persistence
- Add database transaction management

### Web Interface
- Build REST API with Spring Boot
- Create React/Angular frontend
- Implement JWT authentication

### Additional Features
- SMS/Email notifications
- Report generation (PDF)
- Analytics dashboard
- Appointment reminders
- Telemedicine support
- Mobile app development

---

## 🤝 Contributing

Contributions are welcome! To contribute:

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/AmazingFeature`
3. Commit changes: `git commit -m 'Add AmazingFeature'`
4. Push to branch: `git push origin feature/AmazingFeature`
5. Open a Pull Request

### Guidelines
- Follow Java naming conventions
- Maintain OOP principles
- Add comments for complex logic
- Update documentation
- Test thoroughly

---

## 📄 License

This project is licensed under the MIT License – free to use for learning, portfolios, and academic purposes.

---

## 👨‍💻 Author

**Your Name**
- GitHub: [@yourusername](https://github.com/yourusername)
- LinkedIn: [Your LinkedIn Profile](https://linkedin.com/in/yourprofile)
- Email: your.email@example.com

---

## 🙏 Acknowledgments

- Inspired by real-world hospital management systems
- Implements industry-standard OOP practices
- Follows Java best practices and design patterns
- Built as a comprehensive learning resource

---

## 📞 Support

- 🐛 **Report Issues**: [GitHub Issues](https://github.com/yourusername/hospital-management-system/issues)
- 💬 **Discussions**: [GitHub Discussions](https://github.com/yourusername/hospital-management-system/discussions)
- 📧 **Email**: your.email@example.com

---

⭐ **If you find this project helpful, please star the repository!**

🔗 **Share it with others learning OOP and Java!**

📖 **Use it as a reference for your own projects!**

---

**Built with ❤️ for the Java & OOP learning community**
