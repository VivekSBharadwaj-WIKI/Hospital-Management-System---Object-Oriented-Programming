import java.time.*;
import java.util.*;
import java.util.stream.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

enum BloodGroup {
    A_POSITIVE("A+"), A_NEGATIVE("A-"),
    B_POSITIVE("B+"), B_NEGATIVE("B-"),
    AB_POSITIVE("AB+"), AB_NEGATIVE("AB-"),
    O_POSITIVE("O+"), O_NEGATIVE("O-");
    
    private final String value;
    BloodGroup(String value) { this.value = value; }
    public String getValue() { return value; }
}

enum AppointmentStatus { SCHEDULED, COMPLETED, CANCELLED, NO_SHOW }

enum RoomType {
    GENERAL("General Ward"), PRIVATE("Private Room"),
    ICU("Intensive Care Unit"), EMERGENCY("Emergency Room"),
    OPERATION("Operation Theatre");
    
    private final String description;
    RoomType(String description) { this.description = description; }
    public String getDescription() { return description; }
}

enum PrescriptionStatus { ACTIVE, COMPLETED, DISCONTINUED }
enum PaymentMethod { CASH, CARD, INSURANCE, UPI }

class ValidationException extends Exception {
    public ValidationException(String message) { super(message); }
}

class AuthenticationException extends Exception {
    public AuthenticationException(String message) { super(message); }
}

class ResourceNotFoundException extends Exception {
    public ResourceNotFoundException(String message) { super(message); }
}

class InsufficientResourceException extends Exception {
    public InsufficientResourceException(String message) { super(message); }
}

class Address {
    private String street, city, state, postalCode, country;
    
    public Address(String street, String city, String state, String postalCode, String country) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.country = country;
    }
    
    public void validate() throws ValidationException {
        if (postalCode.length() != 6 || !postalCode.matches("\\d+")) {
            throw new ValidationException("Invalid postal code format");
        }
    }
    
    @Override
    public String toString() {
        return String.format("%s, %s, %s - %s, %s", street, city, state, postalCode, country);
    }
    
    public String getStreet() { return street; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getPostalCode() { return postalCode; }
    public String getCountry() { return country; }
}

class ContactInfo {
    private String phone, email, emergencyContact;
    
    public ContactInfo(String phone, String email, String emergencyContact) {
        this.phone = phone;
        this.email = email;
        this.emergencyContact = emergencyContact;
    }
    
    public void validate() throws ValidationException {
        if (!phone.matches("\\d{10}")) {
            throw new ValidationException("Invalid phone number format");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ValidationException("Invalid email format");
        }
        if (!emergencyContact.matches("\\d{10}")) {
            throw new ValidationException("Invalid emergency contact format");
        }
    }
    
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getEmergencyContact() { return emergencyContact; }
}

interface Identifiable {
    String getId();
    String getType();
}

interface Authenticatable {
    boolean authenticate(String password);
    boolean changePassword(String oldPassword, String newPassword) throws AuthenticationException, ValidationException;
}

interface Billable {
    double calculateCharges();
    Map<String, Object> generateInvoice();
}

interface Schedulable {
    boolean schedule(LocalDateTime dateTime);
    boolean cancel();
}

interface Reportable {
    Map<String, Object> generateReport();
}

abstract class Person implements Identifiable {
    protected static int idCounter = 1000;
    protected String id;
    protected String firstName, lastName;
    protected LocalDate dateOfBirth;
    protected String gender;
    protected Address address;
    protected ContactInfo contactInfo;
    protected LocalDateTime createdAt, updatedAt;
    
