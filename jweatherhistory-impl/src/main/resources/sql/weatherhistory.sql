CREATE TABLE RAINENTRY
(
	ID              INT PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY,
	DESCRIPTION     VARCHAR(500),
	VOLUME          INT,
	WEATHERENTRY_ID INT
);
CREATE TABLE WINDENTRY
(
	ID              INT PRIMARY KEY NOT NULL GENERATED ALWAYS AS IDENTITY,
	DESCRIPTION     VARCHAR(500),
	WINDDIRECTION   VARCHAR(64),
	WINDSPEED       INT,
	WEATHERENTRY_ID INT
);
CREATE TABLE WEATHERENTRY
(
	ID           INT NOT NULL GENERATED ALWAYS AS IDENTITY,
	DESCRIPTION  VARCHAR(500),
	MINIMUM_TEMPERATURE INT,
	MAXIMUM_TEMPERATURE INT,
	ENTRY_DATE   DATE,
	CAPTURE_DATE TIMESTAMP,
	WINDENTRY_ID INT,
	RAINENTRY_ID INT,
	PRIMARY KEY (ID),
	FOREIGN KEY (RAINENTRY_ID) REFERENCES RAINENTRY (ID),
	FOREIGN KEY (WINDENTRY_ID) REFERENCES WINDENTRY (ID)
);

ALTER TABLE RAINENTRY
ADD FOREIGN KEY (WEATHERENTRY_ID) REFERENCES WEATHERENTRY (ID);

ALTER TABLE WINDENTRY
ADD FOREIGN KEY (WEATHERENTRY_ID) REFERENCES WEATHERENTRY (ID);
