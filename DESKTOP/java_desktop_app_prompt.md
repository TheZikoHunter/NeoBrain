# Java Desktop Application Development Prompt

## Project Overview
Create a complete desktop application for inventory and sales management using Java Swing, PostgreSQL, and Hibernate ORM. The application should implement role-based authentication and all specified use cases from the provided UML models.

## Technical Stack Requirements
- **Frontend**: Java Swing with modern UI components
- **Backend**: Java with Hibernate ORM
- **Database**: PostgreSQL
- **Architecture**: Layered architecture (Presentation, Service, Repository, Entity)
- **Build Tool**: Maven or Gradle
- **Java Version**: Java 11 or higher

## Database Schema Implementation

### Core Entities to Implement:

#### 1. Person Hierarchy
```sql
-- Base class: Personne
- idPersonne (Primary Key)
- civilite (ENUM: M, MLLE, MME)
- nom, prenom
- dateNaissance
- email, telephone
- nationalite (ENUM: MAROCCAINE, FRANCAISE, AMERICAINE)
- nom_utilisateur, mot_de_passe

-- Inherited: Personnel
- idPersonnel
- dateEmbauche
- role (ENUM: RESPONSABLE_STOCK, EMPLOYE_STOCK, ADMIN, etc.)

-- Inherited: Client
- email (additional)
- estFidele
- modePaiement (ENUM: CHEQUE, ESPECE, CARTE)
```

#### 2. Product Management
```sql
-- Produit
- idProduit (Primary Key)
- description, prix
- categorie (ENUM: INFORMATIQUE, ELECTROMENAGER, VETEMENTS, SPORTS)
- quantiteStock
- disponible, besoinInventaire
- dateAjout, dernierInventaire
```

#### 3. Inventory System
```sql
-- Inventaire
- idInventaire (Primary Key)
- dateDebut, dateFin
- estClos, etatInventaire

-- TacheInventaire
- idTacheInventaire (Primary Key)
- dateTache
- etatTache (ENUM to be defined)
- quantitePhysique
- Foreign Keys: idPersonnel, idProduit, idInventaire
```

#### 4. Sales Management
```sql
-- CommandeVente
- idCommandeVente (Primary Key)
- dateCommandeVente
- prixTotal
- estValide, estExpediee, etatEchec

-- LigneCommande
- idLigneVente (Primary Key)
- quantiteVente
- etatEchec, etatRetour
- Foreign Keys: idCommandeVente, idProduit

-- Reclamation
- idReclamation (Primary Key)
- dateReclamation
- etatReclamation (ENUM: EN_ATTENTE, VALIDEE, REFUSEE)
- typeReclamation (ENUM: RETOUR, ECHEC_RECEPTION)
- Foreign Key: idLigneCommande
```

## Authentication & Authorization System

### Requirements:
1. **Login System**: Username/password authentication
2. **Role-Based Access Control**: Different interfaces based on user roles
3. **Session Management**: Track logged-in users
4. **Password Security**: Hash passwords using BCrypt or similar

### Role Permissions:
- **ADMIN**: Personnel management (add, modify, delete)
- **RESPONSABLE_STOCK**: Product management, inventory management, view sales orders
- **EMPLOYE_STOCK**: View assigned inventory tasks, perform inventory tasks

## Use Cases Implementation

### Admin Module:
1. **Personnel Management**:
   - Add personnel with role assignment
   - Modify personnel information
   - Delete personnel
   - View personnel list

### Stock Manager Module:
1. **Product Management**:
   - Add/modify/delete products
   - View product details
   - Manage product categories

2. **Inventory Management**:
   - Create new inventory
   - Assign inventory tasks to stock employees
   - View inventory reports

3. **Sales Order Management**:
   - View validated sales orders
   - Mark orders as shipped

### Stock Employee Module:
1. **Inventory Tasks**:
   - View assigned inventory tasks
   - Perform inventory tasks (record physical quantities)
   - Update task status

## UI/UX Requirements

### Main Application Structure:
1. **Login Window**: Clean, professional login form
2. **Main Dashboard**: Role-specific navigation menu
3. **CRUD Forms**: Consistent forms for data entry/editing
4. **Data Tables**: Sortable, filterable tables for data display
5. **Dialog Windows**: Modal dialogs for confirmations and detailed views

### UI Components to Include:
- Modern look with consistent styling
- Input validation with user-friendly error messages
- Loading indicators for database operations
- Confirmation dialogs for delete operations
- Search and filter capabilities
- Export functionality for reports

## Application Architecture

### Package Structure:
```
com.yourcompany.inventory
├── entity/          # Hibernate entities
├── repository/      # Data access layer
├── service/         # Business logic layer
├── controller/      # Presentation layer controllers
├── ui/             # Swing UI components
│   ├── login/
│   ├── admin/
│   ├── stock/
│   └── common/
├── config/         # Configuration classes
├── util/           # Utility classes
└── Main.java       # Application entry point
```

### Key Features to Implement:
1. **Connection Pooling**: Use HikariCP for database connections
2. **Transaction Management**: Proper transaction handling
3. **Exception Handling**: Comprehensive error handling
4. **Logging**: Use SLF4J with Logback
5. **Configuration**: External configuration file support

## Development Guidelines

### Code Quality:
- Follow Java naming conventions
- Implement proper exception handling
- Use design patterns where appropriate (DAO, Factory, Observer)
- Write clean, documented code
- Implement input validation

### Database Best Practices:
- Use proper foreign key constraints
- Implement database indexes for performance
- Use connection pooling
- Handle database transactions properly

### Security Considerations:
- Hash passwords before storing
- Validate all user inputs
- Implement proper session management
- Use parameterized queries to prevent SQL injection

## Deliverables Expected:

1. **Complete source code** with proper package structure
2. **Database setup scripts** (PostgreSQL schema creation)
3. **Maven/Gradle configuration** files
4. **Basic documentation** explaining how to run the application
5. **Sample data** for testing purposes

## Additional Features (If Time Permits):
- Data export functionality (CSV, PDF)
- Basic reporting system
- Audit trail for important operations
- Backup/restore functionality
- Multi-language support

## Technical Notes:
- Ensure the application works on Windows, Mac, and Linux
- Handle database connection failures gracefully
- Implement proper resource cleanup
- Use modern Java features appropriately
- Consider using a UI framework like FlatLaf for better look and feel

Please implement this step by step, starting with:
1. Project setup and dependencies
2. Database schema and Hibernate configuration
3. Basic authentication system
4. Core entities and repositories
5. UI framework and main window
6. Individual use case implementations

Focus on creating a professional, maintainable, and user-friendly application that fully implements the requirements from the UML documentation.