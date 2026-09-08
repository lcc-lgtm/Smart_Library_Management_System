# Smart Library Management System

A console-based Java application that models a university library's book, journal, and digital-resource lending workflow. Built as an Object-Oriented Programming (OOP) coursework project (AMCS2204 — TAR UMT), it demonstrates abstraction, encapsulation, inheritance, and polymorphism through a fully working system rather than isolated code snippets.

---

## All Availability Functions

- **Multi-role users** — Student, Faculty, Public Member, and Librarian accounts, each with its own borrowing limit and loan duration.
- **Catalog management** — Books, Journals, and Digital Resources modeled as subclasses of an abstract `Resource` class, searchable by title, author, genre, or ISBN.
- **Borrowing workflow** — Borrow and return items with automatic due-date calculation and availability tracking.
- **Reservations** — Users can queue for items that are currently on loan; the system tracks the reservation order per resource.
- **Fines** — Overdue returns automatically generate fines (RM 1.00/day by default), which users can view and pay, and librarians can process.
- **Librarian tools** — Add, update, and remove catalog resources; manage user accounts; process fine payments.
- **Reports** — Most-borrowed items, revenue from fines, overdue items, and active users.
- **Persistent storage** — Data is serialized to disk (`library_data/*.dat`) and reloaded automatically on the next run.
- **Robust input handling** — Validated input and exception handling throughout so invalid entries don't crash the program.

---

## Project Structure

```text
.
├── SmartLibrarySystem.java   # Entry point / console UI and menu logic
├── Library.java              # Core business logic (users, resources, transactions, fines, reports)
├── Resource.java             # Abstract base class for all lendable items
├── Book.java                 # Resource subclass
├── Journal.java              # Resource subclass
├── DigitalResource.java      # Resource subclass
├── User.java                 # Base user class (Student / Faculty / Public Member)
├── Librarian.java            # User subclass with staff privileges
├── Transaction.java          # Represents a borrow/return record
└── Fine.java                 # Represents an overdue fine
