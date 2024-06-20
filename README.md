# Description
Simple Point of Sales System that built for ticketing.

- Basic username and password authentication to differentiate user access level
  - Cashier user: input customer's order and print the ticket
  - Admin: modify ticket prices
- Ticket printing, currently only support speicific bluetooth printer, bluetooth printer address is hardcoded
- Storing data to the cloud

## Login page
- Username and password login
<img src="https://github.com/bedhilzz/tirta-wahyu/assets/25608832/76cb550e-350c-4911-929d-f1e2cd2345a0" alt="" height="500" />

## Main page
- Left side is the catalog
- Right side is the _cart_
- Add item from catalog to cart with "+" button
- Remove from cart with "X" button
- Process and checkout the item, will initiate ticket printing
<img src="https://github.com/bedhilzz/tirta-wahyu/assets/25608832/542fb39e-8c7f-4da5-bdc8-52ee6c33bf7e" alt="" width="500" />


# Tech Stack
- Android: MVVM, using data binding
- Cloud database: Cloud Firestore

# Future Improvement
- Support multiple kind of printers, allow to choose bluetooth printer from bluetooth discovery results
- Analytics dashboard