    public Person(String firstName, String lastName, LocalDate dateOfBirth, 
                  String gender, Address address, ContactInfo contactInfo) {
        this.id = generateId();
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.address = address;
        this.contactInfo = contactInfo;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    protected static synchronized String generateId() {
        return "PER" + (++idCounter);
    }
    
    public String getId() { return id; }
    public String getFullName() { return firstName + " " + lastName; }
    
    public int getAge() {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }
    
    public void updateContact(ContactInfo contactInfo) throws ValidationException {
        contactInfo.validate();
        this.contactInfo = contactInfo;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void updateAddress(Address address) throws ValidationException {
        address.validate();
        this.address = address;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getGender() { return gender; }
    public Address getAddress() { return address; }
    public ContactInfo getContactInfo() { return contactInfo; }
}

abstract class MedicalStaff extends Person implements Authenticatable {
    protected String employeeId, department;
    protected double salary;
    protected LocalDate joinDate;
    protected String passwordHash;
    protected boolean isActive;
    protected List<Map<String, Object>> shiftSchedule;
    
    public MedicalStaff(String firstName, String lastName, LocalDate dateOfBirth,
                       String gender, Address address, ContactInfo contactInfo,
                       String employeeId, String department, double salary, LocalDate joinDate) {
        super(firstName, lastName, dateOfBirth, gender, address, contactInfo);
        this.employeeId = employeeId;
        this.department = department;
        this.salary = salary;
        this.joinDate = joinDate;
        this.passwordHash = hashPassword("default123");
        this.isActive = true;
        this.shiftSchedule = new ArrayList<>();
    }
    
    protected String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public boolean authenticate(String password) {
        return passwordHash.equals(hashPassword(password));
    }
    
    @Override
    public boolean changePassword(String oldPassword, String newPassword) 
            throws AuthenticationException, ValidationException {
        if (!authenticate(oldPassword)) {
            throw new AuthenticationException("Invalid old password");
        }
        if (newPassword.length() < 8) {
            throw new ValidationException("Password must be at least 8 characters");
        }
        this.passwordHash = hashPassword(newPassword);
        return true;
    }
    
    public void addShift(LocalDate date, LocalTime startTime, LocalTime endTime) {
        Map<String, Object> shift = new HashMap<>();
        shift.put("date", date);
        shift.put("startTime", startTime);
        shift.put("endTime", endTime);
        shiftSchedule.add(shift);
    }
    
    public List<Map<String, Object>> getShifts(LocalDate startDate, LocalDate endDate) {
        return shiftSchedule.stream()
            .filter(shift -> {
                LocalDate date = (LocalDate) shift.get("date");
                return !date.isBefore(startDate) && !date.isAfter(endDate);
            })
            .collect(Collectors.toList());
    }
    
    public double calculateMonthlySalary() { return salary; }
    public void deactivate() { isActive = false; }
    public void activate() { isActive = true; }
    
    public String getEmployeeId() { return employeeId; }
    public String getDepartment() { return department; }
    public double getSalary() { return salary; }
    public boolean isActive() { return isActive; }
}

class Doctor extends MedicalStaff implements Schedulable {
    private String specialization, licenseNumber;
    private double consultationFee;
    private List<Patient> patients;
    private List<Appointment> appointments;
    private List<Map<String, Object>> availableSlots;
    private int totalConsultations;
    
    public Doctor(String firstName, String lastName, LocalDate dateOfBirth,
                 String gender, Address address, ContactInfo contactInfo,
                 String employeeId, String department, double salary, LocalDate joinDate,
                 String specialization, String licenseNumber, double consultationFee) {
        super(firstName, lastName, dateOfBirth, gender, address, contactInfo,
              employeeId, department, salary, joinDate);
        this.specialization = specialization;
        this.licenseNumber = licenseNumber;
        this.consultationFee = consultationFee;
        this.patients = new ArrayList<>();
        this.appointments = new ArrayList<>();
        this.availableSlots = new ArrayList<>();
        this.totalConsultations = 0;
    }
    
    @Override
    public String getType() { return "Doctor"; }
    
    public void addPatient(Patient patient) {
        if (!patients.contains(patient)) {
            patients.add(patient);
        }
    }
    
    public void removePatient(Patient patient) {
        patients.remove(patient);
    }
    
    @Override
    public boolean schedule(LocalDateTime dateTime) {
        if (!isAvailable(dateTime)) return false;
        
        Map<String, Object> slot = new HashMap<>();
        slot.put("dateTime", dateTime);
        slot.put("duration", 30);
        slot.put("isBooked", false);
        availableSlots.add(slot);
        return true;
    }
    
    @Override
    public boolean cancel() {
        return true;
    }
    
    public boolean isAvailable(LocalDateTime dateTime) {
        for (Map<String, Object> slot : availableSlots) {
            if (slot.get("dateTime").equals(dateTime) && (Boolean) slot.get("isBooked")) {
                return false;
            }
        }
        return true;
    }
    
    public boolean bookSlot(LocalDateTime dateTime) {
        for (Map<String, Object> slot : availableSlots) {
            if (slot.get("dateTime").equals(dateTime) && !(Boolean) slot.get("isBooked")) {
                slot.put("isBooked", true);
                return true;
            }
        }
        return false;
    }
    
    public List<LocalDateTime> getAvailableSlots(LocalDate date) {
        return availableSlots.stream()
            .filter(slot -> {
                LocalDateTime dt = (LocalDateTime) slot.get("dateTime");
                return dt.toLocalDate().equals(date) && !(Boolean) slot.get("isBooked");
            })
            .map(slot -> (LocalDateTime) slot.get("dateTime"))
            .collect(Collectors.toList());
    }
    
    public Prescription prescribeMedication(Patient patient, List<Map<String, Object>> medications) {
        return new Prescription(this, patient, medications);
    }
    
    public MedicalRecord diagnose(Patient patient, String diagnosis, String notes) {
        MedicalRecord record = new MedicalRecord(patient, this, diagnosis, notes);
        patient.addMedicalRecord(record);
        return record;
    }
    
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPatients", patients.size());
        stats.put("totalConsultations", totalConsultations);
        stats.put("totalAppointments", appointments.size());
        stats.put("specialization", specialization);
        return stats;
    }
    
    public String getSpecialization() { return specialization; }
    public double getConsultationFee() { return consultationFee; }
    public int getTotalConsultations() { return totalConsultations; }
    public void incrementConsultations() { totalConsultations++; }
    public List<Appointment> getAppointments() { return appointments; }
}

class Nurse extends MedicalStaff {
    private String certification;
    private List<Patient> assignedPatients;
    private List<Map<String, Object>> tasks;
    
    public Nurse(String firstName, String lastName, LocalDate dateOfBirth,
                String gender, Address address, ContactInfo contactInfo,
                String employeeId, String department, double salary, LocalDate joinDate,
                String certification) {
        super(firstName, lastName, dateOfBirth, gender, address, contactInfo,
              employeeId, department, salary, joinDate);
        this.certification = certification;
        this.assignedPatients = new ArrayList<>();
        this.tasks = new ArrayList<>();
    }
    
    @Override
    public String getType() { return "Nurse"; }
    
    public void assignPatient(Patient patient) {
        if (!assignedPatients.contains(patient)) {
            assignedPatients.add(patient);
        }
    }
    
    public void unassignPatient(Patient patient) {
        assignedPatients.remove(patient);
    }
    
    public void addTask(String task, String priority, LocalDateTime dueTime) {
        Map<String, Object> taskEntry = new HashMap<>();
        taskEntry.put("task", task);
        taskEntry.put("priority", priority);
        taskEntry.put("dueTime", dueTime);
        taskEntry.put("completed", false);
        taskEntry.put("createdAt", LocalDateTime.now());
        tasks.add(taskEntry);
    }
    
    public boolean completeTask(int taskIndex) {
        if (taskIndex >= 0 && taskIndex < tasks.size()) {
            tasks.get(taskIndex).put("completed", true);
            tasks.get(taskIndex).put("completedAt", LocalDateTime.now());
            return true;
        }
        return false;
    }
    
    public List<Map<String, Object>> getPendingTasks() {
        return tasks.stream()
            .filter(task -> !(Boolean) task.get("completed"))
            .collect(Collectors.toList());
    }
    
    public void recordVitalSigns(Patient patient, Map<String, Object> vitalSigns) {
        patient.updateVitalSigns(vitalSigns);
    }
    
    public void administerMedication(Patient patient, String medication, 
                                    String dosage, LocalDateTime time) {
        Map<String, Object> logEntry = new HashMap<>();
        logEntry.put("medication", medication);
        logEntry.put("dosage", dosage);
        logEntry.put("administeredBy", getFullName());
        logEntry.put("time", time);
        patient.addMedicationLog(logEntry);
    }
    
    public String getCertification() { return certification; }
}

class Administrator extends MedicalStaff implements Reportable {
    private int accessLevel;
    private List<MedicalStaff> managedStaff;
    
    public Administrator(String firstName, String lastName, LocalDate dateOfBirth,
                        String gender, Address address, ContactInfo contactInfo,
                        String employeeId, String department, double salary, LocalDate joinDate,
                        int accessLevel) {
        super(firstName, lastName, dateOfBirth, gender, address, contactInfo,
              employeeId, department, salary, joinDate);
        this.accessLevel = accessLevel;
        this.managedStaff = new ArrayList<>();
    }
    
    @Override
    public String getType() { return "Administrator"; }
    
    public void addStaff(MedicalStaff staff) {
        if (!managedStaff.contains(staff)) {
            managedStaff.add(staff);
        }
    }
    
    public void removeStaff(MedicalStaff staff) {
        managedStaff.remove(staff);
    }
    
    @Override
    public Map<String, Object> generateReport() {
        Map<String, Object> report = new HashMap<>();
        report.put("totalManagedStaff", managedStaff.size());
        report.put("accessLevel", accessLevel);
        report.put("department", department);
        return report;
    }
    
    public boolean approveLeave(MedicalStaff staff, LocalDate startDate, LocalDate endDate) {
        return true;
    }
    
    public Map<String, Object> conductPerformanceReview(MedicalStaff staff, int rating, String comments) {
        Map<String, Object> review = new HashMap<>();
        review.put("staffId", staff.getEmployeeId());
        review.put("staffName", staff.getFullName());
        review.put("rating", rating);
        review.put("comments", comments);
        review.put("reviewer", getFullName());
        review.put("date", LocalDateTime.now());
        return review;
    }
    
    public int getAccessLevel() { return accessLevel; }
}

class Patient extends Person implements Billable {
    private static int patientCounter = 5000;
    private String patientId;
    private BloodGroup bloodGroup;
    private String insuranceId;
    private List<MedicalRecord> medicalHistory;
    private List<Prescription> prescriptions;
    private List<Appointment> appointments;
    private List<Bill> bills;
    private boolean admitted;
    private Admission admission;
    private List<String> allergies;
    private List<String> chronicConditions;
    private Map<String, Object> vitalSigns;
    private List<Map<String, Object>> medicationLog;
    
    public Patient(String firstName, String lastName, LocalDate dateOfBirth,
                  String gender, Address address, ContactInfo contactInfo,
                  BloodGroup bloodGroup, String insuranceId) {
        super(firstName, lastName, dateOfBirth, gender, address, contactInfo);
        this.patientId = generatePatientId();
        this.bloodGroup = bloodGroup;
        this.insuranceId = insuranceId;
        this.medicalHistory = new ArrayList<>();
        this.prescriptions = new ArrayList<>();
        this.appointments = new ArrayList<>();
        this.bills = new ArrayList<>();
        this.admitted = false;
        this.allergies = new ArrayList<>();
        this.chronicConditions = new ArrayList<>();
        this.vitalSigns = new HashMap<>();
        this.medicationLog = new ArrayList<>();
    }
    
    private static synchronized String generatePatientId() {
        return "PAT" + (++patientCounter);
    }
    
    @Override
    public String getType() { return "Patient"; }
    
    public void addAllergy(String allergy) {
        if (!allergies.contains(allergy)) {
            allergies.add(allergy);
        }
    }
    
    public void addChronicCondition(String condition) {
        if (!chronicConditions.contains(condition)) {
            chronicConditions.add(condition);
        }
    }
    
    public void addMedicalRecord(MedicalRecord record) {
        medicalHistory.add(record);
    }
    
    public void addPrescription(Prescription prescription) {
        prescriptions.add(prescription);
    }
    
    public void updateVitalSigns(Map<String, Object> vitalSigns) {
        this.vitalSigns.putAll(vitalSigns);
        this.vitalSigns.put("timestamp", LocalDateTime.now());
    }
    
    public void addMedicationLog(Map<String, Object> logEntry) {
        medicationLog.add(logEntry);
    }
    
    @Override
    public double calculateCharges() {
        return bills.stream()
            .filter(bill -> !bill.isPaid())
            .mapToDouble(Bill::getTotalAmount)
            .sum();
    }
    
    @Override
    public Map<String, Object> generateInvoice() {
        Map<String, Object> invoice = new HashMap<>();
        invoice.put("patientId", patientId);
        invoice.put("patientName", getFullName());
        invoice.put("totalCharges", calculateCharges());
        invoice.put("unpaidBills", bills.stream().filter(b -> !b.isPaid()).count());
        invoice.put("generatedAt", LocalDateTime.now());
        return invoice;
    }
    
    public Map<String, Object> getMedicalSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("patientId", patientId);
        summary.put("name", getFullName());
        summary.put("age", getAge());
        summary.put("bloodGroup", bloodGroup.getValue());
        summary.put("allergies", allergies);
        summary.put("chronicConditions", chronicConditions);
        summary.put("totalVisits", medicalHistory.size());
        summary.put("currentPrescriptions", 
            prescriptions.stream().filter(p -> p.getStatus() == PrescriptionStatus.ACTIVE).count());
        return summary;
    }
    
    public String getPatientId() { return patientId; }
    public BloodGroup getBloodGroup() { return bloodGroup; }
    public boolean isAdmitted() { return admitted; }
    public void setAdmitted(boolean admitted) { this.admitted = admitted; }
    public void setAdmission(Admission admission) { this.admission = admission; }
    public List<Bill> getBills() { return bills; }
    public List<Appointment> getAppointments() { return appointments; }
    public Map<String, Object> getVitalSigns() { return vitalSigns; }
    public List<MedicalRecord> getMedicalHistory() { return medicalHistory; }
    public List<Prescription> getPrescriptions() { return prescriptions; }
}

class MedicalRecord {
    private static int recordCounter = 10000;
    private String recordId;
    private Patient patient;
    private Doctor doctor;
    private String diagnosis, notes;
    private LocalDateTime date;
    private List<String> testsOrdered;
    private boolean followUpRequired;
    private LocalDate followUpDate;
    
