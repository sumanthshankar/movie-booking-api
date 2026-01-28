### High Level Architecture
Client
  |
REST API (Spring Boot)
  |
Service Layer
  |
Repository
  |
H2 (In memory DB)

### API Endpoints
- POST Request bookings/book `{
    "userEmail": "sumanthshankar333@gmail.com",
    "showId": 1,
    "seatIds": [1,2]
}`
Response `{
    "bookedAt": "2026-01-27T11:03:19.190986",
    "bookingReference": "4955349165",
    "bookingStatus": "CONFIRMED"
}`

### Data Models
- Booking [ID(PK), Total_Amount, Booked_At, Booking_Reference, Booking_Status, Show_ID(FK), User_ID(FK)]

- City [ID(PK), Name]

- Movie [ID(PK), Name]

- Seat [ID(PK), Seat_Number, Status, Version, Show_ID(FK)]

- Show [ID(PK), Price, Show_Date, Show_Time, Movie_ID(FK), Theatre_ID(FK)]

- Theatre [ID(PK), Name, City_ID(FK)]

- Users [ID(PK), Email, Name]

### Design Patterns Used
- Layered architecture
`Controller
   ->
Service
   ->
Repository
   ->
Database`

- DTO pattern

- Optimistic locking: No locks held during read, and assuming conflicts are rare

- Global exception handler

### Non-Functional Requirements for the booking application

Since the data involves multiple relationships and requires strong consistency and transactional guarantees, an RDBMS is the appropriate database choice. It can be horizontally scaled using sharding, with shard keys selected based on region and user.

#### Transaction Management & Consistency: Ticket booking is a critical transactional workflow involving seat selection, payment processing, and booking confirmation. The system must prevent double-booking of seats while handling concurrent user requests.
- Use database transactions for core booking operations.
- Apply optimistic locking to handle concurrent updates safely.
- Introduce a temporary seat-lock mechanism using Redis distributed TTL locks. If payment fails or times out, locked seats are automatically released using TTL-based expiration.

#### Integration & Extensibility: The platform must integrate with theatres having existing IT systems and support the onboarding of new partners.
- Expose REST-based APIs for theatre partners with modern systems.
- Enable manual configuration of shows, seats, and pricing through secure APIs or dashboards.
- Store localised movie metadata.

#### Availability, Scalability and Performance: The platform must handle high read traffic and spiky write traffic. End users expect fast response times, especially during browsing and seat availability checks.
- Stateless service design to enable horizontal scaling behind load balancers.
- Using Elastic Search for low-latency search.
- Cache frequently accessed read data with LRU eviction policy.
- Index frequently queried fields such as city, movie, show date, and theatre.
- Increased infrastructure cost due to caching and replication, balanced against improved performance and user experience.

#### Integration with Payment Gateways: The platform must support secure, reliable, and scalable payment processing for ticket bookings while handling failures, retries, and third-party downtime without impacting seat availability or user experience.
- Use a redirect or hosted checkout model provided by the payment gateway to avoid handling sensitive card data directly.
- Generate a payment intent/order before redirecting the user to the gateway.
- Confirm payment asynchronously via webhooks rather than relying solely on synchronous callbacks.
- Assign a unique payment reference ID per booking.

#### Monetisation Strategy for the Platform: A few ways of generating revenue are 
- Platform charges a percentage-based commission on each ticket sold.
- Flat or dynamic service fee per transaction charged to customers.


#### Security: The platform handles sensitive user and payment-related data and must be protected against common security vulnerabilities.
- Prevent SQL injection using ORM frameworks and prepared statements.
- Secure APIs using OAuth2/JWT-based authentication.
- Role-based access control for Admins, Theatre Partners, and Customers.
- For PCI-DSS compliance, encrypt sensitive data and do not store payment details.
- Implement rate limiting and input validation to prevent abuse.

#### Compliance, Monitoring & Observability: Operational visibility and regulatory compliance are essential for a production-grade platform.
- Centralised logging and metrics collection for key metrics like payment failures and booking success rate.
- Alerting mechanisms for system health.
