CREATE TABLE IF NOT EXISTS public.brand
(
    id character varying(255) COLLATE pg_catalog."default" NOT NULL,
    brand_registration_country character varying(255) COLLATE pg_catalog."default",
    name character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT brand_pkey PRIMARY KEY (id)
)

    TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.brand
    OWNER to postgres;

CREATE TABLE IF NOT EXISTS public.charge_type
(
    id character varying(255) COLLATE pg_catalog."default" NOT NULL,
    name character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT charge_type_pkey PRIMARY KEY (id)
)

    TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.charge_type
    OWNER to postgres;

CREATE TABLE IF NOT EXISTS public.communication_standard
(
    id character varying(255) COLLATE pg_catalog."default" NOT NULL,
    name character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT communication_standard_pkey PRIMARY KEY (id)
)

    TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.communication_standard
    OWNER to postgres;

CREATE TABLE IF NOT EXISTS public.operation_system
(
    id character varying(255) COLLATE pg_catalog."default" NOT NULL,
    name character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT operation_system_pkey PRIMARY KEY (id)
)

    TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.operation_system
    OWNER to postgres;


CREATE TABLE IF NOT EXISTS public.processor
(
    id character varying(255) COLLATE pg_catalog."default" NOT NULL,
    core_frequency real NOT NULL,
    name character varying(255) COLLATE pg_catalog."default",
    number_of_cores integer NOT NULL,
    CONSTRAINT processor_pkey PRIMARY KEY (id)
)

    TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.processor
    OWNER to postgres;

CREATE TABLE IF NOT EXISTS public.type_screen
(
    id character varying(255) COLLATE pg_catalog."default" NOT NULL,
    name character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT type_screen_pkey PRIMARY KEY (id)
)

    TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.type_screen
    OWNER to postgres;

CREATE TABLE IF NOT EXISTS public.rating
(
    id character varying(255) COLLATE pg_catalog."default" NOT NULL,
    number_of_points integer NOT NULL,
    total_points integer NOT NULL,
    CONSTRAINT rating_pkey PRIMARY KEY (id)
)

    TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.rating
    OWNER to postgres;

CREATE TABLE IF NOT EXISTS public.shopping_cart
(
    id character varying(255) COLLATE pg_catalog."default" NOT NULL,
    price double precision NOT NULL,
    CONSTRAINT shopping_cart_pkey PRIMARY KEY (id)
)

    TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.shopping_cart
    OWNER to postgres;