    public MedicalRecord(Patient patient, Doctor doctor, String diagnosis, String notes) {
        this.recordId = generateRecordId();
        this.patient = patient;
        this.doctor = doctor;
        this.diagnosis = diagnosis;
        this.notes = notes;
        this.date = LocalDateTime.now();
        this.testsOrdered = new ArrayList<>();
        this.followUpRequired = false;
    }
    
    private static synchronized String generateRecordId() {
        return "REC" + (++recordCounter);
    }
    
    public void addTest(String testName) {
        testsOrdered.add(testName);
    }
    
    public void setFollowUp(LocalDate date) {
        this.followUpRequired = true;
        this.followUpDate = date;
    }
    
    public String getSummary() {
        return String.format("Record %s: %s by Dr. %s on %s", 
            recordId, diagnosis, doctor.getFullName(), date.toLocalDate());
    }
    
    public String getRecordId() { return recordId; }
    public String getDiagnosis() { return diagnosis; }
}

class Prescription {
    private static int prescriptionCounter = 20000;
    private String prescriptionId;
    private Doctor doctor;
    private Patient patient;
    private List<Map<String, Object>> medications;
    private LocalDateTime datePrescribed;
    private PrescriptionStatus status;
    private LocalDateTime validUntil;
    private int refillsRemaining;
    
    public Prescription(Doctor doctor, Patient patient, List<Map<String, Object>> medications) {
        this.prescriptionId = generatePrescriptionId();
        this.doctor = doctor;
        this.patient = patient;
        this.medications = new ArrayList<>(medications);
        this.datePrescribed = LocalDateTime.now();
        this.status = PrescriptionStatus.ACTIVE;
        this.validUntil = LocalDateTime.now().plusDays(30);
        this.refillsRemaining = 2;
    }
    
    private static synchronized String generatePrescriptionId() {
        return "PRX" + (++prescriptionCounter);
    }
    
    public void addMedication(String name, String dosage, String frequency, int duration) {
        Map<String, Object> medication = new HashMap<>();
        medication.put("name", name);
        medication.put("dosage", dosage);
        medication.put("frequency", frequency);
        medication.put("durationDays", duration);
        medications.add(medication);
    }
    
    public void discontinue() {
        status = PrescriptionStatus.DISCONTINUED;
    }
    
    public boolean refill() {
        if (refillsRemaining > 0 && status == PrescriptionStatus.ACTIVE) {
            refillsRemaining--;
            validUntil = LocalDateTime.now().plusDays(30);
            return true;
        }
        return false;
    }
    
    public boolean isValid() {
        return status == PrescriptionStatus.ACTIVE && LocalDateTime.now().isBefore(validUntil);
    }
    
    public String getPrescriptionId() { return prescriptionId; }
    public PrescriptionStatus getStatus() { return status; }
    public List<Map<String, Object>> getMedications() { return medications; }
    public int getRefillsRemaining() { return refillsRemaining; }
    public LocalDateTime getValidUntil() { return validUntil; }
}

class Appointment implements Schedulable {
    private static int appointmentCounter = 30000;
    private String appointmentId;
    private Patient patient;
    private Doctor doctor;
    private LocalDateTime dateTime;
    private String reason;
    private AppointmentStatus status;
    private LocalDateTime createdAt;
    private String consultationNotes;
    private int durationMinutes;
    
    public Appointment(Patient patient, Doctor doctor, LocalDateTime dateTime, String reason) {
        this.appointmentId = generateAppointmentId();
        this.patient = patient;
        this.doctor = doctor;
        this.dateTime = dateTime;
        this.reason = reason;
        this.status = AppointmentStatus.SCHEDULED;
        this.createdAt = LocalDateTime.now();
        this.durationMinutes = 30;
    }
    
    private static synchronized String generateAppointmentId() {
        return "APT" + (++appointmentCounter);
    }
    
    @Override
    public boolean schedule(LocalDateTime dateTime) {
        if (doctor.bookSlot(dateTime)) {
            this.dateTime = dateTime;
            this.status = AppointmentStatus.SCHEDULED;
            return true;
        }
        return false;
    }
    
    @Override
    public boolean cancel() {
        if (status == AppointmentStatus.SCHEDULED) {
            status = AppointmentStatus.CANCELLED;
            return true;
        }
        return false;
    }
    
    public void complete(String notes) {
        status = AppointmentStatus.COMPLETED;
        consultationNotes = notes;
        doctor.incrementConsultations();
    }
    
    public void markNoShow() {
        status = AppointmentStatus.NO_SHOW;
    }
    
    public boolean reschedule(LocalDateTime newDateTime) {
        if (cancel()) {
            return schedule(newDateTime);
        }
        return false;
    }
    
    public Map<String, Object> getDetails() {
        Map<String, Object> details = new HashMap<>();
        details.put("appointmentId", appointmentId);
        details.put("patient", patient.getFullName());
        details.put("doctor", doctor.getFullName());
        details.put("dateTime", dateTime);
        details.put("status", status);
        details.put("reason", reason);
        return details;
    }
    
    public String getAppointmentId() { return appointmentId; }
    public AppointmentStatus getStatus() { return status; }
    public LocalDateTime getDateTime() { return dateTime; }
}

class Room {
    private static int roomCounter = 100;
    private String roomNumber;
    private RoomType roomType;
    private int floor, capacity;
    private double dailyCharge;
    private boolean isOccupied;
    private Patient currentPatient;
    private boolean maintenanceRequired;
    private List<String> amenities;
    
    public Room(RoomType roomType, int floor, int capacity, double dailyCharge) {
        this.roomNumber = generateRoomNumber();
        this.roomType = roomType;
        this.floor = floor;
        this.capacity = capacity;
        this.dailyCharge = dailyCharge;
        this.isOccupied = false;
        this.maintenanceRequired = false;
        this.amenities = new ArrayList<>();
    }
    
    private static synchronized String generateRoomNumber() {
        return "R" + (++roomCounter);
    }
    
    public void addAmenity(String amenity) {
        amenities.add(amenity);
    }
    
    public boolean occupy(Patient patient) {
        if (!isOccupied && !maintenanceRequired) {
            isOccupied = true;
            currentPatient = patient;
            return true;
        }
        return false;
    }
    
    public boolean vacate() {
        if (isOccupied) {
            isOccupied = false;
            currentPatient = null;
            return true;
        }
        return false;
    }
    
    public void markForMaintenance() {
        if (!isOccupied) {
            maintenanceRequired = true;
        }
    }
    
    public void completeMaintenance() {
        maintenanceRequired = false;
    }
    
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("roomNumber", roomNumber);
        status.put("type", roomType.getDescription());
        status.put("floor", floor);
        status.put("occupied", isOccupied);
        status.put("patient", currentPatient != null ? currentPatient.getFullName() : null);
        status.put("dailyCharge", dailyCharge);
        return status;
    }
    
    public String getRoomNumber() { return roomNumber; }
    public RoomType getRoomType() { return roomType; }
    public boolean isOccupied() { return isOccupied; }
    public boolean isMaintenanceRequired() { return maintenanceRequired; }
    public double getDailyCharge() { return dailyCharge; }
}

class Admission {
    private static int admissionCounter = 40000;
    private String admissionId;
    private Patient patient;
    private Doctor doctor;
    private Room room;
    private LocalDateTime admissionDate, dischargeDate;
    private String reason;
    private boolean isActive;
    private String dischargeSummary;
    
    public Admission(Patient patient, Doctor doctor, Room room, 
                    LocalDateTime admissionDate, String reason) {
        this.admissionId = generateAdmissionId();
        this.patient = patient;
        this.doctor = doctor;
        this.room = room;
        this.admissionDate = admissionDate;
        this.reason = reason;
        this.isActive = true;
    }
    
    private static synchronized String generateAdmissionId() {
        return "ADM" + (++admissionCounter);
    }
    
    public boolean discharge(String summary) {
        if (isActive) {
            dischargeDate = LocalDateTime.now();
            dischargeSummary = summary;
            isActive = false;
            room.vacate();
            patient.setAdmitted(false);
            return true;
        }
        return false;
    }
    
    public long getDurationDays() {
        LocalDateTime endDate = dischargeDate != null ? dischargeDate : LocalDateTime.now();
        return Duration.between(admissionDate, endDate).toDays();
    }
    
    public double calculateRoomCharges() {
        return getDurationDays() * room.getDailyCharge();
    }
    
    public Map<String, Object> getDetails() {
        Map<String, Object> details = new HashMap<>();
        details.put("admissionId", admissionId);
        details.put("patient", patient.getFullName());
        details.put("doctor", doctor.getFullName());
        details.put("room", room.getRoomNumber());
        details.put("admissionDate", admissionDate);
        details.put("dischargeDate", dischargeDate);
        details.put("durationDays", getDurationDays());
        details.put("isActive", isActive);
        return details;
    }
    
    public String getAdmissionId() { return admissionId; }
    public Room getRoom() { return room; }
    public boolean isActive() { return isActive; }
}

class Bill implements Billable {
    private static int billCounter = 50000;
    private String billId;
    private Patient patient;
    private LocalDateTime createdDate;
    private List<Map<String, Object>> items;
    private double subtotal, taxRate, discount, totalAmount;
    private boolean isPaid;
    private LocalDateTime paymentDate;
    private PaymentMethod paymentMethod;
    
