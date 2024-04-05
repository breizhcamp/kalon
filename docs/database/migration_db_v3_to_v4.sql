-- BEGIN CHANGELOG_2.0 --
-- BEGIN PHASE_0 --

---- #2.0.0 ----
CREATE TABLE participation (
	member_id 	
	uuid not null 
	constraint fk_member_part 
	references member(id),
	
	team_id 	
	uuid not null 
	constraint fk_team_part
	references team(id),

	event_id
	numeric not null
	constraint fk_event_part
	references event(id),

	primary_key(member_id, team_id, event_id)
);

-- END PHASE_0 --

-- BEGIN PHASE_1 --

---- Runned only if team table has >0 rows ----
---- #2.0.1.1 ----
INSERT INTO participates (member_id, event_id, team_id)
SELECT me.member_id, me.event_id, (
	SELECT id FROM team LIMIT 1
) FROM member_event AS me;

---- Runned only if event table has >0 rows ----
---- #2.0.1.2 ----
INSERT INTO participates (member_id, team_id, event_id)
SELECT mt.member_id, mt.team_id, (
	SELECT id FROM event
	ORDER BY year DESC LIMIT 1
) FROM member_team AS mt;

-- END PHASE_1 --

-- BEGIN PHASE_2 --

---- Runned only if 2.0.1.1 has been executed ----
---- #2.0.2.1 ----
DROP TABLE member_event;

---- Runned only if 2.0.1.2 has been executed ----
---- #2.0.2.2 ----
DROP TABLE member_team;

-- END PHASE_2 --
-- END CHANGELOG_2.0 --

