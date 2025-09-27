INSERT INTO club (id, name, overall) VALUES
    ('11111111-1111-1111-1111-111111111111', 'Boca Juniors', 85),
    ('22222222-2222-2222-2222-222222222222', 'River Plate', 86);

INSERT INTO fixture (id, home_club_id, away_club_id, scheduled_at, status, goals_home, goals_away) VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', '22222222-2222-2222-2222-222222222222', CURRENT_TIMESTAMP, 'SCHEDULED', 0, 0);