    public Bill(Patient patient) {
        this.billId = generateBillId();
        this.patient = patient;
        this.createdDate = LocalDateTime.now();
        this.items = new ArrayList<>();
        this.subtotal = 0.0;
        this.taxRate = 0.05;
        this.discount = 0.0;
        this.totalAmount = 0.0;
        this.isPaid = false;
    }
    
    private static synchronized String generateBillId() {
        return "BILL" + (++billCounter);
    }
    
    public void addItem(String description, double amount, int quantity) {
        Map<String, Object> item = new HashMap<>();
        item.put("description", description);
        item.put("amount", amount);
        item.put("quantity", quantity);
        item.put("total", amount * quantity);
        items.add(item);
        recalculate();
    }
    
    public void addConsultationFee(Doctor doctor) {
        addItem("Consultation - Dr. " + doctor.getFullName(), doctor.getConsultationFee(), 1);
    }
    
    public void addRoomCharges(Admission admission) {
        addItem("Room Charges - " + admission.getRoom().getRoomType().getDescription(),
                admission.calculateRoomCharges(), 1);
    }
    
    public void addTestCharges(String testName, double amount) {
        addItem("Test - " + testName, amount, 1);
    }
    
    public void addMedicationCharges(String medicationName, double amount, int quantity) {
        addItem("Medication - " + medicationName, amount, quantity);
    }
    
    public void applyDiscount(double discountPercentage) {
        discount = (discountPercentage / 100) * subtotal;
        recalculate();
    }
    
    private void recalculate() {
        subtotal = items.stream()
            .mapToDouble(item -> (Double) item.get("total"))
            .sum();
        double taxAmount = subtotal * taxRate;
        totalAmount = subtotal + taxAmount - discount;
    }
    
    @Override
    public double calculateCharges() {
        return totalAmount;
    }
    
    @Override
    public Map<String, Object> generateInvoice() {
        Map<String, Object> invoice = new HashMap<>();
        invoice.put("billId", billId);
        invoice.put("patientId", patient.getPatientId());
        invoice.put("patientName", patient.getFullName());
        invoice.put("createdDate", createdDate);
        invoice.put("items", items);
        invoice.put("subtotal", subtotal);
        invoice.put("tax", subtotal * taxRate);
        invoice.put("discount", discount);
        invoice.put("totalAmount", totalAmount);
        invoice.put("isPaid", isPaid);
        return invoice;
    }
    
    public boolean processPayment(PaymentMethod method) {
        if (!isPaid) {
            isPaid = true;
            paymentDate = LocalDateTime.now();
            paymentMethod = method;
            return true;
        }
        return false;
    }
    
    public String getBillId() { return billId; }
    public double getTotalAmount() { return totalAmount; }
    public boolean isPaid() { return isPaid; }
    public LocalDateTime getCreatedDate() { return createdDate; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
}

class Inventory {
    private static int itemCounter = 60000;
    private String itemId, itemName, category;
    private int quantity, reorderLevel;
    private double unitPrice;
    private String supplier;
    private LocalDateTime lastRestocked;
    private LocalDate expiryDate;
    
    public Inventory(String itemName, String category, int quantity, 
                    double unitPrice, int reorderLevel) {
        this.itemId = generateItemId();
        this.itemName = itemName;
        this.category = category;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.reorderLevel = reorderLevel;
    }
    
    private static synchronized String generateItemId() {
        return "INV" + (++itemCounter);
    }
    
    public void addStock(int quantity) {
        this.quantity += quantity;
        lastRestocked = LocalDateTime.now();
    }
    
    public void removeStock(int quantity) throws InsufficientResourceException {
        if (this.quantity >= quantity) {
            this.quantity -= quantity;
        } else {
            throw new InsufficientResourceException("Insufficient stock for " + itemName);
        }
    }
    
    public boolean needsReorder() {
        return quantity <= reorderLevel;
    }
    
    public boolean isExpired() {
        return expiryDate != null && LocalDate.now().isAfter(expiryDate);
    }
    
    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }
    
    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    public double getValue() {
        return quantity * unitPrice;
    }
    
    public Map<String, Object> getDetails() {
        Map<String, Object> details = new HashMap<>();
        details.put("itemId", itemId);
        details.put("itemName", itemName);
        details.put("category", category);
        details.put("quantity", quantity);
        details.put("unitPrice", unitPrice);
        details.put("totalValue", getValue());
        details.put("needsReorder", needsReorder());
        details.put("isExpired", isExpired());
        return details;
    }
    
    public String getItemName() { return itemName; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
}

class Laboratory {
    private static int labCounter = 70000;
    private String labId, labName, location;
    private List<Map<String, Object>> testsAvailable;
    private List<LabTest> pendingTests, completedTests;
    private List<String> technicians;
    
    public Laboratory(String labName, String location) {
        this.labId = generateLabId();
        this.labName = labName;
        this.location = location;
        this.testsAvailable = new ArrayList<>();
        this.pendingTests = new ArrayList<>();
        this.completedTests = new ArrayList<>();
        this.technicians = new ArrayList<>();
    }
    
    private static synchronized String generateLabId() {
        return "LAB" + (++labCounter);
    }
    
    public void addTestType(String testName, double cost, int durationHours) {
        Map<String, Object> test = new HashMap<>();
        test.put("name", testName);
        test.put("cost", cost);
        test.put("durationHours", durationHours);
        testsAvailable.add(test);
    }
    
    public void addTechnician(String technicianName) {
        technicians.add(technicianName);
    }
    
    public LabTest orderTest(Patient patient, String testName, Doctor doctor) 
            throws ResourceNotFoundException {
        Map<String, Object> testInfo = testsAvailable.stream()
            .filter(t -> t.get("name").equals(testName))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Test " + testName + " not available"));
        
        LabTest labTest = new LabTest(patient, testName, doctor, this, (Double) testInfo.get("cost"));
        pendingTests.add(labTest);
        return labTest;
    }
    
    public void completeTest(LabTest labTest, String results, String technician) {
        if (pendingTests.contains(labTest)) {
            labTest.complete(results, technician);
            pendingTests.remove(labTest);
            completedTests.add(labTest);
        }
    }
    
    public int getPendingCount() {
        return pendingTests.size();
    }
    
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("labId", labId);
        stats.put("labName", labName);
        stats.put("totalTestsAvailable", testsAvailable.size());
        stats.put("pendingTests", pendingTests.size());
        stats.put("completedTests", completedTests.size());
        stats.put("technicians", technicians.size());
        return stats;
    }
    
    public String getLabId() { return labId; }
    public String getLabName() { return labName; }
}

class LabTest {
    private static int testCounter = 80000;
    private String testId, testName;
    private Patient patient;
    private Doctor doctor;
    private Laboratory laboratory;
    private double cost;
    private LocalDateTime orderedDate, completedDate;
    private String results, technician;
    private boolean isCompleted;
    
    public LabTest(Patient patient, String testName, Doctor doctor, 
                  Laboratory laboratory, double cost) {
        this.testId = generateTestId();
        this.patient = patient;
        this.testName = testName;
        this.doctor = doctor;
        this.laboratory = laboratory;
        this.cost = cost;
        this.orderedDate = LocalDateTime.now();
        this.isCompleted = false;
    }
    
    private static synchronized String generateTestId() {
        return "TEST" + (++testCounter);
    }
    
    public void complete(String results, String technician) {
        this.results = results;
        this.technician = technician;
        this.completedDate = LocalDateTime.now();
        this.isCompleted = true;
    }
    
    public Map<String, Object> getReport() {
        Map<String, Object> report = new HashMap<>();
        report.put("testId", testId);
        report.put("patientName", patient.getFullName());
        report.put("testName", testName);
        report.put("orderedBy", doctor.getFullName());
        report.put("laboratory", laboratory.getLabName());
        report.put("orderedDate", orderedDate);
        report.put("completedDate", completedDate);
        report.put("results", results);
        report.put("technician", technician);
        return report;
    }
    
    public String getTestId() { return testId; }
    public double getCost() { return cost; }
}

class Pharmacy {
    private static int pharmacyCounter = 90000;
    private String pharmacyId, pharmacyName, location;
    private Map<String, Inventory> inventory;
    private List<Map<String, Object>> salesRecords;
    private List<String> pharmacists;
    
    public Pharmacy(String pharmacyName, String location) {
        this.pharmacyId = generatePharmacyId();
        this.pharmacyName = pharmacyName;
        this.location = location;
        this.inventory = new HashMap<>();
        this.salesRecords = new ArrayList<>();
        this.pharmacists = new ArrayList<>();
    }
    
    private static synchronized String generatePharmacyId() {
        return "PHAR" + (++pharmacyCounter);
    }
    
    public void addPharmacist(String pharmacistName) {
        pharmacists.add(pharmacistName);
    }
    
    public void addMedication(String medicationName, int quantity, 
                             double unitPrice, int reorderLevel) {
        Inventory item = new Inventory(medicationName, "Medication", quantity, 
                                      unitPrice, reorderLevel);
        inventory.put(medicationName, item);
    }
    
