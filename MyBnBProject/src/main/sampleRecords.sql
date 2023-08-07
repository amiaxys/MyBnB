-- Insert availability records
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

-- Insert booked records
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
SELECT
    555555555,
    "Expensive Road",
    858,
    "B3C3B2",
    "United States",
    "2023-09-10",
    "2023-09-12",
    "Cash",
    2269.5
FROM
    DUAL
WHERE
    NOT EXISTS (
        SELECT
            *
        FROM
            Booked
        WHERE
            SIN = 555555555
            AND Street = "Expensive Road"
            AND Number = 858
            AND PostalCode = "B3C3B2"
            AND Country = "United States"
        LIMIT
            1
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

-- Insert cancellation records
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

-- Insert comment records
INSERT INTO
    Comment (SIN, Rating, Text)
SELECT
    555555555,
    1,
    "I hate this renter so much"
FROM
    DUAL
WHERE
    NOT EXISTS (
        SELECT
            *
        FROM
            Comment
        WHERE
            SIN = 555555555
            AND Rating = 1
            AND Text = "I hate this renter so much"
        LIMIT
            1
    );

INSERT INTO
    CommentOnUser (CID, SINComment)
SELECT
    LAST_INSERT_ID(),
    123456789
WHERE
    NOT EXISTS (
        SELECT
            *
        FROM
            CommentOnUser
        WHERE
            SINComment = 123456789
        LIMIT
            1
    );

INSERT INTO
    Comment (SIN, Rating, Text)
SELECT
    111111111,
    4,
    "This renter was pretty good... better than most"
FROM
    DUAL
WHERE
    NOT EXISTS (
        SELECT
            *
        FROM
            Comment
        WHERE
            SIN = 111111111
            AND Rating = 4
            AND Text = "This renter was pretty good... better than most"
        LIMIT
            1
    );

INSERT INTO
    CommentOnUser (CID, SINComment)
SELECT
    LAST_INSERT_ID(),
    555555555
WHERE
    NOT EXISTS (
        SELECT
            *
        FROM
            CommentOnUser
        WHERE
            SINComment = 555555555
        LIMIT
            1
    );

INSERT INTO
    Comment (SIN, Rating, Text)
SELECT
    123456789,
    3,
    "This listing was okay, just mediocre"
FROM
    DUAL
WHERE
    NOT EXISTS (
        SELECT
            *
        FROM
            Comment
        WHERE
            SIN = 123456789
            AND Rating = 3
            AND Text = "This listing was okay, just mediocre"
        LIMIT
            1
    );

INSERT INTO
    CommentOnListing (CID, Street, Number, PostalCode, Country)
SELECT
    LAST_INSERT_ID(),
    "Main Street",
    125,
    "A1A1A1",
    "Canada"
WHERE
    NOT EXISTS (
        SELECT
            *
        FROM
            CommentOnListing
        WHERE
            Street = "Main Street"
            AND Number = 125
            AND PostalCode = "A1A1A1"
            AND Country = "Canada"
            AND SIN = 123456789
        LIMIT
            1
    );

INSERT INTO
    Comment (SIN, Rating, Text)
SELECT
    555555555,
    5,
    "This listing was amazing, I know I would've loved it — if I had actually gone."
FROM
    DUAL
WHERE
    NOT EXISTS (
        SELECT
            *
        FROM
            Comment
        WHERE
            SIN = 555555555
            AND Rating = 5
            AND Text = "This listing was amazing, I know I would've loved it — if I had actually gone."
        LIMIT
            1
    );

INSERT INTO
    CommentOnListing (CID, Street, Number, PostalCode, Country)
SELECT
    LAST_INSERT_ID(),
    "Main Street",
    122,
    "A1A1A1",
    "Canada"

WHERE
    NOT EXISTS (
        SELECT
            *
        FROM
            CommentOnListing
        WHERE
            Street = "Main Street"
            AND Number = 122
            AND PostalCode = "A1A1A1"
            AND Country = "Canada"
        LIMIT
            1
    );