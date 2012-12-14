CREATE TABLE WEATHERENTRY (
	ID INT not null GENERATED ALWAYS AS IDENTITY,
 DESCRIPTION varchar (500),
	DATETIME DATE,
	WINDENTRY_ID INT CONSTRAINT WID REFERENCES WINDENTRY,
	RAINENTRY_ID INT CONSTRAINT RID REFERENCES RAINENTRY
);

CREATE TABLE WINDENTRY (
	ID            INT NOT NULL GENERATED ALWAYS AS IDENTITY,
	DESCRIPTION   VARCHAR(500),
	WINDDIRECTION VARCHAR(64),
	WINDSPEED     INT,
	PRIMARY KEY (ID)
);

CREATE TABLE RAINENTRY (
	ID          INT NOT NULL GENERATED ALWAYS AS IDENTITY,
	DESCRIPTION VARCHAR(500),
	VOLUME      INT,
	PRIMARY KEY (ID)
);