    public Map<String, Object> dispenseMedication(Prescription prescription, String pharmacist) 
            throws ValidationException, ResourceNotFoundException, InsufficientResourceException {
        if (!prescription.isValid()) {
            throw new ValidationException("Prescription is not valid");
        }
        
        List<Map<String, Object>> saleItems = new ArrayList<>();
        double totalCost = 0.0;
        
        for (Map<String, Object> medication : prescription.getMedications()) {
            String medName = (String) medication.get("name");
            if (!inventory.containsKey(medName)) {
                throw new ResourceNotFoundException("Medication " + medName + " not in stock");
            }
            
            Inventory item = inventory.get(medName);
            int quantityNeeded = 1;
            
            if (item.getQuantity() < quantityNeeded) {
                throw new InsufficientResourceException("Insufficient stock for " + medName);
            }
            
            item.removeStock(quantityNeeded);
            double itemCost = item.getUnitPrice() * quantityNeeded;
            totalCost += itemCost;
            
            Map<String, Object> saleItem = new HashMap<>();
            saleItem.put("medication", medName);
            saleItem.put("quantity", quantityNeeded);
            saleItem.put("unitPrice", item.getUnitPrice());
            saleItem.put("total", itemCost);
            saleItems.add(saleItem);
        }
        
        Map<String, Object> saleRecord = new HashMap<>();
        saleRecord.put("prescriptionId", prescription.getPrescriptionId());
        saleRecord.put("pharmacist", pharmacist);
        saleRecord.put("items", saleItems);
        saleRecord.put("totalCost", totalCost);
        saleRecord.put("date", LocalDateTime.now());
        
        salesRecords.add(saleRecord);
        return saleRecord;
    }
    
    public Integer checkStock(String medicationName) {
        if (inventory.containsKey(medicationName)) {
            return inventory.get(medicationName).getQuantity();
        }
        return null;
    }
    
    public List<Inventory> getLowStockItems() {
        return inventory.values().stream()
            .filter(Inventory::needsReorder)
            .collect(Collectors.toList());
    }
    
    public void restockMedication(String medicationName, int quantity) 
            throws ResourceNotFoundException {
        if (inventory.containsKey(medicationName)) {
            inventory.get(medicationName).addStock(quantity);
        } else {
            throw new ResourceNotFoundException("Medication " + medicationName + " not found");
        }
    }
    
    public Map<String, Object> getSalesReport(LocalDateTime startDate, LocalDateTime endDate) {
        List<Map<String, Object>> filtered = salesRecords.stream()
            .filter(sale -> {
                LocalDateTime date = (LocalDateTime) sale.get("date");
                return !date.isBefore(startDate) && !date.isAfter(endDate);
            })
            .collect(Collectors.toList());
        
        double totalRevenue = filtered.stream()
            .mapToDouble(sale -> (Double) sale.get("totalCost"))
            .sum();
        
        Map<String, Object> report = new HashMap<>();
        report.put("pharmacyId", pharmacyId);
        report.put("period", startDate.toLocalDate() + " to " + endDate.toLocalDate());
        report.put("totalSales", filtered.size());
        report.put("totalRevenue", totalRevenue);
        report.put("sales", filtered);
        return report;
    }
    
    public String getPharmacyId() { return pharmacyId; }
}

class EmergencyDepartment {
    private static int deptCounter = 100000;
    private String departmentId;
    private int capacity, availableBeds;
    private List<Map<String, Object>> currentPatients, waitingQueue;
    private List<MedicalStaff> emergencyStaff;
    
    public EmergencyDepartment(int capacity) {
        this.departmentId = generateDeptId();
        this.capacity = capacity;
        this.availableBeds = capacity;
        this.currentPatients = new ArrayList<>();
        this.waitingQueue = new ArrayList<>();
        this.emergencyStaff = new ArrayList<>();
    }
    
    private static synchronized String generateDeptId() {
        return "EMERG" + (++deptCounter);
    }
    
    public void addStaff(MedicalStaff staff) {
        emergencyStaff.add(staff);
    }
    
    public boolean triagePatient(Patient patient, int severity, String complaint) 
            throws ValidationException {
        if (severity < 1 || severity > 5) {
            throw new ValidationException("Severity must be between 1 (critical) and 5 (minor)");
        }
        
        Map<String, Object> entry = new HashMap<>();
        entry.put("patient", patient);
        entry.put("severity", severity);
        entry.put("complaint", complaint);
        
        if (availableBeds > 0) {
            currentPatients.add(entry);
            currentPatients.sort(Comparator.comparingInt(e -> (Integer) e.get("severity")));
            availableBeds--;
            return true;
        } else {
            waitingQueue.add(entry);
            waitingQueue.sort(Comparator.comparingInt(e -> (Integer) e.get("severity")));
            return false;
        }
    }
    
    public boolean dischargePatient(Patient patient) {
        for (Map<String, Object> entry : currentPatients) {
            if (entry.get("patient").equals(patient)) {
                currentPatients.remove(entry);
                availableBeds++;
                admitFromQueue();
                return true;
            }
        }
        return false;
    }
    
    private void admitFromQueue() {
        if (!waitingQueue.isEmpty() && availableBeds > 0) {
            Map<String, Object> nextPatient = waitingQueue.remove(0);
            currentPatients.add(nextPatient);
            availableBeds--;
        }
    }
    
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("departmentId", departmentId);
        status.put("capacity", capacity);
        status.put("availableBeds", availableBeds);
        status.put("currentPatients", currentPatients.size());
        status.put("waitingQueue", waitingQueue.size());
        status.put("criticalCases", currentPatients.stream()
            .filter(p -> (Integer) p.get("severity") == 1).count());
        return status;
    }
}

class InsuranceProvider {
    private static int providerCounter = 110000;
    private String providerId, providerName;
    private List<String> policyTypes;
    private Map<String, InsurancePolicy> policies;
    
    public InsuranceProvider(String providerName, List<String> policyTypes) {
        this.providerId = generateProviderId();
        this.providerName = providerName;
        this.policyTypes = new ArrayList<>(policyTypes);
        this.policies = new HashMap<>();
    }
    
    private static synchronized String generateProviderId() {
        return "INS" + (++providerCounter);
    }
    
    public InsurancePolicy createPolicy(Patient patient, String policyType, 
                                       double coverageAmount, double premium) {
        InsurancePolicy policy = new InsurancePolicy(this, patient, policyType, 
                                                    coverageAmount, premium);
        policies.put(policy.getPolicyNumber(), policy);
        return policy;
    }
    
    public InsurancePolicy getPolicy(String policyNumber) {
        return policies.get(policyNumber);
    }
    
    public Map.Entry<Boolean, Double> processClaim(String policyNumber, Bill bill) {
        InsurancePolicy policy = getPolicy(policyNumber);
        if (policy == null || !policy.isActive()) {
            return Map.entry(false, 0.0);
        }
        return policy.processClaim(bill);
    }
    
    public String getProviderId() { return providerId; }
}

class InsurancePolicy {
    private static int policyCounter = 120000;
    private String policyNumber, policyType;
    private InsuranceProvider provider;
    private Patient patient;
    private double coverageAmount, premium, totalClaimed;
    private LocalDate startDate, endDate;
    private boolean isActive;
    private List<Map<String, Object>> claimsHistory;
    
    public InsurancePolicy(InsuranceProvider provider, Patient patient, String policyType,
                          double coverageAmount, double premium) {
        this.policyNumber = generatePolicyNumber();
        this.provider = provider;
        this.patient = patient;
        this.policyType = policyType;
        this.coverageAmount = coverageAmount;
        this.premium = premium;
        this.startDate = LocalDate.now();
        this.endDate = startDate.plusYears(1);
        this.isActive = true;
        this.totalClaimed = 0.0;
        this.claimsHistory = new ArrayList<>();
    }
    
    private static synchronized String generatePolicyNumber() {
        return "POL" + (++policyCounter);
    }
    
    public Map.Entry<Boolean, Double> processClaim(Bill bill) {
        if (!isActive || totalClaimed >= coverageAmount) {
            return Map.entry(false, 0.0);
        }
        
        double claimAmount = Math.min(bill.getTotalAmount(), 
                                     coverageAmount - totalClaimed);
        double coveragePercentage = 0.8;
        double approvedAmount = claimAmount * coveragePercentage;
        
        Map<String, Object> claim = new HashMap<>();
        claim.put("claimId", "CLM" + (claimsHistory.size() + 1));
        claim.put("billId", bill.getBillId());
        claim.put("claimedAmount", claimAmount);
        claim.put("approvedAmount", approvedAmount);
        claim.put("date", LocalDateTime.now());
        
        claimsHistory.add(claim);
        totalClaimed += approvedAmount;
        
        return Map.entry(true, approvedAmount);
    }
    
    public boolean renew() {
        if (LocalDate.now().isBefore(endDate.plusDays(30)) || 
            LocalDate.now().isEqual(endDate.plusDays(30))) {
            startDate = endDate.plusDays(1);
            endDate = startDate.plusYears(1);
            totalClaimed = 0.0;
            return true;
        }
        return false;
    }
    
    public void cancel() {
        isActive = false;
    }
    
    public String getPolicyNumber() { return policyNumber; }
    public boolean isActive() { return isActive; }
}

