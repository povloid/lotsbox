--
-- PostgreSQL database dump
--

-- Dumped from database version 9.3.2
-- Dumped by pg_dump version 9.3.2
-- Started on 2014-02-01 21:52:21 ALMT

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 172 (class 3079 OID 11791)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 1982 (class 0 OID 0)
-- Dependencies: 172
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 171 (class 1259 OID 18836)
-- Name: lots; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE lots (
    id integer NOT NULL,
    stat character varying(10),
    keyname character varying(512),
    caption text,
    description text,
    sum_all numeric(19,2),
    place character varying(512),
    bdate date,
    edate date,
    url text,
    res character varying(20),
    updated timestamp without time zone
);


--
-- TOC entry 170 (class 1259 OID 18834)
-- Name: lots_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE lots_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 1983 (class 0 OID 0)
-- Dependencies: 170
-- Name: lots_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE lots_id_seq OWNED BY lots.id;


--
-- TOC entry 1864 (class 2604 OID 18839)
-- Name: id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY lots ALTER COLUMN id SET DEFAULT nextval('lots_id_seq'::regclass);


--
-- TOC entry 1866 (class 2606 OID 18844)
-- Name: lots_pk; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY lots
    ADD CONSTRAINT lots_pk PRIMARY KEY (id);


--
-- TOC entry 1868 (class 2606 OID 18846)
-- Name: lots_uk1; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY lots
    ADD CONSTRAINT lots_uk1 UNIQUE (res, keyname);


-- Completed on 2014-02-01 21:52:21 ALMT

--
-- PostgreSQL database dump complete
--

