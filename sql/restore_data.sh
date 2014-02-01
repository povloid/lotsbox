dropdb -e -h localhost -U lotsbox -p 5433 lotsbox
createdb -h localhost -U lotsbox -p 5433 -e -E UTF8 -T template0 lotsbox
psql -h localhost -U lotsbox -p 5433 -f lotsbox.sql lotsbox