class Hospital implements Reportable {
    private static int hospitalCounter = 1;
    private String hospitalId, name;
    private Address address;
    private ContactInfo contactInfo;
    private Map<String, Doctor> doctors;
    private Map<String, Nurse> nurses;
    private Map<String, Administrator> administrators;
    private Map<String, Patient> patients;
    private Map<String, Room> rooms;
    private List<Appointment> appointments;
    private List<Admission> admissions;
    private Map<String, Laboratory> laboratories;
    private Map<String, Pharmacy> pharmacies;
    private EmergencyDepartment emergencyDept;
    private Map<String, List<MedicalStaff>> departments;
    private LocalDate foundedDate;
    
    public Hospital(String name, Address address, ContactInfo contactInfo) {
        this.hospitalId = generateHospitalId();
        this.name = name;
        this.address = address;
        this.contactInfo = contactInfo;
        this.doctors = new HashMap<>();
        this.nurses = new HashMap<>();
        this.administrators = new HashMap<>();
        this.patients = new HashMap<>();
        this.rooms = new HashMap<>();
        this.appointments = new ArrayList<>();
        this.admissions = new ArrayList<>();
        this.laboratories = new HashMap<>();
        this.pharmacies = new HashMap<>();
        this.departments = new HashMap<>();
        this.foundedDate = LocalDate.now();
    }
    
    private static synchronized String generateHospitalId() {
        return "HOSP" + (hospitalCounter++);
    }
    
    public void registerDoctor(Doctor doctor) {
        doctors.put(doctor.getEmployeeId(), doctor);
        departments.computeIfAbsent(doctor.getDepartment(), k -> new ArrayList<>()).add(doctor);
    }
    
    public void registerNurse(Nurse nurse) {
        nurses.put(nurse.getEmployeeId(), nurse);
        departments.computeIfAbsent(nurse.getDepartment(), k -> new ArrayList<>()).add(nurse);
    }
    
    public void registerAdministrator(Administrator admin) {
        administrators.put(admin.getEmployeeId(), admin);
        departments.computeIfAbsent(admin.getDepartment(), k -> new ArrayList<>()).add(admin);
    }
    
    public void registerPatient(Patient patient) {
        patients.put(patient.getPatientId(), patient);
    }
    
    public void addRoom(Room room) {
        rooms.put(room.getRoomNumber(), room);
    }
    
    public void addLaboratory(Laboratory lab) {
        laboratories.put(lab.getLabId(), lab);
    }
    
    public void addPharmacy(Pharmacy pharmacy) {
        pharmacies.put(pharmacy.getPharmacyId(), pharmacy);
    }
    
    public void setupEmergencyDepartment(int capacity) {
        emergencyDept = new EmergencyDepartment(capacity);
    }
    
    public Doctor findAvailableDoctor(String specialization, LocalDateTime dateTime) {
        return doctors.values().stream()
            .filter(d -> d.getSpecialization().equalsIgnoreCase(specialization))
            .filter(d -> d.isAvailable(dateTime))
            .findFirst()
            .orElse(null);
    }
    
    public Room findAvailableRoom(RoomType roomType) {
        return rooms.values().stream()
            .filter(r -> r.getRoomType() == roomType)
            .filter(r -> !r.isOccupied() && !r.isMaintenanceRequired())
            .findFirst()
            .orElse(null);
    }
    
    public Appointment scheduleAppointment(Patient patient, Doctor doctor, 
                                          LocalDateTime dateTime, String reason) 
            throws InsufficientResourceException {
        Appointment appointment = new Appointment(patient, doctor, dateTime, reason);
        if (appointment.schedule(dateTime)) {
            appointments.add(appointment);
            patient.getAppointments().add(appointment);
            doctor.getAppointments().add(appointment);
            return appointment;
        }
        throw new InsufficientResourceException("Time slot not available");
    }
    
    public Admission admitPatient(Patient patient, Doctor doctor, RoomType roomType, String reason) 
            throws InsufficientResourceException {
        Room room = findAvailableRoom(roomType);
        if (room == null) {
            throw new InsufficientResourceException("No " + roomType.getDescription() + " available");
        }
        
        Admission admission = new Admission(patient, doctor, room, LocalDateTime.now(), reason);
        room.occupy(patient);
        patient.setAdmitted(true);
        patient.setAdmission(admission);
        admissions.add(admission);
        return admission;
    }
    
    public Bill dischargePatient(Admission admission, String summary) 
            throws ValidationException {
        if (!admission.discharge(summary)) {
            throw new ValidationException("Admission already completed");
        }
        
        Bill bill = new Bill(admission.patient);
        bill.addConsultationFee(admission.doctor);
        bill.addRoomCharges(admission);
        admission.patient.getBills().add(bill);
        
        return bill;
    }
    
    public List<MedicalStaff> getDepartmentStaff(String department) {
        return departments.getOrDefault(department, new ArrayList<>());
    }
    
    public List<Appointment> getDailyAppointments(LocalDate date) {
        return appointments.stream()
            .filter(apt -> apt.getDateTime().toLocalDate().equals(date))
            .filter(apt -> apt.getStatus() == AppointmentStatus.SCHEDULED)
            .collect(Collectors.toList());
    }
    
    public List<Room> getOccupiedRooms() {
        return rooms.values().stream()
            .filter(Room::isOccupied)
            .collect(Collectors.toList());
    }
    
    public List<Room> getAvailableRooms() {
        return rooms.values().stream()
            .filter(r -> !r.isOccupied() && !r.isMaintenanceRequired())
            .collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Object> generateReport() {
        int totalStaff = doctors.size() + nurses.size() + administrators.size();
        int occupiedRooms = (int) rooms.values().stream().filter(Room::isOccupied).count();
        int totalRooms = rooms.size();
        double occupancyRate = totalRooms > 0 ? (occupiedRooms * 100.0 / totalRooms) : 0;
        
        long activeAppointments = appointments.stream()
            .filter(a -> a.getStatus() == AppointmentStatus.SCHEDULED)
            .count();
        
        Map<String, Object> report = new HashMap<>();
        report.put("hospitalId", hospitalId);
        report.put("hospitalName", name);
        report.put("totalDoctors", doctors.size());
        report.put("totalNurses", nurses.size());
        report.put("totalAdministrators", administrators.size());
        report.put("totalStaff", totalStaff);
        report.put("totalPatients", patients.size());
        report.put("totalRooms", totalRooms);
        report.put("occupiedRooms", occupiedRooms);
        report.put("occupancyRate", String.format("%.2f%%", occupancyRate));
        report.put("activeAppointments", activeAppointments);
        report.put("totalAdmissions", admissions.size());
        report.put("activeAdmissions", admissions.stream().filter(Admission::isActive).count());
        report.put("laboratories", laboratories.size());
        report.put("pharmacies", pharmacies.size());
        report.put("departments", new ArrayList<>(departments.keySet()));
        return report;
    }
    
    public Map<String, Object> generateFinancialReport(LocalDateTime startDate, LocalDateTime endDate) {
        double totalRevenue = 0.0;
        double totalPaid = 0.0;
        double totalPending = 0.0;
        
        for (Patient patient : patients.values()) {
            for (Bill bill : patient.getBills()) {
                if (!bill.getCreatedDate().isBefore(startDate) && 
                    !bill.getCreatedDate().isAfter(endDate)) {
                    totalRevenue += bill.getTotalAmount();
                    if (bill.isPaid()) {
                        totalPaid += bill.getTotalAmount();
                    } else {
                        totalPending += bill.getTotalAmount();
                    }
                }
            }
        }
        
        Map<String, Object> report = new HashMap<>();
        report.put("period", startDate.toLocalDate() + " to " + endDate.toLocalDate());
        report.put("totalRevenue", totalRevenue);
        report.put("totalPaid", totalPaid);
        report.put("totalPending", totalPending);
        report.put("collectionRate", 
            totalRevenue > 0 ? String.format("%.2f%%", (totalPaid / totalRevenue * 100)) : "0%");
        return report;
    }
    
    public String getHospitalId() { return hospitalId; }
    public String getName() { return name; }
    public EmergencyDepartment getEmergencyDept() { return emergencyDept; }
}

class HospitalManagementSystem {
    private Map<String, Hospital> hospitals;
    private Map<String, InsuranceProvider> insuranceProviders;
    private LocalDateTime systemStartDate;
    private Map<String, String> adminUsers;
    
    public HospitalManagementSystem() {
        this.hospitals = new HashMap<>();
        this.insuranceProviders = new HashMap<>();
        this.systemStartDate = LocalDateTime.now();
        this.adminUsers = new HashMap<>();
        initializeSystem();
    }
    
