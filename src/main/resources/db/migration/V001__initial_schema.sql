create table exchange_transaction (
	id uuid default random_uuid() primary key,
	user_id varchar(255) not null,
	from_currency varchar(3) not null,
	amount numeric not null,
	to_currency varchar(3) not null,
	rate numeric not null,
	timestamp timestamp
);
