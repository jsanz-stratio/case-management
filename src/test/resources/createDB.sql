-- object: applications | type: SCHEMA --
DROP SCHEMA IF EXISTS applications CASCADE;

GRANT ALL PRIVILEGES ON DATABASE postgres TO docker;

CREATE SCHEMA applications;
-- ddl-end --
ALTER SCHEMA applications OWNER TO postgres;
-- ddl-end --

SET search_path TO pg_catalog,public,applications;
-- ddl-end --

-- object: applications.entity | type: TABLE --
DROP TABLE IF EXISTS applications.entity CASCADE;
CREATE TABLE applications.entity(
	id varchar(50) NOT NULL,
	description varchar(50) NOT NULL,
	CONSTRAINT entity_pk PRIMARY KEY (id),
	CONSTRAINT "uniqueId" UNIQUE (id)

);
-- ddl-end --
ALTER TABLE applications.entity OWNER TO postgres;
-- ddl-end --

-- object: applications.status | type: TABLE --
DROP TABLE IF EXISTS applications.status CASCADE;
CREATE TABLE applications.status(
	id char(4) NOT NULL,
	description varchar(50),
	CONSTRAINT status_pk PRIMARY KEY (id),
	CONSTRAINT uniqueid UNIQUE (id)

);
-- ddl-end --
ALTER TABLE applications.status OWNER TO postgres;
-- ddl-end --

-- object: applications.case_application | type: TABLE --
DROP TABLE IF EXISTS applications.case_application CASCADE;
CREATE TABLE applications.case_application(
	case_id bigint NOT NULL,
	appseq integer NOT NULL,
	process_id varchar(50) NOT NULL,
	status char(4),
	lockedby varchar(50),
	creation_date timestamp NOT NULL DEFAULT current_timestamp,
	creation_user varchar(50) NOT NULL,
	modification_date timestamp NOT NULL DEFAULT current_timestamp,
	modification_user varchar(50) NOT NULL,
	CONSTRAINT case_application_pk PRIMARY KEY (case_id,appseq)

);
-- ddl-end --
ALTER TABLE applications.case_application OWNER TO postgres;
-- ddl-end --

-- object: applications.case_raw_data | type: TABLE --
DROP TABLE IF EXISTS applications.case_raw_data CASCADE;
CREATE TABLE applications.case_raw_data(
	case_id bigint NOT NULL,
	raw json NOT NULL,
	CONSTRAINT case_rawdata_pk PRIMARY KEY (case_id)

);
-- ddl-end --
ALTER TABLE applications.case_raw_data OWNER TO postgres;
-- ddl-end --

-- object: applications.case_raw_attachment | type: TABLE --
DROP TABLE IF EXISTS applications.case_raw_attachment CASCADE;
CREATE TABLE applications.case_raw_attachment(
	case_id bigint NOT NULL,
	seqid smallint NOT NULL,
	data text NOT NULL,
	metadata json NOT NULL,
	CONSTRAINT case_attachment_pk PRIMARY KEY (case_id,seqid)

);
-- ddl-end --
ALTER TABLE applications.case_raw_attachment OWNER TO postgres;
-- ddl-end --

-- object: applications.case_participant | type: TABLE --
DROP TABLE IF EXISTS applications.case_participant CASCADE;
CREATE TABLE applications.case_participant(
	case_id bigint NOT NULL,
	participants_data jsonb NOT NULL,
	CONSTRAINT case_participant_pk PRIMARY KEY (case_id)

);
-- ddl-end --
ALTER TABLE applications.case_participant OWNER TO postgres;
-- ddl-end --

-- object: public.case_id | type: SEQUENCE --
DROP SEQUENCE IF EXISTS public.case_id CASCADE;
CREATE SEQUENCE public.case_id
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --
ALTER SEQUENCE public.case_id OWNER TO postgres;
-- ddl-end --

-- object: public.seq_case_id | type: SEQUENCE --
DROP SEQUENCE IF EXISTS public.seq_case_id CASCADE;
CREATE SEQUENCE public.seq_case_id
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --
ALTER SEQUENCE public.seq_case_id OWNER TO postgres;
-- ddl-end --

-- object: applications.activity | type: TABLE --
DROP TABLE IF EXISTS applications.activity CASCADE;
CREATE TABLE applications.activity(
	id varchar(50) NOT NULL,
	description varchar(50),
	CONSTRAINT activity_pk PRIMARY KEY (id)

);
-- ddl-end --
ALTER TABLE applications.activity OWNER TO postgres;
-- ddl-end --

-- object: applications.activity_status_restriction | type: TABLE --
DROP TABLE IF EXISTS applications.activity_status_restriction CASCADE;
CREATE TABLE applications.activity_status_restriction(
	status_id char(4) NOT NULL,
	activity_id char(4) NOT NULL,
	CONSTRAINT activity_status_restriction_pk PRIMARY KEY (status_id,activity_id)

);
-- ddl-end --
COMMENT ON TABLE applications.activity_status_restriction IS 'shows in what status an activity can be executed';
-- ddl-end --
ALTER TABLE applications.activity_status_restriction OWNER TO postgres;
-- ddl-end --

-- object: pkindex | type: INDEX --
DROP INDEX IF EXISTS applications.pkindex CASCADE;
CREATE INDEX pkindex ON applications.entity
	USING btree
	(
	  id
	);
-- ddl-end --