    private void initializeSystem() {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest("admin123".getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            adminUsers.put("admin", hexString.toString());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void addHospital(Hospital hospital) {
        hospitals.put(hospital.getHospitalId(), hospital);
    }
    
    public void addInsuranceProvider(InsuranceProvider provider) {
        insuranceProviders.put(provider.getProviderId(), provider);
    }
    
    public boolean authenticateAdmin(String username, String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return adminUsers.getOrDefault(username, "").equals(hexString.toString());
        } catch (NoSuchAlgorithmException e) {
            return false;
        }
    }
    
    public Map<String, Object> getSystemStatistics() {
        int totalDoctors = hospitals.values().stream()
            .mapToInt(h -> h.doctors.size()).sum();
        int totalNurses = hospitals.values().stream()
            .mapToInt(h -> h.nurses.size()).sum();
        int totalPatients = hospitals.values().stream()
            .mapToInt(h -> h.patients.size()).sum();
        int totalAppointments = hospitals.values().stream()
            .mapToInt(h -> h.appointments.size()).sum();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("systemUptime", Duration.between(systemStartDate, LocalDateTime.now()));
        stats.put("totalHospitals", hospitals.size());
        stats.put("totalDoctors", totalDoctors);
        stats.put("totalNurses", totalNurses);
        stats.put("totalPatients", totalPatients);
        stats.put("totalAppointments", totalAppointments);
        stats.put("insuranceProviders", insuranceProviders.size());
        return stats;
    }
    
    public List<Doctor> searchDoctor(String specialization) {
        List<Doctor> results = new ArrayList<>();
        for (Hospital hospital : hospitals.values()) {
            for (Doctor doctor : hospital.doctors.values()) {
                if (doctor.getSpecialization().equalsIgnoreCase(specialization)) {
                    results.add(doctor);
                }
            }
        }
        return results;
    }
    
    public Patient searchPatient(String patientId) {
        for (Hospital hospital : hospitals.values()) {
            if (hospital.patients.containsKey(patientId)) {
                return hospital.patients.get(patientId);
            }
        }
        return null;
    }
}

public class HospitalManagementDemo {
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("HOSPITAL MANAGEMENT SYSTEM - DEMONSTRATION");
        System.out.println("=".repeat(80));
        
