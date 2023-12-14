CREATE TABLE properties (
property_id serial4 NOT NULL,
property_name varchar(255) NOT NULL,
data_type varchar(255) NOT NULL, -- Text=text, TextSet=text, Image=text, ImageSet=text, Integer=bigInt, Decimal=double precision, Timestamp=timestamp, Boolean=boolean
type_data varchar(255) NOT NULL,
last_modified_time timestamp(0) NULL,
create_time timestamptz NULL DEFAULT CURRENT_TIMESTAMP,
CONSTRAINT property_pkey PRIMARY KEY (property_id)
);
INSERT INTO properties (property_name,data_type,type_data) VALUES
 ('item_id',"text",'item'),
 ('last_update',"timestamp",'item'),
 ('user_id',"text",'user'),
 ('last_update',"timestam",'user');

CREATE TABLE data_user
(
 user_id varchar(255) not NULL,
 last_update timestamp(0) null,
 constraint data_user_pkey PRIMARY KEY (user_id)
);
CREATE TABLE data_item
(
 item_id varchar(255) not NULL,
 last_update timestamp(0) null,
 constraint data_item_pkey PRIMARY KEY (item_id)
);

SELECT column_name as propertyName, data_type as typeName
FROM information_schema.columns
WHERE table_name = 'data_item';

select * from properties where type_data = 'item' and property_name != 'item_id';

select item_id  from data_item;

insert into data_item (item_id, last_update) values ('huihiw', null);


insert into data_item (item_id, last_update, ) values
('123456','2018-11-12 17:49:29.864 +0700'),
('gswe','2018-11-12 17:49:29.864 +0700'),
('wedv','2018-11-12 17:49:29.864 +0700'),
('afte','2018-11-12 17:49:29.864 +0700'),
('aefwae','2018-11-12 17:49:29.864 +0700'),
('afewaef','2018-11-12 17:49:29.864 +0700')