create table authorities (username varchar(50) not null,authority varchar(50) not null,constraint fk_authorities_users foreign key(username) references users(user_name));

INSERT INTO authorities VALUES ('asgharrr141516', 'read');
INSERT  INTO authorities VALUES ('memol','admin');

ALTER TABLE users ADD COLUMN role VARCHAR(255) ;
ALTER TABLE users add column image_name VARCHAR(255);
ALTER TABLE users ADD COLUMN posts_count INTEGER DEFAULT 0;