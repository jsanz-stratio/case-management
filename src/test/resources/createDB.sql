-- object: applications | type: SCHEMA --
DROP SCHEMA IF EXISTS applications CASCADE;
CREATE SCHEMA applications;
-- ddl-end --
ALTER SCHEMA applications OWNER TO postgres;
-- ddl-end --

-- object: baseents | type: SCHEMA --
DROP SCHEMA IF EXISTS baseents CASCADE;
CREATE SCHEMA baseents;
-- ddl-end --
ALTER SCHEMA baseents OWNER TO postgres;
-- ddl-end --

SET search_path TO pg_catalog,public,applications,baseents;
-- ddl-end --

-- object: applications.case_application | type: TABLE --
DROP TABLE IF EXISTS applications.case_application CASCADE;
CREATE TABLE applications.case_application(
	case_id bigint NOT NULL,
	appseq integer NOT NULL,
	process_id varchar(50) NOT NULL,
	status char(4),
	lockedby varchar(50),
	creation_date timestamptz NOT NULL DEFAULT current_timestamp,
	creation_user varchar(50) NOT NULL,
	modification_date timestamptz NOT NULL DEFAULT current_timestamp,
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
	seqid bigserial NOT NULL,
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

-- object: public.caseid | type: SEQUENCE --
DROP SEQUENCE IF EXISTS public.caseid CASCADE;
CREATE SEQUENCE public.caseid
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --
ALTER SEQUENCE public.caseid OWNER TO postgres;
-- ddl-end --

-- object: public.seq_caseid | type: SEQUENCE --
DROP SEQUENCE IF EXISTS public.seq_caseid CASCADE;
CREATE SEQUENCE public.seq_caseid
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 1
	CACHE 1
	NO CYCLE
	OWNED BY NONE;
-- ddl-end --
ALTER SEQUENCE public.seq_caseid OWNER TO postgres;
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

-- object: applications.case_request | type: TABLE --
DROP TABLE IF EXISTS applications.case_request CASCADE;
CREATE TABLE applications.case_request(
	id bigserial NOT NULL,
	entity varchar(50) NOT NULL,
	creation_date timestamptz NOT NULL DEFAULT current_timestamp,
	creation_user varchar(50) NOT NULL,
	modification_date timestamptz NOT NULL DEFAULT current_timestamp,
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

-- object: applications.application_activity_context | type: TABLE --
DROP TABLE IF EXISTS applications.application_activity_context CASCADE;
CREATE TABLE applications.application_activity_context(
	id bigserial NOT NULL,
	case_id bigint NOT NULL,
	appseq integer NOT NULL,
	activity_id char(4) NOT NULL,
	channel smallint NOT NULL,
	context_type smallint,
	context_value json,
	upsertion_date timestamptz DEFAULT current_timestamp,
	CONSTRAINT application_activity_context_pk PRIMARY KEY (id)

);
-- ddl-end --
ALTER TABLE applications.application_activity_context OWNER TO postgres;
-- ddl-end --

-- object: cluster_idx | type: INDEX --
DROP INDEX IF EXISTS applications.cluster_idx CASCADE;
CREATE INDEX cluster_idx ON applications.application_activity_context
	USING btree
	(
	  id,
	  appseq ASC NULLS LAST,
	  activity_id ASC NULLS LAST,
	  context_type ASC NULLS LAST,
	  channel ASC NULLS LAST
	);
-- ddl-end --

-- object: baseents.activity_status_restriction | type: TABLE --
DROP TABLE IF EXISTS baseents.activity_status_restriction CASCADE;
CREATE TABLE baseents.activity_status_restriction(
	activity_id char(4) NOT NULL,
	status_id char(4) NOT NULL,
	process_id varchar(50) NOT NULL,
	CONSTRAINT activity_status_restriction_pk PRIMARY KEY (activity_id,status_id,process_id)

);
-- ddl-end --
COMMENT ON TABLE baseents.activity_status_restriction IS 'shows in what status an activity can be executed';
-- ddl-end --
ALTER TABLE baseents.activity_status_restriction OWNER TO postgres;
-- ddl-end --

-- object: idx_main_act_st_restriction | type: INDEX --
DROP INDEX IF EXISTS baseents.idx_main_act_st_restriction CASCADE;
CREATE INDEX idx_main_act_st_restriction ON baseents.activity_status_restriction
	USING btree
	(
	  status_id,
	  activity_id ASC NULLS LAST,
	  process_id ASC NULLS LAST
	);
-- ddl-end --

-- object: baseents.process | type: TABLE --
DROP TABLE IF EXISTS baseents.process CASCADE;
CREATE TABLE baseents.process(
	id varchar(50) NOT NULL,
	description varchar(50),
	CONSTRAINT process_pk PRIMARY KEY (id)

);
-- ddl-end --
ALTER TABLE baseents.process OWNER TO postgres;
-- ddl-end --

-- object: baseents.entity | type: TABLE --
DROP TABLE IF EXISTS baseents.entity CASCADE;
CREATE TABLE baseents.entity(
	id varchar(50) NOT NULL,
	description varchar(50) NOT NULL,
	CONSTRAINT entity_pk PRIMARY KEY (id),
	CONSTRAINT "uniqueId" UNIQUE (id)

);
-- ddl-end --
ALTER TABLE baseents.entity OWNER TO postgres;
-- ddl-end --

-- object: baseents.statustostatus | type: TABLE --
DROP TABLE IF EXISTS baseents.statustostatus CASCADE;
CREATE TABLE baseents.statustostatus(
	id serial NOT NULL,
	status_id char(4),
	event_id integer,
	process_id varchar(50),
	newstatus_id char(4),
	CONSTRAINT statustostatus_pk PRIMARY KEY (id)

);
-- ddl-end --
ALTER TABLE baseents.statustostatus OWNER TO postgres;
-- ddl-end --

-- object: baseents.event_type | type: TABLE --
DROP TABLE IF EXISTS baseents.event_type CASCADE;
CREATE TABLE baseents.event_type(
	id smallint NOT NULL,
	type_description smallint,
	CONSTRAINT event_type_pk PRIMARY KEY (id)

);
-- ddl-end --
ALTER TABLE baseents.event_type OWNER TO postgres;
-- ddl-end --

-- object: baseents.event | type: TABLE --
DROP TABLE IF EXISTS baseents.event CASCADE;
CREATE TABLE baseents.event(
	id smallint NOT NULL,
	event_type smallint,
	description varchar(50),
	CONSTRAINT event_pk PRIMARY KEY (id)

);
-- ddl-end --
ALTER TABLE baseents.event OWNER TO postgres;
-- ddl-end --

-- object: baseents.status | type: TABLE --
DROP TABLE IF EXISTS baseents.status CASCADE;
CREATE TABLE baseents.status(
	id char(4) NOT NULL,
	description varchar(50),
	CONSTRAINT status_pk PRIMARY KEY (id),
	CONSTRAINT uniqueid UNIQUE (id)

);
-- ddl-end --
ALTER TABLE baseents.status OWNER TO postgres;
-- ddl-end --

-- object: baseents.channel | type: TABLE --
DROP TABLE IF EXISTS baseents.channel CASCADE;
CREATE TABLE baseents.channel(
	id smallint NOT NULL,
	description varchar(50),
	CONSTRAINT channel_pk PRIMARY KEY (id)

);
-- ddl-end --
ALTER TABLE baseents.channel OWNER TO postgres;
-- ddl-end --

-- object: baseents.activity | type: TABLE --
DROP TABLE IF EXISTS baseents.activity CASCADE;
CREATE TABLE baseents.activity(
	id varchar(50) NOT NULL,
	description varchar(50),
	CONSTRAINT activity_pk PRIMARY KEY (id)

);
-- ddl-end --
ALTER TABLE baseents.activity OWNER TO postgres;
-- ddl-end --

-- object: idx_main_status | type: INDEX --
DROP INDEX IF EXISTS baseents.idx_main_status CASCADE;
CREATE INDEX idx_main_status ON baseents.status
	USING btree
	(
	  id
	);
-- ddl-end --

-- object: idx_main_act | type: INDEX --
DROP INDEX IF EXISTS baseents.idx_main_act CASCADE;
CREATE INDEX idx_main_act ON baseents.activity
	USING btree
	(
	  id
	);
-- ddl-end --

-- object: pkindex | type: INDEX --
DROP INDEX IF EXISTS baseents.pkindex CASCADE;
CREATE INDEX pkindex ON baseents.entity
	USING btree
	(
	  id
	);
-- ddl-end --

-- object: baseents.group_activity_restriction | type: TABLE --
DROP TABLE IF EXISTS baseents.group_activity_restriction CASCADE;
CREATE TABLE baseents.group_activity_restriction(
	activity_id char(4) NOT NULL,
	process_id varchar(50) NOT NULL,
	group_id varchar(50) NOT NULL,
	CONSTRAINT group_status_restriction_pk PRIMARY KEY (activity_id,process_id,group_id)

);
-- ddl-end --
ALTER TABLE baseents.group_activity_restriction OWNER TO postgres;
-- ddl-end --

-- object: baseents.status_activity | type: TABLE --
DROP TABLE IF EXISTS baseents.status_activity CASCADE;
CREATE TABLE baseents.status_activity(
	status_id char(4) NOT NULL,
	activity_id varchar(50) NOT NULL,
	process_id varchar(50) NOT NULL,
	CONSTRAINT status_activity_pk PRIMARY KEY (status_id,activity_id,process_id)

);
-- ddl-end --
ALTER TABLE baseents.status_activity OWNER TO postgres;
-- ddl-end --

-- object: cluster_idx2_1 | type: INDEX --
DROP INDEX IF EXISTS baseents.cluster_idx2_1 CASCADE;
CREATE INDEX cluster_idx2_1 ON baseents.statustostatus
	USING btree
	(
	  status_id,
	  event_id ASC NULLS LAST,
	  process_id ASC NULLS LAST
	);
-- ddl-end --

-- object: cluster_idx | type: INDEX --
DROP INDEX IF EXISTS baseents.cluster_idx CASCADE;
CREATE INDEX cluster_idx ON baseents.status_activity
	USING btree
	(
	  status_id ASC NULLS LAST,
	  activity_id,
	  process_id ASC NULLS LAST
	);
-- ddl-end --

-- object: cluster_idx2 | type: INDEX --
DROP INDEX IF EXISTS baseents.cluster_idx2 CASCADE;
CREATE INDEX cluster_idx2 ON baseents.group_activity_restriction
	USING btree
	(
	  activity_id,
	  process_id ASC NULLS LAST,
	  group_id ASC NULLS LAST
	);
-- ddl-end --

-- object: rel_application_caseid | type: CONSTRAINT --
-- ALTER TABLE applications.case_application DROP CONSTRAINT IF EXISTS rel_application_caseid CASCADE;
ALTER TABLE applications.case_application ADD CONSTRAINT rel_application_caseid FOREIGN KEY (case_id)
REFERENCES applications.case_request (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: rel_pro | type: CONSTRAINT --
-- ALTER TABLE applications.case_application DROP CONSTRAINT IF EXISTS rel_pro CASCADE;
ALTER TABLE applications.case_application ADD CONSTRAINT rel_pro FOREIGN KEY (process_id)
REFERENCES baseents.process (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: rel_application_status | type: CONSTRAINT --
-- ALTER TABLE applications.case_application DROP CONSTRAINT IF EXISTS rel_application_status CASCADE;
ALTER TABLE applications.case_application ADD CONSTRAINT rel_application_status FOREIGN KEY (status)
REFERENCES baseents.status (id) MATCH FULL
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

-- object: rel_participant_caseid | type: CONSTRAINT --
-- ALTER TABLE applications.case_participant DROP CONSTRAINT IF EXISTS rel_participant_caseid CASCADE;
ALTER TABLE applications.case_participant ADD CONSTRAINT rel_participant_caseid FOREIGN KEY (case_id)
REFERENCES applications.case_request (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: has_entity | type: CONSTRAINT --
-- ALTER TABLE applications.case_request DROP CONSTRAINT IF EXISTS has_entity CASCADE;
ALTER TABLE applications.case_request ADD CONSTRAINT has_entity FOREIGN KEY (entity)
REFERENCES baseents.entity (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: rel_app | type: CONSTRAINT --
-- ALTER TABLE applications.application_activity_context DROP CONSTRAINT IF EXISTS rel_app CASCADE;
ALTER TABLE applications.application_activity_context ADD CONSTRAINT rel_app FOREIGN KEY (case_id,appseq)
REFERENCES applications.case_application (case_id,appseq) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: rel_act | type: CONSTRAINT --
-- ALTER TABLE applications.application_activity_context DROP CONSTRAINT IF EXISTS rel_act CASCADE;
ALTER TABLE applications.application_activity_context ADD CONSTRAINT rel_act FOREIGN KEY (activity_id)
REFERENCES baseents.activity (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: rel_channel | type: CONSTRAINT --
-- ALTER TABLE applications.application_activity_context DROP CONSTRAINT IF EXISTS rel_channel CASCADE;
ALTER TABLE applications.application_activity_context ADD CONSTRAINT rel_channel FOREIGN KEY (channel)
REFERENCES baseents.channel (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: restriction_status | type: CONSTRAINT --
-- ALTER TABLE baseents.activity_status_restriction DROP CONSTRAINT IF EXISTS restriction_status CASCADE;
ALTER TABLE baseents.activity_status_restriction ADD CONSTRAINT restriction_status FOREIGN KEY (status_id)
REFERENCES baseents.status (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: restriction_activity | type: CONSTRAINT --
-- ALTER TABLE baseents.activity_status_restriction DROP CONSTRAINT IF EXISTS restriction_activity CASCADE;
ALTER TABLE baseents.activity_status_restriction ADD CONSTRAINT restriction_activity FOREIGN KEY (activity_id)
REFERENCES baseents.activity (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: rel_process | type: CONSTRAINT --
-- ALTER TABLE baseents.activity_status_restriction DROP CONSTRAINT IF EXISTS rel_process CASCADE;
ALTER TABLE baseents.activity_status_restriction ADD CONSTRAINT rel_process FOREIGN KEY (process_id)
REFERENCES baseents.process (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: rel_status1 | type: CONSTRAINT --
-- ALTER TABLE baseents.statustostatus DROP CONSTRAINT IF EXISTS rel_status1 CASCADE;
ALTER TABLE baseents.statustostatus ADD CONSTRAINT rel_status1 FOREIGN KEY (status_id)
REFERENCES baseents.status (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: rel_status2 | type: CONSTRAINT --
-- ALTER TABLE baseents.statustostatus DROP CONSTRAINT IF EXISTS rel_status2 CASCADE;
ALTER TABLE baseents.statustostatus ADD CONSTRAINT rel_status2 FOREIGN KEY (newstatus_id)
REFERENCES baseents.status (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: rel_event2 | type: CONSTRAINT --
-- ALTER TABLE baseents.statustostatus DROP CONSTRAINT IF EXISTS rel_event2 CASCADE;
ALTER TABLE baseents.statustostatus ADD CONSTRAINT rel_event2 FOREIGN KEY (event_id)
REFERENCES baseents.event (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: rel_process2 | type: CONSTRAINT --
-- ALTER TABLE baseents.statustostatus DROP CONSTRAINT IF EXISTS rel_process2 CASCADE;
ALTER TABLE baseents.statustostatus ADD CONSTRAINT rel_process2 FOREIGN KEY (process_id)
REFERENCES baseents.process (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: rel_event | type: CONSTRAINT --
-- ALTER TABLE baseents.event DROP CONSTRAINT IF EXISTS rel_event CASCADE;
ALTER TABLE baseents.event ADD CONSTRAINT rel_event FOREIGN KEY (event_type)
REFERENCES baseents.event_type (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: rel_act3 | type: CONSTRAINT --
-- ALTER TABLE baseents.group_activity_restriction DROP CONSTRAINT IF EXISTS rel_act3 CASCADE;
ALTER TABLE baseents.group_activity_restriction ADD CONSTRAINT rel_act3 FOREIGN KEY (activity_id)
REFERENCES baseents.activity (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: rel_proceso3 | type: CONSTRAINT --
-- ALTER TABLE baseents.group_activity_restriction DROP CONSTRAINT IF EXISTS rel_proceso3 CASCADE;
ALTER TABLE baseents.group_activity_restriction ADD CONSTRAINT rel_proceso3 FOREIGN KEY (process_id)
REFERENCES baseents.process (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: rel_proc_act | type: CONSTRAINT --
-- ALTER TABLE baseents.status_activity DROP CONSTRAINT IF EXISTS rel_proc_act CASCADE;
ALTER TABLE baseents.status_activity ADD CONSTRAINT rel_proc_act FOREIGN KEY (process_id)
REFERENCES baseents.process (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: rel_stat_act | type: CONSTRAINT --
-- ALTER TABLE baseents.status_activity DROP CONSTRAINT IF EXISTS rel_stat_act CASCADE;
ALTER TABLE baseents.status_activity ADD CONSTRAINT rel_stat_act FOREIGN KEY (status_id)
REFERENCES baseents.activity (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --

-- object: rel_act_status | type: CONSTRAINT --
-- ALTER TABLE baseents.status_activity DROP CONSTRAINT IF EXISTS rel_act_status CASCADE;
ALTER TABLE baseents.status_activity ADD CONSTRAINT rel_act_status FOREIGN KEY (status_id)
REFERENCES baseents.status (id) MATCH FULL
ON DELETE NO ACTION ON UPDATE NO ACTION;
-- ddl-end --
