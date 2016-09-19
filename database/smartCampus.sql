CREATE TABLE beacon_category (
  beacon_id INTEGER AUTO_INCREMENT NOT NULL,
  category  VARCHAR(255),
  CONSTRAINT pk_beacon_category PRIMARY KEY (beacon_id)
);

CREATE TABLE beacon_location (
  beacon_id INTEGER AUTO_INCREMENT NOT NULL,
  location  VARCHAR(255),
  CONSTRAINT pk_beacon_location PRIMARY KEY (beacon_id)
);

CREATE TABLE category (
  id          INTEGER AUTO_INCREMENT NOT NULL,
  name        VARCHAR(255),
  description VARCHAR(255),
  CONSTRAINT pk_category PRIMARY KEY (id)
);

CREATE TABLE event (
  id            INTEGER AUTO_INCREMENT NOT NULL,
  name          VARCHAR(255),
  location      VARCHAR(255),
  start_time    DATE,
  end_time      DATE,
  description   VARCHAR(255),
  category      VARCHAR(255),
  external_link VARCHAR(255),
  is_active     TINYINT(1) DEFAULT 0,
  CONSTRAINT pk_event PRIMARY KEY (id)
);

CREATE TABLE location (
  id          INTEGER AUTO_INCREMENT NOT NULL,
  name        VARCHAR(255),
  description VARCHAR(255),
  CONSTRAINT pk_location PRIMARY KEY (id)
);

CREATE TABLE user (
  id         INTEGER AUTO_INCREMENT NOT NULL,
  user_name  VARCHAR(255),
  role       VARCHAR(255),
  categories VARCHAR(255),
  CONSTRAINT pk_user PRIMARY KEY (id)
);