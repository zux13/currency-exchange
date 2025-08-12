-- Валюты
INSERT OR IGNORE INTO Currencies (Code, FullName, Sign)
VALUES ('USD', 'United States dollar', '$');

INSERT OR IGNORE INTO Currencies (Code, FullName, Sign)
VALUES ('EUR', 'Euro', '€');

INSERT OR IGNORE INTO Currencies (Code, FullName, Sign)
VALUES ('RUB', 'Russian Ruble', '₽');

INSERT OR IGNORE INTO Currencies (Code, FullName, Sign)
VALUES ('AUD', 'Australian dollar', 'A$');

-- Курсы
INSERT OR IGNORE INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate)
SELECT c1.ID, c2.ID, 0.91
FROM Currencies c1, Currencies c2
WHERE c1.Code = 'USD' AND c2.Code = 'EUR';

INSERT OR IGNORE INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate)
SELECT c1.ID, c2.ID, 1.1
FROM Currencies c1, Currencies c2
WHERE c1.Code = 'EUR' AND c2.Code = 'USD';

INSERT OR IGNORE INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate)
SELECT c1.ID, c2.ID, 80
FROM Currencies c1, Currencies c2
WHERE c1.Code = 'USD' AND c2.Code = 'RUB';

INSERT OR IGNORE INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate)
SELECT c1.ID, c2.ID, 0.015
FROM Currencies c1, Currencies c2
WHERE c1.Code = 'RUB' AND c2.Code = 'USD';

INSERT OR IGNORE INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate)
SELECT c1.ID, c2.ID, 1.45
FROM Currencies c1, Currencies c2
WHERE c1.Code = 'USD' AND c2.Code = 'AUD';
