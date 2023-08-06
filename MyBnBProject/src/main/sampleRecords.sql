INSERT INTO
    Availability (
        Street,
        Number,
        PostalCode,
        Country,
        Date,
        Available,
        Price
    )
VALUES
    (
        "Expensive Road",
        858,
        "B3C3B2",
        "United States",
        "2023-09-09",
        1,
        999.99
    );

INSERT INTO
    Booked (
        SIN,
        Street,
        Number,
        PostalCode,
        Country,
        FromDate,
        ToDate,
        PaymentMethod,
        Price
    )
VALUES
    (
        555555555,
        "Expensive Road",
        858,
        "B3C3B2",
        "United States",
        "2023-09-10",
        "2023-09-12",
        "Cash",
        2269.5
    );

UPDATE
    Availability
SET
    Available = 0
WHERE
    Street = "Expensive Road"
    AND Number = 858
    AND PostalCode = "B3C3B2"
    AND Country = "United States"
    AND (
        Date BETWEEN "2023-09-10"
        AND "2023-09-12"
    );

UPDATE
    Booked
SET
    Canceled = 1,
    Updated = 1
WHERE
    BID = 1;

INSERT INTO
    Cancellation (BID, SIN)
VALUES
    (1, 111111111) ON DUPLICATE KEY
UPDATE
    SIN = SIN;

UPDATE
    Booked
SET
    Canceled = 1
WHERE
    BID = 3;

INSERT INTO
    Cancellation (BID, SIN)
VALUES
    (3, 123456789) ON DUPLICATE KEY
UPDATE
    SIN = SIN;

INSERT INTO
    Comment (SIN, Rating, Text)
VALUES
    (555555555, 1, "I hate this renter so much");

INSERT INTO
    CommentOnUser (CID, SINComment)
VALUES
    (LAST_INSERT_ID(), 123456789);

INSERT INTO
    Comment (SIN, Rating, Text)
VALUES
    (
        111111111,
        4,
        "This renter was pretty good... better than most"
    );

INSERT INTO
    CommentOnUser (CID, SINComment)
VALUES
    (LAST_INSERT_ID(), 555555555);

INSERT INTO
    Comment (SIN, Rating, Text)
VALUES
    (
        123456789,
        3,
        "This listing was okay, just mediocre"
    );

INSERT INTO
    CommentOnListing (CID, Street, Number, PostalCode, Country)
VALUES
    (
        LAST_INSERT_ID(),
        "Main Street",
        125,
        "A1A1A1",
        "Canada"
    );

INSERT INTO
    Comment (SIN, Rating, Text)
VALUES
    (
        555555555,
        2,
        "I guess it is just mediocre..."
    );

INSERT INTO
    CommentOnListing (CID, Street, Number, PostalCode, Country)
VALUES
    (
        LAST_INSERT_ID(),
        "Main Street",
        125,
        "A1A1A1",
        "Canada"
    );

INSERT INTO
    Comment (SIN, Rating, Text)
VALUES
    (
        555555555,
        5,
        "This listing was amazing, I know I would've loved it — if I had actually gone."
    );

INSERT INTO
    CommentOnListing (CID, Street, Number, PostalCode, Country)
VALUES
    (
        LAST_INSERT_ID(),
        "Main Street",
        122,
        "A1A1A1",
        "Canada"
    );