// Script para mongosh: crear fixture entre el mejor y el peor equipo
use('cyberfoot');

const mejor = db.clubs.find().sort({overall: -1}).limit(1).toArray()[0];
const peor = db.clubs.find().sort({overall: 1}).limit(1).toArray()[0];

print('Mejor equipo:', mejor.name, 'ID:', mejor._id);
print('Peor equipo:', peor.name, 'ID:', peor._id);

const fixture = {
  home_club_id: mejor._id,
  away_club_id: peor._id,
  scheduled_at: new Date(),
  status: 'SCHEDULED',
  goals_home: 0,
  goals_away: 0
};

db.fixtures.insertOne(fixture);
print('Fixture creado:', fixture);