        try {
            HospitalManagementSystem hms = new HospitalManagementSystem();
            
            Address address = new Address("123 Medical Street", "Mumbai", "Maharashtra", 
                                         "400001", "India");
            ContactInfo contact = new ContactInfo("9876543210", "contact@hospital.com", 
                                                 "9876543211");
            
            Hospital hospital = new Hospital("City General Hospital", address, contact);
            hms.addHospital(hospital);
            
            hospital.setupEmergencyDepartment(20);
            
            Address docAddress = new Address("45 Residential Area", "Mumbai", "Maharashtra", 
                                            "400002", "India");
            ContactInfo docContact = new ContactInfo("9123456780", "doctor@email.com", 
                                                    "9123456781");
            
            Doctor doctor = new Doctor(
                "Rajesh", "Kumar", LocalDate.of(1980, 5, 15), "Male",
                docAddress, docContact, "DOC001", "Cardiology", 150000.0,
                LocalDate.of(2010, 1, 1), "Cardiologist", "MCI12345", 1500.0
            );
            
            hospital.registerDoctor(doctor);
            
            for (int i = 0; i < 5; i++) {
                LocalDateTime slotTime = LocalDateTime.now().plusDays(i + 1).plusHours(10 + i);
                doctor.schedule(slotTime);
            }
            
            Address nurseAddress = new Address("67 Housing Colony", "Mumbai", "Maharashtra", 
                                              "400003", "India");
            ContactInfo nurseContact = new ContactInfo("9234567890", "nurse@email.com", 
                                                      "9234567891");
            
            Nurse nurse = new Nurse(
                "Priya", "Sharma", LocalDate.of(1990, 3, 20), "Female",
                nurseAddress, nurseContact, "NUR001", "Cardiology", 50000.0,
                LocalDate.of(2015, 6, 1), "RN Certification"
            );
            
            hospital.registerNurse(nurse);
            
            Address patAddress = new Address("89 Citizen Street", "Mumbai", "Maharashtra", 
                                            "400004", "India");
            ContactInfo patContact = new ContactInfo("9345678901", "patient@email.com", 
                                                    "9345678902");
            
            Patient patient = new Patient(
                "Amit", "Patel", LocalDate.of(1985, 7, 10), "Male",
                patAddress, patContact, BloodGroup.O_POSITIVE, "INS123456"
            );
            
            patient.addAllergy("Penicillin");
            patient.addChronicCondition("Hypertension");
            hospital.registerPatient(patient);
            
            for (int i = 0; i < 5; i++) {
                Room room = new Room(
                    i < 3 ? RoomType.GENERAL : RoomType.PRIVATE,
                    i / 2 + 1, 2, i < 3 ? 2000.0 : 5000.0
                );
                hospital.addRoom(room);
            }
            
            Room icuRoom = new Room(RoomType.ICU, 3, 1, 10000.0);
            hospital.addRoom(icuRoom);
            
            Laboratory lab = new Laboratory("Central Lab", "Ground Floor");
            lab.addTestType("Blood Test", 500.0, 2);
            lab.addTestType("X-Ray", 1000.0, 1);
            lab.addTestType("ECG", 800.0, 1);
            lab.addTechnician("Lab Tech 1");
            hospital.addLaboratory(lab);
            
            Pharmacy pharmacy = new Pharmacy("Main Pharmacy", "First Floor");
            pharmacy.addPharmacist("Pharmacist Gupta");
            pharmacy.addMedication("Aspirin", 100, 50.0, 20);
            pharmacy.addMedication("Paracetamol", 150, 30.0, 30);
            pharmacy.addMedication("Amoxicillin", 80, 100.0, 15);
            hospital.addPharmacy(pharmacy);
            
            LocalDateTime appointmentTime = LocalDateTime.now().plusDays(1).plusHours(10);
            Appointment appointment = hospital.scheduleAppointment(
                patient, doctor, appointmentTime, "Regular Checkup"
            );
            
            System.out.println("\n✓ Appointment scheduled: " + appointment.getDetails());
            
            List<Map<String, Object>> medications = new ArrayList<>();
            Map<String, Object> med1 = new HashMap<>();
            med1.put("name", "Aspirin");
            med1.put("dosage", "75mg");
            med1.put("frequency", "Once daily");
            med1.put("durationDays", 30);
            medications.add(med1);
            
            Map<String, Object> med2 = new HashMap<>();
            med2.put("name", "Paracetamol");
            med2.put("dosage", "500mg");
            med2.put("frequency", "Twice daily");
            med2.put("durationDays", 7);
            medications.add(med2);
            
            Prescription prescription = doctor.prescribeMedication(patient, medications);
            patient.addPrescription(prescription);
            
            System.out.println("\n✓ Prescription created: " + prescription.getPrescriptionId());
            
            MedicalRecord medicalRecord = doctor.diagnose(
                patient, "Mild Hypertension",
                "Patient shows elevated blood pressure. Prescribed medication and lifestyle changes."
            );
            
            System.out.println("\n✓ Medical record created: " + medicalRecord.getSummary());
            
            LabTest labTest = lab.orderTest(patient, "Blood Test", doctor);
            lab.completeTest(labTest, "Normal ranges, Hemoglobin: 14.5 g/dL", "Lab Tech 1");
            
            System.out.println("\n✓ Lab test completed: " + labTest.getReport());
            
            Admission admission = hospital.admitPatient(
                patient, doctor, RoomType.GENERAL, "Chest pain observation"
            );
            
            System.out.println("\n✓ Patient admitted: " + admission.getDetails());
            
            nurse.assignPatient(patient);
            nurse.addTask("Check vital signs", "High", 
                         LocalDateTime.now().plusHours(2));
            
            Map<String, Object> vitalSigns = new HashMap<>();
            vitalSigns.put("bloodPressure", "140/90");
            vitalSigns.put("heartRate", 78);
            vitalSigns.put("temperature", 98.6);
            vitalSigns.put("oxygenSaturation", 98);
            nurse.recordVitalSigns(patient, vitalSigns);
            
            System.out.println("\n✓ Vital signs recorded: " + patient.getVitalSigns());
            
            Bill bill = new Bill(patient);
            bill.addConsultationFee(doctor);
            bill.addRoomCharges(admission);
            bill.addTestCharges("Blood Test", 500.0);
            bill.addMedicationCharges("Aspirin", 50.0, 1);
            bill.addMedicationCharges("Paracetamol", 30.0, 1);
            bill.applyDiscount(10);
            
            patient.getBills().add(bill);
            
            System.out.println("\n✓ Bill generated:");
            System.out.println("  Subtotal: ₹" + String.format("%.2f", bill.getTotalAmount() / 1.05 + bill.discount));
            System.out.println("  Total: ₹" + String.format("%.2f", bill.getTotalAmount()));
            
            InsuranceProvider insuranceProvider = new InsuranceProvider(
                "HealthCare Plus", Arrays.asList("Basic", "Premium")
            );
            hms.addInsuranceProvider(insuranceProvider);
            
            InsurancePolicy policy = insuranceProvider.createPolicy(
                patient, "Premium", 500000.0, 15000.0
            );
            
            System.out.println("\n✓ Insurance policy created: " + policy.getPolicyNumber());
            
            Map.Entry<Boolean, Double> claimResult = insuranceProvider.processClaim(
                policy.getPolicyNumber(), bill
            );
            
            if (claimResult.getKey()) {
                System.out.println("\n✓ Insurance claim approved: ₹" + 
                                 String.format("%.2f", claimResult.getValue()));
                double remaining = bill.getTotalAmount() - claimResult.getValue();
                System.out.println("  Patient pays: ₹" + String.format("%.2f", remaining));
            }
            
            bill.processPayment(PaymentMethod.CARD);
            System.out.println("\n✓ Bill paid using: " + bill.getPaymentMethod());
            
            Map<String, Object> saleRecord = pharmacy.dispenseMedication(
                prescription, "Pharmacist Gupta"
            );
            
            System.out.println("\n✓ Medication dispensed: ₹" + 
                             String.format("%.2f", (Double) saleRecord.get("totalCost")));
            
            if (hospital.getEmergencyDept() != null) {
                Patient emergencyPatient = new Patient(
                    "Ravi", "Singh", LocalDate.of(1995, 4, 25), "Male",
                    patAddress, patContact, BloodGroup.A_POSITIVE, null
                );
                hospital.registerPatient(emergencyPatient);
                
                boolean admitted = hospital.getEmergencyDept().triagePatient(
                    emergencyPatient, 2, "Severe headache and dizziness"
                );
                
                System.out.println("\n✓ Emergency patient triaged: " + 
                                 (admitted ? "Admitted" : "In queue"));
                System.out.println("  Emergency department status: " + 
                                 hospital.getEmergencyDept().getStatus());
            }
            
            Map<String, Object> hospitalReport = hospital.generateReport();
            System.out.println("\n" + "=".repeat(80));
            System.out.println("HOSPITAL REPORT");
            System.out.println("=".repeat(80));
            hospitalReport.forEach((key, value) -> 
                System.out.println(key.replace("_", " ").toUpperCase() + ": " + value)
            );
            
            Map<String, Object> financialReport = hospital.generateFinancialReport(
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now()
            );
            
            System.out.println("\n" + "=".repeat(80));
            System.out.println("FINANCIAL REPORT (Last 30 Days)");
            System.out.println("=".repeat(80));
            financialReport.forEach((key, value) -> 
                System.out.println(key.replace("_", " ").toUpperCase() + ": " + value)
            );
            
            Map<String, Object> doctorStats = doctor.getStatistics();
            System.out.println("\n" + "=".repeat(80));
            System.out.println("DOCTOR STATISTICS: Dr. " + doctor.getFullName());
            System.out.println("=".repeat(80));
            doctorStats.forEach((key, value) -> 
                System.out.println(key.replace("_", " ").toUpperCase() + ": " + value)
            );
            
            Map<String, Object> patientSummary = patient.getMedicalSummary();
            System.out.println("\n" + "=".repeat(80));
            System.out.println("PATIENT SUMMARY: " + patient.getFullName());
            System.out.println("=".repeat(80));
            patientSummary.forEach((key, value) -> 
                System.out.println(key.replace("_", " ").toUpperCase() + ": " + value)
            );
            
            List<Inventory> lowStockItems = pharmacy.getLowStockItems();
            if (!lowStockItems.isEmpty()) {
                System.out.println("\n" + "=".repeat(80));
                System.out.println("PHARMACY LOW STOCK ALERT");
                System.out.println("=".repeat(80));
                for (Inventory item : lowStockItems) {
                    System.out.println("  " + item.getItemName() + ": " + 
                                     item.getQuantity() + " units");
                }
            }
            
            Map<String, Object> systemStats = hms.getSystemStatistics();
            System.out.println("\n" + "=".repeat(80));
            System.out.println("SYSTEM STATISTICS");
            System.out.println("=".repeat(80));
            systemStats.forEach((key, value) -> 
                System.out.println(key.replace("_", " ").toUpperCase() + ": " + value)
            );
            
            System.out.println("\n" + "=".repeat(80));
            System.out.println("ADVANCED FEATURES DEMONSTRATION");
            System.out.println("=".repeat(80));
            
            Address adminAddress = new Address("Admin Block", "Mumbai", "Maharashtra", 
                                              "400005", "India");
            ContactInfo adminContact = new ContactInfo("9456789012", "admin@hospital.com", 
                                                      "9456789013");
            
            Administrator admin = new Administrator(
                "Suresh", "Mehta", LocalDate.of(1975, 8, 12), "Male",
                adminAddress, adminContact, "ADM001", "Administration",
                200000.0, LocalDate.of(2005, 3, 1), 5
            );
            
            hospital.registerAdministrator(admin);
            admin.addStaff(doctor);
            admin.addStaff(nurse);
            
            Map<String, Object> review = admin.conductPerformanceReview(
                doctor, 9, "Excellent patient care and punctuality"
            );
            
            System.out.println("\n✓ Performance review conducted:");
            System.out.println("  Staff: " + review.get("staffName"));
            System.out.println("  Rating: " + review.get("rating") + "/10");
            System.out.println("  Comments: " + review.get("comments"));
            
            doctor.addShift(
                LocalDate.now().plusDays(1),
                LocalTime.of(9, 0),
                LocalTime.of(17, 0)
            );
            
            List<Map<String, Object>> shifts = doctor.getShifts(
                LocalDate.now(),
                LocalDate.now().plusDays(7)
            );
            
            System.out.println("\n✓ Doctor shifts scheduled: " + shifts.size() + " shift(s)");
            
            try {
                String oldPassword = "default123";
                String newPassword = "secure_password_123";
                if (doctor.changePassword(oldPassword, newPassword)) {
                    System.out.println("\n✓ Password changed successfully for Dr. " + 
                                     doctor.getFullName());
                }
            } catch (AuthenticationException | ValidationException e) {
                System.out.println("\n✗ Password change failed: " + e.getMessage());
            }
            
            System.out.println("\n✓ Available doctors for Cardiology:");
            List<Doctor> cardioDoctors = hms.searchDoctor("Cardiologist");
            for (Doctor doc : cardioDoctors) {
                System.out.println("  - Dr. " + doc.getFullName() + 
                                 " (" + doc.getSpecialization() + ")");
            }
            
            List<LocalDateTime> availableSlots = doctor.getAvailableSlots(
                LocalDate.now().plusDays(1)
            );
            System.out.println("\n✓ Available appointment slots: " + availableSlots.size());
            for (int i = 0; i < Math.min(3, availableSlots.size()); i++) {
                System.out.println("  - " + availableSlots.get(i));
            }
            
            List<Map<String, Object>> pendingTasks = nurse.getPendingTasks();
            System.out.println("\n✓ Nurse pending tasks: " + pendingTasks.size());
            for (Map<String, Object> task : pendingTasks) {
                System.out.println("  - " + task.get("task") + 
                                 " (Priority: " + task.get("priority") + ")");
            }
            
            String dischargeSummary = "Patient recovered well. Blood pressure normalized. Continue medication.";
            Bill dischargeBill = hospital.dischargePatient(admission, dischargeSummary);
            
            System.out.println("\n✓ Patient discharged");
            System.out.println("  Discharge bill: " + dischargeBill.getBillId());
            System.out.println("  Total charges: ₹" + 
                             String.format("%.2f", dischargeBill.getTotalAmount()));
            
            if (prescription.isValid() && prescription.getRefillsRemaining() > 0) {
                if (prescription.refill()) {
                    System.out.println("\n✓ Prescription refilled");
                    System.out.println("  Refills remaining: " + 
                                     prescription.getRefillsRemaining());
                    System.out.println("  Valid until: " + 
                                     prescription.getValidUntil().toLocalDate());
                }
            }
            
            appointment.complete("Patient examined. No major concerns. Follow-up in 1 month.");
            System.out.println("\n✓ Appointment completed: " + 
                             appointment.getAppointmentId());
            
            Map<String, Object> labStats = lab.getStatistics();
            System.out.println("\n" + "=".repeat(80));
            System.out.println("LABORATORY STATISTICS");
            System.out.println("=".repeat(80));
            labStats.forEach((key, value) -> 
                System.out.println(key.replace("_", " ").toUpperCase() + ": " + value)
            );
            
            System.out.println("\n" + "=".repeat(80));
            System.out.println("OOP CONCEPTS DEMONSTRATION");
            System.out.println("=".repeat(80));
            
            System.out.println("\n✓ POLYMORPHISM:");
            List<MedicalStaff> staffList = Arrays.asList(doctor, nurse, admin);
            for (MedicalStaff staff : staffList) {
                System.out.println("  - " + staff.getFullName() + ": " + staff.getType());
            }
            
            System.out.println("\n✓ INTERFACES:");
            System.out.println("  - Identifiable: " + doctor.getId() + " (" + doctor.getType() + ")");
            System.out.println("  - Billable: ₹" + String.format("%.2f", patient.calculateCharges()));
            System.out.println("  - Reportable: " + hospital.generateReport().size() + " items");
            
            System.out.println("\n" + "=".repeat(80));
            System.out.println("DEMONSTRATION COMPLETED SUCCESSFULLY");
            System.out.println("=".repeat(80));
            
            System.out.println("\nTotal lines of code: 1800+");
            System.out.println("Total classes: 25+");
            System.out.println("Total methods: 200+");
            System.out.println("\nOOP Concepts Demonstrated:");
            System.out.println("  ✓ Encapsulation");
            System.out.println("  ✓ Inheritance (Single & Multi-level)");
            System.out.println("  ✓ Polymorphism (Method Overriding)");
            System.out.println("  ✓ Abstraction (Abstract Classes)");
            System.out.println("  ✓ Interfaces (Multiple Implementation)");
            System.out.println("  ✓ Composition");
            System.out.println("  ✓ Aggregation");
            System.out.println("  ✓ Association");
            System.out.println("  ✓ Exception Handling");
            System.out.println("  ✓ Static Members");
            System.out.println("  ✓ Enumerations");
            System.out.println("  ✓ Collections Framework");
            System.out.println("  ✓ Streams API");
            System.out.println("  ✓ Lambda Expressions");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
