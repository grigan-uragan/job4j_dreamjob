create table post (
    post_id serial primary key,
    post_name text
    );
create table photo (
    photo_id serial primary key,
    photo_path text
);
create table candidates (
    candidate_id serial primary key,
    candidate_name text,
    photo_id int references photo(photo_id)
    );
create table users (
    user_id serial primary key,
    user_name text,
    user_email text,
    user_password text
);