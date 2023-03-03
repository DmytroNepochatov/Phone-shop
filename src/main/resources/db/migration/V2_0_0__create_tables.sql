CREATE TABLE IF NOT EXISTS public.country
(
    id character varying(255) COLLATE pg_catalog."default" NOT NULL,
    name character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT country_pkey PRIMARY KEY (id),
    CONSTRAINT uk_llidyp77h6xkeokpbmoy710d4 UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS public.brand
(
    id character varying(255) COLLATE pg_catalog."default" NOT NULL,
    name character varying(255) COLLATE pg_catalog."default",
    country_id character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT brand_pkey PRIMARY KEY (id),
    CONSTRAINT uk_rdxh7tq2xs66r485cc8dkxt77 UNIQUE (name),
    CONSTRAINT fk8iehti3u9vucvh3qnd6s143pf FOREIGN KEY (country_id)
        REFERENCES public.country (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS public.charge_type
(
    id character varying(255) COLLATE pg_catalog."default" NOT NULL,
    name character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT charge_type_pkey PRIMARY KEY (id),
    CONSTRAINT uk_j57hkswmd9gfnlqh3ybtm6syw UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS public.communication_standard
(
    id character varying(255) COLLATE pg_catalog."default" NOT NULL,
    name character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT communication_standard_pkey PRIMARY KEY (id),
    CONSTRAINT uk_g0bj8bg2m6d7gs383msr4ljgl UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS public.operation_system
(
    id character varying(255) COLLATE pg_catalog."default" NOT NULL,
    name character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT operation_system_pkey PRIMARY KEY (id),
    CONSTRAINT uk_bx1a1ipeotclkly1g8d7ekt1h UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS public.processor
(
    id character varying(255) COLLATE pg_catalog."default" NOT NULL,
    core_frequency real NOT NULL,
    name character varying(255) COLLATE pg_catalog."default",
    number_of_cores integer NOT NULL,
    CONSTRAINT processor_pkey PRIMARY KEY (id),
    CONSTRAINT uk_fvbm2sf7gln5m3h04dl8hacnq UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS public.type_screen
(
    id character varying(255) COLLATE pg_catalog."default" NOT NULL,
    name character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT type_screen_pkey PRIMARY KEY (id),
    CONSTRAINT uk_t64nx8qvvroo2hc7rpqq1b6au UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS public.rating
(
    id character varying(255) COLLATE pg_catalog."default" NOT NULL,
    number_of_points integer NOT NULL,
    total_points integer NOT NULL,
    CONSTRAINT rating_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.registered_user
(
    id character varying(255) COLLATE pg_catalog."default" NOT NULL,
    date_of_birth timestamp without time zone NOT NULL,
    email_address character varying(255) COLLATE pg_catalog."default",
    first_name character varying(255) COLLATE pg_catalog."default",
    last_name character varying(255) COLLATE pg_catalog."default",
    middle_name character varying(255) COLLATE pg_catalog."default",
    password character varying(255) COLLATE pg_catalog."default",
    phone_number character varying(255) COLLATE pg_catalog."default",
    role character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT registered_user_pkey PRIMARY KEY (id),
    CONSTRAINT uk_f6onv3wuwewrd39x86xydfglw UNIQUE (email_address)
);

CREATE TABLE IF NOT EXISTS public.shopping_cart
(
    id character varying(255) COLLATE pg_catalog."default" NOT NULL,
    registered_user_id character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT shopping_cart_pkey PRIMARY KEY (id),
    CONSTRAINT fk3su8t8ko5anokydt1sgmnqcem FOREIGN KEY (registered_user_id)
        REFERENCES public.registered_user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS public.client_check
(
    id character varying(255) COLLATE pg_catalog."default" NOT NULL,
    closed_date timestamp without time zone,
    created timestamp without time zone NOT NULL,
    is_closed boolean NOT NULL,
    registered_user_id character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT client_check_pkey PRIMARY KEY (id),
    CONSTRAINT fk1is7ivvjp99ymq66d4o7e6krd FOREIGN KEY (registered_user_id)
        REFERENCES public.registered_user (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS public.view
(
    id character varying(255) COLLATE pg_catalog."default" NOT NULL,
    color character varying(255) COLLATE pg_catalog."default",
    left_side_and_right_side character varying(255) COLLATE pg_catalog."default",
    phone_front_and_back character varying(255) COLLATE pg_catalog."default",
    up_side_and_down_side character varying(255) COLLATE pg_catalog."default",
    color_for_which_phone character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT view_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS public.phone_description
(
    id character varying(255) COLLATE pg_catalog."default" NOT NULL,
    degree_of_moisture_protection character varying(255) COLLATE pg_catalog."default",
    diagonal real NOT NULL,
    display_resolution character varying(255) COLLATE pg_catalog."default",
    guarantee_time_months integer NOT NULL,
    height real NOT NULL,
    info_about_front_cameras character varying(5000) COLLATE pg_catalog."default",
    info_about_main_cameras character varying(5000) COLLATE pg_catalog."default",
    is_have_nfc boolean NOT NULL,
    name character varying(255) COLLATE pg_catalog."default",
    number_of_front_cameras integer NOT NULL,
    number_of_main_cameras integer NOT NULL,
    number_of_sim_cards integer NOT NULL,
    screen_refresh_rate integer NOT NULL,
    series character varying(255) COLLATE pg_catalog."default",
    weight real NOT NULL,
    width real NOT NULL,
    brand_id character varying(255) COLLATE pg_catalog."default",
    charge_type_id character varying(255) COLLATE pg_catalog."default",
    communication_standard_id character varying(255) COLLATE pg_catalog."default",
    country_id character varying(255) COLLATE pg_catalog."default",
    operation_system_id character varying(255) COLLATE pg_catalog."default",
    processor_id character varying(255) COLLATE pg_catalog."default",
    type_screen_id character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT phone_description_pkey PRIMARY KEY (id),
    CONSTRAINT fk367ynlbgtihyoldqv6ir5dha5 FOREIGN KEY (operation_system_id)
        REFERENCES public.operation_system (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk5c4viq6sey5vjd5o925x0ea3k FOREIGN KEY (charge_type_id)
        REFERENCES public.charge_type (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk7xs3a7t2nb0w5t0qg88c1o7nk FOREIGN KEY (brand_id)
        REFERENCES public.brand (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk7xwln2ucloednt95sowfodbwt FOREIGN KEY (processor_id)
        REFERENCES public.processor (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk8qlsear3qomyegluoqltu31y8 FOREIGN KEY (communication_standard_id)
        REFERENCES public.communication_standard (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkg97pf4q9wgpqqxily9bcafi0s FOREIGN KEY (country_id)
        REFERENCES public.country (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fknthi46tmgq5r75syt2ngtyxu3 FOREIGN KEY (type_screen_id)
        REFERENCES public.type_screen (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS public.phone
(
    id character varying(255) COLLATE pg_catalog."default" NOT NULL,
    amount_of_built_in_memory integer NOT NULL,
    amount_of_ram integer NOT NULL,
    phone_description_id character varying(255) COLLATE pg_catalog."default",
    rating_id character varying(255) COLLATE pg_catalog."default",
    view_id character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT phone_pkey PRIMARY KEY (id),
    CONSTRAINT fkocsxriagr47svlp0nc0t20y2n FOREIGN KEY (rating_id)
        REFERENCES public.rating (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkr5rycfflyf07tvpc2jmp8r14m FOREIGN KEY (view_id)
        REFERENCES public.view (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkruirf2xv2fif8brm2p7y36dyg FOREIGN KEY (phone_description_id)
        REFERENCES public.phone_description (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

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
);

CREATE TABLE IF NOT EXISTS public.phone_instance
(
    id character varying(255) COLLATE pg_catalog."default" NOT NULL,
    imei character varying(255) COLLATE pg_catalog."default",
    price double precision NOT NULL,
    check_id character varying(255) COLLATE pg_catalog."default",
    phone_id character varying(255) COLLATE pg_catalog."default",
    shopping_cart_id character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT phone_instance_pkey PRIMARY KEY (id),
    CONSTRAINT fk57nit1rcxf5884hyainfu4bq1 FOREIGN KEY (check_id)
        REFERENCES public.client_check (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkoyuovdenq9wiv1a43qri3iejt FOREIGN KEY (shopping_cart_id)
        REFERENCES public.shopping_cart (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkrini1miw8k61ipqp6iyfn5tr1 FOREIGN KEY (phone_id)
        REFERENCES public.phone (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);











