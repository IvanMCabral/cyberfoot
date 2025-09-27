CREATE TABLE club (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    overall INT NOT NULL
);

CREATE TABLE fixture (
    id UUID PRIMARY KEY,
    home_club_id UUID NOT NULL,
    away_club_id UUID NOT NULL,
    scheduled_at TIMESTAMP,
    status VARCHAR(10),
    goals_home INT,
    goals_away INT
);
