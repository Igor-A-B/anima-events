-- drops the events table so hibernate (ddl-auto=update) recreates it with every column on the next start
-- update only adds columns, it never changes or drops them, so a schema change like this one needs a drop
-- subscriptions reference events, a dangling row would block the new foreign key, so they go too
-- run with the database container up, then restart the server:
--   docker exec -i anima-database sh -c 'psql -U "$POSTGRES_USER" -d "$POSTGRES_DB"' < server/db/recreate-events.sql
BEGIN;
DROP TABLE IF EXISTS subscriptions;
DROP TABLE IF EXISTS events;
COMMIT;