CREATE TABLE IF NOT EXISTS public.registered_user
(
    id character varying(255) COLLATE pg_catalog."default" NOT NULL,
    age integer NOT NULL,
    email_address character varying(255) COLLATE pg_catalog."default",
    first_name character varying(255) COLLATE pg_catalog."default",
    last_name character varying(255) COLLATE pg_catalog."default",
    middle_name character varying(255) COLLATE pg_catalog."default",
    password character varying(255) COLLATE pg_catalog."default",
    phone_number character varying(255) COLLATE pg_catalog."default",
    role character varying(255) COLLATE pg_catalog."default",
    shopping_cart_id character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT registered_user_pkey PRIMARY KEY (id),
    CONSTRAINT fkc011k7y10339e8on9wn39nd48 FOREIGN KEY (shopping_cart_id)
        REFERENCES public.shopping_cart (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

    TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.registered_user
    OWNER to postgres;

CREATE TABLE IF NOT EXISTS public.client_check
(
    id character varying(255) COLLATE pg_catalog."default" NOT NULL,
    created timestamp without time zone NOT NULL,
    is_closed boolean NOT NULL,
    total_price double precision NOT NULL,
    registered_user_id character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT client_check_pkey PRIMARY KEY (id),
    CONSTRAINT fk1is7ivvjp99ymq66d4o7e6krd FOREIGN KEY (registered_user_id)
        REFERENCES public.registered_user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

    TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.client_check
    OWNER to postgres;



CREATE TABLE IF NOT EXISTS public.phone
(
    id character varying(255) COLLATE pg_catalog."default" NOT NULL,
    amount_of_built_in_memory integer NOT NULL,
    amount_of_ram integer NOT NULL,
    color character varying(255) COLLATE pg_catalog."default",
    country_producer_of_the_product character varying(255) COLLATE pg_catalog."default",
    currency character varying(255) COLLATE pg_catalog."default",
    degree_of_moisture_protection character varying(255) COLLATE pg_catalog."default",
    diagonal real NOT NULL,
    display_resolution character varying(255) COLLATE pg_catalog."default",
    guarantee_time_months integer NOT NULL,
    height real NOT NULL,
    imei character varying(255) COLLATE pg_catalog."default",
    info_about_front_cameras character varying(255) COLLATE pg_catalog."default",
    info_about_main_cameras character varying(255) COLLATE pg_catalog."default",
    is_have_nfc boolean NOT NULL,
    left_side_and_right_side character varying(255) COLLATE pg_catalog."default",
    name character varying(255) COLLATE pg_catalog."default",
    number_of_front_cameras integer NOT NULL,
    number_of_main_cameras integer NOT NULL,
    number_of_sim_cards integer NOT NULL,
    phone_front_and_back character varying(255) COLLATE pg_catalog."default",
    price double precision NOT NULL,
    screen_refresh_rate integer NOT NULL,
    series character varying(255) COLLATE pg_catalog."default",
    up_side_and_down_side character varying(255) COLLATE pg_catalog."default",
    weight real NOT NULL,
    width real NOT NULL,
    brand_id character varying(255) COLLATE pg_catalog."default",
    charge_type_id character varying(255) COLLATE pg_catalog."default",
    check_id character varying(255) COLLATE pg_catalog."default",
    communication_standard_id character varying(255) COLLATE pg_catalog."default",
    operation_system_id character varying(255) COLLATE pg_catalog."default",
    processor_id character varying(255) COLLATE pg_catalog."default",
    rating_id character varying(255) COLLATE pg_catalog."default",
    shopping_cart_id character varying(255) COLLATE pg_catalog."default",
    type_screen_id character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT phone_pkey PRIMARY KEY (id),
    CONSTRAINT fk4am1tce6224cgaejtvqcdkte4 FOREIGN KEY (operation_system_id)
        REFERENCES public.operation_system (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkbhy7psqpx733tnprkykjgibdn FOREIGN KEY (brand_id)
        REFERENCES public.brand (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkikvj9stf1fbg9bksij2hotisd FOREIGN KEY (communication_standard_id)
        REFERENCES public.communication_standard (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkn0f1hcgap1e94cqagvdkjej9c FOREIGN KEY (type_screen_id)
        REFERENCES public.type_screen (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkocsxriagr47svlp0nc0t20y2n FOREIGN KEY (rating_id)
        REFERENCES public.rating (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkqblianmxonf9u02atmjlliocb FOREIGN KEY (charge_type_id)
        REFERENCES public.charge_type (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkrc9ak5js5csi0tbsl7o6sxyxy FOREIGN KEY (shopping_cart_id)
        REFERENCES public.shopping_cart (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkrrtng5sg7ujwkss5bdyiigldx FOREIGN KEY (processor_id)
        REFERENCES public.processor (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fktet42f63b4quugc78o70qjngc FOREIGN KEY (check_id)
        REFERENCES public.client_check (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

    TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.phone
    OWNER to postgres;

CREATE TABLE IF NOT EXISTS public.comment
(
    id character varying(255) COLLATE pg_catalog."default" NOT NULL,
    description character varying(5000) COLLATE pg_catalog."default",
    phone_id character varying(255) COLLATE pg_catalog."default",
    user_id character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT comment_pkey PRIMARY KEY (id),
    CONSTRAINT fk3urpm3dc7dwm1vvy6kk13w1t7 FOREIGN KEY (user_id)
        REFERENCES public.registered_user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkiap5cre0vk3fej45ymoe6dffy FOREIGN KEY (phone_id)
        REFERENCES public.phone (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

    TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.comment
    OWNER to postgres;

















