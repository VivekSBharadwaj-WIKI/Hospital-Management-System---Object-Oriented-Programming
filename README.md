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

✔ Encapsulation  
✔ Inheritance (Multi-level hierarchy)  
✔ Polymorphism (Runtime & Method Overriding)  
✔ Abstraction (Abstract classes & interfaces)  
✔ Interfaces (Multiple inheritance via interfaces)  
✔ Composition  
✔ Aggregation  
✔ Association  
✔ Exception Handling (Custom exceptions)  
✔ Static Members (ID generation, counters)  
✔ Enums  
✔ Java Collections Framework  
✔ Streams API  
✔ Lambda Expressions  

---

## 🧱 Class Structure Overview

### Core Abstract Classes
- `Person` (Abstract Base Class)
- `MedicalStaff` (Abstract)

### Inheritance Hierarchy



### Key Domain Classes
- `MedicalRecord`
- `Prescription`
- `Appointment`
- `Admission`
- `Room`
- `Bill`
- `Inventory`
- `Laboratory`
- `LabTest`
- `Pharmacy`
- `EmergencyDepartment`
- `InsuranceProvider`
- `InsurancePolicy`
- `Hospital`
- `HospitalManagementSystem`

---

## 🔌 Interfaces Used

- `Identifiable`
- `Authenticatable`
- `Billable`
- `Schedulable`
- `Reportable`

These ensure **loose coupling**, **flexibility**, and **clean design**.

---

## 🔗 Object Relationships

| Concept        | Example |
|----------------|---------|
| **Composition** | Hospital → Departments, Rooms, Staff |
| **Aggregation** | Doctor → Patients |
| **Association** | Appointment → Patient & Doctor |

---

## ⚙️ Functional Modules

- Patient Registration & Records
- Doctor & Staff Management
- Appointment Scheduling
- Admission & Room Allocation
- Pharmacy & Inventory Management
- Laboratory Tests
- Emergency Department Handling
- Billing & Payments
- Insurance Policies & Claims
- System Reporting

---

## 🧪 Java Features Used

- `ArrayList`, `HashMap`, `List`, `Map`
- Java Enums (`BloodGroup`, `RoomType`, `AppointmentStatus`, etc.)
- Streams:
  - `filter()`
  - `map()`
  - `collect()`
- Lambda Expressions
- Custom Exceptions with `try-catch`

---

## ▶️ How to Run the Project

### Prerequisites
- Java JDK 8 or higher
- Command Line / Terminal

### Compile
```bash
javac HospitalManagementDemo.java