-- object: pkappcase | type: INDEX --
DROP INDEX IF EXISTS applications.pkappcase CASCADE;
CREATE INDEX pkappcase ON applications.case_application
	USING btree
	(
	  case_id
	);
-- ddl-end --

-- object: participants_data_jsonb | type: INDEX --
DROP INDEX IF EXISTS applications.participants_data_jsonb CASCADE;
CREATE INDEX participants_data_jsonb ON applications.case_participant
	USING gin
	(
	  participants_data
	);
-- ddl-end --

-- object: pkraw | type: INDEX --
DROP INDEX IF EXISTS applications.pkraw CASCADE;
CREATE INDEX pkraw ON applications.case_raw_data
	USING btree
	(
	  case_id
	);
-- ddl-end --

-- object: pkrawattachment | type: INDEX --
DROP INDEX IF EXISTS applications.pkrawattachment CASCADE;
CREATE INDEX pkrawattachment ON applications.case_raw_attachment
	USING btree
	(
	  case_id,
	  seqid ASC NULLS LAST
	);
-- ddl-end --

-- object: idx_main_act_st_restriction | type: INDEX --
DROP INDEX IF EXISTS applications.idx_main_act_st_restriction CASCADE;
CREATE INDEX idx_main_act_st_restriction ON applications.activity_status_restriction
	USING btree
	(
	  status_id,
	  activity_id ASC NULLS LAST
	);
-- ddl-end --

-- object: idx_main_status | type: INDEX --
DROP INDEX IF EXISTS applications.idx_main_status CASCADE;
CREATE INDEX idx_main_status ON applications.status
	USING btree
	(
	  id
	);
-- ddl-end --

-- object: idx_main_act | type: INDEX --
DROP INDEX IF EXISTS applications.idx_main_act CASCADE;
CREATE INDEX idx_main_act ON applications.activity
	USING btree
	(
	  id
	);
-- ddl-end --

-- object: applications.case_request | type: TABLE --
DROP TABLE IF EXISTS applications.case_request CASCADE;
CREATE TABLE applications.case_request(
	id bigserial NOT NULL,
	entity varchar(50) NOT NULL,
	creation_date timestamp NOT NULL DEFAULT current_timestamp,
	creation_user varchar(50) NOT NULL,
	modification_date timestamp NOT NULL DEFAULT current_timestamp,
	modification_user varchar(50) NOT NULL,
	CONSTRAINT case_pk PRIMARY KEY (id)

);
-- ddl-end --
ALTER TABLE applications.case_request OWNER TO postgres;
-- ddl-end --

-- object: pkindexcase | type: INDEX --
DROP INDEX IF EXISTS applications.pkindexcase CASCADE;
CREATE INDEX pkindexcase ON applications.case_request
	USING btree
	(
	  id
	);
-- ddl-end --

-- object: has_entity | type: CONSTRAINT --
-- ALTER TABLE applications.case_request DROP CONSTRAINT IF EXISTS has_entity CASCADE;
ALTER TABLE applications.case_request ADD CONSTRAINT has_entity FOREIGN KEY (entity)
REFERENCES applications.entity (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: rel_application_case_id | type: CONSTRAINT --
-- ALTER TABLE applications.case_application DROP CONSTRAINT IF EXISTS rel_application_case_id CASCADE;
ALTER TABLE applications.case_application ADD CONSTRAINT rel_application_case_id FOREIGN KEY (case_id)
REFERENCES applications.case_request (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: rel_application_status | type: CONSTRAINT --
-- ALTER TABLE applications.case_application DROP CONSTRAINT IF EXISTS rel_application_status CASCADE;
ALTER TABLE applications.case_application ADD CONSTRAINT rel_application_status FOREIGN KEY (status)
REFERENCES applications.status (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: rawdata_case | type: CONSTRAINT --
-- ALTER TABLE applications.case_raw_data DROP CONSTRAINT IF EXISTS rawdata_case CASCADE;
ALTER TABLE applications.case_raw_data ADD CONSTRAINT rawdata_case FOREIGN KEY (case_id)
REFERENCES applications.case_request (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: rel_raw_attachment_case | type: CONSTRAINT --
-- ALTER TABLE applications.case_raw_attachment DROP CONSTRAINT IF EXISTS rel_raw_attachment_case CASCADE;
ALTER TABLE applications.case_raw_attachment ADD CONSTRAINT rel_raw_attachment_case FOREIGN KEY (case_id)
REFERENCES applications.case_request (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: rel_participant_case_id | type: CONSTRAINT --
-- ALTER TABLE applications.case_participant DROP CONSTRAINT IF EXISTS rel_participant_case_id CASCADE;
ALTER TABLE applications.case_participant ADD CONSTRAINT rel_participant_case_id FOREIGN KEY (case_id)
REFERENCES applications.case_request (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: restriction_status | type: CONSTRAINT --
-- ALTER TABLE applications.activity_status_restriction DROP CONSTRAINT IF EXISTS restriction_status CASCADE;
ALTER TABLE applications.activity_status_restriction ADD CONSTRAINT restriction_status FOREIGN KEY (status_id)
REFERENCES applications.status (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: restriction_activity | type: CONSTRAINT --
-- ALTER TABLE applications.activity_status_restriction DROP CONSTRAINT IF EXISTS restriction_activity CASCADE;
ALTER TABLE applications.activity_status_restriction ADD CONSTRAINT restriction_activity FOREIGN KEY (activity_id)
REFERENCES applications.activity (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --