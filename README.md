# Currency Exchange

This is a simple currency exchange application that provides a RESTful API for managing currencies and exchange rates.

## Building and Running the Project

To build the project, run the following command:

```bash
./gradlew build
```

This will create a `currency-exchange.war` file in the `build/libs` directory. You can then deploy this file to a servlet container like Apache Tomcat.

## API Documentation

The API is divided into three main resources: Currencies, Exchange Rates, and Exchange.

### Currencies

#### Get All Currencies

-   **Endpoint:** `GET /currencies`
-   **Description:** Retrieves a list of all available currencies.
-   **Success Response:**
    -   **Code:** 200 OK
    -   **Content:**
        ```json
        [
            {
                "id": 1,
                "name": "US Dollar",
                "code": "USD",
                "sign": "$"
            },
            {
                "id": 2,
                "name": "Euro",
                "code": "EUR",
                "sign": "€"
            }
        ]
        ```

#### Get Currency by Code

-   **Endpoint:** `GET /currency/{code}`
-   **Description:** Retrieves a single currency by its three-letter code.
-   **URL Parameters:**
    -   `code` (required): The three-letter code of the currency (e.g., `USD`).
-   **Success Response:**
    -   **Code:** 200 OK
    -   **Content:**
        ```json
        {
            "id": 1,
            "name": "US Dollar",
            "code": "USD",
            "sign": "$"
        }
        ```
-   **Error Response:**
    -   **Code:** 404 Not Found
    -   **Content:** `{"message": "Currency with code 'USD' not found"}`

#### Create a New Currency

-   **Endpoint:** `POST /currencies`
-   **Description:** Creates a new currency.
-   **Form Parameters:**
    -   `name` (required): The full name of the currency (e.g., `British Pound`).
    -   `code` (required): The three-letter code of the currency (e.g., `GBP`).
    -   `sign` (required): The symbol of the currency (e.g., `£`).
-   **Success Response:**
    -   **Code:** 201 Created
    -   **Content:**
        ```json
        {
            "id": 3,
            "name": "British Pound",
            "code": "GBP",
            "sign": "£"
        }
        ```
-   **Error Responses:**
    -   **Code:** 400 Bad Request (e.g., missing a field)
    -   **Content:** `{"message": "Field 'name' is required"}`
    -   **Code:** 409 Conflict (e.g., currency code already exists)
    -   **Content:** `{"message": "Currency with code 'GBP' already exists"}`

---

### Exchange Rates

#### Get All Exchange Rates

-   **Endpoint:** `GET /exchangeRates`
-   **Description:** Retrieves a list of all available exchange rates.
-   **Success Response:**
    -   **Code:** 200 OK
    -   **Content:**
        ```json
        [
            {
                "id": 1,
                "baseCurrency": { "id": 1, "name": "US Dollar", "code": "USD", "sign": "$" },
                "targetCurrency": { "id": 2, "name": "Euro", "code": "EUR", "sign": "€" },
                "rate": 0.92
            }
        ]
        ```

#### Get Exchange Rate by Currency Pair

-   **Endpoint:** `GET /exchangeRate/{pair}`
-   **Description:** Retrieves a single exchange rate by its currency pair (e.g., `USDEUR`).
-   **URL Parameters:**
    -   `pair` (required): The currency pair (e.g., `USDEUR`).
-   **Success Response:**
    -   **Code:** 200 OK
    -   **Content:**
        ```json
        {
            "id": 1,
            "baseCurrency": { "id": 1, "name": "US Dollar", "code": "USD", "sign": "$" },
            "targetCurrency": { "id": 2, "name": "Euro", "code": "EUR", "sign": "€" },
            "rate": 0.92
        }
        ```
-   **Error Response:**
    -   **Code:** 404 Not Found
    -   **Content:** `{"message": "Exchange rate for pair 'USDEUR' not found"}`

#### Create a New Exchange Rate

-   **Endpoint:** `POST /exchangeRates`
-   **Description:** Creates a new exchange rate.
-   **Form Parameters:**
    -   `baseCurrencyCode` (required): The code of the base currency.
    -   `targetCurrencyCode` (required): The code of the target currency.
    -   `rate` (required): The exchange rate.
-   **Success Response:**
    -   **Code:** 201 Created
    -   **Content:**
        ```json
        {
            "id": 2,
            "baseCurrency": { "id": 1, "name": "US Dollar", "code": "USD", "sign": "$" },
            "targetCurrency": { "id": 3, "name": "British Pound", "code": "GBP", "sign": "£" },
            "rate": 0.79
        }
        ```
-   **Error Responses:**
    -   **Code:** 404 Not Found (if a currency is not found)
    -   **Content:** `{"message": "Currency with code 'XYZ' not found"}`
    -   **Code:** 409 Conflict (if the exchange rate already exists)
    -   **Content:** `{"message": "Exchange rate for pair 'USDGBP' already exists"}`

#### Update an Exchange Rate

-   **Endpoint:** `PATCH /exchangeRate/{pair}`
-   **Description:** Updates an existing exchange rate.
-   **URL Parameters:**
    -   `pair` (required): The currency pair (e.g., `USDEUR`).
-   **Form Parameters:**
    -   `rate` (required): The new exchange rate.
-   **Success Response:**
    -   **Code:** 200 OK
    -   **Content:**
        ```json
        {
            "id": 1,
            "baseCurrency": { "id": 1, "name": "US Dollar", "code": "USD", "sign": "$" },
            "targetCurrency": { "id": 2, "name": "Euro", "code": "EUR", "sign": "€" },
            "rate": 0.93
        }
        ```
-   **Error Response:**
    -   **Code:** 404 Not Found
    -   **Content:** `{"message": "Exchange rate for pair 'USDEUR' not found"}`

---

### Exchange

#### Calculate Exchange Amount

-   **Endpoint:** `GET /exchange`
-   **Description:** Calculates the exchange amount between two currencies. It can handle direct and reverse exchange rates.
-   **Query Parameters:**
    -   `from` (required): The code of the currency to exchange from.
    -   `to` (required): The code of the currency to exchange to.
    -   `amount` (required): The amount to exchange.
-   **Success Response:**
    -   **Code:** 200 OK
    -   **Content:**
        ```json
        {
            "baseCurrency": { "id": 1, "name": "US Dollar", "code": "USD", "sign": "$" },
            "targetCurrency": { "id": 2, "name": "Euro", "code": "EUR", "sign": "€" },
            "rate": 0.92,
            "amount": 100,
            "convertedAmount": 92.00
        }
        ```
-   **Error Response:**
    -   **Code:** 404 Not Found
    -   **Content:** `{"message": "Exchange rate for pair 'USDEUR' not found"}`
